package janus

import org.springframework.dao.DataIntegrityViolationException

class NotaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [notaInstanceList: Nota.list(params), params: params]
    } //list

    def form_ajax() {
        def notaInstance = new Nota(params)
        if (params.id) {
            notaInstance = Nota.get(params.id)
            if (!notaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Nota con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [notaInstance: notaInstance]
    } //form_ajax

    def save() {

//         println "params "+params
        if (params.piePaginaSel) {
            if (params.piePaginaSel != '-1'){
                params.id = params.piePaginaSel
            }else{
                params.id = null
            }
        }

        def notaInstance
        if (params.id) {
            notaInstance = Nota.get(params.id)
            if (!notaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Nota con id " + params.id
                redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
                return
            }//no existe el objeto
            notaInstance.properties = params
        }//es edit
        else {
            notaInstance = new Nota(params)
        } //es create
        if (!notaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Nota " + (notaInstance.id ? notaInstance.id : "") + "</h4>"

            str += "<ul>"
            notaInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
//            redirect(action: 'list')
            return
        }

        def grabado = ''

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente la Nota " + notaInstance.descripcion

            grabado = '1'

        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente la Nota " + notaInstance.descripcion
            grabado = '2'
        }
//        redirect(action: 'list')
//        redirect(controller: 'documentosObra',action: 'documentosObra',id: params.obra)
          render grabado
    } //save



    def saveNota () {


        println ("params saveNota "+ params)

        if (params.piePaginaSel) {
            if (params.piePaginaSel != '-1'){
                params.id = params.piePaginaSel
            }else{
                params.id = null
            }
        }

        def notaInstance
        if (params.id) {
            notaInstance = Nota.get(params.id)
            if (!notaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Nota con id " + params.id
//                redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
                return
            }//no existe el objeto
            notaInstance.properties = params
        }//es edit
        else {
            notaInstance = new Nota(params)
        } //es create
        if (!notaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Nota " + (notaInstance.id ? notaInstance.id : "") + "</h4>"

            str += "<ul>"
            notaInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
//            redirect(action: 'list')
            return
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente la Nota " + notaInstance.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente la Nota " + notaInstance.descripcion
        }
//        redirect(action: 'list')
//        redirect(controller: 'documentosObra',action: 'documentosObra',id: params.obra)
        render "ok_"+notaInstance?.id
    }

    def show_ajax() {
        def notaInstance = Nota.get(params.id)
        if (!notaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Nota con id " + params.id
//            redirect(action: "list")
            return
        }
        [notaInstance: notaInstance]
    } //show

    def delete() {
        def mensaje
//        println("params" + params)

        def notaInstance = Nota.get(params.id)
        if (!notaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Nota con id " + params.id
//            redirect(controller: 'documentosObra', action: "documentosObra", id: params.obra)
            render "no"
            return
        }

        try {
            notaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Nota " + notaInstance.id
            render "ok"
//            redirect(controller: 'documentosObra', action: "documentosObra", id: params.obra)
            return
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Nota " + (notaInstance.id ? notaInstance.id : "")
            render "no"
//            redirect(controller: 'documentosObra', action: "documentosObra", id: params.obra)
        }

//        redirect(controller: 'documentosObra', action: "documentosObra", id: params.obra)

    } //delete
} //fin controller
