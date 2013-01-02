package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class AnioController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [anioInstanceList: Anio.list(params), params: params]
    } //list

    def form_ajax() {
        def anioInstance = new Anio(params)
        if (params.id) {
            anioInstance = Anio.get(params.id)
            if (!anioInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Anio con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [anioInstance: anioInstance]
    } //form_ajax

    def save() {
        def anioInstance
        if (params.id) {
            anioInstance = Anio.get(params.id)
            if (!anioInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Anio con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            anioInstance.properties = params
        }//es edit
        else {
            anioInstance = new Anio(params)
        } //es create
        if (!anioInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Anio " + (anioInstance.id ? anioInstance.id : "") + "</h4>"

            str += "<ul>"
            anioInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Anio " + anioInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Anio " + anioInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def anioInstance = Anio.get(params.id)
        if (!anioInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Anio con id " + params.id
            redirect(action: "list")
            return
        }
        [anioInstance: anioInstance]
    } //show

    def delete() {
        def anioInstance = Anio.get(params.id)
        if (!anioInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Anio con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            anioInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Anio " + anioInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Anio " + (anioInstance.id ? anioInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
