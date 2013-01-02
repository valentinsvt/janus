package janus

import org.springframework.dao.DataIntegrityViolationException

class ClaveController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [claveInstanceList: Clave.list(params), claveInstanceTotal: Clave.count(), params: params]
    } //list

    def form_ajax() {
        def claveInstance = new Clave(params)
        if (params.id) {
            claveInstance = Clave.get(params.id)
            if (!claveInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Clave con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [claveInstance: claveInstance]
    } //form_ajax

    def save() {
        def claveInstance
        if (params.id) {
            claveInstance = Clave.get(params.id)
            if (!claveInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Clave con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            claveInstance.properties = params
        }//es edit
        else {
            claveInstance = new Clave(params)
        } //es create
        if (!claveInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Clave " + (claveInstance.id ? claveInstance.id : "") + "</h4>"

            str += "<ul>"
            claveInstance.errors.allErrors.each { err ->
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

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Clave " + claveInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Clave " + claveInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def claveInstance = Clave.get(params.id)
        if (!claveInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Clave con id " + params.id
            redirect(action: "list")
            return
        }
        [claveInstance: claveInstance]
    } //show

    def delete() {
        def claveInstance = Clave.get(params.id)
        if (!claveInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Clave con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            claveInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Clave " + claveInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Clave " + (claveInstance.id ? claveInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
