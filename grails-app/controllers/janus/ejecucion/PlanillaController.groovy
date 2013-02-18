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
        def contrato = Contrato.get(params.contrato)
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
