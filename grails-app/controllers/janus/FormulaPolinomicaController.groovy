package janus

import org.springframework.dao.DataIntegrityViolationException

class FormulaPolinomicaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [formulaPolinomicaInstanceList: FormulaPolinomica.list(params), formulaPolinomicaInstanceTotal: FormulaPolinomica.count(), params: params]
    } //list

    def form_ajax() {
        def formulaPolinomicaInstance = new FormulaPolinomica(params)
        if (params.id) {
            formulaPolinomicaInstance = FormulaPolinomica.get(params.id)
            if (!formulaPolinomicaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró FormulaPolinomica con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [formulaPolinomicaInstance: formulaPolinomicaInstance]
    } //form_ajax

    def save() {
        def formulaPolinomicaInstance
        if (params.id) {
            formulaPolinomicaInstance = FormulaPolinomica.get(params.id)
            if (!formulaPolinomicaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró FormulaPolinomica con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            formulaPolinomicaInstance.properties = params
        }//es edit
        else {
            formulaPolinomicaInstance = new FormulaPolinomica(params)
        } //es create
        if (!formulaPolinomicaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar FormulaPolinomica " + (formulaPolinomicaInstance.id ? formulaPolinomicaInstance.id : "") + "</h4>"

            str += "<ul>"
            formulaPolinomicaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamete FormulaPolinomica " + formulaPolinomicaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamete FormulaPolinomica " + formulaPolinomicaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def formulaPolinomicaInstance = FormulaPolinomica.get(params.id)
        if (!formulaPolinomicaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró FormulaPolinomica con id " + params.id
            redirect(action: "list")
            return
        }
        [formulaPolinomicaInstance: formulaPolinomicaInstance]
    } //show

    def delete() {
        def formulaPolinomicaInstance = FormulaPolinomica.get(params.id)
        if (!formulaPolinomicaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró FormulaPolinomica con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            formulaPolinomicaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamete FormulaPolinomica " + formulaPolinomicaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar FormulaPolinomica " + (formulaPolinomicaInstance.id ? formulaPolinomicaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
