package janus

import org.springframework.dao.DataIntegrityViolationException

class TipoObjetivoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [tipoObjetivoInstanceList: TipoObjetivo.list(params), tipoObjetivoInstanceTotal: TipoObjetivo.count(), params: params]
    } //list

    def form_ajax() {
        def tipoObjetivoInstance = new TipoObjetivo(params)
        if (params.id) {
            tipoObjetivoInstance = TipoObjetivo.get(params.id)
            if (!tipoObjetivoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró TipoObjetivo con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoObjetivoInstance: tipoObjetivoInstance]
    } //form_ajax

    def save() {
        def tipoObjetivoInstance
        if (params.id) {
            tipoObjetivoInstance = TipoObjetivo.get(params.id)
            if (!tipoObjetivoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró TipoObjetivo con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoObjetivoInstance.properties = params
        }//es edit
        else {
            tipoObjetivoInstance = new TipoObjetivo(params)
        } //es create
        if (!tipoObjetivoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar TipoObjetivo " + (tipoObjetivoInstance.id ? tipoObjetivoInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoObjetivoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente TipoObjetivo " + tipoObjetivoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente TipoObjetivo " + tipoObjetivoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoObjetivoInstance = TipoObjetivo.get(params.id)
        if (!tipoObjetivoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró TipoObjetivo con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoObjetivoInstance: tipoObjetivoInstance]
    } //show

    def delete() {
        def tipoObjetivoInstance = TipoObjetivo.get(params.id)
        if (!tipoObjetivoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró TipoObjetivo con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoObjetivoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente TipoObjetivo " + tipoObjetivoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar TipoObjetivo " + (tipoObjetivoInstance.id ? tipoObjetivoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
