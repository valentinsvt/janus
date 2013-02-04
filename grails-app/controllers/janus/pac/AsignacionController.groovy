package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class AsignacionController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [asignacionInstanceList: Asignacion.list(params), params: params]
    } //list

    def form_ajax() {
        def asignacionInstance = new Asignacion(params)
        if (params.id) {
            asignacionInstance = Asignacion.get(params.id)
            if (!asignacionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Asignacion con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [asignacionInstance: asignacionInstance]
    } //form_ajax

    def save() {
        def asignacionInstance
        if (params.id) {
            asignacionInstance = Asignacion.get(params.id)
            if (!asignacionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Asignacion con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            asignacionInstance.properties = params
        }//es edit
        else {
            asignacionInstance = new Asignacion(params)
        } //es create
        if (!asignacionInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Asignacion " + (asignacionInstance.id ? asignacionInstance.id : "") + "</h4>"

            str += "<ul>"
            asignacionInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Asignacion " + asignacionInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Asignacion " + asignacionInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def asignacionInstance = Asignacion.get(params.id)
        if (!asignacionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Asignacion con id " + params.id
            redirect(action: "list")
            return
        }
        [asignacionInstance: asignacionInstance]
    } //show

    def delete() {
        def asignacionInstance = Asignacion.get(params.id)
        if (!asignacionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Asignacion con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            asignacionInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Asignacion " + asignacionInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Asignacion " + (asignacionInstance.id ? asignacionInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
