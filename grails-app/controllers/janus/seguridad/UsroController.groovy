package janus.seguridad

import org.springframework.dao.DataIntegrityViolationException

class UsroController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [usroInstanceList: Usro.list(params), params: params]
    } //list

    def form_ajax() {
        def usroInstance = new Usro(params)
        if(params.id) {
            usroInstance = Usro.get(params.id)
            if(!usroInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Usro con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [usroInstance: usroInstance]
    } //form_ajax

    def save() {
        def usroInstance
        if(params.id) {
            usroInstance = Usro.get(params.id)
            if(!usroInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Usro con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            usroInstance.properties = params
        }//es edit
        else {
            usroInstance = new Usro(params)
        } //es create
        if (!usroInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Usro " + (usroInstance.id ? usroInstance.id : "") + "</h4>"

            str += "<ul>"
            usroInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Usro " + usroInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Usro " + usroInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def usroInstance = Usro.get(params.id)
        if (!usroInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Usro con id " + params.id
            redirect(action: "list")
            return
        }
        [usroInstance: usroInstance]
    } //show

    def delete() {
        def usroInstance = Usro.get(params.id)
        if (!usroInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Usro con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            usroInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Usro " + usroInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Usro " + (usroInstance.id ? usroInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
