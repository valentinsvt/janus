package janus

class VerificacionPreciosController {

    def dbConnectionService

    def verificacion () {
        /** muestra precios no actualizados 7 meses atras */
        def cn = dbConnectionService.getConnection()
        def obra = Obra.get(params.id)

        def sql = "SELECT distinct itemcdgo codigo, itemnmbr item, unddcdgo unidad, rbpcpcun  punitario, " +
                "rbpcfcha  fecha FROM obra_rbpc(${params.id}) " +
                "where rbpcfcha < (cast('${obra.fechaPreciosRubros.format('yyyy-MM-dd')}' as date) - 210)  " +
                "ORDER BY itemnmbr"

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
