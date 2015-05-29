package janus

import janus.ejecucion.PeriodoPlanilla
import janus.ejecucion.ValorIndice
import janus.ejecucion.ValorReajuste

class PlanillasService {

    def calculaValores(PeriodoPlanilla periodo,cs,ps,pcs,obra,anticipo){
        println "calculando indices de "+periodo.planilla.id+"  "+periodo.fechaIncio.format("dd-MM-yyyyy")+"  "+periodo.fechaFin.format("dd-MM-yyyyy")
        def totalB0 = 0
        periodo.frReajuste=0
        periodo.b0Reajuste=0
        def valorIndice
        cs.each{c->
            valorIndice = ValorIndice.findByPeriodoAndIndice(periodo.periodoReajuste, c.indice)?.valor
            if (!valorIndice) {
//                println "wtf no valor " + p.periodo + "  " + c.indice
                valorIndice = 0
            }
            def valor
            def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${periodo.planilla.id} and periodoIndice =${periodo.periodo.id} and formulaPolinomica=${c.id}")
            if (vlrj.size() > 0) {
                valor = vlrj.pop()
                valor.periodoReajuste=periodo.periodoReajuste
                valor.valorReajuste=valorIndice*c.valor
                totalB0+=(valorIndice*c.valor).toDouble().round(3)
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
        def periodos2 =[]
        periodos2.add(periodo)

        println("ps " + ps)

        ps.eachWithIndex { p, i ->
            def vlinOferta = null
            periodos.eachWithIndex { per, j ->
                if (j == 0) { // es la oferta
                    def valor = 0
                    if (i == 0) { //es mano de obra
                        if(per.periodoReajuste){
                            vlinOferta = per.b0Reajuste
                            valor = per.b0Reajuste
                        }else{
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
                    if(per.periodoReajuste) {
                        vlin = per.b0Reajuste
                    }else{
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



        println "total b0 "+totalB0
        println "total fr "+periodo.frReajuste

    }


}
