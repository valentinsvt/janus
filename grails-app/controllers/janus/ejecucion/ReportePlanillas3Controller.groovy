package janus.ejecucion

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
import janus.Obra
import janus.Parametros
import janus.VolumenesObra
import janus.pac.PeriodoEjecucion

import java.awt.Color

class ReportePlanillas3Controller {
    def preciosService
    def diasLaborablesService


    def componeMes(mes) {
        if(mes[0..2] == 'Jan') mes = 'Ene' + mes[3..-1]
        if(mes[0..2] == 'Apr') mes = 'Abr' + mes[3..-1]
        if(mes[0..2] == 'Aug') mes = 'Ago' + mes[3..-1]
        if(mes[0..2] == 'Dec') mes = 'Dic' + mes[3..-1]
        mes
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
                case "MMM-yyyy":
                    strFecha = meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yyyy")
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

    private String cap(str) {
        return str.replaceAll(/[a-zA-Z_0-9áéíóúÁÉÍÓÚñÑüÜ]+/, {
            it[0].toUpperCase() + ((it.size() > 1) ? it[1..-1].toLowerCase() : '')
        })
    }

    private String nombrePersona(persona, tipo) {
//        println "nombrePersona" + persona
//        println tipo
        def str = ""
        if (persona) {
            switch (tipo) {
                case "pers":
                    str = cap((persona.titulo ? persona.titulo + " " : "") + persona.nombre + " " + persona.apellido)
                    break;
                case "prov":
                    str = cap((persona.titulo ? persona.titulo + " " : "") + persona.nombreContacto + " " + persona.apellidoContacto)
                    break;
            }
        }
//        println str
//        println "****************************************************"
        return str
    }

    private String nombrePersona(persona) {
        return nombrePersona(persona, "pers")
    }

    def reportePlanilla() {
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.obra
        def contrato = planilla.contrato

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

        def reajustesPlanilla = ReajustePlanilla.findAllByPlanilla(planilla)



        def conDetalles = true
        if (params.detalle) {
            conDetalles = Boolean.parseBoolean(params.detalle)
        }
        if (planilla.tipoPlanilla.codigo == "A") {
            conDetalles = false
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

        def periodoPlanilla
        if(!planilla.periodoAnticipo) {
            periodoPlanilla = PeriodosInec.list([sort: "fechaFin", order: "desc", "limit": 3]).first()
//            planilla.periodoAnticipo=periodoPlanilla
//            planilla.save()
        }else
            periodoPlanilla=planilla.periodoAnticipo
        if (planilla.tipoPlanilla.codigo == "A") {
//            println "aaaa"
            periodoPlanilla = PeriodosInec.list([sort: "fechaFin",order: "desc","limit":3]).first()
//            str += 'Anticipo (' + periodoInec?.descripcion + ")"
            periodoPlanilla = 'Anticipo (' + periodoPlanilla.descripcion + ")"
//            println "bbbbb"
        } else {
            if (planilla.tipoPlanilla.codigo == "L") {
                periodoPlanilla = "Liquidación del reajuste (${fechaConFormato(planilla.fechaPresentacion, 'dd-MMM-yyyy')})"
            } else {
                periodoPlanilla = 'del ' + fechaConFormato(planilla.fechaInicio, "dd-MMM-yyyy") + ' al ' + fechaConFormato(planilla.fechaFin, "dd-MMM-yyyy") + " (liquidación)"
            }
        }


        def baos = new ByteArrayOutputStream()
        def name = "planilla_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)
        Font fontTitle = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font fontTitle1 = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
        Font fontThTiny = new Font(Font.TIMES_ROMAN, 7, Font.BOLD);
        Font fontTdTiny = new Font(Font.TIMES_ROMAN, 7, Font.NORMAL);
        Font fontThFirmas = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTdFirmas = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        Document document
        document = new Document(PageSize.A4.rotate());
//        document.setMargins(2.5,2.5,2.5,1)
        def pdfw = PdfWriter.getInstance(document, baos);
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
            def tablaFirmas = new PdfPTable(5);
            tablaFirmas.setWidthPercentage(100);

            def parametros = Parametros.get(1)

            def contratista = planilla.contrato.oferta.proveedor
            def fiscalizador = planilla.fiscalizador
            def subdirector = parametros.subdirector
            def administrador = contrato.administrador

//            def strContratista = cap(contratista.titulo + " " + contratista.nombreContacto + " " + contratista.apellidoContacto + "\nContratista")
            def strContratista = nombrePersona(contratista, "prov") + "\nContratista"
            def strFiscalizador = nombrePersona(fiscalizador) + "\nFiscalizador"
//            def strSubdirector = nombrePersona(subdirector) + "\nSubdirector"
            def strSubdirector = "Ing. Miguel Velasteguí" + "\nSubdirector"
            def strAdmin = nombrePersona(administrador) + "\nAdministrador - Delegado"
            def strFechaPresentacion = fechaConFormato(planilla.fechaPresentacion, "dd-MMM-yyyy") + "\nFecha de presentación"
            def strFechaAprobacion = "\nFecha de aprobación"

            if (params.tipo == "detalle") {

                fontThFirmas = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
                fontTdFirmas = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);

                tablaFirmas.setWidths(arregloEnteros([35, 5,30,5, 35]))

                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 40, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 40, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 40, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 40, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 40, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])


                addCellTabla(tablaFirmas, new Paragraph(strContratista, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph(strFiscalizador, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph(strSubdirector, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaFirmas, new Paragraph(strFechaPresentacion, fontTdFirmas), [height: 35, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_TOP])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph(strFechaAprobacion, fontTdFirmas), [height: 35, bcb: Color.WHITE, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_TOP])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])


                tablaFirmas.setKeepTogether(true)

            } else if (params.tipo == "otro") {
//                if (planilla.tipoPlanilla.codigo != 'A') {
                if (params.orientacion == "horizontal") {
                    tablaFirmas.setWidths(arregloEnteros([30,5, 30,5, 30]))
                } else if (params.orientacion == "vertical") {
                    tablaFirmas.setWidths(arregloEnteros([25, 5,30,5, 25]))
                }
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph(planilla.tipoPlanilla.codigo == 'A' ? strAdmin : strFiscalizador, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//                }
            }


            document.add(tablaFirmas)
        }

        def tablaObservaciones = new PdfPTable(3);
        tablaObservaciones.setWidthPercentage(100);
        tablaObservaciones.setSpacingBefore(5)

