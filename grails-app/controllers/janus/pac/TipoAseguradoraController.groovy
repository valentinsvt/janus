package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class TipoAseguradoraController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoAseguradoraInstanceList: TipoAseguradora.list(params), params: params]
    } //list

    def form_ajax() {
        def tipoAseguradoraInstance = new TipoAseguradora(params)
        if (params.id) {
            tipoAseguradoraInstance = TipoAseguradora.get(params.id)
            if (!tipoAseguradoraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Aseguradora con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoAseguradoraInstance: tipoAseguradoraInstance]
    } //form_ajax

    def save() {

        params.codigo = params.codigo.toUpperCase();

        def existe = TipoAseguradora.findByCodigo(params.codigo)

        def tipoAseguradoraInstance
        if (params.id) {
            tipoAseguradoraInstance = TipoAseguradora.get(params.id)
            if (!tipoAseguradoraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Aseguradora con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoAseguradoraInstance.properties = params
        }//es edit
        else {
            if(!existe){
                tipoAseguradoraInstance = new TipoAseguradora(params)
            }else{
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar el tipo de aseguradora, el código ya existe!!"
                redirect(action: 'list')
                return
            }

        } //es create
        if (!tipoAseguradoraInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Aseguradora " + (tipoAseguradoraInstance.id ? tipoAseguradoraInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoAseguradoraInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Aseguradora " + tipoAseguradoraInstance?.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Aseguradora " + tipoAseguradoraInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoAseguradoraInstance = TipoAseguradora.get(params.id)
        if (!tipoAseguradoraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Aseguradora con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoAseguradoraInstance: tipoAseguradoraInstance]
    } //show

    def delete() {
        def tipoAseguradoraInstance = TipoAseguradora.get(params.id)
        if (!tipoAseguradoraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Aseguradora con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoAseguradoraInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Tipo Aseguradora " + tipoAseguradoraInstance?.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Tipo Aseguradora " + (tipoAseguradoraInstance.id ? tipoAseguradoraInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
