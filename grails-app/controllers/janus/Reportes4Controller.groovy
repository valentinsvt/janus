package janus

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.Label
import jxl.write.NumberFormat
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook

import java.awt.Color


class Reportes4Controller {

    def index() {}
    def dbConnectionService
    def preciosService
    def buscadorService

    def meses = ['', "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"]

    private String printFecha(Date fecha) {
        if (fecha) {
            return (fecha.format("dd") + ' de ' + meses[fecha.format("MM").toInteger()] + ' de ' + fecha.format("yyyy"))
        } else {
            return "Error: no hay fecha que mostrar"
        }
    }


    static arregloEnteros(array) {
        int[] ia = new int[array.size()]
        array.eachWithIndex { it, i ->
            ia[i] = it.toInteger()
        }

        return ia
    }


    def registradas () {

    }

    def tablaRegistradas () {

//        println("paramsReg" + params)

        def obras

        def sql
        def cn
        def res


        def total1 = 0;
        def totales
        def totalPresupuestoBien=[];
        def valoresTotales = []

        def valores
        def subPres

        params.old = params.criterio

        params.criterio=cleanCriterio(params.criterio)

        def sqlBase =  "SELECT\n" +
                "  o.obra__id    id,\n" +
                "  o.obracdgo    codigo, \n" +
                "  o.obranmbr    nombre,\n" +
                "  o.obratipo    tipo,\n" +
                "  o.obrafcha    fecha,\n" +
                "  c.cmndnmbr    comunidad,\n" +
                "  p.parrnmbr    parroquia,\n" +
                "  n.cntnnmbr    canton,\n" +
                "  o.obraofsl    oficio,\n" +
                "  o.obrammsl    memo,\n" +
                "  e.dptodscr    elaborado,\n" +
                "  d.dptodscr    destino,\n" +
                "  o.obraofig    ingreso,\n" +
                "  o.obraetdo    estado,\n" +
                "  s.prsnnmbr    personan,\n" +
                "  s.prsnapll    personaa,\n" +
                "  t.tpobdscr    tipoobra\n" +
                "FROM obra o\n" +
                "  LEFT JOIN dpto d ON o.dptodstn = d.dpto__id\n" +
                "  LEFT JOIN dpto e ON o.dpto__id = e.dpto__id\n" +
                "  LEFT JOIN cmnd c ON o.cmnd__id = c.cmnd__id\n" +
                "  LEFT JOIN parr p ON c.parr__id = p.parr__id\n" +
                "  LEFT JOIN cntn n ON p.cntn__id = n.cntn__id\n" +
                "  LEFT JOIN prsn s ON o.obrainsp = s.prsn__id\n" +
                "  LEFT JOIN tpob t ON o.tpob__id = t.tpob__id\n"


        def filtro=" where obraetdo='R' or obraofig is NOT NULL "
        def filtroBuscador = ""

        def buscador = ""

        switch (params.buscador) {
            case "cdgo":
            case "nmbr":
            case "ofig":
            case "ofsl":
            case "mmsl":
            case "frpl":
            case "tipo":
                buscador = "obra"+params.buscador
                filtroBuscador =" and ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "cmnd":
                filtroBuscador = " and c.cmndnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "parr":
                filtroBuscador = " and p.parrnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "cntn":
                filtroBuscador = " and n.cntnnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "insp":
            case "rvsr":
                filtroBuscador = " and (s.prsnnmbr ILIKE ('%${params.criterio}%') or s.prsnapll ILIKE ('%${params.criterio}%')) "
                break;

        }



        if(params.estado == '1'){

            filtro = " where (obraetdo='R' or obraofig is NOT NULL) "
        }
        else if(params.estado == '2'){

            filtro = " where obraofig is NOT NULL "

        }
        else if(params.estado == '3'){

            filtro = " where obraetdo='R' "

        }
//        println "====================="
//        println filtro
//        println filtroBuscador
//        println "====================="

        params.criterio = params.old

        sql = sqlBase + filtro + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())
//        println(sql)
//        println(res)

        res.each{

            totales = 0
            total1=0

            valores =  preciosService.rbro_pcun_v2(it.id)

            subPres =  VolumenesObra.findAllByObra(Obra.get(it.id),[sort:"orden"]).subPresupuesto.unique()

            subPres.each { s->

                valores.each {
                    if(it.sbprdscr == s.descripcion){

                        totales = it.totl
                        totalPresupuestoBien = (total1 += totales)
                    }


                }


            }

//           println("--->>" + totalPresupuestoBien)
            valoresTotales += totalPresupuestoBien

        }

//        println("##" + valoresTotales)

        return [obras: obras, res: res, valoresTotales: valoresTotales, params:params]
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }


    def addCellTabla(table, paragraph, params) {
        PdfPCell cell = new PdfPCell(paragraph);
//        println "params "+params
        cell.setBorderColor(Color.BLACK);
        if (params.border) {
            if (!params.bordeBot)
                if (!params.bordeTop)
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

    def reporteRegistradas () {

//        println("params reporte:" + params)

        def sql
        def cn
        def res


        def total1 = 0;
        def totales
        def totalPresupuestoBien=[];
        def valoresTotales = []

        def valores
        def subPres

        def sqlBase =  "SELECT\n" +
                "  o.obra__id    id,\n" +
                "  o.obracdgo    codigo, \n" +
                "  o.obranmbr    nombre,\n" +
                "  o.obratipo    tipo,\n" +
                "  o.obrafcha    fecha,\n" +
                "  c.cmndnmbr    comunidad,\n" +
                "  p.parrnmbr    parroquia,\n" +
                "  n.cntnnmbr    canton,\n" +
                "  o.obraofsl    oficio,\n" +
                "  o.obrammsl    memo,\n" +
                "  e.dptodscr    elaborado,\n" +
                "  d.dptodscr    destino,\n" +
                "  o.obraofig    ingreso,\n" +
                "  o.obraetdo    estado,\n" +
                "  s.prsnnmbr    personan,\n" +
                "  s.prsnapll    personaa,\n" +
                "  t.tpobdscr    tipoobra\n" +
                "FROM obra o\n" +
                "  LEFT JOIN dpto d ON o.dptodstn = d.dpto__id\n" +
                "  LEFT JOIN dpto e ON o.dpto__id = e.dpto__id\n" +
                "  LEFT JOIN cmnd c ON o.cmnd__id = c.cmnd__id\n" +
                "  LEFT JOIN parr p ON c.parr__id = p.parr__id\n" +
                "  LEFT JOIN cntn n ON p.cntn__id = n.cntn__id\n" +
                "  LEFT JOIN prsn s ON o.obrainsp = s.prsn__id\n" +
                "  LEFT JOIN tpob t ON o.tpob__id = t.tpob__id\n"


        def filtro=" where obraetdo='R' or obraofig is NOT NULL "
        def filtroBuscador = ""



        switch (params.buscador) {
            case "cdgo":
            case "nmbr":
            case "ofig":
            case "ofsl":
            case "mmsl":
            case "frpl":
            case "tipo":
                params.buscador = "obra"+params.buscador
                filtroBuscador =" and ${params.buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "cmnd":
                filtroBuscador = " and c.cmndnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "parr":
                filtroBuscador = " and p.parrnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "cntn":
                filtroBuscador = " and n.cntnnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "insp":
            case "rsvr":
                filtroBuscador = " and s.prsnnmbr ILIKE ('%${params.criterio}%') or s.prsnapll ILIKE ('%${params.criterio}%') "
                break;

        }



        if(params.estado == '1'){

            filtro = " where (obraetdo='R' or obraofig is NOT NULL) "
        }
        else if(params.estado == '2'){

            filtro = " where obraofig is NOT NULL "

        }
        else if(params.estado == '3'){

            filtro = " where obraetdo='R' "

        }

        sql = sqlBase + filtro + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())

//        println(sql)

        //valor

        res.each{

            totales = 0
            total1=0

            valores =  preciosService.rbro_pcun_v2(it.id)

            subPres =  VolumenesObra.findAllByObra(Obra.get(it.id),[sort:"orden"]).subPresupuesto.unique()

            subPres.each { s->

                valores.each {
                    if(it.sbprdscr == s.descripcion){

                        totales = it.totl
                        totalPresupuestoBien = (total1 += totales)
                    }


                }


            }

            valoresTotales += totalPresupuestoBien

        }

        //reporte

        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]
        def prmsHeaderHoja3 = [border: Color.WHITE, colspan: 5]
        def prmsHeader = [border: Color.WHITE, colspan: 7, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsHeader2 = [border: Color.WHITE, colspan: 3, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE, bg: Color.WHITE,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead2 = [border: Color.WHITE,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, bordeTop: "1", bordeBot: "1"]
        def prmsCellHead3 = [border: Color.WHITE,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1", bordeBot: "1"]
        def prmsCellHeadRight = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenter = [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenterLeft = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_LEFT]
        def prmsCellRight = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT]
        def prmsCellRight2 = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1", bordeBot: "1"]
        def prmsCellRightTop = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1"]
        def prmsCellRightBot = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeBot: "1"]
        def prmsCellLeft = [border: Color.WHITE, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.WHITE, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum,
                prmsHeaderHoja2: prmsHeaderHoja2, prmsCellRight: prmsCellRight, prmsCellHeadRight: prmsCellHeadRight, prmsCellHead2: prmsCellHead2,
                prmsCellRight2: prmsCellRight2, prmsCellRightTop: prmsCellRightTop, prmsCellRightBot: prmsCellRightBot]

        def baos = new ByteArrayOutputStream()
        def name = "registradas_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        def fonts = [times12bold: times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal, times18bold: times18bold]

        Document document
        document = new Document(PageSize.A4.rotate());
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();

//        document.setMargins(2,2,2,2)
        document.addTitle("ObrasRegistradas " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("documentosObra, janus, presupuesto");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");



        Paragraph headers = new Paragraph();
        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);
        headers.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", times18bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("REPORTE DE OBRAS REGISTRADAS", times12bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("Quito, " + printFecha(new Date()).toUpperCase(), times12bold));
        addEmptyLine(headers, 1);
        document.add(headers);

        PdfPTable tablaRegistradas = new PdfPTable(8);
        tablaRegistradas.setWidthPercentage(100);
        tablaRegistradas.setWidths(arregloEnteros([14, 35, 8, 8, 30, 10, 10,10]))

        addCellTabla(tablaRegistradas, new Paragraph("Código", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Nombre", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Tipo", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Fecha Reg.", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Cantón-Parroquia-Comunidad", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Valor", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Elaborado", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Estado", times8bold), prmsCellHead2)

        res.eachWithIndex {i,j->

            addCellTabla(tablaRegistradas, new Paragraph(i.codigo, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.nombre, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.tipoobra, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatDate(date: i.fecha, format: "dd-MM-yyyy"), times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.canton + "-" + i.parroquia + "-" + i.comunidad, times8normal), prmsCellLeft)
            if(valoresTotales[j] != null){
                addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: valoresTotales[j].toDouble(), minFractionDigits:
                        5, maxFractionDigits: 5, format: "##,##0", locale: "ec"), times8normal), prmsCellRight)
            }else {

                addCellTabla(tablaRegistradas, new Paragraph("", times8normal), prmsCellLeft)

            }

            addCellTabla(tablaRegistradas, new Paragraph(i.elaborado, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.estado, times8normal), prmsCellLeft)



        }


        document.add(tablaRegistradas);
        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)

    }

    def reporteRegistradasExcel () {



        def sql
        def cn
        def res


        def total1 = 0;
        def totales
        def totalPresupuestoBien=[];
        def valoresTotales = []

        def valores
        def subPres

        def sqlBase =  "SELECT\n" +
                "  o.obra__id    id,\n" +
                "  o.obracdgo    codigo, \n" +
                "  o.obranmbr    nombre,\n" +
                "  o.obratipo    tipo,\n" +
                "  o.obrafcha    fecha,\n" +
                "  c.cmndnmbr    comunidad,\n" +
                "  p.parrnmbr    parroquia,\n" +
                "  n.cntnnmbr    canton,\n" +
                "  o.obraofsl    oficio,\n" +
                "  o.obrammsl    memo,\n" +
                "  e.dptodscr    elaborado,\n" +
                "  d.dptodscr    destino,\n" +
                "  o.obraofig    ingreso,\n" +
                "  o.obraetdo    estado,\n" +
                "  s.prsnnmbr    personan,\n" +
                "  s.prsnapll    personaa,\n" +
                "  t.tpobdscr    tipoobra\n" +
                "FROM obra o\n" +
                "  LEFT JOIN dpto d ON o.dptodstn = d.dpto__id\n" +
                "  LEFT JOIN dpto e ON o.dpto__id = e.dpto__id\n" +
                "  LEFT JOIN cmnd c ON o.cmnd__id = c.cmnd__id\n" +
                "  LEFT JOIN parr p ON c.parr__id = p.parr__id\n" +
                "  LEFT JOIN cntn n ON p.cntn__id = n.cntn__id\n" +
                "  LEFT JOIN prsn s ON o.obrainsp = s.prsn__id\n" +
                "  LEFT JOIN tpob t ON o.tpob__id = t.tpob__id\n"


        def filtro=" where obraetdo='R' or obraofig is NOT NULL "
        def filtroBuscador = ""



        switch (params.buscador) {
            case "cdgo":
            case "nmbr":
            case "ofig":
            case "ofsl":
            case "mmsl":
            case "frpl":
            case "tipo":
                params.buscador = "obra"+params.buscador
                filtroBuscador =" and ${params.buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "cmnd":
                filtroBuscador = " and c.cmndnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "parr":
                filtroBuscador = " and p.parrnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "cntn":
                filtroBuscador = " and n.cntnnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "insp":
            case "rsvr":
                filtroBuscador = " and s.prsnnmbr ILIKE ('%${params.criterio}%') or s.prsnapll ILIKE ('%${params.criterio}%') "
                break;

        }



        if(params.estado == '1'){

            filtro = " where (obraetdo='R' or obraofig is NOT NULL) "
        }
        else if(params.estado == '2'){

            filtro = " where obraofig is NOT NULL "

        }
        else if(params.estado == '3'){

            filtro = " where obraetdo='R' "

        }

        sql = sqlBase + filtro + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())

