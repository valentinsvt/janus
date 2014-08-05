package janus.pac

import janus.Presupuesto
import org.springframework.dao.DataIntegrityViolationException

class AsignacionController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def buscadorService

    def index() {
        redirect(action: "form_ajax", params: params)
    } //index

    def list() {
        [asignacionInstanceList: Asignacion.list(params), params: params]
    } //list

    def form_ajax() {
        def campos = ["numero": ["Código", "string"], "descripcion": ["Descripción", "string"]]
        def anio = new Date().format("yyyy")
        def actual = Anio.findByAnio(anio.toString())
        if(!actual){
            actual=Anio.list([sort: "id"])?.pop()
        }

        def asignacionInstance = new Asignacion(params)
        if (params.id) {
            asignacionInstance = Asignacion.get(params.id)
            if (!asignacionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Asignacion con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [asignacionInstance: asignacionInstance,campos:campos,actual:actual]
    } //form_ajax

    def tabla(){
//        println "tabla "+params
        def anio = new Date().format("yyyy")
        if (params.anio)
            anio=params.anio
        def actual = Anio.findByAnio(anio.toString())
        if(!actual){
            actual=Anio.list([sort: "id"])?.pop()
        }
        def asignaciones = Asignacion.findAllByAnio(actual)
        asignaciones.sort{it.prespuesto.descripcion}
        [asignaciones:asignaciones,actual: actual]

    }

    def buscaPrsp(){
//        println("entro prsp!")
        println("params" + params)
        def listaTitulos = ["Código", "Descripción","Fuente","programa","Subprograma","Proyecto"]
        def listaCampos = ["numero", "descripcion","fuente","programa","subPrograma","proyecto"]
        def funciones = [null, null]
//        def url = g.createLink(action: "buscaCpac", controller: "pac")
        def url = g.createLink(action: "buscaPrsp")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-ccp").modal("hide");'
        funcionJs += '$("#item_prsp").val($(this).attr("regId"));$("#item_presupuesto").val($(this).attr("prop_numero"));$("#item_presupuesto").attr("title",$(this).attr("prop_descripcion"));$("#item_desc").val($(this).attr("prop_descripcion"));$("#item_fuente").val($(this).attr("prop_fuente"));$("#item_prog").val($(this).attr("prop_programa"));$("#item_spro").val($(this).attr("prop_subprograma"));$("#item_proy").val($(this).attr("prop_proyecto"));cargarTecho();'
        funcionJs += '}'
        def numRegistros = 20
        def extras = ""
//        if (!params.reporte) {
//            def lista = buscadorService.buscar(janus.Presupuesto, "Presupuesto", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
//            lista.pop()
//            render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
//        } else {
//            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
//            session.dominio = janus.Presupuesto
//            session.funciones = funciones
//            def anchos = [20, 80] /*el ancho de las columnas en porcentajes... solo enteros*/
//            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Presupuesto", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Partidas presupuestarias", anchos: anchos, extras: extras, landscape: false])
//        }


        if (!params.reporte) {
            if(params.excel){
                session.dominio = Presupuesto
                session.funciones = funciones
                def anchos = [15, 50, 70, 20, 20, 20]
                /*anchos para el set column view en excel (no son porcentajes)*/
                redirect(controller: "reportes", action: "reporteBuscadorExcel", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Presupuesto", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Presupuesto", anchos: anchos, extras: extras, landscape: true])
            }else{
                def lista = buscadorService.buscar(janus.Presupuesto, "Presupuesto", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
                lista.pop()
                render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
            }
        } else {
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Presupuesto
            session.funciones = funciones
            def anchos = [20, 20,20,10,10,10] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Presupuesto", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Partidas presupuestarias", anchos: anchos, extras: extras, landscape: false])
        }



    }
    def cargarTecho(){
        def prsp = janus.Presupuesto.get(params.id)
        def anio = Anio.get(params.anio)
        def techo = Asignacion.findByAnioAndPrespuesto(anio,prsp)
        if (!techo)
            techo="0.00"
        else
            techo=techo.valor
        render techo
    }


    def save() {
        def asgn
        def prsp = janus.Presupuesto.get(params.prespuesto.id)
        def anio = Anio.get(params.anio.id)
        asgn = Asignacion.findByAnioAndPrespuesto(anio,prsp)
        if (!asgn){
            asgn= new Asignacion()
            asgn.prespuesto=prsp
            asgn.anio=anio
        }
        asgn.valor=params.valor.toDouble()
        if (!asgn.save()) {
//            println "asgn  errors "+asgn.errors
        } else {
            flash.message="Datos guardados"
            redirect(action: "form_ajax")
        }
        return

    } //save

    def show_ajax() {
        def asignacionInstance = Asignacion.get(params.id)
        if (!asignacionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Asignacion con id " + params.id
            redirect(action: "list")
            return
        }
        [asignacionInstance: asignacionInstance]
    } //show

    def delete() {
        def asignacionInstance = Asignacion.get(params.id)
        if (!asignacionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Asignacion con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            asignacionInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Asignacion " + asignacionInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Asignacion " + (asignacionInstance.id ? asignacionInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
