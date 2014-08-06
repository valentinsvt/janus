package janus

import org.springframework.dao.DataIntegrityViolationException

class AdministracionController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [administracionInstanceList: Administracion.list(params), administracionInstanceTotal: Administracion.count(), params: params]
    } //list

    def form_ajax() {
        def administracionInstance = new Administracion(params)
        if (params.id) {
            administracionInstance = Administracion.get(params.id)
            if (!administracionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Administracion con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [administracionInstance: administracionInstance]
    } //form_ajax

    def save() {
        if(params.fechaInicio){
            params.fechaInicio = new Date().parse("dd-MM-yyyy", params.fechaInicio)
        }
        if(params.fechaFin){
            params.fechaFin = new Date().parse("dd-MM-yyyy", params.fechaFin)
        }


        if(params.fechaInicio >= params.fechaFin){
            flash.clase = "alert-error"
            flash.message = "No se pudo guardar, la Fecha de Inicio debe ser menor a la Fecha Fin"
            redirect(action: 'list')
            return
        }else{
        def administracionInstance
        if (params.id) {
            administracionInstance = Administracion.get(params.id)
            if (!administracionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Administracion con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            administracionInstance.properties = params
        }//es edit
        else {
            administracionInstance = new Administracion(params)
        } //es create
        if (!administracionInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Administracion " + (administracionInstance.id ? administracionInstance.id : "") + "</h4>"

            str += "<ul>"
            administracionInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Administracion " + administracionInstance.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Administracion " + administracionInstance.descripcion
        }
        redirect(action: 'list')
        }
    } //save

    def show_ajax() {
        def administracionInstance = Administracion.get(params.id)
        if (!administracionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Administracion con id " + params.id
            redirect(action: "list")
            return
        }
        [administracionInstance: administracionInstance]
    } //show

    def delete() {
        def administracionInstance = Administracion.get(params.id)
        if (!administracionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Administracion con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            administracionInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Administracion " + administracionInstance.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Administracion " + (administracionInstance.id ? administracionInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
