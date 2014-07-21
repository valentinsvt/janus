package janus

import org.springframework.dao.DataIntegrityViolationException

class TipoItemController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [tipoItemInstanceList: TipoItem.list(params), tipoItemInstanceTotal: TipoItem.count(), params: params]
    } //list

    def form_ajax() {
        def tipoItemInstance = new TipoItem(params)
        if (params.id) {
            tipoItemInstance = TipoItem.get(params.id)
            if (!tipoItemInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró TipoItem con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoItemInstance: tipoItemInstance]
    } //form_ajax

    def save() {


        params.codigo = params.codigo.toUpperCase();

        def existe = TipoItem.findByCodigo(params.codigo)

        def tipoItemInstance
        if (params.id) {
            tipoItemInstance = TipoItem.get(params.id)
            if (!tipoItemInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró TipoItem con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoItemInstance.properties = params
        }//es edit
        else {
            if(!existe){
                tipoItemInstance = new TipoItem(params)
            }else{
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar el tipo de item, el código ya existe!!"
                redirect(action: 'list')
                return
            }

        } //es create
        if (!tipoItemInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo de Item " + (tipoItemInstance.id ? tipoItemInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoItemInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo de Item " + tipoItemInstance.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo de Item " + tipoItemInstance.descripcion
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoItemInstance = TipoItem.get(params.id)
        if (!tipoItemInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo de Item con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoItemInstance: tipoItemInstance]
    } //show

    def delete() {
        def tipoItemInstance = TipoItem.get(params.id)
        if (!tipoItemInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo de Item con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoItemInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Tipo de Item " + tipoItemInstance.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar TipoItem " + (tipoItemInstance.id ? tipoItemInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
