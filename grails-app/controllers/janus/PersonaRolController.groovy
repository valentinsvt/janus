package janus

import org.springframework.dao.DataIntegrityViolationException

class PersonaRolController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [personaRolInstanceList: PersonaRol.list(params), personaRolInstanceTotal: PersonaRol.count(), params: params]
    } //list

    def form_ajax() {
        def personaRolInstance = new PersonaRol(params)
        if (params.id) {
            personaRolInstance = PersonaRol.get(params.id)
            if (!personaRolInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró PersonaRol con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [personaRolInstance: personaRolInstance]
    } //form_ajax

    def save() {
        def personaRolInstance
        if (params.id) {
            personaRolInstance = PersonaRol.get(params.id)
            if (!personaRolInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró PersonaRol con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            personaRolInstance.properties = params
        }//es edit
        else {
            personaRolInstance = new PersonaRol(params)
        } //es create
        if (!personaRolInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar PersonaRol " + (personaRolInstance.id ? personaRolInstance.id : "") + "</h4>"

            str += "<ul>"
            personaRolInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamete PersonaRol " + personaRolInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamete PersonaRol " + personaRolInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def personaRolInstance = PersonaRol.get(params.id)
        if (!personaRolInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró PersonaRol con id " + params.id
            redirect(action: "list")
            return
        }
        [personaRolInstance: personaRolInstance]
    } //show

    def delete() {
        def personaRolInstance = PersonaRol.get(params.id)
        if (!personaRolInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró PersonaRol con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            personaRolInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamete PersonaRol " + personaRolInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar PersonaRol " + (personaRolInstance.id ? personaRolInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
