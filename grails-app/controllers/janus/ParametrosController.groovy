package janus

import org.springframework.dao.DataIntegrityViolationException

class ParametrosController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [parametrosInstanceList: Parametros.list(params), params: params]
    } //list

    def form_ajax() {
        def parametrosInstance = new Parametros(params)
        if(params.id) {
            parametrosInstance = Parametros.get(params.id)
            if(!parametrosInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Parametros con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [parametrosInstance: parametrosInstance]
    } //form_ajax

    def save() {
        def parametrosInstance
        if(params.id) {
            parametrosInstance = Parametros.get(params.id)
            if(!parametrosInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Parametros con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            parametrosInstance.properties = params
        }//es edit
        else {
            parametrosInstance = new Parametros(params)
        } //es create
        if (!parametrosInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Parametros " + (parametrosInstance.id ? parametrosInstance.id : "") + "</h4>"

            str += "<ul>"
            parametrosInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Parametros " + parametrosInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Parametros " + parametrosInstance.id
        }
        redirect(action: 'list')
    } //save

    //save Factores en variables de parámetros

    def saveFactores() {
        def parametrosInstance
        if(params.id) {
            parametrosInstance = Parametros.get(params.id)
            if(!parametrosInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Parametros con id " + params.id
                redirect(controller: 'inicio', action: 'variables')
                return
            }//no existe el objeto
            parametrosInstance.properties = params
        }//es edit
        else {
            parametrosInstance = new Parametros(params)
        } //es create
        if (!parametrosInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar las variables " + (parametrosInstance.id ? parametrosInstance.id : "") + "</h4>"

            str += "<ul>"
            parametrosInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(controller: 'inicio', action: 'variables')
            return
        }

        if(params.id) {
            flash.clase = "alert-success"
            flash.message = "Se han actualizado correctamente las variables "
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Parametros " + parametrosInstance.id
        }
        redirect(controller: 'inicio', action: 'variables')
    } //save


    def show_ajax() {
        def parametrosInstance = Parametros.get(params.id)
        if (!parametrosInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Parametros con id " + params.id
            redirect(action: "list")
            return
        }
        [parametrosInstance: parametrosInstance]
    } //show

    def delete() {
        def parametrosInstance = Parametros.get(params.id)
        if (!parametrosInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Parametros con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            parametrosInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Parametros " + parametrosInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Parametros " + (parametrosInstance.id ? parametrosInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
