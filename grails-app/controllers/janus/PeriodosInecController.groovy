package janus

import org.springframework.dao.DataIntegrityViolationException

class PeriodosInecController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [periodosInecInstanceList: PeriodosInec.list(params), periodosInecInstanceTotal: PeriodosInec.count(), params: params]
    } //list

    def form_ajax() {
        def periodosInecInstance = new PeriodosInec(params)
        if (params.id) {
            periodosInecInstance = PeriodosInec.get(params.id)
            if (!periodosInecInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró PeriodosInec con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [periodosInecInstance: periodosInecInstance]
    } //form_ajax

    def save() {
        def periodosInecInstance
        if (params.id) {
            periodosInecInstance = PeriodosInec.get(params.id)
            if (!periodosInecInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró PeriodosInec con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            periodosInecInstance.properties = params
        }//es edit
        else {
            periodosInecInstance = new PeriodosInec(params)
        } //es create
        if (!periodosInecInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar PeriodosInec " + (periodosInecInstance.id ? periodosInecInstance.id : "") + "</h4>"

            str += "<ul>"
            periodosInecInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente PeriodosInec " + periodosInecInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente PeriodosInec " + periodosInecInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def periodosInecInstance = PeriodosInec.get(params.id)
        if (!periodosInecInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró PeriodosInec con id " + params.id
            redirect(action: "list")
            return
        }
        [periodosInecInstance: periodosInecInstance]
    } //show

    def delete() {
        def periodosInecInstance = PeriodosInec.get(params.id)
        if (!periodosInecInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró PeriodosInec con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            periodosInecInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente PeriodosInec " + periodosInecInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar PeriodosInec " + (periodosInecInstance.id ? periodosInecInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
