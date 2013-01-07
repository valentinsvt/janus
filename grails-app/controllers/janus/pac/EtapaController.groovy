package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class EtapaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [etapaInstanceList: Etapa.list(params), params: params]
    } //list

    def form_ajax() {
        def etapaInstance = new Etapa(params)
        if(params.id) {
            etapaInstance = Etapa.get(params.id)
            if(!etapaInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Etapa con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [etapaInstance: etapaInstance]
    } //form_ajax

    def save() {
        def etapaInstance
        if(params.id) {
            etapaInstance = Etapa.get(params.id)
            if(!etapaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Etapa con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            etapaInstance.properties = params
        }//es edit
        else {
            etapaInstance = new Etapa(params)
        } //es create
        if (!etapaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Etapa " + (etapaInstance.id ? etapaInstance.id : "") + "</h4>"

            str += "<ul>"
            etapaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Etapa " + etapaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Etapa " + etapaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def etapaInstance = Etapa.get(params.id)
        if (!etapaInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Etapa con id " + params.id
            redirect(action: "list")
            return
        }
        [etapaInstance: etapaInstance]
    } //show

    def delete() {
        def etapaInstance = Etapa.get(params.id)
        if (!etapaInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Etapa con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            etapaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Etapa " + etapaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Etapa " + (etapaInstance.id ? etapaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
