package janus

import com.lowagie.text.*

//import com.lowagie.text.Font
import com.lowagie.text.pdf.*
import janus.apus.ArchivoEspecificacion
import janus.ejecucion.*
import janus.pac.CrngEjecucionObra
import janus.pac.CronogramaContratado
import janus.pac.CronogramaContrato
import janus.pac.CronogramaEjecucion
import janus.pac.PeriodoEjecucion
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import org.springframework.dao.DataIntegrityViolationException

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


    def meses = ['', "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"]

    private String printFecha(Date fecha) {
        if (fecha) {
            return (fecha.format("dd") + ' de ' + meses[fecha.format("MM").toInteger()] + ' de ' + fecha.format("yyyy")).toUpperCase()
        } else {
            return "Error: no hay fecha que mostrar"
        }
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
        def persona = Persona.get(session.usuario.id)
//        def rubros = VolumenesObra.findAllByObra(obra, [sort: 'orden']).item.unique()
        def tama = VolumenesObra.findAllByObra(obra, [sort: 'orden']).item.unique().size()
        def rubros

        if((tama -1) >= 100){
            rubros = VolumenesObra.findAllByObra(obra, [sort: 'orden']).item.unique()[0..100]
        }else{
            rubros = VolumenesObra.findAllByObra(obra, [sort: 'orden']).item.unique()[0..(tama - 1)]
        }

//        println "rubros: ${rubros.codigo}"

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
        HeaderFooter footer1 = new HeaderFooter(new Phrase('', fontTd), true);
        footer1.setBorder(Rectangle.NO_BORDER);
        footer1.setAlignment(Element.ALIGN_CENTER);
        document.setFooter(footer1);
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
        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", catFont));
        preface.add(new Paragraph("ANEXO DE ESPECIFICACIÓN DE RUBROS DE LA OBRA " + obra.nombre, catFont));
        addEmptyLine(preface, 1);
        Paragraph preface2 = new Paragraph();
//        preface2.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
        preface2.add(new Paragraph("Generado por el usuario: " + (persona?.titulo ?: '') + ' ' + (persona?.nombre ?: '') + ' ' + (persona?.apellido ?: '') + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
        addEmptyLine(preface2, 1);
        document.add(preface);
        document.add(preface2);

        def prmsTh = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsTd = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsEs = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 5]

        def pagAct = 1

        def rubrosText = "Rubros de la obra " + truncText(obra.nombre)

        def tipo = params.tipo //i: ilustraciones, e: especificaciones, ie: ambas

        rubros.eachWithIndex { rubro, ik ->

            Paragraph paragraphRubro = new Paragraph();
            paragraphRubro.add(new Paragraph(rubro.nombre, fontTitle));

            document.add(paragraphRubro)

            PdfPTable tablaRubro = new PdfPTable(6);
            tablaRubro.setWidths(arregloEnteros([12, 24, 10, 24, 10, 20]))
            tablaRubro.setWidthPercentage(100);
            tablaRubro.setSpacingBefore(10f);

            def extIlustracion = "", extEspecificacion = "", pagesEspecificacion = 0, pagesIlustracion = 0, pathEspecificacion, pathIlustracion
            PdfReader readerEspecificacion
            PdfReader readerIlustracion

            if (rubro.foto && tipo.contains("i")) {
                extIlustracion = rubro.foto.split("\\.")
                extIlustracion = extIlustracion[extIlustracion.size() - 1]

                pathIlustracion = servletContext.getRealPath("/") + "rubros" + File.separatorChar + rubro?.foto

                if (extIlustracion.toLowerCase() in ["pdf"]) {
                    readerIlustracion = new PdfReader(new FileInputStream(pathIlustracion));
                    pagesIlustracion = readerIlustracion.getNumberOfPages()
                }
            }

//            def ares = ArchivoEspecificacion.findByItem(rubro)
            def ares = ArchivoEspecificacion.findByCodigo(rubro.codigoEspecificacion)
//            if (rubro.especificaciones && tipo.contains("e")) {
            if (ares && tipo.contains("e")) {
//                extEspecificacion = rubro.especificaciones.split("\\.")
                extEspecificacion = ares.ruta.split("\\.")
                extEspecificacion = extEspecificacion[extEspecificacion.size() - 1]

//                pathEspecificacion = servletContext.getRealPath("/") + "rubros" + File.separatorChar + rubro?.especificaciones
                pathEspecificacion = servletContext.getRealPath("/") + "rubros" + File.separatorChar + ares?.ruta

                if (extEspecificacion.toLowerCase() == "pdf") {
                    readerEspecificacion = new PdfReader(new FileInputStream(pathEspecificacion));
                    pagesEspecificacion = readerEspecificacion.getNumberOfPages()
                }
            }

            def maxImageSize = 400

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

            if (extEspecificacion && extEspecificacion != "") {
                addCellTabla(tablaRubro, new Paragraph("Especificación", fontTh), prmsTh)
                if (extEspecificacion.toLowerCase() != "pdf") { //es una imgaen png, jpg...
                    def img = Image.getInstance(pathEspecificacion);
//                    if (img.getScaledWidth() > maxImageSize || img.getScaledHeight() > maxImageSize) {
                    img.scaleToFit(maxImageSize, maxImageSize);
//                    }
                    addCellTabla(tablaRubro, img, prmsEs)
                } else {
                    def str = "- PDF de ${pagesEspecificacion} página${pagesEspecificacion == 1 ? '' : 's'} adjunto a partir de la siguiente página -"
                    if (pagesEspecificacion == 1) {
                        str = "- PDF de ${pagesEspecificacion} página${pagesEspecificacion == 1 ? '' : 's'} adjunto en la siguiente página -"
                    }
                    addCellTabla(tablaRubro, new Paragraph(str, fontTd), prmsEs)
                }
            }

            if (extIlustracion && extIlustracion != "") {
                addCellTabla(tablaRubro, new Paragraph("Ilustración", fontTh), prmsTh)
                if (extIlustracion.toLowerCase() != "pdf") { //es una imgaen png, jpg...
                    def img = Image.getInstance(pathIlustracion);

//                    println "ilust: $pathIlustracion"
//                    println "w: ${img.getScaledWidth()}, h: ${img.getScaledHeight()}"

//                    if (img.getScaledWidth() > maxImageSize || img.getScaledHeight() > maxImageSize) {
                    img.scaleToFit(maxImageSize, maxImageSize);
//                    }

                    addCellTabla(tablaRubro, img, prmsEs)
                } else {
                    def str = "- PDF de ${pagesIlustracion} página${pagesIlustracion == 1 ? '' : 's'} adjunto "
                    def adj = "a partir de la siguiente página -"
                    if (pagesIlustracion == 1) {
                        str = "- PDF de ${pagesIlustracion} página${pagesIlustracion == 1 ? '' : 's'} adjunto "
                        adj = "en la siguiente página -"
                    }

                    if (pagesEspecificacion > 0) {
                        adj = "después de la especificación - "
                    }

                    addCellTabla(tablaRubro, new Paragraph(str + adj, fontTd), prmsEs)
                }
            }

            document.add(tablaRubro)

            infoText(cb, document, rubrosText, DOC)
//            infoText(cb, document, pagAct.toString(), PAG)
//            pagAct++

            if (extEspecificacion == "pdf") {
                pagesEspecificacion.times {
                    document.newPage();
                    PdfImportedPage page = pdfw.getImportedPage(readerEspecificacion, it + 1);
                    cb.addTemplate(page, 0, 0);

                    infoText(cb, document, "Especificación del rubro " + truncText(rubro.nombre) + " pág. " + (it + 1) + "/" + pagesEspecificacion, ADJ)
                    infoText(cb, document, rubrosText, DOC)
                    infoText(cb, document, pagAct.toString(), PAG)
                    pagAct++
                }
                document.newPage();
            }

            if (extIlustracion == "pdf") {
                pagesIlustracion.times {
                    document.newPage();
                    PdfImportedPage page = pdfw.getImportedPage(readerIlustracion, it + 1);
                    cb.addTemplate(page, 0, 0);

                    infoText(cb, document, "Ilustración del rubro " + truncText(rubro.nombre) + " pág. " + (it + 1) + "/" + pagesIlustracion, ADJ)
                    infoText(cb, document, rubrosText, DOC)
                    infoText(cb, document, pagAct.toString(), PAG)
                    pagAct++
                }
                document.newPage();
            }
//            document.newPage();
        }



        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }


    def reporteRubroIlustracion2() {
        def obra = Obra.get(params.id)
        def persona = Persona.get(session.usuario.id)
//        def rubros = VolumenesObra.findAllByObra(obra, [sort: 'orden']).item.unique()
        def tama = VolumenesObra.findAllByObra(obra, [sort: 'orden']).item.unique().size()
        def rubros
        if((tama - 1) >= 101){
            rubros = VolumenesObra.findAllByObra(obra, [sort: 'orden']).item.unique()[101..(tama - 1)]
        }else{
            rubros = VolumenesObra.findAllByObra(obra, [sort: 'orden']).item.unique()[0..(tama - 1)]
        }


//        println "rubros: ${rubros.codigo}"

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
        HeaderFooter footer1 = new HeaderFooter(new Phrase('', fontTd), true);
        footer1.setBorder(Rectangle.NO_BORDER);
        footer1.setAlignment(Element.ALIGN_CENTER);
        document.setFooter(footer1);
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
        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", catFont));
        preface.add(new Paragraph("ANEXO DE ESPECIFICACIÓN DE RUBROS DE LA OBRA " + obra.nombre, catFont));
        addEmptyLine(preface, 1);
        Paragraph preface2 = new Paragraph();
//        preface2.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
        preface2.add(new Paragraph("Generado por el usuario: " + (persona?.titulo ?: '') + ' ' + (persona?.nombre ?: '') + ' ' + (persona?.apellido ?: '') + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
        addEmptyLine(preface2, 1);
        document.add(preface);
        document.add(preface2);

        def prmsTh = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsTd = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsEs = [border: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 5]

        def pagAct = 1

        def rubrosText = "Rubros de la obra " + truncText(obra.nombre)

        def tipo = params.tipo //i: ilustraciones, e: especificaciones, ie: ambas

        rubros.eachWithIndex { rubro, ik ->

            Paragraph paragraphRubro = new Paragraph();
            paragraphRubro.add(new Paragraph(rubro.nombre, fontTitle));

            document.add(paragraphRubro)

            PdfPTable tablaRubro = new PdfPTable(6);
            tablaRubro.setWidths(arregloEnteros([12, 24, 10, 24, 10, 20]))
            tablaRubro.setWidthPercentage(100);
            tablaRubro.setSpacingBefore(10f);

            def extIlustracion = "", extEspecificacion = "", pagesEspecificacion = 0, pagesIlustracion = 0, pathEspecificacion, pathIlustracion
            PdfReader readerEspecificacion
            PdfReader readerIlustracion

            if (rubro.foto && tipo.contains("i")) {
                extIlustracion = rubro.foto.split("\\.")
                extIlustracion = extIlustracion[extIlustracion.size() - 1]

                pathIlustracion = servletContext.getRealPath("/") + "rubros" + File.separatorChar + rubro?.foto

                if (extIlustracion.toLowerCase() in ["pdf"]) {
                    readerIlustracion = new PdfReader(new FileInputStream(pathIlustracion));
                    pagesIlustracion = readerIlustracion.getNumberOfPages()
                }
            }

//            def ares = ArchivoEspecificacion.findByItem(rubro)
            def ares = ArchivoEspecificacion.findByCodigo(rubro.codigoEspecificacion)
//            if (rubro.especificaciones && tipo.contains("e")) {
            if (ares && tipo.contains("e")) {
//                extEspecificacion = rubro.especificaciones.split("\\.")
                extEspecificacion = ares.ruta.split("\\.")
                extEspecificacion = extEspecificacion[extEspecificacion.size() - 1]

//                pathEspecificacion = servletContext.getRealPath("/") + "rubros" + File.separatorChar + rubro?.especificaciones
                pathEspecificacion = servletContext.getRealPath("/") + "rubros" + File.separatorChar + ares?.ruta

                if (extEspecificacion.toLowerCase() == "pdf") {
                    readerEspecificacion = new PdfReader(new FileInputStream(pathEspecificacion));
                    pagesEspecificacion = readerEspecificacion.getNumberOfPages()
                }
            }

            def maxImageSize = 400

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

            if (extEspecificacion && extEspecificacion != "") {
                addCellTabla(tablaRubro, new Paragraph("Especificación", fontTh), prmsTh)
                if (extEspecificacion.toLowerCase() != "pdf") { //es una imgaen png, jpg...
                    def img = Image.getInstance(pathEspecificacion);
//                    if (img.getScaledWidth() > maxImageSize || img.getScaledHeight() > maxImageSize) {
                    img.scaleToFit(maxImageSize, maxImageSize);
//                    }
                    addCellTabla(tablaRubro, img, prmsEs)
                } else {
                    def str = "- PDF de ${pagesEspecificacion} página${pagesEspecificacion == 1 ? '' : 's'} adjunto a partir de la siguiente página -"
                    if (pagesEspecificacion == 1) {
                        str = "- PDF de ${pagesEspecificacion} página${pagesEspecificacion == 1 ? '' : 's'} adjunto en la siguiente página -"
                    }
                    addCellTabla(tablaRubro, new Paragraph(str, fontTd), prmsEs)
                }
            }

            if (extIlustracion && extIlustracion != "") {
                addCellTabla(tablaRubro, new Paragraph("Ilustración", fontTh), prmsTh)
                if (extIlustracion.toLowerCase() != "pdf") { //es una imgaen png, jpg...
                    def img = Image.getInstance(pathIlustracion);

//                    println "ilust: $pathIlustracion"
//                    println "w: ${img.getScaledWidth()}, h: ${img.getScaledHeight()}"

//                    if (img.getScaledWidth() > maxImageSize || img.getScaledHeight() > maxImageSize) {
                    img.scaleToFit(maxImageSize, maxImageSize);
//                    }

                    addCellTabla(tablaRubro, img, prmsEs)
                } else {
                    def str = "- PDF de ${pagesIlustracion} página${pagesIlustracion == 1 ? '' : 's'} adjunto "
                    def adj = "a partir de la siguiente página -"
                    if (pagesIlustracion == 1) {
                        str = "- PDF de ${pagesIlustracion} página${pagesIlustracion == 1 ? '' : 's'} adjunto "
                        adj = "en la siguiente página -"
                    }

                    if (pagesEspecificacion > 0) {
                        adj = "después de la especificación - "
                    }

                    addCellTabla(tablaRubro, new Paragraph(str + adj, fontTd), prmsEs)
                }
            }

            document.add(tablaRubro)

            infoText(cb, document, rubrosText, DOC)
//            infoText(cb, document, pagAct.toString(), PAG)
//            pagAct++

            if (extEspecificacion == "pdf") {
                pagesEspecificacion.times {
                    document.newPage();
                    PdfImportedPage page = pdfw.getImportedPage(readerEspecificacion, it + 1);
                    cb.addTemplate(page, 0, 0);

                    infoText(cb, document, "Especificación del rubro " + truncText(rubro.nombre) + " pág. " + (it + 1) + "/" + pagesEspecificacion, ADJ)
                    infoText(cb, document, rubrosText, DOC)
                    infoText(cb, document, pagAct.toString(), PAG)
                    pagAct++
                }
                document.newPage();
            }

            if (extIlustracion == "pdf") {
                pagesIlustracion.times {
                    document.newPage();
                    PdfImportedPage page = pdfw.getImportedPage(readerIlustracion, it + 1);
                    cb.addTemplate(page, 0, 0);

                    infoText(cb, document, "Ilustración del rubro " + truncText(rubro.nombre) + " pág. " + (it + 1) + "/" + pagesIlustracion, ADJ)
                    infoText(cb, document, rubrosText, DOC)
                    infoText(cb, document, pagAct.toString(), PAG)
                    pagAct++
                }
                document.newPage();
            }
//            document.newPage();
        }



        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }


    def comprobarIlustracion (){
        println ".... comprobarIlustracion, params: $params"
        def obra
        if(params.id){
            obra = Obra.get(params.id)
        }
        def persona = Persona.get(session.usuario.id)
        def rubros = VolumenesObra.findAllByObra(obra).item.unique()

        def baos = new ByteArrayOutputStream()
        def arrIl = []
        def arrIe = []
        def mensaje = ""
        def error = false


        def pagAct = 1

        def tipo = params.tipo //i: ilustraciones, e: especificaciones, ie: ambas

        rubros.each { rubro ->
            mensaje = rubro.codigo
            def extIlustracion = "", extEspecificacion = "", pagesEspecificacion = 0, pagesIlustracion = 0, pathEspecificacion, pathIlustracion
            PdfReader readerEspecificacion
            PdfReader readerIlustracion

            if (rubro.foto && tipo.contains("i")) {
                mensaje += rubro?.foto
                extIlustracion = rubro.foto.split("\\.")
                extIlustracion = extIlustracion[extIlustracion.size() - 1]

                pathIlustracion = servletContext.getRealPath("/") + "rubros" + File.separatorChar + rubro?.foto

                arrIl += (rubro?.foto + "*")

                def il

//                if (extIlustracion.toLowerCase() in ["pdf", "png", "jpg"]) {
                if (extIlustracion.toLowerCase() in ["pdf"]) {  //solo pdf se tiene que cargar separadamente los png y jpg se insertan sin problema
                    try{
                        il = new FileInputStream(pathIlustracion)
                        render "SI*"
                        mensaje += "..ok"
                    }
                    catch(e){
                        arrIl += (rubro?.foto+"*")
                        mensaje += " **** no existe"
                        error = true
                        render "NO*" + arrIl[0]
                    }
                }
            }
//            if (rubro.especificaciones && tipo.contains("e")) {
            def ares = ArchivoEspecificacion.findByItem(rubro)
//            println "rubro: ${rubro.codigo}, ruta: ${ares?.ruta}"
            if (ares && tipo.contains("e")) {
                mensaje += " ruta: ${ares?.ruta}"
//                extEspecificacion = rubro.especificaciones.split("\\.")
                extEspecificacion = ares.ruta.split("\\.")
                extEspecificacion = extEspecificacion[extEspecificacion.size() - 1]

//                pathEspecificacion = servletContext.getRealPath("/") + "rubros" + File.separatorChar + rubro?.especificaciones
                pathEspecificacion = servletContext.getRealPath("/") + "rubros" + File.separatorChar + ares?.ruta

                arrIe += ( ares?.ruta + "*")

                def fi
                if (extEspecificacion.toLowerCase() in ["pdf"]) {
                    try {
                        fi = new FileInputStream(pathEspecificacion)
                        render "SI*"
                        mensaje += "..ok"
                    }
                    catch (e){
                        arrIe += (ares?.ruta + "*")
                        mensaje += "********* No existe"
                        error = true
                        render "NO*" + arrIe[0]
                    }
                }
            }
//            println mensaje
            if(!error) render "SI*"
        }
//        println "existió error: $error"

    }

    def reporteRubroIlustracion_bck() {
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
        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", catFont));
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

        def tipo = params.tipo //i: ilustraciones, e: especificaciones, ie: ambas

        rubros.each { rubro ->
            Paragraph paragraphRubro = new Paragraph();
            paragraphRubro.add(new Paragraph(rubro.nombre, fontTitle));

            PdfPTable tablaRubro = new PdfPTable(6);
            tablaRubro.setWidths(arregloEnteros([12, 24, 10, 24, 10, 20]))
            tablaRubro.setWidthPercentage(100);

            def extIlustracion = "", extEspecificacion = ""
            if (rubro.foto && tipo.contains("i")) {
                extIlustracion = rubro.foto.split("\\.")
                extIlustracion = extIlustracion[extIlustracion.size() - 1]
            }
            if (rubro.especificaciones && tipo.contains("e")) {
                extEspecificacion = rubro.especificaciones.split("\\.")
                extEspecificacion = extEspecificacion[extEspecificacion.size() - 1]
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

//            addCellTabla(tablaRubro, new Paragraph("Especificación", fontTh), prmsTh)
//            addCellTabla(tablaRubro, new Paragraph(rubro?.especificaciones, fontTd), prmsEs)

            if (extIlustracion && extIlustracion != "") {
                def path = servletContext.getRealPath("/") + "rubros" + File.separatorChar + rubro.foto
                if (extIlustracion.toLowerCase() != 'pdf') {
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

                        infoText(cb, document, "Ilustración del rubro " + truncText(rubro.nombre) + " pág. " + (it + 1) + "/" + pages, ADJ)
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
//        println ">>" + fecha + "    " + formato
        switch (formato) {
            case "MMM-yy":
                strFecha = meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yy")
                break;
            case "dd-MMM-yyyy":
                strFecha = "" + fecha.format("dd") + " de " + meses[fecha.format("MM").toInteger()] + " " + fecha.format("yyyy")
                break;
            default:
                strFecha = "Formato " + formato + " no reconocido"
                break;
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

    def memoPedidoPago() {

    }

    def imprimirRubrosConsolidado() {
//        println "rep rubros consolidado: "+params
        def rubros = []

        def parts = params.id.split("_")

        switch (parts[0]) {
            case "gr":
                def subGrupo = SubgrupoItems.findAllByGrupo(Grupo.get(parts[1].toLong()))
//                println "subgrupos: $subGrupo"
                def departamentos = DepartamentoItem.findAllBySubgrupoInList(subGrupo)
                rubros = Item.findAllByDepartamentoInList(departamentos, [sort: "nombre"])
                break;
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
        def listas
        if(params.listas){
            listas = params.listas
        }

        try {
            indi = indi.toDouble()
        } catch (e) {
            println "error parse " + e
            indi = 21.5
        }

        def datos = []
        def nombresTodos = []
        def corregidos = []

        rubros.each { rubro ->
            def parametros = "" + rubro.id + ",'" + fecha.format("yyyy-MM-dd") + "'," + listas + "," + params.dsp0 + "," + params.dsp1 + "," + params.dsv0 + "," + params.dsv1 + "," + params.dsv2 + "," + params.chof + "," + params.volq
            preciosService.ac_rbroV2(rubro.id, fecha.format("yyyy-MM-dd"), params.lugar)
            def res = preciosService.rb_precios(parametros, "")

//            println("res" + res)
//            println("rubro " + rubro)

            nombresTodos += rubro.nombre
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
//            println("datos " + datos)
        }

        def l = listas.split(",")
//        println "listas str "+listas+" "+l
        listas = []

        l.each {
            if(it != 'null')
                listas.add(Lugar.get(it).descripcion)
        }
//        println "listas "+listas

        nombresTodos.each {
            def text = (it ?: '')

            text = text.decodeHTML()
            text = text.replaceAll(/</, /&lt;/);
            text = text.replaceAll(/>/, /&gt;/);
            text = text.replaceAll(/"/, /&quot;/);
            corregidos += text
        }
//        println("nombres " +corregidos)

        [datos: datos, fecha: fecha, indi: indi, params: params, listas: listas, nombres: corregidos]
    }



    def imprimirRubrosConsolidado2() {
//        println "consolidado 2: $params"
        def rubros = []

        def parts = params.id.split("_")

        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        def lugar = params.lugar
        def indi = params.indi

        try {
            indi = indi.toDouble()
        } catch (e) {
            println "error parse " + e
            indi = 21.5
        }

        def datos = []
        def nombresTodos = []
        def corregidos = []


        switch (parts[0]) {
            case "gr":
                def subGrupo = SubgrupoItems.findAllByGrupo(Grupo.get(parts[1].toLong()))
//                println "subgrupos: $subGrupo"
                def departamentos = DepartamentoItem.findAllBySubgrupoInList(subGrupo)
                rubros = Item.findAllByDepartamentoInList(departamentos, [sort: "nombre"])
                break;
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

        def parametros = "" + parts[1]+ ",'" + fecha.format("yyyy-MM-dd") + "'," + params.lista1 + "," + params.lista2 + "," +
                params.lista3 + "," + params.lista4 + "," + params.lista5 + "," + params.lista6 + "," + params.dsp0 + "," +
                params.dsp1 + "," + params.dsv0 + "," + params.dsv1 + "," + params.dsv2 + "," + params.chof + "," + params.volq +
                "," + params.indi
//        preciosService.ac_rbroV2(rubro.id, fecha.format("yyyy-MM-dd"), params.lugar)
        def res = preciosService.nv_rubros(parametros)

        res.each { t->
            nombresTodos += t.rbronmbr
        }

        nombresTodos.each {
            def text = (it ?: '')
            text = text.decodeHTML()
            text = text.replaceAll(/</, /&lt;/);
            text = text.replaceAll(/>/, /&gt;/);
            text = text.replaceAll(/"/, /&quot;/);
            corregidos += text
        }
//        println("nombres " +corregidos)

        [datos: datos, fecha: fecha, indi: indi, params: params, nombres: corregidos, lista1: params.lista1, lista2: params.lista2, lista3: params.lista3, lista4: params.lista4, lista5: params.lista5,lista6: params.lista6, res: res]
    }

    def consolidadoExcel () {

//        println "consolidado excel ... $params"
        def parts = params.id.split("_")
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        def parametros = "" + parts[1]+ ",'" + fecha.format("yyyy-MM-dd") + "'," + params.lista1 + "," + params.lista2 + "," +
                params.lista3 + "," + params.lista4 + "," + params.lista5 + "," + params.lista6 + "," + params.dsp0 + "," +
                params.dsp1 + "," + params.dsv0 + "," + params.dsv1 + "," + params.dsv2 + "," + params.chof + "," + params.volq +
                "," + params.indi
        def res = preciosService.nv_rubros(parametros)


        def indi = params.indi

        try {
            indi = indi.toDouble()
        } catch (e) {
            println "error parse " + e
            indi = 21.5
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
        WritableSheet sheet = workbook.createSheet('Consolidado', 0)

        WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, false);
        WritableFont times16 = new WritableFont(WritableFont.ARIAL, 11, WritableFont.NO_BOLD, false);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        WritableCellFormat times16No = new WritableCellFormat(times16);
        sheet.setColumnView(0, 30)
        sheet.setColumnView(1, 30)
        sheet.setColumnView(2, 70)
        sheet.setColumnView(3, 15)
        sheet.setColumnView(4, 20)
        sheet.setColumnView(5, 20)
        sheet.setColumnView(6, 20)
        sheet.setColumnView(7, 20)

        def label
        def number
        def fila = 18;
        def ultimaFila

        label = new jxl.write.Label(1, 2, "SEP - G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
        label = new jxl.write.Label(1, 3, "GESTIÓN DE PRESUPUESTOS", times16format); sheet.addCell(label);
        label = new jxl.write.Label(1, 4, "ANÁLISIS DE PRECIOS UNITARIOS", times16format); sheet.addCell(label);
        label = new jxl.write.Label(0, 6, "Fecha Act. P.U.: ", times16format); sheet.addCell(label);
        label = new jxl.write.Label(1, 6, fecha.format("dd-MM-yyyy"), times16No); sheet.addCell(label);
        label = new jxl.write.Label(2, 6, "% costos indirectos: ", times16format); sheet.addCell(label);
        number = new jxl.write.Number(3, 6, indi.toDouble().round(2) ?: 0); sheet.addCell(number);
        label = new jxl.write.Label(1, 7, "LISTAS DE PRECIOS Y DISTANCIAS", times16format); sheet.addCell(label);
        label = new jxl.write.Label(0, 8, "Mano de Obra y Equipos: ", times16format); sheet.addCell(label);
        label = new jxl.write.Label(1, 8,  Lugar.get(params.lista6).descripcion, times16No); sheet.addCell(label);
        label = new jxl.write.Label(0, 9, "Cantón: ", times16format); sheet.addCell(label);
        label = new jxl.write.Label(1, 9,  Lugar.get(params.lista1).descripcion, times16No); sheet.addCell(label);
        label = new jxl.write.Label(2, 9, "Distancia: ", times16format); sheet.addCell(label);
        label = new jxl.write.Label(3, 9,  params.dsp0, times16No); sheet.addCell(label);
        label = new jxl.write.Label(0, 10, "Especial: ", times16format); sheet.addCell(label);
        label = new jxl.write.Label(1, 10, Lugar.get(params.lista2).descripcion, times16No); sheet.addCell(label);
        label = new jxl.write.Label(2, 10, "Distancia: ", times16format); sheet.addCell(label);
        label = new jxl.write.Label(3, 10, params.dsp1, times16No); sheet.addCell(label);
        label = new jxl.write.Label(0, 11, "Petreos Hormigones: ", times16format); sheet.addCell(label);
        label = new jxl.write.Label(1, 11,  Lugar.get(params.lista3).descripcion, times16No); sheet.addCell(label);
        label = new jxl.write.Label(2, 11, "Distancia: ",times16format); sheet.addCell(label);
        label = new jxl.write.Label(3, 11,  params.dsv0, times16No); sheet.addCell(label);
        label = new jxl.write.Label(0, 12, "Mejoramiento: ", times16format); sheet.addCell(label);
        label = new jxl.write.Label(1, 12, Lugar.get(params.lista4).descripcion, times16No); sheet.addCell(label);
        label = new jxl.write.Label(2, 12, "Distancia: ", times16format); sheet.addCell(label);
        label = new jxl.write.Label(3, 12, params.dsv1, times16No); sheet.addCell(label);
        label = new jxl.write.Label(0, 13, "Carpeta Asfáltica: ", times16format); sheet.addCell(label);
        label = new jxl.write.Label(1, 13,  Lugar.get(params.lista5).descripcion, times16No); sheet.addCell(label);
        label = new jxl.write.Label(2, 13, "Distancia: ", times16format); sheet.addCell(label);
        label = new jxl.write.Label(3, 13,  params.dsv2, times16No); sheet.addCell(label);
        label = new jxl.write.Label(0, 14, "Chofer: ", times16format); sheet.addCell(label);
        label = new jxl.write.Label(1, 14,  Item.get(params.chof).nombre + " (\$ " + params.prch.toDouble().round(2) + ")", times16No); sheet.addCell(label);
        label = new jxl.write.Label(2, 14, "Volquete: ", times16format); sheet.addCell(label);
        label = new jxl.write.Label(3, 14,  Item.get(params.volq).nombre + " (\$ " + params.prvl.toDouble().round(2) + ")", times16No); sheet.addCell(label);

        label = new jxl.write.Label(0, 17, "CODIGO", times16format); sheet.addCell(label);
        label = new jxl.write.Label(1, 17, "CODIGO ESP", times16format); sheet.addCell(label);
        label = new jxl.write.Label(2, 17, "NOMBRE", times16format); sheet.addCell(label);
        label = new jxl.write.Label(3, 17, "UNIDAD", times16format); sheet.addCell(label);
        label = new jxl.write.Label(4, 17, "PRECIO", times16format); sheet.addCell(label);
        label = new jxl.write.Label(5, 17, "ESPECIFICACIONES", times16format); sheet.addCell(label);
        label = new jxl.write.Label(6, 17, "PLANO DE DETALLE", times16format); sheet.addCell(label);
        label = new jxl.write.Label(7, 17, "# OBRAS", times16format); sheet.addCell(label);

        res.each{ k->
            def volumenes = VolumenesObra.findAllByItem(Item.get(k?.item__id));
            def obras = volumenes.obra.nombre.unique()

            label = new jxl.write.Label(0, fila, k?.rbrocdgo); sheet.addCell(label);
            label = new jxl.write.Label(1, fila, janus.Item.get(k?.item__id)?.codigoEspecificacion ?: ''); sheet.addCell(label);
            label = new jxl.write.Label(2, fila, k?.rbronmbr); sheet.addCell(label);
            label = new jxl.write.Label(3, fila, k?.unddcdgo); sheet.addCell(label);
            number = new jxl.write.Number(4, fila, k?.rbropcun ?: 0); sheet.addCell(number);
            label = new jxl.write.Label(5, fila, k?.rbroespc); sheet.addCell(label);
            label = new jxl.write.Label(6, fila, k?.rbrofoto); sheet.addCell(label);
            number = new jxl.write.Number(7, fila, obras.size() ?: 0); sheet.addCell(number);

            fila++
            ultimaFila = fila
        }

        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "consolidadoExcel.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());
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
//        println "reportePrecios params" + params
        def grupo = Grupo.get(params.grupo.toLong())

        def orden = "itemnmbr"
        if (params.orden == "n") {
            orden = "itemcdgo"
        }
        def lugar = Lugar.get(params.lugar.toLong())
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)
//        println("fecha:" + fecha)

        def estado

        if(params.estado == 'true'){
            estado = 'A'
        }else{
            estado = 'B'
        }

        def items = ""
        def lista = Item.withCriteria {
            eq("tipoItem", TipoItem.findByCodigo("I"))
            eq("estado", estado)
            departamento {
                subgrupo {
                    eq("grupo", grupo)
                }
            }
//            eq("estado", "A")
        }
        lista.id.each {
            if (items != "") {
                items += ","
            }
            items += it
        }
        def res = []
        def numeros = []
        def rubros = []

        def tmp
        if(items == [] || items == ''){

            res = []

        }else {

            def nombres = []
            def corregidos = []


            tmp = preciosService.getPrecioRubroItemOrder(fecha, lugar, items, orden, "asc")
            tmp.each {
                res.add(PrecioRubrosItems.get(it))
            }

            res.each {
                nombres += it?.item?.nombre

                def sql2 = "select count(*) from vlobitem where item__id = ${it?.item?.id}"
                println("sql2 " + sql2)
                def cn = dbConnectionService.getConnection()
                numeros += cn.rows(sql2.toString())

                def sql3 = "select count(*) from item it, item rb, rbro where rbro.rbrocdgo = rb.item__id and rbro.item__id = it.item__id and it.item__id = ${it?.item?.id}"
                def cn2 = dbConnectionService.getConnection()
                rubros += cn2.rows(sql3.toString())
            }
//            println("numeros " + numeros)

            nombres.each {
                def text = (it ?: '')

                text = text.decodeHTML()
                text = text.replaceAll(/</, /&lt;/);
                text = text.replaceAll(/>/, /&gt;/);
                text = text.replaceAll(/"/, /&quot;/);
                text = text.replaceAll(/'/, /&apos;/);
                text = text.replaceAll(/&/, /&amp;/);

                corregidos += text
            }

            res.eachWithIndex{ j,i->
                j.item.nombre = corregidos[i]
            }

        }

//        println("res" + res + "grupo" + grupo)

        return [lugar: lugar, cols: params.col, precios: res, grupo: grupo, numeros: numeros, rubros: rubros, estado: estado]
    }

    def reporteExcelComposicionTotales() {

//        println("!!!" + params)

        if (!params.tipo) {
            params.tipo = "-1"
        }
        if (!params.sp) {
            params.sp = '-1'
        }
        if (params.tipo == "-1") {
            params.tipo = "1,2,3"
        }

        def wsp = ""

        if (params.sp.toString() != "-1") {

            wsp = "      AND v.sbpr__id = ${params.sp} \n"
        }

        def obra = Obra.get(params.id)
//        params.tipo = "1,2,3"

        def sql = "SELECT\n" +
                "  i.itemcdgo              codigo,\n" +
                "  i.itemnmbr              item,\n" +
                "  u.unddcdgo              unidad,\n" +
                "  sum(v.voitcntd)         cantidad,\n" +
                "  v.voitpcun              punitario,\n" +
                "  v.voittrnp              transporte,\n" +
                "  v.voitpcun + v.voittrnp costo,\n" +
                "  d.dprtdscr              departamento,\n" +
                "  s.sbgrdscr              subgrupo,\n" +
                "  g.grpodscr              grupo,\n" +
                "  g.grpo__id              grid\n" +
                "FROM vlobitem v\n" +
                "  LEFT JOIN item i ON v.item__id = i.item__id\n" +
                "  LEFT JOIN undd u ON i.undd__id = u.undd__id\n" +
                "  LEFT JOIN dprt d ON i.dprt__id = d.dprt__id\n" +
                "  LEFT JOIN sbgr s ON d.sbgr__id = s.sbgr__id\n" +
                "  LEFT JOIN grpo g ON s.grpo__id = g.grpo__id AND g.grpo__id IN (${params.tipo})\n" +
                "WHERE v.obra__id = ${params.id} \n" +
                "GROUP BY i.itemcdgo, i.itemnmbr, u.unddcdgo, v.voitpcun, v.voittrnp, d.dprtdscr, s.sbgrdscr, g.grpodscr,\n" +
                "  g.grpo__id\n" +
                "ORDER BY grid ASC, i.itemnmbr"

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

//        println sql

        def cn = dbConnectionService.getConnection()

        def res = cn.rows(sql.toString())

//        println("--->>" + res)
        def errores = ""
        if (res.size() != 0) {

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

            def label
            def number
            def fila = 18;
            def totalE = 0;
            def totalM = 0;
            def totalMO = 0;
            def ultimaFila

            label = new jxl.write.Label(1, 2, "SEP - G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
            label = new jxl.write.Label(1, 4, "COMPOSICIÓN: " + obra?.nombre, times16format); sheet.addCell(label);
            label = new jxl.write.Label(1, 6, obra?.departamento?.direccion?.nombre, times16format);
            sheet.addCell(label);
            label = new jxl.write.Label(1, 8, "CÓDIGO: " + obra?.codigo, times16format); sheet.addCell(label);
            label = new jxl.write.Label(1, 10, "DOC. REFERENCIA: " + obra?.oficioIngreso, times16format);
            sheet.addCell(label);
            label = new jxl.write.Label(1, 12, "FECHA: " + printFecha(obra?.fechaCreacionObra), times16format);
            sheet.addCell(label);
            label = new jxl.write.Label(1, 14, "FECHA ACT.PRECIOS: " + printFecha(obra?.fechaPreciosRubros), times16format);
            sheet.addCell(label);

            label = new jxl.write.Label(0, 16, "CODIGO", times16format); sheet.addCell(label);
            label = new jxl.write.Label(1, 16, "ITEM", times16format); sheet.addCell(label);
            label = new jxl.write.Label(2, 16, "UNIDAD", times16format); sheet.addCell(label);
            label = new jxl.write.Label(3, 16, "CANTIDAD", times16format); sheet.addCell(label);
            label = new jxl.write.Label(4, 16, "C. REDONDEADA", times16format); sheet.addCell(label);
            label = new jxl.write.Label(5, 16, "P.UNITARIO", times16format); sheet.addCell(label);
            label = new jxl.write.Label(6, 16, "TRANSPORTE", times16format); sheet.addCell(label);
            label = new jxl.write.Label(7, 16, "COSTO", times16format); sheet.addCell(label);
            label = new jxl.write.Label(8, 16, "TIPO", times16format); sheet.addCell(label);

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

                label = new jxl.write.Label(0, fila, it?.codigo.toString()); sheet.addCell(label);
                label = new jxl.write.Label(1, fila, it?.item.toString()); sheet.addCell(label);
                label = new jxl.write.Label(2, fila, it?.unidad ? it?.unidad.toString() : ""); sheet.addCell(label);
//                number = new jxl.write.Number(3, fila, it?.cantidad.toDouble().round(3) ?: 0); sheet.addCell(number);
                number = new jxl.write.Number(3, fila, it?.cantidad.toDouble() ?: 0); sheet.addCell(number);
                number = new jxl.write.Number(4, fila,0); sheet.addCell(number);
                number = new jxl.write.Number(5, fila, it?.punitario.toDouble().round(6) ?: 0); sheet.addCell(number);
//                number = new jxl.write.Number(6, fila, it?.transporte.toDouble().round(2) ?: 0); sheet.addCell(number);
                number = new jxl.write.Number(6, fila, it?.transporte.toDouble() ?: 0); sheet.addCell(number);
//                number = new jxl.write.Number(7, fila, it?.costo.toDouble().round(6) ?: 0); sheet.addCell(number);
                number = new jxl.write.Number(7, fila, it?.costo.toDouble() ?: 0); sheet.addCell(number);
                label = new jxl.write.Label(8, fila, it?.grupo ? it?.grupo.toString() : ""); sheet.addCell(label);

                fila++

                ultimaFila = fila
            }

//        println ">>>>>>>>>> " + ultimaFila

            workbook.write();
            workbook.close();
            def output = response.getOutputStream()
            def header = "attachment; filename=" + "ComposicionExcelTotales.xls";
            response.setContentType("application/octet-stream")
            response.setHeader("Content-Disposition", header);
            output.write(file.getBytes());
        } else {
            flash.message = "Ha ocurrido un error!"
            redirect(action: "errores")
        }
    }

    def reporteExcelComposicion() {

//        println("!!!" + params)

        if (!params.tipo) {
            params.tipo = "-1"
        }
        if (!params.sp) {
            params.sp = '-1'
        }
        if (params.tipo == "-1") {
            params.tipo = "1,2,3"
        }

        def wsp = ""

        if (params.sp.toString() != "-1") {

            wsp = "      AND v.sbpr__id = ${params.sp} \n"
        }

        def obra = Obra.get(params.id)
//        params.tipo = "1,2,3"

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
                "LEFT JOIN item i ON v.item__id = i.item__id\n" +
                "LEFT JOIN undd u ON i.undd__id = u.undd__id\n" +
                "LEFT JOIN dprt d ON i.dprt__id = d.dprt__id\n" +
                "LEFT JOIN sbgr s ON d.sbgr__id = s.sbgr__id\n" +
                "LEFT JOIN sbpr b ON v.sbpr__id = b.sbpr__id\n" +
                "LEFT JOIN grpo g ON s.grpo__id = g.grpo__id AND g.grpo__id IN (${params.tipo})\n" +
                "WHERE v.obra__id = ${params.id} \n" + wsp +
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

//        println sql

        def cn = dbConnectionService.getConnection()

        def res = cn.rows(sql.toString())

//        println("--->>" + res)
        def errores = ""
        if (res.size() != 0) {

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
            sheet.setColumnView(8, 20)
            sheet.setColumnView(9, 25)
            sheet.setColumnView(10, 20)

            def label
            def number
            def fila = 18;
            def totalE = 0;
            def totalM = 0;
            def totalMO = 0;
            def totalEquipo = 0;
            def totalManoObra = 0;
            def totalMaterial = 0;
            def totalDirecto = 0;
            def ultimaFila

            label = new jxl.write.Label(1, 2, "SEP - G.A.D. PROVINCIA DE PICHINCHA", times16format); sheet.addCell(label);
            label = new jxl.write.Label(1, 4, "COMPOSICIÓN: " + obra?.nombre, times16format); sheet.addCell(label);
            label = new jxl.write.Label(1, 6, obra?.departamento?.direccion?.nombre, times16format);
            sheet.addCell(label);
            label = new jxl.write.Label(1, 8, "CÓDIGO: " + obra?.codigo, times16format); sheet.addCell(label);
            label = new jxl.write.Label(1, 10, "DOC. REFERENCIA: " + obra?.oficioIngreso, times16format);
            sheet.addCell(label);
            label = new jxl.write.Label(1, 12, "FECHA: " + printFecha(obra?.fechaCreacionObra), times16format);
            sheet.addCell(label);
            label = new jxl.write.Label(1, 14, "FECHA ACT.PRECIOS: " + printFecha(obra?.fechaPreciosRubros), times16format);
            sheet.addCell(label);

            label = new jxl.write.Label(0, 16, "CODIGO", times16format); sheet.addCell(label);
            label = new jxl.write.Label(1, 16, "ITEM", times16format); sheet.addCell(label);
            label = new jxl.write.Label(2, 16, "UNIDAD", times16format); sheet.addCell(label);
            label = new jxl.write.Label(3, 16, "CANTIDAD", times16format); sheet.addCell(label);
//            label = new jxl.write.Label(4, 16, "C. REDONDEADA", times16format); sheet.addCell(label);
            label = new jxl.write.Label(4, 16, "P.UNITARIO", times16format); sheet.addCell(label);
            label = new jxl.write.Label(5, 16, "TRANSPORTE", times16format); sheet.addCell(label);
            label = new jxl.write.Label(6, 16, "COSTO", times16format); sheet.addCell(label);
            label = new jxl.write.Label(7, 16, "TOTAL", times16format); sheet.addCell(label);
            label = new jxl.write.Label(8, 16, "TIPO", times16format); sheet.addCell(label);
            label = new jxl.write.Label(9, 16, "SUBPRESUPUESTO", times16format); sheet.addCell(label);

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
                label = new jxl.write.Label(2, fila, it?.unidad ? it?.unidad.toString() : ""); sheet.addCell(label);
//                number = new jxl.write.Number(3, fila, it?.cantidad.toDouble().round(2) ?: 0); sheet.addCell(number);
                number = new jxl.write.Number(3, fila, it?.cantidad.toDouble() ?: 0); sheet.addCell(number);
//                number = new jxl.write.Number(4, fila,0); sheet.addCell(number);
                number = new jxl.write.Number(4, fila, it?.punitario.toDouble().round(6) ?: 0); sheet.addCell(number);
//                number = new jxl.write.Number(5, fila, it?.transporte.toDouble().round(2) ?: 0); sheet.addCell(number);
                number = new jxl.write.Number(5, fila, it?.transporte.toDouble() ?: 0); sheet.addCell(number);
                number = new jxl.write.Number(6, fila, it?.costo.toDouble() ?: 0); sheet.addCell(number);
//                number = new jxl.write.Number(7, fila, it?.total.toDouble().round(2) ?: 0); sheet.addCell(number);
                number = new jxl.write.Number(7, fila, it?.total.toDouble() ?: 0); sheet.addCell(number);
                label = new jxl.write.Label(8,fila, it?.grupo ? it?.grupo.toString() : ""); sheet.addCell(label);
                label = new jxl.write.Label(9, fila, it?.subpresupuesto ? it?.subpresupuesto.toString() : "");
                sheet.addCell(label);

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

//        println ">>>>>>>>>> " + ultimaFila
            if (res.size() > 0) {
                label = new jxl.write.Label(6, ultimaFila, "Total Materiales: ", times16format); sheet.addCell(label);
                number = new jxl.write.Number(7, ultimaFila, totalMaterial.toDouble()?.round(2) ?: 0);
                sheet.addCell(number);

                label = new jxl.write.Label(6, ultimaFila + 1, "Total Mano de Obra: ", times16format);
                sheet.addCell(label);
                number = new jxl.write.Number(7, ultimaFila + 1, totalManoObra.toDouble()?.round(2) ?: 0);
                sheet.addCell(number);

                label = new jxl.write.Label(6, ultimaFila + 2, "Total Equipos: ", times16format); sheet.addCell(label);
//        number = new jxl.write.Number(7, ultimaFila + 2, totalEquipo); sheet.addCell(number);
                number = new jxl.write.Number(7, ultimaFila + 2, totalEquipo.toDouble()?.round(2) ?: 0);
                sheet.addCell(number);

                label = new jxl.write.Label(6, ultimaFila + 3, "TOTAL DIRECTO: ", times16format); sheet.addCell(label);
                number = new jxl.write.Number(7, ultimaFila + 3, totalDirecto.toDouble()?.round(2) ?: 0);
                sheet.addCell(number);

                label = new jxl.write.Label(6, ultimaFila + 4, "TOTAL Indirecto: ", times16format); sheet.addCell(label);
                number = new jxl.write.Number(7, ultimaFila + 4, (obra?.valor ? obra?.valor - totalDirecto : 0)?.round(2) ?: 0);
                sheet.addCell(number);

                label = new jxl.write.Label(6, ultimaFila + 5, "TOTAL: ", times16format); sheet.addCell(label);
                number = new jxl.write.Number(7, ultimaFila + 5, (obra?.valor ?:0)?.round(2) ?: 0);
                sheet.addCell(number);

                workbook.write();
                workbook.close();
                def output = response.getOutputStream()
                def header = "attachment; filename=" + "ComposicionExcel.xls";
                response.setContentType("application/octet-stream")
                response.setHeader("Content-Disposition", header);
                output.write(file.getBytes());
            } else {
                flash.message = "Ha ocurrido un error..."
                redirect(action: "errores")
            }
        } else {
            flash.message = "Ha ocurrido un error!"
            redirect(action: "errores")
        }
    }

    def errores() {

    }

    def reportePreciosExcel() {
//        println("params " + params)
        def orden = "itemnmbr"
        if (params.orden == "n") {
            orden = "itemcdgo"
        }

        def estado = ''

        if(params.estado == 'true'){
            estado = 'A'
        }else{
            estado = 'B'
        }

        def grupo = Grupo.get(params.grupo.toLong())
        def lugar = Lugar.get(params.lugar.toLong())
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        def items = ""
        def lista = Item.withCriteria {
            eq("tipoItem", TipoItem.findByCodigo("I"))
            eq("estado", estado)
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


//        println("excel" + res)
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
        sheet.setColumnView(7, 25)


        def label
        def number
        def fila = 8;

        label = new jxl.write.Label(2, 1, "SEP - G.A.D. PROVINCIA DE PICHINCHA".toUpperCase(), times16format);
        sheet.addCell(label);
        label = new jxl.write.Label(2, 2, "REPORTE COSTOS DE: ${grupo.descripcion.toUpperCase()}", times16format);
        sheet.addCell(label);

        label = new jxl.write.Label(1, 4, "LISTA DE PRECIOS: " + lugar?.descripcion + (estado == 'A' ? ' - ACTIVOS' : ' - INACTIVOS'), times16format);
        sheet.addCell(label);
        label = new jxl.write.Label(4, 4, "FECHA DE CONSULTA: " + new Date().format("dd-MM-yyyy"), times16format);
        sheet.addCell(label);

        def col = 0
        label = new jxl.write.Label(col, 6, "CODIGO", times16format); sheet.addCell(label); col++;
        label = new jxl.write.Label(col, 6, grupo.descripcion.toUpperCase(), times16format); sheet.addCell(label);
        col++;
        label = new jxl.write.Label(col, 6, "UNIDAD", times16format); sheet.addCell(label); col++;
        if (grupo.id == 1) {
            label = new jxl.write.Label(col, 6, "PESO/VOL", times16format); sheet.addCell(label); col++;
        }
        label = new jxl.write.Label(col, 6, "COSTO", times16format); sheet.addCell(label); col++;
        label = new jxl.write.Label(col, 6, "FECHA ACT.", times16format); sheet.addCell(label); col++;
        label = new jxl.write.Label(col, 6, "# RUBROS", times16format); sheet.addCell(label); col++;
        label = new jxl.write.Label(col, 6, "# OBRAS", times16format); sheet.addCell(label); col++;

        res.each {

            def sql2 = "select count(*) from vlobitem where item__id = ${it?.item?.id}"
            def cn = dbConnectionService.getConnection()
            def numero = cn.rows(sql2.toString())

            def sql3 = "select count(*) from item it, item rb, rbro where rbro.rbrocdgo = rb.item__id and rbro.item__id = it.item__id and it.item__id = ${it?.item?.id}"
            def cn2 = dbConnectionService.getConnection()
            def rubros = cn2.rows(sql3.toString())

            col = 0
            label = new jxl.write.Label(col, fila, it?.item?.codigo?.toString()); sheet.addCell(label); col++;
            label = new jxl.write.Label(col, fila, it?.item?.nombre?.toString()); sheet.addCell(label); col++;
            label = new jxl.write.Label(col, fila, it?.item?.unidad?.codigo?.toString()); sheet.addCell(label); col++;
            if (grupo.id == 1) {
                number = new jxl.write.Number(col, fila, it?.item?.peso); sheet.addCell(number); col++;
            }
            number = new jxl.write.Number(col, fila, it?.precioUnitario); sheet.addCell(number); col++;
            label = new jxl.write.Label(col, fila, it?.fecha?.format("dd-MM-yyyy")); sheet.addCell(label); col++;
            number = new jxl.write.Number(col, fila, rubros[0].count); sheet.addCell(number); col++;
            number = new jxl.write.Number(col, fila, numero[0].count); sheet.addCell(number); col++;

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

    def reporteCronogramaPdf() {
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
/*
            case "ejecucion":
                contrato = Contrato.get(params.id)
                obra = contrato.obra
                lbl = "l contrato de la obra"
                break;
*/
        }
        def meses = obra.plazoEjecucionMeses + (obra.plazoEjecucionDias > 0 ? 1 : 0)

        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])

        def precios = [:]
        def indirecto = obra.totales / 100

        preciosService.ac_rbroObra(obra.id)

        detalle.each {
            it.refresh()
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
//            println("res--> " + res["precio"][0])
//            println("indirecto " + indirecto)
            if(res["precio"][0] != null){
                precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))

            }else{
                precios.put(it.id.toString(), (0 * indirecto).toDouble().round(2))

            }
        }

        def baos = new ByteArrayOutputStream()

        def name = "cronograma${tipo.capitalize()}_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Font catFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font catFont2 = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font catFont3 = new Font(Font.TIMES_ROMAN, 16, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font fontTitle = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]
        def prmsHeaderHoja3 = [border: Color.WHITE, colspan: 5]

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

//        preface.add(new Paragraph("CRONOGRAMA DE ${lbl.toUpperCase()} " + obra.nombre, catFont2));
        preface.add(new Paragraph("CRONOGRAMA", catFont2));
        preface.add(new Paragraph("DGCP - COORDINACIÓN DE FIJACIÓN DE PRECIOS UNITARIOS", catFont2));
        addEmptyLine(preface, 1);
        Paragraph preface2 = new Paragraph();
//        preface2.add(new Paragraph("Generado por el usuario: " + session.usuario + "   el: " + new Date().format("dd/MM/yyyy hh:mm"), info))
        document.add(preface);
        document.add(preface2);
        Paragraph pMeses = new Paragraph();
        pMeses.add(new Paragraph("Obra: ${obra.descripcion} (${meses} mes${meses == 1 ? '' : 'es'})", info))
        addEmptyLine(pMeses, 1);
        document.add(pMeses);

        Paragraph pRequirente = new Paragraph();
        pRequirente.add(new Paragraph("Requirente: ${obra?.departamento?.direccion?.nombre + ' - ' + obra.departamento?.descripcion}", info))
//        addEmptyLine(pRequirente, 1);
        document.add(pRequirente);


        Paragraph codigoObra = new Paragraph();
        codigoObra.add(new Paragraph("Código de la Obra: ${obra?.codigo}", info))
        document.add(codigoObra);

        Paragraph docReferencia = new Paragraph();
        docReferencia.add(new Paragraph("Doc. Referencia: ${obra?.oficioIngreso}", info))
        document.add(docReferencia);

        Paragraph fecha = new Paragraph();
        fecha.add(new Paragraph("Fecha: ${printFecha(obra?.fechaCreacionObra)}", info))
//        addEmptyLine(fecha, 1);
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
                    cronos = Cronograma.findAllByVolumenObra(vol)
                    break;
                case "contrato":
                    cronos = CronogramaContrato.findAllByVolumenObra(vol)
                    break;
/*
                case "ejecucion":
                    cronos = CronogramaEjecucion.findAllByVolumenObra(vol)
                    break;
*/
            }
            def totalDolRow = 0, totalPrcRow = 0, totalCanRow = 0
//            def parcial = precios[vol.id.toString()] * vol.cantidad
            def parcial = Math.round(precios[vol.id.toString()] * vol.cantidad*100)/100
            sum += parcial

            addCellTabla(tabla, new Paragraph((vol.rutaCritica == 'S' ? "* " : "") + vol.item.codigo, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tabla, new Paragraph(vol.item.nombre, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tabla, new Paragraph(vol.item.unidad.codigo, fontTd), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tabla, new Paragraph(numero(vol.cantidad, 2), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
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
        def eqTotal = 0
        def eqDesglosado
        def b = []
        def c = []
        def sqlEquipoTotal1
        def sqlVerificarTransporte
        def sqlEquiposs2
        def sqlEquiposs1
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

        def fechaIngreso = printFecha(obra?.fechaCreacionObra)
        def fechaPrecios = printFecha(obra?.fechaPreciosRubros)



        def nuevoa = 0
        def nuevob = 0
        def nuevoc = 0
        def nuevod = 0
        def nuevoe = 0
        def nuevof = 0
        def nuevog = 0
        def nuevoh = 0

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


        sqlEquiposs2 = "SELECT\n" +
                "valor\n" +
                "from mfvl, mfcl\n" +
                "WHERE mfvl.obra__id = mfcl.obra__id AND\n" +
                "mfvl.obra__id = ${obra.id} AND\n" +
                "mfcl.clmndscr = '${Item.findByCodigo("EQPO").id}_T' AND \n" +
                "codigo = 'sS2' AND\n " +
                "mfvl.clmncdgo = mfcl.clmncdgo"


/*
        sqlEquiposs1 = "SELECT\n" +
                "valor\n" +
                "from mfvl, mfcl\n" +
                "WHERE mfvl.obra__id = mfcl.obra__id AND\n" +
                "mfvl.obra__id = ${obra.id} AND\n" +
                "mfcl.clmndscr = '1256_T' AND \n" +
                "codigo = 'sS1' AND\n " +
                "mfvl.clmncdgo = mfcl.clmncdgo"
*/
        sqlEquiposs1 = "SELECT\n" +
                "valor\n" +
                "from mfvl, mfcl\n" +
                "WHERE mfvl.obra__id = mfcl.obra__id AND\n" +
                "mfvl.obra__id = ${obra.id} AND\n" +
                "mfcl.clmndscr = '${Item.findByCodigo("EQPO").id}_T' AND \n" +
                "codigo = 'sS1' AND\n " +
                "mfvl.clmncdgo = mfcl.clmncdgo"

//        println "equipos1" + sqlEquiposs1


        def cn = dbConnectionService.getConnection()

        ed4 = cn.rows(sqlVerificarTransporte.toString())
        ed4.each {
            varTrans = it?.valor
        }

/*
        def idEqpo = Item.findByCodigo("EQPO")
        println "-----------" + idEqpo
*/


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
//            println sqlEquipoDesglosado1
//            println("ed3" + ed3)

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

//        println("obradesglose" + obra?.desgloseEquipo)

//        println("coeficientes:" + valores)
////
//        println("ed1" + ed1)
//        println("eqTotal:" + eqTotal)
//        println("vartrans" + varTrans)


        if (varTrans > 0) {
            valores.eachWithIndex { item, i ->
//println("item " + item)
//println("ed " + ed1)
                if (item > 0 && ed1[1]) {


                    b += (((ed1[i]) / (item)) - eqTotal)
                    //                println(b[0])

                } else {

                    b += 0
                }

            }

        } else {
            b[0] = 0.00
        }

//        println("B:" + b)

        b.each {
            c += (it + eqTotal)
        }

//      println("C:" + c)

        //nuevos valores

        def equipoA = cn.rows(sqlEquiposs1.toString())
        def equipoC = cn.rows(sqlEquiposs2.toString())

//        println("--->" + equipoA)
//        println("--->" + equipoC)

        equipoA.each{

            nuevoa = it?.valor
        }

        equipoC.each {
            nuevoc = it?.valor/obra?.desgloseEquipo
        }
//
//        println("A" + nuevoa)
//        println("C" + nuevoc)

        nuevob = nuevoc -nuevoa

//        println "nuevoB" + nuevob

        nuevod = nuevoc*obra?.desgloseEquipo
        nuevoe = nuevoc*obra?.desgloseRepuestos
        nuevof = nuevoc*obra?.desgloseCombustible
        nuevog = nuevoc*obra?.desgloseMecanico
        nuevoh = nuevoc*obra?.desgloseSaldo

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
        def name = "desglose equipos_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times14bold = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
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


        Paragraph headersTitulo = new Paragraph();
        addEmptyLine(headersTitulo, 1);
        headersTitulo.setAlignment(Element.ALIGN_CENTER);
        headersTitulo.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", times14bold));
        addEmptyLine(headersTitulo, 1);
        headersTitulo.add(new Paragraph(obra?.departamento?.direccion?.nombre, times12bold));
        addEmptyLine(headersTitulo, 1)
        headersTitulo.add(new Paragraph("DESGLOSE DE EQUIPOS", times12bold));
        addEmptyLine(headersTitulo, 1);
        document.add(headersTitulo);
//

        PdfPTable tablaHeader = new PdfPTable(3);
        tablaHeader.setWidthPercentage(100);
        tablaHeader.setWidths(arregloEnteros([20, 2, 70]))

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
        addCellTabla(tablaHeader, new Paragraph(printFecha(obra?.fechaCreacionObra), times8normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("FECHA ACT. PRECIOS", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(printFecha(obra?.fechaPreciosRubros), times8normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)

        document.add(tablaHeader);

        Paragraph headers = new Paragraph();
        headers.setAlignment(Element.ALIGN_LEFT);
        headers.add(new Paragraph("___________________________________________________________________________________________________________", times8bold));
        addEmptyLine(headers, 1);
        document.add(headers);

        PdfPTable tablaDesglose = new PdfPTable(4);
        tablaDesglose.setWidthPercentage(90);
        tablaDesglose.setWidths(arregloEnteros([35, 2, 15, 30]))

        tablaDesglose.setHorizontalAlignment(Element.ALIGN_RIGHT)

        addCellTabla(tablaDesglose, new Paragraph("Valor de Equipos", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" : "), prmsHeaderHoja)
//        addCellTabla(tablaDesglose, new Paragraph(g.formatNumber(number: eqTotal, minFractionDigits:
//                5, maxFractionDigits: 5, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesglose, new Paragraph(g.formatNumber(number: nuevoa , minFractionDigits:
                5, maxFractionDigits: 5, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)

//        if (b[0] > 0) {
//
//
//            addCellTabla(tablaDesglose, new Paragraph("Valor de Transporte excluyendo al Chofer", times10bold), prmsHeaderHoja)
//            addCellTabla(tablaDesglose, new Paragraph(" : "), prmsHeaderHoja)
//            addCellTabla(tablaDesglose, new Paragraph(g.formatNumber(number: b[0], minFractionDigits:
//                    5, maxFractionDigits: 5, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
//            addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
//
//        } else {
//
//
//            addCellTabla(tablaDesglose, new Paragraph("Valor de Transporte excluyendo al Chofer", times10bold), prmsHeaderHoja)
//            addCellTabla(tablaDesglose, new Paragraph(" : "), prmsHeaderHoja)
//            addCellTabla(tablaDesglose, new Paragraph(g.formatNumber(number: b[0], minFractionDigits:
//                    5, maxFractionDigits: 5, format: "##.##", locale: "ec"), times10normal), prmsDerecha)
//            addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
//
//
//        }


        addCellTabla(tablaDesglose, new Paragraph("Valor de Transporte excluyendo al Chofer", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(g.formatNumber(number: nuevob, minFractionDigits:
                5, maxFractionDigits: 5, format: "##.##", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)



        addCellTabla(tablaDesglose, new Paragraph("____________________________"), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph("________"), prmsDerecha)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesglose, new Paragraph("Total Equipos + Transporte", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" : "), prmsHeaderHoja)
//        addCellTabla(tablaDesglose, new Paragraph(g.formatNumber(number: c[0], minFractionDigits:
//                5, maxFractionDigits: 5, format: "###,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesglose, new Paragraph(g.formatNumber(number: nuevoc, minFractionDigits:
                5, maxFractionDigits: 5, format: "###,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesglose, new Paragraph(" "), prmsHeaderHoja)

        PdfPTable tablaDesgloseBody = new PdfPTable(6);
        tablaDesgloseBody.setWidthPercentage(90);
        tablaDesgloseBody.setWidths(arregloEnteros([20, 20, 8, 5, 16, 40]))

        tablaDesgloseBody.setHorizontalAlignment(Element.ALIGN_RIGHT)

        addCellTabla(tablaDesgloseBody, new Paragraph("Equipos", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: valores[0] * 100, minFractionDigits:
                0, maxFractionDigits: 0, format: "###,##0", locale: "ec") + " %", times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
//        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: c[0] * valores[0], minFractionDigits:
//                5, maxFractionDigits: 5, format: "###,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: nuevod, minFractionDigits:
                5, maxFractionDigits: 5, format: "###,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Repuestos", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: valores[1] * 100, minFractionDigits:
                0, maxFractionDigits: 0, format: "###,##0", locale: "ec") + " %", times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
//        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: c[0] * valores[1], minFractionDigits:
//                5, maxFractionDigits: 5, format: "###,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: nuevoe, minFractionDigits:
                5, maxFractionDigits: 5, format: "###,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Combustibles", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: valores[2] * 100, minFractionDigits:
                0, maxFractionDigits: 0, format: "###,##0", locale: "ec") + " %", times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
//        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: c[0] * valores[2], minFractionDigits:
//                5, maxFractionDigits: 5, format: "###,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: nuevof, minFractionDigits:
                5, maxFractionDigits: 5, format: "###,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Mecánico", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: valores[3] * 100, minFractionDigits:
                0, maxFractionDigits: 0, format: "###,##0", locale: "ec") + " %", times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
//        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: c[0] * valores[3], minFractionDigits:
//                5, maxFractionDigits: 5, format: "###,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: nuevog, minFractionDigits:
                5, maxFractionDigits: 5, format: "###,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Saldo", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: valores[4] * 100, minFractionDigits:
                0, maxFractionDigits: 0, format: "###,##0", locale: "ec") + " %", times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
//        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: c[0] * valores[4], minFractionDigits:
//                5, maxFractionDigits: 5, format: "###,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: nuevoh, minFractionDigits:
                5, maxFractionDigits: 5, format: "###,##0", locale: "ec"), times10normal), prmsDerecha)
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


    def reporteCostosIndirectos() {

        //        println("params" + params)

        def obra = Obra.get(params.id)


        def fechaIngreso = printFecha(obra?.fechaCreacionObra)
        def fechaPrecios = printFecha(obra?.fechaPreciosRubros)

        def prmsHeaderHoja = [border: Color.WHITE]
//        def prmsHeaderHojaBorde = [border: Color.WHITE, bordeBot:"1"]
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
        def name = "costos_indirectos_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times14bold = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
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
        document.addTitle("Costos Indirectos " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("documentosObra, janus, presupuesto");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");


        Paragraph headersTitulo = new Paragraph();
        addEmptyLine(headersTitulo, 1);
        headersTitulo.setAlignment(Element.ALIGN_CENTER);
        headersTitulo.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", times14bold));
        addEmptyLine(headersTitulo, 1);
        headersTitulo.add(new Paragraph(obra?.departamento?.direccion?.nombre, times12bold));
        addEmptyLine(headersTitulo, 1)
        headersTitulo.add(new Paragraph("COSTOS INDIRECTOS", times12bold));
        addEmptyLine(headersTitulo, 1);
        document.add(headersTitulo);
//

        PdfPTable tablaHeader = new PdfPTable(3);
        tablaHeader.setWidthPercentage(100);
        tablaHeader.setWidths(arregloEnteros([20, 2, 70]))

        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)


        addCellTabla(tablaHeader, new Paragraph("OBRA", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(obra?.nombre, times8normal), prmsHeaderHoja)

/*
        addCellTabla(tablaHeader, new Paragraph("CÓDIGO", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(obra?.codigo, times8normal), prmsHeaderHoja)
*/

        addCellTabla(tablaHeader, new Paragraph("MEMO CANT. OBRA", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(obra?.memoCantidadObra, times8normal), prmsHeaderHoja)

/*
        addCellTabla(tablaHeader, new Paragraph("DOC. REFERENCIA", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(obra?.oficioIngreso, times8normal), prmsHeaderHoja)
*/

/*
        addCellTabla(tablaHeader, new Paragraph("FECHA", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(printFecha(obra?.fechaCreacionObra), times8normal), prmsHeaderHoja)
*/

        addCellTabla(tablaHeader, new Paragraph("FECHA ACT. PRECIOS", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(printFecha(obra?.fechaPreciosRubros), times8normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)

        document.add(tablaHeader);

        Paragraph headers = new Paragraph();
        headers.setAlignment(Element.ALIGN_LEFT);
        headers.add(new Paragraph("___________________________________________________________________________________________________________", times8bold));
        addEmptyLine(headers, 1);
        document.add(headers);

        PdfPTable tablaDesgloseBody = new PdfPTable(4);
        tablaDesgloseBody.setWidthPercentage(90);
        tablaDesgloseBody.setWidths(arregloEnteros([40, 8, 16, 40]))

        tablaDesgloseBody.setHorizontalAlignment(Element.ALIGN_RIGHT)

        addCellTabla(tablaDesgloseBody, new Paragraph("Dirección de Obra", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceCostosIndirectosObra, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Mantenimiento y Gastos de Oficina", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceCostosIndirectosMantenimiento, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Administrativos", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.administracion, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Garantías", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceCostosIndirectosGarantias, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Costos Financieros", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceCostosIndirectosCostosFinancieros, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Vehículos", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceCostosIndirectosVehiculos, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Promoción", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceCostosIndirectosPromocion, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Gastos Generales (subtotal)", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
//        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceGastosGenerales, minFractionDigits:
//                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10bold), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: (obra?.indiceCostosIndirectosObra + obra?.indiceCostosIndirectosMantenimiento + obra?.administracion + obra?.indiceCostosIndirectosGarantias + obra?.indiceCostosIndirectosCostosFinancieros +
                obra?.indiceCostosIndirectosVehiculos + obra?.indiceCostosIndirectosPromocion), minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10bold), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Imprevistos", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.impreso, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10bold), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Utilidad", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceUtilidad, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10bold), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Timbres provinciales", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceCostosIndirectosTimbresProvinciales, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10bold), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph("_______"), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Total Costos Indirectos", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceCostosIndirectosObra + obra?.indiceCostosIndirectosMantenimiento + obra?.administracion + obra?.indiceCostosIndirectosGarantias + obra?.indiceCostosIndirectosCostosFinancieros +
                obra?.indiceCostosIndirectosVehiculos +  obra?.indiceCostosIndirectosPromocion + obra?.impreso + obra?.indiceUtilidad +
                obra?.indiceCostosIndirectosTimbresProvinciales, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10bold), prmsDerecha)

//        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceGastosGenerales + obra?.impreso + obra?.indiceUtilidad +
//                obra?.indiceCostosIndirectosTimbresProvinciales, minFractionDigits:
//                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10bold), prmsDerecha)


        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)



        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)


        document.add(tablaDesgloseBody)
        document.close();
        pdfw.close()
        byte[] b1 = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b1.length)
        response.getOutputStream().write(b1)


    }

    def reporteCostosIndirectosNuevo () {

        //        println("params" + params)

        def obra = Obra.get(params.id)


        def fechaIngreso = printFecha(obra?.fechaCreacionObra)
        def fechaPrecios = printFecha(obra?.fechaPreciosRubros)

        def prmsHeaderHoja = [border: Color.WHITE]
//        def prmsHeaderHojaBorde = [border: Color.WHITE, bordeBot:"1"]
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
        def name = "costos_indirectos_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times14bold = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
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
        document.addTitle("Costos Indirectos " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("documentosObra, janus, presupuesto");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");


        Paragraph headersTitulo = new Paragraph();
        addEmptyLine(headersTitulo, 1);
        headersTitulo.setAlignment(Element.ALIGN_CENTER);
        headersTitulo.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", times14bold));
        addEmptyLine(headersTitulo, 1);
        headersTitulo.add(new Paragraph(obra?.departamento?.direccion?.nombre, times12bold));
        addEmptyLine(headersTitulo, 1)
        headersTitulo.add(new Paragraph("COSTOS INDIRECTOS, IMPREVISTOS Y UTILIDAD", times12bold));
        addEmptyLine(headersTitulo, 1);
        document.add(headersTitulo);
//

        PdfPTable tablaHeader = new PdfPTable(3);
        tablaHeader.setWidthPercentage(100);
        tablaHeader.setWidths(arregloEnteros([20, 2, 70]))

        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("OBRA", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(obra?.nombre, times8normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("MEMO CANT. OBRA", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(obra?.memoCantidadObra, times8normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph("FECHA ACT. PRECIOS", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" : ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(printFecha(obra?.fechaPreciosRubros), times8normal), prmsHeaderHoja)

        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaHeader, new Paragraph(" ", times8bold), prmsHeaderHoja)

        document.add(tablaHeader);

        Paragraph headers = new Paragraph();
        headers.setAlignment(Element.ALIGN_LEFT);
        headers.add(new Paragraph("___________________________________________________________________________________________________________", times8bold));
        addEmptyLine(headers, 1);
        document.add(headers);

        PdfPTable tablaDesgloseBody = new PdfPTable(4);
        tablaDesgloseBody.setWidthPercentage(90);
        tablaDesgloseBody.setWidths(arregloEnteros([40, 8, 16, 40]))

        tablaDesgloseBody.setHorizontalAlignment(Element.ALIGN_RIGHT)

        addCellTabla(tablaDesgloseBody, new Paragraph("Alquiler y depreciación", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceAlquiler, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Cargos Administrativos", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.administracion, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Cargos Profesionales", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceProfesionales, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Materiales de consumo y mantenimiento", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceCostosIndirectosMantenimiento, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Seguros", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceSeguros, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Seguridad", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceSeguridad, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph("_______"), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Gastos Administración Central", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceGastosGenerales, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10bold), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Cargos de Campo", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceCampo, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Financiamiento", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceCostosIndirectosCostosFinancieros, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Garantías", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceCostosIndirectosGarantias, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Campamento", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceCampamento, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph("_______"), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Gasto Administración de Campo", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceGastoObra, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10bold), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Imprevistos", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.impreso, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Utilidad", times10normal), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceUtilidad, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10normal), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph("_______"), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)

        addCellTabla(tablaDesgloseBody, new Paragraph("Costo total de indirectos", times10bold), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" : "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(g.formatNumber(number: obra?.indiceGastosGenerales + obra?.indiceGastoObra + obra?.impreso + obra?.indiceUtilidad, minFractionDigits:
                2, maxFractionDigits: 2, format: "##,##0", locale: "ec"), times10bold), prmsDerecha)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)



        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)
        addCellTabla(tablaDesgloseBody, new Paragraph(" "), prmsHeaderHoja)


        document.add(tablaDesgloseBody)
        document.close();
        pdfw.close()
        byte[] b1 = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b1.length)
        response.getOutputStream().write(b1)

    }

//    def addCellTabla(table, paragraph, params) {
//        PdfPCell cell = new PdfPCell(paragraph);
////        println "params "+params
//        cell.setBorderColor(Color.BLACK);
//        if (params.border) {
//            if (!params.bordeBot)
//                if (!params.bordeTop)
//                    cell.setBorderColor(params.border);
//        }
//        if (params.bg) {
//            cell.setBackgroundColor(params.bg);
//        }
//        if (params.colspan) {
//            cell.setColspan(params.colspan);
//        }
//        if (params.align) {
//            cell.setHorizontalAlignment(params.align);
//        }
//        if (params.valign) {
//            cell.setVerticalAlignment(params.valign);
//        }
//        if (params.w) {
//            cell.setBorderWidth(params.w);
//        }
//        if (params.bordeTop) {
//            cell.setBorderWidthTop(1)
//            cell.setBorderWidthLeft(0)
//            cell.setBorderWidthRight(0)
//            cell.setBorderWidthBottom(0)
//            cell.setPaddingTop(7);
//
//        }
//        if (params.bordeBot) {
//            cell.setBorderWidthBottom(1)
//            cell.setBorderWidthLeft(0)
//            cell.setBorderWidthRight(0)
//            cell.setPaddingBottom(7)
//
//            if (!params.bordeTop) {
//                cell.setBorderWidthTop(0)
//            }
//        }
//        table.addCell(cell);
//    }



    def reporteCronogramaEjec() {
//        println "reporteCronogramaEjec params: $params"
        def contrato = Contrato.get(params.id)
        def obra = contrato.obra
        def prej = PeriodoEjecucion.findAllByContrato(contrato, [sort: 'fechaInicio'])  //meses
        def vlob = preciosService.rbro_pcun_v2(obra.id)
        def rubros = []

        vlob.each {
            rubros.add([id: it.vlob__id, cdgo: it.rbrocdgo, rbro: it.rbronmbr, undd: it.unddcdgo, cntd: it.vlobcntd, pcun: it.pcun, totl: it.totl])
        }

        def baos = new ByteArrayOutputStream()

        def name = "cronogramaEjecucion_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Font catFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font catFont2 = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font catFont3 = new Font(Font.TIMES_ROMAN, 16, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font fontTitle = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]
        def prmsHeaderHoja3 = [border: Color.WHITE, colspan: 5]

        Document document
        document = new Document(PageSize.A4.rotate());
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Cronograma de Ejecucón de " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        /* ***************************************************** Titulo del reporte *******************************************************/
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);

        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", catFont3));
        preface.add(new Paragraph("CRONOGRAMA DE EJECUÓN DE OBRA", catFont2));
        addEmptyLine(preface, 1);
        Paragraph preface2 = new Paragraph();
        document.add(preface);
        document.add(preface2);
        Paragraph pMeses = new Paragraph();
        pMeses.add(new Paragraph("Obra: ${obra.descripcion}", info))
//        pMeses.add(new Paragraph("Obra: ${obra.descripcion} (${meses} mes${meses == 1 ? '' : 'es'})", info))
//        addEmptyLine(pMeses, 1);
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

        def celdaTitulo = [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def celdaDatoIzq = [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def celdaDatoDer = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def celdaDatoCen = [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]

        /* ***************************************************** Fin Titulo del reporte ***************************************************/
        /* ***************************************************** Tabla cronograma *********************************************************/
        def tams = [10, 30, 5, 6, 6, 7, 2]
        prej.size().times {
            tams.add(7)
        }
        tams.add(8)

        PdfPTable tabla = new PdfPTable(8 + prej.size());
        tabla.setWidthPercentage(100);
        tabla.setWidths(arregloEnteros(tams))
        tabla.setWidthPercentage(100);

        addCellTabla(tabla, new Paragraph("Código", fontTh), celdaTitulo)
        addCellTabla(tabla, new Paragraph("Rubro", fontTh), celdaTitulo)
        addCellTabla(tabla, new Paragraph("Unidad", fontTh), celdaTitulo)
        addCellTabla(tabla, new Paragraph("Cantidad", fontTh), celdaTitulo)
        addCellTabla(tabla, new Paragraph("P. Unitario", fontTh), celdaTitulo)
        addCellTabla(tabla, new Paragraph("C. Total", fontTh), celdaTitulo)
        addCellTabla(tabla, new Paragraph("T.", fontTh), celdaTitulo)
        prej.each { pr ->
            addCellTabla(tabla, new Paragraph("${pr.fechaInicio.format("dd-MM-yy")} a ${pr.fechaFin.format("dd-MM-yy")}", fontTh), celdaTitulo)
        }
        addCellTabla(tabla, new Paragraph("Total Rubro", fontTh), celdaTitulo)

        tabla.setHeaderRows(1)

        def totalMes = []
        def sum = 0.0
        def borderWidth = 2

        rubros.each { rb ->
            def totalDolRow = 0, totalPrcRow = 0, totalCanRow = 0
            sum += rb.totl //total por rubro

//            addCellTabla(tabla, new Paragraph((vol.rutaCritica == 'S' ? "* " : "") + vol.item.codigo, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
            addCellTabla(tabla, new Paragraph(rb.cdgo, fontTd), celdaDatoIzq)
            addCellTabla(tabla, new Paragraph(rb.rbro, fontTd), celdaDatoIzq)
            addCellTabla(tabla, new Paragraph(rb.undd, fontTd), celdaDatoCen)
            addCellTabla(tabla, new Paragraph(numero(rb.cntd, 2), fontTd), celdaDatoDer)
            addCellTabla(tabla, new Paragraph(numero(rb.pcun, 2), fontTd), celdaDatoDer)
            addCellTabla(tabla, new Paragraph(numero(rb.totl, 2), fontTd), celdaDatoDer)
            addCellTabla(tabla, new Paragraph('$', fontTd), celdaDatoIzq)
            def i = 0
            def crej
            prej.each { pr ->
                crej = CronogramaEjecucion.findByVolumenObraAndPeriodo(VolumenesObra.get(rb.id), pr)
                totalDolRow += crej?.precio?:0
                if (!totalMes[i]) {
                    totalMes[i] = 0
                }
                totalMes[i++] += crej?.precio?:0
//                println "crje: $crej ------- ${crej.precio}, ${crej.porcentaje}, ${crej.cantidad}"
                addCellTabla(tabla, new Paragraph(numero(crej?.precio?:0, 2), fontTd), celdaDatoDer)
            }
            addCellTabla(tabla, new Paragraph(numero(totalDolRow, 2) + ' $', fontTh), celdaDatoDer)

            addCellTabla(tabla, new Paragraph(' ', fontTd), celdaDatoDer + [colspan: 6])
            addCellTabla(tabla, new Paragraph('%', fontTd), celdaDatoIzq)
            prej.each { pr ->
                crej = CronogramaEjecucion.findByVolumenObraAndPeriodo(VolumenesObra.get(rb.id), pr)
                totalPrcRow += crej?.porcentaje?:0
                addCellTabla(tabla, new Paragraph(numero(crej?.porcentaje?:0, 2), fontTd), celdaDatoDer)
            }
            addCellTabla(tabla, new Paragraph(numero(totalPrcRow, 2) + ' %', fontTh), celdaDatoDer)

            addCellTabla(tabla, new Paragraph(' ', fontTd), celdaDatoDer + [colspan: 6])
            addCellTabla(tabla, new Paragraph('F', fontTd), celdaDatoIzq)
            prej.each { pr ->
                crej = CronogramaEjecucion.findByVolumenObraAndPeriodo(VolumenesObra.get(rb.id), pr)
                totalCanRow += crej?.cantidad?:0
                addCellTabla(tabla, new Paragraph(numero(crej?.cantidad?:0, 2), fontTd), celdaDatoDer)
            }
            addCellTabla(tabla, new Paragraph(numero(totalCanRow, 2) + ' F', fontTh), celdaDatoDer)
        }

        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("TOTAL PARCIAL", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
        addCellTabla(tabla, new Paragraph(numero(sum, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("T", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        prej.size().times { i ->
            addCellTabla(tabla, new Paragraph(numero(totalMes[i], 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("TOTAL ACUMULADO", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("T", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        def acu = 0
//        println "---- totalMes: $totalMes"
        prej.size().times { i ->
            acu += totalMes[i]
            addCellTabla(tabla, new Paragraph(numero(acu, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("% PARCIAL", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("T", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        prej.size().times { i ->
            def prc = 100 * totalMes[i] / sum
            addCellTabla(tabla, new Paragraph(numero(prc, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("% ACUMULADO", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("T", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        acu = 0
        prej.size().times { i ->
            def prc = 100 * totalMes[i] / sum
            acu += prc
            addCellTabla(tabla, new Paragraph(numero(acu, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        document.add(tabla)
        /* ***************************************************** Fin Tabla cronograma *****************************************************/

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

        if(contrato?.administrador){
            addCellTabla(tablaFirmas, new Paragraph((contrato.administrador?.titulo?.toUpperCase() ?: '') + " " +
                    (contrato.administrador?.nombre.toUpperCase() ?: '' ) + " " +
                    (contrato.administrador?.apellido?.toUpperCase() ?: ''), times8bold), prmsHeaderHoja)
        } else {
            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
        }

        addCellTabla(tablaFirmas, new Paragraph("", times8bold), prmsHeaderHoja)

        if(contrato?.fiscalizador){
            addCellTabla(tablaFirmas, new Paragraph((contrato?.fiscalizador?.titulo?.toUpperCase() ?: '') + " " +
                    (contrato?.fiscalizador?.nombre?.toUpperCase() ?: '') + " " +
                    (contrato?.fiscalizador?.apellido?.toUpperCase() ?: ''), times8bold), prmsHeaderHoja)
        } else {
            addCellTabla(tablaFirmas, new Paragraph("Fiscalizador no asignado", times8bold), prmsHeaderHoja)
        }
        //cargos

        addCellTabla(tablaFirmas, new Paragraph("ADMINISTRADOR", times8bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph("FISCALIZADOR", times8bold), prmsHeaderHoja)

        addCellTabla(tablaFirmas, new Paragraph(contrato?.administrador?.departamento?.descripcion?.toUpperCase() ?: '', times8bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph((contrato?.fiscalizador?.departamento?.descripcion?.toUpperCase() ?: ''), times8bold), prmsHeaderHoja)

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

    def reporteCronogramaEjeComplementario() {
//        println "reporteCronogramaEjec params: $params"
        def contrato = Contrato.get(params.id)
        def obra = contrato.obra
        def prej = PeriodoEjecucion.findAllByContrato(contrato, [sort: 'fechaInicio'])  //meses
//        def vlob = preciosService.rbro_pcun_v2(obra.id)

        def vocr = VolumenContrato.findAllByContrato(contrato, [sort: 'volumenOrden'])
        def rubros = []

//        vlob.each {
//            rubros.add([id: it.vlob__id, cdgo: it.rbrocdgo, rbro: it.rbronmbr, undd: it.unddcdgo, cntd: it.vlobcntd, pcun: it.pcun, totl: it.totl])
//        }

        def baos = new ByteArrayOutputStream()

        def name = "cronogramaEjecucion_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Font catFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font catFont2 = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
        Font catFont3 = new Font(Font.TIMES_ROMAN, 16, Font.BOLD);
        Font info = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font fontTitle = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font fontTd = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja2 = [border: Color.WHITE, colspan: 9]
        def prmsHeaderHoja3 = [border: Color.WHITE, colspan: 5]

        Document document
        document = new Document(PageSize.A4.rotate());
        def pdfw = PdfWriter.getInstance(document, baos);
        document.open();
        PdfContentByte cb = pdfw.getDirectContent();
        document.addTitle("Cronograma de Ejecución de " + obra.nombre + " " + new Date().format("dd_MM_yyyy"));
        document.addSubject("Generado por el sistema Janus");
        document.addKeywords("reporte, janus, planillas");
        document.addAuthor("Janus");
        document.addCreator("Tedein SA");

        /* ***************************************************** Titulo del reporte *******************************************************/
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);

        preface.add(new Paragraph("SEP - G.A.D. PROVINCIA DE PICHINCHA", catFont3));
        preface.add(new Paragraph("CRONOGRAMA DE EJECUCIÓN DE OBRA", catFont2));
        addEmptyLine(preface, 1);
        Paragraph preface2 = new Paragraph();
        document.add(preface);
        document.add(preface2);
        Paragraph pMeses = new Paragraph();
        pMeses.add(new Paragraph("Obra: ${obra.descripcion}", info))
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

        def cmpl = Contrato.findByPadre(contrato)
        def txto = " "
        if(cmpl) {
            txto = " Incluye plazo del contrato complementario: ${cmpl.codigo}"
        }
        Paragraph plazo = new Paragraph();
//        plazo.add(new Paragraph("Plazo: ${obra?.plazoEjecucionMeses} Meses" + " ${obra?.plazoEjecucionDias} Días", info))
        plazo.add(new Paragraph("Plazo: ${contrato?.plazo} Días ${txto}", info))

        document.add(plazo);

        Paragraph rutaCritica = new Paragraph();
        rutaCritica.add(new Paragraph("Los rubros pertenecientes a la ruta crítica están marcados con un * antes de su código.", info))
        addEmptyLine(rutaCritica, 1);
        document.add(rutaCritica);

        def celdaTitulo = [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def celdaDatoIzq = [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def celdaDatoDer = [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE]
        def celdaDatoCen = [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]

        /* ***************************************************** Fin Titulo del reporte ***************************************************/
        /* ***************************************************** Tabla cronograma *********************************************************/
        def tams = [10, 30, 5, 6, 6, 7, 2]
        prej.size().times {
            tams.add(7)
        }
        tams.add(8)

        PdfPTable tabla = new PdfPTable(8 + prej.size());
        tabla.setWidthPercentage(100);
        tabla.setWidths(arregloEnteros(tams))
        tabla.setWidthPercentage(100);

        addCellTabla(tabla, new Paragraph("Código", fontTh), celdaTitulo)
        addCellTabla(tabla, new Paragraph("Rubro", fontTh), celdaTitulo)
        addCellTabla(tabla, new Paragraph("Unidad", fontTh), celdaTitulo)
        addCellTabla(tabla, new Paragraph("Cantidad", fontTh), celdaTitulo)
        addCellTabla(tabla, new Paragraph("P. Unitario", fontTh), celdaTitulo)
        addCellTabla(tabla, new Paragraph("C. Total", fontTh), celdaTitulo)
        addCellTabla(tabla, new Paragraph("T.", fontTh), celdaTitulo)
        prej.each { pr ->
            addCellTabla(tabla, new Paragraph("${pr.fechaInicio.format("dd-MM-yy")} a ${pr.fechaFin.format("dd-MM-yy")}", fontTh), celdaTitulo)
        }
        addCellTabla(tabla, new Paragraph("Total Rubro", fontTh), celdaTitulo)

        tabla.setHeaderRows(1)

        def totalMes = []
        def sum = 0.0
        def borderWidth = 2

//        rubros.each { rb ->
        vocr.each { rb ->
            def totalDolRow = 0, totalPrcRow = 0, totalCanRow = 0
//            sum += rb.totl //total por rubro
            sum += rb.volumenSubtotal //total por rubro
            sum += 0

//            addCellTabla(tabla, new Paragraph(rb.cdgo, fontTd), celdaDatoIzq)
            addCellTabla(tabla, new Paragraph(rb.item.codigo, fontTd), celdaDatoIzq)
//            addCellTabla(tabla, new Paragraph(rb.rbro, fontTd), celdaDatoIzq)
            addCellTabla(tabla, new Paragraph(rb.item.nombre, fontTd), celdaDatoIzq)
            addCellTabla(tabla, new Paragraph(rb.item.unidad.codigo, fontTd), celdaDatoCen)
            addCellTabla(tabla, new Paragraph(numero(rb.volumenCantidad, 2), fontTd), celdaDatoDer)
            addCellTabla(tabla, new Paragraph(numero(rb.volumenPrecio, 2), fontTd), celdaDatoDer)
            addCellTabla(tabla, new Paragraph(numero(rb.volumenSubtotal, 2), fontTd), celdaDatoDer)
            addCellTabla(tabla, new Paragraph('$', fontTd), celdaDatoIzq)
            def i = 0
            def crej
            prej.each { pr ->
//                crej = CronogramaEjecucion.findByVolumenObraAndPeriodo(VolumenesObra.get(rb.id), pr)
                crej = CrngEjecucionObra.findByVolumenObraAndPeriodo(VolumenContrato.get(rb.id), pr)
                totalDolRow += crej?.precio?:0
                if (!totalMes[i]) {
                    totalMes[i] = 0
                }
                totalMes[i++] += crej?.precio?:0
                addCellTabla(tabla, new Paragraph(numero(crej?.precio?:0, 2), fontTd), celdaDatoDer)
            }
            addCellTabla(tabla, new Paragraph(numero(totalDolRow, 2) + ' $', fontTh), celdaDatoDer)

            addCellTabla(tabla, new Paragraph(' ', fontTd), celdaDatoDer + [colspan: 6])
            addCellTabla(tabla, new Paragraph('%', fontTd), celdaDatoIzq)
            prej.each { pr ->
                crej = CrngEjecucionObra.findByVolumenObraAndPeriodo(VolumenContrato.get(rb.id), pr)
                totalPrcRow += crej?.porcentaje?:0
                addCellTabla(tabla, new Paragraph(numero(crej?.porcentaje?:0, 2), fontTd), celdaDatoDer)
            }
            addCellTabla(tabla, new Paragraph(numero(totalPrcRow, 2) + ' %', fontTh), celdaDatoDer)

            addCellTabla(tabla, new Paragraph(' ', fontTd), celdaDatoDer + [colspan: 6])
            addCellTabla(tabla, new Paragraph('F', fontTd), celdaDatoIzq)
            prej.each { pr ->
                crej = CrngEjecucionObra.findByVolumenObraAndPeriodo(VolumenContrato.get(rb.id), pr)
                totalCanRow += crej?.cantidad?:0
                addCellTabla(tabla, new Paragraph(numero(crej?.cantidad?:0, 2), fontTd), celdaDatoDer)
            }
            addCellTabla(tabla, new Paragraph(numero(totalCanRow, 2) + ' F', fontTh), celdaDatoDer)
        }

        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("TOTAL PARCIAL", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
        addCellTabla(tabla, new Paragraph(numero(sum, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("T", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        prej.size().times { i ->
            addCellTabla(tabla, new Paragraph(numero(totalMes[i], 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("TOTAL ACUMULADO", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("T", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        def acu = 0
//        println "---- totalMes: $totalMes"
        prej.size().times { i ->
            acu += totalMes[i]
            addCellTabla(tabla, new Paragraph(numero(acu, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("% PARCIAL", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("T", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        prej.size().times { i ->
            def prc = 100 * totalMes[i] / sum
            addCellTabla(tabla, new Paragraph(numero(prc, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("% ACUMULADO", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 4])
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        addCellTabla(tabla, new Paragraph("T", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
        acu = 0
        prej.size().times { i ->
            def prc = 100 * totalMes[i]/ sum
            acu += prc
            addCellTabla(tabla, new Paragraph(numero(acu, 2), fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
        }
        addCellTabla(tabla, new Paragraph(" ", fontTh), [border: Color.BLACK, bg: Color.LIGHT_GRAY, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])

        document.add(tabla)
        /* ***************************************************** Fin Tabla cronograma *****************************************************/

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

        if(contrato?.administrador){
            addCellTabla(tablaFirmas, new Paragraph((contrato.administrador?.titulo?.toUpperCase() ?: '') + " " +
                    (contrato?.administrador?.nombre?.toUpperCase() ?: '' ) + " " +
                    (contrato?.administrador?.apellido?.toUpperCase() ?: ''), times8bold), prmsHeaderHoja)
        } else {
            addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
        }

        addCellTabla(tablaFirmas, new Paragraph("", times8bold), prmsHeaderHoja)

        if(contrato?.fiscalizador){
            addCellTabla(tablaFirmas, new Paragraph((contrato?.fiscalizador?.titulo?.toUpperCase() ?: '') + " " +
                    (contrato?.fiscalizador?.nombre?.toUpperCase() ?: '') + " " +
                    (contrato?.fiscalizador?.apellido?.toUpperCase() ?: ''), times8bold), prmsHeaderHoja)
        } else {
            addCellTabla(tablaFirmas, new Paragraph("Fiscalizador no asignado", times8bold), prmsHeaderHoja)
        }
        //cargos

        addCellTabla(tablaFirmas, new Paragraph("ADMINISTRADOR", times8bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph("FISCALIZADOR", times8bold), prmsHeaderHoja)

        addCellTabla(tablaFirmas, new Paragraph(contrato?.administrador?.departamento?.descripcion?.toUpperCase() ?: '', times8bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph("", times8bold), prmsHeaderHoja)
        addCellTabla(tablaFirmas, new Paragraph((contrato?.fiscalizador?.departamento?.descripcion?.toUpperCase() ?: ''), times8bold), prmsHeaderHoja)

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


}
