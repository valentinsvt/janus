package janus.ejecucion

import janus.pac.CronogramaEjecucion
import janus.pac.PeriodoEjecucion

class Planilla2Controller {

    def liquidacion() {
//        println "params " + params
        def planilla = Planilla.get(params.id)
        def override = false
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def planillasAnterioresTot = Planilla.withCriteria {
            eq("contrato", contrato)
            and {
                or {
                    lt("fechaInicio", planilla.fechaInicio)
                    isNull("fechaInicio")
                }
                or {
                    eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
                    eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
                }
            }
        }
        def planillasAnteriores = Planilla.withCriteria {
            eq("contrato", contrato)
            or {
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
            }
            order("id", "asc")
        }
        def planillaAnterior = planillasAnteriores[planillasAnteriores.size() - 1]
        def periodosEjec = PeriodoEjecucion.findAllByObra(contrato.oferta.concurso.obra, [sort: "fechaFin"])
        def finalObraCrono = periodosEjec.pop().fechaFin
        def tarde = false
        def periodos = PeriodoPlanilla.findAllByPlanillaInList(planillasAnteriores, [sort: "id"])
        def fechaFinObra = obra.fechaFin

        def pcs = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                or {
                    ilike("numero", "c%")
                    and {
                        ne("numero", "P0")
                        ne("numero", "p01")
                        ilike("numero", "p%")
                    }
                }
                order("numero", "asc")
            }
        }


        def cs = FormulaPolinomicaContractual.findAllByContratoAndNumeroIlike(contrato, "C%", [sort: "numero"])
//        def ps=  FormulaPolinomicaContractual.findAllByContratoAndNumeroIlike(contrato,"P%",[sort:"numero"])
        def ps = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                and {
                    ne("numero", "P0")
                    ilike("numero", "p%")
                }
                order("numero", "asc")
            }
        }
        if (!fechaFinObra) {
            redirect(controller: "contrato", action: "fechasPedidoRecepcion", id: contrato.id)
            flash.message = "Ingrese las fechas de recepción antes de generar la planilla de liquidación de reajuste"
            flash.clase = "alert-error"
            return
        }

        if (fechaFinObra > finalObraCrono) {
            tarde = true
        }

        def existe
        def error = false
        planillasAnteriores.each { pl ->
            def pers = PeriodoPlanilla.findAllByPlanilla(pl)
            pers.each { p ->
                def periodo
                if (tarde == true) {
                    periodo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(p.fechaIncio, p.fechaFin)

                } else {
                    periodo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(pl.fechaPago, pl.fechaPago)
                }
                p.periodoLiquidacion = periodo
                p.save(flush: true)
                existe = verificaIndices(pcs, periodo, 0)
                if (periodo != existe) {
                    error = true
                }

            }
            if (error) {
                redirect(action: "error")
                return
            }
        }

        def tableWidth = 150 * periodos.size() + 400
        ///////////////////////////////////////////////////**********planillas*****************////////////////////////////////////////////////////////////

        def planillaAnticipo = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
        }
        planillaAnticipo = planillaAnticipo.pop()
        def fechaOferta = planillaAnticipo.contrato.oferta.fechaEntrega - 30
        def fechaAnticipo = planillaAnticipo.fechaPresentacion
        def periodosAnticipo = PeriodoPlanilla.findAllByPlanilla(planillaAnticipo, [sort: "id"])
        def planillasAvances = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
        }
        def periodosAvances = PeriodoPlanilla.findAllByPlanillaInList(planillasAvances, [sort: "id"])
        def perOferta = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(fechaOferta, fechaOferta)
        def perAnticipo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(fechaAnticipo, fechaAnticipo)
