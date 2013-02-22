package janus.ejecucion

import groovy.time.TimeCategory
import janus.Contrato
import janus.Obra
import janus.VolumenesObra
import janus.pac.PeriodoEjecucion

class PlanillaController extends janus.seguridad.Shield {

    def preciosService

    def list() {
        def contrato = Contrato.get(params.id)
        def planillaInstanceList = Planilla.findAllByContrato(contrato, [sort: 'numero'])
        return [contrato: contrato, obra: contrato.oferta.concurso.obra, planillaInstanceList: planillaInstanceList]
    }

    def pagar() {
        def planilla = Planilla.get(params.id)
        return [planillaInstance: planilla]
    }

    def savePago() {
        def planilla = Planilla.get(params.id)
        planilla.fechaPago = new Date().parse("dd-MM-yyyy", params.fechaPago)
        flash.message = ""
        if (!planilla.save(flush: true)) {
            println "ERROR al guardar el pago de la planilla " + planilla.errors
            flash.message = "Ha ocurrido un error al efectuar el pago:"
            flash.message += g.renderErrors(bean: planilla)
        } else {
            def obra = Obra.get(planilla.contrato.oferta.concurso.obraId)
            obra.fechaInicio = new Date().parse("dd-MM-yyyy", params.fechaPago)
            if (!obra.save(flush: true)) {
                println "ERROR al guardar el pago de la planilla (fecha inicio obra) " + obra.errors
                flash.message = "Ha ocurrido un error al efectuar el pago:"
                flash.message += g.renderErrors(bean: obra)
            }
        }
        if (flash.message == "") {
            flash.clase = "alert-success"
            redirect(controller: "cronogramaEjecucion", action: "index", id: planilla.contratoId)
        } else {
            flash.clase = "alert-error"
            redirect(action: "pagar", id: planilla.id)
        }
    }

    def form() {
        def contrato = Contrato.get(params.contrato)
        def planillaInstance = new Planilla(params)
        planillaInstance.contrato = contrato
        if (params.id) {
            planillaInstance = Planilla.get(params.id)
        }

        def anticipo = TipoPlanilla.findByCodigo('A')
        def liquidacion = TipoPlanilla.findByCodigo('L')
        def reajusteDefinitivo = TipoPlanilla.findByCodigo('R')

        def tiposPlanilla = TipoPlanilla.list([sort: 'nombre'])

        def planillas = Planilla.findAllByContrato(contrato, [sort: 'periodoIndices', order: "desc"])
        def cPlanillas = planillas.size()
        def esAnticipo = false
        if (cPlanillas == 0) {
            tiposPlanilla = TipoPlanilla.findAllByCodigo('A')
            esAnticipo = true
        } else {
            def pla = Planilla.findByContratoAndTipoPlanilla(contrato, anticipo)
            if (pla) {
                tiposPlanilla -= pla.tipoPlanilla
            }

            def pll = Planilla.findByContratoAndTipoPlanilla(contrato, liquidacion)
            if (pll) {
                tiposPlanilla -= pll.tipoPlanilla
            }

            def plr = Planilla.findByContratoAndTipoPlanilla(contrato, reajusteDefinitivo)
            if (plr) {
                tiposPlanilla -= plr.tipoPlanilla
            }
        }

        if (!params.id) {
            planillaInstance.numero = cPlanillas + 1
        }

        def periodos = []
        if (!esAnticipo) {
            def ultimoPeriodo = planillas.last().fechaFin
            PeriodoEjecucion.findAllByObra(contrato.oferta.concurso.obra, [sort: 'fechaInicio']).each { pe ->
                if (pe.tipo == "P") {
                    periodos += PeriodosInec.withCriteria {
                        or {
                            between("fechaInicio", pe.fechaInicio, pe.fechaFin)
                            between("fechaFin", pe.fechaInicio, pe.fechaFin)
                        }
                        if (ultimoPeriodo) {
                            and {
                                gt("fechaInicio", ultimoPeriodo)
                            }
                        }
                    }
                }
            }
            periodos = periodos.unique().sort { it.fechaInicio }
        }

        return [planillaInstance: planillaInstance, contrato: contrato, tipos: tiposPlanilla, obra: contrato.oferta.concurso.obra, periodos: periodos, esAnticipo: esAnticipo]
    }

