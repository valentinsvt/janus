package janus

import com.itextpdf.text.BaseColor
import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.HeaderFooter
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.Phrase
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import janus.pac.CronogramaContratado
import janus.pac.CronogramaContrato
import janus.seguridad.Shield
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.Label
import jxl.write.Number
import jxl.write.NumberFormat
import jxl.write.WritableCellFeatures
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook

import java.awt.Color
import java.text.DecimalFormat

class Reportes5Controller extends Shield{

    def dbConnectionService
    def preciosService
    def reportesService

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
                "  b.prsn__id               responsable,\n" +
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

    def tablaAvance_old() {
        params.old = params.criterio
        params.criterio = reportesService(params.criterio)

        def res = filasAvance(params)

        def personasUtfpu = Persona.findAllByDepartamento(Departamento.findByCodigo('UTFPU'))
        def responsableObra

        def obrasFiltradas = []


        if(Persona.get(session.usuario.id).departamento?.codigo == 'UTFPU'){
            res.each{
                responsableObra = it.responsable
                if((personasUtfpu.contains(Persona.get(responsableObra))) || it.tipo == 'D'){
                    obrasFiltradas += it
                }
            }
        }else{
            obrasFiltradas = res
        }


//        println res

//        println(obrasFiltradas)
        params.criterio = params.old

        return [res: obrasFiltradas, params: params]
    }

    def tablaAvance() {
//        println "tablaContratadas ok $params , ${reportesService.obrasContratadas()}"
        def cn = dbConnectionService.getConnection()
        def campos = reportesService.obrasAvance()

        params.old = params.criterio
        params.criterio = reportesService.limpiaCriterio(params.criterio)

        def sql = armaSqlAvance(params)
        println "sql: $sql"
        def obras = cn.rows(sql)

//        println "registro retornados del sql: ${obras.size()}"
        params.criterio = params.old
        return [obras: obras, params: params]
    }

    def armaSqlAvance(params){
        def campos = reportesService.obrasAvance()
        def operador = reportesService.operadores()

        def sqlSelect = "select obra.obra__id, obracdgo, obranmbr, cntnnmbr, parrnmbr, cmndnmbr, c.cntrcdgo, " +
                "c.cntrmnto, c.cntrfcsb, prvenmbr, c.cntrplzo, obrafcin, cntrfcfs," +
                "(select(coalesce(sum(plnlmnto), 0)) / cntrmnto av_economico " +
                "from plnl where cntr__id = c.cntr__id and tppl__id > 1), " +
                "(select(coalesce(max(plnlavfs), 0)) av_fisico " +
                "from plnl where cntr__id = c.cntr__id and tppl__id > 1) " +  // no cuenta el anticipo
                "from obra, cntn, parr, cmnd, cncr, ofrt, cntr c, dpto, prve "
        def sqlWhere = "where cmnd.cmnd__id = obra.cmnd__id and " +
                "parr.parr__id = obra.parr__id and cntn.cntn__id = parr.cntn__id and " +
                "cncr.obra__id = obra.obra__id and ofrt.cncr__id = cncr.cncr__id and " +
                "c.ofrt__id = ofrt.ofrt__id and dpto.dpto__id = obra.dpto__id and " +
                "prve.prve__id = c.prve__id"
        def sqlOrder = "order by obracdgo"

//        println "llega params: $params"
        params.nombre = "Código"
        if(campos.find {it.campo == params.buscador}?.size() > 0) {
            def op = operador.find {it.valor == params.operador}
            println "op: $op"
            sqlWhere += " and ${params.buscador} ${op.operador} ${op.strInicio}${params.criterio}${op.strFin}";
        }
//        println "txWhere: $sqlWhere"
//        println "sql armado: sqlSelect: ${sqlSelect} \n sqlWhere: ${sqlWhere} \n sqlOrder: ${sqlOrder}"
//        println "sql: ${sqlSelect} ${sqlWhere} ${sqlOrder}"
        //retorna sql armado:
        "$sqlSelect $sqlWhere $sqlOrder".toString()
    }