        if(planilla.observaciones) {
            addCellTabla(tablaObservaciones, new Paragraph("Observaciones: " + planilla?.observaciones, fontThFirmas), [border: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
        }


        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        def prmsTdBorder = [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsNmBorder = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]


        /* ***************************************************** Header planilla **********************************************************/
//        println "header"
        def headerPlanilla = { params ->

            if (!params.espacio) {
                params.espacio = 2
            }

            Font fontThUsar = new Font(Font.TIMES_ROMAN, params.size, Font.BOLD);
            Font fontTdUsar = new Font(Font.TIMES_ROMAN, params.size, Font.NORMAL);

            Paragraph preface = new Paragraph();
            addEmptyLine(preface, 1);
            preface.setAlignment(Element.ALIGN_CENTER);
            preface.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
            preface.add(new Paragraph("PLANILLA DE ${planilla.tipoPlanilla.nombre.toUpperCase()} DE LA OBRA " + obra.nombre, fontTituloGad));
            addEmptyLine(preface, params.espacio);
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
            addCellTabla(tablaHeaderPlanilla, new Paragraph("Período", fontThUsar), prmsTdNoBorder)
            if(planilla.fechaInicio && planilla.fechaFin){
                addCellTabla(tablaHeaderPlanilla, new Paragraph(fechaConFormato(planilla.fechaInicio, "dd-MMM-yyyy")+" al "+fechaConFormato(planilla.fechaFin, "dd-MMM-yyyy"), fontTdUsar), prmsTdNoBorder)
            }else{
                addCellTabla(tablaHeaderPlanilla, new Paragraph(" ", fontTdUsar), prmsTdNoBorder)
            }
            addCellTabla(tablaHeaderPlanilla, new Paragraph("Plazo", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(planilla.contrato.plazo, 0) + " días", fontTdUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph("Valor obra", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(contrato.monto, 2), fontTdUsar), prmsTdNoBorder)

            document.add(tablaHeaderPlanilla);
        }

        headerPlanilla([size: 10])
        /* ***************************************************** Fin Header planilla ******************************************************/

        /* ***************************************************** Tabla B0 *****************************************************************/

        Paragraph tituloB0 = new Paragraph();
        addEmptyLine(tituloB0, 1);
        tituloB0.setAlignment(Element.ALIGN_CENTER);
        tituloB0.add(new Paragraph("Cálculo de B0", fontTitle));
        addEmptyLine(tituloB0, 1);
        document.add(tituloB0);

        def periodos = [:]
        def pagos = [:]
        def datos = [:]
        def datosFr = [:]
        def tams = [30, 8]
        def tamsFr = [30]
        def reajustesTotales = [:]

        reajustesPlanilla.each { rj ->
            def key = rj.periodo
            if(!periodos[key]) {
                periodos[key] = []
                pagos[key] = [:]
                pagos[key].indice = rj.periodoInec.fechaInicio.format("MMM-yyyy")
                pagos[key].valor = rj.valorReajustado
            }
            if(rj.periodo == 0) {
                periodos[key] += contrato.periodoInec.descripcion
                tams.add(10)
                tams.add(10)
                tamsFr.add(10)
            }
            if(rj.planillaReajustadaId != rj.planillaId) {
                pagos[key].fecha = rj.planillaReajustada?.fechaPago ? "Pago: "+rj.planillaReajustada.fechaPago.format("dd-MM-yyyy") : ""
            }



            periodos[key] += rj.mes
            tams.add(10)
            tams.add(10)
            tamsFr.add(10)


        }

        cs.each { c ->
            def key = c.id
            if(!datos[key]) {
                datos[key] = [:]
                datos[key].fp = c
                datos[key].detalles = [:]
            }
            reajustesPlanilla.each { rj ->
                def det = DetalleReajuste.findAllByReajustePlanillaAndFpContractual(rj, c)
                datos[key].detalles[rj.periodo] = det
            }
        }

//        println("rt " + reajustesTotales)


        PdfPTable tablaB0 = new PdfPTable(tams.size());
        tablaB0.setWidthPercentage(100);
        tablaB0.setWidths(arregloEnteros(tams))
        tablaB0.setWidthPercentage(100);
        tablaB0.setSpacingAfter(5f);

        def coeficientes = 0
        def totalOferta = 0
        def totalAvance = new double[30]
        def totalAnticipo = 0

        addCellTabla(tablaB0, new Paragraph("Cuadrilla Tipo", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
        periodos.each { per, meses ->
            if(per == 0) {
                addCellTabla(tablaB0, new Paragraph("OFERTA", fontTh), [border: Color.BLACK, bcr: Color.LIGHT_GRAY, bwr: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaB0, new Paragraph(meses[0], fontTh), [border: Color.BLACK, bcl: Color.LIGHT_GRAY, bwl: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaB0, new Paragraph("ANTICIPO", fontTh), [border: Color.BLACK, bcr: Color.LIGHT_GRAY, bwr: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaB0, new Paragraph(meses[1], fontTh), [border: Color.BLACK, bcl: Color.LIGHT_GRAY, bwl: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            } else {
                addCellTabla(tablaB0, new Paragraph("AVANCE", fontTh), [border: Color.BLACK, bcr: Color.LIGHT_GRAY, bwr: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaB0, new Paragraph(meses[0], fontTh), [border: Color.BLACK, bcl: Color.LIGHT_GRAY, bwl: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            }
        }
        addCellTabla(tablaB0, new Paragraph("", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
        pagos.each { per, pago ->
            if(per == 0) {
                addCellTabla(tablaB0, new Paragraph(" "), [border: Color.BLACK, bcr: Color.BLACK, bwr: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaB0, new Paragraph(" "), [border: Color.BLACK, bcr: Color.BLACK, bwr: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            }
            addCellTabla(tablaB0, new Paragraph(pago.fecha, fontTh), [border: Color.BLACK, bcr: Color.BLACK, bwr: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaB0, new Paragraph("Indice: " + pago.indice ?: '', fontTh), [border: Color.BLACK, bcr: Color.BLACK, bwr: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        }

        datos.each { k, v ->
            def c = v.fp
            def det = v.detalles

            addCellTabla(tablaB0, new Paragraph(c.indice.descripcion + "(" + c.numero + ")", fontTd), prmsTdBorder)
            addCellTabla(tablaB0, new Paragraph(numero(c.valor), fontTd), prmsNmBorder)
            coeficientes = (coeficientes + c.valor)

            det.each { per, dt ->
                if (dt.size == 1) {
                    dt = dt.first()
                    if (per == 0) {
                        addCellTabla(tablaB0, new Paragraph(numero(dt.indiceOferta, 2), fontTd), [border: Color.BLACK, bcr: Color.BLACK, bwr: 1, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaB0, new Paragraph(numero(dt.valorIndcOfrt), fontTd), [border: Color.BLACK, bcl: Color.BLACK, bwl: 0.1, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                        totalOferta = (totalOferta + dt.valorIndcOfrt)
                        totalAnticipo = (totalAnticipo + dt.valorIndcPrdo)
                    } else {
                        totalAvance[per] = (totalAvance[per] + dt.valorIndcPrdo)
                    }
                    addCellTabla(tablaB0, new Paragraph(numero(dt.indicePeriodo, 2), fontTd), [border: Color.BLACK, bcl: Color.BLACK, bwl: 0.1, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaB0, new Paragraph(numero(dt.valorIndcPrdo), fontTd), [border: Color.BLACK, bcl: Color.BLACK, bwl: 0.1, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                } else {
                    println "Hay mas de 1 detalle para la fp ${c} periodo ${per}"
                }
            }
        }

        addCellTabla(tablaB0, new Paragraph("TOTALES", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaB0, new Paragraph(numero(coeficientes), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

//        println "totalAvance " + totalAvance

        pagos.each {per, pago->
            if(per == 0){
                addCellTabla(tablaB0, new Paragraph("", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaB0, new Paragraph(numero(totalOferta), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaB0, new Paragraph("", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaB0, new Paragraph(numero(totalAnticipo), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            }
            addCellTabla(tablaB0, new Paragraph("", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaB0, new Paragraph(numero(totalAvance[per+1]), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }



        document.add(tablaB0)
        printFirmas([tipo: "otro", orientacion: "horizontal"])

        /* ***************************************************** Fin Tabla B0 *************************************************************/

        /* ***************************************************** Tabla P0 *****************************************************************/


        document.newPage()
        headerPlanilla([size: 10, espacio: 3])

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
        tablaMl.setWidths(arregloEnteros([18, 27, 24, 15, 16]))
        tablaMl.setWidthPercentage(50);
        tablaMl.setHorizontalAlignment(Element.ALIGN_LEFT)

        tablaMl.setSpacingAfter(5f);

        addCellTabla(tablaMl, new Paragraph("Mes y año", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaMl, new Paragraph("Cronograma", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaMl, new Paragraph("Planillado", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaMl, new Paragraph("Multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaMl, new Paragraph("Valor", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])


        def totCrono = 0
        def totPlan = 0
        def totlPo = 0

        reajustesPlanilla.each {
            if(it.periodo == 0){
                addCellTabla(tablaP0, new Paragraph("ANTICIPO", fontTh), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaP0, new Paragraph(it.mes, fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaP0, new Paragraph(numero(it.valorPo, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
            } else{
                addCellTabla(tablaP0, new Paragraph(it?.planilla?.tipoPlanilla?.nombre, fontTh), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaP0, new Paragraph(it.mes, fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaP0, new Paragraph(numero(it.parcialCronograma, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaP0, new Paragraph(numero(it.acumuladoCronograma, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                totCrono = it.acumuladoCronograma
                addCellTabla(tablaP0, new Paragraph(numero(it.parcialPlanillas, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaP0, new Paragraph(numero(it.acumuladoPlanillas, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                totPlan = it.acumuladoPlanillas
                addCellTabla(tablaP0, new Paragraph(numero(it.valorPo, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            }
            totlPo += it.valorPo
        }


        if (periodos.size() > 1) {
            addCellTabla(tablaP0, new Paragraph("TOTAL", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaP0, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaP0, new Paragraph(numero(totCrono, 2) , fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
            addCellTabla(tablaP0, new Paragraph(numero(totPlan, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
            addCellTabla(tablaP0, new Paragraph(numero(totlPo, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }


        document.add(tablaP0);
        printFirmas([tipo: "otro", orientacion: "horizontal"])

        /* ***************************************************** Fin Tabla P0 *************************************************************/

        /* ***************************************************** Tabla Fr *****************************************************************/
        document.setPageSize(PageSize.A4);
        document.newPage();
        headerPlanilla([size: 10, espacio: 3])

        Paragraph tituloFr = new Paragraph();
        addEmptyLine(tituloFr, 1);
        tituloFr.setAlignment(Element.ALIGN_CENTER);
        tituloFr.add(new Paragraph("Cálculo de Fr y Pr", fontTitle));
        addEmptyLine(tituloFr, 1);
        document.add(tituloFr);

        def totalesFr = [:]

        def totalesPeriodoFr = [:]
        def totalesCoeficientes = [:]

        ps.each { c ->
            def key = c.id
            if(!datosFr[key]) {
                datosFr[key] = [:]
                datosFr[key].fp = c
                datosFr[key].detalles = [:]
            }
            reajustesPlanilla.each { rj ->
                def det = DetalleReajuste.findAllByReajustePlanillaAndFpContractual(rj, c)
                datosFr[key].detalles[rj.periodo] = det

                if(!totalesFr[rj.periodo]) {
                    totalesFr[rj.periodo] = 0
                }

                if(!totalesPeriodoFr[rj.periodo]){
                    totalesPeriodoFr[rj.periodo] = 0
                }

                if(!totalesCoeficientes[rj.periodo]){
                    totalesCoeficientes[rj.periodo] = 0
                }
            }
        }


        PdfPTable tablaFr = new PdfPTable(tamsFr.size());
        tablaFr.setWidths(arregloEnteros(tamsFr))
        tablaFr.setWidthPercentage(100);
        tablaFr.setSpacingAfter(5f);

//        PdfPTable inner3 = new PdfPTable(tamsFr.size()-1)
//        addCellTabla(inner3, new Paragraph("Periodo de variación y aplicación de fórmula polinómica", fontTh), [border: Color.BLACK, bwb: 1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: tamsFr.size()-1])

        PdfPTable inner4 = new PdfPTable(1);
        inner4.setExtendLastRow(true);
        def prg = new Paragraph("Componentes", fontTh)
        def str = " "
        if (tamsFr.size() == 2) {
            str += "\n "
        }

        addCellTabla(inner4, new Paragraph(str, fontTh), [border: Color.BLACK, bcb: Color.LIGHT_GRAY, bwb: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner4, prg, [border: Color.BLACK, bg: Color.LIGHT_GRAY, bct: Color.LIGHT_GRAY, bcb: Color.LIGHT_GRAY, bwt: 1, bwb: 1, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner4, new Paragraph(" ", fontTh), [border: Color.BLACK, bct: Color.LIGHT_GRAY, bwt: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner4, new Paragraph("Anticipo", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFr, inner4, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])


        periodos.each { per, meses ->
            if(per == 0) {
                PdfPTable inner5 = new PdfPTable(1);
                def prg2 = new Paragraph("OFERTA", fontTh)
                addCellTabla(inner5, new Paragraph(str, fontTh), [border: Color.BLACK, bcb: Color.LIGHT_GRAY, bct: Color.LIGHT_GRAY, bwt: 0.1, bwb: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, prg2, [border: Color.BLACK, bg: Color.LIGHT_GRAY, bct: Color.LIGHT_GRAY, bwt: 0.1, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, new Paragraph(meses[0], fontTh), [border: Color.BLACK, bwb: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, new Paragraph( contrato?.porcentajeAnticipo + "%", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFr, inner5, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])


                PdfPTable inner6 = new PdfPTable(1);
                addCellTabla(inner6, new Paragraph(str, fontTh), [border: Color.BLACK, bcb: Color.LIGHT_GRAY, bct: Color.LIGHT_GRAY, bwt: 0.1, bwb: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner6, new Paragraph("ANTICIPO", fontTh), [border: Color.BLACK, bcb: Color.LIGHT_GRAY, bct: Color.LIGHT_GRAY, bwt: 0.1, bwb: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner6, new Paragraph(meses[1], fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner6, new Paragraph("ANTICIPO", fontTh), [border: Color.BLACK, bwb: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFr, inner6, [border: Color.BLACK, bwb: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

            } else{
                PdfPTable inner7 = new PdfPTable(1);
                addCellTabla(inner7, new Paragraph(meses[0], fontTh), [border: Color.BLACK, bcb: Color.LIGHT_GRAY, bct: Color.LIGHT_GRAY, bwt: 0.1, bwb: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFr, inner7, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

            }

        }


//        addCellTabla(tablaFr, inner3, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: tamsFr.size()-1])


        def totalCoeficientesPr = 0
        def totalCoeficientesPr2 = 0
        def totalIndiceOfertaPr = 0
        def totalIndicePeriodoPr = 0
        def totalIndicePeriodoPr2 = 0


        datosFr.each { k, v ->

            def c = v.fp
            def det = v.detalles
            addCellTabla(tablaFr, new Paragraph(c.indice.descripcion + "(" + c.numero + ")", fontTd), prmsTdBorder)


            det.each { per, dt ->
                if (dt.size == 1) {
                    dt = dt.first()
                    if (per == 0) {
                        addCellTabla(tablaFr, new Paragraph(numero(c.valor) + "\n" + numero(dt.indiceOferta, 3), fontTh), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaFr, new Paragraph(numero(dt.indicePeriodo, 3) + "\n" + numero(dt.valor), fontTh), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                        totalCoeficientesPr += c.valor

                        totalesPeriodoFr[per] +=  dt.indicePeriodo
                        totalesCoeficientes[per] += dt.valor
                    }else{
                        addCellTabla(tablaFr, new Paragraph(numero(dt.indicePeriodo, 3) + "\n" + numero(dt.valor), fontTh), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
//
                        totalesPeriodoFr[per] +=  dt.indicePeriodo
                        totalesCoeficientes[per] += dt.valor
                        totalesFr[per] += c.valor
                    }
                } else {
                    println "Hay mas de 1 detalle para la fp ${c} periodo ${per}"
                }
            }
        }

        addCellTabla(tablaFr, new Paragraph("Sumatoria", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFr, new Paragraph(numero(totalCoeficientesPr), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

        periodos.each { per, meses ->
            if(per == 0) {
                addCellTabla(tablaFr, new Paragraph(numero(totalesCoeficientes[per]), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            } else{
                addCellTabla(tablaFr, new Paragraph(numero(totalesCoeficientes[per]), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
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

        periodos.eachWithIndex { per3, i ->
            if(per3.key == 0){
                def fr1 = (totalesCoeficientes[per3.key] - 1).round(3)
                cells[0][i] = new Paragraph(numero(totalesCoeficientes[per3.key]), fontTd)
                cells[1][i] = new Paragraph(numero(fr1), fontTd)
//                cells[2][i] = new Paragraph(numero(totalesPeriodoFr[per3.key]), fontTd)
                cells[2][i] = new Paragraph(numero(reajustesPlanilla[0].valorPo,2), fontTd)
                def t = (reajustesPlanilla[0].valorPo * fr1).round(2)
                cells[3][i] = new Paragraph(numero(t,2), fontTd)
                reajusteTotal += t
            }else{
                def fr1 = (totalesCoeficientes[per3.key] - 1).round(3)
                cells[0][i] = new Paragraph(numero(totalesCoeficientes[per3.key]), fontTd)
                cells[1][i] = new Paragraph(numero(fr1), fontTd)
//                cells[2][i] = new Paragraph(numero(totalesPeriodoFr[per3.key]), fontTd)
                cells[2][i] = new Paragraph(numero(reajustesPlanilla[per3.key].valorPo,2), fontTd)
                def t = (reajustesPlanilla[per3.key].valorPo * fr1).round(2)
                cells[3][i] = new Paragraph(numero(t,2), fontTd)
                reajusteTotal += t
            }
        }

        cells.each { k, v ->
            v.eachWithIndex { vv, i ->
                addCellTabla(inner5, vv, [border: Color.BLACK, align: i == 0 ? Element.ALIGN_RIGHT : Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            }
        }

        PdfPTable tablaDatos = new PdfPTable(2);
        tablaDatos.setWidths(arregloEnteros([25, 50]))
        tablaDatos.setWidthPercentage(100);

        addCellTabla(tablaDatos, new Paragraph("Fecha elaboración", fontTh), [border: Color.LIGHT_GRAY, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos, new Paragraph(fechaConFormato(new Date(), "dd-MMM-yyyy"), fontTd), [border: Color.LIGHT_GRAY, bwr: 0.1, bcr: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatos, new Paragraph("Fuente", fontTh), [border: Color.LIGHT_GRAY, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos, new Paragraph("INEC ", fontTh), [border: Color.LIGHT_GRAY, bwr: 0.1, bcr: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatos, new Paragraph("Salarios", fontTh), [border: Color.LIGHT_GRAY, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos, new Paragraph("C.G.E.", fontTd), [border: Color.LIGHT_GRAY, bwr: 0.1, bcr: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])


        PdfPTable tablaDatos2 = new PdfPTable(1);
        tablaDatos2.setWidths(arregloEnteros([25]))
        tablaDatos2.setWidthPercentage(100);
        addCellTabla(tablaDatos2, new Paragraph("Fr", fontTh), [border: Color.BLACK, bwt: 0.1, bwb: 0.1, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos2, new Paragraph("Fr-1", fontTh), [border: Color.BLACK, bwb: 0.1, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos2, new Paragraph("P0", fontTh), [border: Color.BLACK, bwb: 0.1, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos2, new Paragraph("Pr-P", fontTh), [border: Color.BLACK, bwt: 0.1, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        PdfPTable tablaDatos3 = new PdfPTable(1);
        tablaDatos3.setWidths(arregloEnteros([25]))
        tablaDatos3.setWidthPercentage(100);

        addCellTabla(tablaDatos3, new Paragraph("REAJUSTE ANTERIOR", fontTh), [border: Color.BLACK, bwt: 0.1, bwb: 0.1, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos3, new Paragraph("REAJUSTE TOTAL", fontTh), [border: Color.BLACK, bwt: 0.1, bwb: 0.1, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos3, new Paragraph("REAJUSTE PLANILLAR", fontTh), [border: Color.BLACK, bwt: 0.1, bwb: 0.1, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

        PdfPTable tablaDatos4 = new PdfPTable(1);
        tablaDatos4.setWidths(arregloEnteros([25]))
        tablaDatos4.setWidthPercentage(100);


        def num =reajustesPlanilla.first().id
        def tt = num - (pagos.size()-1)
        def tg = []


        def rjTotalAnteriorD2 = 0
        def promedioActualD2 = 0
        def totalProcesadoD2 = 0
        def numD3 = 0
        def anterioresD

//        def anteriorRjD = reajustesPlanilla.size() - (reajustesPlanilla.size() -1)
        def anteriorRjD = 0

//        if(reajustesPlanilla.size() == 1){
//            anteriorRjD = reajustesPlanilla.size() -2
//        }
//        else if(reajustesPlanilla.size() == 2){
//            anteriorRjD = 0
//        }else{
//            anteriorRjD = reajustesPlanilla.size() - (reajustesPlanilla.size() -1)
//        }


//
//        println("---> " + reajustesPlanilla.size())
//        println("---> " + anteriorRjD)

//        if(anteriorRjD >= 0){
//            numD3 = reajustesPlanilla[anteriorRjD].planillaReajustada
//            anterioresD = ReajustePlanilla.findAllByPlanilla(numD3)
//            anterioresD.each{
//                rjTotalAnteriorD2 += it.valorReajustado
//            }
//        }else{
//            rjTotalAnteriorD2 = 0
//        }


        pagos.each{ per, pago ->
            promedioActualD2 += pago.valor
        }

//        totalProcesadoD2 = promedioActualD2 - rjTotalAnteriorD2

//        println("valor reajustado anterior " + rjTotalAnteriorD2)
//        println("valor actual " + promedioActualD2)




        //nuevo algoritmo para busqueda de planillas anteriores

        def ultimoReajuste = reajustesPlanilla.last().planillaReajustada
        def planillasReajuste = []
        def valoresAnteriores = []
        def totalAnteriores = 0

//        println("ultimo " + ultimoReajuste)

        if(reajustesPlanilla.size() > 1){
            reajustesPlanilla.each { pl ->
                if(pl.planillaReajustada != ultimoReajuste){
                    planillasReajuste += pl.planillaReajustada
                }
            }
        }else{
            planillasReajuste += -1
        }

//        println("planilla reajuste " + planillasReajuste.last())

        if(planillasReajuste.last() != -1){
            valoresAnteriores = ReajustePlanilla.findAllByPlanilla(planillasReajuste.last())
            valoresAnteriores.each {
                totalAnteriores += it.valorReajustado
            }
        }else{
            println("anticipo")
        }

//        println("anteriores " + valoresAnteriores)
//        println("total " + totalAnteriores)

        totalProcesadoD2 = promedioActualD2 - totalAnteriores

        addCellTabla(tablaDatos4, new Paragraph(numero(totalAnteriores ?: 0,2), fontTh), [border: Color.BLACK, bwt: 0.1, bwb: 0.1, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos4, new Paragraph(numero(promedioActualD2 ?: 0,2), fontTh), [border: Color.BLACK, bwt: 0.1, bwb: 0.1, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos4, new Paragraph(numero(totalProcesadoD2 ?: 0,2), fontTh), [border: Color.BLACK, bwt: 0.1, bwb: 0.1, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])



        addCellTabla(tablaFr, tablaDatos, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFr, tablaDatos2, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFr, inner5, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: periodos.size()])
        addCellTabla(tablaFr, tablaDatos3, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() +1])
        addCellTabla(tablaFr, tablaDatos4, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 1])

        document.add(tablaFr);

        printFirmas([tipo: "otro", orientacion: "vertical"])

        /* ***************************************************** Fin Tabla Fr *************************************************************/

        /* ***************************************************** Multas ************************************************************/
        def multaPlanilla = MultasPlanilla.findAllByPlanilla(planilla)


//        println("otra multa " + planilla.multaEspecial)

        if(multaPlanilla.size() != 0 || (planilla.multaEspecial != 0 && planilla.multaEspecial != null)){

            document.setPageSize(PageSize.A4);
            document.newPage();


            headerPlanilla([size: 10, espacio: 3])

            Paragraph tituloMt = new Paragraph();
            addEmptyLine(tituloMt, 1);
            tituloMt.setAlignment(Element.ALIGN_CENTER);
            tituloMt.add(new Paragraph("Multas", fontTitle));
            addEmptyLine(tituloMt, 1);
            document.add(tituloMt);



            //planilla 1

            multaPlanilla.each { mt ->


                if(mt.tipoMulta.id == 1){

                    Paragraph tituloMultaNoPres = new Paragraph();
                    tituloMultaNoPres.setAlignment(Element.ALIGN_CENTER);
                    tituloMultaNoPres.add(new Paragraph(mt.tipoMulta.descripcion, fontTitle));
                    addEmptyLine(tituloMultaNoPres, 1);
                    document.add(tituloMultaNoPres);

                    PdfPTable tablaPml = new PdfPTable(2);
                    tablaPml.setWidthPercentage(50);

                    tablaPml.setHorizontalAlignment(Element.ALIGN_LEFT)

                    addCellTabla(tablaPml, new Paragraph("Fecha de presentación de planilla", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaPml, new Paragraph(planilla.fechaPresentacion.format("dd-MM-yyyy"), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaPml, new Paragraph("Periodo de la planilla", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaPml, new Paragraph(planilla?.fechaInicio?.format("dd-MM-yyyy") + " a " + planilla?.fechaFin?.format("dd-MM-yyyy"), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaPml, new Paragraph("Fecha máxima de presentación", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaPml, new Paragraph(mt?.fechaMaxima?.format("dd-MM-yyyy"), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaPml, new Paragraph("Días de retraso", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaPml, new Paragraph(numero(mt.dias,0), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaPml, new Paragraph("Multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaPml, new Paragraph(numero(mt.descripcion, 2) + " x 1000 de \$" + numero((planilla.valor > 0 ? planilla.valor : totalCronoPlanilla), 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaPml, new Paragraph("Valor de la multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaPml, new Paragraph('$' + numero(mt.monto, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

                    document.add(tablaPml);
                }

                if(mt.tipoMulta.id == 2){

                    Paragraph tituloMultaNoPres = new Paragraph();
                    tituloMultaNoPres.setAlignment(Element.ALIGN_CENTER);
                    tituloMultaNoPres.add(new Paragraph(mt.tipoMulta.descripcion, fontTitle));
                    addEmptyLine(tituloMultaNoPres, 1);
                    document.add(tituloMultaNoPres);

                    PdfPTable tablaMultaDisp = new PdfPTable(2);
                    tablaMultaDisp.setWidthPercentage(50);
                    tablaMultaDisp.setSpacingAfter(10f);

                    tablaMultaDisp.setHorizontalAlignment(Element.ALIGN_LEFT)

                    addCellTabla(tablaMultaDisp, new Paragraph("Mes y Año", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph(mt.periodo, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph("Cronograma", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph(numero(mt.valorCronograma,2) , fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph("Planillado", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph(numero(mt.planilla.valor,2) , fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph("Multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph(mt.descripcion , fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph("Valor", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph('$' + numero(mt.monto, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    document.add(tablaMultaDisp);

                }

                if(mt.tipoMulta.id == 3){
                    Paragraph tituloMultaNoPres = new Paragraph();
                    tituloMultaNoPres.setAlignment(Element.ALIGN_CENTER);
                    tituloMultaNoPres.add(new Paragraph(mt.tipoMulta.descripcion, fontTitle));
                    addEmptyLine(tituloMultaNoPres, 1);
                    document.add(tituloMultaNoPres);

                    PdfPTable tablaMultaDisp = new PdfPTable(2);
                    tablaMultaDisp.setWidthPercentage(50);
                    tablaMultaDisp.setSpacingAfter(10f);

                    tablaMultaDisp.setHorizontalAlignment(Element.ALIGN_LEFT)

                    addCellTabla(tablaMultaDisp, new Paragraph("Días", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph(numero(mt.dias,0), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph("Multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph(mt.descripcion , fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph("Valor de la multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph('$' + numero(mt.monto, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    document.add(tablaMultaDisp);
                }
            }

            Paragraph tituloMultaUsu = new Paragraph();
            tituloMultaUsu.setAlignment(Element.ALIGN_CENTER);
            tituloMultaUsu.add(new Paragraph("Otras multas", fontTitle));
            addEmptyLine(tituloMultaUsu, 1);
            document.add(tituloMultaUsu);

            PdfPTable tablaMultaUsu = new PdfPTable(2);
            tablaMultaUsu.setWidthPercentage(50);
            tablaMultaUsu.setSpacingAfter(10f);

            tablaMultaUsu.setHorizontalAlignment(Element.ALIGN_LEFT)

            addCellTabla(tablaMultaUsu, new Paragraph("Concepto", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaMultaUsu, new Paragraph(planilla.descripcionMulta, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaMultaUsu, new Paragraph("Valor", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaMultaUsu, new Paragraph('$'+numero(planilla.multaEspecial, 2),fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

            document.add(tablaMultaUsu);


            printFirmas([tipo: "otro", orientacion: "vertical"])
        }








        /* ***************************************************** Fin Multa  ********************************************************/

        /* ***************************************************** Detalles *****************************************************************/


//        println "detalles"
        if (conDetalles) {
            PdfPTable tablaDetalles = null
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


                addCellTabla(tablaDetalles, new Paragraph("N.", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph("Descripción del rubro", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, bwr: borderWidth, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph("U.", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph("Precio unitario", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
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

//                addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                addCellTabla(tablaDetalles, new Paragraph(txt, fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                addCellTabla(tablaDetalles, new Paragraph(numero(params.ant, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(params.act, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(params.acu, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])


                def planillaAnterior, reajusteAnterior, reajustePlanillar


                planillaAnterior = planillasAnteriores.get(planillasAnteriores.size() - 2)
                reajusteAnterior = (planillaAnterior.reajuste).toDouble().round(2)


                def rjTotalAnteriorD4 = 0
                def promedioActualD4 = 0
                def totalProcesadoD4 = 0
                def numD4
                def anteriores4

                def anteriorRj4 = reajustesPlanilla.size() - 2

                if(anteriorRj4 >= 0){
                    numD4 = reajustesPlanilla[anteriorRj4].planillaReajustada
                    anteriores4 = ReajustePlanilla.findAllByPlanilla(numD4)

                    anteriores4.each{
                        rjTotalAnteriorD4 += it.valorReajustado

                    }
                }else{
                    rjTotalAnteriorD4 = 0
                }



                if (params.completo) {

//                    def bAnt = planillaAnterior.reajuste
                    def bAnt = rjTotalAnteriorD4
//                    println("valor anterior " + bAnt)
                    def bAct = planilla.reajuste - bAnt
                    def bAcu = bAct + bAnt

                    def cpAnt = 0
                    def cpAct = 0

                    def cpPlanilla = Planilla.findAllByPadreCosto(planilla)
                    if (cpPlanilla.size() == 1) {
                        cpPlanilla = cpPlanilla[0]
                        def planillasAnterioresCP = Planilla.withCriteria {
                            eq("contrato", contrato)
                            and {
                                lt("fechaIngreso", cpPlanilla.fechaIngreso)
                                eq("tipoPlanilla", TipoPlanilla.findByCodigo("C"))
                            }
                            order("id", "asc")
                        }

                        cpAct = cpPlanilla.valor
                        cpAnt = planillasAnterioresCP.sum { it.valor } ?: 0

                    } else if (cpPlanilla.size() == 0) {
//                        println "No hay planillas de cp"
                    } else {
                        println "WTF hay mas de una planilla cp asociada a esta planilla??? "
                        println "PLANILLA: " + planilla.id
                        println "PLANILLAS CP: " + cpPlanilla.id
                    }
                    def cpAcu = cpAnt + cpAct

                    def smAnt = params.ant + cpAnt
                    def smAct = params.act + cpAct
                    def smAcu = params.acu + cpAcu

                    def cAnt = params.ant + bAnt + cpAnt
                    def cAct = params.act + bAct + cpAct
                    def cAcu = params.acu + bAcu + cpAcu

                    def dAnt = planillasAnteriores[0..planillasAnteriores.size() - 2].sum { it.descuentos } ?: 0
//                    def d = (valorObra / planilla.contrato.monto) * planilla.contrato.anticipo - dAnt
                    def d = planilla.descuentos

                    def antAnt = dAnt
                    def antAct = d
                    def antAcu = d + dAnt

                    def m1 = planillasAnteriores[0..planillasAnteriores.size() - 2].sum { it.multaRetraso } ?: 0
                    def m2 = planillasAnteriores[0..planillasAnteriores.size() - 2].sum { it.multaPlanilla } ?: 0
                    def m3 = planillasAnteriores[0..planillasAnteriores.size() - 2].sum { it.multaIncumplimiento } ?: 0
                    def m4 = planillasAnteriores[0..planillasAnteriores.size() - 2].sum { it.multaDisposiciones } ?: 0

                    def multasAct = 0

                    multaPlanilla.each {
                        multasAct += it.monto
                    }

//                    println("multas actuales" + multasAct)

                    def listaPlanillasAnt
                    def planAnteriores = []
                    def planillaAnteriorMulta = reajustesPlanilla.size() -2
                    def anteriores1
                    if(planillaAnteriorMulta >= 0){
                        listaPlanillasAnt = reajustesPlanilla[planillaAnteriorMulta].planillaReajustada
                        anteriores1 = ReajustePlanilla.findAllByPlanilla(listaPlanillasAnt)

                        anteriores1.each{
                            planAnteriores += it.planilla
                        }
                    }


//                    println("pa " + planAnteriores)

                    def listaMultasAnt = MultasPlanilla.findAllByPlanilla(planAnteriores.unique())

//                    println("lista m" + listaMultasAnt)
                    def multasAnt = 0

                    listaMultasAnt.each {
                        multasAnt += it.monto
                    }

//                    println("multas anteriores" + multasAnt)

//                    def multasAnt = m1 + m2 + m3 + m4
//                    def multasAct = planilla.multaIncumplimiento + planilla.multaRetraso + planilla.multaDisposiciones + planilla.multaPlanilla+planilla.multaEspecial


//                    println "planilla retraso "+planilla.multaRetraso
//                    println "planilla inc "+planilla.multaIncumplimiento
                    def multasAcu = multasAnt + multasAct

//                    println "multas Ant = " + m1 + " + " + m2 + " + " + m3 + " + " + m4 + " = " + multasAnt
//                    println "multas Act = " + planilla.multaIncumplimiento + " + " + planilla.multaRetraso + " + " + planilla.multaDisposiciones + " + " + planilla.multaPlanilla + " = " + multasAct
//                    println "multas Acu = " + multasAcu

                    def totalAnt = cAnt - antAnt - multasAnt
                    def totalAct = cAct - antAct - multasAct
                    def totalAcu = cAcu - antAcu - multasAcu

                    def sbtAnt = bAnt + params.ant
                    def sbtAct = bAct + params.act
                    def sbtAcu = bAcu + params.acu

                    def rjTotalAnteriorD = 0
                    def promedioActualD = 0
                    def totalProcesadoD = 0
                    def numD2
                    def anteriores

//                    def anteriorRj = reajustesPlanilla.size() - 2
//
//                    if(anteriorRj >= 0){
//                        numD2 = reajustesPlanilla[anteriorRj].planillaReajustada
//                        anteriores = ReajustePlanilla.findAllByPlanilla(numD2)
//
//                        anteriores.each{
//                            rjTotalAnteriorD += it.valorReajustado
//
//                        }
//                    }else{
//                        rjTotalAnteriorD = 0
//                    }
//
//
                    pagos.each{ per, pago ->
                        promedioActualD += pago.valor
                    }

//                    totalProcesadoD = promedioActualD - rjTotalAnteriorD


                    //nuevo algoritmo para busqueda de planillas anteriores

                    def ultimoReajusteD = reajustesPlanilla.last().planillaReajustada
                    def planillasReajusteD = []
                    def valoresAnterioresD = []
                    def totalAnterioresD = 0

//                    println("ultimo " + ultimoReajusteD)

                    if(reajustesPlanilla.size() > 1){
                        reajustesPlanilla.each { pl ->
                            if(pl.planillaReajustada != ultimoReajusteD){
                                planillasReajusteD += pl.planillaReajustada
                            }
                        }
                    }else{
                        planillasReajusteD += -1
                    }

//                    println("planilla reajuste " + planillasReajusteD.last())

                    if(planillasReajusteD.last() != -1){
                        valoresAnterioresD = ReajustePlanilla.findAllByPlanilla(planillasReajusteD.last())
                        valoresAnterioresD.each {
                            totalAnterioresD += it.valorReajustado
                        }
                    }else{
                        println("anticipo")
                    }

//                    println("anteriores " + valoresAnterioresD)
//                    println("total " + totalAnterioresD)

                    totalProcesadoD = promedioActualD - totalAnterioresD



                    def subtotalAnterior = 0
                    def subtotalActual = 0
                    def subtotalAcumulado = 0

                    subtotalAnterior = (params.ant + totalAnterioresD)
                    subtotalActual = (params.act + totalProcesadoD)
                    subtotalAcumulado = (params.acu + promedioActualD)

                    def totalPlanillarAnterior = 0
                    def totalPlanillarActual = 0
                    def totalPlanillarAcumulado = 0

//                    totalPlanillarAnterior = (params.ant -  (antAnt + multasAnt))
//                    totalPlanillarActual = (params.act - (antAct + multasAct))
//                    totalPlanillarAcumulado = (params.acu - (antAcu + multasAcu))


                    totalPlanillarAnterior = (subtotalAnterior + cpAnt) - (antAnt + multasAnt)
                    totalPlanillarActual = (subtotalActual + cpAct) - (antAct + multasAct)
                    totalPlanillarAcumulado = (subtotalAcumulado + cpAcu) - (antAcu + multasAcu)

//                    addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                    addCellTabla(tablaDetalles, new Paragraph("REAJUSTE DE PRECIOS", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                    addCellTabla(tablaDetalles, new Paragraph(numero(totalAnterioresD, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(totalProcesadoD, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(promedioActualD, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                    addCellTabla(tablaDetalles, new Paragraph("SUBTOTAL", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                    addCellTabla(tablaDetalles, new Paragraph(numero(subtotalAnterior, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(subtotalActual, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(subtotalAcumulado, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                    addCellTabla(tablaDetalles, new Paragraph("RUBROS NO CONTRACTUALES COSTO + PORCENTAJE", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                    addCellTabla(tablaDetalles, new Paragraph(numero(cpAnt, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(cpAct, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(cpAcu, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                    addCellTabla(tablaDetalles, new Paragraph("SUMATORIA DE AVANCE DE OBRA Y COSTO + PORCENTAJE", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                    addCellTabla(tablaDetalles, new Paragraph(numero(smAnt, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(smAct, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(smAcu, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

//                    addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                    addCellTabla(tablaDetalles, new Paragraph("TOTAL PLANILLA REAJUSTADA INCLUIDO COSTO + PORCENTAJE", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                    addCellTabla(tablaDetalles, new Paragraph(numero((subtotalAnterior + cpAnt), 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero((subtotalActual + cpAct), 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero((subtotalAcumulado + cpAcu), 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

//                    addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                    addCellTabla(tablaDetalles, new Paragraph("DESCUENTOS CONTRACTUALES", fontThFooter), [border: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 11])

//                    addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                    addCellTabla(tablaDetalles, new Paragraph("ANTICIPO", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                    addCellTabla(tablaDetalles, new Paragraph(numero(antAnt, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(antAct, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(antAcu, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

//                    addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                    addCellTabla(tablaDetalles, new Paragraph("MULTAS", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                    addCellTabla(tablaDetalles, new Paragraph(numero(multasAnt, 2, "hide"), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(multasAct, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(multasAcu, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

//                    addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                    addCellTabla(tablaDetalles, new Paragraph("VALOR LIQUIDO A PAGAR", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                    addCellTabla(tablaDetalles, new Paragraph(numero(totalPlanillarAnterior, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(totalPlanillarActual, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(totalPlanillarAcumulado, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                }

            }

            /* ************* fin header *****************/

            def detalle = [:]
            detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])


            def precios = [:]
            detalle.each {
                def res = preciosService.precioVlob(obra.id, it.item.id)
                precios.put(it.id.toString(), res["precio"][0])
            }


            def totalAnterior = 0, totalActual = 0, totalAcumulado = 0, sp = null
            def height = 12
            def maxRows = 47     //45
            def extraRows = 9    //9
            def currentRows = 1
            def firmaRows = 6

            def sps = detalle.subPresupuesto.unique()
            def totalRows = detalle.size() + sps.size()

            def maxRowsLastPag = maxRows - extraRows

            def totalPags = Math.ceil((totalRows + extraRows) / maxRows).toInteger()

            if (totalRows % maxRows >= maxRowsLastPag) {
                totalPags++
            }

            def currentPag = 1

            def sumaParcialAnterior = 0, sumaTotalAnterior = 0
            def sumaParcialActual = 0, sumaTotalActual = 0
            def sumaParcialAcumulado = 0, sumaTotalAcumulado = 0

            def rowsCurPag = 1

            document.newPage()
            tablaDetalles = new PdfPTable(11);
            tablaDetalles.setWidthPercentage(100);
            tablaDetalles.setWidths(arregloEnteros([12, 35, 5, 11, 11, 11, 11, 11, 11, 11, 11]))
            tablaDetalles.setSpacingAfter(1f);
            printHeaderDetalle([pag: currentPag, total: totalPags])
            rowsCurPag = 1

            /** fin primer página **/

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
//                println "currentPag: $currentPag totalPags: $totalPags rowsCurPag: $rowsCurPag maxRowsLastPag: $maxRowsLastPag currentRows: $currentRows maxRows: $maxRows"
//                if (((currentPag == totalPags) && (rowsCurPag == maxRowsLastPag)) || ((currentRows % maxRows == 0) && (rowsCurPag >= maxRowsLastPag))) {
                if ((currentRows % maxRows == 0)) {
//                    println "crea nueva página por: ${(currentPag == totalPags) && (rowsCurPag == maxRowsLastPag)}, o, ${currentRows % maxRows == 0}"

                    document.newPage()

                    tablaDetalles = new PdfPTable(11);
                    tablaDetalles.setWidthPercentage(100);
                    tablaDetalles.setWidths(arregloEnteros([12, 35, 5, 11, 11, 11, 11, 11, 11, 11, 11]))

                    tablaDetalles.setSpacingAfter(1f);

                    printHeaderDetalle([pag: currentPag, total: totalPags])
                    rowsCurPag = 1
                }
                if (sp != vol.subPresupuestoId) {
//                    println vol.subPresupuesto.descripcion
                    addCellTabla(tablaDetalles, new Paragraph(vol.subPresupuesto.descripcion, fontThTiny), [height: height, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
                    addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    sp = vol.subPresupuestoId
                    currentRows++
                    rowsCurPag++
                }

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
                rowsCurPag++

                if ((currentRows % (maxRows -1) == 0)) {
                    printFooterDetalle(ant: sumaParcialAnterior, act: sumaParcialActual, acu: sumaParcialAcumulado)
                    sumaParcialAnterior = 0
                    sumaParcialActual = 0
                    sumaParcialAcumulado = 0

                    document.add(tablaDetalles)

                    printFirmas([tipo: "detalle", orientacion: "vertical"])
                    currentPag++

                }
            }
            if (currentRows % maxRows < maxRows - 1) {
//                println("entro 11, currentPag: $currentPag totalPags: $totalPags rowsCurPag: $rowsCurPag maxRowsLastPag: $maxRowsLastPag currentRows: $currentRows maxRows: $maxRows")
                printFooterDetalle(ant: sumaTotalAnterior, act: sumaTotalActual, acu: sumaTotalAcumulado, completo: true)

                sumaParcialAnterior = 0
                sumaParcialActual = 0
                sumaParcialAcumulado = 0

                document.add(tablaDetalles)

                printFirmas([tipo: "detalle", orientacion: "vertical"])

                document.add(tablaObservaciones)
            }
//            println "<<<<< currentPag: $currentPag totalPags: $totalPags rowsCurPag: $rowsCurPag maxRowsLastPag: $maxRowsLastPag currentRows: $currentRows maxRows: $maxRows"
        }

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

}
