package janus

import org.springframework.dao.DataIntegrityViolationException

class PrecioRubrosItemsController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [precioRubrosItemsInstanceList: PrecioRubrosItems.list(params), precioRubrosItemsInstanceTotal: PrecioRubrosItems.count(), params: params]
    } //list

    def form_ajax() {
        def precioRubrosItemsInstance = new PrecioRubrosItems(params)
        if (params.id) {
            precioRubrosItemsInstance = PrecioRubrosItems.get(params.id)
            if (!precioRubrosItemsInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró PrecioRubrosItems con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [precioRubrosItemsInstance: precioRubrosItemsInstance]
    } //form_ajax

    def save() {
        def precioRubrosItemsInstance
        if (params.id) {
            precioRubrosItemsInstance = PrecioRubrosItems.get(params.id)
            if (!precioRubrosItemsInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró PrecioRubrosItems con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            precioRubrosItemsInstance.properties = params
        }//es edit
        else {
            precioRubrosItemsInstance = new PrecioRubrosItems(params)
        } //es create
        if (!precioRubrosItemsInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar PrecioRubrosItems " + (precioRubrosItemsInstance.id ? precioRubrosItemsInstance.id : "") + "</h4>"

            str += "<ul>"
            precioRubrosItemsInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente PrecioRubrosItems " + precioRubrosItemsInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente PrecioRubrosItems " + precioRubrosItemsInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def precioRubrosItemsInstance = PrecioRubrosItems.get(params.id)
        if (!precioRubrosItemsInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró PrecioRubrosItems con id " + params.id
            redirect(action: "list")
            return
        }
        [precioRubrosItemsInstance: precioRubrosItemsInstance]
    } //show

    def delete() {
        def precioRubrosItemsInstance = PrecioRubrosItems.get(params.id)
        if (!precioRubrosItemsInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró PrecioRubrosItems con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            precioRubrosItemsInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente PrecioRubrosItems " + precioRubrosItemsInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar PrecioRubrosItems " + (precioRubrosItemsInstance.id ? precioRubrosItemsInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
