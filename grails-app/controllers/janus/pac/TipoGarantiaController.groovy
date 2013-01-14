package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class TipoGarantiaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoGarantiaInstanceList: TipoGarantia.list(params), params: params]
    } //list

    def form_ajax() {
        def tipoGarantiaInstance = new TipoGarantia(params)
        if (params.id) {
            tipoGarantiaInstance = TipoGarantia.get(params.id)
            if (!tipoGarantiaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Garantia con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoGarantiaInstance: tipoGarantiaInstance]
    } //form_ajax

    def save() {
        def tipoGarantiaInstance
        if (params.id) {
            tipoGarantiaInstance = TipoGarantia.get(params.id)
            if (!tipoGarantiaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Garantia con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoGarantiaInstance.properties = params
        }//es edit
        else {
            tipoGarantiaInstance = new TipoGarantia(params)
        } //es create
        if (!tipoGarantiaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Garantia " + (tipoGarantiaInstance.id ? tipoGarantiaInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoGarantiaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Garantia " + tipoGarantiaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Garantia " + tipoGarantiaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoGarantiaInstance = TipoGarantia.get(params.id)
        if (!tipoGarantiaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Garantia con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoGarantiaInstance: tipoGarantiaInstance]
    } //show

    def delete() {
        def tipoGarantiaInstance = TipoGarantia.get(params.id)
        if (!tipoGarantiaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Garantia con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoGarantiaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Tipo Garantia " + tipoGarantiaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Tipo Garantia " + (tipoGarantiaInstance.id ? tipoGarantiaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