//        println(sql)

        //valor

        res.each{

            totales = 0
            total1=0

            valores =  preciosService.rbro_pcun_v2(it.id)

            subPres =  VolumenesObra.findAllByObra(Obra.get(it.id),[sort:"orden"]).subPresupuesto.unique()

            subPres.each { s->

                valores.each {
                    if(it.sbprdscr == s.descripcion){

                        totales = it.totl
                        totalPresupuestoBien = (total1 += totales)
                    }


                }


            }

            valoresTotales += totalPresupuestoBien

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
        WritableSheet sheet = workbook.createSheet('MySheet', 0)
        // fija el ancho de la columna
        // sheet.setColumnView(1,40)

        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, false);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        sheet.setColumnView(0, 12)
        sheet.setColumnView(1, 60)
        sheet.setColumnView(2, 25)
        sheet.setColumnView(3, 25)
        sheet.setColumnView(4, 40)
        sheet.setColumnView(5, 25)
        sheet.setColumnView(8, 20)
        // inicia textos y numeros para asocias a columnas

        def label
        def nmro
        def number

        def fila = 6;


        NumberFormat nf = new NumberFormat("#.##");
        WritableCellFormat cf2obj = new WritableCellFormat(nf);

        label = new Label(1, 1, "G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(1, 2, "REPORTE EXCEL OBRAS REGISTRADAS", times16format); sheet.addCell(label);



        label = new Label(0, 4, "Código: ", times16format); sheet.addCell(label);
        label = new Label(1, 4, "Nombre", times16format); sheet.addCell(label);
        label = new Label(2, 4, "Tipo", times16format); sheet.addCell(label);
        label = new Label(3, 4, "Fecha Reg.", times16format); sheet.addCell(label);
        label = new Label(4, 4, "Cantón-Parroquia-Comunidad", times16format); sheet.addCell(label);
        label = new Label(5, 4, "Valor", times16format); sheet.addCell(label);
        label = new Label(6, 4, "Elaborado", times16format); sheet.addCell(label);
        label = new Label(7, 4, "Estado", times16format); sheet.addCell(label);

        res.eachWithIndex {i, j->


            label = new Label(0, fila, i.codigo.toString()); sheet.addCell(label);
            label = new Label(1, fila, i?.nombre.toString()); sheet.addCell(label);
            label = new Label(2, fila, i?.tipoobra?.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.fecha?.toString()); sheet.addCell(label);
            label = new Label(4, fila, i?.canton?.toString() + " " + i?.parroquia?.toString() + " " + i?.comunidad?.toString()); sheet.addCell(label);
            if(valoresTotales[j]!= null){
                number = new jxl.write.Number(5, fila, valoresTotales[j]); sheet.addCell(number);
            }else{
                label = new Label(5, fila, " "); sheet.addCell(label);
            }

            label = new Label(6, fila, i?.elaborado?.toString()); sheet.addCell(label);
            label = new Label(7, fila, i?.estado?.toString()); sheet.addCell(label);
            fila++

        }


        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "DocumentosObraExcel.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());

    }



    def contratadas () {



    }

    def tablaContratadas () {


//                println("paramsCont" + params)

        def obras = []

        def sql
        def cn
        def res


        def total1 = 0;
        def totales
        def totalPresupuestoBien=[];
        def valoresTotales = []

        def valores
        def subPres

        params.old = params.criterio

        params.criterio=cleanCriterio(params.criterio)

        def sqlBase =  "SELECT\n" +
                "  o.obra__id    id,\n" +
                "  o.obracdgo    codigo, \n" +
                "  o.obranmbr    nombre,\n" +
                "  o.obratipo    tipo,\n" +
                "  o.obrafcha    fecha,\n" +
                "  c.cmndnmbr    comunidad,\n" +
                "  p.parrnmbr    parroquia,\n" +
                "  n.cntnnmbr    canton,\n" +
                "  o.obraofsl    oficio,\n" +
                "  o.obrammsl    memo,\n" +
                "  e.dptodscr    elaborado,\n" +
                "  d.dptodscr    destino,\n" +
                "  o.obraofig    ingreso,\n" +
                "  o.obraetdo    estado,\n" +
                "  s.prsnnmbr    personan,\n" +
                "  s.prsnapll    personaa,\n" +
                "  t.tpobdscr    tipoobra\n" +
                "FROM obra o\n" +
                "  LEFT JOIN dpto d ON o.dptodstn = d.dpto__id\n" +
                "  LEFT JOIN dpto e ON o.dpto__id = e.dpto__id\n" +
                "  LEFT JOIN cmnd c ON o.cmnd__id = c.cmnd__id\n" +
                "  LEFT JOIN parr p ON c.parr__id = p.parr__id\n" +
                "  LEFT JOIN cntn n ON p.cntn__id = n.cntn__id\n" +
                "  LEFT JOIN prsn s ON o.obrainsp = s.prsn__id\n" +
                "  LEFT JOIN tpob t ON o.tpob__id = t.tpob__id\n"

        def filtroBuscador = ""
        def buscador=""



        switch (params.buscador) {
            case "cdgo":
            case "nmbr":
            case "ofig":
            case "ofsl":
            case "mmsl":
            case "frpl":
                buscador = "obra"+params.buscador
                filtroBuscador =" where ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "cmnd":
                filtroBuscador = " where c.cmndnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "parr":
                filtroBuscador = " where p.parrnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "cntn":
                filtroBuscador = " where n.cntnnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "tipo":
                filtroBuscador = " where t.tpobdscr ILIKE ('%${params.criterio}%') "
                break;
            case "insp":
            case "rvsr":
                filtroBuscador = " where (s.prsnnmbr ILIKE ('%${params.criterio}%') or s.prsnapll ILIKE ('%${params.criterio}%')) "
                break;

        }

        params.criterio = params.old
        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())

        println(sql)
//        println(res)


        def concurso
        def obra
        def oferta
        def contrato
        def contratos = []


        res.each{ i->

            obra = Obra.get(i.id)

            concurso = janus.pac.Concurso.findByObra(obra)


            if(concurso){
                oferta = janus.pac.Oferta.findAllByConcurso(concurso)

                if(oferta != [] && concurso != null){

                    oferta.each {j->

                        contrato = Contrato.findByOferta(j)
                        obras += i
                        contratos += contrato
                    }

                }


            }



        }

        obras.each{

            totales = 0
            total1=0

            valores =  preciosService.rbro_pcun_v2(it.id)

            subPres =  VolumenesObra.findAllByObra(Obra.get(it.id),[sort:"orden"]).subPresupuesto.unique()

            subPres.each { s->

                valores.each {
                    if(it.sbprdscr == s.descripcion){

                        totales = it.totl
                        totalPresupuestoBien = (total1 += totales)
                    }


                }


            }

//           println("--->>" + totalPresupuestoBien)
            valoresTotales += totalPresupuestoBien

        }

//        println("##" + valoresTotales)

