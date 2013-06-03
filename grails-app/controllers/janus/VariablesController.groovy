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
        def volquetes2 = []
        def choferes = []
        def grupoTransporte = DepartamentoItem.findAllByTransporteIsNotNull()

        grupoTransporte.each {
            if (it.transporte.codigo == "H")
                choferes = Item.findAllByDepartamento(it)
            if (it.transporte.codigo == "T")
                volquetes = Item.findAllByDepartamento(it)

            volquetes2 += volquetes

//            println("volquetes" + volquetes)


        }

//        println("volquetes2" + volquetes2)

        [choferes: choferes, volquetes: volquetes, obra: obra, par: par, volquetes2: volquetes2]
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
        if (!params.sp) {
            params.sp = '-1'
        }

        def obra = Obra.get(params.id)
        if (params.tipo == "-1") {
            params.tipo = "1,2,3"
        }
        def wsp = ""
        if (params.sp.toString() != "-1") {
            wsp = "      AND v.sbpr__id = ${params.sp} \n"
        }

/*
        def sql = "SELECT \n" +
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
                "  v.sbpr__id                            sp\n" +
                "FROM vlobitem v\n" +
                "INNER JOIN item i ON v.item__id = i.item__id\n" +
                "INNER JOIN undd u ON i.undd__id = u.undd__id\n" +
                "INNER JOIN dprt d ON i.dprt__id = d.dprt__id\n" +
                "INNER JOIN sbgr s ON d.sbgr__id = s.sbgr__id\n" +
                "INNER JOIN grpo g ON s.grpo__id = g.grpo__id AND g.grpo__id IN (${params.tipo})\n" +
                "WHERE v.obra__id = ${params.id} \n" +
                wsp +
                "  ORDER BY grid ASC"
*/
        def sql = "SELECT i.itemcdgo codigo, i.itemnmbr item, u.unddcdgo unidad, sum(v.voitcntd) cantidad, \n" +
                  "v.voitpcun punitario, v.voittrnp transporte, v.voitpcun + v.voittrnp  costo, \n" +
                  "sum((v.voitpcun + v.voittrnp) * v.voitcntd)  total, g.grpodscr grupo, g.grpo__id grid \n" +
                  "FROM vlobitem v INNER JOIN item i ON v.item__id = i.item__id\n" +
                        "INNER JOIN undd u ON i.undd__id = u.undd__id\n" +
                        "INNER JOIN dprt d ON i.dprt__id = d.dprt__id\n" +
                        "INNER JOIN sbgr s ON d.sbgr__id = s.sbgr__id\n" +
                        "INNER JOIN grpo g ON s.grpo__id = g.grpo__id AND g.grpo__id IN (${params.tipo}) \n" +
                  "WHERE v.obra__id = ${params.id} and v.voitcntd >0 \n" + wsp +
                  "group by i.itemcdgo, i.itemnmbr, u.unddcdgo, v.voitpcun, v.voittrnp, v.voitpcun, \n" +
                             "g.grpo__id, g.grpodscr " +
                  "ORDER BY g.grpo__id ASC, i.itemcdgo"

//        println "composicion" + sql
        def sqlSP = "SELECT\n" +
                "  DISTINCT v.sbpr__id      id,\n" +
                "  s.sbprdscr               dsc,\n" +
                "  count(v.item__id)        count\n" +
                "FROM vlobitem v\n" +
                "  INNER JOIN sbpr s\n" +
                "    ON v.sbpr__id = s.sbpr__id\n" +
                "WHERE v.obra__id = ${params.id}\n" +
                "GROUP BY 1, 2"

        def cn = dbConnectionService.getConnection()

        if (params.rend == "screen" || params.rend == "pdf") {

            def res = cn.rows(sql.toString())

            def sp = cn.rows(sqlSP.toString())
            return [res: res, obra: obra, tipo: params.tipo, rend: params.rend, sp: sp, spsel: params.sp]
        }
    }

}
