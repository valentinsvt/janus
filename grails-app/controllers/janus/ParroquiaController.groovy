package janus

import org.springframework.dao.DataIntegrityViolationException

class ParroquiaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [parroquiaInstanceList: Parroquia.list(params), parroquiaInstanceTotal: Parroquia.count(), params: params]
    } //list

    def form_ajax() {
        def parroquiaInstance = new Parroquia(params)
        if (params.id) {
            parroquiaInstance = Parroquia.get(params.id)
            if (!parroquiaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Parroquia con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [parroquiaInstance: parroquiaInstance]
    } //form_ajax

    def save() {
        def parroquiaInstance
        if (params.id) {
            parroquiaInstance = Parroquia.get(params.id)
            if (!parroquiaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Parroquia con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            parroquiaInstance.properties = params
        }//es edit
        else {
            parroquiaInstance = new Parroquia(params)
        } //es create
        if (!parroquiaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Parroquia " + (parroquiaInstance.id ? parroquiaInstance.id : "") + "</h4>"

            str += "<ul>"
            parroquiaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Parroquia " + parroquiaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Parroquia " + parroquiaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def parroquiaInstance = Parroquia.get(params.id)
        if (!parroquiaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Parroquia con id " + params.id
            redirect(action: "list")
            return
        }
        [parroquiaInstance: parroquiaInstance]
    } //show

    def delete() {
        def parroquiaInstance = Parroquia.get(params.id)
        if (!parroquiaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Parroquia con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            parroquiaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Parroquia " + parroquiaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Parroquia " + (parroquiaInstance.id ? parroquiaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
