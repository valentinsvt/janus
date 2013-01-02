package janus

import org.springframework.dao.DataIntegrityViolationException

class RolTramiteController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [rolTramiteInstanceList: RolTramite.list(params), rolTramiteInstanceTotal: RolTramite.count(), params: params]
    } //list

    def form_ajax() {
        def rolTramiteInstance = new RolTramite(params)
        if (params.id) {
            rolTramiteInstance = RolTramite.get(params.id)
            if (!rolTramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró RolTramite con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [rolTramiteInstance: rolTramiteInstance]
    } //form_ajax

    def save() {
        def rolTramiteInstance
        if (params.id) {
            rolTramiteInstance = RolTramite.get(params.id)
            if (!rolTramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró RolTramite con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            rolTramiteInstance.properties = params
        }//es edit
        else {
            rolTramiteInstance = new RolTramite(params)
        } //es create
        if (!rolTramiteInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar RolTramite " + (rolTramiteInstance.id ? rolTramiteInstance.id : "") + "</h4>"

            str += "<ul>"
            rolTramiteInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente RolTramite " + rolTramiteInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente RolTramite " + rolTramiteInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def rolTramiteInstance = RolTramite.get(params.id)
        if (!rolTramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró RolTramite con id " + params.id
            redirect(action: "list")
            return
        }
        [rolTramiteInstance: rolTramiteInstance]
    } //show

    def delete() {
        def rolTramiteInstance = RolTramite.get(params.id)
        if (!rolTramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró RolTramite con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            rolTramiteInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente RolTramite " + rolTramiteInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar RolTramite " + (rolTramiteInstance.id ? rolTramiteInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
