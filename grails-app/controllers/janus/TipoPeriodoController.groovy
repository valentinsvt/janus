package janus

import org.springframework.dao.DataIntegrityViolationException

class TipoPeriodoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoPeriodoInstanceList: TipoPeriodo.list(params), params: params]
    } //list

    def form_ajax() {
        def tipoPeriodoInstance = new TipoPeriodo(params)
        if (params.id) {
            tipoPeriodoInstance = TipoPeriodo.get(params.id)
            if (!tipoPeriodoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Periodo con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoPeriodoInstance: tipoPeriodoInstance]
    } //form_ajax

    def save() {
        def tipoPeriodoInstance
        if (params.id) {
            tipoPeriodoInstance = TipoPeriodo.get(params.id)
            if (!tipoPeriodoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Periodo con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoPeriodoInstance.properties = params
        }//es edit
        else {
            tipoPeriodoInstance = new TipoPeriodo(params)
        } //es create
        if (!tipoPeriodoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Periodo " + (tipoPeriodoInstance.id ? tipoPeriodoInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoPeriodoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Periodo " + tipoPeriodoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Periodo " + tipoPeriodoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoPeriodoInstance = TipoPeriodo.get(params.id)
        if (!tipoPeriodoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Periodo con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoPeriodoInstance: tipoPeriodoInstance]
    } //show

    def delete() {
        def tipoPeriodoInstance = TipoPeriodo.get(params.id)
        if (!tipoPeriodoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Periodo con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoPeriodoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Tipo Periodo " + tipoPeriodoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Tipo Periodo " + (tipoPeriodoInstance.id ? tipoPeriodoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
