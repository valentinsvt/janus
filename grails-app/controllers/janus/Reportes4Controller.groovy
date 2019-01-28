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
import janus.seguridad.Shield
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.Label
import jxl.write.NumberFormat
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook


import java.awt.Color


class Reportes4Controller extends Shield {

    def index() {}
    def dbConnectionService
    def preciosService
    def buscadorService
    def reportesService

    def meses = ['', "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"]

    private String printFecha(Date fecha) {
        if (fecha) {
            return (fecha.format("dd") + ' de ' + meses[fecha.format("MM").toInteger()] + ' de ' + fecha.format("yyyy"))
        } else {
            return ""
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
        def perfil = session.perfil.id
        return [perfil: perfil]
    }

    def presuestadasFinal () {
        def perfil = session.perfil.id
        return [perfil: perfil]
    }

    def imprimeMatrizA4() {

        def obra = Obra.get(params.id)
//
//        println "imprime matriz"
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

                } catch (e) {
                    col = parts[0]
                }

                col += parts[1]?.replaceAll("T", " Total")?.replaceAll("U", " Unitario")
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
//            println("col" + columnas)
            filas.add(tmp)
            cont++
        }

        def baos = new ByteArrayOutputStream()
        def name = "matriz_polinomica_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
//            println "name "+name
        Font titleFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font titleFont3 = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font titleFont2 = new Font(Font.TIMES_ROMAN, 16, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
        Font catFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)

        def prmsHeaderHoja = [border: Color.WHITE]

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
        Font small = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        def titulo = obra.desgloseTransporte == "S" ? '(Con desglose de Transporte)' : '(Sin desglose de Transporte)'
//        println titulo
        Paragraph headersTitulo = new Paragraph();
        addEmptyLine(headersTitulo, 1)
        headersTitulo.setAlignment(Element.ALIGN_CENTER);
        headersTitulo.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", titleFont2));
        addEmptyLine(headersTitulo, 1);
        headersTitulo.add(new Paragraph(obra?.departamento?.direccion?.nombre, titleFont));
        addEmptyLine(headersTitulo, 1);
        headersTitulo.add(new Paragraph("MATRIZ DE LA FORMULA POLINÓMICA " + titulo, titleFont));
        addEmptyLine(headersTitulo, 1);

        document.add(headersTitulo)

