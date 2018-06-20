package janus

class VariablesController  extends janus.seguridad.Shield{

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
        }

        def transporteCamioneta =  Item.findAllByCodigoIlike('tc-%');
        def transporteAcemila =  Item.findAllByCodigoIlike('ta-%');
        def total1 = (obra?.indiceAlquiler ?: 0) + (obra?.administracion ?: 0) + (obra?.indiceCostosIndirectosMantenimiento ?: 0) + (obra?.indiceProfesionales ?: 0) + (obra?.indiceSeguros ?: 0)  + (obra?.indiceSeguridad ?: 0)
        def total2 = (obra?.indiceCampo ?: 0) + (obra?.indiceCostosIndirectosCostosFinancieros ?: 0) + (obra?.indiceCostosIndirectosGarantias ?: 0) + (obra?.indiceCampamento ?: 0)
        def total3 = (total1 ?:0 ) + (total2 ?: 0) + (obra?.impreso ?: 0) + (obra?.indiceUtilidad ?: 0)

        if(obra.estado != 'R') {
            obra.indiceGastosGenerales = total1
            obra.indiceGastoObra = total2
            obra.totales = total3
            obra.save(flush: true)
        }

        [choferes: choferes, volquetes: volquetes, obra: obra, par: par, volquetes2: volquetes2, transporteCamioneta: transporteCamioneta, transporteAcemila: transporteAcemila, total1: total1, total2: total2]
    }

    def saveVar_ajax() {
//        println "save vars aqui"
        //println params

        def obra = Obra.get(params.id)
        obra.properties = params
//        obra.capacidadVolquete=params.asdas.toDouble()
//        obra.factorVolumen=params.factorVolumen.toDouble()
        if (!obra.transporteCamioneta) obra.distanciaCamioneta = 0
        if (!obra.transporteAcemila) obra.distanciaAcemila = 0
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

//        println(params)

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
            return [res: res, obra: obra, tipo: params.tipo, rend: params.rend, sp: sp, spsel: params.sp, sub: params.sp]
        }
    }

    def composicionVae() {
//        println(params)

        if (!params.tipo) {
            params.tipo = "-1"
        }

        def obra = Obra.get(params.id)
        if (params.tipo == "-1") {
            params.tipo = "1,2,3"
        }

        def sql = "SELECT i.item__id, i.itemcdgo codigo, i.itemnmbr item, u.unddcdgo unidad, sum(v.voitcntd) cantidad, \n" +
                  "v.voitpcun punitario, v.voittrnp transporte, v.voitpcun + v.voittrnp  costo, \n" +
                  "sum((v.voitpcun + v.voittrnp) * v.voitcntd)  total, g.grpodscr grupo, g.grpo__id grid, v.tpbnpcnt\n" +
                  "FROM vlobitem v INNER JOIN item i ON v.item__id = i.item__id\n" +
                        "INNER JOIN undd u ON i.undd__id = u.undd__id\n" +
                        "INNER JOIN dprt d ON i.dprt__id = d.dprt__id\n" +
                        "INNER JOIN sbgr s ON d.sbgr__id = s.sbgr__id\n" +
                        "INNER JOIN grpo g ON s.grpo__id = g.grpo__id AND g.grpo__id IN (${params.tipo}) \n" +
                  "WHERE v.obra__id = ${params.id} and v.voitcntd >0 \n" +
                  "group by i.item__id, i.itemcdgo, i.itemnmbr, u.unddcdgo, v.voitpcun, v.voittrnp, v.voitpcun, \n" +
                             "g.grpo__id, g.grpodscr, v.tpbnpcnt " +
                  "ORDER BY g.grpo__id ASC, i.itemcdgo"

//        println "composicion" + sql

        def cn = dbConnectionService.getConnection()

        def res = cn.rows(sql.toString())
        return [res: res, obra: obra, tipo: params.tipo]
    }


    def actualizaVae() {
        def cn = dbConnectionService.getConnection()
//        println "actualizaVae: " + params
//        println("clase " + params?.item?.class)
        // formato de id:###/new _ prin _ indc _ valor
        if(params?.item?.class == java.lang.String) {
            params?.item = [params?.item]
        }

        def oks = "", nos = ""
        params.item.each {
            //println "Procesa: " + it
            def vlor = it.split("_")
//            println "vlor: " + vlor
            if (vlor[0] != "new") {
//                println "nuevo valor: " + vlor[0].toInteger()
//                println "update vlobitem set tpbnpcnt = ${vlor[1].toDouble()} where obra__id = ${params.obra} and item__id = ${vlor[0]}"
                cn.execute("update vlobitem set tpbnpcnt = ${vlor[1].toDouble()} where obra__id = ${params.obra} and item__id = ${vlor[0]}".toString())
            }
        }
        render "ok"
    }


}
