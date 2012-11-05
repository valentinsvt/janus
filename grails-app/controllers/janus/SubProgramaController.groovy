package janus

import org.springframework.dao.DataIntegrityViolationException

class SubProgramaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [subProgramaInstanceList: SubPrograma.list(params), subProgramaInstanceTotal: SubPrograma.count(), params: params]
    } //list

    def form_ajax() {
        def subProgramaInstance = new SubPrograma(params)
        if (params.id) {
            subProgramaInstance = SubPrograma.get(params.id)
            if (!subProgramaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró SubPrograma con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [subProgramaInstance: subProgramaInstance]
    } //form_ajax

    def save() {
        def subProgramaInstance
        if (params.id) {
            subProgramaInstance = SubPrograma.get(params.id)
            if (!subProgramaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró SubPrograma con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            subProgramaInstance.properties = params
        }//es edit
        else {
            subProgramaInstance = new SubPrograma(params)
        } //es create
        if (!subProgramaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar SubPrograma " + (subProgramaInstance.id ? subProgramaInstance.id : "") + "</h4>"

            str += "<ul>"
            subProgramaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamete SubPrograma " + subProgramaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamete SubPrograma " + subProgramaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def subProgramaInstance = SubPrograma.get(params.id)
        if (!subProgramaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró SubPrograma con id " + params.id
            redirect(action: "list")
            return
        }
        [subProgramaInstance: subProgramaInstance]
    } //show

    def delete() {
        def subProgramaInstance = SubPrograma.get(params.id)
        if (!subProgramaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró SubPrograma con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            subProgramaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamete SubPrograma " + subProgramaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar SubPrograma " + (subProgramaInstance.id ? subProgramaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
