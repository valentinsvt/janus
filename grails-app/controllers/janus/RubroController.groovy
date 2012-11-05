package janus

import org.springframework.dao.DataIntegrityViolationException

class RubroController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [rubroInstanceList: Rubro.list(params), rubroInstanceTotal: Rubro.count(), params: params]
    } //list

    def form_ajax() {
        def rubroInstance = new Rubro(params)
        if (params.id) {
            rubroInstance = Rubro.get(params.id)
            if (!rubroInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Rubro con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [rubroInstance: rubroInstance]
    } //form_ajax

    def save() {
        def rubroInstance
        if (params.id) {
            rubroInstance = Rubro.get(params.id)
            if (!rubroInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Rubro con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            rubroInstance.properties = params
        }//es edit
        else {
            rubroInstance = new Rubro(params)
        } //es create
        if (!rubroInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Rubro " + (rubroInstance.id ? rubroInstance.id : "") + "</h4>"

            str += "<ul>"
            rubroInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamete Rubro " + rubroInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamete Rubro " + rubroInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def rubroInstance = Rubro.get(params.id)
        if (!rubroInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Rubro con id " + params.id
            redirect(action: "list")
            return
        }
        [rubroInstance: rubroInstance]
    } //show

    def delete() {
        def rubroInstance = Rubro.get(params.id)
        if (!rubroInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Rubro con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            rubroInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamete Rubro " + rubroInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Rubro " + (rubroInstance.id ? rubroInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
