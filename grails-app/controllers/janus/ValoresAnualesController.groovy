package janus

import org.springframework.dao.DataIntegrityViolationException

class ValoresAnualesController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
//
//        def num = 10687.25
//
//        println num
//        println NumberToLetterConverter.convertNumberToLetter(num)

        [valoresAnualesInstanceList: ValoresAnuales.list(params), params: params]
    } //list

    def form_ajax() {
        def valoresAnualesInstance = new ValoresAnuales(params)
        if (params.id) {
            valoresAnualesInstance = ValoresAnuales.get(params.id)
            if (!valoresAnualesInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Valores Anuales con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [valoresAnualesInstance: valoresAnualesInstance]
    } //form_ajax

    def save() {
//        println("params" + params)
        def existe = ValoresAnuales.findByAnio(params.anio)
        def valoresAnualesInstance
        if (params.id) {
            valoresAnualesInstance = ValoresAnuales.get(params.id)
            if (!valoresAnualesInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Valores Anuales con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            valoresAnualesInstance.properties = params
        }//es edit
        else {
            if(existe){
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar, Año ya existente!"
                redirect(action: 'list')
                return
            }else{
            valoresAnualesInstance = new ValoresAnuales(params)
            }
        } //es create
        if (!valoresAnualesInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Valores Anuales " + (valoresAnualesInstance.id ? valoresAnualesInstance.id : "") + "</h4>"

            str += "<ul>"
            valoresAnualesInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Valores Anuales " + valoresAnualesInstance.anio
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Valores Anuales " + valoresAnualesInstance.anio
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def valoresAnualesInstance = ValoresAnuales.get(params.id)
        if (!valoresAnualesInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Valores Anuales con id " + params.id
            redirect(action: "list")
            return
        }
        [valoresAnualesInstance: valoresAnualesInstance]
    } //show

    def delete() {
        def valoresAnualesInstance = ValoresAnuales.get(params.id)
        if (!valoresAnualesInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Valores Anuales con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            valoresAnualesInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Valores Anuales " + valoresAnualesInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Valores Anuales " + (valoresAnualesInstance.id ? valoresAnualesInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
