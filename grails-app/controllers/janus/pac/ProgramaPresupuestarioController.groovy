package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class ProgramaPresupuestarioController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [programaPresupuestarioInstanceList: ProgramaPresupuestario.list(params), params: params]
    } //list

    def form_ajax() {
        def programaPresupuestarioInstance = new ProgramaPresupuestario(params)
        if (params.id) {
            programaPresupuestarioInstance = ProgramaPresupuestario.get(params.id)
            if (!programaPresupuestarioInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Programa Presupuestario con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [programaPresupuestarioInstance: programaPresupuestarioInstance]
    } //form_ajax

    def save() {
        def programaPresupuestarioInstance
        if (params.id) {
            programaPresupuestarioInstance = ProgramaPresupuestario.get(params.id)
            if (!programaPresupuestarioInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Programa Presupuestario con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            programaPresupuestarioInstance.properties = params
        }//es edit
        else {
            programaPresupuestarioInstance = new ProgramaPresupuestario(params)
        } //es create
        if (!programaPresupuestarioInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Programa Presupuestario " + (programaPresupuestarioInstance.id ? programaPresupuestarioInstance.id : "") + "</h4>"

            str += "<ul>"
            programaPresupuestarioInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
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
            flash.message = "Se ha actualizado correctamente Programa Presupuestario " + programaPresupuestarioInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Programa Presupuestario " + programaPresupuestarioInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def programaPresupuestarioInstance = ProgramaPresupuestario.get(params.id)
        if (!programaPresupuestarioInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Programa Presupuestario con id " + params.id
            redirect(action: "list")
            return
        }
        [programaPresupuestarioInstance: programaPresupuestarioInstance]
    } //show

    def delete() {
        def programaPresupuestarioInstance = ProgramaPresupuestario.get(params.id)
        if (!programaPresupuestarioInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Programa Presupuestario con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            programaPresupuestarioInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Programa Presupuestario " + programaPresupuestarioInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Programa Presupuestario " + (programaPresupuestarioInstance.id ? programaPresupuestarioInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
