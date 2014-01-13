package janus.ejecucion

import org.springframework.dao.DataIntegrityViolationException

class TipoMultaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoMultaInstanceList: TipoMulta.list(), params: params]

    } //list

    def form_ajax() {
        def tipoMultaInstance = new TipoMulta(params)
        if(params.id) {
            tipoMultaInstance = TipoMulta.get(params.id)
            if(!tipoMultaInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Tipo Multa con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoMultaInstance: tipoMultaInstance]
    } //form_ajax

    def save() {
        def tipoMultaInstance
        if(params.id) {
            tipoMultaInstance = TipoMulta.get(params.id)
            if(!tipoMultaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Multa con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoMultaInstance.properties = params
        }//es edit
        else {
            tipoMultaInstance = new TipoMulta(params)
        } //es create
        if (!tipoMultaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Multa " + (tipoMultaInstance.id ? tipoMultaInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoMultaInstance.errors.allErrors.each { err ->
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

        if(params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Tipo Multa " + tipoMultaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Multa " + tipoMultaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoMultaInstance = TipoMulta.get(params.id)
        if (!tipoMultaInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Tipo Multa con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoMultaInstance: tipoMultaInstance]
    } //show

    def delete() {
        def tipoMultaInstance = TipoMulta.get(params.id)
        if (!tipoMultaInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Tipo Multa con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoMultaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Tipo Multa " + tipoMultaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Tipo Multa " + (tipoMultaInstance.id ? tipoMultaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
