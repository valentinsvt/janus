package janus

import org.springframework.dao.DataIntegrityViolationException

class BaseController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [baseInstanceList: Base.list(params), baseInstanceTotal: Base.count(), params: params]
    } //list

    def form_ajax() {
        def baseInstance = new Base(params)
        if (params.id) {
            baseInstance = Base.get(params.id)
            if (!baseInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Base con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [baseInstance: baseInstance]
    } //form_ajax

    def save() {
        def baseInstance
        if (params.id) {
            baseInstance = Base.get(params.id)
            if (!baseInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Base con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            baseInstance.properties = params
        }//es edit
        else {
            baseInstance = new Base(params)
        } //es create
        if (!baseInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Base " + (baseInstance.id ? baseInstance.id : "") + "</h4>"

            str += "<ul>"
            baseInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Base " + baseInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Base " + baseInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def baseInstance = Base.get(params.id)
        if (!baseInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Base con id " + params.id
            redirect(action: "list")
            return
        }
        [baseInstance: baseInstance]
    } //show

    def delete() {
        def baseInstance = Base.get(params.id)
        if (!baseInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Base con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            baseInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Base " + baseInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Base " + (baseInstance.id ? baseInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
