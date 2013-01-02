package janus

import org.springframework.dao.DataIntegrityViolationException

class TipoInstitucionController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [tipoInstitucionInstanceList: TipoInstitucion.list(params), tipoInstitucionInstanceTotal: TipoInstitucion.count(), params: params]
    } //list

    def form_ajax() {
        def tipoInstitucionInstance = new TipoInstitucion(params)
        if (params.id) {
            tipoInstitucionInstance = TipoInstitucion.get(params.id)
            if (!tipoInstitucionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró TipoInstitucion con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoInstitucionInstance: tipoInstitucionInstance]
    } //form_ajax

    def save() {
        def tipoInstitucionInstance
        if (params.id) {
            tipoInstitucionInstance = TipoInstitucion.get(params.id)
            if (!tipoInstitucionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró TipoInstitucion con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoInstitucionInstance.properties = params
        }//es edit
        else {
            tipoInstitucionInstance = new TipoInstitucion(params)
        } //es create
        if (!tipoInstitucionInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar TipoInstitucion " + (tipoInstitucionInstance.id ? tipoInstitucionInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoInstitucionInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente TipoInstitucion " + tipoInstitucionInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente TipoInstitucion " + tipoInstitucionInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoInstitucionInstance = TipoInstitucion.get(params.id)
        if (!tipoInstitucionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró TipoInstitucion con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoInstitucionInstance: tipoInstitucionInstance]
    } //show

    def delete() {
        def tipoInstitucionInstance = TipoInstitucion.get(params.id)
        if (!tipoInstitucionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró TipoInstitucion con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoInstitucionInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente TipoInstitucion " + tipoInstitucionInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar TipoInstitucion " + (tipoInstitucionInstance.id ? tipoInstitucionInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
