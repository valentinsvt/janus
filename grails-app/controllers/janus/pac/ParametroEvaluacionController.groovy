package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class ParametroEvaluacionController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        def concurso = Concurso.get(params.id)
        def parametros = ParametroEvaluacion.findAllByConcurso(concurso)
        return [parametroEvaluacionInstanceList: parametros, params: params, concurso: concurso]
    } //list

    def form_ajax() {
        def parametroEvaluacionInstance = new ParametroEvaluacion(params)
        if (params.id) {
            parametroEvaluacionInstance = ParametroEvaluacion.get(params.id)
            if (!parametroEvaluacionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Parametro Evaluacion con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [parametroEvaluacionInstance: parametroEvaluacionInstance]
    } //form_ajax

    def save() {
        def parametroEvaluacionInstance
        if (params.id) {
            parametroEvaluacionInstance = ParametroEvaluacion.get(params.id)
            if (!parametroEvaluacionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Parametro Evaluacion con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            parametroEvaluacionInstance.properties = params
        }//es edit
        else {
            parametroEvaluacionInstance = new ParametroEvaluacion(params)
        } //es create
        if (!parametroEvaluacionInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Parametro Evaluacion " + (parametroEvaluacionInstance.id ? parametroEvaluacionInstance.id : "") + "</h4>"

            str += "<ul>"
            parametroEvaluacionInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Parametro Evaluacion " + parametroEvaluacionInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Parametro Evaluacion " + parametroEvaluacionInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def parametroEvaluacionInstance = ParametroEvaluacion.get(params.id)
        if (!parametroEvaluacionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Parametro Evaluacion con id " + params.id
            redirect(action: "list")
            return
        }
        [parametroEvaluacionInstance: parametroEvaluacionInstance]
    } //show

    def delete() {
        def parametroEvaluacionInstance = ParametroEvaluacion.get(params.id)
        if (!parametroEvaluacionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Parametro Evaluacion con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            parametroEvaluacionInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Parametro Evaluacion " + parametroEvaluacionInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Parametro Evaluacion " + (parametroEvaluacionInstance.id ? parametroEvaluacionInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
