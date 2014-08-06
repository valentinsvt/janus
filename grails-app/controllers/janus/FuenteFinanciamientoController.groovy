package janus

import org.springframework.dao.DataIntegrityViolationException

class FuenteFinanciamientoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [fuenteFinanciamientoInstanceList: FuenteFinanciamiento.list(params), params: params]
    } //list

    def form_ajax() {
        def fuenteFinanciamientoInstance = new FuenteFinanciamiento(params)
        if (params.id) {
            fuenteFinanciamientoInstance = FuenteFinanciamiento.get(params.id)
            if (!fuenteFinanciamientoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Fuente Financiamiento con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [fuenteFinanciamientoInstance: fuenteFinanciamientoInstance]
    } //form_ajax

    def save() {

//        println("params " + params)

        params.descripcion = params.descripcion.toUpperCase();

        def existe = FuenteFinanciamiento.findByDescripcion(params.descripcion)

        def fuenteFinanciamientoInstance
        if (params.id) {
            fuenteFinanciamientoInstance = FuenteFinanciamiento.get(params.id)
            if (!fuenteFinanciamientoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Fuente Financiamiento con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            fuenteFinanciamientoInstance.properties = params
        }//es edit
        else {
            if(existe){
                flash.clase = "alert-error"
                flash.message = "Ya existe una Fuente de Financiamiento con ese nombre!"
                redirect(action: 'list')
                return
            }else{
                fuenteFinanciamientoInstance = new FuenteFinanciamiento(params)
            }

        } //es create
        if (!fuenteFinanciamientoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Fuente Financiamiento " + (fuenteFinanciamientoInstance.id ? fuenteFinanciamientoInstance.id : "") + "</h4>"

            str += "<ul>"
            fuenteFinanciamientoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Fuente Financiamiento " + fuenteFinanciamientoInstance.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Fuente Financiamiento " + fuenteFinanciamientoInstance.descripcion
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def fuenteFinanciamientoInstance = FuenteFinanciamiento.get(params.id)
        if (!fuenteFinanciamientoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Fuente Financiamiento con id " + params.id
            redirect(action: "list")
            return
        }
        [fuenteFinanciamientoInstance: fuenteFinanciamientoInstance]
    } //show

    def delete() {
        def fuenteFinanciamientoInstance = FuenteFinanciamiento.get(params.id)
        if (!fuenteFinanciamientoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Fuente Financiamiento con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            fuenteFinanciamientoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Fuente Financiamiento " + fuenteFinanciamientoInstance.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Fuente Financiamiento " + (fuenteFinanciamientoInstance.id ? fuenteFinanciamientoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
