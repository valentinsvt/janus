package janus

import org.springframework.dao.DataIntegrityViolationException

class ParroquiaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [parroquiaInstanceList: Parroquia.list(params), parroquiaInstanceTotal: Parroquia.count(), params: params]
    } //list

    def form_ajax() {
        def parroquiaInstance = new Parroquia(params)
        if (params.id) {
            parroquiaInstance = Parroquia.get(params.id)
            if (!parroquiaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Parroquia con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [parroquiaInstance: parroquiaInstance]
    } //form_ajax

    def fixCodigos() {
        def html = "<style>"
        html += "table{ border-collapse: collapse; }"
        html += "th{ padding: 5px; }"
        html += "td{ padding: 2px; }"
        html += "tr.ok, tr.ok td{ background-color: #93C19A; }"
        html += "tr.no, tr.no td{ background-color: #C1939B; }"
        html += "</style>"
        html += "<p>Se han cambiado los códigos de las siguientes parroquias: </p>"
        html += "<table border='1'>"
        html += "<tr>"
        html += "<thead>"
        html += "<th>Parroquia</th>"
        html += "<th>Cantón</th>"
        html += "<th>Código Cantón</th>"
        html += "<th>Código Parroquia</th>"
        html += "<th>Nuevo Código Parroquia</th>"
        html += "</thead>"
        html += "</tr>"
        def list = Parroquia.withCriteria {
            canton {
                order("numero", "asc")
            }
            order("codigo", "asc")
        }
        list.each { parr ->

            parr.codigo = parr.codigo.replaceAll(parr.canton.numero.padLeft(2, '0'), '')
            if (parr.codigo == '') {
                parr.codigo = parr.canton.numero.padLeft(2, '0')
            }
            def nc = parr.canton.numero.padLeft(2, '0') + parr.codigo.padLeft(2, '0')

            def ok = false
            if (Parroquia.countByCodigoAndIdNotEqual(nc, parr.id) > 0) {
                while (Parroquia.countByCodigoAndIdNotEqual(nc, parr.id) > 0) {
                    println "repetido: ${parr.id} ${nc}"
                    nc += "r"
                }
            }
            parr.codigo = nc
            if (parr.save(flush: true)) {
                ok = true
            } else {
                println parr.errors
            }

            html += "<tr class='${ok ? 'ok' : 'no'}'>"
            html += "<td>${parr.nombre}</td>"
            html += "<td>${parr.canton.nombre}</td>"
            html += "<td>${parr.canton.numero}</td>"
            html += "<td>${parr.codigo}</td>"
            html += "<td>${nc}</td>"
            html += "</tr>"
//            }
        }
        html += "</table>"
        render html
    }

    def save() {
        def parroquiaInstance
        if (params.id) {
            parroquiaInstance = Parroquia.get(params.id)
            if (!parroquiaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Parroquia con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            parroquiaInstance.properties = params
        }//es edit
        else {
            parroquiaInstance = new Parroquia(params)
            def existe = Parroquia.findByCodigo(params.codigo)
            if (!existe)
                parroquiaInstance = new Parroquia(params)
            else {
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar el código ya existe."
                redirect(action: 'list')
                return
            }
        } //es create
        if (!parroquiaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Parroquia " + (parroquiaInstance.id ? parroquiaInstance.id : "") + "</h4>"

            str += "<ul>"
            parroquiaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Parroquia " + parroquiaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Parroquia " + parroquiaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def parroquiaInstance = Parroquia.get(params.id)
        if (!parroquiaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Parroquia con id " + params.id
            redirect(action: "list")
            return
        }
        [parroquiaInstance: parroquiaInstance]
    } //show

    def delete() {
        def parroquiaInstance = Parroquia.get(params.id)
        if (!parroquiaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Parroquia con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            parroquiaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Parroquia " + parroquiaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Parroquia " + (parroquiaInstance.id ? parroquiaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
