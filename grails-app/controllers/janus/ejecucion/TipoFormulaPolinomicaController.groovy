package janus.ejecucion

import org.springframework.dao.DataIntegrityViolationException

class TipoFormulaPolinomicaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoFormulaPolinomicaInstanceList: TipoFormulaPolinomica.list(params), params: params]
    } //list

    def form_ajax() {
        def tipoFormulaPolinomicaInstance = new TipoFormulaPolinomica(params)
        if (params.id) {
            tipoFormulaPolinomicaInstance = TipoFormulaPolinomica.get(params.id)
            if (!tipoFormulaPolinomicaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Formula Polinomica con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoFormulaPolinomicaInstance: tipoFormulaPolinomicaInstance]
    } //form_ajax

    def save() {

        params.codigo = params.codigo.toUpperCase();

        def existe = TipoFormulaPolinomica.findByCodigo(params.codigo)

        def tipoFormulaPolinomicaInstance
        if (params.id) {
            tipoFormulaPolinomicaInstance = TipoFormulaPolinomica.get(params.id)
            if (!tipoFormulaPolinomicaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Formula Polinomica con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoFormulaPolinomicaInstance.properties = params
        }//es edit
        else {

            if(!existe){
                tipoFormulaPolinomicaInstance = new TipoFormulaPolinomica(params)
            }else{
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar la fórmula polinómica, el código ya existe!!"
                redirect(action: 'list')
                return
            }


        } //es create
        if (!tipoFormulaPolinomicaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Formula Polinomica " + (tipoFormulaPolinomicaInstance.id ? tipoFormulaPolinomicaInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoFormulaPolinomicaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Formula Polinomica " + tipoFormulaPolinomicaInstance?.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Formula Polinomica " + tipoFormulaPolinomicaInstance?.descripcion
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoFormulaPolinomicaInstance = TipoFormulaPolinomica.get(params.id)
        if (!tipoFormulaPolinomicaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Formula Polinomica con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoFormulaPolinomicaInstance: tipoFormulaPolinomicaInstance]
    } //show

    def delete() {
        def tipoFormulaPolinomicaInstance = TipoFormulaPolinomica.get(params.id)
        if (!tipoFormulaPolinomicaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Formula Polinomica con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoFormulaPolinomicaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Tipo Formula Polinomica " + tipoFormulaPolinomicaInstance?.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Tipo Formula Polinomica " + (tipoFormulaPolinomicaInstance.id ? tipoFormulaPolinomicaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