//        if(!params.criterio){
//
//            obras = []
//        }


        return [obras: obras, res: res, valoresTotales: valoresTotales, params:params, contratos: contratos]



    }


    def reporteContratadas () {


        def obras = []

        def sql
        def cn
        def res
        def total1 = 0;
        def totales
        def totalPresupuestoBien=[];
        def valoresTotales = []

        def valores
        def subPres

        def sqlBase =  "SELECT\n" +
                "  o.obra__id    id,\n" +
                "  o.obracdgo    codigo, \n" +
                "  o.obranmbr    nombre,\n" +
                "  o.obratipo    tipo,\n" +
                "  o.obrafcha    fecha,\n" +
                "  c.cmndnmbr    comunidad,\n" +
                "  p.parrnmbr    parroquia,\n" +
                "  n.cntnnmbr    canton,\n" +
                "  o.obraofsl    oficio,\n" +
                "  o.obrammsl    memo,\n" +
                "  e.dptodscr    elaborado,\n" +
                "  d.dptodscr    destino,\n" +
                "  o.obraofig    ingreso,\n" +
                "  o.obraetdo    estado,\n" +
                "  s.prsnnmbr    personan,\n" +
                "  s.prsnapll    personaa,\n" +
                "  t.tpobdscr    tipoobra\n" +
                "FROM obra o\n" +
                "  LEFT JOIN dpto d ON o.dptodstn = d.dpto__id\n" +
                "  LEFT JOIN dpto e ON o.dpto__id = e.dpto__id\n" +
                "  LEFT JOIN cmnd c ON o.cmnd__id = c.cmnd__id\n" +
                "  LEFT JOIN parr p ON c.parr__id = p.parr__id\n" +
                "  LEFT JOIN cntn n ON p.cntn__id = n.cntn__id\n" +
                "  LEFT JOIN prsn s ON o.obrainsp = s.prsn__id\n" +
                "  LEFT JOIN tpob t ON o.tpob__id = t.tpob__id\n"

        def filtroBuscador = ""

        params.criterio = params.criterio.trim();

        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]
        def prmsHeaderHoja3 = [border: Color.WHITE, colspan: 5]
        def prmsHeader = [border: Color.WHITE, colspan: 7, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsHeader2 = [border: Color.WHITE, colspan: 3, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE, bg: Color.WHITE,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead2 = [border: Color.WHITE,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, bordeTop: "1", bordeBot: "1"]
        def prmsCellHead3 = [border: Color.WHITE,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1", bordeBot: "1"]
        def prmsCellHeadRight = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenter = [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenterLeft = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_LEFT]
        def prmsCellRight = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT]
        def prmsCellRight2 = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1", bordeBot: "1"]
        def prmsCellRightTop = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1"]
        def prmsCellRightBot = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeBot: "1"]
        def prmsCellLeft = [border: Color.WHITE, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.WHITE, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum,
                prmsHeaderHoja2: prmsHeaderHoja2, prmsCellRight: prmsCellRight, prmsCellHeadRight: prmsCellHeadRight, prmsCellHead2: prmsCellHead2,
                prmsCellRight2: prmsCellRight2, prmsCellRightTop: prmsCellRightTop, prmsCellRightBot: prmsCellRightBot]

        def baos = new ByteArrayOutputStream()
        def name = "contratadas_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        def fonts = [times12bold: times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal, times18bold: times18bold]

        Document document
        document = new Document(PageSize.A4.rotate());
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();

//        document.setMargins(2,2,2,2)
        document.addTitle("ObrasContratadas" + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("documentosObra, janus, presupuesto");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");



        Paragraph headers = new Paragraph();
        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);
        headers.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", times18bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("REPORTE DE OBRAS CONTRATADAS", times12bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("Quito, " + printFecha(new Date()).toUpperCase(), times12bold));
        addEmptyLine(headers, 1);
        document.add(headers);

        PdfPTable tablaRegistradas = new PdfPTable(8);
        tablaRegistradas.setWidthPercentage(100);
        tablaRegistradas.setWidths(arregloEnteros([14, 30, 15, 8, 25, 10, 20, 10]))

        addCellTabla(tablaRegistradas, new Paragraph("Código", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Nombre", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Tipo", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Fecha Reg.", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Cantón-Parroquia-Comunidad", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Valor", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Elaborado", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Contrato", times8bold), prmsCellHead2)


//        println "\n\nREPORTE "+params
//        println "SQL   "+sql+"\n\n"

//        if(params.criterio != '') {


        switch (params.buscador) {
            case "cdgo":
            case "nmbr":
            case "ofig":
            case "ofsl":
            case "mmsl":
            case "frpl":
            case "tipo":
                params.buscador = "obra"+params.buscador
                filtroBuscador =" where ${params.buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "cmnd":
                filtroBuscador = " where c.cmndnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "parr":
                filtroBuscador = " where p.parrnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "cntn":
                filtroBuscador = " where n.cntnnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "insp":
            case "rvsr":
                filtroBuscador = " where (s.prsnnmbr ILIKE ('%${params.criterio}%') or s.prsnapll ILIKE ('%${params.criterio}%')) "
                break;

        }



        sql = sqlBase + filtroBuscador

//            println "++++++++ SQL   "+sql+"\n\n"


        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())

//            println(sql)
//        println(res)


        def concurso
        def obra
        def oferta
        def contrato
        def contratos = []


        res.each{ i->

            obra = Obra.get(i.id)

            concurso = janus.pac.Concurso.findByObra(obra)


            if(concurso){
                oferta = janus.pac.Oferta.findAllByConcurso(concurso)

                if(oferta != [] && concurso != null){

                    oferta.each {j->

                        contrato = Contrato.findByOferta(j)
                        obras += i
                        contratos += contrato
                    }

                }


            }

        }

        obras.each{

            totales = 0
            total1=0

            valores =  preciosService.rbro_pcun_v2(it.id)

            subPres =  VolumenesObra.findAllByObra(Obra.get(it.id),[sort:"orden"]).subPresupuesto.unique()

            subPres.each { s->

                valores.each {
                    if(it.sbprdscr == s.descripcion){

                        totales = it.totl
                        totalPresupuestoBien = (total1 += totales)
                    }


                }


            }

//           println("--->>" + totalPresupuestoBien)
            valoresTotales += totalPresupuestoBien

        }


//            println("-->" + obras)
        //reporte

        obras.eachWithIndex {i,j->

            addCellTabla(tablaRegistradas, new Paragraph(i.codigo, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.nombre, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.tipoobra, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatDate(date: i.fecha, format: "dd-MM-yyyy"), times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.canton + "-" + i.parroquia + "-" + i.comunidad, times8normal), prmsCellLeft)
            if(valoresTotales[j] != null){
                addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: valoresTotales[j].toDouble(), minFractionDigits:
                        5, maxFractionDigits: 5, format: "##,##0", locale: "ec"), times8normal), prmsCellRight)
            }else {

                addCellTabla(tablaRegistradas, new Paragraph("", times8normal), prmsCellLeft)

            }
            addCellTabla(tablaRegistradas, new Paragraph(i.elaborado, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(contratos[j].codigo, times8normal), prmsCellLeft)



        }

//        }


        document.add(tablaRegistradas);
        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)

    }

    def reporteExcelContratadas () {




        def obras = []

        def sql
        def cn
        def res
        def total1 = 0;
        def totales
        def totalPresupuestoBien=[];
        def valoresTotales = []

        def valores
        def subPres

        def sqlBase =  "SELECT\n" +
                "  o.obra__id    id,\n" +
                "  o.obracdgo    codigo, \n" +
                "  o.obranmbr    nombre,\n" +
                "  o.obratipo    tipo,\n" +
                "  o.obrafcha    fecha,\n" +
                "  c.cmndnmbr    comunidad,\n" +
                "  p.parrnmbr    parroquia,\n" +
                "  n.cntnnmbr    canton,\n" +
                "  o.obraofsl    oficio,\n" +
                "  o.obrammsl    memo,\n" +
                "  e.dptodscr    elaborado,\n" +
                "  d.dptodscr    destino,\n" +
                "  o.obraofig    ingreso,\n" +
                "  o.obraetdo    estado,\n" +
                "  s.prsnnmbr    personan,\n" +
                "  s.prsnapll    personaa,\n" +
                "  t.tpobdscr    tipoobra\n" +
                "FROM obra o\n" +
                "  LEFT JOIN dpto d ON o.dptodstn = d.dpto__id\n" +
                "  LEFT JOIN dpto e ON o.dpto__id = e.dpto__id\n" +
                "  LEFT JOIN cmnd c ON o.cmnd__id = c.cmnd__id\n" +
                "  LEFT JOIN parr p ON c.parr__id = p.parr__id\n" +
                "  LEFT JOIN cntn n ON p.cntn__id = n.cntn__id\n" +
                "  LEFT JOIN prsn s ON o.obrainsp = s.prsn__id\n" +
                "  LEFT JOIN tpob t ON o.tpob__id = t.tpob__id\n"

        def filtroBuscador = ""

        params.criterio = params.criterio.trim();


        switch (params.buscador) {
            case "cdgo":
            case "nmbr":
            case "ofig":
            case "ofsl":
            case "mmsl":
            case "frpl":
            case "tipo":
                params.buscador = "obra"+params.buscador
                filtroBuscador =" where ${params.buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "cmnd":
                filtroBuscador = " where c.cmndnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "parr":
                filtroBuscador = " where p.parrnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "cntn":
                filtroBuscador = " where n.cntnnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "insp":
            case "rvsr":
                filtroBuscador = " where (s.prsnnmbr ILIKE ('%${params.criterio}%') or s.prsnapll ILIKE ('%${params.criterio}%')) "
                break;

        }



        sql = sqlBase + filtroBuscador

//            println "++++++++ SQL   "+sql+"\n\n"


        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())


//        println(sql)

        //valor

        def concurso
        def obra
        def oferta
        def contrato
        def contratos = []


        res.each{ i->

            obra = Obra.get(i.id)

            concurso = janus.pac.Concurso.findByObra(obra)


            if(concurso){
                oferta = janus.pac.Oferta.findAllByConcurso(concurso)

                if(oferta != [] && concurso != null){

                    oferta.each {j->

                        contrato = Contrato.findByOferta(j)
                        obras += i
                        contratos += contrato
                    }

                }


            }

        }

        obras.each{

            totales = 0
            total1=0

            valores =  preciosService.rbro_pcun_v2(it.id)

            subPres =  VolumenesObra.findAllByObra(Obra.get(it.id),[sort:"orden"]).subPresupuesto.unique()

            subPres.each { s->

                valores.each {
                    if(it.sbprdscr == s.descripcion){

                        totales = it.totl
                        totalPresupuestoBien = (total1 += totales)
                    }


                }


            }

//           println("--->>" + totalPresupuestoBien)
            valoresTotales += totalPresupuestoBien

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
        WritableSheet sheet = workbook.createSheet('MySheet', 0)
        // fija el ancho de la columna
        // sheet.setColumnView(1,40)

        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, false);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        sheet.setColumnView(0, 12)
        sheet.setColumnView(1, 60)
        sheet.setColumnView(2, 25)
        sheet.setColumnView(3, 25)
        sheet.setColumnView(4, 40)
        sheet.setColumnView(5, 25)
        sheet.setColumnView(8, 20)
        // inicia textos y numeros para asocias a columnas

        def label
        def nmro
        def number

        def fila = 6;


        NumberFormat nf = new NumberFormat("#.##");
        WritableCellFormat cf2obj = new WritableCellFormat(nf);

        label = new Label(1, 1, "G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(1, 2, "REPORTE EXCEL OBRAS CONTRATADAS", times16format); sheet.addCell(label);



        label = new Label(0, 4, "Código: ", times16format); sheet.addCell(label);
        label = new Label(1, 4, "Nombre", times16format); sheet.addCell(label);
        label = new Label(2, 4, "Tipo", times16format); sheet.addCell(label);
        label = new Label(3, 4, "Fecha Reg.", times16format); sheet.addCell(label);
        label = new Label(4, 4, "Cantón-Parroquia-Comunidad", times16format); sheet.addCell(label);
        label = new Label(5, 4, "Valor", times16format); sheet.addCell(label);
        label = new Label(6, 4, "Elaborado", times16format); sheet.addCell(label);
        label = new Label(7, 4, "Contrato", times16format); sheet.addCell(label);

        res.eachWithIndex {i, j->


            label = new Label(0, fila, i.codigo.toString()); sheet.addCell(label);
            label = new Label(1, fila, i?.nombre.toString()); sheet.addCell(label);
            label = new Label(2, fila, i?.tipoobra?.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.fecha?.toString()); sheet.addCell(label);
            label = new Label(4, fila, i?.canton?.toString() + " " + i?.parroquia?.toString() + " " + i?.comunidad?.toString()); sheet.addCell(label);
            if(valoresTotales[j]!= null){
                number = new jxl.write.Number(5, fila, valoresTotales[j]); sheet.addCell(number);
            }else{
                label = new Label(5, fila, " "); sheet.addCell(label);
            }

            label = new Label(6, fila, i?.elaborado?.toString()); sheet.addCell(label);
            label = new Label(7, fila, " "); sheet.addCell(label);
//            label = new Label(7, fila, contratos[j].codigo); sheet.addCell(label);
            fila++

        }


        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "DocumentosObraExcel.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());

    }


    def aseguradoras () {



    }


    def tablaAseguradoras () {

        def obras = []

        def sql
        def cn
        def res

        params.old = params.criterio

        params.criterio=cleanCriterio(params.criterio)

        def sqlBase =  "SELECT\n" +
                "  a.asgr__id    id,\n" +
                "  a.asgrfaxx    fax, \n" +
                "  a.asgrtelf    telefono,\n" +
                "  a.asgrnmbr    nombre,\n" +
                "  a.asgrdire    direccion,\n" +
                "  a.asgrrspn    contacto,\n" +
                "  a.asgrobsr    observaciones,\n" +
                "  a.asgrfeccn    fecha,\n" +
                "  t.tpasdscr    tipoaseguradora\n" +
                "FROM asgr a\n" +
                "  LEFT JOIN tpas t ON a.tpas__id = t.tpas__id\n"

        def filtroBuscador = ""
        def buscador=""



        switch (params.buscador) {
            case "nmbr":
            case "telf":
            case "faxx":
            case "rspn":
            case "dire":
                buscador = "asgr"+params.buscador
                filtroBuscador =" where ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "tipo":
                filtroBuscador = " where t.tpasdscr ILIKE ('%${params.criterio}%') "
                break;
//            case "cont":
//                filtroBuscador = " where (s.prsnnmbr ILIKE ('%${params.criterio}%') or s.prsnapll ILIKE ('%${params.criterio}%')) "
//                break;



        }

        params.criterio = params.old
        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())

