package janus.ejecucion

import janus.Contrato

class PlanillaController extends janus.seguridad.Shield {

    def list() {
        def contrato = Contrato.get(params.id)
        def planillaInstanceList = Planilla.findAllByContrato(contrato)
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
        println tiposPlanilla.nombre
        def planillas = Planilla.countByContrato(contrato)
        if (planillas == 0) {
            tiposPlanilla = TipoPlanilla.findAllByCodigo('A')
        } else {
            def pla = Planilla.findByContratoAndTipoPlanilla(contrato, anticipo)
            if (pla) {
                tiposPlanilla -= pla.tipoPlanilla
            }
            println tiposPlanilla.nombre

            def pll = Planilla.findByContratoAndTipoPlanilla(contrato, liquidacion)
            if (pll) {
                tiposPlanilla -= pll.tipoPlanilla
            }
            println tiposPlanilla.nombre

            def plr = Planilla.findByContratoAndTipoPlanilla(contrato, reajusteDefinitivo)
            if (plr) {
                tiposPlanilla -= plr.tipoPlanilla
            }
            println tiposPlanilla.nombre
        }

        if (!params.id) {
            planillaInstance.numero = planillas + 1
        }

        return [planillaInstance: planillaInstance, contrato: contrato, tipos: tiposPlanilla, obra: contrato.oferta.concurso.obra]
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

    }

}
