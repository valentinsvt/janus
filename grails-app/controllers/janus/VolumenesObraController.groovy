package janus

import org.springframework.dao.DataIntegrityViolationException

class VolumenesObraController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [volumenesObraInstanceList: VolumenesObra.list(params), volumenesObraInstanceTotal: VolumenesObra.count(), params: params]
    } //list

    def form_ajax() {
        def volumenesObraInstance = new VolumenesObra(params)
        if (params.id) {
            volumenesObraInstance = VolumenesObra.get(params.id)
            if (!volumenesObraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró VolumenesObra con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [volumenesObraInstance: volumenesObraInstance]
    } //form_ajax

    def save() {
        def volumenesObraInstance
        if (params.id) {
            volumenesObraInstance = VolumenesObra.get(params.id)
            if (!volumenesObraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró VolumenesObra con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            volumenesObraInstance.properties = params
        }//es edit
        else {
            volumenesObraInstance = new VolumenesObra(params)
        } //es create
        if (!volumenesObraInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar VolumenesObra " + (volumenesObraInstance.id ? volumenesObraInstance.id : "") + "</h4>"

            str += "<ul>"
            volumenesObraInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente VolumenesObra " + volumenesObraInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente VolumenesObra " + volumenesObraInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def volumenesObraInstance = VolumenesObra.get(params.id)
        if (!volumenesObraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró VolumenesObra con id " + params.id
            redirect(action: "list")
            return
        }
        [volumenesObraInstance: volumenesObraInstance]
    } //show

    def delete() {
        def volumenesObraInstance = VolumenesObra.get(params.id)
        if (!volumenesObraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró VolumenesObra con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            volumenesObraInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente VolumenesObra " + volumenesObraInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar VolumenesObra " + (volumenesObraInstance.id ? volumenesObraInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
