package janus

import com.itextpdf.text.BadElementException
import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import janus.ejecucion.*
import janus.pac.CronogramaEjecucion
import janus.pac.Garantia
import janus.pac.PeriodoEjecucion
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.*

import java.awt.*


import jxl.write.NumberFormat;

//import java.awt.Label



class ReportesController {

    def index() {}

    def buscadorService
    def preciosService
    def dbConnectionService

    def garantiasContrato() {
        def contrato = Contrato.get(params.id)
        def garantias = Garantia.findAllByContrato(contrato)
        return [contrato: contrato, garantias: garantias]
    }


    def rubro = {
        println "rep!!!  rubro " + params
//        def rubro
//        def grupos = []
//        def volquetes = []
//        def choferes = []
//        def grupoTransporte=DepartamentoItem.findAllByTransporteIsNotNull()
//        grupoTransporte.each {
//            if(it.transporte.codigo=="H")
//                choferes=Item.findAllByDepartamento(it)
//            if(it.transporte.codigo=="T")
//                volquetes=Item.findAllByDepartamento(it)
//        }
//        grupos.add(Grupo.get(4))
//        grupos.add(Grupo.get(5))
//        grupos.add(Grupo.get(6))
//
//        rubro = Item.get(params.id)
//        def items=Rubro.findAllByRubro(rubro)
//        items.sort{it.item.codigo}
//        [ rubro: rubro, grupos: grupos,items:items,choferes:choferes,volquetes:volquetes]
//        render "<html><head></head><body>Hola</body></html>"
        return [algo: "algo"]
    }

    def imprimeMatriz() {

        def obra = Obra.get(params.id)

        println "imprime matriz"
        def cn = buscadorService.dbConnectionService.getConnection()
        def cn2 = buscadorService.dbConnectionService.getConnection()
        def sql = "SELECT clmncdgo,clmndscr,clmntipo from mfcl where obra__id=${params.id} order by 1"
        def columnas = []
        def filas = []
        cn.eachRow(sql.toString()) { r ->
            def col = r[1]
            if (r[2] != "R") {
                def parts = col.split("_")
                //println "parts "+parts
                try {
                    col = parts[0].toLong()
                    col = Item.get(col).nombre

                } catch (e){
                    col = parts[0]
                }

                col += parts[1]?.replaceAll("T"," Total")?.replaceAll("U"," Unitario")
            }

            columnas.add([r[0], col, r[2]])
        }
        sql = "SELECT * from mfrb where obra__id=${params.id} order by orden"
        def cont = 1
        cn.eachRow(sql.toString()) { r ->
            def tmp = [cont, r[0].trim(), r[2], r[3], r[4]]
            def sq = ""
            columnas.each { c ->
                if (c[2] != "R") {
                    sq = "select valor from mfvl where obra__id=${params.id} and clmncdgo=${c[0]} and codigo='${r[0].trim()}'"
                    cn2.eachRow(sq.toString()) { v ->
                        tmp.add(v[0])
                    }
                }

            }
//            println "fila  "+tmp
            println("col" + columnas)
            filas.add(tmp)
            cont++
        }

        def baos = new ByteArrayOutputStream()
        def name = "matriz_polinomica_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
//            println "name "+name
        Font titleFont = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font catFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Document document
        document = new Document(PageSize.A4.rotate());
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();
        document.addTitle("Matriz Polinómica " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus,matriz");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");
        Font small = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Paragraph headers = new Paragraph();
        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);
        headers.add(new Paragraph("GOBIERNO AUTÓNOMO DESCENTRALIZADO DE LA PROVINCIA DE PICHINCHA", titleFont));
        headers.add(new Paragraph("MATRIZ DE LA FORMULA POLINÓMICA", titleFont));
        headers.add(new Paragraph("OBRA: " + obra?.descripcion, titleFont));
        headers.add(new Paragraph("FECHA: " + new Date().format("dd-MM-yyyy"), titleFont));
        addEmptyLine(headers, 1);
        document.add(headers);


        /*table*/

        def parcial = []
        def anchos = [4, 7, 35, 5, 8, 8, 8, 8, 8, 8]     // , 9
        def inicio = 0
        def fin = 10
        def vr = 0
        println "size "+columnas.size()
        while (fin  <= columnas.size() + 1) {  //gdo  <= antes
            println "inicio "+inicio+"  fin  "+fin
            if (inicio != 0)
                anchos = [10, 10, 10, 10, 10, 10, 10, 10, 10, 10]
            if (fin - inicio < 10) {
                anchos = []
                (fin - inicio).toInteger().times { i ->
                    anchos.add((100 / (fin - inicio)).toInteger())
                }
            }
            def parrafo = new Paragraph("")
/*
            if (inicio == fin)
               inicio -= 2       //gdo
*/

            PdfPTable table = new PdfPTable((fin - inicio).toInteger());       //gdo

            table.setWidthPercentage(100);
            table.setWidths(arregloEnteros(anchos))
            (fin - inicio).toInteger().times { i ->



                if(inicio != 0){

                    println("entro" + i)
                    println("--->>>"  + i)
                    println("%%%%"  + inicio)
                    PdfPCell c1 = new PdfPCell(new Phrase(columnas[(inicio+i)-1][1], small));
                    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(c1);


                }
                if(inicio == 0){

                    PdfPCell c1 = new PdfPCell(new Phrase(columnas[inicio+i][1], small));
                    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(c1);

                }
//
//                PdfPCell c1 = new PdfPCell(new Phrase(columnas[inicio+i][1], small));
//                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//                table.addCell(c1);

            }
            table.setHeaderRows(1);
            filas.each { f ->
                (fin - inicio).toInteger().times { i ->

                    if(inicio != 0) {

                        def dato = f[(inicio + i)-1]
                        if (!dato)
                            dato = "0.00"
                        else
                            dato = dato.toString()
                        def cell = new PdfPCell(new Phrase(dato, small));
                        cell.setFixedHeight(16f);
                        table.addCell(cell);

                    }
                    if(inicio == 0){

                        def dato = f[inicio + i]
                        if (!dato)
                            dato = "0.00"
                        else
                            dato = dato.toString()
                        def cell = new PdfPCell(new Phrase(dato, small));
                        cell.setFixedHeight(16f);
                        table.addCell(cell);

                    }
//                    def dato = f[inicio + i]
//                    if (!dato)
//                        dato = "0.00"
//                    else
//                        dato = dato.toString()
//                    def cell = new PdfPCell(new Phrase(dato, small));
//                    cell.setFixedHeight(16f);
//                    table.addCell(cell);
                }
            }
            parrafo.add(table);
            document.add(parrafo);
            document.newPage();
//            inicio = fin + 1
            inicio = fin
            fin = inicio + 10
            if (fin > columnas.size() + 1){
                fin = columnas.size() + 1
            }
            if (inicio > columnas.size())
                break;

        }

        /*table*/

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)


    }

    def reporteBuscador = {

        // println "reporte buscador params !! "+params
        if (!session.dominio)
            response.sendError(403)
        else {
            def listaTitulos = params.listaTitulos
            def listaCampos = params.listaCampos
            def lista = buscadorService.buscar(session.dominio, params.tabla, "excluyente", params, true, params.extras)
            def funciones = session.funciones
            session.dominio = null
            session.funciones = null
            lista.pop()

            def baos = new ByteArrayOutputStream()
            def name = "reporte_de_" + params.titulo.replaceAll(" ", "_") + "_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
//            println "name "+name
            Font catFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
            Font info = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
            Document document
            if (params.landscape)
                document = new Document(PageSize.A4.rotate());
            else
                document = new Document();

            def pdfw = PdfWriter.getInstance(document, baos);

            document.open();
            document.addTitle("Reporte de " + params.titulo + " " + new Date().format("dd_MM_yyyy"));
            document.addSubject("Generado por el sistema Janus");
            document.addKeywords("reporte, elyon," + params.titulo);
            document.addAuthor("Janus");
            document.addCreator("Tedein SA");
            Paragraph preface = new Paragraph();
            addEmptyLine(preface, 1);
            preface.add(new Paragraph("Reporte de " + params.titulo, catFont));
            preface.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
            addEmptyLine(preface, 1);
            document.add(preface);
//        Start a new page
//        document.newPage();
            //System.getProperty("user.name")
            addContent(document, catFont, listaCampos.size(), listaTitulos, params.anchos, listaCampos, funciones, lista);            // Los tamaños son porcentajes!!!!
            document.close();
            pdfw.close()
            byte[] b = baos.toByteArray();
            response.setContentType("application/pdf")
            response.setHeader("Content-disposition", "attachment; filename=" + name)
            response.setContentLength(b.length)
            response.getOutputStream().write(b)
        }
    }

    def pac() {
        println "params REPORTE " + params
        def pac
        def dep
        def anio
        if (!params.todos) {
            anio = janus.pac.Anio.get(params.anio)
            if (params.dpto) {
                dep = Departamento.get(params.dpto)
                pac = janus.pac.Pac.findAllByDepartamentoAndAnio(dep, anio, [sort: "id"])
                dep = dep.descripcion
                anio = anio.anio
            } else {
                pac = janus.pac.Pac.findAllByAnio(janus.pac.Anio.get(params.anio), [sort: "id"])
                dep = "Todos"
                anio = anio.anio
            }
        } else {
            dep = "Todos"
            anio = "Todos"
            pac = janus.pac.Pac.list([sort: "id"])
        }

        [pac: pac, todos: params.todos, dep: dep, anio: anio]
    }

    def pacExcel() {
        println "params REPORTE " + params
        def pac
        def dep
        def anio
        if (!params.todos) {
            anio = janus.pac.Anio.get(params.anio)
            if (params.dpto) {
                dep = Departamento.get(params.dpto)
                pac = janus.pac.Pac.findAllByDepartamentoAndAnio(dep, anio, [sort: "id"])
                dep = dep.descripcion
                anio = anio.anio
            } else {
                pac = janus.pac.Pac.findAllByAnio(janus.pac.Anio.get(params.anio), [sort: "id"])
                dep = "Todos"
                anio = anio.anio
            }
        } else {
            dep = "Todos"
            anio = "Todos"
            pac = janus.pac.Pac.list([sort: "id"])
        }

        WorkbookSettings workbookSettings = new WorkbookSettings()
        workbookSettings.locale = Locale.default
        def file = File.createTempFile('myExcelDocument', '.xls')
        file.deleteOnExit()
        WritableWorkbook workbook = Workbook.createWorkbook(file, workbookSettings)
        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, true);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        WritableFont times10Font = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, true);
        WritableCellFormat times10 = new WritableCellFormat(times10Font);
        def fila = 6

        WritableSheet sheet = workbook.createSheet("PAC", 0)

        sheet.setColumnView(0, 5)
        sheet.setColumnView(1, 10)
        sheet.setColumnView(2, 50)
        sheet.setColumnView(3, 15)
        sheet.setColumnView(4, 15)
        sheet.setColumnView(5, 50)
        sheet.setColumnView(6, 10)
        sheet.setColumnView(7, 5)
        sheet.setColumnView(8, 10)
        sheet.setColumnView(9, 10)
        sheet.setColumnView(10, 5)
        sheet.setColumnView(11, 5)
        sheet.setColumnView(12, 5)

        def label = new Label(0, 1, "Gobierno Autónomo Descentralizado de la provincia de pichincha".toUpperCase(), times16format); sheet.addCell(label);
        label = new Label(0, 2, "Departamento de compras públicas".toUpperCase(), times16format); sheet.addCell(label);
        label = new Label(0, 3, "Plan anual de compras".toUpperCase(), times16format); sheet.addCell(label);
        label = new Label(0, 4, "Departamento: ${dep}".toUpperCase(), times16format); sheet.addCell(label);
        label = new Label(0, 5, "Año: ${anio}".toUpperCase(), times16format); sheet.addCell(label);

        label = new Label(0, fila, "#", times16format); sheet.addCell(label);
        label = new Label(1, fila, "Año", times16format); sheet.addCell(label);
        label = new Label(2, fila, "Partida", times16format); sheet.addCell(label);
        label = new Label(3, fila, "CPP", times16format); sheet.addCell(label);
        label = new Label(4, fila, "Tipo compra", times16format); sheet.addCell(label);
        label = new Label(5, fila, "Descripción", times16format); sheet.addCell(label);
        label = new Label(6, fila, "Cantidad", times16format); sheet.addCell(label);
        label = new Label(7, fila, "Unidad", times16format); sheet.addCell(label);
        label = new Label(8, fila, "Unitario", times16format); sheet.addCell(label);
        label = new Label(9, fila, "Total", times16format); sheet.addCell(label);
        label = new Label(10, fila, "C1", times16format); sheet.addCell(label);
        label = new Label(11, fila, "C2", times16format); sheet.addCell(label);
        label = new Label(12, fila, "C3", times16format); sheet.addCell(label);
        fila++
        def total = 0
        pac.eachWithIndex { p, i ->
            label = new Label(0, fila, "${i + 1}", times10); sheet.addCell(label);
            label = new Label(1, fila, p.anio.anio, times10); sheet.addCell(label);
            label = new Label(2, fila, p.presupuesto.numero, times10); sheet.addCell(label);
            label = new Label(3, fila, p.cpp.numero, times10); sheet.addCell(label);
            label = new Label(4, fila, p.tipoCompra.descripcion, times10); sheet.addCell(label);
            label = new Label(5, fila, p.descripcion, times10); sheet.addCell(label);
            def number = new Number(6, fila, p.cantidad); sheet.addCell(number);
            label = new Label(7, fila, p.unidad.codigo, times10); sheet.addCell(label);
            number = new Number(8, fila, p.costo); sheet.addCell(number);
            number = new Number(9, fila, p.cantidad * p.costo); sheet.addCell(number);
            label = new Label(10, fila, p.c1, times10); sheet.addCell(label);
            label = new Label(11, fila, p.c2, times10); sheet.addCell(label);
            label = new Label(12, fila, p.c3, times10); sheet.addCell(label);
            total += p.cantidad * p.costo
            fila++

        }
        label = new Label(8, fila, "TOTAL", times10); sheet.addCell(label);
        def number = new Number(9, fila, total); sheet.addCell(number);
        fila++


        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "pac.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());
    }


    def analisisPrecios() {

//        def item = Item.get(189)
//
//        println(item.id)
//
//        def rubro = PrecioRubrosItems.get(item.id)
//
//
//
//        println(rubro)
//
//        def grupo = Grupo.get(rubro.item.departamento.subgrupo.grupo.id)
//
//
//        println(grupo)
//        [item: item, rubro: rubro, grupo: grupo]

    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }


    private static void addContent(Document document, catFont, columnas, headers, anchos, campos, funciones, datos) throws DocumentException {
        Font small = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
        def parrafo = new Paragraph("")
        createTable(parrafo, columnas, headers, anchos, campos, funciones, datos);
        document.add(parrafo);


    }


    private static void createTable(Paragraph subCatPart, columnas, headers, anchos, campos, funciones, datos) throws BadElementException {
        PdfPTable table = new PdfPTable(columnas);
        table.setWidthPercentage(100);
        table.setWidths(arregloEnteros(anchos))
        Font small = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
        headers.eachWithIndex { h, i ->
            PdfPCell c1 = new PdfPCell(new Phrase(h, small));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
        }
        table.setHeaderRows(1);
        def tagLib = new BuscadorTagLib()
        datos.each { d ->
            campos.eachWithIndex { c, j ->
                def campo
                if (funciones) {
                    if (funciones[j])
                        campo = tagLib.operacion([propiedad: c, funcion: funciones[j], registro: d]).toString()
                    else
                        campo = d.properties[c].toString()
                } else {
                    campo = d.properties[c].toString()
                }

                table.addCell(new Phrase(campo, small));

            }

        }

        subCatPart.add(table);

    }

    private static void createList(Section subCatPart) {
        List list = new List(true, false, 10);
        list.add(new ListItem("First point"));
        list.add(new ListItem("Second point"));
        list.add(new ListItem("Third point"));
        subCatPart.add(list);
    }


    static arregloEnteros(array) {
        int[] ia = new int[array.size()]
        array.eachWithIndex { it, i ->
            ia[i] = it.toInteger()
        }

        return ia
    }

    def reporteSubgrupos () {

        def obra = Obra.get(params.id)

        def sql ="SELECT\n" +
                 "dprtdscr          descripcion,\n" +
                 "itemnmbr          nombre,\n" +
                 "vlobcntd          cantidad,\n" +
                 "sbprdscr          subpresupuesto,\n" +
                 "grpodscr          grupo,\n"   +
                 "sbgrdscr          subgrupo\n" +
                 "FROM vlob,item,dprt,sbpr,grpo,sbgr\n" +
                 "where item.item__id=vlob.item__id AND\n" +
                 "dprt.dprt__id=item.dprt__id AND\n"  +
                 "sbpr.sbpr__id=vlob.sbpr__id AND\n"  +
                 "sbgr.sbgr__id=dprt.sbgr__id AND\n"  +
                 "grpo.grpo__id=sbgr.grpo__id AND\n"  +
                 "obra__id= ${params.id}\n"+
                 "order by sbprdscr,grpodscr,sbgrdscr,dprtdscr,itemnmbr"

//        println("sql:" + sql)

        def cn = dbConnectionService.getConnection()

        def res = cn.rows(sql.toString())

//        return [res: res, obra: obra]

        def baos = new ByteArrayOutputStream()
        def name = "subgrupos_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        times8boldWhite.setColor(Color.BLACK)
        times10boldWhite.setColor(Color.BLACK)
        def fonts = [times12bold: times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal]

        Document document
        document = new Document(PageSize.A4);
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();
        document.addTitle("Subgrupos " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, rubros");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeader = [border: Color.WHITE, colspan: 7,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsHeader2 = [border: Color.WHITE, colspan: 3,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellIzquierda = [border: Color.WHITE,
                align: Element.ALIGN_LEFT, valign: Element.ALIGN_LEFT]
        def prmsCellCenter = [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellLeft = [border: Color.WHITE, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.WHITE, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum]

            Paragraph headers = new Paragraph();
            addEmptyLine(headers, 1);
            headers.setAlignment(Element.ALIGN_CENTER);
            headers.add(new Paragraph("GOBIERNO AUTÓNOMO DESCENTRALIZADO DE LA PROVINCIA DE PICHINCHA", times12bold));
            headers.add(new Paragraph("SUBGRUPOS", times10bold));
            headers.add(new Paragraph("OBRA:" + obra?.descripcion, times10bold));
            headers.add(new Paragraph("FECHA:" + new Date().format("dd-MM-yyyy"), times10bold));

            addEmptyLine(headers, 1);
            document.add(headers);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(arregloEnteros([16,15,15,20,25,9]))

        addCellTabla(table, new Paragraph("Subpresupuesto", times10boldWhite), prmsCellHead)
        addCellTabla(table, new Paragraph("Solicitante", times10boldWhite), prmsCellHead)
        addCellTabla(table, new Paragraph("Grupo", times10boldWhite), prmsCellHead)
        addCellTabla(table, new Paragraph("Subgrupo", times10boldWhite), prmsCellHead)
        addCellTabla(table, new Paragraph("Item", times10boldWhite), prmsCellHead)
        addCellTabla(table, new Paragraph("Cantidad", times10boldWhite), prmsCellHead)


        res.each {r->
            addCellTabla(table, new Paragraph(r?.subpresupuesto, times8normal), prmsCellIzquierda)
            addCellTabla(table, new Paragraph(r?.grupo, times8normal), prmsCellIzquierda)
            addCellTabla(table, new Paragraph(r?.subgrupo, times8normal), prmsCellIzquierda)
            addCellTabla(table, new Paragraph(r?.descripcion, times8normal), prmsCellIzquierda)
            addCellTabla(table, new Paragraph(r?.nombre, times8normal), prmsCellIzquierda)
            addCellTabla(table, new Paragraph(g.formatNumber(number: r?.cantidad, minFractionDigits: 3, maxFractionDigits: 3, format: "##,###0", locale: "ec"), times8normal), prmsNum)

//            addCellTabla(table, new Paragraph(g.formatNumber(number: r?.subpresupuesto?.descripcion, minFractionDigits: 0, maxFractionDigits: 0, format: "##,#0", locale: "ec"), times8normal), prmsNum)

        }

        document.add(table);
        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)

    }



    def imprimirRubrosExcel() {
        def obra = Obra.get(params.obra.toLong())
        def lugar = obra.lugar
        def fecha = obra.fechaPreciosRubros
        def itemsChofer = [obra.chofer]
        def itemsVolquete = [obra.volquete]
        def indi = obra.totales
        WorkbookSettings workbookSettings = new WorkbookSettings()
        workbookSettings.locale = Locale.default
        def file = File.createTempFile('myExcelDocument', '.xls')
        file.deleteOnExit()
        WritableWorkbook workbook = Workbook.createWorkbook(file, workbookSettings)
        WritableFont font = new WritableFont(WritableFont.ARIAL, 12)
        WritableCellFormat formatXls = new WritableCellFormat(font)
        def row = 0

        preciosService.ac_rbroObra(obra.id)
        VolumenesObra.findAllByObra(obra, [sort: "orden"]).item.eachWithIndex { rubro, i ->
            def res = preciosService.presioUnitarioVolumenObra("* ", obra.id, rubro.id)
            WritableSheet sheet = workbook.createSheet(rubro.codigo, i)
            rubroAExcel(sheet, res, rubro, fecha, indi)
        }
        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "rubro.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());

    }


    def rubroAExcel(sheet, res, rubro, fecha, indi) {
        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, true);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        WritableFont times10Font = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, true);
        WritableCellFormat times10 = new WritableCellFormat(times10Font);
        sheet.setColumnView(0, 20)
        sheet.setColumnView(1, 50)
        sheet.setColumnView(2, 15)
        sheet.setColumnView(3, 15)
        sheet.setColumnView(4, 15)
        sheet.setColumnView(5, 15)
        sheet.setColumnView(6, 15)

