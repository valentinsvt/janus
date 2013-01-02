package janus

import org.springframework.dao.DataIntegrityViolationException

class ComunidadController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [comunidadInstanceList: Comunidad.list(params), comunidadInstanceTotal: Comunidad.count(), params: params]
    } //list

    def form_ajax() {
        def comunidadInstance = new Comunidad(params)
        if (params.id) {
            comunidadInstance = Comunidad.get(params.id)
            if (!comunidadInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Comunidad con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [comunidadInstance: comunidadInstance]
    } //form_ajax

    def save() {
        def comunidadInstance
        if (params.id) {
            comunidadInstance = Comunidad.get(params.id)
            if (!comunidadInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Comunidad con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            comunidadInstance.properties = params
        }//es edit
        else {
            comunidadInstance = new Comunidad(params)
        } //es create
        if (!comunidadInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Comunidad " + (comunidadInstance.id ? comunidadInstance.id : "") + "</h4>"

            str += "<ul>"
            comunidadInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Comunidad " + comunidadInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Comunidad " + comunidadInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def comunidadInstance = Comunidad.get(params.id)
        if (!comunidadInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Comunidad con id " + params.id
            redirect(action: "list")
            return
        }
        [comunidadInstance: comunidadInstance]
    } //show

    def delete() {
        def comunidadInstance = Comunidad.get(params.id)
        if (!comunidadInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Comunidad con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            comunidadInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Comunidad " + comunidadInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Comunidad " + (comunidadInstance.id ? comunidadInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
