package janus

import org.springframework.dao.DataIntegrityViolationException

class CronogramaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [cronogramaInstanceList: Cronograma.list(params), cronogramaInstanceTotal: Cronograma.count(), params: params]
    } //list

    def form_ajax() {
        def cronogramaInstance = new Cronograma(params)
        if (params.id) {
            cronogramaInstance = Cronograma.get(params.id)
            if (!cronogramaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Cronograma con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [cronogramaInstance: cronogramaInstance]
    } //form_ajax

    def save() {
        def cronogramaInstance
        if (params.id) {
            cronogramaInstance = Cronograma.get(params.id)
            if (!cronogramaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Cronograma con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            cronogramaInstance.properties = params
        }//es edit
        else {
            cronogramaInstance = new Cronograma(params)
        } //es create
        if (!cronogramaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Cronograma " + (cronogramaInstance.id ? cronogramaInstance.id : "") + "</h4>"

            str += "<ul>"
            cronogramaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamete Cronograma " + cronogramaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamete Cronograma " + cronogramaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def cronogramaInstance = Cronograma.get(params.id)
        if (!cronogramaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Cronograma con id " + params.id
            redirect(action: "list")
            return
        }
        [cronogramaInstance: cronogramaInstance]
    } //show

    def delete() {
        def cronogramaInstance = Cronograma.get(params.id)
        if (!cronogramaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Cronograma con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            cronogramaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamete Cronograma " + cronogramaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Cronograma " + (cronogramaInstance.id ? cronogramaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
