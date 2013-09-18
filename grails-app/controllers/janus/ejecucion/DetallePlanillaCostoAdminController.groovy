package janus.ejecucion

import org.springframework.dao.DataIntegrityViolationException

class DetallePlanillaCostoAdminController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

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
            flash.message = "Se ha actualizado correctamente Detalle Planilla Costo Admin " + detallePlanillaCostoAdminInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Detalle Planilla Costo Admin " + detallePlanillaCostoAdminInstance.id
        }
        redirect(action: 'list')
    } //save

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
