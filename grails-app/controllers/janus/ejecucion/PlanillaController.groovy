package janus.ejecucion

import groovy.time.TimeCategory
import janus.Contrato
import janus.VolumenesObra
import janus.pac.PeriodoEjecucion

class PlanillaController extends janus.seguridad.Shield {

    def preciosService

    def list() {
        def contrato = Contrato.get(params.id)
        def planillaInstanceList = Planilla.findAllByContrato(contrato, [sort: 'numero'])
        return [contrato: contrato, obra: contrato.oferta.concurso.obra, planillaInstanceList: planillaInstanceList]
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
        if (cPlanillas == 0) {
            tiposPlanilla = TipoPlanilla.findAllByCodigo('A')
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

        def ultimoPeriodo = planillas.last().fechaFin

        PeriodoEjecucion.findAllByObra(contrato.oferta.concurso.obra, [sort: 'fechaInicio']).each { pe ->
//            println pe.fechaInicio.format("dd-MM-yyyy") + "\t" + pe.fechaFin.format("dd-MM-yyyy") + "\t" + pe.tipo
            if (pe.tipo == "P") {

                periodos += PeriodosInec.withCriteria {
                    or {
                        between("fechaInicio", pe.fechaInicio, pe.fechaFin)
                        between("fechaFin", pe.fechaInicio, pe.fechaFin)
                    }
                    and {
                        gt("fechaInicio", ultimoPeriodo)
                    }
                }

//                def mi = pe.fechaInicio.format("MM").toInteger()
//                def mf = pe.fechaFin.format("MM").toInteger()
//                def yi = pe.fechaInicio.format("yyyy")
//                def yf = pe.fechaFin.format("yyyy")
//                println ">>" + ultimoPeriodo.format("dd-MM-yyyy")
//                if (mi < mf) {
//                    for (def i = mi; i <= mf; i++) {
//                        def strDate = "01-" + i.toString().padLeft(2, "0") + "-" + yi
//                        def per = PeriodoValidez.findByFechaInicio(new Date().parse("dd-MM-yyyy", strDate))
////                        if (!planillas.periodoIndices.contains(per)) {
//                        println "\t\t" + i + "\t" + per
////                        }
//                    }
//                } else {
//                    for (def i = mi; i <= 12; i++) {
//                        def strDate = "01-" + i.toString().padLeft(2, "0") + "-" + yi
//                        def per = PeriodoValidez.findByFechaInicio(new Date().parse("dd-MM-yyyy", strDate))
////                        if (!planillas.periodoIndices.contains(per)) {
//                        println "\t\t" + i + "\t" + per
////                        }
//                    }
//                    for (def i = 1; i <= mf; i++) {
//                        def strDate = "01-" + i.toString().padLeft(2, "0") + "-" + yf
//                        def per = PeriodoValidez.findByFechaInicio(new Date().parse("dd-MM-yyyy", strDate))
////                        if (!planillas.periodoIndices.contains(per)) {
//                        println "\t\t" + i + "\t" + per
////                        }
//                    }
//                }
            }
        }
        periodos = periodos.unique().sort { it.fechaInicio }
//        println periodos

//        def periodos = PeriodoValidez.list([sort: 'fechaInicio'])

//        periodos = PeriodoValidez.withCriteria {
//
//        }
//
//        planillas.periodoIndices.each { pi ->
//            if (pi) {
//                def ind = periodos.id.indexOf((pi.id))
//                periodos.remove(ind)
//            }
//        }

        return [planillaInstance: planillaInstance, contrato: contrato, tipos: tiposPlanilla, obra: contrato.oferta.concurso.obra, periodos: periodos]
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
                    def ultimoPeriodo = otrasPlanillas.last().fechaFin
                    def ini
                    use(TimeCategory) {
                        ini = ultimoPeriodo + 1.days
                    }
                    def fin = planillaInstance.periodoIndices.fechaFin

                    planillaInstance.fechaInicio = ini
                    planillaInstance.fechaFin = fin
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
                redirect(action: 'resumen', id: planillaInstance.id, params: [contrato: planillaInstance.contratoId])
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

        def anteriores = []

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



        return [planilla: planilla, detalle: detalle, precios: precios, obra: obra, anteriores: anteriores, contrato: contrato]
    }

}
