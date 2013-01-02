package janus

import org.springframework.dao.DataIntegrityViolationException

class ItemsFormulaPolinomicaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [itemsFormulaPolinomicaInstanceList: ItemsFormulaPolinomica.list(params), itemsFormulaPolinomicaInstanceTotal: ItemsFormulaPolinomica.count(), params: params]
    } //list

    def form_ajax() {
        def itemsFormulaPolinomicaInstance = new ItemsFormulaPolinomica(params)
        if (params.id) {
            itemsFormulaPolinomicaInstance = ItemsFormulaPolinomica.get(params.id)
            if (!itemsFormulaPolinomicaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró ItemsFormulaPolinomica con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [itemsFormulaPolinomicaInstance: itemsFormulaPolinomicaInstance]
    } //form_ajax

    def save() {
        def itemsFormulaPolinomicaInstance
        if (params.id) {
            itemsFormulaPolinomicaInstance = ItemsFormulaPolinomica.get(params.id)
            if (!itemsFormulaPolinomicaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró ItemsFormulaPolinomica con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            itemsFormulaPolinomicaInstance.properties = params
        }//es edit
        else {
            itemsFormulaPolinomicaInstance = new ItemsFormulaPolinomica(params)
        } //es create
        if (!itemsFormulaPolinomicaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar ItemsFormulaPolinomica " + (itemsFormulaPolinomicaInstance.id ? itemsFormulaPolinomicaInstance.id : "") + "</h4>"

            str += "<ul>"
            itemsFormulaPolinomicaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente ItemsFormulaPolinomica " + itemsFormulaPolinomicaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente ItemsFormulaPolinomica " + itemsFormulaPolinomicaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def itemsFormulaPolinomicaInstance = ItemsFormulaPolinomica.get(params.id)
        if (!itemsFormulaPolinomicaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró ItemsFormulaPolinomica con id " + params.id
            redirect(action: "list")
            return
        }
        [itemsFormulaPolinomicaInstance: itemsFormulaPolinomicaInstance]
    } //show

    def delete() {
        def itemsFormulaPolinomicaInstance = ItemsFormulaPolinomica.get(params.id)
        if (!itemsFormulaPolinomicaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró ItemsFormulaPolinomica con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            itemsFormulaPolinomicaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente ItemsFormulaPolinomica " + itemsFormulaPolinomicaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar ItemsFormulaPolinomica " + (itemsFormulaPolinomicaInstance.id ? itemsFormulaPolinomicaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
