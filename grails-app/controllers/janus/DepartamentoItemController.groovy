package janus

import org.springframework.dao.DataIntegrityViolationException

class DepartamentoItemController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [departamentoItemInstanceList: DepartamentoItem.list(params), departamentoItemInstanceTotal: DepartamentoItem.count(), params: params]
    } //list

    def form_ajax() {
        def departamentoItemInstance = new DepartamentoItem(params)
        if (params.id) {
            departamentoItemInstance = DepartamentoItem.get(params.id)
            if (!departamentoItemInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró DepartamentoItem con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [departamentoItemInstance: departamentoItemInstance]
    } //form_ajax

    def save() {
        def departamentoItemInstance
        if (params.id) {
            departamentoItemInstance = DepartamentoItem.get(params.id)
            if (!departamentoItemInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró DepartamentoItem con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            departamentoItemInstance.properties = params
        }//es edit
        else {
            departamentoItemInstance = new DepartamentoItem(params)
        } //es create
        if (!departamentoItemInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar DepartamentoItem " + (departamentoItemInstance.id ? departamentoItemInstance.id : "") + "</h4>"

            str += "<ul>"
            departamentoItemInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente DepartamentoItem " + departamentoItemInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente DepartamentoItem " + departamentoItemInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def departamentoItemInstance = DepartamentoItem.get(params.id)
        if (!departamentoItemInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró DepartamentoItem con id " + params.id
            redirect(action: "list")
            return
        }
        [departamentoItemInstance: departamentoItemInstance]
    } //show

    def delete() {
        def departamentoItemInstance = DepartamentoItem.get(params.id)
        if (!departamentoItemInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró DepartamentoItem con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            departamentoItemInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente DepartamentoItem " + departamentoItemInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar DepartamentoItem " + (departamentoItemInstance.id ? departamentoItemInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
