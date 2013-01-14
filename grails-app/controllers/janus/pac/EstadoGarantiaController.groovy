package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class EstadoGarantiaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [estadoGarantiaInstanceList: EstadoGarantia.list(params), params: params]
    } //list

    def form_ajax() {
        def estadoGarantiaInstance = new EstadoGarantia(params)
        if (params.id) {
            estadoGarantiaInstance = EstadoGarantia.get(params.id)
            if (!estadoGarantiaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Estado Garantia con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [estadoGarantiaInstance: estadoGarantiaInstance]
    } //form_ajax

    def save() {
        def estadoGarantiaInstance
        if (params.id) {
            estadoGarantiaInstance = EstadoGarantia.get(params.id)
            if (!estadoGarantiaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Estado Garantia con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            estadoGarantiaInstance.properties = params
        }//es edit
        else {
            estadoGarantiaInstance = new EstadoGarantia(params)
        } //es create
        if (!estadoGarantiaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Estado Garantia " + (estadoGarantiaInstance.id ? estadoGarantiaInstance.id : "") + "</h4>"

            str += "<ul>"
            estadoGarantiaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Estado Garantia " + estadoGarantiaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Estado Garantia " + estadoGarantiaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def estadoGarantiaInstance = EstadoGarantia.get(params.id)
        if (!estadoGarantiaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Estado Garantia con id " + params.id
            redirect(action: "list")
            return
        }
        [estadoGarantiaInstance: estadoGarantiaInstance]
    } //show

    def delete() {
        def estadoGarantiaInstance = EstadoGarantia.get(params.id)
        if (!estadoGarantiaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Estado Garantia con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            estadoGarantiaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Estado Garantia " + estadoGarantiaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Estado Garantia " + (estadoGarantiaInstance.id ? estadoGarantiaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
