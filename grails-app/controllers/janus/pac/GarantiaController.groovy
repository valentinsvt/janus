package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class GarantiaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [garantiaInstanceList: Garantia.list(params), params: params]
    } //list

    def form_ajax() {
        def garantiaInstance = new Garantia(params)
        if (params.id) {
            garantiaInstance = Garantia.get(params.id)
            if (!garantiaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Garantia con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [garantiaInstance: garantiaInstance]
    } //form_ajax

    def save() {
        def garantiaInstance
        if (params.id) {
            garantiaInstance = Garantia.get(params.id)
            if (!garantiaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Garantia con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            garantiaInstance.properties = params
        }//es edit
        else {
            garantiaInstance = new Garantia(params)
        } //es create
        if (!garantiaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Garantia " + (garantiaInstance.id ? garantiaInstance.id : "") + "</h4>"

            str += "<ul>"
            garantiaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Garantia " + garantiaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Garantia " + garantiaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def garantiaInstance = Garantia.get(params.id)
        if (!garantiaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Garantia con id " + params.id
            redirect(action: "list")
            return
        }
        [garantiaInstance: garantiaInstance]
    } //show

    def delete() {
        def garantiaInstance = Garantia.get(params.id)
        if (!garantiaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Garantia con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            garantiaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Garantia " + garantiaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Garantia " + (garantiaInstance.id ? garantiaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
