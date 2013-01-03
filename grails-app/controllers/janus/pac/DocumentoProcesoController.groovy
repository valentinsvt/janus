package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class DocumentoProcesoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [documentoProcesoInstanceList: DocumentoProceso.list(params), params: params]
    } //list

    def form_ajax() {
        def documentoProcesoInstance = new DocumentoProceso(params)
        if (params.id) {
            documentoProcesoInstance = DocumentoProceso.get(params.id)
            if (!documentoProcesoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Documento Proceso con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [documentoProcesoInstance: documentoProcesoInstance]
    } //form_ajax

    def save() {
        def documentoProcesoInstance
        if (params.id) {
            documentoProcesoInstance = DocumentoProceso.get(params.id)
            if (!documentoProcesoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Documento Proceso con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            documentoProcesoInstance.properties = params
        }//es edit
        else {
            documentoProcesoInstance = new DocumentoProceso(params)
        } //es create
        if (!documentoProcesoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Documento Proceso " + (documentoProcesoInstance.id ? documentoProcesoInstance.id : "") + "</h4>"

            str += "<ul>"
            documentoProcesoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Documento Proceso " + documentoProcesoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Documento Proceso " + documentoProcesoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def documentoProcesoInstance = DocumentoProceso.get(params.id)
        if (!documentoProcesoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Documento Proceso con id " + params.id
            redirect(action: "list")
            return
        }
        [documentoProcesoInstance: documentoProcesoInstance]
    } //show

    def delete() {
        def documentoProcesoInstance = DocumentoProceso.get(params.id)
        if (!documentoProcesoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Documento Proceso con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            documentoProcesoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Documento Proceso " + documentoProcesoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Documento Proceso " + (documentoProcesoInstance.id ? documentoProcesoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
