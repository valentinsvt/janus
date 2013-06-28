package janus.ejecucion

import groovy.json.JsonBuilder
import groovy.time.TimeCategory
import janus.*
import janus.pac.CronogramaEjecucion
import janus.pac.PeriodoEjecucion

class PlanillaController extends janus.seguridad.Shield {

    def preciosService, buscadorService

    def list() {
        def contrato = Contrato.get(params.id)
        def obra = contrato.oferta.concurso.obra

        def fp = janus.FormulaPolinomica.findAllByObra(obra)
//        println fp

        def planillaInstanceList = Planilla.findAllByContrato(contrato, [sort: 'numero'])
        return [contrato: contrato, obra: contrato.oferta.concurso.obra, planillaInstanceList: planillaInstanceList]
    }

    def pagar() {
        def planilla = Planilla.get(params.id)
        return [planillaInstance: planilla]
    }

    def ordenPago() {
        def planilla = Planilla.get(params.id)
        return [planillaInstance: planilla]
    }

    def saveOrdenPago() {
//        println "save orden pago!! "+params
        def planilla = Planilla.get(params.id)
        planilla.fechaOrdenPago = new Date().parse("dd-MM-yyyy", params.fechaOrdenPago)
        planilla.memoOrdenPago = params.memoOrdenPago
        if (planilla.save(flush: true)) {
            flash.message = "Orden de pago registrada"
            redirect(action: "list", id: planilla.contrato.id)
        } else {
            flash.message = "Error al registrar la orden de pago"
            redirect(action: "ordenPago", id: params.id)
        }
    }

    def savePago() {
        def planilla = Planilla.get(params.id)
        planilla.fechaPago = new Date().parse("dd-MM-yyyy", params.fechaPago)

        planilla.memoPago = params.memoPago

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
        def costoPorcentaje = TipoPlanilla.findByCodigo('C')

        def tiposPlanilla = TipoPlanilla.list([sort: 'nombre'])

        def pla = Planilla.findByContratoAndTipoPlanilla(contrato, anticipo)
        def anticipoPagado = false
        if (!pla) {
            anticipoPagado = true
        } else {
            if (pla.fechaPago) {
                anticipoPagado = true
            }
        }

        def planillas = Planilla.findAllByContrato(contrato, [sort: 'periodoIndices', order: "asc"])
        def planillasAvance = Planilla.withCriteria {
            eq("contrato", contrato)
            tipoPlanilla {
                ne("codigo", "A")
            }
            order("periodoIndices", "asc")
        }
        def cPlanillas = planillas.size()

        def esAnticipo = false
        if (cPlanillas == 0) {
            tiposPlanilla = TipoPlanilla.findAllByCodigo('A')
            esAnticipo = true
        } else {
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
            def plc = Planilla.findByContratoAndTipoPlanilla(contrato, costoPorcentaje)
            if (plc) {
                tiposPlanilla -= plc.tipoPlanilla
            }
        }

        if (!params.id) {
            planillaInstance.numero = cPlanillas + 1
        }

        def periodos = []
        if (!esAnticipo) {
//            println "********************* " + planillasAvance
//            println "********************* " + planillas.last().fechaPresentacion
            def ultimoPeriodo
            if (planillasAvance.size() > 0) {
                ultimoPeriodo = planillasAvance.last().fechaFin
//            if (!ultimoPeriodo) {
//                ultimoPeriodo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(planillas.last().fechaPresentacion, planillas.last().fechaPresentacion).fechaFin
//            }
//            println planillasAvance
            } else {
                ultimoPeriodo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(planillas.last().fechaPresentacion, planillas.last().fechaPresentacion).fechaFin
            }
            println ultimoPeriodo
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
            println periodos
            periodos = periodos.unique().sort { it.fechaInicio }
        }

        return [planillaInstance: planillaInstance, contrato: contrato, tipos: tiposPlanilla, obra: contrato.oferta.concurso.obra, periodos: periodos, esAnticipo: esAnticipo, anticipoPagado: anticipoPagado]
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
                    def otrasPlanillas = Planilla.findAllByContratoAndTipoPlanillaNotEqual(planillaInstance.contrato, TipoPlanilla.findByCodigo("A"), [sort: 'periodoIndices', order: 'asc'])

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

                    /* ************** La fecha inicio y fecha fin se vuelven las fechas del periodo ***************************/
                    planillaInstance.fechaInicio = planillaInstance.periodoIndices.fechaInicio
                    planillaInstance.fechaFin = planillaInstance.periodoIndices.fechaFin

                    break;
                case 'A':
                    //es anticipo hay q ingresar el valor de la planilla
                    planillaInstance.valor = planillaInstance.contrato.anticipo
                    break;
                case "C":
                    //es de costo y porcentaje: fecha inicio y fecha fin se ponen la fecha de presentacion (?)
                    planillaInstance.fechaInicio = planillaInstance.fechaPresentacion
                    planillaInstance.fechaFin = planillaInstance.fechaPresentacion
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
            case 'C':
                redirect(action: 'detalleCosto', id: planillaInstance.id, params: [contrato: planillaInstance.contratoId])
                break;
            default:
                redirect(action: 'list', id: planillaInstance.contratoId)
        }
    }

    def deleteReajuste() {
        /*
         *  esta parte es solo para pruebas!!!
         *  borra los valorReajuste de la planilla
         *
         */
        def planilla = Planilla.get(params.id)
        def html = ""
        def valorReajusteBorrar = ValorReajuste.findAllByPlanilla(planilla)
        def cont = 0
        valorReajusteBorrar.each {
            html += "Eliminando " + it.id + "<br/>"
            try {
                it.delete(flush: true)
                cont++
            } catch (e) {
                html += "&nbsp;&nbsp;&nbsp;Error al eliminar: " + e.printStackTrace()
            }
        }
        html += "Terminó de borrar " + cont + " valorReajuste"
        render html
    }

    def resumen() {
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
//        def planillas = Planilla.findAllByContrato(contrato, [sort: "id"])

        def planillas = Planilla.withCriteria {
            and {
                eq("contrato", contrato)
                or {
                    lt("fechaInicio", planilla.fechaFin)
                    isNull("fechaInicio")
                }
                order("id", "asc")
            }
        }

        def fp = FormulaPolinomica.findAllByObra(obra)
        def fr = FormulaPolinomicaContractual.findAllByContrato(contrato)
        def tipo = TipoFormulaPolinomica.get(1)
        def oferta = contrato.oferta
        def periodoOferta = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(oferta.fechaEntrega, oferta.fechaEntrega)

        def periodos = [], periodosPlanilla = []
        def data2 = [
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
        def planillaAnticipo = Planilla.findByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo("A"))
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
//                println pl.id + "   " + pl.fechaInicio.format("dd-MM-yyyy") + " a " + pl.fechaFin.format("dd-MM-yyyy")
                def m1 = pl.fechaInicio.format("MM")
                def m2 = pl.fechaFin.format("MM")
//                println "\t" + m1 + "   " + m2
                if (m1 == m2) {
                    if (pl.periodoIndices) {
//                        println "++ " + pl.periodoIndices
                        periodos.add(pl.periodoIndices)
                    }
                    if (pl == planilla) {
//                        println "-- " + pl
                        periodosPlanilla.add(pl)
                    }
                } else {
                    def y = pl.fechaInicio.format("yyyy")
                    (m1.toInteger()..m2.toInteger()).each {
                        println "\t\t" + it + " - " + y
                        def fi = new Date().parse("dd-MM-yyyy", "01-" + it + "-" + y)
//                        if (pl.periodoIndices.fechaFin >= fi) {
                        def prin = PeriodosInec.findByFechaInicio(fi)
                        if (prin) {
                            periodos.add(prin)
                        }
                        if (pl == planilla) {
                            periodosPlanilla.add(prin)
                        }
                        if (it == 12) {
                            y = pl.fechaFin.format("yyyy")
                        }
//                        }
                    }
                }
            }
        }
        periodos = periodos.unique()
