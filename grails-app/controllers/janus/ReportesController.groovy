package janus

import com.itextpdf.text.BadElementException
import com.lowagie.text.*
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
//import java.awt.Label

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
        def sql = "SELECT clmncdgo,clmndscr,clmntipo from mfcl where obra__id=${params.id} order by 1"
        def columnas = []
        def filas = []
        cn.eachRow(sql.toString()) { r ->
            columnas.add([r[0], r[1], r[2]])
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
//        Paragraph preface = new Paragraph();
//        addEmptyLine(preface, 1);
//        preface.add(new Paragraph("Matriz polinómica", catFont));
//        preface.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
//        addEmptyLine(preface, 1);
//        document.add(preface);
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
                    def cell = new PdfPCell(new Phrase(dato, small));
                    cell.setFixedHeight(28f);
                    table.addCell(cell);
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

    def pac(){
        println "params REPORTE "+params
        def pac
        def dep
        def anio
        if (!params.todos){
            anio=janus.pac.Anio.get(params.anio)
            if (params.dpto){
                dep = Departamento.get(params.dpto)
                pac = janus.pac.Pac.findAllByDepartamentoAndAnio(dep,anio,[sort: "id"])
                dep = dep.descripcion
                anio=anio.anio
            } else{
                pac = janus.pac.Pac.findAllByAnio(janus.pac.Anio.get(params.anio),[sort: "id"])
                dep = "Todos"
                anio=anio.anio
            }
        }else{
            dep = "Todos"
            anio = "Todos"
            pac = janus.pac.Pac.list([sort: "id"])
        }

        [pac:pac,todos:params.todos,dep:dep,anio:anio]
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
//        params.obra = "886"
//        params.transporte = "1"

//        println params

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

        def indi = obra.totales

        def precioChoferA = preciosService.getPrecioItemsString(fecha, lugar, itemsChofer)
        def precioVolqueteA = preciosService.getPrecioItemsString(fecha, lugar, itemsVolquete)

        def parts = precioChoferA.split("&")
        parts = parts[0].split(";")
        def precioChofer = parts[1].toDouble()

        parts = precioVolqueteA.split("&")
        parts = parts[0].split(";")
        def precioVolquete = parts[1].toDouble()

        def rendimientos = preciosService.rendimientoTransporteLuz(obra, precioChofer, precioVolquete)
//        println "rends " + rendimientos
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
        def prmsCellCenter = [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def prmsCellLeft = [border: Color.BLACK, valign: Element.ALIGN_MIDDLE]
        def prmsSubtotal = [border: Color.BLACK, colspan: 6,
                align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def prmsNum = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]

        def prms = [prmsHeaderHoja: prmsHeaderHoja, prmsHeader: prmsHeader, prmsHeader2: prmsHeader2,
                prmsCellHead: prmsCellHead, prmsCell: prmsCellCenter, prmsCellLeft: prmsCellLeft, prmsSubtotal: prmsSubtotal, prmsNum: prmsNum]

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
            addCellTabla(tablaIndirectos, new Paragraph(g.formatNumber(number: indi, minFractionDigits: 2, maxFractionDigits: 2) + "%", fonts.times8normal), prmsNum)
            addCellTabla(tablaIndirectos, new Paragraph(g.formatNumber(number: totalIndi, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), prmsNum)

            tablaTotales.setWidthPercentage(100);
            tablaTotales.setWidths(arregloEnteros([70, 20, 10]))

            addCellTabla(tablaTotales, new Paragraph(" ", fonts.times8bold), prmsHeaderHoja)
            addCellTabla(tablaTotales, new Paragraph("Costo unitario directo", fonts.times8bold), prmsCellLeft)
            addCellTabla(tablaTotales, new Paragraph(g.formatNumber(number: totalRubro, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), prmsNum)

            addCellTabla(tablaTotales, new Paragraph(" ", fonts.times8bold), prmsHeaderHoja)
            addCellTabla(tablaTotales, new Paragraph("Costos indirectos", fonts.times8bold), prmsCellLeft)
            addCellTabla(tablaTotales, new Paragraph(g.formatNumber(number: totalIndi, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), prmsNum)

            addCellTabla(tablaTotales, new Paragraph(" ", fonts.times8bold), prmsHeaderHoja)
            addCellTabla(tablaTotales, new Paragraph("Costos total del rubro", fonts.times8bold), prmsCellLeft)
            addCellTabla(tablaTotales, new Paragraph(g.formatNumber(number: totalRubro + totalIndi, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times8normal), prmsNum)

            addCellTabla(tablaTotales, new Paragraph(" ", fonts.times8bold), prmsHeaderHoja)
            addCellTabla(tablaTotales, new Paragraph("Precio unitario", fonts.times8bold), prmsCellLeft)
            addCellTabla(tablaTotales, new Paragraph(g.formatNumber(number: totalRubro + totalIndi, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0"), fonts.times8normal), prmsNum)


            addTablaHoja(document, headerRubroTabla, false)
            addTablaHoja(document, tablaHerramientas, false)
            addTablaHoja(document, tablaManoObra, false)
            addTablaHoja(document, tablaMateriales, false)
            addTablaHoja(document, tablaTransporte, false)
            addTablaHoja(document, tablaIndirectos, false)
            addTablaHoja(document, tablaTotales, true)

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


    def reporteDocumentosObra () {
//
//           println(params)
//
//
//        println(params.forzarValue)
//
//        println(params.tipoReporte)

//           if (!params.id) {
//            params.id="477"
//        }

        def cd

        def auxiliar = Auxiliar.get(1)


        def nota = Nota.get(params.notaValue)


//        println(nota);

        def obra = Obra.get(params.id)



        def firma

        def firmas

        def cuenta = 0;


        def firma1 = Persona.get(48)
        def firma2 = Persona.get(21)


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

        }else {

            headers.add(new Paragraph("Oficio N°:" + obra?.oficioSalida, times10bold));

        }

        headers.add(new Paragraph("Quito, " + formatDate(date:obra?.fechaOficioSalida, format: "dd-MM-yyyy" ), times10bold));



        Paragraph txtIzq = new Paragraph();
        addEmptyLine(txtIzq, 1);
        txtIzq.setAlignment(Element.ALIGN_LEFT);
        txtIzq.add(new Paragraph(auxiliar?.general, times10bold));

        if (params.tipoReporte == '1') {



            txtIzq.add(new Paragraph(auxiliar?.baseCont, times10bold));

        }
        if(params.tipoReporte == '2'){

            txtIzq.add(new Paragraph(auxiliar?.presupuestoRef, times10bold));


        }



        addEmptyLine(headers, 1);
        document.add(headers);
        document.add(txtIzq);


        PdfPTable tablaPresupuesto = new PdfPTable(4);
        tablaPresupuesto.setWidthPercentage(100);



        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Cant. Obra :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.memoCantidadObra, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Nombre :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.nombre, times8normal), prmsHeaderHoja)


        addCellTabla(tablaPresupuesto, new Paragraph("Fórmula :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.formulaPolinomica, times8normal), prmsHeaderHoja)


        addCellTabla(tablaPresupuesto, new Paragraph("Tipo de Obra :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.tipoObjetivo?.descripcion, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Memo Ref :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.oficioIngreso, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Sitio :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.sitio, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Cantón :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.parroquia?.canton?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Parroquia :", times8bold),prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.parroquia?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Comunidad :", times8bold),prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.comunidad?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("Referencia :", times8bold),prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph(obra?.referencia, times8normal), prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)

        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaPresupuesto, new Paragraph("", times8bold),prmsHeaderHoja)




        PdfPTable tablaVolObra = new PdfPTable(6);
        tablaVolObra.setWidthPercentage(100);


        def detalle

            detalle= VolumenesObra.findAllByObra(obra,[sort:"orden"])
        def subPres = VolumenesObra.findAllByObra(obra,[sort:"orden"]).subPresupuesto.unique()

        def precios = [:]
        def fecha = obra.fechaPreciosRubros
        def dsps = obra.distanciaPeso
        def dsvl = obra.distanciaVolumen
        def lugar = obra.lugar
        def prch = 0
        def prvl = 0
        if (obra.chofer){
            prch = preciosService.getPrecioItems(fecha,lugar,[obra.chofer])
            prch = prch["${obra.chofer.id}"]
            prvl = preciosService.getPrecioItems(fecha,lugar,[obra.volquete])
            prvl = prvl["${obra.volquete.id}"]
        }
        def rendimientos = preciosService.rendimientoTranposrte(dsps,dsvl,prch,prvl)

        if (rendimientos["rdps"].toString()=="NaN")
            rendimientos["rdps"]=0
        if (rendimientos["rdvl"].toString()=="NaN")
            rendimientos["rdvl"]=0

        def indirecto = obra.totales/100

        tablaVolObra.setWidths(arregloEnteros([10,40,5,15,15,15]))

        addCellTabla(tablaVolObra, new Paragraph("Rubro N°", times8bold), prmsCellHead )
        addCellTabla(tablaVolObra, new Paragraph("Descripción", times8bold), prmsCellHead )
        addCellTabla(tablaVolObra, new Paragraph("Unidad", times8bold), prmsCellHead )
        addCellTabla(tablaVolObra, new Paragraph("Cantidad", times8bold), prmsCellHead )
        addCellTabla(tablaVolObra, new Paragraph("P. Unitario", times8bold), prmsCellHead )

        addCellTabla(tablaVolObra, new Paragraph("Costo Total", times8bold), prmsCellHead )

        def c;

        def total1 = 0;

        def totales

        def totalPresupuesto;

        detalle.each{





            def parametros = ""+it.item.id+","+lugar.id+",'"+fecha.format("yyyy-MM-dd")+"',"+dsps.toDouble()+","+dsvl.toDouble()+","+rendimientos["rdps"]+","+rendimientos["rdvl"]
            preciosService.ac_rbro(it.item.id,lugar.id,fecha.format("yyyy-MM-dd"))
            def res = preciosService.rb_precios("sum(parcial)+sum(parcial_t) precio ",parametros,"")
            precios.put(it.id.toString(),res["precio"][0]+res["precio"][0]*indirecto)


            addCellTabla(tablaVolObra, new Paragraph(it?.item?.codigo,times8normal), prmsCellCenter)


            addCellTabla(tablaVolObra, new Paragraph(it?.item?.nombre,times8normal), prmsCellLeft)


            addCellTabla(tablaVolObra, new Paragraph(it?.item?.unidad?.codigo,times8normal), prmsCellCenter)


           addCellTabla(tablaVolObra, new Paragraph(g.formatNumber(number: it?.cantidad, minFractionDigits:
                   2, maxFractionDigits: 2, format: "#####,##0"),times8normal), prmsCellRight)

            addCellTabla(tablaVolObra, new Paragraph (g.formatNumber(number: precios[it.id.toString()], minFractionDigits:
                    2, maxFractionDigits: 2, format: "#####,##0"),times8normal), prmsCellRight)

            addCellTabla(tablaVolObra, new Paragraph (g.formatNumber(number: precios[it.id.toString()]*it.cantidad, minFractionDigits:
                    2, maxFractionDigits: 2, format: "#####,##0"),times8normal), prmsCellRight)



//            println("costo:" + precios[it.id.toString()]*it.cantidad)

            totales =  precios[it.id.toString()]*it.cantidad

//            println("total:" + (total1+=totales) )

            totalPresupuesto = (total1+=totales);


            return totalPresupuesto

        }

//        println(totalPresupuesto)


        PdfPTable tablaTotal = new PdfPTable(6);
        tablaTotal.setWidthPercentage(100);

        tablaTotal.setWidths(arregloEnteros([85,0,0,0,0,15]))

//        addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead )
//        addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsHeaderHoja )
//        addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsHeaderHoja )
//        addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsHeaderHoja )
//        addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsHeaderHoja )
//        addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsHeaderHoja )



        addCellTabla(tablaTotal, new Paragraph("Total del presupuesto: ", times8bold), prmsCellHead )
        addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead )
        addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead )
        addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead )
        addCellTabla(tablaTotal, new Paragraph(" ", times8bold), prmsCellHead )
        addCellTabla(tablaTotal, new Paragraph(g.formatNumber(number: totalPresupuesto, format: "####,###") , times8bold), prmsCellRight)


        Paragraph txtCondiciones = new Paragraph();
        addEmptyLine(txtCondiciones, 1);
        txtCondiciones.setAlignment(Element.ALIGN_LEFT);
        txtCondiciones.add(new Paragraph("CONDICIONES DEL CONTRATO", times10bold));


        PdfPTable tablaCondiciones = new PdfPTable(2);
        tablaCondiciones.setWidthPercentage(100);

        addCellTabla(tablaCondiciones, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaCondiciones, new Paragraph(" ", times8bold), prmsHeaderHoja)


        addCellTabla(tablaCondiciones, new Paragraph("Plazo de Ejecución :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaCondiciones, new Paragraph(g.formatNumber(number: obra?.plazo, format: "##") + " mes(es)", times8normal), prmsHeaderHoja)

        addCellTabla(tablaCondiciones, new Paragraph("Anticipo :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaCondiciones, new Paragraph(g.formatNumber(number: obra?.porcentajeAnticipo, format: "###") + " %", times8normal), prmsHeaderHoja)

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

            }else {

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



        }  else {




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


        if(cuenta == 0) {



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





            document.add(tablaFirmas);



        }


        if(cuenta == 1){


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



            firma.each { f->


                firmas=Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.titulo + " " + firmas?.nombre + " " + firmas?.apellido, times8bold), prmsHeaderHoja)

            }



            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.cargo, times8bold), prmsHeaderHoja)





            firma.each { f->


                firmas=Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.cargo, times8bold), prmsHeaderHoja)




            }

            document.add(tablaFirmas);




        }
        if(cuenta == 2){




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




            addCellTabla(tablaFirmas, new Paragraph(firma1?.titulo + "" + firma1?.nombre + " " + firma1?.apellido, times8bold), prmsHeaderHoja)


            firma.each { f->


                firmas=Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.titulo + "" + firmas?.nombre + " " + firmas?.apellido, times8bold), prmsHeaderHoja)

            }



            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.cargo, times8bold), prmsHeaderHoja)


            firma.each { f->


                firmas=Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.cargo, times8bold), prmsHeaderHoja)




            }

            document.add(tablaFirmas);



        }