    def save() {
        if (params.fechaPresentacion) {
            params.fechaPresentacion = new Date().parse("dd-MM-yyyy", params.fechaPresentacion)
        }
        if (params.fechaIngreso) {
            params.fechaIngreso = new Date().parse("dd-MM-yyyy", params.fechaIngreso)
        }
        if (params.fechaOficioSalida) {
            params.fechaOficioSalida = new Date().parse("dd-MM-yyyy", params.fechaOficioSalida)
        }
        if (params.fechaMemoSalida) {
            params.fechaMemoSalida = new Date().parse("dd-MM-yyyy", params.fechaMemoSalida)
        }

        def planillaInstance
        if (params.id) {
            planillaInstance = Planilla.get(params.id)
            if (!planillaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Planilla con id " + params.id
                params.contrato = params.contrato.id
                redirect(action: 'form', params: params)
                return
            }//no existe el objeto
            planillaInstance.properties = params
        }//es edit
        else {
            planillaInstance = new Planilla(params)

            switch (planillaInstance.tipoPlanilla.codigo) {
                case 'P':
                    //avance de obra: hay q poner fecha inicio y fecha fin

                    //las planillas q no son de avance para ver cual es el ultimo periodo planillado
                    def otrasPlanillas = Planilla.findAllByContratoAndTipoPlanillaNotEqual(planillaInstance.contrato, TipoPlanilla.findByCodigo("A"), [sort: 'periodoIndices', order: 'desc'])

                    def ini
                    if (otrasPlanillas.size() > 0) {
                        def ultimoPeriodo = otrasPlanillas?.last().fechaFin
                        use(TimeCategory) {
                            ini = ultimoPeriodo + 1.days
                        }
                    } else {
                        ini = planillaInstance.contrato.oferta.concurso.obra.fechaInicio
                    }
                    def fin = planillaInstance.periodoIndices.fechaFin

                    planillaInstance.fechaInicio = ini
                    planillaInstance.fechaFin = fin
                    break;
                case 'A':
                    //es anticipo hay q ingresar el valor de la planilla
                    planillaInstance.valor = planillaInstance.contrato.anticipo
                    break;
            }

        } //es create

        if (!planillaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Planilla " + (planillaInstance.id ? planillaInstance.id : "") + "</h4>"

            str += g.renderErrors(bean: planillaInstance)

            flash.message = str
            params.contrato = params.contrato.id
            redirect(action: 'form', params: params)
            return
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Planilla " + planillaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Planilla " + planillaInstance.id
        }

        switch (planillaInstance.tipoPlanilla.codigo) {
            case 'A':
                redirect(action: 'resumen', id: planillaInstance.id)
                break;
            case 'L':
                redirect(action: 'list', id: planillaInstance.contratoId)
                break;
            case 'P':
                redirect(action: 'detalle', id: planillaInstance.id, params: [contrato: planillaInstance.contratoId])
                break;
            default:
                redirect(action: 'list', id: planillaInstance.contratoId)
        }
    }

    def resumen() {
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def planillas = Planilla.findAllByContrato(contrato, [sort: "id"])
        def fp = janus.FormulaPolinomica.findAllByObra(obra)
        def fr = FormulaPolinomicaContractual.findAllByContrato(contrato)
        def tipo = TipoFormulaPolinomica.get(1)
        def oferta = contrato.oferta
        def periodoOferta = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(oferta.fechaEntrega, oferta.fechaEntrega)

        def periodos = []
        def data = [
                c: [:],
                p: [:]
        ]
        def fpB0

        //copia la formula polinomica a la formula polinomica contractual si esta no existe
        if (fr.size() < 5) {
            fr.each {
                it.delete(flush: true)
            }
            fp.each {
                if (it.valor > 0) {
                    def frpl = new FormulaPolinomicaContractual()
                    frpl.valor = it.valor
                    frpl.contrato = contrato
                    frpl.indice = it.indice
                    frpl.tipoFormulaPolinomica = tipo
                    frpl.numero = it.numero
                    if (!frpl.save(flush: true)) {
                        println "error frpl" + frpl.errors
                    }
                }
            }
            def fpP0 = new FormulaPolinomicaContractual()
            fpP0.valor = 0
            fpP0.contrato = contrato
            fpP0.indice = null
            fpP0.tipoFormulaPolinomica = tipo
            fpP0.numero = "P0"
            if (!fpP0.save(flush: true)) {
                println "error fpP0" + fpP0.errors
            }
            fpB0 = new FormulaPolinomicaContractual()
            fpB0.valor = 0
            fpB0.contrato = contrato
            fpB0.indice = null
            fpB0.tipoFormulaPolinomica = tipo
            fpB0.numero = "B0"
            if (!fpB0.save(flush: true)) {
                println "error fpB0" + fpB0.errors
            }
            def fpFr = new FormulaPolinomicaContractual()
            fpFr.valor = 0
            fpFr.contrato = contrato
            fpFr.indice = null
            fpFr.tipoFormulaPolinomica = tipo
            fpFr.numero = "Fr"
            if (!fpFr.save(flush: true)) {
                println "error fpFr" + fpFr.errors
            }
        } else {
            fpB0 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "B0")
        }

