package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.mapper.interfaces.CertificadoDeCalidadMapper;
import ar.utn.ccaffa.mapper.interfaces.OrdenVentaMapper;
import ar.utn.ccaffa.model.dto.*;
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
import java.time.LocalDate;
import java.util.stream.Stream;

@Slf4j
@Service
public class CertificadoCalidadServiceImpl implements CertificadoCalidadService {

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

            agregarEncabezado(controlProceso, fechaEmision, ot);
            agregarEspacio();
            agregarSeccionPartidas(certificadoRequestDTO);
            agregarEspacio();

            agregarSeccionSolicitado(certificadoRequestDTO, ordenVentaDto);
            agregarEspacio();

            agregarSeccionControlado(certificadoRequestDTO, controlProceso);
            agregarEspacio();

            agregarSeccionCalidad();
            agregarEspacio();

            armarTablaComposicion(certificadoRequestDTO);

            cerrarDocumento();

   //         guardarCertificado(certificadoRequestDTO, fechaEmision, controlProceso, ot);

        } catch (FileNotFoundException | DocumentException e) {
            log.error("No se pudo crear el certificado PDF");
        }
    }

    private void agregarSeccionCalidad() throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setTotalWidth(210);
        table.setLockedWidth(true);
        table.addCell(new Phrase("CALIDAD"));
        table.addCell(new Phrase("SAE 1006"));
        table.addCell(new Phrase("PLANITUD"));
        table.addCell(new Phrase("OK"));
        table.addCell(new Phrase("SUPERFICIE"));
        table.addCell(new Phrase("OK"));
        table.addCell(new Phrase("CAMBER"));
        table.addCell(new Phrase("OK"));
        table.addCell(new Phrase("PLEGADO"));
        table.addCell(new Phrase("OK"));
        table.addCell(new Phrase("REBABA"));
        table.addCell(new Phrase("OK"));
        table.addCell(new Phrase("CAMBER"));
        table.addCell(new Phrase("OK"));
        documentParagraph.add(table);
    }

    @Override
    public CertificadoDeCalidadDto findById(Long id) {
        return this.certificadoDeCalidadMapper.toDto(this.certificadoDeCalidadRepository.findById(id));
    }

    private void guardarCertificado(CertificadoRequestDTO certificadoRequestDTO, LocalDate fechaEmision, ControlDeProcesoDto controlProceso, OrdenDeTrabajo ot) {
        CertificadoDeCalidadDto certificado = new CertificadoDeCalidadDto();
        certificado.setNumeroDeCertificado(construirNumeroCertificado(certificadoRequestDTO));
        certificado.setFechaDeEmision(fechaEmision);
        certificado.setAprobador(EmpleadoDto.builder().nombre(controlProceso.getNombreOperario()).id(controlProceso.getIdOperario()).build());
        certificado.setControlDeCalidadId(controlProceso.getIdControl());

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
        Paragraph paragraph = new Paragraph(titulo);
        paragraph.setAlignment(alignCenter);
        paragraph.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 40, Font.BOLD));
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

    private void agregarEncabezado(ControlDeProcesoDto dto, LocalDate fechaEmision, OrdenDeTrabajo ot) throws DocumentException {
        definirTitulo(Element.ALIGN_CENTER, CERTIFICADO_DE_CALIDAD);
        agregarCampo(documentParagraph, Element.ALIGN_LEFT, "FECHA: " + fechaEmision);
        agregarCampo(documentParagraph, Element.ALIGN_LEFT, "CLIENTE:\t" + dto.getNombreCliente());
        agregarCampo(documentParagraph, Element.ALIGN_RIGHT, "O.T.: " + ot.getId());
    }

    private void agregarSeccionPartidas(CertificadoRequestDTO dto) throws DocumentException {
        documentParagraph.add(crearTablaPartidas(dto));
    }

    private void agregarSeccionSolicitado(CertificadoRequestDTO dto, OrdenVentaDto ordenVentaOriginal) throws DocumentException {
        documentParagraph.add(new Paragraph("SOLICITADO"));
        agregarEspacio();
        documentParagraph.add(crearTablaDatosSolicitados(dto, ordenVentaOriginal));
    }

    private void agregarSeccionControlado(CertificadoRequestDTO dto, ControlDeProcesoDto controlProceso) throws DocumentException {
        documentParagraph.add(new Paragraph("CONTROLADO"));
        agregarEspacio();
        documentParagraph.add(crearTablaDatosControlados(dto, controlProceso));
    }

    private PdfPTable crearTablaDatosSolicitados(CertificadoRequestDTO dto, OrdenVentaDto ordenVentaOriginal) {

        PdfPTable izquierda3 = new PdfPTable(3);
        EspecificacionDto especificacion = ordenVentaOriginal.getEspecificacion();
        izquierda3.addCell(new Phrase("CANTIDAD(Kg): "));
        izquierda3.addCell(new Phrase(especificacion.getCantidad()));
        izquierda3.addCell(new Phrase(" "));

        izquierda3.addCell(new Phrase(ANCHO_MM));
        izquierda3.addCell(new Phrase(especificacion.getAncho()));
        izquierda3.addCell(new Phrase(MAS_MENOS + especificacion.getToleranciaAncho()));

        izquierda3.addCell(new Phrase(ESPESOR_MM));
        izquierda3.addCell(new Phrase(especificacion.getEspesor()));
        izquierda3.addCell(new Phrase(MAS_MENOS + especificacion.getToleranciaEspesor()));

        izquierda3.addCell(new Phrase(DUREZA_RB));
        izquierda3.addCell(new Phrase(dto.getDurezaOriginal()));
        izquierda3.addCell(new Phrase(MAS_MENOS + dto.getErrorDurezaOriginal()));


        PdfPTable derecha2 = new PdfPTable(2);

        derecha2.addCell(new Phrase("CALIDAD "));
        derecha2.addCell(new Phrase("Valor Calidad"));

        derecha2.addCell(new Phrase("DIAMETRO INTERNO"));
        derecha2.addCell(new Phrase(especificacion.getDiametroInterno()));

        derecha2.addCell(new Phrase("DIAMETRO EXTERNO"));
        derecha2.addCell(new Phrase(especificacion.getDiametroExterno()));

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
        PdfPTable table = new PdfPTable(3);
        table.addCell(new Phrase(ANCHO_MM));
        table.addCell(new Phrase(controlProceso.getAncho()));
        table.addCell(new Phrase(MAS_MENOS + controlProceso.getToleranciaAncho()));

        table.addCell(new Phrase(ESPESOR_MM));
        table.addCell(new Phrase(controlProceso.getEspesor()));
        table.addCell(new Phrase(MAS_MENOS + controlProceso.getToleranciaEspesor()));

        table.addCell(new Phrase(DUREZA_RB));
        table.addCell(new Phrase(" "));
        table.addCell(new Phrase(MAS_MENOS + " "));
        return table;
    }

    private void agregarEspacio() throws DocumentException {
        documentParagraph.add(new Paragraph(" "));
    }

}
