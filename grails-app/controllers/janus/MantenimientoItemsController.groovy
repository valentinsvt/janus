package janus

import janus.seguridad.Shield
import org.springframework.dao.DataIntegrityViolationException

class MantenimientoItemsController extends Shield {

    def preciosService

    String makeBasicTree(params) {

        def id = params.id
        def tipo = params.tipo
        def precios = params.precios
        def all = params.all ? params.all.toBoolean() : false
        def ignore = params.ignore ? params.ignore.toBoolean() : false

//        println "all:" + all + "     ignore:" + ignore

//        println id
//        println tipo

        def hijos = []

        switch (tipo) {
            case "grupo_material":
            case "grupo_manoObra":
            case "grupo_equipo":
                hijos = SubgrupoItems.findAllByGrupo(Grupo.get(id), [sort: 'descripcion'])
                break;
            case "subgrupo_material":
            case "subgrupo_manoObra":
            case "subgrupo_equipo":
                hijos = DepartamentoItem.findAllBySubgrupo(SubgrupoItems.get(id), [sort: 'descripcion'])
                break;
            case "departamento_material":
            case "departamento_manoObra":
            case "departamento_equipo":
                hijos = Item.findAllByDepartamento(DepartamentoItem.get(id), [sort: 'nombre'])
                break;
            case "item_material":
            case "item_manoObra":
            case "item_equipo":
                if (precios) {
                    if (ignore) {
                        hijos = ["Todos"]
                    } else {
                        if (all) {
                            hijos = Lugar.withCriteria {
                                and {
                                    order("tipo", "asc")
                                    order("descripcion", "asc")
                                }
                            }
                        } else {
                            hijos = Lugar.findAllByTipo("C", [sort: 'descripcion'])
                        }
                    }
                }
                break;
        }

        String tree = "", clase = "", rel = ""

        tree += "<ul>"
        hijos.each { hijo ->
            def hijosH, desc, liId
            switch (tipo) {
                case "grupo_material":
                case "grupo_manoObra":
                case "grupo_equipo":
                    hijosH = DepartamentoItem.findAllBySubgrupo(hijo, [sort: 'descripcion'])
                    desc = hijo.descripcion
                    def parts = tipo.split("_")
                    rel = "subgrupo_" + parts[1]
                    liId = "sg" + "_" + hijo.id
                    break;
                case "subgrupo_material":
                case "subgrupo_manoObra":
                case "subgrupo_equipo":
                    hijosH = Item.findAllByDepartamento(hijo, [sort: 'nombre'])
                    desc = hijo.descripcion
                    def parts = tipo.split("_")
                    rel = "departamento_" + parts[1]
                    liId = "dp" + "_" + hijo.id
                    break;
                case "departamento_material":
                case "departamento_manoObra":
                case "departamento_equipo":
                    hijosH = []
                    if (precios) {
                        if (ignore) {
                            hijosH = ["Todos"]
                        } else {
                            if (all) {
                                hijosH = Lugar.withCriteria {
                                    and {
                                        order("tipo", "asc")
                                        order("descripcion", "asc")
                                    }
                                }
                            } else {
                                hijosH = Lugar.findAllByTipo("C", [sort: 'descripcion'])
                            }
                        }
                    }
                    desc = hijo.nombre
                    def parts = tipo.split("_")
                    rel = "item_" + parts[1]
                    liId = "it" + "_" + hijo.id
                    break;
                case "item_material":
                case "item_manoObra":
                case "item_equipo":
                    if (precios) {
                        hijosH = []
                        if (ignore) {
                            desc = "Todos los lugares"
                            rel = "lugar_all"
                            liId = "lg_" + id + "_all"
                        } else {
                            if (all) {
                                desc = hijo.descripcion + " (" + hijo.tipo + ")"
                            } else {
                                desc = hijo.descripcion
                            }
                            rel = "lugar_" + hijo.tipo
                            liId = "lg_" + id + "_" + hijo.id
                        }
                    }
                    break;
            }

            clase = (hijosH.size() > 0) ? "jstree-closed hasChildren" : ""

            tree += "<li id='" + liId + "' class='" + clase + "' rel='" + rel + "'>"
            tree += "<a href='#' class='label_arbol'>" + desc + "</a>"
            tree += "</li>"
        }
        tree += "</ul>"
        return tree
    }

