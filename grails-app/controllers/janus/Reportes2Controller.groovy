package janus

import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook

class Reportes2Controller {

    def preciosService

    def index() { }

    def test() {
        return [params: params]
    }

    def reportePrecios() {
//        params.orden = "a" //a,n    Alfabetico | Numerico
//        params.col = ["t", "u", "p", "f"] //t,u,p,f   Transporte | Unidad | Precio | Fecha de Act
//        params.fecha = "22-11-2012"
//        params.lugar = "4"
//        params.grupo = "1"
//
//        println params

        def orden = "itemnmbr"
        if (params.orden == "n") {
            orden = "itemcdgo"
        }
        def lugar = Lugar.get(params.lugar.toLong())
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        def items = ""
        def lista = Item.withCriteria {
            eq("tipoItem", TipoItem.findByCodigo("I"))
            departamento {
                subgrupo {
                    eq("grupo", Grupo.get(params.grupo.toLong()))
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

        return [lugar: lugar, cols: params.col, precios: res]
    }


    def reporteExcelComposicion () {

        def obra = Obra.get(params.id)


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
        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, true);
        WritableCellFormat times16format = new WritableCellFormat(times16font);





        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "ComposicionExcel.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());


    }

}