//        println(sql)
//        println(res)


        return [obras: obras, res: res, params:params,]





    }

    def reporteAseguradoras () {


//        println("params reporte asg:" + params)

        def obras = []

        def sql
        def cn
        def res

        def sqlBase =  "SELECT\n" +
                "  a.asgr__id    id,\n" +
                "  a.asgrfaxx    fax, \n" +
                "  a.asgrtelf    telefono,\n" +
                "  a.asgrnmbr    nombre,\n" +
                "  a.asgrdire    direccion,\n" +
                "  a.asgrrspn    contacto,\n" +
                "  a.asgrobsr    observaciones,\n" +
                "  a.asgrfeccn    fecha,\n" +
                "  t.tpasdscr    tipoaseguradora\n" +
                "FROM asgr a\n" +
                "  LEFT JOIN tpas t ON a.tpas__id = t.tpas__id\n"

        def filtroBuscador = ""
        def buscador=""

        params.criterio = params.criterio.trim();

        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]
        def prmsHeaderHoja3 = [border: Color.WHITE, colspan: 5]
        def prmsHeaderHoja4 = [border: Color.WHITE, colspan: 3]
        def prmsHeader = [border: Color.WHITE, colspan: 7, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsHeader2 = [border: Color.WHITE, colspan: 3, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE, bg: Color.WHITE,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead2 = [border: Color.WHITE,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, bordeTop: "1", bordeBot: "1"]
        def prmsCellHead3 = [border: Color.WHITE,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1", bordeBot: "1"]
        def prmsCellHeadRight = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenter = [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenterLeft = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_LEFT]
        def prmsCellRight = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT]
        def prmsCellRight2 = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1", bordeBot: "1"]
        def prmsCellRightTop = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1"]
        def prmsCellRightBot = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeBot: "1"]
        def prmsCellLeft = [border: Color.WHITE, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.WHITE, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum,
                prmsHeaderHoja2: prmsHeaderHoja2, prmsCellRight: prmsCellRight, prmsCellHeadRight: prmsCellHeadRight, prmsCellHead2: prmsCellHead2,
                prmsCellRight2: prmsCellRight2, prmsCellRightTop: prmsCellRightTop, prmsCellRightBot: prmsCellRightBot]

        def baos = new ByteArrayOutputStream()
        def name = "contratadas_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times10normal = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        def fonts = [times12bold: times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal, times18bold: times18bold]

        Document document
        document = new Document(PageSize.A4.rotate());
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();

//        document.setMargins(2,2,2,2)
        document.addTitle("ObrasContratadas" + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("documentosObra, janus, presupuesto");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");



        Paragraph headers = new Paragraph();
        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);
        headers.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", times18bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("REPORTE DE ASEGURADORAS", times12bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("Quito, " + printFecha(new Date()).toUpperCase(), times12bold));
        addEmptyLine(headers, 1);
        document.add(headers);

//        PdfPTable tablaRegistradas = new PdfPTable(8);
//        tablaRegistradas.setWidthPercentage(100);
//        tablaRegistradas.setWidths(arregloEnteros([14, 30, 15, 8, 25, 10, 20, 10]))
//
//        addCellTabla(tablaRegistradas, new Paragraph("Código", times8bold), prmsCellHead2)
//        addCellTabla(tablaRegistradas, new Paragraph("Nombre", times8bold), prmsCellHead2)
//        addCellTabla(tablaRegistradas, new Paragraph("Tipo", times8bold), prmsCellHead2)
//        addCellTabla(tablaRegistradas, new Paragraph("Fecha Reg.", times8bold), prmsCellHead2)
//        addCellTabla(tablaRegistradas, new Paragraph("Cantón-Parroquia-Comunidad", times8bold), prmsCellHead2)
//        addCellTabla(tablaRegistradas, new Paragraph("Valor", times8bold), prmsCellHead2)
//        addCellTabla(tablaRegistradas, new Paragraph("Elaborado", times8bold), prmsCellHead2)
//        addCellTabla(tablaRegistradas, new Paragraph("Contrato", times8bold), prmsCellHead2)



        switch (params.buscador) {
            case "nmbr":
            case "telf":
            case "faxx":
            case "rspn":
            case "dire":
                buscador = "asgr"+params.buscador
                filtroBuscador =" where ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "tipo":
                filtroBuscador = " where t.tpasdscr ILIKE ('%${params.criterio}%') "
                break;


        }


        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())

        PdfPTable tablaRegistradas = new PdfPTable(3);
        tablaRegistradas.setWidthPercentage(100);
        tablaRegistradas.setWidths(arregloEnteros([5, 2, 70]))


        res.each {

            addCellTabla(tablaRegistradas, new Paragraph("Tipo", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.tipoaseguradora, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Nombre", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.nombre, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Dirección", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.direccion, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Teléfono", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.telefono, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Fax", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.fax, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Contacto", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.contacto, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("_____________________________________________________________________________________________________________", times8normal), prmsHeaderHoja4)

        }



        document.add(tablaRegistradas);
        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)

    }


    def reporteExcelAseguradoras() {


//        println(params)

        def obras = []

        def sql
        def cn
        def res

        def sqlBase =  "SELECT\n" +
                "  a.asgr__id    id,\n" +
                "  a.asgrfaxx    fax, \n" +
                "  a.asgrtelf    telefono,\n" +
                "  a.asgrnmbr    nombre,\n" +
                "  a.asgrdire    direccion,\n" +
                "  a.asgrrspn    contacto,\n" +
                "  a.asgrobsr    observaciones,\n" +
                "  a.asgrfeccn    fecha,\n" +
                "  t.tpasdscr    tipoaseguradora\n" +
                "FROM asgr a\n" +
                "  LEFT JOIN tpas t ON a.tpas__id = t.tpas__id\n"

        def filtroBuscador = ""
        def buscador=""

        params.criterio = params.criterio.trim();

        switch (params.buscador) {
            case "nmbr":
            case "telf":
            case "faxx":
            case "rspn":
            case "dire":
                buscador = "asgr"+params.buscador
                filtroBuscador =" where ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "tipo":
                filtroBuscador = " where t.tpasdscr ILIKE ('%${params.criterio}%') "
                break;


        }


        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())



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
        // fija el ancho de la columna
        // sheet.setColumnView(1,40)

        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, false);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        sheet.setColumnView(0, 12)
        sheet.setColumnView(1, 60)
        sheet.setColumnView(2, 25)
        sheet.setColumnView(3, 25)
        sheet.setColumnView(4, 40)
        sheet.setColumnView(5, 25)
        sheet.setColumnView(8, 20)
        // inicia textos y numeros para asocias a columnas

        def label
        def nmro
        def number

        def fila = 6;


        NumberFormat nf = new NumberFormat("#.##");
        WritableCellFormat cf2obj = new WritableCellFormat(nf);

        label = new Label(1, 1, "G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(1, 2, "REPORTE EXCEL ASEGURADORAS", times16format); sheet.addCell(label);



        label = new Label(0, 4, "Tipo: ", times16format); sheet.addCell(label);
        label = new Label(1, 4, "Nombre", times16format); sheet.addCell(label);
        label = new Label(2, 4, "Dirección", times16format); sheet.addCell(label);
        label = new Label(3, 4, "Teléfono", times16format); sheet.addCell(label);
        label = new Label(4, 4, "Fax", times16format); sheet.addCell(label);
        label = new Label(5, 4, "Contacto", times16format); sheet.addCell(label);


        res.eachWithIndex {i, j->


            label = new Label(0, fila, i.tipoaseguradora.toString()); sheet.addCell(label);
            label = new Label(1, fila, i?.nombre.toString()); sheet.addCell(label);
            label = new Label(2, fila, i?.direccion?.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.telefono?.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.fax?.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.contacto?.toString()); sheet.addCell(label);

            fila++

        }


        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "DocumentosObraExcel.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());
    }


    def contratistas () {



    }

    def tablaContratistas (){




        def sql
        def cn
        def res

        params.old = params.criterio

        params.criterio=cleanCriterio(params.criterio)

        def sqlBase =  "SELECT\n" +
                "  p.prve__id    id,\n" +
                "  p.prve_ruc    ruc, \n" +
                "  p.prvesgla    sigla,\n" +
                "  p.prvettlr    titulo,\n" +
                "  p.prvenmbr    nombre,\n" +
                "  e.espcdscr    especialidad,\n" +
                "  p.prvecmra    camara,\n" +
                "  p.prvedire    direccion,\n" +
                "  p.prvetelf    telefono,\n" +
                "  p.prvefaxx    fax,\n" +
                "  p.prvegrnt    garante,\n" +
                "  p.prvenbct    nombrecon,\n" +
                "  p.prveapct    apellidocon,\n" +
                "  p.prvefccn    fecha\n" +
                "FROM prve p\n" +
                "  LEFT JOIN espc e ON p.espc__id = e.espc__id\n"

        def filtroBuscador = ""
        def buscador=""



        switch (params.buscador) {
            case "cdgo":
            case "nmbr":
            case "_ruc":
                buscador = "prve"+params.buscador
                filtroBuscador =" where ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "espe":
                filtroBuscador = " where e.espcdscr ILIKE ('%${params.criterio}%') "
                break;


        }

        params.criterio = params.old
        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())

