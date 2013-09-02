package janus.actas

import groovy.json.JsonBuilder
import org.springframework.dao.DataIntegrityViolationException

class ParrafoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [parrafoInstanceList: Parrafo.list(params), params: params]
    } //list

    def form_ajax() {
        def parrafoInstance = new Parrafo(params)
        if (params.id) {
            parrafoInstance = Parrafo.get(params.id)
            if (!parrafoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Parrafo con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [parrafoInstance: parrafoInstance]
    } //form_ajax

    def updateNumeros() {
        def seccion = Seccion.get(params.id)
        def cont = 1
        Parrafo.findAllBySeccion(seccion).each { par ->
            if (par.numero != cont) {
                par.numero = cont
                if (!par.save(flush: true)) {
                    println "error al renumerar el parrafo " + par.errors
                }
            }
            cont++
        }
    }

    def form_ext_ajax() {
//        println params
        def message
        def parrafoInstance = new Parrafo(params)
        if (params.id) {
            parrafoInstance = Parrafo.get(params.id)
            if (!parrafoInstance) {
                message = "NO_No se encontró Parrafo con id " + params.id
                render message
                return
            } //no existe el objeto
        } //es edit
        else {
            parrafoInstance.seccion = Seccion.get(params.seccion)
            parrafoInstance.numero = params.numero.toInteger()
        }
        return [parrafoInstance: parrafoInstance]
    } //form_ext_ajax

    def save_ext() {
        def message
        def parrafoInstance
        if (params.id) {
            parrafoInstance = Parrafo.get(params.id)
            if (!parrafoInstance) {
                message = "NO_No se encontró Parrafo con id " + params.id
                render message
                return
            }//no existe el objeto
            parrafoInstance.properties = params
        }//es edit
        else {
            parrafoInstance = new Parrafo(params)
        } //es create
        if (!parrafoInstance.save(flush: true)) {
            def str = "<h4>No se pudo guardar Parrafo " + (parrafoInstance.id ? parrafoInstance.id : "") + "</h4>"

            str += "<ul>"
            parrafoInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            message = "NO" + str
            render message
            return
        }

        if (params.id) {
//            flash.clase = "alert-success"
//            flash.message = "Se ha actualizado correctamente Parrafo " + parrafoInstance.id
            message = "update"
        } else {
//            flash.clase = "alert-success"
//            flash.message = "Se ha creado correctamente Parrafo " + parrafoInstance.id
            message = "create"
        }

        def elm = [
                id: parrafoInstance.id,
                numero: parrafoInstance.numero,
                contenido: parrafoInstance.contenido,
                tipoTabla: parrafoInstance.tipoTabla,
                tipo: message
        ]
        def jsonParrafo = new JsonBuilder(elm)
        render jsonParrafo
    } //save_ext

    def delete_ext() {
        def message
        def parrafoInstance = Parrafo.get(params.id)
        def seccion = parrafoInstance.seccion
        if (!parrafoInstance) {
            message = "NO_No se encontró Parrafo con id " + params.id
            render message
            return
        }

        try {
            parrafoInstance.delete(flush: true)
            message = "OK_Se ha eliminado correctamente Parrafo " + seccion.numero + "." + parrafoInstance.numero

            def cont = 1
            Parrafo.findAllBySeccion(seccion).each { par ->
                if (par.numero != cont) {
                    par.numero = cont
                    if (!par.save(flush: true)) {
                        println "error al renumerar el parrafo " + par.errors
                    }
                }
                cont++
            }

            render message
        }
        catch (DataIntegrityViolationException e) {
            message = "NO_No se pudo eliminar Parrafo " + (parrafoInstance.id ? seccion.numero + "." + parrafoInstance.numero : "")
            render message
        }
    } //delete_ext

    def save() {
        def parrafoInstance
        if (params.id) {
            parrafoInstance = Parrafo.get(params.id)
            if (!parrafoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Parrafo con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            parrafoInstance.properties = params
        }//es edit
        else {
            parrafoInstance = new Parrafo(params)
        } //es create
        if (!parrafoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Parrafo " + (parrafoInstance.id ? parrafoInstance.id : "") + "</h4>"

            str += "<ul>"
            parrafoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Parrafo " + parrafoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Parrafo " + parrafoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def parrafoInstance = Parrafo.get(params.id)
        if (!parrafoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Parrafo con id " + params.id
            redirect(action: "list")
            return
        }
        [parrafoInstance: parrafoInstance]
    } //show

    def delete() {
        def parrafoInstance = Parrafo.get(params.id)
        if (!parrafoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Parrafo con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            parrafoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Parrafo " + parrafoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Parrafo " + (parrafoInstance.id ? parrafoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