//        if(cuenta == 3){
//
//            PdfPTable tablaFirmas = new PdfPTable(3);
//            tablaFirmas.setWidthPercentage(100);
//
//            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
//            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
//
//            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
//            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
//
//            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
//            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
//
//            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
//            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
//
//            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
//            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
//
//            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
//            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
//
//
//            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)
//            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)
//            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)
//
//            firma.each { f->
//
//
//                firmas=Persona.get(f)
//
//                addCellTabla(tablaFirmas, new Paragraph(firmas?.titulo + "" + firmas?.nombre + " " + firmas?.apellido, times8bold), prmsHeaderHoja)
//
//
//
//            }
//
//
//            firma.each { f->
//
//
//                firmas=Persona.get(f)
//
//                addCellTabla(tablaFirmas, new Paragraph(firmas?.cargo, times8bold), prmsHeaderHoja)
//
//
//
//
//            }
//
//            document.add(tablaFirmas);
//
//
//        }
        else {


        }

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)


    }


    def reporteDocumentosObraMemo(){


//        println(params)

        def cd

        def auxiliar = Auxiliar.get(1)

        def auxiliarFijo = Auxiliar.get(1)

        def obra = Obra.get(params.id)

        def firma

        def firmas

        def cuenta = 0;

        def firma1 = Persona.get(48)

        def totalBase = params.totalPresupuesto

        def reajusteBase = params.reajusteMemo

        def tipo = params.tipoReporte


        if (obra?.oficioSalida == null){


            obra?.oficioSalida = "";

        }


        if (reajusteBase == "") {


            reajusteBase = 0;

        }else {

            reajusteBase = params.reajusteMemo

        }



        if(totalBase == "") {


            totalBase = 0;

        } else {

            totalBase = params.totalPresupuesto

        }



        def valorTotal = (totalBase.toDouble() + reajusteBase.toDouble())


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

        if (obra?.claseObra?.tipo == 'C') {

            txtIzq.add(new Paragraph("DE : Dpto Infraestructura Comunitaria " , times10bold));
        }


        if (obra?.claseObra?.tipo == 'V') {

            txtIzq.add(new Paragraph("DE : Dpto de Estudios Viales" , times10bold));
        }

        if(obra?.departamento?.descripcion == null) {

            txtIzq.add(new Paragraph("PARA :" + " ", times10bold));

        }else {

        txtIzq.add(new Paragraph("PARA :" + obra?.departamento?.descripcion, times10bold));


        }
        txtIzq.add(new Paragraph(" ", times10bold));

        txtIzq.add(new Paragraph(auxiliarFijo?.memo1, times8normal));

        addEmptyLine(headers, 1);
        document.add(headers);
        document.add(txtIzq);


        PdfPTable tablaMemo = new PdfPTable(2);
        tablaMemo.setWidthPercentage(100);


        addCellTabla(tablaMemo, new Paragraph(" ", times8bold),prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(" ", times8bold),prmsHeaderHoja)


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



        addCellTabla(tablaMemo, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(" ", times8bold),prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(" ", times8bold),prmsHeaderHoja)

        addCellTabla(tablaMemo, new Paragraph("Objeto :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(obra?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(tablaMemo, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(" ", times8bold),prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(" ", times8bold),prmsHeaderHoja)


        addCellTabla(tablaMemo, new Paragraph("Cantón :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(obra?.parroquia?.canton?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(tablaMemo, new Paragraph("Parroquia :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(obra?.parroquia?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(tablaMemo, new Paragraph("Memo Ref :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(obra?.oficioIngreso, times8normal), prmsHeaderHoja)

        addCellTabla(tablaMemo, new Paragraph("Otras Referencias :", times8bold), prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph(obra?.referencia, times8normal), prmsHeaderHoja)

        addCellTabla(tablaMemo, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph("", times8bold),prmsHeaderHoja)
        addCellTabla(tablaMemo, new Paragraph("", times8bold),prmsHeaderHoja)

        PdfPTable tablaBaseMemo = new PdfPTable(3);
        tablaMemo.setWidthPercentage(100);


        if (tipo == '1') {

            addCellTabla(tablaBaseMemo, new Paragraph("Valor de la Base :", times8bold), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: totalBase, format: "####.#####"), times8normal), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

            addCellTabla(tablaBaseMemo, new Paragraph("Valor del Reajuste :", times8bold), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: reajusteBase, format: "####.#####"), times8normal), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

            addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

            addCellTabla(tablaBaseMemo, new Paragraph("Valor Total :", times8bold), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: valorTotal, format: "####.#####"), times8normal), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

        }

        if ( tipo == '2') {


            addCellTabla(tablaBaseMemo, new Paragraph("Valor del P. Referencial :", times8bold), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: totalBase, format: "####.#####"), times8normal), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)


            addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph("_________________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

            addCellTabla(tablaBaseMemo, new Paragraph("Valor Total :", times8bold), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph(g.formatNumber(number: totalBase, format: "####.#####"), times8normal), prmsHeaderHoja)
            addCellTabla(tablaBaseMemo, new Paragraph(" ", times8normal), prmsHeaderHoja)

        }




        document.add(tablaMemo)
        document.add(tablaBaseMemo)


        Paragraph txtAdicionar = new Paragraph();
        addEmptyLine(txtAdicionar, 1);
        txtAdicionar.setAlignment(Element.ALIGN_LEFT);


        txtAdicionar.add(new Paragraph(" ", times10bold));

        txtAdicionar.add(new Paragraph(auxiliarFijo?.memo2, times8normal));

        document.add(txtAdicionar);

//        println(cuenta)


        if (cuenta == 0) {



            PdfPTable tablaFirmas = new PdfPTable(1);
            tablaFirmas.setWidthPercentage(100);


            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph(" ", times10bold), prmsHeaderHoja)


            addCellTabla(tablaFirmas, new Paragraph("______________________________________", times8bold), prmsHeaderHoja)



            addCellTabla(tablaFirmas, new Paragraph(firma1?.titulo + "" + firma1?.nombre + " " + firma1?.apellido, times8bold), prmsHeaderHoja)




            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)


            document.add(tablaFirmas);



        }



        if(cuenta == 1){


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


            firma.each { f->


                firmas=Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.titulo + " " + firmas?.nombre + " " + firmas?.apellido, times8bold), prmsHeaderHoja)

            }



            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)





            firma.each { f->


                firmas=Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.cargo, times8bold), prmsHeaderHoja)




            }

            document.add(tablaFirmas);




        }
        if(cuenta == 2){




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

            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)

            addCellTabla(tablaFirmas, new Paragraph("_______________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("_______________________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph("_______________________________", times8bold), prmsHeaderHoja)



            addCellTabla(tablaFirmas, new Paragraph(firma1?.titulo + "" + firma1?.nombre + " " + firma1?.apellido, times8bold), prmsHeaderHoja)



            firma.each { f->


                firmas=Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.titulo + "" + firmas?.nombre + " " + firmas?.apellido, times8bold), prmsHeaderHoja)

            }



            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)


            firma.each { f->


                firmas=Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.cargo, times8bold), prmsHeaderHoja)




            }

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

        def auxiliarFijo = Auxiliar.get(1)

        def obra = Obra.get(params.id)

        def firma

        def firmas

        def cuenta = 0;

        def formula = FormulaPolinomica.findAllByObra(obra)

        def ps = FormulaPolinomica.findAllByObraAndNumeroIlike(obra, 'p%')

        def p01 = FormulaPolinomica.findByObraAndNumero(obra, 'p01')
        def p02 = FormulaPolinomica.findByObraAndNumero(obra, 'p02')
        def p03 = FormulaPolinomica.findByObraAndNumero(obra, 'p03')
        def p04 = FormulaPolinomica.findByObraAndNumero(obra, 'p04')
        def p05 = FormulaPolinomica.findByObraAndNumero(obra, 'p05')
        def p06 = FormulaPolinomica.findByObraAndNumero(obra, 'p06')
        def p07 = FormulaPolinomica.findByObraAndNumero(obra, 'p07')
        def p08 = FormulaPolinomica.findByObraAndNumero(obra, 'p08')
        def p09 = FormulaPolinomica.findByObraAndNumero(obra, 'p09')
        def p10 = FormulaPolinomica.findByObraAndNumero(obra, 'p10')
        def px = FormulaPolinomica.findByObraAndNumero(obra, 'px')



        def firma1 = Persona.get(48)
        def firma2 = Persona.get(21)


        def valorCoef = 0;


        ps.valor.each {  i->


            valorCoef = i

            valorCoef+=valorCoef

//            println(i)
//            println(valorCoef)

            return valorCoef

        }




        if (params.firmasIdFormu.trim().size() > 0) {
            firma = params.firmasIdFormu.split(",")
        } else {
            firma = []
        }

        cuenta = firma.size()

        def totalBase = params.totalPresupuesto


        if(obra?.formulaPolinomica == null) {

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
        txtIzq.setAlignment(Element.ALIGN_LEFT);

        txtIzq.add(new Paragraph("De existir variaciones en los costos de los componentes de precios unitarios estipulados en el contrato para la contrucción de: ", times10normal));

        txtIzq.add(new Paragraph(" ", times10bold));

        document.add(txtIzq);

        PdfPTable tablaHeader = new PdfPTable(2);
        tablaHeader.setWidthPercentage(100);

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

        document.add(tablaHeader)

        Paragraph txtIzqHeader = new Paragraph();
        addEmptyLine(txtIzqHeader, 1);
        txtIzqHeader.setAlignment(Element.ALIGN_LEFT);

        txtIzqHeader.add(new Paragraph("Los costos se reajustarán para efecto de pago, mediante la fórmula general: ", times10normal));

        txtIzqHeader.add(new Paragraph(" ", times10bold));

        txtIzqHeader.add(new Paragraph("Pr= Po(p01B1/Bo + p02C1/Co + p03D1/Do + p04E1/Eo + p05F1/Fo + p06G1/Go + p07H1/Ho + p08I1/Io + p09J1/Jo + p10K1/Ko + pxX1/Xo) ", times10normal));

        txtIzqHeader.add(new Paragraph(" ", times10bold));

        txtIzqHeader.add(new Paragraph("Pr= Po(${p01.valor}B1/Bo + ${p02.valor}C1/Co + ${p03.valor}D1/Do + ${p04.valor}E1/Eo + ${p05.valor}F1/Fo +" +
                " ${p06.valor}G1/Go + ${p07.valor}H1/Ho + ${p08.valor}I1/Io + ${p09.valor}J1/Jo + ${p10.valor}K1/Ko + ${px.valor}X1/Xo) ", times10bold));

        document.add(txtIzqHeader)

        PdfPTable tablaCoeficiente = new PdfPTable(5);
        tablaCoeficiente.setWidthPercentage(100);
        tablaCoeficiente.setWidths(arregloEnteros([10, 10, 5, 25, 50]))

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("p01 = ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(g.formatNumber(number: p01.valor, format: "##.####"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("Coeficiente del Componente ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(p01?.indice?.descripcion, times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("p02 = ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(g.formatNumber(number: p02.valor, format: "##.####"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("Coeficiente del Componente ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(p02?.indice?.descripcion, times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("p03 = ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(g.formatNumber(number: p03.valor, format: "##.####"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("Coeficiente del Componente ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(p03?.indice?.descripcion, times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("p04 = ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(g.formatNumber(number: p04.valor, format: "##.####"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("Coeficiente del Componente ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(p04?.indice?.descripcion, times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("p05 = ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(g.formatNumber(number: p05.valor, format: "##.####"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("Coeficiente del Componente ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(p05?.indice?.descripcion, times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("p06 = ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(g.formatNumber(number: p06.valor, format: "##.####"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("Coeficiente del Componente ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(p06?.indice?.descripcion, times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("p07 = ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(g.formatNumber(number: p07.valor, format: "##.####"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("Coeficiente del Componente ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(p07?.indice?.descripcion, times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("p08 = ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(g.formatNumber(number: p08.valor, format: "##.####"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("Coeficiente del Componente ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(p08?.indice?.descripcion, times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("p09 = ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(g.formatNumber(number: p09.valor, format: "##.####"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("Coeficiente del Componente ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(p09?.indice?.descripcion, times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("p10 = ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(g.formatNumber(number: p10.valor, format: "##.####"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("Coeficiente del Componente ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(p10?.indice?.descripcion, times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("px = ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(g.formatNumber(number: px.valor, format: "##.####"), times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("Coeficiente del Componente ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(px?.indice?.descripcion, times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("________", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("___", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph("SUMAN : ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(g.formatNumber(number: valorCoef, format: "##.####"), times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaCoeficiente, new Paragraph(" ", times10bold), prmsHeaderHoja)


        document.add(tablaCoeficiente)
//
//        PdfPTable tablaCuadrilla = new PdfPTable(3);
//        tablaCuadrilla.setWidthPercentage(100);
//        tablaCuadrilla.setWidths(arregloEnteros([10, 60, 30]))
//
//        addCellTabla(tablaCuadrilla, new Paragraph(" ", times10bold), prmsHeaderHoja)
//        addCellTabla(tablaCuadrilla, new Paragraph("CUADRILLA TIPO ", times10bold), prmsHeaderHoja)
//        addCellTabla(tablaCuadrilla, new Paragraph(" ", times10bold), prmsHeaderHoja)
//
//        addCellTabla(tablaCuadrilla, new Paragraph(" ", times10bold), prmsHeaderHoja)
//        addCellTabla(tablaCuadrilla, new Paragraph("CLASE OBRERO ", times10bold), prmsHeaderHoja)
//        addCellTabla(tablaCuadrilla, new Paragraph(" ", times10bold), prmsHeaderHoja)
//
//        addCellTabla(tablaCuadrilla, new Paragraph(" ", times10bold), prmsHeaderHoja)
//        addCellTabla(tablaCuadrilla, new Paragraph(" _________________________", times10bold), prmsHeaderHoja)
//        addCellTabla(tablaCuadrilla, new Paragraph(" ", times10bold), prmsHeaderHoja)
//
//
//        document.add(tablaCuadrilla)


        Paragraph txtIzqPie = new Paragraph();
        addEmptyLine(txtIzqPie, 1);
        txtIzqPie.setAlignment(Element.ALIGN_LEFT);

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
        addCellTabla(tablaPie, new Paragraph(g.formatNumber(number: totalBase, minFractionDigits: 5, maxFractionDigits: 5, format: "##,#####0"), fonts.times10normal), prmsHeaderHoja)

        addCellTabla(tablaPie, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaPie, new Paragraph(" ", times10bold), prmsHeaderHoja)

        addCellTabla(tablaPie, new Paragraph("Atentamente,  ", times10normal), prmsHeaderHoja)
        addCellTabla(tablaPie, new Paragraph(" ", times10normal), prmsHeaderHoja)

        addCellTabla(tablaPie, new Paragraph(" ", times10bold), prmsHeaderHoja)
        addCellTabla(tablaPie, new Paragraph(" ", times10bold), prmsHeaderHoja)


        document.add(tablaPie)

        if(cuenta == 0) {



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





            document.add(tablaFirmas);



        }
        if(cuenta == 1){


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



            firma.each { f->


                firmas=Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.titulo + " " + firmas?.nombre + " " + firmas?.apellido, times8bold), prmsHeaderHoja)

            }



            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.cargo, times8bold), prmsHeaderHoja)





            firma.each { f->


                firmas=Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.cargo, times8bold), prmsHeaderHoja)




            }

            document.add(tablaFirmas);




        }
        if(cuenta == 2){




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


            firma.each { f->


                firmas=Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.titulo + "" + firmas?.nombre + " " + firmas?.apellido, times8bold), prmsHeaderHoja)

            }



            addCellTabla(tablaFirmas, new Paragraph(firma1?.cargo, times8bold), prmsHeaderHoja)
            addCellTabla(tablaFirmas, new Paragraph(firma2?.cargo, times8bold), prmsHeaderHoja)


            firma.each { f->


                firmas=Persona.get(f)

                addCellTabla(tablaFirmas, new Paragraph(firmas?.cargo, times8bold), prmsHeaderHoja)




            }

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


    def documentosObraExcel () {

        def obra = Obra.get(params.id)


        def detalle

        detalle= VolumenesObra.findAllByObra(obra,[sort:"orden"])
        def subPres = VolumenesObra.findAllByObra(obra,[sort:"orden"]).subPresupuesto.unique()

        def precios = [:]
        def fecha = obra.fechaPreciosRubros
        def dsps = obra.distanciaPeso
        def dsvl = obra.distanciaVolumen
        def lugar = obra.lugar
        def prch = 0
        def prvl = 0
        if (obra.chofer){
            prch = preciosService.getPrecioItems(fecha,lugar,[obra.chofer])
            prch = prch["${obra.chofer.id}"]
            prvl = preciosService.getPrecioItems(fecha,lugar,[obra.volquete])
            prvl = prvl["${obra.volquete.id}"]
        }
        def rendimientos = preciosService.rendimientoTranposrte(dsps,dsvl,prch,prvl)

        if (rendimientos["rdps"].toString()=="NaN")
            rendimientos["rdps"]=0
        if (rendimientos["rdvl"].toString()=="NaN")
            rendimientos["rdvl"]=0

        def indirecto = obra.totales/100

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

        def fila = 6;

        label = new Label(0, 2, "Presupuesto de la Obra: " + obra?.nombre.toString(), times16format); sheet.addCell(label);

        label = new Label(0, 4, "RUBRO", times16format); sheet.addCell(label);
        label = new Label(1, 4, "NOMBRE", times16format); sheet.addCell(label);
        label = new Label(2, 4, "UNDD", times16format); sheet.addCell(label);
        label = new Label(3, 4, "CANTIDAD", times16format); sheet.addCell(label);
        label = new Label(4, 4, "P_UNIT", times16format); sheet.addCell(label);
        label = new Label(5, 4, "SUBTOTAL", times16format); sheet.addCell(label);
        label = new Label(6, 4, "SUBP", times16format); sheet.addCell(label);
        label = new Label(7, 4, "SUBPRESUPUESTO", times16format); sheet.addCell(label);
        label = new Label(8, 4, "ORDEN", times16format); sheet.addCell(label);



        detalle.each{

            def parametros = ""+it.item.id+","+lugar.id+",'"+fecha.format("yyyy-MM-dd")+"',"+dsps.toDouble()+","+dsvl.toDouble()+","+rendimientos["rdps"]+","+rendimientos["rdvl"]
            preciosService.ac_rbro(it.item.id,lugar.id,fecha.format("yyyy-MM-dd"))
            def res = preciosService.rb_precios("sum(parcial)+sum(parcial_t) precio ",parametros,"")
            precios.put(it.id.toString(),res["precio"][0]+res["precio"][0]*indirecto)

            def precioUnitario = precios[it.id.toString()]

            def subtotal = (precios[it.id.toString()]*it.cantidad)

//            println(precioUnitario)

            label = new Label(0, fila, it?.item?.codigo.toString()); sheet.addCell(label);
            label = new Label(1, fila, it?.item?.nombre.toString()); sheet.addCell(label);
            label = new Label(2, fila, it?.item?.unidad?.codigo.toString()); sheet.addCell(label);
            label = new Label(3, fila, it?.cantidad.toString()); sheet.addCell(label);
            label = new Label(4, fila, precioUnitario.toString()); sheet.addCell(label);
            label = new Label(5, fila, subtotal.toString()); sheet.addCell(label);
            label = new Label(6, fila, "0"); sheet.addCell(label);
            label = new Label(7, fila, obra?.nombre.toString()); sheet.addCell(label);
            label = new Label(8, fila, "0"); sheet.addCell(label);




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


}
