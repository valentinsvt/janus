package janus.ejecucion

import com.itextpdf.text.BaseColor
import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.Image
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfImportedPage
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.Rectangle
import janus.Contrato
import janus.DbConnectionService
import janus.Obra
import janus.Parametros
import janus.VolumenContrato
import janus.VolumenesObra
import janus.pac.PeriodoEjecucion
import janus.pac.Proveedor

import java.awt.Color
import java.text.DecimalFormat


import com.lowagie.text.Phrase
import com.lowagie.text.pdf.PdfPageEventHelper
import com.lowagie.text.pdf.ColumnText

//import com.itextpdf.text.Document;
//import com.itextpdf.text.Element;
//import com.itextpdf.text.Phrase;
//import com.itextpdf.text.pdf.ColumnText;
//import com.itextpdf.text.pdf.PdfPageEventHelper;
//import com.itextpdf.text.pdf.PdfWriter;

class ReportePlanillas3Controller {
    def preciosService
    def planillasService
    def dbConnectionService


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


    private static void addCellTablaWrap(PdfPTable table, paragraph, params) {
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

//        cell.setNoWrap(true);
        cell.setMinimumHeight(10f);
//        cell.setFixedHeight(36f)
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
                    if(persona.tipo == 'N'){
                        str = cap((persona.titulo ? persona.titulo + " " : "") + persona.nombreContacto + " " + persona.apellidoContacto)
                    } else {
                        str = cap(persona.nombre)
                    }
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
//        println("reportePlanilla params " + params.id)
        def planilla = Planilla.get(params.id)

        if (planilla.tipoPlanilla.codigo == 'Q') {
            if (!planilla.contrato.fechaPedidoRecepcionContratista || !planilla.contrato.fechaPedidoRecepcionFiscalizador) {
                flash.message = "Por favor ingrese las fechas de pedido de recepción para generar la planilla " +
                        "final de avance (liquidación)"
                flash.clase = "alert-error"
                redirect(controller: "contrato", action: "fechasPedidoRecepcion", id: planilla.contrato.id)
                return
            }
        }

        def obra = planilla.contrato.obra
        def contrato = planilla.contrato

//        def cs = FormulaPolinomicaContractual.findAllByContratoAndNumeroIlike(contrato, "C%", [sort: "numero"])
        def cs = FormulaPolinomicaContractual.findAllByContratoAndReajusteAndNumeroIlike(contrato, planilla.formulaPolinomicaReajuste, "C%", [sort: "numero"])
        def ps = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                eq("reajuste", planilla.formulaPolinomicaReajuste)
                and {
                    ne("numero", "P0")
                    ilike("numero", "p%")
                }
                order("numero", "asc")
            }
        }

        println "cs: ${cs.size()}, ps: ${ps.size()}"

        def reajustesPlanilla = ReajustePlanilla.findAllByPlanillaAndValorPoNotEqual(planilla, 0, [sort: "periodo", order: "asc"])

