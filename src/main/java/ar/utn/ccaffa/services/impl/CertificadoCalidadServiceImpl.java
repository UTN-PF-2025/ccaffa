package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.exceptions.ResourceNotFoundException;
import ar.utn.ccaffa.mapper.interfaces.CertificadoDeCalidadMapper;
import ar.utn.ccaffa.mapper.interfaces.OrdenVentaMapper;
import ar.utn.ccaffa.model.dto.*;
import ar.utn.ccaffa.model.entity.CertificadoDeCalidad;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.repository.interfaces.CertificadoDeCalidadRepository;
import ar.utn.ccaffa.services.interfaces.CertificadoCalidadService;
import ar.utn.ccaffa.services.interfaces.ControlDeCalidadService;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.Stream;

@Slf4j
@Service
public class CertificadoCalidadServiceImpl implements CertificadoCalidadService {

    public static final Font FONT_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Font.BOLD);
    public static final Font FONT_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD);
    public static final Font FONT_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10);
    public static final Font FONT_CELDA_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD);

    public static final String ANCHO_MM = "ANCHO(mm): ";
    public static final String ESPESOR_MM = "ESPESOR(mm): ";
    public static final String DUREZA_RB = "DUREZA(Rb): ";
    public static final String MAS_MENOS = "+/- ";
    public static final String NUMERO_CERTIFICADO = "NUMERO_CERTIFICADO";
    public static final String CERTIFICADO_DE_CALIDAD = "CERTIFICADO DE CALIDAD";
    public static final String COMPOSICION_CARBONO = "0,06";
    public static final String COMPOSICION_MANGANESO = "0,20";
    public static final String COMPOSICION_FOSFORO = "0,011";
    public static final String COMPOSICION_AZUFRE = "0,014";
    public static final String COMPOSICION_ALUMINIO = "0,029";
    public static final String COMPOSICION_SILICIO = "0,02";
    public static final String SAE_1006 = "SAE 1006";
    public static final String OK = "OK";

    private final ControlDeCalidadService controlDeCalidadService;
    private final CertificadoDeCalidadRepository certificadoDeCalidadRepository;
    private final OrdenDeTrabajoService ordenDeTrabajoService;
    private final OrdenVentaMapper ordenVentaMapper;
    private final CertificadoDeCalidadMapper certificadoDeCalidadMapper;

    Document documentParagraph;

    public CertificadoCalidadServiceImpl(ControlDeCalidadService controlDeCalidadService,
                                         CertificadoDeCalidadRepository certificadoDeCalidadRepository,
                                         OrdenDeTrabajoService ordenDeTrabajoService, OrdenVentaMapper ordenVentaMapper, CertificadoDeCalidadMapper certificadoDeCalidadMapper) {
        this.controlDeCalidadService = controlDeCalidadService;
        this.certificadoDeCalidadRepository = certificadoDeCalidadRepository;
        this.ordenDeTrabajoService = ordenDeTrabajoService;
        this.ordenVentaMapper = ordenVentaMapper;
        this.certificadoDeCalidadMapper = certificadoDeCalidadMapper;
    }

    @Override
    @Transactional
    public void generarCertificado(CertificadoRequestDTO certificadoRequestDTO) {
        try {
            ControlDeProcesoDto controlProceso = this.controlDeCalidadService.getControlDeProceso(certificadoRequestDTO.getControlDeCalidadId());
            OrdenDeTrabajo ot = this.ordenDeTrabajoService.findById(controlProceso.getIdOrden()).get();
            OrdenVentaDto ordenVentaDto = this.ordenVentaMapper.toDto(ot.getOrdenDeVenta());
            LocalDate fechaEmision = LocalDate.now();

            String nombreArchivo = construirNombreArchivo(controlProceso, ot);

            inicializarDocumento(nombreArchivo);
            agregarEncabezado(controlProceso, fechaEmision, ot, ordenVentaDto);
            agregarSeccionPartidas(certificadoRequestDTO);
            agregarSeccionSolicitado(certificadoRequestDTO, ordenVentaDto);
            agregarSeccionControlado(certificadoRequestDTO, controlProceso);
            agregarSeccionCalidad();
            armarTablaComposicion(certificadoRequestDTO);
            agregarPieDePagina();
            cerrarDocumento();

            guardarCertificado(certificadoRequestDTO, fechaEmision, controlProceso, nombreArchivo);

        } catch (FileNotFoundException | DocumentException e) {
            log.error("No se pudo crear el certificado PDF");
        }
    }

    private void agregarSeccionCalidad() throws DocumentException {
        Paragraph titulo = new Paragraph("CALIDAD", FONT_SUBTITULO);
        titulo.setSpacingAfter(10f);
        documentParagraph.add(titulo);

        PdfPTable table = crearTablaConEstilo(2);

        agregarCeldaConEstilo(table, "PARÁMETRO", FONT_CELDA_HEADER, BaseColor.LIGHT_GRAY);
        agregarCeldaConEstilo(table, "RESULTADO", FONT_CELDA_HEADER, BaseColor.LIGHT_GRAY);


        String[][] datosCalidad = {
                {"CALIDAD", SAE_1006},
                {"PLANITUD", OK},
                {"SUPERFICIE", OK},
                {"CAMBER", OK},
                {"PLEGADO", OK},
                {"REBABA", OK}
        };

        for (String[] dato : datosCalidad) {
            agregarCeldaConEstilo(table, dato[0], FONT_NORMAL, null);
            agregarCeldaConEstilo(table, dato[1], FONT_NORMAL, null);
        }

        documentParagraph.add(table);
    }

    private PdfPTable crearTablaConEstilo(int numColumns) {
        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        return table;
    }

    private void agregarCeldaConEstilo(PdfPTable table, String texto, Font font, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        if (backgroundColor != null) {
            cell.setBackgroundColor(backgroundColor);
        }
        cell.setPadding(5f);
        cell.setBorderWidth(1f);
        table.addCell(cell);
    }

    @Override
    public CertificadoDeCalidadDto findById(Long id) {
        return this.certificadoDeCalidadMapper.toDto(this.certificadoDeCalidadRepository.findById(id));
    }

    @Override
    public byte[] obtenerPdf(Long certificadoId) {
        try {
            CertificadoDeCalidad certificado = this.certificadoDeCalidadRepository.findById(certificadoId).orElseThrow(() -> new ResourceNotFoundException("Obtener Certificado", "id de certificado", certificadoId));
            
            String nombreCompleto = certificado.getNombreArchivo().endsWith(".pdf") ? certificado.getNombreArchivo() : certificado.getNombreArchivo() + ".pdf";
            Path rutaArchivo = Paths.get(nombreCompleto);

            if (!Files.exists(rutaArchivo)) {
                log.error("El archivo PDF no existe: {}", rutaArchivo.toAbsolutePath());
                throw new ResourceNotFoundException("Obtener PDF", "nombre de archivo", nombreCompleto);
            }

            byte[] contenidoPdf = Files.readAllBytes(rutaArchivo);
            log.info("Archivo PDF leído exitosamente: {}", nombreCompleto);

            return contenidoPdf;

        } catch (IOException e) {
            log.error("Error al leer el archivo PDF: {}", certificadoId, e);
            throw new RuntimeException("Error al obtener el archivo PDF: " + e.getMessage(), e);
        }

    }

    private void guardarCertificado(CertificadoRequestDTO certificadoRequestDTO, LocalDate fechaEmision, ControlDeProcesoDto controlProceso, String nombreArchivo) {
        CertificadoDeCalidadDto certificado = new CertificadoDeCalidadDto();
        certificado.setNumeroDeCertificado(construirNumeroCertificado(certificadoRequestDTO));
        certificado.setFechaDeEmision(fechaEmision);
        certificado.setAprobador(EmpleadoDto.builder().nombre(controlProceso.getNombreOperario()).id(controlProceso.getIdOperario()).build());
        certificado.setControlDeCalidadId(controlProceso.getIdControl());
        certificado.setNombreArchivo(nombreArchivo);

        this.certificadoDeCalidadRepository.save(this.certificadoDeCalidadMapper.toEntity(certificado));
    }

    private String construirNumeroCertificado(CertificadoRequestDTO certificadoRequestDTO) {
        return NUMERO_CERTIFICADO;
    }

    private void armarTablaComposicion(CertificadoRequestDTO certificadoRequestDTO) throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        agregarHeaderComposicion(table);
        agregarDatoComposicion(table, certificadoRequestDTO);
        documentParagraph.add(table);
    }

    private void agregarDatoComposicion(PdfPTable table, CertificadoRequestDTO certificadoRequestDTO) {
        table.addCell(new Phrase(COMPOSICION_CARBONO));
        table.addCell(new Phrase(COMPOSICION_MANGANESO));
        table.addCell(new Phrase(COMPOSICION_FOSFORO));
        table.addCell(new Phrase(COMPOSICION_AZUFRE));
        table.addCell(new Phrase(COMPOSICION_ALUMINIO));
        table.addCell(new Phrase(COMPOSICION_SILICIO));
    }

    private PdfPTable crearTablaPartidas(CertificadoRequestDTO certificadoRequestDTO) {
        PdfPTable tablePartidas = new PdfPTable(3);
        tablePartidas.setTotalWidth(210);
        tablePartidas.setLockedWidth(true);
        tablePartidas.addCell(nuevaPhrase("PARTIDAS DE MATERIA PRIMA"));

        PdfPTable tablaSegundaColumna = crearSubTabla("Partida 1", "Partida 2", "Partida 3", "Partida 4");
        PdfPCell cellP1 = new PdfPCell(tablaSegundaColumna);
        tablePartidas.addCell(cellP1);

        PdfPTable tablaTerceraColumna = crearSubTabla(certificadoRequestDTO.getPartida1(), certificadoRequestDTO.getPartida2(), certificadoRequestDTO.getPartida3(), certificadoRequestDTO.getPartida4());
        PdfPCell cellP2 = new PdfPCell(tablaTerceraColumna);
        tablePartidas.addCell(cellP2);

        tablePartidas.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return tablePartidas;
    }

    private static PdfPTable crearSubTabla(String textoPrimeraFila, String textoSegundaFila, String textoTerceraaFila, String textoCuartaFila) {
        PdfPTable tablaSegundaColumna = miniTabla();
        tablaSegundaColumna.addCell(new Phrase(textoPrimeraFila));
        tablaSegundaColumna.addCell(new Phrase(textoSegundaFila));
        tablaSegundaColumna.addCell(new Phrase(textoTerceraaFila));
        tablaSegundaColumna.addCell(new Phrase(textoCuartaFila));
        return tablaSegundaColumna;
    }

    private static PdfPTable miniTabla() {
        return new PdfPTable(1);
    }

    private PdfPCell nuevaPhrase(String partidasDeMateriaPrima) {
        Phrase phrase = new Phrase(partidasDeMateriaPrima);
        phrase.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD));
        return new PdfPCell(phrase);
    }

    private void definirTitulo(int alignCenter, String titulo) throws DocumentException {
        Paragraph paragraph = new Paragraph(titulo, FONT_TITULO);
        paragraph.setAlignment(alignCenter);
        paragraph.setSpacingAfter(20f);
        documentParagraph.add(paragraph);
    }

    private void agregarCampo(ElementListener composicion, int alineamiento, String parrafo) throws DocumentException {
        Paragraph paragraph = new Paragraph(parrafo);
        paragraph.setAlignment(alineamiento);
        composicion.add(paragraph);
    }

    private void agregarHeaderComposicion(PdfPTable table) {
        Stream.of("CARBONO", "MANGANESO", "FOSFORO", "AZUFRE", "ALUMINIO", "SILICIO")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void inicializarDocumento(String nombreArchivo) throws FileNotFoundException, DocumentException {
        documentParagraph = new Document();
        PdfWriter.getInstance(documentParagraph, new FileOutputStream(nombreArchivo));
        documentParagraph.open();
    }

    private String construirNombreArchivo(ControlDeProcesoDto controlDeProcesoDto, OrdenDeTrabajo ordenDeTrabajo) {
        String cliente = controlDeProcesoDto.getNombreCliente();
        String nroOrden = ordenDeTrabajo.getId().toString();

        String base = "CertificadoDeCalidad";
        if (cliente != null && !cliente.isBlank() && !nroOrden.isBlank()) {
            return base + "_" + cliente + "_OT-" + nroOrden + ".pdf";
        }
        if (cliente != null && !cliente.isBlank()) {
            return base + "_" + cliente + ".pdf";
        }
        if (!nroOrden.isBlank()) {
            return base + "_OT-" + nroOrden + ".pdf";
        }
        return base + ".pdf";
    }


    private void cerrarDocumento() {
        if (documentParagraph != null) {
            documentParagraph.close();
        }
    }

    private void agregarEncabezado(ControlDeProcesoDto dto, LocalDate fechaEmision, OrdenDeTrabajo ot, OrdenVentaDto ordenVentaDto) throws DocumentException {
        definirTitulo(Element.ALIGN_CENTER, CERTIFICADO_DE_CALIDAD);

        PdfPTable headerTable = crearTablaConEstilo(2);
        headerTable.setWidthPercentage(80);

        agregarCeldaConEstilo(headerTable, "FECHA: " + fechaEmision, FONT_NORMAL, null);
        agregarCeldaConEstilo(headerTable, "O.T.: " + ot.getId(), FONT_NORMAL, null);
        agregarCeldaConEstilo(headerTable, "CLIENTE: " + ordenVentaDto.getCliente().getName(), FONT_NORMAL, null);
        agregarCeldaConEstilo(headerTable, "N° CERTIFICADO: " + construirNumeroCertificado(new CertificadoRequestDTO()), FONT_NORMAL, null);

        documentParagraph.add(headerTable);
        agregarEspacio(0);
    }

    private void agregarPieDePagina() throws DocumentException {
        agregarEspacio(20f);

        Paragraph firma = new Paragraph("_________________________", FONT_NORMAL);
        firma.setAlignment(Element.ALIGN_CENTER);
        documentParagraph.add(firma);

        Paragraph responsable = new Paragraph("Responsable de Calidad", FONT_NORMAL);
        responsable.setAlignment(Element.ALIGN_CENTER);
        documentParagraph.add(responsable);
    }

    private void agregarSeccionPartidas(CertificadoRequestDTO dto) throws DocumentException {
        documentParagraph.add(crearTablaPartidas(dto));
    }

    private void agregarSeccionSolicitado(CertificadoRequestDTO dto, OrdenVentaDto ordenVentaOriginal) throws DocumentException {
        documentParagraph.add(new Paragraph("SOLICITADO"));
        agregarEspacio(0);
        documentParagraph.add(crearTablaDatosSolicitados(dto, ordenVentaOriginal));
    }

    private void agregarSeccionControlado(CertificadoRequestDTO dto, ControlDeProcesoDto controlProceso) throws DocumentException {
        documentParagraph.add(new Paragraph("CONTROLADO"));
        agregarEspacio(0);
        documentParagraph.add(crearTablaDatosControlados(dto, controlProceso));
    }

    private PdfPTable crearTablaDatosSolicitados(CertificadoRequestDTO dto, OrdenVentaDto ordenVentaOriginal) {

        PdfPTable izquierda3 = new PdfPTable(3);
        EspecificacionDto especificacion = ordenVentaOriginal.getEspecificacion();
        izquierda3.addCell(new Phrase("CANTIDAD(Kg): "));
        izquierda3.addCell(new Phrase(especificacion.getCantidad().toString()));
        izquierda3.addCell(new Phrase(" "));

        izquierda3.addCell(new Phrase(ANCHO_MM));
        izquierda3.addCell(new Phrase(especificacion.getAncho().toString()));
        izquierda3.addCell(new Phrase(MAS_MENOS + especificacion.getToleranciaAncho()));

        izquierda3.addCell(new Phrase(ESPESOR_MM));
        izquierda3.addCell(new Phrase(especificacion.getEspesor().toString()));
        izquierda3.addCell(new Phrase(MAS_MENOS + especificacion.getToleranciaEspesor()));

        izquierda3.addCell(new Phrase(DUREZA_RB));
        izquierda3.addCell(new Phrase(dto.getDurezaOriginal()));
        izquierda3.addCell(new Phrase(MAS_MENOS + dto.getErrorDurezaOriginal()));


        PdfPTable derecha2 = new PdfPTable(2);

        derecha2.addCell(new Phrase("CALIDAD "));
        derecha2.addCell(new Phrase(SAE_1006));

        derecha2.addCell(new Phrase("DIAMETRO INTERNO"));
        derecha2.addCell(new Phrase(especificacion.getDiametroInterno().toString()));

        derecha2.addCell(new Phrase("DIAMETRO EXTERNO"));
        derecha2.addCell(new Phrase(especificacion.getDiametroExterno().toString()));

        derecha2.addCell(new Phrase("Cant Rollos"));
        derecha2.addCell(new Phrase(dto.getCantidadOriginal()));


        PdfPTable contenedora = new PdfPTable(2);
        PdfPCell cellIzq = new PdfPCell(izquierda3);
        PdfPCell cellDer = new PdfPCell(derecha2);
        contenedora.addCell(cellIzq);
        contenedora.addCell(cellDer);
        return contenedora;

    }

    private PdfPTable crearTablaDatosControlados(CertificadoRequestDTO dto, ControlDeProcesoDto controlProceso) {
        // TODO: MODIFICAR POR LOS EL ANCHO MEDIO Y ESPESO MEDIO
        PdfPTable table = new PdfPTable(3);
        table.addCell(new Phrase(ANCHO_MM));
        table.addCell(new Phrase(controlProceso.getAnchoMedio().toString()));
        table.addCell(new Phrase(MAS_MENOS + controlProceso.getToleranciaAncho()));

        table.addCell(new Phrase(ESPESOR_MM));
        table.addCell(new Phrase(controlProceso.getEspesorMedio().toString()));
        table.addCell(new Phrase(MAS_MENOS + controlProceso.getToleranciaEspesor()));

        table.addCell(new Phrase(DUREZA_RB));
        table.addCell(new Phrase(" "));
        table.addCell(new Phrase(MAS_MENOS + " "));
        return table;
    }
    private void agregarEspacio(float puntos) throws DocumentException {
        documentParagraph.add(new Paragraph(" "));
        if (puntos > 0) {
            documentParagraph.add(Chunk.NEWLINE);
        }
    }

    private void agregarEspacio() throws DocumentException {
        agregarEspacio(5f);
    }

}
