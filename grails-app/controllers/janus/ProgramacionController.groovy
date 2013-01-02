package janus

import org.springframework.dao.DataIntegrityViolationException

class ProgramacionController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [programacionInstanceList: Programacion.list(params), programacionInstanceTotal: Programacion.count(), params: params]
    } //list

    def form_ajax() {
        def programacionInstance = new Programacion(params)
        if (params.id) {
            programacionInstance = Programacion.get(params.id)
            if (!programacionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Programacion con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [programacionInstance: programacionInstance]
    } //form_ajax

    def save() {
        def programacionInstance
        if (params.id) {
            programacionInstance = Programacion.get(params.id)
            if (!programacionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Programacion con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            programacionInstance.properties = params
        }//es edit
        else {
            programacionInstance = new Programacion(params)
        } //es create
        if (!programacionInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Programacion " + (programacionInstance.id ? programacionInstance.id : "") + "</h4>"

            str += "<ul>"
            programacionInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Programacion " + programacionInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Programacion " + programacionInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def programacionInstance = Programacion.get(params.id)
        if (!programacionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Programacion con id " + params.id
            redirect(action: "list")
            return
        }
        [programacionInstance: programacionInstance]
    } //show

    def delete() {
        def programacionInstance = Programacion.get(params.id)
        if (!programacionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Programacion con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            programacionInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Programacion " + programacionInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Programacion " + (programacionInstance.id ? programacionInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
