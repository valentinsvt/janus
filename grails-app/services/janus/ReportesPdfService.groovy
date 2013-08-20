package janus

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

class ReportesPdfService {

    def serviceMethod() {

    }


    def createDocument(params) {
        if (!params.orientacion) {
            params.orientacion = "v"
        }
        if (!params.margenes) {
            params.margenes = [3, 2, 2, 3]  //top(0)-right(1)-bottom(2)-left(3) en cm
        }
        if (!params.keywords) {
            params.keywords = "janus"
        }
        if (params.margenes instanceof String) {
            params.margenes = [params.margenes.toInteger(), params.margenes.toInteger(), params.margenes.toInteger(), params.margenes.toInteger()]
        }
        def orientacion = params.orientacion.toLowerCase()[0]
        def margenes = params.margenes
        def titulo = params.titulo
        def keywords = params.keywords

        def logoPath
        Image logo
        if (params.logo) {
            logoPath = params.logo
            logo = Image.getInstance(logoPath);
            logo.setAlignment(Image.LEFT | Image.TEXTWRAP)
        }

        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font fontInfo = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)
        Font fontFooter = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)

        def baos = new ByteArrayOutputStream()
        Document document
        if (orientacion == "v") {
            document = new Document(PageSize.A4);
        } else {
            document = new Document(PageSize.A4.rotate());
        }

        document.setMargins(margenes[3] * 28.1, margenes[1] * 28.1, margenes[0] * 28.1, margenes[2] * 28.1);
        // margins: left, right, top, bottom
        // 1 in = 72, 1cm=28.1, 3cm = 86.4
        def pdfw = PdfWriter.getInstance(document, baos);

//        Paragraph preface = new Paragraph();
//        preface.setAlignment(Element.ALIGN_CENTER);
//        preface.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
//        preface.add(new Paragraph(titulo + "\n", fontTituloGad));
//        if (params.logo) {
//            preface.add(logo)
//        }

        if (params.footer) {
            HeaderFooter footer = new HeaderFooter(new Phrase(params.footer, fontFooter), false); // true aqui pone numero de pagina
            footer.setBorder(Rectangle.NO_BORDER);
            footer.setBorder(Rectangle.TOP);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.setFooter(footer);
        }
        if (params.header) {
//            HeaderFooter header = new HeaderFooter(preface, false); // true aqui pone numero de pagina
//            header.setBorder(Rectangle.NO_BORDER);
//            header.setBorder(Rectangle.TOP);
//            header.setAlignment(Element.ALIGN_CENTER);
//            document.setHeader(header);
        }
        document.open();
        document.addTitle(titulo);
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords(keywords);
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");
//
//        Paragraph preface = new Paragraph();
//        preface.setAlignment(Element.ALIGN_CENTER);
//        preface.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
//        preface.add(new Paragraph(titulo + "\n", fontTituloGad));
//        if (params.logo) {
//            preface.add(logo)
//        }
//        document.add(preface);

        PdfPTable tablaHeader = new PdfPTable(2);
        tablaHeader.setWidthPercentage(100);
        tablaHeader.setWidths([20.toInteger(), 80.toInteger()])


        if (params.usuario) {
            Paragraph preface2 = new Paragraph();
            preface2.add(new Paragraph("Generado por el usuario: " + params.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm") + "\n", fontInfo))
            document.add(preface2)
        }


        return [document: document, pdfw: pdfw, baos: baos]
    }

    private static void addCellTabla(PdfPTable table, paragraph, params) {
        PdfPCell cell = new PdfPCell(paragraph);
        if (params.height) {
            cell.setFixedHeight(params.height.toFloat());
        }
        if (params.border) {
            cell.setBorderColor(params.border);
        }
        if (params.bg) {
            cell.setBackgroundColor(params.bg);
        }
        if (params.colspan) {
            cell.setColspan(params.colspan);
        }
        if (params.align) {
            cell.setHorizontalAlignment(params.align);
        }
        if (params.valign) {
            cell.setVerticalAlignment(params.valign);
        }
        if (params.w) {
            cell.setBorderWidth(params.w);
            cell.setUseBorderPadding(true);
        }
        if (params.bwl) {
            cell.setBorderWidthLeft(params.bwl.toFloat());
            cell.setUseBorderPadding(true);
        }
        if (params.bwb) {
            cell.setBorderWidthBottom(params.bwb.toFloat());
            cell.setUseBorderPadding(true);
        }
        if (params.bwr) {
            cell.setBorderWidthRight(params.bwr.toFloat());
            cell.setUseBorderPadding(true);
        }
        if (params.bwt) {
            cell.setBorderWidthTop(params.bwt.toFloat());
            cell.setUseBorderPadding(true);
        }
        if (params.bcl) {
            cell.setBorderColorLeft(params.bcl);
        }
        if (params.bcb) {
            cell.setBorderColorBottom(params.bcb);
        }
        if (params.bcr) {
            cell.setBorderColorRight(params.bcr);
        }
        if (params.bct) {
            cell.setBorderColorTop(params.bct);
        }
        if (params.padding) {
            cell.setPadding(params.padding.toFloat());
        }
        if (params.pl) {
            cell.setPaddingLeft(params.pl.toFloat());
        }
        if (params.pr) {
            cell.setPaddingRight(params.pr.toFloat());
        }
        if (params.pt) {
            cell.setPaddingTop(params.pt.toFloat());
        }
        if (params.pb) {
            cell.setPaddingBottom(params.pb.toFloat());
        }

        table.addCell(cell);
    }

}
