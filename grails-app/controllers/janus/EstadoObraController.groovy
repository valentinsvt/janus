package janus

import org.springframework.dao.DataIntegrityViolationException

class EstadoObraController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [estadoObraInstanceList: EstadoObra.list(params), estadoObraInstanceTotal: EstadoObra.count(), params: params]
    } //list

    def form_ajax() {
        def estadoObraInstance = new EstadoObra(params)
        if (params.id) {
            estadoObraInstance = EstadoObra.get(params.id)
            if (!estadoObraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró EstadoObra con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [estadoObraInstance: estadoObraInstance]
    } //form_ajax

    def save() {
        def estadoObraInstance

        params.codigo = params.codigo.toUpperCase();

        def existe = EstadoObra.findByCodigo(params.codigo)

        if (params.id) {
            estadoObraInstance = EstadoObra.get(params.id)
            if (!estadoObraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Estado Obra con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            estadoObraInstance.properties = params
        }//es edit
        else {

            if(!existe){
                estadoObraInstance = new EstadoObra(params)
            }else{
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar el estado de la obra, el código ya existe!!"
                redirect(action: 'list')
                return

            }


        } //es create
        if (!estadoObraInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Estado Obra " + (estadoObraInstance.id ? estadoObraInstance.id : "") + "</h4>"

            str += "<ul>"
            estadoObraInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Estado Obra: " + estadoObraInstance?.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Estado Obra " + estadoObraInstance?.descripcion
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def estadoObraInstance = EstadoObra.get(params.id)
        if (!estadoObraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Estado Obra con id " + params.id
            redirect(action: "list")
            return
        }
        [estadoObraInstance: estadoObraInstance]
    } //show

    def delete() {
        def estadoObraInstance = EstadoObra.get(params.id)
        if (!estadoObraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Estado Obra con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            estadoObraInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Estado Obra " + estadoObraInstance?.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Estado Obra " + (estadoObraInstance.id ? estadoObraInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