        def pcs = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                or {
                    ilike("numero", "c%")
                    and {
                        ne("numero", "P0")
                        ilike("numero", "p%")
                    }
                }
                order("numero", "asc")
            }
        }
//        println pcs.numero

        //llena el arreglo de periodos
        //el periodo que corresponde a la fecha de entrega de la oferta
        periodos.add(periodoOferta)
        planillas.each { pl ->
            if (pl.tipoPlanilla.codigo == 'A') {
                //si es anticipo: el periodo q corresponde a la fecha del anticipo
                def prin = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(pl.fechaPresentacion, pl.fechaPresentacion)
                periodos.add(prin)
            } else {
                periodos.add(pl.periodoIndices)
            }
        }

        periodos.eachWithIndex { per, perNum ->
            def pl = planilla
            if (perNum == 0) {
                pl = null
            }
            def valRea = ValorReajuste.findAllByObraAndPeriodoIndice(obra, per)
            def tot = [c: 0, p: 0]
            //si no existen valores de reajuste, se crean
            if (valRea.size() == 0) {
                pcs.each { c ->
                    def val = ValorIndice.findByPeriodoAndIndice(per, c.indice)?.valor
                    if (!val) {
                        val = 1
                    }
                    def vr = new ValorReajuste([
                            valor: val * c.valor,
                            formulaPolinomica: FormulaPolinomicaContractual.findByIndiceAndContrato(c.indice, contrato),
                            obra: obra,
                            periodoIndice: per,
                            planilla: pl
                    ])
                    if (!vr.save(flush: true)) {
                        println "vr errors " + vr.errors
                    }
                    def pos = "p"
                    if (c.numero.contains("c")) {
                        pos = "c"
                    }
                    tot[pos] += (vr.valor * c.valor).round(3)
//                    println "\t\t" + pos + "   " + (vr.valor * c.valor)
                    if (!data[pos][per]) {
                        data[pos][per] = [valores: [], total: 0]
                    }
                    data[pos][per]["valores"].add([formulaPolinomica: c, valorReajuste: vr])
                } //pcs.each
            } //valRea.size == 0
            else {
                valRea.each { v ->
                    def c = pcs.find { it.indice == v.formulaPolinomica.indice }
                    if (c) {
                        def pos = "p"
                        if (c.numero.contains("c")) {
                            pos = "c"
                        }
                        tot[pos] += (v.valor * c.valor).round(3)
//                        println "\t\t" + pos + "   " + (v.valor * c.valor)
                        if (!data[pos][per]) {
                            data[pos][per] = [valores: [], total: 0]
                        }
                        data[pos][per]["valores"].add([formulaPolinomica: c, valorReajuste: v])
                    }
                }
            } //valRea.size == 0
            data["c"][per]["total"] = tot["c"]
            data["p"][per]["total"] = tot["p"]
            def vrB0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, fpB0)
            if (!vrB0) {
                vrB0 = new ValorReajuste([
                        obra: obra,
                        planilla: pl,
                        periodoIndice: per,
                        formulaPolinomica: fpB0
                ])
            }
            vrB0.valor = tot["c"]
            if (!vrB0.save(flush: true)) {
                println "error al guardar valor de B0: " + tot["c"] + "\n" + vrB0.errors
            }
            def p01 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "p01")
            if (p01) {
                def vrP01 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p01)
                vrP01.valor = tot["c"]
                if (!vrP01.save(flush: true)) {
                    println "error al guardar valor de p01: " + tot["c"] + "\n" + vrP01.errors
                }
            }
        }
        def p0s = []
        def tbodyP0 = ""
        periodos.eachWithIndex { per, i ->
            if (i > 0) {
                tbodyP0 += "<tr>"
                if (i == 1) {
                    tbodyP0 += "<th>ANTICIPO</th>"
                    tbodyP0 += "<th>"
                    tbodyP0 += planilla.fechaPresentacion.format("MMM-yy")
                    tbodyP0 += "</th>"
                    tbodyP0 += "<td colspan='4'></td>"
                    tbodyP0 += "<td class='number>"
                    tbodyP0 += elm.numero(number: planilla.valor)
                    tbodyP0 += "</td>"

                    def p0 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "P0")
                    def vrP0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p0)
                    if (!vrP0) {
                        vrP0 = new ValorReajuste([
                                obra: obra,
                                planilla: planilla,
                                periodoIndice: per,
                                formulaPolinomica: p0
                        ])
                    }
                    vrP0.valor = planilla.valor
                    if (!vrP0.save(flush: true)) {
                        println "ERROR guardando P0: " + vrP0.errors
                    }
                    data["p"][per]["p0"] = vrP0.valor
                    p0s[i - 1] = vrP0.valor
                } else {

                }
                tbodyP0 += "</tr>"
            }
        }

        def a = 0, b = 0, c = 0, d = 0, tots = []
        def tbodyFr = "<tbody>"
        pcs.findAll { it.numero.contains('p') }.eachWithIndex { p, i ->
            tbodyFr += "<tr>"
            tbodyFr += "<td>" + p.indice.descripcion + " (" + p.numero + ")</td>"

            data.p.eachWithIndex { cp, j ->
                def act = cp.value.valores.find { it.formulaPolinomica.indice == p.indice }
                if (j == 0) {
                    c = act.formulaPolinomica.valor
                    b = act.valorReajuste.valor
                    tbodyFr += "<td class='number'>"
                    tbodyFr += "<div>"
                    tbodyFr += elm.numero(number: c, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "<div class='bold'>"
                    tbodyFr += elm.numero(number: b, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "</td>"
                } //j==0
                else {
                    a = act.valorReajuste.valor
                    d = (a / b) * c
                    tbodyFr += "<td class='number'>"
                    tbodyFr += "<div>"
                    tbodyFr += elm.numero(number: a, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "<div class='bold'>"
                    tbodyFr += elm.numero(number: d, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "</td>"
                    if (!tots[j - 1]) {
                        tots[j - 1] = [
                                per: cp.key,
                                total: 0
                        ]
                    }
                    tots[j - 1].total += d
                }
            } //data.p.each
            tbodyFr += "</tr>"
        } //pcs.p.each

        def filaFr = "", filaFr1 = "", filaP0 = "", filaPr = ""
//        println ">>>"
//        println tots

        def totalReajuste = 0

        tots.eachWithIndex { t, i ->
            def fpFr = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "Fr")
            def vrFr1 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(t.per, fpFr)
            if (!vrFr1) {
                vrFr1 = new ValorReajuste([
                        obra: obra,
                        planilla: planilla,
                        periodoIndice: t.per,
                        formulaPolinomica: fpFr
                ])
            }
            vrFr1.valor = t.total - 1
            if (!vrFr1.save(flush: true)) {
                println "ERROR guardando Fr-1: " + vrFr1.errors
            }
            def pr = (t.total - 1) * p0s[i]
            totalReajuste += pr
            filaFr += "<td class='number'>" + elm.numero(number: t.total, decimales: 3) + "</td>"
            filaFr1 += "<td class='number'>" + elm.numero(number: t.total - 1, decimales: 3) + "</td>"
            filaP0 += "<td class='number'>" + elm.numero(number: p0s[i]) + "</td>"
            filaPr += "<td class='number'>" + elm.numero(number: pr) + "</td>"
        }

        planilla.reajuste = totalReajuste
        if (!planilla.save(flush: true)) {
            println "ERROR al guardar reajuste de la planilla " + planilla.id + " " + totalReajuste + "\n" + planilla.errors
        }

        tbodyFr += "</tbody>"
        tbodyFr += "<tfoot>"

        tbodyFr += "<tr>"
        tbodyFr += "<th rowspan='4'>1.000</th>"
        tbodyFr += "<th>F<sub>r</sub></th>"
        tbodyFr += filaFr
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th>F<sub>r</sub>-1</th>"
        tbodyFr += filaFr1
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th>P<sub>0</sub></th>"
        tbodyFr += filaP0
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th>P<sub>r</sub>-P</th>"
        tbodyFr += filaPr
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th colspan='2'>REAJUSTE TOTAL</th>"
        tbodyFr += "<td class='number'>"
        tbodyFr += elm.numero(number: totalReajuste)
        tbodyFr += "</td>"
        tbodyFr += "</tr>"

        tbodyFr += "</tfoot>"

//        println "DATA C"
//        data.c.each {
//            println it
//        }
//        println "DATA P"
//        data.p.each {
//            println it
//        }

        return [tbodyFr: tbodyFr, planilla: planilla, obra: obra, oferta: oferta, contrato: contrato, pcs: pcs, data: data, periodos: periodos]
    }

    def resumen2() {
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def planillas = Planilla.findAllByContrato(contrato, [sort: "id"])
        def fp = janus.FormulaPolinomica.findAllByObra(obra)
        def fr = FormulaPolinomicaContractual.findAllByContrato(contrato)
        def tipo = TipoFormulaPolinomica.get(1)
        def oferta = contrato.oferta

        //copia la formula polinomica a la formula polinomica contractual si esta no existe
        if (fr.size() < 4) {
            fr.each {
                it.delete(flush: true)
            }
            fp.each {
                if (it.valor > 0) {
                    def frpl = new FormulaPolinomicaContractual()
                    frpl.valor = it.valor
                    frpl.contrato = contrato
                    frpl.indice = it.indice
                    frpl.tipoFormulaPolinomica = tipo
                    frpl.numero = it.numero
                    if (!frpl.save(flush: true)) {
                        println "error " + frpl.errors
                    }
                }
            }
            def frpl = new FormulaPolinomicaContractual()
            frpl.valor = 0
            frpl.contrato = contrato
            frpl.indice = null
            frpl.tipoFormulaPolinomica = tipo
            frpl.numero = "P0"
            if (!frpl.save(flush: true)) {
                println "error " + frpl.errors
            }
            frpl = new FormulaPolinomicaContractual()
            frpl.valor = 0
            frpl.contrato = contrato
            frpl.indice = null
            frpl.tipoFormulaPolinomica = tipo
            frpl.numero = "Fr"
            if (!frpl.save(flush: true)) {
                println "error " + frpl.errors
            }
        }

        // para B0: los indices de mano de obra: los c
        def cs = FormulaPolinomicaContractual.findAllByContratoAndNumeroLike(contrato, "c%", [sort: "numero"])
//        def ps = FormulaPolinomicaContractual.findAllByContratoAndNumeroLike(contrato, "p%", [sort: "numero"])
        //Para Fr y Pr: los p
        def ps = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                ne("numero", "P0")
                ilike("numero", "p%")
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
                        ilike("numero", "p%")
                    }
                }
                order("numero", "asc")
            }
        }
        println pcs.numero

        def datos = [], datosP = [], periodos = []
        def periodoOferta = PeriodosInec.findAllByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(oferta.fechaEntrega, oferta.fechaEntrega)

        periodos.add(periodoOferta[0])

        planillas.each { pl ->
            if (pl.tipoPlanilla.codigo == 'A') {
                def prin = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(pl.fechaPresentacion, pl.fechaPresentacion)
                periodos.add(prin)
            } else {
                periodos.add(pl.periodoIndices)
            }
        }

        def tot = 0, totP = 0

        periodos.each { per ->
            def vlin = ValorReajuste.findAllByObraAndPeriodoIndice(obra, per)
//            println ">>>>" + vlin.formulaPolinomica.numero

            if (vlin.size() == 0) {
                def tmp = [:], tmpP = [:]
                tot = 0
                totP = 0
                pcs.each { c ->
                    def val = ValorIndice.findByPeriodoAndIndice(per, c.indice)?.valor
                    if (!val) {
                        val = 1
                    }
                    def vr = new ValorReajuste([
                            valor: val * c.valor,
                            formulaPolinomica: FormulaPolinomicaContractual.findByIndiceAndContrato(c.indice, contrato),
                            obra: obra,
                            periodoIndice: per,
                            planilla: planilla
                    ])
                    if (!vr.save(flush: true)) {
                        println "vr errors " + vr.errors
                    }
                    if (c.numero.contains("c")) {
                        tmp.put(c.numero, vr.valor)
                        tot += vr.valor * c.valor
                    } else if (c.numero.contains("p")) {
                        println "\t\t" + c + "\t" + val
                        tmpP.put(c.numero, vr.valor)
                        totP += vr.valor * c.valor
                    }
                } //cs.each
                if (tmp.size() > 0) {
                    tmp.put("tot", tot)
                    datos.add(tmp)
                }
                if (tmpP.size() > 0) {
                    tmpP.put("tot", tot)
                    datosP.add(tmpP)
                }
            } // if(vlin.size=0
            else {
                def tmp = [:], tmpP = [:]
                tot = 0
                totP = 0
                vlin.each { v ->
                    pcs.each { c ->
//                        println "\t" + c.numero + " :: " + c.indiceId + " " + v.formulaPolinomica.indiceId
                        if (c.indiceId.toInteger() == v.formulaPolinomica?.indiceId?.toInteger()) {
                            if (c.numero.contains("c")) {
                                tmp.put(c.numero, v.valor)
                                tot += v.valor * c.valor
                            } else if (c.numero.contains("p")) {
                                println "\t\t" + c + "\t" + v
                                tmpP.put(c.numero, v.valor)
                                totP += v.valor * c.valor
                            }
                        }
                    }
//                    println "tmp "+tmp
                }
                if (tmp.size() > 0) {
                    tmp.put("tot", tot)
                    datos.add(tmp)
                }
                if (tmpP.size() > 0) {
                    tmpP.put("tot", tot)
                    datosP.add(tmpP)
                }
            } //else
        } //periodos.each
        println "DATOS:"
        datos.each {
            println "it " + it
        }
        println "DATOSP:"
        datosP.each {
            println "it " + it
        }

        def cant = []
        0.upto(datos.size() - 1) {
            cant.add(it)
        }

        def cantP = []
        0.upto(datosP.size() - 1) {
            cantP.add(it)
        }
