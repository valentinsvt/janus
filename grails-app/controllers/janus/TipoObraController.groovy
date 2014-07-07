package janus

import org.springframework.dao.DataIntegrityViolationException

class TipoObraController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoObraInstanceList: TipoObra.list(params), params: params]
    } //list

    def form_ajax() {
        def tipoObraInstance = new TipoObra(params)
        if(params.id) {
            tipoObraInstance = TipoObra.get(params.id)
            if(!tipoObraInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Tipo Obra con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoObraInstance: tipoObraInstance]
    } //form_ajax

    def save() {

        params.codigo = params.codigo.toUpperCase()
        def existe = TipoObra.findByCodigo(params.codigo)

        def tipoObraInstance
        if(params.id) {
            tipoObraInstance = TipoObra.get(params.id)
            if(!tipoObraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Obra con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoObraInstance.properties = params
        }//es edit
        else {
            if(!existe){
                tipoObraInstance = new TipoObra(params)
            }else{
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar la programación, el código ya existe!!"
                redirect(action: 'list')
                return
            }

        } //es create
        if (!tipoObraInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Obra " + (tipoObraInstance.id ? tipoObraInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoObraInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Obra " + tipoObraInstance.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Obra " + tipoObraInstance.descripcion
        }
        redirect(action: 'list')
    } //save


    def checkCodigo () {



            def tipos = TipoObra.findAllByCodigo(params.codigo)
            if (tipos.size() == 0) {
                render true
            } else {
                render false
            }


    }

    def checkDesc () {



        def tipos = TipoObra.findAllByDescripcion(params.descripcion)
        if (tipos.size() == 0) {
            render true
        } else {
            render false
        }


    }

    def saveTipoObra () {

        println("params" + params)

        def tipoObraInstance

        def grupo = Grupo.get(params."grupo.id")

        params.grupo = grupo

        params.codigo = params.codigo.toUpperCase();

        def existe = TipoObra.findByCodigo(params.codigo)


        if(params.id) {
            tipoObraInstance = TipoObra.get(params.id)
            if(!tipoObraInstance) {
//                flash.clase = "alert-error"
//                flash.message = "No se encontró Tipo Obra con id " + params.id
//                redirect(action: 'list')
                render "error"
                return
            }//no existe el objeto
            tipoObraInstance.properties = params
        }//es edit
        else {
            if(!existe){
                tipoObraInstance = new TipoObra(params)
            }else{
                render 'error'
                return
            }

        } //es create
        if (!tipoObraInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Obra " + (tipoObraInstance.id ? tipoObraInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoObraInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
//            redirect(action: 'list')
            render "error"
            return
        }

//        if(params.id) {
//            flash.clase = "alert-success"
//            flash.message = "Se ha actualizado correctamente Tipo Obra " + tipoObraInstance.id
//        } else {
//            flash.clase = "alert-success"
//            flash.message = "Se ha creado correctamente el Tipo de Obra: " + tipoObraInstance.descripcion
//        }

//        def sel = g.select(name:"tipoObjetivo.id", class:"tipoObjetivo required", from:janus.TipoObra?.list(), value:tipoObraInstance?.id, optionValue:"descripcion", optionKey:"id", style:"margin-left: -60px; width: 290px")
        def sel = g.select(name:"tipoObjetivo.id", class:"tipoObjetivo required", from:TipoObra.list(), value:tipoObraInstance?.id, optionValue:"descripcion", optionKey:"id", style:"margin-left: -60px; width: 290px")

         render sel
//        redirect(controller: 'obra', action: 'registroObra')
    } //save







    def show_ajax() {
        def tipoObraInstance = TipoObra.get(params.id)
        if (!tipoObraInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Tipo Obra con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoObraInstance: tipoObraInstance]
    } //show

    def delete() {
        def tipoObraInstance = TipoObra.get(params.id)
        if (!tipoObraInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Tipo Obra con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tipoObraInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Tipo Obra " + tipoObraInstance.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Tipo Obra " + (tipoObraInstance.id ? tipoObraInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
