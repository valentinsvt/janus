package janus

import org.springframework.dao.DataIntegrityViolationException

class SubPresupuestoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [subPresupuestoInstanceList: SubPresupuesto.list(params), params: params]
    } //list

    def form_ajax() {
        def subPresupuestoInstance = new SubPresupuesto(params)
        if(params.id) {
            subPresupuestoInstance = SubPresupuesto.get(params.id)
            if(!subPresupuestoInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Sub Presupuesto con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [subPresupuestoInstance: subPresupuestoInstance]
    } //form_ajax

    def save() {
        def subPresupuestoInstance
        if(params.id) {
            subPresupuestoInstance = SubPresupuesto.get(params.id)
            if(!subPresupuestoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Sub Presupuesto con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            subPresupuestoInstance.properties = params
        }//es edit
        else {
            subPresupuestoInstance = new SubPresupuesto(params)
        } //es create
        if (!subPresupuestoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Sub Presupuesto " + (subPresupuestoInstance.id ? subPresupuestoInstance.id : "") + "</h4>"

            str += "<ul>"
            subPresupuestoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Sub Presupuesto " + subPresupuestoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Sub Presupuesto " + subPresupuestoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def subPresupuestoInstance = SubPresupuesto.get(params.id)
        if (!subPresupuestoInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Sub Presupuesto con id " + params.id
            redirect(action: "list")
            return
        }
        [subPresupuestoInstance: subPresupuestoInstance]
    } //show

    def delete() {
        def subPresupuestoInstance = SubPresupuesto.get(params.id)
        if (!subPresupuestoInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Sub Presupuesto con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            subPresupuestoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Sub Presupuesto " + subPresupuestoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Sub Presupuesto " + (subPresupuestoInstance.id ? subPresupuestoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
