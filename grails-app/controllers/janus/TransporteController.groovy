package janus

import org.springframework.dao.DataIntegrityViolationException

class TransporteController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [transporteInstanceList: Transporte.list(params), transporteInstanceTotal: Transporte.count(), params: params]
    } //list

    def form_ajax() {
        def transporteInstance = new Transporte(params)
        if (params.id) {
            transporteInstance = Transporte.get(params.id)
            if (!transporteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Transporte con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [transporteInstance: transporteInstance]
    } //form_ajax

    def save() {
        params.codigo = params.codigo.toUpperCase();

        def existe = Transporte.findByCodigo(params.codigo)

        def transporteInstance
        if (params.id) {
            transporteInstance = Transporte.get(params.id)
            if (!transporteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Transporte con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto

            transporteInstance.properties = params
        }//es edit
        else {
            if(!existe){
                transporteInstance = new Transporte(params)
            }else{
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar el transporte, el código ya existe!!"
                redirect(action: 'list')
                return
            }

        } //es create
        if (!transporteInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Transporte " + (transporteInstance.id ? transporteInstance.id : "") + "</h4>"

            str += "<ul>"
            transporteInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Transporte " + transporteInstance.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Transporte " + transporteInstance.descripcion
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def transporteInstance = Transporte.get(params.id)
        if (!transporteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Transporte con id " + params.id
            redirect(action: "list")
            return
        }
        [transporteInstance: transporteInstance]
    } //show

    def delete() {
        def transporteInstance = Transporte.get(params.id)
        if (!transporteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Transporte con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            transporteInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Transporte " + transporteInstance.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Transporte " + (transporteInstance.descripcion ? transporteInstance.descripcion : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