    def loadTreePart() {
        render(makeBasicTree(params))
    }

    def searchTree_ajax() {
//        println params
//        def parts = params.search_string.split("~")
        def search = params.search.trim()
        if (search != "") {
            def id = params.tipo
            def find = Item.withCriteria {
                departamento {
                    subgrupo {
                        grupo {
                            eq("id", id.toLong())
                        }
                    }
                }
                ilike("nombre", "%" + search + "%")
            }
            def departamentos = [], subgrupos = [], grupos = []
            find.each { item ->
                if (!departamentos.contains(item.departamento))
                    departamentos.add(item.departamento)
                if (!subgrupos.contains(item.departamento.subgrupo))
                    subgrupos.add(item.departamento.subgrupo)
                if (!grupos.contains(item.departamento.subgrupo.grupo))
                    grupos.add(item.departamento.subgrupo.grupo)
            }

            def ids = "["

            if (find.size() > 0) {
                ids += "\"#materiales_1\","

                grupos.each { gr ->
                    ids += "\"#gr_" + gr.id + "\","
                }
                subgrupos.each { sg ->
                    ids += "\"#sg_" + sg.id + "\","
                }
                departamentos.each { dp ->
                    ids += "\"#dp_" + dp.id + "\","
                }
                ids = ids[0..-2]
            }
            ids += "]"
//            println ">>>>>>"
//            println ids
//            println "<<<<<<<"
            render ids
        } else {
            render ""
        }
    }

    def search_ajax() {
        def search = params.search.trim()
        def id = params.tipo
        def find = Item.withCriteria {
            departamento {
                subgrupo {
                    grupo {
                        eq("id", id.toLong())
                    }
                }
            }
            ilike("nombre", "%" + search + "%")
        }
        def json = "["
        find.each { item ->
            if (json != "[") {
                json += ","
            }
            json += "\"" + item.nombre + "\""
        }
        json += "]"
        render json
    }

    def registro() {
        //<!--grpo--><!--sbgr -> Grupo--><!--dprt -> Subgrupo--><!--item-->
        //materiales = 1
        //mano de obra = 2
        //equipo = 3
    }

    def loadLugarPorTipo() {
        params.tipo = params.tipo.toString().toUpperCase()
        def lugares = Lugar.findAllByTipo(params.tipo, [sort: 'descripcion'])
        def sel = g.select(name: "lugar", from: lugares, optionKey: "id", optionValue: {it.descripcion + ' (' + it.tipo + ')'})
        render sel
    }

    def reportePreciosUI() {
        def lugares = Lugar.list()
        def grupo = Grupo.get(params.grupo)
        return [lugares: lugares, grupo: grupo]
    }

    def precios() {
        //lugar
        //rubro precio
    }

    def showGr_ajax() {
        def grupoInstance = Grupo.get(params.id)
        return [grupoInstance: grupoInstance]
    }

    def showSg_ajax() {
        def subgrupoItemsInstance = SubgrupoItems.get(params.id)
        return [subgrupoItemsInstance: subgrupoItemsInstance]
    }

    def formSg_ajax() {
        def grupo = Grupo.get(params.grupo)
        def subgrupoItemsInstance = new SubgrupoItems()
        if (params.id) {
            subgrupoItemsInstance = SubgrupoItems.get(params.id)
        }
        return [grupo: grupo, subgrupoItemsInstance: subgrupoItemsInstance]
    }

    def checkDsSg_ajax() {
        if (params.id) {
            def subgrupo = SubgrupoItems.get(params.id)
            if (params.descripcion == subgrupo.descripcion) {
                render true
            } else {
                def subgrupos = SubgrupoItems.findAllByDescripcion(params.descripcion)
                if (subgrupos.size() == 0) {
                    render true
                } else {
                    render false
                }
            }
        } else {
            def subgrupos = SubgrupoItems.findAllByDescripcion(params.descripcion)
            if (subgrupos.size() == 0) {
                render true
            } else {
                render false
            }
        }
    }

    def saveSg_ajax() {
        def accion = "create"
        def subgrupo = new SubgrupoItems()
        if (params.id) {
            subgrupo = SubgrupoItems.get(params.id)
            accion = "edit"
        }
        subgrupo.properties = params
        if (subgrupo.save(flush: true)) {
            render "OK_" + accion + "_" + subgrupo.id + "_" + subgrupo.descripcion
        } else {
            def errores = g.renderErrors(bean: subgrupo)
            render "NO_" + errores
        }
    }

