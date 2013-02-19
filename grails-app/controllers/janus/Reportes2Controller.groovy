package janus

import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import jxl.write.Label
import jxl.write.Number


import java.lang.Number



class Reportes2Controller {

    def preciosService
    def dbConnectionService

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


        params.tipo = "1,2,3"

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
                "  g.grpo__id                            grid\n" +
                "FROM vlobitem v\n" +
                "INNER JOIN item i ON v.item__id = i.item__id\n" +
                "INNER JOIN undd u ON i.undd__id = u.undd__id\n" +
                "INNER JOIN dprt d ON i.dprt__id = d.dprt__id\n" +
                "INNER JOIN sbgr s ON d.sbgr__id = s.sbgr__id\n" +
                "INNER JOIN grpo g ON s.grpo__id = g.grpo__id AND g.grpo__id IN (${params.tipo})\n" +
                "WHERE v.obra__id = ${params.id} \n" +
                "  ORDER BY grid ASC"

        def cn = dbConnectionService.getConnection()

        def res = cn.rows(sql.toString())

//        println(res)

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

        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, true);
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
        def fila = 8;
        def totalE = 0;
        def totalM = 0;
        def totalMO = 0;
        def totalEquipo = 0;
        def totalManoObra = 0;
        def totalMaterial = 0;
        def totalDirecto = 0;
        def ultimaFila

        label = new Label(2, 4, "Composición de " + obra?.nombre, times16format); sheet.addCell(label);

        label = new Label(0, 6, "CODIGO", times16format); sheet.addCell(label);
        label = new Label(1, 6, "ITEM", times16format); sheet.addCell(label);
        label = new Label(2, 6, "UNIDAD", times16format); sheet.addCell(label);
        label = new Label(3, 6, "CANTIDAD", times16format); sheet.addCell(label);
        label = new Label(4, 6, "P.UNITARIO", times16format); sheet.addCell(label);
        label = new Label(5, 6, "TRANSPORTE", times16format); sheet.addCell(label);
        label = new Label(6, 6, "COSTO", times16format); sheet.addCell(label);
        label = new Label(7, 6, "TOTAL", times16format); sheet.addCell(label);
        label = new Label(8, 6, "TIPO", times16format); sheet.addCell(label);

        res.each {


        label = new Label(0, fila, it?.codigo.toString()); sheet.addCell(label);
        label = new Label(1, fila, it?.item.toString()); sheet.addCell(label);
        label = new Label(2, fila, it?.unidad.toString()); sheet.addCell(label);
        number = new jxl.write.Number(3, fila, it?.cantidad); sheet.addCell(number);
        number = new jxl.write.Number(4, fila, it?.punitario); sheet.addCell(number);
        number = new jxl.write.Number(5, fila, it?.transporte); sheet.addCell(number);
        number = new jxl.write.Number(6, fila, it?.costo); sheet.addCell(number);
        number = new jxl.write.Number(7, fila, it?.total); sheet.addCell(number);
        label = new Label(8, fila, it?.grupo.toString()); sheet.addCell(label);

        fila++

            if (it?.grid == 1){

                totalMaterial = (totalM+=it?.total)

            }
            if(it?.grid == 2) {

                totalManoObra = (totalMO+=it?.total)
            }

            if(it?.grid == 3) {

                totalEquipo = (totalE+=it?.total)

            }

            totalDirecto = totalEquipo+totalManoObra+totalMaterial;


         ultimaFila = fila


        }

        label = new Label(6, ultimaFila, "Total Materiales: ", times16format); sheet.addCell(label);
        number = new jxl.write.Number(7, ultimaFila, totalMaterial); sheet.addCell(number);

        label = new Label(6, ultimaFila+1, "Total Mano de Obra: ", times16format); sheet.addCell(label);
        number = new jxl.write.Number(7, ultimaFila+1, totalManoObra); sheet.addCell(number);

        label = new Label(6, ultimaFila+2, "Total Equipos: ", times16format); sheet.addCell(label);
        number = new jxl.write.Number(7, ultimaFila+2, totalEquipo); sheet.addCell(number);

        label = new Label(6, ultimaFila+3, "TOTAL DIRECTO: ", times16format); sheet.addCell(label);
        number = new jxl.write.Number(7, ultimaFila+3, totalDirecto); sheet.addCell(number);

        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "ComposicionExcel.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());


    }


    def reportePreciosExcel() {

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

        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, true);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        sheet.setColumnView(0, 20)
        sheet.setColumnView(1, 60)
        sheet.setColumnView(2, 15)
        sheet.setColumnView(3, 20)
        sheet.setColumnView(4, 20)
        sheet.setColumnView(5, 20)
        sheet.setColumnView(6, 25)


        def label
        def number
        def fila = 8;

        label = new Label(2, 1, "Gobierno Autónomo Descentralizado de la Provincia de Pichincha".toUpperCase(), times16format); sheet.addCell(label);
        label = new Label(2, 2, "Reporte de Costos de Materiales", times16format); sheet.addCell(label);

        label = new Label(1,4, lugar?.descripcion, times16format); sheet.addCell(label);
        label = new Label(4,4, "Fecha Consulta: " + new Date().format("dd-MM-yyyy"), times16format); sheet.addCell(label);


        label = new Label(0, 6, "CODIGO", times16format); sheet.addCell(label);
        label = new Label(1, 6, "MATERIAL", times16format); sheet.addCell(label);
        label = new Label(2, 6, "UNIDAD", times16format); sheet.addCell(label);
        label = new Label(3, 6, "PESO/VOL", times16format); sheet.addCell(label);
        label = new Label(4, 6, "COSTO", times16format); sheet.addCell(label);
        label = new Label(5, 6, "FECHA ACT.", times16format); sheet.addCell(label);

        res.each {

            label = new Label(0, fila, it?.item?.codigo.toString()); sheet.addCell(label);
            label = new Label(1, fila, it?.item?.nombre.toString()); sheet.addCell(label);
            label = new Label(2, fila, it?.item?.unidad?.codigo.toString()); sheet.addCell(label);
            number = new jxl.write.Number(3, fila, it?.item?.peso); sheet.addCell(number);
            number = new jxl.write.Number(4, fila, it?.precioUnitario); sheet.addCell(number);
            label = new Label(5, fila, it?.fecha.format("dd-MM-yyyy"));  sheet.addCell(label);

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


}
