package janus

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter
import janus.actas.Acta
import janus.actas.Avance
import janus.actas.FraseClima;
import janus.ejecucion.DetallePlanilla;
import janus.ejecucion.DetallePlanillaCosto
import janus.ejecucion.DetallePlanillaEjecucion;
import janus.ejecucion.FormulaPolinomicaContractual
import janus.ejecucion.MultasPlanilla;
import janus.ejecucion.PeriodoPlanilla;
import janus.ejecucion.PeriodosInec;
import janus.ejecucion.Planilla
import janus.ejecucion.ReajustePlanilla;
import janus.ejecucion.TipoPlanilla;
import janus.ejecucion.ValorIndice;
import janus.ejecucion.ValorReajuste;
import janus.ejecucion.Pdfs;
import janus.pac.CronogramaEjecucion;
import janus.pac.CrngEjecucionObra;
import janus.pac.PeriodoEjecucion

import java.awt.Color;

import com.lowagie.text.*

import java.text.SimpleDateFormat

class ReportesPlanillasController {

    def preciosService
    def dbConnectionService
    def diasLaborablesService
    def planillasService


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

    def actaRecepcion() {
        println "acta recepcion: " + params
        def acta = Acta.get(params.id)
//        println ">>>> " + acta
        def direccion = Direccion.findAllByNombreIlike("%FISCALIZACI%")
        def delegadoFiscalizacion = null
        if(acta.contrato.delegadoFiscalizacion) {
            delegadoFiscalizacion = acta.contrato.delegadoFiscalizacion
        }
        def espacios = ""
        if(acta.espacios > 0) {
            acta.espacios.times {
               espacios += "<br/>"
            }
        }

///        println "espacios: $espacios"
        return [actaInstance: acta, directorDeFiscalizacion: delegadoFiscalizacion, espacios: espacios]
    }

    def actaRecepcionTotl() {
//        println "acta recepcion total: " + params
        def acta = Acta.get(params.id)
        def cmpl = Contrato.findByPadre(acta.contrato)
        def total = acta.contrato.monto + cmpl.monto
//        println ">>>> " + acta
        def direccion = Direccion.findAllByNombreIlike("%FISCALIZACI%")
        def delegadoFiscalizacion = null
        if(acta.contrato.delegadoFiscalizacion) {
            delegadoFiscalizacion = acta.contrato.delegadoFiscalizacion
        }
        def espacios = ""
        if(acta.espacios > 0) {
            acta.espacios.times {
               espacios += "<br/>"
            }
        }

//        println "cmpl: $cmpl, ${cmpl.monto}"
        return [actaInstance: acta, directorDeFiscalizacion: delegadoFiscalizacion, espacios: espacios,
        total: total, cmpl: cmpl]
    }

    def reporteDiferencias() {
        def contrato = Contrato.get(params.id)
        def obra = contrato.oferta.concurso.obra

        def planillasAvance = Planilla.findAllByContratoAndTipoPlanillaInList(contrato, TipoPlanilla.findAllByCodigoInList(["P", "Q"]), [sort: 'fechaInicio'])
//        def planillasAvance = Planilla.withCriteria {
//            eq("contrato", contrato)
//            eq("tipoPlanilla", TipoPlanilla.findAllByCodigo("P"))
//            order("fechaInicio", "asc")
//        }

        def planillasCosto = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("C"))
        }

        def planillaAnticipo = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
        }[0]

        def indirecto = obra.totales / 100
        preciosService.ac_rbroObra(obra.id)
        def detalles = [:]
//        def volumenes = VolumenesObra.findAllByObra(obra, [sort: "item"])
        def volumenes = VolumenContrato.findAllByObra(obra, [sort: "item"])

        volumenes.each { vol ->
            vol.refresh()
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, vol.item.id)
            def precio = (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2)

