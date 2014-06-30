package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class MonedaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [monedaInstanceList: Moneda.list(params), params: params]
    } //list

    def form_ajax() {
        def monedaInstance = new Moneda(params)
        if (params.id) {
            monedaInstance = Moneda.get(params.id)
            if (!monedaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Moneda con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [monedaInstance: monedaInstance]
    } //form_ajax

    def save() {
//        println params

        params.codigo = params.codigo.toUpperCase();

        def existe = Moneda.findByCodigo(params.codigo)

        def monedaInstance
        if (params.id) {
            monedaInstance = Moneda.get(params.id)
            if (!monedaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Moneda con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            monedaInstance.properties = params
        }//es edit
        else {
            if(!existe){

                monedaInstance = new Moneda(params)
            }else{
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar la moneda, el código ya existe!!"
                redirect(action: 'list')
                return
            }


        } //es create
        if (!monedaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Moneda " + (monedaInstance.id ? monedaInstance.id : "") + "</h4>"

            str += "<ul>"
            monedaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Moneda " + monedaInstance?.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Moneda " + monedaInstance?.descripcion
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def monedaInstance = Moneda.get(params.id)
        if (!monedaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Moneda con id " + params.id
            redirect(action: "list")
            return
        }
        [monedaInstance: monedaInstance]
    } //show

    def delete() {
        def monedaInstance = Moneda.get(params.id)
        if (!monedaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Moneda con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            monedaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Moneda " + monedaInstance?.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Moneda " + (monedaInstance.id ? monedaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