//        def conDetalles = true
//        def conDetalles = planilla.tipoPlanilla.codigo != 'L'
        def conDetalles = false

        if(planilla.tipoPlanilla.codigo in ['P', 'Q', 'R']){
            conDetalles = true
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
        document.setMargins(50,30,30,28)  //se 28 equivale a 1 cm: izq, derecha, arriba y abajo
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

            def strContratista = nombrePersona(contratista, "prov") + "\nContratista"
            def strFiscalizador = nombrePersona(fiscalizador) + "\nFiscalizador"
            def strSubdirector = "Ing. Miguel Velasteguí" + "\nSubdirector"
            def strAdmin = nombrePersona(administrador) + "\nAdministrador del Contrato - Delegado"
            def strFechaPresentacion = fechaConFormato(planilla.fechaPresentacion, "dd-MMM-yyyy") + "\nFecha de presentación"
            def strFechaAprobacion = "\nFecha de aprobación"

            if (params.tipo == "detalle") {

                fontThFirmas = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
                fontTdFirmas = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);

                tablaFirmas.setWidths(arregloEnteros([35, 5, 30, 5, 35]))

                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 40, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 40, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 40, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

/* un espacio en blanco hace que no se imprima la línea para la firma */

                addCellTabla(tablaFirmas, new Paragraph(" ", fontThFirmas), [height: 40, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                // pone linea en blanco //
                addCellTabla(tablaFirmas, new Paragraph(" ", fontThFirmas), [height: 40, bwb: 1, bcb: Color.WHITE, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])


                addCellTabla(tablaFirmas, new Paragraph(strContratista, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph(strFiscalizador, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaFirmas, new Paragraph(strFechaPresentacion, fontTdFirmas), [height: 35, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_TOP])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph(strFechaAprobacion, fontTdFirmas), [height: 35, bcb: Color.WHITE, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_TOP])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

                tablaFirmas.setKeepTogether(true)

            } else if (params.tipo == "otro") {
                if (params.orientacion == "horizontal") {
                    tablaFirmas.setWidths(arregloEnteros([30,5, 30,5, 30]))
                } else if (params.orientacion == "vertical") {
                    tablaFirmas.setWidths(arregloEnteros([25, 5,35,5, 25]))
                }
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph(strFiscalizador, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            }

            document.add(tablaFirmas)
        }


        def tablaObservaciones = new PdfPTable(3);
        tablaObservaciones.setWidthPercentage(100);
        tablaObservaciones.setSpacingBefore(0)

        if(planilla.observaciones) {
            addCellTabla(tablaObservaciones, new Paragraph("Observaciones: " + planilla?.observaciones, fontTdFirmas), [border: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
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
                params.espacio = 1
            }

            Font fontThUsar = new Font(Font.TIMES_ROMAN, params.size, Font.BOLD);
            Font fontTdUsar = new Font(Font.TIMES_ROMAN, params.size, Font.NORMAL);

            Paragraph preface = new Paragraph();
            addEmptyLine(preface, 1);
            preface.setAlignment(Element.ALIGN_CENTER);
            preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
            preface.add(new Paragraph("PLANILLA DE ${planilla.tipoPlanilla.nombre.toUpperCase()} DE LA OBRA " + obra.nombre, fontTituloGad));
            addEmptyLine(preface, params.espacio);
            Paragraph preface2 = new Paragraph();
            preface2.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
//            addEmptyLine(preface2, 1);
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
/*
            if(planilla.fechaInicio && planilla.fechaFin){
                addCellTabla(tablaHeaderPlanilla, new Paragraph(fechaConFormato(planilla.fechaInicio, "dd-MMM-yyyy")+" al "+fechaConFormato(planilla.fechaFin, "dd-MMM-yyyy"), fontTdUsar), prmsTdNoBorder)
            }else{
                addCellTabla(tablaHeaderPlanilla, new Paragraph(" ", fontTdUsar), prmsTdNoBorder)
            }
*/
            def valorObra = ReajustePlanilla.executeQuery("select max(acumuladoPlanillas) from ReajustePlanilla " +
                    "where planilla = :p", [p: planilla])[0]?:0

            addCellTabla(tablaHeaderPlanilla, new Paragraph(ponePeriodoPlanilla(planilla), fontTdUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph("Plazo", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(planilla.contrato.plazo, 0) + " días", fontTdUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph("Valor obra", fontThUsar), prmsTdNoBorder)
//            addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(contrato.monto, 2), fontTdUsar), prmsTdNoBorder)
            addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(valorObra, 2), fontTdUsar), prmsTdNoBorder)

            document.add(tablaHeaderPlanilla);
        }

        headerPlanilla([size: 10])
        /* ***************************************************** Fin Header planilla ******************************************************/

        /* ***************************************************** Tabla B0 *****************************************************************/

        Paragraph tituloB0 = new Paragraph();
//        addEmptyLine(tituloB0, 1);
        tituloB0.setAlignment(Element.ALIGN_CENTER);
        tituloB0.add(new Paragraph("Cálculo de B0", fontTitle));
//        addEmptyLine(tituloB0, 1);
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
                pagos[key].indice = preciosService.componeMes(rj.periodoInec.fechaInicio.format("MMM-yyyy"))
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
//        printFirmas([tipo: "otro", orientacion: "horizontal"])
        document.add(firmas("otro", "horizontal", planilla))

        /* ***************************************************** Fin Tabla B0 *************************************************************/

        /* ***************************************************** Tabla P0 *****************************************************************/


        document.newPage()
        headerPlanilla([size: 10, espacio: 2])

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
            } else {
                addCellTabla(tablaP0, new Paragraph(it?.planillaReajustada?.tipoPlanilla?.nombre, fontTh), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaP0, new Paragraph(it.mes, fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                if (it.planillaReajustada.tipoPlanilla.codigo == 'O') {
                    addCellTabla(tablaP0, new Paragraph("", fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaP0, new Paragraph("", fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                } else {
                    addCellTabla(tablaP0, new Paragraph(numero(it.parcialCronograma, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaP0, new Paragraph(numero(it.acumuladoCronograma, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    totCrono = it.acumuladoCronograma
                }
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
//        printFirmas([tipo: "otro", orientacion: "horizontal"])
        document.add(firmas("otro", "horizontal", planilla))

        /* ***************************************************** Fin Tabla P0 *************************************************************/

        /* ***************************************************** Tabla Fr *****************************************************************/
        document.setPageSize(PageSize.A4);
        document.newPage();
        headerPlanilla([size: 10, espacio: 1])

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
//            println "----------------1"
            def key = c.id
            if(!datosFr[key]) {
                datosFr[key] = [:]
                datosFr[key].fp = c
                datosFr[key].detalles = [:]
            }
            reajustesPlanilla.each { rj ->
//                println "----------------2"
                def det = DetalleReajuste.findAllByReajustePlanillaAndFpContractual(rj, c)
                datosFr[key].detalles[rj.periodo] = det

                if(!totalesFr[rj.periodo]) {
                    totalesFr[rj.periodo] = 0
                }

                if(!totalesPeriodoFr[rj.periodo]){
                    totalesPeriodoFr[rj.periodo] = 0
                }

                if(!totalesCoeficientes[rj.periodo]){
//                    println "----------------3"
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

//        println "+++++++++++++++++++++ ${periodos}"
        periodos.each { per, meses ->
            if(per == 0) {
                PdfPTable inner5 = new PdfPTable(1);
                def prg2 = new Paragraph("OFERTA", fontTh)
                addCellTabla(inner5, new Paragraph(str, fontTh), [border: Color.BLACK, bcb: Color.LIGHT_GRAY, bct: Color.LIGHT_GRAY, bwt: 0.1, bwb: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, prg2, [border: Color.BLACK, bg: Color.LIGHT_GRAY, bct: Color.LIGHT_GRAY, bwt: 0.1, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, new Paragraph(meses[0].split(" ")[0][0..2] + " " + meses[0].split(" ")[1], fontTh), [border: Color.BLACK, bwb: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
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
//        println "periodos: $periodos"

        periodos.eachWithIndex { per3, i ->
//            println "per: $per3, key: ${per3.key}, totales: $totalesCoeficientes"
            if(per3.key == 0){
                def fr1 = (totalesCoeficientes[per3.key] - 1).round(3)
                cells[0][i] = new Paragraph(numero(totalesCoeficientes[per3.key]), fontTd)
                cells[1][i] = new Paragraph(numero(fr1), fontTd)
//                cells[2][i] = new Paragraph(numero(totalesPeriodoFr[per3.key]), fontTd)
                cells[2][i] = new Paragraph(numero(reajustesPlanilla[0].valorPo,2), fontTd)
                def t = (reajustesPlanilla[0].valorPo * fr1).round(2)
//                cells[3][i] = new Paragraph(numero(t,2), fontTd)
                cells[3][i] = new Paragraph(numero(reajustesPlanilla[0].valorReajustado,2), fontTd)
                reajusteTotal += t
            } else {
                def fr1 = (totalesCoeficientes[per3.key] - 1).round(3)
                cells[0][i] = new Paragraph(numero(totalesCoeficientes[per3.key]), fontTd)
                cells[1][i] = new Paragraph(numero(fr1), fontTd)

//                cells[2][i] = new Paragraph(numero(reajustesPlanilla[per3.key].valorPo,2), fontTd)

                cells[2][i] = new Paragraph(numero(reajustesPlanilla.find {it.periodo == per3.key}.valorPo,2), fontTd)

//                def t = (reajustesPlanilla[per3.key].valorPo * fr1).round(2)
                def t = (reajustesPlanilla.find {it.periodo == per3.key}.valorPo * fr1).round(2)
//                cells[3][i] = new Paragraph(numero(reajustesPlanilla[per3.key].valorReajustado), fontTd)
                cells[3][i] = new Paragraph(numero(reajustesPlanilla.find {it.periodo == per3.key}.valorReajustado), fontTd)
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

        pagos.each{ per, pago ->
            promedioActualD2 += pago.valor
        }

        //nuevo algoritmo para busqueda de planillas anteriores

        def ultimoReajuste = reajustesPlanilla.last().planillaReajustada
        def planillasReajuste = []
        def valoresAnteriores = []
        def totalAnteriores = 0

//        println "último " + ultimoReajuste

        if(reajustesPlanilla.size() > 1){
            reajustesPlanilla.each { pl ->
                if(pl.planillaReajustada != ultimoReajuste){
                    planillasReajuste += pl.planillaReajustada
                }
            }
        }else{
            planillasReajuste += -1
        }

//        println("planilla reajuste " + planillasReajuste)

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

        if(planilla.tipoPlanilla.codigo == 'L') {
            totalAnteriores = planillasService.reajusteAnteriorLq(planilla.id)
        }

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
        document.add(tablaObservaciones)

//        printFirmas([tipo: "otro", orientacion: "vertical"])
        document.add(firmas("otro", "vertical", planilla))

        /* ***************************************************** Fin Tabla Fr *************************************************************/

        /* ***************************************************** Multas ************************************************************/
        def multaPlanilla = MultasPlanilla.findAllByPlanilla(planilla)


//        println("otra multa " + planilla.multaEspecial)

        if(multaPlanilla.size() != 0 || (planilla.multaEspecial != 0 && planilla.multaEspecial != null)){

            document.setPageSize(PageSize.A4);
            document.newPage();


            headerPlanilla([size: 10, espacio: 2])

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
                    tablaPml.setWidthPercentage(60);

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
                    addCellTabla(tablaPml, new Paragraph(numero(mt.descripcion, 2) + " x 1000 de \$" + numero((planilla.valor > 0 ? planilla.valor : mt.valorCronograma), 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaPml, new Paragraph("Valor de la multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaPml, new Paragraph('$' + numero(mt.monto, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

                    document.add(tablaPml);
                }

                if(mt.tipoMulta.id == 2){   // incumplimiento del cronograma
                    def acPlanilla = reajustesPlanilla.last().acumuladoPlanillas
                    def acCronograma = reajustesPlanilla.last().acumuladoCronograma
//                    println "reajustes: ${reajustesPlanilla.last().acumuladoCronograma}"

                    Paragraph tituloMultaNoPres = new Paragraph();
                    tituloMultaNoPres.setAlignment(Element.ALIGN_CENTER);
                    tituloMultaNoPres.add(new Paragraph(mt.tipoMulta.descripcion, fontTitle));
                    addEmptyLine(tituloMultaNoPres, 1);
                    document.add(tituloMultaNoPres);

                    PdfPTable tablaMultaDisp = new PdfPTable(2);
                    tablaMultaDisp.setWidthPercentage(60);
                    tablaMultaDisp.setSpacingAfter(10f);

                    tablaMultaDisp.setHorizontalAlignment(Element.ALIGN_LEFT)

                    addCellTabla(tablaMultaDisp, new Paragraph("Mes y Año", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph(mt.periodo, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph("Cronograma", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph(numero(acCronograma,2) , fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph("Planillado", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//                    addCellTabla(tablaMultaDisp, new Paragraph(numero(mt.planilla.valor,2) , fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph(numero(acPlanilla,2) , fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
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
                    tablaMultaDisp.setWidthPercentage(60);
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

                if(mt.tipoMulta.id == 4){  //retraso de obra
                    Paragraph tituloMultaNoPres = new Paragraph();
                    tituloMultaNoPres.setAlignment(Element.ALIGN_CENTER);
                    tituloMultaNoPres.add(new Paragraph(mt.tipoMulta.descripcion, fontTitle));
                    addEmptyLine(tituloMultaNoPres, 1);
                    document.add(tituloMultaNoPres);

                    PdfPTable tablaMultaDisp = new PdfPTable(2);
                    tablaMultaDisp.setWidthPercentage(60);
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

            if(planilla.multaEspecial > 0) {
                Paragraph tituloMultaUsu = new Paragraph();
                tituloMultaUsu.setAlignment(Element.ALIGN_CENTER);
                tituloMultaUsu.add(new Paragraph("Otras multas", fontTitle));
                addEmptyLine(tituloMultaUsu, 1);
                document.add(tituloMultaUsu);

                PdfPTable tablaMultaUsu = new PdfPTable(2);
                tablaMultaUsu.setWidthPercentage(60);
                tablaMultaUsu.setSpacingAfter(10f);

                tablaMultaUsu.setHorizontalAlignment(Element.ALIGN_LEFT)

                addCellTabla(tablaMultaUsu, new Paragraph("Concepto", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaMultaUsu, new Paragraph(planilla.descripcionMulta, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaMultaUsu, new Paragraph("Valor", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                if(planilla.multaEspecial){
                    addCellTabla(tablaMultaUsu, new Paragraph('$'+numero(planilla.multaEspecial, 2),fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                } else {
                    addCellTabla(tablaMultaUsu, new Paragraph(''+numero(planilla.multaEspecial, 2),fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                }

                document.add(tablaMultaUsu);
            }

            printFirmas([tipo: "otro", orientacion: "vertical"])
        }



        /* ***************************************************** Fin Multa  ********************************************************/

        /* ***************************************************** Detalles *****************************************************************/


//        println "detalles"
        if (conDetalles) {
//            println "....1"
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
                addCellTabla(tablaHeaderDetalles, new Paragraph("Período", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaHeaderDetalles, new Paragraph(ponePeriodoPlanilla(planilla), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 5])
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

                addCellTabla(tablaDetalles, new Paragraph(txt, fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                addCellTabla(tablaDetalles, new Paragraph(numero(params.ant, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(params.act, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(params.acu, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])


                def planillaAnterior, reajusteAnterior, reajustePlanillar

                //nuevo algoritmo para busqueda de planillas anteriores
                def promedioActualD4 = 0
                def totalProcesadoD4 = 0
                def numD4
                def anteriores4

                pagos.each{ per, pago ->
                    promedioActualD4 += pago.valor
                }

                def ultimoReajusteD4 = reajustesPlanilla.last().planillaReajustada
                def planillasReajusteD4 = []
                def valoresAnterioresD4 = []
                def totalAnterioresD4 = 0


                if(reajustesPlanilla.size() > 1){
                    reajustesPlanilla.each { pl ->
                        if(pl.planillaReajustada != ultimoReajusteD4){

                            planillasReajusteD4 += pl.planillaReajustada
                        }
                    }
                }else{
                    planillasReajusteD4 += -1
                }


                if(planillasReajusteD4.last() != -1){
                    valoresAnterioresD4 = ReajustePlanilla.findAllByPlanilla(planillasReajusteD4.last())
                    valoresAnterioresD4.each {
                        totalAnterioresD4 += it.valorReajustado
                    }
                }else{
                    println("anticipo")
                }



                if (params.completo) {

//                    def bAnt = rjTotalAnteriorD4
                    def bAnt = totalAnterioresD4
                    def bAct = planilla.reajuste - bAnt
                    def bAcu = bAct + bAnt

                    def cpAnt = 0
                    def cpAct = 0

                    def planillasAnterioresCP = Planilla.withCriteria {
                        eq("contrato", contrato)
                        and {
                            lt("fechaIngreso", planilla.fechaIngreso)
                            eq("tipoPlanilla", TipoPlanilla.findByCodigo("C"))
                        }
                        order("id", "asc")
                    }

                    cpAnt = planillasAnterioresCP.sum { it.valor } ?: 0

                    def cpPlanilla = Planilla.findAllByPadreCosto(planilla)
                    if (cpPlanilla.size() == 1) {
                        cpPlanilla = cpPlanilla[0]
                        cpAct = cpPlanilla.valor
                    } else if (cpPlanilla.size() == 0) {
//                        println "No hay planillas de cp"
                    } else {
                        println "WTF hay mas de una planilla cp asociada a esta planilla??? "
                        println "PLANILLA: " + planilla.id
                        println "PLANILLAS CP: " + cpPlanilla.id
                    }
                    def cpAcu = cpAnt + cpAct

//                    println "params.ant ${params.ant}, ${params.ant}, ${params.acu}"
                    def smAnt = params.ant + cpAnt
                    def smAct = params.act + cpAct
                    def smAcu = params.acu + cpAcu

                    def cAnt = params.ant + bAnt + cpAnt
                    def cAct = params.act + bAct + cpAct
                    def cAcu = params.acu + bAcu + cpAcu

                    def dAntPlanilla = Planilla.findAllByContratoAndTipoPlanillaInListAndFechaPresentacionLessThan(contrato,
                            TipoPlanilla.findAllByCodigoInList(["P", "Q"]), planilla.fechaPresentacion, [sort: "fechaPresentacion"])

                    def dAnt = 0

                    dAntPlanilla.each {
                        dAnt += it.descuentos
                    }

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

                    multasAct += planilla.multaEspecial?:0
//                    println "multas actuales $multasAct especiales: ${planilla.multaEspecial}"

                    def listaPlanillasAnt


                    def multasAnt = 0

//                    println "planillasReajusteD4: $planillasReajusteD4"
                    if(planillasReajusteD4.last() != -1) {
                        def listaMultasAnt = MultasPlanilla.findAllByPlanillaInList(planillasReajusteD4)
                        listaMultasAnt.each {
                            multasAnt += it.monto
                        }
                    }

                    def multasAcu = multasAnt + multasAct

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

                    pagos.each{ per, pago ->
                        promedioActualD += pago.valor
                    }

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


//                    println("1 " + subtotalAnterior)
//                    println("2 " + cpAnt)
//                    println("3 " + antAnt)
//                    println("4 " + multasAnt)

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

                    if(planilla.noPagoValor > 0) {
                        // valor que no se paga por falta de recursos    todo: como se hará luego para pagar lo aqui descontado, como se imprime pedido de pago???
                        addCellTabla(tablaDetalles, new Paragraph(planilla.noPago, fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                        addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaDetalles, new Paragraph(numero(planilla.noPagoValor, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaDetalles, new Paragraph(numero(planilla.noPagoValor, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    }
//                    addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                    addCellTabla(tablaDetalles, new Paragraph("VALOR LIQUIDO A PAGAR", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                    addCellTabla(tablaDetalles, new Paragraph(numero(totalPlanillarAnterior, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(totalPlanillarActual - planilla.noPagoValor, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(totalPlanillarAcumulado - planilla.noPagoValor, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

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
            def maxRows = 48     //45
            def extraRows = 18   //9
            def currentRows = 1

            def sps = detalle.subPresupuesto.unique()
            def totalRows = detalle.size() + sps.size()

            def chequeoPg = 0

            def totalPags = Math.ceil((totalRows + extraRows) / maxRows).toInteger()

//            println "totalRows: $totalRows, extraRows: $extraRows, maxRows: $maxRows, --- totalPags: $totalPags, resto: ${(totalRows + extraRows) % maxRows}"

            if ((totalRows + extraRows) % maxRows <= extraRows) {
                chequeoPg = totalPags - 1
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

            //nuevo algoritmo para busqueda de planillas anteriores
            def promedioActualD5 = 0
            def totalProcesadoD5 = 0
            def numD5
            def anteriores5

            pagos.each{ per, pago ->
                promedioActualD5 += pago.valor
            }

            def ultimoReajusteD5 = reajustesPlanilla.last().planillaReajustada
            def planillasReajusteD5 = []
            def valoresAnterioresD5 = []
            def totalAnterioresD5 = 0

//            println "+++reajustesPlanilla: ${reajustesPlanilla.size()}, reajustes: ${reajustesPlanilla} y, ultimoReajusteD5: $ultimoReajusteD5"

            if(reajustesPlanilla.size() > 1){
                reajustesPlanilla.each { pl ->
                    if(pl.planillaReajustada != ultimoReajusteD5){
                        planillasReajusteD5 += pl.planillaReajustada
                    }
                }
            }else{
                planillasReajusteD5 += -1
            }

//            println "planilla reajuste $planillasReajusteD5"

            if(planillasReajusteD5.last() != -1){
                valoresAnterioresD5 = ReajustePlanilla.findAllByPlanilla(planillasReajusteD5.last())
                valoresAnterioresD5.each {
                    totalAnterioresD5 += it.valorReajustado
                }
            }else{
                println("anticipo")
            }

            detalle.eachWithIndex { vol, i ->
                def det = DetallePlanilla.findByPlanillaAndVolumenObra(planilla, vol)
//                def anteriores = DetallePlanilla.findAllByPlanillaInListAndVolumenObra(planillasAnteriores[0..planillasAnteriores.size() - 2], vol)

                def anteriores
                if(planillasReajusteD5.last() != -1) {
                    anteriores = DetallePlanilla.findAllByPlanillaInListAndVolumenObra(planillasReajusteD5, vol)
                }
//                def anteriores = DetallePlanilla.findAllByPlanillaInListAndVolumenObra(planillasReajusteD5, vol)

                def cantAnt = anteriores?.sum { it.cantidad } ?: 0
                def valAnt = anteriores?.sum { it.monto } ?: 0
                def cant = det?.cantidad ?: 0
                def val = det?.monto ?: 0
                totalAnterior += valAnt
                totalActual += (val.toDouble().round(2))
                totalAcumulado += (val.toDouble().round(2) + valAnt)

/*
                if ((currentRows % maxRows == 0)) {
                    println("---currentPag: $currentPag totalPags: $totalPags rowsCurPag: $rowsCurPag maxRowsLastPag: $maxRowsLastPag currentRows: $currentRows maxRows: $maxRows")
                    document.newPage()
                    tablaDetalles = new PdfPTable(11);
                    tablaDetalles.setWidthPercentage(100);
                    tablaDetalles.setWidths(arregloEnteros([12, 35, 5, 11, 11, 11, 11, 11, 11, 11, 11]))

                    tablaDetalles.setSpacingAfter(1f);

                    printHeaderDetalle([pag: currentPag, total: totalPags])
                    rowsCurPag = 1
                }
*/
                if (sp != vol.subPresupuestoId) {
//                    println vol.subPresupuesto.descripcion
                    addCellTabla(tablaDetalles, new Paragraph(vol.subPresupuesto.descripcion, fontThTiny), [height: height, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
//                    addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    sp = vol.subPresupuestoId
                    currentRows++
                    rowsCurPag++
                }
//                println "${vol.orden} : ${vol.item.nombre}"
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

//                if (currentRows % (maxRows - 1) == 0) {
                if( ((currentPag == chequeoPg) && ((currentRows + 6) % maxRows == 0)) || (currentRows % (maxRows) == 0)) {
//                    println("__currentPag: $currentPag chequeoPg: $chequeoPg totalPags: $totalPags rowsCurPag: $rowsCurPag currentRows: $currentRows maxRows: $maxRows")
                    printFooterDetalle(ant: sumaParcialAnterior, act: sumaParcialActual, acu: sumaParcialAcumulado)

                    sumaParcialAnterior = 0
                    sumaParcialActual = 0
                    sumaParcialAcumulado = 0

                    document.add(tablaDetalles)

                    printFirmas([tipo: "detalle", orientacion: "vertical"])
                    currentPag++

                    //-------------------
                    document.newPage()
                    tablaDetalles = new PdfPTable(11);
                    tablaDetalles.setWidthPercentage(100);
                    tablaDetalles.setWidths(arregloEnteros([12, 35, 5, 11, 11, 11, 11, 11, 11, 11, 11]))
                    tablaDetalles.setSpacingAfter(1f);
                    printHeaderDetalle([pag: currentPag, total: totalPags])
                    rowsCurPag = 1
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

//                document.add(tablaObservaciones)
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

    /**
     * Obtiene los reportes pdf de tablas, resumen de reajustes, multas y detalle de planilla si existe
     * crear reporte de resumen de reajustes
     **/
    def reportePlanillaNuevo() {
        println "reportePlanillaNuevo --> params: $params"
        def planilla = Planilla.get(params.id)
        if(planilla.tipoPlanilla.codigo.trim() == 'E') {
            redirect action: 'rptPlnlEntrega', params: params
        }

        println "tipo planilla: ${planilla.tipoPlanilla.codigo}"
        if (planilla.tipoPlanilla.codigo == 'Q') {
            if (!planilla.contrato.fechaPedidoRecepcionContratista || !planilla.contrato.fechaPedidoRecepcionFiscalizador) {
                flash.message = "Por favor ingrese las fechas de pedido de recepción para generar la planilla " +
                        "final de avance (liquidación)"
                flash.clase = "alert-error"
                redirect(controller: "contrato", action: "fechasPedidoRecepcion", id: planilla.contrato.id)
                return
            }
        }

        def rjpl = ReajustePlanilla.findAllByPlanilla(planilla)
        def reajustes = []
        def pl = new ByteArrayOutputStream()
        byte[] b
        def pdfs = []  /** pdfs a armar en el nuevo documento **/
        def contador = 0
        def name = "reajustes_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        rjpl.each {rj ->
            reajustes.add([planilla: rj.planilla, reajuste: rj.fpReajuste])
        }
//        println "reajustes: $reajustes"
        reajustes.unique()
//        println "reajustes unique: $reajustes"

        /* todo: hacer que se imprima el reporteTablas tantas veces como fprj hayan
        * crear nuevas FP en el contrato 24, igual que en la BD janus_prdc para probar */


        if(planilla.contrato.aplicaReajuste == 1) {
            //** genera B0, P0 y Fr de la planilla **
//        println "reajustes: ${reajustes}"
            reajustes.each {
                pl = reporteTablas(it.planilla, it.reajuste)
                pdfs.add(pl.toByteArray())
                contador++
            }
            if(planilla.tipoPlanilla.codigo == 'A') {
                println "invoca a resumen... planilla"
                pl = resumenAnticipo(planilla)
                pdfs.add(pl.toByteArray())
                contador++
            }
        }
        if(planilla.tipoPlanilla.codigo in ['P', 'Q', 'R', 'L']) {
            println "invoca multas"
            pl = multas(planilla, "")
            if(pl) {
                pdfs.add(pl.toByteArray())
                contador++
            }

            println "invoca detalle"
//            pl = detalle(planilla, planilla.tipoContrato)
            pl = detalleTodo(planilla, planilla.tipoContrato)
//            pl = detalleAdicional(planilla, planilla.tipoContrato)  /* columna adicional */

            pdfs.add(pl.toByteArray())
            contador++
        }

        if(contador > 1) {
            def baos = new ByteArrayOutputStream()
            Document document
            document = new Document(PageSize.A4);

            def pdfw = PdfWriter.getInstance(document, baos);
            document.open();
            PdfContentByte cb = pdfw.getDirectContent();

            pdfs.each {f ->
                PdfReader reader = new PdfReader(f);
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    //nueva página
                    document.newPage();
                    //importa la página "i" de la fuente "reader"
                    PdfImportedPage page = pdfw.getImportedPage(reader, i);
                    //añade página
                    cb.addTemplate(page, 0, 0);
                }
            }
            document.close();
            b = baos.toByteArray();
        } else {
            b = pl.toByteArray();
        }

        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=${name}")
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    /**
     * Obtiene los reportes pdf de tablas, resumen de reajustes, multas y detalle de planilla si existe
     * crear reporte de resumen de reajustes
     **/
    def rptPlnlEntrega() {
//        println "reportePlanillaNuevo params: $params"
        def planilla = Planilla.get(params.id)
        def pl = new ByteArrayOutputStream()
        byte[] b
        def pdfs = []  /** pdfs a armar en el nuevo documento **/
        def contador = 0
        def name = "planilla_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

//            println "invoca multas"
        pl = multas(planilla, "")
        if(pl) {
            pdfs.add(pl.toByteArray())
            contador++
        }

//            println "invoca detalle"
        pl = detalleEntrega(planilla, planilla.tipoContrato)
        pdfs.add(pl.toByteArray())
        contador++

        if(contador > 1) {
            def baos = new ByteArrayOutputStream()
            Document document
            document = new Document(PageSize.A4);

            def pdfw = PdfWriter.getInstance(document, baos);
            document.open();
            PdfContentByte cb = pdfw.getDirectContent();

            pdfs.each {f ->
                PdfReader reader = new PdfReader(f);
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    //nueva página
                    document.newPage();
                    //importa la página "i" de la fuente "reader"
                    PdfImportedPage page = pdfw.getImportedPage(reader, i);
                    //añade página
                    cb.addTemplate(page, 0, 0);
                }
            }
            document.close();
            b = baos.toByteArray();
        } else {
            b = pl.toByteArray();
        }

        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=${name}")
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    /**
     * Obtiene los reportes pdf de tablas, resumen de reajustes, multas y detalle de planilla si existe
     * crear reporte de resumen de reajustes
     **/
    def reportePlanillaTotal() {
//        println "reportePlanillaTotal params: $params"
        def planilla = Planilla.get(params.id)

        if (planilla.tipoPlanilla.codigo == 'Q') {
            if (!planilla.contrato.fechaPedidoRecepcionContratista || !planilla.contrato.fechaPedidoRecepcionFiscalizador) {
                flash.message = "Por favor ingrese las fechas de pedido de recepción para generar la planilla " +
                        "final de avance (liquidación)"
                flash.clase = "alert-error"
                redirect(controller: "contrato", action: "fechasPedidoRecepcion", id: planilla.contrato.id)
                return
            }
        }

        def rjpl = ReajustePlanilla.findAllByPlanilla(planilla)
        def reajustes = []
        def pl = new ByteArrayOutputStream()
        byte[] b
        def pdfs = []  /** pdfs a armar en el nuevo documento **/
        def contador = 0
        def name = "reajustes_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        rjpl.each {rj ->
            reajustes.add([planilla: rj.planilla, reajuste: rj.fpReajuste])
        }
//        println "reajustes: $reajustes"
        reajustes.unique()
//        println "reajustes unique: $reajustes"

        /* parte de complemetario */
        def rjplCmpl = ReajustePlanilla.findAllByPlanilla(planilla.planillaCmpl)
        def rjCmpl = []

        rjplCmpl.each {rj ->
            rjCmpl.add([planilla: rj.planilla, reajuste: rj.fpReajuste])
        }
        rjCmpl.unique()
//        println "rlCmpl unique: $rjCmpl"



        //** genera B0, P0 y Fr de la planilla **
        println "reajustes: ${reajustes}"
        reajustes.each {
            pl = reporteTablas(it.planilla, it.reajuste)
            pdfs.add(pl.toByteArray())
            contador++
        }

        rjCmpl.each {
            pl = reporteTablas(it.planilla, it.reajuste)
            pdfs.add(pl.toByteArray())
            contador++
        }

        if(planilla.tipoPlanilla.codigo == 'A') {
//            println "invoca a resumen... planilla"
            pl = resumenAnticipo(planilla)
            pdfs.add(pl.toByteArray())
            contador++
        }

        if(planilla.tipoPlanilla.codigo in ['P', 'Q']) {
//            println "invoca multas"
            pl = multas(planilla, 'T')
            pdfs.add(pl.toByteArray())
            contador++

//            println "invoca detalle"
            pl = detalle(planilla, 'T')
            pdfs.add(pl.toByteArray())
            contador++
        }

        if(contador > 1) {
            def baos = new ByteArrayOutputStream()
            Document document
            document = new Document(PageSize.A4);

            def pdfw = PdfWriter.getInstance(document, baos);
            document.open();
            PdfContentByte cb = pdfw.getDirectContent();

            pdfs.each {f ->
                PdfReader reader = new PdfReader(f);
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    //nueva página
                    document.newPage();
                    //importa la página "i" de la fuente "reader"
                    PdfImportedPage page = pdfw.getImportedPage(reader, i);
                    //añade página
                    cb.addTemplate(page, 0, 0);
                }
            }
            document.close();
            b = baos.toByteArray();
        } else {
            b = pl.toByteArray();
        }

        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=${name}")
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }


    /**
     * Imprime B0, P0 y Fr de la planilla
     **/
    def reporteTablas(planilla, fpReajuste) {
        println "reporteTablas de la planilla ${planilla.id} y fpReajuste: ${fpReajuste.id}"
        def obra = planilla.contrato.obra
        def contrato = planilla.contrato
        def reajustesPlanilla = ReajustePlanilla.findAllByPlanillaAndFpReajuste(planilla, fpReajuste, [sort: "periodo", order: "asc"])
        def rjpl = reajustesPlanilla.first()
//        println "reajustesPlanilla: $reajustesPlanilla, Po: ${reajustesPlanilla.valorPo}"

        /* crea el PDF */
        def baos = new ByteArrayOutputStream()
//        def name = "planilla_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)
        Font fontTitle = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 7, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Document document
        document = new Document(PageSize.A4.rotate());
        document.setMargins(50,30,30,28)  // 28 equivale a 1 cm: izq, derecha, arriba y abajo
        def pdfw = PdfWriter.getInstance(document, baos);
        document.resetHeader()
        document.resetFooter()

        document.open();
        document.addTitle("Planillas de la obra " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        def bordeThDerecho = [border: Color.BLACK, bcr: Color.LIGHT_GRAY, bwr: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def bordeThIzquierdo = [border: Color.BLACK, bcl: Color.LIGHT_GRAY, bwl: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def bordeThRecuadro = [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]

        def bordeTdSinBorde = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def bordeTdRecuadro = [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def bordeTdRecuadroDer = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]


//        headerPlanilla([size: 10])
        /* ---------------------- Fin Header planilla --------------------------*/

        document.add(titlLogo())
        document.add(titlInst(1, planilla, obra));
        document.add(titlSbtt(planilla.fechaIngreso));
        document.add(encabezado(2, 10, planilla, ""))

        /* ********************************************* Tabla B0 *****************************************************/

        def tbBo = planillasService.armaTablaFr(rjpl.planilla.id, rjpl.fpReajuste.id, 'c')
        def titlIndices = tbBo.pop()
        def titulos = tbBo.pop()
//        println "tbBo: $tbBo"
//        println "resumen titulosIndices: $titlIndices"

        Paragraph tituloB0 = new Paragraph();
        addEmptyLine(tituloB0, 1);
        tituloB0.setAlignment(Element.ALIGN_CENTER);
        tituloB0.add(new Paragraph("Cálculo de B0: ${rjpl.fpReajuste.descripcion}", fontTitle));
        tituloB0.setSpacingAfter(10); //pone espacio luego de la línea
//        addEmptyLine(tituloB0, 1);
        document.add(tituloB0);

        def periodos = [:]
//        def pagos = [:]
        def columnas = [30, 8]

        titulos.size().times {   //añade tantas columnas como títulos hayan
            columnas.add(10)
        }

        PdfPTable tablaB0 = new PdfPTable(columnas.size());
        tablaB0.setWidthPercentage(100);
        tablaB0.setWidths(arregloEnteros(columnas))
        tablaB0.setWidthPercentage(100);
        tablaB0.setSpacingAfter(5f);

        def coeficientes = 0.0
        def totalIndiceOferta = 0.0
        def totalAvance = []


        addCellTabla(tablaB0, new Paragraph("Cuadrilla Tipo", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])

//        println "tbBo: $tbBo, periodos: $periodos"

        def borde = bordeThDerecho
        for(i in 0..titulos.size()-1){
            addCellTabla(tablaB0, new Paragraph(titulos[i], fontTh), borde)
            borde = (borde == bordeThDerecho)? bordeThIzquierdo : bordeThDerecho
        }
        addCellTabla(tablaB0, new Paragraph("Nombre del Índice", fontTh), bordeThRecuadro)
        addCellTabla(tablaB0, new Paragraph("Valor", fontTh), bordeThRecuadro)
        for(i in 0..titlIndices.size()-1){
            addCellTabla(tablaB0, new Paragraph(titlIndices[i], fontTh), bordeThRecuadro)
        }

        def planillas = (tbBo[0].size() - 5)/2
        tbBo.each { d ->
            addCellTabla(tablaB0, new Paragraph(d.descripcion + "(" + d.numero + ")", fontTd), bordeTdRecuadro)
            addCellTabla(tablaB0, new Paragraph(numero(d.coeficiente,3), fontTd), bordeTdRecuadroDer)
            addCellTabla(tablaB0, new Paragraph(numero(d.indice,2), fontTd), bordeTdRecuadroDer)
            addCellTabla(tablaB0, new Paragraph(numero(d.valor,3), fontTd), bordeTdRecuadroDer)

            for(i in 1..planillas){
                if(!totalAvance[i-1]) totalAvance[i-1] = 0
                addCellTabla(tablaB0, new Paragraph(numero(d["indc$i"], 2), fontTd), bordeTdRecuadroDer)
                addCellTabla(tablaB0, new Paragraph(numero(d["vlor$i"], 3), fontTd), bordeTdRecuadroDer)
//                println "----- d[vlor $i ]: ${d["vlor$i"]}"
                totalAvance[i-1] += d["vlor$i"]
//                totalAvance[i-1] += d["vlor$i"]?:0
            }
            totalIndiceOferta += d.valor
            coeficientes += d.coeficiente
        }

        addCellTabla(tablaB0, new Paragraph("TOTALES", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaB0, new Paragraph(numero(coeficientes), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaB0, new Paragraph("", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaB0, new Paragraph(numero(totalIndiceOferta), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        for(i in 1..planillas) {
            addCellTabla(tablaB0, new Paragraph("", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaB0, new Paragraph(numero(totalAvance[i-1]), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }

        document.add(tablaB0)

//        imprimirFirmas([tipo: "otro", orientacion: "horizontal"])
        document.add(firmas("otro", "horizontal", planilla))

        /* --------------------------- Fin Tabla B0 ----------------------------*/

        /* ***************************************************** Tabla P0 *****************************************************************/

        document.newPage()
//        headerPlanilla([size: 10, espacio: 2])
        document.add(titlLogo())
        document.add(titlInst(1, planilla, obra));
        document.add(titlSbtt(planilla.fechaIngreso));
        document.add(encabezado(2, 10, planilla, ""))

        Paragraph tituloP0 = new Paragraph();
        addEmptyLine(tituloP0, 1);
        tituloP0.setAlignment(Element.ALIGN_CENTER);
        tituloP0.add(new Paragraph("Cálculo de P0 ${rjpl.fpReajuste.descripcion}", fontTitle));
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

        def tbPo = planillasService.armaTablaPo(rjpl.planilla.id, rjpl.fpReajuste.id)
//        def tbPo = planillasService.armaTablaPo(reajustesPlanilla.planilla.id, reajustesPlanilla.fpReajuste.id)
        def totCrono = 0
        def totPlan = 0
        def totlPo = 0

//        println "----> tbpo: $tbPo"
        for(i in 0..tbPo.size()-1 ){
            def tipo = ""
            if(tbPo[i].tipo.indexOf(' ') > 0) {
                tipo = "${tbPo[i].tipo.substring(0, tbPo[i].tipo.indexOf(' '))}"
            } else {
                tipo = "${tbPo[i].tipo}"
            }
            addCellTabla(tablaP0, new Paragraph(tipo, fontTh), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaP0, new Paragraph(tbPo[i].mes, fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaP0, new Paragraph(tbPo[i].crpa >= 0 ? numero(tbPo[i].crpa, 2) : '', fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaP0, new Paragraph(tbPo[i].crpa >= 0 ? numero(tbPo[i].crac, 2) : '', fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaP0, new Paragraph(tbPo[i].crpa >= 0 ? numero(tbPo[i].plpa, 2) : '', fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaP0, new Paragraph(tbPo[i].crpa >= 0 ? numero(tbPo[i].plac, 2) : '', fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaP0, new Paragraph(numero(tbPo[i].po, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE,])
            totCrono = tbPo[i].crac
            totPlan = tbPo[i].plac
            totlPo += tbPo[i].po
        }

        addCellTabla(tablaP0, new Paragraph("TOTAL", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(tablaP0, new Paragraph(numero(totCrono, 2) , fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(tablaP0, new Paragraph(numero(totPlan, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(tablaP0, new Paragraph(numero(totlPo, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaP0);

//        imprimirFirmas([tipo: "otro", orientacion: "horizontal"])
        document.add(firmas("otro", "horizontal", planilla))
        /* --------------------------- Fin Tabla P0 ----------------------------*/

        /* ***************************************************** Tabla Fr *****************************************************************/
        document.setPageSize(PageSize.A4);
        document.newPage();
//        headerPlanilla([size: 10, espacio: 1])
        document.add(titlLogo())
        document.add(titlInst(1, planilla, obra));
        document.add(titlSbtt(planilla.fechaIngreso));
        document.add(encabezado(2, 10, planilla, ""))

        Paragraph tituloFr = new Paragraph();
        addEmptyLine(tituloFr, 1);
        tituloFr.setAlignment(Element.ALIGN_CENTER);
        tituloFr.add(new Paragraph("Cálculo de Fr y Pr ${rjpl.fpReajuste.descripcion}", fontTitle));
        addEmptyLine(tituloFr, 1);
        document.add(tituloFr);

        def tbFr = planillasService.armaTablaFr(rjpl.planilla.id, rjpl.fpReajuste.id, 'p')
        tbFr.pop()  // elimina los titulos de indices
        tbFr.pop()  // elimina los titulos
//        println "... tbFr: ${tbFr}"
        planillas = ((tbFr[0].size() - 3)/2).toInteger()   // incluye columna de oferta
        columnas = [30]
        planillas.times {   //añade tantas columnas como títulos hayan
            columnas.add(10)
        }
//        println "....... columnas fr: $columnas"

        PdfPTable tablaFr = new PdfPTable(columnas.size());
        tablaFr.setWidths(arregloEnteros(columnas))
        tablaFr.setWidthPercentage(100);
        tablaFr.setSpacingAfter(5f);

        addCellTabla(tablaFr, new Paragraph("Componentes", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        def txto = ""
        def j = 0
        for(i in 0..planillas-1){
            txto = "${titulos[j++]} \n ${titulos[j++]}"
            addCellTabla(tablaFr, new Paragraph(txto, fontTh), [border: Color.BLACK, bcr: Color.LIGHT_GRAY, bwr: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        }

        addCellTabla(tablaFr, new Paragraph("Nombre del Índice", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        j = 0
        for(i in 0..planillas-1){
            txto = "${titlIndices[j++]} \n ${titlIndices[j++]}"
            addCellTabla(tablaFr, new Paragraph(txto, fontTh), [border: Color.BLACK, bcr: Color.LIGHT_GRAY, bwr: 0.1, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        }

        planillas = (tbFr[0].size() - 5)/2  // incluye opferta
        tbFr.each { d ->
            addCellTabla(tablaFr, new Paragraph(d.descripcion, fontTd), bordeTdRecuadro)
            txto = "${numero(d.coeficiente, 3)} \n ${numero(d.indice)}"
            addCellTabla(tablaFr, new Paragraph(txto, fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

            for(i in 1..planillas){
                txto = numero(d["indc$i"], 3) + "\n" + numero(d["indc$i"]/d.indice*d.coeficiente)
                addCellTabla(tablaFr, new Paragraph(txto, fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            }
        }

        addCellTabla(tablaFr, new Paragraph("Sumatoria", fontTh), bordeTdRecuadroDer + [bg: Color.LIGHT_GRAY])
        addCellTabla(tablaFr, new Paragraph(numero(1), fontTh), bordeTdRecuadroDer + [bg: Color.LIGHT_GRAY])
        for(i in 0..planillas-1) {
            addCellTabla(tablaFr, new Paragraph(numero(reajustesPlanilla[i].factor), fontTh), bordeTdRecuadroDer + [bg: Color.LIGHT_GRAY])
        }

        def reajusteTotal = 0

        PdfPTable tablaDatos = new PdfPTable(2);
        tablaDatos.setWidths(arregloEnteros([25, 50]))
        tablaDatos.setWidthPercentage(100);

        addCellTabla(tablaDatos, new Paragraph("Fecha elaboración", fontTh), [border: Color.LIGHT_GRAY, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos, new Paragraph(fechaConFormato(new Date(), "dd-MMM-yyyy"), fontTd), [border: Color.LIGHT_GRAY, bwr: 0.1, bcr: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatos, new Paragraph("Fuente", fontTh), [border: Color.LIGHT_GRAY, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos, new Paragraph("INEC ", fontTh), [border: Color.LIGHT_GRAY, bwr: 0.1, bcr: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatos, new Paragraph("Salarios", fontTh), [border: Color.LIGHT_GRAY, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos, new Paragraph("C.G.E.", fontTd), [border: Color.LIGHT_GRAY, bwr: 0.1, bcr: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        // añade tablaDatos
        addCellTabla(tablaFr, tablaDatos, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        PdfPTable tb1 = new PdfPTable(1);
        tb1.setWidths(arregloEnteros([10]))
        tb1.setWidthPercentage(100);
        addCellTabla(tb1, new Paragraph("Fr", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tb1, new Paragraph("Fr - 1", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tb1, new Paragraph("P0", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tb1, new Paragraph("Pr - P", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaFr, tb1, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

//        println "reajustes: ${reajustesPlanilla.valorReajustado}"

        for(i in 0..planillas-1) {
            PdfPTable tb = new PdfPTable(1);
            tb.setWidths(arregloEnteros([10]))
            tb.setWidthPercentage(100);

            addCellTabla(tb, new Paragraph(numero(reajustesPlanilla[i].factor), fontTd), bordeTdRecuadroDer + [bg: Color.LIGHT_GRAY])
            addCellTabla(tb, new Paragraph(numero(reajustesPlanilla[i].factor - 1), fontTd), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tb, new Paragraph(numero(reajustesPlanilla[i].valorPo, 2), fontTd), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tb, new Paragraph(numero(reajustesPlanilla[i].valorReajustado, 2), fontTd), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

            reajusteTotal += reajustesPlanilla[i].valorReajustado
            addCellTabla(tablaFr, tb, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        }


        PdfPTable tablaDatos3 = new PdfPTable(1);
        tablaDatos3.setWidths(arregloEnteros([25]))
        tablaDatos3.setWidthPercentage(100);

        addCellTabla(tablaDatos3, new Paragraph("REAJUSTE ANTERIOR", fontTh), [border: Color.BLACK, bwt: 0.1, bwb: 0.1, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos3, new Paragraph("REAJUSTE TOTAL", fontTh), [border: Color.BLACK, bwt: 0.1, bwb: 0.1, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos3, new Paragraph("REAJUSTE PLANILLAR", fontTh), [border: Color.BLACK, bwt: 0.1, bwb: 0.1, bwl: 0.1, bcl: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

        PdfPTable tablaDatos4 = new PdfPTable(1);
        tablaDatos4.setWidths(arregloEnteros([25]))
        tablaDatos4.setWidthPercentage(100);

//        def anterior = planillasService.reajusteAnterior(rjpl.planilla.id, rjpl.fpReajuste.id)
        def anterior = planillasService.reajusteAnterior(rjpl.planilla)

        addCellTabla(tablaDatos4, new Paragraph(numero(anterior ?: 0,2), fontTh), bordeThRecuadro + [align: Element.ALIGN_RIGHT])
        addCellTabla(tablaDatos4, new Paragraph(numero(reajusteTotal ?: 0,2), fontTh), bordeThRecuadro + [align: Element.ALIGN_RIGHT])
        addCellTabla(tablaDatos4, new Paragraph(numero((reajusteTotal - anterior) ?: 0,2), fontTh), bordeThRecuadro + [align: Element.ALIGN_RIGHT])


        addCellTabla(tablaFr, tablaDatos3, [colspan: (1+ planillas).toInteger()]) //++++
        addCellTabla(tablaFr, tablaDatos4, [colspan: 1])

        document.add(tablaFr);

//        imprimirFirmas([tipo: "otro", orientacion: "vertical"])
        document.add(firmas("otro", "vertical", planilla))
        /* ------------------------- Fin Tabla Fr ---------------------------*/

        document.close();
        pdfw.close()
        return baos
/*
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
*/
    }

    def ponePeriodoPlanilla(plnl) {
        def periodoPlanilla
        if (plnl.tipoPlanilla.codigo == "A") {
            periodoPlanilla = "Anticipo"
        } else if (plnl.tipoPlanilla.codigo == "L") {
            periodoPlanilla = "Liquidación del reajuste (${fechaConFormato(plnl.fechaPresentacion, 'dd-MMM-yyyy')})"
        } else if (plnl.tipoPlanilla.codigo in ["P", "D"]){
            periodoPlanilla = 'del ' + fechaConFormato(plnl.fechaInicio, "dd-MMM-yyyy") + ' al ' + fechaConFormato(plnl.fechaFin, "dd-MMM-yyyy")
        } else if (plnl.tipoPlanilla.codigo == "Q"){
            periodoPlanilla = 'del ' + fechaConFormato(plnl.fechaInicio, "dd-MMM-yyyy") + ' al ' +
                    fechaConFormato(plnl.contrato.fechaPedidoRecepcionFiscalizador, "dd-MMM-yyyy") + " (liquidación)"
        }
        periodoPlanilla
    }

    def resumenAnticipo(planilla) {

//        println "reporteTablas de la planilla ${planilla.id}"
        def obra = planilla.contrato.obra
        def contrato = planilla.contrato

        /* crea el PDF */
        def baos = new ByteArrayOutputStream()

        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)
        Font fontTitle = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Document document
        document = new Document();
        document.setMargins(50,30,30,28)  // 28 equivale a 1 cm: izq, derecha, arriba y abajo
        def pdfw = PdfWriter.getInstance(document, baos);
        document.resetHeader()
        document.resetFooter()

        document.open();
        document.addTitle("Planillas de la obra " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        def bordeThRecuadro = [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def bordeTdRecuadro = [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def bordeThRecuadroDer= [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def bordeTdRecuadroDer = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]


        document.setPageSize(PageSize.A4);
        document.newPage();
//            headerPlanilla([size: 10, espacio: 2])
        document.add(titlLogo())
        document.add(titlInst(1, planilla, obra));
        document.add(titlSbtt(planilla.fechaIngreso));
        document.add(encabezado(2, 10, planilla, ""))


        Paragraph titulo = new Paragraph();
        addEmptyLine(titulo, 1);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.add(new Paragraph("Resumen del Reajuste del Anticipo", fontTitle));
        addEmptyLine(titulo, 1);
        document.add(titulo);

        /** anticipo */

        PdfPTable tbAntc = new PdfPTable(3);
        tbAntc.setWidths(arregloEnteros([30, 10, 10]))
        tbAntc.setWidthPercentage(90);
        tbAntc.setSpacingAfter(5f);

        addCellTabla(tbAntc, new Paragraph("Fórmula polinómica", fontTh), bordeThRecuadro)
        addCellTabla(tbAntc, new Paragraph("Po", fontTh), bordeThRecuadro)
        addCellTabla(tbAntc, new Paragraph("Reajuste", fontTh), bordeThRecuadro)

        def tbAn = planillasService.armaResumenAntc(planilla.id)
        def total = 0

        tbAn.each {
            addCellTabla(tbAntc, new Paragraph(it.fp, fontTd), bordeTdRecuadro)
            addCellTabla(tbAntc, new Paragraph(numero(it.po, 2), fontTd), bordeTdRecuadroDer)
            addCellTabla(tbAntc, new Paragraph(numero(it.vlor,2), fontTd), bordeTdRecuadroDer)
            total += it.vlor
        }

        addCellTabla(tbAntc, new Paragraph("Total reajuste", fontTh), bordeThRecuadro + [colspan: 2])
        addCellTabla(tbAntc, new Paragraph(numero(total, 2), fontTh), bordeThRecuadroDer)

        document.add(tbAntc)

        document.close();
        pdfw.close()
        return baos
    }


    def multas(planilla, tipo) {
//        println "reporteTablas de la planilla ${planilla.id}"
        def obra = planilla.contrato.obra
        def monto = planilla.contrato.monto
        if(tipo == 'T') {
            def cmpl = Contrato.findByPadre(planilla.contrato)
            monto += cmpl.monto
        }
        /* crea el PDF */
        def baos = new ByteArrayOutputStream()
        def multaPlanilla = MultasPlanilla.findAllByPlanilla(planilla)

        Font fontTitle = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Document document
        document = new Document();
        document.setMargins(50,30,30,28)  // 28 equivale a 1 cm: izq, derecha, arriba y abajo
        def pdfw = PdfWriter.getInstance(document, baos);
        document.resetHeader()
        document.resetFooter()

        document.open();
        document.addTitle("Planillas de la obra " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

//        def bordeTdSinBorde = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

//        println("otra multa " + planilla.multaEspecial)
        if(multaPlanilla.size() != 0 || (planilla.multaEspecial != 0 && planilla.multaEspecial != null)){
            document.setPageSize(PageSize.A4);
            document.newPage();
//            headerPlanilla([size: 10, espacio: 2])
            document.add(titlLogo())
            document.add(titlInst(1, planilla, obra));
            document.add(titlSbtt(planilla.fechaIngreso));
            document.add(encabezado(2, 10, planilla, tipo))


            Paragraph tituloMt = new Paragraph();
            addEmptyLine(tituloMt, 1);
            tituloMt.setAlignment(Element.ALIGN_CENTER);
            tituloMt.add(new Paragraph("Multas", fontTitle));
            addEmptyLine(tituloMt, 1);
            document.add(tituloMt);

            multaPlanilla.each { mt ->

                if(mt.tipoMulta.id == 1){

                    Paragraph tituloMultaNoPres = new Paragraph();
                    tituloMultaNoPres.setAlignment(Element.ALIGN_CENTER);
                    tituloMultaNoPres.add(new Paragraph(mt.tipoMulta.descripcion, fontTitle));
                    addEmptyLine(tituloMultaNoPres, 1);
                    document.add(tituloMultaNoPres);

                    PdfPTable tablaPml = new PdfPTable(2);
                    tablaPml.setWidthPercentage(60);

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
                    addCellTabla(tablaPml, new Paragraph(numero(mt.descripcion, 2) + " x 1000 de \$" + numero((planilla.valor > 0 ? planilla.valor : mt.valorCronograma), 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaPml, new Paragraph("Valor de la multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaPml, new Paragraph('$' + numero(mt.monto, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

                    document.add(tablaPml);
                }

                def reajustesPlanilla = ReajustePlanilla.findAllByPlanillaAndValorPoNotEqual(planilla, 0, [sort: "periodo", order: "asc"])

                if(mt.tipoMulta.id == 2){   // incumplimiento del cronograma
                    def acPlanilla = reajustesPlanilla.last().acumuladoPlanillas
                    if(tipo == 'T') {
                        acPlanilla += planilla.planillaCmpl.valor
                    }
                    def acCronograma = reajustesPlanilla.last().acumuladoCronograma
//                    println "reajustes: ${reajustesPlanilla.last().acumuladoCronograma}"

                    Paragraph tituloMultaNoPres = new Paragraph();
                    tituloMultaNoPres.setAlignment(Element.ALIGN_CENTER);
                    tituloMultaNoPres.add(new Paragraph(mt.tipoMulta.descripcion, fontTitle));
                    addEmptyLine(tituloMultaNoPres, 1);
                    document.add(tituloMultaNoPres);

                    PdfPTable tablaMultaDisp = new PdfPTable(2);
                    tablaMultaDisp.setWidthPercentage(60);
                    tablaMultaDisp.setSpacingAfter(10f);

                    tablaMultaDisp.setHorizontalAlignment(Element.ALIGN_LEFT)

                    addCellTabla(tablaMultaDisp, new Paragraph("Mes y Año", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph(mt.periodo, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph("Cronograma", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph(numero(acCronograma,2) , fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph("Planillado", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph(numero(acPlanilla,2) , fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph("Días de retraso", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY,
                       align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph(numero(mt.dias,0), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//                    addCellTabla(tablaMultaDisp, new Paragraph(numero(mt.planilla.valor,2) , fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
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
                    tablaMultaDisp.setWidthPercentage(60);
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

                if(mt.tipoMulta.id == 4){  //retraso de obra
                    Paragraph tituloMultaNoPres = new Paragraph();
                    tituloMultaNoPres.setAlignment(Element.ALIGN_CENTER);
                    tituloMultaNoPres.add(new Paragraph(mt.tipoMulta.descripcion, fontTitle));
                    addEmptyLine(tituloMultaNoPres, 1);
                    document.add(tituloMultaNoPres);

                    PdfPTable tablaMultaDisp = new PdfPTable(2);
                    tablaMultaDisp.setWidthPercentage(60);
                    tablaMultaDisp.setSpacingAfter(10f);

                    tablaMultaDisp.setHorizontalAlignment(Element.ALIGN_LEFT)

                    addCellTabla(tablaMultaDisp, new Paragraph("Días", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph(numero(mt.dias,0), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph("Multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph(mt.descripcion , fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph("Valor de la multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaMultaDisp, new Paragraph('$' + numero(mt.monto, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

                    if(tipo == 'T') {
                        def mlCp = MultasPlanilla.findByPlanillaAndTipoMulta(planilla.planillaCmpl, TipoMulta.get(4))
                        addCellTabla(tablaMultaDisp, new Paragraph("Días", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaMultaDisp, new Paragraph(numero(mlCp.dias,0), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaMultaDisp, new Paragraph("Multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaMultaDisp, new Paragraph(mlCp.descripcion , fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaMultaDisp, new Paragraph("Valor de la multa", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaMultaDisp, new Paragraph('$' + numero(mlCp.monto, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

                        addCellTabla(tablaMultaDisp, new Paragraph("Valor total", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                        addCellTabla(tablaMultaDisp, new Paragraph('$' + numero(mt.monto + mlCp.monto, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    }

                    document.add(tablaMultaDisp);
                }
            }

            if(planilla.multaEspecial > 0) {
                Paragraph tituloMultaUsu = new Paragraph();
                tituloMultaUsu.setAlignment(Element.ALIGN_CENTER);
                tituloMultaUsu.add(new Paragraph("Otras multas", fontTitle));
                addEmptyLine(tituloMultaUsu, 1);
                document.add(tituloMultaUsu);

                PdfPTable tablaMultaUsu = new PdfPTable(2);
                tablaMultaUsu.setWidthPercentage(60);
                tablaMultaUsu.setSpacingAfter(10f);

                tablaMultaUsu.setHorizontalAlignment(Element.ALIGN_LEFT)

                addCellTabla(tablaMultaUsu, new Paragraph("Concepto", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaMultaUsu, new Paragraph(planilla.descripcionMulta, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaMultaUsu, new Paragraph("Valor", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                if(planilla.multaEspecial){
                    addCellTabla(tablaMultaUsu, new Paragraph('$'+numero(planilla.multaEspecial, 2),fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                } else {
                    addCellTabla(tablaMultaUsu, new Paragraph(''+numero(planilla.multaEspecial, 2),fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                }
                document.add(tablaMultaUsu);
            }

            document.add(firmas("otro", "vertical", planilla))
            document.close();
            pdfw.close()
            return baos
        } else {
            return null
        }



    }


    def detalleAdicional(planilla, tipoRprt) {
//        println "reporteTablas de la planilla ${planilla.id}"
        def obra = planilla.contrato.obra
        def cntr = planilla.contrato
        def cn = dbConnectionService.getConnection()
        def monto = planilla.contrato.monto
        if(tipoRprt in ['C', 'T']) {
            def cmpl = Contrato.findByPadre(planilla.contrato)
            if(tipoRprt == 'C') {
                monto = cmpl.monto
            } else {
                monto += cmpl.monto
            }
        }

        /* crea el PDF */
        def baos = new ByteArrayOutputStream()
        def multaPlanilla = MultasPlanilla.findAllByPlanilla(planilla)

        Font fontTitle = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Font fontThTiny = new Font(Font.TIMES_ROMAN, 7, Font.BOLD);
        Font fontThTiny2 = new Font(Font.TIMES_ROMAN, 4, Font.BOLD);
        Font fontThTiny3 = new Font(Font.TIMES_ROMAN, 7, Font.BOLD);
        Font fontTdTiny = new Font(Font.TIMES_ROMAN, 7, Font.NORMAL);
        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        Document document
//        document = new Document(PageSize.A4.rotate());
        document = new Document(PageSize.A4);
        document.setMargins(50,30,30,28)  // 28 equivale a 1 cm: izq, derecha, arriba y abajo
        def pdfw = PdfWriter.getInstance(document, baos);
        document.resetHeader()
        document.resetFooter()

        document.open();
        document.addTitle("Planillas de la obra " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.scaleToFit(52, 52)
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        PdfPTable tablaDetalles = null
        def borderWidth = 0.3

        def sql = "select count(distinct(sbpr__id)) cnta from detalle(${cntr.id}, ${obra.id}, ${planilla.id}, " +
                "'${tipoRprt}')"
        def sps = cn.rows(sql.toString())[0].cnta
        sql = "select * from detalle(${cntr.id}, ${obra.id}, ${planilla.id}, '${tipoRprt}')"
        def vocr = cn.rows(sql.toString())

        def existe = 0

        vocr.each {mk ->
            if(mk.cntdacml > mk.vocrcntd){
                existe ++
            }
        }

//        println("existe " + existe)

        def printHeaderDetalle = { params ->

            def tablaHeaderDetalles

            if(existe == 0){
                tablaHeaderDetalles = new PdfPTable(11);
                tablaHeaderDetalles.setWidthPercentage(100);
                tablaHeaderDetalles.setWidths(arregloEnteros([13, 35, 5, 10, 11, 10, 10, 10, 13, 13, 14]))
            }else{
                tablaHeaderDetalles = new PdfPTable(12);
                tablaHeaderDetalles.setWidthPercentage(100);
                tablaHeaderDetalles.setWidths(arregloEnteros([14, 31, 8, 8, 9, 16, 8, 8, 8, 8, 8, 8]))
            }

            addCellTabla(tablaHeaderDetalles, new Paragraph("Obra", fontThTiny), prmsTdNoBorder)
            addCellTabla(tablaHeaderDetalles, new Paragraph(obra.nombre, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 11])

            addCellTabla(tablaHeaderDetalles, new Paragraph("Lugar", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph((obra.lugar?.descripcion ?: ""), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Planilla", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph(planilla.numero, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 6])
            if(existe != 0){
                addCellTabla(tablaHeaderDetalles, new Paragraph(" ", fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            }


            addCellTabla(tablaHeaderDetalles, new Paragraph("Ubicación", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Parroquia " + obra.parroquia?.nombre + " Cantón " + obra.parroquia?.canton?.nombre, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Monto", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph(numero(monto, 2), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 5])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Pág. " + params.pag + " de " + params.total, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])

            addCellTabla(tablaHeaderDetalles, new Paragraph("Contratista", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph(planilla.contrato.oferta.proveedor.nombre, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Período", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph(ponePeriodoPlanilla(planilla), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 6])
            if(existe != 0){
                addCellTabla(tablaHeaderDetalles, new Paragraph(" ", fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            }


            addCellTabla(tablaDetalles, logo, [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, tablaHeaderDetalles, [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 11, pl: 40])
            addCellTabla(tablaDetalles, new Paragraph(" ", fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 12])

            addCellTabla(tablaDetalles, new Paragraph("N.", fontThTiny), [border: Color.BLACK, bwb: 0.1, bcb: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph("Descripción del rubro", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, bwr: borderWidth, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph("U.", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph("Precio unitario", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph("Vol. contrat.", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

            PdfPTable inner6 = new PdfPTable(3);
            addCellTabla(inner6, new Paragraph("Cantidades", fontThTiny), [border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
            addCellTabla(inner6, new Paragraph("Anterior", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(inner6, new Paragraph("Actual", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(inner6, new Paragraph("Acumul.", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, inner6, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])

            PdfPTable inner7 = new PdfPTable(3);
            addCellTabla(inner7, new Paragraph("Valores", fontThTiny), [border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
            addCellTabla(inner7, new Paragraph("Anterior", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(inner7, new Paragraph("Actual", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(inner7, new Paragraph("Acumul.", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, inner7, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])

            if(existe != 0){
                addCellTabla(tablaDetalles, new Paragraph("Cant. Adicio.", fontThTiny), [bwb: 0.1, bcb: Color.BLACK, border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            }
        }

        def totalAnterior = 0, totalActual = 0, totalAcumulado = 0, sp = null
        def height = 12
        def currentRows = 1
        def chequeoPg = 0
        def rowsCurPag = 1
        def maxRows = 0
        def extraRows = 0

        if(existe == 0){
            maxRows = 45     //45
            extraRows = 10   //18
            document.newPage()
            tablaDetalles = new PdfPTable(11);
            tablaDetalles.setWidthPercentage(100);
            tablaDetalles.setWidths(arregloEnteros([12, 35, 5, 11, 11, 11, 11, 11, 11, 11, 11]))
        }else{
            maxRows = 43     // en horizontal: 25
            extraRows = 18   //9
            document.newPage()
            tablaDetalles = new PdfPTable(12);
            tablaDetalles.setWidthPercentage(100);
//        tablaDetalles.setWidths(arregloEnteros([14, 40, 5, 9, 9, 9, 9, 9, 11, 11, 11, 8]))
            tablaDetalles.setWidths(arregloEnteros([14, 34, 5, 9, 9, 9, 9, 9, 14, 11, 14, 8]))
        }


        tablaDetalles.setSpacingAfter(1f);
        def currentPag = 1
        def sumaPrclAntr = 0, sumaTotlAntr = 0
        def sumaPrclActl = 0, sumaTotlActl = 0
        def sumaPrclAcml = 0, sumaTotlAcml = 0

        def frmtSbpr = [height: height, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY,
                        border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 12]
        def frmtDtIz = [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK,
                        align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def frmtDtDr = [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK,
                        align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def frmtDtDrBorde = [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, bwr: borderWidth,
                             border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def totalPags = Math.ceil((vocr.size() + sps + extraRows) / maxRows).toInteger()
        printHeaderDetalle([pag: currentPag, total: totalPags])

        Font fontThFooter = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        def frmtCol8 = [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8]
        def frmtCol11 = [border: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 12]
        def frmtSuma = [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def printFooterDetalle = { params ->


            def txt = "AVANCE DE OBRA PARCIAL"
            if (params.completo) {
                txt = "AVANCE DE OBRA"
            }

            addCellTabla(tablaDetalles, new Paragraph(txt, fontThFooter), frmtCol8)
            addCellTabla(tablaDetalles, new Paragraph(numero(params.ant, 2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(params.act, 2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(params.acu, 2), fontThFooter), frmtSuma)
            if(existe != 0){
                addCellTabla(tablaDetalles, new Paragraph('', fontThFooter), frmtCol8)
            }
        }


        sp = 0
        vocr.each {vo ->
            if (sp != vo.sbpr__id) {
                addCellTabla(tablaDetalles, new Paragraph('Subpresupuesto: ' + vo.sbprdscr, fontThTiny), frmtSbpr)

                sp = vo.sbpr__id
                currentRows++
                rowsCurPag++
            }

            addCellTabla(tablaDetalles, new Paragraph(vo.rbrocdgo, fontTdTiny), frmtDtIz)
            addCellTabla(tablaDetalles, new Paragraph(vo.rbronmbr, fontTdTiny), frmtDtIz)

            addCellTabla(tablaDetalles, new Paragraph(vo.unddcdgo, fontTdTiny), frmtDtDr)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vocrpcun, 2, "hide"), fontTdTiny), frmtDtDr)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vocrcntd, 2, "hide"), fontTdTiny), frmtDtDr)

            addCellTabla(tablaDetalles, new Paragraph(numero(vo.cntdantr, 2, "hide"), fontTdTiny), frmtDtDrBorde)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.cntdactl, 2, "hide"), fontTdTiny), frmtDtDrBorde)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.cntdacml, 2, "hide"), fontTdTiny), frmtDtDrBorde)

            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vlorantr, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vloractl, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vloracml, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

            if(existe != 0){
                if(vo.cntdacml > vo.vocrcntd){
                    addCellTabla(tablaDetalles, new Paragraph(numero(vo.cntdacml - vo.vocrcntd, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                }else{
                    addCellTabla(tablaDetalles, new Paragraph('', fontTdTiny), frmtDtIz)
                }
            }



            currentRows++
            rowsCurPag++
            sumaTotlAntr += vo.vlorantr
            sumaTotlActl += vo.vloractl
            sumaTotlAcml += vo.vloracml

            sumaPrclAntr += vo.vlorantr
            sumaPrclActl += vo.vloractl
            sumaPrclAcml += vo.vloracml

//            println "currentRows $currentRows, maxRows $maxRows"
            if(currentRows >= (maxRows) ) {
                printFooterDetalle([ant: sumaPrclAntr, act: sumaPrclActl, acu: sumaPrclAcml])

                sumaPrclAntr = 0
                sumaPrclActl = 0
                sumaPrclAcml = 0
                currentRows = 0

                document.add(tablaDetalles)

                document.add(firmas("detalle", "vertical", planilla))
//                printFirmas([tipo: "detalle", orientacion: "vertical"])
                currentPag++

                //-------------------
                document.newPage()

                if(existe == 0){
                    tablaDetalles = new PdfPTable(11);
                    tablaDetalles.setWidthPercentage(100);
                    tablaDetalles.setWidths(arregloEnteros([12, 35, 5, 11, 11, 11, 11, 11, 11, 11, 11]))
                }else{
                    tablaDetalles = new PdfPTable(12);
                    tablaDetalles.setWidthPercentage(100);
                    tablaDetalles.setWidths(arregloEnteros([14, 40, 5, 9, 9, 9, 9, 9, 11, 11, 11, 8]))
                }

                tablaDetalles.setSpacingAfter(1f);
                printHeaderDetalle([pag: currentPag, total: totalPags])
                rowsCurPag = 1
            }
        }

        printFooterDetalle([ant: sumaTotlAntr,  act: sumaTotlActl, acu: sumaTotlAcml, completo: true])

        def rjplAntr = planillasService.reajusteAnterior(planilla)
        def rjplAcml = planillasService.reajusteAcumulado(planilla)
        def rjplActl = rjplAcml - rjplAntr

        if(tipoRprt == 'T') {
            def rjplAntrCp = planillasService.reajusteAnterior(planilla.planillaCmpl)
            def rjplAcmlCp = planillasService.reajusteAcumulado(planilla.planillaCmpl)
            rjplAntr += rjplAntrCp
            rjplAcml += rjplAcmlCp
            rjplActl = rjplAcml - rjplAntr
        }

        addCellTabla(tablaDetalles, new Paragraph("REAJUSTE DE PRECIOS", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(rjplAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(rjplActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(rjplAcml, 2), fontThFooter), frmtSuma)
        if(existe != 0) {
            addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), frmtCol8)

        }

        addCellTabla(tablaDetalles, new Paragraph("SUBTOTAL", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr + rjplAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl + rjplActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml + rjplAcml, 2), fontThFooter), frmtSuma)
        if(existe != 0) {
            addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), frmtCol8)
        }


        sql = "select sum(plnlmnto) suma from plnl where cntr__id = ${planilla.contrato.id} and " +
                "tppl__id = 5 and plnlfcfn < '${planilla.fechaInicio.format('yyy-MM-yy')}'"

        def cstoAntr = cn.rows(sql.toString())[0].suma?:0
        sql = "select plnlmnto from plnl where plnl__id = (select plnl__id from plnl " +
                "where plnlpdcs = ${planilla.id})"
//        println "sql.....: $sql"
        def cstoActl = cn.rows(sql.toString())[0]?.plnlmnto?:0
        def cstoAcml = cstoAntr + cstoActl

        addCellTabla(tablaDetalles, new Paragraph("RUBROS NO CONTRACTUALES COSTO + PORCENTAJE", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(cstoAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(cstoActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(cstoAcml, 2), fontThFooter), frmtSuma)
        if(existe != 0) {
            addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), frmtCol8)
        }

        addCellTabla(tablaDetalles, new Paragraph("SUMATORIA DE AVANCE DE OBRA Y COSTO + PORCENTAJE", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr + cstoAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl + cstoActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml + cstoAcml, 2), fontThFooter), frmtSuma)
        if(existe != 0) {
            addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), frmtCol8)
        }

        sumaTotlAntr += rjplAntr + cstoAntr
        sumaTotlActl += rjplActl + cstoActl
        sumaTotlAcml += rjplAcml + cstoAcml

        addCellTabla(tablaDetalles, new Paragraph("TOTAL PLANILLA REAJUSTADA INCLUIDO COSTO + PORCENTAJE", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml, 2), fontThFooter), frmtSuma)
        if(existe != 0) {
            addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), frmtCol8)
        }

        addCellTabla(tablaDetalles, new Paragraph("DESCUENTOS CONTRACTUALES", fontThFooter), frmtCol11)


        sql = "select sum(plnldsct) suma from plnl where cntr__id = ${planilla.contrato.id} and " +
                "plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}' and plnltipo = '${planilla.tipoContrato}'"
//        println "sql.....: $sql"
        def antcAntr = cn.rows(sql.toString())[0].suma?:0
        def antcActl = planilla.descuentos
        def antcAcml = antcAntr + antcActl

        if(tipoRprt == 'T') {
            antcActl = planilla.descuentos + Planilla.get(planilla.planillaCmpl.id).descuentos
            antcAcml = antcAntr + antcActl
        }

        addCellTabla(tablaDetalles, new Paragraph("ANTICIPO", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(antcAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(antcActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(antcAcml, 2), fontThFooter), frmtSuma)
        if(existe != 0) {
            addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), frmtCol8)
        }


        sql = "select sum(mlplmnto) suma from mlpl where plnl__id in (select plnl__id from plnl where " +
                "cntr__id = ${planilla.contrato.id} and plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}' " +
                " and plnltipo = '${planilla.tipoContrato}')"
//        println "sql.....: $sql"
        def mltaAntr = cn.rows(sql.toString())[0].suma?:0

        sql = "select sum(plnlmles) suma from plnl where " +
                "cntr__id = ${planilla.contrato.id} and plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}' and " +
                "plnltipo = '${planilla.tipoContrato}'"
//        println "sql.....: $sql"
        mltaAntr += cn.rows(sql.toString())[0].suma?:0

        sql = "select sum(mlplmnto) suma from mlpl where plnl__id = ${planilla.id}"
        println "sql...multa..: $sql"
        def mltaActl = cn.rows(sql.toString())[0].suma?:0

        sql = "select sum(plnlmles) suma from plnl where plnl__id = ${planilla.id}"
//        println "sql.....: $sql"
        mltaActl += cn.rows(sql.toString())[0].suma?:0

        def mltaAcml = mltaAntr + mltaActl


        addCellTabla(tablaDetalles, new Paragraph("MULTAS", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(mltaAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(mltaActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(mltaAcml, 2), fontThFooter), frmtSuma)
        if(existe != 0) {
            addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), frmtCol8)
        }

        sql = "select sum(plnlnpvl) suma from plnl where cntr__id = ${planilla.contrato.id} and " +
                "plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}'"
//        println "sql.....: $sql"
        def nopgAntr = cn.rows(sql.toString())[0].suma?:0
        def nopgActl = planilla.noPagoValor
        def nopgAcml = nopgAntr + nopgActl

        sumaTotlAntr -= mltaAntr + antcAntr + nopgAntr
        sumaTotlActl -= mltaActl + antcActl + nopgActl
        sumaTotlAcml -= mltaAcml + antcAcml + nopgAcml

//        println "nopgAntr $nopgAntr, nopgActl $nopgActl, nopgAcml $nopgAcml"
        if((nopgAntr + nopgActl + nopgAcml) > 0) {
            addCellTabla(tablaDetalles, new Paragraph(planilla.noPago?:" ", fontThFooter), frmtCol8)
            addCellTabla(tablaDetalles, new Paragraph(numero(nopgAntr,2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(nopgActl, 2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(nopgAcml, 2), fontThFooter), frmtSuma)
            if(existe != 0) {
                addCellTabla(tablaDetalles, new Paragraph(" ", fontThFooter), frmtCol8)
            }
        }

        addCellTabla(tablaDetalles, new Paragraph("VALOR LIQUIDO A PAGAR", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml, 2), fontThFooter), frmtSuma)
        if(existe != 0) {
            addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), frmtCol8)
        }

        document.add(tablaDetalles)

        document.add(firmas("detalle", "vertical", planilla))

        document.close();
        pdfw.close()
        return baos
    }

    def detalleTodo(planilla, tipoRprt) {
        println "detalle de la planilla ${planilla.id}, tipo: $tipoRprt"
        def obra = planilla.contrato.obra
        def cntr = planilla.contrato
        def cn = dbConnectionService.getConnection()
        def monto = planilla.contrato.monto
        if(tipoRprt in ['C', 'T']) {
            def cmpl = Contrato.findByPadre(planilla.contrato)
            if(tipoRprt == 'C') {
                monto = cmpl.monto
            } else {
                monto += cmpl.monto
            }
        }

        /* crea el PDF */
        def baos = new ByteArrayOutputStream()
        def multaPlanilla = MultasPlanilla.findAllByPlanilla(planilla)

        Font fontTitle = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Font fontThTiny = new Font(Font.TIMES_ROMAN, 7, Font.BOLD);
        Font fontTdTiny = new Font(Font.TIMES_ROMAN, 7, Font.NORMAL);
        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        Document document
        document = new Document();
        document.setMargins(50,30,30,28)  // 28 equivale a 1 cm: izq, derecha, arriba y abajo
        def pdfw = PdfWriter.getInstance(document, baos);
        document.resetHeader()
        document.resetFooter()

//        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
//        pdfw.setPageEvent(event);

//        graficarFooter(pdfw,planilla)

        document.open();
        document.addTitle("Planillas de la obra " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.scaleToFit(52,52)
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        PdfPTable tablaDetalles = null
        def borderWidth = 1

        def printHeaderDetalle = { params ->
            def tablaHeaderDetalles = new PdfPTable(11);
            tablaHeaderDetalles.setWidthPercentage(100);
            tablaHeaderDetalles.setWidths(arregloEnteros([13, 35, 5, 10, 11, 10, 10, 10, 13, 13, 14]))

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
//            addCellTabla(tablaHeaderDetalles, new Paragraph(numero(planilla.contrato.monto, 2), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
            addCellTabla(tablaHeaderDetalles, new Paragraph(numero(monto, 2), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Pág. " + params.pag + " de " + params.total, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaHeaderDetalles, new Paragraph("Contratista", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph(planilla.contrato.oferta.proveedor.nombre, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Período", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph(ponePeriodoPlanilla(planilla), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 5])
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

        def totalAnterior = 0, totalActual = 0, totalAcumulado = 0, sp = null
        def height = 12
//        def maxRows = 45     //45
        def maxRows = 58     //45
        def extraRows = 10   //18
        def currentRows = 1
        def chequeoPg = 0

        def rowsCurPag = 1
        document.newPage()
        tablaDetalles = new PdfPTable(11);
        tablaDetalles.setWidthPercentage(100);
        tablaDetalles.setWidths(arregloEnteros([12, 35, 5, 11, 11, 11, 11, 11, 11, 11, 11]))
        tablaDetalles.setSpacingAfter(1f);
        tablaDetalles.setSplitLate(false);
        def currentPag = 1
        def sumaPrclAntr = 0, sumaTotlAntr = 0
        def sumaPrclActl = 0, sumaTotlActl = 0
        def sumaPrclAcml = 0, sumaTotlAcml = 0



        def frmtSbpr = [height: height, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY,
                        border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 11]
        def frmtDtIz = [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK,
                        align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def frmtDtIz2 = [bwt: 0.1, bct: Color.BLACK, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK,
                        align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def frmtDtDr = [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK,
                        align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def frmtDtDr2 = [bwt: 0.1, bct: Color.BLACK, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK,
                        align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def frmtDtDrBorde = [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, bwr: borderWidth,
                             border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def frmtDtDrBorde2 = [bwt: 0.1, bct: Color.BLACK, bwb: 0.1, bcb: Color.WHITE, height: height, bwr: borderWidth,
                             border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]


        def sql = "select count(distinct(sbpr__id)) cnta from detalle(${cntr.id}, ${obra.id}, ${planilla.id}, " +
                "'${tipoRprt}')"

        def sps = cn.rows(sql.toString())[0].cnta
        sql = "select * from detalle(${cntr.id}, ${obra.id}, ${planilla.id}, '${tipoRprt}')"

        println "+++sql: $sql"
        def vocr = cn.rows(sql.toString())

        println "registros: ${vocr.size()}, ln: ${vocr.vocrlnea.sum()} sps: $sps, extra: $extraRows --> num: ${(vocr.size() + sps + extraRows)} / $maxRows "
//        def totalPags = Math.ceil((vocr.size() + sps + extraRows) / maxRows).toInteger()
        def totalPags = Math.ceil((vocr.vocrlnea.sum() + sps + extraRows) / maxRows).toInteger()
        printHeaderDetalle([pag: currentPag, total: totalPags])


        Font fontThFooter = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        def frmtCol8 = [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8]
        def frmtCol11 = [border: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 11]
        def frmtSuma = [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def printFooterDetalle = { params ->


            def txt = "AVANCE DE OBRA PARCIAL"
            if (params.completo) {
                txt = "AVANCE DE OBRA"
            }

            addCellTabla(tablaDetalles, new Paragraph(txt, fontThFooter), frmtCol8)
            addCellTabla(tablaDetalles, new Paragraph(numero(params.ant, 2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(params.act, 2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(params.acu, 2), fontThFooter), frmtSuma)
        }


        sp = 0
        println("---- " + vocr.size())
        vocr.each {vo ->
            if (sp != vo.sbpr__id) {
                addCellTabla(tablaDetalles, new Paragraph('Subpresupuesto: ' + vo.sbprdscr, fontThTiny), frmtSbpr)

                sp = vo.sbpr__id
//                currentRows++
                currentRows += vo.vocrlnea
                rowsCurPag++
            }

            addCellTabla(tablaDetalles, new Paragraph(vo.rbrocdgo, fontTdTiny), frmtDtIz2)
            addCellTablaWrap(tablaDetalles, new Paragraph(vo.rbronmbr, fontTdTiny), frmtDtIz2)

            addCellTabla(tablaDetalles, new Paragraph(vo.unddcdgo, fontTdTiny), frmtDtDr2)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vocrpcun, 2, "hide"), fontTdTiny), frmtDtDr2)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vocrcntd, 2, "hide"), fontTdTiny), frmtDtDr2)

            addCellTabla(tablaDetalles, new Paragraph(numero(vo.cntdantr, 2, "hide"), fontTdTiny), frmtDtDrBorde2)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.cntdactl, 2, "hide"), fontTdTiny), frmtDtDrBorde2)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.cntdacml, 2, "hide"), fontTdTiny), frmtDtDrBorde2)

            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vlorantr, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.BLACK, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vloractl, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.BLACK, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vloracml, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.BLACK, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

//            currentRows++
            currentRows += vo.vocrlnea
            rowsCurPag++
            sumaTotlAntr += vo.vlorantr
            sumaTotlActl += vo.vloractl
            sumaTotlAcml += vo.vloracml

            sumaPrclAntr += vo.vlorantr
            sumaPrclActl += vo.vloractl
            sumaPrclAcml += vo.vloracml

//            println "currentRows $currentRows, maxRows $maxRows"
            if(currentRows >= (maxRows) ) {
                printFooterDetalle([ant: sumaPrclAntr, act: sumaPrclActl, acu: sumaPrclAcml])

                sumaPrclAntr = 0
                sumaPrclActl = 0
                sumaPrclAcml = 0
                currentRows = 0

                document.add(tablaDetalles)

                document.add(firmas("detalle", "vertical", planilla))
//                printFirmas([tipo: "detalle", orientacion: "vertical"])
                currentPag++

                //-------------------
                document.newPage()
                tablaDetalles = new PdfPTable(11);
                tablaDetalles.setWidthPercentage(100);
                tablaDetalles.setWidths(arregloEnteros([12, 35, 5, 11, 11, 11, 11, 11, 11, 11, 11]))
                tablaDetalles.setSpacingAfter(1f);
                printHeaderDetalle([pag: currentPag, total: totalPags])
                rowsCurPag = 1
            }
        }

        printFooterDetalle([ant: sumaTotlAntr,  act: sumaTotlActl, acu: sumaTotlAcml, completo: true])


        def rjplAntr = planillasService.reajusteAnterior(planilla)
        def rjplAcml = planillasService.reajusteAcumulado(planilla)
        def rjplActl = rjplAcml - rjplAntr

        if(tipoRprt == 'T') {
            def rjplAntrCp = planillasService.reajusteAnterior(planilla.planillaCmpl)
            def rjplAcmlCp = planillasService.reajusteAcumulado(planilla.planillaCmpl)
            rjplAntr += rjplAntrCp
            rjplAcml += rjplAcmlCp
            rjplActl = rjplAcml - rjplAntr
        }

        addCellTabla(tablaDetalles, new Paragraph("REAJUSTE DE PRECIOS", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(rjplAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(rjplActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(rjplAcml, 2), fontThFooter), frmtSuma)

        addCellTabla(tablaDetalles, new Paragraph("SUBTOTAL", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr + rjplAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl + rjplActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml + rjplAcml, 2), fontThFooter), frmtSuma)

        sql = "select sum(plnlmnto) suma from plnl where cntr__id = ${planilla.contrato.id} and " +
                "tppl__id = 5 and plnlfcfn < '${planilla.fechaInicio.format('yyy-MM-yy')}'"

        def cstoAntr = cn.rows(sql.toString())[0].suma?:0
        sql = "select plnlmnto from plnl where plnl__id = (select plnl__id from plnl " +
                "where plnlpdcs = ${planilla.id})"
//        println "sql.....: $sql"
        def cstoActl = cn.rows(sql.toString())[0]?.plnlmnto?:0
        def cstoAcml = cstoAntr + cstoActl

        addCellTabla(tablaDetalles, new Paragraph("RUBROS NO CONTRACTUALES COSTO + PORCENTAJE", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(cstoAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(cstoActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(cstoAcml, 2), fontThFooter), frmtSuma)

        addCellTabla(tablaDetalles, new Paragraph("SUMATORIA DE AVANCE DE OBRA Y COSTO + PORCENTAJE", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr + cstoAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl + cstoActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml + cstoAcml, 2), fontThFooter), frmtSuma)

        sumaTotlAntr += rjplAntr + cstoAntr
        sumaTotlActl += rjplActl + cstoActl
        sumaTotlAcml += rjplAcml + cstoAcml

        addCellTabla(tablaDetalles, new Paragraph("TOTAL PLANILLA REAJUSTADA INCLUIDO COSTO + PORCENTAJE", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml, 2), fontThFooter), frmtSuma)

        addCellTabla(tablaDetalles, new Paragraph("DESCUENTOS CONTRACTUALES", fontThFooter), frmtCol11)

        def tipoplnl = "'${planilla.tipoContrato}'"

        if(tipoRprt =='T'){
            tipoplnl += ",'C'"
        }

        def cmpl = Contrato.findByPadre(planilla.contrato)
        if(tipoRprt == 'C') {
            sql = "select sum(plnldsct) suma from plnl where cntr__id = ${cmpl.id} and " +
                    "plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}' and plnltipo in (${tipoplnl})"

        } else {
            sql = "select sum(plnldsct) suma from plnl where cntr__id = ${planilla.contrato.id} and " +
//                "plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}' and plnltipo = '${planilla.tipoContrato}'"
                    "plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}' and plnltipo in (${tipoplnl})"
        }
//        println "sql.....: $sql"
        def antcAntr = cn.rows(sql.toString())[0].suma?:0
        def antcActl = planilla.descuentos
        def antcAcml = antcAntr + antcActl

        if(tipoRprt == 'T') {
            antcActl = planilla.descuentos + Planilla.get(planilla.planillaCmpl.id).descuentos
            antcAcml = antcAntr + antcActl
        }

        addCellTabla(tablaDetalles, new Paragraph("ANTICIPO", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(antcAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(antcActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(antcAcml, 2), fontThFooter), frmtSuma)

        def plnlIn = ""
        if(tipoRprt == 'T'){
            plnlIn = "${planilla.id}, ${planilla.planillaCmpl?.id?:0}"
        } else {
            plnlIn = "${planilla.id}"
        }

        /* Todo: incluir planillas de complementario  ---- */
        sql = "select sum(mlplmnto) suma from mlpl where plnl__id in (select plnl__id from plnl where " +
                "cntr__id = ${planilla.contrato.id} and plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}' " +
                " and plnltipo = '${planilla.tipoContrato}')"
//        println "sql.....: $sql"
        def mltaAntr = cn.rows(sql.toString())[0].suma?:0

        sql = "select sum(plnlmles) suma from plnl where " +
                "cntr__id = ${planilla.contrato.id} and plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}' and " +
                "plnltipo = '${planilla.tipoContrato}'"
//        println "sql.....: $sql"
        mltaAntr += cn.rows(sql.toString())[0].suma?:0


        sql = "select sum(mlplmnto) suma from mlpl where plnl__id in (${plnlIn})"
//        println "sql.....: $sql"
        def mltaActl = cn.rows(sql.toString())[0].suma?:0

        sql = "select sum(plnlmles) suma from plnl where plnl__id in (${plnlIn})"
        println "sql. multas....: $sql"
        mltaActl += cn.rows(sql.toString())[0].suma?:0

        def mltaAcml = mltaAntr + mltaActl


        addCellTabla(tablaDetalles, new Paragraph("MULTAS", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(mltaAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(mltaActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(mltaAcml, 2), fontThFooter), frmtSuma)

        sql = "select sum(plnlnpvl) suma from plnl where cntr__id = ${planilla.contrato.id} and " +
                "plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}'"
//        println "sql.....: $sql"
        def nopgAntr = cn.rows(sql.toString())[0].suma?:0
        def nopgActl = planilla.noPagoValor
        def nopgAcml = nopgAntr + nopgActl

        sumaTotlAntr -= mltaAntr + antcAntr + nopgAntr
        sumaTotlActl -= mltaActl + antcActl + nopgActl
        sumaTotlAcml -= mltaAcml + antcAcml + nopgAcml

//        println "nopgAntr $nopgAntr, nopgActl $nopgActl, nopgAcml $nopgAcml"
        if((nopgAntr + nopgActl + nopgAcml) > 0) {
            addCellTabla(tablaDetalles, new Paragraph(planilla.noPago?:" ", fontThFooter), frmtCol8)
            addCellTabla(tablaDetalles, new Paragraph(numero(nopgAntr,2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(nopgActl, 2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(nopgAcml, 2), fontThFooter), frmtSuma)
        }

        addCellTabla(tablaDetalles, new Paragraph("VALOR LIQUIDO A PAGAR", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml, 2), fontThFooter), frmtSuma)

        document.add(tablaDetalles)

        document.add(firmas("detalle", "vertical", planilla))

//        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
//        pdfw.setPageEvent(event);

        document.close();
        pdfw.close()
        return baos
    }


    def detalle(planilla, tipoRprt) {
        println "detalle de la planilla ${planilla.id}, tipo: $tipoRprt"
        def obra = planilla.contrato.obra
        def cntr = planilla.contrato
        def cn = dbConnectionService.getConnection()
        def monto = planilla.contrato.monto
        if(tipoRprt in ['C', 'T']) {
            def cmpl = Contrato.findByPadre(planilla.contrato)
            if(tipoRprt == 'C') {
                monto = cmpl.monto
            } else {
                monto += cmpl.monto
            }
        }

        /* crea el PDF */
        def baos = new ByteArrayOutputStream()
        def multaPlanilla = MultasPlanilla.findAllByPlanilla(planilla)

        Font fontTitle = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Font fontThTiny = new Font(Font.TIMES_ROMAN, 7, Font.BOLD);
        Font fontTdTiny = new Font(Font.TIMES_ROMAN, 7, Font.NORMAL);
        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        Document document
        document = new Document();
        document.setMargins(50,30,30,28)  // 28 equivale a 1 cm: izq, derecha, arriba y abajo
        def pdfw = PdfWriter.getInstance(document, baos);
        document.resetHeader()
        document.resetFooter()

//        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
//        pdfw.setPageEvent(event);

//        graficarFooter(pdfw,planilla)

        document.open();
        document.addTitle("Planillas de la obra " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.scaleToFit(52,52)
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        PdfPTable tablaDetalles = null
        def borderWidth = 1

        def printHeaderDetalle = { params ->
            def tablaHeaderDetalles = new PdfPTable(11);
            tablaHeaderDetalles.setWidthPercentage(100);
            tablaHeaderDetalles.setWidths(arregloEnteros([13, 35, 5, 10, 11, 10, 10, 10, 13, 13, 14]))

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
//            addCellTabla(tablaHeaderDetalles, new Paragraph(numero(planilla.contrato.monto, 2), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
            addCellTabla(tablaHeaderDetalles, new Paragraph(numero(monto, 2), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Pág. " + params.pag + " de " + params.total, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaHeaderDetalles, new Paragraph("Contratista", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph(planilla.contrato.oferta.proveedor.nombre, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Período", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph(ponePeriodoPlanilla(planilla), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 5])
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

        def totalAnterior = 0, totalActual = 0, totalAcumulado = 0, sp = null
        def height = 12
        def maxRows = 35     //45
        def extraRows = 6   //18
        def currentRows = 1
        def chequeoPg = 0

        def rowsCurPag = 1
        document.newPage()
        tablaDetalles = new PdfPTable(11);
        tablaDetalles.setWidthPercentage(100);
        tablaDetalles.setWidths(arregloEnteros([10, 40, 5, 8, 11, 11, 11, 11, 11, 11, 11]))
//        tablaDetalles.setSpacingAfter(1f);
        tablaDetalles.setSplitLate(false);
        def currentPag = 1
        def sumaPrclAntr = 0, sumaTotlAntr = 0
        def sumaPrclActl = 0, sumaTotlActl = 0
        def sumaPrclAcml = 0, sumaTotlAcml = 0



        def frmtSbpr = [height: height, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY,
                        border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 11]
        def frmtDtIz = [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK,
                        align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def frmtDtIz2 = [bwt: 0.1, bct: Color.BLACK, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK,
                         align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def frmtDtDr = [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK,
                        align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def frmtDtDrBorde = [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, bwr: borderWidth,
                             border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]


        def sql = "select count(distinct(sbpr__id)) cnta from detalle(${cntr.id}, ${obra.id}, ${planilla.id}, " +
                "'${tipoRprt}')"

        def sps = cn.rows(sql.toString())[0].cnta
        sql = "select * from detalle(${cntr.id}, ${obra.id}, ${planilla.id}, '${tipoRprt}')"
//        println "sql: $sql"
        def vocr = cn.rows(sql.toString())

        println "registros: ${vocr.size()}, sps: $sps, extra: $extraRows --> num: ${(vocr.size() + sps + extraRows)} / $maxRows "
        def totalPags = Math.ceil((vocr.size() + sps + extraRows) / maxRows).toInteger()
        printHeaderDetalle([pag: currentPag, total: totalPags])

        Font fontThFooter = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        def frmtCol8 = [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8]
        def frmtCol11 = [border: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 11]
        def frmtSuma = [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def printFooterDetalle = { params ->


            def txt = "AVANCE DE OBRA PARCIAL"
            if (params.completo) {
                txt = "AVANCE DE OBRA"
            }

            addCellTabla(tablaDetalles, new Paragraph(txt, fontThFooter), frmtCol8)
            addCellTabla(tablaDetalles, new Paragraph(numero(params.ant, 2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(params.act, 2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(params.acu, 2), fontThFooter), frmtSuma)
        }


        sp = 0
        vocr.each {vo ->
            if (sp != vo.sbpr__id) {
                addCellTabla(tablaDetalles, new Paragraph('Subpresupuesto: ' + vo.sbprdscr, fontThTiny), frmtSbpr)

                sp = vo.sbpr__id
                currentRows++
                rowsCurPag++
            }

            addCellTabla(tablaDetalles, new Paragraph(vo.rbrocdgo, fontTdTiny), frmtDtIz)
            addCellTablaWrap(tablaDetalles, new Paragraph(vo.rbronmbr, fontTdTiny), frmtDtIz2)

            addCellTabla(tablaDetalles, new Paragraph(vo.unddcdgo, fontTdTiny), frmtDtDr)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vocrpcun, 2, "hide"), fontTdTiny), frmtDtDr)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vocrcntd, 2, "hide"), fontTdTiny), frmtDtDr)

            addCellTabla(tablaDetalles, new Paragraph(numero(vo.cntdantr, 2, "hide"), fontTdTiny), frmtDtDrBorde)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.cntdactl, 2, "hide"), fontTdTiny), frmtDtDrBorde)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.cntdacml, 2, "hide"), fontTdTiny), frmtDtDrBorde)

            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vlorantr, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vloractl, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vloracml, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

            currentRows++
            rowsCurPag++
            sumaTotlAntr += vo.vlorantr
            sumaTotlActl += vo.vloractl
            sumaTotlAcml += vo.vloracml

            sumaPrclAntr += vo.vlorantr
            sumaPrclActl += vo.vloractl
            sumaPrclAcml += vo.vloracml

//            println "currentRows $currentRows, maxRows $maxRows"
            if(currentRows >= (maxRows) ) {
                printFooterDetalle([ant: sumaPrclAntr, act: sumaPrclActl, acu: sumaPrclAcml])

                sumaPrclAntr = 0
                sumaPrclActl = 0
                sumaPrclAcml = 0
                currentRows = 0

                document.add(tablaDetalles)

//                document.add(firmas("detalle", "vertical", planilla))
//                printFirmas([tipo: "detalle", orientacion: "vertical"])
                currentPag++

                //-------------------
                document.newPage()
                tablaDetalles = new PdfPTable(11);
                tablaDetalles.setWidthPercentage(100);
                tablaDetalles.setWidths(arregloEnteros([10, 40, 5, 8, 11, 11, 11, 11, 11, 11, 11]))
                tablaDetalles.setSpacingAfter(1f);
                printHeaderDetalle([pag: currentPag, total: totalPags])
                rowsCurPag = 1
            }
        }


        printFooterDetalle([ant: sumaTotlAntr,  act: sumaTotlActl, acu: sumaTotlAcml, completo: true])

        def rjplAntr = planillasService.reajusteAnterior(planilla)
        def rjplAcml = planillasService.reajusteAcumulado(planilla)
        def rjplActl = rjplAcml - rjplAntr

        if(tipoRprt == 'T') {
            def rjplAntrCp = planillasService.reajusteAnterior(planilla.planillaCmpl)
            def rjplAcmlCp = planillasService.reajusteAcumulado(planilla.planillaCmpl)
            rjplAntr += rjplAntrCp
            rjplAcml += rjplAcmlCp
            rjplActl = rjplAcml - rjplAntr
        }

        addCellTabla(tablaDetalles, new Paragraph("REAJUSTE DE PRECIOS", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(rjplAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(rjplActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(rjplAcml, 2), fontThFooter), frmtSuma)

        addCellTabla(tablaDetalles, new Paragraph("SUBTOTAL", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr + rjplAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl + rjplActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml + rjplAcml, 2), fontThFooter), frmtSuma)

        sql = "select sum(plnlmnto) suma from plnl where cntr__id = ${planilla.contrato.id} and " +
                "tppl__id = 5 and plnlfcfn < '${planilla.fechaInicio.format('yyy-MM-yy')}'"

        def cstoAntr = cn.rows(sql.toString())[0].suma?:0
        sql = "select plnlmnto from plnl where plnl__id = (select plnl__id from plnl " +
                "where plnlpdcs = ${planilla.id})"
//        println "sql.....: $sql"
        def cstoActl = cn.rows(sql.toString())[0]?.plnlmnto?:0
        def cstoAcml = cstoAntr + cstoActl

        addCellTabla(tablaDetalles, new Paragraph("RUBROS NO CONTRACTUALES COSTO + PORCENTAJE", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(cstoAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(cstoActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(cstoAcml, 2), fontThFooter), frmtSuma)

        addCellTabla(tablaDetalles, new Paragraph("SUMATORIA DE AVANCE DE OBRA Y COSTO + PORCENTAJE", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr + cstoAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl + cstoActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml + cstoAcml, 2), fontThFooter), frmtSuma)

        sumaTotlAntr += rjplAntr + cstoAntr
        sumaTotlActl += rjplActl + cstoActl
        sumaTotlAcml += rjplAcml + cstoAcml

        addCellTabla(tablaDetalles, new Paragraph("TOTAL PLANILLA REAJUSTADA INCLUIDO COSTO + PORCENTAJE", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml, 2), fontThFooter), frmtSuma)

        addCellTabla(tablaDetalles, new Paragraph("DESCUENTOS CONTRACTUALES", fontThFooter), frmtCol11)

        def tipoplnl = "'${planilla.tipoContrato}'"

        if(tipoRprt =='T'){
            tipoplnl += ",'C'"
        }

        sql = "select sum(plnldsct) suma from plnl where cntr__id = ${planilla.contrato.id} and " +
//                "plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}' and plnltipo = '${planilla.tipoContrato}'"
                "plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}' and plnltipo in (${tipoplnl})"
//        println "sql.....: $sql"
        def antcAntr = cn.rows(sql.toString())[0].suma?:0
        def antcActl = planilla.descuentos
        def antcAcml = antcAntr + antcActl

        if(tipoRprt == 'T') {
            antcActl = planilla.descuentos + Planilla.get(planilla.planillaCmpl.id).descuentos
            antcAcml = antcAntr + antcActl
        }

        addCellTabla(tablaDetalles, new Paragraph("ANTICIPO", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(antcAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(antcActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(antcAcml, 2), fontThFooter), frmtSuma)

        def plnlIn = ""
        if(tipoRprt == 'T'){
            plnlIn = "${planilla.id}, ${planilla.planillaCmpl?.id?:0}"
        } else {
            plnlIn = "${planilla.id}"
        }

        /* Todo: incluir planillas de complementario  ---- */
        sql = "select sum(mlplmnto) suma from mlpl where plnl__id in (select plnl__id from plnl where " +
                "cntr__id = ${planilla.contrato.id} and plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}' " +
                " and plnltipo = '${planilla.tipoContrato}')"
//        println "sql.....: $sql"
        def mltaAntr = cn.rows(sql.toString())[0].suma?:0

        sql = "select sum(plnlmles) suma from plnl where " +
                "cntr__id = ${planilla.contrato.id} and plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}' and " +
                "plnltipo = '${planilla.tipoContrato}'"
//        println "sql.....: $sql"
        mltaAntr += cn.rows(sql.toString())[0].suma?:0


        sql = "select sum(mlplmnto) suma from mlpl where plnl__id in (${plnlIn})"
//        println "sql.....: $sql"
        def mltaActl = cn.rows(sql.toString())[0].suma?:0

        sql = "select sum(plnlmles) suma from plnl where plnl__id in (${plnlIn})"
        println "sql. multas....: $sql"
        mltaActl += cn.rows(sql.toString())[0].suma?:0

        def mltaAcml = mltaAntr + mltaActl


        addCellTabla(tablaDetalles, new Paragraph("MULTAS", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(mltaAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(mltaActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(mltaAcml, 2), fontThFooter), frmtSuma)

        sql = "select sum(plnlnpvl) suma from plnl where cntr__id = ${planilla.contrato.id} and " +
                "plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}'"
//        println "sql.....: $sql"
        def nopgAntr = cn.rows(sql.toString())[0].suma?:0
        def nopgActl = planilla.noPagoValor
        def nopgAcml = nopgAntr + nopgActl

        sumaTotlAntr -= mltaAntr + antcAntr + nopgAntr
        sumaTotlActl -= mltaActl + antcActl + nopgActl
        sumaTotlAcml -= mltaAcml + antcAcml + nopgAcml

//        println "nopgAntr $nopgAntr, nopgActl $nopgActl, nopgAcml $nopgAcml"
        if((nopgAntr + nopgActl + nopgAcml) > 0) {
            addCellTabla(tablaDetalles, new Paragraph(planilla.noPago?:" ", fontThFooter), frmtCol8)
            addCellTabla(tablaDetalles, new Paragraph(numero(nopgAntr,2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(nopgActl, 2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(nopgAcml, 2), fontThFooter), frmtSuma)
        }

        addCellTabla(tablaDetalles, new Paragraph("VALOR LIQUIDO A PAGAR", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml, 2), fontThFooter), frmtSuma)

        document.add(tablaDetalles)

        document.add(firmas("detalle", "vertical", planilla))  //gdo mar-2020

//        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
//        pdfw.setPageEvent(event);

        document.close();
        pdfw.close()
        return baos
    }


    def graficarFooter(pdfw, planilla){
        HeaderFooterPageEvent event = new HeaderFooterPageEvent(planilla);
        pdfw.setPageEvent(event);
    }


    public class HeaderFooterPageEvent extends PdfPageEventHelper {

        Planilla planilla
//        public void onStartPage(PdfWriter writer, Document document) {
//            document.add(firmas("detalle", "vertical", planilla))

//            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Left"), 30, 800, 0);
//            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Right"), 550, 800, 0);
//            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Página " + document.getPageNumber()), 550, 10, 0);
//        }

        public HeaderFooterPageEvent(planilla) {
            this.planilla = planilla
        }

        public void onEndPage(PdfWriter writer, Document document) {
            document.add(firmas("detalle", "vertical", planilla))
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Página " + document.getPageNumber()), 550, 10, 0);
        }

    }



    def detalleEntrega(planilla, tipoRprt) {
//        println "detalleEntrega ${planilla.id}"
        def obra = planilla.contrato.obra
        def cntr = planilla.contrato
        def cn = dbConnectionService.getConnection()
        def monto = planilla.contrato.monto
        if(tipoRprt in ['C', 'T']) {
            def cmpl = Contrato.findByPadre(planilla.contrato)
            if(tipoRprt == 'C') {
                monto = cmpl.monto
            } else {
                monto += cmpl.monto
            }
        }

        /* crea el PDF */
        def baos = new ByteArrayOutputStream()
        def multaPlanilla = MultasPlanilla.findAllByPlanilla(planilla)

        Font fontTitle = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Font fontThTiny = new Font(Font.TIMES_ROMAN, 7, Font.BOLD);
        Font fontTdTiny = new Font(Font.TIMES_ROMAN, 7, Font.NORMAL);
        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        Document document
        document = new Document();
        document.setMargins(50,30,30,28)  // 28 equivale a 1 cm: izq, derecha, arriba y abajo
        def pdfw = PdfWriter.getInstance(document, baos);
        document.resetHeader()
        document.resetFooter()

        document.open();
        document.addTitle("Planillas de la obra " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        PdfPTable tablaDetalles = null
        def borderWidth = 1

//        println ".........1"
        def printHeaderDetalle = { params ->
            def tablaHeaderDetalles = new PdfPTable(11);
            tablaHeaderDetalles.setWidthPercentage(100);
            tablaHeaderDetalles.setWidths(arregloEnteros([13, 35, 5, 10, 11, 10, 10, 10, 13, 13, 14]))

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
//            addCellTabla(tablaHeaderDetalles, new Paragraph(numero(planilla.contrato.monto, 2), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
            addCellTabla(tablaHeaderDetalles, new Paragraph(numero(monto, 2), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Pág. " + params.pag + " de " + params.total, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaHeaderDetalles, new Paragraph("Contratista", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph(planilla.contrato.oferta.proveedor.nombre, fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
            addCellTabla(tablaHeaderDetalles, new Paragraph("Período", fontThTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaHeaderDetalles, new Paragraph(ponePeriodoPlanilla(planilla), fontTdTiny), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 5])
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

        def totalAnterior = 0, totalActual = 0, totalAcumulado = 0, sp = null
        def height = 12
        def maxRows = 43     //45
        def extraRows = 18   //9
        def currentRows = 1
        def chequeoPg = 0

        def rowsCurPag = 1
        document.newPage()
        tablaDetalles = new PdfPTable(11);
        tablaDetalles.setWidthPercentage(100);
        tablaDetalles.setWidths(arregloEnteros([12, 35, 5, 11, 11, 11, 11, 11, 11, 11, 11]))
        tablaDetalles.setSpacingAfter(1f);
        def currentPag = 1
        def sumaPrclAntr = 0, sumaTotlAntr = 0
        def sumaPrclActl = 0, sumaTotlActl = 0
        def sumaPrclAcml = 0, sumaTotlAcml = 0



        def frmtSbpr = [height: height, bwr: borderWidth, bwt: 0.1, bct: Color.LIGHT_GRAY, bwb: 0.1, bcb: Color.LIGHT_GRAY,
                        border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 11]
        def frmtDtIz = [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK,
                        align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def frmtDtDr = [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK,
                        align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def frmtDtDrBorde = [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, bwr: borderWidth,
                             border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]


        def sql = "select count(distinct(sbpr__id)) cnta from detalle(${cntr.id}, ${obra.id}, ${planilla.id}, " +
                "'${tipoRprt}')"

        def sps = cn.rows(sql.toString())[0].cnta
        sql = "select * from detalle(${cntr.id}, ${obra.id}, ${planilla.id}, '${tipoRprt}')"
//        println "sql: $sql"
        def vocr = cn.rows(sql.toString())

        def totalPags = Math.ceil((vocr.size() + sps + extraRows) / maxRows).toInteger()
        printHeaderDetalle([pag: currentPag, total: totalPags])

        Font fontThFooter = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        def frmtCol8 = [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8]
        def frmtCol11 = [border: Color.BLACK, bg: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 11]
        def frmtSuma = [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def printFooterDetalle = { params ->


            def txt = "AVANCE DE OBRA PARCIAL"
            if (params.completo) {
                txt = "AVANCE DE OBRA"
            }

            addCellTabla(tablaDetalles, new Paragraph(txt, fontThFooter), frmtCol8)
            addCellTabla(tablaDetalles, new Paragraph(numero(params.ant, 2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(params.act, 2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(params.acu, 2), fontThFooter), frmtSuma)
        }


        sp = 0
        vocr.each {vo ->
            if (sp != vo.sbpr__id) {
                addCellTabla(tablaDetalles, new Paragraph('Subpresupuesto: ' + vo.sbprdscr, fontThTiny), frmtSbpr)

                sp = vo.sbpr__id
                currentRows++
                rowsCurPag++
            }

            addCellTabla(tablaDetalles, new Paragraph(vo.rbrocdgo, fontTdTiny), frmtDtIz)
            addCellTabla(tablaDetalles, new Paragraph(vo.rbronmbr, fontTdTiny), frmtDtIz)

            addCellTabla(tablaDetalles, new Paragraph(vo.unddcdgo, fontTdTiny), frmtDtDr)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vocrpcun, 2, "hide"), fontTdTiny), frmtDtDr)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vocrcntd, 2, "hide"), fontTdTiny), frmtDtDr)

            addCellTabla(tablaDetalles, new Paragraph(numero(vo.cntdantr, 2, "hide"), fontTdTiny), frmtDtDrBorde)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.cntdactl, 2, "hide"), fontTdTiny), frmtDtDrBorde)
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.cntdacml, 2, "hide"), fontTdTiny), frmtDtDrBorde)

            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vlorantr, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vloractl, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(vo.vloracml, 2, "hide"), fontTdTiny), [bwt: 0.1, bct: Color.WHITE, bwb: 0.1, bcb: Color.WHITE, height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

            currentRows++
            rowsCurPag++
            sumaTotlAntr += vo.vlorantr
            sumaTotlActl += vo.vloractl
            sumaTotlAcml += vo.vloracml

            sumaPrclAntr += vo.vlorantr
            sumaPrclActl += vo.vloractl
            sumaPrclAcml += vo.vloracml

//            println "currentRows $currentRows, maxRows $maxRows"
            if(currentRows >= (maxRows) ) {
                printFooterDetalle([ant: sumaPrclAntr, act: sumaPrclActl, acu: sumaPrclAcml])

                sumaPrclAntr = 0
                sumaPrclActl = 0
                sumaPrclAcml = 0
                currentRows = 0

                document.add(tablaDetalles)

                document.add(firmas("detalle", "vertical", planilla))
//                printFirmas([tipo: "detalle", orientacion: "vertical"])
                currentPag++

                //-------------------
                document.newPage()
                tablaDetalles = new PdfPTable(11);
                tablaDetalles.setWidthPercentage(100);
                tablaDetalles.setWidths(arregloEnteros([12, 35, 5, 11, 11, 11, 11, 11, 11, 11, 11]))
                tablaDetalles.setSpacingAfter(1f);
                printHeaderDetalle([pag: currentPag, total: totalPags])
                rowsCurPag = 1
            }
        }

        printFooterDetalle([ant: sumaTotlAntr,  act: sumaTotlActl, acu: sumaTotlAcml, completo: true])

        def rjplAntr = 0
        def rjplAcml = 0
        def rjplActl = 0

        if(tipoRprt == 'T') {
            def rjplAntrCp = planillasService.reajusteAnterior(planilla.planillaCmpl)
            def rjplAcmlCp = planillasService.reajusteAcumulado(planilla.planillaCmpl)
            rjplAntr += rjplAntrCp
            rjplAcml += rjplAcmlCp
            rjplActl = rjplAcml - rjplAntr
        }

        addCellTabla(tablaDetalles, new Paragraph("REAJUSTE DE PRECIOS", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(rjplAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(rjplActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(rjplAcml, 2), fontThFooter), frmtSuma)

        addCellTabla(tablaDetalles, new Paragraph("SUBTOTAL", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr + rjplAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl + rjplActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml + rjplAcml, 2), fontThFooter), frmtSuma)

        sql = "select sum(plnlmnto) suma from plnl where cntr__id = ${planilla.contrato.id} and " +
                "tppl__id = 5 and plnlfcfn < '${planilla.fechaInicio.format('yyy-MM-yy')}'"

        def cstoAntr = cn.rows(sql.toString())[0].suma?:0
        sql = "select plnlmnto from plnl where plnl__id = (select plnl__id from plnl " +
                "where plnlpdcs = ${planilla.id})"
//        println "sql.....: $sql"
        def cstoActl = cn.rows(sql.toString())[0]?.plnlmnto?:0
        def cstoAcml = cstoAntr + cstoActl

        addCellTabla(tablaDetalles, new Paragraph("RUBROS NO CONTRACTUALES COSTO + PORCENTAJE", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(cstoAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(cstoActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(cstoAcml, 2), fontThFooter), frmtSuma)

        addCellTabla(tablaDetalles, new Paragraph("SUMATORIA DE AVANCE DE OBRA Y COSTO + PORCENTAJE", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr + cstoAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl + cstoActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml + cstoAcml, 2), fontThFooter), frmtSuma)

        sumaTotlAntr += rjplAntr + cstoAntr
        sumaTotlActl += rjplActl + cstoActl
        sumaTotlAcml += rjplAcml + cstoAcml

        addCellTabla(tablaDetalles, new Paragraph("TOTAL PLANILLA REAJUSTADA INCLUIDO COSTO + PORCENTAJE", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml, 2), fontThFooter), frmtSuma)

        addCellTabla(tablaDetalles, new Paragraph("DESCUENTOS CONTRACTUALES", fontThFooter), frmtCol11)


        sql = "select sum(plnldsct) suma from plnl where cntr__id = ${planilla.contrato.id} and " +
                "plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}' and plnltipo = '${planilla.tipoContrato}'"
//        println "sql.....: $sql"
        def antcAntr = cn.rows(sql.toString())[0].suma?:0
        def antcActl = planilla.descuentos
        def antcAcml = antcAntr + antcActl

        if(tipoRprt == 'T') {
            antcActl = planilla.descuentos + Planilla.get(planilla.planillaCmpl.id).descuentos
            antcAcml = antcAntr + antcActl
        }

        addCellTabla(tablaDetalles, new Paragraph("ANTICIPO", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(antcAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(antcActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(antcAcml, 2), fontThFooter), frmtSuma)

        sql = "select sum(mlplmnto) suma from mlpl where plnl__id in (select plnl__id from plnl where " +
                "cntr__id = ${planilla.contrato.id} and plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}' " +
                " and plnltipo = '${planilla.tipoContrato}')"
//        println "sql.....: $sql"
        def mltaAntr = cn.rows(sql.toString())[0].suma?:0

        sql = "select sum(plnlmles) suma from plnl where " +
                "cntr__id = ${planilla.contrato.id} and plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}' and " +
                "plnltipo = '${planilla.tipoContrato}'"
//        println "sql.....: $sql"
        mltaAntr += cn.rows(sql.toString())[0].suma?:0

        sql = "select sum(mlplmnto) suma from mlpl where plnl__id = ${planilla.id}"
//        println "sql.....: $sql"
        def mltaActl = cn.rows(sql.toString())[0].suma?:0

        sql = "select sum(plnlmles) suma from plnl where plnl__id = ${planilla.id}"
//        println "sql.....: $sql"
        mltaActl += cn.rows(sql.toString())[0].suma?:0

        def mltaAcml = mltaAntr + mltaActl


        addCellTabla(tablaDetalles, new Paragraph("MULTAS", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(mltaAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(mltaActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(mltaAcml, 2), fontThFooter), frmtSuma)

        sql = "select sum(plnlnpvl) suma from plnl where cntr__id = ${planilla.contrato.id} and " +
                "plnlfcfn < '${planilla.fechaInicio.format('yyyy-MM-dd')}'"
//        println "sql.....: $sql"
        def nopgAntr = cn.rows(sql.toString())[0].suma?:0
        def nopgActl = planilla.noPagoValor
        def nopgAcml = nopgAntr + nopgActl

        sumaTotlAntr -= mltaAntr + antcAntr + nopgAntr
        sumaTotlActl -= mltaActl + antcActl + nopgActl
        sumaTotlAcml -= mltaAcml + antcAcml + nopgAcml

//        println "nopgAntr $nopgAntr, nopgActl $nopgActl, nopgAcml $nopgAcml"
        if((nopgAntr + nopgActl + nopgAcml) > 0) {
            addCellTabla(tablaDetalles, new Paragraph(planilla.noPago?:" ", fontThFooter), frmtCol8)
            addCellTabla(tablaDetalles, new Paragraph(numero(nopgAntr,2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(nopgActl, 2), fontThFooter), frmtSuma)
            addCellTabla(tablaDetalles, new Paragraph(numero(nopgAcml, 2), fontThFooter), frmtSuma)
        }

        addCellTabla(tablaDetalles, new Paragraph("VALOR LIQUIDO A PAGAR", fontThFooter), frmtCol8)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAntr, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlActl, 2), fontThFooter), frmtSuma)
        addCellTabla(tablaDetalles, new Paragraph(numero(sumaTotlAcml, 2), fontThFooter), frmtSuma)

        document.add(tablaDetalles)

        document.add(firmas("detalle", "vertical", planilla))

        document.close();
        pdfw.close()
        return baos
    }


    def titlLogo() {
        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.scaleToFit(52,52)
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        return logo
    }

    def titlInst(espacio, planilla, obra) {
        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);

        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
        preface.add(new Paragraph("PLANILLA DE ${planilla.tipoPlanilla.nombre.toUpperCase()} DE LA OBRA " + obra.nombre, fontTituloGad));
        addEmptyLine(preface, espacio);

        return preface
    }

    def titlSbtt(fcha) {
        Font info = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL)
        Paragraph preface2 = new Paragraph()
//        preface2.setAlignment(Element.ALIGN_RIGHT)
//        preface2.add(new Paragraph("Generado por: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
//        println "fcha: $fcha"
        preface2.add(new Paragraph("Generado por: " + session.usuario + "   el: " + fcha.format("dd/MM/yyyy hh:mm"), info))
        preface2.setSpacingAfter(5);
//        addEmptyLine(preface2, 1);

        return preface2
    }

    def encabezado(espacio, size, planilla, tipo) {
        def obra = planilla.contrato.obra
        def contrato = planilla.contrato
//        def cntrCmpl = janus.Contrato.findByPadre(planilla.contrato)
//        println "contrato cmpl: $cntrCmpl"
        def monto = planilla.contrato.monto
        def valorObra = ReajustePlanilla.executeQuery("select max(acumuladoPlanillas) from ReajustePlanilla " +
                "where planilla = :p", [p: planilla])[0]?:0
        def valorCmpl = Planilla.executeQuery("select sum(valor) from Planilla where tipoContrato = 'C' and " +
                "contrato = :c and tipoPlanilla != :t", [c: planilla.contrato, t: TipoPlanilla.get(10)])[0]?:0

//        println "---->> tipo: <${tipo}>"
        if(tipo == 'T') {
            def cmpl = Contrato.findByPadre(planilla.contrato)
            monto += cmpl.monto
            valorObra += valorCmpl
        }
        if(planilla.tipoContrato == 'C') {
            contrato = janus.Contrato.findByPadre(planilla.contrato)
            monto = contrato.monto
            valorObra = valorCmpl
        }

//        println "-----valor obra: $valorObra, valorCmpl: ${valorCmpl}, cmpl: ${monto}"

        Font fontThUsar = new Font(Font.TIMES_ROMAN, size, Font.BOLD);
        Font fontTdUsar = new Font(Font.TIMES_ROMAN, size, Font.NORMAL);
        def bordeTdSinBorde = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        PdfPTable tablaHeaderPlanilla = new PdfPTable(5);
        tablaHeaderPlanilla.setWidthPercentage(100);
        tablaHeaderPlanilla.setWidths(arregloEnteros([12, 24, 10, 12, 24]))
        tablaHeaderPlanilla.setWidthPercentage(100);

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Obra", fontThUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(obra.nombre, fontTdUsar), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Lugar", fontThUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph((obra.lugar?.descripcion ?: ""), fontTdUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("Planilla", fontThUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(planilla.numero, fontTdUsar), bordeTdSinBorde)

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Ubicación", fontThUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(obra.parroquia?.nombre + " - Cantón " + obra.parroquia?.canton?.nombre, fontTdUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("Monto contrato", fontThUsar), bordeTdSinBorde)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(planilla.contrato.monto, 2), fontTdUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(monto, 2), fontTdUsar), bordeTdSinBorde)

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Contratista", fontThUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(planilla.contrato.oferta.proveedor.nombre, fontTdUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("Período", fontThUsar), bordeTdSinBorde)

        addCellTabla(tablaHeaderPlanilla, new Paragraph(ponePeriodoPlanilla(planilla), fontTdUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("Plazo", fontThUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(planilla.contrato.plazo, 0) + " días", fontTdUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("Valor obra", fontThUsar), bordeTdSinBorde)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(contrato.monto, 2), fontTdUsar), bordeTdSinBorde)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(valorObra, 2), fontTdUsar), bordeTdSinBorde)

        return tablaHeaderPlanilla
    }

    def firmas(tipo, orientacion, planilla) {
        def obra = planilla.contrato.obra
        def contrato = planilla.contrato

        def tablaFirmas = new PdfPTable(5);
        tablaFirmas.setWidthPercentage(100);

        def parametros = Parametros.get(1)

        def contratista = planilla.contrato.oferta.proveedor
        def fiscalizador = planilla.fiscalizador
        def subdirector = parametros.subdirector
        def administrador = contrato.administrador

        def strContratista = nombrePersona(contratista, "prov") + "\nContratista"
        def strFiscalizador = nombrePersona(fiscalizador) + "\nFiscalizador"
        def strSubdirector = "Ing. Miguel Velasteguí" + "\nSubdirector"
//        def strAdmin = nombrePersona(administrador) + "\nAdministrador del Contrato - Delegado"
        def strAdmin = nombrePersona(administrador) + "\nAdministrador"
        def strFechaPresentacion = fechaConFormato(planilla.fechaPresentacion, "dd-MMM-yyyy") + "\nFecha de presentación"
        def strFechaAprobacion = fechaConFormato(planilla.fechaIngreso, "dd-MMM-yyyy") + "\nFecha de aprobación"

        Font fontThFirmas = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTdFirmas = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        if (tipo == "detalle") {
            fontThFirmas = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
            fontTdFirmas = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);
            def frmtCol5 = [height: 10, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE,  colspan: 5]

            tablaFirmas.setWidths(arregloEnteros([40, 3, 40, 3, 40]))
            tablaFirmas.totalHeight = 100f

            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), frmtCol5)

            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 40, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 40, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 40, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            /* un espacio en blanco hace que no se imprima la línea para la firma */
            addCellTabla(tablaFirmas, new Paragraph(" ", fontThFirmas), [height: 40, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            // pone linea en blanco //
//            addCellTabla(tablaFirmas, new Paragraph(" ", fontThFirmas), [height: 40, bwb: 1, bcb: Color.WHITE, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph(" ", fontThFirmas), [height: 40, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])


            addCellTabla(tablaFirmas, new Paragraph(strContratista, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph(strFiscalizador, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph(strAdmin, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaFirmas, new Paragraph(strFechaPresentacion, fontTdFirmas), [height: 35, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_TOP])
            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph(strFechaAprobacion, fontTdFirmas), [height: 35, bcb: Color.WHITE, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_TOP])
            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph(strFechaAprobacion, fontTdFirmas), [height: 35, bcb: Color.WHITE, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_TOP])

            tablaFirmas.setKeepTogether(true)

        } else if (tipo == "otro") {
            if (orientacion == "horizontal") {
                tablaFirmas.setWidths(arregloEnteros([30,5, 30,5, 30]))
            } else if (orientacion == "vertical") {
                tablaFirmas.setWidths(arregloEnteros([25, 5,35,5, 25]))
            }
            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 25, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 25, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 25, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 25, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 25, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 15, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph(strFiscalizador, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        }

        return tablaFirmas
    }



}