//        println(sql)
//        println(res)



        return [res: res, params:params]

    }

    def reporteContratistas () {

        def sql
        def cn
        def res


        def sqlBase =  "SELECT\n" +
                "  p.prve__id    id,\n" +
                "  p.prve_ruc    ruc, \n" +
                "  p.prvesgla    sigla,\n" +
                "  p.prvettlr    titulo,\n" +
                "  p.prvenmbr    nombre,\n" +
                "  e.espcdscr    especialidad,\n" +
                "  p.prvecmra    camara,\n" +
                "  p.prvedire    direccion,\n" +
                "  p.prvetelf    telefono,\n" +
                "  p.prvefaxx    fax,\n" +
                "  p.prvegrnt    garante,\n" +
                "  p.prvenbct    nombrecon,\n" +
                "  p.prveapct    apellidocon,\n" +
                "  p.prvefccn    fecha\n" +
                "FROM prve p\n" +
                "  LEFT JOIN espc e ON p.espc__id = e.espc__id\n"

        def filtroBuscador = ""
        def buscador=""


        params.criterio = params.criterio.trim();

        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]
        def prmsHeaderHoja3 = [border: Color.WHITE, colspan: 5]
        def prmsHeaderHoja4 = [border: Color.WHITE, colspan: 3]
        def prmsHeader = [border: Color.WHITE, colspan: 7, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsHeader2 = [border: Color.WHITE, colspan: 3, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE, bg: Color.WHITE,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead2 = [border: Color.WHITE,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, bordeTop: "1", bordeBot: "1"]
        def prmsCellHead3 = [border: Color.WHITE,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1", bordeBot: "1"]
        def prmsCellHeadRight = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenter = [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenterLeft = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_LEFT]
        def prmsCellRight = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT]
        def prmsCellRight2 = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1", bordeBot: "1"]
        def prmsCellRightTop = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1"]
        def prmsCellRightBot = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeBot: "1"]
        def prmsCellLeft = [border: Color.WHITE, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.WHITE, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum,
                prmsHeaderHoja2: prmsHeaderHoja2, prmsCellRight: prmsCellRight, prmsCellHeadRight: prmsCellHeadRight, prmsCellHead2: prmsCellHead2,
                prmsCellRight2: prmsCellRight2, prmsCellRightTop: prmsCellRightTop, prmsCellRightBot: prmsCellRightBot]

        def baos = new ByteArrayOutputStream()
        def name = "contratistas_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times10normal = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        def fonts = [times12bold: times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal, times18bold: times18bold]

        Document document
        document = new Document(PageSize.A4.rotate());
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();

//        document.setMargins(2,2,2,2)
        document.addTitle("Contratistas" + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("documentosObra, janus, presupuesto");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");



        Paragraph headers = new Paragraph();
        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);
        headers.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", times18bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("REPORTE DE CONTRATISTAS", times12bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("Quito, " + printFecha(new Date()).toUpperCase(), times12bold));
        addEmptyLine(headers, 1);
        document.add(headers);


        switch (params.buscador) {
            case "cdgo":
            case "nmbr":
            case "_ruc":
                buscador = "prve"+params.buscador
                filtroBuscador =" where ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "espe":
                filtroBuscador = " where e.espcdscr ILIKE ('%${params.criterio}%') "
                break;


        }


        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())


        PdfPTable tablaRegistradas = new PdfPTable(3);
        tablaRegistradas.setWidthPercentage(100);
        tablaRegistradas.setWidths(arregloEnteros([10, 2, 70]))


        res.each {

            addCellTabla(tablaRegistradas, new Paragraph("Nombre", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.nombre, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Cédula/RUC", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.ruc, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Especialidad", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.especialidad, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Cámara", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.camara, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Contacto", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.nombrecon + " " + it?.apellidocon, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Dirección", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.direccion, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Teléfono", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.telefono, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("_____________________________________________________________________________________________________________", times8normal), prmsHeaderHoja4)

        }



        document.add(tablaRegistradas);
        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)

    }


    def reporteExcelContratistas() {

        def sql
        def cn
        def res


        def sqlBase =  "SELECT\n" +
                "  p.prve__id    id,\n" +
                "  p.prve_ruc    ruc, \n" +
                "  p.prvesgla    sigla,\n" +
                "  p.prvettlr    titulo,\n" +
                "  p.prvenmbr    nombre,\n" +
                "  e.espcdscr    especialidad,\n" +
                "  p.prvecmra    camara,\n" +
                "  p.prvedire    direccion,\n" +
                "  p.prvetelf    telefono,\n" +
                "  p.prvefaxx    fax,\n" +
                "  p.prvegrnt    garante,\n" +
                "  p.prvenbct    nombrecon,\n" +
                "  p.prveapct    apellidocon,\n" +
                "  p.prvefccn    fecha\n" +
                "FROM prve p\n" +
                "  LEFT JOIN espc e ON p.espc__id = e.espc__id\n"

        def filtroBuscador = ""
        def buscador=""


        params.criterio = params.criterio.trim();


        switch (params.buscador) {
            case "cdgo":
            case "nmbr":
            case "_ruc":
                buscador = "prve"+params.buscador
                filtroBuscador =" where ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "espe":
                filtroBuscador = " where e.espcdscr ILIKE ('%${params.criterio}%') "
                break;


        }


        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())


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
        // fija el ancho de la columna
        // sheet.setColumnView(1,40)

        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, false);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        sheet.setColumnView(0, 12)
        sheet.setColumnView(1, 60)
        sheet.setColumnView(2, 25)
        sheet.setColumnView(3, 25)
        sheet.setColumnView(4, 40)
        sheet.setColumnView(5, 25)
        sheet.setColumnView(8, 20)
        // inicia textos y numeros para asocias a columnas

        def label
        def nmro
        def number

        def fila = 6;


        NumberFormat nf = new NumberFormat("#.##");
        WritableCellFormat cf2obj = new WritableCellFormat(nf);

        label = new Label(1, 1, "G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(1, 2, "REPORTE EXCEL CONTRATISTAS", times16format); sheet.addCell(label);



        label = new Label(0, 4, "Nombre: ", times16format); sheet.addCell(label);
        label = new Label(1, 4, "Cédula/RUC", times16format); sheet.addCell(label);
        label = new Label(2, 4, "Especialidad", times16format); sheet.addCell(label);
        label = new Label(3, 4, "Cámara", times16format); sheet.addCell(label);
        label = new Label(4, 4, "Contacto", times16format); sheet.addCell(label);
        label = new Label(5, 4, "Dirección", times16format); sheet.addCell(label);
        label = new Label(6, 4, "Teléfono", times16format); sheet.addCell(label);


        res.eachWithIndex {i, j->


            label = new Label(0, fila, i.nombre.toString()); sheet.addCell(label);
            label = new Label(1, fila, i?.ruc.toString()); sheet.addCell(label);
            label = new Label(2, fila, i?.especialidad?.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.camara?.toString()); sheet.addCell(label);
            label = new Label(4, fila, i?.nombrecon?.toString() + " " + i?.apellidocon?.toString()); sheet.addCell(label);
            label = new Label(5, fila, i?.direccion?.toString()); sheet.addCell(label);
            label = new Label(6, fila, i?.telefono?.toString()); sheet.addCell(label);

            fila++

        }


        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "ContratistasExcel.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());
    }


    def contratos (){



    }

    def tablaContratos () {

        println(params)

        def sql
        def res
        def cn

        params.old = params.criterio

        params.criterio=cleanCriterio(params.criterio)


        def sqlBase =  "SELECT\n" +
                "  c.cntr__id    id,\n" +
                "  c.cntrcdgo    codigo, \n" +
                "  c.cntrmemo    memo,\n" +
                "  c.cntrfcsb    fechasu,\n" +
                "  r.cncrcdgo    concurso,\n" +
                "  o.obracdgo    obracodigo,\n" +
                "  o.obranmbr    obranombre,\n" +
                "  n.cntnnmbr    canton,\n" +
                "  p.parrnmbr    parroquia,\n" +
                "  t.tpobdscr    tipoobra,\n" +
                "  e.tpcrdscr    tipocontrato,\n" +
                "  c.cntrmnto    monto,\n" +
                "  c.cntrpcan    porcentaje,\n" +
                "  c.cntrantc    anticipo,\n" +
                "  g.prvenmbr    nombrecontra,\n" +
                "  b.prinfcin    fechainicio,\n" +
                "  b.prinfcfn    fechafin,\n" +
                "  z.tppzdscr    plazo\n" +
                "FROM cntr c\n" +
                "  LEFT JOIN ofrt f ON c.ofrt__id = f.ofrt__id\n" +
                "  LEFT JOIN cncr r ON f.cncr__id = r.cncr__id\n" +
                "  LEFT JOIN obra o ON r.obra__id = o.obra__id\n" +
                "  LEFT JOIN cmnd d ON o.cmnd__id = d.cmnd__id\n" +
                "  LEFT JOIN parr p ON o.parr__id = p.parr__id\n" +
                "  LEFT JOIN cntn n ON p.cntn__id = n.cntn__id\n" +
                "  LEFT JOIN tpcr e ON c.tpcr__id = e.tpcr__id\n" +
                "  LEFT JOIN tpob t ON o.tpob__id = t.tpob__id\n" +
                "  LEFT JOIN prin b ON c.prin__id = b.prin__id\n" +
                "  LEFT JOIN tppz z ON c.tppz__id = z.tppz__id\n" +
                "  LEFT JOIN prve g ON f.prve__id = g.prve__id\n"

        def filtroBuscador = ""

        def buscador = ""

        switch (params.buscador) {
            case "cdgo":
            case "memo":
//            case "fcsb":
            case "ofsl":
            case "mnto":
                buscador = "cntr"+params.buscador
                filtroBuscador =" where ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "cncr":
                filtroBuscador = " where r.cncrcdgo ILIKE ('%${params.criterio}%') "
                break;
            case "parr":
                filtroBuscador = " where p.parrnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "cntn":
                filtroBuscador = " where n.cntnnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "obra":
                filtroBuscador = " where o.obracdgo ILIKE ('%${params.criterio}%') "
                break;
            case "clas":
                filtroBuscador = " where t.tpodscr ILIKE ('%${params.criterio}%') "
                break;
            case "nmbr":
                filtroBuscador = " where o.obranmbr ILIKE ('%${params.criterio}%') "
                break;
            case "tipo":
                filtroBuscador = " where e.tpcrdscr ILIKE ('%${params.criterio}%') "
                break;
            case "cont":
                filtroBuscador = " where g.prvenmbr ILIKE ('%${params.criterio}%') "
                break;
            case "tppz":
                filtroBuscador = " where z.tppzdscr ILIKE ('%${params.criterio}%') "
                break;
            case "inic":
            case "fin":
            case "fcsb":
                break;

        }


        params.criterio = params.old

        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())