    def deleteSg_ajax() {
        def subgrupo = SubgrupoItems.get(params.id)
        try {
            subgrupo.delete(flush: true)
            render "OK"
        }
        catch (DataIntegrityViolationException e) {
            println e
            render "NO"
        }
    }

    def showDp_ajax() {
        def departamentoItemInstance = DepartamentoItem.get(params.id)
        return [departamentoItemInstance: departamentoItemInstance]
    }

    def formDp_ajax() {
        def subgrupo = SubgrupoItems.get(params.subgrupo)
        def departamentoItemInstance = new DepartamentoItem()
        if (params.id) {
            departamentoItemInstance = DepartamentoItem.get(params.id)
        }
        return [subgrupo: subgrupo, departamentoItemInstance: departamentoItemInstance]
    }

    def checkCdDp_ajax() {
//        println params
        if (params.id) {
            def departamento = DepartamentoItem.get(params.id)
//            println params.codigo
//            println params.codigo.class
//            println departamento.codigo
//            println departamento.codigo.class
            if (params.codigo == departamento.codigo.toString()) {
                render true
            } else {
                def departamentos = DepartamentoItem.findAllByCodigoAndSubgrupo(params.codigo, SubgrupoItems.get(params.sg))
                if (departamentos.size() == 0) {
                    render true
                } else {
                    render false
                }
            }
        } else {
            def departamentos = DepartamentoItem.findAllByCodigoAndSubgrupo(params.codigo, SubgrupoItems.get(params.sg))
            if (departamentos.size() == 0) {
                render true
            } else {
                render false
            }
        }
    }

    def checkDsDp_ajax() {
        if (params.id) {
            def departamento = DepartamentoItem.get(params.id)
            if (params.descripcion == departamento.descripcion) {
                render true
            } else {
                def departamentos = DepartamentoItem.findAllByDescripcion(params.descripcion)
                if (departamentos.size() == 0) {
                    render true
                } else {
                    render false
                }
            }
        } else {
            def departamentos = DepartamentoItem.findAllByDescripcion(params.descripcion)
            if (departamentos.size() == 0) {
                render true
            } else {
                render false
            }
        }
    }

    def saveDp_ajax() {
//        println params
        def accion = "create"
        def departamento = new DepartamentoItem()
        if (params.id) {
//            println "EDIT!!!!"
            departamento = DepartamentoItem.get(params.id)
//            println "\t\t" + departamento.codigo
            accion = "edit"
        }
        departamento.properties = params
        if (departamento.save(flush: true)) {
            render "OK_" + accion + "_" + departamento.id + "_" + departamento.descripcion
        } else {
            println departamento.errors
            def errores = g.renderErrors(bean: departamento)
            render "NO_" + errores
        }
    }

    def deleteDp_ajax() {
        def departamento = DepartamentoItem.get(params.id)
        try {
            departamento.delete(flush: true)
            render "OK"
        }
        catch (DataIntegrityViolationException e) {
            println e
            render "NO"
        }
    }

    def showIt_ajax() {
        def itemInstance = Item.get(params.id)
        return [itemInstance: itemInstance]
    }

    def formIt_ajax() {
        def departamento = DepartamentoItem.get(params.departamento)
        def itemInstance = new Item()
        if (params.id) {
            itemInstance = Item.get(params.id)
        }
        return [departamento: departamento, itemInstance: itemInstance, grupo:params.grupo]
    }

    def checkCdIt_ajax() {
        def dep = DepartamentoItem.get(params.dep)
        params.codigo = dep.subgrupo.codigo.toString().padLeft(3, '0') + "." + dep.codigo.toString().padLeft(3, '0') + "." + params.codigo
        println params
        if (params.id) {
            def item = Item.get(params.id)
            if (params.codigo.toString().trim() == item.codigo.toString().trim()) {
                render true
            } else {
                def items = Item.findAllByCodigo(params.codigo)
                if (items.size() == 0) {
                    render true
                } else {
                    render false
                }
            }
        } else {
            def items = Item.findAllByCodigo(params.codigo)
            if (items.size() == 0) {
                render true
            } else {
                render false
            }
        }
    }

