package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.model.dto.CertificadoRequestDTO;
import ar.utn.ccaffa.services.interfaces.CertificadoCalidadService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.stream.Stream;

@Slf4j
@Service
public class CertificadoCalidadServiceImpl implements CertificadoCalidadService {

    Document documentParagraph;
    @Override
    public void generarCertificado(CertificadoRequestDTO certificadoRequestDTO) {
        try {
            documentParagraph = new Document();
            PdfWriter.getInstance(documentParagraph, new FileOutputStream("CertificadoDeCalidad.pdf"));
            documentParagraph.open();
            definirTitulo(Element.ALIGN_CENTER, certificadoRequestDTO.getTitulo());
            agregarCampo(documentParagraph,Element.ALIGN_LEFT, "FECHA: "+certificadoRequestDTO.getFecha());
            agregarCampo(documentParagraph,Element.ALIGN_LEFT, "CLIENTE:\t"+certificadoRequestDTO.getCliente());

            documentParagraph.add(crearTablaPartidas(certificadoRequestDTO));

            documentParagraph.add(new Paragraph(" "));
            documentParagraph.add(new Paragraph(" "));
            agregarCampo(documentParagraph, Element.ALIGN_LEFT, "CANTIDAD(mm): "+certificadoRequestDTO.getCantidadOriginal());
            agregarCampo(documentParagraph, Element.ALIGN_LEFT, "ANCHO(mm): "+ certificadoRequestDTO.getAnchoOriginal());
            agregarCampo(documentParagraph, Element.ALIGN_LEFT, "ESPESOR(mm): "+ certificadoRequestDTO.getEspesorOriginal());
            agregarCampo(documentParagraph, Element.ALIGN_LEFT, "DUREZA(Rb): " + certificadoRequestDTO.getDurezaOriginal());

            documentParagraph.add(new Paragraph(" "));

            Chapter datosMedidos = new Chapter(new Paragraph("CONTROLADO"), 1);

            agregarCampo(documentParagraph, Element.ALIGN_LEFT, "ANCHO(mm): "+ certificadoRequestDTO.getAnchoReal());
            agregarCampo(documentParagraph, Element.ALIGN_LEFT, "ESPESOR(mm): "+ certificadoRequestDTO.getEspesorReal());
            agregarCampo(documentParagraph, Element.ALIGN_LEFT, "DUREZA(Rb): " + certificadoRequestDTO.getDurezaReal());
            

            PdfPTable table = new PdfPTable(6);
            agregarHeaderComposicion(table);
            agregarDatoComposicion(table, certificadoRequestDTO);
            documentParagraph.add(table);
            documentParagraph.close();
        } catch (FileNotFoundException | DocumentException e) {
            log.error("No se pudo crear el certificado PDF");
        }
    }

    private void agregarDatoComposicion(PdfPTable table, CertificadoRequestDTO certificadoRequestDTO) {
        table.addCell(new Phrase(certificadoRequestDTO.getComposicionCarbono()));
        table.addCell(new Phrase(certificadoRequestDTO.getComposicionManganeso()));
        table.addCell(new Phrase(certificadoRequestDTO.getComposicionFosforo()));
        table.addCell(new Phrase(certificadoRequestDTO.getComposicionAzufre()));
        table.addCell(new Phrase(certificadoRequestDTO.getComposicionAluminio()));
        table.addCell(new Phrase(certificadoRequestDTO.getComposicionSilicio()));
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
}
