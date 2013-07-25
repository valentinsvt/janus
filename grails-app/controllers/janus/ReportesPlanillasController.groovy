package janus

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.Image
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import janus.ejecucion.DetallePlanilla
import janus.ejecucion.DetallePlanillaCosto
import janus.ejecucion.FormulaPolinomicaContractual
import janus.ejecucion.PeriodoPlanilla
import janus.ejecucion.PeriodosInec
import janus.ejecucion.Planilla
import janus.ejecucion.TipoPlanilla
import janus.ejecucion.ValorIndice
import janus.ejecucion.ValorReajuste
import janus.pac.PeriodoEjecucion

import java.awt.Color

class ReportesPlanillasController {

    def preciosService
    def dbConnectionService

    def reportePlanilla() {
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato

        def conDetalles = true
        if (params.detalle) {
            conDetalles = Boolean.parseBoolean(params.detalle)
        }
        if (planilla.tipoPlanilla.codigo == "A") {
            conDetalles = false
        }

        def periodoPlanilla
        if (planilla.tipoPlanilla.codigo == "A") {
            periodoPlanilla = 'Anticipo (' + PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(planilla.fechaPresentacion, planilla.fechaPresentacion).descripcion + ")"
        } else {
            if (planilla.tipoPlanilla.codigo == "L") {
                periodoPlanilla = "Liquidación del reajuste (${fechaConFormato(planilla.fechaPresentacion, 'dd-MMM-yyyy')})"
            } else {
                periodoPlanilla = 'del ' + fechaConFormato(planilla.fechaInicio, "dd-MMM-yyyy") + ' al ' + fechaConFormato(planilla.fechaFin, "dd-MMM-yyyy")
            }
        }

        def planillasAnteriores = Planilla.withCriteria {
            eq("contrato", contrato)
            and {
                or {
                    le("fechaInicio", planilla.fechaInicio)
                    isNull("fechaInicio")
                }
                or {
                    eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
                    eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
                }
            }
            order("id", "asc")
        }

        def valorObra = planillasAnteriores.sum { it.valor }

//        def pa = PeriodoPlanilla.findAllByPlanilla(planilla)
        def periodos = PeriodoPlanilla.findAllByPlanillaInList(planillasAnteriores, [sort: "id"])

//        periodos = periodos.sort { it.fechaIncio }

//        println periodos.id
//        println periodos.periodo.descripcion

        def cs = FormulaPolinomicaContractual.findAllByContratoAndNumeroIlike(contrato, "C%", [sort: "numero"])
        def ps = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                and {
                    ne("numero", "P0")
                    ilike("numero", "p%")
                }
                order("numero", "asc")
            }
        }
//        def pcs = FormulaPolinomicaContractual.withCriteria {
//            and {
//                eq("contrato", contrato)
//                or {
//                    ilike("numero", "c%")
//                    and {
//                        ne("numero", "P0")
//                        ne("numero", "p01")
//                        ilike("numero", "p%")
//                    }
//                }
//                order("numero", "asc")
//            }
//        }
//        def periodosEjecucion = PeriodoEjecucion.withCriteria {
//            and {
//                eq("obra", obra)
//                order("fechaInicio", "asc")
//            }
//        }

        def baos = new ByteArrayOutputStream()
        def name = "planilla_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font catFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)
        Font fontTitle = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
        Font fontThTiny = new Font(Font.TIMES_ROMAN, 7, Font.BOLD);
        Font fontTdTiny = new Font(Font.TIMES_ROMAN, 7, Font.NORMAL);
        Font fontThFirmas = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTdFirmas = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        Document document
        document = new Document(PageSize.A4.rotate());
        def pdfw = PdfWriter.getInstance(document, baos);