//        println "planilla "+planilla+"  "+fechaOferta+"  "+fechaAnticipo+" pers "+periodos+"  "+perOferta+"  "+perAnticipo


        def tablaBo = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:&&px;'>"
        tablaBo += "<thead>"
        tablaBo += "<tr>"
        tablaBo += "<th colspan=\"2\">Cuadrilla Tipo</th>"
        periodosAnticipo.each {
            it.planilla.reajusteLiq = 0
            it.totalLiq = 0
            it.frLiq = 0
            tablaBo += "<th>${it.titulo}</th>"
            tablaBo += "<th class='nb'>" + fechaConFormato(it.fechaIncio) + "</th>"
        }
        periodosAvances.each {
            it.planilla.reajusteLiq = 0
            it.totalLiq = 0
            it.frLiq = 0
            tablaBo += "<th>${it.titulo}</th>"
            tablaBo += "<th class='nb'>" + fechaConFormato(it.fechaIncio) + "</th>"
        }

        tablaBo += "</tr>"
        tablaBo += "</thead>"
        tablaBo += "<tbody>"
        def totalCoef = 0
        cs.each { c ->
            tablaBo += "<tr>"
            tablaBo += "<th class='tal'>" + c.indice.descripcion + " (${c.numero})</th>"
            tablaBo += "<th class='number'>" + numero(c.valor) + "</th>"
            totalCoef += c.valor
            periodosAnticipo.each { p ->
                def valor = ValorIndice.findByPeriodoAndIndice(p.periodoLiquidacion, c.indice).valor
                if (!valor) {
                    println "wtf no valor " + p.periodoLiquidacion + "  " + c.indice
                    valor = 0
                }

                p.totalLiq += (valor * c.valor).round(3)
                p.save(flush: true)

                tablaBo += "<td class='number'>" + numero(valor, 2) + "</td>"
                valor = (valor * c.valor).round(3)
                def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${p.periodoLiquidacion.id} and formulaPolinomica=${c.id} and planillaLiq = ${planillaAnticipo.id} ")
                if (vlrj.size() > 0) {
                    vlrj = vlrj.pop()
                    if (vlrj != valor) {
                        vlrj.valor = valor
                        if (!vlrj.save(flush: true)) {
                            println "error vlrj update " + vlrj.errors
                        }
                    }
                } else {
                    vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: p.periodoLiquidacion, formulaPolinomica: c, valor: valor, planillaLiq: planillaAnticipo])
                    if (!vlrj.save(flush: true)) {
                        println "error vlrj insert " + vlrj.errors
                    }
                }
                tablaBo += "<td class='number'>" + numero(valor) + "</td>"
            }

            periodosAvances.each { p ->
                def valor = ValorIndice.findByPeriodoAndIndice(p.periodoLiquidacion, c.indice).valor
                if (!valor) {
                    println "wtf no valor liq " + p.periodoLiquidacion + "  " + c.indice
                    valor = 0
                }

                p.totalLiq += (valor * c.valor).round(3)
                p.save(flush: true)

                tablaBo += "<td class='number'>" + numero(valor, 2) + "</td>"
                valor = (valor * c.valor).round(3)
                def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${p.periodoLiquidacion.id} and formulaPolinomica=${c.id} and planillaLiq=${p.planilla.id}")
                if (vlrj.size() > 0) {
                    vlrj = vlrj.pop()
                    if (vlrj != valor) {
                        vlrj.valor = valor
                        if (!vlrj.save(flush: true)) {
                            println "error vlrj update " + vlrj.errors
                        }
                    }
                } else {
                    vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: p.periodoLiquidacion, formulaPolinomica: c, valor: valor, planillaLiq: p.planilla])
                    if (!vlrj.save(flush: true)) {
                        println "error vlrj insert " + vlrj.errors
                    }
                }
                tablaBo += "<td class='number'>" + numero(valor) + "</td>"
            }

            tablaBo += "</tr>"
        }

        tablaBo += "</tbody><tfoot>"
        tablaBo += "<tr>" + "<th>TOTALES</th><th class='number'>${numero(totalCoef)}</th>"



        periodosAnticipo.each { p ->
            tablaBo += "<td></td><th class='number'>${numero(p.totalLiq)}</th>"
        }
        periodosAvances.each { p ->
            tablaBo += "<td></td><th class='number'>${numero(p.totalLiq)}</th>"
        }

        tablaBo += "</tr></tfoot></table>"

        //////////////////////////**********************P0*********************///////////////////////////
        def tablaP0 = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:&&px; margin-top:10px;' >"
        tablaP0 += '<thead>'
        tablaP0 += '<tr>'
        tablaP0 += '<th colspan="2" rowspan="2">Mes y año</th>'
        tablaP0 += '<th colspan="2">Cronograma</th>'
        tablaP0 += '<th colspan="2">Planillado</th>'
        tablaP0 += '<th colspan="2" rowspan="2">Valor P<sub>0</sub></th>'
        tablaP0 += '</tr>'
        tablaP0 += '<tr>'
        tablaP0 += '<th>Parcial</th>'
        tablaP0 += '<th>Acumulado</th>'
        tablaP0 += '<th>Parcial</th>'
        tablaP0 += '<th>Acumulado</th>'
        tablaP0 += '</tr>'
        tablaP0 += '</thead>'
        tablaP0 += '<tbody>'
        def act = 0
        def act2 = 0
        def diasTot = 0, totCrono = 0, totPlan = 0, totalMultaRetraso = 0
        (periodosAnticipo + periodosAvances).each { p ->
            if (p.titulo != "OFERTA") {
                tablaP0 += '<tr>'
                if (p.titulo == "ANTICIPO") {
                    tablaP0 += "<th>${p.titulo}</th>"
                    tablaP0 += "<th>${fechaConFormato(p.fechaIncio, 'MMM-yy')}</th>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td class='number'>${numero(p.p0, 2)}</td>"
                } else {
                    def dias = p.fechaFin - p.fechaIncio + 1
                    diasTot += dias
                    tablaP0 += "<th>${fechaConFormato(p.fechaIncio, 'MMM-yy')}</th>"
                    tablaP0 += "<th>(${dias})</th>"
                    tablaP0 += "<td class='number'>${numero(p.parcialCronograma, 2)}</td>"
                    act += p.parcialCronograma
                    totCrono += p.parcialCronograma
                    tablaP0 += "<td class='number'>${numero(act, 2)}</td>"
                    tablaP0 += "<td class='number'>${numero(p.parcialPlanilla, 2)}</td>"
                    act2 += p.parcialPlanilla
                    totPlan += p.parcialPlanilla
                    tablaP0 += "<td class='number'>${numero(act2, 2)}</td>"
                    tablaP0 += "<td class='number'>${numero(p.p0, 2)}</td>"
//                    def retraso=0, multa=0
//                    if (p.parcialCronograma > p.parcialPlanilla) {
//                        def totalContrato = contrato.monto
//                        def prmlMulta = contrato.multaPlanilla
//                        def valorDia = p.parcialCronograma / p.dias
//                        retraso = ((p.parcialCronograma - p.parcialPlanilla) / valorDia).round(2)
//                        multa = ((totalContrato) * (prmlMulta / 1000) * retraso).round(2)
//                    }
////                    totalMultaRetraso+=multa
//                    bodyMultaRetraso += "<tr>"
//                    bodyMultaRetraso += "<th class='tal'>${fechaConFormato(p.fechaIncio,'MMM-yy')}</th>"
//                    bodyMultaRetraso += "<td class='number'>${p.parcialCronograma}</td>"
//                    bodyMultaRetraso += "<td class='number'>${p.parcialPlanilla}</td>"
//                    bodyMultaRetraso += "<td class='number'>${retraso}</td>"
//                    bodyMultaRetraso += "<td class='number'>${multa}</td>"
//                    bodyMultaRetraso += "</tr>"
                }
            }
        }
        tablaP0 += '</tbody>'
        tablaP0 += '<tfoot>'
        //totales aqui
        tablaP0 += "<th>TOTAL</th>"
        tablaP0 += "<th>(${diasTot})</th>"
        tablaP0 += "<td class='number'></td>"
        tablaP0 += "<th class='number'>${numero(totCrono, 2)}</th>"
        tablaP0 += "<td class='number'></td>"
        tablaP0 += "<th class='number'>${numero(totPlan, 2)}</th>"
        tablaP0 += "<td class='number'></td>"
        tablaP0 += '</tfoot>'
        tablaP0 += '</table>'

        //////////////////////////////////////////////*****************FR*********************************/////////////////

        def tr1 = "<tr>"
        def tr2 = "<tr>"
        def tr3 = "<tr>"
        def tablaFr = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:&&px; margin-top:10px;'>"
        tablaFr += '<thead>'
        tr1 = '<tr>'
        tr1 += '<th rowspan="2">Componentes</th>'
        tr3 += "<th>Anticipo</th>"

        def tdRowSpan = '<th colspan="' + (periodosAvances.size() + periodosAnticipo.size() - 1) + '">Periodo de variación y aplicación de fórmula polinómica</th>'
        (periodosAnticipo + periodosAvances).eachWithIndex { p, i ->
            if (i == 0) { //oferta
                tr1 += "<th>" + p.titulo + "</th>"
                tr2 += "<th>" + fechaConFormato(p.fechaIncio, "MMM-yy") + "</th>"
                tr3 += "<th class='number'>" + planilla.contrato.porcentajeAnticipo + "%</th>"
            } //oferta
            else if (i == 1) { //anticipo
                tr1 += tdRowSpan
                tr2 += "<th>" + p.titulo + "<br/>" + fechaConFormato(p.fechaIncio, "MMM-yy") + "</th>"
                tr3 += "<th>" + p.titulo + "</th>"
            } //anticipo
            else { //otros
                tr2 += "<th rowspan='2'>" + fechaConFormato(p.fechaIncio, "MMM-yy") + "</th>"
            }//otros
        }  // periodos.each para el header
        tr1 += "</tr>"
        tr2 += "</tr>"
        tr3 += "</tr>"
        tablaFr += tr1 + tr2 + tr3
        tablaFr += "</thead>"
        tablaFr += "<tbody>"

        def totalFr = 0

        ps.eachWithIndex { p, i ->
            tablaFr += "<tr>"
            tablaFr += "<th class='tal'>" + p?.indice?.descripcion + " (${p?.numero})</th>"
            def vlinOferta
            (periodosAnticipo + periodosAvances).eachWithIndex { per, j ->
                if (j == 0) { // es la oferta
                    def valor = 0
                    if (i == 0) { //es mano de obra
                        vlinOferta = per.totalLiq
                        tablaFr += "<td class='number'><div>${p.valor}</div><div class='bold'>${per.totalLiq}</div></td>"
                        valor = per.totalLiq
                    } else {
                        vlinOferta = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodoLiquidacion).valor
                        tablaFr += "<td class='number'><div>${p.valor}</div><div class='bold'>${vlinOferta}</div></td>"
                        valor = vlinOferta
                    }
                    def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${per.periodo.id} and formulaPolinomica=${p.id} and planillaLiq=${per.planilla.id}")
                    if (vlrj.size() > 0) {
                        vlrj = vlrj.pop()
                        if (vlrj != valor) {
                            vlrj.valor = valor
                            if (!vlrj.save(flush: true)) {
                                println "error vlrj update " + vlrj.errors
                            }
                        }
                    } else {
                        vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: per.periodoLiquidacion, formulaPolinomica: p, valor: valor, planillaLiq: per.planilla])
                        if (!vlrj.save(flush: true)) {
                            println "error vlrj insert " + vlrj.errors
                        }
                    }

                } else {
                    def vlin
                    def dec
                    if (i == 0) {
                        vlin = per.totalLiq
                        dec=3
                    } else {
                        vlin = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodoLiquidacion).valor
                        dec=2
                    }
