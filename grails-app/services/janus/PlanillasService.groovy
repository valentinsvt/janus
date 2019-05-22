package janus

import janus.ejecucion.PeriodoPlanilla
import janus.ejecucion.Planilla
import janus.ejecucion.ReajustePlanilla
import janus.ejecucion.ValorIndice
import janus.ejecucion.ValorReajuste

class PlanillasService {
    def preciosService
    def dbConnectionService

    def calculaValores(PeriodoPlanilla periodo, cs, ps, pcs, obra, anticipo) {
        println "calculando indices de " + periodo.planilla.id + "  " + periodo.fechaIncio.format("dd-MM-yyyyy") + "  " + periodo.fechaFin.format("dd-MM-yyyyy")
        def totalB0 = 0
        periodo.frReajuste = 0
        periodo.b0Reajuste = 0
        def valorIndice
        cs.each { c ->
            valorIndice = ValorIndice.findByPeriodoAndIndice(periodo.periodoReajuste, c.indice)?.valor
            if (!valorIndice) {
//                println "wtf no valor " + p.periodo + "  " + c.indice
                valorIndice = 0
            }
            def valor
            def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${periodo.planilla.id} and periodoIndice =${periodo.periodo.id} and formulaPolinomica=${c.id}")
            if (vlrj.size() > 0) {
                valor = vlrj.pop()
                valor.periodoReajuste = periodo.periodoReajuste
                valor.valorReajuste = valorIndice * c.valor
                totalB0 += (valorIndice * c.valor).toDouble().round(3)
                valor.save(flush: true)
            } else {
//                println "error wtf no hay vlrj => from ValorReajuste where obra=${obra.id} and planilla=${p.planilla.id} and periodoIndice =${p.periodo.id} and formulaPolinomica=${c.id}"
                /*Aqui recalcular*/
                valor = -1
            }
        }
        periodo.b0Reajuste = totalB0
        periodo.save(flush: true)
        def periodos = PeriodoPlanilla.findAllByPlanilla(anticipo, [sort: "id"])
        def periodos2 = []
        periodos2.add(periodo)

        println("ps " + ps)

        ps.eachWithIndex { p, i ->
            def vlinOferta = null
            periodos.eachWithIndex { per, j ->
                if (j == 0) { // es la oferta
                    def valor = 0
                    if (i == 0) { //es mano de obra
                        if (per.periodoReajuste) {
                            vlinOferta = per.b0Reajuste
                            valor = per.b0Reajuste
                        } else {
                            vlinOferta = per.total
                            valor = per.total
                        }

                    } else {
                        vlinOferta = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
                        valor = vlinOferta
                    }
                } else {
                    def vlin, dec = 3
                    if (i == 0) {
                        // println "es mano de obra "+per.id+"  "+per+"  "+per.b0Reajuste+" "+per.total
                        vlin = (per.periodoReajuste ? per.b0Reajuste : per.total)//
                        //  println "vlin "+vlin
                    } else {//
                        if (per.periodoReajuste) {
                            vlin = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodoReajuste).valor
                        } else {
                            vlin = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
                        }
                    }
                    def valor = (vlin / vlinOferta * p.valor).round(3)
                }
            }

            periodos2.eachWithIndex { per, j ->
                def vlin
                if (i == 0) {
                    if (per.periodoReajuste) {
                        vlin = per.b0Reajuste
                    } else {
                        vlin = per.total
                    }
                } else {

//                    vlin = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodoReajuste).valor
                    vlin = 0
                }
                // println "-->  indice: "+p.indice+" periodo  "+per.periodo.id+" VLIN  "+vlin+"  VLINOFERTA "+vlinOferta+" Valor "+p.valor+"  "
                def valor = (vlin / vlinOferta * p.valor).round(3)

                def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${periodo.planilla.id} and periodoIndice =${per.periodo.id} and formulaPolinomica=${p.id}")
                if (vlrj.size() > 0) {
                    vlrj = vlrj.pop()
                    if (vlrj.valorReajuste != valor) {
                        vlrj.valorReajuste = valor
                        if (!vlrj.save(flush: true)) {
                            println "error vlrj update " + vlrj.errors
                        }
                    }
                } else {
                    vlrj = new ValorReajuste([obra: obra, planilla: periodo.planilla, periodoIndice: per.periodo, formulaPolinomica: p, valorReajuste: valor])
                    if (!vlrj.save(flush: true)) {
                        println "error vlrj insert " + vlrj.errors
                    }
                }
//                println "per "+per.fr
                //println "suma "+valor+" "
                per.frReajuste += valor
                if (!per.save(flush: true)) {
                    println "error fr " + per.errors
                }


            }
        }