//        HeaderFooter footer = new HeaderFooter(new Phrase("This is page: "), true);
//        document.setFooter(footer);
        document.resetHeader()
        document.resetFooter()

        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Planillas de la obra " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        def printFirmas = { params ->
            def tablaFirmas = new PdfPTable(3);
            tablaFirmas.setWidthPercentage(100);

            def parametros = Parametros.get(1)

            def contratista = planilla.contrato.oferta.proveedor
            def fiscalizador = planilla.fiscalizador
            def subdirector = parametros.subdirector

            def strContratista = contratista.titulo + " " + contratista.nombreContacto + " " + contratista.apellidoContacto + "\nContratista"
            def strFiscalizador = fiscalizador.titulo + " " + fiscalizador.nombre + " " + fiscalizador.apellido + "\nFiscalizador"
            def strSubdirector = subdirector.titulo + " " + subdirector.nombre + " " + subdirector.apellido + "\nSUBDIRECTOR"
            def strAdmin = "\nADMINISTRADOR"
            def strFechaPresentacion = fechaConFormato(planilla.fechaPresentacion, "dd-MMM-yyyy") + "\nFecha de presentación"
            def strFechaAprobacion = "\nFecha de aprobación"

            if (params.tipo == "detalle") {

                fontThFirmas = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
                fontTdFirmas = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);

                tablaFirmas.setWidths(arregloEnteros([40, 20, 40]))
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaFirmas, new Paragraph(strContratista, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph(strFiscalizador, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaFirmas, new Paragraph(strFechaPresentacion, fontTdFirmas), [height: 58, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_TOP])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 58, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph(strFechaAprobacion, fontTdFirmas), [height: 58, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_TOP])

                addCellTabla(tablaFirmas, new Paragraph(strSubdirector, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph(strAdmin, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            } else if (params.tipo == "otro") {
                if (params.orientacion == "horizontal") {
                    tablaFirmas.setWidths(arregloEnteros([40, 20, 40]))
                } else if (params.orientacion == "vertical") {
                    tablaFirmas.setWidths(arregloEnteros([30, 40, 30]))
                }
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 50, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 50, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 50, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph(strFiscalizador, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            }
            document.add(tablaFirmas)
        }

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        def prmsTdBorder = [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsNmBorder = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        /* ***************************************************** Titulo del reporte *******************************************************/

        /* ***************************************************** Fin Titulo del reporte ***************************************************/

        /* ***************************************************** Header planilla **********************************************************/

        def headerPlanilla = { params ->

            Font fontThUsar = new Font(Font.TIMES_ROMAN, params.size, Font.BOLD);
            Font fontTdUsar = new Font(Font.TIMES_ROMAN, params.size, Font.NORMAL);

            Paragraph preface = new Paragraph();
            addEmptyLine(preface, 1);
            preface.setAlignment(Element.ALIGN_CENTER);
            preface.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", catFont));
            preface.add(new Paragraph("PLANILLA DE ${planilla.tipoPlanilla.nombre.toUpperCase()} DE LA OBRA " + obra.nombre, catFont));
            addEmptyLine(preface, 1);
            Paragraph preface2 = new Paragraph();
            preface2.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
            addEmptyLine(preface2, 1);
            document.add(logo)
            document.add(preface);
            document.add(preface2);

            PdfPTable tablaHeaderPlanilla = new PdfPTable(5);
            tablaHeaderPlanilla.setWidthPercentage(100);
            tablaHeaderPlanilla.setWidths(arregloEnteros([12, 24, 10, 12, 24]))
            tablaHeaderPlanilla.setWidthPercentage(100);

            addCellTabla(tablaHeaderPlanilla, new Paragraph("Obra", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph(obra.nombre, fontTdUsar), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])

            addCellTabla(tablaHeaderPlanilla, new Paragraph("Lugar", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph((obra.lugar?.descripcion ?: ""), fontTdUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph("Planilla", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph(planilla.numero, fontTdUsar), prmsTdNoBorder)

            addCellTabla(tablaHeaderPlanilla, new Paragraph("Ubicación", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph(obra.parroquia?.nombre + " - Cantón " + obra.parroquia?.canton?.nombre, fontTdUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph("Monto contrato", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(planilla.contrato.monto, 2), fontTdUsar), prmsTdNoBorder)

            addCellTabla(tablaHeaderPlanilla, new Paragraph("Contratista", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph(planilla.contrato.oferta.proveedor.nombre, fontTdUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph("Periodo", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph(periodoPlanilla, fontTdUsar), prmsTdNoBorder)

            addCellTabla(tablaHeaderPlanilla, new Paragraph("Plazo", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(planilla.contrato.plazo, 0) + " días", fontTdUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph("Valor obra", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(valorObra, 2), fontTdUsar), prmsTdNoBorder)

            document.add(tablaHeaderPlanilla);
        }
        /* ***************************************************** Fin Header planilla ******************************************************/

        /* ***************************************************** Tabla B0 *****************************************************************/

        headerPlanilla([size: 10])

        Paragraph tituloB0 = new Paragraph();
        addEmptyLine(tituloB0, 1);
        tituloB0.setAlignment(Element.ALIGN_CENTER);
        tituloB0.add(new Paragraph("Cálculo de B0", fontTitle));
        addEmptyLine(tituloB0, 1);
        document.add(tituloB0);

        def tams = [30, 8]
        periodos.size().times {
            tams.add(10)
            tams.add(10)
        }

        PdfPTable tablaB0 = new PdfPTable(2 + (2 * periodos.size()));
        tablaB0.setWidthPercentage(100);
        tablaB0.setWidths(arregloEnteros(tams))
        tablaB0.setWidthPercentage(100);
        tablaB0.setSpacingAfter(5f);

        addCellTabla(tablaB0, new Paragraph("Cuadrilla Tipo", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
        periodos.each { per ->
            addCellTabla(tablaB0, new Paragraph(per.titulo, fontTh), [border: Color.BLACK, bcr: Color.LIGHT_GRAY, bwr: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaB0, new Paragraph(fechaConFormato(per.fechaIncio), fontTh), [border: Color.BLACK, bcl: Color.LIGHT_GRAY, bwl: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        }

        def totalCoef = 0
        cs.each { c ->
            addCellTabla(tablaB0, new Paragraph(c.indice.descripcion + "(" + c.numero + ")", fontTd), prmsTdBorder)
            addCellTabla(tablaB0, new Paragraph(numero(c.valor), fontTd), prmsNmBorder)
            totalCoef += c.valor
            periodos.each { per ->
                def valor = ValorIndice.findByPeriodoAndIndice(per.periodo, c.indice).valor
                if (!valor) {
                    println "wtf no valor " + per.periodo + "  " + c.indice
                    valor = 0
                }
                addCellTabla(tablaB0, new Paragraph(numero(valor, 2), fontTd), [border: Color.BLACK, bcr: Color.WHITE, bwr: 1, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${per.planilla.id} and periodoIndice =${per.periodo.id} and formulaPolinomica=${c.id}")
                if (vlrj.size() > 0) {
                    valor = vlrj.pop().valor
                } else {
                    println "error wtf no hay vlrj => from ValorReajuste where obra=${obra.id} and planilla=${per.planilla.id} and periodoIndice =${per.periodo.id} and formulaPolinomica=${c.id}"
                    valor = -1
                }
                addCellTabla(tablaB0, new Paragraph(numero(valor), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 1, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            }
        }

        addCellTabla(tablaB0, new Paragraph("TOTALES", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaB0, new Paragraph(numero(totalCoef), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        periodos.each { per ->
            addCellTabla(tablaB0, new Paragraph(numero(per.total), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
        }

        document.add(tablaB0);
        printFirmas([tipo: "otro", orientacion: "horizontal"])
        /* ***************************************************** Fin Tabla B0 *************************************************************/

        /* ***************************************************** Tabla P0 *****************************************************************/

        document.newPage()
        headerPlanilla([size: 10])

        Paragraph tituloP0 = new Paragraph();
        addEmptyLine(tituloP0, 1);
        tituloP0.setAlignment(Element.ALIGN_CENTER);
        tituloP0.add(new Paragraph("Cálculo de P0", fontTitle));
        addEmptyLine(tituloP0, 1);
        document.add(tituloP0);

        PdfPTable tablaP0 = new PdfPTable(7);
        tablaP0.setWidths(arregloEnteros([20, 10, 10, 10, 10, 10, 10]))
        tablaP0.setWidthPercentage(100);

        tablaP0.setSpacingAfter(5f);

        addCellTabla(tablaP0, new Paragraph("Mes y año", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
        PdfPTable inner1 = new PdfPTable(2);
        addCellTabla(inner1, new Paragraph("Cronograma", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(inner1, new Paragraph("Parcial", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner1, new Paragraph("Acumulado", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaP0, inner1, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
        PdfPTable inner2 = new PdfPTable(2);
        addCellTabla(inner2, new Paragraph("Planillado", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(inner2, new Paragraph("Parcial", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner2, new Paragraph("Acumulado", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaP0, inner2, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(tablaP0, new Paragraph("Valor P0", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, rowspan: 2])


        PdfPTable tablaMl = new PdfPTable(5);
        tablaMl.setWidthPercentage(50);
        tablaMl.setHorizontalAlignment(Element.ALIGN_LEFT)

        tablaMl.setSpacingAfter(5f);

        addCellTabla(tablaMl, new Paragraph("Mes y año", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaMl, new Paragraph("Cronograma", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaMl, new Paragraph("Planillado", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaMl, new Paragraph("Retraso", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaMl, new Paragraph("Multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        def act = 0
        def act2 = 0
        def diasTot = 0, totCrono = 0, totPlan = 0, totalMultaRetraso = 0
        periodos.each { per ->
            if (per.titulo != "OFERTA") {
                if (per.titulo == "ANTICIPO") {
                    addCellTabla(tablaP0, new Paragraph(per.titulo, fontTh), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaP0, new Paragraph(fechaConFormato(per.fechaIncio), fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaP0, new Paragraph(numero(per.p0, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                } else {
                    def dias = per.fechaFin - per.fechaIncio + 1
                    diasTot += dias
                    addCellTabla(tablaP0, new Paragraph(fechaConFormato(per.fechaIncio), fontTh), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaP0, new Paragraph("(" + dias + ")", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaP0, new Paragraph(numero(per.parcialCronograma, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    act += per.parcialCronograma
                    totCrono += per.parcialCronograma
                    addCellTabla(tablaP0, new Paragraph(numero(act, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaP0, new Paragraph(numero(per.parcialPlanilla, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    act2 += per.parcialPlanilla
                    totPlan += per.parcialPlanilla
                    addCellTabla(tablaP0, new Paragraph(numero(act2, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaP0, new Paragraph(numero(per.p0, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                    if (per.planilla == planilla) {
                        def retraso = 0, multa = 0
                        if (per.parcialCronograma > per.parcialPlanilla) {
                            def totalContrato = contrato.monto
                            def prmlMulta = contrato.multaPlanilla
                            def valorDia = per.parcialCronograma / per.dias
                            retraso = ((per.parcialCronograma - per.parcialPlanilla) / valorDia).round(2)
                            multa = ((totalContrato) * (prmlMulta / 1000) * retraso).round(2)
                        }
                        totalMultaRetraso += multa

                        addCellTabla(tablaMl, new Paragraph(fechaConFormato(per.fechaIncio), fontTd), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaMl, new Paragraph(numero(per.parcialCronograma, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaMl, new Paragraph(numero(per.parcialPlanilla, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaMl, new Paragraph(numero(retraso, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaMl, new Paragraph(numero(multa, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    }
                }
            }
        }
        if (periodos.size() > 2) {
            addCellTabla(tablaP0, new Paragraph("TOTAL", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaP0, new Paragraph("(" + diasTot + ")", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaP0, new Paragraph(numero(totCrono, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
            addCellTabla(tablaP0, new Paragraph(numero(totPlan, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
            addCellTabla(tablaP0, new Paragraph("", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }
        document.add(tablaP0);

        printFirmas([tipo: "otro", orientacion: "horizontal"])

        /* ***************************************************** Fin Tabla P0 *************************************************************/

        /* ***************************************************** Tabla Fr *****************************************************************/

        document.setPageSize(PageSize.A4);
        document.newPage();
//        document.add(new Paragraph("AQUI", fontTitle))
//        document.newPage();

//        def tablaHeaderFr = new PdfPTable(2)
//        tablaHeaderFr.setWidths(arregloEnteros([20, 80]))
//        tablaHeaderFr.setWidthPercentage(100);
//
//        addCellTabla(tablaHeaderFr, logo, [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaHeaderFr, tablaHeaderPlanilla, [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//
//        document.add(preface);
//        document.add(preface2);
//        document.add(tablaHeaderFr);

//        document.add(tablaHeaderPlanilla);

        headerPlanilla([size: 8])

        Paragraph tituloFr = new Paragraph();
        addEmptyLine(tituloFr, 1);
        tituloFr.setAlignment(Element.ALIGN_CENTER);
        tituloFr.add(new Paragraph("Cálculo de Fr y Pr", fontTitle));
        addEmptyLine(tituloFr, 1);
        document.add(tituloFr);

        PdfPTable tablaFr = new PdfPTable(1 + periodos.size());
        tams = [20]
        periodos.size().times {
            tams.add(10)
        }
        tablaFr.setWidths(arregloEnteros(tams))
        tablaFr.setWidthPercentage(100);
        tablaFr.setSpacingAfter(5f);

        PdfPTable inner3 = new PdfPTable(periodos.size() - 1)
        addCellTabla(inner3, new Paragraph("Periodo de variación y aplicación de fórmula polinómica", fontTh), [border: Color.BLACK, bwb: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])

        PdfPTable inner4 = new PdfPTable(1);
        inner4.setExtendLastRow(true);
        def prg = new Paragraph("Componentes", fontTh)
        def str = " "
        if (periodos.size() == 2) {
            str += "\n "
        }
        addCellTabla(inner4, new Paragraph(str, fontTh), [border: Color.BLACK, bcb: Color.LIGHT_GRAY, bwb: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner4, prg, [border: Color.BLACK, bg: Color.LIGHT_GRAY, bct: Color.LIGHT_GRAY, bcb: Color.LIGHT_GRAY, bwt: 1, bwb: 1, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner4, new Paragraph(" ", fontTh), [border: Color.BLACK, bct: Color.LIGHT_GRAY, bwt: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner4, new Paragraph("Anticipo", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFr, inner4, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        periodos.eachWithIndex { per, i ->
            if (i == 0) { //oferta
                PdfPTable inner5 = new PdfPTable(1);
                def prg2 = new Paragraph(per.titulo, fontTh)
                addCellTabla(inner5, new Paragraph(str, fontTh), [border: Color.BLACK, bcb: Color.LIGHT_GRAY, bwb: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, prg2, [border: Color.BLACK, bg: Color.LIGHT_GRAY, bct: Color.LIGHT_GRAY, bwt: 1, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, new Paragraph(fechaConFormato(per.fechaIncio), fontTh), [border: Color.BLACK, bwb: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, new Paragraph(numero(per.planilla.contrato.porcentajeAnticipo, 0) + "%", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFr, inner5, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//                addCellTabla(tablaFr, new Paragraph("asdf", fontTd), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            } else if (i == 1) { //anticipo
                PdfPTable inner5 = new PdfPTable(1);
                addCellTabla(inner5, new Paragraph(per.titulo, fontTh), [border: Color.BLACK, bwb: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, new Paragraph(fechaConFormato(per.fechaIncio), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, new Paragraph(per.titulo, fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner3, inner5, [border: Color.BLACK, bwb: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//                addCellTabla(inner3, new Paragraph("123", fontTd), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            } else { //otros
                addCellTabla(inner3, new Paragraph(fechaConFormato(per.fechaIncio), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            }
        }
        addCellTabla(tablaFr, inner3, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])

        def totalFr = 0

        ps.eachWithIndex { p, i ->
            addCellTabla(tablaFr, new Paragraph(p.indice.descripcion + " (" + p.numero + ")", fontTh), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            def vlinOferta
            periodos.eachWithIndex { per, j ->
                if (j == 0) { //oferta
                    if (i == 0) { //mano de obra
                        vlinOferta = per.total
                        addCellTabla(tablaFr, new Paragraph(numero(p.valor) + "\n" + numero(per.total), fontTh), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
//                        println "\t1 " + p.valor
                        totalFr += p.valor
                    } else {
                        vlinOferta = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
                        addCellTabla(tablaFr, new Paragraph(numero(p.valor) + "\n" + numero(vlinOferta, 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
//                        println "\t2 " + p.valor
                        totalFr += p.valor
                    }
                } else {
                    def vlin
                    def dec
                    if (i == 0) {
                        vlin = per.total
                        dec = 3
                    } else {
                        vlin = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
                        dec = 2
                    }
//                    println "error "+p.indice+" "+p.indice.id+"  "+per.periodo.id+"   "+vlin+"  "+vlinOferta+"  "+p.valor+"  "+"  "+per.periodo.id
                    def valor = (vlin / vlinOferta * p.valor).round(3)
                    addCellTabla(tablaFr, new Paragraph(numero(vlin, dec) + "\n" + numero(valor), fontTh), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
//                    println "\t3 " + vlin
//                    totalFr += vlin
                }
            }
        }
        addCellTabla(tablaFr, new Paragraph("Sumatoria", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFr, new Paragraph(numero(totalFr), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFr, new Paragraph("", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])

        PdfPTable inner5 = new PdfPTable(periodos.size());

        def cells = [
                0: [new Paragraph("Fr", fontTh)],
                1: [new Paragraph("Fr-1", fontTh)],
                2: [new Paragraph("P0", fontTh)],
                3: [new Paragraph("Pr-P", fontTh)]
        ]

        def reajusteTotal = 0
        periodos.eachWithIndex { per, i ->
            if (i > 0) {
                def fr1 = (per.fr - 1).round(3)

                cells[0][i] = new Paragraph(numero(per.fr), fontTd)
                cells[1][i] = new Paragraph(numero(fr1), fontTd)
                cells[2][i] = new Paragraph(numero(per.p0, 2), fontTd)
                def t = (per.p0 * fr1).round(2)
                cells[3][i] = new Paragraph(numero(t, 2), fontTd)
                reajusteTotal += t
            }
        }
        reajusteTotal = reajusteTotal.toDouble().round(2)

        cells.each { k, v ->
            v.eachWithIndex { vv, i ->
                addCellTabla(inner5, vv, [border: Color.BLACK, align: i == 0 ? Element.ALIGN_CENTER : Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            }
        }

        PdfPTable tablaDatos = new PdfPTable(2);
        tablaDatos.setWidths(arregloEnteros([25, 50]))
        tablaDatos.setWidthPercentage(100);

        addCellTabla(tablaDatos, new Paragraph("Fecha", fontTh), [border: Color.LIGHT_GRAY, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatos, new Paragraph(fechaConFormato(planilla.fechaPresentacion, "dd-MMM-yyyy"), fontTd), [border: Color.LIGHT_GRAY, bwr: 0.1, bcr: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos, new Paragraph(fechaConFormato(new Date(), "dd-MMM-yyyy"), fontTd), [border: Color.LIGHT_GRAY, bwr: 0.1, bcr: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatos, new Paragraph("Fuente", fontTh), [border: Color.LIGHT_GRAY, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos, new Paragraph("INEC " + PeriodoPlanilla.findAllByPlanilla(planilla, [sort: "id", order: "desc"])[0].periodo.descripcion, fontTd), [border: Color.LIGHT_GRAY, bwr: 0.1, bcr: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatos, new Paragraph("Salarios", fontTh), [border: Color.LIGHT_GRAY, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos, new Paragraph("C.G.E.", fontTd), [border: Color.LIGHT_GRAY, bwr: 0.1, bcr: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])


        addCellTabla(tablaFr, tablaDatos, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFr, inner5, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: periodos.size()])

        addCellTabla(tablaFr, new Paragraph("REAJUSTE TOTAL", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(tablaFr, new Paragraph(numero(reajusteTotal, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])

        def planillaAnterior, reajusteAnterior, reajustePlanillar

        if (periodos.size() > 2) {
            planillaAnterior = planillasAnteriores.get(planillasAnteriores.size() - 2)
            reajusteAnterior = (planillaAnterior.reajuste).toDouble().round(2)
            reajustePlanillar = reajusteTotal - reajusteAnterior

            addCellTabla(tablaFr, new Paragraph("REAJUSTE ANTERIOR", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
            addCellTabla(tablaFr, new Paragraph(numero(reajusteAnterior, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])
            addCellTabla(tablaFr, new Paragraph("REAJUSTE A PLANILLAR", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
            addCellTabla(tablaFr, new Paragraph(/*numero(reajustePlanillar, 2) + " ** " +*/ numero(planilla.reajuste, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])
        }

        document.add(tablaFr);
        printFirmas([tipo: "otro", orientacion: "vertical"])
        /* ***************************************************** Fin Tabla Fr *************************************************************/

        /* ***************************************************** Multa no presentacion ****************************************************/
        def totalMulta = 0
        if (planilla.tipoPlanilla.codigo != "A") {
            document.newPage();
            headerPlanilla([size: 8])
            Paragraph tituloMultas = new Paragraph();
            addEmptyLine(tituloMultas, 1);
            tituloMultas.setAlignment(Element.ALIGN_CENTER);
            tituloMultas.add(new Paragraph("Multas", fontTitle));
            document.add(tituloMultas);

            Paragraph tituloMultaNoPres = new Paragraph();
            tituloMultaNoPres.setAlignment(Element.ALIGN_CENTER);
            tituloMultaNoPres.add(new Paragraph("Multa por no presentación de planilla", fontTitle));
            addEmptyLine(tituloMultaNoPres, 1);
            document.add(tituloMultaNoPres);

            def diasMax = 5
            def fechaFinPer = planilla.fechaFin
            def fechaMax = fechaFinPer

            def noLaborables = ["Sat", "Sun"]

//            println fechaMax
            diasMax.times {
                fechaMax++
//                println fechaMax
                def fmt = new java.text.SimpleDateFormat("EEE", new Locale("en"))
                while (noLaborables.contains(fmt.format(fechaMax))) {
//                    println fmt.format(fechaMax)
                    fechaMax++
//                    println fechaMax
                }
            }
//            println "***** "+fechaMax

            def fechaPresentacion = planilla.fechaPresentacion
            def retraso = fechaPresentacion - fechaMax + 1


            def totalContrato = contrato.monto
            def prmlMulta = contrato.multaPlanilla
            if (retraso > 0) {
//            totalMulta = (totalContrato) * (prmlMulta / 1000) * retraso
                totalMulta = (PeriodoPlanilla.findAllByPlanilla(planilla).sum { it.parcialCronograma }) * (prmlMulta / 1000) * retraso
            } else {
                retraso = 0
            }

            PdfPTable tablaPml = new PdfPTable(2);
            tablaPml.setWidthPercentage(50);

            tablaPml.setHorizontalAlignment(Element.ALIGN_LEFT)

            addCellTabla(tablaPml, new Paragraph("Fecha de presentación de planilla", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaPml, new Paragraph(fechaPresentacion.format("dd-MM-yyyy"), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaPml, new Paragraph("Periodo de la planilla", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaPml, new Paragraph(planilla.fechaInicio.format("dd-MM-yyyy") + " a " + planilla.fechaFin.format("dd-MM-yyyy"), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaPml, new Paragraph("Fecha máxima de presentación", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaPml, new Paragraph(fechaMax.format("dd-MM-yyyy"), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaPml, new Paragraph("Días de retraso", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaPml, new Paragraph("" + retraso, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaPml, new Paragraph("Multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaPml, new Paragraph(numero(prmlMulta, 2) + "‰ de \$" + numero(totalContrato, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaPml, new Paragraph("Valor de la multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaPml, new Paragraph('$' + numero(totalMulta, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            document.add(tablaPml);
            /* ***************************************************** Fin Multa no presentacion ************************************************/

            /* ***************************************************** Multa retraso ************************************************************/
            Paragraph tituloMultaRetraso = new Paragraph();
            addEmptyLine(tituloMultaRetraso, 1);
            tituloMultaRetraso.setAlignment(Element.ALIGN_CENTER);
            tituloMultaRetraso.add(new Paragraph("Multa por retraso de obra", fontTitle));
            addEmptyLine(tituloMultaRetraso, 1);
            document.add(tituloMultaRetraso);

            addCellTabla(tablaMl, new Paragraph("TOTAL", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaMl, new Paragraph(numero(totalMultaRetraso, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 4])

            document.add(tablaMl);


            printFirmas([tipo: "otro", orientacion: "vertical"])
        }
        /* ***************************************************** Fin Multa retraso ********************************************************/

        /* ***************************************************** Detalles *****************************************************************/
        if (conDetalles) {
//            document.newPage()
//
//            PdfPTable tablaDetalles = new PdfPTable(11);
//            tablaDetalles.setWidthPercentage(100);
//            tablaDetalles.setWidths(arregloEnteros([12, 35, 5, 11, 11, 11, 11, 11, 11, 11, 11]))
            PdfPTable tablaDetalles
            def borderWidth = 1

            def printHeaderDetalle = { params ->
                def tablaHeaderDetalles = new PdfPTable(11);
                tablaHeaderDetalles.setWidthPercentage(100);
                tablaHeaderDetalles.setWidths(arregloEnteros([13, 35, 5, 11, 11, 11, 11, 11, 11, 11, 13]))

                addCellTabla(tablaHeaderDetalles, new Paragraph("Obra", fontThTiny), prmsTdNoBorder)
                addCellTabla(tablaHeaderDetalles, new Paragraph(obra.nombre, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 10])

                addCellTabla(tablaHeaderDetalles, new Paragraph("Lugar", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaHeaderDetalles, new Paragraph((obra.lugar?.descripcion ?: ""), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
                addCellTabla(tablaHeaderDetalles, new Paragraph("Planilla", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaHeaderDetalles, new Paragraph(planilla.numero, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
                addCellTabla(tablaHeaderDetalles, new Paragraph(" ", fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaHeaderDetalles, new Paragraph("Ubicación", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaHeaderDetalles, new Paragraph("Parroquia " + obra.parroquia?.nombre + " Cantón " + obra.parroquia?.canton?.nombre, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
                addCellTabla(tablaHeaderDetalles, new Paragraph("Monto", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaHeaderDetalles, new Paragraph(numero(planilla.contrato.monto, 2), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
                addCellTabla(tablaHeaderDetalles, new Paragraph("Pág. " + params.pag + " de " + params.total, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaHeaderDetalles, new Paragraph("Contratista", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaHeaderDetalles, new Paragraph(planilla.contrato.oferta.proveedor.nombre, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
                addCellTabla(tablaHeaderDetalles, new Paragraph("Periodo", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaHeaderDetalles, new Paragraph(periodoPlanilla, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
                addCellTabla(tablaHeaderDetalles, new Paragraph(" ", fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])


                addCellTabla(tablaDetalles, logo, [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, tablaHeaderDetalles, [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 10, pl: 40])

                addCellTabla(tablaDetalles, new Paragraph(" ", fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 11])


                addCellTabla(tablaDetalles, new Paragraph("N.", fontThTiny), [border: Color.BLACK, bwb: 1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph("Descripción del rubro", fontThTiny), [bwb: 1, bcb: Color.BLACK, bwr: borderWidth, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph("U.", fontThTiny), [bwb: 1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph("Precio unitario", fontThTiny), [bwb: 1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph("Volumen contratado", fontThTiny), [bwb: 1, bcb: Color.BLACK, border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

                PdfPTable inner6 = new PdfPTable(3);
                addCellTabla(inner6, new Paragraph("Cantidades", fontThTiny), [border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                addCellTabla(inner6, new Paragraph("Anterior", fontThTiny), [bwb: 1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner6, new Paragraph("Actual", fontThTiny), [bwb: 1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner6, new Paragraph("Acumulado", fontThTiny), [bwb: 1, bcb: Color.BLACK, border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, inner6, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])

                PdfPTable inner7 = new PdfPTable(3);
                addCellTabla(inner7, new Paragraph("Valores", fontThTiny), [border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                addCellTabla(inner7, new Paragraph("Anterior", fontThTiny), [bwb: 1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner7, new Paragraph("Actual", fontThTiny), [bwb: 1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner7, new Paragraph("Acumulado", fontThTiny), [bwb: 1, bcb: Color.BLACK, border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, inner7, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
            }

            def printFooterDetalle = { params ->

                Font fontThFooter = new Font(Font.TIMES_ROMAN, 7, Font.BOLD);

                addCellTabla(tablaDetalles, new Paragraph("", fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                addCellTabla(tablaDetalles, new Paragraph("TOTAL AVANCE DE OBRA", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])
                addCellTabla(tablaDetalles, new Paragraph(numero(params.ant, 2), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(params.act, 2), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(params.acu, 2), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                if (params.completo) {

                    def bAnt = planillaAnterior.reajuste
                    def bAct = planilla.reajuste
                    def bAcu = bAct - bAnt

                    def cAnt = params.ant + bAnt
                    def cAct = params.act + bAct
                    def cAcu = params.acu + bAcu

                    def dAnt = planillasAnteriores[0..planillasAnteriores.size() - 2].sum { it.descuentos }
//                    def d = (valorObra / planilla.contrato.monto) * planilla.contrato.anticipo - dAnt
                    def d = planilla.descuentos

                    def antAnt = dAnt
                    def antAct = d
                    def antAcu = d + dAnt

                    def multasAnt = 0
                    def multasAct = totalMultaRetraso + totalMulta
                    def multasAcu = totalMultaRetraso + totalMulta

                    def totalAcu = cAcu - antAcu - multasAcu

                    addCellTabla(tablaDetalles, new Paragraph("", fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                    addCellTabla(tablaDetalles, new Paragraph("TOTAL REAJUSTE DE PRECIOS", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph(numero(bAnt, 2), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(bAct, 2), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(bAcu, 2), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                    addCellTabla(tablaDetalles, new Paragraph("", fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                    addCellTabla(tablaDetalles, new Paragraph("TOTAL PLANILLA REAJUSTADA", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph(numero(cAnt, 2), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(cAct, 2), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(cAcu, 2), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                    addCellTabla(tablaDetalles, new Paragraph("", fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                    addCellTabla(tablaDetalles, new Paragraph("DESCUENTOS CONTRACTUALES", fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 6])

                    addCellTabla(tablaDetalles, new Paragraph("", fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                    addCellTabla(tablaDetalles, new Paragraph("ANTICIPO", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph(numero(antAnt, 2), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(antAct, 2), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(antAcu, 2), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                    addCellTabla(tablaDetalles, new Paragraph("", fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                    addCellTabla(tablaDetalles, new Paragraph("MULTAS", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph(numero(multasAnt, 2, "hide"), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(multasAct, 2), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(multasAcu, 2), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                    addCellTabla(tablaDetalles, new Paragraph("", fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                    addCellTabla(tablaDetalles, new Paragraph("VALOR LIQUIDO A PAGAR", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph("", fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph("", fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(totalAcu, 2), fontThTiny), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                }
            }

            /* ************* fin header *****************/

//            addCellTabla(tablaDetalles, new Paragraph(" ", fontTh), [border: Color.BLACK, height: 72, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 11])

//            tablaDetalles.setHeaderRows(7);
//            tablaDetalles.setFooterRows(1);
//            tablaDetalles.setHeaderRows(5);

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
            def currentRows = 0

            def sps = detalle.subPresupuesto.unique()
            def totalRows = detalle.size() + sps.size()

            def totalPags = Math.ceil(totalRows / maxRows).toInteger()
            def currentPag = 1

            def sumaParcialAnterior = 0, sumaTotalAnterior = 0
            def sumaParcialActual = 0, sumaTotalActual = 0
            def sumaParcialAcumulado = 0, sumaTotalAcumulado = 0

            detalle.eachWithIndex { vol, i ->
                def det = DetallePlanilla.findByPlanillaAndVolumenObra(planilla, vol)
                def anteriores = DetallePlanilla.findAllByPlanillaInListAndVolumenObra(planillasAnteriores[0..planillasAnteriores.size() - 2], vol)
                def cantAnt = anteriores.sum { it.cantidad } ?: 0
                def valAnt = anteriores.sum { it.monto } ?: 0
                def cant = det?.cantidad ?: 0
                def val = det?.monto ?: 0
                totalAnterior += valAnt
                totalActual += (val.toDouble().round(2))
                totalAcumulado += (val.toDouble().round(2) + valAnt)
                if (currentRows % maxRows == 0) {
                    document.newPage()
//
                    tablaDetalles = new PdfPTable(11);
                    tablaDetalles.setWidthPercentage(100);
                    tablaDetalles.setWidths(arregloEnteros([12, 35, 5, 11, 11, 11, 11, 11, 11, 11, 11]))

                    tablaDetalles.setSpacingAfter(1f);

//                    document.add(logo);
                    printHeaderDetalle([pag: currentPag, total: totalPags])
                }
                if (sp != vol.subPresupuestoId) {
//                    println vol.subPresupuesto.descripcion
                    addCellTabla(tablaDetalles, new Paragraph(vol.subPresupuesto.descripcion, fontThTiny), [height: height, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
                    addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    sp = vol.subPresupuestoId
                    currentRows++
                }
//                if ((i + 1) % 40 == 0) {
//                    document.add(tablaDetalles);
//                    tablaDetalles.deleteBodyRows();
//                    tablaDetalles.setSkipFirstHeader(true);
//                }

                addCellTabla(tablaDetalles, new Paragraph(vol.item.codigo, fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
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
                if (currentRows % maxRows == maxRows - 1) {
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

                    printFirmas([tipo: "detalle", orientacion: "vertical"])
                    currentPag++
                }
            }
            if (currentRows % maxRows < maxRows - 1) {
                printFooterDetalle(ant: sumaTotalAnterior, act: sumaTotalActual, acu: sumaTotalAcumulado, completo: true)

                sumaParcialAnterior = 0
                sumaParcialActual = 0
                sumaParcialAcumulado = 0

                document.add(tablaDetalles)

                printFirmas([tipo: "detalle", orientacion: "vertical"])
            }
        }
        /* ***************************************************** Fin Detalles *************************************************************/

        document.close();

//        // Create a reader
//        PdfReader reader = new PdfReader(baos.toByteArray());
//        // Create a stamper
////        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(RESULT));
//        PdfStamper stamper = new PdfStamper(reader, baos);
//        // Loop over the pages and add a header to each page
//        int n = reader.getNumberOfPages();
//        for (int i = 1; i <= n; i++) {
//            PdfPTable table = new PdfPTable(2);
//            table.setTotalWidth(527);
//            table.setLockedWidth(true);
//            table.getDefaultCell().setFixedHeight(20);
//            table.getDefaultCell().setBorder(Rectangle.BOTTOM);
//            table.addCell("Planilla");
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(String.format("Pág. %d of %d", i, n));
//            return table;
//        }
//        // Close the stamper
//        stamper.close();

        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def reportePlanillaLiquidacion() {
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato

        def conDetalles = true
        if (params.detalle) {
            conDetalles = Boolean.parseBoolean(params.detalle)
        }
        if (planilla.tipoPlanilla.codigo == "A") {
            conDetalles = false
        }

        def periodoPlanilla
        if (planilla.tipoPlanilla.codigo == "A") {
            periodoPlanilla = 'Anticipo (' + PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(planilla.fechaPresentacion, planilla.fechaPresentacion).descripcion + ")"
        } else {
            if (planilla.tipoPlanilla.codigo == "L") {
                periodoPlanilla = "Liquidación del reajuste (${planilla.fechaPresentacion.format('dd-MM-yyyy')})"
            } else {
                periodoPlanilla = 'del ' + planilla.fechaInicio.format('dd-MM-yyyy') + ' al ' + planilla.fechaFin.format('dd-MM-yyyy')
            }
        }

        def planillasAnteriores = Planilla.withCriteria {
            eq("contrato", contrato)
            and {
                or {
                    eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
                    eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
                }
            }
            order("id", "asc")
        }
//        println planillasAnteriores.id
        def pa = PeriodoPlanilla.findAllByPlanilla(planilla)
        def periodos = PeriodoPlanilla.findAllByPlanillaInList(planillasAnteriores, [sort: "id"])
//        def planillaAnterior = planillasAnteriores[planillasAnteriores.size() - 1]
        def periodosEjec = PeriodoEjecucion.findAllByObra(contrato.oferta.concurso.obra, [sort: "fechaFin"])
        def finalObraCrono = periodosEjec.pop().fechaFin
        def tarde = false
        def fechaFinObra = obra.fechaFin
        def planillaAnticipo = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
        }
        planillaAnticipo = planillaAnticipo.pop()
//        println periodos.id
//        println periodos.periodo.descripcion

        if (fechaFinObra > finalObraCrono) {
            tarde = true
        }

        def cs = FormulaPolinomicaContractual.findAllByContratoAndNumeroIlike(contrato, "C%", [sort: "numero"])
        def ps = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                and {
                    ne("numero", "P0")
                    ilike("numero", "p%")
                }
                order("numero", "asc")
            }
        }
        def pcs = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                or {
                    ilike("numero", "c%")
                    and {
                        ne("numero", "P0")
                        ne("numero", "p01")
                        ilike("numero", "p%")
                    }
                }
                order("numero", "asc")
            }
        }
        def periodosEjecucion = PeriodoEjecucion.withCriteria {
            and {
                eq("obra", obra)
                order("fechaInicio", "asc")
            }
        }

        def baos = new ByteArrayOutputStream()
        def name = "planilla_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font catFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font fontTitle = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Document document
        document = new Document(PageSize.A4.rotate());
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Planillas de la obra " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        /* ***************************************************** Titulo del reporte *******************************************************/
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", catFont));
        preface.add(new Paragraph("PLANILLA DE ${planilla.tipoPlanilla.nombre.toUpperCase()} DE LA OBRA " + obra.nombre, catFont));
        addEmptyLine(preface, 1);
        Paragraph preface2 = new Paragraph();
        preface2.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
        addEmptyLine(preface2, 1);
        document.add(preface);
        document.add(preface2);
        /* ***************************************************** Fin Titulo del reporte ***************************************************/

        /* ***************************************************** Header planilla **********************************************************/

        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        def prmsTdBorder = [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsNmBorder = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        PdfPTable tablaHeaderPlanilla = new PdfPTable(5);
        tablaHeaderPlanilla.setWidthPercentage(100);
        tablaHeaderPlanilla.setWidths(arregloEnteros([12, 24, 10, 12, 24]))
        tablaHeaderPlanilla.setWidthPercentage(100);

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Obra", fontTh), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(obra.nombre, fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Lugar", fontTh), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph((obra.lugar?.descripcion ?: ""), fontTd), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTh), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("Planilla", fontTh), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(planilla.numero, fontTd), prmsTdNoBorder)

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Ubicación", fontTh), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(obra.parroquia?.nombre + " - Cantón " + obra.parroquia?.canton?.nombre, fontTd), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTh), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("Monto contrato", fontTh), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(planilla.contrato.monto, 2), fontTd), prmsTdNoBorder)

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Contratista", fontTh), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(planilla.contrato.oferta.proveedor.nombre, fontTd), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTh), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("Periodo", fontTh), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(periodoPlanilla, fontTd), prmsTdNoBorder)

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Plazo", fontTh), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(planilla.contrato.plazo, 0) + " días", fontTd), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTh), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTh), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTd), prmsTdNoBorder)

        document.add(tablaHeaderPlanilla);
        /* ***************************************************** Fin Header planilla ******************************************************/

        /* ***************************************************** Tabla B0 *****************************************************************/
        Paragraph tituloB0 = new Paragraph();
        addEmptyLine(tituloB0, 1);
        tituloB0.setAlignment(Element.ALIGN_CENTER);
        tituloB0.add(new Paragraph("Cálculo de B0", fontTitle));
        addEmptyLine(tituloB0, 1);
        document.add(tituloB0);

        def tams = [30, 8]
        periodos.size().times {
            tams.add(10)
            tams.add(10)
        }

        PdfPTable tablaB0 = new PdfPTable(2 + (2 * periodos.size()));
        tablaB0.setWidthPercentage(100);
        tablaB0.setWidths(arregloEnteros(tams))
        tablaB0.setWidthPercentage(100);

        addCellTabla(tablaB0, new Paragraph("Cuadrilla Tipo", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
        periodos.each { per ->
            addCellTabla(tablaB0, new Paragraph(per.titulo, fontTh), [border: Color.BLACK, bcr: Color.LIGHT_GRAY, bwr: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaB0, new Paragraph(fechaConFormato(per.fechaIncio), fontTh), [border: Color.BLACK, bcl: Color.LIGHT_GRAY, bwl: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        }

        def totalCoef = 0
        cs.each { c ->
            addCellTabla(tablaB0, new Paragraph(c.indice.descripcion + "(" + c.numero + ")", fontTd), prmsTdBorder)
            addCellTabla(tablaB0, new Paragraph(numero(c.valor), fontTd), prmsNmBorder)
            totalCoef += c.valor
            periodos.each { per ->
                def valor = ValorIndice.findByPeriodoAndIndice(per.periodoLiquidacion, c.indice).valor
                if (!valor) {
                    println "wtf no valor " + per.periodoLiquidacion + "  " + c.indice
                    valor = 0
                }
                addCellTabla(tablaB0, new Paragraph(numero(valor, 2), fontTd), [border: Color.BLACK, bcr: Color.WHITE, bwr: 1, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${per.periodoLiquidacion.id} and formulaPolinomica=${c.id} and planillaLiq = ${per.planilla.id}")
                if (vlrj.size() > 0) {
                    valor = vlrj.pop().valor
                } else {
                    println "error wtf no hay vlrj => from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${per.periodoLiquidacion.id} and formulaPolinomica=${c.id} and planillaLiq = ${per.planilla.id}"
                    valor = -1
                }
                addCellTabla(tablaB0, new Paragraph(numero(valor), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 1, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            }
        }

        addCellTabla(tablaB0, new Paragraph("TOTALES", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaB0, new Paragraph(numero(totalCoef), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        periodos.each { per ->
            addCellTabla(tablaB0, new Paragraph(numero(per.totalLiq), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
        }

        document.add(tablaB0);
        /* ***************************************************** Fin Tabla B0 *************************************************************/

        /* ***************************************************** Tabla P0 *****************************************************************/
        Paragraph tituloP0 = new Paragraph();
        addEmptyLine(tituloP0, 1);
        tituloP0.setAlignment(Element.ALIGN_CENTER);
        tituloP0.add(new Paragraph("Cálculo de P0", fontTitle));
        addEmptyLine(tituloP0, 1);
        document.add(tituloP0);

        PdfPTable tablaP0 = new PdfPTable(7);
        tablaP0.setWidths(arregloEnteros([20, 10, 10, 10, 10, 10, 10]))
        tablaP0.setWidthPercentage(100);

        addCellTabla(tablaP0, new Paragraph("Mes y año", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
        PdfPTable inner1 = new PdfPTable(2);
        addCellTabla(inner1, new Paragraph("Cronograma", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(inner1, new Paragraph("Parcial", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner1, new Paragraph("Acumulado", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaP0, inner1, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
        PdfPTable inner2 = new PdfPTable(2);
        addCellTabla(inner2, new Paragraph("Planillado", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(inner2, new Paragraph("Parcial", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner2, new Paragraph("Acumulado", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaP0, inner2, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(tablaP0, new Paragraph("Valor P0", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, rowspan: 2])


        PdfPTable tablaMl = new PdfPTable(5);
        tablaMl.setWidthPercentage(50);
        addCellTabla(tablaMl, new Paragraph("Mes y año", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaMl, new Paragraph("Cronograma", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaMl, new Paragraph("Planillado", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaMl, new Paragraph("Retraso", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaMl, new Paragraph("Multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        def act = 0
        def act2 = 0
        def diasTot = 0, totCrono = 0, totPlan = 0, totalMultaRetraso = 0
        periodos.each { per ->
            if (per.titulo != "OFERTA") {
                if (per.titulo == "ANTICIPO") {
                    addCellTabla(tablaP0, new Paragraph(per.titulo, fontTh), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaP0, new Paragraph(fechaConFormato(per.fechaIncio), fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaP0, new Paragraph(numero(per.p0, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                } else {
                    def dias = per.fechaFin - per.fechaIncio + 1
                    diasTot += dias
                    addCellTabla(tablaP0, new Paragraph(fechaConFormato(per.fechaIncio), fontTh), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaP0, new Paragraph("(" + dias + ")", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaP0, new Paragraph(numero(per.parcialCronograma, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    act += per.parcialCronograma
                    totCrono += per.parcialCronograma
                    addCellTabla(tablaP0, new Paragraph(numero(act, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaP0, new Paragraph(numero(per.parcialPlanilla, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    act2 += per.parcialPlanilla
                    totPlan += per.parcialPlanilla
                    addCellTabla(tablaP0, new Paragraph(numero(act2, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaP0, new Paragraph(numero(per.p0, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                    if (per.planilla == planilla) {
                        def retraso = 0, multa = 0
                        if (per.parcialCronograma > per.parcialPlanilla) {
                            def totalContrato = contrato.monto
                            def prmlMulta = contrato.multaPlanilla
                            def valorDia = per.parcialCronograma / per.dias
                            retraso = ((per.parcialCronograma - per.parcialPlanilla) / valorDia).round(2)
                            multa = ((totalContrato) * (prmlMulta / 1000) * retraso).round(2)
                        }
                        totalMultaRetraso += multa

                        addCellTabla(tablaMl, new Paragraph(fechaConFormato(per.fechaIncio), fontTd), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaMl, new Paragraph(numero(per.parcialCronograma, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaMl, new Paragraph(numero(per.parcialPlanilla, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaMl, new Paragraph(numero(retraso, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaMl, new Paragraph(numero(multa, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    }
                }
            }
        }
        if (periodos.size() > 2) {
            addCellTabla(tablaP0, new Paragraph("TOTAL", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaP0, new Paragraph("(" + diasTot + ")", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaP0, new Paragraph(numero(totCrono, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
            addCellTabla(tablaP0, new Paragraph(numero(totPlan, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
            addCellTabla(tablaP0, new Paragraph("", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }
        document.add(tablaP0);
        /* ***************************************************** Fin Tabla P0 *************************************************************/

        /* ***************************************************** Tabla Fr *****************************************************************/
        document.setPageSize(PageSize.A4);
        document.newPage();
        document.add(tablaHeaderPlanilla);
        Paragraph tituloFr = new Paragraph();
        addEmptyLine(tituloFr, 1);
        tituloFr.setAlignment(Element.ALIGN_CENTER);
        tituloFr.add(new Paragraph("Cálculo de Fr y Pr", fontTitle));
        addEmptyLine(tituloFr, 1);
        document.add(tituloFr);

        PdfPTable tablaFr = new PdfPTable(1 + periodos.size());
        tablaFr.setWidthPercentage(100);
        tams = [20]
        periodos.size().times {
            tams.add(10)
        }
        tablaFr.setWidths(arregloEnteros(tams))
        tablaFr.setWidthPercentage(100);

        PdfPTable inner3 = new PdfPTable(periodos.size() - 1)
        addCellTabla(inner3, new Paragraph("Periodo de variación y aplicación de fórmula polinómica", fontTh), [border: Color.BLACK, bwb: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])

        PdfPTable inner4 = new PdfPTable(1);
        inner4.setExtendLastRow(true);
        def prg = new Paragraph("Componentes", fontTh)
        def str = " "
        if (periodos.size() == 2) {
            str += "\n "
        }
        addCellTabla(inner4, new Paragraph(str, fontTh), [border: Color.BLACK, bcb: Color.LIGHT_GRAY, bwb: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner4, prg, [border: Color.BLACK, bg: Color.LIGHT_GRAY, bct: Color.LIGHT_GRAY, bcb: Color.LIGHT_GRAY, bwt: 1, bwb: 1, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner4, new Paragraph(" ", fontTh), [border: Color.BLACK, bct: Color.LIGHT_GRAY, bwt: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner4, new Paragraph("Anticipo", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFr, inner4, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        periodos.eachWithIndex { per, i ->
            if (i == 0) { //oferta
                PdfPTable inner5 = new PdfPTable(1);
                def prg2 = new Paragraph(per.titulo, fontTh)
                addCellTabla(inner5, new Paragraph(str, fontTh), [border: Color.BLACK, bcb: Color.LIGHT_GRAY, bwb: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, prg2, [border: Color.BLACK, bg: Color.LIGHT_GRAY, bct: Color.LIGHT_GRAY, bwt: 1, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, new Paragraph(fechaConFormato(per.fechaIncio), fontTh), [border: Color.BLACK, bwb: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, new Paragraph(numero(per.planilla.contrato.porcentajeAnticipo, 0) + "%", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFr, inner5, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//                addCellTabla(tablaFr, new Paragraph("asdf", fontTd), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            } else if (i == 1) { //anticipo
                PdfPTable inner5 = new PdfPTable(1);
                addCellTabla(inner5, new Paragraph(per.titulo, fontTh), [border: Color.BLACK, bwb: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, new Paragraph(fechaConFormato(per.fechaIncio), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, new Paragraph(per.titulo, fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner3, inner5, [border: Color.BLACK, bwb: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//                addCellTabla(inner3, new Paragraph("123", fontTd), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            } else { //otros
                addCellTabla(inner3, new Paragraph(fechaConFormato(per.fechaIncio), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            }
        }
        addCellTabla(tablaFr, inner3, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])

        def totalFr = 0

        ps.eachWithIndex { p, i ->
            addCellTabla(tablaFr, new Paragraph(p.indice.descripcion + " (" + p.numero + ")", fontTh), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            def vlinOferta
            periodos.eachWithIndex { per, j ->
                if (j == 0) { //oferta
                    if (i == 0) { //mano de obra
                        vlinOferta = per.totalLiq
                        addCellTabla(tablaFr, new Paragraph(numero(p.valor) + "\n" + numero(per.totalLiq), fontTh), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    } else {
                        vlinOferta = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodoLiquidacion).valor
                        addCellTabla(tablaFr, new Paragraph(numero(p.valor) + "\n" + numero(vlinOferta, 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    }
                } else {
                    def vlin
                    def dec
                    if (i == 0) {
                        vlin = per.totalLiq
                        dec = 3
                    } else {
                        vlin = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
                        dec = 2
                    }
//                    println "error "+p.indice+" "+p.indice.id+"  "+per.periodo.id+"   "+vlin+"  "+vlinOferta+"  "+p.valor+"  "+"  "+per.periodo.id
                    def valor = (vlin / vlinOferta * p.valor).round(3)
                    addCellTabla(tablaFr, new Paragraph(numero(vlin, dec) + "\n" + numero(valor), fontTh), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                }
            }
        }

        PdfPTable inner5 = new PdfPTable(periodos.size());

        def cells = [
                0: [new Paragraph("Fr", fontTh)],
                1: [new Paragraph("Fr-1", fontTh)],
                2: [new Paragraph("P0", fontTh)],
                3: [new Paragraph("Pr-P", fontTh)]
        ]

        def reajusteTotal = 0
        periodos.eachWithIndex { per, i ->
            if (i > 0) {
                def fr1 = (per.frLiq - 1).round(3)

                cells[0][i] = new Paragraph(numero(per.frLiq), fontTd)
                cells[1][i] = new Paragraph(numero(fr1), fontTd)
                cells[2][i] = new Paragraph(numero(per.p0, 2), fontTd)
                def t = (per.p0 * fr1).round(2)
                cells[3][i] = new Paragraph(numero(t, 2), fontTd)
                reajusteTotal += t
            }
        }
        reajusteTotal = reajusteTotal.toDouble().round(2)

        cells.each { k, v ->
            v.eachWithIndex { vv, i ->
                addCellTabla(inner5, vv, [border: Color.BLACK, align: i == 0 ? Element.ALIGN_CENTER : Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            }
        }

        addCellTabla(tablaFr, new Paragraph("1.000", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFr, inner5, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: periodos.size()])

        addCellTabla(tablaFr, new Paragraph("REAJUSTE TOTAL", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(tablaFr, new Paragraph(numero(reajusteTotal, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])

        if (periodos.size() > 2) {
            def planillaAnterior = planillasAnteriores.get(planillasAnteriores.size() - 2)
            def reajusteAnterior = (planillaAnterior.reajusteLiq).toDouble().round(2)
            addCellTabla(tablaFr, new Paragraph("REAJUSTE ANTERIOR", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
            addCellTabla(tablaFr, new Paragraph(numero(reajusteAnterior, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])
            addCellTabla(tablaFr, new Paragraph("REAJUSTE A PLANILLAR", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
            addCellTabla(tablaFr, new Paragraph(numero(reajusteTotal - reajusteAnterior, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])
        }

        document.add(tablaFr);
        /* ***************************************************** Fin Tabla Fr *************************************************************/

        /* ***************************************************** Multa no presentacion ****************************************************/
        if (planilla.tipoPlanilla.codigo != "A") {
            Paragraph tituloMultas = new Paragraph();
            addEmptyLine(tituloMultas, 1);
            tituloMultas.setAlignment(Element.ALIGN_CENTER);
            tituloMultas.add(new Paragraph("Multas", fontTitle));
            document.add(tituloMultas);

//            Paragraph tituloMultaNoPres = new Paragraph();
//            tituloMultaNoPres.setAlignment(Element.ALIGN_CENTER);
//            tituloMultaNoPres.add(new Paragraph("Multa por no presentación de planilla", fontTitle));
//            addEmptyLine(tituloMultaNoPres, 1);
//            document.add(tituloMultaNoPres);
//
//            def diasMax = 5
//            def fechaFinPer = planilla.fechaFin
//            def fechaMax = fechaFinPer
//
//            def noLaborables = ["Sat", "Sun"]
//
////            println fechaMax
//            diasMax.times {
//                fechaMax++
////                println fechaMax
//                def fmt = new java.text.SimpleDateFormat("EEE", new Locale("en"))
//                while (noLaborables.contains(fmt.format(fechaMax))) {
////                    println fmt.format(fechaMax)
//                    fechaMax++
////                    println fechaMax
//                }
//            }
////            println "***** "+fechaMax
//
//            def fechaPresentacion = planilla.fechaPresentacion
//            def retraso = fechaPresentacion - fechaMax + 1
//
//            def totalMulta = 0
//
//            def totalContrato = contrato.monto
//            def prmlMulta = contrato.multaPlanilla
//            if (retraso > 0) {
////            totalMulta = (totalContrato) * (prmlMulta / 1000) * retraso
//                totalMulta = (PeriodoPlanilla.findAllByPlanilla(planilla).sum { it.parcialCronograma }) * (prmlMulta / 1000) * retraso
//            } else {
//                retraso = 0
//            }
//
//            PdfPTable tablaPml = new PdfPTable(2);
//            tablaPml.setWidthPercentage(50);
//            addCellTabla(tablaPml, new Paragraph("Fecha de presentación de planilla", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaPml, new Paragraph(fechaPresentacion.format("dd-MM-yyyy"), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaPml, new Paragraph("Periodo de la planilla", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaPml, new Paragraph(planilla.fechaInicio.format("dd-MM-yyyy") + " a " + planilla.fechaFin.format("dd-MM-yyyy"), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaPml, new Paragraph("Fecha máxima de presentación", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaPml, new Paragraph(fechaMax.format("dd-MM-yyyy"), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaPml, new Paragraph("Días de retraso", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaPml, new Paragraph("" + retraso, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaPml, new Paragraph("Multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaPml, new Paragraph(numero(prmlMulta, 2) + "‰ de \$" + numero(totalContrato, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaPml, new Paragraph("Valor de la multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaPml, new Paragraph('$' + numero(totalMulta, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            document.add(tablaPml);
            /* ***************************************************** Fin Multa no presentacion ************************************************/

            /* ***************************************************** Multa retraso ************************************************************/
//            Paragraph tituloMultaRetraso = new Paragraph();
//            addEmptyLine(tituloMultaRetraso, 1);
//            tituloMultaRetraso.setAlignment(Element.ALIGN_CENTER);
//            tituloMultaRetraso.add(new Paragraph("Multa por retraso de obra", fontTitle));
//            addEmptyLine(tituloMultaRetraso, 1);
//            document.add(tituloMultaRetraso);
//
//            addCellTabla(tablaMl, new Paragraph("TOTAL", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaMl, new Paragraph(numero(totalMultaRetraso, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 4])
//
//            document.add(tablaMl);
        }
        /* ***************************************************** Fin Multa retraso ********************************************************/

        /* ***************************************************** Detalles *****************************************************************/
        if (conDetalles) {
            document.newPage()

            PdfPTable tablaDetalles = new PdfPTable(11);
            tablaDetalles.setWidthPercentage(100);
            tablaDetalles.setWidths(arregloEnteros([12, 35, 5, 11, 11, 11, 11, 11, 11, 11, 11]))

            def borderWidth = 1

            addCellTabla(tablaDetalles, new Paragraph("Obra", fontTh), prmsTdNoBorder)
            addCellTabla(tablaDetalles, new Paragraph(obra.nombre, fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 10])

            addCellTabla(tablaDetalles, new Paragraph("Lugar", fontTh), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph((obra.lugar?.descripcion ?: ""), fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
            addCellTabla(tablaDetalles, new Paragraph("Planilla", fontTh), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(planilla.numero, fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
            addCellTabla(tablaDetalles, new Paragraph(" ", fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaDetalles, new Paragraph("Ubicación", fontTh), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph("Parroquia " + obra.parroquia?.nombre + " Cantón " + obra.parroquia?.canton?.nombre, fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
            addCellTabla(tablaDetalles, new Paragraph("Monto", fontTh), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(planilla.contrato.monto, 2), fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
            addCellTabla(tablaDetalles, new Paragraph(" ", fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaDetalles, new Paragraph("Contratista", fontTh), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(planilla.contrato.oferta.proveedor.nombre, fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
            addCellTabla(tablaDetalles, new Paragraph("Periodo", fontTh), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(periodoPlanilla, fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
            addCellTabla(tablaDetalles, new Paragraph(" ", fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaDetalles, new Paragraph(" ", fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 11])


            addCellTabla(tablaDetalles, new Paragraph("N.", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph("Descripción del rubro", fontTh), [bwr: borderWidth, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph("U.", fontTh), [border: Color.BLACK, bwl: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph("Precio unitario", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph("Vol. contrat.", fontTh), [border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

            PdfPTable inner6 = new PdfPTable(3);
            addCellTabla(inner6, new Paragraph("Cantidades", fontTh), [border: Color.BLACK, bwl: borderWidth, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
            addCellTabla(inner6, new Paragraph("Ant.", fontTh), [border: Color.BLACK, bwl: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(inner6, new Paragraph("Act.", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(inner6, new Paragraph("Acu.", fontTh), [border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, inner6, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])

            PdfPTable inner7 = new PdfPTable(3);
            addCellTabla(inner7, new Paragraph("Valores", fontTh), [border: Color.BLACK, bwl: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
            addCellTabla(inner7, new Paragraph("Ant.", fontTh), [border: Color.BLACK, bwl: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(inner7, new Paragraph("Act.", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(inner7, new Paragraph("Acu.", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, inner7, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])

            /* ************* fin header *****************/

            addCellTabla(tablaDetalles, new Paragraph(" ", fontTh), [border: Color.BLACK, height: 72, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 11])
//            addCellTabla(tablaDetalles, new Paragraph("Observaciones:", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 5])
//            addCellTabla(tablaDetalles, new Paragraph("A) TOTAL AVANCE DE OBRA", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
//            addCellTabla(tablaDetalles, new Paragraph("totAnt", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph("totAct", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph("totAcu", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

//            addCellTabla(tablaDetalles, new Paragraph("a", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 5])
//            addCellTabla(tablaDetalles, new Paragraph("B) TOTAL REAJUSTE DE PRECIOS", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
//            addCellTabla(tablaDetalles, new Paragraph("1", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph("2", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph("3", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//
//            addCellTabla(tablaDetalles, new Paragraph("b", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 5])
//            addCellTabla(tablaDetalles, new Paragraph("C) TOTAL PLANILLA REAJUSTADA", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
//            addCellTabla(tablaDetalles, new Paragraph("4", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph("5", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph("6", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

            tablaDetalles.setHeaderRows(7);
            tablaDetalles.setFooterRows(1);
//            tablaDetalles.setHeaderRows(5);

            def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
            def precios = [:]
            def indirecto = obra.totales / 100
            preciosService.ac_rbroObra(obra.id)
            detalle.each {
                def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
                precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
            }
            def totalAnterior = 0, totalActual = 0, totalAcumulado = 0, sp = null

            detalle.eachWithIndex { vol, i ->
                def det = DetallePlanilla.findByPlanillaAndVolumenObra(planilla, vol)
                def anteriores = DetallePlanilla.findAllByPlanillaInListAndVolumenObra(planillasAnteriores[0..planillasAnteriores.size() - 2], vol)
                def cantAnt = anteriores.sum { it.cantidad } ?: 0
                def valAnt = anteriores.sum { it.monto } ?: 0
                def cant = det?.cantidad ?: 0
                def val = det?.monto ?: 0
                totalAnterior += valAnt
                totalActual += (val.toDouble().round(2))
                totalAcumulado += (val.toDouble().round(2) + valAnt)
                if (sp != vol.subPresupuestoId) {
                    addCellTabla(tablaDetalles, new Paragraph(vol.subPresupuesto.descripcion, fontTh), [bwr: borderWidth, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
                    addCellTabla(tablaDetalles, new Paragraph(" ", fontTh), [border: Color.BLACK, bwl: borderWidth, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph(" ", fontTh), [border: Color.BLACK, bwl: borderWidth, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph(" ", fontTh), [border: Color.BLACK, bwl: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    sp = vol.subPresupuestoId
                }
//                if ((i + 1) % 40 == 0) {
//                    document.add(tablaDetalles);
//                    tablaDetalles.deleteBodyRows();
//                    tablaDetalles.setSkipFirstHeader(true);
//                }

                addCellTabla(tablaDetalles, new Paragraph(vol.item.codigo, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(vol.item.nombre, fontTd), [bwr: borderWidth, border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaDetalles, new Paragraph(vol.item.unidad.codigo, fontTd), [bwl: borderWidth, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(precios[vol.id.toString()], 2, "hide"), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(vol.cantidad, 2, "hide"), fontTd), [bwr: borderWidth, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaDetalles, new Paragraph(numero(cantAnt, 2, "hide"), fontTd), [bwl: borderWidth, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(cant, 2, "hide"), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(cant + cantAnt, 2, "hide"), fontTd), [bwr: borderWidth, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaDetalles, new Paragraph(numero(valAnt, 2, "hide"), fontTd), [bwl: borderWidth, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(val, 2, "hide"), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(val + valAnt, 2, "hide"), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            }
            document.add(tablaDetalles);
        }
        /* ***************************************************** Fin Detalles *************************************************************/

        document.close();

//        // Create a reader
//        PdfReader reader = new PdfReader(baos.toByteArray());
//        // Create a stamper
////        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(RESULT));
//        PdfStamper stamper = new PdfStamper(reader, baos);
//        // Loop over the pages and add a header to each page
//        int n = reader.getNumberOfPages();
//        for (int i = 1; i <= n; i++) {
//            PdfPTable table = new PdfPTable(2);
//            table.setTotalWidth(527);
//            table.setLockedWidth(true);
//            table.getDefaultCell().setFixedHeight(20);
//            table.getDefaultCell().setBorder(Rectangle.BOTTOM);
//            table.addCell("Planilla");
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(String.format("Pág. %d of %d", i, n));
//            return table;
//        }
//        // Close the stamper
//        stamper.close();

        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def reportePlanillaCosto() {
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato

        def detalles = DetallePlanillaCosto.findAllByPlanilla(planilla)

        def baos = new ByteArrayOutputStream()
        def name = "planilla_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)
        Font fontTh = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);
        Font fontThFirmas = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontThUsar = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTdUsar = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
        Font fontResumen = new Font(Font.TIMES_ROMAN, 11, Font.BOLD);

        def prmsTablaHead = [bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsTabla = [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsTablaNum = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        Document document
        document = new Document(PageSize.A4.rotate());
        def pdfw = PdfWriter.getInstance(document, baos);

        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Planillas de la obra " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
        preface.add(new Paragraph("PLANILLA DE ${planilla.tipoPlanilla.nombre.toUpperCase()} DE LA OBRA " + obra.nombre, fontTituloGad));
        addEmptyLine(preface, 1);
        Paragraph preface2 = new Paragraph();
        preface2.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
        addEmptyLine(preface2, 1);
        document.add(logo)
        document.add(preface);
        document.add(preface2);

        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsTdBorder = [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsNmBorder = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        /* ************************************************************* HEADER PLANILLA ***************************************************************************/
        PdfPTable tablaHeaderPlanilla = new PdfPTable(5);
        tablaHeaderPlanilla.setWidthPercentage(100);
        tablaHeaderPlanilla.setWidths(arregloEnteros([12, 24, 10, 12, 24]))
        tablaHeaderPlanilla.setWidthPercentage(100);

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Obra", fontThUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(obra.nombre, fontTdUsar), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Lugar", fontThUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph((obra.lugar?.descripcion ?: ""), fontTdUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("Planilla", fontThUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(planilla.numero, fontTdUsar), prmsTdNoBorder)

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Ubicación", fontThUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(obra.parroquia?.nombre + " - Cantón " + obra.parroquia?.canton?.nombre, fontTdUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("Monto contrato", fontThUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(planilla.contrato.monto, 2), fontTdUsar), prmsTdNoBorder)

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Contratista", fontThUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(planilla.contrato.oferta.proveedor.nombre, fontTdUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("Fecha", fontThUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(fechaConFormato(planilla.fechaPresentacion, "dd-MMM-yyyy"), fontTdUsar), prmsTdNoBorder)

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Plazo", fontThUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(planilla.contrato.plazo, 0) + " días", fontTdUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThUsar), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTdUsar), prmsTdNoBorder)

        document.add(tablaHeaderPlanilla);
        /* ************************************************************* FIN HEADER PLANILLA ***************************************************************************/

        /* ************************************************************* TABLA DE DATOS ***************************************************************************/
        def tabla = new PdfPTable(7);
        tabla.setWidthPercentage(100);
        tabla.setWidths(arregloEnteros([10, 40, 10, 10, 10, 10, 10]))
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(1f);
        tabla.setSpacingBefore(10f);

        addCellTabla(tabla, new Paragraph("Factura N.", fontTh), prmsTablaHead)
        addCellTabla(tabla, new Paragraph("Descripción del rubro", fontTh), prmsTablaHead)
        addCellTabla(tabla, new Paragraph("U.", fontTh), prmsTablaHead)
        addCellTabla(tabla, new Paragraph("Valor sin IVA", fontTh), prmsTablaHead)
        addCellTabla(tabla, new Paragraph("Valor con IVA", fontTh), prmsTablaHead)
        addCellTabla(tabla, new Paragraph("% de indirectos (" + numero(detalles[0].indirectos, 0) + "%)", fontTh), prmsTablaHead)
        addCellTabla(tabla, new Paragraph("Valor total", fontTh), prmsTablaHead)

        def total = 0

        detalles.each { det ->
            def tot = det.montoIndirectos + det.montoIva
            total += tot
            addCellTabla(tabla, new Paragraph(det.factura, fontTd), prmsTabla)
            addCellTabla(tabla, new Paragraph(det.rubro, fontTd), prmsTabla)
            addCellTabla(tabla, new Paragraph(det.unidad.codigo, fontTd), prmsTabla)
            addCellTabla(tabla, new Paragraph(numero(det.monto, 2), fontTd), prmsTablaNum)
            addCellTabla(tabla, new Paragraph(numero(det.montoIva, 2), fontTd), prmsTablaNum)
            addCellTabla(tabla, new Paragraph(numero(det.montoIndirectos, 2), fontTd), prmsTablaNum)
            addCellTabla(tabla, new Paragraph(numero(tot, 2), fontTd), prmsTablaNum)
        }
        addCellTabla(tabla, new Paragraph("TOTAL", fontTh), [bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 6])
        addCellTabla(tabla, new Paragraph(numero(total, 2), fontTh), [bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

        document.add(tabla)
        /* ************************************************************* FIN TABLA DE DATOS ***************************************************************************/

        /* ************************************************************* TABLAS DE RESUMEN ***************************************************************************/
        def tablaResumen1 = new PdfPTable(2);
        tablaResumen1.setWidthPercentage(100);
        tablaResumen1.setWidths(arregloEnteros([80, 20]))
        tablaResumen1.setSpacingAfter(1f);
        tablaResumen1.setSpacingBefore(10f);

        addCellTabla(tablaResumen1, new Paragraph("TOTAL OBRAS BAJO LA MODALIDAD COSTO + PORCENTAJE", fontResumen), [padding: 5, bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaResumen1, new Paragraph(numero(total, 2), fontResumen), [padding: 5, border: 2, bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        document.add(tablaResumen1)

        def tablaResumen2 = new PdfPTable(2);
        tablaResumen2.setWidthPercentage(100);
        tablaResumen2.setWidths(arregloEnteros([80, 20]))
        tablaResumen2.setSpacingAfter(1f);
        tablaResumen2.setSpacingBefore(10f);

        def porcentaje = total / contrato.monto

        addCellTabla(tablaResumen2, new Paragraph("% TOTAL OBRAS ADICIONALES NO CONTRACTUALES", fontResumen), [padding: 5, bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaResumen2, new Paragraph(numero(porcentaje, 2), fontResumen), [padding: 5, border: 2, bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        document.add(tablaResumen2)
        /* ************************************************************* FIN TABLAS DE RESUMEN ***************************************************************************/

        /* ************************************************************* TABLA DE FIRMAS ***************************************************************************/
        def tablaFirmas = new PdfPTable(3);
        tablaFirmas.setWidthPercentage(100);

        def fiscalizador = planilla.fiscalizador
        def strFiscalizador = fiscalizador.titulo + " " + fiscalizador.nombre + " " + fiscalizador.apellido + "\nFiscalizador"
        tablaFirmas.setWidths(arregloEnteros([40, 20, 40]))

        addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 50, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 50, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 50, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFirmas, new Paragraph(strFiscalizador, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        document.add(tablaFirmas)
        /* ************************************************************* FIN TABLA DE FIRMAS ***************************************************************************/

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
        def strFecha
//        println ">>" + fecha + "    " + formato
        switch (formato) {
            case "MMM-yy":
                strFecha = meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yy")
                break;
            case "dd-MMM-yyyy":
                strFecha = "" + fecha.format("dd") + " de " + meses[fecha.format("MM").toInteger()] + " " + fecha.format("yyyy")
                break;
            default:
                strFecha = "Formato " + formato + " no reconocido"
                break;
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