//                    println "error "+p.indice+" "+p.indice.id+"  "+per.periodo.id+"   "+vlin+"  "+vlinOferta+"  "+p.valor+"  "+"  "+per.periodo.id
                    def valor = (vlin / vlinOferta * p.valor).round(3)

                    def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${per.periodo.id} and formulaPolinomica=${p.id} and planillaLiq=${per.planilla.id}")
                    if (vlrj.size() > 0) {
                        vlrj = vlrj.pop()
                        if (vlrj != valor) {
                            vlrj.valor = valor
                            if (!vlrj.save(flush: true)) {
                                println "error vlrj update " + vlrj.errors
                            }
                        }
                    } else {
                        vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: per.periodoLiquidacion, formulaPolinomica: p, valor: valor, planillaLiq: per.planilla])
                        if (!vlrj.save(flush: true)) {
                            println "error vlrj insert " + vlrj.errors
                        }
                    }

                    per.frLiq += valor
                    if (!per.save(flush: true)) {
                        println "error fr " + per.errors
                    }
                    tablaFr += "<td class='number'><div>${numero(vlin,dec)}</div><div class='bold'>${numero(valor)}</div></td>"
                }

            }


            tablaFr += "</tr>"
        }

        (periodosAnticipo + periodosAvances).each { p ->
            def fr1 = (p.frLiq - 1).round(3)
            p.planilla.reajusteLiq += (p.p0 * fr1).round(2)
            p.planilla.save(flush: true)
        }

        planillasAnteriores.eachWithIndex { p, i ->
            if (i > 0) {
                p.reajusteLiq += planillasAnteriores[i - 1].reajusteLiq
                p.save(flush: true)
            }

        }


        tr1 = "<tr>"
        tr2 = "<tr>"
        tr3 = "<tr>"
        def tr4 = "<tr>"
        def tr5 = "<tr>"
        def tr6 = "<tr>"
        def tr7 = "<tr>"

        tr1 += "<th rowspan='4'>1.000</th>"
        tr1 += "<th>F<sub>r</sub></th>"
        tr2 += "<th>F<sub>r</sub>-1</th>"
        tr3 += "<th>P<sub>0</sub></th>"
        tr4 += "<th>P<sub>r</sub>-P</th>"

        def reajusteTotal = 0

        (periodosAnticipo + periodosAvances).eachWithIndex { per, i ->
            if (i > 0) {
                def fr1 = (per.frLiq - 1).round(3)
                tr1 += "<th class='number'>${per.frLiq}</th>"
                tr2 += "<th class='number'>${fr1}</th>"
                tr3 += "<th class='number'>${per.p0}</th>"
                def t = (per.p0 * fr1).round(2)
                tr4 += "<th class='number'>${t}</th>"
                reajusteTotal += t
            }
        }

        reajusteTotal = reajusteTotal.toDouble().round(2)

        def reajusteAnterior = (planillaAnterior.reajusteLiq).toDouble().round(2)

        tr5 += "<th colspan='2'>REAJUSTE TOTAL</th><th colspan='${periodosAnticipo.size() + periodosAvances.size() - 1}' class='number'>" + reajusteTotal + "</th>"
        tr6 += "<th colspan='2'>REAJUSTE ANTERIOR</th><th colspan='${periodosAnticipo.size() + periodosAvances.size() - 1}' class='number'>" + reajusteAnterior + "</th>"
        tr7 += "<th colspan='2'>REAJUSTE A PLANILLAR</th><th colspan='${periodosAnticipo.size() + periodosAvances.size() - 1}' class='number'>" + ((reajusteTotal - reajusteAnterior).toDouble().round(2)) + "</th>"

        tr1 += "</tr>"
        tr2 += "</tr>"
        tr3 += "</tr>"
        tr4 += "</tr>"
        tr5 += "</tr>"
        tr6 += "</tr>"
        tr7 += "</tr>"

        tablaFr += "<tfoot>"
        tablaFr += tr1 + tr2 + tr3 + tr4 + tr5 + tr6 + tr7
        tablaFr += "</tfoot></table>"

        planilla.reajuste = reajusteTotal
        if (!planilla.save(flush: true)) {
            println "error planilla reajuste " + planilla.id
        }

//        tr5+="<th colspan='2'>REAJUSTE TOTAL</th><th class='number'>"+reajusteTotal+"</th>"
//
//        tablaFr+="<tfoot>"
//        tablaFr+=tr1+tr2+tr3+tr4+tr5
//        tablaFr+="</tfoot></table>"

        //////////////////////////////////////////fin anticipo//////////////////////////////////////////////////////////////////////////////////////////////////
        tablaBo.replaceAll("&&", "" + tableWidth)
        tablaP0.replaceAll("&&", "" + tableWidth)
        tablaFr.replaceAll("&&", "" + tableWidth)

        [tablaBo: tablaBo, planilla: planilla, tablaP0: tablaP0, tablaFr: tablaFr]
    }

    def avance() {
//        println "params " + params
        def planilla = Planilla.get(params.id)
        def override = false
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def planillasAnteriores = Planilla.withCriteria {
            eq("contrato", contrato)
            and {
                or {
                    lt("fechaInicio", planilla.fechaInicio)
                    isNull("fechaInicio")
                }
                or {
                    eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
                    eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
                }
            }
            order("id", "asc")
        }
        def planillaAnterior = planillasAnteriores.get(planillasAnteriores.size() - 1)
//        println "planillas " + planillasAnteriores
        def periodos = PeriodoPlanilla.findAllByPlanillaInList(planillasAnteriores, [sort: "id"])

//        println periodos.id
//        println periodos.periodo.descripcion

        def periodos2 = []
//        println "periodos "+periodos
        def cs = FormulaPolinomicaContractual.findAllByContratoAndNumeroIlike(contrato, "C%", [sort: "numero"])
        def ps = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                and {
                    ne("numero", "P0")
                    ilike("numero", "p%")
                }
                order("numero", "asc")
            }
        }
        def pcs = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                or {
                    ilike("numero", "c%")
                    and {
                        ne("numero", "P0")
                        ne("numero", "p01")
                        ilike("numero", "p%")
                    }
                }
                order("numero", "asc")
            }
        }

        def bodyMultaRetraso = ""

        def pa = PeriodoPlanilla.findAllByPlanilla(planilla)
