package janus

import org.springframework.dao.DataIntegrityViolationException

class DepartamentoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 15, 100)
        [departamentoInstanceList: Departamento.list(params), departamentoInstanceTotal: Departamento.count(), params: params]
    } //list

    def form_ajax() {
        def departamentoInstance = new Departamento(params)
        if (params.id) {
            departamentoInstance = Departamento.get(params.id)
            if (!departamentoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Departamento con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [departamentoInstance: departamentoInstance]
    } //form_ajax

    def save() {
        def departamentoInstance
        if (params.id) {
            departamentoInstance = Departamento.get(params.id)
            if (!departamentoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Departamento con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            departamentoInstance.properties = params
        }//es edit
        else {
            departamentoInstance = new Departamento(params)
        } //es create
        if (!departamentoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Departamento " + (departamentoInstance.id ? departamentoInstance.id : "") + "</h4>"

            str += "<ul>"
            departamentoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Departamento " + departamentoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Departamento " + departamentoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def departamentoInstance = Departamento.get(params.id)
        if (!departamentoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Departamento con id " + params.id
            redirect(action: "list")
            return
        }
        [departamentoInstance: departamentoInstance]
    } //show

    def delete() {
        def departamentoInstance = Departamento.get(params.id)
        if (!departamentoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Departamento con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            departamentoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Departamento " + departamentoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Departamento " + (departamentoInstance.id ? departamentoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
