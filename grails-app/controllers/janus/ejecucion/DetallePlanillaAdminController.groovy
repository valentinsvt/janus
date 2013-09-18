package janus.ejecucion

import org.springframework.dao.DataIntegrityViolationException

class DetallePlanillaAdminController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [detallePlanillaAdminInstanceList: DetallePlanillaAdmin.list(params), params: params]
    } //list

    def form_ajax() {
        def detallePlanillaAdminInstance = new DetallePlanillaAdmin(params)
        if (params.id) {
            detallePlanillaAdminInstance = DetallePlanillaAdmin.get(params.id)
            if (!detallePlanillaAdminInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Detalle Planilla Admin con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [detallePlanillaAdminInstance: detallePlanillaAdminInstance]
    } //form_ajax

    def save() {
        def detallePlanillaAdminInstance
        if (params.id) {
            detallePlanillaAdminInstance = DetallePlanillaAdmin.get(params.id)
            if (!detallePlanillaAdminInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Detalle Planilla Admin con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            detallePlanillaAdminInstance.properties = params
        }//es edit
        else {
            detallePlanillaAdminInstance = new DetallePlanillaAdmin(params)
        } //es create
        if (!detallePlanillaAdminInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Detalle Planilla Admin " + (detallePlanillaAdminInstance.id ? detallePlanillaAdminInstance.id : "") + "</h4>"

            str += "<ul>"
            detallePlanillaAdminInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Detalle Planilla Admin " + detallePlanillaAdminInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Detalle Planilla Admin " + detallePlanillaAdminInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def detallePlanillaAdminInstance = DetallePlanillaAdmin.get(params.id)
        if (!detallePlanillaAdminInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Detalle Planilla Admin con id " + params.id
            redirect(action: "list")
            return
        }
        [detallePlanillaAdminInstance: detallePlanillaAdminInstance]
    } //show

    def delete() {
        def detallePlanillaAdminInstance = DetallePlanillaAdmin.get(params.id)
        if (!detallePlanillaAdminInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Detalle Planilla Admin con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            detallePlanillaAdminInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Detalle Planilla Admin " + detallePlanillaAdminInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Detalle Planilla Admin " + (detallePlanillaAdminInstance.id ? detallePlanillaAdminInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
