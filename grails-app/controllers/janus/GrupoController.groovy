package janus

import org.springframework.dao.DataIntegrityViolationException

class GrupoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [grupoInstanceList: Grupo.list(params), grupoInstanceTotal: Grupo.count(), params: params]
    } //list

    def form_ajax() {
        def grupoInstance = new Grupo(params)
        if (params.id) {
            grupoInstance = Grupo.get(params.id)
            if (!grupoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Grupo con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [grupoInstance: grupoInstance]
    } //form_ajax

    def save() {
        def grupoInstance
        if (params.id) {
            grupoInstance = Grupo.get(params.id)
            if (!grupoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Grupo con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            grupoInstance.properties = params
        }//es edit
        else {
            grupoInstance = new Grupo(params)
        } //es create
        if (!grupoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Grupo " + (grupoInstance.id ? grupoInstance.id : "") + "</h4>"

            str += "<ul>"
            grupoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Grupo " + grupoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Grupo " + grupoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def grupoInstance = Grupo.get(params.id)
        if (!grupoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Grupo con id " + params.id
            redirect(action: "list")
            return
        }
        [grupoInstance: grupoInstance]
    } //show

    def delete() {
        def grupoInstance = Grupo.get(params.id)
        if (!grupoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Grupo con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            grupoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Grupo " + grupoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Grupo " + (grupoInstance.id ? grupoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