//        println periodos
//        render periodos.descripcion

        def errorIndice = ""

//        println "Aqui empieza las inserciones"
        periodos.eachWithIndex { per, perNum ->
            def pl = planilla
            if (perNum == 0) {
                pl = null
            }
            def valRea = ValorReajuste.findAllByObraAndPeriodoIndice(obra, per)

//            println ">>>Periodo " + per.descripcion + " (${perNum}) hay " + valRea.size() + " valRea: " + valRea.id + " (obra: ${obra.id})"

            def tot = [c: 0, p: 0]
            //si no existen valores de reajuste, se crean

            if (valRea.size() == 0) {
                pcs.each { c ->
                    def val = ValorIndice.findByPeriodoAndIndice(per, c.indice)?.valor
                    if (!val) {
                        println per
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
                    } else {
//                        println "crea vr ${vr.id}"
                    }
                    def pos = "p"
                    if (c.numero.contains("c")) {
                        pos = "c"
                    }
                    tot[pos] += (vr.valor * c.valor).round(3)
//                    println "\t\t" + pos + "   " + (vr.valor * c.valor)
                    if (!data2[pos][perNum]) {
//                        println "\t\tCrea data2[${pos}][${perNum}]"
                        data2[pos][perNum] = [valores: [], total: 0, periodo: per]
                    }
                    data2[pos][perNum]["valores"].add([formulaPolinomica: c, valorReajuste: vr, valorTabla: vr.valor])
                } //pcs.each
            } //valRea.size == 0
            else {
                valRea.each { v ->
                    def c = pcs.find { it.indice == v.formulaPolinomica.indice }
                    if (c) {
                        def val = ValorIndice.findByPeriodoAndIndice(per, c.indice)?.valor
                        if (!val) {
                            val = 1
                        }
//                        println "\t\t"+per.descripcion+"   "+c.indice+"   "+val+"   "+c.id
                        def pos = "p"
                        if (c.numero.contains("c")) {
                            pos = "c"
                        }
                        tot[pos] += (val * c.valor).round(3)
//                        println "\t\t" + pos + "   " + (v.valor * c.valor)
                        if (!data2[pos][perNum]) {
//                            println "\t\tCrea data2[${pos}][${perNum}]"
                            data2[pos][perNum] = [valores: [], total: 0, periodo: per]
                        }

                        data2[pos][perNum]["valores"].add([formulaPolinomica: c, valorReajuste: v, valorTabla: val])
                    }
                }
            } //valRea.size == 0

//            println "data[c][${perNum}][total]=${tot['c']}"
            data2["c"][perNum]["total"] = tot["c"]
//            println "data[p][${perNum}][total]=${tot['p']}"
            data2["p"][perNum]["total"] = tot["p"]
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

        def tableWidth = 150 * periodos.size() + 400

        def tablaBo = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px;'>"
        tablaBo += "<thead>"
        tablaBo += "<tr>"
        tablaBo += "<th colspan=\"2\">Cuadrilla Tipo</th>"
        tablaBo += "<th>Oferta</th>"
        tablaBo += "<th class='nb'>" + oferta.fechaEntrega.format("MMM-yy") + "</th>"
        tablaBo += "<th>Variación</th>"
        tablaBo += "<th class='nb'>Anticipo (" + planillaAnticipo.fechaPresentacion.format("MMM-yy") + ")</th>"
        if (periodos.size() > 2) {
            periodos.eachWithIndex { per, i ->
                if (i > 1) {
                    tablaBo += "<th>Variación</th>"
                    tablaBo += "<th class='nb'>" + per?.descripcion + "</th>"
                }
            }
        }
        tablaBo += "</tr>"
        tablaBo += "</thead>"


        def tbodyB0 = "<tbody>"
        def totC = 0
        pcs.findAll { it.numero.contains("c") }.each { c ->
            tbodyB0 += "<tr>"
            tbodyB0 += "<td>" + c.indice.descripcion + " (" + c.numero + ")</td>"
            tbodyB0 += "<td class='number'>" + elm.numero(number: c.valor, decimales: 3) + "</td>"
            totC += c.valor
            data2.c.each { cp ->
                def act = cp.value.valores.find { it.formulaPolinomica.indice == c.indice }
//                def val = act.valorReajuste.valor
                def val = act.valorTabla
                def valReajuste = act.valorReajuste.valor
                tbodyB0 += "<td class='number'>" + elm.numero(number: val, decimales: 3) + "</td>"
                tbodyB0 += "<td class='number'>" + elm.numero(number: valReajuste, decimales: 3) + "</td>"
            }
            tbodyB0 += "</tr>"
        }
        tbodyB0 += "</tbody>"
        tbodyB0 += "<tfoot>"
        tbodyB0 += "<tr>"
        tbodyB0 += "<th>TOTALES</th>"
        tbodyB0 += "<td class='number bold'>" + elm.numero(number: totC, decimales: 3) + "</td>"
        data2.c.each { cp ->
            tbodyB0 += "<td></td>"
            tbodyB0 += "<td class='number bold'>" + elm.numero(number: cp.value.total, decimales: 3) + "</td>"
        }
        tbodyB0 += "</tr>"
        tbodyB0 += "</tfoot>"

        tablaBo += tbodyB0
        tablaBo += "</table>"

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

        def p0s = []
        def tbodyP0 = "<tbody>"
        def tbodyMl = "<tbody>"

        def diasPlanilla = 0

        if (planilla.tipoPlanilla.codigo != "A") {
            diasPlanilla = planilla.fechaFin - planilla.fechaInicio + 1
        }
        def valorPlanilla = planilla.valor

        def acumuladoCrono = 0, acumuladoPlan = 0

        def diasAll = 0

        def totalMultaRetraso = 0, valorTotalPeriodoActual = 0

//        println "periodos "+periodos

        periodos.eachWithIndex { per, i ->
            if (i > 0) {
                def planillaActual = Planilla.findByPeriodoIndicesAndContrato(per, contrato)
                tbodyP0 += "<tr>"
                if (i == 1) {
                    tbodyP0 += "<th>ANTICIPO</th>"
                    tbodyP0 += "<th>"
                    tbodyP0 += planillaAnticipo.fechaPresentacion.format("MMM-yy")
                    tbodyP0 += "</th>"
                    tbodyP0 += "<td colspan='4'></td>"
                    tbodyP0 += "<td class='number'>"
                    tbodyP0 += elm.numero(number: planillaAnticipo.valor)
                    tbodyP0 += "</td>"

                    def p0 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "P0")
                    def vrP0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p0)
                    if (!vrP0) {
                        vrP0 = new ValorReajuste([
                                obra: obra,
                                planilla: planillaAnticipo,
                                periodoIndice: per,
                                formulaPolinomica: p0
                        ])
                    }
                    vrP0.valor = planillaAnticipo.valor
                    if (!vrP0.save(flush: true)) {
                        println "ERROR guardando P0: " + vrP0.errors
                    }
                    data2["p"][i]["p0"] = vrP0.valor
                    p0s[i - 1] = vrP0.valor
                } else {
//                    def periodosEjecucion = PeriodoEjecucion.findAllByObra(obra)
//                    def periodosEjecucion = PeriodoEjecucion.withCriteria {
//                        and {
//                            eq("obra", obra)
//                                or {
//                                    between("fechaInicio", per.fechaInicio, per.fechaFin)
//                                    between("fechaFin", per.fechaInicio, per.fechaFin)
//                                }
//                            order("fechaInicio")
//                        }
//                    }
//                    println "planilla fin: "+planilla.fechaFin

                    def periodosEjecucion = PeriodoEjecucion.withCriteria {
                        and {
                            eq("obra", obra)
                            or {
                                between("fechaInicio", per.fechaInicio, per.fechaFin)
                                between("fechaFin", per.fechaInicio, per.fechaFin)
                            }
                            le("fechaFin", planilla.fechaFin)
                            order("fechaInicio")
                        }
                    }


                    def diasTotal = 0, valorTotal = 0
//                    println "\t\t++ "+per.fechaInicio.format("dd-MM-yyyy") + "  " + per.fechaFin.format("dd-MM-yyyy")
//                    println "\t\t-- "+periodosEjecucion

                    tbodyP0 += "<th>"
                    tbodyP0 += per?.descripcion
                    tbodyP0 += "</th>"
//                    println "????"
                    periodosEjecucion.each { pe ->
//                        println "\t" + pe.tipo + "  " + pe.fechaInicio.format("dd-MM-yyyy") + "   " + pe.fechaFin.format("dd-MM-yyyy")
                        if (pe.tipo == "P") {
                            def diasUsados = 0
                            def diasPeriodo = pe.fechaFin - pe.fechaInicio + 1
//                            println "\t\tdias periodo: " + diasPeriodo
                            def crono = CronogramaEjecucion.findAllByPeriodo(pe)
                            def valorPeriodo = crono.sum { it.precio }
//                            println "\t\tvalor periodo: " + valorPeriodo
                            if (per) {
                                if (pe.fechaInicio <= per.fechaInicio) {
                                    diasUsados = pe.fechaFin - per.fechaInicio + 1
//                                    println "\t\t1 dias usados: " + diasUsados
                                } else if (pe.fechaInicio > per.fechaInicio && pe.fechaFin < per.fechaFin) {
                                    diasUsados = pe.fechaFin - pe.fechaInicio + 1
//                                    println "\t\t2 dias usados: " + diasUsados
                                } else if (pe.fechaFin >= per.fechaFin) {
                                    diasUsados = per.fechaFin - pe.fechaInicio + 1
//                                    println "\t\t3 dias usados: " + diasUsados
                                }
                                diasTotal += diasUsados
                            }
                            def valorUsado = (valorPeriodo / diasPeriodo) * diasUsados
                            valorTotal += valorUsado
//                            println "\t\tvalor usado: " + valorUsado
                        }
                    }
                    acumuladoCrono += valorTotal
//                    println "***** " + valorPlanilla + "  " + diasPlanilla + "  " + diasTotal
                    def planillado = (valorPlanilla / diasPlanilla) * diasTotal
                    acumuladoPlan += planillado
//                    println "TOTAL: " + diasTotal + " dias"
//                    println "PLANILLADO: " + planillado

                    def p0 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "P0")
                    def vrP0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p0)
                    if (!vrP0) {
                        vrP0 = new ValorReajuste([
                                obra: obra,
                                planilla: planillaActual,
                                periodoIndice: per,
                                formulaPolinomica: p0
                        ])
                    }
                    vrP0.valor = Math.max(valorTotal, planillado)
                    if (!vrP0.save(flush: true)) {
                        println "ERROR guardando P0: " + vrP0.errors
                    }
                    data2["p"][i]["p0"] = vrP0.valor
                    p0s[i - 1] = vrP0.valor
                    diasAll += diasTotal
                    tbodyP0 += "<th>"
                    tbodyP0 += "(" + diasTotal + ")"
                    tbodyP0 += "</th>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: valorTotal) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: acumuladoCrono) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: planillado) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: acumuladoPlan) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: vrP0.valor) + "</td>"

                    if (periodosPlanilla.contains(per)) {
                        def multa = 0
                        def retraso = 0
                        if (valorTotal > planillado) {
                            def totalContrato = contrato.monto
                            def prmlMulta = contrato.multaPlanilla
                            def valorDia = valorTotal / diasTotal
                            retraso = (valorTotal - planillado) / valorDia
                            multa = (totalContrato) * (prmlMulta / 1000) * retraso
                        }
                        if (per == planilla.periodoIndices) {
                            valorTotalPeriodoActual = valorTotal
                        }
                        totalMultaRetraso += multa
                        tbodyMl += "<tr>"
                        tbodyMl += "<th>"
                        tbodyMl += per.descripcion
                        tbodyMl += "</th>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: valorTotal) + "</td>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: planillado) + "</td>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: retraso) + "</td>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: multa) + "</td>"
                        tbodyMl += "</tr>"
