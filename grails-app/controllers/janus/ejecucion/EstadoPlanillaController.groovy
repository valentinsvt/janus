package janus.ejecucion



import org.springframework.dao.DataIntegrityViolationException

class EstadoPlanillaController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [estadoPlanillaInstanceList: EstadoPlanilla.list(params), params: params]
    } //list

    def form_ajax() {
        def estadoPlanillaInstance = new EstadoPlanilla(params)
        if (params.id) {
            estadoPlanillaInstance = EstadoPlanilla.get(params.id)
            if (!estadoPlanillaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Estado Planilla con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [estadoPlanillaInstance: estadoPlanillaInstance]
    } //form_ajax

    def save() {
        def estadoPlanillaInstance
        if (params.id) {
            estadoPlanillaInstance = EstadoPlanilla.get(params.id)
            if (!estadoPlanillaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Estado Planilla con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            estadoPlanillaInstance.properties = params
        }//es edit
        else {
            estadoPlanillaInstance = new EstadoPlanilla(params)
        } //es create
        if (!estadoPlanillaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Estado Planilla " + (estadoPlanillaInstance.id ? estadoPlanillaInstance.id : "") + "</h4>"

            str += "<ul>"
            estadoPlanillaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Estado Planilla " + estadoPlanillaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Estado Planilla " + estadoPlanillaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def estadoPlanillaInstance = EstadoPlanilla.get(params.id)
        if (!estadoPlanillaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Estado Planilla con id " + params.id
            redirect(action: "list")
            return
        }
        [estadoPlanillaInstance: estadoPlanillaInstance]
    } //show

    def delete() {
        def estadoPlanillaInstance = EstadoPlanilla.get(params.id)
        if (!estadoPlanillaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Estado Planilla con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            estadoPlanillaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Estado Planilla " + estadoPlanillaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Estado Planilla " + (estadoPlanillaInstance.id ? estadoPlanillaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
