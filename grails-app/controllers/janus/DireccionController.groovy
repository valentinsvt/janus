package janus

import org.springframework.dao.DataIntegrityViolationException

class DireccionController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [direccionInstanceList: Direccion.list(params), params: params]
    } //list

    def form_ajax() {
        def direccionInstance = new Direccion(params)
        if(params.id) {
            direccionInstance = Direccion.get(params.id)
            if(!direccionInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Direccion con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [direccionInstance: direccionInstance]
    } //form_ajax

    def save() {
        def direccionInstance
        if(params.id) {
            direccionInstance = Direccion.get(params.id)
            if(!direccionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Direccion con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            direccionInstance.properties = params
        }//es edit
        else {
            direccionInstance = new Direccion(params)
        } //es create
        if (!direccionInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Direccion " + (direccionInstance.id ? direccionInstance.id : "") + "</h4>"

            str += "<ul>"
            direccionInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Direccion " + direccionInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Direccion " + direccionInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def direccionInstance = Direccion.get(params.id)
        if (!direccionInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Direccion con id " + params.id
            redirect(action: "list")
            return
        }
        [direccionInstance: direccionInstance]
    } //show

    def delete() {
        def direccionInstance = Direccion.get(params.id)
        if (!direccionInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Direccion con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            direccionInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Direccion " + direccionInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Direccion " + (direccionInstance.id ? direccionInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
