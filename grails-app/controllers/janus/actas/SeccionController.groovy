package janus.actas

import org.springframework.dao.DataIntegrityViolationException

class SeccionController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [seccionInstanceList: Seccion.list(params), params: params]
    } //list

    def form_ajax() {
        def seccionInstance = new Seccion(params)
        if (params.id) {
            seccionInstance = Seccion.get(params.id)
            if (!seccionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Seccion con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [seccionInstance: seccionInstance]
    } //form_ajax

    def form_ext_ajax() {
        def seccionInstance = new Seccion(params)
        if (params.id) {
            seccionInstance = Seccion.get(params.id)
            if (!seccionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Seccion con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        else {
            seccionInstance.acta = Acta.get(params.acta)
            seccionInstance.numero = params.numero.toInteger()
        }
        return [seccionInstance: seccionInstance]
    } //form_ajax

    def save() {
        def seccionInstance
        if (params.id) {
            seccionInstance = Seccion.get(params.id)
            if (!seccionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Seccion con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            seccionInstance.properties = params
        }//es edit
        else {
            seccionInstance = new Seccion(params)
        } //es create
        if (!seccionInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Seccion " + (seccionInstance.id ? seccionInstance.id : "") + "</h4>"

            str += "<ul>"
            seccionInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Seccion " + seccionInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Seccion " + seccionInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def seccionInstance = Seccion.get(params.id)
        if (!seccionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Seccion con id " + params.id
            redirect(action: "list")
            return
        }
        [seccionInstance: seccionInstance]
    } //show

    def delete() {
        def seccionInstance = Seccion.get(params.id)
        if (!seccionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Seccion con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            seccionInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Seccion " + seccionInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Seccion " + (seccionInstance.id ? seccionInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
