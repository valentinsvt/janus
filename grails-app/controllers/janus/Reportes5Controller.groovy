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

import java.awt.Color

class Reportes5Controller {

    def dbConnectionService

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
//        println res
        params.criterio = params.old

        return [res: res, params: params]
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
}
