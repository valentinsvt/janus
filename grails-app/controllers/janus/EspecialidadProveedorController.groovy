package janus

import org.springframework.dao.DataIntegrityViolationException

class EspecialidadProveedorController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [especialidadProveedorInstanceList: EspecialidadProveedor.list(params), especialidadProveedorInstanceTotal: EspecialidadProveedor.count(), params: params]
    } //list

    def form_ajax() {
        def especialidadProveedorInstance = new EspecialidadProveedor(params)
        if (params.id) {
            especialidadProveedorInstance = EspecialidadProveedor.get(params.id)
            if (!especialidadProveedorInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró EspecialidadProveedor con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [especialidadProveedorInstance: especialidadProveedorInstance]
    } //form_ajax

    def save() {
        def especialidadProveedorInstance
        if (params.id) {
            especialidadProveedorInstance = EspecialidadProveedor.get(params.id)
            if (!especialidadProveedorInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró EspecialidadProveedor con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            especialidadProveedorInstance.properties = params
        }//es edit
        else {
            especialidadProveedorInstance = new EspecialidadProveedor(params)
        } //es create
        if (!especialidadProveedorInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar EspecialidadProveedor " + (especialidadProveedorInstance.id ? especialidadProveedorInstance.id : "") + "</h4>"

            str += "<ul>"
            especialidadProveedorInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente EspecialidadProveedor " + especialidadProveedorInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente EspecialidadProveedor " + especialidadProveedorInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def especialidadProveedorInstance = EspecialidadProveedor.get(params.id)
        if (!especialidadProveedorInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró EspecialidadProveedor con id " + params.id
            redirect(action: "list")
            return
        }
        [especialidadProveedorInstance: especialidadProveedorInstance]
    } //show

    def delete() {
        def especialidadProveedorInstance = EspecialidadProveedor.get(params.id)
        if (!especialidadProveedorInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró EspecialidadProveedor con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            especialidadProveedorInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente EspecialidadProveedor " + especialidadProveedorInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar EspecialidadProveedor " + (especialidadProveedorInstance.id ? especialidadProveedorInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