//        println(sql)

        return [res: res, params:params]

    }

    def reporteContratos () {

        def sql
        def cn
        def res


        def sqlBase =  "SELECT\n" +
                "  c.cntr__id    id,\n" +
                "  c.cntrcdgo    codigo, \n" +
                "  c.cntrmemo    memo,\n" +
                "  c.cntrfcsb    fechasu,\n" +
                "  r.cncrcdgo    concurso,\n" +
                "  o.obracdgo    obracodigo,\n" +
                "  o.obranmbr    obranombre,\n" +
                "  n.cntnnmbr    canton,\n" +
                "  p.parrnmbr    parroquia,\n" +
                "  t.tpobdscr    tipoobra,\n" +
                "  e.tpcrdscr    tipocontrato,\n" +
                "  c.cntrmnto    monto,\n" +
                "  c.cntrpcan    porcentaje,\n" +
                "  c.cntrantc    anticipo,\n" +
                "  g.prvenmbr    nombrecontra,\n" +
                "  b.prinfcin    fechainicio,\n" +
                "  b.prinfcfn    fechafin,\n" +
                "  z.tppzdscr    plazo\n" +
                "FROM cntr c\n" +
                "  LEFT JOIN ofrt f ON c.ofrt__id = f.ofrt__id\n" +
                "  LEFT JOIN cncr r ON f.cncr__id = r.cncr__id\n" +
                "  LEFT JOIN obra o ON r.obra__id = o.obra__id\n" +
                "  LEFT JOIN cmnd d ON o.cmnd__id = d.cmnd__id\n" +
                "  LEFT JOIN parr p ON o.parr__id = p.parr__id\n" +
                "  LEFT JOIN cntn n ON p.cntn__id = n.cntn__id\n" +
                "  LEFT JOIN tpcr e ON c.tpcr__id = e.tpcr__id\n" +
                "  LEFT JOIN tpob t ON o.tpob__id = t.tpob__id\n" +
                "  LEFT JOIN prin b ON c.prin__id = b.prin__id\n" +
                "  LEFT JOIN tppz z ON c.tppz__id = z.tppz__id\n" +
                "  LEFT JOIN prve g ON f.prve__id = g.prve__id\n"

        def filtroBuscador = ""

        def buscador = ""

        params.criterio = params.criterio.trim();

        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]
        def prmsHeaderHoja3 = [border: Color.WHITE, colspan: 5]
        def prmsHeader = [border: Color.WHITE, colspan: 7, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsHeader2 = [border: Color.WHITE, colspan: 3, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE, bg: Color.WHITE,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead2 = [border: Color.WHITE,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, bordeTop: "1", bordeBot: "1"]
        def prmsCellHead3 = [border: Color.WHITE,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1", bordeBot: "1"]
        def prmsCellHeadRight = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenter = [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenterLeft = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_LEFT]
        def prmsCellRight = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT]
        def prmsCellRight2 = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1", bordeBot: "1"]
        def prmsCellRightTop = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1"]
        def prmsCellRightBot = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeBot: "1"]
        def prmsCellLeft = [border: Color.WHITE, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.WHITE, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum,
                prmsHeaderHoja2: prmsHeaderHoja2, prmsCellRight: prmsCellRight, prmsCellHeadRight: prmsCellHeadRight, prmsCellHead2: prmsCellHead2,
                prmsCellRight2: prmsCellRight2, prmsCellRightTop: prmsCellRightTop, prmsCellRightBot: prmsCellRightBot]

        def baos = new ByteArrayOutputStream()
        def name = "contratos_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        def fonts = [times12bold: times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal, times18bold: times18bold]

        Document document
        document = new Document(PageSize.A4.rotate());
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();

//        document.setMargins(2,2,2,2)
        document.addTitle("Contratos" + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("documentosObra, janus, presupuesto");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");



        Paragraph headers = new Paragraph();
        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);
        headers.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", times18bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("REPORTE DE CONTRATOS", times12bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("Quito, " + printFecha(new Date()).toUpperCase(), times12bold));
        addEmptyLine(headers, 1);
        document.add(headers);

        PdfPTable tablaRegistradas = new PdfPTable(10);
        tablaRegistradas.setWidthPercentage(100);
        tablaRegistradas.setWidths(arregloEnteros([14, 14, 15, 30, 25, 10, 20, 10, 10, 10]))

        addCellTabla(tablaRegistradas, new Paragraph("N° Contrato", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Fecha Suscripcion", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Memo", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Obra", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Cantón - Parroquia - Comunidad", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Plazo", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Contratista", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Monto", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("% Anticipo", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Anticipo", times8bold), prmsCellHead2)


        switch (params.buscador) {
            case "cdgo":
            case "memo":
//            case "fcsb":
            case "ofsl":
            case "mnto":
                buscador = "cntr"+params.buscador
                filtroBuscador =" where ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "cncr":
                filtroBuscador = " where r.cncrcdgo ILIKE ('%${params.criterio}%') "
                break;
            case "parr":
                filtroBuscador = " where p.parrnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "cntn":
                filtroBuscador = " where n.cntnnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "obra":
                filtroBuscador = " where o.obracdgo ILIKE ('%${params.criterio}%') "
                break;
            case "clas":
                filtroBuscador = " where t.tpodscr ILIKE ('%${params.criterio}%') "
                break;
            case "nmbr":
                filtroBuscador = " where o.obranmbr ILIKE ('%${params.criterio}%') "
                break;
            case "tipo":
                filtroBuscador = " where e.tpcrdscr ILIKE ('%${params.criterio}%') "
                break;
            case "cont":
                filtroBuscador = " where g.prvenmbr ILIKE ('%${params.criterio}%') "
                break;
            case "tppz":
                filtroBuscador = " where z.tppzdscr ILIKE ('%${params.criterio}%') "
                break;
            case "inic":
            case "fin":
            case "fcsb":
                break;

        }

        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())

        res.eachWithIndex {i,j->

            addCellTabla(tablaRegistradas, new Paragraph(i.codigo, times8normal), prmsCellLeft)
//            addCellTabla(tablaRegistradas, new Paragraph(g.formatDate(date: i.fechasu, format: "dd-MM-yyyy"), times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph("", times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.memo, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.obranombre, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.canton + "-" + i.parroquia, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.plazo, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.nombrecontra, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: i.monto, minFractionDigits:
                    5, maxFractionDigits: 5, format: "##,##0", locale: "ec"), times8normal), prmsCellRight)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: i.porcentaje, minFractionDigits:
                    0, maxFractionDigits: 0, format: "##,##0", locale: "ec"), times8normal), prmsCellRight)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: i.anticipo, minFractionDigits:
                    5, maxFractionDigits: 5, format: "##,##0", locale: "ec"), times8normal), prmsCellRight)

        }


        document.add(tablaRegistradas);
        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)

    }

    def reporteExcelContratos () {


        def sql
        def cn
        def res


        def sqlBase =  "SELECT\n" +
                "  c.cntr__id    id,\n" +
                "  c.cntrcdgo    codigo, \n" +
                "  c.cntrmemo    memo,\n" +
                "  c.cntrfcsb    fechasu,\n" +
                "  r.cncrcdgo    concurso,\n" +
                "  o.obracdgo    obracodigo,\n" +
                "  o.obranmbr    obranombre,\n" +
                "  n.cntnnmbr    canton,\n" +
                "  p.parrnmbr    parroquia,\n" +
                "  t.tpobdscr    tipoobra,\n" +
                "  e.tpcrdscr    tipocontrato,\n" +
                "  c.cntrmnto    monto,\n" +
                "  c.cntrpcan    porcentaje,\n" +
                "  c.cntrantc    anticipo,\n" +
                "  g.prvenmbr    nombrecontra,\n" +
                "  b.prinfcin    fechainicio,\n" +
                "  b.prinfcfn    fechafin,\n" +
                "  z.tppzdscr    plazo\n" +
                "FROM cntr c\n" +
                "  LEFT JOIN ofrt f ON c.ofrt__id = f.ofrt__id\n" +
                "  LEFT JOIN cncr r ON f.cncr__id = r.cncr__id\n" +
                "  LEFT JOIN obra o ON r.obra__id = o.obra__id\n" +
                "  LEFT JOIN cmnd d ON o.cmnd__id = d.cmnd__id\n" +
                "  LEFT JOIN parr p ON o.parr__id = p.parr__id\n" +
                "  LEFT JOIN cntn n ON p.cntn__id = n.cntn__id\n" +
                "  LEFT JOIN tpcr e ON c.tpcr__id = e.tpcr__id\n" +
                "  LEFT JOIN tpob t ON o.tpob__id = t.tpob__id\n" +
                "  LEFT JOIN prin b ON c.prin__id = b.prin__id\n" +
                "  LEFT JOIN tppz z ON c.tppz__id = z.tppz__id\n" +
                "  LEFT JOIN prve g ON f.prve__id = g.prve__id\n"

        def filtroBuscador = ""

        def buscador = ""

        params.criterio = params.criterio.trim();





        switch (params.buscador) {
            case "cdgo":
            case "memo":
//            case "fcsb":
            case "ofsl":
            case "mnto":
                buscador = "cntr"+params.buscador
                filtroBuscador =" where ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "cncr":
                filtroBuscador = " where r.cncrcdgo ILIKE ('%${params.criterio}%') "
                break;
            case "parr":
                filtroBuscador = " where p.parrnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "cntn":
                filtroBuscador = " where n.cntnnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "obra":
                filtroBuscador = " where o.obracdgo ILIKE ('%${params.criterio}%') "
                break;
            case "clas":
                filtroBuscador = " where t.tpodscr ILIKE ('%${params.criterio}%') "
                break;
            case "nmbr":
                filtroBuscador = " where o.obranmbr ILIKE ('%${params.criterio}%') "
                break;
            case "tipo":
                filtroBuscador = " where e.tpcrdscr ILIKE ('%${params.criterio}%') "
                break;
            case "cont":
                filtroBuscador = " where g.prvenmbr ILIKE ('%${params.criterio}%') "
                break;
            case "tppz":
                filtroBuscador = " where z.tppzdscr ILIKE ('%${params.criterio}%') "
                break;
            case "inic":
            case "fin":
            case "fcsb":
                break;

        }

        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())



        WorkbookSettings workbookSettings = new WorkbookSettings()
        workbookSettings.locale = Locale.default

        def file = File.createTempFile('myExcelDocument', '.xls')
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
        sheet.setColumnView(2, 25)
        sheet.setColumnView(3, 25)
        sheet.setColumnView(4, 40)
        sheet.setColumnView(5, 25)
        sheet.setColumnView(8, 20)
        // inicia textos y numeros para asocias a columnas

        def label
        def nmro
        def number

        def fila = 6;


        NumberFormat nf = new NumberFormat("#.##");
        WritableCellFormat cf2obj = new WritableCellFormat(nf);

        label = new Label(1, 1, "G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(1, 2, "REPORTE EXCEL CONTRATOS", times16format); sheet.addCell(label);



        label = new Label(0, 4, "N° Contrato: ", times16format); sheet.addCell(label);
        label = new Label(1, 4, "Fecha Suscripción", times16format); sheet.addCell(label);
        label = new Label(2, 4, "Memo", times16format); sheet.addCell(label);
        label = new Label(3, 4, "Obra", times16format); sheet.addCell(label);
        label = new Label(4, 4, "Cantón-Parroquia-Comunidad", times16format); sheet.addCell(label);
        label = new Label(5, 4, "Plazo", times16format); sheet.addCell(label);
        label = new Label(6, 4, "Contratista", times16format); sheet.addCell(label);
        label = new Label(7, 4, "Monto", times16format); sheet.addCell(label);
        label = new Label(8, 4, "% Anticipo", times16format); sheet.addCell(label);
        label = new Label(9, 4, "Anticipo", times16format); sheet.addCell(label);


        res.eachWithIndex {i, j->


            label = new Label(0, fila, i?.codigo.toString()); sheet.addCell(label);
            label = new Label(1, fila, ""); sheet.addCell(label);
            label = new Label(2, fila, i.memo.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.obranombre?.toString()); sheet.addCell(label);
            label = new Label(4, fila, i?.canton?.toString() + " " + i?.parroquia?.toString() + " " + i?.comunidad?.toString()); sheet.addCell(label);
            label = new Label(5, fila, i?.plazo?.toString()); sheet.addCell(label);
            label = new Label(6, fila, i?.nombrcontra?.toString()); sheet.addCell(label);
            number = new jxl.write.Number(7, fila, i.monto); sheet.addCell(number);
            number = new jxl.write.Number(8, fila, i.porcentaje); sheet.addCell(number);
            number = new jxl.write.Number(9, fila, i.anticipo); sheet.addCell(number);


            fila++

        }


        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "ContratosExcel.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());
    }

    def garantias () {

    }

    private String cleanCriterio(String criterio) {
        if(!criterio) {
            criterio = ""
        }
//        println "entra: "+criterio
        criterio = criterio.toLowerCase()
        criterio = criterio.replaceAll(";","")
        criterio = criterio.replaceAll(":","")
        criterio = criterio.replaceAll("select","")
        criterio = criterio.replaceAll("\\*","")
        criterio = criterio.replaceAll("#","")
        criterio = criterio.replaceAll("%","")
        criterio = criterio.replaceAll("/","")
        criterio = criterio.replaceAll("drop","")
        criterio = criterio.replaceAll("table","")
        criterio = criterio.replaceAll("from","")
        criterio = criterio.replaceAll("'","")
        criterio = criterio.replaceAll('"',"")
        criterio = criterio.replaceAll("\\\\","")
        criterio=criterio.trim()
//        println "sale: "+criterio
        return criterio
    }


    def tablaGarantias () {
//        println(params)
        def sql
        def res
        def cn

        params.old = params.criterio

        params.criterio=cleanCriterio(params.criterio)

        def sqlBase =  "SELECT\n" +
                "  g.grnt__id    id,\n" +
                "  g.grntcdgo    codigo, \n" +
                "  g.grntnmrv    renovacion,\n" +
                "  c.cntrcdgo    codigocontrato,\n" +
                "  t.tpgrdscr    tipogarantia,\n" +
                "  q.tdgrdscr    documento,\n" +
                "  a.asgrnmbr    aseguradora,\n" +
                "  s.prvenmbr    contratista,\n" +
                "  g.grntetdo    estado,\n" +
                "  g.grntmnto    monto,\n" +
                "  m.mndacdgo    moneda,\n" +
                "  g.grntfcin    emision,\n" +
                "  g.grntfcfn    vencimiento,\n" +
                "  g.grntdias    dias\n" +
                "FROM grnt g\n" +
                "  LEFT JOIN cntr c ON g.cntr__id = c.cntr__id\n" +
                "  LEFT JOIN tpgr t ON g.tpgr__id = t.tpgr__id\n" +
                "  LEFT JOIN tdgr q ON g.tdgr__id = q.tdgr__id\n" +
                "  LEFT JOIN asgr a ON g.asgr__id = a.asgr__id\n" +
                "  LEFT JOIN prve s ON c.prve__id = s.prve__id\n" +
                "  LEFT JOIN mnda m ON g.mnda__id = m.mnda__id\n"


        def filtroBuscador = ""

        def buscador = ""

        switch (params.buscador) {
            case "cdgo":
            case "nmrv":
            case "etdo":
//            case "mnto":
            case "dias":
                buscador = "grnt"+params.buscador
                filtroBuscador =" where ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "contrato":
                filtroBuscador = " where c.cntrcdgo ILIKE ('%${params.criterio}%') "
                break;
            case "tpgr":
                filtroBuscador = " where t.tpgrdscr ILIKE ('%${params.criterio}%') "
                break;
            case "tdgr":
                filtroBuscador = " where q.tdgrdscr ILIKE ('%${params.criterio}%') "
                break;
            case "aseguradora":
                filtroBuscador = " where a.asgrnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "cont":
                filtroBuscador = " where s.prvenmbr ILIKE ('%${params.criterio}%') "
                break;
            case "mnda":
                filtroBuscador = " where m.mndacdgo ILIKE ('%${params.criterio}%') "
                break;
            case "mnto":
                if(!params.criterio){
                    params.criterio=-5
                }
                try {
                    filtroBuscador = " where g.grntmnto=${params.criterio.trim().toDouble()} "
                } catch(e) {
                    println "error: "+params.criterio
                    println e
                    params.criterio=-5
                    filtroBuscador = " where g.grntmnto=-5"
                }
                break;
            case "fcin":
            case "fcfn":
                break;

        }

       params.criterio = params.old


        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())


