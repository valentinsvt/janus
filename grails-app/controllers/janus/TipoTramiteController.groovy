package janus

import groovy.json.JsonBuilder
import org.springframework.dao.DataIntegrityViolationException

class TipoTramiteController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [tipoTramiteInstanceList: TipoTramite.list(params), params: params]
    } //list

    def addDep() {
        def dep = new DepartamentoTramite([
                tipoTramite: TipoTramite.get(params.tipo.toLong()),
                rolTramite: RolTramite.get(params.rol.toLong()),
                departamento: Departamento.get(params.dep.toLong())
        ])

        if (dep.save(flush: true)) {
            render "OK_" + dep.id
        } else {
            println "Error al guardar tipo tramite - rol - departamento: " + dep.errors
            render "NO"
        }
    }

    def delDep() {
        def dep = DepartamentoTramite.get(params.id.toLong())
        def ok = "OK"
        try {
            dep.delete(flush: true)
        } catch (e) {
            println e
            ok = "NO"
        }
        render ok
    }

    def departamentos_ajax() {
        def tipoTramite = TipoTramite.get(params.tramite.toLong())

        def dps = []
        DepartamentoTramite.findAllByTipoTramite(tipoTramite).each { dep ->
            dps.add([
                    id: dep.id,
                    departamento: dep.departamento.descripcion,
                    departamento_id: dep.departamentoId,
                    rol: dep.rolTramite.descripcion,
                    rol_id: dep.rolTramiteId
            ])
        }
        def json = new JsonBuilder(dps)

        return [tipoTramite: tipoTramite, departamentos: json]
    }

    def checkCd_ajax() {
        //        println params
        if (params.id) {
            def tipoTramite = TipoTramite.get(params.id)
//            println params.codigo
//            println params.codigo.class
//            println departamento.codigo
//            println departamento.codigo.class
            if (params.codigo == tipoTramite.codigo.toString()) {
                render true
            } else {
                def tiposTramite = TipoTramite.findAllByCodigoIlike(params.codigo)
                if (tiposTramite.size() == 0) {
                    render true
                } else {
                    render false
                }
            }
        } else {
            def tiposTramite = TipoTramite.findAllByCodigoIlike(params.codigo)
            if (tiposTramite.size() == 0) {
                render true
            } else {
                render false
            }
        }
    }

    def form_ajax() {
        def tipoTramiteInstance = new TipoTramite(params)
        if (params.id) {
            tipoTramiteInstance = TipoTramite.get(params.id)
            if (!tipoTramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Tramite con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tipoTramiteInstance: tipoTramiteInstance]
    } //form_ajax

    def save() {
        def tipoTramiteInstance
        params.codigo = params.codigo.toUpperCase()
        if (params.id) {
            tipoTramiteInstance = TipoTramite.get(params.id)
            if (!tipoTramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Tramite con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tipoTramiteInstance.properties = params
        }//es edit
        else {
            tipoTramiteInstance = new TipoTramite(params)
        } //es create
        if (!tipoTramiteInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tipo Tramite " + (tipoTramiteInstance.id ? tipoTramiteInstance.id : "") + "</h4>"

            str += "<ul>"
            tipoTramiteInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Tipo Tramite " + tipoTramiteInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tipo Tramite " + tipoTramiteInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tipoTramiteInstance = TipoTramite.get(params.id)
        if (!tipoTramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Tramite con id " + params.id
            redirect(action: "list")
            return
        }
        [tipoTramiteInstance: tipoTramiteInstance]
    } //show

    def delete() {
        def tipoTramiteInstance = TipoTramite.get(params.id)
        if (!tipoTramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tipo Tramite con id " + params.id
            redirect(action: "list")
            return
        }

        def hijos = TipoTramite.countByPadre(tipoTramiteInstance)
        def departamentos = DepartamentoTramite.countByTipoTramite(tipoTramiteInstance)
        def tramites = Tramite.countByTipoTramite(tipoTramiteInstance)

        if (departamentos > 0 || hijos > 0 || tramites > 0) {
            flash.message = "El tipo de trámite tiene "
            def str = ""
            if (departamentos > 0) {
                str += departamentos + " departamento${departamentos == 1 ? '' : 's'}"
            }
            if (hijos > 0) {
                if (str != "") {
                    str += ","
                }
                str += hijos + " hijo${hijos == 1 ? '' : 's'}"
            }
            if (tramites > 0) {
                if (str != "") {
                    str += ","
                }
                str += tramites + " trámite${tramites == 1 ? '' : 's'}"
            }
            flash.clase = "alert-error"
            flash.message += str + " por lo que no pudo ser eliminado."
            redirect(action: "list")
            return
        }

        try {
            tipoTramiteInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Tipo Tramite " + tipoTramiteInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Tipo Tramite " + (tipoTramiteInstance.id ? tipoTramiteInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
