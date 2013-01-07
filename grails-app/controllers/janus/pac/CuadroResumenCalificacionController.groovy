package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class CuadroResumenCalificacionController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [cuadroResumenCalificacionInstanceList: CuadroResumenCalificacion.list(params), params: params]
    } //list

    def form_ajax() {
        def cuadroResumenCalificacionInstance = new CuadroResumenCalificacion(params)
        if(params.id) {
            cuadroResumenCalificacionInstance = CuadroResumenCalificacion.get(params.id)
            if(!cuadroResumenCalificacionInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Cuadro Resumen Calificacion con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [cuadroResumenCalificacionInstance: cuadroResumenCalificacionInstance]
    } //form_ajax

    def save() {
        def cuadroResumenCalificacionInstance
        if(params.id) {
            cuadroResumenCalificacionInstance = CuadroResumenCalificacion.get(params.id)
            if(!cuadroResumenCalificacionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Cuadro Resumen Calificacion con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            cuadroResumenCalificacionInstance.properties = params
        }//es edit
        else {
            cuadroResumenCalificacionInstance = new CuadroResumenCalificacion(params)
        } //es create
        if (!cuadroResumenCalificacionInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Cuadro Resumen Calificacion " + (cuadroResumenCalificacionInstance.id ? cuadroResumenCalificacionInstance.id : "") + "</h4>"

            str += "<ul>"
            cuadroResumenCalificacionInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Cuadro Resumen Calificacion " + cuadroResumenCalificacionInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Cuadro Resumen Calificacion " + cuadroResumenCalificacionInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def cuadroResumenCalificacionInstance = CuadroResumenCalificacion.get(params.id)
        if (!cuadroResumenCalificacionInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Cuadro Resumen Calificacion con id " + params.id
            redirect(action: "list")
            return
        }
        [cuadroResumenCalificacionInstance: cuadroResumenCalificacionInstance]
    } //show

    def delete() {
        def cuadroResumenCalificacionInstance = CuadroResumenCalificacion.get(params.id)
        if (!cuadroResumenCalificacionInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Cuadro Resumen Calificacion con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            cuadroResumenCalificacionInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Cuadro Resumen Calificacion " + cuadroResumenCalificacionInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Cuadro Resumen Calificacion " + (cuadroResumenCalificacionInstance.id ? cuadroResumenCalificacionInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
