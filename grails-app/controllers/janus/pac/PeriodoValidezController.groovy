package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class PeriodoValidezController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        if (!params.sort) {
            params.sort = "fechaInicio"
        }
        [periodoValidezInstanceList: PeriodoValidez.list(params), params: params]
    } //list

    def form_ajax() {
        def periodoValidezInstance = new PeriodoValidez(params)
        if (params.id) {
            periodoValidezInstance = PeriodoValidez.get(params.id)
            if (!periodoValidezInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Periodo Validez con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [periodoValidezInstance: periodoValidezInstance]
    } //form_ajax

    def save() {
        if (params.fechaInicio) {
            params.fechaInicio = new Date().parse("dd-MM-yyyy", params.fechaInicio)
        }
        if (params.fechaFin) {
            params.fechaFin = new Date().parse("dd-MM-yyyy", params.fechaFin)
        }
        def periodoValidezInstance
        if (params.id) {
            periodoValidezInstance = PeriodoValidez.get(params.id)
            if (!periodoValidezInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Periodo Validez con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            periodoValidezInstance.properties = params
        }//es edit
        else {
            periodoValidezInstance = new PeriodoValidez(params)
        } //es create
        if (!periodoValidezInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Periodo Validez " + (periodoValidezInstance.id ? periodoValidezInstance.id : "") + "</h4>"

            str += "<ul>"
            periodoValidezInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Periodo Validez " + periodoValidezInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Periodo Validez " + periodoValidezInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def periodoValidezInstance = PeriodoValidez.get(params.id)
        if (!periodoValidezInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Periodo Validez con id " + params.id
            redirect(action: "list")
            return
        }
        [periodoValidezInstance: periodoValidezInstance]
    } //show

    def delete() {
        def periodoValidezInstance = PeriodoValidez.get(params.id)
        if (!periodoValidezInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Periodo Validez con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            periodoValidezInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Periodo Validez " + periodoValidezInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Periodo Validez " + (periodoValidezInstance.id ? periodoValidezInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
