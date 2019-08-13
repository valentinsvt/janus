package janus

import com.itextpdf.awt.DefaultFontMapper
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfTemplate
import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import janus.ejecucion.DetallePlanillaCosto
import janus.ejecucion.Planilla

import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import java.text.DecimalFormat

import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.category.BarRenderer
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer
import org.jfree.data.KeyToGroupMap
import org.jfree.data.category.CategoryDataset
import org.jfree.data.category.DefaultCategoryDataset

import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

class Reportes6Controller {

    def dbConnectionService

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

    private String numero(num, decimales, cero) {
        if (num == 0 && cero.toString().toLowerCase() == "hide") {
            return " ";
        }
        if (decimales == 0) {
            return formatNumber(number: num, minFractionDigits: decimales, maxFractionDigits: decimales, locale: "ec")
        } else {
            def format
            if (decimales == 2) {
                format = "##,##0"
            } else if (decimales == 3) {
                format = "##,###0"
            }
            return formatNumber(number: num, minFractionDigits: decimales, maxFractionDigits: decimales, locale: "ec", format: format)
        }
    }

    private String numero(num, decimales) {
        return numero(num, decimales, "show")
    }

    private String numero(num) {
        return numero(num, 3)
    }

    def reporteOrdenCambio () {

//        println("params " + params)

//        def contrato = Contrato.get(params.id)
//        def planilla = Planilla.get(params.planilla)

        def planilla = Planilla.get(params.id).refresh()
        def contrato = planilla.contrato

        def contratista = contrato.oferta.proveedor
        def strContratista = nombrePersona(contratista, "prov")

        def cn = dbConnectionService.getConnection()
        def cn2 = dbConnectionService.getConnection()
        def sql = "select max(prejfcfn) fecha from prej where prejtipo in ('A', 'P') and cntr__id = ${contrato?.id};"
        def res = cn.rows(sql.toString())
        def fin = res?.first()?.fecha?.format("dd/MM/yyyy")

        def baos = new ByteArrayOutputStream()

        def colorGris = new Color(245, 243, 245);

        Font fontTitle = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Font fontThTiny = new Font(Font.TIMES_ROMAN, 7, Font.BOLD);
        Font fontThTinyN = new Font(Font.TIMES_ROMAN, 7, Font.NORMAL);
        Font fontThTiny2 = new Font(Font.TIMES_ROMAN, 6, Font.BOLD);
        Font fontThTinyN2 = new Font(Font.TIMES_ROMAN, 6, Font.NORMAL);

        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def borderWidth = 0.3

        Locale loc = new Locale("en_US")
        java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(loc);
        DecimalFormat df = (DecimalFormat)nf;
//        df.applyPattern("\$##,###.##");
        df.applyPattern("##,###.##");

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

        def fondoGris = [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: colorGris, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def frmtDato = [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]

        addCellTabla(tabla1, new Paragraph("GOBIERNO AUTÓNOMO DESCENTRALIZADO DE LA PROVINCIA DE PICHINCHA", fontThTiny), fondoGris + [colspan: 4, height: 30])

        addCellTabla(tabla1, new Paragraph("ORDEN DE CAMBIO N° " + (planilla?.numeroOrden ?: ''), fontThTiny), fondoGris + [colspan: 4, height: 30])

        addCellTabla(tabla1, new Paragraph("CONTRATO N°", fontThTiny), fondoGris + [colspan: 2])
        addCellTabla(tabla1, new Paragraph(contrato?.codigo ?: '', fontThTinyN), frmtDato + [colspan: 2])

        addCellTabla(tabla1, new Paragraph("CONTRATISTA:", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(tabla1, new Paragraph(contrato?.contratista?.nombre ?: '', fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tabla1, new Paragraph("OBJETO DEL CONTRATO:", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2, height: 20])
        addCellTabla(tabla1, new Paragraph(contrato?.objeto ?: '', fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tabla1, new Paragraph("MONTO DEL CONTRATO:", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])

        PdfPTable inner1 = new PdfPTable(3);
        addCellTabla(inner1, new Paragraph(df.format(contrato?.monto) + "", fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner1, new Paragraph("FECHA DE INICIO CONTRACTUAL:", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner1, new Paragraph(contrato?.obra?.fechaInicio?.format("dd/MM/yyyy"), fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla1, inner1, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tabla1, new Paragraph("PLAZO:", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])

        PdfPTable inner2 = new PdfPTable(3);
        addCellTabla(inner2, new Paragraph((contrato?.plazo?.toInteger() + " días") ?: '', fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner2, new Paragraph("FECHA DE TÉRMINO CONTRACTUAL:", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner2, new Paragraph( (fin ?: ''), fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla1, inner2, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tabla1, new Paragraph("", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 4, height: 15])

        addCellTabla(tabla1, new Paragraph("De acuerdo al " +
                "informe técnico de Fiscalización en el memorando N°" + (planilla?.memoOrden ?: '') + ", se ha establecido " +
                "la necesidad de ejecutar diferencias de cantidades de obra en el Contrato Original, con la " +
                "finalidad de cumplir efectivamente con el " +
                "objeto del contrato", fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 4, height: 30])

        document.add(tabla1)

//        sql = "select * from detalle(${planilla?.contrato?.id}, ${planilla?.contrato?.obra?.id}, ${planilla.id}, '${"T"}')"
        sql = "select rbrocdgo, rbronmbr, unddcdgo, vocrcntd, cntdacml - cntdantr - vocrcntd diff, vocrpcun, vloracml-vlorantr-vocrsbtt vlor from detalle(${planilla?.contrato?.id}, ${planilla?.contrato?.obra?.id}, ${planilla?.id}, 'P') where (cntdacml - cntdantr) > vocrcntd ;"
        def vocr = cn2.rows(sql.toString())


        def existe = 0

        PdfPTable tabla2 = new PdfPTable(7);
        tabla2.setWidthPercentage(100);
        tabla2.setWidths(arregloEnteros([12,35,5,11,12,12,12]))

        addCellTabla(tabla2, new Paragraph("DIFERENCIA DE CANTIDADES DE OBRA", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 7, height: 20])

        addCellTabla(tabla2, new Paragraph("ITEM", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 20])
        addCellTabla(tabla2, new Paragraph("DESCRIPCIÓN", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE,  height: 20])
        addCellTabla(tabla2, new Paragraph("U", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 20])
        addCellTabla(tabla2, new Paragraph("CANTIDAD CONTRATADA", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE,  height: 20])
        addCellTabla(tabla2, new Paragraph("DIFERENCIA DE CANTIDAD", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 20])
        addCellTabla(tabla2, new Paragraph("PRECIO UNITARIO", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 20])
        addCellTabla(tabla2, new Paragraph("PRECIO TOTAL", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 20])

        def total = 0

        vocr.each {mk ->
            addCellTabla(tabla2, new Paragraph(mk?.rbrocdgo ?: '', fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 15])
            addCellTabla(tabla2, new Paragraph(mk?.rbronmbr ?: '', fontThTinyN2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE,  height: 15])
            addCellTabla(tabla2, new Paragraph(mk?.unddcdgo ?: '', fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 15])
            addCellTabla(tabla2, new Paragraph((mk?.vocrcntd ?: '')+ '', fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE,  height: 15])
            addCellTabla(tabla2, new Paragraph((mk?.diff ?: '') + '', fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 15])
            addCellTabla(tabla2, new Paragraph(mk?.vocrpcun + "", fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 15])
            addCellTabla(tabla2, new Paragraph(mk?.vlor + "", fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 15])
            total += (mk?.vlor ?: 0)
        }

        def porcentaje = (total / contrato.monto) * 100

        addCellTabla(tabla2, new Paragraph("TOTAL DE EXCEDENTES DE OBRA", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 6, height: 20])
        addCellTabla(tabla2, new Paragraph(numero(total,2), fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE,  height: 20])

        addCellTabla(tabla2, new Paragraph("PORCENTAJE DEL MONTO DEL CONTRATO", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 6, height: 20])
        addCellTabla(tabla2, new Paragraph(numero(porcentaje, 2), fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE,  height: 20])


        addCellTabla(tabla2, new Paragraph("", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 7, height: 15])

        addCellTabla(tabla2, new Paragraph("Valor que cuenta con la Certificación Presupuestaria N°" + (planilla?.numeroCertificacionOrden ?: '') + ", de fecha " + (planilla?.fechaCertificacionOrden?.format("dd/MM/yyyy") ?: '') + " suscrito por la Coordinación de Administración Presupuestaria", fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 7, height: 15])

        addCellTabla(tabla2, new Paragraph("Adicionalmente el contratista emite la siguiente garantía: " + (planilla?.garantiaOrden ?: ''), fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 7, height: 15])

        addCellTabla(tabla2, new Paragraph("El presente documento se inscribe en la Ley Orgánica para la Eficiencia Sistema Nacional de Contratación Pública, 'Art.9 - Diferencia de Cantidades de Obra, que señala que la " +
                "entidad contratante podrá ordenar y pagar directamente sin necesidad de contrato complementario, hasta el cinco (5%) por ciento del valor reajustado del contrato, " +
                "siempre que no se modifique el objeto contractual'.", fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 7, height: 30])

        addCellTabla(tabla2, new Paragraph("", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 7, height: 15])

        addCellTabla(tabla2, new Paragraph("Y de acuerdo al memorando N° 2501-DGCP-17, suscrito por el Director de Gestión de Compras Públicas, delegado del señor Prefecto, " +
                "en el que se indica que las 'PARTES' que señala en la Ley Orgánica para la Eficiencia en la Contratación Pública son: la Entidad seccional autónoma como " +
                "contratante y otra persona natural o jurídica como contratista, por lo tanto el GAD de la Provincia de Pichincha, constituye una 'parte' del contrato " +
                "administrativo, representado por el Administrador del contrato, se procesde a suscribit este documento (3 originales) en unidad de acto." , fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 7, height: 40])

        document.add(tabla2)

        PdfPTable tabla3 = new PdfPTable(7);
        tabla3.setWidthPercentage(100);
        tabla3.setWidths(arregloEnteros([8,23,8,23,8,22,8]))

        addCellTabla(tabla3, new Paragraph("Dado en Quito, " + rep.fechaConFormato(fecha: planilla?.fechaCertificacionOrden, formato: "dd MMMM yyyy").toString(), fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.WHITE, bcl: Color.BLACK, bcr: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 7])

        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.BLACK, bcr: Color.WHITE,  bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE,bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE,bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE,bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE,bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.BLACK, bct: Color.WHITE,bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])

        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.BLACK, bcr: Color.WHITE, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph((contrato?.administradorContrato?.administrador?.titulo?.toUpperCase() ?: '') + " " + (contrato?.administradorContrato?.administrador?.nombre ?: '') + " " + (contrato?.administradorContrato?.administrador?.apellido ?: ''), fontThTiny2), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph(strContratista?.toUpperCase() ?: '' , fontThTiny2), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE,bwb: 0.1, bcb: Color.WHITE, bct: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph((contrato?.fiscalizador?.titulo?.toUpperCase() ?: '') + " " + (contrato?.fiscalizador?.nombre ?: '') + " " + (contrato?.fiscalizador?.apellido ?: ''), fontThTiny2), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE,bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 30])
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
        response.setHeader("Content-disposition", "attachment; filename=" + 'ordenDeCambio_' + new Date().format("dd-MM-yyyy"))
        response.setContentLength(b.length)
        response.getOutputStream().write(b)

    }


    def reporteOrdenDeTrabajo () {

        def planilla = Planilla.get(params.id).refresh()
        def contrato = planilla.contrato
        def contratista = contrato.oferta.proveedor
        def strContratista = nombrePersona(contratista, "prov")

        def detalles = DetallePlanillaCosto.findAllByPlanilla(planilla)

        def cn = dbConnectionService.getConnection()
        def sql = "select max(prejfcfn) fecha from prej where prejtipo in ('A', 'P') and cntr__id = ${contrato?.id};"
        def res = cn.rows(sql.toString())
        def fin = res?.first()?.fecha?.format("dd/MM/yyyy")

        def baos = new ByteArrayOutputStream()

        BaseColor colorAzul = new BaseColor(50, 96, 144)

        Font fontTitle = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Font fontThTiny = new Font(Font.TIMES_ROMAN, 7, Font.BOLD);
        Font fontThTinyN = new Font(Font.TIMES_ROMAN, 7, Font.NORMAL);
        Font fontThTiny2 = new Font(Font.TIMES_ROMAN, 6, Font.BOLD);
        Font fontThTinyN2 = new Font(Font.TIMES_ROMAN, 6, Font.NORMAL);

        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def borderWidth = 0.3

        Locale loc = new Locale("en_US")
        java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(loc);
        DecimalFormat df = (DecimalFormat)nf;
//        df.applyPattern("\$##,###.##");
        df.applyPattern("##,###.##");

        Document document
        document = new Document(PageSize.A4);
        document.setMargins(50,30,30,28)  // 28 equivale a 1 cm: izq, derecha, arriba y abajo
        def pdfw = PdfWriter.getInstance(document, baos);

        document.open();
        document.addTitle("");
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, orden de trabajo");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");


        PdfPTable tabla1 = new PdfPTable(4);
        tabla1.setWidthPercentage(100);
        tabla1.setWidths(arregloEnteros([15,13,47,15]))

        addCellTabla(tabla1, new Paragraph("GOBIERNO AUTÓNOMO DESCENTRALIZADO DE LA PROVINCIA DE PICHINCHA", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 4, height: 30])

        addCellTabla(tabla1, new Paragraph("ORDEN DE TRABAJO N° " + (planilla?.numeroTrabajo ?: '') , fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 4, height: 20])

        addCellTabla(tabla1, new Paragraph("CONTRATO N°", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(tabla1, new Paragraph(contrato?.codigo ?: '', fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tabla1, new Paragraph("CONTRATISTA:", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(tabla1, new Paragraph(contrato?.contratista?.nombre ?: '', fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tabla1, new Paragraph("OBJETO DEL CONTRATO:", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2, height: 20])
        addCellTabla(tabla1, new Paragraph(contrato?.objeto ?: '', fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tabla1, new Paragraph("MONTO DEL CONTRATO:", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])

        PdfPTable inner1 = new PdfPTable(3);
        addCellTabla(inner1, new Paragraph(df.format(contrato?.monto) + "", fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner1, new Paragraph("FECHA DE INICIO CONTRACTUAL:", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner1, new Paragraph(contrato?.obra?.fechaInicio?.format("dd/MM/yyyy"), fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla1, inner1, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tabla1, new Paragraph("PLAZO:", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])

        PdfPTable inner2 = new PdfPTable(3);
        addCellTabla(inner2, new Paragraph((contrato?.plazo?.toInteger() + " días") ?: '', fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner2, new Paragraph("FECHA DE TÉRMINO CONTRACTUAL:", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner2, new Paragraph( ( (fin ?: '') + " ") ?: '', fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla1, inner2, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tabla1, new Paragraph("", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 4, height: 15])

        addCellTabla(tabla1, new Paragraph("De acuerdo al " +
                "informe técnico de Fiscalización en el memorando N° " + (planilla?.memoTrabajo ?: '')+ ", se ha establecido " +
                "la necesidad de ejecutar rubros nuevos  en el Contrato Original, con la " +
                "finalidad de cumplir efectivamente con el " +
                "objeto del contrato", fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 4, height: 30])

        document.add(tabla1)

        def total = 0

        PdfPTable tabla2 = new PdfPTable(6);
        tabla2.setWidthPercentage(100);
        tabla2.setWidths(arregloEnteros([10,44,6,10,12,12]))

        addCellTabla(tabla2, new Paragraph("RUBROS NUEVOS", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 6, height: 20])

        addCellTabla(tabla2, new Paragraph("ITEM", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 20])
        addCellTabla(tabla2, new Paragraph("DESCRIPCIÓN", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE,  height: 20])
        addCellTabla(tabla2, new Paragraph("U", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 20])
        addCellTabla(tabla2, new Paragraph("CANTIDAD", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE,  height: 20])
        addCellTabla(tabla2, new Paragraph("PRECIO REFERENCIAL", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 20])
        addCellTabla(tabla2, new Paragraph("PRECIO TOTAL", fontThTiny2), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 20])

        detalles.each{ dt->
            def tot = dt.monto + dt.montoIndirectos
            addCellTabla(tabla2, new Paragraph(dt?.factura, fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 15])
            addCellTabla(tabla2, new Paragraph(dt?.rubro, fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE,  height: 15])
            addCellTabla(tabla2, new Paragraph(dt?.unidad?.codigo, fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 15])
            addCellTabla(tabla2, new Paragraph(numero(dt?.cantidad, 2), fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE,  height: 15])
            addCellTabla(tabla2, new Paragraph(numero((tot / dt?.cantidad),2) + "", fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 15])
            addCellTabla(tabla2, new Paragraph(numero(tot,2) + "", fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 15])
            total += (tot ?: 0)
        }

        def porcentaje = (total / contrato.monto) *100

        addCellTabla(tabla2, new Paragraph("TOTAL DE RUBROS NUEVOS", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5, height: 20])
        addCellTabla(tabla2, new Paragraph(numero(total,2), fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE,  height: 20])

        addCellTabla(tabla2, new Paragraph("PORCENTAJE RESPECTO AL MONTO DEL CONTRATO", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5, height: 20])
        addCellTabla(tabla2, new Paragraph(numero(porcentaje, 2), fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE,  height: 20])

        addCellTabla(tabla2, new Paragraph("", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 7, height: 15])

        addCellTabla(tabla2, new Paragraph("Valor que cuenta con la Certificación Presupuestaria N° " + (planilla?.numeroCertificacionTrabajo ?: '')+ ", de fecha " + (planilla?.fechaCertificacionTrabajo?.format("dd/MM/yyyy") ?: '') + " suscrito por la Coordinación de Administración Presupuestaria", fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 6, height: 15])

        addCellTabla(tabla2, new Paragraph("Adicionalmente el contratista emite la siguiente garantía de FIEL CUMPLIMIENTO  N° " + (planilla?.garantiaTrabajo ?: ''), fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 6, height: 15])

        addCellTabla(tabla2, new Paragraph("El presente documento se inscribe en la Ley Orgánica para la eficiencia del Sistema Nacional de Contratación Pública, 'Art.10.- ORDENES DE TRABAJO:- La Entidad " +
                "Contratante podrá disponer, durante la ejecución de la obra, hasta del dos (2%) por ciento del valor actualizado o reajustado del contrato principal, para la realización " +
                "de rubros nuevos, mediante órdenes de trabajo y empleando la modalidad de costo más porcentaje. En todo caso, los rescursos deberán estar presupuestados de conformidad con la " +
                "presente Ley. Las órdenes de trabajo contendrán las firmas de las partes dy de la fiscalización'.", fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 6, height: 40])

        addCellTabla(tabla2, new Paragraph("", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 6, height: 15])

        addCellTabla(tabla2, new Paragraph("Y de acuerdo al memorando N° 2501-DGCP-17, suscrito por el Director de Gestión de Compras Públicas, delegado del señor Prefecto, " +
                "en el que se indica que las 'PARTES' que señala en la Ley Orgánica para la Eficiencia en la Contratación Pública son: la Entidad seccional autónoma como " +
                "contratante y otra persona natural o jurídica como contratista, por lo tanto el GAD de la Provincia de Pichincha, constituye una 'parte' del contrato " +
                "administrativo, representado por el Administrador del contrato, se procesde a suscribit este documento (3 originales) en unidad de acto." , fontThTinyN), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 6, height: 40])

        document.add(tabla2)

        PdfPTable tabla3 = new PdfPTable(7);
        tabla3.setWidthPercentage(100);
        tabla3.setWidths(arregloEnteros([8,23,8,23,8,22,8]))

        addCellTabla(tabla3, new Paragraph("Dado en Quito, " + rep.fechaConFormato(fecha: planilla?.fechaCertificacionTrabajo, formato: "dd MMMM yyyy").toString(), fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.WHITE, bcl: Color.BLACK, bcr: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 7])

        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.BLACK, bcr: Color.WHITE,  bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE,bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE,bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE, bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE,bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE,bwb: 0.1, bcb: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.BLACK, bct: Color.WHITE,bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 50])

        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.BLACK, bcr: Color.WHITE, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph((contrato?.administradorContrato?.administrador?.titulo?.toUpperCase() ?: '') + " " + (contrato?.administradorContrato?.administrador?.nombre ?: '') + " " + (contrato?.administradorContrato?.administrador?.apellido ?: ''), fontThTiny2), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph(strContratista?.toUpperCase() ?: '' , fontThTiny2), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph("", fontThTiny), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE,bwb: 0.1, bcb: Color.WHITE, bct: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, height: 30])
        addCellTabla(tabla3, new Paragraph((contrato?.fiscalizador?.titulo?.toUpperCase() ?: '') + " " + (contrato?.fiscalizador?.nombre ?: '') + " " + (contrato?.fiscalizador?.apellido ?: ''), fontThTiny2), [border: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE,bwb: 0.1, bcb: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, height: 30])
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
        response.setHeader("Content-disposition", "attachment; filename=" + 'ordenDeTrabajo_' + new Date().format("dd-MM-yyyy"))
        response.setContentLength(b.length)
        response.getOutputStream().write(b)

    }


    private String cap(str) {
        return str.replaceAll(/[a-zA-Z_0-9áéíóúÁÉÍÓÚñÑüÜ]+/, {
            it[0].toUpperCase() + ((it.size() > 1) ? it[1..-1].toLowerCase() : '')
        })
    }

    private String nombrePersona(persona, tipo) {
        def str = ""
        if (persona) {
            switch (tipo) {
                case "pers":
                    str = cap((persona.titulo ? persona.titulo + " " : "") + persona.nombre + " " + persona.apellido)
                    break;
                case "prov":
                    if(persona.tipo == 'N'){
                        str = cap((persona.titulo ? persona.titulo + " " : "") + persona.nombreContacto + " " + persona.apellidoContacto)
                    } else {
                        str = cap(persona.nombreContacto)
                    }
                    break;
            }
        }
        return str
    }


    def graficoAvance () {
        def cn = dbConnectionService.getConnection()
        def data = []
        def cont = 0

        def sql = "select cntnnmbr, sum(cntrmnto) contratado, avg(avncecon)::numeric(6,2)*100 economico, " +
                "avg(avncfsco)::numeric(6,2) fisico from rp_contrato() group by cntnnmbr order by 2 desc;"
        def datos = cn.rows(sql.toString())

        com.itextpdf.text.Font fontTitulo = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 14, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font fontTtlo = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 18, com.itextpdf.text.Font.BOLD);

        def tipo = params.tipo
        def subtitulo = 'AVANCE DE OBRAS'
        def tituloArchivo = 'Por Cantón'

        data = []

        cn.eachRow(sql.toString()) { d ->
            data.add([nmro: cont, nmbr: d.cntnnmbr, vlor: d.contratado, econ: d.economico, fsco: d.fisico])
            cont++
        }
//        println "data: $data"

        def baos = new ByteArrayOutputStream()

        com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4);
        def pdfw = com.itextpdf.text.pdf.PdfWriter.getInstance(document, baos);

        document.open();

        com.itextpdf.text.Paragraph parrafoUniversidad = new com.itextpdf.text.Paragraph("GOBIERNO DE PICHINCHA", fontTitulo)
        parrafoUniversidad.setAlignment(com.lowagie.text.Element.ALIGN_CENTER)
        com.itextpdf.text.Paragraph parrafoFacultad = new com.itextpdf.text.Paragraph("", fontTitulo)
        parrafoFacultad.setAlignment(com.lowagie.text.Element.ALIGN_CENTER)
        com.itextpdf.text.Paragraph parrafoEscuela = new com.itextpdf.text.Paragraph("", fontTitulo)
        parrafoEscuela.setAlignment(com.lowagie.text.Element.ALIGN_CENTER)
        com.itextpdf.text.Paragraph linea = new com.itextpdf.text.Paragraph(" ", fontTitulo)
        parrafoFacultad.setAlignment(com.lowagie.text.Element.ALIGN_CENTER)

        com.itextpdf.text.Paragraph titulo = new com.itextpdf.text.Paragraph(subtitulo, fontTtlo)
        titulo.setAlignment(com.lowagie.text.Element.ALIGN_CENTER)

        document.add(parrafoUniversidad)
        document.add(parrafoFacultad)
        document.add(parrafoEscuela)
        document.add(linea)
//        document.add(titulo)

        final CategoryDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);
        def ancho = 500
        def alto = 300

        try {

            PdfContentByte contentByte = pdfw.getDirectContent();

            com.itextpdf.text.Paragraph parrafo1 = new com.itextpdf.text.Paragraph();
            com.itextpdf.text.Paragraph parrafo2 = new com.itextpdf.text.Paragraph();

            PdfTemplate template = contentByte.createTemplate(ancho, alto);
            PdfTemplate template2 = contentByte.createTemplate(ancho, alto/10);
            Graphics2D graphics2d = template.createGraphics(ancho, alto, new DefaultFontMapper());
            Graphics2D graphics2d2 = template2.createGraphics(ancho, alto/10, new DefaultFontMapper());
            Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, ancho, alto);

            //color
            CategoryPlot plot = chart.getCategoryPlot();
            BarRenderer renderer = (BarRenderer) plot.getRenderer();

            Color color = new Color(79, 129, 189);
            renderer.setSeriesPaint(0, color);

            chart.draw(graphics2d, rectangle2d);

            graphics2d.dispose();
            Image chartImage = Image.getInstance(template);
            parrafo1.add(chartImage);

            graphics2d2.dispose();
            Image chartImage3 = Image.getInstance(template2);
            parrafo2.add(chartImage3);

            document.add(parrafo1)
            document.add(parrafo2)


        } catch (Exception e) {
            e.printStackTrace();
        }

        float[] columnas = [18,75,30,20,20]

        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(columnas); // 3 columns.
        table.setWidthPercentage(100);
        com.itextpdf.text.pdf.PdfPTable table2 = new com.itextpdf.text.pdf.PdfPTable(columnas); // 3 columns.
        table2.setWidthPercentage(100);

        com.itextpdf.text.pdf.PdfPCell cell1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Leyenda"))
        com.itextpdf.text.pdf.PdfPCell cell2 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Cantón"));
        com.itextpdf.text.pdf.PdfPCell cell3 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Valor Total Contratado"));
        com.itextpdf.text.pdf.PdfPCell cell4 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Avance Económico"));
        com.itextpdf.text.pdf.PdfPCell cell5 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Avance Físico"));

        cell1.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER)
        cell2.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER)
        cell1.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_CENTER)
        cell2.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_CENTER)
        cell3.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_CENTER)
        cell4.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_CENTER)
        cell5.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_CENTER)
        cell3.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER)
        cell4.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER)
        cell5.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER)

