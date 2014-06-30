package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class TipoDocumentoGarantiaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoDocumentoGarantiaInstanceList: TipoDocumentoGarantia.list(params), params: params]
    } //list

    def form_ajax() {
        def tipoDocumentoGarantiaInstance = new TipoDocumentoGarantia(params)
        if (params.id) {
            tipoDocumentoGarantiaInstance = TipoDocumentoGarantia.get(params.id)
            if (!tipoDocumentoGarantiaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Documento Garantia con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoDocumentoGarantiaInstance: tipoDocumentoGarantiaInstance]
    } //form_ajax

    def save() {

        params.codigo = params.codigo.toUpperCase();

        def existe = TipoDocumentoGarantia.findByCodigo(params.codigo)
        def tipoDocumentoGarantiaInstance
        if (params.id) {
            tipoDocumentoGarantiaInstance = TipoDocumentoGarantia.get(params.id)
            if (!tipoDocumentoGarantiaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Documento Garantia con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoDocumentoGarantiaInstance.properties = params
        }//es edit
        else {
            if(!existe){
                tipoDocumentoGarantiaInstance = new TipoDocumentoGarantia(params)
            }else{
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar el grupo, el código ya existe!!"
                redirect(action: 'list')
                return
            }

        } //es create
        if (!tipoDocumentoGarantiaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Documento Garantia " + (tipoDocumentoGarantiaInstance.id ? tipoDocumentoGarantiaInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoDocumentoGarantiaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Documento Garantia " + tipoDocumentoGarantiaInstance?.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Documento Garantia " + tipoDocumentoGarantiaInstance?.descripcion
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoDocumentoGarantiaInstance = TipoDocumentoGarantia.get(params.id)
        if (!tipoDocumentoGarantiaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Documento Garantia con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoDocumentoGarantiaInstance: tipoDocumentoGarantiaInstance]
    } //show

    def delete() {
        def tipoDocumentoGarantiaInstance = TipoDocumentoGarantia.get(params.id)
        if (!tipoDocumentoGarantiaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Documento Garantia con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoDocumentoGarantiaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Tipo Documento Garantia " + tipoDocumentoGarantiaInstance.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Tipo Documento Garantia " + (tipoDocumentoGarantiaInstance.id ? tipoDocumentoGarantiaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
