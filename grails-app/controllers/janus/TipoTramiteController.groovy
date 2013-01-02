package janus

import org.springframework.dao.DataIntegrityViolationException

class TipoTramiteController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [tipoTramiteInstanceList: TipoTramite.list(params), tipoTramiteInstanceTotal: TipoTramite.count(), params: params]
    } //list

    def form_ajax() {
        def tipoTramiteInstance = new TipoTramite(params)
        if (params.id) {
            tipoTramiteInstance = TipoTramite.get(params.id)
            if (!tipoTramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró TipoTramite con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoTramiteInstance: tipoTramiteInstance]
    } //form_ajax

    def save() {
        def tipoTramiteInstance
        if (params.id) {
            tipoTramiteInstance = TipoTramite.get(params.id)
            if (!tipoTramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró TipoTramite con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoTramiteInstance.properties = params
        }//es edit
        else {
            tipoTramiteInstance = new TipoTramite(params)
        } //es create
        if (!tipoTramiteInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar TipoTramite " + (tipoTramiteInstance.id ? tipoTramiteInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoTramiteInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente TipoTramite " + tipoTramiteInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente TipoTramite " + tipoTramiteInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoTramiteInstance = TipoTramite.get(params.id)
        if (!tipoTramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró TipoTramite con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoTramiteInstance: tipoTramiteInstance]
    } //show

    def delete() {
        def tipoTramiteInstance = TipoTramite.get(params.id)
        if (!tipoTramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró TipoTramite con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoTramiteInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente TipoTramite " + tipoTramiteInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar TipoTramite " + (tipoTramiteInstance.id ? tipoTramiteInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