        PdfPTable tablaHeader = new PdfPTable(3);
        tablaHeader.setWidthPercentage(100);
        tablaHeader.setWidths(arregloEnteros([15, 2, 70]))

        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)


        addCellTabla(tablaHeader, new Paragraph("OBRA", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(obra?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("CÓDIGO", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(obra?.codigo, times8normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("MEMO CANT. OBRA", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(obra?.memoCantidadObra, times8normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("DOC. REFERENCIA", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(obra?.oficioIngreso, times8normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("FECHA", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(printFecha(obra?.fechaCreacionObra).toUpperCase(), times8normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("FECHA ACT. PRECIOS", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(printFecha(obra?.fechaPreciosRubros).toUpperCase(), times8normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)

        document.add(tablaHeader);

        /*table*/

        def parcial = []
        def anchos = [5, 6, 35, 5, 8, 8, 8, 8, 8, 8]     // , 9
        def anchos2 = [5, 6, 35, 5, 8, 8, 8, 8, 8]     // , 9

        def inicio = 0
        def fin = 10

        def inicioCab = 1
        def finCab = 10

//        println "size "+columnas.size()
        while (fin <= columnas.size() + 1) {  //gdo  <= antes

//            println "inicio "+inicio+"  fin  "+fin
//            println "iniciocab "+inicioCab+"  fincab  "+finCab
            if (inicio != 0) {
                anchos = [10, 10, 10, 10, 10, 10, 10, 10, 10, 10]
                anchos2 = [10, 10, 10, 10, 10, 10, 10, 10, 10]
            }

            if (fin - inicio < 10) {
                anchos = []
                (fin - inicio).toInteger().times { i ->
                    anchos.add((100 / (fin - inicio)).toInteger())
                }

                anchos2 = []
                ((fin - inicio).toInteger() - 1).times { i ->
                    anchos2.add((100 / (((fin - inicio).toInteger()) - 1)).toInteger())
                }

            }
            def parrafo = new Paragraph("")
/*
            if (inicio == fin)
               inicio -= 2       //gdo
*/
//            println "anchos "+anchos
//            println "anchos2 "+anchos2
            PdfPTable table = new PdfPTable((fin - inicio).toInteger());       //gdo
//            println("-->>" + (fin-inicio))
            PdfPTable table2 = new PdfPTable(((fin - inicio).toInteger()) - 1);

            def tam = 100
            if(anchos.size()<10)
                tam=(anchos.size()*10).toInteger()
            table.setWidthPercentage(tam);
            table.setWidths(arregloEnteros(anchos))
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            table2.setWidthPercentage(tam);
            table2.setWidths(arregloEnteros(anchos2))
            table2.setHorizontalAlignment(Element.ALIGN_LEFT);

            if (inicio == 0) {
                (finCab - inicioCab).toInteger().times { i ->
//                if(inicio != 0){
//                    println("entro" + i)
//                    println("--->>>"  + i)
//                    println("%%%%"  + inicio)
//                    PdfPCell c1 = new PdfPCell(new Phrase(columnas[((inicio+i)-1)][1], small));
//                    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//                    table.addCell(c1);
//                }
//                if(inicio == 0){
//
//                    PdfPCell c1 = new PdfPCell(new Phrase(columnas[inicio+i][1], small));
//                    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//                    table.addCell(c1);
//                }
//                    println columnas
                    PdfPCell c0 = new PdfPCell(new Phrase(columnas[(inicioCab + i) - 1][1], small));
                    c0.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table2.addCell(c0);
                }
                table2.setHeaderRows(1);
                filas.each { f ->
                    (finCab - inicioCab).toInteger().times { i ->

//                    if(inicio != 0) {
//
//                        def dato = f[(inicio + i)-1]
//                        if (!dato)
//                            dato = "0.00"
//                        else
//                            dato = dato.toString()
//                        def cell = new PdfPCell(new Phrase(dato, small))
//                        cell.setFixedHeight(16f);
//                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
//                        table2.addCell(cell);
//                    }
//                    if(inicio == 0){
//                        def dato = f[(inicio + i)]
//                        if (!dato)
//                            dato = "0.00"
//                        else
//                            dato = dato.toString()
//                        def cell = new PdfPCell(new Phrase(dato, small))
//                        cell.setFixedHeight(16f);
////                        if (i > 3) cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
//                        table2.addCell(cell);
//                    }
                        def fuente = small
                        def borde = 1.5
                        if (f[1]=~"sS") {
                            fuente = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
                        }

                        def dato = f[(inicio + i)]
                        if (!dato)
                            dato = "0.00"
                        else
                            dato = dato.toString()
                        def cell = new PdfPCell(new Phrase(dato, fuente));
                        cell.setFixedHeight(16f);
                        if (f[1] == "sS1")
                            cell.setBorderWidthTop(borde)
                        if (f[1] == "sS2")
                            cell.setBorderWidthBottom(borde)
                        table2.addCell(cell);
                    }
                }

            } else {
                (finCab - inicioCab).toInteger().times { i ->
//                println "columnas "+columnas[(inicioCab + i)-1][1]
                    PdfPCell c1 = new PdfPCell(new Phrase(columnas[(inicioCab + i) - 1][1], small));
                    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(c1);
                }
                table.setHeaderRows(1);
                filas.each { f ->
//                    println "f "+f[1]
                    def fuente = small
                    def borde = 1.5
                    if (f[1]=~"sS") {
                        fuente = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
                    }
                    if (f[1] == "sS1" || f[1] == "sS2")
                        borde = 1.5
                    (fin - inicio).toInteger().times { i ->

                        def dato = f[(inicio + i) - 1]
                        if (!dato)
                            dato = "0.00"
                        else
                            dato = dato.toString()
                        def cell = new PdfPCell(new Phrase(dato, fuente));
                        cell.setFixedHeight(16f);
                        if (f[1] == "sS1")
                            cell.setBorderWidthTop(borde)
                        if (f[1] == "sS2")
                            cell.setBorderWidthBottom(borde)
                        table.addCell(cell);
                    }
                }
            }

            parrafo.add(table2)
            parrafo.add(table);
            document.add(parrafo);
            document.newPage();
//            inicio = fin + 1
            inicio = fin
            fin = inicio + 10

            inicioCab = finCab
            finCab = inicioCab + 10

            if (fin > columnas.size() + 1) {
                fin = columnas.size() + 1
            }
            if (finCab > columnas.size() + 1) {
                finCab = columnas.size() + 1
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

    def tablaRegistradas_old() {
        println("paramsReg" + params)
        def obras
        def sql
        def cn
        def res

        def obrasFiltradas = []

        def total1 = 0;
        def totales
        def totalPresupuestoBien=[];
        def valoresTotales = []

        def valores
        def subPres


        def personasUtfpu = Persona.findAllByDepartamento(Departamento.findByCodigo('UTFPU'))
        def responsableObra

        params.old = params.criterio

        params.criterio = reportesService.limpiaCriterio(params.criterio)

        def sqlBase =  "SELECT\n" +
                "  o.obra__id    id,\n" +
                "  o.obracdgo    codigo, \n" +
                "  o.obranmbr    nombre,\n" +
                "  o.obratipo    tipo,\n" +
                "  o.obrafcha    fecha,\n" +
                "  o.prsn__id    responsable,\n" +
                "  c.cmndnmbr    comunidad,\n" +
                "  p.parrnmbr    parroquia,\n" +
                "  n.cntnnmbr    canton,\n" +
                "  o.obraofsl    oficio,\n" +
                "  o.obrammsl    memo,\n" +
                "  e.dptodscr    elaborado,\n" +
                "  e.dptocdgo    codigodepar,\n" +
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
//            case "tipo":
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
            case "tipo":
                filtroBuscador = " and t.tpobdscr ILIKE ('%${params.criterio}%') "
                break;

        }


        filtro = " where obraetdo='N' "



//        println "====================="
//        println filtro
//        println filtroBuscador
//        println "====================="

        params.criterio = params.old

        sql = sqlBase + filtro + filtroBuscador

        cn = dbConnectionService.getConnection()

        println(sql)
        res = cn.rows(sql.toString())
//        println(res)

        res.each{

/*
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
*/

//           println("--->>" + totalPresupuestoBien)
//            valoresTotales += totalPresupuestoBien
            valoresTotales += preciosService.valor_de_obra(it.id)
        }

//        println("##" + valoresTotales)
//          println("->" + personasUtfpu)



        if(Persona.get(session.usuario.id).departamento?.codigo == 'UTFPU'){

            res.each{
                responsableObra = it.responsable
                if((personasUtfpu.contains(Persona.get(responsableObra))) || it.tipo == 'D'){
                    obrasFiltradas += it
                }
            }
        }else {
            obrasFiltradas = res
        }

//        println("obras filtradas " + obrasFiltradas)
        return [obras: obras, res: obrasFiltradas, valoresTotales: valoresTotales, params:params]
    }

    def tablaRegistradas() {

//        println("presu " + params)

        def cn = dbConnectionService.getConnection()
        def campos = reportesService.obrasPresupuestadas()

        params.old = params.criterio
        params.criterio = reportesService.limpiaCriterio(params.criterio)

        def sql = armaSqlRegistradas(params)
        def obras = cn.rows(sql)

//        println "registro retornados del sql: ${obras.size()}"
        params.criterio = params.old

        return [obras: obras, params: params]
    }

    def armaSqlRegistradas(params){
        def campos = reportesService.obrasPresupuestadas()
        def operador = reportesService.operadores()
//        println("operador " + operador)

        def sqlSelect = "select obra.obra__id, obracdgo, obranmbr, tpobdscr, obrafcha, cntnnmbr, parrnmbr, cmndnmbr, " +
                "dptodscr, obrarefe, obravlor, case when obraetdo = 'N' THEN 'No registrada' end estado " +
                "from obra, tpob, cntn, parr, cmnd, dpto "
        def sqlWhere = "where tpob.tpob__id = obra.tpob__id and cmnd.cmnd__id = obra.cmnd__id and " +
                "parr.parr__id = obra.parr__id and cntn.cntn__id = parr.cntn__id  and " +
                "dpto.dpto__id = obra.dpto__id and obraetdo = 'N'"

        def sqlOrder = "order by obracdgo"

        println "llega params: $params"
        params.nombre = "Código"
        if(campos.find {it.campo == params.buscador}?.size() > 0) {
            def op = operador.find {it.valor == params.operador}
//            println "op: $op"
            sqlWhere += " and ${params.buscador} ${op.operador} ${op.strInicio}${params.criterio}${op.strFin}";
        }
//        println "txWhere: $sqlWhere"
//        println "sql armado: sqlSelect: ${sqlSelect} \n sqlWhere: ${sqlWhere} \n sqlOrder: ${sqlOrder}"
        println "sql: ${sqlSelect} ${sqlWhere} ${sqlOrder}"
        //retorna sql armado:
        "$sqlSelect $sqlWhere $sqlOrder".toString()
    }



    def tablaPresupuestadas_old () {

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

        def obrasFiltradas = []

        def personasUtfpu = Persona.findAllByDepartamento(Departamento.findByCodigo('UTFPU'))
        def responsableObra

        params.old = params.criterio

        params.criterio = reportesService.limpiaCriterio(params.criterio)

        def sqlBase =  "SELECT\n" +
                "  o.obra__id    id,\n" +
                "  o.obracdgo    codigo, \n" +
                "  o.obranmbr    nombre,\n" +
                "  o.obratipo    tipo,\n" +
                "  o.obrafcha    fecha,\n" +
                "  o.prsn__id    responsable,\n" +
                "  c.cmndnmbr    comunidad,\n" +
                "  p.parrnmbr    parroquia,\n" +
                "  n.cntnnmbr    canton,\n" +
                "  o.obraofsl    oficio,\n" +
                "  o.obrammsl    memo,\n" +
                "  e.dptodscr    elaborado,\n" +
                "  e.dptocdgo    codigodepar,\n" +
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
//            case "tipo":
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
            case "tipo":
                filtroBuscador = " and tpobdscr ILIKE ('%${params.criterio}%') "
                break;

        }

        filtro = " where obraetdo='R' "



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

//        res.each{
//
////            println("res " + it)
//
//            totales = 0
//            total1=0
//
//            valores =  preciosService.rbro_pcun_v2(it.id)
//            subPres =  VolumenesObra.findAllByObra(Obra.get(it.id),[sort:"orden"]).subPresupuesto.unique()
//            println("sub " + subPres.size())
//            subPres.each { s->
//                valores.each {
//                    if(it?.sbprdscr == s?.descripcion){
//                        totales = it?.totl
//                        totalPresupuestoBien = (total1 += totales)
//                    }
//                }
//            }
////           println("--->>" + totalPresupuestoBien)
//            valoresTotales += totalPresupuestoBien
//        }

//        println("##" + valoresTotales)

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

        return [obras: obras, res: obrasFiltradas, valoresTotales: valoresTotales, params:params]

    }


    def tablaPresupuestadas() {

//        println("presu " + params)

        def cn = dbConnectionService.getConnection()
        def campos = reportesService.obrasPresupuestadas()

        params.old = params.criterio
        params.criterio = reportesService.limpiaCriterio(params.criterio)

        def sql = armaSqlPresupuestadas(params)
        def obras = cn.rows(sql)

//        println "registro retornados del sql: ${obras.size()}"
        params.criterio = params.old

        return [obras: obras, params: params]
    }

    def armaSqlPresupuestadas(params){
        def campos = reportesService.obrasPresupuestadas()
        def operador = reportesService.operadores()
//        println("operador " + operador)

        def sqlSelect = "select obra.obra__id, obracdgo, obranmbr, tpobdscr, obrafcha, cntnnmbr, parrnmbr, cmndnmbr, " +
                "dptodscr, obrarefe, obravlor, case when obraetdo = 'R' THEN 'Registrada' end estado " +
                "from obra, tpob, cntn, parr, cmnd, dpto "
        def sqlWhere = "where tpob.tpob__id = obra.tpob__id and cmnd.cmnd__id = obra.cmnd__id and " +
                "parr.parr__id = obra.parr__id and cntn.cntn__id = parr.cntn__id  and " +
                "dpto.dpto__id = obra.dpto__id and obraetdo = 'R'"

        def sqlOrder = "order by obracdgo"

        println "llega params: $params"
        params.nombre = "Código"
        if(campos.find {it.campo == params.buscador}?.size() > 0) {
            def op = operador.find {it.valor == params.operador}
//            println "op: $op"
            sqlWhere += " and ${params.buscador} ${op.operador} ${op.strInicio}${params.criterio}${op.strFin}";
        }
//        println "txWhere: $sqlWhere"
//        println "sql armado: sqlSelect: ${sqlSelect} \n sqlWhere: ${sqlWhere} \n sqlOrder: ${sqlOrder}"
        println "sql: ${sqlSelect} ${sqlWhere} ${sqlOrder}"
        //retorna sql armado:
        "$sqlSelect $sqlWhere $sqlOrder".toString()
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

        def cn = dbConnectionService.getConnection()
        def campos = reportesService.obrasPresupuestadas()

        params.old = params.criterio
        params.criterio = reportesService.limpiaCriterio(params.criterio)

        def sql = armaSqlRegistradas(params)
        def obras = cn.rows(sql)

//        println "registro retornados del sql: ${obras.size()}"
        params.criterio = params.old


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
        headers.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", times18bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("REPORTE DE OBRAS INGRESADAS", times12bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("AL " + printFecha(new Date()).toUpperCase(), times12bold));
        addEmptyLine(headers, 1);
        document.add(headers);

        PdfPTable tablaRegistradas = new PdfPTable(9);
        tablaRegistradas.setWidthPercentage(100);
        tablaRegistradas.setWidths(arregloEnteros([14, 35, 18, 8, 30, 10, 10,14,8]))

        addCellTabla(tablaRegistradas, new Paragraph("Código", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Nombre", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Tipo", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Fecha Reg.", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Cantón-Parroquia-Comunidad", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Valor", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Requirente", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Doc. Referencia", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Estado", times8bold), prmsCellHead2)

        obras.eachWithIndex {i,j->
            addCellTabla(tablaRegistradas, new Paragraph(i.obracdgo, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.obranmbr, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.tpobdscr, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatDate(date: i?.obrafcha, format: "dd-MM-yyyy"), times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.cntnnmbr + "-" + i.parrnmbr + "-" + i.cmndnmbr, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: i.obravlor, minFractionDigits:
                        2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times8normal), prmsCellRight)
            addCellTabla(tablaRegistradas, new Paragraph(i.dptodscr, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.obrarefe, times8normal), prmsCellLeft)
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


    def reportePresupuestadas () {

        //        println("params reporte:" + params)

        def cn = dbConnectionService.getConnection()
        def campos = reportesService.obrasPresupuestadas()

        params.old = params.criterio
        params.criterio = reportesService.limpiaCriterio(params.criterio)

        def sql = armaSqlPresupuestadas(params)
        def obras = cn.rows(sql)

        params.criterio = params.old

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
        def name = "presupuestadas_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
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
        document.addTitle("ObrasPresupuestadas " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("documentosObra, janus, presupuesto");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");


        Paragraph headers = new Paragraph();
        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);
        headers.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", times18bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("REPORTE DE OBRAS PRESUPUESTADAS", times12bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("AL " + printFecha(new Date()).toUpperCase(), times12bold));
        addEmptyLine(headers, 1);
        document.add(headers);

        PdfPTable tablaRegistradas = new PdfPTable(9);
        tablaRegistradas.setWidthPercentage(100);
        tablaRegistradas.setWidths(arregloEnteros([14, 35, 18, 8, 25, 13, 12,14,8]))

        addCellTabla(tablaRegistradas, new Paragraph("Código", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Nombre", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Tipo", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Fecha Reg.", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Cantón-Parroquia-Comunidad", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Valor", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Requirente", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Doc. Referencia", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Estado", times8bold), prmsCellHead2)

        obras.eachWithIndex {i,j->
            addCellTabla(tablaRegistradas, new Paragraph(i.obracdgo, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.obranmbr, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.tpobdscr, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatDate(date: i?.obrafcha, format: "dd-MM-yyyy"), times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.cntnnmbr + "-" + i.parrnmbr + "-" + i.cmndnmbr, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: i.obravlor.toDouble(), minFractionDigits:
                        2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times8normal), prmsCellRight)
            addCellTabla(tablaRegistradas, new Paragraph(i.dptodscr, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.obrarefe, times8normal), prmsCellLeft)
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

        def cn = dbConnectionService.getConnection()
        def campos = reportesService.obrasPresupuestadas()

        params.old = params.criterio
        params.criterio = reportesService.limpiaCriterio(params.criterio)

        def sql = armaSqlRegistradas(params)
        def obras = cn.rows(sql)

//        println "registro retornados del sql: ${obras.size()}"
        params.criterio = params.old

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
        sheet.setColumnView(6, 25)
        sheet.setColumnView(7, 15)
        sheet.setColumnView(8, 15)
        // inicia textos y numeros para asocias a columnas

        def label
        def nmro
        def number

        def fila = 6;

        NumberFormat nf = new NumberFormat("#.##");
        WritableCellFormat cf2obj = new WritableCellFormat(nf);

        label = new Label(1, 1, "SEP - G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(1, 2, "REPORTE EXCEL OBRAS INGRESADAS", times16format); sheet.addCell(label);

        label = new Label(0, 4, "Código: ", times16format); sheet.addCell(label);
        label = new Label(1, 4, "Nombre", times16format); sheet.addCell(label);
        label = new Label(2, 4, "Tipo", times16format); sheet.addCell(label);
        label = new Label(3, 4, "Fecha Reg.", times16format); sheet.addCell(label);
        label = new Label(4, 4, "Cantón-Parroquia-Comunidad", times16format); sheet.addCell(label);
        label = new Label(5, 4, "Valor", times16format); sheet.addCell(label);
        label = new Label(6, 4, "Requirente", times16format); sheet.addCell(label);
        label = new Label(7, 4, "Doc. Referencia", times16format); sheet.addCell(label);
        label = new Label(8, 4, "Estado", times16format); sheet.addCell(label);

        obras.eachWithIndex {i, j->
            label = new Label(0, fila, i.obracdgo.toString()); sheet.addCell(label);
            label = new Label(1, fila, i.obranmbr.toString()); sheet.addCell(label);
            label = new Label(2, fila, i.tpobdscr.toString()); sheet.addCell(label);
            label = new Label(3, fila, i.obrafcha.toString()); sheet.addCell(label);
            label = new Label(4, fila, i.cntnnmbr.toString() + " " + i.parrnmbr.toString() + " " + i?.cmndnmbr?.toString()); sheet.addCell(label);
            number = new jxl.write.Number(5, fila, i.obravlor ); sheet.addCell(number);
            label = new Label(6, fila, i?.dptodscr?.toString()); sheet.addCell(label);
            label = new Label(7, fila, i?.obrarefe?.toString()); sheet.addCell(label);
            label = new Label(8, fila, i.estado); sheet.addCell(label);
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


    def reporteExcelPresupuestadas () {

        def cn = dbConnectionService.getConnection()
        def campos = reportesService.obrasPresupuestadas()

        params.old = params.criterio
        params.criterio = reportesService.limpiaCriterio(params.criterio)

        def sql = armaSqlPresupuestadas(params)
        def obras = cn.rows(sql)

        params.criterio = params.old

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
        sheet.setColumnView(6, 25)
        sheet.setColumnView(7, 15)
        sheet.setColumnView(8, 15)
        // inicia textos y numeros para asocias a columnas

        def label
        def nmro
        def number

        def fila = 6;


        NumberFormat nf = new NumberFormat("#.##");
        WritableCellFormat cf2obj = new WritableCellFormat(nf);

        label = new Label(1, 1, "SEP - G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(1, 2, "REPORTE EXCEL OBRAS PRESUPUESTADAS", times16format); sheet.addCell(label);



        label = new Label(0, 4, "Código: ", times16format); sheet.addCell(label);
        label = new Label(1, 4, "Nombre", times16format); sheet.addCell(label);
        label = new Label(2, 4, "Tipo", times16format); sheet.addCell(label);
        label = new Label(3, 4, "Fecha Reg.", times16format); sheet.addCell(label);
        label = new Label(4, 4, "Cantón-Parroquia-Comunidad", times16format); sheet.addCell(label);
        label = new Label(5, 4, "Valor", times16format); sheet.addCell(label);
        label = new Label(6, 4, "Requirente", times16format); sheet.addCell(label);
        label = new Label(7, 4, "Doc. Referencia", times16format); sheet.addCell(label);
        label = new Label(8, 4, "Estado", times16format); sheet.addCell(label);

        obras.eachWithIndex {i, j->
            label = new Label(0, fila, i.obracdgo.toString()); sheet.addCell(label);
            label = new Label(1, fila, i.obranmbr.toString()); sheet.addCell(label);
            label = new Label(2, fila, i?.tpobdscr?.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.obrafcha?.toString()); sheet.addCell(label);
            label = new Label(4, fila, i?.cntnnmbr?.toString() + " " + i?.parrnmbr?.toString() + " " + i?.cmndnmbr?.toString()); sheet.addCell(label);
            number = new jxl.write.Number(5, fila, i.obravlor); sheet.addCell(number);
            label = new Label(6, fila, i?.dptodscr?.toString()); sheet.addCell(label);
            label = new Label(7, fila, i?.obrarefe?.toString()); sheet.addCell(label);
            label = new Label(8, fila, i.estado.toString()); sheet.addCell(label);
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
/*
        def perfil = session.perfil.id
        return [perfil: perfil]
*/
    }


    def tablaContratadas () {
//        println "tablaContratadas ok $params , ${reportesService.obrasContratadas()}"
        def cn = dbConnectionService.getConnection()
        def campos = reportesService.obrasContratadas()

        params.old = params.criterio
        params.criterio = reportesService.limpiaCriterio(params.criterio)

        def sql = armaSqlContratadas(params)
        def obras = cn.rows(sql)

//        println "registro retornados del sql: ${obras.size()}"
        params.criterio = params.old
        return [obras: obras, params: params]
    }

    def armaSqlContratadas(params){
        def campos = reportesService.obrasContratadas()
        def operador = reportesService.operadores()
//        println("operador " + operador)

        def sqlSelect = "select obra.obra__id, obracdgo, obranmbr, tpobdscr, obrafcha, cntnnmbr, parrnmbr, cmndnmbr, " +
                "cntrmnto, dptodscr, cntrcdgo " +
                "from obra, tpob, cntn, parr, cmnd, cncr, ofrt, cntr, dpto "
        def sqlWhere = "where tpob.tpob__id = obra.tpob__id and cmnd.cmnd__id = obra.cmnd__id and " +
                "parr.parr__id = obra.parr__id and cntn.cntn__id = parr.cntn__id  and " +
                "cncr.obra__id = obra.obra__id and ofrt.cncr__id = cncr.cncr__id and " +
                "cntr.ofrt__id = ofrt.ofrt__id and dpto.dpto__id = obra.dpto__id "

        def sqlOrder = "order by obracdgo"

        println "llega params: $params"
        params.nombre = "Código"
        if(campos.find {it.campo == params.buscador}?.size() > 0) {
            def op = operador.find {it.valor == params.operador}
//            println "op: $op"
            sqlWhere += " and ${params.buscador} ${op.operador} ${op.strInicio}${params.criterio}${op.strFin}";
        }
//        println "txWhere: $sqlWhere"
//        println "sql armado: sqlSelect: ${sqlSelect} \n sqlWhere: ${sqlWhere} \n sqlOrder: ${sqlOrder}"
        //retorna sql armado:
        "$sqlSelect $sqlWhere $sqlOrder".toString()
    }


    def reporteContratadas () {

//        println("params contratadas " + params)

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
        headers.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", times18bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("REPORTE DE OBRAS CONTRATADAS", times12bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("Quito, " + printFecha(new Date()).toUpperCase(), times12bold));
        addEmptyLine(headers, 1);
        document.add(headers);

        PdfPTable tablaRegistradas = new PdfPTable(8);
        tablaRegistradas.setWidthPercentage(100);
        tablaRegistradas.setWidths(arregloEnteros([14, 30, 15, 25, 15, 10, 15, 10]))

        addCellTabla(tablaRegistradas, new Paragraph("Código", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Nombre", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Tipo", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Cantón-Parroquia-Comunidad", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Valor", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Fecha Contrato", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Coordinación", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Contrato", times8bold), prmsCellHead2)

        def cn = dbConnectionService.getConnection()

        params.old = params.criterio
        params.criterio = reportesService.limpiaCriterio(params.criterio)
        def sql2 = armaSqlContratadas(params)
        def nuevoRes = cn.rows(sql2)
        params.criterio = params.old

        //reporte

        nuevoRes.eachWithIndex {i,j->

            addCellTabla(tablaRegistradas, new Paragraph(i.obracdgo, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.obranmbr, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.tpobdscr, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.cntnnmbr + "-" + i.parrnmbr + "-" + i.cmndnmbr, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: i.cntrmnto.toDouble(), minFractionDigits:
                    2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times8normal), prmsCellRight)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatDate(date: i.obrafcha, format: "dd-MM-yyyy"), times8normal), prmsCellRight)
            addCellTabla(tablaRegistradas, new Paragraph(i.dptodscr, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.cntrcdgo, times8normal), prmsCellLeft)

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

    def reporteExcelContratadas () {


        def cn = dbConnectionService.getConnection()

        params.old = params.criterio
        params.criterio = reportesService.limpiaCriterio(params.criterio)
        def sql2 = armaSqlContratadas(params)
        def nuevoRes = cn.rows(sql2)
        params.criterio = params.old


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

        label = new Label(1, 1, "SEP - G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(1, 2, "REPORTE EXCEL OBRAS CONTRATADAS", times16format); sheet.addCell(label);

        label = new Label(0, 4, "Código: ", times16format); sheet.addCell(label);
        label = new Label(1, 4, "Nombre", times16format); sheet.addCell(label);
        label = new Label(2, 4, "Tipo", times16format); sheet.addCell(label);
        label = new Label(3, 4, "Cantón-Parroquia-Comunidad", times16format); sheet.addCell(label);
        label = new Label(4, 4, "Valor", times16format); sheet.addCell(label);
        label = new Label(5, 4, "Fecha Contrato", times16format); sheet.addCell(label);
        label = new Label(6, 4, "Coordinación", times16format); sheet.addCell(label);
        label = new Label(7, 4, "Contrato", times16format); sheet.addCell(label);

        nuevoRes.eachWithIndex {i, j->

            label = new Label(0, fila, i.obracdgo.toString()); sheet.addCell(label);
            label = new Label(1, fila, i?.obranmbr.toString()); sheet.addCell(label);
            label = new Label(2, fila, i?.tpobdscr?.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.cntnnmbr?.toString() + " " + i?.parrnmbr?.toString() + " " + i?.cmndnmbr?.toString()); sheet.addCell(label);
            number = new jxl.write.Number(4, fila, i.cntrmnto); sheet.addCell(number);
            label = new Label(5, fila, i?.obrafcha?.toString()); sheet.addCell(label);
            label = new Label(6, fila, i?.dptodscr?.toString()); sheet.addCell(label);
            label = new Label(7, fila, i.cntrcdgo?.toString()); sheet.addCell(label);
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

        def perfil = session.perfil.id
        return [perfil: perfil]
    }


    def tablaAseguradoras () {

        def obras = []

        def sql
        def cn
        def res

        params.old = params.criterio

        params.criterio = reportesService.limpiaCriterio(params.criterio)

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
        document = new Document(PageSize.A4);
        document.setMargins(56.2, 56.2, 50, 28.1);
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
        headers.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", times18bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("REPORTE DE ASEGURADORAS", times12bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("AL " + printFecha(new Date()).toUpperCase(), times12bold));
        addEmptyLine(headers, 1);
        document.add(headers);

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
        tablaRegistradas.setWidths(arregloEnteros([20, 2, 70]))


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

            addCellTabla(tablaRegistradas, new Paragraph("Fecha Contacto", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(printFecha(it?.fecha), times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Observaciones", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.observaciones, times10normal), prmsCellLeft)

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
        sheet.setColumnView(1, 40)
        sheet.setColumnView(2, 25)
        sheet.setColumnView(3, 25)
        sheet.setColumnView(4, 20)
        sheet.setColumnView(5, 25)
        sheet.setColumnView(6, 20)
        sheet.setColumnView(7, 20)
        // inicia textos y numeros para asocias a columnas

        def label
        def nmro
        def number

        def fila = 6;


        NumberFormat nf = new NumberFormat("#.##");
        WritableCellFormat cf2obj = new WritableCellFormat(nf);

        label = new Label(1, 1, "SEP - G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(1, 2, "REPORTE EXCEL ASEGURADORAS", times16format); sheet.addCell(label);



        label = new Label(0, 4, "Tipo: ", times16format); sheet.addCell(label);
        label = new Label(1, 4, "Nombre", times16format); sheet.addCell(label);
        label = new Label(2, 4, "Dirección", times16format); sheet.addCell(label);
        label = new Label(3, 4, "Teléfono", times16format); sheet.addCell(label);
        label = new Label(4, 4, "Fax", times16format); sheet.addCell(label);
        label = new Label(5, 4, "Contacto", times16format); sheet.addCell(label);
        label = new Label(6, 4, "Fecha Contacto", times16format); sheet.addCell(label);
        label = new Label(7, 4, "Observaciones", times16format); sheet.addCell(label);


        res.eachWithIndex {i, j->


            label = new Label(0, fila, i?.tipoaseguradora?.toString()); sheet.addCell(label);
            label = new Label(1, fila, i?.nombre.toString()); sheet.addCell(label);
            label = new Label(2, fila, i?.direccion?.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.telefono?.toString()); sheet.addCell(label);
            label = new Label(4, fila, i?.fax?.toString()); sheet.addCell(label);
            label = new Label(5, fila, i?.contacto?.toString()); sheet.addCell(label);
            label = new Label(6, fila, i?.fecha?.toString()); sheet.addCell(label);
            label = new Label(7, fila, i?.observaciones?.toString()); sheet.addCell(label);

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

        def perfil = session.perfil.id

        return [perfil: perfil]



    }

    def tablaContratistas (){

        def sql
        def cn
        def res
        params.old = params.criterio
        params.criterio = reportesService.limpiaCriterio(params.criterio)

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
                "  p.prvefccn    fecha,\n" +
                "  f.cntrfcsb    fechacontrato\n" +
                "FROM prve p\n" +
                "  LEFT JOIN espc e ON p.espc__id = e.espc__id\n"+
                "  LEFT JOIN ofrt o ON p.prve__id = o.prve__id\n"+
                "  LEFT JOIN cntr f ON o.ofrt__id = f.ofrt__id\n"

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
//        println(sql)
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
                "  p.prvefccn    fecha,\n" +
                "  f.cntrfcsb    fechacontrato\n" +
                "FROM prve p\n" +
                "  LEFT JOIN espc e ON p.espc__id = e.espc__id\n"+
                "  LEFT JOIN ofrt o ON p.prve__id = o.prve__id\n"+
                "  LEFT JOIN cntr f ON o.ofrt__id = f.ofrt__id\n"

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
        document = new Document(PageSize.A4);
        document.setMargins(56.2, 56.2, 50, 28.1);
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
        headers.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", times18bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("REPORTE DE CONTRATISTAS", times12bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("AL " + printFecha(new Date()).toUpperCase(), times12bold));
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
        tablaRegistradas.setWidths(arregloEnteros([15, 2, 65]))


        res.each {

            addCellTabla(tablaRegistradas, new Paragraph("Nombre", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.nombre, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Cédula/RUC", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.ruc, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Siglas", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.sigla, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Título", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.titulo, times10normal), prmsCellLeft)

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

            addCellTabla(tablaRegistradas, new Paragraph("Garante", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(it?.garante, times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Fecha Cont.", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(printFecha(it?.fecha), times10normal), prmsCellLeft)

            addCellTabla(tablaRegistradas, new Paragraph("Fecha Contrato", times10bold), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(" : ", times10normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(printFecha(it?.fechacontrato), times10normal), prmsCellLeft)



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
                "  p.prvefccn    fecha,\n" +
                "  f.cntrfcsb    fechacontrato\n" +
                "FROM prve p\n" +
                "  LEFT JOIN espc e ON p.espc__id = e.espc__id\n"+
                "  LEFT JOIN ofrt o ON p.prve__id = o.prve__id\n"+
                "  LEFT JOIN cntr f ON o.ofrt__id = f.ofrt__id\n"

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
        sheet.setColumnView(0, 35)
        sheet.setColumnView(1, 20)
        sheet.setColumnView(2, 10)
        sheet.setColumnView(3, 10)
        sheet.setColumnView(4, 15)
        sheet.setColumnView(5, 15)
        sheet.setColumnView(6, 35)
        sheet.setColumnView(7, 35)
        sheet.setColumnView(8, 15)
        sheet.setColumnView(9, 30)
        sheet.setColumnView(10, 20)
        sheet.setColumnView(11, 20)
        // inicia textos y numeros para asocias a columnas

        def label
        def nmro
        def number

        def fila = 6;


        NumberFormat nf = new NumberFormat("#.##");
        WritableCellFormat cf2obj = new WritableCellFormat(nf);

        label = new Label(1, 1, "SEP - G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(1, 2, "REPORTE EXCEL CONTRATISTAS", times16format); sheet.addCell(label);

        label = new Label(0, 4, "Nombre: ", times16format); sheet.addCell(label);
        label = new Label(1, 4, "Cédula/RUC", times16format); sheet.addCell(label);
        label = new Label(2, 4, "Siglas", times16format); sheet.addCell(label);
        label = new Label(3, 4, "Título", times16format); sheet.addCell(label);
        label = new Label(4, 4, "Especialidad", times16format); sheet.addCell(label);
        label = new Label(5, 4, "Cámara", times16format); sheet.addCell(label);
        label = new Label(6, 4, "Contacto", times16format); sheet.addCell(label);
        label = new Label(7, 4, "Dirección", times16format); sheet.addCell(label);
        label = new Label(8, 4, "Teléfono", times16format); sheet.addCell(label);
        label = new Label(9, 4, "Garante", times16format); sheet.addCell(label);
        label = new Label(10, 4, "Fecha Cont.", times16format); sheet.addCell(label);
        label = new Label(11, 4, "Fecha Contrato", times16format); sheet.addCell(label);


        res.eachWithIndex {i, j->
            label = new Label(0, fila, i.nombre.toString()); sheet.addCell(label);
            label = new Label(1, fila, i?.ruc.toString()); sheet.addCell(label);
            label = new Label(2, fila, i?.sigla?.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.titulo.toString()); sheet.addCell(label);
            label = new Label(4, fila, i?.especialidad?.toString()); sheet.addCell(label);
            label = new Label(5, fila, i?.camara?.toString()); sheet.addCell(label);
            label = new Label(6, fila, i?.nombrecon?.toString() + " " + i?.apellidocon?.toString()); sheet.addCell(label);
            label = new Label(7, fila, i?.direccion?.toString()); sheet.addCell(label);
            label = new Label(8, fila, i?.telefono?.toString()); sheet.addCell(label);
            label = new Label(9, fila, i?.garante?.toString()); sheet.addCell(label);
            label = new Label(10, fila, i?.fecha?.toString()); sheet.addCell(label);
            label = new Label(11, fila, i?.fechacontrato?.toString()); sheet.addCell(label);

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

        def perfil = session.perfil.id
        return [perfil: perfil]

    }

    def tablaContratos () {

//        println(params)

        def sql
        def res
        def cn

        params.old = params.criterio

        params.criterio = reportesService.limpiaCriterio(params.criterio)


        def sqlBase =  "SELECT\n" +
                "  c.cntr__id    id,\n" +
                "  c.cntrcdgo    codigo, \n" +
                "  c.cntrmemo    memo,\n" +
                "  c.cntrfcsb    fechasu,\n" +
                "  r.cncrcdgo    concurso,\n" +
                "  o.obracdgo    obracodigo,\n" +
                "  o.obratipo     tipo,\n" +
                "  o.obranmbr    obranombre,\n" +
                "  k.dptocdgo    codigodepar,\n"+
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
                "  LEFT JOIN dpto k ON o.dpto__id = k.dpto__id\n" +
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
//            case "mnto":
                buscador = "cntr"+params.buscador
                filtroBuscador =" where ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "mnto":
                filtroBuscador = " where c.cntrmnto= '${params.criterio}' "
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

                if(params.fecha){

                    def fecha =new Date().parse("dd-MM-yyyy", params.fecha)
                    params.fecha = fecha
                    def dia = formatDate(date: fecha, format: "yyyy-MM-dd")
                    filtroBuscador = " where b.prinfcin= '${dia}' "
                }

                break;
            case "fin":
                if(params.fecha){

                    def fecha =new Date().parse("dd-MM-yyyy", params.fecha)
                    params.fecha = fecha
                    def dia = formatDate(date: fecha, format: "yyyy-MM-dd")
                    filtroBuscador = " where b.prinfcfn= '${dia}' "
                }

                break;
            case "fcsb":

                if(params.fecha){

                    def fecha =new Date().parse("dd-MM-yyyy", params.fecha)
                    params.fecha = fecha
                    def dia = formatDate(date: fecha, format: "yyyy-MM-dd")
                    filtroBuscador = " where c.cntrfcsb= '${dia}' "
                }


                break;

        }


        params.criterio = params.old

        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())


        def obrasFiltradas = []

        res.each{
            if(it.codigodepar == 'UTFPU' || it.tipo == 'D'){
                obrasFiltradas += it
            }
        }


//        return [res: obrasFiltradas, params:params]
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
        headers.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", times18bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("REPORTE DE CONTRATOS", times12bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("Quito, " + printFecha(new Date()).toUpperCase(), times12bold));
        addEmptyLine(headers, 1);
        document.add(headers);

        PdfPTable tablaRegistradas = new PdfPTable(11);
        tablaRegistradas.setWidthPercentage(100);
        tablaRegistradas.setWidths(arregloEnteros([14, 14,15, 15,30, 25,15, 20, 15, 10, 15]))

        addCellTabla(tablaRegistradas, new Paragraph("N° Contrato", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Fecha Suscripcion", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Concurso", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Obra", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Nombre de la Obra", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Cantón - Parroquia - Comunidad", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Clase de Obra", times8bold), prmsCellHead2)
//        addCellTabla(tablaRegistradas, new Paragraph("Tipo de Contrato", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Contratista", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Monto", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("% Anticipo", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Anticipo", times8bold), prmsCellHead2)
//        addCellTabla(tablaRegistradas, new Paragraph("Fecha Inicio", times8bold), prmsCellHead2)
//        addCellTabla(tablaRegistradas, new Paragraph("Fecha Fin", times8bold), prmsCellHead2)
//        addCellTabla(tablaRegistradas, new Paragraph("Plazo", times8bold), prmsCellHead2)

        switch (params.buscador) {
            case "cdgo":
            case "memo":
//            case "fcsb":
            case "ofsl":
//            case "mnto":
                buscador = "cntr"+params.buscador
                filtroBuscador =" where ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "cncr":
                filtroBuscador = " where r.cncrcdgo ILIKE ('%${params.criterio}%') "
                break;
            case "mnto":
                filtroBuscador = " where c.cntrmnto= '${params.criterio}' "
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

                if(params.fecha){

                    def fecha =new Date().parse("dd-MM-yyyy", params.fecha)
                    params.fecha = fecha
                    def dia = formatDate(date: fecha, format: "yyyy-MM-dd")
                    filtroBuscador = " where b.prinfcin= '${dia}' "
                }

                break;
            case "fin":
                if(params.fecha){

                    def fecha =new Date().parse("dd-MM-yyyy", params.fecha)
                    params.fecha = fecha
                    def dia = formatDate(date: fecha, format: "yyyy-MM-dd")
                    filtroBuscador = " where b.prinfcfn= '${dia}' "
                }

                break;
            case "fcsb":

                if(params.fecha){

                    def fecha =new Date().parse("dd-MM-yyyy", params.fecha)
                    params.fecha = fecha
                    def dia = formatDate(date: fecha, format: "yyyy-MM-dd")
                    filtroBuscador = " where c.cntrfcsb= '${dia}' "
                }


                break;


        }

        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())



        res.eachWithIndex {i,j->
            addCellTabla(tablaRegistradas, new Paragraph(i.codigo, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatDate(date: i.fechasu, format: "dd-MM-yyyy"), times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.concurso, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.obracodigo, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.obranombre, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.canton + "-" + i.parroquia, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.tipoobra, times8normal), prmsCellLeft)
//            addCellTabla(tablaRegistradas, new Paragraph(i.tipocontrato, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.nombrecontra, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: i.monto, minFractionDigits:
                    2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times8normal), prmsCellRight)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: i.porcentaje, minFractionDigits:
                    0, maxFractionDigits: 0, format: "##,##0", locale: "ec"), times8normal), prmsCellRight)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: i.anticipo, minFractionDigits:
                    2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times8normal), prmsCellRight)
//            addCellTabla(tablaRegistradas, new Paragraph(g.formatDate(date: i.fechainicio, format: "dd-MM-yyyy"), times8normal), prmsCellLeft)
//            addCellTabla(tablaRegistradas, new Paragraph(g.formatDate(date: i.fechafin, format: "dd-MM-yyyy"), times8normal), prmsCellLeft)
//            addCellTabla(tablaRegistradas, new Paragraph(i.plazo, times8normal), prmsCellLeft)
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
        sheet.setColumnView(1, 20)
        sheet.setColumnView(2, 25)
        sheet.setColumnView(3, 25)
        sheet.setColumnView(4, 40)
        sheet.setColumnView(5, 25)
        sheet.setColumnView(8, 20)
        sheet.setColumnView(9, 10)
        sheet.setColumnView(10, 10)
        sheet.setColumnView(11, 15)
        sheet.setColumnView(12, 15)
        sheet.setColumnView(13, 15)
        sheet.setColumnView(14, 10)
        // inicia textos y numeros para asocias a columnas

        def label
        def nmro
        def number

        def fila = 6;


        NumberFormat nf = new NumberFormat("#.##");
        WritableCellFormat cf2obj = new WritableCellFormat(nf);

        label = new Label(1, 1, "SEP - G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(1, 2, "REPORTE EXCEL CONTRATOS", times16format); sheet.addCell(label);



        label = new Label(0, 4, "N° Contrato: ", times16format); sheet.addCell(label);
        label = new Label(1, 4, "Fecha Suscripción", times16format); sheet.addCell(label);
        label = new Label(2, 4, "Memo", times16format); sheet.addCell(label);
        label = new Label(3, 4, "Obra", times16format); sheet.addCell(label);
        label = new Label(4, 4, "Nombre de la Obra", times16format); sheet.addCell(label);
        label = new Label(5, 4, "Cantón-Parroquia-Comunidad", times16format); sheet.addCell(label);
        label = new Label(6, 4, "Clase de Obra", times16format); sheet.addCell(label);
        label = new Label(7, 4, "Tipo de Obra", times16format); sheet.addCell(label);
        label = new Label(8, 4, "Contratista", times16format); sheet.addCell(label);
        label = new Label(9, 4, "Monto", times16format); sheet.addCell(label);
        label = new Label(10, 4, "% Anticipo", times16format); sheet.addCell(label);
        label = new Label(11, 4, "Anticipo", times16format); sheet.addCell(label);
        label = new Label(12, 4, "Fecha Inicio", times16format); sheet.addCell(label);
        label = new Label(13, 4, "Fecha Fin", times16format); sheet.addCell(label);
        label = new Label(14, 4, "Plazo", times16format); sheet.addCell(label);



        res.eachWithIndex {i, j->
            label = new Label(0, fila, i?.codigo?.toString()); sheet.addCell(label);
            label = new Label(1, fila, i?.fechasu?.toString()); sheet.addCell(label);
            label = new Label(2, fila, i.concurso.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.obracodigo?.toString()); sheet.addCell(label);
            label = new Label(4, fila, i?.obranombre?.toString()); sheet.addCell(label);
            label = new Label(5, fila, i?.canton?.toString() + " " + i?.parroquia?.toString()); sheet.addCell(label);
            label = new Label(6, fila, i?.tipoobra?.toString()); sheet.addCell(label);
            label = new Label(7, fila, i?.tipocontrato?.toString()); sheet.addCell(label);
            label = new Label(8, fila, i?.nombrecontra?.toString()); sheet.addCell(label);
            number = new jxl.write.Number(9, fila, i.monto); sheet.addCell(number);
            number = new jxl.write.Number(10, fila, i.porcentaje); sheet.addCell(number);
            number = new jxl.write.Number(11, fila, i.anticipo); sheet.addCell(number);
            label = new Label(12, fila, i?.fechainicio?.toString()); sheet.addCell(label);
            label = new Label(13, fila, i?.fechafin?.toString()); sheet.addCell(label);
            label = new Label(14, fila, i?.plazo); sheet.addCell(label);

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
        def perfil = session.perfil.id
        return [perfil: perfil]
    }


    def tablaGarantias () {
        println(params)
        def sql
        def res
        def cn

        params.old = params.criterio
        params.criterio = reportesService.limpiaCriterio(params.criterio)

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
                "  LEFT JOIN ofrt o ON c.ofrt__id = o.ofrt__id\n" +
                "  LEFT JOIN tpgr t ON g.tpgr__id = t.tpgr__id\n" +
                "  LEFT JOIN tdgr q ON g.tdgr__id = q.tdgr__id\n" +
                "  LEFT JOIN asgr a ON g.asgr__id = a.asgr__id\n" +
                "  LEFT JOIN prve s ON o.prve__id = s.prve__id\n" +
                "  LEFT JOIN mnda m ON g.mnda__id = m.mnda__id\n"


        def filtroBuscador = ""

        def buscador = ""

        switch (params.buscador) {
            case "cdgo":
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
            case "nmrv":
                if(!params.criterio){
                    params.criterio=0
                }

                filtroBuscador =" where g.grntnmrv = ${params.criterio} "
                break;
            case "fcin":

                if(params.fecha){

//                    println("---->>>>****" + params.fecha)

                    def fecha =new Date().parse("dd-MM-yyyy", params.fecha)
                    params.fecha = fecha
//                    println("---->>>>" + params.fecha)
                    def dia = formatDate(date: fecha, format: "yyyy-MM-dd")
                    filtroBuscador = " where g.grntfcin = '${dia}' "
                }

                break;

            case "fcfn":

                if(params.fecha){

//                    println("---->>>>****" + params.fecha)

                    def fecha =new Date().parse("dd-MM-yyyy", params.fecha)
                    params.fecha = fecha
//                    println("---->>>>" + params.fecha)
                    def dia = formatDate(date: fecha, format: "yyyy-MM-dd")
                    filtroBuscador = " where g.grntfcfn= '${dia}' "
                }

                break;

        }

        params.criterio = params.old

        sql = sqlBase + filtroBuscador
        cn = dbConnectionService.getConnection()
        res = cn.rows(sql.toString())

//        println(sql)

//        println("res" + res)


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
                "  LEFT JOIN ofrt o ON c.ofrt__id = o.ofrt__id\n" +
                "  LEFT JOIN tpgr t ON g.tpgr__id = t.tpgr__id\n" +
                "  LEFT JOIN tdgr q ON g.tdgr__id = q.tdgr__id\n" +
                "  LEFT JOIN asgr a ON g.asgr__id = a.asgr__id\n" +
                "  LEFT JOIN prve s ON o.prve__id = s.prve__id\n" +
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
        headers.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", times18bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("REPORTE DE GARANTÍAS", times12bold));
        addEmptyLine(headers, 1);
        headers.add(new Paragraph("Quito, " + printFecha(new Date()).toUpperCase(), times12bold));
        addEmptyLine(headers, 1);
        document.add(headers);

        PdfPTable tablaRegistradas = new PdfPTable(13);
        tablaRegistradas.setWidthPercentage(100);
//        tablaRegistradas.setWidths(arregloEnteros([15, 30, 25, 20, 8, 15, 30, 15, 9, 20,15,15,12,10]))
        tablaRegistradas.setWidths(arregloEnteros([15, 30, 25, 20, 8, 30, 15, 9, 20,15,15,12,10]))

        addCellTabla(tablaRegistradas, new Paragraph("N° Contrato", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Contratista", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Tipo de Garantía", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("N° Garantía", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Rnov", times8bold), prmsCellHead2)
//        addCellTabla(tablaRegistradas, new Paragraph("Original", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Aseguradora", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Documento", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Estado", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Monto", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Emisión", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Vencimiento", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Cancela-ción", times8bold), prmsCellHead2)
        addCellTabla(tablaRegistradas, new Paragraph("Moneda", times8bold), prmsCellHead2)

        switch (params.buscador) {
            case "cdgo":
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
            case "nmrv":
                if(!params.criterio){
                    params.criterio=0
                }

                filtroBuscador =" where g.grntnmrv = ${params.criterio} "
                break;
            case "fcin":

                if(params.fecha){

//                    println("---->>>>****" + params.fecha)

                    def fecha =new Date().parse("dd-MM-yyyy", params.fecha)
                    params.fecha = fecha
//                    println("---->>>>" + params.fecha)
                    def dia = formatDate(date: fecha, format: "yyyy-MM-dd")
                    filtroBuscador = " where g.grntfcin = '${dia}' "
                }

                break;

            case "fcfn":

                if(params.fecha){

//                    println("---->>>>****" + params.fecha)

                    def fecha =new Date().parse("dd-MM-yyyy", params.fecha)
                    params.fecha = fecha
//                    println("---->>>>" + params.fecha)
                    def dia = formatDate(date: fecha, format: "yyyy-MM-dd")
                    filtroBuscador = " where g.grntfcfn= '${dia}' "
                }

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
//            if(i.padre){
//                addCellTabla(tablaRegistradas, new Paragraph(janus.pac.Garantia.get(i.padre).codigo, times8normal), prmsCellLeft)
//            }else {
//                addCellTabla(tablaRegistradas, new Paragraph("", times8normal), prmsCellLeft)
//            }
            addCellTabla(tablaRegistradas, new Paragraph(i.aseguradora, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.documento, times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(i.estado, times8normal), prmsCellRight3)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: i.monto, minFractionDigits:
                    5, maxFractionDigits: 5, format: "##,##0", locale: "ec"), times8normal), prmsCellRight)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatDate(date: i.emision, format: "dd-MM-yyyy"), times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatDate(date: i.vencimiento, format: "dd-MM-yyyy"), times8normal), prmsCellLeft)
            addCellTabla(tablaRegistradas, new Paragraph(g.formatNumber(number: i.dias, minFractionDigits:
                    0, maxFractionDigits: 0, format: "##,##0", locale: "ec")+ " Días", times8normal), prmsCellRight)
            addCellTabla(tablaRegistradas, new Paragraph(i.moneda, times8normal), prmsCellLeft)

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
                "  LEFT JOIN ofrt o ON c.ofrt__id = o.ofrt__id\n" +
                "  LEFT JOIN tpgr t ON g.tpgr__id = t.tpgr__id\n" +
                "  LEFT JOIN tdgr q ON g.tdgr__id = q.tdgr__id\n" +
                "  LEFT JOIN asgr a ON g.asgr__id = a.asgr__id\n" +
                "  LEFT JOIN prve s ON o.prve__id = s.prve__id\n" +
                "  LEFT JOIN mnda m ON g.mnda__id = m.mnda__id\n"


        def filtroBuscador = ""
        def buscador = ""

        params.criterio = params.criterio.trim();

        switch (params.buscador) {
            case "cdgo":
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
            case "nmrv":
                if(!params.criterio){
                    params.criterio=0
                }
                filtroBuscador =" where g.grntnmrv = ${params.criterio} "
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
        sheet.setColumnView(0, 20)
        sheet.setColumnView(1, 35)
        sheet.setColumnView(2, 25)
        sheet.setColumnView(3, 15)
        sheet.setColumnView(4, 15)
        sheet.setColumnView(5, 15)
        sheet.setColumnView(6, 40)
        sheet.setColumnView(7, 25)
        sheet.setColumnView(8, 15)
        sheet.setColumnView(9, 15)
        sheet.setColumnView(10, 15)
        sheet.setColumnView(11, 15)
        sheet.setColumnView(12, 15)
//        sheet.setColumnView(13, 15)
        // inicia textos y numeros para asocias a columnas

        def label
        def nmro
        def number

        def fila = 6;

        NumberFormat nf = new NumberFormat("#.##");
        WritableCellFormat cf2obj = new WritableCellFormat(nf);

        label = new Label(1, 1, "SEP - G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(1, 2, "REPORTE EXCEL REGISTRADAS", times16format); sheet.addCell(label);

        label = new Label(0, 4, "N° Contrato: ", times16format); sheet.addCell(label);
        label = new Label(1, 4, "Contratista", times16format); sheet.addCell(label);
        label = new Label(2, 4, "Tipo de Garantía", times16format); sheet.addCell(label);
        label = new Label(3, 4, "N° Garantía", times16format); sheet.addCell(label);
        label = new Label(4, 4, "Rnov", times16format); sheet.addCell(label);
//        label = new Label(5, 4, "Original", times16format); sheet.addCell(label);
        label = new Label(5, 4, "Aseguradora", times16format); sheet.addCell(label);
        label = new Label(6, 4, "Documento", times16format); sheet.addCell(label);
        label = new Label(7, 4, "Estado", times16format); sheet.addCell(label);
        label = new Label(8, 4, "Monto", times16format); sheet.addCell(label);
        label = new Label(9, 4, "Emisión", times16format); sheet.addCell(label);
        label = new Label(10, 4, "Vencimiento", times16format); sheet.addCell(label);
        label = new Label(11, 4, "Cancelación", times16format); sheet.addCell(label);
        label = new Label(12, 4, "Moneda", times16format); sheet.addCell(label);


        res.eachWithIndex {i, j->

            label = new Label(0, fila, i?.codigocontrato?.toString()); sheet.addCell(label);
            label = new Label(1, fila, i?.contratista); sheet.addCell(label);
            label = new Label(2, fila, i.tipogarantia.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.codigo?.toString()); sheet.addCell(label);
            number = new jxl.write.Number(4, fila, i.renovacion); sheet.addCell(number);
//            label = new Label(5, fila, ""); sheet.addCell(label);
            label = new Label(5, fila, i?.aseguradora?.toString()); sheet.addCell(label);
            label = new Label(6, fila, i?.documento?.toString()); sheet.addCell(label);
            label = new Label(7, fila, i?.estado?.toString()); sheet.addCell(label);
            number = new jxl.write.Number(8, fila, i.monto); sheet.addCell(number);
            label = new Label(9, fila, i?.emision?.toString()); sheet.addCell(label);
            label = new Label(10, fila, i?.vencimiento?.toString()); sheet.addCell(label);
            number = new jxl.write.Number(11, fila, i.dias); sheet.addCell(number);
            label = new Label(12, fila, i?.moneda?.toString()); sheet.addCell(label);

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
        def perfil = session.perfil.id
        return [perfil: perfil]

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

    def reporteExcelItemsVae () {

        def sql
        def cn
        def res

        def fecha =new Date().parse("dd-MM-yyyy", params.fecha)

        fecha = fecha.format("yyyy/MM/dd")

        sql = "select * from items_ver_vae( cast( '${fecha}' as date))"

//        println("vae " + sql)

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
        sheet.setColumnView(0, 25)
        sheet.setColumnView(1, 80)
        sheet.setColumnView(2, 20)
        sheet.setColumnView(3, 20)
        sheet.setColumnView(4, 30)

        // inicia textos y numeros para asocias a columnas

        def label
        def nmro
        def number
        def fila = 6;

        NumberFormat nf = new NumberFormat("#.##");
        WritableCellFormat cf2obj = new WritableCellFormat(nf);

        label = new Label(1, 1, "SEP - G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new Label(1, 2, "REPORTE EXCEL ITEMS VAE", times16format); sheet.addCell(label);

        label = new Label(0, 4, "Código: ", times16format); sheet.addCell(label);
        label = new Label(1, 4, "Nombre", times16format); sheet.addCell(label);
        label = new Label(2, 4, "Unidad", times16format); sheet.addCell(label);
        label = new Label(3, 4, "VAE", times16format); sheet.addCell(label);
        label = new Label(4, 4, "Valor", times16format); sheet.addCell(label);

        res.eachWithIndex {i, j->
            label = new Label(0, fila, i?.itemcdgo?.toString()); sheet.addCell(label);
            label = new Label(1, fila, i?.itemnmbr?.toString()); sheet.addCell(label);
            label = new Label(2, fila, i?.unddcdgo?.toString()); sheet.addCell(label);
            label = new Label(3, fila, i?.item_vae?.toString()); sheet.addCell(label);
            number = new jxl.write.Number(4, fila, i?.itvapcnt ?: 0); sheet.addCell(number);
            fila++
        }
        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "ItemsVae.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());
    }



}
