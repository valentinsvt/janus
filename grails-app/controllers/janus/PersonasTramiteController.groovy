package janus

import org.springframework.dao.DataIntegrityViolationException

class PersonasTramiteController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [personasTramiteInstanceList: PersonasTramite.list(params), personasTramiteInstanceTotal: PersonasTramite.count(), params: params]
    } //list

    def form_ajax() {
        def personasTramiteInstance = new PersonasTramite(params)
        if (params.id) {
            personasTramiteInstance = PersonasTramite.get(params.id)
            if (!personasTramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró PersonasTramite con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [personasTramiteInstance: personasTramiteInstance]
    } //form_ajax

    def save() {
        def personasTramiteInstance
        if (params.id) {
            personasTramiteInstance = PersonasTramite.get(params.id)
            if (!personasTramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró PersonasTramite con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            personasTramiteInstance.properties = params
        }//es edit
        else {
            personasTramiteInstance = new PersonasTramite(params)
        } //es create
        if (!personasTramiteInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar PersonasTramite " + (personasTramiteInstance.id ? personasTramiteInstance.id : "") + "</h4>"

            str += "<ul>"
            personasTramiteInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente PersonasTramite " + personasTramiteInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente PersonasTramite " + personasTramiteInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def personasTramiteInstance = PersonasTramite.get(params.id)
        if (!personasTramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró PersonasTramite con id " + params.id
            redirect(action: "list")
            return
        }
        [personasTramiteInstance: personasTramiteInstance]
    } //show

    def delete() {
        def personasTramiteInstance = PersonasTramite.get(params.id)
        if (!personasTramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró PersonasTramite con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            personasTramiteInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente PersonasTramite " + personasTramiteInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar PersonasTramite " + (personasTramiteInstance.id ? personasTramiteInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
