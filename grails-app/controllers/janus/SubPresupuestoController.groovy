package janus

import org.springframework.dao.DataIntegrityViolationException

class SubPresupuestoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [subPresupuestoInstanceList: SubPresupuesto.list(params), params: params]
    } //list

    def form_ajax() {

        def grupo = Grupo.findAllByCodigoNotIlikeAndCodigoNotIlikeAndCodigoNotIlike('1','2', '3');

        def subPresupuestoInstance = new SubPresupuesto(params)

        if (params.id) {
            subPresupuestoInstance = SubPresupuesto.get(params.id)
            if (!subPresupuestoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Sub Presupuesto con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [subPresupuestoInstance: subPresupuestoInstance, grupo: grupo]
    } //form_ajax

    def save() {



        def grupoFiltrado = Grupo.findAllByCodigoNotIlikeAndCodigoNotIlikeAndCodigoNotIlike('1','2', '3');

        def subpreFiltrado = []

        def var


        grupoFiltrado.each { i->

            var = SubPresupuesto.findAllByGrupo(i)

//            subpreFiltrado.add(var)

            subpreFiltrado += var


        }



        println "save sp: " + params
        def subPresupuestoInstance
        if (params.id) {
            subPresupuestoInstance = SubPresupuesto.get(params.id)
            if (!subPresupuestoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Sub Presupuesto con id " + params.id

                if (params.volob.toString() == "1") {
                    render "NO"
                } else {
                    redirect(action: 'list')
                }
                return
            }//no existe el objeto
            subPresupuestoInstance.properties = params
        }//es edit
        else {
            subPresupuestoInstance = new SubPresupuesto(params)
        } //es create
        if (!subPresupuestoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Sub Presupuesto " + (subPresupuestoInstance.id ? subPresupuestoInstance.id : "") + "</h4>"

            str += "<ul>"
            subPresupuestoInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            if (params.volob.toString() == "1") {
                render "NO"
            } else {
                redirect(action: 'list')
            }
            return
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Sub Presupuesto " + subPresupuestoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Sub Presupuesto " + subPresupuestoInstance.id
        }
        if (params.volob.toString() == "1") {
//            def sel = g.select(name: "subpresupuesto", from: SubPresupuesto.list([order: 'descripcion']), optionKey: "id", optionValue: "descripcion", style: "width: 300px;font-size: 10px", id: "subPres", value: subPresupuestoInstance.id)

            def sel = g.select (name:"subpresupuesto", from: subpreFiltrado , optionKey:"id", optionValue:"descripcion", style:"width: 300px;;font-size: 10px", id:"subPres", value: subPresupuestoInstance.id)

            render sel

                } else {
            redirect(action: 'list')
        }
    } //save

    def show_ajax() {
        def subPresupuestoInstance = SubPresupuesto.get(params.id)
        if (!subPresupuestoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Sub Presupuesto con id " + params.id
            redirect(action: "list")
            return
        }
        [subPresupuestoInstance: subPresupuestoInstance]
    } //show

    def delete() {
        def subPresupuestoInstance = SubPresupuesto.get(params.id)

        println("paramsdelete:" + params)
        println("sub" + subPresupuestoInstance)

        if (!subPresupuestoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Sub Presupuesto con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            subPresupuestoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Sub Presupuesto " + subPresupuestoInstance.descripcion
            def sel = g.select(name: "subpresupuesto", from: SubPresupuesto.list([order: 'descripcion', sort: 'descripcion']), optionKey: "id", optionValue: "descripcion", style: "width: 300px;font-size: 10px", id: "subPres", value: subPresupuestoInstance.id)
            render sel
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Sub Presupuesto:  " + (subPresupuestoInstance.descripcion ? subPresupuestoInstance.descripcion : "")
            render "NO"
//            redirect(action: "list")
        }
    } //delete
} //fin controller
