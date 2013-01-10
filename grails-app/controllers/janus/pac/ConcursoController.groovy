package janus.pac

import janus.Administracion
import org.springframework.dao.DataIntegrityViolationException

class ConcursoController extends janus.seguridad.Shield {

    def buscadorService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        def campos = ["descripcion": ["Descripción", "string"]]
        return [concursoInstanceList: Concurso.list(params), params: params, campos: campos]
    } //list

    def nuevoProceso() {
        def pac = Pac.get(params.id)
        def admin = Administracion.findAllByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(new Date(), new Date())
        def concurso = new Concurso()
        concurso.pac = pac
        if (admin.size() == 1) {
            concurso.administracion = admin[0]
        } else if (admin.size() > 1) {
            println "hay mas de una admin: " + admin
        } else {
            println "no hay admin q asignar"
        }
        concurso.costoBases = 0
        concurso.objeto = pac.descripcion
        if (concurso.save(flush: true)) {
            println "saved ok"
            flash.clase = "alert-success"
            flash.message = "Proceso creado"
            redirect(action: 'list')
        } else {
            println "not saved"
            flash.clase = "alert-error"
            flash.message = "Ha ocurrido un error al crear el proceso"
            redirect(action: 'list')
        }
    }

    def show(){
        def concursoInstance = Concurso.get(params.id)
        if (!concursoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Concurso con id " + params.id
            redirect(action: "list")
            return
        }
        [concursoInstance: concursoInstance]
    }

    def buscaPac() {
        def listaTitulos = ["Descripción", "Departamento", "Presupuesto"]
        def listaCampos = ["descripcion", "departamento", "presupuesto"]
        def funciones = [null, null]
        def url = g.createLink(action: "buscaPac", controller: "concurso")
//        def funcionJs = ""
        def funcionJs = "function(){"
        funcionJs += '$("#modal-pac").modal("hide");'
        funcionJs += 'var id=$(this).attr("regId");'
        funcionJs += 'console.log(id);'
        funcionJs += 'var url = "' + createLink(controller: 'concurso', action: 'nuevoProceso') + '/"+id;'
        funcionJs += 'location.href = url;'
        funcionJs += '}'
        def numRegistros = 20
        def extras = ""
        if (!params.reporte) {
            def lista2 = buscadorService.buscar(Pac, "Pac", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
            lista2.pop()
            def lista = []
            lista2.each { l ->
                if (Concurso.countByPac(l) == 0) {
                    lista.add(l)
                }
            }

            render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
        } else {
            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Pac
            session.funciones = funciones
            def anchos = [20, 80] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Pac", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Rubros", anchos: anchos, extras: extras, landscape: true])
        }
    }

    def form_ajax() {
        def concursoInstance = new Concurso(params)
        if (params.id) {
            concursoInstance = Concurso.get(params.id)
            if (!concursoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Concurso con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [concursoInstance: concursoInstance]
    } //form_ajax

    def save() {
        def concursoInstance
        if (params.id) {
            concursoInstance = Concurso.get(params.id)
            if (!concursoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Concurso con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            concursoInstance.properties = params
        }//es edit
        else {
            concursoInstance = new Concurso(params)
        } //es create
        if (!concursoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Concurso " + (concursoInstance.id ? concursoInstance.id : "") + "</h4>"

            str += "<ul>"
            concursoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Concurso " + concursoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Concurso " + concursoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def concursoInstance = Concurso.get(params.id)
        if (!concursoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Concurso con id " + params.id
            redirect(action: "list")
            return
        }
        [concursoInstance: concursoInstance]
    } //show

    def delete() {
        def concursoInstance = Concurso.get(params.id)
        if (!concursoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Concurso con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            concursoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Concurso " + concursoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Concurso " + (concursoInstance.id ? concursoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
