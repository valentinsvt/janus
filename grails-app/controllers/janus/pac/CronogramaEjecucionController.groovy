package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class CronogramaEjecucionController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [cronogramaEjecucionInstanceList: CronogramaEjecucion.list(params), params: params]
    } //list

    def form_ajax() {
        def cronogramaEjecucionInstance = new CronogramaEjecucion(params)
        if (params.id) {
            cronogramaEjecucionInstance = CronogramaEjecucion.get(params.id)
            if (!cronogramaEjecucionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Cronograma Ejecucion con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [cronogramaEjecucionInstance: cronogramaEjecucionInstance]
    } //form_ajax

    def save() {
        def cronogramaEjecucionInstance
        if (params.id) {
            cronogramaEjecucionInstance = CronogramaEjecucion.get(params.id)
            if (!cronogramaEjecucionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Cronograma Ejecucion con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            cronogramaEjecucionInstance.properties = params
        }//es edit
        else {
            cronogramaEjecucionInstance = new CronogramaEjecucion(params)
        } //es create
        if (!cronogramaEjecucionInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Cronograma Ejecucion " + (cronogramaEjecucionInstance.id ? cronogramaEjecucionInstance.id : "") + "</h4>"

            str += "<ul>"
            cronogramaEjecucionInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
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
            flash.message = "Se ha actualizado correctamente Cronograma Ejecucion " + cronogramaEjecucionInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Cronograma Ejecucion " + cronogramaEjecucionInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def cronogramaEjecucionInstance = CronogramaEjecucion.get(params.id)
        if (!cronogramaEjecucionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Cronograma Ejecucion con id " + params.id
            redirect(action: "list")
            return
        }
        [cronogramaEjecucionInstance: cronogramaEjecucionInstance]
    } //show

    def delete() {
        def cronogramaEjecucionInstance = CronogramaEjecucion.get(params.id)
        if (!cronogramaEjecucionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Cronograma Ejecucion con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            cronogramaEjecucionInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Cronograma Ejecucion " + cronogramaEjecucionInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Cronograma Ejecucion " + (cronogramaEjecucionInstance.id ? cronogramaEjecucionInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
