package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class TipoCompraController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoCompraInstanceList: TipoCompra.list(params), params: params]
    } //list

    def form_ajax() {
        def tipoCompraInstance = new TipoCompra(params)
        if (params.id) {
            tipoCompraInstance = TipoCompra.get(params.id)
            if (!tipoCompraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró el Tipo Compra con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoCompraInstance: tipoCompraInstance]
    } //form_ajax

    def save() {
        def tipoCompraInstance
        if (params.id) {
            tipoCompraInstance = TipoCompra.get(params.id)
            if (!tipoCompraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró el Tipo Compra con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoCompraInstance.properties = params
        }//es edit
        else {
            tipoCompraInstance = new TipoCompra(params)
        } //es create
        if (!tipoCompraInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar el Tipo Compra " + (tipoCompraInstance.id ? tipoCompraInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoCompraInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente el Tipo Compra " + tipoCompraInstance.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente el Tipo Compra " + tipoCompraInstance.descripcion
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoCompraInstance = TipoCompra.get(params.id)
        if (!tipoCompraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Compra con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoCompraInstance: tipoCompraInstance]
    } //show

    def delete() {
        def tipoCompraInstance = TipoCompra.get(params.id)
        if (!tipoCompraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Compra con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoCompraInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente el Tipo Compra " + tipoCompraInstance.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar el Tipo Compra " + (tipoCompraInstance.id ? tipoCompraInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