//        println(sql)

        return [res: res, params:params]


    }

    def reporteGarantias () {

        def sql
        def res
        def cn


        def sqlBase =  "SELECT\n" +
                "  g.grnt__id    id,\n" +
                "  g.grntcdgo    codigo, \n" +
                "  g.grntnmrv    renovacion,\n" +
                "  g.grntpdre    padre,\n" +
                "  c.cntrcdgo    codigocontrato,\n" +
                "  t.tpgrdscr    tipogarantia,\n" +
                "  q.tdgrdscr    documento,\n" +
                "  a.asgrnmbr    aseguradora,\n" +
                "  s.prvenmbr    contratista,\n" +
                "  g.grntetdo    estado,\n" +
                "  g.grntmnto    monto,\n" +
                "  m.mndacdgo    moneda,\n" +
                "  g.grntfcin    emision,\n" +
                "  g.grntfcfn    vencimiento,\n" +
                "  g.grntdias    dias\n" +
                "FROM grnt g\n" +
                "  LEFT JOIN cntr c ON g.cntr__id = c.cntr__id\n" +
                "  LEFT JOIN tpgr t ON g.tpgr__id = t.tpgr__id\n" +
                "  LEFT JOIN tdgr q ON g.tdgr__id = q.tdgr__id\n" +
                "  LEFT JOIN asgr a ON g.asgr__id = a.asgr__id\n" +
                "  LEFT JOIN prve s ON c.prve__id = s.prve__id\n" +
                "  LEFT JOIN mnda m ON g.mnda__id = m.mnda__id\n"


        def filtroBuscador = ""

        def buscador = ""

        params.criterio = params.criterio.trim();

        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]
        def prmsHeaderHoja3 = [border: Color.WHITE, colspan: 5]
        def prmsHeader = [border: Color.WHITE, colspan: 7, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsHeader2 = [border: Color.WHITE, colspan: 3, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE, bg: Color.WHITE,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead2 = [border: Color.WHITE,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, bordeTop: "1", bordeBot: "1"]
        def prmsCellHead3 = [border: Color.WHITE,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1", bordeBot: "1"]
        def prmsCellHeadRight = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenter = [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenterLeft = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_LEFT]
        def prmsCellRight = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT]
        def prmsCellRight3 = [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_CENTER]
        def prmsCellRight2 = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1", bordeBot: "1"]
        def prmsCellRightTop = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeTop: "1"]
        def prmsCellRightBot = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT, bordeBot: "1"]
        def prmsCellLeft = [border: Color.WHITE, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.WHITE, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum,
                prmsHeaderHoja2: prmsHeaderHoja2, prmsCellRight: prmsCellRight, prmsCellHeadRight: prmsCellHeadRight, prmsCellHead2: prmsCellHead2,
                prmsCellRight2: prmsCellRight2, prmsCellRightTop: prmsCellRightTop, prmsCellRightBot: prmsCellRightBot]

        def baos = new ByteArrayOutputStream()
        def name = "contratos_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        def fonts = [times12bold: times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal, times18bold: times18bold]

        Document document
        document = new Document(PageSize.A4.rotate());
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();