        cell1.setBackgroundColor(BaseColor.LIGHT_GRAY)
        cell2.setBackgroundColor(BaseColor.LIGHT_GRAY)
        cell3.setBackgroundColor(BaseColor.LIGHT_GRAY)
        cell4.setBackgroundColor(BaseColor.LIGHT_GRAY)
        cell5.setBackgroundColor(BaseColor.LIGHT_GRAY)

        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);

        data.each { d ->
            table2.addCell(crearCelda(tipo, 'G' + (d.nmro), 'C' + (d.nmro + 1)))
//            println "------> ${d.fsco}, ${d.econ}"
            table2.addCell(crearCeldaTexto(d.nmbr))
            table2.addCell(crearCeldaNumero(numero(d.vlor, 2)))
            table2.addCell(crearCeldaNumero(numero(d.econ, 2) + '%'))
            table2.addCell(crearCeldaNumero(numero(d.fsco, 2) + '%'))
        }

        document.add(table);
        document.add(table2);

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + "reporte_de_avance_" + new Date().format("dd-MM-yyyy") + ".pdf")
        response.setContentLength(b.length)
        response.getOutputStream().write(b)

    }

    def crearCelda (tipo,g,e) {
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(tipo == '1' ? g : e));
        cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER)
        return cell
    }

    def crearCeldaTexto (txt) {
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(txt));
        return cell
    }

    def crearCeldaNumero (txt) {
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(txt));
        cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT)
        return cell
    }


    private JFreeChart createChart(final CategoryDataset dataset) {

        final JFreeChart chart = ChartFactory.createBarChart("Avance de Obras Contratadas", // chart
                // title
                "Cantones", // domain axis label
                "Contratado", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
        );

        chart.setBackgroundPaint(Color.white);

        final CategoryPlot plot = chart.getCategoryPlot();
//        plot.setBackgroundPaint(Color.lightGray);
        plot.setBackgroundPaint(Color.white);
//        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        final GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, Color.lightGray);
        final GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green, 0.0f, 0.0f, Color.lightGray);
        final GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red, 0.0f, 0.0f, Color.lightGray);
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

        return chart;
    }


    private CategoryDataset createDataset() {

        def cn = dbConnectionService.getConnection()
        def data = [:]
        def parts1 = []
        def parts2 = []

        def sql = "select cntnnmbr, sum(cntrmnto) contratado, avg(avncecon)::numeric(6,2)*100 economico, " +
                "avg(avncfsco)::numeric(6,2) fisico from rp_contrato() group by cntnnmbr order by 2 desc"
        def datos = cn.rows(sql.toString())
        data = [:]
        cn.eachRow(sql.toString()) { d ->
            data.put((d.cntnnmbr), d.contratado + "_" + d.economico + "_" + d.fisico )
        }

        def tam = data.size()
        def ges = []
        def ees = []

        tam.times{
            ees.add('C' + (it + 1))
        }

        final String series1 = "Contratado";
        final String series2 = "Avance Económico";
        final String series3 = "Avance Físico";

        final String category1 = "Category 1";
        final String category2 = "Category 2";
        final String category3 = "Category 3";
        final String category4 = "Category 4";
        final String category5 = "Category 5";

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();


        data.eachWithIndex { q, k ->

//            println("q " + q + " k " + k)

            parts1[k] = q.value.split("_")
            parts2[k] = q.key

            //1
            dataset.addValue( parts1[k][0].toDouble() , series1 ,  ees[k]);
            //2
            dataset.addValue( (parts1[k][1].toDouble() * parts1[k][0].toDouble() / 100)  , series2 ,  ees[k]);
            //3
            dataset.addValue( (parts1[k][2].toDouble() * parts1[k][0].toDouble() / 100) , series3 ,  ees[k]);
        }

        return dataset;
    }







}
