package janus

import org.springframework.dao.DataIntegrityViolationException

class TipoIndiceController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoIndiceInstanceList: TipoIndice.list(params), params: params]
    } //list

    def form_ajax() {
        def tipoIndiceInstance = new TipoIndice(params)
        if (params.id) {
            tipoIndiceInstance = TipoIndice.get(params.id)
            if (!tipoIndiceInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Indice con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoIndiceInstance: tipoIndiceInstance]
    } //form_ajax

    def save() {
        def tipoIndiceInstance
        if (params.id) {
            tipoIndiceInstance = TipoIndice.get(params.id)
            if (!tipoIndiceInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Indice con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoIndiceInstance.properties = params
        }//es edit
        else {
            tipoIndiceInstance = new TipoIndice(params)
        } //es create
        if (!tipoIndiceInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Indice " + (tipoIndiceInstance.id ? tipoIndiceInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoIndiceInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Indice " + tipoIndiceInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Indice " + tipoIndiceInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoIndiceInstance = TipoIndice.get(params.id)
        if (!tipoIndiceInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Indice con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoIndiceInstance: tipoIndiceInstance]
    } //show

    def delete() {
        def tipoIndiceInstance = TipoIndice.get(params.id)
        if (!tipoIndiceInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Indice con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoIndiceInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Tipo Indice " + tipoIndiceInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Tipo Indice " + (tipoIndiceInstance.id ? tipoIndiceInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