//        document.setMargins(2,2,2,2)
        document.addTitle("Contratos" + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("documentosObra, janus, presupuesto");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");



        Paragraph headers = new Paragraph();
        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);
        headers.add(new Paragraph("G.A.D. PROVINCIA DE PICHINCHA", times18bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("REPORTE DE GARANTÍAS", times12bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("Quito, " + printFecha(new Date()).toUpperCase(), times12bold));
        addEmptyLine(headers, 1);
        document.add(headers);

        PdfPTable tablaRegistradas = new PdfPTable(13);
        tablaRegistradas.setWidthPercentage(100);
        tablaRegistradas.setWidths(arregloEnteros([15, 30, 25, 20, 8, 20, 30, 15, 10, 20,15,15,10]))

        addCellTabla(tablaRegistradas, new Paragraph("N° Contrato", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Contratista", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Tipo de Garantía", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("N° Garantía", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Rnov", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Original", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Aseguradora", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Documento", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Estado", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Monto", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Emisión", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Vencimiento", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Cancelación", times8bold), prmsCellHead2)

        switch (params.buscador) {
            case "cdgo":
            case "nmrv":
            case "etdo":
            case "mnto":
            case "dias":
                buscador = "grnt"+params.buscador
                filtroBuscador =" where ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "contrato":
                filtroBuscador = " where c.cntrcdgo ILIKE ('%${params.criterio}%') "
                break;
            case "tpgr":
                filtroBuscador = " where t.tpgrdscr ILIKE ('%${params.criterio}%') "
                break;
            case "tdgr":
                filtroBuscador = " where q.tdgrdscr ILIKE ('%${params.criterio}%') "
                break;
            case "aseguradora":
                filtroBuscador = " where a.asgrnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "cont":
                filtroBuscador = " where s.prvenmbr ILIKE ('%${params.criterio}%') "
                break;
            case "mnda":
                filtroBuscador = " where m.mndacdgo ILIKE ('%${params.criterio}%') "
                break;
            case "fcin":
            case "fcfn":
                break;

        }

        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())


        res.eachWithIndex {i,j->

            addCellTabla(tablaRegistradas, new Paragraph(i.codigocontrato, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.contratista, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.tipogarantia, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.codigo, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: i.renovacion, minFractionDigits:
                    0, maxFractionDigits: 0, format: "##,##0", locale: "ec"), times8normal), prmsCellRight3)
            if(i.padre){
                addCellTabla(tablaRegistradas, new Paragraph(janus.pac.Garantia.get(i.padre).codigo, times8normal), prmsCellLeft)
            }else {
                addCellTabla(tablaRegistradas, new Paragraph("", times8normal), prmsCellLeft)
            }
            addCellTabla(tablaRegistradas, new Paragraph(i.aseguradora, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.documento, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.estado, times8normal), prmsCellRight3)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: i.monto, minFractionDigits:
                    5, maxFractionDigits: 5, format: "##,##0", locale: "ec"), times8normal), prmsCellRight)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatDate(date: i.emision, format: "dd-MM-yyyy"), times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatDate(date: i.vencimiento, format: "dd-MM-yyyy"), times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: i.dias, minFractionDigits:
                    0, maxFractionDigits: 0, format: "##,##0", locale: "ec")+ " Días", times8normal), prmsCellRight)

        }





        document.add(tablaRegistradas);
        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)




    }


    def reporteExcelGarantias() {



        def sql
        def res
        def cn


        def sqlBase =  "SELECT\n" +
                "  g.grnt__id    id,\n" +
                "  g.grntcdgo    codigo, \n" +
                "  g.grntnmrv    renovacion,\n" +
                "  g.grntpdre    padre,\n" +
                "  c.cntrcdgo    codigocontrato,\n" +
                "  t.tpgrdscr    tipogarantia,\n" +
                "  q.tdgrdscr    documento,\n" +
                "  a.asgrnmbr    aseguradora,\n" +
                "  s.prvenmbr    contratista,\n" +
                "  g.grntetdo    estado,\n" +
                "  g.grntmnto    monto,\n" +
                "  m.mndacdgo    moneda,\n" +
                "  g.grntfcin    emision,\n" +
                "  g.grntfcfn    vencimiento,\n" +
                "  g.grntdias    dias\n" +
                "FROM grnt g\n" +
                "  LEFT JOIN cntr c ON g.cntr__id = c.cntr__id\n" +
                "  LEFT JOIN tpgr t ON g.tpgr__id = t.tpgr__id\n" +
                "  LEFT JOIN tdgr q ON g.tdgr__id = q.tdgr__id\n" +
                "  LEFT JOIN asgr a ON g.asgr__id = a.asgr__id\n" +
                "  LEFT JOIN prve s ON c.prve__id = s.prve__id\n" +
                "  LEFT JOIN mnda m ON g.mnda__id = m.mnda__id\n"


        def filtroBuscador = ""

        def buscador = ""

        params.criterio = params.criterio.trim();


        switch (params.buscador) {
            case "cdgo":
            case "nmrv":
            case "etdo":
            case "mnto":
            case "dias":
                buscador = "grnt"+params.buscador
                filtroBuscador =" where ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "contrato":
                filtroBuscador = " where c.cntrcdgo ILIKE ('%${params.criterio}%') "
                break;
            case "tpgr":
                filtroBuscador = " where t.tpgrdscr ILIKE ('%${params.criterio}%') "
                break;
            case "tdgr":
                filtroBuscador = " where q.tdgrdscr ILIKE ('%${params.criterio}%') "
                break;
            case "aseguradora":
                filtroBuscador = " where a.asgrnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "cont":
                filtroBuscador = " where s.prvenmbr ILIKE ('%${params.criterio}%') "
                break;
            case "mnda":
                filtroBuscador = " where m.mndacdgo ILIKE ('%${params.criterio}%') "
                break;
            case "fcin":
            case "fcfn":
                break;

        }

        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())



        WorkbookSettings workbookSettings = new WorkbookSettings()
        workbookSettings.locale = Locale.default

        def file = File.createTempFile('myExcelDocument', '.xls')
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
        sheet.setColumnView(2, 25)
        sheet.setColumnView(3, 25)
        sheet.setColumnView(4, 40)
        sheet.setColumnView(5, 25)
        sheet.setColumnView(8, 20)
        // inicia textos y numeros para asocias a columnas

        def label
        def nmro
        def number

        def fila = 6;


        NumberFormat nf = new NumberFormat("#.##");
        WritableCellFormat cf2obj = new WritableCellFormat(nf);

        label = new Label(1, 1, "G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(1, 2, "REPORTE EXCEL REGISTRADAS", times16format); sheet.addCell(label);



        label = new Label(0, 4, "N° Contrato: ", times16format); sheet.addCell(label);
        label = new Label(1, 4, "Contratista", times16format); sheet.addCell(label);
        label = new Label(2, 4, "Tipo de Garantía", times16format); sheet.addCell(label);
        label = new Label(3, 4, "N° Garantía", times16format); sheet.addCell(label);
        label = new Label(4, 4, "Rnov", times16format); sheet.addCell(label);
        label = new Label(5, 4, "Original", times16format); sheet.addCell(label);
        label = new Label(6, 4, "Aseguradora", times16format); sheet.addCell(label);
        label = new Label(7, 4, "DOcumento", times16format); sheet.addCell(label);
        label = new Label(8, 4, "Estado", times16format); sheet.addCell(label);
        label = new Label(9, 4, "Monto", times16format); sheet.addCell(label);
        label = new Label(10, 4, "Emisión", times16format); sheet.addCell(label);
        label = new Label(11, 4, "Vencimiento", times16format); sheet.addCell(label);
        label = new Label(12, 4, "Cancelación", times16format); sheet.addCell(label);


        res.eachWithIndex {i, j->


            label = new Label(0, fila, i?.codigocontrato.toString()); sheet.addCell(label);
            label = new Label(1, fila, i?.contratista); sheet.addCell(label);
            label = new Label(2, fila, i.tipogarantia.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.codigo?.toString()); sheet.addCell(label);
            label = new Label(4, fila, i?.renovacion?.toString() + " " + i?.parroquia?.toString() + " " + i?.comunidad?.toString()); sheet.addCell(label);
            label = new Label(5, fila, ""); sheet.addCell(label);
            label = new Label(6, fila, i?.aseguradora?.toString()); sheet.addCell(label);
            label = new Label(7, fila, i?.documento?.toString()); sheet.addCell(label);
            label = new Label(8, fila, i?.estado?.toString()); sheet.addCell(label);
            number = new jxl.write.Number(9, fila, i.monto); sheet.addCell(number);
            label = new Label(10, fila, i?.emision?.toString()); sheet.addCell(label);
            label = new Label(11, fila, i?.vencimiento?.toString()); sheet.addCell(label);
            number = new jxl.write.Number(12, fila, i.dias); sheet.addCell(number);


            fila++

        }


        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "ContratosExcel.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());
    }


    def presupuestadas () {


        def campos = ["codigo": ["Código", "string"], "nombre": ["Nombre", "string"], "descripcion": ["Descripción", "string"], "oficioIngreso": ["Memo ingreso", "string"], "oficioSalida": ["Memo salida", "string"], "sitio": ["Sitio", "string"], "plazoEjecucionMeses": ["Plazo", "number"], "parroquia": ["Parroquia", "string"], "comunidad": ["Comunidad", "string"], "departamento": ["Dirección", "string"], "fechaCreacionObra": ["Fecha", "date"]]
        [campos:campos]

    }

    def buscarObraPre(){
        println "buscar obra pre"
        def extraParr = ""
        def extraCom = ""
        def extraDep = ""

        if (params.campos instanceof java.lang.String) {
            if (params.campos == "parroquia") {
                def parrs = Parroquia.findAll("from Parroquia where nombre like '%${params.criterios.toUpperCase()}%'")
                params.criterios = ""
                parrs.eachWithIndex { p, i ->
                    extraParr += "" + p.id
                    if (i < parrs.size() - 1)
                        extraParr += ","
                }
                if (extraParr.size() < 1)
                    extraParr = "-1"
                params.campos = ""
                params.operadores = ""
            }
            if (params.campos == "comunidad") {
                def coms = Comunidad.findAll("from Comunidad where nombre like '%${params.criterios.toUpperCase()}%'")
                params.criterios = ""
                coms.eachWithIndex { p, i ->
                    extraCom += "" + p.id
                    if (i < coms.size() - 1)
                        extraCom += ","
                }
                if (extraCom.size() < 1)
                    extraCom = "-1"
                params.campos = ""
                params.operadores = ""
            }
            if (params.campos == "departamento") {
                def dirs = Direccion.findAll("from Direccion where nombre like '%${params.criterios.toUpperCase()}%'")
                def deps = Departamento.findAllByDireccionInList(dirs)
                params.criterios = ""
                deps.eachWithIndex { p, i ->
                    extraDep += "" + p.id
                    if (i < deps.size() - 1)
                        extraDep += ","
                }
                if (extraDep.size() < 1)
                    extraDep = "-1"
                params.campos = ""
                params.operadores = ""
            }
        } else {
            def remove = []
            params.campos.eachWithIndex { p, i ->
                if (p == "comunidad") {
                    def coms = Comunidad.findAll("from Comunidad where nombre like '%${params.criterios[i].toUpperCase()}%'")

                    coms.eachWithIndex { c, j ->
                        extraCom += "" + c.id
                        if (j < coms.size() - 1)
                            extraCom += ","
                    }
                    if (extraCom.size() < 1)
                        extraCom = "-1"
                    remove.add(i)
                }
                if (p == "parroquia") {
                    def parrs = Parroquia.findAll("from Parroquia where nombre like '%${params.criterios[i].toUpperCase()}%'")

                    parrs.eachWithIndex { c, j ->
                        extraParr += "" + c.id
                        if (j < parrs.size() - 1)
                            extraParr += ","
                    }
                    if (extraParr.size() < 1)
                        extraParr = "-1"
                    remove.add(i)
                }
                if (p == "departamento") {
                    def dirs = Direccion.findAll("from Direccion where nombre like '%${params.criterios.toUpperCase()}%'")
                    def deps = Departamento.findAllByDireccionInList(dirs)

                    deps.eachWithIndex { c, j ->
                        extraDep += "" + c.id
                        if (j < deps.size() - 1)
                            extraDep += ","
                    }
                    if (extraDep.size() < 1)
                        extraDep = "-1"
                    remove.add(i)
                }
            }
            remove.each {
                params.criterios[it] = null
                params.campos[it] = null
                params.operadores[it] = null
            }
        }


//        def extras = " and liquidacion=0 and fechaFin is not null"
        def extras = " and estado='R'"
        if (extraParr.size() > 1)
            extras += " and parroquia in (${extraParr})"
        if (extraCom.size() > 1)
            extras += " and comunidad in (${extraCom})"
        if (extraDep.size() > 1)
            extras += " and departamento in (${extraDep})"

//        println "extas "+extras
        def parr = { p ->
            return p.parroquia?.nombre
        }
        def comu = { c ->
            return c.comunidad?.nombre
        }
        def listaTitulos = ["CODIGO", "NOMBRE", "DESCRIPCION", "DIRECCION", "FECHA REG.", "M. INGRESO", "M. SALIDA", "SITIO", "PLAZO", "PARROQUIA", "COMUNIDAD", "INSPECTOR", "REVISOR", "RESPONSABLE", "ESTADO"]
        def listaCampos = ["codigo", "nombre", "descripcion", "departamento", "fechaCreacionObra", "oficioIngreso", "oficioSalida", "sitio", "plazoEjecucionMeses", "parroquia", "comunidad", "inspector", "revisor", "responsableObra", "estado"]
        def funciones = [null, null, null, null, ["format": ["dd/MM/yyyy hh:mm"]], null, null, null, null, ["closure": [parr, "&"]], ["closure": [comu, "&"]], null, null, null, null]
        def url = g.createLink(action: "buscarObraFin", controller: "obra")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-busqueda").modal("hide");'
        funcionJs += 'location.href="' + g.createLink(action: 'registroObra', controller: 'obra') + '?obra="+$(this).attr("regId");'
        funcionJs += '}'
        def numRegistros = 20
//        println "params " + params.reporte + "  " + params.excel

        if (!params.reporte) {
            if (params.excel) {
                session.dominio = Obra
                session.funciones = funciones
                def anchos = [15, 50, 70, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20]
                /*anchos para el set column view en excel (no son porcentajes)*/
                redirect(controller: "reportes", action: "reporteBuscadorExcel", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Obra", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Obras", anchos: anchos, extras: extras, landscape: true])
            } else {
                def lista = buscadorService.buscar(Obra, "Obra", "excluyente", params, true, extras)
                /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
                lista.pop()
                render(view: '../tablaBuscador', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "obra", numRegistros: numRegistros, funcionJs: funcionJs, width: 1800, paginas: 12])
            }

        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Obra
            session.funciones = funciones
            def anchos = [7, 10, 7, 7, 7, 7, 7, 7, 4, 7, 7, 7, 7, 7, 7]
            /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador2", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Obra", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Lista de obras", anchos: anchos, extras: extras, landscape: true])
        }
    }


}
