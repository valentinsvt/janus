package janus

import org.springframework.dao.DataIntegrityViolationException

class TipoObraController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoObraInstanceList: TipoObra.list(params), params: params]
    } //list

    def form_ajax() {
        def tipoObraInstance = new TipoObra(params)
        if(params.id) {
            tipoObraInstance = TipoObra.get(params.id)
            if(!tipoObraInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Tipo Obra con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoObraInstance: tipoObraInstance]
    } //form_ajax

    def save() {
        def tipoObraInstance
        if(params.id) {
            tipoObraInstance = TipoObra.get(params.id)
            if(!tipoObraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Obra con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoObraInstance.properties = params
        }//es edit
        else {
            tipoObraInstance = new TipoObra(params)
        } //es create
        if (!tipoObraInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Obra " + (tipoObraInstance.id ? tipoObraInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoObraInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Obra " + tipoObraInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Obra " + tipoObraInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoObraInstance = TipoObra.get(params.id)
        if (!tipoObraInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Tipo Obra con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoObraInstance: tipoObraInstance]
    } //show

    def delete() {
        def tipoObraInstance = TipoObra.get(params.id)
        if (!tipoObraInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Tipo Obra con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoObraInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Tipo Obra " + tipoObraInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Tipo Obra " + (tipoObraInstance.id ? tipoObraInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
