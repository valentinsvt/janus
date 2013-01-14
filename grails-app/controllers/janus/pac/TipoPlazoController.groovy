package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class TipoPlazoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoPlazoInstanceList: TipoPlazo.list(params), params: params]
    } //list

    def form_ajax() {
        def tipoPlazoInstance = new TipoPlazo(params)
        if (params.id) {
            tipoPlazoInstance = TipoPlazo.get(params.id)
            if (!tipoPlazoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Plazo con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoPlazoInstance: tipoPlazoInstance]
    } //form_ajax

    def save() {
        def tipoPlazoInstance
        if (params.id) {
            tipoPlazoInstance = TipoPlazo.get(params.id)
            if (!tipoPlazoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Plazo con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoPlazoInstance.properties = params
        }//es edit
        else {
            tipoPlazoInstance = new TipoPlazo(params)
        } //es create
        if (!tipoPlazoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Plazo " + (tipoPlazoInstance.id ? tipoPlazoInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoPlazoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Plazo " + tipoPlazoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Plazo " + tipoPlazoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoPlazoInstance = TipoPlazo.get(params.id)
        if (!tipoPlazoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Plazo con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoPlazoInstance: tipoPlazoInstance]
    } //show

    def delete() {
        def tipoPlazoInstance = TipoPlazo.get(params.id)
        if (!tipoPlazoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Plazo con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoPlazoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Tipo Plazo " + tipoPlazoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Tipo Plazo " + (tipoPlazoInstance.id ? tipoPlazoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
