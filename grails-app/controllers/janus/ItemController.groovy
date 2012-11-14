package janus

import org.springframework.dao.DataIntegrityViolationException
import jxl.write.DateTime

class ItemController extends janus.seguridad.Shield {

    def preciosService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [itemInstanceList: Item.list(params), itemInstanceTotal: Item.count(), params: params]
    } //list

    def mantenimientoPrecios() {

    }

    def tabla() {

        println "tabla " + params
        if (!params.max || params.max == 0) {
            params.max = 10
        } else {
            params.max = params.max.toInteger()
        }
        if (!params.pag) {
            params.pag = 1;
        } else {
            params.pag = params.pag.toInteger()
        }


        params.offset = params.max * (params.pag - 1)



        def f = new Date().parse("dd-MM-yyyy", params.fecha)

        println("fechaControlador:" + f)


        def t = params.todos

        def todos = [];

        todos = Lugar.list();

        def lugar;

        def rubroPrecio;

        if (t == "1") {

            lugar = Lugar.get(params.lgar)


            def c = PrecioRubrosItems.createCriteria()
            rubroPrecio = c.list(max: params.max, offset: params.offset) {
                eq("lugar", lugar)
                item {
                    order("nombre", "asc")
                }
            }

            params.totalRows = rubroPrecio.totalCount
            params.totalPags = Math.ceil(rubroPrecio.totalCount / params.max).toInteger()

            if (params.totalPags <= 10) {
                params.first = 1
                params.last = params.last = params.totalPags
            } else {
                params.first = Math.max(1, params.pag.toInteger() - 5)
                params.last = Math.min(params.totalPags, params.pag + 5)

                def ts = params.last - params.first
                if (ts < 9) {
                    def r = 10 - ts
                    params.last = Math.min(params.totalPags, params.last + r).toInteger()
                }
            }


        } else {

//            def fcha = PrecioRubrosItems.createCriteria()
//            rubroPrecio = fcha.list (max: params.max, offset: params.offset){
//
//                 le("fecha",f)
//                item {
//                    order("nombre","asc")
//                }
//
//            }

            lugar = Lugar.get(params.lgar)

//            def c = PrecioRubrosItems.createCriteria()
//            rubroPrecio = c.list(max: params.max, offset: params.offset) {
//                eq("lugar", lugar)
//                projections {
//                    distinct("item")
//                }
//
//            }

            /*todo paginacion y como sacar N sin repetidos?? sql*/
            rubroPrecio = PrecioRubrosItems.findAllByLugar(lugar,[max: 100]).item
            println "items " + rubroPrecio

            def precios = preciosService.getPrecioRubroItem(f, lugar, rubroPrecio)
            rubroPrecio=[]
            precios.each {
                  rubroPrecio.add(PrecioRubrosItems.get(it))
            }

            println "precios " + precios
            println "precios " + rubroPrecio

//            rubroPrecio = PrecioRubrosItems.list(max: params.max, offset: params.offset);


            params.totalRows = 100
            params.totalPags = Math.ceil(100 / params.max).toInteger()

            if (params.totalPags <= 10) {
                params.first = 1
                params.last = params.last = params.totalPags
            } else {
                params.first = Math.max(1, params.pag.toInteger() - 5)
                params.last = Math.min(params.totalPags, params.pag + 5)

                def ts = params.last - params.first
                if (ts < 9) {
                    def r = 10 - ts
                    params.last = Math.min(params.totalPags, params.last + r).toInteger()
                }
            }


        }

        [rubroPrecio: rubroPrecio, params: params]
    }



    def form_ajax() {
        def itemInstance = new Item(params)
        if (params.id) {
            itemInstance = Item.get(params.id)
            if (!itemInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Item con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [itemInstance: itemInstance]
    } //form_ajax

    def save() {
        def itemInstance
        if (params.id) {
            itemInstance = Item.get(params.id)
            if (!itemInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Item con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            itemInstance.properties = params
        }//es edit
        else {
            itemInstance = new Item(params)
        } //es create
        if (!itemInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Item " + (itemInstance.id ? itemInstance.id : "") + "</h4>"

            str += "<ul>"
            itemInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamete Item " + itemInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamete Item " + itemInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def itemInstance = Item.get(params.id)
        if (!itemInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Item con id " + params.id
            redirect(action: "list")
            return
        }
        [itemInstance: itemInstance]
    } //show

    def delete() {
        def itemInstance = Item.get(params.id)
        if (!itemInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Item con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            itemInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamete Item " + itemInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Item " + (itemInstance.id ? itemInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
