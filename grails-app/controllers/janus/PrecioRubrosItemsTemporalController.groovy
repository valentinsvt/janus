package janus

import org.springframework.dao.DataIntegrityViolationException

class PrecioRubrosItemsTemporalController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [precioRubrosItemsTemporalInstanceList: PrecioRubrosItemsTemporal.list(params), precioRubrosItemsTemporalInstanceTotal: PrecioRubrosItemsTemporal.count(), params: params]
    } //list

    def form_ajax() {
        def precioRubrosItemsTemporalInstance = new PrecioRubrosItemsTemporal(params)
        if (params.id) {
            precioRubrosItemsTemporalInstance = PrecioRubrosItemsTemporal.get(params.id)
            if (!precioRubrosItemsTemporalInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró PrecioRubrosItemsTemporal con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [precioRubrosItemsTemporalInstance: precioRubrosItemsTemporalInstance]
    } //form_ajax

    def save() {
        def precioRubrosItemsTemporalInstance
        if (params.id) {
            precioRubrosItemsTemporalInstance = PrecioRubrosItemsTemporal.get(params.id)
            if (!precioRubrosItemsTemporalInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró PrecioRubrosItemsTemporal con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            precioRubrosItemsTemporalInstance.properties = params
        }//es edit
        else {
            precioRubrosItemsTemporalInstance = new PrecioRubrosItemsTemporal(params)
        } //es create
        if (!precioRubrosItemsTemporalInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar PrecioRubrosItemsTemporal " + (precioRubrosItemsTemporalInstance.id ? precioRubrosItemsTemporalInstance.id : "") + "</h4>"

            str += "<ul>"
            precioRubrosItemsTemporalInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente PrecioRubrosItemsTemporal " + precioRubrosItemsTemporalInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente PrecioRubrosItemsTemporal " + precioRubrosItemsTemporalInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def precioRubrosItemsTemporalInstance = PrecioRubrosItemsTemporal.get(params.id)
        if (!precioRubrosItemsTemporalInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró PrecioRubrosItemsTemporal con id " + params.id
            redirect(action: "list")
            return
        }
        [precioRubrosItemsTemporalInstance: precioRubrosItemsTemporalInstance]
    } //show

    def delete() {
        def precioRubrosItemsTemporalInstance = PrecioRubrosItemsTemporal.get(params.id)
        if (!precioRubrosItemsTemporalInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró PrecioRubrosItemsTemporal con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            precioRubrosItemsTemporalInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente PrecioRubrosItemsTemporal " + precioRubrosItemsTemporalInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar PrecioRubrosItemsTemporal " + (precioRubrosItemsTemporalInstance.id ? precioRubrosItemsTemporalInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