//        println "peridos planilla " + pa
        if (pa.size() == 0) {
            def inicio = planilla.fechaInicio
            def fin = getLastDayOfMonth(planilla.fechaInicio)
//            println "inicio " + inicio + " fin " + fin + "  planilla " + planilla.fechaFin
            if (fin > planilla.fechaFin) {
                fin = planilla.fechaFin
                while (fin <= planilla.fechaFin) {
                    def perInec = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(inicio, fin)
//                    println "----------!!!perinec " + perInec + " inicio " + inicio + "  " + fin
                    def per = verificaIndices(pcs, perInec, 0)
                    def periodo = new PeriodoPlanilla([planilla: planilla, periodo: per, fechaIncio: inicio, fechaFin: fin, titulo: "AVANCE"])
                    if (!periodo.save(flush: true)) {
                        println "error al crear periodo avance " + periodo.errors
                    } else {
                        periodos2.add(periodo)
                    }
                    inicio = fin + 1
                    fin = getLastDayOfMonth(inicio)
                }
            } else {
                while (fin <= planilla.fechaFin) {
                    def perInec = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(inicio, fin)
//                    println "----------!!!perinec " + perInec + " inicio " + inicio + "  " + fin
                    def per = verificaIndices(pcs, perInec, 0)
                    def periodo = new PeriodoPlanilla([planilla: planilla, periodo: per, fechaIncio: inicio, fechaFin: fin, titulo: "AVANCE"])
                    if (!periodo.save(flush: true)) {
                        println "error al crear periodo avance " + periodo.errors
                    } else {
                        periodos2.add(periodo)
                    }
                    inicio = fin + 1
                    fin = getLastDayOfMonth(inicio)
                }
                if (fin > planilla.fechaFin && (fin.format("MM") == planilla.fechaFin.format("MM"))) {
                    def perInec = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(inicio, planilla.fechaFin)
                    def per = verificaIndices(pcs, perInec, 0)
                    def periodo = new PeriodoPlanilla([planilla: planilla, periodo: per, fechaIncio: inicio, fechaFin: planilla.fechaFin, titulo: "AVANCE"])
                    if (!periodo.save(flush: true)) {
                        println "error al crear periodo avance " + periodo.errors
                    } else {
                        periodos2.add(periodo)
                    }
                }
            }
        } else {
            periodos += pa
        }

        def tableWidth = 150 * (periodos.size() + periodos2.size()) + 400
        def smallTableWidth = 400

