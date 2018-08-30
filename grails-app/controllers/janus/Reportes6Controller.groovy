package janus

import com.itextpdf.text.BaseColor
import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter

import java.awt.Color
import java.text.DecimalFormat

class Reportes6Controller {

    def index() { }


    private static int[] arregloEnteros(array) {
        int[] ia = new int[array.size()]
        array.eachWithIndex { it, i ->
            ia[i] = it.toInteger()
        }
        return ia
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

    def reporteOrdenCambio () {

        def baos = new ByteArrayOutputStream()

        BaseColor colorAzul = new BaseColor(50, 96, 144)

        Font fontTitle = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Font fontThTiny = new Font(Font.TIMES_ROMAN, 7, Font.BOLD);
        Font fontThTiny2 = new Font(Font.TIMES_ROMAN, 6, Font.BOLD);

        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def borderWidth = 0.3

        Locale loc = new Locale("en_US")
        java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(loc);
        DecimalFormat df = (DecimalFormat)nf;
        df.applyPattern("\$##,###.##");

        Document document
        document = new Document(PageSize.A4);
        document.setMargins(50,30,30,28)  // 28 equivale a 1 cm: izq, derecha, arriba y abajo
        def pdfw = PdfWriter.getInstance(document, baos);

        document.open();
        document.addTitle("");
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, orden de cambio");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");


        PdfPTable tabla1 = new PdfPTable(4);
        tabla1.setWidthPercentage(100);
        tabla1.setWidths(arregloEnteros([15,13,47,15]))

        addCellTabla(tabla1, new Paragraph("GOBIERNO AUTÓNOMO DESCENTRALIZADO DE LA PROVINCIA DE PICHINCHA", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 4, height: 30])

        addCellTabla(tabla1, new Paragraph("ORDEN DE CAMBIO N 01-C-36-DGCP-2018", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 4, height: 20])

        addCellTabla(tabla1, new Paragraph("CONTRATO N°", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(tabla1, new Paragraph("36-DGCP-18", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tabla1, new Paragraph("CONTRATISTA:", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(tabla1, new Paragraph("ING PATRICIO XAVIER ONTANEDA", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tabla1, new Paragraph("OBJETO DEL CONTRATO:", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2, height: 20])
        addCellTabla(tabla1, new Paragraph("construcción del CENTRO TECNOLÓGICO COMUNITARIO LA ABUNDANCIA", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tabla1, new Paragraph("MONTO DEL CONTRATO:", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])

        PdfPTable inner1 = new PdfPTable(3);
        addCellTabla(inner1, new Paragraph("51,332.76", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner1, new Paragraph("FECHA DE INICIO CONTRACTUAL:", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner1, new Paragraph("23/04/2018", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla1, inner1, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tabla1, new Paragraph("PLAZO:", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])

        PdfPTable inner2 = new PdfPTable(3);
        addCellTabla(inner2, new Paragraph("90", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner2, new Paragraph("FECHA DE TÉRMINO CONTRACTUAL:", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner2, new Paragraph("23/04/2018", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla1, inner2, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tabla1, new Paragraph("", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 4, height: 15])

        addCellTabla(tabla1, new Paragraph("De acuerdo al " +
                "informe técnico de Fiscalización en el memorando N° MEM-1297-DGFZ-17, se ha establecido " +
                "la necesidad de ejecutar diferencias de cantidades de obra en el Contrato Original, con la " +
                "finalidad de cumplir efectivamente con el " +
                "objeto del contrato", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 4, height: 30])

        document.add(tabla1)

        PdfPTable tabla2 = new PdfPTable(7);
        tabla2.setWidthPercentage(100);
        tabla2.setWidths(arregloEnteros([12,30,5,12,16,12,12]))

        addCellTabla(tabla2, new Paragraph("DIFERENCIA DE CANTIDADES DE OBRA", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 7, height: 20])

        addCellTabla(tabla2, new Paragraph("ITEM", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 20])
        addCellTabla(tabla2, new Paragraph("DESCRIPCIÓN", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE,  height: 20])
        addCellTabla(tabla2, new Paragraph("U", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 20])
        addCellTabla(tabla2, new Paragraph("CANTIDAD CONTRATADA", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE,  height: 20])
        addCellTabla(tabla2, new Paragraph("DIFERENCIA DE CANTIDAD", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 20])
        addCellTabla(tabla2, new Paragraph("PRECIO UNITARIO", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 20])
        addCellTabla(tabla2, new Paragraph("PRECIO TOTAL", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 20])

        addCellTabla(tabla2, new Paragraph("V-307-RS", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 15])
        addCellTabla(tabla2, new Paragraph("RELLENO CON SUB BASE", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE,  height: 15])
        addCellTabla(tabla2, new Paragraph("M3", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 15])
        addCellTabla(tabla2, new Paragraph("0", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE,  height: 15])
        addCellTabla(tabla2, new Paragraph("21.89", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 15])
        addCellTabla(tabla2, new Paragraph("33.02", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 15])
        addCellTabla(tabla2, new Paragraph("722.81", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 15])

        addCellTabla(tabla2, new Paragraph("TOTAL DE EXCEDENTES DE OBRA", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 6, height: 20])
        addCellTabla(tabla2, new Paragraph("722.81", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE,  height: 20])

        addCellTabla(tabla2, new Paragraph("PORCENTAJE DEL MONTO DEL CONTRATO", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 6, height: 20])
        addCellTabla(tabla2, new Paragraph("1.98%", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE,  height: 20])

        addCellTabla(tabla2, new Paragraph("", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 7, height: 15])

        addCellTabla(tabla2, new Paragraph("Valor que cuenta con la Certificación Presupuestaria N° 002333, de fecha 01/12/2018 suscrito por la Coordinación de Administración Presupuestaria", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 7, height: 15])

        addCellTabla(tabla2, new Paragraph("Adicionalmente el contratista emite la siguiente garantía: POLIZA N° 1014319 de la ASEGURADORA DEL SUR S.A. vigencia hasta 01/12/2018", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 7, height: 15])

        addCellTabla(tabla2, new Paragraph("El presente documento se inscribe en la Ley Orgánica para la Eficiencia del Sistema Nacional de Contratación Pública, 'Art.9 - Diferencia de Cantidades de Obra, que señala que la " +
                "entidad contratante podrá ordenar y pagar directamente sin necesidad de contrato complementario, hasta el cinco (5%) por ciento del valor reajustado del contrato, " +
                "siempre que no se modifique el objeto contractual'.", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 7, height: 30])

        addCellTabla(tabla2, new Paragraph("", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 7, height: 15])

        addCellTabla(tabla2, new Paragraph("Y de acuerdo al memorando N° 2501-DGCP-17, suscrito por el Director de Gestión de Compras Públicas, delegado del señor Prefecto, " +
                "en el que se indica que las 'PARTES' que señala en la Ley Orgánica para la Eficiencia en la Contratación Pública son: la Entidad seccional autónoma como " +
                "contratante y otra persona natural o jurídica como contratista, por lo tanto el GAD de la Provincia de Pichincha, constituye una 'parte' del contrato " +
                "administrativo, representado por el Administrador del contrato, se procesde a suscribit este documento (3 originales) en unidad de acto." , fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 7, height: 40])

        document.add(tabla2)

        PdfPTable tabla3 = new PdfPTable(7);
        tabla3.setWidthPercentage(100);
        tabla3.setWidths(arregloEnteros([8,23,8,23,8,22,8]))

        addCellTabla(tabla3, new Paragraph("Dado en Quito, 01 de Diciembre de 2017", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.WHITE, bcl: Color.BLACK, bcr: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 7])

        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.BLACK, bcr: Color.WHITE,  bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE,bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE,bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE,bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE,bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.BLACK, bct: Color.WHITE,bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])

        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.BLACK, bcr: Color.WHITE, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph("ARQ. FRANCISCO ARGUELLO CORONADO", fontThTiny2), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph("ING. PATRICIO PUENTE ONTANEDA", fontThTiny2), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE,bwb: 0.1, bcb: Color.WHITE, bct: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph("ARQ. CÉSAR GARZÓN ZAMBRANO", fontThTiny2), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE,bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.BLACK,bwb: 0.1, bcb: Color.WHITE, bct: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 30])

        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.BLACK, bcr: Color.WHITE, bct: Color.WHITE, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 15])
        addCellTabla(tabla3, new Paragraph("ADMINISTRADOR DEL CONTRATO", fontThTiny2), [border: Color.BLACK,bct: Color.WHITE, bcl: Color.WHITE, bcr: Color.WHITE, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 15])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 15])
        addCellTabla(tabla3, new Paragraph("CONTRATISTA", fontThTiny2), [border: Color.BLACK, bcl: Color.WHITE,bct: Color.WHITE, bcr: Color.WHITE, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 15])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE,bwb: 0.1, bcb: Color.BLACK, bct: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 15])
        addCellTabla(tabla3, new Paragraph("FISCALIZADOR", fontThTiny2), [border: Color.BLACK, bcl: Color.WHITE, bct: Color.WHITE,bcr: Color.WHITE,bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 15])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.BLACK,bwb: 0.1, bcb: Color.BLACK, bct: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 15])

        document.add(tabla3)

        document.close()
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + 'rolPagos')
        response.setContentLength(b.length)
        response.getOutputStream().write(b)

    }

}
