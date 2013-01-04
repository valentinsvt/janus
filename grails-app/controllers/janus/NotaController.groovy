package janus

import org.springframework.dao.DataIntegrityViolationException

class NotaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [notaInstanceList: Nota.list(params), params: params]
    } //list

    def form_ajax() {
        def notaInstance = new Nota(params)
        if (params.id) {
            notaInstance = Nota.get(params.id)
            if (!notaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Nota con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [notaInstance: notaInstance]
    } //form_ajax

    def save() {
         println "params "+params
        if (params.piePaginaSel) {

            params.id = params.piePaginaSel

        }


        def notaInstance
        if (params.id) {
            notaInstance = Nota.get(params.id)
            if (!notaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Nota con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            notaInstance.properties = params
        }//es edit
        else {
            notaInstance = new Nota(params)
        } //es create
        if (!notaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Nota " + (notaInstance.id ? notaInstance.id : "") + "</h4>"

            str += "<ul>"
            notaInstance.errors.allErrors.each { err ->
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

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Nota " + notaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Nota " + notaInstance.id
        }
//        redirect(action: 'list')
        redirect(controller: 'documentosObra',action: 'documentosObra',id: params.obra)

    } //save

    def show_ajax() {
        def notaInstance = Nota.get(params.id)
        if (!notaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Nota con id " + params.id
            redirect(action: "list")
            return
        }
        [notaInstance: notaInstance]
    } //show

    def delete() {
        def notaInstance = Nota.get(params.id)
        if (!notaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Nota con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            notaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Nota " + notaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Nota " + (notaInstance.id ? notaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
