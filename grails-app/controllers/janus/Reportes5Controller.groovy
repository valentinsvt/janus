package janus

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.HeaderFooter
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.Phrase
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.Label
import jxl.write.Number
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook

import java.awt.Color

class Reportes5Controller {

    def dbConnectionService

    def meses = ['', "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"]

    private String printFecha(Date fecha) {
        if (fecha) {
            return (fecha.format("dd") + ' de ' + meses[fecha.format("MM").toInteger()] + ' de ' + fecha.format("yyyy"))
        } else {
            return "Error: no hay fecha que mostrar"
        }
    }

    private filasAvance(params) {
//        def sqlBase = "SELECT\n" +
//                "  c.cntr__id               id,\n" +
//                "  b.obracdgo               obra_cod,\n" +
//                "  b.obranmbr               obra_nmbr,\n" +
//                "  m.cmndnmbr               comunidad,\n" +
//                "  a.parrnmbr               parroquia,\n" +
//                "  k.cntnnmbr               canton,\n" +
//                "  c.cntrcdgo               num_contrato,\n" +
//                "  p.prvenmbr               proveedor,\n" +
//                "  c.cntrmnto               monto,\n" +
//                "  c.cntrfcsb               fecha,\n" +
//                "  c.cntrplzo               plazo,\n" +
//                "  (SELECT\n" +
//                "  coalesce(sum(plnlmnto), 0)\n" +
//                "   FROM plnl\n" +
//                "   WHERE cntr__id = c.cntr__id\n" +
//                "         AND tppl__id = 3) sum\n" +
//                "FROM cntr c\n" +
//                "  INNER JOIN ofrt o ON c.ofrt__id = o.ofrt__id\n" +
//                "  INNER JOIN cncr n ON o.cncr__id = n.cncr__id\n" +
//                "  INNER JOIN obra b ON n.obra__id = b.obra__id\n" +
//                "  INNER JOIN tpob t ON b.tpob__id = t.tpob__id\n" +
//                "  INNER JOIN prve p ON o.prve__id = p.prve__id\n" +
//                "  INNER JOIN cmnd m ON b.cmnd__id = m.cmnd__id\n" +
//                "  INNER JOIN parr a ON m.parr__id = a.parr__id\n" +
//                "  INNER JOIN cntn k ON a.cntn__id = k.cntn__id"

        def sqlBase = "SELECT\n" +
                "  c.cntr__id               id,\n" +
                "  b.obracdgo               obra_cod,\n" +
                "  b.obranmbr               obra_nmbr,\n" +
                "  b.obratipo               tipo,\n" +
                "  z.dptocdgo               codigodepar,\n" +
                "  m.cmndnmbr               comunidad,\n" +
                "  a.parrnmbr               parroquia,\n" +
                "  k.cntnnmbr               canton,\n" +
                "  c.cntrcdgo               num_contrato,\n" +
                "  p.prvenmbr               proveedor,\n" +
                "  c.cntrmnto               monto,\n" +
                "  c.cntrfcsb               fecha,\n" +
                "  c.cntrplzo               plazo,\n" +
                "  (SELECT\n" +
                "  coalesce(sum(plnlmnto), 0)\n" +
                "   FROM plnl\n" +
                "   WHERE cntr__id = c.cntr__id\n" +
                "         AND tppl__id = 3) sum,\n" +
                "  (SELECT\n" +
                "  plnlavfs\n" +
                "   FROM plnl\n" +
                "   WHERE cntr__id = c.cntr__id\n" +
                "         AND plnlfcin IS NOT null\n" +
                "   ORDER BY plnlfcin DESC\n" +
                "   LIMIT 1)                fisico,\n" +
                "  b.obrafcin               inicio,\n" +
                "  c.cntrfccn               recepcion_contratista,\n" +
                "  c.cntrfcfs               recepcion_fisc\n" +
                "FROM cntr c\n" +
                "  INNER JOIN ofrt o ON c.ofrt__id = o.ofrt__id\n" +
                "  INNER JOIN cncr n ON o.cncr__id = n.cncr__id\n" +
                "  INNER JOIN obra b ON n.obra__id = b.obra__id\n" +
                "  INNER JOIN dpto z ON b.dpto__id = z.dpto__id\n" +
                "  INNER JOIN tpob t ON b.tpob__id = t.tpob__id\n" +
                "  INNER JOIN prve p ON o.prve__id = p.prve__id\n" +
                "  INNER JOIN cmnd m ON b.cmnd__id = m.cmnd__id\n" +
                "  INNER JOIN parr a ON m.parr__id = a.parr__id\n" +
                "  INNER JOIN cntn k ON a.cntn__id = k.cntn__id"

        def filtroBuscador = "", buscador

        switch (params.buscador) {
            case "cdgo":
            case "nmbr":
            case "ofig":
            case "ofsl":
            case "mmsl":
            case "frpl":
            case "tipo":
                buscador = "b.obra" + params.buscador
                filtroBuscador = " ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "cmnd":
                filtroBuscador = " m.cmndnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "parr":
                filtroBuscador = " a.parrnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "cntn":
                filtroBuscador = " k.cntnnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "cntr":
                filtroBuscador = " c.cntrcdgo ILIKE ('%${params.criterio}%') "
                break;
            case "cnts":
                filtroBuscador = " p.prvenmbr ILIKE ('%${params.criterio}%') OR p.prvenbct ILIKE ('%${params.criterio}%') OR p.prveapct ILIKE ('%${params.criterio}%')"
                break;
        }

        if (filtroBuscador != "") {
            filtroBuscador = " WHERE " + filtroBuscador
        }

        def sql = sqlBase + filtroBuscador

//        println sql

        def cn = dbConnectionService.getConnection()

//        println sql

        return cn.rows(sql.toString())



    }

