package janus

import org.springframework.dao.DataIntegrityViolationException

class CantonController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [cantonInstanceList: Canton.list(params), cantonInstanceTotal: Canton.count(), params: params]
    } //list

    def form_ajax() {
        def cantonInstance = new Canton(params)
        if (params.id) {
            cantonInstance = Canton.get(params.id)
            if (!cantonInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Canton con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [cantonInstance: cantonInstance]
    } //form_ajax

    def save() {
        def cantonInstance
        if (params.id) {
            cantonInstance = Canton.get(params.id)
            if (!cantonInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Canton con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            cantonInstance.properties = params
        }//es edit
        else {
            cantonInstance = new Canton(params)
        } //es create
        if (!cantonInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Canton " + (cantonInstance.id ? cantonInstance.id : "") + "</h4>"

            str += "<ul>"
            cantonInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Canton " + cantonInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Canton " + cantonInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def cantonInstance = Canton.get(params.id)
        if (!cantonInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Canton con id " + params.id
            redirect(action: "list")
            return
        }
        [cantonInstance: cantonInstance]
    } //show

    def delete() {
        def cantonInstance = Canton.get(params.id)
        if (!cantonInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Canton con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            cantonInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Canton " + cantonInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Canton " + (cantonInstance.id ? cantonInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
