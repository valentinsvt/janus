package janus

import org.springframework.dao.DataIntegrityViolationException
import jxl.write.DateTime

class ItemController extends janus.seguridad.Shield {

    def preciosService
    def dbConnectionService

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

//        println "tabla " + params
        if (!params.max || params.max == 0) {
            params.max = 100
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
        //        println("fechaControlador:" + f)
        def t = params.todos
//        def todos = Lugar.list();
        def lugar;
        def rubroPrecio;

        if (t == "1") {
            println "AQUI " + params
            lugar = Lugar.get(params.lgar)
            def c = PrecioRubrosItems.createCriteria()
            rubroPrecio = c.list(max: params.max, offset: params.offset) {
                eq("lugar", lugar)
                item {
                    if (params.tipo != "-1") {
                        departamento {
                            subgrupo {
                                eq("grupo", Grupo.get(params.tipo))
                            }
                        }
                    }
                    order("codigo", "asc")
                }
                order("fecha", "desc")
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

            println("aca")

            lugar = Lugar.get(params.lgar)

            def sql


            sql = "select distinct rbpc.item__id, item.itemcdgo "
            sql += "from rbpc, item"
            if (params.tipo != "-1") {
                sql += ", dprt, sbgr, grpo"
            }
            sql += " where rbpc.item__id=item.item__id and lgar__id=${lugar.id} "
            if (params.tipo != "-1") {
                sql += "and item.dprt__id = dprt.dprt__id "
                sql += "and dprt.sbgr__id = sbgr.sbgr__id "
                sql += "and sbgr.grpo__id = grpo.grpo__id "
                sql += "and grpo.grpo__id = ${params.tipo}"
            }
            sql += " order by itemcdgo "
            sql += "limit ${params.max} "
            sql += "offset ${params.offset} "

//            println sql

            def itemsIds = ""

            def cn = dbConnectionService.getConnection()
            cn.eachRow(sql.toString()) {row ->
                if (itemsIds != "") {
                    itemsIds += ","
                }
                itemsIds += row[0]
            }

            def precios = preciosService.getPrecioRubroItem(f, lugar, itemsIds)
            rubroPrecio = []

            precios.each {
                rubroPrecio.add(PrecioRubrosItems.get(it))

            }

            if (!params.totalRows) {
                sql = "select count(distinct rbpc.item__id) "
                sql += "from rbpc "
                sql += "where lgar__id=${lugar.id}"

                def totalCount
                cn.eachRow(sql.toString()) {row ->
                    totalCount = row[0]
                }

                params.totalRows = totalCount

                params.totalPags = Math.ceil(params.totalRows / params.max).toInteger()

                println("total" + params.totalPags)

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
            cn.close()
        }

        [rubroPrecio: rubroPrecio, params: params, lugar:lugar]
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