//        println "periodos  " + periodos
//        println "periodos  2v " + periodos2
        /////////////////*************************b0****************************/////////////////////////

        def tablaBo = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px;'>"
        tablaBo += "<thead>"
        tablaBo += "<tr>"
        tablaBo += "<th colspan=\"2\">Cuadrilla Tipo</th>"
        periodos.each {
            tablaBo += "<th>${it.titulo}</th>"
            tablaBo += "<th class='nb'>" + fechaConFormato(it.fechaIncio) + "</th>"
        }
        periodos2.each {
            tablaBo += "<th>${it.titulo}</th>"
            tablaBo += "<th class='nb'>" + fechaConFormato(it.fechaIncio) + "</th>"
        }

        tablaBo += "</tr>"
        tablaBo += "</thead>"
        tablaBo += "<tbody>"

        def totalCoef = 0
        cs.each { c ->
            tablaBo += "<tr>"
            tablaBo += "<th class='tal'>" + c.indice.descripcion + " (${c.numero})</th>"
            tablaBo += "<th class='number'>" + numero(c.valor) + "</th>"
            totalCoef += c.valor
            periodos.each { p ->
                def valor = ValorIndice.findByPeriodoAndIndice(p.periodo, c.indice).valor
                if (!valor) {
                    println "wtf no valor " + p.periodo + "  " + c.indice
                    valor = 0
                }
                tablaBo += "<td class='number'>" + numero(valor, 2) + "</td>"
//                valor = (valor*c.valor).round(3)
                def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${p.planilla.id} and periodoIndice =${p.periodo.id} and formulaPolinomica=${c.id}")
                if (vlrj.size() > 0) {
                    valor = vlrj.pop().valor
                } else {
                    println "error wtf no hay vlrj => from ValorReajuste where obra=${obra.id} and planilla=${p.planilla.id} and periodoIndice =${p.periodo.id} and formulaPolinomica=${c.id}"
                    valor = -1
                }
                tablaBo += "<td class='number'>" + numero(valor) + "</td>"
            }
            periodos2.each { p ->
                def valor = ValorIndice.findByPeriodoAndIndice(p.periodo, c.indice).valor
                if (!valor) {
                    println "wtf no valor " + p.periodo + "  " + c.indice
                    valor = 0
                }

                p.total += (valor * c.valor).round(3)
                p.save(flush: true)

                tablaBo += "<td class='number'>" + numero(valor) + "</td>"
                valor = (valor * c.valor).round(3)
                def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${p.periodo.id} and formulaPolinomica=${c.id}")
                if (vlrj.size() > 0) {
                    vlrj = vlrj.pop()
                    if (vlrj != valor) {
                        vlrj.valor = valor
                        if (!vlrj.save(flush: true)) {
                            println "error vlrj update " + vlrj.errors
                        }
                    }
                } else {
                    vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: p.periodo, formulaPolinomica: c, valor: valor])
                    if (!vlrj.save(flush: true)) {
                        println "error vlrj insert " + vlrj.errors
                    }
                }
                tablaBo += "<td class='number'>" + numero(valor) + "</td>"
            }

            tablaBo += "</tr>"
        }
        tablaBo += "</tbody><tfoot>"
        tablaBo += "<tr>" + "<th>TOTALES</th><th class='number'>${numero(totalCoef)}</th>"
        periodos.each { p ->
            tablaBo += "<td></td><th class='number'>${numero(p.total)}</th>"
        }
        periodos2.each { p ->
            tablaBo += "<td></td><th class='number'>${numero(p.total)}</th>"
        }
        tablaBo += "</tr></tfoot></table>"

        //////////////////////////**********************P0*********************///////////////////////////
        def tablaP0 = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;' >"
        tablaP0 += '<thead>'
        tablaP0 += '<tr>'
        tablaP0 += '<th colspan="2" rowspan="2">Mes y año</th>'
        tablaP0 += '<th colspan="2">Cronograma</th>'
        tablaP0 += '<th colspan="2">Planillado</th>'
        tablaP0 += '<th colspan="2" rowspan="2">Valor P<sub>0</sub></th>'
        tablaP0 += '</tr>'
        tablaP0 += '<tr>'
        tablaP0 += '<th>Parcial</th>'
        tablaP0 += '<th>Acumulado</th>'
        tablaP0 += '<th>Parcial</th>'
        tablaP0 += '<th>Acumulado</th>'
        tablaP0 += '</tr>'
        tablaP0 += '</thead>'
        tablaP0 += '<tbody>'
        def periodosEjecucion = PeriodoEjecucion.withCriteria {
            and {
                eq("obra", obra)
                order("fechaInicio", "asc")
            }
        }

        periodos2.each { p ->
//            println "per "+p.id
            def dias = p.fechaFin - p.fechaIncio + 1
            def pers = periodosEjecucion.findAll {
                (it.fechaInicio <= p.fechaIncio && p.fechaIncio <= it.fechaFin) || (it.fechaInicio <= p.fechaFin && p.fechaFin <= it.fechaFin)
            }
//            println "pers ! "+pers+ " "+dias +" = "+p.fechaFin+" - "+p.fechaIncio+" +1"
            def parcialCronograma = 0, diasTotal = 0
            pers.each { pr ->
                def pc = CronogramaEjecucion.findAllByPeriodo(pr).sum { it.precio }
                def diasPr = pr.fechaFin - pr.fechaInicio + 1
                def dias2
                if (p.fechaIncio >= pr.fechaInicio && p.fechaFin <= pr.fechaFin) {
                    dias2 = dias
//                    println "dias 2 1 "+dias2+" == dias"
                } else {
                    if (p.fechaIncio < pr.fechaInicio) {
                        dias2 = p.fechaFin - pr.fechaInicio + 1
//                        println "dias 2 2 "+dias2+" = "+p.fechaFin+" - "+pr.fechaInicio+" +1"
                    } else {
                        dias2 = pr.fechaFin - p.fechaIncio + 1
//                        println "dias 2 3 "+dias2+" = "+pr.fechaFin+" - "+p.fechaIncio+" +1"
                    }
                }
                diasTotal += dias2
                parcialCronograma += ((pc / diasPr) * dias2).round(2)
//                println "parcial crono += "+((pc/diasPr)*dias2).round(2)+"   = "+parcialCronograma+"     ("+pc+")   "+diasPr

            }
            p.parcialCronograma = parcialCronograma.toDouble().round(2)
            p.dias = diasTotal
            def totalPlanilla = planilla.valor
            def diasPlanilla = planilla.fechaFin - planilla.fechaInicio + 1
            def totDiario = totalPlanilla / diasPlanilla
            p.parcialPlanilla = (totDiario * dias).toDouble().round(2)
            p.p0 = Math.max(p.parcialCronograma, p.parcialPlanilla)
            if (!p.save(flush: true)) {
                println "error calculo p0 " + p.errors
            }
        }
        def act = 0
        def act2 = 0
        def diasTot = 0, totCrono = 0, totPlan = 0, totalMultaRetraso = 0
        (periodos + periodos2).each { p ->
            if (p.titulo != "OFERTA") {
                tablaP0 += '<tr>'
                if (p.titulo == "ANTICIPO") {
                    tablaP0 += "<th>${p.titulo}</th>"
                    tablaP0 += "<th>${fechaConFormato(p.fechaIncio, 'MMM-yy')}</th>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td class='number'>${numero(p.p0, 2)}</td>"
                } else {
                    def dias = p.fechaFin - p.fechaIncio + 1
                    diasTot += dias
                    tablaP0 += "<th>${fechaConFormato(p.fechaIncio, 'MMM-yy')}</th>"
                    tablaP0 += "<th>(${dias})</th>"
                    tablaP0 += "<td class='number'>${numero(p.parcialCronograma, 2)}</td>"
                    act += p.parcialCronograma
                    totCrono += p.parcialCronograma
                    tablaP0 += "<td class='number'>${numero(act, 2)}</td>"
                    tablaP0 += "<td class='number'>${numero(p.parcialPlanilla, 2)}</td>"
                    act2 += p.parcialPlanilla
                    totPlan += p.parcialPlanilla
                    tablaP0 += "<td class='number'>${numero(act2, 2)}</td>"
                    tablaP0 += "<td class='number'>${numero(p.p0, 2)}</td>"

                    if (p.planilla == planilla) {
                        def retraso = 0, multa = 0
                        if (p.parcialCronograma > p.parcialPlanilla) {
                            def totalContrato = contrato.monto
                            def prmlMulta = contrato.multaPlanilla
                            def valorDia = p.parcialCronograma / p.dias
                            retraso = ((p.parcialCronograma - p.parcialPlanilla) / valorDia).round(2)
                            multa = ((totalContrato) * (prmlMulta / 1000) * retraso).round(2)
                        }
                        totalMultaRetraso += multa
                        bodyMultaRetraso += "<tr>"
                        bodyMultaRetraso += "<th class='tal'>${fechaConFormato(p.fechaIncio, 'MMM-yy')}</th>"
                        bodyMultaRetraso += "<td class='number'>${numero(p.parcialCronograma, 2)}</td>"
                        bodyMultaRetraso += "<td class='number'>${numero(p.parcialPlanilla, 2)}</td>"
                        bodyMultaRetraso += "<td class='number'>${numero(retraso, 2)}</td>"
                        bodyMultaRetraso += "<td class='number'>${numero(multa, 2)}</td>"
                        bodyMultaRetraso += "</tr>"
                    }
                }
            }
        }

        tablaP0 += '</tbody>'
        tablaP0 += '<tfoot>'
        //totales aqui
        tablaP0 += "<th>TOTAL</th>"
        tablaP0 += "<th>(${diasTot})</th>"
        tablaP0 += "<td class='number'></td>"
        tablaP0 += "<th class='number'>${numero(totCrono, 2)}</th>"
        tablaP0 += "<td class='number'></td>"
        tablaP0 += "<th class='number'>${numero(totPlan, 2)}</th>"
        tablaP0 += "<td class='number'></td>"
        tablaP0 += '</tfoot>'
        tablaP0 += '</table>'

        //////////////////////////////////////////////***************** FR *********************************/////////////////

        def tr1 = "<tr>"
        def tr2 = "<tr>"
        def tr3 = "<tr>"
        def tablaFr = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;'>"
        tablaFr += '<thead>'
        tr1 = '<tr>'
        tr1 += '<th rowspan="2">Componentes</th>'
        tr3 += "<th>Anticipo</th>"

        def tdRowSpan = '<th colspan="' + (periodos.size() + periodos2.size() - 1) + '">Periodo de variación y aplicación de fórmula polinómica</th>'
        (periodos + periodos2).eachWithIndex { p, i ->
            if (i == 0) { //oferta
                tr1 += "<th>" + p.titulo + "</th>"
                tr2 += "<th>" + fechaConFormato(p.fechaIncio, "MMM-yy") + "</th>"
                tr3 += "<th class='number'>" + numero(p.planilla.contrato.porcentajeAnticipo, 0) + "%</th>"
            } //oferta
            else if (i == 1) { //anticipo
                tr1 += tdRowSpan
                tr2 += "<th>" + p.titulo + "<br/>" + fechaConFormato(p.fechaIncio, "MMM-yy") + "</th>"
                tr3 += "<th>" + p.titulo + "</th>"
            } //anticipo
            else { //otros
                tr2 += "<th rowspan='2'>" + fechaConFormato(p.fechaIncio, "MMM-yy") + "</th>"
            }//otros
        }  // periodos.each para el header
        tr1 += "</tr>"
        tr2 += "</tr>"
        tr3 += "</tr>"
        tablaFr += tr1 + tr2 + tr3
        tablaFr += "</thead>"
        tablaFr += "<tbody>"

        def totalFr = 0

        ps.eachWithIndex { p, i ->
            tablaFr += "<tr>"
            tablaFr += "<th class='tal'>" + p?.indice?.descripcion + " (${p?.numero})</th>"
            def vlinOferta
            periodos.eachWithIndex { per, j ->
                if (j == 0) { // es la oferta
                    def valor = 0
                    if (i == 0) { //es mano de obra
                        vlinOferta = per.total
                        tablaFr += "<td class='number'><div>${numero(p.valor)}</div><div class='bold'>${numero(per.total)}</div></td>"
                        valor = per.total
                    } else {
                        vlinOferta = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
                        tablaFr += "<td class='number'><div>${numero(p.valor)}</div><div class='bold'>${numero(vlinOferta, 2)}</div></td>"
                        valor = vlinOferta
                    }


                } else {
                    def vlin, dec
                    if (i == 0) {
                        vlin = per.total
                        dec = 3
                    } else {
                        vlin = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
                        dec = 2
                    }
//                    println "error "+p.indice+" "+p.indice.id+"  "+per.periodo.id+"   "+vlin+"  "+vlinOferta+"  "+p.valor+"  "+"  "+per.periodo.id
                    def valor = (vlin / vlinOferta * p.valor).round(3)

                    tablaFr += "<td class='number'><div>${numero(vlin, dec)}</div><div class='bold'>${numero(valor)}</div></td>"
                }
            }

            periodos2.eachWithIndex { per, j ->

                def vlin
                if (i == 0) {
                    vlin = per.total
                } else {
                    vlin = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
                }
//                    println "error "+p.indice+" "+p.indice.id+"  "+per.periodo.id+"   "+vlin+"  "+vlinOferta+"  "+p.valor+"  "+"  "+per.periodo.id
                def valor = (vlin / vlinOferta * p.valor).round(3)

                def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${per.periodo.id} and formulaPolinomica=${p.id}")
                if (vlrj.size() > 0) {
                    vlrj = vlrj.pop()
                    if (vlrj != valor) {
                        vlrj.valor = valor
                        if (!vlrj.save(flush: true)) {
                            println "error vlrj update " + vlrj.errors
                        }
                    }
                } else {
                    vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: per.periodo, formulaPolinomica: p, valor: valor])
                    if (!vlrj.save(flush: true)) {
                        println "error vlrj insert " + vlrj.errors
                    }
                }

                per.fr += valor
                if (!per.save(flush: true)) {
                    println "error fr " + per.errors
                }
                tablaFr += "<td class='number'><div>${numero(vlin, 2)}</div><div class='bold'>${numero(valor)}</div></td>"

            }

            tablaFr += "</tr>"
        }
        tablaFr += "</tbody>"

        tr1 = "<tr>"
        tr2 = "<tr>"
        tr3 = "<tr>"
        def tr4 = "<tr>"
        def tr5 = "<tr>"
        def tr6 = "<tr>"
        def tr7 = "<tr>"

        tr1 += "<th rowspan='4'>1.000</th>"
        tr1 += "<th>F<sub>r</sub></th>"
        tr2 += "<th>F<sub>r</sub>-1</th>"
        tr3 += "<th>P<sub>0</sub></th>"
        tr4 += "<th>P<sub>r</sub>-P</th>"

        def reajusteTotal = 0

        (periodos + periodos2).eachWithIndex { per, i ->
            if (i > 0) {
                def fr1 = (per.fr - 1).round(3)
                tr1 += "<th class='number'>${numero(per.fr)}</th>"
                tr2 += "<th class='number'>${numero(fr1)}</th>"
                tr3 += "<th class='number'>${numero(per.p0, 2)}</th>"
                def t = (per.p0 * fr1).round(2)
                tr4 += "<th class='number'>${numero(t, 2)}</th>"
                reajusteTotal += t
            }
        }

        reajusteTotal = reajusteTotal.toDouble().round(2)

        def reajusteAnterior = (planillaAnterior.reajuste).toDouble().round(2)

        tr5 += "<th colspan='2'>REAJUSTE TOTAL</th><th colspan='${periodos.size() + periodos2.size() - 1}' class='number'>" + numero(reajusteTotal, 2) + "</th>"
        tr6 += "<th colspan='2'>REAJUSTE ANTERIOR</th><th colspan='${periodos.size() + periodos2.size() - 1}' class='number'>" + numero(reajusteAnterior, 2) + "</th>"
        tr7 += "<th colspan='2'>REAJUSTE A PLANILLAR</th><th colspan='${periodos.size() + periodos2.size() - 1}' class='number'>" + numero((reajusteTotal - reajusteAnterior).toDouble().round(2), 2) + "</th>"

        tr1 += "</tr>"
        tr2 += "</tr>"
        tr3 += "</tr>"
        tr4 += "</tr>"
        tr5 += "</tr>"
        tr6 += "</tr>"
        tr7 += "</tr>"

        tablaFr += "<tfoot>"
        tablaFr += tr1 + tr2 + tr3 + tr4 + tr5 + tr6 + tr7
        tablaFr += "</tfoot></table>"
        ///////////////////////////////////////////************************************ multa retraso **********************////////////////////////////

        def tablaMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
        tablaMl += '<thead>'
        tablaMl += '<tr>'
        tablaMl += '<th>Mes y año</th>'
        tablaMl += '<th>Cronograma</th>'
        tablaMl += '<th>Planillado</th>'
        tablaMl += '<th>Retraso</th>'
        tablaMl += '<th>Multa</th>'
        tablaMl += '</tr>'
        tablaMl += '</thead>'
        tablaMl += '<tbody>'
        tablaMl += bodyMultaRetraso
        tablaMl += '</tbody>'
        tablaMl += '<tfoot>'
        tablaMl += '<tr>'
        tablaMl += '<th>TOTAL</th>'
        tablaMl += '<td colspan="3"></td>'
        tablaMl += "<th class='number'>${numero(totalMultaRetraso, 2)}</th>"
        tablaMl += '</tr>'
        tablaMl += '</tfoot>'
        tablaMl += '</table>'

        ///////////////////////////////////////////************************************ multa no presentacion planilla **********************////////////////////////////

        def totalMultaPlanilla = 0
        def diasMax = 5
        def fechaFinPer = planilla.fechaFin
        def fechaMax = fechaFinPer

        def noLaborables = ["Sat", "Sun"]

