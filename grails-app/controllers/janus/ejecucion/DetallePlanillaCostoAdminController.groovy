package janus.ejecucion

import groovy.json.JsonBuilder
import janus.Contrato
import janus.Parametros
import org.springframework.dao.DataIntegrityViolationException

class DetallePlanillaCostoAdminController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def detalleCosto() {
        def planilla = PlanillaAdmin.get(params.id)
        def contrato = Contrato.get(params.contrato)
//        def obra = contrato.oferta.concurso.obra
        def obra = planilla.obra
        def editable = true //planilla.fechaMemoSalidaPlanilla == null
        def iva = Parametros.get(1).iva
        def dets = []

        def detalles = DetallePlanillaCostoAdmin.findAllByPlanilla(planilla)
        /*"planilla.id"   :${planilla.id},
            factura         : factura,
            rubro           : rubro,
            "unidad.id"     : unidadId,
            unidadText      : unidadText,
            monto           : valor,
            montoIva        : valorIva,
            montoIndirectos : valorIndi,
            indirectos      : $("#thIndirectos").data("indi"),
            total           : total
            */
        detalles.each { dp ->
            dets.add([
                    id: dp.id,
                    "planilla.id": planilla.id,
                    rubro: dp.rubro,
                    "unidad.id": dp.unidadId,
                    unidadText: dp.unidad.codigo,
                    valor: dp.valor,
                    valorIva: dp.valorIva,
                    cantidad: dp.cantidad,
                    total: dp.valorIva * dp.cantidad
            ])
        }

//        def anteriores = Planilla.withCriteria {
//            eq("tipoPlanilla", TipoPlanilla.findByCodigo("C"))
//            ne("id", planilla.id)
//        }

//        def totalAnterior = anteriores.size() > 0 ? anteriores.sum { it.valor } : 0

//        def max = contrato.monto * 0.1
//        max -= totalAnterior

        def json = new JsonBuilder(dets)
//        println json.toPrettyString()
        return [planilla: planilla, obra: obra, contrato: contrato, editable: editable, detalles: json, iva: iva, detallesSize: detalles.size()/*, max: max*/]
    }

    def list() {
        [detallePlanillaCostoAdminInstanceList: DetallePlanillaCostoAdmin.list(params), params: params]
    } //list

    def form_ajax() {
        def detallePlanillaCostoAdminInstance = new DetallePlanillaCostoAdmin(params)
        if (params.id) {
            detallePlanillaCostoAdminInstance = DetallePlanillaCostoAdmin.get(params.id)
            if (!detallePlanillaCostoAdminInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Detalle Planilla Costo Admin con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [detallePlanillaCostoAdminInstance: detallePlanillaCostoAdminInstance]
    } //form_ajax

    def save() {
        def detallePlanillaCostoAdminInstance
        if (params.id) {
            detallePlanillaCostoAdminInstance = DetallePlanillaCostoAdmin.get(params.id)
            if (!detallePlanillaCostoAdminInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Detalle Planilla Costo Admin con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            detallePlanillaCostoAdminInstance.properties = params
        }//es edit
        else {
            detallePlanillaCostoAdminInstance = new DetallePlanillaCostoAdmin(params)
        } //es create
        if (!detallePlanillaCostoAdminInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Detalle Planilla Costo Admin " + (detallePlanillaCostoAdminInstance.id ? detallePlanillaCostoAdminInstance.id : "") + "</h4>"

            str += "<ul>"
            detallePlanillaCostoAdminInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(action: 'list')
            return
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Detalle Planilla Costo Admin   " + detallePlanillaCostoAdminInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Detalle Planilla Costo Admin " + detallePlanillaCostoAdminInstance.id
        }
        redirect(action: 'list')
    } //save

    def addDetalleCosto() {
        def detalle = new DetallePlanillaCostoAdmin()
        if (params.id) {
            detalle = DetallePlanillaCostoAdmin.get(params.id)
        }
        detalle.properties = params
        if (detalle.save(flush: true)) {
            def planilla = PlanillaAdmin.get(params.planilla.id)
            updatePlanilla(planilla)
            render "OK_" + detalle.id
        } else {
            println "ERROR: " + detalle.errors
            render "NO_Ha ocurrido un error al guardar el rubro"
        }
    }

    def deleteDetalleCosto() {
        def detalle = DetallePlanillaCostoAdmin.get(params.id)
        def planilla = PlanillaAdmin.get(detalle.planillaId)
        detalle.delete(flush: true)
        updatePlanilla(planilla)
        render "OK"
    }

    private boolean updatePlanilla(planilla) {
        def detalles = DetallePlanillaCostoAdmin.findAllByPlanilla(planilla)
        def totalMonto = detalles.size() > 0 ? detalles.sum { it.valorIva * it.cantidad } : 0
        planilla.valor = totalMonto
        if (!planilla.save(flush: true)) {
            println "error al actualizar el valor de la planilla " + planilla.errors
            return false
        }
        return true
    }

    def show_ajax() {
        def detallePlanillaCostoAdminInstance = DetallePlanillaCostoAdmin.get(params.id)
        if (!detallePlanillaCostoAdminInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Detalle Planilla Costo Admin con id " + params.id
            redirect(action: "list")
            return
        }
        [detallePlanillaCostoAdminInstance: detallePlanillaCostoAdminInstance]
    } //show

    def delete() {
        def detallePlanillaCostoAdminInstance = DetallePlanillaCostoAdmin.get(params.id)
        if (!detallePlanillaCostoAdminInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Detalle Planilla Costo Admin con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            detallePlanillaCostoAdminInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Detalle Planilla Costo Admin " + detallePlanillaCostoAdminInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Detalle Planilla Costo Admin " + (detallePlanillaCostoAdminInstance.id ? detallePlanillaCostoAdminInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