//                        tbodyMl += "<tr>"
//                        tbodyMl += "<th>Mes y año</th>"
//                        tbodyMl += "<td>${per.descripcion}</td>"
//                        tbodyMl += "</tr>"
//                        tbodyMl += "<tr>"
//                        tbodyMl += "<th>Cronograma</th>"
//                        tbodyMl += "<td class='number'>" + elm.numero(number: valorTotal) + "</td>"
//                        tbodyMl += "</tr>"
//                        tbodyMl += "<tr>"
//                        tbodyMl += "<th>Planillado</th>"
//                        tbodyMl += "<td class='number'>" + elm.numero(number: planillado) + "</td>"
//                        tbodyMl += "</tr>"
//                        tbodyMl += "<tr>"
//                        tbodyMl += "<th>Retraso</th>"
//                        tbodyMl += "<td class='number'>" + elm.numero(number: retraso) + "</td>"
//                        tbodyMl += "</tr>"
//                        tbodyMl += "<tr>"
//                        tbodyMl += "<th>Multa</th>"
//                        tbodyMl += "<td class='number'>" + elm.numero(number: multa) + "</td>"
//                        tbodyMl += "</tr>"


                    }
                }
                tbodyP0 += "</tr>"
            }
        }
        tbodyMl += "</tbody>"
        tbodyP0 += "</tbody>"
        if (planilla.tipoPlanilla.codigo != "A") {
            tbodyP0 += "<tfoot>"
            tbodyP0 += "<tr>"
            tbodyP0 += "<th>TOTAL</th>"
            tbodyP0 += "<th>(" + diasAll + ")</th>"
            tbodyP0 += "<td></td>"
            tbodyP0 += "<td class='number bold'>" + elm.numero(number: acumuladoCrono) + "</td>"
            tbodyP0 += "<td></td>"
            tbodyP0 += "<td class='number bold'>" + elm.numero(number: acumuladoPlan) + "</td>"
            tbodyP0 += "<td></td>"
            tbodyP0 += "</tr>"
            tbodyP0 += "</tfoot>"
        }

        tablaP0 += tbodyP0
        tablaP0 += "</table>"

        def tablaFr = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;'>"
        tablaFr += '<thead>'
        tablaFr += '<tr>'
        tablaFr += '<th rowspan="2">Componentes</th>'
        tablaFr += '<th>Oferta</th>'
        tablaFr += '<th colspan="' + (periodos.size() - 1) + '">Periodo de variación y aplicación de fórmula polinómica</th>'
        tablaFr += '</tr>'
        tablaFr += '<tr>'
        tablaFr += '<th>' + (oferta.fechaEntrega.format("MMM-yy")) + '</th>'
        tablaFr += '<th>Anticipo <br/>' + planillaAnticipo.fechaPresentacion.format("MMM-yy") + '</th>'
        if (periodos.size() > 2) {
            periodos.eachWithIndex { per, i ->
                if (i > 1) {
                    tablaFr += '<th rowspan="2">' + per?.descripcion + '</th>'
                }
            }
        }
        tablaFr += '</tr>'
        tablaFr += '<tr>'
        tablaFr += '<th>Anticipo</th>'
        tablaFr += '<th>'
        tablaFr += elm.numero(number: contrato.porcentajeAnticipo, decimales: 0) + "%"
        tablaFr += '</th>'
        tablaFr += '<th>Anticipo</th>'
        tablaFr += '</tr>'
        tablaFr += '</thead>'

        def a = 0, b = 0, c = 0, d = 0, tots = []
        def tbodyFr = "<tbody>"
        pcs.findAll { it.numero.contains('p') }.eachWithIndex { p, i ->
            tbodyFr += "<tr>"
            tbodyFr += "<td>" + p.indice.descripcion + " (" + p.numero + ")</td>"

            data2.p.eachWithIndex { cp, j ->
                def act = cp.value.valores.find { it.formulaPolinomica.indice == p.indice }
                if (j == 0) {
                    c = act.formulaPolinomica.valor
                    b = act.valorTabla
                    if (i == 0) {
                        b = act.valorReajuste.valor
                    }
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
                    if (i > 0) {
                        a = act.valorTabla
                    }
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
                                per: cp.value.periodo,
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
//        println p0s

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
            def pr = (t.total - 1).round(3) * p0s[i]

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
        tbodyFr += "<td colspan='${periodos.size()}' class='number bold'>"
        tbodyFr += elm.numero(number: totalReajuste)
        tbodyFr += "</td>"
        tbodyFr += "</tr>"

        tbodyFr += "</tfoot>"


        tablaFr += tbodyFr
        tablaFr += "</table>"

        def smallTableWidth = 400

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

        tablaMl += tbodyMl

        tablaMl += "<tfoot>"
        tablaMl += '<th >Total</th>'
        tablaMl += '<td colspan="3"></td>'
        tablaMl += '<td class="bold number">'
        tablaMl += elm.numero(number: totalMultaRetraso)
        tablaMl += '</td>'
        tablaMl += "</tfoot>"

        tablaMl += "</table>"

        def pMl = ""

        if (planilla.periodoIndices) {
            def diasMax = 5
            def fechaFinPer = planilla.periodoIndices?.fechaFin
            def fechaMax = fechaFinPer
//            use(TimeCategory) {
//                fechaMax = fechaFinPer + diasMax.days
//            }

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
            def retraso = fechaPresentacion - fechaMax

            def totalMulta = 0

            def totalContrato = contrato.monto
            def prmlMulta = contrato.multaPlanilla
            if (retraso > 0) {
//            totalMulta = (totalContrato) * (prmlMulta / 1000) * retraso
                totalMulta = (valorTotalPeriodoActual) * (prmlMulta / 1000) * retraso
            } else {
                retraso = 0
            }

            pMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
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
            pMl += '<th class="tal">Multa</th> <td>' + elm.numero(number: prmlMulta) + "&#8240; de \$" + elm.numero(number: totalContrato) + "</td>"
            pMl += "</tr>"
            pMl += "<tr>"
            pMl += '<th class="tal">Total multa</th> <td>$' + elm.numero(number: totalMulta) + "</td>"
            pMl += "</tr>"
            pMl += '</table>'
        }
        return [tablaB0: tablaBo, tablaP0: tablaP0, tablaFr: tablaFr, tablaMl: tablaMl, pMl: pMl, planilla: planilla, obra: obra, oferta: oferta, contrato: contrato]
    }

    private String formatoFecha(Date fecha, String format) {
        def meses = ["", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"]
        def str = ""
        switch (format) {
            case "MMM-yy":
                str = meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yy")
                break;
        }
        return str
    }

    def resumenPlanilla() {
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def oferta = contrato.oferta

        def tablaB0, tablaP0, tablaFr, tablaMl, pMl
        def planillaAnticipo = Planilla.findByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo("A"))
        def planillas = Planilla.withCriteria {
            and {
                eq("contrato", contrato)
                or {
                    lt("fechaInicio", planilla.fechaFin)
                    isNull("fechaInicio")
                }
                order("id", "asc")
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
        def fp = FormulaPolinomica.findAllByObra(obra)
        def fr = FormulaPolinomicaContractual.findAllByContrato(contrato)
        def tipo = TipoFormulaPolinomica.get(1)
        def periodoOferta = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(oferta.fechaEntrega, oferta.fechaEntrega)

        def periodos = [], periodosPlanilla = []
        def data2 = [
                c: [:],
                p: [:]
        ]
        def fpB0, fpP0

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
            fpP0 = new FormulaPolinomicaContractual()
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

        //llena el arreglo de periodos
        //el periodo que corresponde a la fecha de entrega de la oferta
        periodos.add(periodoOferta)
        planillas.each { pl ->
            if (pl.tipoPlanilla.codigo == 'A') {
                //si es anticipo: el periodo q corresponde a la fecha del anticipo
                def prin = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(pl.fechaPresentacion, pl.fechaPresentacion)
                periodos.add(prin)
            } else {
//                println pl.id + "   " + pl.fechaInicio.format("dd-MM-yyyy") + " a " + pl.fechaFin.format("dd-MM-yyyy")
                def m1 = pl.fechaInicio.format("MM")
                def m2 = pl.fechaFin.format("MM")
//                println "\t" + m1 + "   " + m2
                if (m1 == m2) {
                    if (pl.periodoIndices) {
                        println "++ " + pl.periodoIndices
                        periodos.add(pl.periodoIndices)
                    }
                    if (pl == planilla) {
                        println "-- " + pl
                        periodosPlanilla.add(pl)
                    }
                } else {
                    def y = pl.fechaInicio.format("yyyy")
                    (m1.toInteger()..m2.toInteger()).each {
//                        println "\t\t" + it + " - " + y
                        def fi = new Date().parse("dd-MM-yyyy", "01-" + it + "-" + y)
                        def prin = PeriodosInec.findByFechaInicio(fi)
                        if (prin) {
                            periodos.add(prin)
                        }
                        if (pl == planilla) {
                            periodosPlanilla.add(prin)
                        }
                        if (it == 12) {
                            y = pl.fechaFin.format("yyyy")
                        }
                    }
                }
            }
        }
        periodos = periodos.unique()
//        println periodos

//        println "Aqui empieza las inserciones"
        periodos.eachWithIndex { per, perNum ->
            def pl = planilla
            if (perNum == 0) {
                pl = null
            }
            def valRea = ValorReajuste.findAllByObraAndPeriodoIndice(obra, per)

//            println ">>>Periodo " + per.descripcion + " (${perNum}) hay " + valRea.size() + " valRea: " + valRea.id + " (obra: ${obra.id})"

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
                    } else {
//                        println "crea vr ${vr.id}"
                    }
                    def pos = "p"
                    if (c.numero.contains("c")) {
                        pos = "c"
                    }
                    tot[pos] += (vr.valor * c.valor).round(3)
//                    println "\t\t" + pos + "   " + (vr.valor * c.valor)
                    if (!data2[pos][perNum]) {
//                        println "\t\tCrea data2[${pos}][${perNum}]"
                        data2[pos][perNum] = [valores: [], total: 0, periodo: per]
                    }
                    data2[pos][perNum]["valores"].add([formulaPolinomica: c, valorReajuste: vr, valorTabla: vr.valor])
                } //pcs.each
            } //valRea.size == 0
            else {
                valRea.each { v ->
                    def c = pcs.find { it.indice == v.formulaPolinomica.indice }
                    if (c) {
                        def val = ValorIndice.findByPeriodoAndIndice(per, c.indice)?.valor
                        if (!val) {
                            val = 1
                        }
//                        println "\t\t"+per.descripcion+"   "+c.indice+"   "+val+"   "+c.id
                        def pos = "p"
                        if (c.numero.contains("c")) {
                            pos = "c"
                        }
                        tot[pos] += (val * c.valor).round(3)
//                        println "\t\t" + pos + "   " + (v.valor * c.valor)
                        if (!data2[pos][perNum]) {
//                            println "\t\tCrea data2[${pos}][${perNum}]"
                            data2[pos][perNum] = [valores: [], total: 0, periodo: per]
                        }

                        data2[pos][perNum]["valores"].add([formulaPolinomica: c, valorReajuste: v, valorTabla: val])
                    }
                }
            } //valRea.size == 0

//            println "data[c][${perNum}][total]=${tot['c']}"
            data2["c"][perNum]["total"] = tot["c"]
//            println "data[p][${perNum}][total]=${tot['p']}"
            data2["p"][perNum]["total"] = tot["p"]
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

        def tableWidth = 150 * periodos.size() + 400

        tablaB0 = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px;'>"
        tablaB0 += "<thead>"
        tablaB0 += "<tr>"
        tablaB0 += "<th colspan=\"2\">Cuadrilla Tipo</th>"
        tablaB0 += "<th>Oferta</th>"
        tablaB0 += "<th class='nb'>" + formatoFecha(oferta.fechaEntrega, "MMM-yy") + "</th>"
        tablaB0 += "<th>Variación</th>"
        tablaB0 += "<th class='nb'>Anticipo (" + formatoFecha(planillaAnticipo.fechaPresentacion, "MMM-yy") + ")</th>"
        if (periodos.size() > 2) {
            periodos.eachWithIndex { per, i ->
                if (i > 1) {
                    tablaB0 += "<th>Variación</th>"
                    tablaB0 += "<th class='nb'>" + per?.descripcion + "</th>"
                }
            }
        }
        tablaB0 += "</tr>"
        tablaB0 += "</thead>"

        def tbodyB0 = "<tbody>"
        def totC = 0
        pcs.findAll { it.numero.contains("c") }.each { c ->
            tbodyB0 += "<tr>"
            tbodyB0 += "<td>" + c.indice.descripcion + " (" + c.numero + ")</td>"
            tbodyB0 += "<td class='number'>" + elm.numero(number: c.valor, decimales: 3) + "</td>"
            totC += c.valor
            data2.c.each { cp ->
                def act = cp.value.valores.find { it.formulaPolinomica.indice == c.indice }
//                def val = act.valorReajuste.valor
                def val = act.valorTabla
                def valReajuste = act.valorReajuste.valor
                tbodyB0 += "<td class='number'>" + elm.numero(number: val, decimales: 3) + "</td>"
                tbodyB0 += "<td class='number'>" + elm.numero(number: valReajuste, decimales: 3) + "</td>"
            }
            tbodyB0 += "</tr>"
        }
        tbodyB0 += "</tbody>"
        tbodyB0 += "<tfoot>"
        tbodyB0 += "<tr>"
        tbodyB0 += "<th>TOTALES</th>"
        tbodyB0 += "<td class='number bold'>" + elm.numero(number: totC, decimales: 3) + "</td>"
        data2.c.each { cp ->
            tbodyB0 += "<td></td>"
            tbodyB0 += "<td class='number bold'>" + elm.numero(number: cp.value.total, decimales: 3) + "</td>"
        }
        tbodyB0 += "</tr>"
        tbodyB0 += "</tfoot>"

        tablaB0 += tbodyB0
        tablaB0 += "</table>"

        tablaP0 = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;' >"
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

        def p0s = []
        def tbodyP0 = "<tbody>"
        def tbodyMl = "<tbody>"

        def diasPlanilla = 0

        if (planilla.tipoPlanilla.codigo != "A") {
            diasPlanilla = planilla.fechaFin - planilla.fechaInicio + 1
        }
        def valorPlanilla = planilla.valor

        def acumuladoCrono = 0, acumuladoPlan = 0

        def diasAll = 0

        def totalMultaRetraso = 0, valorTotalPeriodoActual = 0

//        println "periodos "+periodos

        periodos.eachWithIndex { per, i ->
            if (i > 0) {
                def planillaActual = Planilla.findByPeriodoIndicesAndContrato(per, contrato)
                tbodyP0 += "<tr>"
                if (i == 1) {
                    tbodyP0 += "<th>ANTICIPO</th>"
                    tbodyP0 += "<th>"
                    tbodyP0 += planillaAnticipo.fechaPresentacion.format("MMM-yy")
                    tbodyP0 += "</th>"
                    tbodyP0 += "<td colspan='4'></td>"
                    tbodyP0 += "<td class='number'>"
                    tbodyP0 += elm.numero(number: planillaAnticipo.valor)
                    tbodyP0 += "</td>"

                    def p0 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "P0")
                    def vrP0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p0)
                    if (!vrP0) {
                        vrP0 = new ValorReajuste([
                                obra: obra,
                                planilla: planillaAnticipo,
                                periodoIndice: per,
                                formulaPolinomica: p0
                        ])
                    }
                    vrP0.valor = planillaAnticipo.valor
                    if (!vrP0.save(flush: true)) {
                        println "ERROR guardando P0: " + vrP0.errors
                    }
                    data2["p"][i]["p0"] = vrP0.valor
                    p0s[i - 1] = vrP0.valor
                } else {
//                    def periodosEjecucion = PeriodoEjecucion.findAllByObra(obra)
//                    def periodosEjecucion = PeriodoEjecucion.withCriteria {
//                        and {
//                            eq("obra", obra)
//                                or {
//                                    between("fechaInicio", per.fechaInicio, per.fechaFin)
//                                    between("fechaFin", per.fechaInicio, per.fechaFin)
//                                }
//                            order("fechaInicio")
//                        }
//                    }
//                    println "planilla fin: "+planilla.fechaFin

                    def periodosEjecucion = PeriodoEjecucion.withCriteria {
                        and {
                            eq("obra", obra)
                            or {
                                between("fechaInicio", per.fechaInicio, per.fechaFin)
                                between("fechaFin", per.fechaInicio, per.fechaFin)
                            }
                            le("fechaFin", planilla.fechaFin)
                            order("fechaInicio")
                        }
                    }


                    def diasTotal = 0, valorTotal = 0
//                    println "\t\t++ "+per.fechaInicio.format("dd-MM-yyyy") + "  " + per.fechaFin.format("dd-MM-yyyy")
//                    println "\t\t-- "+periodosEjecucion

                    tbodyP0 += "<th>"
                    tbodyP0 += per?.descripcion
                    tbodyP0 += "</th>"
//                    println "????"
                    periodosEjecucion.each { pe ->
//                        println "\t" + pe.tipo + "  " + pe.fechaInicio.format("dd-MM-yyyy") + "   " + pe.fechaFin.format("dd-MM-yyyy")
                        if (pe.tipo == "P") {
                            def diasUsados = 0
                            def diasPeriodo = pe.fechaFin - pe.fechaInicio + 1
//                            println "\t\tdias periodo: " + diasPeriodo
                            def crono = CronogramaEjecucion.findAllByPeriodo(pe)
                            def valorPeriodo = crono.sum { it.precio }
//                            println "\t\tvalor periodo: " + valorPeriodo
                            if (per) {
                                if (pe.fechaInicio <= per.fechaInicio) {
                                    diasUsados = pe.fechaFin - per.fechaInicio + 1
//                                    println "\t\t1 dias usados: " + diasUsados
                                } else if (pe.fechaInicio > per.fechaInicio && pe.fechaFin < per.fechaFin) {
                                    diasUsados = pe.fechaFin - pe.fechaInicio + 1
//                                    println "\t\t2 dias usados: " + diasUsados
                                } else if (pe.fechaFin >= per.fechaFin) {
                                    diasUsados = per.fechaFin - pe.fechaInicio + 1
//                                    println "\t\t3 dias usados: " + diasUsados
                                }
                                diasTotal += diasUsados
                            }
                            def valorUsado = (valorPeriodo / diasPeriodo) * diasUsados
                            valorTotal += valorUsado
//                            println "\t\tvalor usado: " + valorUsado
                        }
                    }
                    acumuladoCrono += valorTotal
//                    println "***** " + valorPlanilla + "  " + diasPlanilla + "  " + diasTotal
                    def planillado = (valorPlanilla / diasPlanilla) * diasTotal
                    acumuladoPlan += planillado
//                    println "TOTAL: " + diasTotal + " dias"
//                    println "PLANILLADO: " + planillado

                    def p0 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "P0")
                    def vrP0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p0)
                    if (!vrP0) {
                        vrP0 = new ValorReajuste([
                                obra: obra,
                                planilla: planillaActual,
                                periodoIndice: per,
                                formulaPolinomica: p0
                        ])
                    }
                    vrP0.valor = Math.max(valorTotal, planillado)
                    if (!vrP0.save(flush: true)) {
                        println "ERROR guardando P0: " + vrP0.errors
                    }
                    data2["p"][i]["p0"] = vrP0.valor
                    p0s[i - 1] = vrP0.valor
                    diasAll += diasTotal
                    tbodyP0 += "<th>"
                    tbodyP0 += "(" + diasTotal + ")"
                    tbodyP0 += "</th>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: valorTotal) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: acumuladoCrono) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: planillado) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: acumuladoPlan) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: vrP0.valor) + "</td>"

                    if (periodosPlanilla.contains(per)) {
                        def multa = 0
                        def retraso = 0
                        if (valorTotal > planillado) {
                            def totalContrato = contrato.monto
                            def prmlMulta = contrato.multaPlanilla
                            def valorDia = valorTotal / diasTotal
                            retraso = (valorTotal - planillado) / valorDia
                            multa = (totalContrato) * (prmlMulta / 1000) * retraso
                        }
                        if (per == planilla.periodoIndices) {
                            valorTotalPeriodoActual = valorTotal
                        }
                        totalMultaRetraso += multa
                        tbodyMl += "<tr>"
                        tbodyMl += "<th>"
                        tbodyMl += per.descripcion
                        tbodyMl += "</th>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: valorTotal) + "</td>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: planillado) + "</td>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: retraso) + "</td>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: multa) + "</td>"
                        tbodyMl += "</tr>"
                    }
                }
                tbodyP0 += "</tr>"
            }
        }
        tbodyMl += "</tbody>"
        tbodyP0 += "</tbody>"
        if (planilla.tipoPlanilla.codigo != "A") {
            tbodyP0 += "<tfoot>"
            tbodyP0 += "<tr>"
            tbodyP0 += "<th>TOTAL</th>"
            tbodyP0 += "<th>(" + diasAll + ")</th>"
            tbodyP0 += "<td></td>"
            tbodyP0 += "<td class='number bold'>" + elm.numero(number: acumuladoCrono) + "</td>"
            tbodyP0 += "<td></td>"
            tbodyP0 += "<td class='number bold'>" + elm.numero(number: acumuladoPlan) + "</td>"
            tbodyP0 += "<td></td>"
            tbodyP0 += "</tr>"
            tbodyP0 += "</tfoot>"
        }

        tablaP0 += tbodyP0
        tablaP0 += "</table>"

        tablaFr = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;'>"
        tablaFr += '<thead>'
        tablaFr += '<tr>'
        tablaFr += '<th rowspan="2">Componentes</th>'
        tablaFr += '<th>Oferta</th>'
        tablaFr += '<th colspan="' + (periodos.size() - 1) + '">Periodo de variación y aplicación de fórmula polinómica</th>'
        tablaFr += '</tr>'
        tablaFr += '<tr>'
        tablaFr += '<th>' + (oferta.fechaEntrega.format("MMM-yy")) + '</th>'
        tablaFr += '<th>Anticipo <br/>' + planillaAnticipo.fechaPresentacion.format("MMM-yy") + '</th>'
        if (periodos.size() > 2) {
            periodos.eachWithIndex { per, i ->
                if (i > 1) {
                    tablaFr += '<th rowspan="2">' + per?.descripcion + '</th>'
                }
            }
        }
        tablaFr += '</tr>'
        tablaFr += '<tr>'
        tablaFr += '<th>Anticipo</th>'
        tablaFr += '<th>'
        tablaFr += elm.numero(number: contrato.porcentajeAnticipo, decimales: 0) + "%"
        tablaFr += '</th>'
        tablaFr += '<th>Anticipo</th>'
        tablaFr += '</tr>'
        tablaFr += '</thead>'

        def a = 0, b = 0, c = 0, d = 0, tots = []
        def tbodyFr = "<tbody>"
        pcs.findAll { it.numero.contains('p') }.eachWithIndex { p, i ->
            tbodyFr += "<tr>"
            tbodyFr += "<td>" + p.indice.descripcion + " (" + p.numero + ")</td>"

            data2.p.eachWithIndex { cp, j ->
                def act = cp.value.valores.find { it.formulaPolinomica.indice == p.indice }
                if (j == 0) {
                    c = act.formulaPolinomica.valor
                    b = act.valorTabla
                    if (i == 0) {
                        b = act.valorReajuste.valor
                    }
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
                    if (i > 0) {
                        a = act.valorTabla
                    }
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
                                per: cp.value.periodo,
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
//        println p0s

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
            def pr = (t.total - 1).round(3) * p0s[i]

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
        tbodyFr += "<td colspan='${periodos.size()}' class='number bold'>"
        tbodyFr += elm.numero(number: totalReajuste)
        tbodyFr += "</td>"
        tbodyFr += "</tr>"

        tbodyFr += "</tfoot>"


        tablaFr += tbodyFr
        tablaFr += "</table>"

        def smallTableWidth = 400

        tablaMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
        tablaMl += '<thead>'
        tablaMl += '<tr>'
        tablaMl += '<th>Mes y año</th>'
        tablaMl += '<th>Cronograma</th>'
        tablaMl += '<th>Planillado</th>'
        tablaMl += '<th>Retraso</th>'
        tablaMl += '<th>Multa</th>'
        tablaMl += '</tr>'
        tablaMl += '</thead>'

        tablaMl += tbodyMl

        tablaMl += "<tfoot>"
        tablaMl += '<th >Total</th>'
        tablaMl += '<td colspan="3"></td>'
        tablaMl += '<td class="bold number">'
        tablaMl += elm.numero(number: totalMultaRetraso)
        tablaMl += '</td>'
        tablaMl += "</tfoot>"

        tablaMl += "</table>"

        if (planilla.periodoIndices) {
            def diasMax = 5
            def fechaFinPer = planilla.periodoIndices?.fechaFin
            def fechaMax = fechaFinPer
//            use(TimeCategory) {
//                fechaMax = fechaFinPer + diasMax.days
//            }

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
            def retraso = fechaPresentacion - fechaMax

            def totalMulta = 0

            def totalContrato = contrato.monto
            def prmlMulta = contrato.multaPlanilla
            if (retraso > 0) {
//            totalMulta = (totalContrato) * (prmlMulta / 1000) * retraso
                totalMulta = (valorTotalPeriodoActual) * (prmlMulta / 1000) * retraso
            } else {
                retraso = 0
            }

            pMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
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
            pMl += '<th class="tal">Multa</th> <td>' + elm.numero(number: prmlMulta) + "&#8240; de \$" + elm.numero(number: totalContrato) + "</td>"
            pMl += "</tr>"
            pMl += "<tr>"
            pMl += '<th class="tal">Total multa</th> <td>$' + elm.numero(number: totalMulta) + "</td>"
            pMl += "</tr>"
            pMl += '</table>'
        }

        return [planilla: planilla, contrato: contrato, tablaB0: tablaB0, tablaP0: tablaP0, tablaFr: tablaFr, tablaMl: tablaMl, pMl: pMl]
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
//        println pcs.numero

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
//                        println "\t\t" + c + "\t" + val
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
//                                println "\t\t" + c + "\t" + v
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
//        println "DATOS:"
//        datos.each {
//            println "it " + it
//        }
//        println "DATOSP:"
//        datosP.each {
//            println "it " + it
//        }

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
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
        }

        def planillasAnteriores = Planilla.withCriteria {
            eq("contrato", contrato)
            lt("fechaFin", planilla.fechaInicio)
        }
//        println planillasAnteriores

        def editable = planilla.fechaOrdenPago == null
//        println editable

        return [planilla: planilla, detalle: detalle, precios: precios, obra: obra, planillasAnteriores: planillasAnteriores, contrato: contrato, editable: editable]

    }

    def detalleCosto() {
        def planilla = Planilla.get(params.id)
        def contrato = Contrato.get(params.contrato)

        def obra = contrato.oferta.concurso.obra

        def editable = planilla.fechaPago == null
        def campos = ["codigo": ["Código", "string"], "nombre": ["Descripción", "string"]]

        def dets = []
        DetallePlanilla.findAllByPlanillaAndItemIsNotNull(planilla).each { det ->
            dets.add([
                    edit: true,
                    id: det.id,
                    item: det.itemId,
                    unidad: det.item.unidad.codigo,
                    nombre: det.item.nombre,
                    codigo: det.item.codigo,
                    cantidad: det.cantidad,
                    precio: det.monto,
                    total: det.cantidad * det.monto
            ])
        }
        def json = new JsonBuilder(dets)
//        println json.toPrettyString()

        return [planilla: planilla, obra: obra, contrato: contrato, editable: editable, campos: campos, detalles: json]

    }

    def deleteDetalleCosto() {
        def item = Item.get(params.item)
        def planilla = Planilla.get(params.pln)
        def detalle = DetallePlanilla.findByPlanillaAndItem(planilla, item)
        detalle.delete(flush: true)
        render "OK"
    }

    def addDetalleCosto() {
        def planilla = Planilla.get(params.pln)
        def item = Item.get(params.item)
        def cant = params.cant.toDouble()
        def prec = params.prec.toDouble()

        def detalle
        if (params.id) {
            detalle = DetallePlanilla.get(params.id)
            detalle.cantidad = cant
            detalle.monto = prec
        } else {
            detalle = new DetallePlanilla([
                    planilla: planilla,
                    item: item,
                    cantidad: cant,
                    monto: prec
            ])
        }
        if (!detalle.save(flush: true)) {
            println "ERROR: " + detalle.errors
            render "NO"
        } else {
            planilla.valor = params.totalPl.toDouble()
            if (!planilla.save(flush: true)) {
                println "ERROR save planilla " + planilla.errors
            }
            render "OK_" + detalle.id
        }
    }

    def buscaRubro() {
        def listaTitulos = ["Código", "Descripción", "Unidad"]
        def listaCampos = ["codigo", "nombre", "unidad"]
        def funciones = [null, null]
        def url = g.createLink(action: "buscaRubro", controller: "rubro")
        def funcionJs = "function(){"
        funcionJs += 'clickBuscar($(this));'
        funcionJs += '}'
        def numRegistros = 20
        def extras = " and tipoItem = 2"
        if (!params.reporte) {
            def lista = buscadorService.buscar(Item, "Item", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
            lista.pop()
            render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Item
            session.funciones = funciones
            def anchos = [20, 80] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Item", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Rubros", anchos: anchos, extras: extras, landscape: true])
        }
    }

    def saveDetalle() {
//        println params
        def pln = Planilla.get(params.id)
        def err = 0

        if (params.d.class == java.lang.String) {
            params.d = [params.d]
        }
//
//        println params

        params.d.each { p ->
            def parts = p.split("_")
            if (parts.size() == 3) {
                //create
//                println "CREATE"
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
//                println "UPDATE"
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
            pln.valor = params.total.toDouble()
            if (!pln.save(flush: true)) {
                flash.clase = "alert-error"
                flash.message = "Ocurrió un error al guardar la planilla"
            } else {
                flash.clase = "alert-success"
                flash.message = "Planilla guardada exitosamente"
            }
        }
        redirect(controller: "planilla", action: "list", id: pln.contratoId)
//        render params
    }

}
