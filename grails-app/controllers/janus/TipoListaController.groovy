package janus

import org.springframework.dao.DataIntegrityViolationException

class TipoListaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoListaInstanceList: TipoLista.list(params).sort{it.codigo}, params: params]
    } //list

    def form_ajax() {
        def tipoListaInstance = new TipoLista(params)
        if(params.id) {
            tipoListaInstance = TipoLista.get(params.id)
            if(!tipoListaInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Tipo Lista con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoListaInstance: tipoListaInstance]
    } //form_ajax

    def save() {
        def tipoListaInstance
        if(params.id) {
            tipoListaInstance = TipoLista.get(params.id)
            if(!tipoListaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Lista con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoListaInstance.properties = params
        }//es edit
        else {
            tipoListaInstance = new TipoLista(params)
        } //es create
        if (!tipoListaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Lista " + (tipoListaInstance.id ? tipoListaInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoListaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Lista " + tipoListaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Lista " + tipoListaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoListaInstance = TipoLista.get(params.id)
        if (!tipoListaInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Tipo Lista con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoListaInstance: tipoListaInstance]
    } //show

    def delete() {
        def tipoListaInstance = TipoLista.get(params.id)
        if (!tipoListaInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Tipo Lista con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoListaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Tipo Lista " + tipoListaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Tipo Lista " + (tipoListaInstance.id ? tipoListaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
