package janus

import org.springframework.dao.DataIntegrityViolationException

class ItemController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [itemInstanceList: Item.list(params), itemInstanceTotal: Item.count(), params: params]
    } //list

    def mantenimientoPrecios(){


        def lugar = Lugar.get(14)

        def rubroPrecio = PrecioRubrosItems.findAllByLugar(lugar,[max:10])



        [rubroPrecio: rubroPrecio, lugar:lugar]

    }

    def form_ajax() {
        def itemInstance = new Item(params)
        if (params.id) {
            itemInstance = Item.get(params.id)
            if (!itemInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Item con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [itemInstance: itemInstance]
    } //form_ajax

    def save() {
        def itemInstance
        if (params.id) {
            itemInstance = Item.get(params.id)
            if (!itemInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Item con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            itemInstance.properties = params
        }//es edit
        else {
            itemInstance = new Item(params)
        } //es create
        if (!itemInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Item " + (itemInstance.id ? itemInstance.id : "") + "</h4>"

            str += "<ul>"
            itemInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamete Item " + itemInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamete Item " + itemInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def itemInstance = Item.get(params.id)
        if (!itemInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Item con id " + params.id
            redirect(action: "list")
            return
        }
        [itemInstance: itemInstance]
    } //show

    def delete() {
        def itemInstance = Item.get(params.id)
        if (!itemInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Item con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            itemInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamete Item " + itemInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Item " + (itemInstance.id ? itemInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
