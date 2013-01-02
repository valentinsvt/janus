package janus

import org.springframework.dao.DataIntegrityViolationException

class LugarController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [lugarInstanceList: Lugar.list(params), lugarInstanceTotal: Lugar.count(), params: params]
    } //list

    def form_ajax() {
        def lugarInstance = new Lugar(params)
        if (params.id) {
            lugarInstance = Lugar.get(params.id)
            if (!lugarInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Lugar con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [lugarInstance: lugarInstance]
    } //form_ajax

    def save() {
        def lugarInstance
        if (params.id) {
            lugarInstance = Lugar.get(params.id)
            if (!lugarInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Lugar con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            lugarInstance.properties = params
        }//es edit
        else {
            lugarInstance = new Lugar(params)
        } //es create
        if (!lugarInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Lugar " + (lugarInstance.id ? lugarInstance.id : "") + "</h4>"

            str += "<ul>"
            lugarInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Lugar " + lugarInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Lugar " + lugarInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def lugarInstance = Lugar.get(params.id)
        if (!lugarInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Lugar con id " + params.id
            redirect(action: "list")
            return
        }
        [lugarInstance: lugarInstance]
    } //show

    def delete() {
        def lugarInstance = Lugar.get(params.id)
        if (!lugarInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Lugar con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            lugarInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Lugar " + lugarInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Lugar " + (lugarInstance.id ? lugarInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
