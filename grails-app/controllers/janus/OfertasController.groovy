package janus

import org.springframework.dao.DataIntegrityViolationException

class OfertasController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [ofertasInstanceList: Ofertas.list(params), ofertasInstanceTotal: Ofertas.count(), params: params]
    } //list

    def form_ajax() {
        def ofertasInstance = new Ofertas(params)
        if (params.id) {
            ofertasInstance = Ofertas.get(params.id)
            if (!ofertasInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Ofertas con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [ofertasInstance: ofertasInstance]
    } //form_ajax

    def save() {
        def ofertasInstance
        if (params.id) {
            ofertasInstance = Ofertas.get(params.id)
            if (!ofertasInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Ofertas con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            ofertasInstance.properties = params
        }//es edit
        else {
            ofertasInstance = new Ofertas(params)
        } //es create
        if (!ofertasInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Ofertas " + (ofertasInstance.id ? ofertasInstance.id : "") + "</h4>"

            str += "<ul>"
            ofertasInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamete Ofertas " + ofertasInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamete Ofertas " + ofertasInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def ofertasInstance = Ofertas.get(params.id)
        if (!ofertasInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Ofertas con id " + params.id
            redirect(action: "list")
            return
        }
        [ofertasInstance: ofertasInstance]
    } //show

    def delete() {
        def ofertasInstance = Ofertas.get(params.id)
        if (!ofertasInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Ofertas con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            ofertasInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamete Ofertas " + ofertasInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Ofertas " + (ofertasInstance.id ? ofertasInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
