package janus

import org.springframework.dao.DataIntegrityViolationException

class ConcursoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [concursoInstanceList: Concurso.list(params), concursoInstanceTotal: Concurso.count(), params: params]
    } //list

    def form_ajax() {
        def concursoInstance = new Concurso(params)
        if (params.id) {
            concursoInstance = Concurso.get(params.id)
            if (!concursoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Concurso con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [concursoInstance: concursoInstance]
    } //form_ajax

    def save() {
        def concursoInstance
        if (params.id) {
            concursoInstance = Concurso.get(params.id)
            if (!concursoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Concurso con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            concursoInstance.properties = params
        }//es edit
        else {
            concursoInstance = new Concurso(params)
        } //es create
        if (!concursoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Concurso " + (concursoInstance.id ? concursoInstance.id : "") + "</h4>"

            str += "<ul>"
            concursoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamete Concurso " + concursoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamete Concurso " + concursoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def concursoInstance = Concurso.get(params.id)
        if (!concursoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Concurso con id " + params.id
            redirect(action: "list")
            return
        }
        [concursoInstance: concursoInstance]
    } //show

    def delete() {
        def concursoInstance = Concurso.get(params.id)
        if (!concursoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Concurso con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            concursoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamete Concurso " + concursoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Concurso " + (concursoInstance.id ? concursoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
