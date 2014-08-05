package janus

import org.springframework.dao.DataIntegrityViolationException

class ClaseObraController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 100, 100)
        [claseObraInstanceList: ClaseObra.list(params), claseObraInstanceTotal: ClaseObra.count(), params: params]
    } //list

    def form_ext_ajax() {

        def grupo = params.grupo

        def claseObraInstance = new ClaseObra(params)
        if (params.id) {
            claseObraInstance = ClaseObra.get(params.id)
            if (!claseObraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró ClaseObra con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [claseObraInstance: claseObraInstance, grupo: grupo]
    } //form_ajax

    def save_ext() {

//println("params" + params)
//        params.codigo = params.codigo.toUpperCase();
//        def  existe = ClaseObra.findByCodigo(params.codigo)

        def grupo = Grupo.get(params."grupo.id")
        params.grupo = grupo

//        println("rupo" + params.grupo)

        def clases = ClaseObra.list()
        def clasesOrdenadas = ClaseObra.list(sort: 'codigo' )
        def codigos = []
        clasesOrdenadas.each {
            codigos += it?.codigo
        }

        def claseObraInstance, message
        if (params.id) {
            claseObraInstance = ClaseObra.get(params.id)
            if (!claseObraInstance) {
                message = "No se encontró ClaseObra con id " + params.id
                println message
                render "error"
                return
            }//no existe el objeto
            claseObraInstance.properties = params
        }//es edit
        else {
//            if(!existe){
//                claseObraInstance = new ClaseObra(params)
//            }else{
//                render "No se pudo guardar la clase de obra, el código ya existe!!"
//                return
//
//            }
            claseObraInstance = new ClaseObra(params)
            claseObraInstance.codigo =  (codigos.last() + 1)

        } //es create
        if (!claseObraInstance.save(flush: true)) {
            def str = "<h4>No se pudo guardar ClaseObra " + (claseObraInstance.id ? claseObraInstance.id : "") + "</h4>"

            str += "<ul>"
            claseObraInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"
            println message
            render "No se pudo guardar la clase de obra!"
            return
        }

        if (params.id) {
            message = "Se ha actualizado correctamente ClaseObra " + claseObraInstance.id
        } else {
            message = "Se ha creado correctamente ClaseObra " + claseObraInstance.id
        }

        println message

        def sel = g.select(id: "claseObra", name: "claseObra.id", "class": "claseObra required", from: ClaseObra.list(), value: claseObraInstance.id,
                optionValue: "descripcion", optionKey: "id", style: "margin-left: -35px; width: 230px", title: "Clase de Obra")


        render sel
    } //save

    def form_ajax() {
        def claseObraInstance = new ClaseObra(params)
        if (params.id) {
            claseObraInstance = ClaseObra.get(params.id)
            if (!claseObraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró ClaseObra con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [claseObraInstance: claseObraInstance]
    } //form_ajax

    def save() {

//        println("params " + params)

        def clases = ClaseObra.list()

        def clasesOrdenadas = ClaseObra.list(sort: 'codigo' )
//        def pp = ClaseObra.findByCodigo(params.codigo)

        def codigos = []

        clasesOrdenadas.each {
            codigos += it?.codigo
        }

//        println("codigos " + codigos)

        def claseObraInstance
        if (params.id) {
            claseObraInstance = ClaseObra.get(params.id)
            if (!claseObraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró ClaseObra con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            claseObraInstance.properties = params
        }//es edit
        else {
//            if(!pp){
//                claseObraInstance = new ClaseObra(params)
//            }else{
//                flash.clase = "alert-error"
//                flash.message = "No se pudo guardar la clase de obra, el código ya existe!!"
//                redirect(action: 'list')
//                return
//            }

            println("codigo ultimo " + (codigos.last() + 1))

            claseObraInstance = new ClaseObra(params)
            claseObraInstance.codigo =  (codigos.last() + 1)


        } //es create
        if (!claseObraInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar ClaseObra " + (claseObraInstance.id ? claseObraInstance.id : "") + "</h4>"

            str += "<ul>"
            claseObraInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente ClaseObra " + claseObraInstance?.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente ClaseObra " + claseObraInstance?.descripcion
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def claseObraInstance = ClaseObra.get(params.id)
        if (!claseObraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró ClaseObra con id " + params.id
            redirect(action: "list")
            return
        }
        [claseObraInstance: claseObraInstance]
    } //show

    def delete() {
        def claseObraInstance = ClaseObra.get(params.id)
        if (!claseObraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró ClaseObra con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            claseObraInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente ClaseObra " + claseObraInstance?.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar ClaseObra " + (claseObraInstance.id ? claseObraInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
