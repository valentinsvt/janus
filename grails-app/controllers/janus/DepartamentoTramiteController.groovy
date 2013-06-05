package janus

import org.springframework.dao.DataIntegrityViolationException

class DepartamentoTramiteController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [departamentoTramiteInstanceList: DepartamentoTramite.list(params), params: params]
    } //list

    def form_ajax() {
        def departamentoTramiteInstance = new DepartamentoTramite(params)
        if (params.id) {
            departamentoTramiteInstance = DepartamentoTramite.get(params.id)
            if (!departamentoTramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Departamento Tramite con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [departamentoTramiteInstance: departamentoTramiteInstance]
    } //form_ajax

    def save() {
//        println params
        def departamentoTramiteInstance
        if (params.id) {
            departamentoTramiteInstance = DepartamentoTramite.get(params.id)
            if (!departamentoTramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Departamento Tramite con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            departamentoTramiteInstance.properties = params
        }//es edit
        else {
            departamentoTramiteInstance = new DepartamentoTramite(params)
        } //es create
        if (!departamentoTramiteInstance.save(flush: true)) {
            println "departamentoTramite controller, l.48: "+departamentoTramiteInstance.errors
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Departamento Tramite " + (departamentoTramiteInstance.id ? departamentoTramiteInstance.id : "") + "</h4>"

            str += "<ul>"
            departamentoTramiteInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Departamento Tramite " + departamentoTramiteInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Departamento Tramite " + departamentoTramiteInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def departamentoTramiteInstance = DepartamentoTramite.get(params.id)
        if (!departamentoTramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Departamento Tramite con id " + params.id
            redirect(action: "list")
            return
        }
        [departamentoTramiteInstance: departamentoTramiteInstance]
    } //show

    def delete() {
        def departamentoTramiteInstance = DepartamentoTramite.get(params.id)
        if (!departamentoTramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Departamento Tramite con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            departamentoTramiteInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Departamento Tramite " + departamentoTramiteInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Departamento Tramite " + (departamentoTramiteInstance.id ? departamentoTramiteInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
