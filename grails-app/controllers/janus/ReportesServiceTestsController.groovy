package janus

import com.lowagie.text.Element
import com.lowagie.text.Image
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter

import java.awt.Color

class ReportesServiceTestsController {

    def reportesPdfService

    def reporteTest() {
        def baos = new ByteArrayOutputStream()
        def name = "prueba_" + new Date().format("ddMMyyyy_hhmm") + ".pdf"
        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        def document = reportesPdfService.crearDocumento()
        def pdfw = PdfWriter.getInstance(document, baos)

        reportesPdfService.documentoFooter(document, "Manuel Larrea N. 13-45 y Antonio Ante / Teléfonos troncal: (593-2)252 7077 - 254 9222 - 254 9020 - 254 9163 / www.pichincha.gob.ec")
        reportesPdfService.documentoHeader(document, "SEP - G.A.D. Pichincha", true)

        document.open();
        reportesPdfService.propiedadesDocumento(document, "Pruebas " + new Date().format("dd_MM_yyyy"));

        Paragraph preface = new Paragraph();
        reportesPdfService.addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", reportesPdfService.fontTituloGad));
        preface.add(new Paragraph("PRUEBAS", reportesPdfService.fontTituloGad));
        reportesPdfService.addEmptyLine(preface, 1);
        Paragraph preface2 = new Paragraph();
        preface2.add(new Paragraph("Generado el: " + new Date().format("dd/MM/yyyy hh:mm"), reportesPdfService.fontInfo))
        reportesPdfService.addEmptyLine(preface2, 1);
        document.add(logo)
        document.add(preface);
        document.add(preface2);

        PdfPTable tablaTest = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([12, 24, 10, 12, 24]));

        reportesPdfService.addCellTabla(tablaTest, new Paragraph("Prueba", reportesPdfService.fontTd), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 5])

        document.add(tablaTest)

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }


}
