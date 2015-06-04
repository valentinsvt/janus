package janus

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.HeaderFooter
import com.lowagie.text.Image
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.Phrase
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import janus.ejecucion.DetallePlanilla
import janus.ejecucion.DetallePlanillaAdmin
import janus.ejecucion.DetallePlanillaCostoAdmin
import janus.ejecucion.PlanillaAdmin

import java.awt.Color

class ReportesPlanillasAdminController {

    def preciosService

    def index() {}

    def reporteDetalle() {
        def obra = Obra.get(params.obra.toLong())
        def planilla = PlanillaAdmin.get(params.id.toLong())

        def planillasAnteriores = PlanillaAdmin.withCriteria {
            eq("obra", obra)
            lt("fechaIngreso", planilla.fechaIngreso)
            order("fechaIngreso", "desc")
        }
        def planillaAnterior = null
        if (planillasAnteriores.size() > 0) {
            planillasAnteriores.first()
        }

        def baos = new ByteArrayOutputStream()
        def name = "detalles_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font fontInfo = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)
        Font fontFooter = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font fontThHeader = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font fontTdHeader = new Font(Font.TIMES_ROMAN, 12, Font.NORMAL);
        Font fontThTiny = new Font(Font.TIMES_ROMAN, 7, Font.BOLD);
        Font fontTdTiny = new Font(Font.TIMES_ROMAN, 7, Font.NORMAL);
        Font fontTdTiny2 = new Font(Font.TIMES_ROMAN, 5, Font.NORMAL);

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        Document document
        document = new Document(PageSize.A4);
        document.setMargins(56.2, 30, 50, 28.1);
        // margins: left, right, top, bottom
        // 1 in = 72, 1cm=28.1, 3cm = 86.4
        def pdfw = PdfWriter.getInstance(document, baos);

        // headers and footers must be added before the document is opened
        HeaderFooter footer1 = new HeaderFooter(new Phrase("Manuel Larrea N. 13-45 y Antonio Ante / Teléfonos troncal: (593-2)252 7077 - 254 9222 - 254 9020 - 254 9163 / www.pichincha.gob.ec", fontFooter), false);
        // true aqui pone numero de pagina
        footer1.setBorder(Rectangle.NO_BORDER);
        footer1.setBorder(Rectangle.TOP);
        footer1.setAlignment(Element.ALIGN_CENTER);
        document.setFooter(footer1);

        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Detalles " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

//        Paragraph preface = new Paragraph();
//        addEmptyLine(preface, 1);
//        preface.setAlignment(Element.ALIGN_CENTER);
//        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
//        addEmptyLine(preface, 1);
//        document.add(preface);

        /* *********************************** DETALLES **********************************/
        PdfPTable tablaDetalles = null
        def borderWidth = 1

        def printHeaderDetalle = { params ->
            def tablaHeaderDetalles = new PdfPTable(11);
            tablaHeaderDetalles.setWidthPercentage(100);
            tablaHeaderDetalles.setWidths(arregloEnteros([15, 30, 4, 8, 11, 11, 11, 15, 11, 11, 15]))
            tablaDetalles.setWidthPercentage(100);
//            tablaDetalles.setWidths(arregloEnteros([16, 35, 3, 8, 11, 11, 11, 12, 11, 12, 12]))

            addCellTabla(tablaHeaderDetalles, new Paragraph("Obra", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph(obra.nombre, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 10])

            addCellTabla(tablaHeaderDetalles, new Paragraph("Lugar", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph((obra.lugar?.descripcion ?: ""), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 6])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Planilla", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph(planilla.numero, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
//            addCellTabla(tablaHeaderDetalles, new Paragraph(" ", fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaHeaderDetalles, new Paragraph("Ubicación", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Parroquia " + obra.parroquia?.nombre + " Cantón " + obra.parroquia?.canton?.nombre, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 6])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Monto", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph(numero(obra.valor, 2), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Pág. " + params.pag + " de " + params.total, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaHeaderDetalles, new Paragraph("Contratista", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Administración directa", fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 6])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Fecha", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph(fechaConFormato(planilla.fechaIngreso, "dd-MM-yyyy"), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])
            addCellTabla(tablaHeaderDetalles, new Paragraph(" ", fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaDetalles, logo, [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, tablaHeaderDetalles, [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 10, pl: 40])

            addCellTabla(tablaDetalles, new Paragraph(" ", fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 11])

            addCellTabla(tablaDetalles, new Paragraph("N.", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph("Descripción del rubro", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, bwr: borderWidth, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph("U", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph("Precio unit.", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph("Volumen contratado", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

            PdfPTable inner6 = new PdfPTable(3);
            addCellTabla(inner6, new Paragraph("Cantidades", fontThTiny), [border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
            addCellTabla(inner6, new Paragraph("Anterior", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(inner6, new Paragraph("Actual", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(inner6, new Paragraph("Acumulado", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, inner6, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])

            PdfPTable inner7 = new PdfPTable(3);
            addCellTabla(inner7, new Paragraph("Valores", fontThTiny), [border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
            addCellTabla(inner7, new Paragraph("Anterior", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(inner7, new Paragraph("Actual", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(inner7, new Paragraph("Acumulado", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, inner7, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
        }
        def printFooterDetalle = { params ->

            Font fontThFooter = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
            def txt = "AVANCE DE OBRA PARCIAL"
            if (params.completo) {
                txt = "AVANCE DE OBRA"
            }

            addCellTabla(tablaDetalles, new Paragraph(txt, fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
            addCellTabla(tablaDetalles, new Paragraph(numero(params.ant, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(params.act, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(params.acu, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

            if (params.completo) {

                def bAnt = 0
                def bAct = 0
                def bAcu = bAct + bAnt

                def cpAnt = 0
                def cpAct = 0
                def cpAcu = cpAnt + cpAct

                def smAnt = params.ant + cpAnt
                def smAct = params.act + cpAct
                def smAcu = params.acu + cpAcu

                def cAnt = params.ant + bAnt + cpAnt
                def cAct = params.act + bAct + cpAct
                def cAcu = params.acu + bAcu + cpAcu

                def dAnt = 0
                if (planillasAnteriores.size() > 1) {
                    planillasAnteriores[0..planillasAnteriores.size() - 2].sum { it.descuentos } ?: 0
                }
                def d = 0

                def antAnt = dAnt
                def antAct = d
                def antAcu = d + dAnt

                def totalAnt = cAnt - antAnt
                def totalAct = cAct - antAct
                def totalAcu = cAcu - antAcu

                def sbtAnt = bAnt + params.ant
                def sbtAct = bAct + params.act
                def sbtAcu = bAcu + params.acu

//                addCellTabla(tablaDetalles, new Paragraph("SUBTOTAL", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
//                addCellTabla(tablaDetalles, new Paragraph(numero(sbtAnt, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
//                addCellTabla(tablaDetalles, new Paragraph(numero(sbtAct, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
//                addCellTabla(tablaDetalles, new Paragraph(numero(sbtAcu, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaDetalles, new Paragraph("TOTAL", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                addCellTabla(tablaDetalles, new Paragraph(numero(totalAnt, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(totalAct, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(totalAcu, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            }
        }

        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def precios = [:]
        def indirecto = obra.totales / 100
        preciosService.ac_rbroObra(obra.id)
        detalle.each {
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
        }
        def totalAnterior = 0, totalActual = 0, totalAcumulado = 0, sp = null
        def height = 12
        def maxRows = 45
        def extraRows = 9
        def currentRows = 0

        def sps = detalle.subPresupuesto.unique()
        def totalRows = detalle.size() + sps.size()

        def maxRowsLastPag = maxRows - extraRows

        def totalPags = Math.ceil(totalRows / maxRows).toInteger()

        if (totalRows % maxRows >= maxRowsLastPag) {
            totalPags++
        }

        def currentPag = 1

        def sumaParcialAnterior = 0, sumaTotalAnterior = 0
        def sumaParcialActual = 0, sumaTotalActual = 0
        def sumaParcialAcumulado = 0, sumaTotalAcumulado = 0

        def rowsCurPag = 1
        detalle.eachWithIndex { vol, i ->
            def det = DetallePlanillaAdmin.findByPlanillaAndVolumenObra(planilla, vol)
            def anteriores = []
            if (planillasAnteriores.size() > 1) {
                anteriores = DetallePlanillaAdmin.findAllByPlanillaInListAndVolumenObra(planillasAnteriores[0..planillasAnteriores.size() - 2], vol)
            }
            def cantAnt = anteriores.sum { it.cantidad } ?: 0
            def valAnt = anteriores.sum { it.monto } ?: 0
            def cant = det?.cantidad ?: 0
            def val = det?.monto ?: 0
            totalAnterior += valAnt
            totalActual += (val.toDouble().round(2))
            totalAcumulado += (val.toDouble().round(2) + valAnt)
            if ((currentPag == totalPags && rowsCurPag == maxRowsLastPag) || (currentRows % maxRows == 0)) {
//                if (currentPag != 1) {
                document.newPage()
//                }

                tablaDetalles = new PdfPTable(11);
                tablaDetalles.setWidthPercentage(100);
                tablaDetalles.setWidths(arregloEnteros([16, 35, 3, 8, 11, 11, 11, 12, 11, 12, 12]))
//                tablaDetalles.setWidths(arregloEnteros([15, 35, 5, 11, 11, 11, 11, 11, 11, 12, 12]))

                tablaDetalles.setSpacingAfter(1f);

                printHeaderDetalle([pag: currentPag, total: totalPags])
                rowsCurPag = 1
            }
            if (sp != vol.subPresupuestoId) {
                addCellTabla(tablaDetalles, new Paragraph(vol.subPresupuesto.descripcion, fontThTiny), [height: height, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
                addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                sp = vol.subPresupuestoId
                currentRows++
                rowsCurPag++
            }
//            addCellTabla(tablaDetalles, new Paragraph(vol.item.codigo, fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(vol.item.codigo, fontTdTiny2), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(vol.item.nombre, fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, bwr: borderWidth, border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaDetalles, new Paragraph(vol.item.unidad.codigo, fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(precios[vol.id.toString()], 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(vol.cantidad, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, bwr: borderWidth, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaDetalles, new Paragraph(numero(cantAnt, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(cant, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(cant + cantAnt, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, bwr: borderWidth, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaDetalles, new Paragraph(numero(valAnt, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(val, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(val + valAnt, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

            sumaParcialAnterior += valAnt
            sumaParcialActual += val
            sumaParcialAcumulado += (val + valAnt)
            sumaTotalAnterior += valAnt
            sumaTotalActual += val
            sumaTotalAcumulado += (val + valAnt)

            currentRows++
            rowsCurPag++

            if ((currentPag == totalPags - 1 && rowsCurPag % maxRowsLastPag == maxRowsLastPag - 1) || (currentRows % maxRows == maxRows - 1)) {
                //footer aca
                if (currentPag == totalPags) {
                    printFooterDetalle(ant: sumaTotalAnterior, act: sumaTotalActual, acu: sumaTotalAcumulado, completo: true)
                } else {
                    printFooterDetalle(ant: sumaParcialAnterior, act: sumaParcialActual, acu: sumaParcialAcumulado)
                }
                sumaParcialAnterior = 0
                sumaParcialActual = 0
                sumaParcialAcumulado = 0

                document.add(tablaDetalles)

//                printFirmas([tipo: "detalle", orientacion: "vertical"])
                currentPag++
            }
        }
        if (currentRows % maxRows < maxRows - 1) {
            printFooterDetalle(ant: sumaTotalAnterior, act: sumaTotalActual, acu: sumaTotalAcumulado, completo: true)

            sumaParcialAnterior = 0
            sumaParcialActual = 0
            sumaParcialAcumulado = 0

            document.add(tablaDetalles)

//            printFirmas([tipo: "detalle", orientacion: "vertical"])
        }

        /* *********************************** FIN DETALLES **********************************/

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def reporteMateriales() {
        def obra = Obra.get(params.obra.toLong())
        def planilla = PlanillaAdmin.get(params.id.toLong())

        def baos = new ByteArrayOutputStream()
        def name = "materiales_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font fontInfo = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)
        Font fontFooter = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font fontTh = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
        Font fontThHeader = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font fontTdHeader = new Font(Font.TIMES_ROMAN, 12, Font.NORMAL);

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        Document document
        document = new Document(PageSize.A4);
        document.setMargins(56.2, 56.2, 50, 28.1);
        // margins: left, right, top, bottom
        // 1 in = 72, 1cm=28.1, 3cm = 86.4
        def pdfw = PdfWriter.getInstance(document, baos);

        // headers and footers must be added before the document is opened
        HeaderFooter footer1 = new HeaderFooter(new Phrase("Manuel Larrea N. 13-45 y Antonio Ante / Teléfonos troncal: (593-2)252 7077 - 254 9222 - 254 9020 - 254 9163 / www.pichincha.gob.ec", fontFooter), false);
        // true aqui pone numero de pagina
        footer1.setBorder(Rectangle.NO_BORDER);
        footer1.setBorder(Rectangle.TOP);
        footer1.setAlignment(Element.ALIGN_CENTER);
        document.setFooter(footer1);

        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Materiales " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
        preface.add(new Paragraph("Planilla de materiales de " + obra.nombre, fontTituloGad));
        addEmptyLine(preface, 1);
//        Paragraph preface2 = new Paragraph();
//        preface2.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), fontInfo))
//        addEmptyLine(preface2, 1);
        document.add(logo)
        document.add(preface);
//        document.add(preface2);

        /* *********************************** DATOS OBRA **********************************/
        PdfPTable tablaDatosObra = new PdfPTable(5);
        tablaDatosObra.setWidthPercentage(100);
        tablaDatosObra.setWidths(arregloEnteros([12, 24, 10, 12, 24]))
        tablaDatosObra.setSpacingAfter(10f)

        def bgObra = Color.LIGHT_GRAY
        def brObra = Color.BLACK

        addCellTabla(tablaDatosObra, new Paragraph("Obra", fontThHeader), [bg: bgObra, border: bgObra, bct: brObra, bwt: 0.1, bcl: brObra, bwl: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(obra.nombre, fontTdHeader), [bg: bgObra, border: bgObra, bct: brObra, bwt: 0.1, bcr: brObra, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])

        addCellTabla(tablaDatosObra, new Paragraph("Lugar", fontThHeader), [bg: bgObra, border: bgObra, bcl: brObra, bwl: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph((obra.lugar?.descripcion ?: ""), fontTdHeader), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeader), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("Planilla", fontThHeader), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(planilla.numero, fontTdHeader), [bg: bgObra, border: bgObra, bcr: brObra, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Ubicación", fontThHeader), [bg: bgObra, border: bgObra, bcl: brObra, bwl: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(obra.parroquia?.nombre + " - Cantón " + obra.parroquia?.canton?.nombre, fontTdHeader), [bg: bgObra, bcr: brObra, bwr: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])


        addCellTabla(tablaDatosObra, new Paragraph("Contratista", fontThHeader), [bg: bgObra, bcl: brObra, bwl: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("Administración directa", fontTdHeader), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeader), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("Fecha", fontThHeader), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(fechaConFormato(planilla.fechaIngreso, "dd-MM-yyyy"), fontTdHeader), [bg: bgObra, bcr: brObra, bwr: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Plazo", fontThHeader), [pb: 5, bg: bgObra, bcl: brObra, bwl: 0.1, bcb: brObra, bwb: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(numero(obra.plazoEjecucionMeses * 30 + obra.plazoEjecucionDias, 0) + " días", fontTdHeader), [bg: bgObra, bcb: brObra, bwb: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeader), [bg: bgObra, border: bgObra, bcb: brObra, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("Monto obra", fontThHeader), [bg: bgObra, border: bgObra, bcb: brObra, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(numero(obra.valor, 2), fontTdHeader), [bcr: brObra, bwr: 0.1, bcb: brObra, bwb: 0.1, bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaDatosObra);
        /* *********************************** FIN DATOS OBRA **********************************/

        /* ****************************** detalles ********************************************/
        def detalles = DetallePlanillaCostoAdmin.findAllByPlanilla(planilla)

        PdfPTable tablaDetalles = new PdfPTable(6);
        tablaDetalles.setWidthPercentage(100);
        tablaDetalles.setWidths(arregloEnteros([25, 15, 15, 15, 15, 15]))

        addCellTabla(tablaDetalles, new Paragraph("Descripción del rubro", fontTh), [bg: bgObra, border: brObra, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalles, new Paragraph("Unidad", fontTh), [bg: bgObra, border: brObra, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalles, new Paragraph("Cantidad", fontTh), [bg: bgObra, border: brObra, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalles, new Paragraph("Valor sin IVA", fontTh), [bg: bgObra, border: brObra, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalles, new Paragraph("Valor con IVA", fontTh), [bg: bgObra, border: brObra, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalles, new Paragraph("Valor total", fontTh), [bg: bgObra, border: brObra, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        def total = 0

        detalles.each { det ->
            def tot = det.cantidad * det.valorIva
            total += tot
            addCellTabla(tablaDetalles, new Paragraph(det.rubro, fontTd), [border: brObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(det.unidad.codigo, fontTd), [border: brObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(det.cantidad, 2), fontTd), [border: brObra, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(det.valor, 2), fontTd), [border: brObra, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(det.valorIva, 2), fontTd), [border: brObra, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(tot, 2), fontTd), [border: brObra, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tablaDetalles, new Paragraph("TOTAL", fontTh), [bg: bgObra, border: brObra, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
        addCellTabla(tablaDetalles, new Paragraph(numero(total, 2), fontTh), [bg: bgObra, border: brObra, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaDetalles)
        /* ****************************** fin detalles ********************************************/

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }


    private String fechaConFormato(fecha, formato) {
        def meses = ["", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"]
        def mesesLargo = ["", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"]
        def strFecha = ""
//        println ">>" + fecha + "    " + formato
        if (fecha) {
            switch (formato) {
                case "MMM-yy":
                    strFecha = meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yy")
                    break;
                case "dd-MM-yyyy":
                    strFecha = "" + fecha.format("dd-MM-yyyy")
                    break;
                case "dd-MMM-yyyy":
                    strFecha = "" + fecha.format("dd") + "-" + meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yyyy")
                    break;
                case "dd MMMM yyyy":
                    strFecha = "" + fecha.format("dd") + " de " + mesesLargo[fecha.format("MM").toInteger()] + " de " + fecha.format("yyyy")
                    break;
                default:
                    strFecha = "Formato " + formato + " no reconocido"
                    break;
            }
        }
//        println ">>>>>>" + strFecha
        return strFecha
    }

    private String fechaConFormato(fecha) {
        return fechaConFormato(fecha, "MMM-yy")
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

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

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
}
