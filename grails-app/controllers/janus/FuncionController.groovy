package janus

import org.springframework.dao.DataIntegrityViolationException

class FuncionController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [funcionInstanceList: Funcion.list(params), funcionInstanceTotal: Funcion.count(), params: params]
    } //list

    def form_ajax() {
        def funcionInstance = new Funcion(params)
        if (params.id) {
            funcionInstance = Funcion.get(params.id)
            if (!funcionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Funcion con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [funcionInstance: funcionInstance]
    } //form_ajax

    def save() {
        params.codigo = params.codigo.toUpperCase();

        def existe = Funcion.findByCodigo(params.codigo)

        def funcionInstance
        if (params.id) {
            funcionInstance = Funcion.get(params.id)
            if (!funcionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Funcion con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            funcionInstance.properties = params
        }//es edit
        else {
            if(!existe){
                funcionInstance = new Funcion(params)
            }else{
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar la función, el código ya existe!!"
                redirect(action: 'list')
                return
            }

        } //es create
        if (!funcionInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Funcion " + (funcionInstance.id ? funcionInstance.id : "") + "</h4>"

            str += "<ul>"
            funcionInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Funcion " + funcionInstance.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Funcion " + funcionInstance.descripcion
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def funcionInstance = Funcion.get(params.id)
        if (!funcionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Funcion con id " + params.id
            redirect(action: "list")
            return
        }
        [funcionInstance: funcionInstance]
    } //show

    def delete() {
        def funcionInstance = Funcion.get(params.id)
        if (!funcionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Funcion con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            funcionInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Funcion " + funcionInstance.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Funcion " + (funcionInstance.descripcion ? funcionInstance.descripcion : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
