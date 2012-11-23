package janus
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;


import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.PageSize;

class ReportesController {

    def index() { }

    def buscadorService



    def rubro = {
        println "rep!!!  rubro "+params
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
        return [algo:"algo"]
    }

    def reporteBuscador= {

        // println "reporte buscador params !! "+params
        if (!session.dominio)
            response.sendError(403)
        else{
            def listaTitulos = params.listaTitulos
            def listaCampos = params.listaCampos
            def lista = buscadorService.buscar(session.dominio, params.tabla, "excluyente", params, true,params.extras)
            def funciones = session.funciones
            session.dominio=null
            session.funciones=null
            lista.pop()

            def baos = new ByteArrayOutputStream()
            def name = "reporte_de_"+params.titulo.replaceAll(" ","_")+"_"+new Date().format("ddMMyyyy_hhmm")+".pdf";
//            println "name "+name
            Font catFont = new Font(Font.TIMES_ROMAN, 10,Font.BOLD);
            Font info = new Font(Font.TIMES_ROMAN, 8,Font.NORMAL)
            Document document
            if(params.landscape)
                document = new Document(PageSize.A4.rotate());
            else
                document = new Document();

            def pdfw= PdfWriter.getInstance(document,baos);

            document.open();
            document.addTitle("Reporte de "+params.titulo+" "+new Date().format("dd_MM_yyyy"));
            document.addSubject("Generado por el sistema Janus");
            document.addKeywords("reporte, elyon,"+params.titulo);
            document.addAuthor("Janus");
            document.addCreator("Tedein SA");
            Paragraph preface = new Paragraph();
            addEmptyLine(preface, 1);
            preface.add(new Paragraph("Reporte de "+params.titulo, catFont));
            preface.add(new Paragraph("Generado por el usuario: "+session.usuario+"   el: "+new Date().format("dd/MM/yyyy hh:mm"),info))
            addEmptyLine(preface, 1);
            document.add(preface);
//        Start a new page
//        document.newPage();
            //System.getProperty("user.name")
            addContent(document,catFont,listaCampos.size(),listaTitulos,params.anchos,listaCampos,funciones,lista);            // Los tamaños son porcentajes!!!!
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


    private static void addContent(Document document,catFont,columnas,headers,anchos,campos,funciones,datos) throws DocumentException {
        Font small= new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
        def parrafo =  new Paragraph("")
        createTable(parrafo,columnas,headers,anchos,campos,funciones,datos);
        document.add(parrafo);



    }


    private static void createTable(Paragraph subCatPart,columnas,headers,anchos,campos,funciones,datos) throws BadElementException {
        PdfPTable table = new PdfPTable(columnas);
        table.setWidthPercentage(100);
        table.setWidths(arregloEnteros(anchos))
        Font small= new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
        headers.eachWithIndex{h,i->
            PdfPCell c1 = new PdfPCell(new Phrase(h,small));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
        }
        table.setHeaderRows(1);
        def tagLib = new BuscadorTagLib()
        datos.each{d->
            campos.eachWithIndex{c,j->
                def campo
                if(funciones){
                    if(funciones[j])
                        campo = tagLib.operacion([propiedad:c,funcion:funciones[j],registro:d]).toString()
                    else
                        campo = d.properties[c].toString()
                }else{
                    campo = d.properties[c].toString()
                }

                table.addCell(new Phrase(campo,small));

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


    static arregloEnteros(array){
        int[] ia= new int [array.size()]
        array.eachWithIndex{it,i->
            ia[i]=it.toInteger()
        }

        return ia
    }


}
