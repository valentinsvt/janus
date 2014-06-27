package janus

import org.springframework.dao.DataIntegrityViolationException

class UnidadController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [unidadInstanceList: Unidad.list(params), unidadInstanceTotal: Unidad.count(), params: params]
    } //list

    def form_ajax() {
        def unidadInstance = new Unidad(params)
        if (params.id) {
            unidadInstance = Unidad.get(params.id)
            if (!unidadInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Unidad con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [unidadInstance: unidadInstance]
    } //form_ajax

    def save() {
        def unidadInstance
        if (params.id) {
            unidadInstance = Unidad.get(params.id)
            if (!unidadInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Unidad con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            unidadInstance.properties = params
        }//es edit
        else {
            def existe= Unidad.findByCodigo(params.codigo)
            if(!existe)
                unidadInstance = new Unidad(params)
            else{
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar el código ya existe."
                redirect(action: 'list')
                return
            }
        } //es create
        if (!unidadInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Unidad " + (unidadInstance.id ? unidadInstance.id : "") + "</h4>"

            str += "<ul>"
            unidadInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Unidad " + unidadInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Unidad " + unidadInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def unidadInstance = Unidad.get(params.id)
        if (!unidadInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Unidad con id " + params.id
            redirect(action: "list")
            return
        }
        [unidadInstance: unidadInstance]
    } //show

    def delete() {
        def unidadInstance = Unidad.get(params.id)
        if (!unidadInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Unidad con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            unidadInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Unidad " + unidadInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Unidad " + (unidadInstance.id ? unidadInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
