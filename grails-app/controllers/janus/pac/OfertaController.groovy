package janus.pac

import janus.Persona
import janus.PersonaRol
import org.springframework.dao.DataIntegrityViolationException

class OfertaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        def concurso = Concurso.get(params.id)
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        def ofertas = Oferta.findAllByConcurso(concurso, params)
        return [concurso: concurso, ofertaInstanceList: ofertas, params: params]
    } //list

    def form_ajax() {
        def ofertaInstance = new Oferta(params)
        if (params.id) {
            ofertaInstance = Oferta.get(params.id)
            if (!ofertaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Oferta con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        else {
            def concurso = Concurso.get(params.cncr)
            ofertaInstance.concurso = concurso
        }

        def responsablesProceso = PersonaRol.withCriteria {
            persona {
                eq("activo", 1)
                order("apellido", "asc")
            }
            funcion {
                eq("codigo", "C")
            }
        }.persona

        return [ofertaInstance: ofertaInstance, responsablesProceso: responsablesProceso]
    } //form_ajax

    def save() {
        if (params.fechaEntrega) {
            params.fechaEntrega = new Date().parse('dd-MM-yyyy', params.fechaEntrega)
        }
        def ofertaInstance
        if (params.id) {
            ofertaInstance = Oferta.get(params.id)
            if (!ofertaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Oferta con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            ofertaInstance.properties = params
        }//es edit
        else {
            ofertaInstance = new Oferta(params)
        } //es create
        if (!ofertaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Oferta " + (ofertaInstance.id ? ofertaInstance.id : "") + "</h4>"

            str += "<ul>"
            ofertaInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(action: 'list', id: ofertaInstance.concursoId)
            return
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Oferta " + ofertaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Oferta " + ofertaInstance.id
        }
        redirect(action: 'list', id: ofertaInstance.concursoId)
    } //save

    def show_ajax() {
        def ofertaInstance = Oferta.get(params.id)
        if (!ofertaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Oferta con id " + params.id
            redirect(action: "list")
            return
        }
        [ofertaInstance: ofertaInstance]
    } //show

    def delete() {
        def ofertaInstance = Oferta.get(params.id)
        if (!ofertaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Oferta con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            ofertaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Oferta " + ofertaInstance.id
            redirect(controller: "concurso", action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Oferta " + (ofertaInstance.id ? ofertaInstance.id : "")
            redirect(controller: "concurso",action: "list")
        }
    } //delete
} //fin controller