    def checkNmIt_ajax() {
        if (params.id) {
            def item = Item.get(params.id)
            if (params.nombre == item.nombre) {
                render true
            } else {
                def items = Item.findAllByNombre(params.nombre)
                if (items.size() == 0) {
                    render true
                } else {
                    render false
                }
            }
        } else {
            def items = Item.findAllByNombre(params.nombre)
            if (items.size() == 0) {
                render true
            } else {
                render false
            }
        }
    }

    def checkCmIt_ajax() {
        if (params.id) {
            def item = Item.get(params.id)
            if (params.campo == item.campo) {
                render true
            } else {
                def items = Item.findAllByCampo(params.campo)
                if (items.size() == 0) {
                    render true
                } else {
                    render false
                }
            }
        } else {
            def items = Item.findAllByCampo(params.campo)
            if (items.size() == 0) {
                render true
            } else {
                render false
            }
        }
    }

    def saveIt_ajax() {
        def dep = DepartamentoItem.get(params.departamento.id)
        params.tipoItem = TipoItem.findByCodigo("I")
        if (!params.codigo.contains(".")) {
            params.codigo = dep.subgrupo.codigo.toString().padLeft(3, '0') + "." + dep.codigo.toString().padLeft(3, '0') + "." + params.codigo
        }
        if (params.fecha) {
            params.fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        }
        def accion = "create"
        def item = new Item()
        if (params.id) {
            item = Item.get(params.id)
            accion = "edit"
        }
//        println "ITEM: " + params
        item.properties = params
        if (item.save(flush: true)) {
            render "OK_" + accion + "_" + item.id + "_" + item.nombre
        } else {
            println item.errors
            def errores = g.renderErrors(bean: item)
            render "NO_" + errores
        }
    }

    def deleteIt_ajax() {
        def item = Item.get(params.id)
        try {
            item.delete(flush: true)
            render "OK"
        }
        catch (DataIntegrityViolationException e) {
            println e
            render "NO"
        }
    }

    def formPrecio_ajax() {
        def item = Item.get(params.item)
        def lugar = null
        if (params.lugar != "all") {
            lugar = Lugar.get(params.lugar)
        }
        def precioRubrosItemsInstance = new PrecioRubrosItems()
        precioRubrosItemsInstance.item = item
        if (lugar) {
            precioRubrosItemsInstance.lugar = lugar
        }
        return [precioRubrosItemsInstance: precioRubrosItemsInstance, lugar: lugar, lugarNombre: params.nombreLugar, fecha: params.fecha, params: params]
    }

    def savePrecio_ajax() {
        println params
        params.fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        if (params.lugar.id != "-1") {
            def precioRubrosItemsInstance = new PrecioRubrosItems(params)
            if (precioRubrosItemsInstance.save(flush: true)) {
                render "OK"
            } else {
                println precioRubrosItemsInstance.errors
                render "NO"
            }
        } else {
            def tipo = ["C"]
            if (params.ignore == "true") {
                if (params.all == "true") {
                    tipo.add("V")
                }
                def error = 0
                Lugar.findAllByTipoInList(tipo).each { lugar ->
                    def precioRubrosItemsInstance = new PrecioRubrosItems()
                    precioRubrosItemsInstance.precioUnitario = params.precioUnitario.toDouble()
                    precioRubrosItemsInstance.lugar = lugar
                    precioRubrosItemsInstance.item = Item.get(params.item.id)
                    precioRubrosItemsInstance.fecha = params.fecha
                    if (precioRubrosItemsInstance.save(flush: true)) {
                        println "OK"
                    } else {
                        println precioRubrosItemsInstance.errors
                        error++
                    }
                }
                if (error == 0) {
                    render "OK"
                } else {
                    render "NO"
                }
            }
        }
    }

    def deletePrecio_ajax() {
        def rubroPrecioInstance = PrecioRubrosItems.get(params.id);
        try {
            rubroPrecioInstance.delete(flush: true)
            render "OK"
        }
        catch (DataIntegrityViolationException e) {
            println e
            render "NO"
        }
    }