//            println fechaMax
        diasMax.times {
            fechaMax++
//                println fechaMax
            def fmt = new java.text.SimpleDateFormat("EEE", new Locale("en"))
            while (noLaborables.contains(fmt.format(fechaMax))) {
//                    println fmt.format(fechaMax)
                fechaMax++
//                    println fechaMax
            }
        }
//            println "***** "+fechaMax

        def fechaPresentacion = planilla.fechaPresentacion
        def retraso = fechaPresentacion - fechaMax + 1

        def totalMulta = 0

        def totalContrato = contrato.monto
        def prmlMulta = contrato.multaPlanilla
        if (retraso > 0) {
//            totalMulta = (totalContrato) * (prmlMulta / 1000) * retraso
            totalMulta = (PeriodoPlanilla.findAllByPlanilla(planilla).sum { it.parcialCronograma }) * (prmlMulta / 1000) * retraso
        } else {
            retraso = 0
        }

        def pMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
        pMl += "<tr>"
        pMl += '<th class="tal">Fecha presentación planilla</th><td>' + fechaPresentacion.format("dd-MM-yyyy") + ' </td>'
        pMl += "</tr>"
        pMl += "<tr>"
        pMl += '<th class="tal">Periodo planilla</th><td>' + planilla.fechaInicio.format("dd-MM-yyyy") + " a " + planilla.fechaFin.format("dd-MM-yyyy") + ' </td>'
        pMl += "</tr>"
        pMl += "<tr>"
        pMl += '<th class="tal">Fecha máximo presentación</th> <td>' + fechaMax.format("dd-MM-yyyy") + ' </td>'
        pMl += "</tr>"
        pMl += "<tr>"
        pMl += '<th class="tal">Días de retraso</th> <td>' + retraso + "</td>"
        pMl += "</tr>"
        pMl += "<tr>"
        pMl += '<th class="tal">Multa</th> <td>' + prmlMulta + "&#8240; de \$" + numero(totalContrato, 2) + "</td>"
        pMl += "</tr>"
        pMl += "<tr>"
        pMl += '<th class="tal">Total multa</th> <td>$' + numero(totalMulta, 2) + "</td>"
        pMl += "</tr>"
        pMl += '</table>'
        ///////////////////////////////////////////************************************fin**********************////////////////////////////

        planilla.reajuste = reajusteTotal
        planilla.multaPlanilla = totalMulta
        planilla.multaRetraso = totalMultaRetraso
        if (!planilla.save(flush: true)) {
            println "error planilla reajuste " + planilla.id
        }

        [tablaBo: tablaBo, planilla: planilla, tablaP0: tablaP0, tablaFr: tablaFr, tablaMl: tablaMl, pMl: pMl]
    }

    def anticipo() {

        def planilla = Planilla.get(params.id)
        def override = true
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def fechaOferta = planilla.contrato.oferta.fechaEntrega - 30
        def fechaAnticipo = planilla.fechaPresentacion
        def periodos = PeriodoPlanilla.findAllByPlanilla(planilla, [sort: "id"])

        def perOferta = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(fechaOferta, fechaOferta)
        def perAnticipo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(fechaAnticipo, fechaAnticipo)
//        println "planilla "+planilla+"  "+fechaOferta+"  "+fechaAnticipo+" pers "+periodos+"  "+perOferta+"  "+perAnticipo
        def pcs = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                or {
                    ilike("numero", "c%")
                    and {
                        ne("numero", "P0")
                        ne("numero", "p01")
                        ilike("numero", "p%")
                    }
                }
                order("numero", "asc")
            }
        }

        def cs = FormulaPolinomicaContractual.findAllByContratoAndNumeroIlike(contrato, "C%", [sort: "numero"])
