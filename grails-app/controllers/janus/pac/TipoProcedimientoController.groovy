package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class TipoProcedimientoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoProcedimientoInstanceList: TipoProcedimiento.list(params), params: params]
    } //list

    def form_ajax() {
        def tipoProcedimientoInstance = new TipoProcedimiento(params)
        if (params.id) {
            tipoProcedimientoInstance = TipoProcedimiento.get(params.id)
            if (!tipoProcedimientoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Procedimiento con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoProcedimientoInstance: tipoProcedimientoInstance]
    } //form_ajax

    def save() {
        def tipoProcedimientoInstance
        if (params.id) {
            tipoProcedimientoInstance = TipoProcedimiento.get(params.id)
            if (!tipoProcedimientoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Procedimiento con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoProcedimientoInstance.properties = params
        }//es edit
        else {
            tipoProcedimientoInstance = new TipoProcedimiento(params)
        } //es create
        if (!tipoProcedimientoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Procedimiento " + (tipoProcedimientoInstance.id ? tipoProcedimientoInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoProcedimientoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Procedimiento " + tipoProcedimientoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Procedimiento " + tipoProcedimientoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoProcedimientoInstance = TipoProcedimiento.get(params.id)
        if (!tipoProcedimientoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Procedimiento con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoProcedimientoInstance: tipoProcedimientoInstance]
    } //show

    def delete() {
        def tipoProcedimientoInstance = TipoProcedimiento.get(params.id)
        if (!tipoProcedimientoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Procedimiento con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoProcedimientoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Tipo Procedimiento " + tipoProcedimientoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Tipo Procedimiento " + (tipoProcedimientoInstance.id ? tipoProcedimientoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
