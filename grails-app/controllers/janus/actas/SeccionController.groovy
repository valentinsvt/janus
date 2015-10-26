package janus.actas

import groovy.json.JsonBuilder
import org.springframework.dao.DataIntegrityViolationException

class SeccionController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [seccionInstanceList: Seccion.list(params), params: params]
    } //list

    def form_ajax() {
        def seccionInstance = new Seccion(params)
        if (params.id) {
            seccionInstance = Seccion.get(params.id)
            if (!seccionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Seccion con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [seccionInstance: seccionInstance]
    } //form_ajax

    def updateNumeros() {
        println params
        def msg = "OK"
        def acta = Acta.get(params.acta)
        def seccion = Seccion.get(params.id)
        seccion.numero = params.numero.toInteger()

        if(seccion.numero < 1){
            seccion.numero = 1
        }

        if (!seccion.save(flush: true)) {
            println "error al renumerar la seccion " + seccion.errors
            msg = "NO"
        }
//        println "update este " + seccion.id + "   " + seccion.numero + "  " + seccion.titulo
        def cont = seccion.numero + 1

        def secciones = Seccion.withCriteria {
            eq("acta", acta)
            ge("numero", seccion.numero)
            ne("id", seccion.id)
            order("numero", "asc")
        }

        secciones.each { sec ->
//            println sec.id + "  " + sec.numero + "  " + sec.titulo + "    (" + cont + ")"
            if (sec.numero != cont) {
                sec.numero = cont
                if (!sec.save(flush: true)) {
                    println "error al renumerar la seccion " + sec.errors
                }
//                println "   -->" + sec.id + "   " + sec.numero
            }
            cont++
        }
        render msg
    }

    def form_ext_ajax() {
        def message
        def seccionInstance = new Seccion(params)
        if (params.id) {
            seccionInstance = Seccion.get(params.id)
            if (!seccionInstance) {
                message = "NO_No se encontró Seccion con id " + params.id
                render message
                return
            } //no existe el objeto
        } //es edit
        else {
            seccionInstance.acta = Acta.get(params.acta)
            seccionInstance.numero = params.numero.toInteger()
        }
        return [seccionInstance: seccionInstance]
    } //form_ext_ajax

    def save_ext() {
        def message
        def seccionInstance
        if (params.id) {
            seccionInstance = Seccion.get(params.id)
            if (!seccionInstance) {
                message = "NO_No se encontró Seccion con id " + params.id
                render message
                return
            }//no existe el objeto
            seccionInstance.properties = params
        }//es edit
        else {
            seccionInstance = new Seccion(params)
        } //es create
        if (!seccionInstance.save(flush: true)) {
            def str = "<h4>No se pudo guardar Seccion " + (seccionInstance.id ? seccionInstance.id : "") + "</h4>"

            str += "<ul>"
            seccionInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            message = "NO_" + str
            render message
            return
        }

        if (params.id) {
//            message = "OK_Se ha actualizado correctamente Seccion " + seccionInstance.id
            message = "update"
        } else {
//            message = "OK_Se ha creado correctamente Seccion " + seccionInstance.id
            message = "create"
        }
//        render message

        def elm = [
                id: seccionInstance.id,
                numero: seccionInstance.numero,
                titulo: seccionInstance.titulo,
                tipo: message
        ]
        def jsonSeccion = new JsonBuilder(elm)

        render jsonSeccion
    } //save_ext

    def delete_ext() {
        def message
        def seccionInstance = Seccion.get(params.id)
        def acta = seccionInstance.acta
        if (!seccionInstance) {
            message = "NO_No se encontró Seccion con id " + params.id
            render message
            return
        }

        try {
            seccionInstance.delete(flush: true)
            message = "OK_Se ha eliminado correctamente Seccion " + seccionInstance.titulo

            def cont = 1
            Seccion.findAllByActa(acta).each { sec ->
                if (sec.numero != cont) {
                    sec.numero = cont
                    if (!sec.save(flush: true)) {
                        println "error al renumerar la seccion " + sec.errors
                    }
                }
                cont++
            }

            render message
        }
        catch (DataIntegrityViolationException e) {
            message = "NO_No se pudo eliminar Seccion " + (seccionInstance.id ? seccionInstance.titulo : "")
            render message
        }
    } //delete_ext

    def save() {
        def seccionInstance
        if (params.id) {
            seccionInstance = Seccion.get(params.id)
            if (!seccionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Seccion con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            seccionInstance.properties = params
        }//es edit
        else {
            seccionInstance = new Seccion(params)
        } //es create
        if (!seccionInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Seccion " + (seccionInstance.id ? seccionInstance.id : "") + "</h4>"

            str += "<ul>"
            seccionInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Seccion " + seccionInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Seccion " + seccionInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def seccionInstance = Seccion.get(params.id)
        if (!seccionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Seccion con id " + params.id
            redirect(action: "list")
            return
        }
        [seccionInstance: seccionInstance]
    } //show

    def delete() {
        def seccionInstance = Seccion.get(params.id)
        if (!seccionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Seccion con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            seccionInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Seccion " + seccionInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Seccion " + (seccionInstance.id ? seccionInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
