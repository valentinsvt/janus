package janus

import org.springframework.dao.DataIntegrityViolationException

class CantonController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]


    String makeBasicTree(tipo, id) {
        String tree = "", clase = ""
        switch (tipo) {
            case "init": //cargo "Division politica"
                tree += "<ul type='pais'>" // <ul pais
                clase = ""
                tree += "<li id='pais_' class='pais jstree-closed' rel='pais'>" // <li pais
                tree += "<a href='#' class='label_arbol'>División política</a>" // </> a href pais
                tree += "</ul>"
                break;


            case "provincia": // cargo los cantones de la provincia
                def provincia = Provincia.get(params.id)

                def cantones = Canton.findAllByProvincia(provincia, [sort: 'nombre'])
                clase = (cantones.size() > 0) ? "jstree-closed" : ""

                if (cantones.size() > 0) {
                    tree += "<ul type='canton'>" // < ul cantones
                    cantones.each { canton ->
                        def parroquias = Parroquia.findAllByCanton(canton, [sort: 'nombre'])

                        clase = (parroquias.size() > 0) ? "jstree-closed" : ""
                        tree += "<li id='canton_" + canton.id + "' class='canton " + clase + "' rel='canton'>" // <li canton
                        tree += "<a href='#' id='link_canton_" + canton.id + "' class='label_arbol'>" + canton.nombre + "</a>" // </> a href canton
                        tree += "</li>" // </> li canton
                    }
                    tree += "</ul>" // </> ul cantones
                }
                break;
            case "canton":
                def canton = Canton.get(params.id)

                def parroquias = Parroquia.findAllByCanton(canton, [sort: 'nombre'])

                clase = ""

                if (parroquias.size() > 0) {
                    tree += "<ul type='parroquia'>" // < ul parroquias
                    parroquias.each { parroquia ->
                        tree += "<li id='parroquia_" + parroquia.id + "' class='parroquia " + clase + "' rel='parroquia'>" // <li parroquia
                        tree += "<a href='#' id='link_parroquia_" + parroquia.id + "' class='label_arbol'>" + parroquia.nombre + "</a>" // </> a href parroquia
                        tree += "</li>" // </> li parroquia
                    }
                    tree += "</ul>" // </> ul parroquias
                }
                break;
        }
        return tree
    }

    def loadTreePart = {
        render(makeBasicTree(params.tipo, params.id))
    }


    def infoForTree = {
        redirect(action: 'info' + (params.tipo).capitalize(), params: params)
    }
//
//    def infoProvincia = {
//        def obj = Provincia.get(params.id)
//        return [provinciaInstance: obj]
//    }
    def infoCanton = {
        def obj = Canton.get(params.id)
        return [cantonInstance: obj]
    }
    def infoParroquia = {
        def obj = Parroquia.get(params.id)
        return [parroquiaInstance: obj]
    }

    def arbol () {



    }



    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [cantonInstanceList: Canton.list(params), cantonInstanceTotal: Canton.count(), params: params]
    } //list




    def form_ajax() {
        def cantonInstance = new Canton(params)
        if (params.id) {
            cantonInstance = Canton.get(params.id)
            if (!cantonInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Canton con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [cantonInstance: cantonInstance]
    } //form_ajax

    def save() {
        def cantonInstance
        if (params.id) {
            cantonInstance = Canton.get(params.id)
            if (!cantonInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Canton con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            cantonInstance.properties = params
        }//es edit
        else {
            cantonInstance = new Canton(params)
        } //es create
        if (!cantonInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Canton " + (cantonInstance.id ? cantonInstance.id : "") + "</h4>"

            str += "<ul>"
            cantonInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Canton " + cantonInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Canton " + cantonInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def cantonInstance = Canton.get(params.id)
        if (!cantonInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Canton con id " + params.id
            redirect(action: "list")
            return
        }
        [cantonInstance: cantonInstance]
    } //show

    def delete() {
        def cantonInstance = Canton.get(params.id)
        if (!cantonInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Canton con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            cantonInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Canton " + cantonInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Canton " + (cantonInstance.id ? cantonInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