//        sheet.setColumnView(4, 30)
//        sheet.setColumnView(8, 20)
        def label = new Label(0, 1, "Gobierno Autónomo Descentralizado de la provincia de pichincha".toUpperCase(), times16format); sheet.addCell(label);
        label = new Label(0, 2, "Departamento de costos".toUpperCase(), times16format); sheet.addCell(label);
        label = new Label(0, 3, "Análisis de precios unitarios".toUpperCase(), times16format); sheet.addCell(label);

        sheet.mergeCells(0, 1, 1, 1)
        sheet.mergeCells(0, 2, 1, 2)
        sheet.mergeCells(0, 3, 1, 3)
        label = new Label(0, 5, "Fecha: " + new Date().format("dd-MM-yyyy"), times16format); sheet.addCell(label);
        sheet.mergeCells(0, 5, 1, 5)
        label = new Label(0, 6, "Código: " + rubro.codigo, times16format); sheet.addCell(label);
        sheet.mergeCells(0, 6, 1, 6)
        label = new Label(0, 7, "Descripción: " + rubro.nombre, times16format); sheet.addCell(label);
        sheet.mergeCells(0, 7, 1, 7)
        label = new Label(5, 5, "Fecha Act. P.U: " + fecha?.format("dd-MM-yyyy"), times16format); sheet.addCell(label);
        sheet.mergeCells(5, 5, 6, 5)
        label = new Label(5, 6, "Unidad: " + rubro.unidad?.codigo, times16format); sheet.addCell(label);
        sheet.mergeCells(5, 6, 6, 6)

        def fila = 9
        label = new Label(0, fila, "Herramientas", times16format); sheet.addCell(label);
        sheet.mergeCells(0, fila, 1, fila)
        fila++
        def number
        def totalHer = 0
        def totalMan = 0
        def totalMat = 0
        def total = 0
        def band = 0
        def rowsTrans = []
        res.each { r ->
//            println "r "+r
            if (r["grpocdgo"] == 3) {
                if (band == 0) {
                    label = new Label(0, fila, "Código", times16format); sheet.addCell(label);
                    label = new Label(1, fila, "Descripción", times16format); sheet.addCell(label);
                    label = new Label(2, fila, "Cantidad", times16format); sheet.addCell(label);
                    label = new Label(3, fila, "Tarifa", times16format); sheet.addCell(label);
                    label = new Label(4, fila, "Costo", times16format); sheet.addCell(label);
                    label = new Label(5, fila, "Rendimiento", times16format); sheet.addCell(label);
                    label = new Label(6, fila, "C.Total", times16format); sheet.addCell(label);
                    fila++
                }
                band = 1
                label = new Label(0, fila, r["itemcdgo"], times10); sheet.addCell(label);
                label = new Label(1, fila, r["itemnmbr"], times10); sheet.addCell(label);
                number = new Number(2, fila, r["rbrocntd"]); sheet.addCell(number);
                number = new Number(3, fila, r["rbpcpcun"]); sheet.addCell(number);
                number = new Number(4, fila, r["rbpcpcun"] * r["rbrocntd"]); sheet.addCell(number);
                number = new Number(5, fila, r["rndm"]); sheet.addCell(number);
                number = new Number(6, fila, r["parcial"]); sheet.addCell(number);
                totalHer += r["parcial"]
                fila++
            }
            if (r["grpocdgo"] == 2) {
                if (band == 1) {
                    label = new Label(0, fila, "SUBTOTAL", times10); sheet.addCell(label);
                    number = new Number(6, fila, totalHer); sheet.addCell(number);
                    fila++
                }
                if (band != 2) {
                    fila++
                    label = new Label(0, fila, "Mano de obra", times16format); sheet.addCell(label);
                    sheet.mergeCells(0, fila, 1, fila)
                    fila++
                    label = new Label(0, fila, "Código", times16format); sheet.addCell(label);
                    label = new Label(1, fila, "Descripción", times16format); sheet.addCell(label);
                    label = new Label(2, fila, "Cantidad", times16format); sheet.addCell(label);
                    label = new Label(3, fila, "Jornal", times16format); sheet.addCell(label);
                    label = new Label(4, fila, "Costo", times16format); sheet.addCell(label);
                    label = new Label(5, fila, "Rendimiento", times16format); sheet.addCell(label);
                    label = new Label(6, fila, "C.Total", times16format); sheet.addCell(label);
                    fila++
                }
                band = 2
                label = new Label(0, fila, r["itemcdgo"], times10); sheet.addCell(label);
                label = new Label(1, fila, r["itemnmbr"], times10); sheet.addCell(label);
                number = new Number(2, fila, r["rbrocntd"]); sheet.addCell(number);
                number = new Number(3, fila, r["rbpcpcun"]); sheet.addCell(number);
                number = new Number(4, fila, r["rbpcpcun"] * r["rbrocntd"]); sheet.addCell(number);
                number = new Number(5, fila, r["rndm"]); sheet.addCell(number);
                number = new Number(6, fila, r["parcial"]); sheet.addCell(number);
                totalMan += r["parcial"]
                fila++
            }
            if (r["grpocdgo"] == 1) {
                if (band == 2) {
                    label = new Label(0, fila, "SUBTOTAL", times10); sheet.addCell(label);
                    number = new Number(6, fila, totalMan); sheet.addCell(number);
                    fila++
                }
                if (band != 3) {
                    fila++
                    label = new Label(0, fila, "Materiales", times16format); sheet.addCell(label);
                    sheet.mergeCells(0, fila, 1, fila)
                    fila++
                    label = new Label(0, fila, "Código", times16format); sheet.addCell(label);
                    label = new Label(1, fila, "Descripción", times16format); sheet.addCell(label);
                    label = new Label(2, fila, "Cantidad", times16format); sheet.addCell(label);
                    label = new Label(3, fila, "Unitario", times16format); sheet.addCell(label);
                    label = new Label(6, fila, "C.Total", times16format); sheet.addCell(label);
                    fila++
                }
                band = 3
                label = new Label(0, fila, r["itemcdgo"], times10); sheet.addCell(label);
                label = new Label(1, fila, r["itemnmbr"], times10); sheet.addCell(label);
                number = new Number(2, fila, r["rbrocntd"]); sheet.addCell(number);
                number = new Number(3, fila, r["rbpcpcun"]); sheet.addCell(number);
                number = new Number(6, fila, r["parcial"]); sheet.addCell(number);
                totalMat += r["parcial"]
                fila++

            }
            if (r["parcial_t"] > 0) {
                rowsTrans.add(r)
                total += r["parcial_t"]
            }

        }
        if (band == 3) {
            label = new Label(0, fila, "SUBTOTAL", times10); sheet.addCell(label);
            number = new Number(6, fila, totalMat); sheet.addCell(number);
            fila++
        }

        /*Tranporte*/
        if (rowsTrans.size() > 0) {
            fila++
            label = new Label(0, fila, "Transporte", times16format); sheet.addCell(label);
            sheet.mergeCells(0, fila, 1, fila)
            fila++
            label = new Label(0, fila, "Código", times16format); sheet.addCell(label);
            label = new Label(1, fila, "Descripción", times16format); sheet.addCell(label);
            label = new Label(2, fila, "Peso/Vol", times16format); sheet.addCell(label);
            label = new Label(3, fila, "Cantidad", times16format); sheet.addCell(label);
            label = new Label(4, fila, "Distancia", times16format); sheet.addCell(label);
            label = new Label(5, fila, "Unitario", times16format); sheet.addCell(label);
            label = new Label(6, fila, "C.Total", times16format); sheet.addCell(label);
            fila++
            rowsTrans.each { rt ->
                label = new Label(0, fila, rt["itemcdgo"], times10); sheet.addCell(label);
                label = new Label(1, fila, rt["itemnmbr"], times10); sheet.addCell(label);
                number = new Number(2, fila, rt["itempeso"]); sheet.addCell(number);
                number = new Number(3, fila, rt["rbrocntd"]); sheet.addCell(number);
                number = new Number(4, fila, rt["distancia"]); sheet.addCell(number);
                number = new Number(5, fila, rt["parcial_t"] / (rt["itempeso"] * rt["rbrocntd"] * rt["distancia"])); sheet.addCell(number);
                number = new Number(6, fila, rt["parcial_t"]); sheet.addCell(number);
                fila++
            }
            label = new Label(0, fila, "SUBTOTAL", times10); sheet.addCell(label);
            number = new Number(6, fila, total); sheet.addCell(number);
            fila++
            fila++
        }

        /*indirectos */

        label = new Label(0, fila, "Costos Indirectos", times16format); sheet.addCell(label);
        sheet.mergeCells(0, fila, 1, fila)
        fila++

        label = new Label(0, fila, "Descripción", times16format); sheet.addCell(label);
        sheet.mergeCells(0, fila, 1, fila)
        label = new Label(5, fila, "Porcentaje", times16format); sheet.addCell(label);
        label = new Label(6, fila, "Valor", times16format); sheet.addCell(label);
        fila++
        def totalRubro = total + totalHer + totalMan + totalMat
        def totalIndi = totalRubro * indi / 100
        label = new Label(0, fila, "Costos indirectos", times10); sheet.addCell(label);
        sheet.mergeCells(0, fila, 1, fila)
        number = new Number(5, fila, indi); sheet.addCell(number);
        number = new Number(6, fila, totalIndi); sheet.addCell(number);

        /*Totales*/
        fila += 4
        label = new Label(4, fila, "Costo unitario directo", times16format); sheet.addCell(label);
        sheet.mergeCells(4, fila, 5, fila)
        label = new Label(4, fila + 1, "Costos indirectos", times16format); sheet.addCell(label);
        sheet.mergeCells(4, fila + 1, 5, fila + 1)
        label = new Label(4, fila + 2, "Costo total del rubro", times16format); sheet.addCell(label);
        sheet.mergeCells(4, fila + 2, 5, fila + 2)
        label = new Label(4, fila + 3, "Precio unitario", times16format); sheet.addCell(label);
        sheet.mergeCells(4, fila + 3, 5, fila + 3)
        number = new Number(6, fila, totalRubro); sheet.addCell(number);
        number = new Number(6, fila + 1, totalIndi); sheet.addCell(number);
        number = new Number(6, fila + 2, totalRubro + totalIndi); sheet.addCell(number);
        number = new Number(6, fila + 3, (totalRubro + totalIndi).toDouble().round(2)); sheet.addCell(number);
        return sheet
    }



    def imprimirRubros() {


        def obra = Obra.get(params.obra.toLong())


        def lugar = obra.lugar
        def fecha = obra.fechaPreciosRubros
        def itemsChofer = [obra.chofer]
        def itemsVolquete = [obra.volquete]

        def indi = obra.totales

        preciosService.ac_rbroObra(obra.id)

        def baos = new ByteArrayOutputStream()
        def name = "rubros_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        times8boldWhite.setColor(Color.BLACK)
        times10boldWhite.setColor(Color.BLACK)
        def fonts = [times12bold: times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal]

        Document document
        document = new Document(PageSize.A4);
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();
        document.addTitle("Rubros " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, rubros");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeader = [border: Color.WHITE, colspan: 7,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsHeader2 = [border: Color.WHITE, colspan: 3,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE,
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenter = [border: Color.WHITE, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellLeft = [border: Color.WHITE, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.WHITE, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.WHITE, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum]

        VolumenesObra.findAllByObra(obra, [sort: "orden"]).item.each { rubro ->

            Paragraph headers = new Paragraph();
            addEmptyLine(headers, 1);
            headers.setAlignment(Element.ALIGN_CENTER);
            headers.add(new Paragraph("GOBIERNO AUTÓNOMO DESCENTRALIZADO DE LA PROVINCIA DE PICHINCHA", times12bold));
            headers.add(new Paragraph("GESTIÓN DE PRESUPUESTOS", times10bold));
            headers.add(new Paragraph("ANÁLISIS DE PRECIOS UNITARIOS", times10bold));
//            headers.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el " + new Date().format("dd/MM/yyyy hh:mm"), times8normal))
            addEmptyLine(headers, 1);
            document.add(headers);

            def id = rubro.id

//            def parametros = "" + id + "," + lugar.id + ",'" + fecha + "'," + obra.distanciaPeso + "," + obra.distanciaVolumen + "," + rendimientos["rdps"] + "," + rendimientos["rdvl"]
//            preciosService.ac_rbro(id, lugar.id, fecha)
//            def res = preciosService.rb_precios(parametros, "")

            def res = preciosService.presioUnitarioVolumenObra("* ", obra.id, id)

            PdfPTable headerRubroTabla = new PdfPTable(4); // 4 columns.
            headerRubroTabla.setWidthPercentage(100);
            headerRubroTabla.setWidths(arregloEnteros([10, 68, 12, 10]))

            addCellTabla(headerRubroTabla, new Paragraph("Fecha:", times8bold), prmsHeaderHoja)
            addCellTabla(headerRubroTabla, new Paragraph(new Date().format("dd-MM-yyyy"), times8normal), prmsHeaderHoja)

            addCellTabla(headerRubroTabla, new Paragraph(" ", times8bold), prmsHeaderHoja)
            addCellTabla(headerRubroTabla, new Paragraph(" ", times8normal), prmsHeaderHoja)


            addCellTabla(headerRubroTabla, new Paragraph("Presupuesto:", times8bold), prmsHeaderHoja)
            addCellTabla(headerRubroTabla, new Paragraph(obra.descripcion, times8normal), prmsHeaderHoja)

            addCellTabla(headerRubroTabla, new Paragraph("Fecha Act. PU:", times8bold), prmsHeaderHoja)
            addCellTabla(headerRubroTabla, new Paragraph(fecha.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja)


            addCellTabla(headerRubroTabla, new Paragraph("Código:", times8bold), prmsHeaderHoja)
            addCellTabla(headerRubroTabla, new Paragraph(rubro.codigo, times8normal), prmsHeaderHoja)

            addCellTabla(headerRubroTabla, new Paragraph("Unidad:", times8bold), prmsHeaderHoja)
            addCellTabla(headerRubroTabla, new Paragraph(rubro.unidad.codigo, times8normal), prmsHeaderHoja)


            addCellTabla(headerRubroTabla, new Paragraph("Descripción:", times8bold), prmsHeaderHoja)
            addCellTabla(headerRubroTabla, new Paragraph(rubro.nombre, times8normal), prmsHeaderHoja)

            addCellTabla(headerRubroTabla, new Paragraph(" ", times8bold), prmsHeaderHoja)
            addCellTabla(headerRubroTabla, new Paragraph(" ", times8normal), prmsHeaderHoja)

            PdfPTable tablaHerramientas = new PdfPTable(7);
            PdfPTable tablaManoObra = new PdfPTable(7);
            PdfPTable tablaMateriales = new PdfPTable(7);
            PdfPTable tablaTransporte = new PdfPTable(7);
            PdfPTable tablaIndirectos = new PdfPTable(3);
            PdfPTable tablaTotales = new PdfPTable(3);

            creaHeadersTabla(tablaHerramientas, fonts, prms, "Herramientas")
            creaHeadersTabla(tablaManoObra, fonts, prms, "Mano de obra")
            creaHeadersTabla(tablaMateriales, fonts, prms, "Materiales")
            if (params.transporte == "1") {
                creaHeadersTabla(tablaTransporte, fonts, prms, "Transporte")
            }
            creaHeadersTabla(tablaIndirectos, fonts, prms, "Costos Indirectos")

            def totalTrans = 0, totalHer = 0, totalMan = 0, totalMat = 0
            def totalRubro

            res.each { r ->
                if (r["grpocdgo"] == 3) {
                    llenaDatos(tablaHerramientas, r, fonts, prms, "H")
                    totalHer += r.parcial
//                    if (params.transporte != "1") {
//                        totalHer += r.parcial_t
//                    }
                }
                if (r["grpocdgo"] == 2) {
                    llenaDatos(tablaManoObra, r, fonts, prms, "O")
                    totalMan += r.parcial
//                    if (params.transporte != "1") {
//                        totalMan += r.parcial_t
//                    }
                }
                if (r["grpocdgo"] == 1) {
                    if (params.transporte == "1") {
                        llenaDatos(tablaMateriales, r, fonts, prms, "M")
                    } else {
                        llenaDatos(tablaMateriales, r, fonts, prms, "MNT")
                    }
                    totalMat += r.parcial
                    if (params.transporte != "1") {
                        totalMat += r.parcial_t
                    }
                }
                if (r["parcial_t"] > 0 && params.transporte == "1") {
                    llenaDatos(tablaTransporte, r, fonts, prms, "T")
                    totalTrans += r.parcial_t
                }
            }
            totalRubro = totalHer + totalMan + totalMat
            if (params.transporte == "1") {
                totalRubro += totalTrans
            }
            def totalIndi = totalRubro * (indi / 100)

            addSubtotal(tablaHerramientas, totalHer, fonts, prms)
            addSubtotal(tablaManoObra, totalMan, fonts, prms)
            addSubtotal(tablaMateriales, totalMat, fonts, prms)
            if (params.transporte == "1") {
                addSubtotal(tablaTransporte, totalTrans, fonts, prms)
            }

            addCellTabla(tablaIndirectos, new Paragraph("Costos Indirectos", fonts.times8bold), prmsCellLeft)
            addCellTabla(tablaIndirectos, new Paragraph(g.formatNumber(number: indi, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec") + "%", fonts.times8normal), prmsNum)
            addCellTabla(tablaIndirectos, new Paragraph(g.formatNumber(number: totalIndi, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), prmsNum)

            tablaTotales.setWidthPercentage(100);
            tablaTotales.setWidths(arregloEnteros([70, 20, 10]))

            addCellTabla(tablaTotales, new Paragraph(" ", fonts.times8bold), prmsHeaderHoja)
            addCellTabla(tablaTotales, new Paragraph("Costo unitario directo", fonts.times8bold), prmsCellLeft)
            addCellTabla(tablaTotales, new Paragraph(g.formatNumber(number: totalRubro, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), prmsNum)

            addCellTabla(tablaTotales, new Paragraph(" ", fonts.times8bold), prmsHeaderHoja)
            addCellTabla(tablaTotales, new Paragraph("Costos indirectos", fonts.times8bold), prmsCellLeft)
            addCellTabla(tablaTotales, new Paragraph(g.formatNumber(number: totalIndi, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), prmsNum)

            addCellTabla(tablaTotales, new Paragraph(" ", fonts.times8bold), prmsHeaderHoja)
            addCellTabla(tablaTotales, new Paragraph("Costos total del rubro", fonts.times8bold), prmsCellLeft)
            addCellTabla(tablaTotales, new Paragraph(g.formatNumber(number: totalRubro + totalIndi, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), prmsNum)

            addCellTabla(tablaTotales, new Paragraph(" ", fonts.times8bold), prmsHeaderHoja)
            addCellTabla(tablaTotales, new Paragraph("Precio unitario USD", fonts.times8bold), prmsCellLeft)
            addCellTabla(tablaTotales, new Paragraph(g.formatNumber(number: totalRubro + totalIndi, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), fonts.times8normal), prmsNum)



            PdfPTable pieTabla = new PdfPTable(2);
            pieTabla.setWidthPercentage(100);
            pieTabla.setWidths(arregloEnteros([30, 70]))


            addCellTabla(pieTabla, new Paragraph(" ", fonts.times8bold), prmsHeaderHoja)
            addCellTabla(pieTabla, new Paragraph("Parámetros para los datos de presupuesto obtenidos de la obra: " + obra?.nombre, fonts.times8bold), prmsHeaderHoja)

            addCellTabla(pieTabla, new Paragraph(" ", fonts.times8bold), prmsHeaderHoja)
            addCellTabla(pieTabla, new Paragraph("Nota: Los cálculos se hacen con todos los decimales y el resultado final se lo redondea a dos decimales.   ", fonts.times8bold), prmsHeaderHoja)


            addTablaHoja(document, headerRubroTabla, false)
            addTablaHoja(document, tablaHerramientas, false)
            addTablaHoja(document, tablaManoObra, false)
            addTablaHoja(document, tablaMateriales, false)
            addTablaHoja(document, tablaTransporte, false)
            addTablaHoja(document, tablaIndirectos, false)
            addTablaHoja(document, tablaTotales, true)
            addTablaHoja(document, pieTabla, false)

            document.newPage();
//            println res
        }

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)

    }

    def llenaDatos(PdfPTable table, r, fonts, params, tipo) {
        addCellTabla(table, new Paragraph(r.itemcdgo, fonts.times8normal), params.prmsCellLeft)
        addCellTabla(table, new Paragraph(r.itemnmbr, fonts.times8normal), params.prmsCellLeft)
        switch (tipo) {
            case "H":
            case "O":
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rbrocntd, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rbpcpcun, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rbpcpcun * r.rbrocntd, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rndm, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.parcial, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                break;
            case "M":
                addCellTabla(table, new Paragraph(r.unddcdgo, fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rbrocntd, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rbpcpcun, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph("", fonts.times8normal), params.prmsCell)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.parcial, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                break;
            case "MNT":
                addCellTabla(table, new Paragraph(r.unddcdgo, fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rbrocntd, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
//                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rbpcpcun, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: ((r.parcial + r.parcial_t)/r.rbrocntd), minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph("", fonts.times8normal), params.prmsCell)
                addCellTabla(table, new Paragraph(g.formatNumber(number: (r.parcial + r.parcial_t), minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                break;
            case "T":
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.itempeso, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rbrocntd, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.distancia, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.parcial_t / (r.itempeso * r.rbrocntd * r.distancia), minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.parcial_t, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8normal), params.prmsNum)
                break;
        }
    }

    def addSubtotal(PdfPTable table, subtotal, fonts, params) {
        addCellTabla(table, new Paragraph("Subtotal", fonts.times8bold), params.prmsSubtotal)
        addCellTabla(table, new Paragraph(g.formatNumber(number: subtotal, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0", locale: "ec"), fonts.times8bold), params.prmsNum)
    }

    def creaHeadersTabla(PdfPTable table, fonts, params, String tipo) {
        table.setWidthPercentage(100);
        if (tipo == "Costos Indirectos") {
            table.setWidths(arregloEnteros([70, 15, 15]))
            addCellTabla(table, new Paragraph(tipo, fonts.times10boldWhite), params.prmsHeader2)

            addCellTabla(table, new Paragraph("Descripción", fonts.times8boldWhite), params.prmsCellHead)
            addCellTabla(table, new Paragraph("Porcentaje", fonts.times8boldWhite), params.prmsCellHead)
            addCellTabla(table, new Paragraph("Valor", fonts.times8boldWhite), params.prmsCellHead)
        } else {
            if (tipo == "Materiales") {
                table.setWidths(arregloEnteros([10, 48, 8, 8, 8, 10, 8]))
                addCellTabla(table, new Paragraph(tipo, fonts.times10boldWhite), params.prmsHeader)

                addCellTabla(table, new Paragraph("Código", fonts.times8boldWhite), params.prmsCellHead)
                addCellTabla(table, new Paragraph("Descripción", fonts.times8boldWhite), params.prmsCellHead)
                addCellTabla(table, new Paragraph("Unidad", fonts.times8boldWhite), params.prmsCellHead)
                addCellTabla(table, new Paragraph("Cantidad", fonts.times8boldWhite), params.prmsCellHead)
                addCellTabla(table, new Paragraph("Tarifa", fonts.times8boldWhite), params.prmsCellHead)
                addCellTabla(table, new Paragraph("", fonts.times8boldWhite), params.prmsCellHead)
                addCellTabla(table, new Paragraph("C.Total", fonts.times8boldWhite), params.prmsCellHead)
            }else{
                table.setWidths(arregloEnteros([10, 48, 8, 8, 8, 10, 8]))
                addCellTabla(table, new Paragraph(tipo, fonts.times10boldWhite), params.prmsHeader)

                addCellTabla(table, new Paragraph("Código", fonts.times8boldWhite), params.prmsCellHead)
                addCellTabla(table, new Paragraph("Descripción", fonts.times8boldWhite), params.prmsCellHead)
                addCellTabla(table, new Paragraph("Cantidad", fonts.times8boldWhite), params.prmsCellHead)
                addCellTabla(table, new Paragraph("Tarifa", fonts.times8boldWhite), params.prmsCellHead)
                addCellTabla(table, new Paragraph("Costo", fonts.times8boldWhite), params.prmsCellHead)
                addCellTabla(table, new Paragraph("Rendimiento", fonts.times8boldWhite), params.prmsCellHead)
                addCellTabla(table, new Paragraph("C.Total", fonts.times8boldWhite), params.prmsCellHead)
            }

        }
    }

    def addTablaHoja(Document document, PdfPTable table, boolean right) {
        Paragraph paragraph = new Paragraph()
        if (right) {
            paragraph.setAlignment(Element.ALIGN_RIGHT);
        }
        paragraph.setSpacingAfter(10);
//        addEmptyLine(paragraph, 1);
        paragraph.add(table);
        document.add(paragraph);
    }

    def addCellTabla(PdfPTable table, Paragraph paragraph, params) {
        PdfPCell cell = new PdfPCell(paragraph);
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
        }
        table.addCell(cell);
    }



    def reporteRegistro() {

        def obra = Obra.get(params.id)

        def auxiliar = Auxiliar.get(1)


        def prmsHeaderHoja = [border: Color.WHITE]


        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]


        def prmsHeader = [border: Color.WHITE, colspan: 7, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]



        def prmsHeader2 = [border: Color.WHITE, colspan: 3, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenter = [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellRight = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT]
        def prmsCellLeft = [border: Color.BLACK, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.BLACK, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum, prmsHeaderHoja2: prmsHeaderHoja2, prmsCellRight: prmsCellRight]



        def baos = new ByteArrayOutputStream()
        def name = "presupuesto_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times10normal = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
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
        document.addTitle("Registro " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("documentosObra, janus, presupuesto");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");



        Paragraph headers = new Paragraph();
        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);
        headers.add(new Paragraph(auxiliar.titulo, times12bold));
        headers.add(new Paragraph(" ", times10bold));
//        headers.add(new Paragraph(" ", times10bold));
        headers.add(new Paragraph(obra?.departamento?.descripcion, times10bold));
        headers.add(new Paragraph(" ", times10bold));

        document.add(headers)


        PdfPTable tablaCoeficiente = new PdfPTable(3);
        tablaCoeficiente.setWidthPercentage(100);
        tablaCoeficiente.setWidths(arregloEnteros([30, 40, 30]))

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph("Obra: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(obra?.nombre, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph("Código: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(obra?.oficioIngreso, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)


        addCellTabla(tablaCoeficiente, new Paragraph("Descripción: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(obra?.descripcion, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph("Tipo de Obra: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(obra?.tipoObjetivo?.descripcion, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph("Programa: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(obra?.programacion?.descripcion, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph("Clase de Obra: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(obra?.claseObra?.descripcion, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph("Plazo: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(obra?.plazoEjecucionMeses + " Mes(es)" + " " + obra?.plazoEjecucionDias + " Días", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph("Documento de Referencia: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(obra?.oficioIngreso, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph("Oficio de Salida: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(obra?.oficioSalida, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)


        PdfPTable tablaDistancias = new PdfPTable(4);
        tablaDistancias.setWidthPercentage(100);
        tablaDistancias.setWidths(arregloEnteros([25, 25, 25, 25]))

        addCellTabla(tablaDistancias, new Paragraph("Listas de Precios al Peso", times12bold), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph("Distancias", times12bold), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(" ", times10bold), prmsHeaderHoja)

        addCellTabla(tablaDistancias, new Paragraph("Lista Cantón", times10bold), prmsHeaderHoja)
//        addCellTabla(tablaCoeficiente2, new Paragraph(g.formatNumber(number: obra?.lugar, format: "###.##", locale: "ec"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(obra?.lugar?.descripcion, times10normal), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph("Capital de Cantón ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(g.formatNumber(number: obra?.distanciaPeso, format: "###.##", locale: "ec"), times10normal), prmsHeaderHoja)
//        addCellTabla(tablaDistancias, new Paragraph(obra?.distanciaPeso, times10bold), prmsHeaderHoja)

        addCellTabla(tablaDistancias, new Paragraph("Lista Peso Especial", times10bold), prmsHeaderHoja)
//        addCellTabla(tablaCoeficiente2, new Paragraph(g.formatNumber(number: obra?.listaPeso1, format: "###.##", locale: "ec"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(obra?.listaPeso1?.descripcion, times10normal), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph("Distancia Peso Espeacial ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(g.formatNumber(number: obra?.distanciaPesoEspecial, format: "###.##", locale: "ec"), times10normal), prmsHeaderHoja)

//        addCellTabla(tablaDistancias, new Paragraph(obra?.distanciaPesoEspecial, times10bold), prmsHeaderHoja)


        addCellTabla(tablaDistancias, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(" ", times10normal), prmsHeaderHoja)


        addCellTabla(tablaDistancias, new Paragraph("Listas Volumen", times12bold), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph("Distancias", times12bold), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(" ", times10bold), prmsHeaderHoja)

        addCellTabla(tablaDistancias, new Paragraph("Lista Materiales Petreos Hormigones", times10bold), prmsHeaderHoja)
//        addCellTabla(tablaCoeficiente2, new Paragraph(g.formatNumber(number: obra?.listaVolumen0, format: "###.##", locale: "ec"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(obra?.listaVolumen0?.descripcion, times10normal), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph("Distancia Materiales Petreos Hormigones", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(g.formatNumber(number: obra?.distanciaVolumen, format: "###.##", locale: "ec"), times10normal), prmsHeaderHoja)
//        addCellTabla(tablaDistancias, new Paragraph(obra?.distanciaVolumen, times10bold), prmsHeaderHoja)

        addCellTabla(tablaDistancias, new Paragraph("Lista Materiales Mejoramiento", times10bold), prmsHeaderHoja)
//        addCellTabla(tablaCoeficiente2, new Paragraph(g.formatNumber(number: obra?.listaVolumen1, format: "###.##", locale: "ec"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(obra?.listaVolumen1?.descripcion, times10normal), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph("Distancia Materiales Mejoramiento", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(g.formatNumber(number: obra?.distanciaVolumenMejoramiento, format: "###.##", locale: "ec"), times10normal), prmsHeaderHoja)
//        addCellTabla(tablaDistancias, new Paragraph(obra?.distanciaVolumenMejoramiento, times10bold), prmsHeaderHoja)

        addCellTabla(tablaDistancias, new Paragraph("Lista Materiales Carpeta Asfáltica", times10bold), prmsHeaderHoja)
//        addCellTabla(tablaCoeficiente2, new Paragraph(g.formatNumber(number: obra?.listaVolumen2, format: "###.##", locale: "ec"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(obra?.listaVolumen2?.descripcion, times10normal), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph("Distancia Materiales Carpeta Asfáltica", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDistancias, new Paragraph(g.formatNumber(number: obra?.distanciaVolumenCarpetaAsfaltica, format: "###.##", locale: "ec"), times10normal), prmsHeaderHoja)
//        addCellTabla(tablaDistancias, new Paragraph(obra?.distanciaVolumenCarpetaAsfaltica, times10bold), prmsHeaderHoja)


        PdfPTable tablaCoeficiente2 = new PdfPTable(3);
        tablaCoeficiente2.setWidthPercentage(100);
        tablaCoeficiente2.setWidths(arregloEnteros([30, 40, 30]))

        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente2, new Paragraph("Ubicación", times12bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente2, new Paragraph("Cantón: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(obra?.parroquia?.canton?.nombre, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente2, new Paragraph("Parroquia: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(obra?.parroquia?.nombre, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente2, new Paragraph("Comunidad: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(obra?.comunidad?.nombre, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente2, new Paragraph("Barrio: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(obra?.barrio, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente2, new Paragraph("Sitio: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(obra?.sitio, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente2, new Paragraph("Latitud: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(g.formatNumber(number: obra?.latitud, format: "###.##", locale: "ec"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente2, new Paragraph("Longitud: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(g.formatNumber(number: obra?.longitud, format: "###.##", locale: "ec"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)



        addCellTabla(tablaCoeficiente2, new Paragraph("Lugar de Referencia de Precios: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(obra?.lugar?.descripcion, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente2, new Paragraph("Datos Generales", times12bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente2, new Paragraph("Inspector: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(obra?.inspector?.nombre + " " + obra?.inspector?.apellido, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente2, new Paragraph("Revisor: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(obra?.revisor?.nombre + " " + obra?.revisor?.apellido, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente2, new Paragraph("Responsable: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)


        addCellTabla(tablaCoeficiente2, new Paragraph("Fecha de Registro: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(formatDate(date: obra?.fechaCreacionObra, format: "yyyy-MM-dd"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente2, new Paragraph("Observaciones: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(obra?.observaciones, times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente2, new Paragraph(" ", times10normal), prmsHeaderHoja)

        document.add(tablaCoeficiente)
        document.add(tablaDistancias)
        document.add(tablaCoeficiente2)


        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)


    }

    //reporte registroTramite

    def reporteRegistroTramite() {

//        println("--->" + params)

        def usuario = session.usuario

        def tramites = PersonasTramite.withCriteria {
            eq("persona", usuario)
            ne("rolTramite", RolTramite.findByCodigo("CC"))
            tramite{
                ne("estado",EstadoTramite.findByCodigo('F'))
            }
        }.tramite.unique()



        def prmsHeaderHoja = [border: Color.WHITE]


        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]


        def prmsHeader = [border: Color.WHITE, colspan: 7, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]



        def prmsHeader2 = [border: Color.WHITE, colspan: 3, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHeadRight = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenter = [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellRight = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT]
        def prmsCellLeft = [border: Color.BLACK, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.BLACK, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum, prmsHeaderHoja2: prmsHeaderHoja2, prmsCellRight: prmsCellRight, prmsCellHeadRight: prmsCellHeadRight]



        def baos = new ByteArrayOutputStream()
        def name = "registroTramite_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        def fonts = [times12bold: times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal]

        Document document
        document = new Document(PageSize.A4);
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();
        document.addTitle("Presupuesto " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("tramite, janus, registroTramite");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        Paragraph headers = new Paragraph();
        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);

        headers.add(new Paragraph("Gobierno Autónomo Descentralizado de la Provincia de Pichincha"))
        headers.add(new Paragraph(" "))
        headers.add(new Paragraph("TRÁMITES EN PROCESO"))
        headers.add(new Paragraph(" "))
        headers.add(new Paragraph("Quito, " + formatDate(date: new Date(), format: "dd-MM-yyyy"), times10bold));
        headers.add(new Paragraph(" ", times10bold));

        PdfPTable tablaVolObra = new PdfPTable(8);
        tablaVolObra.setWidthPercentage(100);


        tablaVolObra.setWidths(arregloEnteros([10, 20, 20, 14, 9, 12, 8, 7]))

        addCellTabla(tablaVolObra, new Paragraph("Tipo", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("Envia", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("Recibe", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("Asunto", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("Fecha", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("Documentos", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("N° Memo SAD", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("Estado", times8bold), prmsCellHead)


        tramites.each {

            addCellTabla(tablaVolObra, new Paragraph(it?.tipoTramite?.descripcion, times8normal), prmsCellCenter)

            addCellTabla(tablaVolObra, new Paragraph(PersonasTramite.findByRolTramiteAndTramite(RolTramite.findByCodigo('DE'), it).persona.nombre + " " + PersonasTramite.findByRolTramiteAndTramite(RolTramite.findByCodigo('DE'), it).persona.apellido, times8normal), prmsCellLeft)

            addCellTabla(tablaVolObra, new Paragraph(PersonasTramite.findByRolTramiteAndTramite(RolTramite.findByCodigo('PARA'), it).persona.nombre + " " + PersonasTramite.findByRolTramiteAndTramite(RolTramite.findByCodigo('PARA'), it).persona.apellido , times8normal), prmsCellLeft)

            addCellTabla(tablaVolObra, new Paragraph(it?.descripcion, times8normal), prmsCellCenter)

            addCellTabla(tablaVolObra, new Paragraph(formatDate(date: it?.fecha, format: "dd-MM-yyyy"), times8normal), prmsCellCenter)

            addCellTabla(tablaVolObra, new Paragraph(it?.documentosAdjuntos, times8normal), prmsCellCenter)

            addCellTabla(tablaVolObra, new Paragraph(it?.memo, times8normal), prmsCellCenter)

            addCellTabla(tablaVolObra, new Paragraph(it?.estado?.codigo, times8normal), prmsCellCenter)

        }

        document.add(headers)
        document.add(tablaVolObra)


        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)


    }

     ///////



    //reporte registroTramite

    def reporteRegistroTramitexObra() {


//        println("params" + params)

        def obra = Obra.get(params.idObra)

        def usuario = session.usuario


        def tramites = Tramite.findAllByObra(obra)

        def prmsHeaderHoja = [border: Color.WHITE]


        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]


        def prmsHeader = [border: Color.WHITE, colspan: 7, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]



        def prmsHeader2 = [border: Color.WHITE, colspan: 3, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHeadRight = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenter = [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellRight = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT]
        def prmsCellLeft = [border: Color.BLACK, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.BLACK, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum, prmsHeaderHoja2: prmsHeaderHoja2, prmsCellRight: prmsCellRight, prmsCellHeadRight: prmsCellHeadRight]



        def baos = new ByteArrayOutputStream()
        def name = "registroTramite_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
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
        document.addTitle("Presupuesto " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("tramite, janus, registroTramite");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        Paragraph headers = new Paragraph();
        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);

        headers.add(new Paragraph("Gobierno Autónomo Descentralizado de la Provincia de Pichincha"))
        headers.add(new Paragraph(" "))
        headers.add(new Paragraph("TRÁMITES POR OBRA"))
        headers.add(new Paragraph(" "))
        headers.add(new Paragraph("Quito, " + formatDate(date: new Date(), format: "dd-MM-yyyy"), times10bold));
//        headers.add(new Paragraph(" ", times10bold));


        Paragraph txtObra = new Paragraph();
        addEmptyLine(txtObra, 1);
        txtObra.setAlignment(Element.ALIGN_LEFT);

        txtObra.add(new Paragraph("Obra: " + obra?.nombre, times10normal))
        txtObra.add(new Paragraph("Código: " + obra?.codigo , times10normal))
        txtObra.add(new Paragraph("Estado de la Obra: " + obra?.estadoObra , times10normal))
        txtObra.add(new Paragraph("Departamento: " + obra?.departamento , times10normal))
        txtObra.add(new Paragraph(" "))

        PdfPTable tablaVolObra = new PdfPTable(8);
        tablaVolObra.setWidthPercentage(100);


        tablaVolObra.setWidths(arregloEnteros([10, 20, 20, 14, 9, 12, 8, 7]))

        addCellTabla(tablaVolObra, new Paragraph("Tipo", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("Envia", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("Recibe", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("Asunto", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("Fecha", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("Documentos", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("N° Memo SAD", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("Estado", times8bold), prmsCellHead)


        tramites.each {

            addCellTabla(tablaVolObra, new Paragraph(it?.tipoTramite?.descripcion, times8normal), prmsCellCenter)

            addCellTabla(tablaVolObra, new Paragraph(PersonasTramite.findByRolTramiteAndTramite(RolTramite.findByCodigo('DE'), it).persona.nombre + " " + PersonasTramite.findByRolTramiteAndTramite(RolTramite.findByCodigo('DE'), it).persona.apellido, times8normal), prmsCellLeft)

            addCellTabla(tablaVolObra, new Paragraph(PersonasTramite.findByRolTramiteAndTramite(RolTramite.findByCodigo('PARA'), it).persona.nombre + " " + PersonasTramite.findByRolTramiteAndTramite(RolTramite.findByCodigo('PARA'), it).persona.apellido , times8normal), prmsCellLeft)

            addCellTabla(tablaVolObra, new Paragraph(it?.descripcion, times8normal), prmsCellCenter)

            addCellTabla(tablaVolObra, new Paragraph(formatDate(date: it?.fecha, format: "dd-MM-yyyy"), times8normal), prmsCellCenter)

            addCellTabla(tablaVolObra, new Paragraph(it?.documentosAdjuntos, times8normal), prmsCellCenter)

            addCellTabla(tablaVolObra, new Paragraph(it?.memo, times8normal), prmsCellCenter)

            addCellTabla(tablaVolObra, new Paragraph(it?.estado?.codigo, times8normal), prmsCellCenter)

        }

        document.add(headers)
        document.add(txtObra)
        document.add(tablaVolObra)


        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)


    }





    def reporteDocumentosObra() {

//        println("--->" + params)

        def cd

        def auxiliar = Auxiliar.get(1);


        def nota

        if (params.notaValue !=  'null'){



            nota = Nota.get(params.notaValue)

        }else{


            nota = new Nota();
        }


        def obra = Obra.get(params.id)

        def paux = Parametros.get(1);


        def ivaTotal = 0

        def proyeccionTotal = 0

        def presupuestoTotal = 0;

        def inflacion = 0;

        def meses = 0;

        def firma

        def firmas

        def cuenta = 0;

        def cantidadMeses = params.meses


//        println(obra.revisor.nombre);
//        println(obra.inspector.nombre);
//        println(obra.responsableObra.nombre);


        def firma1 = obra?.responsableObra;
        def firma2 = obra?.revisor

//        firma = params.firmasId.split(",")

        if (params.firmasId.trim().size() > 0) {
            firma = params.firmasId.split(",")
        } else {
            firma = []
        }


        cuenta = firma.size()




        def prmsHeaderHoja = [border: Color.WHITE]


        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]


        def prmsHeader = [border: Color.WHITE, colspan: 7, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]



        def prmsHeader2 = [border: Color.WHITE, colspan: 3, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHeadRight = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenter = [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellRight = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT]
        def prmsCellLeft = [border: Color.BLACK, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.BLACK, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum, prmsHeaderHoja2: prmsHeaderHoja2, prmsCellRight: prmsCellRight, prmsCellHeadRight: prmsCellHeadRight]



        def baos = new ByteArrayOutputStream()
        def name = "presupuesto_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        def fonts = [times12bold: times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal]

        Document document
        document = new Document(PageSize.A4);
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();
        document.addTitle("Presupuesto " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("documentosObra, janus, presupuesto");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");



        Paragraph headers = new Paragraph();
        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);
        headers.add(new Paragraph(auxiliar.titulo, times12bold));

        if (obra?.oficioSalida == null) {

            headers.add(new Paragraph("Oficio N°:" + " ", times10bold));

        } else {

            headers.add(new Paragraph("Oficio N°:" + obra?.oficioSalida, times10bold));

        }

        headers.add(new Paragraph("Quito, " + formatDate(date: obra?.fechaOficioSalida, format: "dd-MM-yyyy"), times10bold));



        Paragraph txtIzq = new Paragraph();
        addEmptyLine(txtIzq, 1);
        txtIzq.setAlignment(Element.ALIGN_LEFT);
        txtIzq.add(new Paragraph(auxiliar?.general, times10bold));

        if (params.tipoReporte == '1') {


            txtIzq.add(new Paragraph(auxiliar?.baseCont, times10bold));

        }
        if (params.tipoReporte == '2') {

            txtIzq.add(new Paragraph(auxiliar?.presupuestoRef, times10bold));


        }



        addEmptyLine(headers, 1);
        document.add(headers);
        document.add(txtIzq);


        PdfPTable tablaPresupuesto = new PdfPTable(4);
        tablaPresupuesto.setWidthPercentage(100);



        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Memo Cant. Obra :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.memoCantidadObra, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Nombre :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.nombre, times8normal), prmsHeaderHoja)


        addCellTabla(tablaPresupuesto, new Paragraph("Fórmula :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.formulaPolinomica, times8normal), prmsHeaderHoja)


        addCellTabla(tablaPresupuesto, new Paragraph("Tipo de Obra :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.tipoObjetivo?.descripcion, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Documento de Referencia :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.oficioIngreso, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Sitio :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.sitio, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Cantón :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.parroquia?.canton?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Parroquia :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.parroquia?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Comunidad :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.comunidad?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Referencia :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.referencia, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Latitud :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(g.formatNumber(number: obra?.latitud, minFractionDigits:
                1, maxFractionDigits: 5, format: "#####.##", locale: "ec"), times8normal), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("Longitud :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(g.formatNumber(number: obra?.longitud, minFractionDigits:
                1, maxFractionDigits: 5, format: "#####.##", locale: "ec"), times8normal), prmsHeaderHoja)


        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold), prmsHeaderHoja)


        PdfPTable tablaVolObra = new PdfPTable(6);
        tablaVolObra.setWidthPercentage(100);


        PdfPTable tablaTotalSub = new PdfPTable(6);
        tablaVolObra.setWidthPercentage(100);

        def detalle

        detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        preciosService.ac_rbroObra(obra.id)


        def precios = [:]

        def indirecto = obra.totales / 100

        def c;

        def total1 = 0;
        def total2 = 0;

        def totales

        def totalPresupuesto = 0;
        def totalPrueba = 0
        def totalPrueba1 = 0

        def valores = preciosService.rbro_pcun_v2(obra.id)

        def subPres = VolumenesObra.findAllByObra(obra,[sort:"orden"]).subPresupuesto.unique()

        tablaVolObra.setWidths(arregloEnteros([10, 40, 8, 12, 15, 15]))


        subPres.each { s->

            total2 = 0

        addCellTabla(tablaVolObra, new Paragraph("Presupuesto:", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph(s.descripcion, times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("", times8bold), prmsCellHead)

        addCellTabla(tablaVolObra, new Paragraph("Rubro N°", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("Descripción", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("Unidad", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("Cantidad", times8bold), prmsCellHead)
        addCellTabla(tablaVolObra, new Paragraph("P. Unitario", times8bold), prmsCellHead)

        addCellTabla(tablaVolObra, new Paragraph("Costo Total", times8bold), prmsCellHead)


            valores.each {


               if(it.sbprdscr == s.descripcion){




                addCellTabla(tablaVolObra, new Paragraph(it.rbrocdgo, times8normal), prmsCellCenter)


                addCellTabla(tablaVolObra, new Paragraph(it.rbronmbr, times8normal), prmsCellLeft)


                addCellTabla(tablaVolObra, new Paragraph(it.unddcdgo, times8normal), prmsCellCenter)


                addCellTabla(tablaVolObra, new Paragraph(g.formatNumber(number: it.vlobcntd, minFractionDigits:
                        2, maxFractionDigits: 2, format: "###,###0", locale: "ec"), times8normal), prmsCellRight)

                addCellTabla(tablaVolObra, new Paragraph(g.formatNumber(number: it.pcun, minFractionDigits:
                        2, maxFractionDigits: 2, format: "###,###0", locale: "ec"), times8normal), prmsCellRight)

                addCellTabla(tablaVolObra, new Paragraph(g.formatNumber(number: it.totl, minFractionDigits:
                        2, maxFractionDigits: 2, format: "###,###0", locale: "ec"), times8normal), prmsCellRight)

                totales =  it.totl

                totalPresupuesto = (total1 += totales);


                   totalPrueba = total2 += totales



               }else{
               }


            }


            addCellTabla(tablaVolObra, new Paragraph("", times8bold), prmsCellHead)
            addCellTabla(tablaVolObra, new Paragraph("Total Subpresupuesto:", times8bold), prmsCellHead)
            addCellTabla(tablaVolObra, new Paragraph("", times8bold), prmsCellHead)
            addCellTabla(tablaVolObra, new Paragraph("", times8bold), prmsCellHead)
            addCellTabla(tablaVolObra, new Paragraph("", times8bold), prmsCellHead)
            addCellTabla(tablaVolObra, new Paragraph(g.formatNumber(number: totalPrueba, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8bold), prmsCellRight)




        }

//        detalle.each {
//
////            preciosService.ac_rbro(it.item.id,lugar.id,fecha.format("yyyy-MM-dd"))
////            def res = preciosService.rb_precios("sum(parcial)+sum(parcial_t) precio ",parametros,"")
//            def res = preciosService.presioUnitarioVolumenObra("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
//            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
////            precios.put(it.id.toString(),res["precio"][0]+res["precio"][0]*indirecto)
//
//
//            addCellTabla(tablaVolObra, new Paragraph(it?.item?.codigo, times8normal), prmsCellCenter)
//
//
//            addCellTabla(tablaVolObra, new Paragraph(it?.item?.nombre, times8normal), prmsCellLeft)
//
//
//            addCellTabla(tablaVolObra, new Paragraph(it?.item?.unidad?.codigo, times8normal), prmsCellCenter)
//
//
//            addCellTabla(tablaVolObra, new Paragraph(g.formatNumber(number: it?.cantidad, minFractionDigits:
//                    2, maxFractionDigits: 2, format: "###,###", locale: "ec"), times8normal), prmsCellRight)
//
//            addCellTabla(tablaVolObra, new Paragraph(g.formatNumber(number: precios[it.id.toString()], minFractionDigits:
//                    2, maxFractionDigits: 2, format: "###,###", locale: "ec"), times8normal), prmsCellRight)
//
//            addCellTabla(tablaVolObra, new Paragraph(g.formatNumber(number: precios[it.id.toString()] * it.cantidad, minFractionDigits:
//                    2, maxFractionDigits: 2, format: "###,###", locale: "ec"), times8normal), prmsCellRight)
//
//            totales = precios[it.id.toString()] * it.cantidad
//
//            totalPresupuesto = (total1 += totales);
//
////
//
//        }


        //nuevo

//        valores.each {
//
//
//            addCellTabla(tablaVolObra, new Paragraph(it.rbrocdgo, times8normal), prmsCellCenter)
//
//
//            addCellTabla(tablaVolObra, new Paragraph(it.rbronmbr, times8normal), prmsCellLeft)
//
//
//            addCellTabla(tablaVolObra, new Paragraph(it.unddcdgo, times8normal), prmsCellCenter)
//
//
//            addCellTabla(tablaVolObra, new Paragraph(g.formatNumber(number: it.vlobcntd, minFractionDigits:
//                    2, maxFractionDigits: 2, format: "###,###0", locale: "ec"), times8normal), prmsCellRight)
//
//            addCellTabla(tablaVolObra, new Paragraph(g.formatNumber(number: it.pcun, minFractionDigits:
//                    2, maxFractionDigits: 2, format: "###,###0", locale: "ec"), times8normal), prmsCellRight)
//
//            addCellTabla(tablaVolObra, new Paragraph(g.formatNumber(number: it.totl, minFractionDigits:
//                    2, maxFractionDigits: 2, format: "###,###0", locale: "ec"), times8normal), prmsCellRight)
//
//            totales =  it.totl
//
//            totalPresupuesto = (total1 += totales);
//
//
//        }



//        println("TP" + totalPresupuesto)


        PdfPTable tablaTotal = new PdfPTable(6);
        tablaTotal.setWidthPercentage(100);

        tablaTotal.setWidths(arregloEnteros([85, 0, 0, 0, 0, 15]))


        addCellTabla(tablaTotal, new Paragraph("Total del presupuesto: ", times8bold), prmsCellHeadRight)
        addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
        addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
        addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
        addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
        addCellTabla(tablaTotal, new Paragraph(g.formatNumber(number: totalPresupuesto, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8bold), prmsCellRight)



        //solo IVA
        if(params.iva == 'true' && params.proyeccion == 'false'){

//            println("entroIva")
//
//            println(paux?.iva)
//            println(totalPresupuesto)

           ivaTotal = (totalPresupuesto * paux?.iva)/100
           presupuestoTotal = totalPresupuesto + ivaTotal;


            addCellTabla(tablaTotal, new Paragraph("IVA " + paux?.iva + "% : " , times8bold), prmsCellHeadRight)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(g.formatNumber(number: paux?.iva, format: "####.##", locale: "ec"), times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(g.formatNumber(number: ivaTotal, format: "###,###", locale: "ec",maxFractionDigits: 2, minFractionDigits: 2), times8bold), prmsCellRight)


            addCellTabla(tablaTotal, new Paragraph("Presupuesto Total: ", times8bold), prmsCellHeadRight)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(g.formatNumber(number: presupuestoTotal, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8bold), prmsCellRight)


        }
        //solo Proyeccion del reajuste

        if(params.proyeccion == 'true' && cantidadMeses.toInteger() >= 1 && params.iva == 'false'){

//            println("entroProyeccion")
//
//            println(paux?.inflacion)
//            println(totalPresupuesto)

            inflacion = paux.inflacion
            meses = params.meses

            proyeccionTotal = (totalPresupuesto*((inflacion/1200)*meses.toInteger()))
            presupuestoTotal = totalPresupuesto + proyeccionTotal;


            addCellTabla(tablaTotal, new Paragraph("Proyeccion del Reajuste (período: " + meses + " meses, inflación: " + inflacion + " % ) :", times8bold), prmsCellHeadRight)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(g.formatNumber(number: paux?.inflacion, format: "####.##", locale: "ec"), times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(g.formatNumber(number: proyeccionTotal, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8bold), prmsCellRight)



            addCellTabla(tablaTotal, new Paragraph("Presupuesto Total: ", times8bold), prmsCellHeadRight)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(g.formatNumber(number: presupuestoTotal, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8bold), prmsCellRight)


        }

        if(params.proyeccion == 'true' && cantidadMeses.toInteger() >= 1 && params.iva == 'true'){

//            println("entroProyeccion + Iva")
//
//            println(paux?.inflacion)
//            println(totalPresupuesto)

            inflacion = paux.inflacion
            meses = params.meses

            proyeccionTotal = (totalPresupuesto*((inflacion/1200)*meses.toInteger()))


            ivaTotal = ((totalPresupuesto + proyeccionTotal)*paux?.iva)/100;

            presupuestoTotal = ((totalPresupuesto + proyeccionTotal) + ivaTotal)

            addCellTabla(tablaTotal, new Paragraph("Proyeccion del Reajuste (período: " + meses + " meses, inflación: " + inflacion + " % ) :", times8bold), prmsCellHeadRight)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(g.formatNumber(number: paux?.inflacion, format: "####.##", locale: "ec"), times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(g.formatNumber(number: proyeccionTotal, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8bold), prmsCellRight)


            addCellTabla(tablaTotal, new Paragraph("IVA " + paux?.iva + "% : " , times8bold), prmsCellHeadRight)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(g.formatNumber(number: paux?.iva, format: "###,###", locale: "ec"), times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(g.formatNumber(number: ivaTotal, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8bold), prmsCellRight)



            addCellTabla(tablaTotal, new Paragraph("Presupuesto Total: ", times8bold), prmsCellHeadRight)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead)
            addCellTabla(tablaTotal, new Paragraph(g.formatNumber(number: presupuestoTotal, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8bold), prmsCellRight)


        }

        Paragraph txtCondiciones = new Paragraph();
        addEmptyLine(txtCondiciones, 1);
        txtCondiciones.setAlignment(Element.ALIGN_LEFT);
        txtCondiciones.add(new Paragraph("CONDICIONES DEL CONTRATO", times10bold));


        PdfPTable tablaCondiciones = new PdfPTable(2);
        tablaCondiciones.setWidthPercentage(100);

        addCellTabla(tablaCondiciones, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaCondiciones, new Paragraph(" ", times8bold), prmsHeaderHoja)


        addCellTabla(tablaCondiciones, new Paragraph("Plazo de Ejecución :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaCondiciones, new Paragraph(g.formatNumber(number: obra?.plazo, format: "##", locale: "ec") + " mes(es)", times8normal), prmsHeaderHoja)

        addCellTabla(tablaCondiciones, new Paragraph("Anticipo :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaCondiciones, new Paragraph(g.formatNumber(number: obra?.porcentajeAnticipo, format: "###", locale: "ec") + " %", times8normal), prmsHeaderHoja)

        addCellTabla(tablaCondiciones, new Paragraph("Elaboró :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaCondiciones, new Paragraph(obra?.inspector?.nombre + " " + obra?.inspector?.apellido, times8normal), prmsHeaderHoja)

        document.add(tablaPresupuesto)
        document.add(tablaVolObra)
        document.add(tablaTotal);
        document.add(txtCondiciones);
        document.add(tablaCondiciones)



        if (params.forzarValue == '1') {


            document.newPage();



            Paragraph headerForzar = new Paragraph();
            addEmptyLine(headerForzar, 1);
            headerForzar.setAlignment(Element.ALIGN_CENTER);
            headerForzar.add(new Paragraph(auxiliar.titulo, times12bold));




            if (obra?.oficioSalida == null) {

                headerForzar.add(new Paragraph("Oficio N°:" + " ", times10bold));

            } else {

                headerForzar.add(new Paragraph("Oficio N°:" + obra?.oficioSalida, times10bold));

            }

            headerForzar.add(new Paragraph(" ", times12bold));
            headerForzar.add(new Paragraph(" ", times12bold));


            PdfPTable tablaRetenciones = new PdfPTable(2);
            tablaRetenciones.setWidthPercentage(100);

            addCellTabla(tablaRetenciones, new Paragraph(" ", times8bold), prmsHeaderHoja)
            addCellTabla(tablaRetenciones, new Paragraph(" ", times8bold), prmsHeaderHoja)

            addCellTabla(tablaRetenciones, new Paragraph("RETENCIONES", times8bold), prmsHeaderHoja)
            addCellTabla(tablaRetenciones, new Paragraph(auxiliar?.retencion, times8normal), prmsHeaderHoja)

            addCellTabla(tablaRetenciones, new Paragraph("NOTAS", times8bold), prmsHeaderHoja)


            addCellTabla(tablaRetenciones, new Paragraph(nota?.texto, times8normal), prmsHeaderHoja)

            addCellTabla(tablaRetenciones, new Paragraph(" ", times8bold), prmsHeaderHoja)
            addCellTabla(tablaRetenciones, new Paragraph(auxiliar?.notaAuxiliar, times8normal), prmsHeaderHoja)

            addCellTabla(tablaRetenciones, new Paragraph(" ", times8bold), prmsHeaderHoja)
            addCellTabla(tablaRetenciones, new Paragraph(nota?.adicional, times8normal), prmsHeaderHoja)


            Paragraph txtRetenciones = new Paragraph();
            addEmptyLine(txtRetenciones, 1);
            txtRetenciones.setAlignment(Element.ALIGN_LEFT);
            txtRetenciones.add(new Paragraph("Atentamente, ", times8bold));


            document.add(headerForzar);
            document.add(tablaRetenciones);
            document.add(txtRetenciones);


        } else {


            PdfPTable tablaRetenciones = new PdfPTable(2);
            tablaRetenciones.setWidthPercentage(100);

            addCellTabla(tablaRetenciones, new Paragraph(" ", times8bold), prmsHeaderHoja)
            addCellTabla(tablaRetenciones, new Paragraph(" ", times8bold), prmsHeaderHoja)

            addCellTabla(tablaRetenciones, new Paragraph("RETENCIONES", times8bold), prmsHeaderHoja)
            addCellTabla(tablaRetenciones, new Paragraph(auxiliar?.retencion, times8normal), prmsHeaderHoja)

            addCellTabla(tablaRetenciones, new Paragraph("NOTAS", times8bold), prmsHeaderHoja)
            addCellTabla(tablaRetenciones, new Paragraph(nota?.texto, times8normal), prmsHeaderHoja)

            addCellTabla(tablaRetenciones, new Paragraph(" ", times8bold), prmsHeaderHoja)
            addCellTabla(tablaRetenciones, new Paragraph(auxiliar?.notaAuxiliar, times8normal), prmsHeaderHoja)

            addCellTabla(tablaRetenciones, new Paragraph(" ", times8bold), prmsHeaderHoja)
            addCellTabla(tablaRetenciones, new Paragraph(nota?.adicional, times8normal), prmsHeaderHoja)


            Paragraph txtRetenciones = new Paragraph();
            addEmptyLine(txtRetenciones, 1);
            txtRetenciones.setAlignment(Element.ALIGN_LEFT);
            txtRetenciones.add(new Paragraph("Atentamente, ", times8bold));

            document.add(tablaRetenciones);
            document.add(txtRetenciones);

        }


        if (cuenta == 0) {


            PdfPTable tablaFirmas = new PdfPTable(2);
            tablaFirmas.setWidthPercentage(100);


            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)


            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)


            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)



            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)



            addCellTabla(tablaFirmas, new Paragraph(firma1?.titulo + "" + firma1?.nombre + " " + firma1?.apellido, times8bold), prmsHeaderHoja)



            addCellTabla(tablaFirmas, new Paragraph(firma2?.titulo + "" + firma2?.nombre + " " + firma2?.apellido, times8bold), prmsHeaderHoja)



            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.cargo, times8bold), prmsHeaderHoja)


            addCellTabla(tablaFirmas, new Paragraph("Responsable de la Obra", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("Revisor de la Obra", times8bold), prmsHeaderHoja)




            document.add(tablaFirmas);


        }


        if (cuenta == 1) {


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
            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)


            addCellTabla(tablaFirmas, new Paragraph(firma1?.titulo + "" + firma1?.nombre + " " + firma1?.apellido, times8bold), prmsHeaderHoja)



            addCellTabla(tablaFirmas, new Paragraph(firma2?.titulo + "" + firma2?.nombre + " " + firma2?.apellido, times8bold), prmsHeaderHoja)



            firma.each { f ->


                firmas = Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.titulo + " " + firmas?.nombre + " " + firmas?.apellido, times8bold), prmsHeaderHoja)

            }



            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.cargo, times8bold), prmsHeaderHoja)





            firma.each { f ->


                firmas = Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.cargo, times8bold), prmsHeaderHoja)


            }


            addCellTabla(tablaFirmas, new Paragraph("Responsable de la Obra", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("Revisor de la Obra", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("", times8bold), prmsHeaderHoja)


            document.add(tablaFirmas);


        }
        if (cuenta == 2) {


            PdfPTable tablaFirmas = new PdfPTable(4);
            tablaFirmas.setWidthPercentage(100);

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph("_______________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("_______________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("_______________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("_______________________________", times8bold), prmsHeaderHoja)



            addCellTabla(tablaFirmas, new Paragraph(firma1?.titulo + "" + firma1?.nombre + " " + firma1?.apellido, times8bold), prmsHeaderHoja)




            addCellTabla(tablaFirmas, new Paragraph(firma2?.titulo + "" + firma2?.nombre + " " + firma2?.apellido, times8bold), prmsHeaderHoja)


            firma.each { f ->


                firmas = Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.titulo + "" + firmas?.nombre + " " + firmas?.apellido, times8bold), prmsHeaderHoja)

            }



            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.cargo, times8bold), prmsHeaderHoja)


            firma.each { f ->


                firmas = Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.cargo, times8bold), prmsHeaderHoja)


            }

            document.add(tablaFirmas);


        } else {


        }

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)


    }


    def reporteDocumentosObraMemo() {

//        println("--->>" + params)

        def cd

        def auxiliar = Auxiliar.get(1)

        def auxiliarFijo = Auxiliar.get(1)

        def obra = Obra.get(params.id)

        def paux = Parametros.get(1);

        def firma

        def firmas

        def cuenta = 0;

        def firma1 = obra?.responsableObra;

        def firma2 = obra?.revisor;

        def totalBase = params.totalPresupuesto

//        def reajusteBase = params.reajusteMemo

        def tipo = params.tipoReporte

        def usuario = session.usuario.id

        def persona = Persona.get(usuario)

        def ivaTotal

        def valorTotal

        def mesesMemo

        def inflacion

        def proyeccionTotalMemo

        def cantidadMesesMemo = params.reajusteMesesMemo


        if (obra?.oficioSalida == null) {


            obra?.oficioSalida = "";

        }


//        if (reajusteBase == "") {
//
//
//            reajusteBase = 0;
//
//        } else {
//
//            reajusteBase = params.reajusteMemo
//
//        }
//
//
//

        if (totalBase == "") {


            totalBase = 0;

        } else {

            totalBase = params.totalPresupuesto

        }

//

//        def valorTotal = (totalBase.toDouble() + reajusteBase.toDouble())


        if (params.firmasIdMemo.trim().size() > 0) {
            firma = params.firmasIdMemo.split(",")
        } else {
            firma = []
        }

//        println("firma:" + firma)
//        println("firma:" + firma.size())
//
//        firma.each { f->
//            println ":........................."
//            println "**"+f+"**"
//            println f.class
//            println ":........................."
//        }

        cuenta = firma.size()

//            println("cuenta:" + cuenta)

//            return cuenta

//        }


        def prmsHeaderHoja = [border: Color.WHITE]

        def prmsHeaderHojaRight = [border: Color.WHITE, align: Element.ALIGN_RIGHT]

        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]


        def prmsHeader = [border: Color.WHITE, colspan: 7, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]



        def prmsHeader2 = [border: Color.WHITE, colspan: 3, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenter = [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellRight = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT]
        def prmsCellLeft = [border: Color.BLACK, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.BLACK, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum, prmsHeaderHoja2: prmsHeaderHoja2, prmsCellRight: prmsCellRight]



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
        document.addTitle("Memorando " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("documentosObra, janus, presupuesto");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");



        Paragraph headers = new Paragraph();
        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);
        headers.add(new Paragraph(auxiliar.titulo, times12bold));

        headers.add(new Paragraph(" ", times12bold));


        headers.add(new Paragraph("MEMORANDO"))



        Paragraph txtIzq = new Paragraph();
        addEmptyLine(txtIzq, 1);
        txtIzq.setAlignment(Element.ALIGN_LEFT);

        if (obra?.memoSalida == null) {
            txtIzq.add(new Paragraph("Memo N°: " + " ", times10bold));

        } else {

            txtIzq.add(new Paragraph("Memo N°: " + obra?.memoSalida, times10bold));

        }

//        txtIzq.add(new Paragraph("Quito," + obra?.fechaCreacionObra, times10bold));
        txtIzq.add(new Paragraph("Quito, " + formatDate(date: obra?.fechaCreacionObra, format: "dd-MM-yyyy"), times10bold));
//
//        if (obra?.claseObra?.tipo == 'C') {
//
//            txtIzq.add(new Paragraph("DE : Dpto Infraestructura Comunitaria ", times10bold));
//        }
//
//
//        if (obra?.claseObra?.tipo == 'V') {
//
//            txtIzq.add(new Paragraph("DE : Dpto de Estudios Viales", times10bold));
//        }


        txtIzq.add(new Paragraph("DE : " + persona?.departamento?.descripcion, times10bold));


        if (obra?.departamento?.descripcion == null) {

            txtIzq.add(new Paragraph("PARA : " + " ", times10bold));

        } else {

            txtIzq.add(new Paragraph("PARA : " + obra?.departamento?.descripcion, times10bold));


        }
        txtIzq.add(new Paragraph(" ", times10bold));

        txtIzq.add(new Paragraph(auxiliarFijo?.memo1, times10normal));
        txtIzq.add(new Paragraph(" ", times10normal));

        addEmptyLine(headers, 1);
        document.add(headers);
        document.add(txtIzq);


        PdfPTable tablaMemo = new PdfPTable(2);
        tablaMemo.setWidthPercentage(100);


        addCellTabla(tablaMemo, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(" ", times8bold), prmsHeaderHoja)


        if (tipo == '1') {


            addCellTabla(tablaMemo, new Paragraph("Base de Contrato :", times8bold), prmsHeaderHoja)
            addCellTabla(tablaMemo, new Paragraph("Oficio N°" + obra?.oficioSalida, times8normal), prmsHeaderHoja)

            addCellTabla(tablaMemo, new Paragraph("Fórmula Polinómica :", times8bold), prmsHeaderHoja)
            addCellTabla(tablaMemo, new Paragraph(obra?.formulaPolinomica, times8normal), prmsHeaderHoja)


        }
        if (tipo == '2') {

            addCellTabla(tablaMemo, new Paragraph("Presupuesto Referencial :", times8bold), prmsHeaderHoja)
            addCellTabla(tablaMemo, new Paragraph("Oficio N°" + obra?.oficioSalida, times8normal), prmsHeaderHoja)

        }



        addCellTabla(tablaMemo, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(" ", times8bold), prmsHeaderHoja)

        addCellTabla(tablaMemo, new Paragraph("Objeto :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(obra?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(tablaMemo, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(" ", times8bold), prmsHeaderHoja)


        addCellTabla(tablaMemo, new Paragraph("Cantón :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(obra?.parroquia?.canton?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(tablaMemo, new Paragraph("Parroquia :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(obra?.parroquia?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(tablaMemo, new Paragraph("Documento de Referencia :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(obra?.oficioIngreso, times8normal), prmsHeaderHoja)

        addCellTabla(tablaMemo, new Paragraph("Otras Referencias :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(obra?.referencia, times8normal), prmsHeaderHoja)

        addCellTabla(tablaMemo, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph("", times8bold), prmsHeaderHoja)

        PdfPTable tablaBaseMemo = new PdfPTable(3);
        tablaMemo.setWidthPercentage(100);


        if (tipo == '1') {

            addCellTabla(tablaBaseMemo, new Paragraph("Valor de la Base :", times8bold), prmsHeaderHojaRight)
            addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: totalBase, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8normal), prmsHeaderHojaRight)
            addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

//            solo Iva
            if(params.reajusteIvaMemo == 'true' && params.proyeccionMemo == 'false'){


            ivaTotal = (totalBase.toDouble() * paux?.iva)/100
            valorTotal = totalBase.toDouble() + ivaTotal;


                addCellTabla(tablaBaseMemo, new Paragraph("Iva " + paux?.iva + " % :", times8bold), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: ivaTotal, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8normal), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

                addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
                addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

                addCellTabla(tablaBaseMemo, new Paragraph("Valor Total incluido IVA:", times8bold), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: valorTotal, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8normal), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)


            }


            //solo proyeccion del reajuste

            if(params.proyeccionMemo == 'true' && cantidadMesesMemo >= '1' && params.reajusteIvaMemo == 'false') {

               inflacion = paux?.inflacion
                mesesMemo = params.reajusteMesesMemo

                proyeccionTotalMemo = (totalBase.toDouble() * ((inflacion/1200)*mesesMemo.toInteger()))
                valorTotal = totalBase.toDouble() + proyeccionTotalMemo;


                addCellTabla(tablaBaseMemo, new Paragraph("Proyeccion del Reajuste : ", times8bold), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: proyeccionTotalMemo , format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8normal), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

                addCellTabla(tablaBaseMemo, new Paragraph("(Período : " + g.formatNumber(number: mesesMemo, format: "##.##", locale: "ec") + " meses, Inflación : " + g.formatNumber(number: paux?.inflacion, format: "####.##", locale: "ec") + "% )"  , times8bold), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)


                addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
                addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

                addCellTabla(tablaBaseMemo, new Paragraph("Valor Total :", times8bold), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: valorTotal, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8normal), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)



            }

            if(params.proyeccionMemo == 'true' && cantidadMesesMemo>= '1' && params.reajusteIvaMemo == 'true'){

                inflacion = paux?.inflacion
                mesesMemo = params.reajusteMesesMemo

                proyeccionTotalMemo = (totalBase.toDouble() * ((inflacion/1200)*mesesMemo.toInteger()))

                ivaTotal = ((totalBase.toDouble() + proyeccionTotalMemo)*paux?.iva)/100;

                valorTotal = ((totalBase.toDouble() + proyeccionTotalMemo) + ivaTotal)



                addCellTabla(tablaBaseMemo, new Paragraph("Proyeccion del Reajuste : ", times8bold), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: proyeccionTotalMemo, format: "####,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8normal), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

                addCellTabla(tablaBaseMemo, new Paragraph("(Período : " + g.formatNumber(number: mesesMemo, format: "##.##", locale: "ec") + " meses, Inflación : " + g.formatNumber(number: paux?.inflacion, format: "####.##", locale: "ec") + "% )"  , times8bold), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

                addCellTabla(tablaBaseMemo, new Paragraph("Iva " + paux?.iva + " % :", times8bold), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: ivaTotal, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8normal), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

                addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
                addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

                addCellTabla(tablaBaseMemo, new Paragraph("Valor Total incluido IVA:", times8bold), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: valorTotal, format: "####,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8normal), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)




            }
            if(params.proyeccionMemo == 'false' && params.reajusteIvaMemo == 'false') {

               valorTotal = totalBase.toDouble();

                addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
                addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

                addCellTabla(tablaBaseMemo, new Paragraph("Valor Total :", times8bold), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: valorTotal, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8normal), prmsHeaderHojaRight)
                addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)


            }


//            addCellTabla(tablaBaseMemo, new Paragraph("Valor del Reajuste :", times8bold), prmsHeaderHoja)
//            addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: reajusteBase, format: "####.##", locale: "ec"), times8normal), prmsHeaderHoja)
//            addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)
//
//            addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
//            addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
//            addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)
//
//            addCellTabla(tablaBaseMemo, new Paragraph("Valor Total :", times8bold), prmsHeaderHoja)
//            addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: valorTotal, format: "####.##", locale: "ec"), times8normal), prmsHeaderHoja)
//            addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

        }

        if (tipo == '2') {


            addCellTabla(tablaBaseMemo, new Paragraph("Valor del P. Referencial :", times8bold), prmsHeaderHojaRight)
            addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: totalBase, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8normal), prmsHeaderHojaRight)
            addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)


            addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

            addCellTabla(tablaBaseMemo, new Paragraph("Valor Total :", times8bold), prmsHeaderHojaRight)
            addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: totalBase, format: "###,###", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2), times8normal), prmsHeaderHojaRight)
            addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

        }




        document.add(tablaMemo)
        document.add(tablaBaseMemo)


        Paragraph txtAdicionar = new Paragraph();
        addEmptyLine(txtAdicionar, 1);
        txtAdicionar.setAlignment(Element.ALIGN_LEFT);


        txtAdicionar.add(new Paragraph(" ", times10bold));

        txtAdicionar.add(new Paragraph(auxiliarFijo?.memo2, times10normal));
        txtAdicionar.add(new Paragraph(" ", times10normal));

        document.add(txtAdicionar);

//        println(cuenta)


        if (cuenta == 0) {


            PdfPTable tablaFirmas = new PdfPTable(2);
            tablaFirmas.setWidthPercentage(100);


            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)


            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)



            addCellTabla(tablaFirmas, new Paragraph(firma1?.titulo + "" + firma1?.nombre + " " + firma1?.apellido, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.titulo + "" + firma2?.nombre + " " + firma2?.apellido, times8bold), prmsHeaderHoja)




            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.cargo, times8bold), prmsHeaderHoja)


            addCellTabla(tablaFirmas, new Paragraph("Responsable de la Obra", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("Revisor de la Obra", times8bold), prmsHeaderHoja)



            document.add(tablaFirmas);


        }



        if (cuenta == 1) {


            PdfPTable tablaFirmas = new PdfPTable(3);
            tablaFirmas.setWidthPercentage(100);


            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)


            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)


            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)


            addCellTabla(tablaFirmas, new Paragraph(firma1?.titulo + "" + firma1?.nombre + " " + firma1?.apellido, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.titulo + "" + firma2?.nombre + " " + firma2?.apellido, times8bold), prmsHeaderHoja)


            firma.each { f ->


                firmas = Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.titulo + " " + firmas?.nombre + " " + firmas?.apellido, times8bold), prmsHeaderHoja)

            }



            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.cargo, times8bold), prmsHeaderHoja)


            firma.each { f ->

                firmas = Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.cargo, times8bold), prmsHeaderHoja)

            }

            addCellTabla(tablaFirmas, new Paragraph("Responsable de la Obra", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("Revisor de la Obra", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("", times8bold), prmsHeaderHoja)


            document.add(tablaFirmas);


        }
        if (cuenta == 2) {


            PdfPTable tablaFirmas = new PdfPTable(4);
            tablaFirmas.setWidthPercentage(100);

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph("_______________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("_______________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("_______________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("_______________________________", times8bold), prmsHeaderHoja)



            addCellTabla(tablaFirmas, new Paragraph(firma1?.titulo + "" + firma1?.nombre + " " + firma1?.apellido, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.titulo + "" + firma12.nombre + " " + firma12.apellido, times8bold), prmsHeaderHoja)



            firma.each { f ->


                firmas = Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.titulo + "" + firmas?.nombre + " " + firmas?.apellido, times8bold), prmsHeaderHoja)

            }



            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.cargo, times8bold), prmsHeaderHoja)


            firma.each { f ->


                firmas = Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.cargo, times8bold), prmsHeaderHoja)


            }

            addCellTabla(tablaFirmas, new Paragraph("Responsable de la Obra", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("Revisor de la Obra", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("", times8bold), prmsHeaderHoja)



            document.add(tablaFirmas);


        }


        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)


    }



    def reporteDocumentosObraFormu() {


        def auxiliar = Auxiliar.get(1)

//        println(auxiliar)

        def auxiliarFijo = Auxiliar.get(1)

        def obra = Obra.get(params.id)

        def firma

        def firmas

        def cuenta = 0;

        def formula = FormulaPolinomica.findAllByObra(obra)

        def ps = FormulaPolinomica.findAllByObraAndNumeroIlike(obra, 'p%')

        def cuadrilla = FormulaPolinomica.findAllByObraAndNumeroIlike(obra, 'c%')
//
//        println("---->>>>>"+ps)

        def c

        def z = []

        def banderafp = 0

        def firma1 = obra?.responsableObra;
        def firma2 = obra?.revisor;


        if (params.firmasIdFormu.trim().size() > 0) {
            firma = params.firmasIdFormu.split(",")
        } else {
            firma = []
        }

        cuenta = firma.size()

        def totalBase = params.totalPresupuesto


        if (obra?.formulaPolinomica == null) {

            obra?.formulaPolinomica = ""

        }


        def prmsHeaderHoja = [border: Color.WHITE]


        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]


        def prmsHeader = [border: Color.WHITE, colspan: 7, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]



        def prmsHeader2 = [border: Color.WHITE, colspan: 3, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenter = [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellRight = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_RIGHT]
        def prmsCellLeft = [border: Color.BLACK, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.BLACK, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum, prmsHeaderHoja2: prmsHeaderHoja2, prmsCellRight: prmsCellRight]



        def baos = new ByteArrayOutputStream()
        def name = "formulaPolinomica_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times10normal = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
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
        document.addTitle("Formula " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("documentosObra, janus, presupuesto");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");



        Paragraph headers = new Paragraph();
        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);
        headers.add(new Paragraph(auxiliar.titulo, times12bold));

        headers.add(new Paragraph(" ", times12bold));


        headers.add(new Paragraph("FÓRMULA POLINÓMICA N°:" + obra?.formulaPolinomica, times12bold))


        document.add(headers);


        Paragraph txtIzq = new Paragraph();
        addEmptyLine(txtIzq, 1);
        txtIzq.setAlignment(Element.ALIGN_CENTER);

        txtIzq.add(new Paragraph("De existir variaciones en los costos de los componentes de precios unitarios estipulados en el contrato para la contrucción de: ", times10normal));

        txtIzq.add(new Paragraph(" ", times10bold));

        document.add(txtIzq);

        PdfPTable tablaHeader = new PdfPTable(2);
        tablaHeader.setWidthPercentage(100);
        tablaHeader.setWidths(arregloEnteros([15, 85]))

        addCellTabla(tablaHeader, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times10bold), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("Nombre: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(obra?.nombre, times10normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times10bold), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("Tipo de Obra: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(obra?.tipoObjetivo?.descripcion, times10normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("Ubicación : ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(obra?.sitio, times10normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("Cantón : ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(obra?.parroquia?.canton?.nombre, times10normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("Parroquia : ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(obra?.parroquia?.nombre, times10normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("Latitud :", times10bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(g.formatNumber(number: obra?.latitud, minFractionDigits:
                1, maxFractionDigits: 5, format: "#####.##", locale: "ec"), times10normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("Longitud :", times10bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(g.formatNumber(number: obra?.longitud, minFractionDigits:
                1, maxFractionDigits: 5, format: "#####.##", locale: "ec"), times10normal), prmsHeaderHoja)

        document.add(tablaHeader)

        Paragraph txtIzqHeader = new Paragraph();
        addEmptyLine(txtIzqHeader, 1);
        txtIzqHeader.setAlignment(Element.ALIGN_CENTER);

        txtIzqHeader.add(new Paragraph("Los costos se reajustarán para efecto de pago, mediante la fórmula general: ", times10normal));

        txtIzqHeader.add(new Paragraph(" ", times10bold));



        txtIzqHeader.add(new Paragraph("Pr= Po(p01B1/Bo + p02C1/Co + p03D1/Do + p04E1/Eo + p05F1/Fo + p06G1/Go + p07H1/Ho + p08I1/Io + p09J1/Jo + p10K1/Ko + pxX1/Xo) ", times10normal));

        txtIzqHeader.add(new Paragraph(" ", times10bold));


        def textoFormula = "Pr=Po(";

        def formulaCompleta

        def valorP

        ps.each {j->

            if(j.valor != 0.0 || j.valor != 0) {

                if(j.numero == 'p01'){

                    def p01valores = j.valor + "B1/Bo"

                    textoFormula = textoFormula+p01valores


                }
                if(j.numero == 'p02'){

                    def p02valores = j.valor + "C1/Co"

                    textoFormula = textoFormula + " + " + p02valores
                }

                if(j.numero == 'p03'){

                    def p03valores = j.valor + "D1/Do"

                    textoFormula = textoFormula + " + " + p03valores

                }
                if(j.numero == 'p04'){

                    def p04valores = j.valor + "E1/Eo"

                    textoFormula = textoFormula + " + " + p04valores
                }
                if(j.numero == 'p05'){

                    def p05valores = j.valor + "F1/Fo"

                    textoFormula = textoFormula + " + " + p05valores

                }
                if(j.numero == 'p06'){

                    def p06valores = j.valor + "G1/Go"

                    textoFormula = textoFormula + " + " + p06valores

                }
                if(j.numero == 'p07'){

                    def p07valores = j.valor + "H1/Ho"

                    textoFormula = textoFormula + " + " + p07valores
                }
                if(j.numero == 'p08'){

                    def p08valores = j.valor + "I1/Io"

                    textoFormula = textoFormula + " + " + p08valores
                }
                if(j.numero == 'p09'){

                    def p09valores = j.valor + "J1/Jo"

                    textoFormula = textoFormula + " + " + p09valores

                }
                if(j.numero == 'p10'){

                    def p10valores = j.valor + "K1/Ko"

                    textoFormula = textoFormula + " + " + p10valores
                }
                if(j.numero == 'px'){


                    def pxvalores = j.valor + "X1/Xo"

                    textoFormula = textoFormula + " + " + pxvalores

                }


                formulaCompleta =  textoFormula + ")";


            }else {


                formulaCompleta = textoFormula + ")"


            }






        }

//        txtIzqHeader.add(new Paragraph("Pr= Po(${p01valor}B1/Bo + ${p02valor}C1/Co + ${p03valor}D1/Do + ${p04valor}E1/Eo + ${p05valor}F1/Fo +" +
//                " ${p06valor}G1/Go + ${p07valor}H1/Ho + ${p08valor}I1/Io + ${p09valor}J1/Jo + ${p10valor}K1/Ko + ${pxvalor}X1/Xo) ", times10bold));

        txtIzqHeader.add(new Paragraph(formulaCompleta, times10bold));

        document.add(txtIzqHeader)

        PdfPTable tablaCoeficiente = new PdfPTable(5);
        tablaCoeficiente.setWidthPercentage(100);
        tablaCoeficiente.setWidths(arregloEnteros([10, 10, 15, 25, 40]))

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)


        def valorTotal = 0

        ps.each {i->

                      if(i.valor != 0.0 || i.valor != 0) {

                         addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
                         addCellTabla(tablaCoeficiente, new Paragraph(i.numero + " = ", times10normal), prmsHeaderHoja)
                         addCellTabla(tablaCoeficiente, new Paragraph(g.formatNumber(number: i.valor, format: "##.####", locale: "ec"), times10normal), prmsHeaderHoja)
                         addCellTabla(tablaCoeficiente, new Paragraph("Coeficiente del Componente ", times10normal), prmsHeaderHoja)
                         addCellTabla(tablaCoeficiente, new Paragraph(i?.indice?.descripcion, times10normal), prmsHeaderHoja)





                       valorTotal = i.valor + valorTotal

                      }
            else{


                      }

        }


        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("________", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("___", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("SUMAN : ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(g.formatNumber(number: valorTotal, format: "##.####", locale: "ec"), times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)


        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)


        PdfPTable tablaCuadrilla = new PdfPTable(3);
        tablaCuadrilla.setWidthPercentage(100);
        tablaCuadrilla.setWidths(arregloEnteros([10,60,30]))

        addCellTabla(tablaCuadrilla, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCuadrilla, new Paragraph("CUADRILLA TIPO ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCuadrilla, new Paragraph(" ", times10bold), prmsHeaderHoja)

        addCellTabla(tablaCuadrilla, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCuadrilla, new Paragraph("CLASE OBRERO ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCuadrilla, new Paragraph(" ", times10bold), prmsHeaderHoja)

        addCellTabla(tablaCuadrilla, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCuadrilla, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCuadrilla, new Paragraph(" ", times10bold), prmsHeaderHoja)

        def valorTotalCuadrilla = 0;

        cuadrilla.each {i->


            if(i.valor != 0.0 || i.valor != 0) {

                addCellTabla(tablaCuadrilla, new Paragraph(" ", times10normal), prmsHeaderHoja)
                addCellTabla(tablaCuadrilla, new Paragraph(i?.indice?.descripcion, times10normal),prmsHeaderHoja)
                addCellTabla(tablaCuadrilla, new Paragraph(g.formatNumber(number: i?.valor, format: "##.####", locale: "ec"), times10normal), prmsHeaderHoja)


                valorTotalCuadrilla = i.valor + valorTotalCuadrilla

            } else {


            }

        }


        addCellTabla(tablaCuadrilla, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCuadrilla, new Paragraph("________", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCuadrilla, new Paragraph("________", times10bold), prmsHeaderHoja)

        addCellTabla(tablaCuadrilla, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCuadrilla, new Paragraph("SUMAN : ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCuadrilla, new Paragraph(g.formatNumber(number: valorTotalCuadrilla, format: "##.####", locale: "ec"), times10bold), prmsHeaderHoja)




        document.add(tablaCoeficiente)
        document.add(tablaCuadrilla)

        Paragraph txtIzqPie = new Paragraph();
        addEmptyLine(txtIzqPie, 1);
        txtIzqPie.setAlignment(Element.ALIGN_CENTER);

        txtIzqPie.add(new Paragraph(auxiliarFijo?.notaFormula, times10normal));

        txtIzqPie.add(new Paragraph(" ", times10bold));

        document.add(txtIzqPie)

        PdfPTable tablaPie = new PdfPTable(2);
        tablaPie.setWidthPercentage(100);

        addCellTabla(tablaPie, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaPie, new Paragraph(" ", times10bold), prmsHeaderHoja)

        addCellTabla(tablaPie, new Paragraph("Fecha de actualizacion: ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaPie, new Paragraph(formatDate(date: obra?.fechaPreciosRubros, format: "dd-MM-yyyy"), times10normal), prmsHeaderHoja)

        addCellTabla(tablaPie, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaPie, new Paragraph(" ", times10bold), prmsHeaderHoja)

        addCellTabla(tablaPie, new Paragraph("Monto del Contrato : ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaPie, new Paragraph("\$ " + g.formatNumber(number: totalBase, minFractionDigits: 2, maxFractionDigits: 2, format: "###,###", locale: "ec"), fonts.times10normal), prmsHeaderHoja)

        addCellTabla(tablaPie, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaPie, new Paragraph(" ", times10bold), prmsHeaderHoja)

        addCellTabla(tablaPie, new Paragraph("Atentamente,  ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaPie, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaPie, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaPie, new Paragraph(" ", times10bold), prmsHeaderHoja)


        document.add(tablaPie)

        if (cuenta == 0) {


            PdfPTable tablaFirmas = new PdfPTable(2);
            tablaFirmas.setWidthPercentage(100);


            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)


            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)


            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)



            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)



            addCellTabla(tablaFirmas, new Paragraph(firma1?.titulo + "" + firma1?.nombre + " " + firma1?.apellido, times8bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph(firma2?.titulo + "" + firma2?.nombre + " " + firma2?.apellido, times8bold), prmsHeaderHoja)



            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.cargo, times8bold), prmsHeaderHoja)


            addCellTabla(tablaFirmas, new Paragraph("Responsable de la Obra", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("Revisor de la Obra", times8bold), prmsHeaderHoja)

            document.add(tablaFirmas);


        }
        if (cuenta == 1) {


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
            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)


            addCellTabla(tablaFirmas, new Paragraph(firma1?.titulo + "" + firma1?.nombre + " " + firma1?.apellido, times8bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph(firma2?.titulo + "" + firma2?.nombre + " " + firma2?.apellido, times8bold), prmsHeaderHoja)



            firma.each { f ->


                firmas = Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.titulo + " " + firmas?.nombre + " " + firmas?.apellido, times8bold), prmsHeaderHoja)

            }



            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.cargo, times8bold), prmsHeaderHoja)

            firma.each { f ->


                firmas = Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.cargo, times8bold), prmsHeaderHoja)

            }

            addCellTabla(tablaFirmas, new Paragraph("Responsable de la Obra", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("Revisor de la Obra", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("", times8bold), prmsHeaderHoja)

            document.add(tablaFirmas);

        }
        if (cuenta == 2) {


            PdfPTable tablaFirmas = new PdfPTable(4);
            tablaFirmas.setWidthPercentage(100);

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph("_______________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("_______________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("_______________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("_______________________________", times8bold), prmsHeaderHoja)



            addCellTabla(tablaFirmas, new Paragraph(firma1?.titulo + "" + firma1?.nombre + " " + firma1?.apellido, times8bold), prmsHeaderHoja)




            addCellTabla(tablaFirmas, new Paragraph(firma2?.titulo + "" + firma2?.nombre + " " + firma2?.apellido, times8bold), prmsHeaderHoja)


            firma.each { f ->


                firmas = Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.titulo + "" + firmas?.nombre + " " + firmas?.apellido, times8bold), prmsHeaderHoja)

            }



            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.cargo, times8bold), prmsHeaderHoja)


            firma.each { f ->


                firmas = Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.cargo, times8bold), prmsHeaderHoja)


            }

            addCellTabla(tablaFirmas, new Paragraph("Responsable de la Obra", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("Revisor de la Obra", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("", times8bold), prmsHeaderHoja)

            document.add(tablaFirmas);


        }


        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)


    }


    def documentosObraExcel() {


        def obra = Obra.get(params.id)

        def tasa = params.tasa


        def detalle

        detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def subPres = VolumenesObra.findAllByObra(obra, [sort: "orden"]).subPresupuesto.unique()

        def precios = [:]
        def fecha = obra.fechaPreciosRubros
        def dsps = obra.distanciaPeso
        def dsvl = obra.distanciaVolumen
        def lugar = obra.lugar
        def prch = 0
        def prvl = 0
//        if (obra.chofer) {
//            prch = preciosService.getPrecioItems(fecha, lugar, [obra.chofer])
//            prch = prch["${obra.chofer.id}"]
//            prvl = preciosService.getPrecioItems(fecha, lugar, [obra.volquete])
//            prvl = prvl["${obra.volquete.id}"]
//        }
//        def rendimientos = preciosService.rendimientoTranposrte(dsps, dsvl, prch, prvl)
//
//        if (rendimientos["rdps"].toString() == "NaN")
//            rendimientos["rdps"] = 0
//        if (rendimientos["rdvl"].toString() == "NaN")
//            rendimientos["rdvl"] = 0

        def indirecto = obra.totales / 100

        def c;

        def total1 = 0;

        def totales

        def totalPresupuesto;

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


        params.id = params.id.split(",")
        if (params.id.class == java.lang.String) {
            params.id = [params.id]
        }
        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, true);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        sheet.setColumnView(0, 60)
        sheet.setColumnView(1, 12)
        sheet.setColumnView(2, 25)
        sheet.setColumnView(3, 25)
        sheet.setColumnView(4, 30)
        sheet.setColumnView(8, 20)
        // inicia textos y numeros para asocias a columnas
//        def label = new Label(0, 1, "Texto", times16format);
//        def nmro = new Number(12, 1, 9999);

        def label
        def nmro
        def number

        def fila = 6;



        NumberFormat nf=new NumberFormat("#.##");
        WritableCellFormat cf2obj=new WritableCellFormat(nf);


        label = new Label(0, 2, "Presupuesto de la Obra: " + obra?.nombre.toString(), times16format); sheet.addCell(label);

        label = new Label(0, 4, "RUBRO", times16format); sheet.addCell(label);
        label = new Label(1, 4, "NOMBRE", times16format); sheet.addCell(label);
        label = new Label(2, 4, "UNDD", times16format); sheet.addCell(label);
        label = new Label(3, 4, "CANTIDAD", times16format); sheet.addCell(label);
        label = new Label(4, 4, "P_UNIT", times16format); sheet.addCell(label);
        label = new Label(5, 4, "SUBTOTAL", times16format); sheet.addCell(label);
//        label = new Label(6, 4, "SUBP", times16format); sheet.addCell(label);
//        label = new Label(7, 4, "SUBPRESUPUESTO", times16format); sheet.addCell(label);
//        label = new Label(8, 4, "ORDEN", times16format); sheet.addCell(label);



        detalle.each {

//            def parametros = "" + it.item.id + "," + lugar.id + ",'" + fecha.format("yyyy-MM-dd") + "'," + dsps.toDouble() + "," + dsvl.toDouble() + preciosService.ac_rbro(it.item.id, lugar.id, fecha.format("yyyy-MM-dd"))

//            def res = preciosService.presioUnitarioVolumenObra("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
//            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))


            def res = preciosService.presioUnitarioVolumenObra("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))


            def precioUnitario = precios[it.id.toString()]


            def subtotal = (precios[it.id.toString()] * it.cantidad)


//            println(precioUnitario)

            label = new Label(0, fila, it?.item?.codigo.toString()); sheet.addCell(label);
            label = new Label(1, fila, it?.item?.nombre.toString()); sheet.addCell(label);
            label = new Label(2, fila, it?.item?.unidad?.codigo.toString()); sheet.addCell(label);
//            label = new Label(3, fila, it?.cantidad.toString()); sheet.addCell(label);
//            label = new Label(4, fila, precioUnitario.toString()); sheet.addCell(label);
//            label = new Label(5, fila, subtotal.toString()); sheet.addCell(label);

            number = new jxl.write.Number(3, fila, it?.cantidad); sheet.addCell(number);
            number = new jxl.write.Number(4, fila, precioUnitario,cf2obj); sheet.addCell(number);
            number = new jxl.write.Number(5, fila, subtotal,cf2obj); sheet.addCell(number);



//            label = new Label(6, fila, "0"); sheet.addCell(label);
//            label = new Label(7, fila, obra?.nombre.toString()); sheet.addCell(label);
//            label = new Label(8, fila, "0"); sheet.addCell(label);

//            addCellTabla(tablaVolObra, new Paragraph(it?.item?.codigo,times8normal), prmsCellCenter)
//
//
//            addCellTabla(tablaVolObra, new Paragraph(it?.item?.nombre,times8normal), prmsCellLeft)
//
//
//            addCellTabla(tablaVolObra, new Paragraph(it?.item?.unidad?.codigo,times8normal), prmsCellCenter)
//
//
//            addCellTabla(tablaVolObra, new Paragraph(g.formatNumber(number: it?.cantidad, minFractionDigits:
//                    2, maxFractionDigits: 2, format: "#####,##0"),times8normal), prmsCellRight)
//
//            addCellTabla(tablaVolObra, new Paragraph (g.formatNumber(number: precios[it.id.toString()], minFractionDigits:
//                    2, maxFractionDigits: 2, format: "#####,##0"),times8normal), prmsCellRight)
//
//            addCellTabla(tablaVolObra, new Paragraph (g.formatNumber(number: precios[it.id.toString()]*it.cantidad, minFractionDigits:
//                    2, maxFractionDigits: 2, format: "#####,##0"),times8normal), prmsCellRight)

//            println("costo:" + precios[it.id.toString()]*it.cantidad)

//            totales =  precios[it.id.toString()]*it.cantidad

//            println("total:" + (total1+=totales) )

//            totalPresupuesto = (total1+=totales);


            fila++

//            return totalPresupuesto

        }


        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "DocumentosObraExcel.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());


    }

    def documentosObraTasaExcel() {



       def obra = Obra.get(params.id)

        def tasa = params.tasa


        def detalle

        detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def subPres = VolumenesObra.findAllByObra(obra, [sort: "orden"]).subPresupuesto.unique()

        def precios = [:]
        def fecha = obra.fechaPreciosRubros
        def dsps = obra.distanciaPeso
        def dsvl = obra.distanciaVolumen
        def lugar = obra.lugar
        def prch = 0
        def prvl = 0

        def indirecto = obra.totales / 100

        def c;

        def total1 = 0;

        def totales

        def totalPresupuesto;



        NumberFormat nf=new NumberFormat("#.##");
        WritableCellFormat cf2obj=new WritableCellFormat(nf);

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
        params.id = params.id.split(",")
        if (params.id.class == java.lang.String) {
            params.id = [params.id]
        }
        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, true);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        sheet.setColumnView(0, 60)
        sheet.setColumnView(1, 12)
        sheet.setColumnView(2, 25)
        sheet.setColumnView(3, 25)
        sheet.setColumnView(4, 30)
        sheet.setColumnView(8, 20)
        // inicia textos y numeros para asocias a columnas

        def label
        def nmro
        def number

        def fila = 6;

        label = new Label(0, 2, "Presupuesto de la Obra: " + obra?.nombre.toString(), times16format); sheet.addCell(label);

        label = new Label(0, 4, "RUBRO", times16format); sheet.addCell(label);
        label = new Label(1, 4, "NOMBRE", times16format); sheet.addCell(label);
        label = new Label(2, 4, "UNDD", times16format); sheet.addCell(label);
        label = new Label(3, 4, "CANTIDAD", times16format); sheet.addCell(label);
        label = new Label(4, 4, "P_UNIT", times16format); sheet.addCell(label);
        label = new Label(5, 4, "SUBTOTAL", times16format); sheet.addCell(label);

        detalle.each {

            def res = preciosService.presioUnitarioVolumenObra("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))




            def precioUnitario = (precios[it.id.toString()])*tasa.toDouble()

            def subtotal = (((precios[it.id.toString()])*tasa.toDouble()) * it.cantidad)

            label = new Label(0, fila, it?.item?.codigo.toString()); sheet.addCell(label);
            label = new Label(1, fila, it?.item?.nombre.toString()); sheet.addCell(label);
            label = new Label(2, fila, it?.item?.unidad?.codigo.toString()); sheet.addCell(label);
            number = new jxl.write.Number(3, fila, it?.cantidad); sheet.addCell(number);
            number = new jxl.write.Number(4, fila, precioUnitario,cf2obj); sheet.addCell(number);
            number = new jxl.write.Number(5, fila, subtotal,cf2obj); sheet.addCell(number);


            fila++


        }

        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "DocumentosObraExcelTasa.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());


    }




    def reporteExcelVolObra() {

        def obra = Obra.get(params.id)
        def detalle
        detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def subPres = VolumenesObra.findAllByObra(obra, [sort: "orden"]).subPresupuesto.unique()

        def precios = [:]
        def fecha = obra.fechaPreciosRubros
        def dsps = obra.distanciaPeso
        def dsvl = obra.distanciaVolumen
        def lugar = obra.lugar
        def prch = 0
        def prvl = 0
        preciosService.ac_rbroObra(obra.id)

        def valores = preciosService.rbro_pcun_v2(obra.id)

        def indirecto = obra.totales / 100

        def c;

        def total1 = 0;

        def totales = 0

        def totalPresupuesto = 0;

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


        params.id = params.id.split(",")
        if (params.id.class == java.lang.String) {
            params.id = [params.id]
        }
        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, true);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        sheet.setColumnView(0, 12)
        sheet.setColumnView(1, 25)
        sheet.setColumnView(2, 60)
        sheet.setColumnView(3, 25)
        sheet.setColumnView(4, 25)
        sheet.setColumnView(5, 25)
        sheet.setColumnView(6, 25)


        def label
        def number
        def nmro
        def numero = 1;

        def fila = 9;

        def ultimaFila



        label = new Label(2, 2, "GOBIERNO AUTÓNOMO DESCENTRALIZADO DE LA PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);


        label = new Label(2, 4, "DEPARTAMENTO DE COSTOS", times16format); sheet.addCell(label);


        label = new Label(2, 6, "ANÁLISIS DE PRECIOS UNITARIOS DEL SUBPRESUPUESTO: " + subPres?.descripcion.toString(), times16format); sheet.addCell(label);



        label = new Label(0, 8, "#", times16format); sheet.addCell(label);
        label = new Label(1, 8, "CÓDIGO", times16format); sheet.addCell(label);
        label = new Label(2, 8, "RUBRO", times16format); sheet.addCell(label);
        label = new Label(3, 8, "UNIDAD", times16format); sheet.addCell(label);
        label = new Label(4, 8, "CANTIDAD", times16format); sheet.addCell(label);
        label = new Label(5, 8, "UNITARIO", times16format); sheet.addCell(label);
        label = new Label(6, 8, "C.TOTAL", times16format); sheet.addCell(label);

        valores.each {

//            def res = preciosService.presioUnitarioVolumenObra("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
//            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
//
//            def precioUnitario = precios[it.id.toString()]
//
//            def subtotal = (precios[it.id.toString()] * it.cantidad)

//            number = new Number(0, fila, numero++); sheet.addCell(number);
//            label = new Label(1, fila, it?.item?.codigo.toString()); sheet.addCell(label);
//            label = new Label(2, fila, it?.item?.nombre.toString()); sheet.addCell(label);
//            label = new Label(3, fila, it?.item?.unidad?.codigo.toString()); sheet.addCell(label);
//            number = new Number(4, fila, it?.cantidad); sheet.addCell(number);
//            number = new Number(5, fila, precioUnitario); sheet.addCell(number);
//            number = new Number(6, fila, subtotal); sheet.addCell(number);

            number = new Number(0, fila, numero++); sheet.addCell(number);
            label = new Label(1, fila, it.rbrocdgo.toString()); sheet.addCell(label);
            label = new Label(2, fila, it.rbronmbr.toString()); sheet.addCell(label);
            label = new Label(3, fila, it.unddcdgo.toString()); sheet.addCell(label);
            number = new Number(4, fila, it.vlobcntd); sheet.addCell(number);
            number = new Number(5, fila, it.pcun); sheet.addCell(number);
            number = new Number(6, fila, it.totl); sheet.addCell(number);

            fila++
            totales = it.totl
            totalPresupuesto = (total1 += totales);
            ultimaFila = fila

        }

        label = new Label(5, ultimaFila, "TOTAL ", times16format); sheet.addCell(label);
        number = new Number(6, ultimaFila, totalPresupuesto); sheet.addCell(number);



        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "VolObraExcel.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());


    }


    def dummyReportes() {

        return false

    }



    def pagarAnticipoPdf() {


        def baos = new ByteArrayOutputStream()
        def name = "pagarAnticipo_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        def fonts = [times12bold: times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal]

        Document document
        document = new Document(PageSize.A4);
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();
        document.addTitle("Pagar Anticipo " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, rubros");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");


        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeader = [border: Color.WHITE, colspan: 7, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsHeader2 = [border: Color.WHITE, colspan: 3, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellHead = [border: Color.WHITE, bg: new Color(73, 175, 205),
                align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellCenter = [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellLeft = [border: Color.BLACK, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.BLACK, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum]

        def planilla = janus.ejecucion.Planilla.get(params.id)

        def contrato = Contrato.get(planilla?.contrato?.id)

        def obra = Obra.get(contrato?.oferta?.concurso?.obra?.id)

        def suma = (planilla?.reajuste + planilla?.valor)

        PdfPTable headerRubroTabla = new PdfPTable(4); // 4 columns.
        headerRubroTabla.setWidthPercentage(100);
        headerRubroTabla.setWidths(arregloEnteros([10, 40, 10, 40]))


        addCellTabla(headerRubroTabla, new Paragraph("Obra1:", times8bold), prmsHeaderHoja)
        addCellTabla(headerRubroTabla, new Paragraph(obra?.nombre + " " + obra?.descripcion, times8normal), prmsHeaderHoja)

        addCellTabla(headerRubroTabla, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(headerRubroTabla, new Paragraph(" ", times8normal), prmsHeaderHoja)

        addCellTabla(headerRubroTabla, new Paragraph("Lugar:", times8bold), prmsHeaderHoja)
        addCellTabla(headerRubroTabla, new Paragraph(obra?.lugar?.descripcion, times8normal), prmsHeaderHoja)

        addCellTabla(headerRubroTabla, new Paragraph("Planilla:", times8bold), prmsHeaderHoja)
        addCellTabla(headerRubroTabla, new Paragraph(g.formatNumber(number: planilla?.numero, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), fonts.times8normal), prmsHeaderHoja)

        addCellTabla(headerRubroTabla, new Paragraph("Ubicación:", times8bold), prmsHeaderHoja)
        addCellTabla(headerRubroTabla, new Paragraph(obra?.parroquia?.nombre + " " + obra?.parroquia?.canton?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(headerRubroTabla, new Paragraph("Monto Contrato:", times8bold), prmsHeaderHoja)
        addCellTabla(headerRubroTabla, new Paragraph(g.formatNumber(number: contrato?.monto, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), fonts.times8normal), prmsHeaderHoja)

        addCellTabla(headerRubroTabla, new Paragraph("Contratista:", times8bold), prmsHeaderHoja)
        addCellTabla(headerRubroTabla, new Paragraph(contrato?.oferta?.proveedor?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(headerRubroTabla, new Paragraph("Período:", times8bold), prmsHeaderHoja)
        addCellTabla(headerRubroTabla, new Paragraph((planilla.tipoPlanilla.codigo == 'A' ? 'Anticipo' : 'del ' + planilla.fechaInicio.format('dd-MM-yyyy') + ' al ' + planilla.fechaFin.format('dd-MM-yyyy')), times8normal), prmsHeaderHoja)

        addCellTabla(headerRubroTabla, new Paragraph("Plazo:", times8bold), prmsHeaderHoja)
        addCellTabla(headerRubroTabla, new Paragraph(g.formatNumber(number: contrato?.plazo, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), fonts.times8normal), prmsHeaderHoja)

        addCellTabla(headerRubroTabla, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(headerRubroTabla, new Paragraph(" ", times8normal), prmsHeaderHoja)

        addCellTabla(headerRubroTabla, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(headerRubroTabla, new Paragraph(" ", times8normal), prmsHeaderHoja)
        addCellTabla(headerRubroTabla, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(headerRubroTabla, new Paragraph(" ", times8normal), prmsHeaderHoja)


        PdfPTable anticipoTabla = new PdfPTable(2);
        anticipoTabla.setWidthPercentage(100);
        anticipoTabla.setWidths(arregloEnteros([50, 50]))



        if (planilla?.tipoPlanilla?.codigo == 'A') {



            addCellTabla(anticipoTabla, new Paragraph(contrato?.porcentajeAnticipo + " % de anticipo:", times8bold), prmsHeaderHoja)

            addCellTabla(anticipoTabla, new Paragraph(g.formatNumber(number: planilla?.valor, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), fonts.times8normal), prmsHeaderHoja)

            addCellTabla(anticipoTabla, new Paragraph("(+) Reajuste provisional del anticipo", times8bold), prmsHeaderHoja)

            addCellTabla(anticipoTabla, new Paragraph(g.formatNumber(number: planilla?.reajuste, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), fonts.times8normal), prmsHeaderHoja)

            addCellTabla(anticipoTabla, new Paragraph("SUMA:", times8bold), prmsHeaderHoja)

            addCellTabla(anticipoTabla, new Paragraph(g.formatNumber(number: suma, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), fonts.times8normal), prmsHeaderHoja)

            addCellTabla(anticipoTabla, new Paragraph("A FAVOR DEL CONTRATISTA:", times8bold), prmsHeaderHoja)
            addCellTabla(anticipoTabla, new Paragraph(g.formatNumber(number: suma, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), fonts.times8normal), prmsHeaderHoja)


        } else {

            addCellTabla(anticipoTabla, new Paragraph("Valor Planilla", times8bold), prmsHeaderHoja)

            addCellTabla(anticipoTabla, new Paragraph(g.formatNumber(number: planilla?.valor, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), fonts.times8normal), prmsHeaderHoja)

            addCellTabla(anticipoTabla, new Paragraph("(+) Reajuste provisional del anticipo", times8bold), prmsHeaderHoja)

            addCellTabla(anticipoTabla, new Paragraph(g.formatNumber(number: planilla?.reajuste, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), fonts.times8normal), prmsHeaderHoja)

            addCellTabla(anticipoTabla, new Paragraph("SUMA:", times8bold), prmsHeaderHoja)

            addCellTabla(anticipoTabla, new Paragraph(g.formatNumber(number: suma, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), fonts.times8normal), prmsHeaderHoja)

            addCellTabla(anticipoTabla, new Paragraph("A FAVOR DEL CONTRATISTA:", times8bold), prmsHeaderHoja)
            addCellTabla(anticipoTabla, new Paragraph(g.formatNumber(number: suma, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), fonts.times8normal), prmsHeaderHoja)


        }

//        def planillas = Planilla.withCriteria {
//            and {
//                eq("contrato", contrato)
//                or {
//                    lt("fechaInicio", planilla.fechaFin)
//                    isNull("fechaInicio")
//                }
//                order("id", "asc")
//            }
//        }
//
//
//        def fp = janus.FormulaPolinomica.findAllByObra(obra)
//        def fr = FormulaPolinomicaContractual.findAllByContrato(contrato)
//        def tipo = TipoFormulaPolinomica.get(1)
//        def oferta = contrato.oferta
//        def periodoOferta = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(oferta.fechaEntrega, oferta.fechaEntrega)
//
//        def periodos = []
//        def data = [
//                c: [:],
//                p: [:]
//        ]
//        def fpB0
//
//        def pcs = FormulaPolinomicaContractual.withCriteria {
//            and {
//                eq("contrato", contrato)
//                or {
//                    ilike("numero", "c%")
//                    and {
//                        ne("numero", "P0")
//                        ilike("numero", "p%")
//                    }
//                }
//                order("numero", "asc")
//            }
//        }
//
//
//        PdfPTable b0Tabla = new PdfPTable(7);
//        b0Tabla.setWidthPercentage(210);
//        b0Tabla.setWidths(arregloEnteros([30, 30, 30, 30, 30, 30, 30]))
//
//        b0Tabla(headerRubroTabla, new Paragraph("Cuadrilla Tipo:", times8bold), prmsHeaderHoja)
//        b0Tabla(headerRubroTabla, new Paragraph(obra?.nombre + " " + obra?.descripcion, times8normal), prmsHeaderHoja)
//
//        b0Tabla(headerRubroTabla, new Paragraph(" ", times8bold), prmsHeaderHoja)
//        b0Tabla(headerRubroTabla, new Paragraph(" ", times8normal), prmsHeaderHoja)


        document.add(headerRubroTabla);
//        document.add(anticipoTexto);
        document.add(anticipoTabla);

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)


    }


    def anticipoReporte() {

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
        def diasPlanilla = 0

        if (planilla.tipoPlanilla.codigo != "A") {
            diasPlanilla = planilla.fechaFin - planilla.fechaInicio
        }
        def valorPlanilla = planilla.valor

        def acumuladoCrono = 0, acumuladoPlan = 0

        def diasAll = 0

        periodos.eachWithIndex { per, i ->
            if (i > 0) {
                def planillaActual = Planilla.findByPeriodoIndicesAndContrato(per, contrato)
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

        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def precios = [:]
        def indirecto = obra.totales / 100

        preciosService.ac_rbroObra(obra.id)

        detalle.each {
            def res = preciosService.presioUnitarioVolumenObra("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
        }

        def planillasAnteriores = Planilla.withCriteria {
            eq("contrato", contrato)
            lt("fechaFin", planilla.fechaInicio)
        }

        def numerosALetras = NumberToLetterConverter.convertNumberToLetter(planilla?.valor + planilla?.reajuste)


        return [tbodyFr: tbodyFr, tbodyP0: tbodyP0, tbodyB0: tbodyB0, planilla: planilla, obra: obra, oferta: oferta, contrato: contrato, pcs: pcs, data: data, periodos: periodos, detalle: detalle, planillasAnteriores: planillasAnteriores, precios: precios, numerosALetras: numerosALetras]

    }


    def aseguradoras() {
        def asg = janus.pac.Aseguradora.list()
        [asg: asg]
    }


}
