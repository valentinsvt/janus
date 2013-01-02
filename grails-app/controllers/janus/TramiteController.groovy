package janus

import org.springframework.dao.DataIntegrityViolationException

class TramiteController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [tramiteInstanceList: Tramite.list(params), tramiteInstanceTotal: Tramite.count(), params: params]
    } //list

    def form_ajax() {
        def tramiteInstance = new Tramite(params)
        if (params.id) {
            tramiteInstance = Tramite.get(params.id)
            if (!tramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tramite con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tramiteInstance: tramiteInstance]
    } //form_ajax

    def save() {
        def tramiteInstance
        if (params.id) {
            tramiteInstance = Tramite.get(params.id)
            if (!tramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tramite con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tramiteInstance.properties = params
        }//es edit
        else {
            tramiteInstance = new Tramite(params)
        } //es create
        if (!tramiteInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tramite " + (tramiteInstance.id ? tramiteInstance.id : "") + "</h4>"

            str += "<ul>"
            tramiteInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
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
            flash.message = "Se ha actualizado correctamente Tramite " + tramiteInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tramite " + tramiteInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tramiteInstance = Tramite.get(params.id)
        if (!tramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tramite con id " + params.id
            redirect(action: "list")
            return
        }
        [tramiteInstance: tramiteInstance]
    } //show

    def delete() {
        def tramiteInstance = Tramite.get(params.id)
        if (!tramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tramite con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tramiteInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Tramite " + tramiteInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Tramite " + (tramiteInstance.id ? tramiteInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
