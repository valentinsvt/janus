package janus

import com.lowagie.text.*
import com.lowagie.text.pdf.*
import janus.ejecucion.*
import janus.pac.CronogramaEjecucion
import janus.pac.PeriodoEjecucion
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook

import java.awt.Color

//import java.awt.*

class Reportes2Controller {

    def preciosService
    def dbConnectionService

    static int DOC = 1, ADJ = 2, PAG = 3

    def index() {}

    def test() {
        return [params: params]
    }

    def reporteRubro() {
        def obra = Obra.get(params.id)
        def rubros = VolumenesObra.findAllByObra(obra).item
        return [obra: obra, rubros: rubros]
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

        table.addCell(cell);
    }

//    def pagina(PdfContentByte cb, Document document, int pag) {
//        BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
//        cb.beginText();
//        cb.setFontAndSize(bf, 9);
//        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, pag.toString(), (document.right() - 15).toFloat(), (document.bottom() - 10).toFloat(), 0);
//        cb.endText();
//    }

    private static void infoText(PdfContentByte cb, Document document, String info, int tipo) {
        def posx = 0, posy = 0

        switch (tipo) {
            case DOC:
                posx = ((document.right() - document.left()) / 2 + document.leftMargin()).toFloat()
                posy = (document.bottom() - 10).toFloat()
                break;
            case ADJ:
                posx = ((document.right() - document.left()) / 2 + document.leftMargin()).toFloat()
                posy = (document.top() + 10).toFloat()
                break;
            case PAG:
                posx = (document.right() - 15).toFloat()
                posy = (document.bottom() - 10).toFloat()
                break;
        }

        BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        cb.beginText();
        cb.setFontAndSize(bf, 9);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, info, posx, posy, 0);
        cb.endText();
    }

    private static String truncText(String str, int max = 50) {
        if (str.length() > max) {
            str = str[0..max - 4] + "..."
        }
        return str
    }

    def reporteRubroIlustracion() {
        def obra = Obra.get(params.id)
        def rubros = VolumenesObra.findAllByObra(obra).item


        def baos = new ByteArrayOutputStream()
        def name = "rubros_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font catFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font fontTitle = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Document document
        document = new Document(PageSize.A4);
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Rubros de la obra " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, rubros");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", catFont));
        preface.add(new Paragraph("ANEXO DE ESPECIFICACIÓN DE RUBROS DE LA OBRA " + obra.nombre, catFont));
        addEmptyLine(preface, 1);
        Paragraph preface2 = new Paragraph();
        preface2.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
        addEmptyLine(preface2, 1);
        document.add(preface);
        document.add(preface2);

        def prmsTh = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsTd = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsEs = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 5]

        def pagAct = 1

        def rubrosText = "Rubros de la obra " + truncText(obra.nombre)

        rubros.each { rubro ->
            Paragraph paragraphRubro = new Paragraph();
            paragraphRubro.add(new Paragraph(rubro.nombre, fontTitle));

            PdfPTable tablaRubro = new PdfPTable(6);
            tablaRubro.setWidths(arregloEnteros([12, 24, 10, 24, 10, 20]))
            tablaRubro.setWidthPercentage(100);

            def ext = ""
            if (rubro.foto) {
                ext = rubro.foto.split("\\.")
                ext = ext[ext.size() - 1]
            }

            addCellTabla(tablaRubro, new Paragraph("Código", fontTh), prmsTh)
            addCellTabla(tablaRubro, new Paragraph(rubro.codigo, fontTd), prmsTd)
            addCellTabla(tablaRubro, new Paragraph("Unidad", fontTh), prmsTh)
            addCellTabla(tablaRubro, new Paragraph(rubro.unidad.descripcion, fontTd), prmsTd)
            addCellTabla(tablaRubro, new Paragraph("", fontTh), prmsTh)
            addCellTabla(tablaRubro, new Paragraph("", fontTd), prmsTd)

            addCellTabla(tablaRubro, new Paragraph("Fecha creación", fontTh), prmsTh)
            addCellTabla(tablaRubro, new Paragraph(rubro.fecha?.format("dd-MM-yyyy"), fontTd), prmsTd)
            addCellTabla(tablaRubro, new Paragraph("Fecha modificación", fontTh), prmsTh)
            addCellTabla(tablaRubro, new Paragraph(rubro.fechaModificacion?.format("dd-MM-yyyy"), fontTd), prmsTd)
            addCellTabla(tablaRubro, new Paragraph("", fontTh), prmsTh)
            addCellTabla(tablaRubro, new Paragraph("", fontTd), prmsTd)

            addCellTabla(tablaRubro, new Paragraph("Solicitante", fontTh), prmsTh)
            addCellTabla(tablaRubro, new Paragraph(rubro?.departamento?.subgrupo?.grupo?.descripcion, fontTd), prmsTd)
            addCellTabla(tablaRubro, new Paragraph("Grupo", fontTh), prmsTh)
            addCellTabla(tablaRubro, new Paragraph(rubro?.departamento?.subgrupo?.descripcion, fontTd), prmsTd)
            addCellTabla(tablaRubro, new Paragraph("Subgrupo", fontTh), prmsTh)
            addCellTabla(tablaRubro, new Paragraph(rubro?.departamento?.descripcion, fontTd), prmsTd)

            addCellTabla(tablaRubro, new Paragraph("Especificación", fontTh), prmsTh)
            addCellTabla(tablaRubro, new Paragraph(rubro?.especificaciones, fontTd), prmsEs)

            if (ext && ext != "") {
                def path = servletContext.getRealPath("/") + "rubros" + File.separatorChar + rubro.foto
                if (ext.toLowerCase() != 'pdf') {
                    def maxImageSize = 400
                    addCellTabla(tablaRubro, new Paragraph("Ilustración", fontTh), prmsTh)

                    def img = Image.getInstance(path);
                    if (img.getScaledWidth() > maxImageSize || img.getScaledHeight() > maxImageSize) {
                        img.scaleToFit(maxImageSize, maxImageSize);
                    }
                    addCellTabla(tablaRubro, img, prmsEs)
                    paragraphRubro.add(tablaRubro)
                    document.add(paragraphRubro);

                    infoText(cb, document, rubrosText, DOC)
                    infoText(cb, document, pagAct.toString(), PAG)
                    pagAct++
                } else {
                    PdfReader reader = new PdfReader(new FileInputStream(path));
                    def pages = reader.getNumberOfPages()
                    addCellTabla(tablaRubro, new Paragraph("Ilustración", fontTh), prmsTh)
                    addCellTabla(tablaRubro, new Paragraph("- PDF de ${pages} página${pages == 1 ? '' : 's'} adjunto a partir de la siguiente página -", fontTd), prmsTh)

                    paragraphRubro.add(tablaRubro)
                    document.add(paragraphRubro);

                    infoText(cb, document, rubrosText, DOC)
                    infoText(cb, document, pagAct.toString(), PAG)
                    pagAct++

                    pages.times {
                        document.newPage();
                        PdfImportedPage page = pdfw.getImportedPage(reader, it + 1);
                        cb.addTemplate(page, 0, 0);

                        infoText(cb, document, "Especificación del rubro " + truncText(rubro.nombre) + " pág. " + (it + 1) + "/" + pages, ADJ)
                        infoText(cb, document, rubrosText, DOC)
                        infoText(cb, document, pagAct.toString(), PAG)
                        pagAct++
                    }
                    document.newPage();

                }
            } else {
                paragraphRubro.add(tablaRubro)
                document.add(paragraphRubro);
                infoText(cb, document, rubrosText, DOC)
                infoText(cb, document, pagAct.toString(), PAG)
                pagAct++
            }
            document.newPage();
        }

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
        switch (formato) {
            case "MMM-yy":
                strFecha = meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yy")
                break;
        }
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
                periodoPlanilla = "Liquidación del reajuste (${planilla.fechaPresentacion.format('dd-MM-yyyy')})"
            } else {
                periodoPlanilla = 'del ' + planilla.fechaInicio.format('dd-MM-yyyy') + ' al ' + planilla.fechaFin.format('dd-MM-yyyy')
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
        def pa = PeriodoPlanilla.findAllByPlanilla(planilla)
        def periodos = PeriodoPlanilla.findAllByPlanillaInList(planillasAnteriores, [sort: "id"])

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
            addCellTabla(tablaB0, new Paragraph(per.titulo, fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaB0, new Paragraph(fechaConFormato(per.fechaIncio), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
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
                addCellTabla(tablaB0, new Paragraph(numero(valor, 2), fontTd), prmsNmBorder)
                def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${per.planilla.id} and periodoIndice =${per.periodo.id} and formulaPolinomica=${c.id}")
                if (vlrj.size() > 0) {
                    valor = vlrj.pop().valor
                } else {
                    println "error wtf no hay vlrj => from ValorReajuste where obra=${obra.id} and planilla=${per.planilla.id} and periodoIndice =${per.periodo.id} and formulaPolinomica=${c.id}"
                    valor = -1
                }
                addCellTabla(tablaB0, new Paragraph(numero(valor), fontTd), prmsNmBorder)
            }
        }

        addCellTabla(tablaB0, new Paragraph("TOTALES", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaB0, new Paragraph(numero(totalCoef), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        periodos.each { per ->
            addCellTabla(tablaB0, new Paragraph(numero(per.total), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: 2])
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
        addCellTabla(inner3, new Paragraph("Periodo de variación y aplicación de fórmula polinómica", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])

        PdfPTable inner4 = new PdfPTable(1);
        inner4.setExtendLastRow(true);
        def prg = new Paragraph("Componentes", fontTh)
        def str = " "
        if (periodos.size() == 2) {
            str += "\n "
        }
        addCellTabla(inner4, new Paragraph(str, fontTh), [bcb: Color.LIGHT_GRAY, border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner4, prg, [border: Color.BLACK, bg: Color.LIGHT_GRAY, bct: Color.LIGHT_GRAY, bcb: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner4, new Paragraph(" ", fontTh), [border: Color.BLACK, bct: Color.LIGHT_GRAY, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(inner4, new Paragraph("Anticipo", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFr, inner4, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        periodos.eachWithIndex { per, i ->
            if (i == 0) { //oferta
                PdfPTable inner5 = new PdfPTable(1);
                def prg2 = new Paragraph(per.titulo, fontTh)
                addCellTabla(inner5, new Paragraph(str, fontTh), [border: Color.BLACK, bcb: Color.LIGHT_GRAY, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, prg2, [border: Color.BLACK, bg: Color.LIGHT_GRAY, bct: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, new Paragraph(fechaConFormato(per.fechaIncio), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, new Paragraph(numero(per.planilla.contrato.porcentajeAnticipo, 0) + "%", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(tablaFr, inner5, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
//                addCellTabla(tablaFr, new Paragraph("asdf", fontTd), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            } else if (i == 1) { //anticipo
                PdfPTable inner5 = new PdfPTable(1);
                addCellTabla(inner5, new Paragraph(per.titulo, fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, new Paragraph(fechaConFormato(per.fechaIncio), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner5, new Paragraph(per.titulo, fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
                addCellTabla(inner3, inner5, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
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
                    } else {
                        vlinOferta = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
                        addCellTabla(tablaFr, new Paragraph(numero(p.valor) + "\n" + numero(vlinOferta, 2), fontTh), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
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

        addCellTabla(tablaFr, new Paragraph("1.000", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaFr, inner5, [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: periodos.size()])

        addCellTabla(tablaFr, new Paragraph("REAJUSTE TOTAL", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
        addCellTabla(tablaFr, new Paragraph(numero(reajusteTotal, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])

        if (periodos.size() > 2) {
            def planillaAnterior = planillasAnteriores.get(planillasAnteriores.size() - 2)
            def reajusteAnterior = (planillaAnterior.reajuste).toDouble().round(2)
            addCellTabla(tablaFr, new Paragraph("REAJUSTE ANTERIOR", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
            addCellTabla(tablaFr, new Paragraph(numero(reajusteAnterior, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE, colspan: periodos.size() - 1])
            addCellTabla(tablaFr, new Paragraph("REAJUSTE A PLANILLAR", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
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

            def totalMulta = 0

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
        }
        /* ***************************************************** Fin Multa retraso ********************************************************/

        /* ***************************************************** Detalles *****************************************************************/
        if (conDetalles) {
            document.newPage()

            PdfPTable tablaDetalles = new PdfPTable(11);
            tablaDetalles.setWidthPercentage(100);
            tablaDetalles.setWidths(arregloEnteros([12, 35, 5, 11, 11, 11, 11, 11, 11, 11, 11]))

            def borderWidth = 1

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

            tablaDetalles.setHeaderRows(6);
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
//        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(RESULT));
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

    def tablasPlanilla() {
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
//        def planillas = Planilla.findAllByContrato(contrato, [sort: "id"])

        def planillas = Planilla.withCriteria {
            and {
                eq("contrato", contrato)
                or {
                    lt("fechaInicio", planilla.fechaFin)
                    isNull("fechaInicio")
                }
                order("id", "asc")
            }
        }

        def oferta = contrato.oferta
        def fechaEntregaOferta = oferta.fechaEntrega - 30

        def periodoOferta = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(fechaEntregaOferta, fechaEntregaOferta)


        def periodos = [], periodosPlanilla = [], periodosTmp = []
        def data2 = [
                c: [:],
                p: [:]
        ]
        def fpB0 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "B0")

        def pcs = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                or {
                    ilike("numero", "c%")
                    and {
                        ne("numero", "P0")
                        ilike("numero", "p%")
                    }
                }
                order("numero", "asc")
            }
        }

        def planillaAnticipo = Planilla.findByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo("A"))
        def fechaPlanillaAnticipo
        // utilizamos siempre la fecha de presentacion del anticipo y se hace una planilla de reajuste final
        // para reajustar el anticipo a la fecha de pago en las planillas de avnace descomentar esta parte
//        if (planilla.tipoPlanilla.codigo == "A") {
        fechaPlanillaAnticipo = planillaAnticipo.fechaPresentacion
//        } else {
//            fechaPlanillaAnticipo = planillaAnticipo.fechaPago
//        }
        def periodoAnticipo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(fechaPlanillaAnticipo, fechaPlanillaAnticipo)

//        println pcs.numero

        //llena el arreglo de periodos
        //el periodo que corresponde a la fecha de entrega de la oferta
        periodos.add(periodoOferta)
        periodos.add(periodoAnticipo)

        planillas.each { pl ->
            if (pl.tipoPlanilla.codigo == 'A') {
                //si es anticipo: el periodo q corresponde a la fecha del anticipo
//                def prin = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(pl.fechaPresentacion, pl.fechaPresentacion)
//                periodos.add(prin)
            } else {
//                println pl.id + "   " + pl.fechaInicio.format("dd-MM-yyyy") + " a " + pl.fechaFin.format("dd-MM-yyyy")
                def m1 = pl.fechaInicio.format("MM")
                def m2 = pl.fechaFin.format("MM")
//                println "\t" + m1 + "   " + m2
                if (m1 == m2) {
                    if (pl.periodoIndices) {
//                        println "++ " + pl.periodoIndices
                        periodosTmp.add(pl.periodoIndices)
                    }
                    if (pl == planilla) {
//                        println "-- " + pl
                        periodosPlanilla.add(pl)
                    }
                } else {
                    def y = pl.fechaInicio.format("yyyy")
                    (m1.toInteger()..m2.toInteger()).each {
//                        println "\t\t" + it + " - " + y
                        def fi = new Date().parse("dd-MM-yyyy", "01-" + it + "-" + y)
                        def prin = PeriodosInec.findByFechaInicio(fi)
                        if (prin) {
                            periodosTmp.add(prin)
                        }
                        if (pl == planilla) {
                            periodosPlanilla.add(prin)
                        }
                        if (it == 12) {
                            y = pl.fechaFin.format("yyyy")
                        }
                    }
                }
            }
        }
//        println periodos
//        render periodos.descripcion
        periodosTmp = periodosTmp.unique()
        periodos = periodos + periodosTmp

//        println "Aqui empieza las inserciones"
        periodos.eachWithIndex { per, perNum ->
            def pl = planilla
            if (perNum == 0) {
                pl = null
            }
            def valRea = ValorReajuste.findAllByObraAndPeriodoIndice(obra, per)
//            println ">>>>>>>>>>>>>>>>>>>obra: " + obra.id
//            println ">>>>>>>>>>>>>>>>>>>per: " + per.id
//            println ">>>>>>>>>>>>>>>>>>>valRea: " + valRea

//            println ">>>Periodo " + per.descripcion + " (${perNum}) hay " + valRea.size() + " valRea: " + valRea.id + " (obra: ${obra.id})"

            def tot = [c: 0, p: 0]
            //si no existen valores de reajuste, se crean

            if (valRea.size() == 0) {
                println "No hay valores de reajuste????"
            } //valRea.size == 0
            else {
                valRea.each { v ->
                    def c = pcs.find { it.indice == v.formulaPolinomica.indice }
                    if (c) {
                        def val = ValorIndice.findByPeriodoAndIndice(per, c.indice)?.valor
                        if (!val) {
                            val = 1
                        }
//                        println "\t\t"+per.descripcion+"   "+c.indice+"   "+val+"   "+c.id
                        def pos = "p"
                        if (c.numero.contains("c")) {
                            pos = "c"
                        }
                        tot[pos] += (val * c.valor).round(3)
//                        println "\t\t" + pos + "   " + (v.valor * c.valor)
                        if (!data2[pos][perNum]) {
//                            println "\t\tCrea data2[${pos}][${perNum}]"
                            data2[pos][perNum] = [valores: [], total: 0, periodo: per]
                        }

                        data2[pos][perNum]["valores"].add([formulaPolinomica: c, valorReajuste: v, valorTabla: val])
                    }
                }
            } //valRea.size == 0

//            println "data[c][${perNum}][total]=${tot['c']}"
            println "tot[c]: ${tot['c']}, ${tot['p']}"
            data2["c"][perNum]["total"] = tot["c"]
            println "${data2["c"][perNum]["total"]}"
//            println "data[p][${perNum}][total]=${tot['p']}"
            data2["p"][perNum]["total"] = tot["p"]
            def vrB0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, fpB0)
            if (!vrB0) {
                println "No hay valor de reajuste de B0??"
            }
            def p01 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "p01")
            if (p01) {
                def vrP01 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p01)
            }
        }

        def tableWidth = 150 * periodos.size() + 0

        def tablaBo = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px;'>"
        tablaBo += "<thead>"
        tablaBo += "<tr>"
        tablaBo += "<th colspan=\"2\">Cuadrilla Tipo</th>"
        tablaBo += "<th>Oferta</th>"
        tablaBo += "<th class='nb'>" + oferta.fechaEntrega.format("MMM-yy") + "</th>"
        tablaBo += "<th>Variación</th>"
        tablaBo += "<th class='nb'>Anticipo (" + planillaAnticipo.fechaPresentacion.format("MMM-yy") + ")</th>"
        if (periodos.size() > 2) {
            periodos.eachWithIndex { per, i ->
                if (i > 1) {
                    tablaBo += "<th>Variación</th>"
                    tablaBo += "<th class='nb'>" + per?.descripcion + "</th>"
                }
            }
        }
        tablaBo += "</tr>"
        tablaBo += "</thead>"


        def tbodyB0 = "<tbody>"
        def totC = 0
        pcs.findAll { it.numero.contains("c") }.each { c ->
            tbodyB0 += "<tr>"
            tbodyB0 += "<td>" + c.indice.descripcion + " (" + c.numero + ")</td>"
            tbodyB0 += "<td class='number'>" + elm.numero(number: c.valor, decimales: 3) + "</td>"
            totC += c.valor
            data2.c.each { cp ->
                def act = cp.value.valores.find { it.formulaPolinomica.indice == c.indice }
//                def val = act.valorReajuste.valor
                def val = act.valorTabla
                def valReajuste = act.valorReajuste.valor
                tbodyB0 += "<td class='number'>" + elm.numero(number: val, decimales: 3) + "</td>"
                tbodyB0 += "<td class='number'>" + elm.numero(number: valReajuste, decimales: 3) + "</td>"
            }
            tbodyB0 += "</tr>"
        }
        tbodyB0 += "</tbody>"
        tbodyB0 += "<tfoot>"
        tbodyB0 += "<tr>"
        tbodyB0 += "<th>TOTALES</th>"
        tbodyB0 += "<td class='number bold'>" + elm.numero(number: totC, decimales: 3) + "</td>"
        data2.c.each { cp ->
            tbodyB0 += "<td></td>"
            tbodyB0 += "<td class='number bold'>" + elm.numero(number: cp.value.total, decimales: 3) + "</td>"
        }
        tbodyB0 += "</tr>"
        tbodyB0 += "</tfoot>"

        tablaBo += tbodyB0
        tablaBo += "</table>"

        def tablaP0 = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;' >"
        tablaP0 += '<thead>'
        tablaP0 += '<tr>'
        tablaP0 += '<th colspan="2" rowspan="2">Mes y año</th>'
        tablaP0 += '<th colspan="2">Cronograma</th>'
        tablaP0 += '<th colspan="2">Planillado</th>'
        tablaP0 += '<th colspan="2" rowspan="2">Valor P<sub>0</sub></th>'
        tablaP0 += '</tr>'
        tablaP0 += '<tr>'
        tablaP0 += '<th>Parcial</th>'
        tablaP0 += '<th>Acumulado</th>'
        tablaP0 += '<th>Parcial</th>'
        tablaP0 += '<th>Acumulado</th>'
        tablaP0 += '</tr>'
        tablaP0 += '</thead>'

        def p0s = []
        def tbodyP0 = "<tbody>"
        def tbodyMl = "<tbody>"

        def diasPlanilla = 0

        if (planilla.tipoPlanilla.codigo != "A") {
            diasPlanilla = planilla.fechaFin - planilla.fechaInicio + 1
        }
        def valorPlanilla = planilla.valor

        def acumuladoCrono = 0, acumuladoPlan = 0

        def diasAll = 0

        def totalMultaRetraso = 0, valorTotalPeriodoActual = 0

//        println "periodos "+periodos

        periodos.eachWithIndex { per, i ->
            if (i > 0) {
                def planillaActual = Planilla.findByPeriodoIndicesAndContrato(per, contrato)
                tbodyP0 += "<tr>"
                if (i == 1) {
                    tbodyP0 += "<th>ANTICIPO</th>"
                    tbodyP0 += "<th>"
                    tbodyP0 += planillaAnticipo.fechaPresentacion.format("MMM-yy")
                    tbodyP0 += "</th>"
                    tbodyP0 += "<td colspan='4'></td>"
                    tbodyP0 += "<td class='number'>"
                    tbodyP0 += elm.numero(number: planillaAnticipo.valor)
                    tbodyP0 += "</td>"

                    def p0 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "P0")
                    def vrP0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p0)
                    if (!vrP0) {
                        println "No hay valor reajuste de P0???"
                    }
                    data2["p"][i]["p0"] = vrP0.valor
                    p0s[i - 1] = vrP0.valor
                } else {
//                    def periodosEjecucion = PeriodoEjecucion.findAllByObra(obra)
//                    def periodosEjecucion = PeriodoEjecucion.withCriteria {
//                        and {
//                            eq("obra", obra)
//                                or {
//                                    between("fechaInicio", per.fechaInicio, per.fechaFin)
//                                    between("fechaFin", per.fechaInicio, per.fechaFin)
//                                }
//                            order("fechaInicio")
//                        }
//                    }
//                    println "planilla fin: "+planilla.fechaFin

                    def periodosEjecucion = PeriodoEjecucion.withCriteria {
                        and {
                            eq("obra", obra)
                            or {
                                between("fechaInicio", per.fechaInicio, per.fechaFin)
                                between("fechaFin", per.fechaInicio, per.fechaFin)
                            }
                            le("fechaFin", planilla.fechaFin)
                            order("fechaInicio")
                        }
                    }


                    def diasTotal = 0, valorTotal = 0
//                    println "\t\t++ "+per.fechaInicio.format("dd-MM-yyyy") + "  " + per.fechaFin.format("dd-MM-yyyy")
//                    println "\t\t-- "+periodosEjecucion

                    tbodyP0 += "<th>"
                    tbodyP0 += per?.descripcion
                    tbodyP0 += "</th>"
                    periodosEjecucion.each { pe ->
//                        println "\t" + pe.tipo + "  " + pe.fechaInicio.format("dd-MM-yyyy") + "   " + pe.fechaFin.format("dd-MM-yyyy")
                        if (pe.tipo == "P") {
                            def diasUsados = 0
                            def diasPeriodo = pe.fechaFin - pe.fechaInicio + 1
//                            println "\t\tdias periodo: " + diasPeriodo
                            def crono = CronogramaEjecucion.findAllByPeriodo(pe)
                            def valorPeriodo = crono.sum { it.precio }
//                            println "\t\tvalor periodo: " + valorPeriodo
                            if (per) {
                                if (pe.fechaInicio <= per.fechaInicio) {
                                    diasUsados = pe.fechaFin - per.fechaInicio + 1
//                                if (diasUsados == 0) diasUsados = 1
                                    diasTotal += diasUsados
//                                println "\t\t1 dias usados: " + diasUsados
                                } else if (pe.fechaInicio > per.fechaInicio && pe.fechaFin < per.fechaFin) {
                                    diasUsados = pe.fechaFin - pe.fechaInicio + 1
//                                if (diasUsados == 0) diasUsados = 1
                                    diasTotal += diasUsados
//                                println "\t\t2 dias usados: " + diasUsados
                                } else if (pe.fechaFin >= per.fechaFin) {
                                    diasUsados = per.fechaFin - pe.fechaInicio + 1
//                                if (diasUsados == 0) diasUsados = 1
                                    diasTotal += diasUsados
//                                println "\t\t3 dias usados: " + diasUsados
                                }
                            }
                            def valorUsado = (valorPeriodo / diasPeriodo) * diasUsados
                            valorTotal += valorUsado
//                            println "\t\tvalor usado: " + valorUsado
                        }
                    }
                    acumuladoCrono += valorTotal
//                    println "***** "+valorPlanilla+"  "+diasPlanilla+"  "+diasTotal
                    def planillado = (valorPlanilla / diasPlanilla) * diasTotal
                    acumuladoPlan += planillado
//                    println "TOTAL: " + diasTotal + " dias"
//                    println "PLANILLADO: " + planillado

                    def p0 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "P0")
                    def vrP0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p0)
                    if (!vrP0) {
                        println "No hay valor reajuste de P0???"
                    }
                    data2["p"][i]["p0"] = vrP0.valor
                    p0s[i - 1] = vrP0.valor
                    diasAll += diasTotal
                    tbodyP0 += "<th>"
                    tbodyP0 += "(" + diasTotal + ")"
                    tbodyP0 += "</th>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: valorTotal) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: acumuladoCrono) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: planillado) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: acumuladoPlan) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: vrP0.valor) + "</td>"

                    if (periodosPlanilla.contains(per)) {
                        def multa = 0
                        def retraso = 0
                        if (valorTotal > planillado) {
                            def totalContrato = contrato.monto
                            def prmlMulta = contrato.multaPlanilla
                            def valorDia = valorTotal / diasTotal
                            retraso = (valorTotal - planillado) / valorDia
                            multa = (totalContrato) * (prmlMulta / 1000) * retraso
                        }
                        if (per == planilla.periodoIndices) {
                            valorTotalPeriodoActual = valorTotal
                        }
                        totalMultaRetraso += multa
                        tbodyMl += "<tr>"
                        tbodyMl += "<th>"
                        tbodyMl += per.descripcion
                        tbodyMl += "</th>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: valorTotal) + "</td>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: planillado) + "</td>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: retraso) + "</td>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: multa) + "</td>"
                        tbodyMl += "</tr>"
                    }
                }
                tbodyP0 += "</tr>"
            }
        }
        tbodyMl += "</tbody>"
        tbodyP0 += "</tbody>"
        if (planilla.tipoPlanilla.codigo != "A") {
            tbodyP0 += "<tfoot>"
            tbodyP0 += "<tr>"
            tbodyP0 += "<th>TOTAL</th>"
            tbodyP0 += "<th>(" + diasAll + ")</th>"
            tbodyP0 += "<td></td>"
            tbodyP0 += "<td class='number bold'>" + elm.numero(number: acumuladoCrono) + "</td>"
            tbodyP0 += "<td></td>"
            tbodyP0 += "<td class='number bold'>" + elm.numero(number: acumuladoPlan) + "</td>"
            tbodyP0 += "<td></td>"
            tbodyP0 += "</tr>"
            tbodyP0 += "</tfoot>"
        }

        tablaP0 += tbodyP0
        tablaP0 += "</table>"

        def tablaFr = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;'>"
        tablaFr += '<thead>'
        tablaFr += '<tr>'
        tablaFr += '<th rowspan="2">Componentes</th>'
        tablaFr += '<th>Oferta</th>'
        tablaFr += '<th colspan="' + (periodos.size() - 1) + '">Periodo de variación y aplicación de fórmula polinómica</th>'
        tablaFr += '</tr>'
        tablaFr += '<tr>'
        tablaFr += '<th>' + (oferta.fechaEntrega.format("MMM-yy")) + '</th>'
        tablaFr += '<th>Anticipo <br/>' + planillaAnticipo.fechaPresentacion.format("MMM-yy") + '</th>'
        if (periodos.size() > 2) {
            periodos.eachWithIndex { per, i ->
                if (i > 1) {
                    tablaFr += '<th rowspan="2">' + per?.descripcion + '</th>'
                }
            }
        }
        tablaFr += '</tr>'
        tablaFr += '<tr>'
        tablaFr += '<th>Anticipo</th>'
        tablaFr += '<th>'
        tablaFr += elm.numero(number: contrato.porcentajeAnticipo, decimales: 0) + "%"
        tablaFr += '</th>'
        tablaFr += '<th>Anticipo</th>'
        tablaFr += '</tr>'
        tablaFr += '</thead>'

        def a = 0, b = 0, c = 0, d = 0, tots = []
        def tbodyFr = "<tbody>"
        pcs.findAll { it.numero.contains('p') }.eachWithIndex { p, i ->
            tbodyFr += "<tr>"
            tbodyFr += "<td>" + p.indice.descripcion + " (" + p.numero + ")</td>"

            data2.p.eachWithIndex { cp, j ->
                def act = cp.value.valores.find { it.formulaPolinomica.indice == p.indice }
                if (j == 0) {
                    c = act.formulaPolinomica.valor
                    b = act.valorTabla
                    if (i == 0) {
                        b = act.valorReajuste.valor
                    }
                    tbodyFr += "<td class='number'>"
                    tbodyFr += "<div>"
                    tbodyFr += elm.numero(number: c, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "<div class='bold'>"
                    tbodyFr += elm.numero(number: b, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "</td>"
                } //j==0
                else {
                    a = act.valorReajuste.valor
                    if (i > 0) {
                        a = act.valorTabla
                    }
                    d = (a / b) * c
                    tbodyFr += "<td class='number'>"
                    tbodyFr += "<div>"
                    tbodyFr += elm.numero(number: a, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "<div class='bold'>"
                    tbodyFr += elm.numero(number: d, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "</td>"
                    if (!tots[j - 1]) {
                        tots[j - 1] = [
                                per: cp.value.periodo,
                                total: 0
                        ]
                    }
                    tots[j - 1].total += d
                }
            } //data.p.each
            tbodyFr += "</tr>"
        } //pcs.p.each

        def filaFr = "", filaFr1 = "", filaP0 = "", filaPr = ""
//        println ">>>"
//        println p0s

        def totalReajuste = 0

        tots.eachWithIndex { t, i ->
            def fpFr = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "Fr")
            def vrFr1 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(t.per, fpFr)
            if (!vrFr1) {
                println "NO hay valor de reajuste para Fr??"
            }
            def pr = (t.total - 1).round(3) * p0s[i]

            totalReajuste += pr
            filaFr += "<td class='number'>" + elm.numero(number: t.total, decimales: 3) + "</td>"
            filaFr1 += "<td class='number'>" + elm.numero(number: t.total - 1, decimales: 3) + "</td>"
            filaP0 += "<td class='number'>" + elm.numero(number: p0s[i]) + "</td>"
            filaPr += "<td class='number'>" + elm.numero(number: pr) + "</td>"
        }

        tbodyFr += "</tbody>"
        tbodyFr += "<tfoot>"

        tbodyFr += "<tr>"
        tbodyFr += "<th rowspan='4'>1.000</th>"
        tbodyFr += "<th>F<sub>r</sub></th>"
        tbodyFr += filaFr
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th>F<sub>r</sub>-1</th>"
        tbodyFr += filaFr1
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th>P<sub>0</sub></th>"
        tbodyFr += filaP0
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th>P<sub>r</sub>-P</th>"
        tbodyFr += filaPr
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th colspan='2'>REAJUSTE TOTAL</th>"
        tbodyFr += "<td colspan='${periodos.size()}' class='number bold'>"
        tbodyFr += elm.numero(number: totalReajuste)
        tbodyFr += "</td>"
        tbodyFr += "</tr>"

        tbodyFr += "</tfoot>"


        tablaFr += tbodyFr
        tablaFr += "</table>"

        def smallTableWidth = 400

        def tablaMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
        tablaMl += '<thead>'
        tablaMl += '<tr>'
        tablaMl += '<th>Mes y año</th>'
        tablaMl += '<th>Cronograma</th>'
        tablaMl += '<th>Planillado</th>'
        tablaMl += '<th>Retraso</th>'
        tablaMl += '<th>Multa</th>'
        tablaMl += '</tr>'
        tablaMl += '</thead>'

        tablaMl += tbodyMl

        tablaMl += "<tfoot>"
        tablaMl += '<th >Total</th>'
        tablaMl += '<td colspan="3"></td>'
        tablaMl += '<td class="bold number">'
        tablaMl += elm.numero(number: totalMultaRetraso)
        tablaMl += '</td>'
        tablaMl += "</tfoot>"

        tablaMl += "</table>"

        def pMl = ""

        if (planilla.periodoIndices) {
            def diasMax = 5
            def fechaFinPer = planilla.periodoIndices?.fechaFin
            def fechaMax = fechaFinPer
//            use(TimeCategory) {
//                fechaMax = fechaFinPer + diasMax.days
//            }

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
            def retraso = fechaPresentacion - fechaMax

            def totalMulta = 0

            def totalContrato = contrato.monto
            def prmlMulta = contrato.multaPlanilla
            if (retraso > 0) {
//            totalMulta = (totalContrato) * (prmlMulta / 1000) * retraso
                totalMulta = (valorTotalPeriodoActual) * (prmlMulta / 1000) * retraso
            } else {
                retraso = 0
            }

            pMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
            pMl += "<tr>"
            pMl += '<th class="tal">Fecha presentación planilla</th><td>' + fechaPresentacion.format("dd-MM-yyyy") + ' </td>'
            pMl += "</tr>"
            pMl += "<tr>"
            pMl += '<th class="tal">Periodo planilla</th><td>' + planilla.fechaInicio.format("dd-MM-yyyy") + " a " + planilla.fechaFin.format("dd-MM-yyyy") + ' </td>'
            pMl += "</tr>"
            pMl += "<tr>"
            pMl += '<th class="tal">Fecha máximo presentación</th> <td>' + fechaMax.format("dd-MM-yyyy") + ' </td>'
            pMl += "</tr>"
            pMl += "<tr>"
            pMl += '<th class="tal">Días de retraso</th> <td>' + retraso + "</td>"
            pMl += "</tr>"
            pMl += "<tr>"
            pMl += '<th class="tal">Multa</th> <td>' + elm.numero(number: prmlMulta) + "‰ de \$" + elm.numero(number: totalContrato) + "</td>"
            pMl += "</tr>"
            pMl += "<tr>"
            pMl += '<th class="tal">Total multa</th> <td>$' + elm.numero(number: totalMulta) + "</td>"
            pMl += "</tr>"
            pMl += '</table>'
        }


        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])

        def precios = [:]
        def indirecto = obra.totales / 100

        preciosService.ac_rbroObra(obra.id)

        detalle.each {
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
        }

        def planillasAnteriores = Planilla.withCriteria {
            eq("contrato", contrato)
            lt("fechaFin", planilla.fechaInicio)
        }
//        println planillasAnteriores


        return [tablaB0: tablaBo, tablaP0: tablaP0, tablaFr: tablaFr, tablaMl: tablaMl, pMl: pMl, planilla: planilla, obra: obra, oferta: oferta, contrato: contrato, detalle: detalle, precios: precios, planillasAnteriores: planillasAnteriores, imprimeDetalle: params.imprimeDetalle]
    }




    def imprimirRubrosConsolidado() {
//        println "rep rubros "+params
        def rubros = []

        def parts = params.id.split("_")

        switch (parts[0]) {
            case "sg":
                def departamentos = DepartamentoItem.findAllBySubgrupo(SubgrupoItems.get(parts[1].toLong()))
                rubros = Item.findAllByDepartamentoInList(departamentos, [sort: "nombre"])
                break;
            case "dp":
                rubros = Item.findAllByDepartamento(DepartamentoItem.get(parts[1].toLong()), [sort: "nombre"])
                break;
            case "rb":
                rubros = [Item.get(parts[1].toLong())]
                break;
        }

        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        def lugar = params.lugar
        def indi = params.indi
        def listas = params.listas
        try {
            indi = indi.toDouble()
        } catch (e) {
            println "error parse " + e
            indi = 21.5
        }

        def datos = []
        rubros.each { rubro ->
            def parametros = "" + rubro.id + ",'" + fecha.format("yyyy-MM-dd") + "'," + listas + "," + params.dsp0 + "," + params.dsp1 + "," + params.dsv0 + "," + params.dsv1 + "," + params.dsv2 + "," + params.chof + "," + params.volq
            preciosService.ac_rbroV2(rubro.id, fecha.format("yyyy-MM-dd"), params.lugar)
            def res = preciosService.rb_precios(parametros, "")
            def temp = [:]
            def total = 0
            res.each { r ->
                total += r["parcial"] + r["parcial_t"]
            }
            total = total * (1 + indi / 100)
            temp.put("codigo", rubro.codigo)
            temp.put("nombre", rubro.nombre)
            temp.put("unidad", rubro.unidad.codigo)
            temp.put("total", total)
            datos.add(temp)
//            println "res "+res

        }
        def l = listas.split(",")
//        println "listas str "+listas+" "+l
        listas = []
        l.each {
            listas.add(Lugar.get(it).descripcion)
        }
//        println "listas "+listas

        [datos: datos, fecha: fecha, indi: indi, params: params, listas: listas]
    }


    def tablasPlanilla_bck() {
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def oferta = contrato.oferta
        def planillas = Planilla.withCriteria {
            and {
                eq("contrato", contrato)
                or {
                    lt("fechaInicio", planilla.fechaFin)
                    isNull("fechaInicio")
                }
                order("id", "asc")
            }
        }
        def periodoOferta = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(oferta.fechaEntrega, oferta.fechaEntrega)
//        println "periodoOferta" + periodoOferta
        def periodos = []
        def data = [
                c: [:],
                p: [:]
        ]
        def pcs = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                or {
                    ilike("numero", "c%")
                    and {
                        ne("numero", "P0")
                        ilike("numero", "p%")
                    }
                }
                order("numero", "asc")
            }
        }
        periodos.add(periodoOferta)
        planillas.each { pl ->
            if (pl.tipoPlanilla.codigo == 'A') {
                //si es anticipo: el periodo q corresponde a la fecha del anticipo
                def prin = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(pl.fechaPresentacion, pl.fechaPresentacion)
                periodos.add(prin)
            } else {
                periodos.add(pl.periodoIndices)
            }
        }
        periodos.eachWithIndex { per, perNum ->
            def valRea = ValorReajuste.findAllByObraAndPeriodoIndice(obra, per)
            def tot = [c: 0, p: 0]
            valRea.each { v ->
                def c = pcs.find { it.indice == v.formulaPolinomica.indice }
                if (c) {
                    def pos = "p"
                    if (c.numero.contains("c")) {
                        pos = "c"
                    }
                    tot[pos] += (v.valor * c.valor).round(3)
//                        println "\t\t" + pos + "   " + (v.valor * c.valor)
                    if (!data[pos][per]) {
                        data[pos][per] = [valores: [], total: 0]
                    }
                    data[pos][per]["valores"].add([formulaPolinomica: c, valorReajuste: v])
                }
            }
            data["c"][per]["total"] = tot["c"]
            data["p"][per]["total"] = tot["p"]
        }//periodos.each

        def tbodyB0 = "<tbody>"
        def totC = 0
        pcs.findAll { it.numero.contains("c") }.each { c ->
            tbodyB0 += "<tr>"
            tbodyB0 += "<td>" + c.indice.descripcion + " (" + c.numero + ")</td>"
            tbodyB0 += "<td class='number'>" + elm.numero(number: c.valor, decimales: 3) + "</td>"
            totC += c.valor
            data.c.each { cp ->
                def act = cp.value.valores.find { it.formulaPolinomica.indice == c.indice }
                def val = act.valorReajuste.valor
                tbodyB0 += "<td class='number'>" + elm.numero(number: val) + "</td>"
                tbodyB0 += "<td class='number'>" + elm.numero(number: val * c.valor, decimales: 3) + "</td>"
            }
            tbodyB0 += "</tr>"
        }
        tbodyB0 += "<tr>"
        tbodyB0 += "<th>TOTALES</th>"
        tbodyB0 += "<td class='number'>" + elm.numero(number: totC, decimales: 3) + "</td>"
        data.c.each { cp ->
            tbodyB0 += "<td></td>"
            tbodyB0 += "<td class='number'>" + elm.numero(number: cp.value.total) + "</td>"
        }
        tbodyB0 += "</tr>"
        tbodyB0 += "</tbody>"

        def p0s = []
        def tbodyP0 = "<tbody>"
//        def diasPlanilla = planilla.fechaFin - planilla.fechaInicio
        def diasPlanilla = 0

        if (planilla.tipoPlanilla.codigo != "A") {
            diasPlanilla = planilla.fechaFin - planilla.fechaInicio
        }
        def valorPlanilla = planilla.valor

        def acumuladoCrono = 0, acumuladoPlan = 0

        def diasAll = 0

        periodos.eachWithIndex { per, i ->
            if (i > 0) {
                tbodyP0 += "<tr>"
                if (i == 1) {
                    tbodyP0 += "<th>ANTICIPO</th>"
                    tbodyP0 += "<th>"
                    def planillaAnticipo = Planilla.findByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo("A"))
                    tbodyP0 += planillaAnticipo.fechaPresentacion.format("MMM-yy")
                    tbodyP0 += "</th>"
                    tbodyP0 += "<td colspan='4'></td>"
                    tbodyP0 += "<td class='number'>"
                    tbodyP0 += elm.numero(number: planillaAnticipo.valor)
                    tbodyP0 += "</td>"

                    def p0 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "P0")
                    def vrP0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p0)
                    data["p"][per]["p0"] = vrP0.valor
                    p0s[i - 1] = vrP0.valor
                } else {
//                    def periodosEjecucion = PeriodoEjecucion.findAllByObra(obra)
                    def periodosEjecucion = PeriodoEjecucion.withCriteria {
                        and {
                            eq("obra", obra)
                            or {
                                between("fechaInicio", per.fechaInicio, per.fechaFin)
                                between("fechaFin", per.fechaInicio, per.fechaFin)
                            }
                            order("fechaInicio")
                        }
                    }
                    def diasTotal = 0, valorTotal = 0
//                    println per.fechaInicio.format("dd-MM-yyyy") + "  " + per.fechaFin.format("dd-MM-yyyy")
                    tbodyP0 += "<th>"
                    tbodyP0 += per.descripcion
                    tbodyP0 += "</th>"
                    periodosEjecucion.each { pe ->
//                        println "\t" + pe.tipo + "  " + pe.fechaInicio.format("dd-MM-yyyy") + "   " + pe.fechaFin.format("dd-MM-yyyy")
                        if (pe.tipo == "P") {
                            def diasUsados
                            def diasPeriodo = pe.fechaFin - pe.fechaInicio
//                            println "\t\tdias periodo: " + diasPeriodo
                            def crono = CronogramaEjecucion.findAllByPeriodo(pe)
                            def valorPeriodo = crono.sum { it.precio }
//                            println "\t\tvalor periodo: " + valorPeriodo
                            if (pe.fechaInicio <= per.fechaInicio) {
                                diasUsados = pe.fechaFin - per.fechaInicio
                                if (diasUsados == 0) diasUsados = 1
                                diasTotal += diasUsados
//                                println "\t\tdias usados: " + diasUsados
                            } else if (pe.fechaInicio > per.fechaInicio && pe.fechaFin < per.fechaFin) {
                                diasUsados = pe.fechaFin - pe.fechaInicio
                                if (diasUsados == 0) diasUsados = 1
                                diasTotal += diasUsados
//                                println "\t\tdias usados: " + diasUsados
                            } else if (pe.fechaFin >= per.fechaFin) {
                                diasUsados = per.fechaFin - pe.fechaInicio
                                if (diasUsados == 0) diasUsados = 1
                                diasTotal += diasUsados
//                                println "\t\tdias usados: " + diasUsados
                            }
                            def valorUsado = (valorPeriodo / diasPeriodo) * diasUsados
                            valorTotal += valorUsado
//                            println "\t\tvalor usado: " + valorUsado
                        }
                    }
                    acumuladoCrono += valorTotal
                    def planillado = (valorPlanilla / diasPlanilla) * diasTotal
                    acumuladoPlan += planillado
//                    println "TOTAL: " + diasTotal + " dias"
//                    println "PLANILLADO: " + planillado

                    def p0 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "P0")
                    def vrP0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p0)
                    data["p"][per]["p0"] = vrP0.valor
                    p0s[i - 1] = vrP0.valor
                    diasAll += diasTotal
                    tbodyP0 += "<th>"
                    tbodyP0 += "(" + diasTotal + ")"
                    tbodyP0 += "</th>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: valorTotal) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: acumuladoCrono) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: planillado) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: acumuladoPlan) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: vrP0.valor) + "</td>"
                }
                tbodyP0 += "</tr>"
            }
        }
        tbodyP0 += "</tbody>"
        tbodyP0 += "<tfoot>"
        tbodyP0 += "<tr>"
        tbodyP0 += "<th>TOTAL</th>"
        tbodyP0 += "<th>(" + diasAll + ")</th>"
        tbodyP0 += "<td></td>"
        tbodyP0 += "<td class='number bold'>" + elm.numero(number: acumuladoCrono) + "</td>"
        tbodyP0 += "<td></td>"
        tbodyP0 += "<td class='number bold'>" + elm.numero(number: acumuladoPlan) + "</td>"
        tbodyP0 += "<td></td>"
        tbodyP0 += "</tr>"
        tbodyP0 += "</tfoot>"

        def a = 0, b = 0, c = 0, d = 0, tots = []
        def tbodyFr = "<tbody>"
        pcs.findAll { it.numero.contains('p') }.eachWithIndex { p, i ->
            tbodyFr += "<tr>"
            tbodyFr += "<td>" + p.indice.descripcion + " (" + p.numero + ")</td>"

            data.p.eachWithIndex { cp, j ->
                def act = cp.value.valores.find { it.formulaPolinomica.indice == p.indice }
                if (j == 0) {
                    c = act.formulaPolinomica.valor
                    b = act.valorReajuste.valor
                    tbodyFr += "<td class='number'>"
                    tbodyFr += "<div>"
                    tbodyFr += elm.numero(number: c, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "<div class='bold'>"
                    tbodyFr += elm.numero(number: b, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "</td>"
                } //j==0
                else {
                    a = act.valorReajuste.valor
                    d = (a / b) * c
                    tbodyFr += "<td class='number'>"
                    tbodyFr += "<div>"
                    tbodyFr += elm.numero(number: a, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "<div class='bold'>"
                    tbodyFr += elm.numero(number: d, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "</td>"
                    if (!tots[j - 1]) {
                        tots[j - 1] = [
                                per: cp.key,
                                total: 0
                        ]
                    }
                    tots[j - 1].total += d
                }
            } //data.p.each
            tbodyFr += "</tr>"
        } //pcs.p.each

        def filaFr = "", filaFr1 = "", filaP0 = "", filaPr = ""
//        println ">>>"
//        println p0s

        def totalReajuste = 0

        tots.eachWithIndex { t, i ->
            def pr = (t.total - 1) * p0s[i]
            totalReajuste += pr
            filaFr += "<td class='number'>" + elm.numero(number: t.total, decimales: 3) + "</td>"
            filaFr1 += "<td class='number'>" + elm.numero(number: t.total - 1, decimales: 3) + "</td>"
            filaP0 += "<td class='number'>" + elm.numero(number: p0s[i]) + "</td>"
            filaPr += "<td class='number'>" + elm.numero(number: pr) + "</td>"
        }
        tbodyFr += "</tbody>"
        tbodyFr += "<tfoot>"

        tbodyFr += "<tr>"
        tbodyFr += "<th rowspan='4'>1.000</th>"
        tbodyFr += "<th>F<sub>r</sub></th>"
        tbodyFr += filaFr
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th>F<sub>r</sub>-1</th>"
        tbodyFr += filaFr1
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th>P<sub>0</sub></th>"
        tbodyFr += filaP0
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th>P<sub>r</sub>-P</th>"
        tbodyFr += filaPr
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th colspan='2'>REAJUSTE TOTAL</th>"
        tbodyFr += "<td colspan='2' class='number bold'>"
        tbodyFr += elm.numero(number: totalReajuste)
        tbodyFr += "</td>"
        tbodyFr += "</tr>"

        tbodyFr += "</tfoot>"

        return [tbodyFr: tbodyFr, tbodyP0: tbodyP0, tbodyB0: tbodyB0, planilla: planilla, obra: obra, oferta: oferta, contrato: contrato, pcs: pcs, data: data, periodos: periodos]

    } //tablasPlanilla()


    def reportePrecios() {
//        params.orden = "a" //a,n    Alfabetico | Numerico
//        params.col = ["t", "u", "p", "f"] //t,u,p,f   Transporte | Unidad | Precio | Fecha de Act
//        params.fecha = "22-11-2012"
//        params.lugar = "4"
//        params.grupo = "1"
//
//        println "params" + params

        def grupo = Grupo.get(params.grupo.toLong())

        def orden = "itemnmbr"
        if (params.orden == "n") {
            orden = "itemcdgo"
        }
        def lugar = Lugar.get(params.lugar.toLong())
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)
//        println("fecha:" + fecha)

        def items = ""
        def lista = Item.withCriteria {
            eq("tipoItem", TipoItem.findByCodigo("I"))
            departamento {
                subgrupo {
                    eq("grupo", grupo)
                }
            }
        }
        lista.id.each {
            if (items != "") {
                items += ","
            }
            items += it
        }
        def res = []
//        println items
        def tmp = preciosService.getPrecioRubroItemOrder(fecha, lugar, items, orden, "asc")
        tmp.each {
            res.add(PrecioRubrosItems.get(it))
        }

        return [lugar: lugar, cols: params.col, precios: res, grupo: grupo]
    }


    def reporteExcelComposicion() {

        def obra = Obra.get(params.id)
        params.tipo = "1,2,3"

        def sql = "SELECT\n" +
                "  v.voit__id                            id,\n" +
                "  i.itemcdgo                            codigo,\n" +
                "  i.itemnmbr                            item,\n" +
                "  u.unddcdgo                            unidad,\n" +
                "  v.voitcntd                            cantidad,\n" +
                "  v.voitpcun                            punitario,\n" +
                "  v.voittrnp                            transporte,\n" +
                "  v.voitpcun + v.voittrnp               costo,\n" +
                "  (v.voitpcun + v.voittrnp)*v.voitcntd  total,\n" +
                "  d.dprtdscr                            departamento,\n" +
                "  s.sbgrdscr                            subgrupo,\n" +
                "  g.grpodscr                            grupo,\n" +
                "  g.grpo__id                            grid,\n" +
                "  v.sbpr__id                            sp,\n" +
                "  b.sbprdscr                            subpresupuesto\n" +
                "FROM vlobitem v\n" +
                "INNER JOIN item i ON v.item__id = i.item__id\n" +
                "INNER JOIN undd u ON i.undd__id = u.undd__id\n" +
                "INNER JOIN dprt d ON i.dprt__id = d.dprt__id\n" +
                "INNER JOIN sbgr s ON d.sbgr__id = s.sbgr__id\n" +
                "INNER JOIN sbpr b ON v.sbpr__id = b.sbpr__id\n" +
                "INNER JOIN grpo g ON s.grpo__id = g.grpo__id AND g.grpo__id IN (${params.tipo})\n" +
                "WHERE v.obra__id = ${params.id} \n" +
                "ORDER BY v.sbpr__id, grid ASC, i.itemnmbr"

/*
        def sql = "SELECT i.itemcdgo codigo, i.itemnmbr item, u.unddcdgo unidad, sum(v.voitcntd) cantidad, \n" +
                "v.voitpcun punitario, v.voittrnp transporte, v.voitpcun + v.voittrnp  costo, \n" +
                "sum((v.voitpcun + v.voittrnp) * v.voitcntd)  total, g.grpodscr grupo, g.grpo__id grid,\n" +
                "FROM vlobitem v INNER JOIN item i ON v.item__id = i.item__id\n" +
                "INNER JOIN undd u ON i.undd__id = u.undd__id\n" +
                "INNER JOIN dprt d ON i.dprt__id = d.dprt__id\n" +
                "INNER JOIN sbgr s ON d.sbgr__id = s.sbgr__id\n" +
                "INNER JOIN sbpr b ON v.sbpr__id = b.sbpr__id\n" +
                "INNER JOIN grpo g ON s.grpo__id = g.grpo__id AND g.grpo__id IN (${params.tipo}) \n" +
                "WHERE v.obra__id = ${params.id} and v.voitcntd >0 \n" +
                "group by i.itemcdgo, i.itemnmbr, u.unddcdgo, v.voitpcun, v.voittrnp, v.voitpcun, \n" +
                "g.grpo__id, g.grpodscr " +
                "ORDER BY g.grpo__id ASC, i.itemcdgo"
*/

        //println sql

        def cn = dbConnectionService.getConnection()

        def res = cn.rows(sql.toString())

//        println("--->>" + res)

        //excel
        WorkbookSettings workbookSettings = new WorkbookSettings()
        workbookSettings.locale = Locale.default

        def file = File.createTempFile('myExcelDocument', '.xls')
        file.deleteOnExit()
        WritableWorkbook workbook = Workbook.createWorkbook(file, workbookSettings)

        WritableFont font = new WritableFont(WritableFont.ARIAL, 12)
        WritableCellFormat formatXls = new WritableCellFormat(font)

        def row = 0
        WritableSheet sheet = workbook.createSheet('Composicion', 0)

        WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, false);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        sheet.setColumnView(0, 20)
        sheet.setColumnView(1, 60)
        sheet.setColumnView(2, 10)
        sheet.setColumnView(3, 20)
        sheet.setColumnView(4, 20)
        sheet.setColumnView(5, 20)
        sheet.setColumnView(6, 20)
        sheet.setColumnView(7, 20)
        sheet.setColumnView(8, 25)
        sheet.setColumnView(9, 20)

        def label
        def number
        def fila = 8;
        def totalE = 0;
        def totalM = 0;
        def totalMO = 0;
        def totalEquipo = 0;
        def totalManoObra = 0;
        def totalMaterial = 0;
        def totalDirecto = 0;
        def ultimaFila

        label = new jxl.write.Label(2, 4, "Composición de " + obra?.nombre, times16format); sheet.addCell(label);

        label = new jxl.write.Label(0, 6, "CODIGO", times16format); sheet.addCell(label);
        label = new jxl.write.Label(1, 6, "ITEM", times16format); sheet.addCell(label);
        label = new jxl.write.Label(2, 6, "UNIDAD", times16format); sheet.addCell(label);
        label = new jxl.write.Label(3, 6, "CANTIDAD", times16format); sheet.addCell(label);
        label = new jxl.write.Label(4, 6, "P.UNITARIO", times16format); sheet.addCell(label);
        label = new jxl.write.Label(5, 6, "TRANSPORTE", times16format); sheet.addCell(label);
        label = new jxl.write.Label(6, 6, "COSTO", times16format); sheet.addCell(label);
        label = new jxl.write.Label(7, 6, "TOTAL", times16format); sheet.addCell(label);
        label = new jxl.write.Label(8, 6, "TIPO", times16format); sheet.addCell(label);
        label = new jxl.write.Label(9, 6, "SUBPRESUPUESTO", times16format); sheet.addCell(label);

        res.each {

            if (it?.item == null) {

                it?.item = " "
            }

            if (it?.cantidad == null) {


                it?.cantidad = 0
            }
            if (it?.punitario == null) {

                it?.punitario = 0

            }
            if (it?.transporte == null) {

                it?.transporte = 0
            }
            if (it?.costo == null) {

                it?.costo = 0

            }
            if (it?.total == null) {

                it?.total = 0
            }

            label = new jxl.write.Label(0, fila, it?.codigo.toString()); sheet.addCell(label);
            label = new jxl.write.Label(1, fila, it?.item.toString()); sheet.addCell(label);
            label = new jxl.write.Label(2, fila, it?.unidad.toString()); sheet.addCell(label);
            number = new jxl.write.Number(3, fila, it?.cantidad); sheet.addCell(number);
            number = new jxl.write.Number(4, fila, it?.punitario); sheet.addCell(number);
            number = new jxl.write.Number(5, fila, it?.transporte); sheet.addCell(number);
            number = new jxl.write.Number(6, fila, it?.costo); sheet.addCell(number);
            number = new jxl.write.Number(7, fila, it?.total); sheet.addCell(number);
            label = new jxl.write.Label(8, fila, it?.grupo.toString()); sheet.addCell(label);
            label = new jxl.write.Label(9, fila, it?.subpresupuesto.toString()); sheet.addCell(label);

            fila++

            if (it?.grid == 1) {
                totalMaterial = (totalM += it?.total)
            }
            if (it?.grid == 2) {
                totalManoObra = (totalMO += it?.total)
            }
            if (it?.grid == 3) {
                totalEquipo = (totalE += it?.total)
            }
            totalDirecto = totalEquipo + totalManoObra + totalMaterial;
            ultimaFila = fila
        }

        label = new jxl.write.Label(6, ultimaFila, "Total Materiales: ", times16format); sheet.addCell(label);
        number = new jxl.write.Number(7, ultimaFila, totalMaterial); sheet.addCell(number);

        label = new jxl.write.Label(6, ultimaFila + 1, "Total Mano de Obra: ", times16format); sheet.addCell(label);
        number = new jxl.write.Number(7, ultimaFila + 1, totalManoObra); sheet.addCell(number);

        label = new jxl.write.Label(6, ultimaFila + 2, "Total Equipos: ", times16format); sheet.addCell(label);
        number = new jxl.write.Number(7, ultimaFila + 2, totalEquipo); sheet.addCell(number);

        label = new jxl.write.Label(6, ultimaFila + 3, "TOTAL DIRECTO: ", times16format); sheet.addCell(label);
        number = new jxl.write.Number(7, ultimaFila + 3, totalDirecto); sheet.addCell(number);

        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "ComposicionExcel.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());
    }


    def reportePreciosExcel() {

        def orden = "itemnmbr"
        if (params.orden == "n") {
            orden = "itemcdgo"
        }
        def grupo = Grupo.get(params.grupo.toLong())
        def lugar = Lugar.get(params.lugar.toLong())
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        def items = ""
        def lista = Item.withCriteria {
            eq("tipoItem", TipoItem.findByCodigo("I"))
            departamento {
                subgrupo {
                    eq("grupo", grupo)
                }
            }
        }
        lista.id.each {
            if (items != "") {
                items += ","
            }
            items += it
        }
        def res = []
//        println items
        def tmp = preciosService.getPrecioRubroItemOrder(fecha, lugar, items, orden, "asc")
        tmp.each {
            res.add(PrecioRubrosItems.get(it))
        }

        //excel

        WorkbookSettings workbookSettings = new WorkbookSettings()
        workbookSettings.locale = Locale.default

        def file = File.createTempFile('myExcelDocument', '.xls')
        file.deleteOnExit()
        WritableWorkbook workbook = Workbook.createWorkbook(file, workbookSettings)

        WritableFont font = new WritableFont(WritableFont.ARIAL, 12)
        WritableCellFormat formatXls = new WritableCellFormat(font)

        def row = 0
        WritableSheet sheet = workbook.createSheet('Presupuesto', 0)

        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, false);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        sheet.setColumnView(0, 20)
        sheet.setColumnView(1, 60)
        sheet.setColumnView(2, 15)
        sheet.setColumnView(3, 20)
        sheet.setColumnView(4, 20)
        sheet.setColumnView(5, 20)
        sheet.setColumnView(6, 25)


        def label
        def number
        def fila = 8;

        label = new jxl.write.Label(2, 1, "G.A.D. PROVINCIA DE PICHINCHA".toUpperCase(), times16format); sheet.addCell(label);
        label = new jxl.write.Label(2, 2, "Reporte de Costos de ${grupo.descripcion.toLowerCase()}", times16format); sheet.addCell(label);

        label = new jxl.write.Label(1, 4, lugar?.descripcion, times16format); sheet.addCell(label);
        label = new jxl.write.Label(4, 4, "Fecha Consulta: " + new Date().format("dd-MM-yyyy"), times16format); sheet.addCell(label);

        def col = 0
        label = new jxl.write.Label(col, 6, "CODIGO", times16format); sheet.addCell(label); col++;
        label = new jxl.write.Label(col, 6, grupo.descripcion.toUpperCase(), times16format); sheet.addCell(label); col++;
        label = new jxl.write.Label(col, 6, "UNIDAD", times16format); sheet.addCell(label); col++;
        if (grupo.id == 1) {
            label = new jxl.write.Label(col, 6, "PESO/VOL", times16format); sheet.addCell(label); col++;
        }
        label = new jxl.write.Label(col, 6, "COSTO", times16format); sheet.addCell(label); col++;
        label = new jxl.write.Label(col, 6, "FECHA ACT.", times16format); sheet.addCell(label); col++;

        res.each {
            col = 0
            label = new jxl.write.Label(col, fila, it?.item?.codigo.toString()); sheet.addCell(label); col++;
            label = new jxl.write.Label(col, fila, it?.item?.nombre.toString()); sheet.addCell(label); col++;
            label = new jxl.write.Label(col, fila, it?.item?.unidad?.codigo.toString()); sheet.addCell(label); col++;
            if (grupo.id == 1) {
                number = new jxl.write.Number(col, fila, it?.item?.peso); sheet.addCell(number); col++;
            }
            number = new jxl.write.Number(col, fila, it?.precioUnitario); sheet.addCell(number); col++;
            label = new jxl.write.Label(col, fila, it?.fecha.format("dd-MM-yyyy")); sheet.addCell(label); col++;

            fila++

        }


        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "MantenimientoPreciosExcel.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());


    }

    def reporteCronograma() {
        def obra = Obra.get(params.id.toLong())
        def meses = obra.plazoEjecucionMeses + (obra.plazoEjecucionDias > 0 ? 1 : 0)

        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])

        def precios = [:]
        def indirecto = obra.totales / 100

        preciosService.ac_rbroObra(obra.id)

        detalle.each {
            it.refresh()
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
        }
        return [detalle: detalle, precios: precios, obra: obra, meses: meses]
    }


    def reporteDesgloseEquipos() {

//        println("params" + params)

        def obra = Obra.get(params.id)

        def transTotal
        def eqTotal
        def eqDesglosado
        def b = []
        def c = []
        def sqlEquipoTotal1
        def sqlVerificarTransporte
        def et1 = []
        def et2
        def et3
        def ed1 = []
        def ed2
        def ed3
        def ed4
        def items = []
        def varTrans
        def valores = []
        def desglose = []
        def columnas = [5051, 5050, 5052, 3978, 5049]

        def sqlTransTotal = "SELECT\n" +
                "valor\n" +
                "FROM mfvl, mfcl\n" +
                "WHERE mfvl.obra__id = mfcl.obra__id AND\n" +
                "mfvl.obra__id = ${obra.id} AND\n" +
                "mfcl.clmndscr = 'TRANSPORTE_T' AND\n" +
                "codigo = 'sS1' AND\n" +
                "mfvl.clmncdgo = mfcl.clmncdgo"

//        select valor
//        from mfvl, mfcl
//        where mfvl.obra__id = mfcl.obra__id and mfvl.obra__id = 1430
//        and mfcl.clmndscr = 'TRANSPORTE_T' and
//        codigo = 'sS1'  and mfvl.clmncdgo = mfcl.clmncdgo;

        //equipoTotal
        sqlEquipoTotal1 = "SELECT\n" +
                "valor\n" +
                "from mfvl, mfcl\n" +
                "WHERE mfvl.obra__id = mfcl.obra__id AND\n" +
                "mfvl.obra__id = ${obra.id} AND\n" +
                "mfcl.clmndscr = '5051_T' AND \n" +
                "codigo = 'sS1' AND\n " +
                "mfvl.clmncdgo = mfcl.clmncdgo"

        sqlVerificarTransporte = "SELECT\n" +
                "valor\n" +
                "from mfvl, mfcl\n" +
                "WHERE mfvl.obra__id = mfcl.obra__id AND\n" +
                "mfvl.obra__id = ${obra.id} AND\n" +
                "mfcl.clmndscr = 'TRANSPORTE_T' AND \n" +
                "codigo = 'sS1' AND\n " +
                "mfvl.clmncdgo = mfcl.clmncdgo"

//        println("--><" + sqlVerificarTransporte)

//            select valor
//        from mfvl, mfcl
//        where mfvl.obra__id = mfcl.obra__id and mfvl.obra__id = 1430
//        and mfcl.clmndscr = '5051_T' and
//        codigo = 'sS1'  and mfvl.clmncdgo = mfcl.clmncdgo;

//
//        select valor
//        from mfvl, mfcl
//        where mfvl.obra__id = mfcl.obra__id
//        and mfvl.obra__id = 1430 and mfcl.clmndscr = '5051_T' and
//        codigo = 'sS2'
//        and mfvl.clmncdgo = mfcl.clmncdgo;


        def cn = dbConnectionService.getConnection()

        ed4 = cn.rows(sqlVerificarTransporte.toString())
        ed4.each {

            varTrans = it?.valor

        }


        columnas.each {

            //equipoDesglosado

            def sqlEquipoDesglosado1 = "SELECT\n" +
                    "valor\n" +
                    "from mfvl, mfcl\n" +
                    "where mfvl.obra__id = mfcl.obra__id AND\n" +
                    "mfvl.obra__id = ${obra.id} AND\n" +
                    "mfcl.clmndscr = '${it}_T' AND\n" +
                    "codigo= 'sS2' AND\n" +
                    "mfvl.clmncdgo = mfcl.clmncdgo"


            ed3 = cn.rows(sqlEquipoDesglosado1.toString())

            ed3.each {


                ed2 = it?.valor

            }


            ed1 += ed2


        }


        def tt = cn.rows(sqlTransTotal.toString())
        et1 = cn.rows(sqlEquipoTotal1.toString())




        tt.each {

            transTotal = it?.valor

        }

        et1.each {

            eqTotal = it?.valor

        }

//        println("A:" + eqTotal)
//        println("D:" + ed1)
//

        valores += (obra?.desgloseEquipo)
        valores += (obra?.desgloseRepuestos)
        valores += (obra?.desgloseCombustible)
        valores += (obra?.desgloseMecanico)
        valores += (obra?.desgloseSaldo)

//        valores.add(obra?.desgloseEquipo)

//        println("coeficientes:" + valores)
//
//        println("ed1" + ed1)
//        println("eqTotal:" + eqTotal)


        if (varTrans > 0) {
            valores.eachWithIndex { item, i ->

                b += (((ed1[i]) / (item)) - eqTotal)
                //                println(b[0])
            }

        } else {


            b[0] = 0.00

        }

//        println("B:" + b)

        b.each {


            c += (it + eqTotal)


        }

//      println("C:" + c)


        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]
        def prmsHeader = [border: Color.WHITE, colspan: 7, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsHeader2 = [border: Color.WHITE, colspan: 3, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE, bg: Color.WHITE,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHeadRight = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenter = [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellRight = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT]
        def prmsCellLeft = [border: Color.BLACK, valign: Element.ALIGN_MIDDLE]
        def prmsDerecha = [border: Color.WHITE, valign: Element.ALIGN_MIDDLE, align: Element.ALIGN_RIGHT]
        def prmsSubtotal = [border: Color.BLACK, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum, prmsHeaderHoja2: prmsHeaderHoja2, prmsCellRight: prmsCellRight, prmsCellHeadRight: prmsCellHeadRight, prmsDerecha: prmsDerecha]


        def baos = new ByteArrayOutputStream()
        def name = "presupuesto_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times10normal = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        def fonts = [times12bold: times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal, times10normal: times10normal]


        Document document
        document = new Document(PageSize.A4);
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();
//        document.setMargins(2,2,2,2)
        document.addTitle("Desglose de Equipos " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("documentosObra, janus, presupuesto");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");


        Paragraph headers = new Paragraph();
        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);
        headers.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", times12bold));
        headers.add(new Paragraph("DESGLOSE DE EQUIPOS", times12bold));
        headers.add(new Paragraph("OBRA: " + obra?.descripcion, times12bold));

        addEmptyLine(headers, 1);
        document.add(headers);

        PdfPTable tablaDesglose = new PdfPTable(4);
        tablaDesglose.setWidthPercentage(90);
        tablaDesglose.setWidths(arregloEnteros([35, 2, 15, 30]))

        addCellTabla(tablaDesglose, new Paragraph("Valor de Equipos", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(g.formatNumber(number: eqTotal, minFractionDigits:
                5, maxFractionDigits: 5, format: "###,###", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)

        if (b[0] > 0) {


            addCellTabla(tablaDesglose, new Paragraph("Valor de Transporte excluyendo al Chofer", times10bold), prmsHeaderHoja)
            addCellTabla(tablaDesglose, new Paragraph(" : "), prmsHeaderHoja)
            addCellTabla(tablaDesglose, new Paragraph(g.formatNumber(number: b[0], minFractionDigits:
                    5, maxFractionDigits: 5, format: "###,###", locale: "ec"), times10normal), prmsDerecha)
            addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)

        } else {


            addCellTabla(tablaDesglose, new Paragraph("Valor de Transporte excluyendo al Chofer", times10bold), prmsHeaderHoja)
            addCellTabla(tablaDesglose, new Paragraph(" : "), prmsHeaderHoja)
            addCellTabla(tablaDesglose, new Paragraph(g.formatNumber(number: b[0], minFractionDigits:
                    5, maxFractionDigits: 5, format: "##.##", locale: "ec"), times10normal), prmsDerecha)
            addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)


        }



        addCellTabla(tablaDesglose, new Paragraph("____________________________"), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph("________"), prmsDerecha)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesglose, new Paragraph("Total Equipos + Transporte", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(g.formatNumber(number: c[0], minFractionDigits:
                5, maxFractionDigits: 5, format: "###,###", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)

        PdfPTable tablaDesgloseBody = new PdfPTable(6);
        tablaDesgloseBody.setWidthPercentage(90);
        tablaDesgloseBody.setWidths(arregloEnteros([20, 20, 8, 5, 16, 40]))

        addCellTabla(tablaDesgloseBody, new Paragraph("Equipos", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: valores[0] * 100, minFractionDigits:
                0, maxFractionDigits: 0, format: "###,###", locale: "ec") + " %", times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: ed1[0], minFractionDigits:
                5, maxFractionDigits: 5, format: "###,###", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Repuestos", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: valores[1] * 100, minFractionDigits:
                0, maxFractionDigits: 0, format: "###,###", locale: "ec") + " %", times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: ed1[1], minFractionDigits:
                5, maxFractionDigits: 5, format: "###,###", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Combustibles", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: valores[2] * 100, minFractionDigits:
                0, maxFractionDigits: 0, format: "###,###", locale: "ec") + " %", times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: ed1[2], minFractionDigits:
                5, maxFractionDigits: 5, format: "###,###", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Mecánico", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: valores[3] * 100, minFractionDigits:
                0, maxFractionDigits: 0, format: "###,###", locale: "ec") + " %", times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: ed1[3], minFractionDigits:
                5, maxFractionDigits: 5, format: "###,###", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Saldo", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: valores[4] * 100, minFractionDigits:
                0, maxFractionDigits: 0, format: "###,###", locale: "ec") + " %", times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: ed1[4], minFractionDigits:
                5, maxFractionDigits: 5, format: "###,###", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)


        document.add(tablaDesglose)
        document.add(tablaDesgloseBody)
        document.close();
        pdfw.close()
        byte[] b1 = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b1.length)
        response.getOutputStream().write(b1)


    }


}
