package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class AseguradoraController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [aseguradoraInstanceList: Aseguradora.list(params), params: params]
    } //list

    def form_ajax() {
        def aseguradoraInstance = new Aseguradora(params)
        if (params.id) {
            aseguradoraInstance = Aseguradora.get(params.id)
            if (!aseguradoraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Aseguradora con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [aseguradoraInstance: aseguradoraInstance]
    } //form_ajax

    def save() {


        println("params" + params)

        def fecha

        if(params.fechaContacto){
            fecha = new Date().parse("dd-MM-yyyy", params.fechaContacto)
        }else{
//            fecha = new Date()
        }



        def aseguradoraInstance
        if (params.id) {
            aseguradoraInstance = Aseguradora.get(params.id)
            if(params.fechaContacto){
                params.fechaContacto=new Date().parse("dd-MM-yyyy",params.fechaContacto)
            }
            if (!aseguradoraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Aseguradora con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            aseguradoraInstance.properties = params
        }//es edit
        else {

            params.fechaContacto = fecha
            aseguradoraInstance = new Aseguradora(params)
        } //es create
        if (!aseguradoraInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Aseguradora " + (aseguradoraInstance.id ? aseguradoraInstance.id : "") + "</h4>"

            str += "<ul>"
            aseguradoraInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Aseguradora " + aseguradoraInstance?.nombre
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Aseguradora " + aseguradoraInstance?.nombre
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def aseguradoraInstance = Aseguradora.get(params.id)
        if (!aseguradoraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Aseguradora con id " + params.id
            redirect(action: "list")
            return
        }
        [aseguradoraInstance: aseguradoraInstance]
    } //show

    def delete() {
        def aseguradoraInstance = Aseguradora.get(params.id)
        if (!aseguradoraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Aseguradora con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            aseguradoraInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Aseguradora " + aseguradoraInstance?.nombre
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Aseguradora " + (aseguradoraInstance.id ? aseguradoraInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
