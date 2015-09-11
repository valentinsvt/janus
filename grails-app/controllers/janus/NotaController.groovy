package janus

import org.springframework.dao.DataIntegrityViolationException

class NotaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [notaInstanceList: Nota.list(params), params: params]
    } //list

    def form_ajax() {
        def notaInstance = new Nota(params)
        if (params.id) {
            notaInstance = Nota.get(params.id)
            if (!notaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Nota con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [notaInstance: notaInstance]
    } //form_ajax

    def save() {

//         println "params "+params
        if (params.piePaginaSel) {
            if (params.piePaginaSel != '-1'){
                params.id = params.piePaginaSel
            }else{
                params.id = null
            }
        }

        def notaInstance
        if (params.id) {
            notaInstance = Nota.get(params.id)
            if (!notaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Nota con id " + params.id
                redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
                return
            }//no existe el objeto
            notaInstance.properties = params
        }//es edit
        else {
            notaInstance = new Nota(params)
        } //es create
        if (!notaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Nota " + (notaInstance.id ? notaInstance.id : "") + "</h4>"

            str += "<ul>"
            notaInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
//            redirect(action: 'list')
            return
        }

        def grabado = ''

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente la Nota " + notaInstance.descripcion

            grabado = '1'

        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente la Nota " + notaInstance.descripcion
            grabado = '2'
        }
//        redirect(action: 'list')
//        redirect(controller: 'documentosObra',action: 'documentosObra',id: params.obra)
          render grabado
    } //save



    def saveNota () {
        def dia = new Date()
//        println ("params saveNota "+ params)
        if (params.piePaginaSel) {
            if (params.piePaginaSel != '-1'){
                params.id = params.piePaginaSel
            }else{
                params.id = null
            }
        }

        def notaInstance = null

        if (params.id) {

            notaInstance = Nota.get(params.id)
            if (!notaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Nota con id " + params.id
//                redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
                return
            }//no existe el objeto
            notaInstance.properties = params
        }//es edit
        else {

            if(params.adicional == '' && params.texto == ''){


            }else {

                notaInstance = new Nota()
                if(params.descripcion == ''){

                   notaInstance.descripcion = "Nota " + dia.format("dd-MM-yyyy")
                   notaInstance.adicional = params.adicional
                    notaInstance.texto = params.texto
                }else {

                    notaInstance.descripcion = params.descripcion
                    notaInstance.adicional = params.adicional
                    notaInstance.texto = params.texto
                }

            }

        } //es create

        if (notaInstance && notaInstance.save(flush: true)) {

//            if (params.id) {
//                flash.clase = "alert-success"
//                flash.message = "Se ha actualizado correctamente la Nota " + notaInstance.descripcion
//            } else {
//                flash.clase = "alert-success"
//                flash.message = "Se ha creado correctamente la Nota " + notaInstance.descripcion
//            }

            render "ok_"+notaInstance?.id


        } else {
            flash.clase = "alert-error"
            def str
            if(notaInstance) {
                str = "<h4>No se pudo guardar Nota " + (notaInstance.id ? notaInstance.id : "") + "</h4>"

                str += "<ul>"
                notaInstance.errors.allErrors.each { err ->
                    def msg = err.defaultMessage
                    err.arguments.eachWithIndex {  arg, i ->
                        msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                    }
                    str += "<li>" + msg + "</li>"
                }
                str += "</ul>"
            } else {
//                str = "No se pudo guardar porque la descripción y el texto están vacíos"
            }

            flash.message = str
            render "ok"
//            redirect(action: 'list')
            return

        }


    }

    //save notaMemo

    def saveNotaMemo() {

//         println ("params " + params)

        def fecha = new Date();

        if (params.selMemo) {
            if (params.selMemo != '-1'){
                params.id = params.selMemo
            }else{
                params.id = null
            }
        }

        def notaInstance
        if (params.id) {
            notaInstance = Nota.get(params.id)
            if (!notaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Nota con id " + params.id
//                redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
                return
            }//no existe el objeto
            notaInstance.properties = params
            notaInstance.tipo = 'memo'
        }//es edit
        else {

            if(params.adicional == '' && params.texto == ''){

            }else {
                notaInstance = new Nota()
                notaInstance.adicional = params.pie
                notaInstance.texto = params.texto
                notaInstance.tipo = 'memo'
                if(params.descripcion != ''){
                    notaInstance.descripcion = params.descripcion
                }else {
                    notaInstance.descripcion = "Nota Memo " + fecha.format("dd-MM-yyyy");
                }
            }
        } //es create
        if (!notaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Nota " + (notaInstance.id ? notaInstance.id : "") + "</h4>"

            str += "<ul>"
            notaInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
//            redirect(action: 'list')
            return
        }

//        def grabado = ''
//
//        if (params.id) {
//            flash.clase = "alert-success"
//            flash.message = "Se ha actualizado correctamente la Nota " + notaInstance.descripcion
//
//            grabado = '1'
//
//        } else {
//            flash.clase = "alert-success"
//            flash.message = "Se ha creado correctamente la Nota " + notaInstance.descripcion
//            grabado = '2'
//        }
//
//        render grabado




        if (notaInstance && notaInstance.save(flush: true)) {

            render "ok_"+notaInstance?.id

        } else {
            flash.clase = "alert-error"
            def str
            if(notaInstance) {
                str = "<h4>No se pudo guardar Nota " + (notaInstance.id ? notaInstance.id : "") + "</h4>"

                str += "<ul>"
                notaInstance.errors.allErrors.each { err ->
                    def msg = err.defaultMessage
                    err.arguments.eachWithIndex {  arg, i ->
                        msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                    }
                    str += "<li>" + msg + "</li>"
                }
                str += "</ul>"
            } else {
//                str = "No se pudo guardar porque la descripción y el texto están vacíos"
            }

            flash.message = str
            render "ok"
//            redirect(action: 'list')
            return

        }

    } //save






    //save notaFormu


    def saveNotaFormu() {

//        println ("params " + params)

        def fecha = new Date();

        if (params.selFormu) {
            if (params.selFormu != '-1'){
                params.id = params.selFormu
            }else{
                params.id = null
            }
        }

        def notaInstance
        if (params.id) {
            notaInstance = Nota.get(params.id)
            if (!notaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Nota con id " + params.id
                redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
                return
            }//no existe el objeto
            notaInstance.properties = params
            notaInstance.tipo = 'formula'
        }//es edit
        else {
            notaInstance = new Nota()
            notaInstance.texto = params.texto
            notaInstance.tipo = 'formula'
            if(params.descripcion != ''){
                notaInstance.descripcion = params.descripcion
            }else {
                notaInstance.descripcion = "Nota Formula " + fecha.format("dd-MM-yyyy");
            }
        } //es create
        if (!notaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Nota " + (notaInstance.id ? notaInstance.id : "") + "</h4>"

            str += "<ul>"
            notaInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            return
        }

        if (notaInstance && notaInstance.save(flush: true)) {

            render "ok_"+notaInstance?.id

        } else {
            flash.clase = "alert-error"
            def str
            if(notaInstance) {
                str = "<h4>No se pudo guardar Nota " + (notaInstance.id ? notaInstance.id : "") + "</h4>"

                str += "<ul>"
                notaInstance.errors.allErrors.each { err ->
                    def msg = err.defaultMessage
                    err.arguments.eachWithIndex {  arg, i ->
                        msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                    }
                    str += "<li>" + msg + "</li>"
                }
                str += "</ul>"
            } else {
//                str = "No se pudo guardar porque la descripción y el texto están vacíos"
            }

            flash.message = str
            render "ok"
//            redirect(action: 'list')
            return

        }




    } //save



    def show_ajax() {
        def notaInstance = Nota.get(params.id)
        if (!notaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Nota con id " + params.id
//            redirect(action: "list")
            return
        }
        [notaInstance: notaInstance]
    } //show

    def delete() {
        def mensaje
//        println("params" + params)

        def notaInstance = Nota.get(params.id)
        if (!notaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Nota con id " + params.id
//            redirect(controller: 'documentosObra', action: "documentosObra", id: params.obra)
            render "no"
            return
        }

        try {
            notaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Nota " + notaInstance.id
            render "ok"
//            redirect(controller: 'documentosObra', action: "documentosObra", id: params.obra)
            return
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Nota " + (notaInstance.id ? notaInstance.id : "")
            render "no"
//            redirect(controller: 'documentosObra', action: "documentosObra", id: params.obra)
        }

//        redirect(controller: 'documentosObra', action: "documentosObra", id: params.obra)

    } //delete
} //fin controller