    def actualizarPrecios_ajax() {
        if (params.item instanceof java.lang.String) {
            params.item = [params.item]
        }

        def oks = "", nos = ""

        params.item.each {
            def parts = it.split("_")

            println parts

            def rubroId = parts[0]
            def nuevoPrecio = parts[1]

            def rubroPrecioInstance = PrecioRubrosItems.get(rubroId);
            rubroPrecioInstance.precioUnitario = nuevoPrecio.toDouble();
            println rubroPrecioInstance.precioUnitario
            if (!rubroPrecioInstance.save(flush: true)) {
                println "error " + parts
                if (nos != "") {
                    nos += ","
                }
                nos += "#" + rubroId
            } else {
                if (oks != "") {
                    oks += ","
                }
                oks += "#" + rubroId
            }

        }
        render oks + "_" + nos
    }

    def showLg_ajax() {
//        params.tipo = "C"
//        params.operador = "<"
        if (params.fecha == "all") {
            params.todasLasFechas = "true"
        } else {
            params.todasLasFechas = "false"
            params.fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        }
//        println "show lg" + params

        def parts = params.id.split("_")
        def itemId = parts[0]
        def lugarId = parts[1]
        def item = Item.get(itemId)
        def lugar = []
        def precios = []
        def operador = params.operador
        def fecha = params.fecha

        def lugarNombre, lugarTipo

        if (params.todasLasFechas == "true") {
            fecha = null
        }
        if (params.all == "true") {
            params.tipo = ['C', 'V']
            lugarTipo = " (C y V)"
        } else {
            params.tipo = ['C']
            lugarTipo = " (C)"
        }
        if (lugarId == "all") {
            lugar = Lugar.findAllByTipoInList(params.tipo, [sort: "descripcion"])
            lugarNombre = "todos los lugares" + lugarTipo
        } else {
            lugar.add(Lugar.get(lugarId))
            lugarNombre = lugar[0].descripcion
        }
//        println "parametros busqueda "+fecha+" - "+itemId+" - "+operador
        lugar.each {
            def tmp = preciosService.getPrecioRubroItemOperador(fecha, it, itemId, operador)
            if (tmp.size() > 0)
                precios += tmp
        }
        def res = []
        precios.each {
            res.add(PrecioRubrosItems.get(it))
        }
        precios = res

        return [item: item, lugarNombre: lugarNombre, lugarId: lugarId, precios: precios, lgar: lugarId == "all", fecha: operador == "=" ? fecha.format("dd-MM-yyyy") : null, params: params]
    }

    def formLg_ajax() {
        def lugarInstance = new Lugar()
        def tipo = "C"
        if (params.id) {
            lugarInstance = Lugar.get(params.id)
            tipo = lugarInstance.tipo
        }
        return [lugarInstance: lugarInstance, all: params.all, tipo: tipo]
    }

    def checkCdLg_ajax() {
        if (params.id) {
            def lugar = Lugar.get(params.id)
            if (params.codigo.toString().trim() == lugar.codigo.toString().trim()) {
                render true
            } else {
                def lugares = Lugar.findAllByCodigo(params.codigo)
                if (lugares.size() == 0) {
                    render true
                } else {
                    render false
                }
            }
        } else {
            def lugares = Lugar.findAllByCodigo(params.codigo)
            if (lugares.size() == 0) {
                render true
            } else {
                render false
            }
        }
    }

    def checkDsLg_ajax() {
        if (params.id) {
            def lugar = Lugar.get(params.id)
            if (params.descripcion == lugar.descripcion) {
                render true
            } else {
                def lugares = Lugar.findAllByDescripcion(params.descripcion)
                if (lugares.size() == 0) {
                    render true
                } else {
                    render false
                }
            }
        } else {
            def lugares = Lugar.findAllByDescripcion(params.descripcion)
            if (lugares.size() == 0) {
                render true
            } else {
                render false
            }
        }
    }

    def saveLg_ajax() {
        def accion = "create"
        def lugar = new Lugar()
        if (params.id) {
            lugar = Lugar.get(params.id)
            accion = "edit"
        }
        lugar.properties = params
        if (lugar.save(flush: true)) {
            render "OK_" + accion + "_" + lugar.id + "_" + (lugar.descripcion + (params.all.toString().toBoolean() ? " (" + lugar.tipo + ")" : "")) + "_" + lugar.tipo.toLowerCase()
        } else {
            println lugar.errors
            def errores = g.renderErrors(bean: lugar)
            render "NO_" + errores
        }
    }

    def deleteLg_ajax() {
        def lugar = Lugar.get(params.id)
        try {
            lugar.delete(flush: true)
            render "OK"
        }
        catch (DataIntegrityViolationException e) {
            println e
            render "NO"
        }
    }
}
