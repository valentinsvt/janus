package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class TipoContratoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoContratoInstanceList: TipoContrato.list(params), params: params]
    } //list

    def form_ajax() {
        def tipoContratoInstance = new TipoContrato(params)
        if (params.id) {
            tipoContratoInstance = TipoContrato.get(params.id)
            if (!tipoContratoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Contrato con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoContratoInstance: tipoContratoInstance]
    } //form_ajax

    def save() {

        params.codigo = params.codigo.toUpperCase();

        def existe = TipoContrato.findByCodigo(params.codigo)

        def tipoContratoInstance
        if (params.id) {
            tipoContratoInstance = TipoContrato.get(params.id)
            if (!tipoContratoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Contrato con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoContratoInstance.properties = params
        }//es edit
        else {

            if(!existe){
                tipoContratoInstance = new TipoContrato(params)
            }else{
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar el tipo de contrato, el código ya existe!!"
                redirect(action: 'list')
                return
            }


        } //es create
        if (!tipoContratoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Contrato " + (tipoContratoInstance.id ? tipoContratoInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoContratoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Contrato " + tipoContratoInstance?.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Contrato " + tipoContratoInstance?.descripcion
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoContratoInstance = TipoContrato.get(params.id)
        if (!tipoContratoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Contrato con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoContratoInstance: tipoContratoInstance]
    } //show

    def delete() {
        def tipoContratoInstance = TipoContrato.get(params.id)
        if (!tipoContratoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Contrato con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoContratoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Tipo Contrato " + tipoContratoInstance?.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Tipo Contrato " + (tipoContratoInstance.id ? tipoContratoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
