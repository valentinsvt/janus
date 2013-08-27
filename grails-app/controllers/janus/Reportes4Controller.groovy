package janus

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter

import java.awt.Color


class Reportes4Controller {

    def index() {}
    def dbConnectionService
    def preciosService

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

        println("params reporte:" + params)

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

            filtro = " where obraetdo='R' or obraofig is NOT NULL "
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

    def contratadas () {



    }

    def tablaContratadas () {


        //        println("paramsCont" + params)

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
        def buscador=""



        switch (params.buscador) {
            case "cdgo":
            case "nmbr":
            case "ofig":
            case "ofsl":
            case "mmsl":
            case "frpl":
            case "tipo":
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
            case "insp":
            case "rvsr":
                filtroBuscador = " where (s.prsnnmbr ILIKE ('%${params.criterio}%') or s.prsnapll ILIKE ('%${params.criterio}%')) "
                break;

        }


        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())

//        println(sql)
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
//
//


//
//            println("obras:" + obra)
//            println("concurso:" + concurso)
//            if(concurso){
//                println("oferta:" + oferta)
//            }
//
//            if(oferta && concurso){
//                println("contrato:" + contrato)
//            }




        }

//        println("-->>" + obras)
//        println("-->>" + contratos)



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


    def aseguradoras () {



    }


    def tablaAseguradoras () {

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


    def contratistas () {



    }

    def tablaContratistas (){


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


    def contratos (){



    }

    def tablaContratos () {

        def sql
        def res
        def cn


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
                "  LEFT JOIN obra o ON r.cncr__id = o.obra__id\n" +
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
            case "fcsb":
            case "ofsl":
            case "mnto":
                buscador = "cntr"+params.buscador
                filtroBuscador =" and ${buscador} ILIKE ('%${params.criterio}%') "
                break;
            case "cncr":
                filtroBuscador = " and r.cncrcdgo ILIKE ('%${params.criterio}%') "
                break;
            case "parr":
                filtroBuscador = " and p.parrnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "cntn":
                filtroBuscador = " and n.cntnnmbr ILIKE ('%${params.criterio}%') "
                break;
            case "obra":
                filtroBuscador = " and n.cntnnmbr ILIKE ('%${params.criterio}%') "
                break;


        }




        sql = sqlBase + filtroBuscador

        cn = dbConnectionService.getConnection()

        res = cn.rows(sql.toString())

        return [res: res, params:params]

    }

    def reporteContratos () {



    }

}
