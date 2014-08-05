package janus

import org.springframework.dao.DataIntegrityViolationException

class DepartamentoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        def lista = Departamento.findAll("from Departamento order by direccion desc, descripcion")
        [departamentoInstanceList: lista, params: params]
    } //list

    def form_ajax() {
        def departamentoInstance = new Departamento(params)
        if(params.id) {
            departamentoInstance = Departamento.get(params.id)
            if(!departamentoInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró el Departamento con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [departamentoInstance: departamentoInstance]
    } //form_ajax

    def save() {

        params.codigo = params.codigo.toUpperCase();

        def departamentoInstance
        if(params.id) {
            departamentoInstance = Departamento.get(params.id)
            if(!departamentoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró el Departamento con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            departamentoInstance.properties = params
        }//es edit
        else {
            departamentoInstance = new Departamento(params)
        } //es create
        if (!departamentoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar el Departamento " + (departamentoInstance.id ? departamentoInstance.id : "") + "</h4>"

            str += "<ul>"
            departamentoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente el Departamento " + departamentoInstance.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente el Departamento " + departamentoInstance.descripcion
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def departamentoInstance = Departamento.get(params.id)
        if (!departamentoInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Departamento con id " + params.id
            redirect(action: "list")
            return
        }
        [departamentoInstance: departamentoInstance]
    } //show

    def delete() {
        def departamentoInstance = Departamento.get(params.id)
        if (!departamentoInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Departamento con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            departamentoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente el Departamento " + departamentoInstance.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar el Departamento " + (departamentoInstance.id ? departamentoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