//            def precio = preciosService.rbro_pcun_v2_item(obra.id, vol.subPresupuesto.id, vol.item.id)

            if (!detalles[vol.item]) {
                detalles[vol.item] = [
                        codigo  : vol.item.codigo,
                        nombre  : vol.item.nombre,
                        unidad  : vol.item.unidad.codigo,
                        precio  : precio,
                        cantidad: [
                                contratado: 0,
                                ejecutado : 0
                        ],
                        valor   : [
                                contratado: 0,
                                ejecutado : 0
                        ]
                ]
            }
            detalles[vol.item].cantidad.contratado += vol.volumenCantidad
            detalles[vol.item].valor.contratado += ((vol.volumenCantidad * precio).toDouble().round(2))
        }

        planillasAvance.each { pla ->
//            def det = DetallePlanilla.findAllByPlanilla(pla)
            def det = DetallePlanillaEjecucion.findAllByPlanilla(pla)
            det.each { dt ->
                if (detalles[dt.volumenContrato.item]) {
                    detalles[dt.volumenContrato.item].cantidad.ejecutado += dt.cantidad
                    detalles[dt.volumenContrato.item].valor.ejecutado += dt.monto
                } else {
                    println "no existe detalle para " + dt.volumenObra.item + "???"
                }
            }
        }

        def baos = new ByteArrayOutputStream()
        def name = "diferencias_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)

        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
        Font fontNombre = new Font(Font.TIMES_ROMAN, 7, Font.NORMAL);

        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        Font fontThHeader = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTdHeader = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        def formatoFechasTabla = "dd-MM-yyyy"

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.scaleToFit(52, 52)
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        Document document
        document = new Document(PageSize.A4);
        document.setMargins(60, 30, 60, 60);
        // margins: left, right, top, bottom
        // 1 in = 72, 1cm=28.1, 3cm = 86.4
        def pdfw = PdfWriter.getInstance(document, baos);

        document.resetHeader()
        document.resetFooter()

        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Cuadro de diferencias de volúmenes entre contratados y ejecutados");
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
        preface.add(new Paragraph("Cuadro de diferencias de volúmenes entre contratados y ejecutados", fontTituloGad));
        addEmptyLine(preface, 1);
        Paragraph preface2 = new Paragraph();
        preface2.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
        addEmptyLine(preface2, 1);
        document.add(logo)
        document.add(preface);
        document.add(preface2);

        /* ************************************************************** HEADER ****************************************************************************************************/
        PdfPTable tablaHeaderPlanilla = new PdfPTable(5);
        tablaHeaderPlanilla.setWidthPercentage(100);
        tablaHeaderPlanilla.setWidths(arregloEnteros([12, 24, 10, 12, 24]))
        tablaHeaderPlanilla.setSpacingAfter(10f)

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Obra", fontThHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(obra.nombre, fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Lugar", fontThHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph((obra.lugar?.descripcion ?: ""), fontTdHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTdHeader), prmsTdNoBorder)

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Ubicación", fontThHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(obra.parroquia?.nombre + " - Cantón " + obra.parroquia?.canton?.nombre, fontTdHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("Monto contrato", fontThHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(contrato.monto, 2), fontTdHeader), prmsTdNoBorder)

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Contratista", fontThHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(contrato.oferta.proveedor.nombre, fontTdHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTdHeader), prmsTdNoBorder)

        addCellTabla(tablaHeaderPlanilla, new Paragraph("Plazo", fontThHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(contrato.plazo, 0) + " días", fontTdHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontThHeader), prmsTdNoBorder)
        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTdHeader), prmsTdNoBorder)

        document.add(tablaHeaderPlanilla);
        /* ************************************************************** FIN HEADER ****************************************************************************************************/
        /* ************************************************************** DETALLES ****************************************************************************************************/
        PdfPTable tablaCant = new PdfPTable(3);
        tablaCant.setWidthPercentage(100);
        addCellTabla(tablaCant, new Paragraph("Cantidades", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
        addCellTabla(tablaCant, new Paragraph("Ejecutadas", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaCant, new Paragraph("En más", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaCant, new Paragraph("En menos", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        PdfPTable tablaVal = new PdfPTable(3);
        tablaVal.setWidthPercentage(100);
        addCellTabla(tablaVal, new Paragraph("Valores", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
        addCellTabla(tablaVal, new Paragraph("Ejecutados", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaVal, new Paragraph("En más", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaVal, new Paragraph("En menos", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        PdfPTable tablaDetalles = new PdfPTable(11);
        tablaDetalles.setWidthPercentage(100);
        tablaDetalles.setWidths(arregloEnteros([9, 19, 3, 7, 8, 9, 9, 9, 9, 9, 9]))
        tablaDetalles.setSpacingAfter(10f)

        addCellTabla(tablaDetalles, new Paragraph("N.", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalles, new Paragraph("Descripción del rubro", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalles, new Paragraph("U.", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalles, new Paragraph("Precio unitario", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalles, new Paragraph("Volumen contrat.", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalles, tablaCant, [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
        addCellTabla(tablaDetalles, tablaVal, [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])

        def height = 12

        def totalEjecutado = 0, totalMas = 0, totalMenos = 0
        def tot = 0

        detalles.each { k, det ->
            def cantMas = 0, cantMenos = 0, valMas = 0, valMenos = 0
            def difCant = det.cantidad.contratado - det.cantidad.ejecutado
            def difVal = det.valor.contratado - det.valor.ejecutado
            if (difCant >= 0) {
//                cantMas = difCant
                cantMenos = difCant
            } else {
//                cantMenos = difCant
                cantMas = difCant * -1
            }
            if (difVal >= 0) {
//                valMas = difVal
                valMenos = difVal
            } else {
//                valMenos = difVal
                valMas = difVal * -1
            }

            addCellTabla(tablaDetalles, new Paragraph(det.codigo, fontTd), [height: height, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(det.nombre, fontNombre), [height: height, border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(det.unidad, fontTd), [height: height, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(det.precio, 2), fontTd), [height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(det.cantidad.contratado, 2), fontTd), [height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

            tot += (det.precio * det.cantidad.contratado)

            addCellTabla(tablaDetalles, new Paragraph(numero(det.cantidad.ejecutado, 2), fontTd), [height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(cantMas, 2, "hide"), fontTd), [height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(cantMenos, 2, "hide"), fontTd), [height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tablaDetalles, new Paragraph(numero(det.valor.ejecutado, 2), fontTd), [height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(valMas, 2, "hide"), fontTd), [height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalles, new Paragraph(numero(valMenos, 2, "hide"), fontTd), [height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

            totalEjecutado += det.valor.ejecutado
            totalMas += valMas
            totalMenos += valMenos
        }

        addCellTabla(tablaDetalles, new Paragraph("", fontTd), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 8])
        addCellTabla(tablaDetalles, new Paragraph(numero(totalEjecutado, 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalles, new Paragraph(numero(totalMas, 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalles, new Paragraph(numero(totalMenos, 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaDetalles);
        /* ************************************************************** FIN DETALLES ****************************************************************************************************/
        /* ************************************************************** DIFERENCIA ****************************************************************************************************/
//        println "totalMas $totalMas, totalMenos $totalMenos"
        def totalMenosMas = totalMas - Math.abs(totalMenos)
        def totalEjecContrat = totalEjecutado - contrato.monto

        def num = (totalMenosMas / contrato.monto) * 100

        PdfPTable tablaPrct = new PdfPTable(1);
        tablaPrct.setWidthPercentage(100);
        addCellTabla(tablaPrct, new Paragraph("%", fontTh), [bct: Color.WHITE, bwt: 0.1, bwr: 0.1, bwl: 0.1, bwb: 0.1, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaPrct, new Paragraph(numero(num, 2), fontTh), [bcb: Color.WHITE, bwb: 0.1, bwr: 0.1, bwl: 0.1, bwt: 0.1, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        PdfPTable tablaDifs = new PdfPTable(3);
        tablaDifs.setWidthPercentage(100);
        addCellTabla(tablaDifs, new Paragraph(numero(Math.abs(totalMenos), 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDifs, new Paragraph("-", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDifs, new Paragraph(numero(Math.abs(totalMas), 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDifs, new Paragraph(numero(totalMenosMas, 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])

        addCellTabla(tablaDifs, new Paragraph(numero(totalEjecutado, 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDifs, new Paragraph("-", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDifs, new Paragraph(numero(contrato.monto, 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDifs, new Paragraph(numero(totalEjecContrat, 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])

        PdfPTable tablaDiferencia = new PdfPTable(3);
        tablaDiferencia.setWidthPercentage(100);
        tablaDiferencia.setWidths(arregloEnteros([64, 9, 27]))
        tablaDiferencia.setSpacingAfter(10f)

        addCellTabla(tablaDiferencia, new Paragraph("DIFERENCIA", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDiferencia, tablaPrct, [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDiferencia, tablaDifs, [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaDiferencia)
        /* ************************************************************** FIN DIFERENCIA ****************************************************************************************************/
        /* ************************************************************** COSTO ****************************************************************************************************/
        def totalCosto = planillasCosto.sum { it.valor } ?: 0
        def prctCosto = (totalCosto / contrato.monto) * 100

        PdfPTable tablaCosto = new PdfPTable(3);
        tablaCosto.setWidthPercentage(100);
        tablaCosto.setWidths(arregloEnteros([64, 9, 27]))
        tablaCosto.setSpacingAfter(10f)

        addCellTabla(tablaCosto, new Paragraph("OBRAS BAJO LA MODALIDAD COSTO + PORCENTAJE", fontTh), [padding: 8, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaCosto, new Paragraph(numero(prctCosto, 2), fontTh), [padding: 8, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaCosto, new Paragraph(numero(totalCosto, 2), fontTh), [padding: 8, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        document.add(tablaCosto)
        /* ************************************************************** FIN COSTO ****************************************************************************************************/
        /* ************************************************************** TOTAL ****************************************************************************************************/

        def totalAdicional = totalCosto + totalMenosMas
        def prctAdicional = (totalAdicional / contrato.monto) * 100

        PdfPTable tablaTots = new PdfPTable(3);
        tablaTots.setWidthPercentage(100);
        addCellTabla(tablaTots, new Paragraph(numero(totalCosto, 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaTots, new Paragraph("+", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaTots, new Paragraph(numero(totalMenosMas, 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaTots, new Paragraph(numero(totalAdicional, 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])

        PdfPTable tablaTotal = new PdfPTable(3);
        tablaTotal.setWidthPercentage(100);
        tablaTotal.setWidths(arregloEnteros([64, 9, 27]))
        tablaTotal.setSpacingAfter(1f)

        addCellTabla(tablaTotal, new Paragraph("TOTAL OBRAS ADICIONALES", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaTotal, new Paragraph(numero(prctAdicional, 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaTotal, tablaTots, [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        document.add(tablaTotal)
        /* ************************************************************** FIN DIFERENCIA ****************************************************************************************************/


        /*--------- firmas -------*/
        def tablaFirmas = new PdfPTable(1);
        tablaFirmas.setWidthPercentage(30);
        def fiscalizador = contrato.fiscalizador
        def strFiscalizador = nombrePersona(fiscalizador) + "\nFiscalizador"

        addCellTabla(tablaFirmas, new Paragraph("", fontTd), [height: 35, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFirmas, new Paragraph(strFiscalizador, fontThHeader), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

//        println "document.properties: ${document.properties}"

        document.add(tablaFirmas)
        /*---- fin de firmas -----*/

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def reporteAvanceUI() {
        def contrato = Contrato.get(params.id.toLong())
        def tppl = TipoPlanilla.findAllByCodigoInList(["P", "Q", "R"])

        def tipoObras = TipoPlanilla.findByCodigo("O")

        def planillaObras = Planilla.findAllByContratoAndTipoPlanilla(contrato, tipoObras)


        def planillasAvance = Planilla.findAllByContratoAndTipoPlanillaInList(contrato, tppl, [sort: 'fechaInicio'])
/*
        def planillasAvance = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
            order("fechaInicio", "asc")
        }
*/

        def fechas = planillasAvance.fechaFin*.format("dd-MM-yyyy")

        return [contrato: contrato, fechas: fechas, planillas: planillasAvance, planillaObras: planillaObras]
    }

    def tablaAvance() {
//        println "tablaAvance...: $params"
        def cn = dbConnectionService.getConnection()
        def contrato = Contrato.get(params.id)
        def plnl = Planilla.get(params.plnl)
        def avanceContrato = Avance.findAllByContratoAndPlanilla(contrato, plnl)
        def frases = []
        def band = 0
        if (avanceContrato.size() == 0) {
            band = 1
        } else if (avanceContrato.size() == 1) {
            avanceContrato = avanceContrato[0]
            frases = FraseClima.findAllByAvance(avanceContrato, [sort: "fecha"])
        } else {
            println "Hay mas de un avance para el contrato ${contrato.id} para la planilla ${params.plnl}: ${avanceContrato.id}"
            avanceContrato = avanceContrato[0]
            frases = FraseClima.findAllByAvance(avanceContrato, [sort: "fecha"])
        }
        def titulos = [
                "A.- Resultados de los ensayos de materiales",
                "B.- Análisis de la cantidad y calidad de ls equipos y maquinaria en obra",
                "C.- Cuadro de las condiciones climáticas del sitio de la obra",
                "D.- Detalle de la correspondencia intercambiada con el contratista (20 líneas)",
                "E.- Análisis del personal técnico del contratista",
                "F.- Actividades más importantes del periodo (16 líneas)",
                "G.- Seguridad industrial y personal",
                "H.- Cumplimiento de especificaciones técnicas",
                "I.- Decisiones importantes",
                "J.- Detalle de reuniones",
                "K.- Visitas programadas a la obra para el período",
                "L.- Visitas efectuadas por la fiscalización en el período",
                "Observaciones",
                "Conclusiones y recomendaciones"
        ]
        def html = "<legend>Textos del informe de avance<span style='margin-left: 180px'>" +
                "<a href='#' class='btn btn-success btnSave'><i class='icon icon-save'></i>Guardar</a> </span>" +
                "<a href='#' class='btn btn-primary btnPrint'><i class='icon icon-print'></i>Imprimir</a>" +
                "<a href='#' class='btn btn-success btnPrintTotal'><i class='icon icon-print'></i>Imprimir Total</a></legend>"
//        if (band == 1) {
        def suspension = cn.rows("select prejfcin, prejfcfn from prej where cntr__id = ${contrato.id} and prejtipo = 'S'".toString())[0]
//        println "suspensión: $suspension, ${suspension.prejfcin.class}"
        def dateFormat = new SimpleDateFormat("EEEE dd-MM-yyyy", new Locale("es"))
        titulos.eachWithIndex { t, i ->
            html += "<h5>${t}</h5>"
            if (i == 2) {
                //lo del clima

                html += "<table border='1' class='tabla'>"
                html += "<thead>"
                html += "<tr>"
                html += "<th>Día</th>"
                html += "<th>Mañana</th>"
                html += "<th>Tarde</th>"
                html += "</tr>"
                html += "</thead>"
                def dia = plnl.fechaInicio
                def fin = plnl.fechaFin
                while (dia <= fin) {
                    /** eliminar suspensiones **/
                    if(dia >= suspension?.prejfcin && dia <= suspension?.prejfcfn) {
//                        println "dia: $dia es suspensión"
                    } else {
                        def valM = "", valT = ""
                        if (frases.size() > 0) {
                            def fr = frases.find { it.fecha == dia }
                            valM = fr ? fr.manana : ""
                            valT = fr ? fr.tarde : ""
                        }
                        html += "<tr>"
                        html += "<td>${dateFormat.format(dia).capitalize()}</td>"
                        html += "<td>${g.select(name: 'clima_m_' + dia.format('dd-MM-yyyy'), from: ["Lluvioso", "Nublado", "Soleado"], value: valM, "class": "clima", "data-tipo": "m", "data-fecha": dia.format("dd-MM-yyyy"))}</td>"
                        html += "<td>${g.select(name: 'clima_t_' + dia.format('dd-MM-yyyy'), from: ["Lluvioso", "Nublado", "Soleado"], value: valT, "class": "clima2", "data-tipo": "t", "data-fecha": dia.format("dd-MM-yyyy"))}</td>"
                        html += "</tr>"
                    }
                    dia++
                }
                html += "</table>"
            } else {
                def num = (i + 1).toString().padLeft(2, "0")
                def val = band == 0 ? avanceContrato["frase" + num] : ""
                if(i == 3) {  //la frase 6: F.- Actividades más importantes del periodo va hasta 1023 c
                    html += g.textArea(name: "texto_${i + 1}", value: val, "class": "texto", "data-num": num, "maxLength": 2040, style: "width: 1000px; height:100px;")
                } else if(i == 5) {  //la frase 6: F.- Actividades más importantes del periodo va hasta 1023 c
                    html += g.textArea(name: "texto_${i + 1}", value: val, "class": "texto", "data-num": num, "maxLength":1023, style: "width: 1000px; height:100px;")
                } else {
                    html += g.textArea(name: "texto_${i + 1}", value: val, "class": "texto", "data-num": num, "maxLength":511, style: "width: 1000px; height:100px;")
                }
            }
        }
        html += "<div style='margin-bottom:10px; margin-top:5px;'>" +
                "<a href='#' class='btn btn-success btnSave'><i class='icon icon-save'></i>Guardar</a>" +
                "<a href='#' id='btnSpin' hidden >Procesando</a>" +
                "<a href='#' class='btn btn-primary btnPrint' style='margin-left:15px;'><i class='icon icon-print'></i>Imprimir</a>" +
                "</div>"

        return [html: html, contrato: contrato, fecha: params.plnl, plnl: plnl.id]
    }

    def saveAvance() {
//        println "---saveAvance planilla:$params.plnl"
        def contrato = Contrato.get(params.id)
        def plnl = Planilla.get(params.plnl)
        def avanceContrato = Avance.findAllByContratoAndPlanilla(contrato, plnl)
        def errores = ""

        if (avanceContrato.size() == 0) {
//            println "0 -- crea avance"
            avanceContrato = new Avance([contrato: contrato, planilla: plnl])
//            println "avance: $avanceContrato"
        } else if (avanceContrato.size() == 1) {
//            println "1"
            avanceContrato = avanceContrato[0]
        } else {
            println "error... hay mas de un avance"
            avanceContrato = avanceContrato[0]
        }

        params.texto.each { t ->
            //numer^texto
            def parts = t.toString().split("\\^")
//            println "--------- ${parts[0]}: ${parts[1]?.size()}"
            if (parts.size() == 2) {
                avanceContrato["frase" + parts[0]] = parts[1]
            } else if (parts.size() == 1){
                avanceContrato["frase" + parts[0]] = ""
            }
        }
        if (!avanceContrato.save(flush: true)) {
            println "Error saveAvance: " + avanceContrato.errors
            errores += "<li>Ha ocurrido un error: " + renderErrors(bean: avanceContrato) + "</li>"
        }
        params.clima.each { c ->
            //fecha^manana^tarde
            def parts = c.toString().split("\\^")
//            println "clima: $parts"
            if (parts.size() == 3) {
                def fechaAct = new Date().parse("dd-MM-yyyy", parts[0])
                def fr = FraseClima.findAllByAvanceAndFecha(avanceContrato, fechaAct)
                if (fr.size() == 0) {
                    fr = new FraseClima(avance: avanceContrato, fecha: fechaAct)
                } else if (fr.size() == 1) {
                    fr = fr[0]
                } else {
                    println "WTF hay ${fr.size()} frases clima para el avance ${avanceContrato.id} para la fecha ${parts[0]}: ${avanceContrato.id}"
                    fr = fr[0]
                }
                fr.manana = parts[1]
                fr.tarde = parts[2]
                if (!fr.save(flush: true)) {
                    println "Error: " + fr.errors
                    errores += "<li>Ha ocurrido un error: " + renderErrors(bean: fr) + "</li>"
                }
            }
        }
        if (errores == "") {
            render "OK"
        } else {
            render "<ul>" + errores + "</ul>"
        }
    }

    def reporteAvance() {
        println "reporteAvance $params"
        def cn = dbConnectionService.getConnection()
        def plnl = Planilla.get(params.plnl)
        if (!params.id) {
            flash.message = "No se puede mostrar el reporte de avance sin seleccionar un contrato."
            redirect(action: "errores")
            return
        }

        def contrato = plnl.contrato
        def avanceContrato

        def modificaciones = Modificaciones.findAllByContratoAndFechaFinIsNotNull(contrato)

//        println("Modificaciones " + modificaciones)

        avanceContrato = Avance.findByContratoAndPlanilla(contrato, plnl)

        def obra = contrato.obra
//
//        def planillasAvance = Planilla.withCriteria {
//            eq("contrato", contrato)
//            eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
//            le("fechaFin", fecha)
//            order("fechaInicio", "asc")
//        }

        def tipoP = TipoPlanilla.findByCodigo("P")
        def tipoQ = TipoPlanilla.findByCodigo("Q")
        def tipoD = TipoPlanilla.findByCodigo("D")
        def tipos = [tipoQ, tipoP, tipoD]


        def planillasAvance = Planilla.findAllByContratoAndTipoPlanillaInListAndFechaFinLessThanEquals(contrato,tipos, plnl.fechaFin, [sort: 'fechaFin'])


        def planillasCosto = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("C"))
//            le("fechaFin", plnl.fechaFin)
            eq("padreCosto", plnl)
        }

        def planillaAnticipo = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
        }[0]

//        def tramiteInicioObra = Tramite.withCriteria {
//            eq("planilla", planillaAnticipo)
//            eq("tipoTramite", TipoTramite.findByCodigo("INOB"))
//        }[0]

        def prej = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaInicio', order: "asc"])

        def anticipoDescontado = planillasAvance.sum { it.descuentos } ?: 0
        def prctAnticipo = 100 * anticipoDescontado / contrato.anticipo;

        def detalles = VolumenContrato.findAllByObra(obra, [sort: "volumenOrden"])
        def crej = CrngEjecucionObra.withCriteria {
            inList("volumenObra", detalles)
            if(plnl.tipoPlanilla.id != tipoQ.id) {
                periodo {
                    le("fechaFin", plnl.fechaFin)
                }
            }

//            periodo {
//                le("fechaFin", plnl.fechaFin)
//            }
        }

        println "crej: ${crej.size()}"

        def inversionProgramada = crej.sum { it.precio } ?: 0
        def inversionReal = planillasAvance.sum { it.valor } ?: 0


        def tx = "select sum(plnlmnto) suma from plnl where cntr__id = ${plnl.contrato.id} and tppl__id = 5 and " +
                "plnlfcfn <= '${plnl.fechaFin}'"

        def costoPorcentaje = cn.rows(tx.toString())[0]?.suma ?: 0


//        def multas = planillasAvance.sum { it.multaRetraso + it.multaPlanilla } ?: 0
        tx = "select sum(mlplmnto) suma from mlpl where plnl__id in (select plnl__id from plnl " +
                "where cntr__id = ${plnl.contrato.id} and tppl__id in (3,9,4) and plnlfcfn <= '${plnl.fechaFin}')"

//        println "multas: $tx"
        def multas = cn.rows(tx.toString())[0]?.suma?:0
        println "multas mlpl: $multas, este plnlmles: ${plnl.multaEspecial?:0}"
        tx = "select sum(plnlmles) suma from plnl where cntr__id = ${plnl.contrato.id} and " +
                "tppl__id in (3,9,4) and plnlfcfn <= '${plnl.fechaFin}'"
        multas += cn.rows(tx.toString())[0]?.suma?:0  /* multas especiales anteriores */

        multas += plnl.multaEspecial?:0

//        println "sum: $tx"
        def baos = new ByteArrayOutputStream()
        def name = "avance_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)
        Font fontTitle = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        def formatoFechasTabla = "dd-MM-yyyy"

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.scaleToFit(52, 52)
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        Document document
        document = new Document(PageSize.A4);
        def pdfw = PdfWriter.getInstance(document, baos);

//        HeaderFooter footer = new HeaderFooter(new Phrase("This is page: "), true);
//        document.setFooter(footer);
        document.resetHeader()
        document.resetFooter()

        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Avance de la obra " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
//        preface.add(new Paragraph("AVANCE DE LA OBRA " + obra.nombre + " AL " + fechaConFormato(plnl.fechaFin, "dd MMMM yyyy").toUpperCase(), fontTituloGad));
        preface.add(new Paragraph("AVANCE DE LA OBRA " + obra.nombre + " AL " + fechaConFormato(contrato?.fechaPedidoRecepcionFiscalizador, "dd MMMM yyyy").toUpperCase(), fontTituloGad));
        addEmptyLine(preface, 1);
        Paragraph preface2 = new Paragraph();
        Paragraph titulo = new Paragraph("INFORME DE FISCALIZACIÓN N°: " + plnl.numero, fontTituloGad);
//        preface2.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
        preface2.add(new Paragraph("Fiscalizador: " + planillasService.nombrePersona(plnl.fiscalizador, 'pers') +
                "   Fecha: " + fechaConFormato(plnl.fechaIngreso, "dd-MMM-yyyy")))
        addEmptyLine(preface2, 1);
        document.add(logo)
        document.add(preface);
        document.add(titulo);
        document.add(preface2);

        /* **************************************************************** GENERALIDADES ******************************************************************************/
        PdfPTable tablaGeneralidades = new PdfPTable(2);
        tablaGeneralidades.setWidthPercentage(100);
        tablaGeneralidades.setWidths(arregloEnteros([35, 65]))

        addCellTabla(tablaGeneralidades, new Paragraph("1.- GENERALIDADES", fontTitle), [padding: 3, pb: 5, border: Color.WHITE, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tablaGeneralidades, new Paragraph("OBRA", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(obra.nombre, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("LUGAR", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(obra.sitio, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("UBICACIÓN", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph("PARROQUIA " + obra.parroquia.nombre + " CANTÓN " + obra.parroquia.canton.nombre, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("CONTRATISTA", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(contrato.oferta.proveedor.nombre, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("MONTO DEL CONTRATO " + '$.', fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(numero(contrato.monto, 2), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

//        addCellTabla(tablaGeneralidades, new Paragraph("MONTO DEL CONTRATO COMP. "+'$.', fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaGeneralidades, new Paragraph("?", fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("NÚMERO CONTRATO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(contrato.codigo, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("MODALIDAD", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(contrato.tipoContrato.descripcion, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("TIPO DE OBRA", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(obra.tipoObjetivo.descripcion, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("OBJETO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(contrato.objeto, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

//        addCellTabla(tablaGeneralidades, new Paragraph("TELÉFONOS", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaGeneralidades, new Paragraph("?", fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("FISCALIZADOR", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(nombrePersona(planillasAvance.size() > 0 ? planillasAvance.last().fiscalizador : null), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("FECHA DE SUSCRIPCIÓN", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(fechaConFormato(contrato.fechaSubscripcion, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaGeneralidades)
        /* **************************************************************** FIN GENERALIDADES **************************************************************************/
        /* **************************************************************** DETALLE PLAZOS ******************************************************************************/
        PdfPTable tablaDetalle = new PdfPTable(4);
        tablaDetalle.setWidthPercentage(100);
        tablaDetalle.setWidths(arregloEnteros([35, 25, 15, 25]))
        tablaDetalle.setSpacingBefore(5f);

        addCellTabla(tablaDetalle, new Paragraph("2.- DETALLE DE PLAZOS", fontTitle), [padding: 3, pb: 5, border: Color.WHITE, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])

        addCellTabla(tablaDetalle, new Paragraph("PLAZO CONTRACTUAL", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(numero(contrato.plazo, 0) + " DÍAS CALENDARIO", fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])

        addCellTabla(tablaDetalle, new Paragraph("FECHA TRÁMITE ANTICIPO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(planillaAnticipo.fechaOficioEntradaPlanilla, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(planillaAnticipo.oficioEntradaPlanilla, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDetalle, new Paragraph("FECHA ENTREGA ANTICIPO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(planillaAnticipo.fechaMemoPagoPlanilla, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(planillaAnticipo.memoPagoPlanilla, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDetalle, new Paragraph("FECHA INICIO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(prej.first().fechaInicio, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(obra.memoInicioObra, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])


        addCellTabla(tablaDetalle, new Paragraph("FECHA VENCIMIENTO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(prej.first().fechaInicio + contrato?.plazo?.toInteger() -1, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])

        if(plnl.tipoPlanilla.codigo == 'Q') {
            addCellTabla(tablaDetalle, new Paragraph("FECHA DE TERMINACIÓN", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(plnl.fechaFin, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])
        }

        def totalDias = 0

        if(modificaciones.size() > 0){
        modificaciones.each {mod ->
            totalDias += mod.dias;
            if(mod.tipo == 'A'){
                addCellTabla(tablaDetalle, new Paragraph("AMPLIACIÓN DE PLAZO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalle, new Paragraph(numero(mod?.dias,0) + " DÍAS", fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalle, new Paragraph(mod?.memo, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            }
            if(mod.tipo == 'S'){
                addCellTabla(tablaDetalle, new Paragraph("SUSPENSIÓN", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalle, new Paragraph(numero(mod?.dias,0) + " DÍAS", fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalle, new Paragraph(mod?.memo, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaDetalle, new Paragraph("REINICIO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(mod.fechaFin + 1, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalle, new Paragraph("", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalle, new Paragraph("", fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            }
        }
            addCellTabla(tablaDetalle, new Paragraph("NUEVA FECHA VENCIMIENTO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(prej.last().fechaFin, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])

        }

        document.add(tablaDetalle)
        /* **************************************************************** FIN DETALLE PLAZOS **************************************************************************/
        /* **************************************************************** EVALUACION ******************************************************************************/
        PdfPTable tablaEvaluacion = new PdfPTable(3);

        tablaEvaluacion.setWidthPercentage(100);
        tablaEvaluacion.setWidths(arregloEnteros([35, 33, 32]))
        tablaEvaluacion.setSpacingBefore(5f);

        addCellTabla(tablaEvaluacion, new Paragraph("3.- EVALUACIÓN DEL AVANCE FÍSICO", fontTitle), [padding: 3, pb: 5, border: Color.WHITE, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])

        addCellTabla(tablaEvaluacion, new Paragraph("VALOR DEL ANTICIPO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(numero(contrato.anticipo, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(numero(contrato.porcentajeAnticipo, 2) + '%', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEvaluacion, new Paragraph("ANTICIPO DESCONTADO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(numero(anticipoDescontado, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(numero(prctAnticipo, 2) + '%', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEvaluacion, new Paragraph("AVANCE FÍSICO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(numero(planillasAvance.last().avanceFisico, 2), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(' ', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

//        addCellTabla(tablaEvaluacion, new Paragraph("PROGRAMADO ACUMULADO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaEvaluacion, new Paragraph(numero(anticipoDescontado, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaEvaluacion, new Paragraph(numero(prctAnticipo, 2) + '%', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])


        document.add(tablaEvaluacion)
        /* **************************************************************** FIN EVALUACION **************************************************************************/
        /* **************************************************************** ECONOMICO ******************************************************************************/
        PdfPTable tablaEconomico = new PdfPTable(2);

        tablaEconomico.setWidthPercentage(100);
        tablaEconomico.setWidths(arregloEnteros([60, 40]))
        tablaEconomico.setSpacingBefore(5f);

        addCellTabla(tablaEconomico, new Paragraph("4.- AVANCE ECONÓMICO", fontTitle), [padding: 3, pb: 5, border: Color.WHITE, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tablaEconomico, new Paragraph("INVERSION PROGRAMADA ACUMULADA", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEconomico, new Paragraph(numero(inversionProgramada, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEconomico, new Paragraph("INVERSION REAL ACUMULADA", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEconomico, new Paragraph(numero(inversionReal, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEconomico, new Paragraph("VALOR POR COSTO + PORCENTAJE ACUMULADO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEconomico, new Paragraph(numero(costoPorcentaje, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEconomico, new Paragraph("MULTAS ACUMULADAS", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEconomico, new Paragraph(numero(multas, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaEconomico)
        /* **************************************************************** FIN ECONOMICO **************************************************************************/
        /* **************************************************************** RESUMEN ******************************************************************************/
        PdfPTable tablaResumen = new PdfPTable(1);
        tablaResumen.setWidthPercentage(100);
        tablaResumen.setSpacingBefore(5f);
        addCellTabla(tablaResumen, new Paragraph("5.- RESUMEN DE DECISIONES IMPORTANTES DE AVANCE DE OBRA O ACTIVIDADES REALIZADAS EN ESTE PERIODO", fontTitle), [padding: 3, pb: 5, border: Color.WHITE, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        document.add(tablaResumen)

        def frases = []
/*
        if (avanceContrato.size() == 0) {
            println "No hay un avance para el contrato ${contrato.id} para la fecha ${plnl.fechaFin}"
            avanceContrato = null
        } else if (avanceContrato.size() == 1) {
//            avanceContrato = avanceContrato[0]
            frases = FraseClima.findAllByAvance(avanceContrato, [sort: "fecha"])
        } else {
            println "Hay mas de un avance para el contrato ${contrato.id} para la fecha ${plnl.fechaFin}: ${avanceContrato.id}"
            avanceContrato = avanceContrato[0]
            frases = FraseClima.findAllByAvance(avanceContrato, [sort: "fecha"])
        }
*/
        frases = FraseClima.findAllByAvance(avanceContrato, [sort: "fecha"])
//        println "frases ok, $avanceContrato.id, $frases"

        if (avanceContrato) {
            def titulos = [
                    "A.- Resultados de los ensayos de materiales",
                    "B.- Análisis de la cantidad y calidad de los equipos y maquinaria en obra",
                    "C.- Cuadro de las condiciones climáticas del sitio de la obra",
                    "D.- Detalle de la correspondencia intercambiada con el contratista",
                    "E.- Análisis del personal técnico del contratista",
                    "F.- Actividades más importantes del período",
                    "G.- Seguridad industrial y personal",
                    "H.- Cumplimiento de especificaciones técnicas",
                    "I.- Decisiones importantes",
                    "J.- Detalle de reuniones",
                    "K.- Visitas programadas a la obra para el período",
                    "L.- Visitas efectuadas por la fiscalización en el período",
                    "Observaciones",
                    "Conclusiones y recomendaciones"
            ]
            def dateFormat = new SimpleDateFormat("EEEE dd-MM-yyyy", new Locale("es"))
            titulos.eachWithIndex { t, i ->
                document.add(new Paragraph(t, fontTitle))
                if (i == 2) {
                    //lo del clima
//                    def periodo = Planilla.findAllById(planillasAvance.last().id, [sort: "fechaInicio", order: "desc"])
//                    def dia = periodo[0].fechaInicio
//                    def fin = periodo[0].fechaFin
                    def dia = plnl.fechaInicio
                    def fin = plnl.fechaFin
                    def suspension = cn.rows("select prejfcin, prejfcfn from prej where cntr__id = ${contrato.id} and prejtipo = 'S'".toString())[0]
                    def tablaClima = new PdfPTable(3);
                    tablaClima.setWidths(arregloEnteros([40, 30, 30]))
                    tablaClima.setWidthPercentage(50);
                    tablaClima.setSpacingBefore(5f);

                    addCellTabla(tablaClima, new Paragraph('Día', fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaClima, new Paragraph('Mañana', fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaClima, new Paragraph('Tarde', fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

                    while (dia <= fin) {
                        if(dia >= suspension?.prejfcin && dia <= suspension?.prejfcfn) {
//                            println "dia: $dia es suspensión"
                        } else {
                            def valM = "", valT = ""
                            if (frases.size() > 0) {
                                def fr = frases.find { it.fecha == dia }
                                valM = fr ? fr.manana : ""
                                valT = fr ? fr.tarde : ""
                            }
                            addCellTabla(tablaClima, new Paragraph(dateFormat.format(dia).capitalize(), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                            addCellTabla(tablaClima, new Paragraph(valM, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                            addCellTabla(tablaClima, new Paragraph(valT, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                        }
                        dia++
                    }
                    document.add(tablaClima)
                } //if i==2: lo del clima
                else {
                    def num = (i + 1).toString().padLeft(2, "0")
                    def val = avanceContrato["frase" + num]
                    document.add(new Paragraph(val, fontTd))
                }// if i!= 2: lo q no es el clima
            }
        }

        /* **************************************************************** FIN RESUMEN ******************************************************************************/

        /* **************************************************************** FIRMA ******************************************************************************/
        def strFiscalizador = planillasService.nombrePersona(plnl.fiscalizador, 'pers') + "\n                FISCALIZADOR"
        document.add(new Paragraph(" ", fontTd))
        document.add(new Paragraph(" ", fontTd))
        document.add(new Paragraph(" ", fontTd))
        document.add(new Paragraph(" ", fontTd))
        document.add(new Paragraph("_________________________________________", fontTd))
//        document.add(new Paragraph("           FIRMA FISCALIZADOR", fontTd))

        document.add(new Paragraph(strFiscalizador, fontTd))
//        addCellTabla(tablaClima, new Paragraph(valT, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        /* **************************************************************** FIN FIRMA ******************************************************************************/

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }



    def reporteAvanceTotal() {
        println "reporteAvanceTotal: $params"
        def cn = dbConnectionService.getConnection()
        def plnl = Planilla.get(params.plnl)

        def monto = plnl.contrato.monto
        def cmpl = Contrato.findByPadre(plnl.contrato)
        monto += cmpl.monto

        if (!params.id) {
            flash.message = "No se puede mostrar el reporte de avance sin seleccionar un contrato."
            redirect(action: "errores")
            return
        }

        def contrato = plnl.contrato
        def avanceContrato

        def modificaciones = Modificaciones.findAllByContrato(contrato)

        avanceContrato = Avance.findByContratoAndPlanilla(contrato, plnl)

        def obra = contrato.obra

        def tipoP = TipoPlanilla.findByCodigo("P")
        def tipoQ = TipoPlanilla.findByCodigo("Q")
        def tipoD = TipoPlanilla.findByCodigo("D")
        def tipos = [tipoQ, tipoP, tipoD]


        def planillasAvance = Planilla.findAllByContratoAndTipoPlanillaInListAndFechaFinLessThanEquals(contrato,tipos, plnl.fechaFin, [sort: 'fechaFin'])
//        def plnlAvanceCmpl = Planilla.findAllByContratoAndTipoPlanillaInListAndFechaFinLessThanEquals(contrato,tipos, plnl.fechaFin, [sort: 'fechaFin'])


        def planillasCosto = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("C"))
            eq("padreCosto", plnl)
        }

        def planillaAnticipo = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
        }[0]
        def antcCmpl = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("B"))
        }[0]

        def prej = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaInicio', order: "asc"])

        def anticipoDescontado = planillasAvance.sum { it.descuentos } ?: 0
        def prctAnticipo = 100 * anticipoDescontado / (contrato.anticipo + cmpl.anticipo);

        def detalles = VolumenContrato.findAllByObra(obra, [sort: "volumenOrden"])
        def crej = CrngEjecucionObra.withCriteria {
            inList("volumenObra", detalles)
            if(plnl.tipoPlanilla.id != tipoQ.id) {
                periodo {
                    le("fechaFin", plnl.fechaFin)
                }
            }
        }

        def inversionProgramada = crej.sum { it.precio } ?: 0
        def inversionReal = planillasAvance.sum { it.valor } ?: 0


        def tx = "select sum(plnlmnto) suma from plnl where cntr__id = ${plnl.contrato.id} and tppl__id = 5 and " +
                "plnlfcfn <= '${plnl.fechaFin}'"

        def costoPorcentaje = cn.rows(tx.toString())[0]?.suma ?: 0

        tx = "select sum(mlplmnto) suma from mlpl where plnl__id in (select plnl__id from plnl " +
                "where cntr__id = ${plnl.contrato.id} and tppl__id in (3,9,4) and plnlfcfn <= '${plnl.fechaFin}')"

        def multas = cn.rows(tx.toString())[0].suma + plnl.multaEspecial?:0

//        println "sum: $tx"
        def baos = new ByteArrayOutputStream()
        def name = "avance_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)
        Font fontTitle = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        def formatoFechasTabla = "dd-MM-yyyy"

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.scaleToFit(52, 52)
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        Document document
        document = new Document(PageSize.A4);
        def pdfw = PdfWriter.getInstance(document, baos);

        document.resetHeader()
        document.resetFooter()

        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Avance de la obra " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
        preface.add(new Paragraph("AVANCE DE LA OBRA " + obra.nombre + " AL " + fechaConFormato(contrato?.fechaPedidoRecepcionFiscalizador, "dd MMMM yyyy").toUpperCase(), fontTituloGad));
        addEmptyLine(preface, 1);
        Paragraph preface2 = new Paragraph();
        preface2.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
        addEmptyLine(preface2, 1);
        document.add(logo)
        document.add(preface);
        document.add(preface2);

        /* ********************************** GENERALIDADES ************************************/
        PdfPTable tablaGeneralidades = new PdfPTable(2);
        tablaGeneralidades.setWidthPercentage(100);
        tablaGeneralidades.setWidths(arregloEnteros([35, 65]))

        addCellTabla(tablaGeneralidades, new Paragraph("1.- GENERALIDADES", fontTitle), [padding: 3, pb: 5, border: Color.WHITE, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tablaGeneralidades, new Paragraph("OBRA", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(obra.nombre, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("LUGAR", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(obra.sitio, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("UBICACIÓN", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph("PARROQUIA " + obra.parroquia.nombre + " CANTÓN " + obra.parroquia.canton.nombre, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("CONTRATISTA", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(contrato.oferta.proveedor.nombre, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("MONTO DEL CONTRATO PRINCIPAL" + '$.', fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(numero(contrato.monto, 2), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("NÚMERO CONTRATO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(contrato.codigo, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("MONTO DEL CONTRATO COMPLEMENTARIO" + '$.', fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(numero(cmpl.monto, 2), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("NÚMERO CONTRATO COMPLEMENTARIO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(cmpl.codigo, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("MODALIDAD", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(contrato.tipoContrato.descripcion + " y Complementario", fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("TIPO DE OBRA", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(obra.tipoObjetivo.descripcion, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("OBJETO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(contrato.objeto, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("FISCALIZADOR", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(nombrePersona(planillasAvance.size() > 0 ? planillasAvance.last().fiscalizador : null), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("FECHA DE SUSCRIPCIÓN", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(fechaConFormato(contrato.fechaSubscripcion, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaGeneralidades)
        /* **************************************** FIN GENERALIDADES ************************************/
        /* **************************************** DETALLE PLAZOS ***************************************/
        PdfPTable tablaDetalle = new PdfPTable(4);
        tablaDetalle.setWidthPercentage(100);
        tablaDetalle.setWidths(arregloEnteros([35, 25, 15, 25]))
        tablaDetalle.setSpacingBefore(5f);

        addCellTabla(tablaDetalle, new Paragraph("2.- DETALLE DE PLAZOS", fontTitle), [padding: 3, pb: 5, border: Color.WHITE, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])

        addCellTabla(tablaDetalle, new Paragraph("PLAZO CONTRACTUAL PRINCIPAL", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(numero(contrato.plazo - cmpl.plazo, 0) + " DÍAS CALENDARIO", fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])

        addCellTabla(tablaDetalle, new Paragraph("PLAZO CONTRATO COMPLEMENTARIO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(numero(cmpl.plazo, 0) + " DÍAS CALENDARIO", fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])

        addCellTabla(tablaDetalle, new Paragraph("FECHA TRÁMITE ANTICIPO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(planillaAnticipo.fechaOficioEntradaPlanilla, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(planillaAnticipo.oficioEntradaPlanilla, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDetalle, new Paragraph("FECHA ENTREGA ANTICIPO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(planillaAnticipo.fechaMemoPagoPlanilla, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(planillaAnticipo.memoPagoPlanilla, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

/*
        addCellTabla(tablaDetalle, new Paragraph("ANTICIPO COMPLEMENTARIO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(antcCmpl.fechaMemoPagoPlanilla, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(planillaAnticipo.memoPagoPlanilla, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
*/

        addCellTabla(tablaDetalle, new Paragraph("FECHA INICIO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(prej.first().fechaInicio, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(obra.memoInicioObra, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])


        addCellTabla(tablaDetalle, new Paragraph("FECHA VENCIMIENTO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(prej.first().fechaInicio + contrato?.plazo?.toInteger() -1, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])

        def totalDias = 0

        if(modificaciones.size() > 0){
        modificaciones.each {mod ->
            totalDias += mod.dias;
            if(mod.tipo == 'A'){
                addCellTabla(tablaDetalle, new Paragraph("AMPLIACIÓN DE PLAZO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalle, new Paragraph(numero(mod?.dias,0) + " DÍAS", fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalle, new Paragraph(mod?.memo, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            }
            if(mod.tipo == 'S'){
                addCellTabla(tablaDetalle, new Paragraph("SUSPENSIÓN", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalle, new Paragraph(numero(mod?.dias,0) + " DÍAS", fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalle, new Paragraph(mod?.memo, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            }
        }
            addCellTabla(tablaDetalle, new Paragraph("NUEVA FECHA VENCIMIENTO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(prej.last().fechaFin, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])

        }

        document.add(tablaDetalle)
        /* ********************************** FIN DETALLE PLAZOS ****************************************/
        /* ************************************** EVALUACION ********************************************/
        PdfPTable tablaEvaluacion = new PdfPTable(3);

        tablaEvaluacion.setWidthPercentage(100);
        tablaEvaluacion.setWidths(arregloEnteros([35, 33, 32]))
        tablaEvaluacion.setSpacingBefore(5f);

        addCellTabla(tablaEvaluacion, new Paragraph("3.- EVALUACIÓN DEL AVANCE FÍSICO", fontTitle), [padding: 3, pb: 5, border: Color.WHITE, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])

        addCellTabla(tablaEvaluacion, new Paragraph("VALOR DEL ANTICIPO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(numero(contrato.anticipo + cmpl.anticipo, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(numero(contrato.porcentajeAnticipo, 2) + '%', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEvaluacion, new Paragraph("ANTICIPO DESCONTADO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(numero(anticipoDescontado, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(numero(prctAnticipo, 2) + '%', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEvaluacion, new Paragraph("AVANCE FÍSICO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(numero(planillasAvance.last().avanceFisico, 2), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(' ', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaEvaluacion)
        /* ************************************ FIN EVALUACION ******************************************/
        /* ************************************** ECONOMICO *********************************************/
        PdfPTable tablaEconomico = new PdfPTable(2);

        tablaEconomico.setWidthPercentage(100);
        tablaEconomico.setWidths(arregloEnteros([60, 40]))
        tablaEconomico.setSpacingBefore(5f);

        addCellTabla(tablaEconomico, new Paragraph("4.- AVANCE ECONÓMICO", fontTitle), [padding: 3, pb: 5, border: Color.WHITE, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tablaEconomico, new Paragraph("INVERSION PROGRAMADA ACUMULADA", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEconomico, new Paragraph(numero(inversionProgramada, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEconomico, new Paragraph("INVERSION REAL ACUMULADA", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEconomico, new Paragraph(numero(inversionReal, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEconomico, new Paragraph("VALOR POR COSTO + PORCENTAJE ACUMULADO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEconomico, new Paragraph(numero(costoPorcentaje, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEconomico, new Paragraph("MULTAS ACUMULADAS", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEconomico, new Paragraph(numero(multas, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaEconomico)
        /* ******************************* FIN ECONOMICO *******************************************/
        /* ********************************** RESUMEN **********************************************/
        PdfPTable tablaResumen = new PdfPTable(1);
        tablaResumen.setWidthPercentage(100);
        tablaResumen.setSpacingBefore(5f);
//        document.newPage();
        addCellTabla(tablaResumen, new Paragraph(" ", fontTitle), [padding: 3, pb: 5, border: Color.WHITE, bg: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaResumen, new Paragraph("5.- RESUMEN DE DECISIONES IMPORTANTES DE AVANCE DE OBRA O ACTIVIDADES REALIZADAS EN ESTE PERIODO", fontTitle), [padding: 3, pb: 5, border: Color.WHITE, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        document.add(tablaResumen)

        def frases = []

        frases = FraseClima.findAllByAvance(avanceContrato, [sort: "fecha"])
//        println "frases ok, $avanceContrato.id, $frases"

        if (avanceContrato) {
            def titulos = [
                    "A.- Resultados de los ensayos de materiales",
                    "B.- Análisis de la cantidad y calidad de los equipos y maquinaria en obra",
                    "C.- Cuadro de las condiciones climáticas del sitio de la obra",
                    "D.- Detalle de la correspondencia intercambiada con el contratista",
                    "E.- Análisis del personal técnico del contratista",
                    "F.- Actividades más importantes del período",
                    "G.- Seguridad industrial y personal",
                    "H.- Cumplimiento de especificaciones técnicas",
                    "I.- Decisiones importantes",
                    "J.- Detalle de reuniones",
                    "K.- Visitas programadas a la obra para el período",
                    "L.- Visitas efectuadas por la fiscalización en el período",
                    "Observaciones",
                    "Conclusiones y recomendaciones"
            ]
            def dateFormat = new SimpleDateFormat("EEEE dd-MM-yyyy", new Locale("es"))
            titulos.eachWithIndex { t, i ->
                document.add(new Paragraph(t, fontTitle))
                if (i == 2) {
                    //lo del clima
                    def dia = plnl.fechaInicio
                    def fin = plnl.fechaFin
                    def suspension = cn.rows("select prejfcin, prejfcfn from prej where cntr__id = ${contrato.id} and prejtipo = 'S'".toString())[0]
                    def tablaClima = new PdfPTable(3);
                    tablaClima.setWidths(arregloEnteros([40, 30, 30]))
                    tablaClima.setWidthPercentage(50);
                    tablaClima.setSpacingBefore(5f);

                    addCellTabla(tablaClima, new Paragraph('Día', fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaClima, new Paragraph('Mañana', fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaClima, new Paragraph('Tarde', fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

                    while (dia <= fin) {
                        if(dia >= suspension?.prejfcin && dia <= suspension?.prejfcfn) {
//                            println "dia: $dia es suspensión"
                        } else {
                            def valM = "", valT = ""
                            if (frases.size() > 0) {
                                def fr = frases.find { it.fecha == dia }
                                valM = fr ? fr.manana : ""
                                valT = fr ? fr.tarde : ""
                            }
                            addCellTabla(tablaClima, new Paragraph(dateFormat.format(dia).capitalize(), fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                            addCellTabla(tablaClima, new Paragraph(valM, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                            addCellTabla(tablaClima, new Paragraph(valT, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                        }
                        dia++
                    }
                    document.add(tablaClima)
                } //if i==2: lo del clima
                else {
                    def num = (i + 1).toString().padLeft(2, "0")
                    def val = avanceContrato["frase" + num]
                    document.add(new Paragraph(val, fontTd))
                }// if i!= 2: lo q no es el clima
            }
        }

        /* **************************** FIN RESUMEN ************************************/

        /* ******************************* FIRMA ***************************************/
        def strFiscalizador = planillasService.nombrePersona(plnl.fiscalizador, 'pers') + "\n                FISCALIZADOR"
        document.add(new Paragraph(" ", fontTd))
        document.add(new Paragraph(" ", fontTd))
        document.add(new Paragraph(" ", fontTd))
        document.add(new Paragraph(" ", fontTd))
        document.add(new Paragraph("_________________________________________", fontTd))
        document.add(new Paragraph(strFiscalizador, fontTd))
        /* ***************************** FIN FIRMA *************************************/

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }


    def reporteObrasAdicionales() {

        def tipoLiquidacion = TipoPlanilla.findByCodigo("Q")

        def contrato = Contrato.get(params.contrato)

        def plnl = Planilla.findByContratoAndTipoPlanilla(contrato,tipoLiquidacion)

        if (!params.id) {
            flash.message = "No se puede mostrar el reporte sin seleccionar un contrato."
            redirect(action: "errores")
            return
        }

        def avanceContrato
        def modificaciones = Modificaciones.findAllByContrato(contrato)

//        println("Modificaciones " + modificaciones)

        avanceContrato = Avance.findByContratoAndPlanilla(contrato, plnl)

        def obra = contrato.obra

        def tipoP = TipoPlanilla.findByCodigo("P")
        def tipoQ = TipoPlanilla.findByCodigo("Q")
        def tipoD = TipoPlanilla.findByCodigo("D")
        def tipoO = TipoPlanilla.findByCodigo("O")
        def tipoC = TipoPlanilla.findByCodigo("C")
        def tipos = [tipoQ, tipoP, tipoD]


        def planillasAvance = Planilla.findAllByContratoAndTipoPlanillaInListAndFechaFinLessThanEquals(contrato,tipos, plnl.fechaFin)


        def planillasCosto = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("C"))
            le("fechaFin", plnl.fechaFin)
        }

        def plncto = Planilla.findByContratoAndTipoPlanilla(contrato,tipoC)

        println("costo " + plncto)

        def planillaAnticipo = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
        }[0]

        //planilla obras adicionales

        def planillaObrasAdicionales = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("O"))
        }

        def planoa = Planilla.findByContratoAndTipoPlanilla(contrato,tipoO)

        def prej = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaInicio', order: "asc"])

        def anticipoDescontado = planillasAvance.sum { it.descuentos } ?: 0
        def prctAnticipo = 100 * anticipoDescontado / contrato.anticipo;

        def detalles = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def crej = CronogramaEjecucion.withCriteria {
            inList("volumenObra", detalles)
            periodo {
                le("fechaFin", plnl.fechaFin)
            }
        }

        def inversionProgramada = crej.sum { it.precio } ?: 0
        def inversionReal = planillasAvance.sum { it.valor } ?: 0
        def costoPorcentaje = planillasCosto.sum { it.valor } ?: 0
        def multas = planillasAvance.sum { it.multaRetraso + it.multaPlanilla } ?: 0

        def baos = new ByteArrayOutputStream()
        def name = "avance_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)
        Font fontTitle = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        def formatoFechasTabla = "dd-MM-yyyy"

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.scaleToFit(52, 52)
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        Document document
        document = new Document(PageSize.A4);
        def pdfw = PdfWriter.getInstance(document, baos);

        document.resetHeader()
        document.resetFooter()

        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Avance de la obra " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
//        preface.add(new Paragraph("AVANCE DE LA OBRA " + obra.nombre + " AL " + fechaConFormato(plnl.fechaFin, "dd MMMM yyyy").toUpperCase(), fontTituloGad));
        preface.add(new Paragraph("AVANCE DE LA OBRA " + obra.nombre + " AL " + fechaConFormato(contrato.fechaPedidoRecepcionFiscalizador, "dd MMMM yyyy").toUpperCase(), fontTituloGad));
        addEmptyLine(preface, 1);
        Paragraph preface2 = new Paragraph();
        preface2.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
        addEmptyLine(preface2, 1);
        document.add(logo)
        document.add(preface);
        document.add(preface2);

        /* **************************************************************** GENERALIDADES ******************************************************************************/
        PdfPTable tablaGeneralidades = new PdfPTable(2);
        tablaGeneralidades.setWidthPercentage(100);
        tablaGeneralidades.setWidths(arregloEnteros([35, 65]))

        addCellTabla(tablaGeneralidades, new Paragraph("1.- GENERALIDADES", fontTitle), [padding: 3, pb: 5, border: Color.WHITE, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tablaGeneralidades, new Paragraph("OBRA", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(obra.nombre, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("LUGAR", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(obra.sitio, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("UBICACIÓN", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph("PARROQUIA " + obra.parroquia.nombre + " CANTÓN " + obra.parroquia.canton.nombre, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("CONTRATISTA", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(contrato.oferta.proveedor.nombre, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("MONTO DEL CONTRATO " + '$.', fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(numero(contrato.monto, 2), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("NÚMERO CONTRATO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(contrato.codigo, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("MODALIDAD", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(contrato.tipoContrato.descripcion, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("TIPO DE OBRA", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(obra.tipoObjetivo.descripcion, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("OBJETO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(contrato.objeto, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("FISCALIZADOR", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(nombrePersona(planillasAvance.size() > 0 ? planillasAvance.last().fiscalizador : null), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaGeneralidades, new Paragraph("FECHA DE SUSCRIPCIÓN", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaGeneralidades, new Paragraph(fechaConFormato(contrato.fechaSubscripcion, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaGeneralidades)
        /* **************************************************************** FIN GENERALIDADES **************************************************************************/
        /* **************************************************************** DETALLE PLAZOS ******************************************************************************/
        PdfPTable tablaDetalle = new PdfPTable(4);
        tablaDetalle.setWidthPercentage(100);
        tablaDetalle.setWidths(arregloEnteros([35, 25, 15, 25]))
        tablaDetalle.setSpacingBefore(5f);

        addCellTabla(tablaDetalle, new Paragraph("2.- DETALLE DE PLAZOS", fontTitle), [padding: 3, pb: 5, border: Color.WHITE, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])

        addCellTabla(tablaDetalle, new Paragraph("PLAZO CONTRACTUAL", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(numero(contrato.plazo, 0) + " DÍAS CALENDARIO", fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])

        addCellTabla(tablaDetalle, new Paragraph("FECHA TRÁMITE ANTICIPO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(planillaAnticipo.fechaOficioEntradaPlanilla, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(planillaAnticipo.oficioEntradaPlanilla, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDetalle, new Paragraph("FECHA ENTREGA ANTICIPO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(planillaAnticipo.fechaMemoPagoPlanilla, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(planillaAnticipo.memoPagoPlanilla, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDetalle, new Paragraph("FECHA INICIO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(prej.first().fechaInicio, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(obra.memoInicioObra, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])


        addCellTabla(tablaDetalle, new Paragraph("FECHA VENCIMIENTO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(prej.first().fechaInicio + contrato?.plazo.toInteger(), formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])

        def totalDias = 0

        if(modificaciones.size() > 0){
            modificaciones.each {mod ->
                totalDias += mod.dias;
                if(mod.tipo == 'A'){
                    addCellTabla(tablaDetalle, new Paragraph("AMPLIACIÓN DE PLAZO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalle, new Paragraph(numero(mod?.dias,0) + " DÍAS", fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalle, new Paragraph(mod?.memo, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                }
                if(mod.tipo == 'S'){
                    addCellTabla(tablaDetalle, new Paragraph("SUSPENSIÓN", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalle, new Paragraph(numero(mod?.dias,0) + " DÍAS", fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalle, new Paragraph("MM. N.", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalle, new Paragraph(mod?.memo, fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                }
            }
            addCellTabla(tablaDetalle, new Paragraph("NUEVA FECHA VENCIMIENTO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDetalle, new Paragraph(fechaConFormato(prej.last().fechaFin, formatoFechasTabla), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])

        }

        document.add(tablaDetalle)
        /* **************************************************************** FIN DETALLE PLAZOS **************************************************************************/
        /* **************************************************************** EVALUACION ******************************************************************************/
        PdfPTable tablaEvaluacion = new PdfPTable(3);

        tablaEvaluacion.setWidthPercentage(100);
        tablaEvaluacion.setWidths(arregloEnteros([35, 33, 32]))
        tablaEvaluacion.setSpacingBefore(5f);

        addCellTabla(tablaEvaluacion, new Paragraph("3.- EVALUACIÓN DEL AVANCE FÍSICO", fontTitle), [padding: 3, pb: 5, border: Color.WHITE, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 3])

        addCellTabla(tablaEvaluacion, new Paragraph("VALOR DEL ANTICIPO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(numero(contrato.anticipo, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(numero(contrato.porcentajeAnticipo, 2) + '%', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEvaluacion, new Paragraph("ANTICIPO DESCONTADO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(numero(anticipoDescontado, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(numero(prctAnticipo, 2) + '%', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEvaluacion, new Paragraph("AVANCE FÍSICO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(numero(planillasAvance.last().avanceFisico, 2), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEvaluacion, new Paragraph(' ', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaEvaluacion)
        /* **************************************************************** FIN EVALUACION **************************************************************************/
        /* **************************************************************** ECONOMICO ******************************************************************************/
        PdfPTable tablaEconomico = new PdfPTable(2);

        tablaEconomico.setWidthPercentage(100);
        tablaEconomico.setWidths(arregloEnteros([60, 40]))
        tablaEconomico.setSpacingBefore(5f);

        addCellTabla(tablaEconomico, new Paragraph("4.- AVANCE ECONÓMICO", fontTitle), [padding: 3, pb: 5, border: Color.WHITE, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])

        addCellTabla(tablaEconomico, new Paragraph("INVERSION PROGRAMADA ACUMULADA", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEconomico, new Paragraph(numero(inversionProgramada, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEconomico, new Paragraph("INVERSION REAL ACUMULADA", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEconomico, new Paragraph(numero(inversionReal, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEconomico, new Paragraph("VALOR POR COSTO + PORCENTAJE ACUMULADO", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEconomico, new Paragraph(numero(plncto?.valor, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEconomico, new Paragraph("VALOR DE OBRAS ADICIONALES CONTRACTUALES", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEconomico, new Paragraph(numero(planoa?.valor, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaEconomico, new Paragraph("MULTAS ACUMULADAS", fontTh), [pl: 20, border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaEconomico, new Paragraph(numero(multas, 2) + ' $', fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 0.1, bcr: Color.WHITE, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaEconomico)
        /* **************************************************************** FIN ECONOMICO **************************************************************************/


        /* **************************************************************** FIRMA ******************************************************************************/
        document.add(new Paragraph(" ", fontTd))
        document.add(new Paragraph(" ", fontTd))
        document.add(new Paragraph(" ", fontTd))
        document.add(new Paragraph(" ", fontTd))
        document.add(new Paragraph("_________________________________________", fontTd))
        document.add(new Paragraph("           FIRMA FISCALIZADOR", fontTd))
        /* **************************************************************** FIN FIRMA ******************************************************************************/


        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def reportePlanillaLiquidacion() {
        println("entro planillas liquidarcion")
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
            or {
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
            }
            order("id", "asc")
//            order("fechaIncio", "desc")
        }
//        println planillasAnteriores.id
        def valorObra = planillasAnteriores.sum { it.valor }
        def pa = PeriodoPlanilla.findAllByPlanilla(planilla)
        def periodos = PeriodoPlanilla.findAllByPlanillaInList(planillasAnteriores, [sort: "fechaIncio", order: "desc"])
//        def periodos = PeriodoPlanilla.findAllByPlanillaInList(planillasAnteriores, [sort: "id"])
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

        def obraLiquidacion = Obra.withCriteria {
            eq("liquidacion", 1)
            eq("codigo", obra.codigo + "LQ")
        }
        if (obraLiquidacion.size() == 0) {
            println "error 1"
            flash.message = "No se encontró la obra de liquidación"

            def url = g.createLink(controller: "planilla", action: "list", id: contrato.id)
            def link = "<a href='${url}' class='btn btn-danger'>Regresar</a>"

            redirect(action: "errores", params: [link: link])
            return
        } else if (obraLiquidacion.size() > 1) {
            println "error 2"
            flash.message = "Se encontró más de una obra de liquidación"
            def url = g.createLink(controller: "planilla", action: "list", id: contrato.id)
            def link = "<a href='${url}' class='btn btn-danger'>Regresar</a>"

            redirect(action: "errores", params: [link: link])
            return
        }
        obraLiquidacion = obraLiquidacion[0]
//        def cs = FormulaPolinomicaContractual.findAllByContratoAndNumeroIlike(contrato, "C%", [sort: "numero"])

        def cs = FormulaPolinomica.withCriteria {
            eq("obra", obraLiquidacion)
            ilike("numero", "C%")
//                gt("valor", 0)
            order("numero")
        }

//        def ps = FormulaPolinomicaContractual.withCriteria {
//            and {
//                eq("contrato", contrato)
//                and {
//                    ne("numero", "P0")
//                    ilike("numero", "p%")
//                }
//                order("numero", "asc")
//            }
//        }

        def ps = FormulaPolinomica.withCriteria {
            and {
                eq("obra", obraLiquidacion)
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

        def pcs = FormulaPolinomica.withCriteria {
            and {
                eq("obra", obraLiquidacion)
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
        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
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
            def administrador = contrato.administrador

//            def strContratista = cap(contratista.titulo + " " + contratista.nombreContacto + " " + contratista.apellidoContacto + "\nContratista")
            def strContratista = nombrePersona(contratista, "prov") + "\nContratista"
            def strFiscalizador = nombrePersona(fiscalizador) + "\nFiscalizador"
            def strSubdirector = nombrePersona(subdirector) + "\nSubdirector"
            def strAdmin = nombrePersona(administrador) + "\nAdministrador"
            def strFechaPresentacion = fechaConFormato(planilla.fechaPresentacion, "dd-MMM-yyyy") + "\nFecha de presentación"
            def strFechaAprobacion = "\nFecha de aprobación"

            if (params.tipo == "detalle") {

                fontThFirmas = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
                fontTdFirmas = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);

                tablaFirmas.setWidths(arregloEnteros([35, 30, 35]))
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 35, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaFirmas, new Paragraph(strContratista, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph(strFiscalizador, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaFirmas, new Paragraph(strFechaPresentacion, fontTdFirmas), [height: 45, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_TOP])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [height: 45, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph(strFechaAprobacion, fontTdFirmas), [height: 45, bwb: 1, bcb: Color.BLACK, border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_TOP])

                addCellTabla(tablaFirmas, new Paragraph(strSubdirector, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph("", fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFirmas, new Paragraph(strAdmin, fontThFirmas), [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            } else if (params.tipo == "otro") {
                if (params.orientacion == "horizontal") {
                    tablaFirmas.setWidths(arregloEnteros([40, 20, 40]))
                } else if (params.orientacion == "vertical") {
                    tablaFirmas.setWidths(arregloEnteros([25, 50, 25]))
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
        logo.scaleToFit(52, 52)
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        /* ***************************************************** Titulo del reporte *******************************************************/
//        Paragraph preface = new Paragraph();
//        addEmptyLine(preface, 1);
//        preface.setAlignment(Element.ALIGN_CENTER);
//        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", catFont));
//        preface.add(new Paragraph("PLANILLA DE ${planilla.tipoPlanilla.nombre.toUpperCase()} DE LA OBRA " + obra.nombre, catFont));
//        addEmptyLine(preface, 1);
//        Paragraph preface2 = new Paragraph();
//        preface2.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
//        addEmptyLine(preface2, 1);
//        document.add(preface);
//        document.add(preface2);
        /* ***************************************************** Fin Titulo del reporte ***************************************************/

        /* ***************************************************** Header planilla **********************************************************/

        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        def prmsTdBorder = [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsNmBorder = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def headerPlanilla = { params ->

            Font fontThUsar = new Font(Font.TIMES_ROMAN, params.size, Font.BOLD);
            Font fontTdUsar = new Font(Font.TIMES_ROMAN, params.size, Font.NORMAL);

            Paragraph preface = new Paragraph();
            addEmptyLine(preface, 1);
            preface.setAlignment(Element.ALIGN_CENTER);
            preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
            preface.add(new Paragraph("PLANILLA DE ${planilla.tipoPlanilla.nombre.toUpperCase()} DE LA OBRA " + obra.nombre, fontTituloGad));
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

//        PdfPTable tablaHeaderPlanilla = new PdfPTable(5);
//        tablaHeaderPlanilla.setWidthPercentage(100);
//        tablaHeaderPlanilla.setWidths(arregloEnteros([12, 24, 10, 12, 24]))
//        tablaHeaderPlanilla.setWidthPercentage(100);
//
//        addCellTabla(tablaHeaderPlanilla, new Paragraph("Obra", fontTh), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph(obra.nombre, fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
//
//        addCellTabla(tablaHeaderPlanilla, new Paragraph("Lugar", fontTh), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph((obra.lugar?.descripcion ?: ""), fontTd), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTh), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph("Planilla", fontTh), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph(planilla.numero, fontTd), prmsTdNoBorder)
//
//        addCellTabla(tablaHeaderPlanilla, new Paragraph("Ubicación", fontTh), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph(obra.parroquia?.nombre + " - Cantón " + obra.parroquia?.canton?.nombre, fontTd), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTh), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph("Monto contrato", fontTh), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(planilla.contrato.monto, 2), fontTd), prmsTdNoBorder)
//
//        addCellTabla(tablaHeaderPlanilla, new Paragraph("Contratista", fontTh), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph(planilla.contrato.oferta.proveedor.nombre, fontTd), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTh), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph("Periodo", fontTh), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph(periodoPlanilla, fontTd), prmsTdNoBorder)
//
//        addCellTabla(tablaHeaderPlanilla, new Paragraph("Plazo", fontTh), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph(numero(planilla.contrato.plazo, 0) + " días", fontTd), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTh), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTh), prmsTdNoBorder)
//        addCellTabla(tablaHeaderPlanilla, new Paragraph("", fontTd), prmsTdNoBorder)
//
//        document.add(tablaHeaderPlanilla);
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
            if (c.valor > 0) {
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

                    def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${per.periodoLiquidacion.id} and formulaPolinomicaLiq=${c.id} and planillaLiq = ${per.planilla.id}")
                    if (vlrj.size() > 0) {
                        valor = vlrj.pop().valor
                    } else {
                        println "error wtf no hay vlrj => from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${per.periodoLiquidacion.id} and formulaPolinomicaLiq=${c.id} and planillaLiq = ${per.planilla.id}"
                        valor = -1
                    }
                    addCellTabla(tablaB0, new Paragraph(numero(valor), fontTd), [border: Color.BLACK, bcl: Color.WHITE, bwl: 1, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                }
            }
        }

        addCellTabla(tablaB0, new Paragraph("TOTALES", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaB0, new Paragraph(numero(totalCoef), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        periodos.each { per ->
            addCellTabla(tablaB0, new Paragraph(numero(per.totalLiq), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
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
//        document.add(tablaHeaderPlanilla);

        headerPlanilla([size: 8])
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

        def planillaAnterior, reajusteAnterior, reajustePlanillar

        if (periodos.size() > 2) {
            planillaAnterior = planillasAnteriores.get(planillasAnteriores.size() - 1)
//            println "reporte " + planillaAnterior
            reajusteAnterior = (planillaAnterior.reajusteLiq).toDouble().round(2)
            addCellTabla(tablaFr, new Paragraph("REAJUSTE ANTERIOR", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
            addCellTabla(tablaFr, new Paragraph(numero(reajusteAnterior, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])
            addCellTabla(tablaFr, new Paragraph("REAJUSTE A PLANILLAR", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
            addCellTabla(tablaFr, new Paragraph(numero(reajusteTotal - reajusteAnterior, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])
        }

        document.add(tablaFr);
        printFirmas([tipo: "otro", orientacion: "vertical"])
        /* ***************************************************** Fin Tabla Fr *************************************************************/

        /* ***************************************************** Multa no presentacion ****************************************************/
        if (planilla.tipoPlanilla.codigo != "A") {
//            Paragraph tituloMultas = new Paragraph();
//            addEmptyLine(tituloMultas, 1);
//            tituloMultas.setAlignment(Element.ALIGN_CENTER);
//            tituloMultas.add(new Paragraph("Multas", fontTitle));
//            document.add(tituloMultas);

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

//            PdfPTable tablaDetalles = new PdfPTable(11);
//            tablaDetalles.setWidthPercentage(100);
//            tablaDetalles.setWidths(arregloEnteros([12, 35, 5, 11, 11, 11, 11, 11, 11, 11, 11]))
//
//            def borderWidth = 1
//
//            addCellTabla(tablaDetalles, new Paragraph("Obra", fontTh), prmsTdNoBorder)
//            addCellTabla(tablaDetalles, new Paragraph(obra.nombre, fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 10])
//
//            addCellTabla(tablaDetalles, new Paragraph("Lugar", fontTh), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph((obra.lugar?.descripcion ?: ""), fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
//            addCellTabla(tablaDetalles, new Paragraph("Planilla", fontTh), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph(planilla.numero, fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
//            addCellTabla(tablaDetalles, new Paragraph(" ", fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//
//            addCellTabla(tablaDetalles, new Paragraph("Ubicación", fontTh), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph("Parroquia " + obra.parroquia?.nombre + " Cantón " + obra.parroquia?.canton?.nombre, fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
//            addCellTabla(tablaDetalles, new Paragraph("Monto", fontTh), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph(numero(planilla.contrato.monto, 2), fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
//            addCellTabla(tablaDetalles, new Paragraph(" ", fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//
//            addCellTabla(tablaDetalles, new Paragraph("Contratista", fontTh), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph(planilla.contrato.oferta.proveedor.nombre, fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
//            addCellTabla(tablaDetalles, new Paragraph("Periodo", fontTh), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph(periodoPlanilla, fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
//            addCellTabla(tablaDetalles, new Paragraph(" ", fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//
//            addCellTabla(tablaDetalles, new Paragraph(" ", fontTd), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 11])
//
//
//            addCellTabla(tablaDetalles, new Paragraph("N.", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph("Descripción del rubro", fontTh), [bwr: borderWidth, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph("U.", fontTh), [border: Color.BLACK, bwl: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph("Precio unitario", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, new Paragraph("Vol. contrat.", fontTh), [border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//
//            PdfPTable inner6 = new PdfPTable(3);
//            addCellTabla(inner6, new Paragraph("Cantidades", fontTh), [border: Color.BLACK, bwl: borderWidth, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
//            addCellTabla(inner6, new Paragraph("Ant.", fontTh), [border: Color.BLACK, bwl: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(inner6, new Paragraph("Act.", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(inner6, new Paragraph("Acu.", fontTh), [border: Color.BLACK, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, inner6, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
//
//            PdfPTable inner7 = new PdfPTable(3);
//            addCellTabla(inner7, new Paragraph("Valores", fontTh), [border: Color.BLACK, bwl: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
//            addCellTabla(inner7, new Paragraph("Ant.", fontTh), [border: Color.BLACK, bwl: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(inner7, new Paragraph("Act.", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(inner7, new Paragraph("Acu.", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//            addCellTabla(tablaDetalles, inner7, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])

            /* ************* fin header *****************/

//            addCellTabla(tablaDetalles, new Paragraph(" ", fontTh), [border: Color.BLACK, height: 72, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 11])
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

//            tablaDetalles.setHeaderRows(7);
//            tablaDetalles.setFooterRows(1);
//            tablaDetalles.setHeaderRows(5);
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

                Font fontThFooter = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);

//                addCellTabla(tablaDetalles, new Paragraph("", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 5])
                addCellTabla(tablaDetalles, new Paragraph("TOTAL AVANCE DE OBRA", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                addCellTabla(tablaDetalles, new Paragraph(numero(params.ant, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(params.act, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(params.acu, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                if (params.completo) {

                    def bAnt = planillaAnterior.reajuste
                    def bAct = planilla.reajuste
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
                        println "No hay planillas de cp"
                    } else {
                        println "WTF hay mas de una planilla cp asociada a esta planilla??? "
                        println "PLANILLA: " + planilla.id
                        println "PLANILLAS CP: " + cpPlanilla.id
                    }
                    def cpAcu = cpAnt + cpAct

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

                    def multasAnt = m1 + m2 + m3 + m4
                    def multasAct = 0//totalMultaRetraso + totalMulta
                    def multasAcu = multasAnt + multasAct

                    def totalAnt = cAnt - antAnt - multasAnt
                    def totalAct = cAct - antAct - multasAct
                    def totalAcu = cAcu - antAcu - multasAcu

                    addCellTabla(tablaDetalles, new Paragraph("TOTAL REAJUSTE DE PRECIOS", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                    addCellTabla(tablaDetalles, new Paragraph(numero(bAnt, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(bAct, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(bAcu, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                    addCellTabla(tablaDetalles, new Paragraph("TOTAL RUBROS NO CONTRACTUALES COSTO + %", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                    addCellTabla(tablaDetalles, new Paragraph(numero(cpAnt, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(cpAct, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(cpAcu, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                    addCellTabla(tablaDetalles, new Paragraph("TOTAL PLANILLA REAJUSTADA", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                    addCellTabla(tablaDetalles, new Paragraph(numero(cAnt, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(cAct, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(cAcu, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                    addCellTabla(tablaDetalles, new Paragraph("DESCUENTOS CONTRACTUALES", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 11])

                    addCellTabla(tablaDetalles, new Paragraph("ANTICIPO", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                    addCellTabla(tablaDetalles, new Paragraph(numero(antAnt, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(antAct, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(antAcu, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                    addCellTabla(tablaDetalles, new Paragraph("MULTAS", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
                    addCellTabla(tablaDetalles, new Paragraph(numero(multasAnt, 2, "hide"), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(multasAct, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                    addCellTabla(tablaDetalles, new Paragraph(numero(multasAcu, 2), fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                    addCellTabla(tablaDetalles, new Paragraph("VALOR LIQUIDO A PAGAR", fontThFooter), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 8])
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
                    printHeaderDetalle([pag: currentPag, total: totalPags])
                }
                if (sp != vol.subPresupuestoId) {
                    addCellTabla(tablaDetalles, new Paragraph(vol.subPresupuesto.descripcion, fontThTiny), [height: height, bwr: borderWidth, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
                    addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwl: borderWidth, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwl: borderWidth, bwr: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    addCellTabla(tablaDetalles, new Paragraph(" ", fontThTiny), [height: height, border: Color.BLACK, bwl: borderWidth, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
                    sp = vol.subPresupuestoId
                    currentRows++
                }

                addCellTabla(tablaDetalles, new Paragraph(vol.item.codigo, fontTdTiny), [height: height, border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(vol.item.nombre, fontTdTiny), [height: height, bwr: borderWidth, border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaDetalles, new Paragraph(vol.item.unidad.codigo, fontTdTiny), [height: height, bwl: borderWidth, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(precios[vol.id.toString()], 2, "hide"), fontTdTiny), [height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(vol.cantidad, 2, "hide"), fontTdTiny), [height: height, bwr: borderWidth, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaDetalles, new Paragraph(numero(cantAnt, 2, "hide"), fontTdTiny), [height: height, bwl: borderWidth, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(cant, 2, "hide"), fontTdTiny), [height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(cant + cantAnt, 2, "hide"), fontTdTiny), [height: height, bwr: borderWidth, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

                addCellTabla(tablaDetalles, new Paragraph(numero(valAnt, 2, "hide"), fontTdTiny), [height: height, bwl: borderWidth, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(val, 2, "hide"), fontTdTiny), [height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaDetalles, new Paragraph(numero(val + valAnt, 2, "hide"), fontTdTiny), [height: height, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

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
//            document.add(tablaDetalles);
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
        def centrado  = [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsTablaNum = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.scaleToFit(52, 52)
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
        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
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
        def tabla = new PdfPTable(8);
        tabla.setWidthPercentage(100);
        tabla.setWidths(arregloEnteros([10, 40, 10, 10, 10, 10, 10, 10]))
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(1f);
        tabla.setSpacingBefore(10f);

        addCellTabla(tabla, new Paragraph("Factura N.", fontTh), prmsTablaHead)
        addCellTabla(tabla, new Paragraph("Descripción del rubro", fontTh), prmsTablaHead)
        addCellTabla(tabla, new Paragraph("U.", fontTh), prmsTablaHead)
        addCellTabla(tabla, new Paragraph("Cantidad", fontTh), prmsTablaHead)
        addCellTabla(tabla, new Paragraph("Valor sin IVA", fontTh), prmsTablaHead)
        addCellTabla(tabla, new Paragraph("Valor con IVA", fontTh), prmsTablaHead)
        addCellTabla(tabla, new Paragraph("% de indirectos (" + contrato?.indirectos + "%)", fontTh), prmsTablaHead)
        addCellTabla(tabla, new Paragraph("Valor total", fontTh), prmsTablaHead)

        def total = 0
        def totalSinIva = 0
        def totalConIva = 0
        def totalIndirectos = 0

        detalles.each { det ->
            def tot = det.monto + det.montoIndirectos
            total += tot
            totalSinIva += det.monto
            totalConIva += det.montoIva
            totalIndirectos += det.montoIndirectos

            addCellTabla(tabla, new Paragraph(det.factura, fontTd), prmsTabla)
            addCellTabla(tabla, new Paragraph(det.rubro, fontTd), prmsTabla)
            addCellTabla(tabla, new Paragraph(det.unidad.codigo, fontTd), centrado)
            addCellTabla(tabla, new Paragraph(numero(det.cantidad,2), fontTd), prmsTablaNum)
            addCellTabla(tabla, new Paragraph(numero(det.monto, 2), fontTd), prmsTablaNum)
            addCellTabla(tabla, new Paragraph(numero(det.montoIva, 2), fontTd), prmsTablaNum)
            addCellTabla(tabla, new Paragraph(numero(det.montoIndirectos, 2), fontTd), prmsTablaNum)
            addCellTabla(tabla, new Paragraph(numero(tot, 2), fontTd), prmsTablaNum)
        }
        addCellTabla(tabla, new Paragraph("TOTALES", fontTh), [bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 4])
        addCellTabla(tabla, new Paragraph(numero(totalSinIva, 2), fontTh), [bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph(numero(totalConIva, 2), fontTh), [bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph(numero(totalIndirectos, 2), fontTh), [bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph(numero(total, 2), fontTh), [bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        document.add(tabla)
        /* ************************************************************* FIN TABLA DE DATOS ***************************************************************************/

        /* ************************************************************* TABLAS DE RESUMEN ***************************************************************************/
        def tablaResumen1 = new PdfPTable(2);
        tablaResumen1.setWidthPercentage(100);
        tablaResumen1.setWidths(arregloEnteros([80, 20]))
        tablaResumen1.setSpacingAfter(1f);
        tablaResumen1.setSpacingBefore(10f);

        def totalCostoPorcentaje = totalConIva+totalIndirectos

        addCellTabla(tablaResumen1, new Paragraph("TOTAL OBRAS BAJO LA MODALIDAD COSTO + PORCENTAJE (NO INCLUYE IVA)", fontResumen), [padding: 5, bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaResumen1, new Paragraph(numero(total, 2), fontResumen), [padding: 5, border: 2, bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        document.add(tablaResumen1)

        def tablaResumen2 = new PdfPTable(2);
        tablaResumen2.setWidthPercentage(100);
        tablaResumen2.setWidths(arregloEnteros([80, 20]))
        tablaResumen2.setSpacingAfter(1f);
        tablaResumen2.setSpacingBefore(10f);

        def porcentaje = total / contrato.monto

        addCellTabla(tablaResumen2, new Paragraph("% TOTAL OBRAS ADICIONALES NO CONTRACTUALES", fontResumen), [padding: 5, bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaResumen2, new Paragraph(numero(porcentaje*100, 2) + " %", fontResumen), [padding: 5, border: 2, bg: Color.LIGHT_GRAY, border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        document.add(tablaResumen2)
        /* ************************************************************* FIN TABLAS DE RESUMEN ***************************************************************************/

        /* ************************************************************* TABLA DE FIRMAS ***************************************************************************/
        def tablaFirmas = new PdfPTable(3);
        tablaFirmas.setWidthPercentage(100);

        def fiscalizador = planilla.fiscalizador
        def strFiscalizador = nombrePersona(fiscalizador) + "\nFiscalizador"
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

    def memoPedidoPago() {
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def costo = Planilla.findByPadreCosto(planilla)?.valor?:0

        def ok = true
        def str = ""

        def texto = Pdfs.findAllByPlanilla(planilla)
        if (texto.size() == 0) {
            redirect(controller: "planilla", action: "configPedidoPago", id: planilla.id)
            return
        } else if (texto.size() > 1) {
            str += "<li>Se encontraron ${texto.size()} textos. No se pudo generar el pdf.</li>"
            ok = false
        } else {
            texto = texto.first()
        }

        if (!ok) {
            flash.message = "<ul>" + str + "</ul>"

            def url = g.createLink(controller: "planilla", action: "list", id: contrato.id)
            def link = "<a href='${url}' class='btn btn-danger'>Regresar</a>"

            redirect(action: "errores", params: [link: link])
            return
        }


        def tramite = Tramite.findByPlanillaAndTipoTramite(planilla, TipoTramite.findByCodigo("PDPG"))
        def prsn = PersonasTramite.findAllByTramite(tramite, [sort: "rolTramite"])
        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def precios = [:]
        def indirecto = obra.totales / 100

//        println "personas: ${prsn.persona.id} ${prsn.persona.nombre}"
        preciosService.ac_rbroObra(obra.id)

        detalle.each {
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
        }

        def baos = new ByteArrayOutputStream()
        def name = "memo_pedido_pago_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font fontInfo = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)

        Font fontThHeader = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTdHeader = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        Font fontThHeaderGris = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
        Font fontTdHeaderGris = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        Font fontThTabla = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTdTabla = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        Font fontContenido = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        Font fontFirma = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.scaleToFit(52, 52)
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        Document document
        document = new Document(PageSize.A4);
        document.setMargins(86.4, 56.2, 56.2, 56.2);
        // margins: left, right, top, bottom
        // 1 in = 72, 1cm=28.1, 3cm = 86.4
        def pdfw = PdfWriter.getInstance(document, baos);

        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Memo de pedido de pago" + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, memo, pedido, pago, anticipo");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
        preface.add(new Paragraph("MEMO DE PEDIDO DE PAGO ", fontTituloGad));
        preface.add(new Paragraph(obra.nombre, fontTituloGad));
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

        def strPeriodo = (planilla.tipoPlanilla.codigo == 'A' ? 'Anticipo' : 'del ' + planilla.fechaInicio.format('dd-MM-yyyy') + ' al ' + planilla.fechaFin.format('dd-MM-yyyy'))

        addCellTabla(tablaDatosObra, new Paragraph("Obra", fontThHeaderGris), [bg: bgObra, border: bgObra, bct: Color.BLACK, bwt: 0.1, bcl: Color.BLACK, bwl: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(obra.nombre, fontTdHeaderGris), [bg: bgObra, border: bgObra, bct: Color.BLACK, bwt: 0.1, bcr: Color.BLACK, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])

        addCellTabla(tablaDatosObra, new Paragraph("Lugar", fontThHeaderGris), [bg: bgObra, border: bgObra, bcl: Color.BLACK, bwl: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(obra.barrio, fontTdHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])
//        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Planilla", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(planilla.numero, fontTdHeaderGris), [bg: bgObra, border: bgObra, bcr: Color.BLACK, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Ubicación", fontThHeaderGris), [bg: bgObra, border: bgObra, bcl: Color.BLACK, bwl: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(obra.parroquia?.nombre + " - Cantón " + obra.parroquia?.canton?.nombre, fontTdHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])
//        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Monto contrato", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(numero(contrato.monto, 2), fontTdHeaderGris), [bcr: Color.BLACK, bwr: 0.1, bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Contratista", fontThHeaderGris), [bg: bgObra, bcl: Color.BLACK, bwl: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(contrato.oferta.proveedor.nombre, fontTdHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])
//        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Periodo", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(strPeriodo, fontTdHeaderGris), [bg: bgObra, bcr: Color.BLACK, bwr: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Plazo", fontThHeaderGris), [pb: 5, bg: bgObra, bcl: Color.BLACK, bwl: 0.1, bcb: Color.BLACK, bwb: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(numero(contrato.plazo, 0) + " días", fontTdHeaderGris), [bg: bgObra, bcb: Color.BLACK, bwb: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, bcb: Color.BLACK, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, bcb: Color.BLACK, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("", fontTdHeaderGris), [bg: bgObra, bcr: Color.BLACK, bwr: 0.1, bcb: Color.BLACK, bwb: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaDatosObra);
        /* *********************************** FIN DATOS OBRA **********************************/

        /* *********************************** DATOS MEMO **********************************/
        PdfPTable tablaDatosMemo = new PdfPTable(2);
        tablaDatosMemo.setWidthPercentage(100);
        tablaDatosMemo.setWidths(arregloEnteros([12, 88]))
        tablaDatosMemo.setSpacingAfter(10f)

        addCellTabla(tablaDatosMemo, new Paragraph("No.", fontThHeader), [border: Color.WHITE, bct: Color.BLACK, bwt: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosMemo, new Paragraph(tramite.memo, fontTdHeader), [border: Color.WHITE, bct: Color.BLACK, bwt: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosMemo, new Paragraph("Para", fontThHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosMemo, new Paragraph(nombrePersona(prsn[1].persona), fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosMemo, new Paragraph(prsn[1].persona?.departamento?.descripcion, fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosMemo, new Paragraph(prsn[1].persona?.departamento?.direccion?.nombre, fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosMemo, new Paragraph(prsn[0].persona?.departamento?.descripcion, fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosMemo, new Paragraph("De", fontThHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosMemo, new Paragraph(nombrePersona(prsn[0].persona), fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosMemo, new Paragraph(prsn[0].persona?.departamento?.descripcion, fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosMemo, new Paragraph("Fecha", fontThHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosMemo, new Paragraph(fechaConFormato(tramite?.fecha, "dd-MM-yyyy"), fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosMemo, new Paragraph("Asunto", fontThHeader), [pb: 5, border: Color.WHITE, bcb: Color.BLACK, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosMemo, new Paragraph(tramite?.descripcion, fontTdHeader), [border: Color.WHITE, bcb: Color.BLACK, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaDatosMemo);
        /* *********************************** FIN DATOS MEMO **********************************/

        /* *********************************** CONTENIDOS **********************************/
//        def strParrafo1 = "De acuerdo al Contrato N° ${contrato?.codigo}, suscrito el ${fechaConFormato(contrato?.fechaSubscripcion, 'dd-MM-yyyy')}, por el valor de " +
//                "USD ${numero(contrato?.monto, 2)}  sin incluir IVA, para realizar ${contrato?.objeto}, " +
//                "ubicada en el Barrio ${contrato?.oferta?.concurso?.obra?.barrio}, Parroquia ${contrato?.oferta?.concurso?.obra?.parroquia}, " +
//                "Cantón ${contrato?.oferta?.concurso?.obra?.parroquia?.canton}, de la Provincia de ${contrato?.oferta?.concurso?.obra?.parroquia?.canton?.provincia?.nombre}"
//        Paragraph parrafo1 = new Paragraph(strParrafo1, fontContenido);
        Paragraph parrafo1 = new Paragraph(texto.parrafo1, fontContenido);
        parrafo1.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafo1, 1);
        document.add(parrafo1)

//        def strParrafo2 = "Sírvase disponer el trámite respectivo para el pago del ${numero(contrato?.porcentajeAnticipo, 0)}% del anticipo, a favor de ${nombrePersona(contrato?.oferta?.proveedor, 'prov')} " +
//                "según claúsula sexta, literal a) del citado documento. El detalle es el siguiente:"
//        Paragraph parrafo2 = new Paragraph(strParrafo2, fontContenido);
        Paragraph parrafo2 = new Paragraph(texto.parrafo2, fontContenido);
        parrafo2.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafo2, 1);
        document.add(parrafo2)

        def cn = dbConnectionService.getConnection()
        def sql= "select sum(rjplvlor) suma from rjpl where plnl__id = (select max(plnlrjst) from rjpl " +
                "where plnl__id = ${planilla.id} and plnlrjst < plnl__id)"
        println "--sql: $sql"
        def reajusteAnterior = cn.rows(sql.toString())[0].suma
        def reajuste = ReajustePlanilla.findAllByPlanilla(planilla).sum{ it.valorReajustado} - reajusteAnterior


        def tablaValores = new PdfPTable(2);
        tablaValores.setWidthPercentage(75);
        tablaValores.setHorizontalAlignment(Element.ALIGN_LEFT);
        tablaValores.setWidths(arregloEnteros([70, 30]))
        tablaValores.setSpacingBefore(5f)
        tablaValores.setSpacingAfter(5f)

        if (planilla.tipoPlanilla.codigo == 'A') {
            addCellTabla(tablaValores, new Paragraph("${numero(contrato?.porcentajeAnticipo, 0)}% de anticipo", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        } else {
            addCellTabla(tablaValores, new Paragraph("Valor planilla", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tablaValores, new Paragraph("${numero(planilla.valor, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaValores, new Paragraph("(+) Reajuste  ${planilla.tipoPlanilla.codigo == 'A' ? 'del anticipo' : ''}", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("${numero(reajuste, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("Total planilla + reajuste ", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("${numero((reajuste + planilla.valor), 2)}", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("(-) Anticipo  ", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("${numero(planilla.descuentos, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("(-) Multas  ", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        def multas = 0
        multas = MultasPlanilla.executeQuery("select sum(monto) from MultasPlanilla where planilla = :p", [p: planilla])[0]?:0
        multas += planilla.multaEspecial?:0
        addCellTabla(tablaValores, new Paragraph("${numero(multas, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])


        addCellTabla(tablaValores, new Paragraph("SUMA", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("${numero(planilla.valor + reajuste - planilla.descuentos - multas, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

        if(planilla.noPagoValor > 0) {
            addCellTabla(tablaValores, new Paragraph("${planilla.noPago}", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaValores, new Paragraph("${numero(planilla.noPagoValor, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }

        if(costo){  // existe planilla de costo + porcentaje
            addCellTabla(tablaValores, new Paragraph("(+) Costo + Porcentaje ", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaValores, new Paragraph("${numero(costo, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }

        addCellTabla(tablaValores, new Paragraph("A FAVOR DEL CONTRATISTA", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("${numero(planilla.valor + reajuste - planilla.descuentos - multas - planilla.noPagoValor + costo, 2)}", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaValores)
        def totalLetras = planilla.valor + planilla.reajuste - planilla.descuentos - multas + costo
        def neg = ""
        if (totalLetras < 0) {
            totalLetras = totalLetras * -1
            neg = "MENOS "
        }
        def numerosALetras = NumberToLetterConverter.convertNumberToLetter(totalLetras)
//        println "a letras: $totalLetras, resulta: $numerosALetras"
//       def strParrafo3 = "Son ${neg}${numerosALetras}"
//        Paragraph parrafo3 = new Paragraph(strParrafo3, fontContenido);
        Paragraph parrafo3 = new Paragraph(texto.parrafo3, fontContenido);
        parrafo3.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafo3, 1);
        document.add(parrafo3)

//        def strParrafo4 = "A fin de en forma oportuna dar al contratista la orden de inicio de la obra, informar a esta " +
//                "Dirección la fecha de pago del anticipo reajustado y su valor."

//        def strParrafo4 = "A fin de en forma oportuna dar al contratista la orden de inicio de la obra, informar a esta Dirección la fecha de transferencia del valor a pagar a la cuenta del contratista."
//        Paragraph parrafo4 = new Paragraph(strParrafo4, fontContenido);
        Paragraph parrafo4 = new Paragraph(texto.parrafo4, fontContenido);
        parrafo4.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafo4, 1);
        document.add(parrafo4)

        Paragraph parrafo5 = new Paragraph(texto.parrafo5, fontContenido);
        parrafo5.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(parrafo5)
        /* *********************************** FIN CONTENIDOS **********************************/

        /* *********************************** FIRMA **********************************/

        def strParrafoFirma = "\n\n\n______________________________________\n${nombrePersona(prsn[0].persona)}\n${prsn[0].persona.departamento}"
        Paragraph parrafoFirma = new Paragraph(strParrafoFirma, fontFirma);
        parrafoFirma.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafoFirma, 1);
        document.add(parrafoFirma)
        /* *********************************** FIN FIRMA **********************************/


        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def memoPedidoPagoComp() {
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def cmpl = Contrato.findByPadre(contrato)
        def costo = Planilla.findByPadreCosto(planilla)?.valor?:0

        def rjplAntr = planillasService.reajusteAnterior(planilla)
        def rjplAcml = planillasService.reajusteAcumulado(planilla)
        def rjplActl = rjplAcml - rjplAntr

        def rjplAntrCp = planillasService.reajusteAnterior(planilla.planillaCmpl)
        def rjplAcmlCp = planillasService.reajusteAcumulado(planilla.planillaCmpl)
        rjplAntr += rjplAntrCp
        rjplAcml += rjplAcmlCp
        rjplActl = rjplAcml - rjplAntr


        def ok = true
        def str = ""

        def texto = Pdfs.findAllByPlanilla(planilla)
        if (texto.size() == 0) {
            redirect(controller: "planilla", action: "configPedidoPagoComp", id: planilla.id)
            return
        } else if (texto.size() > 1) {
            str += "<li>Se encontraron ${texto.size()} textos. No se pudo generar el pdf.</li>"
            ok = false
        } else {
            texto = texto.first()
        }

        if (!ok) {
            flash.message = "<ul>" + str + "</ul>"

            def url = g.createLink(controller: "planilla", action: "list", id: contrato.id)
            def link = "<a href='${url}' class='btn btn-danger'>Regresar</a>"

            redirect(action: "errores", params: [link: link])
            return
        }


        def tramite = Tramite.findByPlanillaAndTipoTramite(planilla, TipoTramite.findByCodigo("PDPG"))
        def prsn = PersonasTramite.findAllByTramite(tramite, [sort: "rolTramite"])
        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def precios = [:]
        def indirecto = obra.totales / 100

//        println "personas: ${prsn.persona.id} ${prsn.persona.nombre}"
        preciosService.ac_rbroObra(obra.id)

        detalle.each {
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
        }

        def baos = new ByteArrayOutputStream()
        def name = "memo_pedido_pago_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font fontInfo = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)

        Font fontThHeader = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTdHeader = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        Font fontThHeaderGris = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
        Font fontTdHeaderGris = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        Font fontThTabla = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTdTabla = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        Font fontContenido = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        Font fontFirma = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.scaleToFit(52, 52)
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        Document document
        document = new Document(PageSize.A4);
        document.setMargins(86.4, 56.2, 56.2, 56.2);
        // margins: left, right, top, bottom
        // 1 in = 72, 1cm=28.1, 3cm = 86.4
        def pdfw = PdfWriter.getInstance(document, baos);

        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Memo de pedido de pago" + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, memo, pedido, pago, anticipo");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
        preface.add(new Paragraph("MEMO DE PEDIDO DE PAGO ", fontTituloGad));
        preface.add(new Paragraph(obra.nombre, fontTituloGad));
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

        def strPeriodo = (planilla.tipoPlanilla.codigo == 'A' ? 'Anticipo' : 'del ' + planilla.fechaInicio.format('dd-MM-yyyy') + ' al ' + planilla.fechaFin.format('dd-MM-yyyy'))

        addCellTabla(tablaDatosObra, new Paragraph("Obra", fontThHeaderGris), [bg: bgObra, border: bgObra, bct: Color.BLACK, bwt: 0.1, bcl: Color.BLACK, bwl: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(obra.nombre, fontTdHeaderGris), [bg: bgObra, border: bgObra, bct: Color.BLACK, bwt: 0.1, bcr: Color.BLACK, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])

        addCellTabla(tablaDatosObra, new Paragraph("Lugar", fontThHeaderGris), [bg: bgObra, border: bgObra, bcl: Color.BLACK, bwl: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(obra.barrio, fontTdHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])
//        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Planilla", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(planilla.numero, fontTdHeaderGris), [bg: bgObra, border: bgObra, bcr: Color.BLACK, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Ubicación", fontThHeaderGris), [bg: bgObra, border: bgObra, bcl: Color.BLACK, bwl: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(obra.parroquia?.nombre + " - Cantón " + obra.parroquia?.canton?.nombre, fontTdHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])
//        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Monto contrato", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(numero(contrato.monto, 2), fontTdHeaderGris), [bcr: Color.BLACK, bwr: 0.1, bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Contratista", fontThHeaderGris), [bg: bgObra, bcl: Color.BLACK, bwl: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(contrato.oferta.proveedor.nombre, fontTdHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2])
//        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Periodo", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(strPeriodo, fontTdHeaderGris), [bg: bgObra, bcr: Color.BLACK, bwr: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Plazo", fontThHeaderGris), [pb: 5, bg: bgObra, bcl: Color.BLACK, bwl: 0.1, bcb: Color.BLACK, bwb: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(numero(contrato.plazo, 0) + " días", fontTdHeaderGris), [bg: bgObra, bcb: Color.BLACK, bwb: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, bcb: Color.BLACK, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, bcb: Color.BLACK, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("", fontTdHeaderGris), [bg: bgObra, bcr: Color.BLACK, bwr: 0.1, bcb: Color.BLACK, bwb: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaDatosObra);
        /* *********************************** FIN DATOS OBRA **********************************/

        /* *********************************** DATOS MEMO **********************************/
        PdfPTable tablaDatosMemo = new PdfPTable(2);
        tablaDatosMemo.setWidthPercentage(100);
        tablaDatosMemo.setWidths(arregloEnteros([12, 88]))
        tablaDatosMemo.setSpacingAfter(10f)

        addCellTabla(tablaDatosMemo, new Paragraph("No.", fontThHeader), [border: Color.WHITE, bct: Color.BLACK, bwt: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosMemo, new Paragraph(tramite.memo, fontTdHeader), [border: Color.WHITE, bct: Color.BLACK, bwt: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosMemo, new Paragraph("Para", fontThHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosMemo, new Paragraph(nombrePersona(prsn[1].persona), fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosMemo, new Paragraph(prsn[1].persona?.departamento?.descripcion, fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosMemo, new Paragraph(prsn[1].persona?.departamento?.direccion?.nombre, fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosMemo, new Paragraph(prsn[0].persona?.departamento?.descripcion, fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosMemo, new Paragraph("De", fontThHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosMemo, new Paragraph(nombrePersona(prsn[0].persona), fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosMemo, new Paragraph(prsn[0].persona?.departamento?.descripcion, fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosMemo, new Paragraph("Fecha", fontThHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosMemo, new Paragraph(fechaConFormato(tramite?.fecha, "dd-MM-yyyy"), fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosMemo, new Paragraph("Asunto", fontThHeader), [pb: 5, border: Color.WHITE, bcb: Color.BLACK, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosMemo, new Paragraph(tramite?.descripcion, fontTdHeader), [border: Color.WHITE, bcb: Color.BLACK, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaDatosMemo);
        /* *********************************** FIN DATOS MEMO **********************************/

        /* *********************************** CONTENIDOS **********************************/
//        def strParrafo1 = "De acuerdo al Contrato N° ${contrato?.codigo}, suscrito el ${fechaConFormato(contrato?.fechaSubscripcion, 'dd-MM-yyyy')}, por el valor de " +
//                "USD ${numero(contrato?.monto, 2)}  sin incluir IVA, para realizar ${contrato?.objeto}, " +
//                "ubicada en el Barrio ${contrato?.oferta?.concurso?.obra?.barrio}, Parroquia ${contrato?.oferta?.concurso?.obra?.parroquia}, " +
//                "Cantón ${contrato?.oferta?.concurso?.obra?.parroquia?.canton}, de la Provincia de ${contrato?.oferta?.concurso?.obra?.parroquia?.canton?.provincia?.nombre}"
//        Paragraph parrafo1 = new Paragraph(strParrafo1, fontContenido);
        Paragraph parrafo1 = new Paragraph(texto.parrafo1, fontContenido);
        parrafo1.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafo1, 1);
        document.add(parrafo1)

//        def strParrafo2 = "Sírvase disponer el trámite respectivo para el pago del ${numero(contrato?.porcentajeAnticipo, 0)}% del anticipo, a favor de ${nombrePersona(contrato?.oferta?.proveedor, 'prov')} " +
//                "según claúsula sexta, literal a) del citado documento. El detalle es el siguiente:"
//        Paragraph parrafo2 = new Paragraph(strParrafo2, fontContenido);
        Paragraph parrafo2 = new Paragraph(texto.parrafo2, fontContenido);
        parrafo2.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafo2, 1);
        document.add(parrafo2)

        def cn = dbConnectionService.getConnection()
        def sql= "select sum(rjplvlor) suma from rjpl where plnl__id = (select max(plnlrjst) from rjpl " +
                "where plnl__id = ${planilla.id} and plnlrjst < plnl__id)"
        println "--sql: $sql"
        def reajusteAnterior = cn.rows(sql.toString())[0].suma
        def reajuste = ReajustePlanilla.findAllByPlanilla(planilla).sum{ it.valorReajustado} - reajusteAnterior


        def tablaValores = new PdfPTable(2);
        tablaValores.setWidthPercentage(75);
        tablaValores.setHorizontalAlignment(Element.ALIGN_LEFT);
        tablaValores.setWidths(arregloEnteros([70, 30]))
        tablaValores.setSpacingBefore(5f)
        tablaValores.setSpacingAfter(5f)

        if (planilla.tipoPlanilla.codigo == 'A') {
            addCellTabla(tablaValores, new Paragraph("${numero(contrato?.porcentajeAnticipo, 0)}% de anticipo", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        } else {
            addCellTabla(tablaValores, new Paragraph("Valor planilla", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tablaValores, new Paragraph("${numero(planilla.valor + planilla.planillaCmpl.valor, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaValores, new Paragraph("(+) Reajuste  ${planilla.tipoPlanilla.codigo == 'A' ? 'del anticipo' : ''}", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("${numero(rjplActl, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("Total planilla + reajuste ", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("${numero((rjplActl + planilla.valor + planilla.planillaCmpl.valor), 2)}", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("(-) Anticipo  ", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("${numero(planilla.descuentos, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("(-) Multas  ", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        def multas = 0
        multas = MultasPlanilla.executeQuery("select sum(monto) from MultasPlanilla where planilla = :p", [p: planilla])[0]?:0
        multas += planilla.multaEspecial?:0
        addCellTabla(tablaValores, new Paragraph("${numero(multas, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])


        addCellTabla(tablaValores, new Paragraph("SUMA", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("${numero(planilla.valor + planilla.planillaCmpl.valor + rjplActl - planilla.descuentos - multas, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

        if(planilla.noPagoValor > 0) {
            addCellTabla(tablaValores, new Paragraph("${planilla.noPago}", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaValores, new Paragraph("${numero(planilla.noPagoValor, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }

        if(costo){  // existe planilla de costo + porcentaje
            addCellTabla(tablaValores, new Paragraph("(+) Costo + Porcentaje ", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaValores, new Paragraph("${numero(costo, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }

        addCellTabla(tablaValores, new Paragraph("A FAVOR DEL CONTRATISTA", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("${numero(planilla.valor + rjplActl + planilla.planillaCmpl.valor - planilla.descuentos - multas - planilla.noPagoValor + costo, 2)}", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaValores)
        def totalLetras = planilla.valor + planilla.reajuste - planilla.descuentos - multas + costo
        def neg = ""
        if (totalLetras < 0) {
            totalLetras = totalLetras * -1
            neg = "MENOS "
        }
        def numerosALetras = NumberToLetterConverter.convertNumberToLetter(totalLetras)
//        println "a letras: $totalLetras, resulta: $numerosALetras"
//       def strParrafo3 = "Son ${neg}${numerosALetras}"
//        Paragraph parrafo3 = new Paragraph(strParrafo3, fontContenido);
        Paragraph parrafo3 = new Paragraph(texto.parrafo3, fontContenido);
        parrafo3.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafo3, 1);
        document.add(parrafo3)

//        def strParrafo4 = "A fin de en forma oportuna dar al contratista la orden de inicio de la obra, informar a esta " +
//                "Dirección la fecha de pago del anticipo reajustado y su valor."

//        def strParrafo4 = "A fin de en forma oportuna dar al contratista la orden de inicio de la obra, informar a esta Dirección la fecha de transferencia del valor a pagar a la cuenta del contratista."
//        Paragraph parrafo4 = new Paragraph(strParrafo4, fontContenido);
        Paragraph parrafo4 = new Paragraph(texto.parrafo4, fontContenido);
        parrafo4.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafo4, 1);
        document.add(parrafo4)

        Paragraph parrafo5 = new Paragraph(texto.parrafo5, fontContenido);
        parrafo5.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(parrafo5)
        /* *********************************** FIN CONTENIDOS **********************************/

        /* *********************************** FIRMA **********************************/

        def strParrafoFirma = "\n\n\n______________________________________\n${nombrePersona(prsn[0].persona)}\n${prsn[0].persona.departamento}"
        Paragraph parrafoFirma = new Paragraph(strParrafoFirma, fontFirma);
        parrafoFirma.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafoFirma, 1);
        document.add(parrafoFirma)
        /* *********************************** FIN FIRMA **********************************/


        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def memoPedidoPagoAnticipo() {
        def planilla = Planilla.get(params.id)
        println("memoPedidoPagoAnticipo - planilla " + params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
//        def tramite = Tramite.findByPlanilla(planilla)

        def ok = true
        def str = ""
//        println "memo "+params+" "+planilla
        def texto = Pdfs.findAllByPlanilla(planilla)
        if (texto.size() == 0) {
            redirect(controller: "planilla", action: "configPedidoPagoAnticipo", id: planilla.id)
            return
        } else if (texto.size() > 1) {
            str += "<li>Se encontraron ${texto.size()} textos. No se pudo generar el pdf.</li>"
            ok = false
        } else {
            texto = texto.first()
        }

        if (!ok) {
            flash.message = "<ul>" + str + "</ul>"

            def url = g.createLink(controller: "planilla", action: "list", id: contrato.id)
            def link = "<a href='${url}' class='btn btn-danger'>Regresar</a>"

            redirect(action: "errores", params: [link: link])
            return
        }

        def tramite = Tramite.findAllByPlanillaAndTipoTramite(planilla, TipoTramite.findByCodigo("PDPG"), [sort:'id', order: 'desc']).first()
        def prsn = PersonasTramite.findAllByTramite(tramite, [sort: "rolTramite"])

        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def precios = [:]
        def indirecto = obra.totales / 100

        preciosService.ac_rbroObra(obra.id)

        detalle.each {
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
        }

        def baos = new ByteArrayOutputStream()
        def name = "memo_pedido_pago_anticipo_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font fontInfo = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)
        Font fontThHeaderGris = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
        Font fontTdHeaderGris = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);
        Font fontThHeader = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTdHeader = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
        Font fontThTabla = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTdTabla = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
        Font fontContenido = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
        Font fontFirma = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        def prmsTdNoBorder = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
        Image logo = Image.getInstance(logoPath);
        logo.scaleToFit(52, 52)
        logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

        Document document
        document = new Document(PageSize.A4);
//        document.setMargins(86.4, 56.2, 56.2, 86.4);
        document.setMargins(86.4, 56.2, 56.2, 56.2);
        // margins: left, right, top, bottom
        // 1 in = 72, 1cm=28.1, 3cm = 86.4
        def pdfw = PdfWriter.getInstance(document, baos);

        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Memo de pedido de pago del anticipo de la obra " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, memo, pedido, pago, anticipo");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
        preface.add(new Paragraph("MEMO DE PEDIDO DE PAGO DEL ANTICIPO DE LA OBRA ", fontTituloGad));
        preface.add(new Paragraph(obra.nombre, fontTituloGad));
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

        def strPeriodo = (planilla.tipoPlanilla.codigo == 'A' ? 'Anticipo' : 'del ' + planilla.fechaInicio.format('dd-MM-yyyy') + ' al ' + planilla.fechaFin.format('dd-MM-yyyy'))

        addCellTabla(tablaDatosObra, new Paragraph("Obra", fontThHeaderGris), [bg: bgObra, border: bgObra, bct: Color.BLACK, bwt: 0.1, bcl: Color.BLACK, bwl: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(obra.nombre, fontTdHeaderGris), [bg: bgObra, border: bgObra, bct: Color.BLACK, bwt: 0.1, bcr: Color.BLACK, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])

        addCellTabla(tablaDatosObra, new Paragraph("Lugar", fontThHeaderGris), [bg: bgObra, border: bgObra, bcl: Color.BLACK, bwl: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph((obra.lugar?.descripcion ?: ""), fontTdHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

//        addCellTabla(tablaDatosObra, new Paragraph("Planilla", fontThHeaderGris), [bg: bgObra, border: bgObra, bcl: Color.BLACK, bwl: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosObra, new Paragraph(planilla.numero, fontTdHeaderGris), [bg: bgObra, border: bgObra, bcr: Color.BLACK, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Planilla", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(planilla.numero, fontTdHeaderGris), [bg: bgObra, border: bgObra, bcr: Color.BLACK, bwr: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Ubicación", fontThHeaderGris), [bg: bgObra, border: bgObra, bcl: Color.BLACK, bwl: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(obra.parroquia?.nombre + " - Cantón " + obra.parroquia?.canton?.nombre, fontTdHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Monto contrato", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(numero(contrato.monto, 2), fontTdHeaderGris), [bcr: Color.BLACK, bwr: 0.1, bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Contratista", fontThHeaderGris), [bg: bgObra, bcl: Color.BLACK, bwl: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(contrato.oferta.proveedor.nombre, fontTdHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Periodo", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(strPeriodo, fontTdHeaderGris), [bg: bgObra, bcr: Color.BLACK, bwr: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosObra, new Paragraph("Plazo", fontThHeaderGris), [pb: 5, bg: bgObra, bcl: Color.BLACK, bwl: 0.1, bcb: Color.BLACK, bwb: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph(numero(contrato.plazo, 0) + " días", fontTdHeaderGris), [bg: bgObra, bcb: Color.BLACK, bwb: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, bcb: Color.BLACK, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, bcb: Color.BLACK, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosObra, new Paragraph("", fontTdHeaderGris), [bg: bgObra, bcr: Color.BLACK, bwr: 0.1, bcb: Color.BLACK, bwb: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

//
//        addCellTabla(tablaDatosObra, new Paragraph("Ubicación", fontThHeaderGris), [bg: bgObra, border: bgObra, bcl: Color.BLACK, bwl: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosObra, new Paragraph(obra.parroquia?.nombre + " - Cantón " + obra.parroquia?.canton?.nombre, fontTdHeaderGris), [bcr: Color.BLACK, bwr: 0.1, bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
//        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//
//        addCellTabla(tablaDatosObra, new Paragraph("Monto contrato", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosObra, new Paragraph(numero(contrato.monto, 2), fontTdHeaderGris), [bcr: Color.BLACK, bwr: 0.1, bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//
//        addCellTabla(tablaDatosObra, new Paragraph("Contratista", fontThHeaderGris), [bg: bgObra, bcl: Color.BLACK, bwl: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosObra, new Paragraph(contrato.oferta.proveedor.nombre, fontTdHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//
//        addCellTabla(tablaDatosObra, new Paragraph("Periodo", fontThHeaderGris), [bg: bgObra, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosObra, new Paragraph(strPeriodo, fontTdHeaderGris), [bg: bgObra, bcr: Color.BLACK, bwr: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//
//        addCellTabla(tablaDatosObra, new Paragraph("Plazo", fontThHeaderGris), [pb: 5, bg: bgObra, bcl: Color.BLACK, bwl: 0.1, bcb: Color.BLACK, bwb: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosObra, new Paragraph(numero(contrato.plazo, 0) + " días", fontTdHeaderGris), [bg: bgObra, bcb: Color.BLACK, bwb: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, bcb: Color.BLACK, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosObra, new Paragraph("", fontThHeaderGris), [bg: bgObra, border: bgObra, bcb: Color.BLACK, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosObra, new Paragraph("", fontTdHeaderGris), [bg: bgObra, bcr: Color.BLACK, bwr: 0.1, bcb: Color.BLACK, bwb: 0.1, border: bgObra, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaDatosObra);
        /* *********************************** FIN DATOS OBRA **********************************/

        /* *********************************** DATOS MEMO **********************************/
        PdfPTable tablaDatosMemo = new PdfPTable(2);
        tablaDatosMemo.setWidthPercentage(100);
        tablaDatosMemo.setWidths(arregloEnteros([12, 88]))
        tablaDatosMemo.setSpacingAfter(10f)

        addCellTabla(tablaDatosMemo, new Paragraph("No.", fontThHeader), [border: Color.WHITE, bct: Color.BLACK, bwt: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosMemo, new Paragraph(tramite.memo, fontTdHeader), [border: Color.WHITE, bct: Color.BLACK, bwt: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosMemo, new Paragraph("Para", fontThHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosMemo, new Paragraph(prsn[0]?.persona?.departamento?.direccion?.nombre, fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosMemo, new Paragraph(prsn[1].persona?.departamento?.direccion?.nombre, fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosMemo, new Paragraph(nombrePersona(prsn[1]?.persona), fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosMemo, new Paragraph(prsn[1]?.persona?.departamento?.descripcion, fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosMemo, new Paragraph("De", fontThHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosMemo, new Paragraph(nombrePersona(prsn[0]?.persona), fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//        addCellTabla(tablaDatosMemo, new Paragraph(prsn[0]?.persona?.departamento?.direccion?.nombre, fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosMemo, new Paragraph("Fecha", fontThHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosMemo, new Paragraph(fechaConFormato(tramite?.fecha, "dd-MM-yyyy"), fontTdHeader), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tablaDatosMemo, new Paragraph("Asunto", fontThHeader), [pb: 5, border: Color.WHITE, bcb: Color.BLACK, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatosMemo, new Paragraph(tramite?.descripcion, fontTdHeader), [border: Color.WHITE, bcb: Color.BLACK, bwb: 0.1, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaDatosMemo);
        /* *********************************** FIN DATOS MEMO **********************************/

        /* *********************************** CONTENIDOS **********************************/
//        def strParrafo1 = "De acuerdo al Contrato N° ${contrato?.codigo}, suscrito el ${fechaConFormato(contrato?.fechaSubscripcion, 'dd-MM-yyyy')}, por el valor de " +
//                "USD ${numero(contrato?.monto, 2)}  sin incluir IVA, para realizar ${contrato?.objeto}, " +
//                "ubicada en el Barrio ${contrato?.oferta?.concurso?.obra?.barrio}, Parroquia ${contrato?.oferta?.concurso?.obra?.parroquia}, " +
//                "Cantón ${contrato?.oferta?.concurso?.obra?.parroquia?.canton}, de la Provincia de ${contrato?.oferta?.concurso?.obra?.parroquia?.canton?.provincia?.nombre}"
//        Paragraph parrafo1 = new Paragraph(strParrafo1, fontContenido);
        Paragraph parrafo1 = new Paragraph(texto.parrafo1, fontContenido);
        parrafo1.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafo1, 1);
        document.add(parrafo1)

//        def strParrafo2 = "Sírvase disponer el trámite respectivo para el pago del ${numero(contrato?.porcentajeAnticipo, 0)}% del anticipo, a favor de ${nombrePersona(contrato?.oferta?.proveedor, 'prov')} " +
//                "según claúsula sexta, literal a) del citado documento. El detalle es el siguiente:"
//        Paragraph parrafo2 = new Paragraph(strParrafo2, fontContenido);
        Paragraph parrafo2 = new Paragraph(texto.parrafo2, fontContenido);
        parrafo2.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafo2, 1);
        document.add(parrafo2)

        def tablaValores = new PdfPTable(2);
        tablaValores.setWidthPercentage(75);
        tablaValores.setHorizontalAlignment(Element.ALIGN_LEFT);
        tablaValores.setWidths(arregloEnteros([70, 30]))
        tablaValores.setSpacingBefore(5f)
        tablaValores.setSpacingAfter(5f)

        def reajuste = ReajustePlanilla.findByPlanilla(planilla).valorReajustado

        if (planilla.tipoPlanilla.codigo == 'A') {
            addCellTabla(tablaValores, new Paragraph("${numero(contrato?.porcentajeAnticipo, 0)}% de anticipo", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        } else {
            addCellTabla(tablaValores, new Paragraph("Valor planilla", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tablaValores, new Paragraph("${numero(planilla.valor, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("Reajuste provisional ${planilla.tipoPlanilla.codigo == 'A' ? 'del anticipo' : ''}", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("${numero(reajuste, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("SUMA", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("${numero(planilla.valor + reajuste, 2)}", fontTdTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("A FAVOR DEL CONTRATISTA", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaValores, new Paragraph("${numero(planilla.valor + reajuste, 2)}", fontThTabla), [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

        document.add(tablaValores)

        Paragraph parrafo3 = new Paragraph(texto.parrafo3, fontContenido);
        parrafo3.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafo3, 1);
        document.add(parrafo3)

//        def strParrafo4 = "A fin de en forma oportuna dar al contratista la orden de inicio de la obra, informar a esta " +
//                "Dirección la fecha de pago del anticipo reajustado y su valor."
//        def strParrafo4 = "A fin de en forma oportuna dar al contratista la orden de inicio de la obra, informar a esta Dirección la fecha de transferencia del anticipo a la cuenta del contratista."
//        Paragraph parrafo4 = new Paragraph(strParrafo4, fontContenido);
        Paragraph parrafo4 = new Paragraph(texto.parrafo4, fontContenido);
        parrafo4.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafo4, 1);
        document.add(parrafo4)

        Paragraph parrafo5 = new Paragraph(texto.parrafo5 ?: "", fontContenido);
        parrafo5.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafo5, 1);
        document.add(parrafo5)
        /* *********************************** FIN CONTENIDOS **********************************/

        /* *********************************** FIRMA **********************************/

        def strParrafoFirma = "\n\n\n______________________________________\n${nombrePersona(prsn[0].persona)}\nAdministrador del Contrato - Delegado"
        Paragraph parrafoFirma = new Paragraph(strParrafoFirma, fontFirma);
        parrafoFirma.setAlignment(Element.ALIGN_JUSTIFIED);
        addEmptyLine(parrafoFirma, 1);
        document.add(parrafoFirma)
        if(texto.copia!="" && texto.copia!=null){
            Paragraph cc = new Paragraph("CC:"+texto.copia, fontFirma);
            cc.setAlignment(Element.ALIGN_JUSTIFIED);
            document.add(cc)
        }

        /* *********************************** FIN FIRMA **********************************/


        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def errores() {
        return [params: params]
    }

    /** imprime orden de inicio de Obra si existe, cas contrario redirecciona a planilla.configOrdenInicioObra**/
    def reporteContrato() {

        def obra = Obra.get(params.id)
        def concurso = janus.pac.Concurso.findByObra(obra)
        def oferta = janus.pac.Oferta.findByConcurso(concurso)
        def contrato = Contrato.findByOferta(oferta)

        def ok = true
        def str = ""

        def texto = Pdfs.findAllByObra(obra)
        println "................." + texto.size() + " obra:" + obra.id
        if (texto.size() == 0) {
            /* accede a crear inicio de obra solo el administrador */
            if (contrato.administrador.id == session.usuario.id) {
                redirect(controller: "planilla", action: "configOrdenInicioObra", id: obra.id)
                return
            } else {
                str += "<li>No se ha generado la Orden de Inicio de Obra</li>"
                ok = false
            }

        } else if (texto.size() > 1) {
            str += "<li>Se encontraron ${texto.size()} textos. No se pudo generar el pdf.</li>"
            ok = false
        } else {
            texto = texto.first()
        }

        if (!obra.fechaInicio) {
            str += "<li>No se ha registrado la fecha de inicio para la obra. Por favor corrija esto para mostrar la orden de inicio de obra</li>"
            ok = false
        }


        if (!ok) {
            flash.message = "<ul>" + str + "</ul>"
            def url = g.createLink(controller: "planilla", action: "list", id: contrato.id)
            def link = "<a href='${url}' class='btn btn-danger'>Regresar</a>"
            redirect(action: "errores", params: [link: link])

        } else {

            def tipoPlanilla = TipoPlanilla.findByCodigoIlike("A");
            def planillaDesc = Planilla.findByContratoAndTipoPlanilla(contrato, tipoPlanilla)
            def logoPath = servletContext.getRealPath("/") + "images/logo_gadpp_reportes.png"
            Image logo = Image.getInstance(logoPath);
            logo.scaleToFit(52, 52)
            logo.setAlignment(Image.LEFT | Image.TEXTWRAP)

            def prmsHeaderHoja = [border: Color.WHITE]
            def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]
            def prmsHeader = [border: Color.WHITE, colspan: 7, bg: new Color(73, 175, 205),
                              align : Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
            def prmsHeader2 = [border: Color.WHITE, colspan: 3, bg: new Color(73, 175, 205),
                               align : Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
            def prmsCellHead = [border: Color.WHITE, bg: new Color(73, 175, 205),
                                align : Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
            def prmsCellHead2 = [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
            def prmsCellCenter = [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
            def prmsCellRight = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT]
            def prmsCellRight2 = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT]
            def prmsCellLeft = [border: Color.BLACK, valign: Element.ALIGN_MIDDLE]
            def prmsCellLeft2 = [border: Color.WHITE, valign: Element.ALIGN_MIDDLE, align: Element.ALIGN_LEFT]
            def prmsCellLeft3 = [border: Color.WHITE, valign: Element.ALIGN_LEFT, align: Element.ALIGN_LEFT, colspan: 2]
            def prmsSubtotal = [border: Color.BLACK, colspan: 6,
                                align : Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
            def prmsNum = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

            def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                        prmsCellHead  : prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum, prmsHeaderHoja2: prmsHeaderHoja2,
                        prmsCellRight : prmsCellRight, prmsCellHead2: prmsCellHead2, prmsCellLeft2: prmsCellLeft2]


            def baos = new ByteArrayOutputStream()
            def name = "contrato_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
            Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
            Font times12normal = new Font(Font.TIMES_ROMAN, 12, Font.NORMAL);
            Font times14bold = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
            Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
            Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
            Font times10normal = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
            Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
            Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
            Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
            Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)

            Font fontTituloGad = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
            Font fontInfo = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)

            times8boldWhite.setColor(Color.WHITE)
            times10boldWhite.setColor(Color.WHITE)
            def fonts = [times12bold     : times12bold, times10bold: times10bold, times8bold: times8bold,
                         times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal, times10normal: times10normal]

            Document document
            document = new Document(PageSize.A4);
            document.setMargins(86.4, 56.2, 50, 10);

            def pdfw = PdfWriter.getInstance(document, baos);

            // headers and footers must be added before the document is opened
            HeaderFooter footer1 = new HeaderFooter(
                    new Phrase("Manuel Larrea N. 13-45 y Antonio Ante / Teléfonos troncal: (593-2)252 7077 - 254 9222 - 254 9020 - 254 9163 / www.pichincha.gob.ec", new Font(times8normal)), false);
            // true aqui pone numero de pagina
            footer1.setBorder(Rectangle.NO_BORDER);
            footer1.setBorder(Rectangle.TOP);
            footer1.setAlignment(Element.ALIGN_CENTER);
            document.setFooter(footer1);
            document.open();
            document.addTitle("Contrato " + new Date().format("dd_MM_yyyy"));
            document.addSubject("Generado por el sistema Janus");
            document.addKeywords("documentosObra, janus, presupuesto");
            document.addAuthor("Janus");
            document.addCreator("Tedein SA");

            Paragraph preface = new Paragraph();
            addEmptyLine(preface, 1);
            preface.setAlignment(Element.ALIGN_CENTER);
            preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", fontTituloGad));
            preface.add(new Paragraph("OFICIO DE INICIO DE LA OBRA " + obra.nombre, fontTituloGad));
            addEmptyLine(preface, 1);
            document.add(logo)
            document.add(preface);

            Paragraph headers = new Paragraph();
            headers.setAlignment(Element.ALIGN_LEFT);

            headers.add(new Paragraph("Oficio N°: " + obra.memoInicioObra, times12bold))
//            headers.add(new Paragraph("Quito, " + fechaConFormato(new Date(), "dd MMMM yyyy"), times12bold));
            headers.add(new Paragraph("Quito, " + fechaConFormato(obra.fechaImpresionInicioObra, "dd MMMM yyyy"), times12bold));
            headers.add(new Paragraph(" ", times10bold));
//            headers.add(new Paragraph(" ", times10bold));
            headers.add(new Paragraph(oferta?.proveedor?.titulo, times12bold));
            headers.add(new Paragraph(oferta?.proveedor?.nombreContacto.toUpperCase() + " " + oferta?.proveedor?.apellidoContacto.toUpperCase(), times12bold));
            if (oferta?.proveedor?.tipo == 'J')
                headers.add(new Paragraph(oferta?.proveedor?.nombre.toUpperCase(), times12bold));

            headers.add(new Paragraph("Presente", times12bold));
            headers.add(new Paragraph("", times10bold));
//            headers.add(new Paragraph("", times10bold));
            headers.add(new Paragraph("De nuestra consideración:", times12bold));
            headers.add(new Paragraph("", times10bold));
            document.add(headers);

//            def par1 = "Para los fines consiguientes me permito indicarle que la fecha de inicio del contrato N° " + contrato?.codigo.trim() + ", "
//            par1 += "para la construcción de " + obra?.descripcion.trim()
//            par1 += ", ubicada en la Parroquia " + obra?.parroquia?.nombre.trim() + ", Distrito Metropolitano de "
//            par1 += "Quito, de la Provincia de Pichincha, por un valor de US\$ "
//            par1 += g.formatNumber(number: contrato?.monto, format: "##,##0", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2) + " sin incluir IVA, consta "
//            par1 += "de la cláusula ${contrato.clausula}, numeral ${contrato.numeralPlazo}, que señala que el plazo total que el contratista tiene para "
//            par1 += "ejecutar, terminar y entregar a entera satisfacción es de " + NumberToLetterConverter.numberToLetter(contrato?.plazo).toLowerCase() + " días calendario " + "("
//            par1 += g.formatNumber(number: contrato?.plazo, format: "##,##0", locale: "ec", maxFractionDigits: 0, minFractionDigits: 0) + "), "
//            par1 += "contados a partir de la fecha de efectivización del anticipo y, en el numeral ${contrato.numeralAnticipo} se dice que se "
//            par1 += "entenderá entregado el anticipo una vez transcurridas veinte y cuatro (24) horas de realizada "
//            par1 += "la transferencia de fondos a la cuenta bancaria que para el efecto indique el contratista. "
//
//            def par2 = "Tesorería de la Corporación, remite a la " + obra?.departamento?.direccion?.nombre.trim()
//            par2 += ", copia del reporte de pago del " + contrato?.porcentajeAnticipo + "% del anticipo, por un valor de US\$ " + g.formatNumber(number: (planillaDesc?.valor + planillaDesc.reajuste), format: "##,##0", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2)
//            par2 += ", menos los descuentos de ley, fue acreditado el " + fechaConFormato(planillaDesc?.fechaMemoPagoPlanilla, "dd MMMM yyyy").toLowerCase()
//
//
//            def par3 = "Por las razones indicadas la fecha de inicio de la obra, del contrato N° " + contrato?.codigo.trim() + ", será el " + fechaConFormato(obra?.fechaInicio, "dd MMMM yyyy").toLowerCase() + "."


            def par1 = texto.parrafo1
            def par2 = texto.parrafo2
            def par3 = texto.parrafo3
            def par4 = texto.parrafo4

            Paragraph prueba = new Paragraph(par1.toString(), times12normal);
            prueba.setAlignment(Element.ALIGN_JUSTIFIED);
            prueba.setSpacingAfter(20);

            Paragraph prueba2 = new Paragraph(par2.toString(), times12normal);
            prueba2.setAlignment(Element.ALIGN_JUSTIFIED);
            prueba2.setSpacingAfter(20);

            Paragraph prueba3 = new Paragraph(par3.toString(), times12normal);
            prueba3.setAlignment(Element.ALIGN_JUSTIFIED);
            prueba3.setSpacingAfter(20);

            Paragraph prueba4 = new Paragraph(par4.toString(), times12normal);
            prueba4.setAlignment(Element.ALIGN_JUSTIFIED);
            prueba4.setSpacingAfter(20);

            document.add(prueba);
            document.add(prueba2);
            document.add(prueba3);
            document.add(prueba4);

            PdfPTable tablaFirmas = new PdfPTable(1);
            tablaFirmas.setWidthPercentage(100);
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)

//            def firmas = obra.firmaInicioObra
//            addCellTabla(tablaFirmas, new Paragraph(firmas?.titulo + " " + firmas?.nombre + " " + firmas?.apellido, times12bold), prmsHeaderHoja)
//            addCellTabla(tablaFirmas, new Paragraph(firmas?.cargo.toUpperCase(), times12bold), prmsHeaderHoja)
//            document.add(tablaFirmas);
            def firmas = contrato.administrador
            def firma = firmas?.titulo ? firmas?.titulo + " " : ""
            firma += firmas?.nombre + " " + firmas?.apellido
//            println "***************************************"
//            println contrato.administrador
//            println firmas
//            println firmas.titulo
//            println firmas.nombre
//            println firmas.apellido
//            println firma
//            println "***************************************"
            addCellTabla(tablaFirmas, new Paragraph(firma, times12bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("Administrador del Contrato - Delegado", times12bold), prmsHeaderHoja)
            document.add(tablaFirmas);

//        def footer = new PdfPTable(1);
//        footer.setWidthPercentage(100);
//        footer.setSpacingBefore(55f);
//
//        addCellTabla(footer, new Paragraph("Manuel Larrea N. 13-45 y Antonio Ante / Teléfonos troncal: (593-2)252 7077 - 254 9222 - 254 9020 - 254 9163 / www.pichincha.gob.ec", times8normal), [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
//
//        document.add(footer)

            document.close();
            pdfw.close()
            byte[] b = baos.toByteArray();
            response.setContentType("application/pdf")
            response.setHeader("Content-disposition", "attachment; filename=" + name)
            response.setContentLength(b.length)
            response.getOutputStream().write(b)
        }
    }

}
