package janus

import com.itextpdf.text.BadElementException
import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter

import java.awt.*

class ReportesController {

    def index() {}

    def buscadorService
    def preciosService


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
        println "imprime matriz"
        def cn = buscadorService.dbConnectionService.getConnection()
        def cn2 = buscadorService.dbConnectionService.getConnection()
        def sql = "SELECT clmncdgo,clmndscr,clmntipo from obcl_${session.usuario} order by 1"
        def columnas = []
        def filas = []
        cn.eachRow(sql.toString()) { r ->
            columnas.add([r[0], r[1], r[2]])
        }
        sql = "SELECT * from obrb_${session.usuario} order by orden"
        def cont = 1
        cn.eachRow(sql.toString()) { r ->
            def tmp = [cont, r[0].trim(), r[1], r[2], r[3]]
            def sq = ""
            columnas.each { c ->
                if (c[2] != "R") {
                    sq = "select valor from obvl_${session.usuario} where clmncdgo=${c[0]} and codigo='${r[0].trim()}'"
                    cn2.eachRow(sq.toString()) { v ->
                        tmp.add(v[0])
                    }
                }

            }
//            println "fila  "+tmp
            filas.add(tmp)
            cont++
        }

        def baos = new ByteArrayOutputStream()
        def name = "matriz_polinomica_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
//            println "name "+name
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
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("Matriz polinómica", catFont));
        preface.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
        addEmptyLine(preface, 1);
        document.add(preface);
        Font small = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        /*table*/

        def parcial = []
        def anchos = [5, 10, 30, 5, 10, 10, 10, 10]
        def inicio = 0
        def fin = 8
