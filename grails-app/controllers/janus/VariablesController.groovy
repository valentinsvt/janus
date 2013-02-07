package janus

class VariablesController {

    def dbConnectionService

    def variables_ajax() {
//        println params

        def obra = Obra.get(params.obra)
        def par = Parametros.list()
        if (par.size() > 0)
            par = par.pop()

        def volquetes = []
        def choferes = []
        def grupoTransporte = DepartamentoItem.findAllByTransporteIsNotNull()
        grupoTransporte.each {
            if (it.transporte.codigo == "H")
                choferes = Item.findAllByDepartamento(it)
            if (it.transporte.codigo == "T")
                volquetes = Item.findAllByDepartamento(it)
        }

        [choferes: choferes, volquetes: volquetes, obra: obra, par: par]
    }

    def saveVar_ajax() {
        println "save vars aqui"
        println params
        def obra = Obra.get(params.id)
        obra.properties = params
//        obra.capacidadVolquete=params.asdas.toDouble()
//        obra.factorVolumen=params.factorVolumen.toDouble()
        if (obra.save(flush: true)) {
            render "OK"
        } else {
            println obra.errors
            render "NO"
        }
    }

    def composicion() {
//        if (!params.id) {
//            params.id = "886"
//        }
        if (!params.tipo) {
            params.tipo = "-1"
        }
        if (!params.rend) {
            params.rend = "screen"
        }

        def obra = Obra.get(params.id)
        if (params.tipo == "-1") {
            params.tipo = "1,2,3"
        }

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

        if (params.rend == "screen" || params.rend == "pdf") {
            def res = cn.rows(sql.toString())
            return [res: res, obra: obra, tipo: params.tipo, rend: params.rend]
        }
    }

}
