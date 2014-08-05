package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class TipoProcedimientoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoProcedimientoInstanceList: TipoProcedimiento.list([sort: 'id']), params: params]
    } //list

    def form_ajax() {
        def tipoProcedimientoInstance = new TipoProcedimiento(params)
        if(params.id) {
            tipoProcedimientoInstance = TipoProcedimiento.get(params.id)
            if(!tipoProcedimientoInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Tipo Procedimiento con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoProcedimientoInstance: tipoProcedimientoInstance]
    } //form_ajax

    def save() {

        params.sigla = params.sigla.toUpperCase();

        def tipoProcedimientoInstance
        if(params.id) {
            tipoProcedimientoInstance = TipoProcedimiento.get(params.id)
            if(!tipoProcedimientoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Procedimiento con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoProcedimientoInstance.properties = params
        }//es edit
        else {
            def existe= TipoProcedimiento.findBySigla(params.sigla)
            if(!existe)
                tipoProcedimientoInstance = new TipoProcedimiento(params)
            else{
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar el código ya existe."
                redirect(action: 'list')
                return
            }
        } //es create
        if (!tipoProcedimientoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Procedimiento " + (tipoProcedimientoInstance.id ? tipoProcedimientoInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoProcedimientoInstance.errors.allErrors.each { err ->
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

        if(params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Tipo Procedimiento " + tipoProcedimientoInstance.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Procedimiento " + tipoProcedimientoInstance.descripcion
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoProcedimientoInstance = TipoProcedimiento.get(params.id)
        if (!tipoProcedimientoInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Tipo Procedimiento con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoProcedimientoInstance: tipoProcedimientoInstance]
    } //show

    def delete() {
        def tipoProcedimientoInstance = TipoProcedimiento.get(params.id)
        if (!tipoProcedimientoInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Tipo Procedimiento con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoProcedimientoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Tipo Procedimiento " + tipoProcedimientoInstance.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Tipo Procedimiento " + (tipoProcedimientoInstance.id ? tipoProcedimientoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
