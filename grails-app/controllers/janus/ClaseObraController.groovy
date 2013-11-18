package janus

import org.springframework.dao.DataIntegrityViolationException

class ClaseObraController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 15, 100)
        [claseObraInstanceList: ClaseObra.list(params), claseObraInstanceTotal: ClaseObra.count(), params: params]
    } //list

    def form_ext_ajax() {

        def grupo = params.grupo

        def claseObraInstance = new ClaseObra(params)
        if (params.id) {
            claseObraInstance = ClaseObra.get(params.id)
            if (!claseObraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró ClaseObra con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [claseObraInstance: claseObraInstance, grupo: grupo]
    } //form_ajax

    def save_ext() {

        def grupo = Grupo.get(params.grupo)
        params.grupo = grupo

        def claseObraInstance, message
        if (params.id) {
            claseObraInstance = ClaseObra.get(params.id)
            if (!claseObraInstance) {
                message = "No se encontró ClaseObra con id " + params.id
                println message
                render "error"
                return
            }//no existe el objeto
            claseObraInstance.properties = params
        }//es edit
        else {
            claseObraInstance = new ClaseObra(params)
        } //es create
        if (!claseObraInstance.save(flush: true)) {
            def str = "<h4>No se pudo guardar ClaseObra " + (claseObraInstance.id ? claseObraInstance.id : "") + "</h4>"

            str += "<ul>"
            claseObraInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"
            println message
            render "error"
            return
        }

        if (params.id) {
            message = "Se ha actualizado correctamente ClaseObra " + claseObraInstance.id
        } else {
            message = "Se ha creado correctamente ClaseObra " + claseObraInstance.id
        }

        println message
//        def sel = g.select(id: "claseObra", name: "claseObra.id", "class": "claseObra required", from: ClaseObra?.list(), value: claseObraInstance.id,
//                optionValue: "descripcion", optionKey: "id", style: "margin-left: -35px; width: 230px", title: "Clase de Obra")

        def sel = g.select(id: "claseObra", name: "claseObra.id", "class": "claseObra required", from: ClaseObra.findAllByGrupo(grupo), value: claseObraInstance.id,
                optionValue: "descripcion", optionKey: "id", style: "margin-left: -35px; width: 230px", title: "Clase de Obra")


        render sel
    } //save

    def form_ajax() {
        def claseObraInstance = new ClaseObra(params)
        if (params.id) {
            claseObraInstance = ClaseObra.get(params.id)
            if (!claseObraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró ClaseObra con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [claseObraInstance: claseObraInstance]
    } //form_ajax

    def save() {
        def claseObraInstance
        if (params.id) {
            claseObraInstance = ClaseObra.get(params.id)
            if (!claseObraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró ClaseObra con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            claseObraInstance.properties = params
        }//es edit
        else {
            claseObraInstance = new ClaseObra(params)
        } //es create
        if (!claseObraInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar ClaseObra " + (claseObraInstance.id ? claseObraInstance.id : "") + "</h4>"

            str += "<ul>"
            claseObraInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente ClaseObra " + claseObraInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente ClaseObra " + claseObraInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def claseObraInstance = ClaseObra.get(params.id)
        if (!claseObraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró ClaseObra con id " + params.id
            redirect(action: "list")
            return
        }
        [claseObraInstance: claseObraInstance]
    } //show

    def delete() {
        def claseObraInstance = ClaseObra.get(params.id)
        if (!claseObraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró ClaseObra con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            claseObraInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente ClaseObra " + claseObraInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar ClaseObra " + (claseObraInstance.id ? claseObraInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
