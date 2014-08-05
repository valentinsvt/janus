package janus.ejecucion

import org.springframework.dao.DataIntegrityViolationException

class PeriodosInecController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def migracionService

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        println params
        def periodos = PeriodosInec.list([sort: 'descripcion'])
//        [periodosInecInstanceList: PeriodosInec.list(params), params: params]
        [periodosInecInstanceList: periodos, params: params]
    } //list

    def form_ajax() {
        def periodosInecInstance = new PeriodosInec(params)
        if(params.id) {
            periodosInecInstance = PeriodosInec.get(params.id)
            if(!periodosInecInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Periodos Inec con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [periodosInecInstance: periodosInecInstance]
    } //form_ajax

    def save() {
//        println "save3 "+params
        def periodosInecInstance
        if (params.fechaInicio){
            params.fechaInicio=new Date().parse("dd-MM-yyyy",params.fechaInicio)
        }
        if (params.fechaFin){
            params.fechaFin=new Date().parse("dd-MM-yyyy",params.fechaFin)
        }


        if(params.fechaInicio >= params.fechaFin){

            flash.clase = "alert-error"
            flash.message = "No se pudo guardar el Período Inec, la Fecha Fin debe ser mayor a la Fecha Inicio"
            redirect(action: 'list')

        }else{

            if(params.id) {
                periodosInecInstance = PeriodosInec.get(params.id)
                if(!periodosInecInstance) {
                    flash.clase = "alert-error"
                    flash.message = "No se encontró Periodos Inec con id " + params.id
                    redirect(action: 'list')
                    return
                }//no existe el objeto
                periodosInecInstance.properties = params
            }//es edit
            else {
                periodosInecInstance = new PeriodosInec(params)

            } //es create

            if (!periodosInecInstance.save(flush: true)) {
                flash.clase = "alert-error"
                def str = "<h4>No se pudo guardar Periodos Inec " + (periodosInecInstance.id ? periodosInecInstance.id : "") + "</h4>"

                str += "<ul>"
                periodosInecInstance.errors.allErrors.each { err ->
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

            if(params.id) {
                flash.clase = "alert-success"
                flash.message = "Se ha actualizado correctamente el Período Inec " + periodosInecInstance.descripcion
            } else {
                flash.clase = "alert-success"
                flash.message = "Se ha creado correctamente el Período Inec " + periodosInecInstance.descripcion
            }
            redirect(action: 'list')
        }



    } //save

    def show_ajax() {
        def periodosInecInstance = PeriodosInec.get(params.id)
        if (!periodosInecInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Periodos Inec con id " + params.id
            redirect(action: "list")
            return
        }
        [periodosInecInstance: periodosInecInstance]
    } //show

    def delete() {
        def periodosInecInstance = PeriodosInec.get(params.id)
        if (!periodosInecInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró el Período Inec con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            periodosInecInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente el Período Inec " + periodosInecInstance.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar el Período Inec " + (periodosInecInstance.id ? periodosInecInstance.id : "")
            redirect(action: "list")
        }
    } //delete

    def generarIndices(){
        render migracionService.insertRandomIndices()
    }

} //fin controller
