package janus

import org.springframework.dao.DataIntegrityViolationException

class IndiceController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [indiceInstanceList: Indice.list(params), indiceInstanceTotal: Indice.count(), params: params]
    } //list

    def form_ajax() {
        def indiceInstance = new Indice(params)
        if (params.id) {
            indiceInstance = Indice.get(params.id)
            if (!indiceInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Indice con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [indiceInstance: indiceInstance]
    } //form_ajax

    def save() {
        def indiceInstance
        if (params.id) {
            indiceInstance = Indice.get(params.id)
            if (!indiceInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Indice con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            indiceInstance.properties = params
        }//es edit
        else {
            indiceInstance = new Indice(params)
        } //es create
        if (!indiceInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Indice " + (indiceInstance.id ? indiceInstance.id : "") + "</h4>"

            str += "<ul>"
            indiceInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamete Indice " + indiceInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamete Indice " + indiceInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def indiceInstance = Indice.get(params.id)
        if (!indiceInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Indice con id " + params.id
            redirect(action: "list")
            return
        }
        [indiceInstance: indiceInstance]
    } //show

    def delete() {
        def indiceInstance = Indice.get(params.id)
        if (!indiceInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Indice con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            indiceInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamete Indice " + indiceInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Indice " + (indiceInstance.id ? indiceInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
