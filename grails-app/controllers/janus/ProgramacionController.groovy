package janus

import org.springframework.dao.DataIntegrityViolationException

class ProgramacionController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [programacionInstanceList: Programacion.list(params), programacionInstanceTotal: Programacion.count(), params: params]
    } //list

    def form_ajax() {
        def programacionInstance = new Programacion(params)
        if (params.id) {
            programacionInstance = Programacion.get(params.id)
            if (!programacionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Programacion con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [programacionInstance: programacionInstance]
    } //form_ajax

    def form_ext_ajax() {

//        println("form" + params)

        def grupo = params.grupo

        def programacionInstance = new Programacion(params)

        if (params.id) {
            programacionInstance = Programacion.get(params.id)
            if (!programacionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Programacion con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [programacionInstance: programacionInstance, grupo: grupo]
    } //form_ajax

    def save() {


//        println("params " + params )

        def fechaI = new Date().parse("dd-MM-yyyy", params.fechaInicio)
        def fechaF = new Date().parse("dd-MM-yyyy", params.fechaFin)

//        println("fechas" + fechaI + ' ' + fechaF)

        params.fechaInicio = fechaI
        params.fechaFin = fechaF


        def programacionInstance
        if (params.id) {
            programacionInstance = Programacion.get(params.id)
            if (!programacionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Programacion con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            programacionInstance.properties = params
        }//es edit
        else {
            programacionInstance = new Programacion(params)
        } //es create
        if (!programacionInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Programacion " + (programacionInstance.id ? programacionInstance.id : "") + "</h4>"

            str += "<ul>"
            programacionInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Programación " + programacionInstance?.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Programación " + programacionInstance?.descripcion
        }
        redirect(action: 'list')
    } //save

    def save_ext() {
//        println ("save" + params)

        def grupo = Grupo.get(params.grupo)

        params.grupo = grupo

        def programa = Programacion.findAllByGrupo(grupo)

        def programacionInstance, message

        if (params.fechaInicio) {
            params.fechaInicio = new Date().parse("dd-MM-yyyy", params.fechaInicio)
        }
        if (params.fechaFin) {
            params.fechaFin = new Date().parse("dd-MM-yyyy", params.fechaFin)
        }

        if (params.id) {
            programacionInstance = Programacion.get(params.id)
            if (!programacionInstance) {
                message = "No se encontró Programacion con id " + params.id
                println message
                render "error"
                return
            }//no existe el objeto
            programacionInstance.properties = params
        }//es edit
        else {
            programacionInstance = new Programacion(params)

        } //es create
        if (!programacionInstance.save(flush: true)) {
            def str = "<h4>No se pudo guardar Programacion " + (programacionInstance.id ? programacionInstance.id : "") + "</h4>"

            str += "<ul>"
            programacionInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            message = str
            println message
            render "error"
            return
        }

        if (params.id) {
            message = "Se ha actualizado correctamente Programacion " + programacionInstance.id
        } else {
            message = "Se ha creado correctamente Programacion " + programacionInstance.id
        }
        println message

//        def sel = g.select(id: "programacion", name: "programacion.id", "class": "programacion required", from: Programacion?.list(), value: programacionInstance.id, optionValue: "descripcion",
//                optionKey: "id", title: "Programa")

        def sel = g.select(id: "programacion", name: "programacion.id", "class": "programacion required", from: Programacion.findAllByGrupo(grupo), value: programacionInstance.id, optionValue: "descripcion",
                optionKey: "id", title: "Programa")

//        <g:select id="programacion" name="programacion.id" class="programacion required" from="${programa}" value="${obra?.programacion?.id}" optionValue="descripcion" optionKey="id" title="Programa"/>




                render sel
    } //save

    def show_ajax() {
        def programacionInstance = Programacion.get(params.id)
        if (!programacionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Programacion con id " + params.id
            redirect(action: "list")
            return
        }
        [programacionInstance: programacionInstance]
    } //show

    def delete() {
        def programacionInstance = Programacion.get(params.id)
        if (!programacionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Programacion con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            programacionInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Programacion " + programacionInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Programacion " + (programacionInstance.id ? programacionInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
