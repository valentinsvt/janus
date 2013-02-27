package janus

import org.springframework.dao.DataIntegrityViolationException

class AuxiliarController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [auxiliarInstanceList: Auxiliar.list(params), params: params]
    } //list

    def form_ajax() {
        def auxiliarInstance = new Auxiliar(params)
        if(params.id) {
            auxiliarInstance = Auxiliar.get(params.id)
            if(!auxiliarInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Auxiliar con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [auxiliarInstance: auxiliarInstance]
    } //form_ajax

    def save() {
        def auxiliarInstance
        if(params.id) {
            auxiliarInstance = Auxiliar.get(params.id)
            if(!auxiliarInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Auxiliar con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            auxiliarInstance.properties = params
        }//es edit
        else {
            auxiliarInstance = new Auxiliar(params)
        } //es create
        if (!auxiliarInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Auxiliar " + (auxiliarInstance.id ? auxiliarInstance.id : "") + "</h4>"

            str += "<ul>"
            auxiliarInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Auxiliar " + auxiliarInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Auxiliar " + auxiliarInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def auxiliarInstance = Auxiliar.get(params.id)
        if (!auxiliarInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Auxiliar con id " + params.id
            redirect(action: "list")
            return
        }
        [auxiliarInstance: auxiliarInstance]
    } //show

    def delete() {
        def auxiliarInstance = Auxiliar.get(params.id)
        if (!auxiliarInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Auxiliar con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            auxiliarInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Auxiliar " + auxiliarInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Auxiliar " + (auxiliarInstance.id ? auxiliarInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
