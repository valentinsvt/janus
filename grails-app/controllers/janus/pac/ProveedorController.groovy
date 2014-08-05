package janus.pac

import org.springframework.dao.DataIntegrityViolationException



class ProveedorController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [proveedorInstanceList: Proveedor.list(params), params: params]
    } //list

    def form_ajax() {
        def proveedorInstance = new Proveedor(params)
        if (params.id) {
            proveedorInstance = Proveedor.get(params.id)
            if (!proveedorInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Proveedor con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [proveedorInstance: proveedorInstance]
    } //form_ajax

    def form() {
        def proveedorInstance = new Proveedor(params)
        if (params.id) {
            proveedorInstance = Proveedor.get(params.id)
            if (!proveedorInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Proveedor con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [proveedorInstance: proveedorInstance]
    } //form_ajax

    def form_ajax_fo() {
        def proveedorInstance = new Proveedor(params)
        if (params.id) {
            proveedorInstance = Proveedor.get(params.id)
            if (!proveedorInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Proveedor con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [proveedorInstance: proveedorInstance]
    } //form_ajax

    def saveFo() {
        if (params.fechaContacto) {
            params.fechaContacto = new Date().parse('dd-MM-yyyy', params.fechaContacto)
        }
        def proveedorInstance
        if (params.id) {
            proveedorInstance = Proveedor.get(params.id)
            if (!proveedorInstance) {
                println "nop"
                render "NO"
                return
            }//no existe el objeto
            proveedorInstance.properties = params
        }//es edit
        else {
            proveedorInstance = new Proveedor(params)
        } //es create
        if (!proveedorInstance.save(flush: true)) {
            println "error: " + proveedorInstance.errors
            render "NO"
            return
        }

        /*
          <g:select id="proveedor" name="proveedor.id" from="${janus.pac.Proveedor.list()}" optionKey="id" class="many-to-one "
                          value="${ofertaInstance?.proveedor?.id}" noSelection="['null': '']" optionValue="nombre"/>
         */
        def sel = g.select(id: "proveedor", name: 'proveedor.id', from: Proveedor.list([sort: 'nombre']), optionKey: "id", optionValue: "nombre", "class": "many-to-one", value: proveedorInstance.id, noSelection: ['null': ''])

        render sel
    }

    def save() {


//        println("params: " + params)
        if(params.fechaContacto){
            params.fechaContacto = new Date().parse("dd-MM-yyyy", params.fechaContacto)
        }



        def proveedorInstance
        if (params.id) {
            proveedorInstance = Proveedor.get(params.id)
            if (!proveedorInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Proveedor con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            proveedorInstance.properties = params
        }//es edit
        else {
            proveedorInstance = new Proveedor(params)
        } //es create
        if (!proveedorInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Proveedor " + (proveedorInstance.id ? proveedorInstance.id : "") + "</h4>"

            str += "<ul>"
            proveedorInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Proveedor " + proveedorInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Proveedor " + proveedorInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def proveedorInstance = Proveedor.get(params.id)
        if (!proveedorInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Proveedor con id " + params.id
            redirect(action: "list")
            return
        }
        [proveedorInstance: proveedorInstance]
    } //show

    def delete() {
        def proveedorInstance = Proveedor.get(params.id)
        if (!proveedorInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Proveedor con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            proveedorInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Proveedor " + proveedorInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Proveedor " + (proveedorInstance.id ? proveedorInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
