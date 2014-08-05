package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class UnidadIncopController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [unidadIncopInstanceList: UnidadIncop.list(params), params: params]
    } //list

    def form_ajax() {
        def unidadIncopInstance = new UnidadIncop(params)
        if(params.id) {
            unidadIncopInstance = UnidadIncop.get(params.id)
            if(!unidadIncopInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Unidad Incop con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [unidadIncopInstance: unidadIncopInstance]
    } //form_ajax

    def save() {

//        println("params " + params)

        params.codigo = params.codigo.toUpperCase();

        def unidadIncopInstance
        if(params.id) {
            unidadIncopInstance = UnidadIncop.get(params.id)
            if(!unidadIncopInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Unidad Incop con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            unidadIncopInstance.properties = params
        }//es edit
        else {
            def existe= UnidadIncop.findByCodigo(params.codigo)
            if(!existe)
                unidadIncopInstance = new UnidadIncop(params)
            else{
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar el código ya existe."
                redirect(action: 'list')
                return
            }
        } //es create
        if (!unidadIncopInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Unidad Incop " + (unidadIncopInstance.id ? unidadIncopInstance.id : "") + "</h4>"

            str += "<ul>"
            unidadIncopInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Unidad Incop " + unidadIncopInstance.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Unidad Incop " + unidadIncopInstance.descripcion
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def unidadIncopInstance = UnidadIncop.get(params.id)
        if (!unidadIncopInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Unidad Incop con id " + params.id
            redirect(action: "list")
            return
        }
        [unidadIncopInstance: unidadIncopInstance]
    } //show

    def delete() {
        def unidadIncopInstance = UnidadIncop.get(params.id)
        if (!unidadIncopInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Unidad Incop con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            unidadIncopInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Unidad Incop " + unidadIncopInstance.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Unidad Incop " + (unidadIncopInstance.id ? unidadIncopInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
