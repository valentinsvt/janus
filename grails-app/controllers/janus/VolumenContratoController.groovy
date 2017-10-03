package janus

import org.springframework.dao.DataIntegrityViolationException

class VolumenContratoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [volumenContratoInstanceList: VolumenContrato.list(params), params: params]
    } //list

    def form_ajax() {
        def volumenContratoInstance = new VolumenContrato(params)
        if(params.id) {
            volumenContratoInstance = VolumenContrato.get(params.id)
            if(!volumenContratoInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Volumen Contrato con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [volumenContratoInstance: volumenContratoInstance]
    } //form_ajax

    def save() {
        def volumenContratoInstance
        if(params.id) {
            volumenContratoInstance = VolumenContrato.get(params.id)
            if(!volumenContratoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Volumen Contrato con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            volumenContratoInstance.properties = params
        }//es edit
        else {
            volumenContratoInstance = new VolumenContrato(params)
        } //es create
        if (!volumenContratoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Volumen Contrato " + (volumenContratoInstance.id ? volumenContratoInstance.id : "") + "</h4>"

            str += "<ul>"
            volumenContratoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Volumen Contrato " + volumenContratoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Volumen Contrato " + volumenContratoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def volumenContratoInstance = VolumenContrato.get(params.id)
        if (!volumenContratoInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Volumen Contrato con id " + params.id
            redirect(action: "list")
            return
        }
        [volumenContratoInstance: volumenContratoInstance]
    } //show

    def delete() {
        def volumenContratoInstance = VolumenContrato.get(params.id)
        if (!volumenContratoInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Volumen Contrato con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            volumenContratoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Volumen Contrato " + volumenContratoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Volumen Contrato " + (volumenContratoInstance.id ? volumenContratoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