    def reporteAvance() {
        println("params-->" + params)

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
        headersTitulo.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", titleFont2));
        addEmptyLine(headersTitulo, 1);
        headersTitulo.add(new Paragraph("REPORTE DE AVANCE DE OBRAS", titleFont));
        headersTitulo.add(new Paragraph("Quito, " + fechaConFormato(new Date(), "dd MMMM yyyy").toUpperCase(), titleFont3));
        addEmptyLine(headersTitulo, 1);
        addEmptyLine(headersTitulo, 1);

        document.add(headersTitulo)

        params.old = params.criterio
//        params.criterio = cleanCriterio(params.criterio)
        params.criterio = reportesService.limpiaCriterio(params.criterio)

//        def res = filasAvance(params)

        def cn = dbConnectionService.getConnection()
        def campos = reportesService.obrasAvance()
        def sql = armaSqlAvance(params)
        def obras = cn.rows(sql)
        params.criterio = params.old


        def tablaDatos = new PdfPTable(10);
        tablaDatos.setWidthPercentage(100);
        tablaDatos.setWidths(arregloEnteros([7, 18, 13, 9, 14, 10, 9, 6, 5, 5]))

        def paramsHead = [border: Color.BLACK,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, bordeTop: "1", bordeBot: "1"]
        def prmsCellLeft = [border: Color.WHITE, valign: Element.ALIGN_MIDDLE]
        def prmsCellRight = [border: Color.WHITE, valign: Element.ALIGN_RIGHT]

        addCellTabla(tablaDatos, new Paragraph("Código", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Nombre", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Cantón-Parroquia-Comunidad", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Núm. Contrato", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Contratista", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Monto", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Fecha suscripción", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Plazo", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("% Avance", fontTh), paramsHead)
        addCellTabla(tablaDatos, new Paragraph("Avance Físico", fontTh), paramsHead)
//        addCellTabla(tablaDatos, new Paragraph("Estado", fontTh), paramsHead)

        obras.each { fila ->
//            def estado = ""
//            if (fila.inicio) {
//                estado = "Iniciada el " + (fila.inicio.format("dd-MM-yyyy"))
//                if (fila.recepcion_contratista && fila.recepcion_fisc) {
//                    estado = "Finalizada el " + (fila.recepcion_fisc.format("dd-MM-yyyy"))
//                }
//            }
            addCellTabla(tablaDatos, new Paragraph(fila.obracdgo, fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(fila.obranmbr, fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(fila.cntnnmbr + " - " + fila.parrnmbr + " - " + fila.cmndnmbr, fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(fila.cntrcdgo, fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(fila.prvenmbr, fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(numero(fila.cntrmnto, 2), fontTd), prmsCellRight)
            addCellTabla(tablaDatos, new Paragraph(fila.cntrfcsb.format("dd-MM-yyyy"), fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(numero(fila.cntrplzo, 0) + " días", fontTd), prmsCellLeft)
            addCellTabla(tablaDatos, new Paragraph(numero( (fila.av_economico) * 100, 2) + "%", fontTd), prmsCellRight)
            addCellTabla(tablaDatos, new Paragraph(numero(fila.av_fisico, 2), fontTd), prmsCellRight)
//            addCellTabla(tablaDatos, new Paragraph(estado, fontTd), prmsCellLeft)
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


    def reporteExcelAvance () {

        def cn = dbConnectionService.getConnection()
        def campos = reportesService.obrasAvance()
        params.old = params.criterio
        params.criterio = reportesService.limpiaCriterio(params.criterio)
        def sql = armaSqlAvance(params)
        def obras = cn.rows(sql)
        params.criterio = params.old


        //excel
        WorkbookSettings workbookSettings = new WorkbookSettings()
        workbookSettings.locale = Locale.default

        def file = File.createTempFile('myExcelDocument', '.xls')
//        def file = File.createTempFile('myExcelDocument', '.ods')
        file.deleteOnExit()

        WritableWorkbook workbook = Workbook.createWorkbook(file, workbookSettings)

        WritableFont font = new WritableFont(WritableFont.ARIAL, 12)
        WritableCellFormat formatXls = new WritableCellFormat(font)

        def row = 0
        WritableSheet sheet = workbook.createSheet('MySheet', 0)
        // fija el ancho de la columna
        // sheet.setColumnView(1,40)

        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, false);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        sheet.setColumnView(0, 12)
        sheet.setColumnView(1, 60)
        sheet.setColumnView(2, 30)
        sheet.setColumnView(3, 20)
        sheet.setColumnView(4, 40)
        sheet.setColumnView(5, 15)
        sheet.setColumnView(6, 17)
        sheet.setColumnView(7, 10)
        sheet.setColumnView(8, 15)
        sheet.setColumnView(9, 15)
        // inicia textos y numeros para asocias a columnas

        def label
        def nmro
        def number

        def fila = 6;


        NumberFormat nf = new NumberFormat("#.##");
        WritableCellFormat cf2obj = new WritableCellFormat(nf);

        label = new Label(1, 1, "SEP - G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(1, 2, "REPORTE EXCEL AVANCE DE OBRAS", times16format); sheet.addCell(label);

        label = new Label(0, 4, "Código: ", times16format); sheet.addCell(label);
        label = new Label(1, 4, "Nombre", times16format); sheet.addCell(label);
        label = new Label(2, 4, "Cantón-Parroquia-Comunidad", times16format); sheet.addCell(label);
        label = new Label(3, 4, "Num. Contrato", times16format); sheet.addCell(label);
        label = new Label(4, 4, "Contratista", times16format); sheet.addCell(label);
        label = new Label(5, 4, "Monto", times16format); sheet.addCell(label);
        label = new Label(6, 4, "Fecha suscripción", times16format); sheet.addCell(label);
        label = new Label(7, 4, "Plazo", times16format); sheet.addCell(label);
        label = new Label(8, 4, "% Avance", times16format); sheet.addCell(label);
        label = new Label(9, 4, "Avance Físico", times16format); sheet.addCell(label);

        obras.eachWithIndex {i, j->

            label = new Label(0, fila, i.obracdgo.toString()); sheet.addCell(label);
            label = new Label(1, fila, i?.obranmbr?.toString()); sheet.addCell(label);
            label = new Label(2, fila, i?.cntnnmbr?.toString() + " " + i?.parrnmbr?.toString() + " " + i?.cmndnmbr?.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.cntrcdgo?.toString()); sheet.addCell(label);
            label = new Label(4, fila, i?.prvenmbr?.toString()); sheet.addCell(label);
            number = new jxl.write.Number(5, fila, i.cntrmnto); sheet.addCell(number);
            label = new Label(6, fila, i?.cntrfcsb?.toString()); sheet.addCell(label);
            number = new jxl.write.Number(7, fila, i.cntrplzo); sheet.addCell(number);
            number = new jxl.write.Number(8, fila, (i.av_economico * 100)); sheet.addCell(number);
            number = new jxl.write.Number(9, fila, (i.av_fisico * 100)); sheet.addCell(number);


            fila++

        }

        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "DocumentosObraExcel.xls";
//        def header = "attachment; filename=" + "AvancesObraExcel.ods";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());
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

    private static void addCellTabla2(PdfPTable table, paragraph, params) {
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


    def reporteFormulaExcel() {
        println("params " + params)
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
        def nota


        if(params.notaPoli != '-1' || params.notaPoli != -1){
            nota = Nota.get(params.notaPoli)?.texto
        }else {
            nota = ''
        }

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

        label = new Label(1, 2, "SEP - G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);

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

        label = new Label(1, salto3 + 4, nota, times16format); sheet.addCell(label);

        label = new Label(1, salto3 + 6, "Fecha de actualización", times16format); sheet.addCell(label);
        label = new Label(2, salto3 + 6, printFecha(obra?.fechaPreciosRubros), times16format); sheet.addCell(label);
        label = new Label(3, salto3 + 6, "Monto del Contrato", times16format); sheet.addCell(label);
        label = new Label(4, salto3 + 6, "\$" + g.formatNumber(number: totalBase, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times16format);
        sheet.addCell(label);

        label = new Label(1, salto3 + 8, "Atentamente,  ", times16format); sheet.addCell(label);

        label = new Label(1, salto3 + 13, "______________________________________", times16format);
        sheet.addCell(label);

        def firmaC


        if(params.firmaCoordinador != ''){
            def personaRol = PersonaRol.get(params.firmaCoordinador)
            firmaC = personaRol.persona

            label = new Label(1, salto3 + 14, firmaC?.titulo?.toUpperCase() ?: '' + " " + (firmaC?.nombre?.toUpperCase() ?: '' + " " + firmaC?.apellido?.toUpperCase() ?: ''), times16format);
            sheet.addCell(label);

        }else{
            label = new Label(1, salto3 + 14, "Coordinador no asignado", times16format);
            sheet.addCell(label);
        }

//        label = new Label(1, salto3 + 14, "", times16format);
//        sheet.addCell(label);

//        if (cuenta == 3) {
//            label = new Label(1, salto3 + 13, "______________________________________", times16format);
//            sheet.addCell(label);
//            label = new Label(2, salto3 + 13, "______________________________________", times16format);
//            sheet.addCell(label);
//            label = new Label(3, salto3 + 13, "______________________________________", times16format);
//            sheet.addCell(label);
//            def salto4 = salto3 + 13
//
//            firmaFijaFormu.eachWithIndex { f, h ->
//
//                if (f != '') {
//
//                    firmas = Persona.get(f)
//
//                    label = new Label(h + 1, salto4 + 1, firmas?.titulo + " " + firmas?.nombre + " " + firmas?.apellido, times16format);
//                    sheet.addCell(label);
//                } else {
//                    label = new Label(h + 1, salto4 + 1, "Sin asignar,  ", times16format); sheet.addCell(label);
//                }
//            }
//
//            firmas = Persona.get(firmaFijaFormu[0])
//            label = new Label(1, salto4 + 2, firmas?.cargo, times16format); sheet.addCell(label);
//            label = new Label(2, salto4 + 2, "REVISOR", times16format); sheet.addCell(label);
//            label = new Label(3, salto4 + 2, "ELABORÓ", times16format); sheet.addCell(label);
//        }

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
        headersTitulo.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", titleFont2));
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



    def reporteVaeExcel () {

//        println("params " + params)

        def obra = Obra.get(params.id)
        def detalle
        detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def subPres = VolumenesObra.findAllByObra(obra, [sort: "orden"]).subPresupuesto.unique()

        def subPre
        def valores
        def fechaNueva = obra?.fechaCreacionObra?.format("dd-MM-yyyy");
        def fechaPU = (obra?.fechaPreciosRubros?.format("dd-MM-yyyy"));

        if (params.sub) {
            if (params.sub == '-1') {
                valores = preciosService.rbro_pcun_vae(obra?.id)
            } else {
                valores = preciosService.rbro_pcun_vae2(obra?.id, params.sub)
            }
        }
        else {
            valores = preciosService.rbro_pcun_vae(obra.id)
        }

        if (params.sub != '-1'){
            subPre= SubPresupuesto.get(params.sub).descripcion
        }else {
            subPre= -1
        }

        def nombres = []
        def corregidos = []
        def prueba = []
        valores.each {
            nombres += it.rbronmbr
        }

        nombres.each {
            def text = (it ?: '')
            text = text.decodeHTML()
            text = text.replaceAll(/</, /&lt;/);
            text = text.replaceAll(/>/, /&gt;/);
            text = text.replaceAll(/"/, /&quot;/);
            corregidos += text
        }

        valores.eachWithIndex{ j,i->
            j.rbronmbr = corregidos[i]
        }

        valores.each {
            prueba += it.rbronmbr
        }

        def indirecto = obra.totales / 100
        def c;
        def total1 = 0;
        def totales = 0
        def totalPresupuesto = 0;
        def vaeTotal = 0
        def vaeTotal1 = 0
        def totalVae= 0

        //excel

        WorkbookSettings workbookSettings = new WorkbookSettings()
        workbookSettings.locale = Locale.default

        def file = File.createTempFile('myExcelDocument', '.xls')
        file.deleteOnExit()
        WritableWorkbook workbook = Workbook.createWorkbook(file, workbookSettings)

        WritableFont font = new WritableFont(WritableFont.ARIAL, 12)
        WritableCellFormat formatXls = new WritableCellFormat(font)

        def row = 0
        WritableSheet sheet = workbook.createSheet('MySheet', 0)


        params.id = params.id.split(",")
        if (params.id.class == java.lang.String) {
            params.id = [params.id]
        }
        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, false);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        sheet.setColumnView(0, 12)
        sheet.setColumnView(1, 20)
        sheet.setColumnView(2, 35)
        sheet.setColumnView(3, 60)
        sheet.setColumnView(4, 25)
        sheet.setColumnView(5, 15)
        sheet.setColumnView(6, 15)
        sheet.setColumnView(7, 25)

        def label
        def number
        def nmro
        def numero = 1;
        def fila = 18;
        def filaSub = 17
        def ultimaFila


        //cabecera
        label = new Label(2, 2, "SEP - G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(2, 4, "DGCP - COORDINACIÓN DE FIJACIÓN DE PRECIOS", times16format); sheet.addCell(label);
        label = new Label(2, 6, "PRESUPUESTO", times16format); sheet.addCell(label);
        label = new Label(2, 8, "REQUIRENTE: " + obra?.departamento?.direccion?.nombre, times16format); sheet.addCell(label);
        label = new Label(2, 9, "FECHA: " + fechaNueva, times16format);
        sheet.addCell(label);
        label = new Label(2, 10, "FECHA Act. P.U.: " + fechaPU, times16format);
        sheet.addCell(label);
        label = new Label(2, 11, "NOMBRE: " + obra?.nombre, times16format); sheet.addCell(label);
        label = new Label(2, 12, "MEMO CANT. DE OBRA: " + obra?.memoCantidadObra, times16format); sheet.addCell(label);
        label = new Label(2, 13, "CÓDIGO OBRA: " + obra?.codigo, times16format); sheet.addCell(label);
        label = new Label(2, 14, "DOC. REFERENCIA: " + (obra?.oficioIngreso ?: '') + "  " + (obra?.referencia ?: ''), times16format); sheet.addCell(label);

        //columnas
        label = new Label(0, 16, "N°", times16format); sheet.addCell(label);
        label = new Label(1, 16, "CÓDIGO", times16format); sheet.addCell(label);
        label = new Label(2, 16, "ESPEC", times16format); sheet.addCell(label);
        label = new Label(3, 16, "RUBRO", times16format); sheet.addCell(label);
        label = new Label(4, 16, "DESCRIPCIÓN", times16format); sheet.addCell(label);
        label = new Label(5, 16, "UNIDAD", times16format); sheet.addCell(label);
        label = new Label(6, 16, "CANTIDAD", times16format); sheet.addCell(label);
        label = new Label(7, 16, "P.U.", times16format); sheet.addCell(label);
        label = new Label(8, 16, "C.TOTAL", times16format); sheet.addCell(label);
        label = new Label(9, 16, "PESO RELATIVO", times16format); sheet.addCell(label);
        label = new Label(10, 16, "VAE RUBRO", times16format); sheet.addCell(label);
        label = new Label(11, 16, "VAE TOTAL", times16format); sheet.addCell(label);


        subPres.each {sp->


                label = new Label(0, filaSub, sp?.descripcion?.toString()); sheet.addCell(label);

            valores.each {val->

            if(val.sbpr__id == sp.id){
                number = new Number(0, fila, val.vlobordn); sheet.addCell(number);
                label = new Label(1, fila, val.rbrocdgo.toString()); sheet.addCell(label);
                label = new Label(2, fila, val?.itemcdes?.toString() ?: ''); sheet.addCell(label);
                label = new Label(3, fila, val.rbronmbr.toString()); sheet.addCell(label);
                label = new Label(4, fila, val?.vlobdscr?.toString() ?: ''); sheet.addCell(label);
                label = new Label(5, fila, val.unddcdgo.toString()); sheet.addCell(label);
                number = new Number(6, fila, val.vlobcntd); sheet.addCell(number);
                number = new Number(7, fila, val.pcun); sheet.addCell(number);
                number = new Number(8, fila, val.totl); sheet.addCell(number);
                number = new Number(9, fila, val.relativo); sheet.addCell(number);
                if(val.vae_rbro != null){
                    number = new Number(10, fila, val.vae_rbro); sheet.addCell(number);
                }else{
                    number = new Number(10, fila, 0); sheet.addCell(number);
                }
                if(val.vae_totl != null){
                    number = new Number(11, fila, val.vae_totl); sheet.addCell(number);
                }else{
                    number = new Number(11, fila, 0); sheet.addCell(number);
                }


                fila++
                filaSub++
                totales = val.totl
                if(val.vae_totl != null){
                    vaeTotal = val.vae_totl
                }else{
                    vaeTotal = 0
                }

                totalPresupuesto = (total1 += totales);
                totalVae = (vaeTotal1 += vaeTotal)
                ultimaFila = fila

            }
            }

            fila++
            filaSub++

        }

        label = new Label(7, ultimaFila, "TOTAL ", times16format); sheet.addCell(label);
        number = new Number(8, ultimaFila, totalPresupuesto); sheet.addCell(number);
        number = new Number(9, ultimaFila, 100); sheet.addCell(number);
        number = new Number(11, ultimaFila, totalVae); sheet.addCell(number);

        label = new Label(2, ultimaFila+1, "CONDICIONES DEL CONTRATO ", times16format); sheet.addCell(label);
        label = new Label(2, ultimaFila+2, "Plazo de Ejecución: ", times16format); sheet.addCell(label);
        label = new Label(3, ultimaFila+2,  obra?.plazoEjecucionMeses + " mes(meses)", times16format); sheet.addCell(label);
        label = new Label(2, ultimaFila+3, "Anticipo: ", times16format); sheet.addCell(label);
        label = new Label(3, ultimaFila+3,  obra?.porcentajeAnticipo + " %", times16format); sheet.addCell(label);
        label = new Label(2, ultimaFila+4, "Elaboró: ", times16format); sheet.addCell(label);
        label = new Label(3, ultimaFila+4, (obra?.responsableObra?.titulo ?: '') + (obra?.responsableObra?.nombre ?: '') + ' ' + (obra?.responsableObra?.apellido ?: ''), times16format); sheet.addCell(label);



        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "VaeExcel.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());
    }



    def reporteCronogramaNuevoPdf() {
//        println "reporteCronogramaPdf params: $params"
        def tipo = params.tipo
        def obra = null, contrato = null, lbl = ""
        switch (tipo) {
            case "obra":
                obra = Obra.get(params.id.toLong())
                lbl = " la obra"
                break;
            case "contrato":
                contrato = Contrato.get(params.id)
                obra = contrato.obra
                lbl = "l contrato de la obra"
                break;
        }

        def meses = obra.plazoEjecucionMeses + (obra.plazoEjecucionDias > 0 ? 1 : 0)
//        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def detalle = VolumenContrato.findAllByObra(obra, [sort: "volumenOrden"])

        def precios = [:]
        def indirecto = obra.totales / 100

        preciosService.ac_rbroObra(obra.id)

        detalle.each {
            it.refresh()
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)

            if(res["precio"][0] != null){
                precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
            }else{
                precios.put(it.id.toString(), (0 * indirecto).toDouble().round(2))
            }
        }

        def baos = new ByteArrayOutputStream()

        def name = "cronograma${tipo.capitalize()}_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Font catFont2 = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font catFont3 = new Font(Font.TIMES_ROMAN, 16, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        def prmsHeaderHoja = [border: Color.WHITE]

        Document document
        document = new Document(PageSize.A4.rotate());
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Cronograma de${lbl} " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        /* ***************************************************** Titulo del reporte *******************************************************/
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", catFont3));
        preface.add(new Paragraph("CRONOGRAMA", catFont2));
        preface.add(new Paragraph("DGCP - COORDINACIÓN DE FIJACIÓN DE PRECIOS UNITARIOS", catFont2));
        addEmptyLine(preface, 1);
        Paragraph preface2 = new Paragraph();
        document.add(preface);
        document.add(preface2);
        Paragraph pMeses = new Paragraph();
        pMeses.add(new Paragraph("Obra: ${obra.descripcion} (${meses} mes${meses == 1 ? '' : 'es'})", info))
        addEmptyLine(pMeses, 1);
        document.add(pMeses);

        Paragraph pRequirente = new Paragraph();
        pRequirente.add(new Paragraph("Requirente: ${obra?.departamento?.direccion?.nombre + ' - ' + obra.departamento?.descripcion}", info))
        document.add(pRequirente);

        Paragraph codigoObra = new Paragraph();
        codigoObra.add(new Paragraph("Código de la Obra: ${obra?.codigo}", info))
        document.add(codigoObra);

        Paragraph docReferencia = new Paragraph();
        docReferencia.add(new Paragraph("Doc. Referencia: ${obra?.oficioIngreso}", info))
        document.add(docReferencia);

        Paragraph fecha = new Paragraph();
        fecha.add(new Paragraph("Fecha: ${printFecha(obra?.fechaCreacionObra)}", info))
        document.add(fecha);

        Paragraph plazo = new Paragraph();
        plazo.add(new Paragraph("Plazo: ${obra?.plazoEjecucionMeses} Meses" + " ${obra?.plazoEjecucionDias} Días", info))
        document.add(plazo);

        Paragraph rutaCritica = new Paragraph();
        rutaCritica.add(new Paragraph("Los rubros pertenecientes a la ruta crítica están marcados con un * antes de su código.", info))
        addEmptyLine(rutaCritica, 1);
        document.add(rutaCritica);

        /* ***************************************************** Fin Titulo del reporte ***************************************************/
        /* ***************************************************** Tabla cronograma *********************************************************/
        def tams = [10, 40, 5, 6, 6, 6, 2]
        meses.times {
            tams.add(7)
        }
        tams.add(10)

        PdfPTable tabla = new PdfPTable(8 + meses);
        tabla.setWidthPercentage(100);
        tabla.setWidths(arregloEnteros(tams))
        tabla.setWidthPercentage(100);

        addCellTabla(tabla, new Paragraph("Código", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("Rubro", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("Unidad", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("Cantidad", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("P. Unitario", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("C. Total", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("T.", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        meses.times { i ->
            addCellTabla(tabla, new Paragraph("Mes " + (i + 1), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tabla, new Paragraph("Total Rubro", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        tabla.setHeaderRows(1)

        def totalMes = []
        def sum = 0
        def borderWidth = 2

        detalle.eachWithIndex { vol, s ->
            def cronos
            switch (tipo) {
                case "obra":
//                    cronos = Cronograma.findAllByVolumenObra(vol)
                    cronos = Cronograma.findAllByVolumenObra(VolumenesObra.findByItemAndOrdenAndObra(vol.item, vol.volumenOrden, vol.obra))
                    break;
                case "contrato":
//                    cronos = CronogramaContrato.findAllByVolumenObra(vol)
                    cronos = CronogramaContratado.findAllByVolumenContrato(vol)
                    break;
            }

            def totalDolRow = 0, totalPrcRow = 0, totalCanRow = 0
            def parcial = Math.round(precios[vol.id.toString()] * vol.volumenCantidad*100)/100
            sum += parcial

            addCellTabla(tabla, new Paragraph((vol.rutaCritica == 'S' ? "* " : "") + vol.item.codigo, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tabla, new Paragraph(vol.item.nombre, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tabla, new Paragraph(vol.item.unidad.codigo, fontTd), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tabla, new Paragraph(numero(vol.volumenCantidad, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tabla, new Paragraph(numero(precios[vol.id.toString()], 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tabla, new Paragraph(numero(parcial, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tabla, new Paragraph('$', fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            meses.times { i ->
                def prec = cronos.find { it.periodo == i + 1 }
                totalDolRow += (prec ? prec.precio : 0)
                if (!totalMes[i]) {
                    totalMes[i] = 0
                }
                totalMes[i] += (prec ? prec.precio : 0)
                addCellTabla(tabla, new Paragraph(numero(prec?.precio, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            }
            addCellTabla(tabla, new Paragraph(numero(totalDolRow, 2) + ' $', fontTh), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tabla, new Paragraph(' ', fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 6])
            addCellTabla(tabla, new Paragraph('%', fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            meses.times { i ->
                def porc = cronos.find { it.periodo == i + 1 }
                totalPrcRow += (porc ? porc.porcentaje : 0)
                addCellTabla(tabla, new Paragraph(numero(porc?.porcentaje, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            }
            addCellTabla(tabla, new Paragraph(numero(totalPrcRow, 2) + ' %', fontTh), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])

            addCellTabla(tabla, new Paragraph(' ', fontTd), [border: Color.BLACK, bwb: borderWidth, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 6])
            addCellTabla(tabla, new Paragraph('F', fontTd), [border: Color.BLACK, bwb: borderWidth, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            meses.times { i ->
                def cant = cronos.find { it.periodo == i + 1 }
                totalCanRow += (cant ? cant.cantidad : 0)
                addCellTabla(tabla, new Paragraph(numero(cant?.cantidad, 2), fontTd), [border: Color.BLACK, bwb: borderWidth, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
            }
            addCellTabla(tabla, new Paragraph(numero(totalCanRow, 2) + ' F', fontTh), [border: Color.BLACK, bwb: borderWidth, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }

        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("TOTAL PARCIAL", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
        addCellTabla(tabla, new Paragraph(numero(sum, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("T", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        meses.times { i ->
            addCellTabla(tabla, new Paragraph(numero(totalMes[i], 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("TOTAL ACUMULADO", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("T", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        def acu = 0
        meses.times { i ->
            acu += totalMes[i]
            addCellTabla(tabla, new Paragraph(numero(acu, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("% PARCIAL", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("T", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        meses.times { i ->
            def prc = 100 * totalMes[i] / sum
            addCellTabla(tabla, new Paragraph(numero(prc, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("% ACUMULADO", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("T", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        acu = 0
        meses.times { i ->
            def prc = 100 * totalMes[i] / sum
            acu += prc
            addCellTabla(tabla, new Paragraph(numero(acu, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        document.add(tabla)
        /* ***************************************************** Fin Tabla cronograma *****************************************************/

        def personaElaboro
        def firmaCoordinador
        def ban = 0

        def deptoUsu = Persona.get(session.usuario.id).departamento

        def funcionCoor = Funcion.findByCodigo('O')
        def funcionElab = Funcion.findByCodigo('E')

        def personasDep = Persona.findAllByDepartamento(deptoUsu)
        def personasUtfpu = Persona.findAllByDepartamento(Departamento.findByCodigo('UTFPU'))

        def coordinador = PersonaRol.findByPersonaInListAndFuncion(personasDep,funcionCoor)
        def coordinadorUtfpu = PersonaRol.findByPersonaInListAndFuncion(personasUtfpu,funcionCoor)

        def elabUtfpu = PersonaRol.findAllByPersonaInListAndFuncion(personasUtfpu,funcionElab)

        def responsableRol = PersonaRol.findByPersona(Persona.get(obra?.responsableObra?.id))

        elabUtfpu.each {
            if(it?.id == responsableRol?.id){
                ban = 1
            }
        }


        PdfPTable tablaFirmas = new PdfPTable(3);
        tablaFirmas.setWidthPercentage(100);

        addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

        addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

        addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

        addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)

        if(obra?.responsableObra){
            personaElaboro = Persona.get(obra?.responsableObra?.id)
            addCellTabla(tablaFirmas, new Paragraph((personaElaboro?.titulo?.toUpperCase() ?: '') + " " + (personaElaboro?.nombre.toUpperCase() ?: '' ) + " " + (personaElaboro?.apellido?.toUpperCase() ?: ''), times8bold), prmsHeaderHoja)
        }else{
            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
        }

        addCellTabla(tablaFirmas, new Paragraph("", times8bold), prmsHeaderHoja)

        if(coordinador){
            if(ban == 1){
                firmaCoordinador = coordinadorUtfpu.persona
                addCellTabla(tablaFirmas, new Paragraph((firmaCoordinador?.titulo?.toUpperCase() ?: '') + " " + (firmaCoordinador?.nombre?.toUpperCase() ?: '') + " " + (firmaCoordinador?.apellido?.toUpperCase() ?: ''), times8bold), prmsHeaderHoja)
            }else{
                firmaCoordinador = coordinador.persona
                addCellTabla(tablaFirmas, new Paragraph((firmaCoordinador?.titulo?.toUpperCase() ?: '') + " " + (firmaCoordinador?.nombre?.toUpperCase() ?: '') + " " + (firmaCoordinador?.apellido?.toUpperCase() ?: ''), times8bold), prmsHeaderHoja)
            }

        }else{
            addCellTabla(tablaFirmas, new Paragraph("Coordinador no asignado", times8bold), prmsHeaderHoja)
        }
        //cargos

        addCellTabla(tablaFirmas, new Paragraph("ELABORÓ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph("COORDINADOR", times8bold), prmsHeaderHoja)

        addCellTabla(tablaFirmas, new Paragraph(personaElaboro?.departamento?.descripcion?.toUpperCase() ?: '', times8bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph((firmaCoordinador?.departamento?.descripcion?.toUpperCase() ?: ''), times8bold), prmsHeaderHoja)

        addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

        document.add(tablaFirmas);

        document.close();

        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)

    }

    def contratoFechas () {


        def cn = dbConnectionService.getConnection()
        def sql = "select * from rp_contrato()"
        def res =  cn.rows(sql.toString())

        //excel

        WorkbookSettings workbookSettings = new WorkbookSettings()
        workbookSettings.locale = Locale.default

        def file = File.createTempFile('myExcelDocument', '.xls')
        file.deleteOnExit()
        WritableWorkbook workbook = Workbook.createWorkbook(file, workbookSettings)

        WritableFont font = new WritableFont(WritableFont.ARIAL, 12)
        WritableCellFormat formatXls = new WritableCellFormat(font)

        def row = 0
        WritableSheet sheet = workbook.createSheet('MySheet', 0)


        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, false);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        sheet.setColumnView(0, 20)
        sheet.setColumnView(1, 20)
        sheet.setColumnView(2, 20)
        sheet.setColumnView(3, 20)
        sheet.setColumnView(4, 15)
        sheet.setColumnView(5, 20)
        sheet.setColumnView(6, 45)
        sheet.setColumnView(7, 20)
        sheet.setColumnView(8, 20)
        sheet.setColumnView(9, 15)
        sheet.setColumnView(10, 15)
        sheet.setColumnView(11, 15)
        sheet.setColumnView(12, 15)
        sheet.setColumnView(13, 15)
        sheet.setColumnView(14, 15)
        sheet.setColumnView(15, 15)

        def label
        def number
        def fila = 12;


        //cabecera
        label = new Label(2, 2, "SEP - G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(2, 4, "FISCALIZACIÓN", times16format); sheet.addCell(label);
        label = new Label(2, 6, "CONTRATOS", times16format); sheet.addCell(label);

        //columnas
        label = new Label(0, 10, "CÓDIGO CONTRATO", times16format); sheet.addCell(label);
        label = new Label(1, 10, "MONTO", times16format); sheet.addCell(label);
        label = new Label(2, 10, "ANTICIPO PAGADO", times16format); sheet.addCell(label);
        label = new Label(3, 10, "TOTAL PLANILLADO", times16format); sheet.addCell(label);
        label = new Label(4, 10, "PLAZO", times16format); sheet.addCell(label);
        label = new Label(5, 10, "CÓDIGO OBRA", times16format); sheet.addCell(label);
        label = new Label(6, 10, "NOMBRE OBRA", times16format); sheet.addCell(label);
        label = new Label(7, 10, "CANTÓN", times16format); sheet.addCell(label);
        label = new Label(8, 10, "PARROQUIA", times16format); sheet.addCell(label);
        label = new Label(9, 10, "FECHA DE SUBSCRIPCIÓN", times16format); sheet.addCell(label);
        label = new Label(10, 10, "FECHA INICIO OBRA", times16format); sheet.addCell(label);
        label = new Label(11, 10, "F ADMINISTRADOR", times16format); sheet.addCell(label);
        label = new Label(12, 10, "F PIDE PAGO ANTC", times16format); sheet.addCell(label);
        label = new Label(13, 10, "FECHA FINALIZACIÓN", times16format); sheet.addCell(label);
        label = new Label(14, 10, "FECHA ACTA PROVISIONAL", times16format); sheet.addCell(label);
        label = new Label(15, 10, "FECHA ACTA DEFINITIVA", times16format); sheet.addCell(label);


        res.each{ contrato->

            label = new Label(0, fila, contrato.cntrcdgo.toString() ?: ''); sheet.addCell(label);
            number = new Number(1, fila, contrato.cntrmnto ?: 0); sheet.addCell(number);
            number = new Number(2, fila, contrato.cntrantc ?: 0); sheet.addCell(number);
            number = new Number(3, fila, contrato.plnltotl ?: 0); sheet.addCell(number);
            number = new Number(4, fila, contrato.cntrplzo ?: 0); sheet.addCell(number);
            label = new Label(5, fila, contrato?.obracdgo?.toString() ?: ''); sheet.addCell(label);
            label = new Label(6, fila, contrato?.obranmbr?.toString() ?: ''); sheet.addCell(label);
            label = new Label(7, fila, contrato?.cntnnmbr?.toString() ?: ''); sheet.addCell(label);
            label = new Label(8, fila, contrato?.parrnmbr?.toString() ?: ''); sheet.addCell(label);
            label = new Label(9, fila, contrato?.cntrfcsb?.toString() ?: ''); sheet.addCell(label);
            label = new Label(10, fila, contrato?.obrafcin?.toString() ?: ''); sheet.addCell(label);
            label = new Label(11, fila, contrato?.fchaadmn?.toString() ?: ''); sheet.addCell(label);
            label = new Label(12, fila, contrato?.fchapdpg?.toString() ?: ''); sheet.addCell(label);

            label = new Label(13, fila, contrato?.cntrfcfs?.toString() ?: ''); sheet.addCell(label);
            label = new Label(14, fila, contrato?.acprfcha?.toString() ?: ''); sheet.addCell(label);
            label = new Label(15, fila, contrato?.acdffcha?.toString() ?: ''); sheet.addCell(label);

            fila++
        }


/*Para dropdown*/
//        WritableCellFeatures cellFeatures = new WritableCellFeatures();
//        cellFeatures.setComment("Seleccione:", 5, 2);
//        cellFeatures.setDataValidationList([1,2,3]);
//        jxl.write.Label dropDown = new jxl.write.Label(2, 5, "Select");
//        dropDown.setCellFeatures(cellFeatures);
//        sheet.addCell(dropDown);




        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "contratos.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());
    }



}
