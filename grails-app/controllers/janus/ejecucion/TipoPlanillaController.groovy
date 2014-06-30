package janus.ejecucion

import org.springframework.dao.DataIntegrityViolationException

class TipoPlanillaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoPlanillaInstanceList: TipoPlanilla.list(params), params: params]
    } //list

    def form_ajax() {
        def tipoPlanillaInstance = new TipoPlanilla(params)
        if(params.id) {
            tipoPlanillaInstance = TipoPlanilla.get(params.id)
            if(!tipoPlanillaInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Tipo Planilla con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoPlanillaInstance: tipoPlanillaInstance]
    } //form_ajax

    def save() {
        if(params.codigo){
            params.codigo = params.codigo.toUpperCase();
        }
        def tipoPlanillaInstance
        if(params.id) {
            tipoPlanillaInstance = TipoPlanilla.get(params.id)
            if(!tipoPlanillaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Planilla con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoPlanillaInstance.properties = params
        }//es edit
        else {
            def existe= TipoPlanilla.findByCodigo(params.codigo)
            if(!existe)
                tipoPlanillaInstance = new TipoPlanilla(params)
            else{
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar el código ya existe."
                redirect(action: 'list')
                return
            }
        } //es create
        if (!tipoPlanillaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Planilla " + (tipoPlanillaInstance.id ? tipoPlanillaInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoPlanillaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Planilla " + tipoPlanillaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Planilla " + tipoPlanillaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoPlanillaInstance = TipoPlanilla.get(params.id)
        if (!tipoPlanillaInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Tipo Planilla con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoPlanillaInstance: tipoPlanillaInstance]
    } //show

    def delete() {
        def tipoPlanillaInstance = TipoPlanilla.get(params.id)
        if (!tipoPlanillaInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Tipo Planilla con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoPlanillaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Tipo Planilla " + tipoPlanillaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Tipo Planilla " + (tipoPlanillaInstance.id ? tipoPlanillaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
