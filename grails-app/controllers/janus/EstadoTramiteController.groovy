package janus

import org.springframework.dao.DataIntegrityViolationException

class EstadoTramiteController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [estadoTramiteInstanceList: EstadoTramite.list(params), params: params]
    } //list

    def form_ajax() {
        def estadoTramiteInstance = new EstadoTramite(params)
        if (params.id) {
            estadoTramiteInstance = EstadoTramite.get(params.id)
            if (!estadoTramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Estado Tramite con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [estadoTramiteInstance: estadoTramiteInstance]
    } //form_ajax

    def save() {
        def estadoTramiteInstance
        if (params.id) {
            estadoTramiteInstance = EstadoTramite.get(params.id)
            if (!estadoTramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Estado Tramite con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            estadoTramiteInstance.properties = params
        }//es edit
        else {
            estadoTramiteInstance = new EstadoTramite(params)
        } //es create
        if (!estadoTramiteInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Estado Tramite " + (estadoTramiteInstance.id ? estadoTramiteInstance.id : "") + "</h4>"

            str += "<ul>"
            estadoTramiteInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Estado Tramite " + estadoTramiteInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Estado Tramite " + estadoTramiteInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def estadoTramiteInstance = EstadoTramite.get(params.id)
        if (!estadoTramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Estado Tramite con id " + params.id
            redirect(action: "list")
            return
        }
        [estadoTramiteInstance: estadoTramiteInstance]
    } //show

    def delete() {
        def estadoTramiteInstance = EstadoTramite.get(params.id)
        if (!estadoTramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Estado Tramite con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            estadoTramiteInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Estado Tramite " + estadoTramiteInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Estado Tramite " + (estadoTramiteInstance.id ? estadoTramiteInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