    def avance() {
        def perfil = session.perfil.id
        return [perfil: perfil]
    }

    def tablaAvance() {
        params.old = params.criterio
        params.criterio = cleanCriterio(params.criterio)

        def res = filasAvance(params)


        def obrasFiltradas = []

        res.each{
            if(it.codigodepar == 'UTFPU' || it.tipo == 'D'){
                obrasFiltradas += it
            }
        }


//        println res

//        println(obrasFiltradas)
        params.criterio = params.old

        return [res: obrasFiltradas, params: params]
    }

    def reporteAvance() {
        def baos = new ByteArrayOutputStream()
        def name = "avance_obras_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
//            println "name "+name
        Font titleFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font titleFont3 = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font titleFont2 = new Font(Font.TIMES_ROMAN, 16, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
        Font catFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font small = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Font fontTh = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        Document document
        document = new Document(PageSize.A4.rotate());
        def pdfw = PdfWriter.getInstance(document, baos);

        HeaderFooter footer1 = new HeaderFooter(new Phrase(" ", times8normal), true);
        // true aqui pone numero de pagina
        footer1.setBorder(Rectangle.NO_BORDER);
//        footer1.setBorder(Rectangle.TOP);
        footer1.setAlignment(Element.ALIGN_CENTER);

        document.setFooter(footer1);

        document.open();
        document.addTitle("Matriz Polinómica " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus,matriz");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

//        println titulo
        Paragraph headersTitulo = new Paragraph();
        addEmptyLine(headersTitulo, 1)
        headersTitulo.setAlignment(Element.ALIGN_CENTER);
        headersTitulo.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", titleFont2));
        addEmptyLine(headersTitulo, 1);
        headersTitulo.add(new Paragraph("REPORTE DE AVANCE DE OBRAS", titleFont));
        headersTitulo.add(new Paragraph("Quito, " + fechaConFormato(new Date(), "dd MMMM yyyy").toUpperCase(), titleFont3));
        addEmptyLine(headersTitulo, 1);
        addEmptyLine(headersTitulo, 1);

        document.add(headersTitulo)

        params.old = params.criterio
        params.criterio = cleanCriterio(params.criterio)

        def res = filasAvance(params)

        def tablaDatos = new PdfPTable(10);
        tablaDatos.setWidthPercentage(100);
        tablaDatos.setWidths(arregloEnteros([7, 18, 13, 9, 14, 7, 9, 6, 7, 10]))

        def paramsHead = [border: Color.BLACK,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, bordeTop: "1", bordeBot: "1"]
        def prmsCellLeft = [border: Color.WHITE, valign: Element.ALIGN_MIDDLE]

        addCellTabla(tablaDatos, new Paragraph("Código", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Nombre", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Cantón-Parroquia-Comunidad", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Núm. Contrato", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Contratista", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Monto", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Fecha suscripción", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Plazo", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("% Avance", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Estado", fontTh), paramsHead)

        res.each { fila ->
            def estado = ""
            if (fila.inicio) {
                estado = "Iniciada el " + (fila.inicio.format("dd-MM-yyyy"))
                if (fila.recepcion_contratista && fila.recepcion_fisc) {
                    estado = "Finalizada el " + (fila.recepcion_fisc.format("dd-MM-yyyy"))
                }
            }
            addCellTabla(tablaDatos, new Paragraph(fila.obra_cod, fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(fila.obra_nmbr, fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(fila.canton + " - " + fila.parroquia + " - " + fila.comunidad, fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(fila.num_contrato, fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(fila.proveedor, fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(numero(fila.monto, 2), fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(fila.fecha.format("dd-MM-yyyy"), fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(numero(fila.plazo, 0) + " días", fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(numero((fila.sum / fila.monto) * 100, 2) + "%", fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(estado, fontTd), prmsCellLeft)
        }

        document.add(tablaDatos)

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    private String cleanCriterio(String criterio) {
        if (!criterio) {
            criterio = ""
        }
//        println "entra: "+criterio
        criterio = criterio.toLowerCase()
        criterio = criterio.replaceAll(";", "")
        criterio = criterio.replaceAll(":", "")
        criterio = criterio.replaceAll("select", "")
        criterio = criterio.replaceAll("\\*", "")
        criterio = criterio.replaceAll("#", "")
        criterio = criterio.replaceAll("%", "")
        criterio = criterio.replaceAll("/", "")
        criterio = criterio.replaceAll("drop", "")
        criterio = criterio.replaceAll("table", "")
        criterio = criterio.replaceAll("from", "")
        criterio = criterio.replaceAll("'", "")
        criterio = criterio.replaceAll('"', "")
        criterio = criterio.replaceAll("\\\\", "")
        criterio = criterio.trim()
//        println "sale: "+criterio
        return criterio
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
        if (params.bordeTop) {
            cell.setBorderWidthTop(1)
            cell.setBorderWidthLeft(0)
            cell.setBorderWidthRight(0)
            cell.setBorderWidthBottom(0)
            cell.setPaddingTop(7);
        }
        if (params.bordeBot) {
            cell.setBorderWidthBottom(1)
            cell.setBorderWidthLeft(0)
            cell.setBorderWidthRight(0)
            cell.setPaddingBottom(7)

            if (!params.bordeTop) {
                cell.setBorderWidthTop(0)
            }
        }
        table.addCell(cell);
    }


    def reporteFormulaExcel() {
        def auxiliar = Auxiliar.get(1)
        def auxiliarFijo = Auxiliar.get(1)
        def obra = Obra.get(params.id)
        def firma
        def firmas
        def firmaFijaFormu
        def cuenta = 0;
        def formula = FormulaPolinomica.findAllByObra(obra)
        def ps = FormulaPolinomica.findAllByObraAndNumeroIlike(obra, 'p%', [sort: 'numero'])
        def cuadrilla = FormulaPolinomica.findAllByObraAndNumeroIlike(obra, 'c%', [sort: 'numero'])
        def c
        def z = []
        def banderafp = 0
        def firma1 = obra?.responsableObra;
        def firma2 = obra?.revisor;

        if (params.firmasIdFormu.trim().size() > 0) {
            firma = params.firmasIdFormu.split(",")
            firma = firma.toList().unique()
        } else {
            firma = []
        }
        if (params.firmasFijasFormu.trim().size() > 0) {
            firmaFijaFormu = params.firmasFijasFormu.split(",")
//            firmaFijaFormu = firmaFijaFormu.toList().unique()
        } else {
            firmaFijaFormu = []
        }

        cuenta = firma.size() + firmaFijaFormu.size()

        def totalBase = params.totalPresupuesto

        if (obra?.formulaPolinomica == null) {
            obra?.formulaPolinomica = ""
        }

        //excel
        WorkbookSettings workbookSettings = new WorkbookSettings()
        workbookSettings.locale = Locale.default

        def file = File.createTempFile('myExcelDocument', '.xls')
        file.deleteOnExit()
//        println "paso"
        WritableWorkbook workbook = Workbook.createWorkbook(file, workbookSettings)

        WritableFont font = new WritableFont(WritableFont.ARIAL, 12)
        WritableCellFormat formatXls = new WritableCellFormat(font)

        def row = 0
        WritableSheet sheet = workbook.createSheet('MySheet', 0)
        // fija el ancho de la columna
        // sheet.setColumnView(1,40)

//        params.id = params.id.split(",")
//        if (params.id.class == java.lang.String) {
//            params.id = [params.id]
//        }
        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, false);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        sheet.setColumnView(0, 12)
        sheet.setColumnView(1, 25)
        sheet.setColumnView(2, 25)
        sheet.setColumnView(3, 60)
        sheet.setColumnView(4, 25)
        sheet.setColumnView(5, 25)
        sheet.setColumnView(6, 25)
        sheet.setColumnView(7, 25)

        def label
        def number
        def nmro
        def numero = 1;

        def fila = 16;

        def ultimaFila

        label = new Label(1, 2, "G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);

        label = new Label(1, 4, "FÓRMULA POLINÓMICA", times16format); sheet.addCell(label);

        label = new Label(1, 6, obra?.formulaPolinomica, times16format); sheet.addCell(label);

        label = new Label(1, 8, "De existir variaciones en los costos de los componentes de precios unitarios estipulados en el contrato para la contrucción de:", times16format);
        sheet.addCell(label);

        label = new Label(1, 10, "Nombre: ", times16format); sheet.addCell(label);
        label = new Label(2, 10, obra?.nombre, times16format); sheet.addCell(label);
        label = new Label(1, 11, "Tipo de Obra: ", times16format); sheet.addCell(label);
        label = new Label(2, 11, obra?.tipoObjetivo?.descripcion, times16format); sheet.addCell(label);
        label = new Label(1, 12, "Código Obra: ", times16format); sheet.addCell(label);
        label = new Label(2, 12, obra?.codigo, times16format); sheet.addCell(label);
        label = new Label(1, 13, "Ubicación: ", times16format); sheet.addCell(label);
        label = new Label(2, 13, obra?.sitio, times16format); sheet.addCell(label);
        label = new Label(1, 14, "Fecha: ", times16format); sheet.addCell(label);
        label = new Label(2, 14, printFecha(obra?.fechaOficioSalida), times16format); sheet.addCell(label);
        label = new Label(1, 15, "Cantón: ", times16format); sheet.addCell(label);
        label = new Label(2, 15, obra?.parroquia?.canton?.nombre, times16format); sheet.addCell(label);
        label = new Label(1, 16, "Parroquia: ", times16format); sheet.addCell(label);
        label = new Label(2, 16, obra?.parroquia?.nombre, times16format); sheet.addCell(label);
        label = new Label(1, 17, "Coordenadas: ", times16format); sheet.addCell(label);
        label = new Label(2, 17, obra?.coordenadas, times16format); sheet.addCell(label);

        label = new Label(1, 19, "Los costos se reajustarán para efecto de pago, mediante la fórmula general: ", times16format);
        sheet.addCell(label);

        label = new Label(1, 21, "Pr = Po (p01B1/Bo + p02C1/Co + p03D1/Do + p04E1/Eo + p05F1/Fo + p06G1/Go + p07H1/Ho + p08I1/Io + p09J1/Jo + p10K1/Ko + pxX1/Xo) ", times16format);
        sheet.addCell(label);

        def textoFormula = "Pr=Po(";
        def txInicio = "Pr = Po (";
        def txFin = ")";
        def txSuma = " + "
        def txExtra = ""
        def tx = []
        def valores = []
        def formulaCompleta
        def valorP

        ps.each { j ->

            if (j.valor != 0.0 || j.valor != 0) {
                if (j.numero == 'p01') {
                    tx[0] = g.formatNumber(number: j.valor, maxFractionDigits: 3, minFractionDigits: 3) + "B1/Bo"
                    valores[0] = j
                }
                if (j.numero == 'p02') {
                    tx[1] = g.formatNumber(number: j.valor, maxFractionDigits: 3, minFractionDigits: 3) + "C1/Co"
                    valores[1] = j
                }
                if (j.numero == 'p03') {
                    tx[2] = g.formatNumber(number: j.valor, maxFractionDigits: 3, minFractionDigits: 3) + "D1/Do"
                    valores[2] = j
                }
                if (j.numero == 'p04') {
                    tx[3] = g.formatNumber(number: j.valor, maxFractionDigits: 3, minFractionDigits: 3) + "E1/Eo"
                    valores[3] = j
                }
                if (j.numero == 'p05') {
                    tx[4] = g.formatNumber(number: j.valor, maxFractionDigits: 3, minFractionDigits: 3) + "F1/Fo"
                    valores[4] = j
                }
                if (j.numero == 'p06') {
                    tx[5] = g.formatNumber(number: j.valor, maxFractionDigits: 3, minFractionDigits: 3) + "G1/Go"
                    valores[5] = j
                }
                if (j.numero == 'p07') {
//                    def p07valores =
                    tx[6] = g.formatNumber(number: j.valor, maxFractionDigits: 3, minFractionDigits: 3) + "H1/Ho"
                    valores[6] = j
                }
                if (j.numero == 'p08') {
                    tx[7] = g.formatNumber(number: j.valor, maxFractionDigits: 3, minFractionDigits: 3) + "I1/Io"
                    valores[7] = j
                }
                if (j.numero == 'p09') {
                    tx[8] = g.formatNumber(number: j.valor, maxFractionDigits: 3, minFractionDigits: 3) + "J1/Jo"
                    valores[8] = j
                }
                if (j.numero == 'p10') {

                    tx[9] = g.formatNumber(number: j.valor, maxFractionDigits: 3, minFractionDigits: 3) + "K1/Ko"
                    valores[9] = j
                }
                if (j.numero.trim() == 'px') {
                    tx[10] = g.formatNumber(number: j.valor, maxFractionDigits: 3, minFractionDigits: 3) + "X1/Xo"
                    valores[10] = j
                }
            }
        }

        def formulaStr = txInicio
        tx.eachWithIndex { linea, k ->
            if (linea) {
                formulaStr += linea
                if (k < tx.size() - 1)
                    formulaStr += " + "
            }
        }
        formulaStr += txFin
        label = new Label(1, 23, formulaStr, times16format); sheet.addCell(label);
        label = new Label(1, 24, " ", times16format); sheet.addCell(label);

        def valorTotal = 0
        def salto = 1

        valores.eachWithIndex { i, j ->

            if (i) {
                if (i.valor != 0.0 || i.valor != 0) {

                    label = new Label(1, 24 + salto, i.numero + "= " + g.formatNumber(number: i.valor, format: "##,##0", minFractionDigits: 3, maxFractionDigits: 3, locale: "ec") +
                            "    Coeficiente del Componente    " + i?.indice?.descripcion.toUpperCase(), times16format);
                    sheet.addCell(label);
                    valorTotal = i.valor + valorTotal
                    salto++
                }
            }
        }

        def salto2 = 24 + salto

        label = new Label(1, salto2, "___________________", times16format); sheet.addCell(label);
        label = new Label(1, salto2 + 1, "SUMAN : " + g.formatNumber(number: valorTotal, format: "##,##0", minFractionDigits: 3, maxFractionDigits: 3, locale: "ec"), times16format);
        sheet.addCell(label);

        label = new Label(1, salto2 + 3, "CUADRILLA TIPO", times16format); sheet.addCell(label);
        label = new Label(2, salto2 + 3, "CLASE OBRERO", times16format); sheet.addCell(label);

        def valorTotalCuadrilla = 0;
        def salto3 = salto2 + 5

        cuadrilla.eachWithIndex { i, s ->


            if (i.valor != 0.0 || i.valor != 0) {
                label = new Label(1, salto3, i?.numero + "  " + g.formatNumber(number: i?.valor, format: "##.####", locale: "ec"), times16format);
                sheet.addCell(label);
                label = new Label(2, salto3, i?.indice?.descripcion, times16format); sheet.addCell(label);
                valorTotalCuadrilla = i.valor + valorTotalCuadrilla
                salto3++
            } else {
            }
        }

        label = new Label(1, salto3 + 1, "___________________", times16format); sheet.addCell(label);
        label = new Label(1, salto3 + 2, "SUMAN : " + g.formatNumber(number: valorTotalCuadrilla, format: "##,##0", minFractionDigits: 3, maxFractionDigits: 3, locale: "ec"), times16format);
        sheet.addCell(label);

        label = new Label(1, salto3 + 4, auxiliarFijo?.notaFormula, times16format); sheet.addCell(label);

        label = new Label(1, salto3 + 6, "Fecha de actualización", times16format); sheet.addCell(label);
        label = new Label(2, salto3 + 6, printFecha(obra?.fechaPreciosRubros), times16format); sheet.addCell(label);
        label = new Label(3, salto3 + 6, "Monto del Contrato", times16format); sheet.addCell(label);
        label = new Label(4, salto3 + 6, "\$" + g.formatNumber(number: totalBase, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times16format);
        sheet.addCell(label);

        label = new Label(1, salto3 + 8, "Atentamente,  ", times16format); sheet.addCell(label);

        if (cuenta == 3) {
            label = new Label(1, salto3 + 13, "______________________________________", times16format);
            sheet.addCell(label);
            label = new Label(2, salto3 + 13, "______________________________________", times16format);
            sheet.addCell(label);
            label = new Label(3, salto3 + 13, "______________________________________", times16format);
            sheet.addCell(label);
            def salto4 = salto3 + 13

            firmaFijaFormu.eachWithIndex { f, h ->

                if (f != '') {

                    firmas = Persona.get(f)

                    label = new Label(h + 1, salto4 + 1, firmas?.titulo + " " + firmas?.nombre + " " + firmas?.apellido, times16format);
                    sheet.addCell(label);
                } else {
                    label = new Label(h + 1, salto4 + 1, "Sin asignar,  ", times16format); sheet.addCell(label);
                }
            }

            firmas = Persona.get(firmaFijaFormu[0])
            label = new Label(1, salto4 + 2, firmas?.cargo, times16format); sheet.addCell(label);
            label = new Label(2, salto4 + 2, "REVISOR", times16format); sheet.addCell(label);
            label = new Label(3, salto4 + 2, "ELABORÓ", times16format); sheet.addCell(label);
        }

        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "FormulaPolinomicaExcel.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());
    }

    def imprimirCoeficientes() {

        def obra = Obra.get(params.id)

        def baos = new ByteArrayOutputStream()
        def name = "coeficientes_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
//            println "name "+name
        Font titleFont = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font titleFont3 = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font titleFont2 = new Font(Font.TIMES_ROMAN, 16, Font.BOLD);
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Font fontTh = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        Document document
        document = new Document(PageSize.A4);
        def pdfw = PdfWriter.getInstance(document, baos);

        HeaderFooter footer1 = new HeaderFooter(new Phrase(" ", times8normal), true);
        // true aqui pone numero de pagina
        footer1.setBorder(Rectangle.NO_BORDER);
//        footer1.setBorder(Rectangle.TOP);
        footer1.setAlignment(Element.ALIGN_CENTER);

        document.setFooter(footer1);

        document.open();
        document.addTitle("Coeficientes " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus,coeficientes");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

//        println titulo
        Paragraph headersTitulo = new Paragraph();
        addEmptyLine(headersTitulo, 1)
        headersTitulo.setAlignment(Element.ALIGN_CENTER);
        headersTitulo.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", titleFont2));
        addEmptyLine(headersTitulo, 1);
        headersTitulo.add(new Paragraph("COEFICIENTES DE LA FÓRMULA POLINÓMICA DE LA OBRA ${obra.nombre}", titleFont));
//        headersTitulo.add(new Paragraph("Quito, " + fechaConFormato(new Date(), "dd MMMM yyyy").toUpperCase(), titleFont3));
        addEmptyLine(headersTitulo, 1);
        addEmptyLine(headersTitulo, 1);

        document.add(headersTitulo)

        def sql = "SELECT DISTINCT\n" +
//                "  v.voit__id id,\n" +
//                "  i.item__id iid,\n" +
                "  i.itemcdgo codigo,\n" +
                "  i.itemnmbr item,\n" +
                "  v.voitcoef aporte,\n" +
                "  v.voitpcun precio,\n" +
                "  g.grpodscr grupo\n" +
                "FROM vlobitem v\n" +
                "  INNER JOIN item i ON v.item__id = i.item__id\n" +
                "  INNER JOIN grpo g ON v.voitgrpo = g.grpo__id\n" +
                "WHERE v.obra__id = ${params.id}\n" +
                "      AND voitgrpo IN (1, 2)\n" + //cambiar aqui si hay que filtrar solo mano de obra o no: 1:formula polinomica, 2:mano de obra
                "ORDER BY g.grpodscr, i.itemnmbr;"

        def tablaDatos = new PdfPTable(3);
        tablaDatos.setWidthPercentage(100);
        tablaDatos.setWidths(arregloEnteros([15, 77, 8]))

        addCellTabla(tablaDatos, new Paragraph("Item", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos, new Paragraph("Descripción", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tablaDatos, new Paragraph("Aporte", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        def grupo = "null"

        def cn = dbConnectionService.getConnection()
        cn.eachRow(sql.toString()) { row ->
            if (row.grupo != grupo) {
                grupo = row.grupo
                addCellTabla(tablaDatos, new Paragraph(row.grupo, fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
            }
            addCellTabla(tablaDatos, new Paragraph(row.codigo, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDatos, new Paragraph(row.item, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tablaDatos, new Paragraph(numero(row.aporte, 5), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }

        document.add(tablaDatos)

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

}
