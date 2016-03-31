package janus

class VerificacionPreciosController {

    def dbConnectionService

    def verificacion () {
        def cn = dbConnectionService.getConnection()
        def obra = Obra.get(params.id)

        def sql = "SELECT distinct\n" +
                  "itemcdgo  codigo,\n"      +
                  "itemnmbr  item,\n"      +
                  "unddcdgo  unidad,\n"      +
                  "rbpcpcun  punitario,\n"      +
                  "rbpcfcha  fecha\n"      +
                  "FROM obra_rbpc(${params.id}) \n"  +
                  "ORDER BY item ASC"

        def res = cn.rows(sql.toString())

        return[res: res, obra: obra]
    }

    def preciosCero () {
        def cn = dbConnectionService.getConnection()
        def obra = Obra.get(params.id)

        def sql = "select distinct item.itemcdgo codigo, itemnmbr item, unddcdgo unidad, voitpcun  punitario " +
                "from vlobitem, item, undd " +
                "where obra__id = ${params.id} and voitpcun = 0 and item.item__id = vlobitem.item__id and " +
                "undd.undd__id = item.undd__id order by item.itemcdgo"

//        println "sql: $sql"

        def res = cn.rows(sql.toString())

        return[res: res, obra: obra]
    }

}
