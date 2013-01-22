package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class CronogramaContratoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {


    } //index

    def list() {
        [cronogramaContratoInstanceList: CronogramaContrato.list(params), params: params]
    } //list

    def form_ajax() {
        def cronogramaContratoInstance = new CronogramaContrato(params)
        if (params.id) {
            cronogramaContratoInstance = CronogramaContrato.get(params.id)
            if (!cronogramaContratoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Cronograma Contrato con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [cronogramaContratoInstance: cronogramaContratoInstance]
    } //form_ajax

    def save() {
        def cronogramaContratoInstance
        if (params.id) {
            cronogramaContratoInstance = CronogramaContrato.get(params.id)
            if (!cronogramaContratoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Cronograma Contrato con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            cronogramaContratoInstance.properties = params
        }//es edit
        else {
            cronogramaContratoInstance = new CronogramaContrato(params)
        } //es create
        if (!cronogramaContratoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Cronograma Contrato " + (cronogramaContratoInstance.id ? cronogramaContratoInstance.id : "") + "</h4>"

            str += "<ul>"
            cronogramaContratoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Cronograma Contrato " + cronogramaContratoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Cronograma Contrato " + cronogramaContratoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def cronogramaContratoInstance = CronogramaContrato.get(params.id)
        if (!cronogramaContratoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Cronograma Contrato con id " + params.id
            redirect(action: "list")
            return
        }
        [cronogramaContratoInstance: cronogramaContratoInstance]
    } //show

    def delete() {
        def cronogramaContratoInstance = CronogramaContrato.get(params.id)
        if (!cronogramaContratoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Cronograma Contrato con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            cronogramaContratoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Cronograma Contrato " + cronogramaContratoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Cronograma Contrato " + (cronogramaContratoInstance.id ? cronogramaContratoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
