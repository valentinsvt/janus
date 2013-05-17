package janus

class VerificacionPreciosController {

    def dbConnectionService

    def verificacion () {

        def obra = Obra.get(params.id)

        def vol = janus.VolumenesObra.findAllByObra(obra)

//        println("params:" + params)

        def sql = "SELECT distinct\n" +
                  "itemcdgo                     codigo,\n"      +
                  "itemnmbr                     item,\n"      +
                  "unddcdgo                     unidad,\n"      +
                  "rbpcpcun                     punitario,\n"      +
                  "rbpcfcha                     fecha\n"      +
                  "FROM obra_rbpc(${params.id}) \n"  +
                  "ORDER BY item ASC"


//        println("sql:" + sql)

        def cn = dbConnectionService.getConnection()

        def res = cn.rows(sql.toString())

        return[res: res, obra: obra, vol: vol]

    }


}
