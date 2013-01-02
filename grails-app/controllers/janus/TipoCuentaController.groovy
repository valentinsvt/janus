package janus

import org.springframework.dao.DataIntegrityViolationException

class TipoCuentaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [tipoCuentaInstanceList: TipoCuenta.list(params), tipoCuentaInstanceTotal: TipoCuenta.count(), params: params]
    } //list

    def form_ajax() {
        def tipoCuentaInstance = new TipoCuenta(params)
        if (params.id) {
            tipoCuentaInstance = TipoCuenta.get(params.id)
            if (!tipoCuentaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró TipoCuenta con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoCuentaInstance: tipoCuentaInstance]
    } //form_ajax

    def save() {
        def tipoCuentaInstance
        if (params.id) {
            tipoCuentaInstance = TipoCuenta.get(params.id)
            if (!tipoCuentaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró TipoCuenta con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoCuentaInstance.properties = params
        }//es edit
        else {
            tipoCuentaInstance = new TipoCuenta(params)
        } //es create
        if (!tipoCuentaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar TipoCuenta " + (tipoCuentaInstance.id ? tipoCuentaInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoCuentaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente TipoCuenta " + tipoCuentaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente TipoCuenta " + tipoCuentaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoCuentaInstance = TipoCuenta.get(params.id)
        if (!tipoCuentaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró TipoCuenta con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoCuentaInstance: tipoCuentaInstance]
    } //show

    def delete() {
        def tipoCuentaInstance = TipoCuenta.get(params.id)
        if (!tipoCuentaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró TipoCuenta con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoCuentaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente TipoCuenta " + tipoCuentaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar TipoCuenta " + (tipoCuentaInstance.id ? tipoCuentaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