        println "total b0 " + totalB0
        println "total fr " + periodo.frReajuste
    }

    /* arma la tabla Bo para la planilla reajustada plnl con FP=fprj
     * 1. carga los índices de la oferta y añade columnas para cada reajuste de la planilla
     * tp: 'c' para cuadrilla tipo y 'p' para FP */
    def armaTablaFr(plnl, fprj, tp) {
//        println "arma tablaFr plnl: $plnl, fprj: $fprj, tp: $tp"
        def cn = dbConnectionService.getConnection()
        def planilla = Planilla.get(plnl)
        def tblaBo = []
        def sql = ""
        def orden = 1
        /* Oferta y fecha de oferta */
        def titulos = tituloSuperior(plnl, fprj)

        sql = "select rjpl__id, prindscr, rjplprdo from rjpl, plnl, prin " +
                "where rjpl.plnl__id = ${plnl} and rjpl.fprj__id = ${fprj} and plnl.plnl__id = rjpl.plnl__id and " +
                "prin.prin__id = rjpl.prin__id order by rjpl.rjplprdo"
//        println "sql armaTablaFr: $sql"
        cn.eachRow(sql.toString()) {rj ->
//            println "arma tablaFr --2 rj: ${rj.rjplprdo}, ${rj.rjpl__id} --> indc,vlor: $orden"
            if(rj.rjplprdo == 0) {
                tblaBo = armaIndices(rj.rjpl__id, tp)
                tblaBo = aumentaColumna(tblaBo, rj.rjpl__id, orden, tp)
                orden++
            }  else {
                tblaBo = aumentaColumna(tblaBo, rj.rjpl__id, orden, tp)
                orden++
            }
//            println "tablaBo: $tblaBo"
//            println "titulos: $titulos"
        }
        cn.close()
//        def cb = cabeceraBo(tblaBo, plnl)
        tblaBo.add(titulos[0])
        tblaBo.add(titulos[1])
        tblaBo  //retorna tabla armada
    }

    /* Oferta y fecha de oferta, tipo planilla y fecha de presentación */
    def tituloSuperior(plnl, fprj){
        def cn = dbConnectionService.getConnection()
        def planilla = Planilla.get(plnl)
        def titlSuperior = ["OFERTA"]
        def titlIndices = ["", ""]
        def pcan
        def sql = "select inof.prindscr prin__of, inpl.prindscr prin__pl, cntrpcan, plnlfcpr from prin inof, prin inpl, rjpl, cntr, plnl " +
                "where rjpl.plnl__id = ${plnl} and rjpl.fprj__id = ${fprj} and plnl.plnl__id = rjpl.plnl__id and " +
                "cntr.cntr__id = plnl.cntr__id and inof.prin__id = cntr.prin__id and inpl.prin__id = rjpl.prin__id limit 1"
        if(planilla.tipoContrato == 'C') {
            def cmpl = Contrato.findByPadre(planilla.contrato)
            sql = "select prindscr prin__of, cntrpcan from prin, cntr " +
                    "where cntr__id = ${cmpl.id} and prin.prin__id = cntr.prin__id"
            cn.eachRow(sql.toString()) {d ->
                titlSuperior.add(d.prin__of)
                pcan = d.cntrpcan
            }
        } else {
            cn.eachRow(sql.toString()) {d ->
                titlSuperior.add(d.prin__of)
                pcan = d.cntrpcan
            }
        }
//        println "sql tituloSuperior: $sql"

        sql = "select rjpl__id, prindscr, rjplprdo, rjpl_mes, plnlfcpr, plnlfcpg from rjpl, plnl, prin " +
                "where rjpl.plnl__id = ${plnl} and rjpl.fprj__id = ${fprj} and plnl.plnl__id = rjpl.plnlrjst and " +
                "prin.prin__id = rjpl.prin__id order by rjpl.rjplprdo"
//        println "sql titulos y fechas: $sql"
        cn.eachRow(sql.toString()) {rj ->
//            println "arma tablaFr --2 rj: ${rj.rjplprdo}, ${rj.rjpl__id} --> indc,vlor: $orden"
            if(rj.rjplprdo == 0) {
                titlSuperior.add("ANTICIPO ${pcan}%")
                titlSuperior.add("${rj.plnlfcpg? preciosService.componeMes(rj.plnlfcpg.format('MMM-yyyy')) : preciosService.componeMes(rj.plnlfcpr.format('MMM-yyyy'))}")
                titlIndices.add("${rj.plnlfcpg? "Pago: ${rj.plnlfcpg}" : 'Sin Pago'}")
                titlIndices.add("Indices: ${rj.prindscr}")
            }  else {
                titlSuperior.add("AVANCE")
                titlSuperior.add("${rj.rjpl_mes}")
                titlIndices.add("${rj.plnlfcpg? "Pago: ${rj.plnlfcpg}" : 'Sin Pago'}")
                titlIndices.add("Indices: ${rj.prindscr}")
            }
        }
        cn.close()
//        println "titlSuperior: $titlSuperior"
//        println "titlIndices:  $titlIndices"
        [titlSuperior, titlIndices]
    }


    /* arma indices coeficientes y valores de oferta, retorna lista de Bo */
    def armaIndices(rjpl, tp) {
        def cn = dbConnectionService.getConnection()
        def tbla = []
        def mapa = [:]
        def sql = "select indcdscr, frplvlor, frplnmro, dtrjinof, dtrjvlof from dtrj, frpl, indc " +
                "where indc.indc__id = frpl.indc__id and dtrj.frpl__id = frpl.frpl__id and " +
                "rjpl__id = ${rjpl} and frplnmro ilike '${tp}%' order by frplnmro"
//        println "sql armaIndices: $sql"
        cn.eachRow(sql.toString()){d ->
            mapa = [:]
            mapa.numero      = d.frplnmro
            mapa.descripcion = d.indcdscr
            mapa.coeficiente = d.frplvlor
            mapa.indice      = d.dtrjinof
            mapa.valor       = d.dtrjvlof
            tbla.add(mapa)
        }
        cn.close()
        tbla  //retorna mapa
    }

    def aumentaColumna(tbla, rjpl, orden, tp) {
        def cn = dbConnectionService.getConnection()
        def mapa = [:]
        def sql = "select frplnmro, dtrjinpr, dtrjvlpr from dtrj, frpl " +
                "where dtrj.frpl__id = frpl.frpl__id and rjpl__id = ${rjpl} and frplnmro ilike '${tp}%' " +
                "order by frplnmro"
//        println "sql armaIndices: $sql"
        cn.eachRow(sql.toString()){d ->
            tbla.find {it.numero == d.frplnmro}["indc${orden}"] = d.dtrjinpr
            tbla.find {it.numero == d.frplnmro}["vlor${orden}"] = d.dtrjvlpr
        }
        cn.close()
        tbla  //retorna mapa
    }

    /* arma la tabla Po para la planilla reajustada plnl con FP=fprj
     * Po usa un título estático, y valores de rjpl
    * */
    def armaTablaPo(plnl, fprj) {
//        println "arma tablaPo para plnl: $plnl y fprj: $fprj"
        def cn = dbConnectionService.getConnection()
        def tblaPo = []
        def orden = 1
        def sql = "select tppldscr, rjpl_mes, rjplcrpa, rjplcrac, rjplplpa, rjplplac, rjplvlpo, rjplprdo " +
                "from rjpl, plnl, tppl, plnl rj " +
                "where rjpl.plnl__id = ${plnl} and rjpl.fprj__id = ${fprj} and plnl.plnl__id = rjpl.plnl__id and " +
                "rj.plnl__id = rjpl.plnlrjst and tppl.tppl__id = rj.tppl__id order by rjpl.rjplprdo"
//        println "sql armaTablaPo: $sql"
        cn.eachRow(sql.toString()) {rj ->
            tblaPo.add([tipo: rj.tppldscr, mes: rj.rjpl_mes, crpa: rj.rjplcrpa, crac: rj.rjplcrac, plpa: rj.rjplplpa,
              plac: rj.rjplplac, po: rj.rjplvlpo])
        }
        cn.close()
        tblaPo  //retorna tabla armada
    }

    def reajusteAnterior(plnl) {
        println "arma reajsute anterior de plnl: ${plnl.id}"
        def cn = dbConnectionService.getConnection()
        def sql = "select sum(rjplvlor) suma from rjpl where plnl__id = (select rjpl.plnlrjst from rjpl, plnl " +
                "where rjpl.plnl__id = ${plnl.id} and rjpl.plnlrjst < rjpl.plnl__id and " +
                "rjpl.fprj__id = ${plnl.formulaPolinomicaReajuste.id} and plnl.plnl__id = rjpl.plnlrjst and " +
                "plnltipo = '${plnl.tipoContrato}' " +
                "order by plnlrjst desc limit 1) and rjpl.fprj__id = ${plnl.formulaPolinomicaReajuste.id}"
//        println "sql reajusteAnterior: $sql"
        def valor = cn.rows(sql.toString())[0].suma
        if(!valor) return 0
        return valor
        cn.close()
    }

    def reajusteAcumulado(plnl) {
//        println "arma reajsute total cn esta planilla: ${plnl.id}"
        def cn = dbConnectionService.getConnection()
        def sql = "select sum(rjplvlor) suma from rjpl where plnl__id = ${plnl.id} and  " +
                "fprj__id = ${plnl.formulaPolinomicaReajuste.id}"
//        println "sql reajusteAcumulado: $sql"
        def valor = cn.rows(sql.toString())[0].suma
        if(!valor) return 0
        return valor
        cn.close()
    }

    def reajusteAnteriorLq(plnl) {
//        println "arma reajsute anterior liq. reajuste plnl: $plnl"
        def cn = dbConnectionService.getConnection()
        def sql = "select sum(rjplvlor) suma from rjpl where plnl__id = (select max(plnlrjst) from rjpl " +
                "where plnl__id = ${plnl})"
//        println "sql reajusteAnterior: $sql"
        def valor = cn.rows(sql.toString())[0].suma
        if(!valor) return 0
        return valor
        cn.close()
    }

    /* arma la tabla de resumen al anticipo: fprj, Po y reajuste
     * */
    def armaResumenAntc(plnl) {
//        println "arma tabla resumen anticipo para plnl: $plnl"
        def cn = dbConnectionService.getConnection()
        def tblaRs = []
        def sql = "select frpldscr, rjplvlpo, rjplvlor from rjpl, fprj " +
                "where rjpl.plnl__id = ${plnl} and rjplprdo = 0 and fprj.fprj__id = rjpl.fprj__id order by rjpl.fprj__id"
//        println "sql armaTablaRseumen: $sql"
        cn.eachRow(sql.toString()) {d ->
            tblaRs.add([fp: d.frpldscr, po: d.rjplvlpo, vlor: d.rjplvlor])
        }
        cn.close()
        tblaRs  //retorna tabla armada
    }

    def nombrePersona(persona, tipo) {  /** personas y proveedores */
//        println "nombrePersona" + persona
//        println tipo
        def str = ""
        if (persona) {
            switch (tipo) {
                case "pers":
                    str = ((persona.titulo ? persona.titulo + " " : "") + persona.nombre + " " + persona.apellido).toUpperCase()
                    break;
                case "prov":
                    str = ((persona.titulo ? persona.titulo + " " : "").toUpperCase() + persona.nombreContacto +
                            " " + persona.apellidoContacto).toUpperCase()
                    break;
            }
        }
//        println str
//        println "****************************************************"
        return str
    }




}