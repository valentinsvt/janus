package janus

import org.springframework.dao.DataIntegrityViolationException

class PrecioVentaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [precioVentaInstanceList: PrecioVenta.list(params), precioVentaInstanceTotal: PrecioVenta.count(), params: params]
    } //list

    def form_ajax() {
        def precioVentaInstance = new PrecioVenta(params)
        if (params.id) {
            precioVentaInstance = PrecioVenta.get(params.id)
            if (!precioVentaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró PrecioVenta con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [precioVentaInstance: precioVentaInstance]
    } //form_ajax

    def save() {
        def precioVentaInstance
        if (params.id) {
            precioVentaInstance = PrecioVenta.get(params.id)
            if (!precioVentaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró PrecioVenta con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            precioVentaInstance.properties = params
        }//es edit
        else {
            precioVentaInstance = new PrecioVenta(params)
        } //es create
        if (!precioVentaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar PrecioVenta " + (precioVentaInstance.id ? precioVentaInstance.id : "") + "</h4>"

            str += "<ul>"
            precioVentaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente PrecioVenta " + precioVentaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente PrecioVenta " + precioVentaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def precioVentaInstance = PrecioVenta.get(params.id)
        if (!precioVentaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró PrecioVenta con id " + params.id
            redirect(action: "list")
            return
        }
        [precioVentaInstance: precioVentaInstance]
    } //show

    def delete() {
        def precioVentaInstance = PrecioVenta.get(params.id)
        if (!precioVentaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró PrecioVenta con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            precioVentaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente PrecioVenta " + precioVentaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar PrecioVenta " + (precioVentaInstance.id ? precioVentaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