//        println "size "+columnas.size()
        while (fin <= columnas.size()) {
//            println "inicio "+inicio+"  fin  "+fin
            if (inicio != 0)
                anchos = [12, 12, 12, 12, 12, 12, 12, 12]
            if (fin - inicio < 8) {
                anchos = []
                (fin - inicio).toInteger().times { i ->
                    anchos.add((100 / (fin - inicio)).toInteger())
                }
            }
            def parrafo = new Paragraph("")
            PdfPTable table = new PdfPTable((fin - inicio).toInteger());
            table.setWidthPercentage(100);
            table.setWidths(arregloEnteros(anchos))
            (fin - inicio).toInteger().times { i ->
                PdfPCell c1 = new PdfPCell(new Phrase(columnas[inicio + i][1], small));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c1);
            }
            table.setHeaderRows(1);
            filas.each { f ->
                (fin - inicio).toInteger().times { i ->
                    def dato = f[inicio + i]
                    if (!dato)
                        dato = "0.00"
                    else
                        dato = dato.toString()
                    table.addCell(new Phrase(dato, small));
                }
            }
            parrafo.add(table);
            document.add(parrafo);
            document.newPage();
            inicio = fin + 1
            fin = inicio + 8
            if (fin > columnas.size())
                fin = columnas.size()
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
            Font info = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
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

    def imprimirRubros() {
        params.obra = "886"
        params.transporte = "1"

//        render "AQUI"

        def obra = Obra.get(params.obra.toLong())

        /*
           params.dsps=0 //distancia peso
            params.prch=0 //precio chofer
            params.prvl=0 //precio volquete
            params.dsvs=0 //distancia volumen
         */

        def lugar = obra.lugar
        def fecha = obra.fechaPreciosRubros
        def itemsChofer = [obra.chofer]
        def itemsVolquete = [obra.volquete]

        def precioChoferA = preciosService.getPrecioItemsString(fecha, lugar, itemsChofer)
        def precioVolqueteA = preciosService.getPrecioItemsString(fecha, lugar, itemsVolquete)

        def parts = precioChoferA.split("&")
        parts = parts[0].split(";")
        def precioChofer = parts[1].toDouble()

        parts = precioVolqueteA.split("&")
        parts = parts[0].split(";")
        def precioVolquete = parts[1].toDouble()

        def rendimientos = preciosService.rendimientoTransporteLuz(obra, precioChofer, precioVolquete)
        println "rends " + rendimientos
        if (rendimientos["rdps"].toString() == "NaN" || rendimientos["rdps"].toString() == "Infinity") {
            rendimientos["rdps"] = 0
            rendimientos["rdvl"] = 0
        }
        if (rendimientos["rdvl"].toString() == "NaN" || rendimientos["rdvl"].toString() == "Infinity") {
            rendimientos["rdvl"] = 0
            rendimientos["rdps"] = 0
        }

        def baos = new ByteArrayOutputStream()
        def name = "rubros_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
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
        document.addTitle("Rubros " + new Date().format("dd_MM_yyyy"));
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
        def prmsCell = [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.BLACK, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCell, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum]

        VolumenesObra.findAllByObra(obra, [sort: "orden"]).item.each { rubro ->

            Paragraph headers = new Paragraph();
            addEmptyLine(headers, 1);
            headers.setAlignment(Element.ALIGN_CENTER);
            headers.add(new Paragraph("GOBIERNO DE LA PROVINCIA DE PICHINCHA", times12bold));
            headers.add(new Paragraph("DEPARTAMENTO DE COSTOS", times10bold));
            headers.add(new Paragraph("ANALISIS DE PRECIOS UNITARIOS DE PRESUPUESTO", times10bold));
//            headers.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el " + new Date().format("dd/MM/yyyy hh:mm"), times8normal))
            addEmptyLine(headers, 1);
            document.add(headers);

            def id = rubro.id

            def parametros = "" + id + "," + lugar.id + ",'" + fecha + "'," + obra.distanciaPeso + "," + obra.distanciaVolumen + "," + rendimientos["rdps"] + "," + rendimientos["rdvl"]
            preciosService.ac_rbro(id, lugar.id, fecha)
            def res = preciosService.rb_precios(parametros, "")

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

            creaHeadersTabla(tablaHerramientas, fonts, prms, "Herramientas")
            creaHeadersTabla(tablaManoObra, fonts, prms, "Mano de obra")
            creaHeadersTabla(tablaMateriales, fonts, prms, "Materiales")
            creaHeadersTabla(tablaTransporte, fonts, prms, "Transporte")
            creaHeadersTabla(tablaIndirectos, fonts, prms, "Costos Indirectos")

            def totalTrans = 0, totalHer = 0, totalMan = 0, totalMat = 0

            res.each { r ->
                if (r["grpocdgo"] == 3) {
                    llenaDatos(tablaHerramientas, r, fonts, prms, "H")
                    totalHer += r.parcial
                }
                if (r["grpocdgo"] == 2) {
                    llenaDatos(tablaManoObra, r, fonts, prms, "O")
                    totalMan += r.parcial
                }
                if (r["grpocdgo"] == 1) {
                    llenaDatos(tablaMateriales, r, fonts, prms, "M")
                    totalMat += r.parcial
                }
                if (r["parcial_t"] > 0 && params.transporte == "1") {
                    llenaDatos(tablaTransporte, r, fonts, prms, "T")
                    totalTrans += r.parcial_t
                }
            }

            addSubtotal(tablaHerramientas, totalHer, fonts, prms)
            addSubtotal(tablaManoObra, totalMan, fonts, prms)
            addSubtotal(tablaMateriales, totalMat, fonts, prms)
            if (params.transporte == "1") {
                addSubtotal(tablaTransporte, totalTrans, fonts, prms)
            }

            addTablaHoja(document, headerRubroTabla)
            addTablaHoja(document, tablaHerramientas)
            addTablaHoja(document, tablaManoObra)
            addTablaHoja(document, tablaMateriales)
            addTablaHoja(document, tablaTransporte)
            addTablaHoja(document, tablaIndirectos)

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
        addCellTabla(table, new Paragraph(r.itemcdgo, fonts.times8normal), params.prmsCell)
        addCellTabla(table, new Paragraph(r.itemnmbr, fonts.times8normal), params.prmsCell)
        switch (tipo) {
            case "H":
            case "O":
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rbrocntd, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rbpcpcun, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rbpcpcun * r.rbrocntd, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rndm, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.parcial, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), params.prmsNum)
                break;
            case "M":
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rbrocntd, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rbpcpcun, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph("", fonts.times8normal), params.prmsCell)
                addCellTabla(table, new Paragraph("", fonts.times8normal), params.prmsCell)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.parcial, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), params.prmsNum)
                break;
            case "T":
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.itempeso, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.rbrocntd, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.distancia, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.parcial_t / (r.itempeso * r.rbrocntd * r.distancia), minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), params.prmsNum)
                addCellTabla(table, new Paragraph(g.formatNumber(number: r.parcial_t, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), params.prmsNum)
                break;
        }
    }

    def addSubtotal(PdfPTable table, subtotal, fonts, params) {
        addCellTabla(table, new Paragraph("Subtotal", fonts.times8bold), params.prmsSubtotal)
        addCellTabla(table, new Paragraph(g.formatNumber(number: subtotal, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8bold), params.prmsNum)
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

    def addTablaHoja(Document document, PdfPTable table) {
        Paragraph paragraph = new Paragraph()
        addEmptyLine(paragraph, 1);
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


}
