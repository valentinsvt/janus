package janus.ejecucion

import org.springframework.dao.DataIntegrityViolationException

class TipoDescuentoPlanillaController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoDescuentoPlanillaInstanceList: TipoDescuentoPlanilla.list(params), params: params]
    } //list

    def form_ajax() {
        def tipoDescuentoPlanillaInstance = new TipoDescuentoPlanilla(params)
        if (params.id) {
            tipoDescuentoPlanillaInstance = TipoDescuentoPlanilla.get(params.id)
            if (!tipoDescuentoPlanillaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Descuento Planilla con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoDescuentoPlanillaInstance: tipoDescuentoPlanillaInstance]
    } //form_ajax

    def save() {
        def tipoDescuentoPlanillaInstance
        if (params.id) {
            tipoDescuentoPlanillaInstance = TipoDescuentoPlanilla.get(params.id)
            if (!tipoDescuentoPlanillaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Descuento Planilla con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoDescuentoPlanillaInstance.properties = params
        }//es edit
        else {
            tipoDescuentoPlanillaInstance = new TipoDescuentoPlanilla(params)
        } //es create
        if (!tipoDescuentoPlanillaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Descuento Planilla " + (tipoDescuentoPlanillaInstance.id ? tipoDescuentoPlanillaInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoDescuentoPlanillaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Descuento Planilla " + tipoDescuentoPlanillaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Descuento Planilla " + tipoDescuentoPlanillaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoDescuentoPlanillaInstance = TipoDescuentoPlanilla.get(params.id)
        if (!tipoDescuentoPlanillaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Descuento Planilla con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoDescuentoPlanillaInstance: tipoDescuentoPlanillaInstance]
    } //show

    def delete() {
        def tipoDescuentoPlanillaInstance = TipoDescuentoPlanilla.get(params.id)
        if (!tipoDescuentoPlanillaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Descuento Planilla con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoDescuentoPlanillaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Tipo Descuento Planilla " + tipoDescuentoPlanillaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Tipo Descuento Planilla " + (tipoDescuentoPlanillaInstance.id ? tipoDescuentoPlanillaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
