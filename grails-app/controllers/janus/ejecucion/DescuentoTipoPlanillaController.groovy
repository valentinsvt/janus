package janus.ejecucion

import org.springframework.dao.DataIntegrityViolationException

class DescuentoTipoPlanillaController  {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [descuentoTipoPlanillaInstanceList: DescuentoTipoPlanilla.list(params), params: params]
    } //list

    def form_ajax() {
        def descuentoTipoPlanillaInstance = new DescuentoTipoPlanilla(params)
        if (params.id) {
            descuentoTipoPlanillaInstance = DescuentoTipoPlanilla.get(params.id)
            if (!descuentoTipoPlanillaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Descuento Tipo Planilla con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [descuentoTipoPlanillaInstance: descuentoTipoPlanillaInstance]
    } //form_ajax

    def save() {
        def descuentoTipoPlanillaInstance
        if (params.id) {
            descuentoTipoPlanillaInstance = DescuentoTipoPlanilla.get(params.id)
            if (!descuentoTipoPlanillaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Descuento Tipo Planilla con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            descuentoTipoPlanillaInstance.properties = params
        }//es edit
        else {
            descuentoTipoPlanillaInstance = new DescuentoTipoPlanilla(params)
        } //es create
        if (!descuentoTipoPlanillaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Descuento Tipo Planilla " + (descuentoTipoPlanillaInstance.id ? descuentoTipoPlanillaInstance.id : "") + "</h4>"

            str += "<ul>"
            descuentoTipoPlanillaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Descuento Tipo Planilla " + descuentoTipoPlanillaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Descuento Tipo Planilla " + descuentoTipoPlanillaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def descuentoTipoPlanillaInstance = DescuentoTipoPlanilla.get(params.id)
        if (!descuentoTipoPlanillaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Descuento Tipo Planilla con id " + params.id
            redirect(action: "list")
            return
        }
        [descuentoTipoPlanillaInstance: descuentoTipoPlanillaInstance]
    } //show

    def delete() {
        def descuentoTipoPlanillaInstance = DescuentoTipoPlanilla.get(params.id)
        if (!descuentoTipoPlanillaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Descuento Tipo Planilla con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            descuentoTipoPlanillaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Descuento Tipo Planilla " + descuentoTipoPlanillaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Descuento Tipo Planilla " + (descuentoTipoPlanillaInstance.id ? descuentoTipoPlanillaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
