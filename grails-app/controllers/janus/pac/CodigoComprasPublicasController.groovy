package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class CodigoComprasPublicasController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [codigoComprasPublicasInstanceList: CodigoComprasPublicas.list(params), params: params]
    } //list

    def form_ajax() {
        def codigoComprasPublicasInstance = new CodigoComprasPublicas(params)
        if (params.id) {
            codigoComprasPublicasInstance = CodigoComprasPublicas.get(params.id)
            if (!codigoComprasPublicasInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Codigo Compras Publicas con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [codigoComprasPublicasInstance: codigoComprasPublicasInstance]
    } //form_ajax

    def save() {
        def codigoComprasPublicasInstance
        if (params.id) {
            codigoComprasPublicasInstance = CodigoComprasPublicas.get(params.id)
            if (!codigoComprasPublicasInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Codigo Compras Publicas con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            codigoComprasPublicasInstance.properties = params
        }//es edit
        else {
            codigoComprasPublicasInstance = new CodigoComprasPublicas(params)
        } //es create
        if (!codigoComprasPublicasInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Codigo Compras Publicas " + (codigoComprasPublicasInstance.id ? codigoComprasPublicasInstance.id : "") + "</h4>"

            str += "<ul>"
            codigoComprasPublicasInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Codigo Compras Publicas " + codigoComprasPublicasInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Codigo Compras Publicas " + codigoComprasPublicasInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def codigoComprasPublicasInstance = CodigoComprasPublicas.get(params.id)
        if (!codigoComprasPublicasInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Codigo Compras Publicas con id " + params.id
            redirect(action: "list")
            return
        }
        [codigoComprasPublicasInstance: codigoComprasPublicasInstance]
    } //show

    def delete() {
        def codigoComprasPublicasInstance = CodigoComprasPublicas.get(params.id)
        if (!codigoComprasPublicasInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Codigo Compras Publicas con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            codigoComprasPublicasInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Codigo Compras Publicas " + codigoComprasPublicasInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Codigo Compras Publicas " + (codigoComprasPublicasInstance.id ? codigoComprasPublicasInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