//        def ps=  FormulaPolinomicaContractual.findAllByContratoAndNumeroIlike(contrato,"P%",[sort:"numero"])
        def ps = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                and {
                    ne("numero", "P0")
                    ilike("numero", "p%")
                }
                order("numero", "asc")
            }
        }

        if (override && periodos.size() > 0) {
            periodos.each {
                it.delete(flush: true)
            }
            periodos = []
        }



        if (periodos.size() == 0) {

//            pcs.each {c->
            println " per o " + perOferta + "  per a " + perAnticipo
            perOferta = verificaIndices(pcs, perOferta, 0)
            perAnticipo = verificaIndices(pcs, perAnticipo, 0)
            println " per o " + perOferta + "  per a " + perAnticipo

            def p1 = new PeriodoPlanilla([planilla: planilla, periodo: perOferta, fechaIncio: fechaOferta, fechaFin: getLastDayOfMonth(fechaOferta), titulo: "OFERTA"])
            if (!p1.save(flush: true)) {
                println "p1 " + p1.errors
            }
            def p2 = new PeriodoPlanilla([planilla: planilla, periodo: perAnticipo, fechaIncio: fechaAnticipo, fechaFin: getLastDayOfMonth(fechaAnticipo), titulo: "ANTICIPO"])
            if (!p2.save(flush: true)) {
                println "p2 " + p2.errors
            }
            periodos.add(p1)
            periodos.add(p2)

        }


        def tableWidth = 150 * periodos.size() + 400

        def tablaBo = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px;'>"
        tablaBo += "<thead>"
        tablaBo += "<tr>"
        tablaBo += "<th colspan=\"2\">Cuadrilla Tipo</th>"
        periodos.each {
            it.total = 0
            it.fr = 0
            tablaBo += "<th>${it.titulo}</th>"
            tablaBo += "<th class='nb'>" + fechaConFormato(it.fechaIncio, "MMM-yy") + "</th>"
        }

        tablaBo += "</tr>"
        tablaBo += "</thead>"
        tablaBo += "<tbody>"
        def totalCoef = 0
        cs.each { c ->
            tablaBo += "<tr>"
            tablaBo += "<th class='tal'>" + c.indice.descripcion + " (${c.numero})</th>"
            tablaBo += "<th class='number'>" + numero(c.valor) + "</th>"
            totalCoef += c.valor
            periodos.each { p ->
                def valor = ValorIndice.findByPeriodoAndIndice(p.periodo, c.indice).valor
                if (!valor) {
                    println "wtf no valor " + p.periodo + "  " + c.indice
                    valor = 0
                }

                p.total += (valor * c.valor).round(3)
                p.save(flush: true)

                tablaBo += "<td class='number'>" + numero(valor, 2) + "</td>"
                valor = (valor * c.valor).round(3)
                def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${p.periodo.id} and formulaPolinomica=${c.id}")
                if (vlrj.size() > 0) {
                    vlrj = vlrj.pop()
                    if (vlrj != valor) {
                        vlrj.valor = valor
                        if (!vlrj.save(flush: true)) {
                            println "error vlrj update " + vlrj.errors
                        }
                    }
                } else {
                    vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: p.periodo, formulaPolinomica: c, valor: valor])
                    if (!vlrj.save(flush: true)) {
                        println "error vlrj insert " + vlrj.errors
                    }
                }
                tablaBo += "<td class='number'>" + numero(valor) + "</td>"
            }

            tablaBo += "</tr>"
        }
        tablaBo += "</tbody><tfoot>"
        tablaBo += "<tr>" + "<th>TOTALES</th><th class='number'>${numero(totalCoef)}</th>"
        periodos.each { p ->
            tablaBo += "<td></td><th class='number'>${numero(p.total)}</th>"
        }
        tablaBo += "</tr></tfoot></table>"

        //////////////////////////**********************P0*********************///////////////////////////
        def tablaP0 = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;' >"
        tablaP0 += '<thead>'
        tablaP0 += '<tr>'
        tablaP0 += '<th colspan="2" rowspan="2">Mes y año</th>'
        tablaP0 += '<th colspan="2">Cronograma</th>'
        tablaP0 += '<th colspan="2">Planillado</th>'
        tablaP0 += '<th colspan="2" rowspan="2">Valor P<sub>0</sub></th>'
        tablaP0 += '</tr>'
        tablaP0 += '<tr>'
        tablaP0 += '<th>Parcial</th>'
        tablaP0 += '<th>Acumulado</th>'
        tablaP0 += '<th>Parcial</th>'
        tablaP0 += '<th>Acumulado</th>'
        tablaP0 += '</tr>'
        tablaP0 += '</thead>'
        tablaP0 += '<tbody>'
        periodos.each { p ->
            if (p.titulo != "OFERTA") {
                tablaP0 += '<tr>'
                if (p.titulo == "ANTICIPO") {
                    tablaP0 += "<th>${p.titulo}</th>"
                    tablaP0 += "<th>${fechaConFormato(p.fechaIncio, 'MMM-yy')}</th>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td class='number'>${numero(planilla.valor, 2)}</td>"
                    if (p.p0 == 0 || override) {
                        p.p0 = planilla.valor
                        if (!p.save(flush: true)) {
                            println "error p0 " + p
                        }
                    }
                }

            }
        }
        tablaP0 += '</tbody>'
        tablaP0 += '<tfoot>'
        //totales aqui
        tablaP0 += '</tfoot>'
        tablaP0 += '</table>'

        //////////////////////////////////////////////*****************FR*********************************/////////////////

        def tr1 = "<tr>"
        def tr2 = "<tr>"
        def tr3 = "<tr>"
        def tablaFr = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;'>"
        tablaFr += '<thead>'
        tr1 = '<tr>'
        tr1 += '<th rowspan="2">Componentes</th>'
        tr3 += "<th>Anticipo</th>"

        def tdRowSpan = '<th colspan="' + (periodos.size() - 1) + '">Periodo de variación y aplicación de fórmula polinómica</th>'
        periodos.eachWithIndex { p, i ->
            if (i == 0) { //oferta
                tr1 += "<th>" + p.titulo + "</th>"
                tr2 += "<th>" + fechaConFormato(p.fechaIncio) + "</th>"
                tr3 += "<th class='number'>" + numero(planilla.contrato.porcentajeAnticipo, 0) + "%</th>"
            } //oferta
            else if (i == 1) { //anticipo
                tr1 += tdRowSpan
                tr2 += "<th>" + p.titulo + "<br/>" + fechaConFormato(p.fechaIncio) + "</th>"
                tr3 += "<th>" + p.titulo + "</th>"
            } //anticipo
            else { //otros
                tr2 += "<th rowspan='2'>" + fechaConFormato(p.fechaIncio) + "</th>"
            }//otros
        }  // periodos.each para el header
        tr1 += "</tr>"
        tr2 += "</tr>"
        tr3 += "</tr>"
        tablaFr += tr1 + tr2 + tr3
        tablaFr += "</thead>"
        tablaFr += "<tbody>"

        def totalFr = 0

        ps.eachWithIndex { p, i ->
            tablaFr += "<tr>"
            tablaFr += "<th class='tal'>" + p?.indice?.descripcion + " (${p?.numero})</th>"
            def vlinOferta
            periodos.eachWithIndex { per, j ->
                if (j == 0) { // es la oferta
                    def valor = 0
                    if (i == 0) { //es mano de obra
                        vlinOferta = per.total
                        tablaFr += "<td class='number'><div>${numero(p.valor, 2)}</div><div class='bold'>${numero(per.total)}</div></td>"
                        valor = per.total
                    } else {
                        vlinOferta = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
                        tablaFr += "<td class='number'><div>${numero(p.valor)}</div><div class='bold'>${numero(vlinOferta, 2)}</div></td>"
                        valor = vlinOferta
                    }
                    def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${per.periodo.id} and formulaPolinomica=${p.id}")
                    if (vlrj.size() > 0) {
                        vlrj = vlrj.pop()
                        if (vlrj != valor) {
                            vlrj.valor = valor
                            if (!vlrj.save(flush: true)) {
                                println "error vlrj update " + vlrj.errors
                            }
                        }
                    } else {
                        vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: per.periodo, formulaPolinomica: p, valor: valor])
                        if (!vlrj.save(flush: true)) {
                            println "error vlrj insert " + vlrj.errors
                        }
                    }

                } else {
                    def vlin
                    if (i == 0) {
                        vlin = per.total
                    } else {
                        vlin = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
                    }
//                    println "error "+p.indice+" "+p.indice.id+"  "+per.periodo.id+"   "+vlin+"  "+vlinOferta+"  "+p.valor+"  "+"  "+per.periodo.id
                    def valor = (vlin / vlinOferta * p.valor).round(3)

                    def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${per.periodo.id} and formulaPolinomica=${p.id}")
                    if (vlrj.size() > 0) {
                        vlrj = vlrj.pop()
                        if (vlrj != valor) {
                            vlrj.valor = valor
                            if (!vlrj.save(flush: true)) {
                                println "error vlrj update " + vlrj.errors
                            }
                        }
                    } else {
                        vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: per.periodo, formulaPolinomica: p, valor: valor])
                        if (!vlrj.save(flush: true)) {
                            println "error vlrj insert " + vlrj.errors
                        }
                    }

                    per.fr += valor
                    if (!per.save(flush: true)) {
                        println "error fr " + per.errors
                    }
                    tablaFr += "<td class='number'><div>${numero(vlin, 2)}</div><div class='bold'>${numero(valor)}</div></td>"
                }
            }
            tablaFr += "</tr>"
        }
        tablaFr += "</tbody>"

        tr1 = "<tr>"
        tr2 = "<tr>"
        tr3 = "<tr>"
        def tr4 = "<tr>"
        def tr5 = "<tr>"

        tr1 += "<th rowspan='4'>1.000</th>"
        tr1 += "<th>F<sub>r</sub></th>"
        tr2 += "<th>F<sub>r</sub>-1</th>"
        tr3 += "<th>P<sub>0</sub></th>"
        tr4 += "<th>P<sub>r</sub>-P</th>"

        def reajusteTotal = 0

        periodos.eachWithIndex { per, i ->
            if (i > 0) {
                def fr1 = (per.fr - 1).round(3)
                tr1 += "<th class='number'>${numero(per.fr)}</th>"
                tr2 += "<th class='number'>${numero(fr1)}</th>"
                tr3 += "<th class='number'>${numero(per.p0, 2)}</th>"
                def t = (per.p0 * fr1).round(3)
                tr4 += "<th class='number'>${numero(t, 2)}</th>"
                reajusteTotal += t
            }
        }

        planilla.reajuste = reajusteTotal
        if (!planilla.save(flush: true)) {
            println "error planilla reajuste " + planilla.id
        }

        tr5 += "<th colspan='2'>REAJUSTE TOTAL</th><th class='number'>" + numero(reajusteTotal, 2) + "</th>"

        tablaFr += "<tfoot>"
        tablaFr += tr1 + tr2 + tr3 + tr4 + tr5
        tablaFr += "</tfoot></table>"

        [tablaBo: tablaBo, planilla: planilla, tablaP0: tablaP0, tablaFr: tablaFr]
    }

    def verificaIndices(pcs, per, i) {

//        println "\t"+i+"   "+per

        def perNuevo = per
        pcs.each { c ->
            def val = ValorIndice.findByIndiceAndPeriodo(c.indice, per)
            if (!val) {
                def vals = ValorIndice.withCriteria {
                    eq("indice", c.indice)
                    periodo {
                        le("fechaInicio", per.fechaInicio)
                        order("fechaInicio", "asc")
                    }
                }
//                println "no hay val   ${c.indice.id}---- ${per} "+vals.periodo
                if (vals.size() > 0) {
                    perNuevo = vals.pop().periodo
                } else {
                    println "error wtf  " + c.indice + " " + per
                }
            }
        }

        if (i > 12 * 5) {
            return null
        }

        if (per != perNuevo) {
            i++
            perNuevo = verificaIndices(pcs, perNuevo, i)
        }

        return perNuevo
    }

    def getLastDayOfMonth(fecha) {
        Date today = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);

        Date firstDayOfMonth = calendar.getTime();

        return firstDayOfMonth
    }

    private String fechaConFormato(fecha, formato) {
        def meses = ["", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"]
        def strFecha
        switch (formato) {
            case "MMM-yy":
                strFecha = meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yy")
                break;
        }
        return strFecha
    }

    private String fechaConFormato(fecha) {
        return fechaConFormato(fecha, "MMM-yy")
    }

    private String numero(num, decimales) {
        if (decimales == 0) {
            return formatNumber(number: num, minFractionDigits: decimales, maxFractionDigits: decimales, locale: "ec")
        } else {
            def format
            if (decimales == 2) {
                format = "##,##0"
            } else if (decimales == 3) {
                format = "##,###0"
            }
            return formatNumber(number: num, minFractionDigits: decimales, maxFractionDigits: decimales, locale: "ec", format: format)
        }
    }

    private String numero(num) {
        return numero(num, 3)
    }
}