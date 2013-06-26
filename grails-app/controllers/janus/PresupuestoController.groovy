package janus

import org.springframework.dao.DataIntegrityViolationException

class PresupuestoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 15, 100)
        [presupuestoInstanceList: Presupuesto.list(params), presupuestoInstanceTotal: Presupuesto.count(), params: params]
    } //list

    def form_ajax() {
        def presupuestoInstance = new Presupuesto(params)
        if (params.id) {
            presupuestoInstance = Presupuesto.get(params.id)
            if (!presupuestoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Presupuesto con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [presupuestoInstance: presupuestoInstance]
    } //form_ajax

    def saveAjax() {
       println "save prsp "+params
        def presupuestoInstance
        if (params.id) {
            presupuestoInstance = Presupuesto.get(params.id)
            if (!presupuestoInstance) {
                render "error"
                return
            }//no existe el objeto
            presupuestoInstance.properties = params
        }//es edit
        else {
            presupuestoInstance = new Presupuesto(params)
        } //es create
        println "sprog "+presupuestoInstance.subPrograma
        if (!presupuestoInstance.save(flush: true)) {
           // println "error save prsp "+presupuestoInstance.errors
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Presupuesto " + (presupuestoInstance.id ? presupuestoInstance.id : "") + "</h4>"

            str += "<ul>"
            presupuestoInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"


            render "error"
            return
        }

        render "${presupuestoInstance.id}&${presupuestoInstance.numero}&${presupuestoInstance.descripcion}&${presupuestoInstance.fuente}&${presupuestoInstance.programa}&${presupuestoInstance.subPrograma}&${presupuestoInstance.proyecto}"
        return
    } //save

    def save() {

        def presupuestoInstance
        if (params.id) {
            presupuestoInstance = Presupuesto.get(params.id)
            if (!presupuestoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Presupuesto con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            presupuestoInstance.properties = params
        }//es edit
        else {
            presupuestoInstance = new Presupuesto(params)
        } //es create
        if (!presupuestoInstance.save(flush: true)) {

            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Presupuesto " + (presupuestoInstance.id ? presupuestoInstance.id : "") + "</h4>"

            str += "<ul>"
            presupuestoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Presupuesto " + presupuestoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Presupuesto " + presupuestoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def presupuestoInstance = Presupuesto.get(params.id)
        if (!presupuestoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Presupuesto con id " + params.id
            redirect(action: "list")
            return
        }
        [presupuestoInstance: presupuestoInstance]
    } //show

    def delete() {
        def presupuestoInstance = Presupuesto.get(params.id)
        if (!presupuestoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Presupuesto con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            presupuestoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Presupuesto " + presupuestoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Presupuesto " + (presupuestoInstance.id ? presupuestoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
