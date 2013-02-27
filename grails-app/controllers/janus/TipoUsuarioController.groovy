package janus

import org.springframework.dao.DataIntegrityViolationException

class TipoUsuarioController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoUsuarioInstanceList: TipoUsuario.list(params), params: params]
    } //list

    def form_ajax() {
        def tipoUsuarioInstance = new TipoUsuario(params)
        if (params.id) {
            tipoUsuarioInstance = TipoUsuario.get(params.id)
            if (!tipoUsuarioInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Usuario con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoUsuarioInstance: tipoUsuarioInstance]
    } //form_ajax

    def save() {
        def tipoUsuarioInstance
        if (params.id) {
            tipoUsuarioInstance = TipoUsuario.get(params.id)
            if (!tipoUsuarioInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Usuario con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoUsuarioInstance.properties = params
        }//es edit
        else {
            tipoUsuarioInstance = new TipoUsuario(params)
        } //es create
        if (!tipoUsuarioInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Usuario " + (tipoUsuarioInstance.id ? tipoUsuarioInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoUsuarioInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Usuario " + tipoUsuarioInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Usuario " + tipoUsuarioInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoUsuarioInstance = TipoUsuario.get(params.id)
        if (!tipoUsuarioInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Usuario con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoUsuarioInstance: tipoUsuarioInstance]
    } //show

    def delete() {
        def tipoUsuarioInstance = TipoUsuario.get(params.id)
        if (!tipoUsuarioInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Usuario con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoUsuarioInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Tipo Usuario " + tipoUsuarioInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Tipo Usuario " + (tipoUsuarioInstance.id ? tipoUsuarioInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