//        println "cant " + cant
//        println "cantP " + cantP

        return [datos: datos, datosP: datosP, cs: cs, ps: ps, cant: cant, cantP: cantP, periodos: periodos, planilla: planilla, oferta: oferta, contrato: contrato]
    }

    def detalle() {
        def planilla = Planilla.get(params.id)
        def contrato = Contrato.get(params.contrato)

        def obra = contrato.oferta.concurso.obra
        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])

        def precios = [:]
        def indirecto = obra.totales / 100

        preciosService.ac_rbroObra(obra.id)

        detalle.each {
            def res = preciosService.presioUnitarioVolumenObra("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
        }

        def planillasAnteriores = Planilla.withCriteria {
            eq("contrato", contrato)
            lt("fechaFin", planilla.fechaInicio)
        }
//        println planillasAnteriores

        def editable = planilla.fechaPago == null
        println editable

        return [planilla: planilla, detalle: detalle, precios: precios, obra: obra, planillasAnteriores: planillasAnteriores, contrato: contrato, editable: editable]
    }

    def saveDetalle() {
        def pln = Planilla.get(params.id)
        def err = 0
        params.d.each { p ->
            def parts = p.split("_")
            if (parts.size() == 3) {
                //create
                println "CREATE"
                def vol = VolumenesObra.get(parts[0])
                def cant = parts[1].toDouble()
                def val = parts[2].toDouble()

                def detalle = new DetallePlanilla([
                        planilla: pln,
                        volumenObra: vol,
                        cantidad: cant,
                        monto: val
                ])
                if (!detalle.save(flush: true)) {
                    println "error guardando detalle (create) " + detalle.errors
                    err++
                }
            } else if (parts.size() == 4) {
                //update
                println "UPDATE"
                def cant = parts[1].toDouble()
                def val = parts[2].toDouble()

                def detalle = DetallePlanilla.get(parts[3])
                detalle.cantidad = cant
                detalle.monto = val
                if (!detalle.save(flush: true)) {
                    println "error guardando detalle (update) " + detalle.errors
                    err++
                }
            }
        }
        if (err > 0) {
            flash.clase = "alert-error"
            flash.message = "Ocurrieron " + err + " errores"
        } else {
            flash.clase = "alert-success"
            flash.message = "Planilla guardada exitosamente"
        }
        redirect(controller: "planilla", action: "list", id: pln.contratoId)
//        render params
    }

}
