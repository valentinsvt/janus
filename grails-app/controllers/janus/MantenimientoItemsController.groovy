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
            case "grupo_manoObra":
                hijos = SubgrupoItems.findAllByGrupo(Grupo.get(id), [sort: 'codigo'])[0].id
                hijos = DepartamentoItem.findAllBySubgrupo(SubgrupoItems.get(hijos), [sort: 'codigo'])
                println hijos.descripcion
                break;
            case "grupo_material":
            case "grupo_equipo":
                hijos = SubgrupoItems.findAllByGrupo(Grupo.get(id), [sort: 'codigo'])
                break;
            case "subgrupo_manoObra":
                hijos = Item.findAllByDepartamento(DepartamentoItem.get(id), [sort: 'codigo'])
                println hijos.nombre
                break;
            case "subgrupo_material":
            case "subgrupo_equipo":
                hijos = DepartamentoItem.findAllBySubgrupo(SubgrupoItems.get(id), [sort: 'codigo'])
                break;
            case "departamento_manoObra":
            case "departamento_material":
            case "departamento_equipo":
                hijos = Item.findAllByDepartamento(DepartamentoItem.get(id), [sort: 'codigo'])
                break;
            case "item_manoObra":
            case "item_material":
            case "item_equipo":


                def tipoLista = Item.get(id).tipoLista
//                println(tipoLista)
//
//                println("id" + id)
//                println("item:" + Item.get(id))

                if (precios) {
                    if (ignore) {
                        hijos = ["Todos"]
                    } else {
                        hijos = []
                        if (tipoLista) {
                            hijos = Lugar.findAllByTipoLista(tipoLista)
                        }

//                        hijos = Lugar.list([sort: "descripcion"])
//                        hijos = Lugar.withCriteria {
//                            and {
//                                order("tipo", "asc")
//                                order("descripcion", "asc")
//                            }
//                        }
//                        if (all) {
//                            hijos = Lugar.withCriteria {
//                                and {
//                                    order("tipo", "asc")
//                                    order("descripcion", "asc")
//                                }
//                            }
//                        } else {
//                            hijos = Lugar.findAllByTipo("C", [sort: 'descripcion'])
//                            /*hijos = Lugar.findAll([sort: 'descripcion'])*/
//                        }
                    }
                }
                break;
        }

        String tree = "", clase = "", rel = "", extra = ""

        tree += "<ul>"
        hijos.each { hijo ->
            def hijosH, desc, liId
            switch (tipo) {
                case "grupo_manoObra":
                    hijosH = Item.findAllByDepartamento(hijo, [sort: 'codigo'])
                    desc = hijo.codigo.toString().padLeft(3, '0') + " " + hijo.descripcion
                    def parts = tipo.split("_")
                    rel = "subgrupo_" + parts[1]
                    liId = "sg" + "_" + hijo.id
//                    println "_______________________"
//                    println hijo.descripcion
//                    println hijosH.descripcion
                    break;
                case "grupo_material":
                case "grupo_equipo":
                    hijosH = DepartamentoItem.findAllBySubgrupo(hijo, [sort: 'codigo'])
                    desc = hijo.codigo.toString().padLeft(3, '0') + " " + hijo.descripcion
                    def parts = tipo.split("_")
                    rel = "subgrupo_" + parts[1]
                    liId = "sg" + "_" + hijo.id
                    break;
                case "subgrupo_manoObra":
                    hijosH = []
                    def tipoLista = hijo.tipoLista
                    if (precios) {
                        if (ignore) {
                            hijosH = ["Todos"]
                        } else {
                            if (tipoLista) {
                                hijosH = Lugar.findAllByTipoLista(tipoLista)
                            }
                        }
                    }
                    desc = hijo.codigo + " " + hijo.nombre
                    def parts = tipo.split("_")
                    rel = "item_" + parts[1]
                    liId = "it" + "_" + hijo.id
                    break;
                case "subgrupo_material":
                case "subgrupo_equipo":
                    hijosH = Item.findAllByDepartamento(hijo, [sort: 'codigo'])
                    desc = hijo.subgrupo.codigo.toString().padLeft(3, '0') + '.' + hijo.codigo.toString().padLeft(3, '0') + " " + hijo.descripcion
                    def parts = tipo.split("_")
                    rel = "departamento_" + parts[1]
                    liId = "dp" + "_" + hijo.id
                    break;
                case "departamento_manoObra":
                    hijosH = []
                    if (precios) {
                        hijosH = []
                        if (ignore) {
                            desc = "Todos los lugares"
                            rel = "lugar_all"
                            liId = "lg_" + id + "_all"
                        } else {

//                            println("entro")

                            if (all) {
                                desc = hijo.descripcion + " (" + hijo.tipo + ")"
                            } else {
                                desc = hijo.descripcion
                            }
//                            rel = "lugar_" + hijo.tipo
                            rel = "lugar"
                            liId = "lg_" + id + "_" + hijo.id

                            def obras = Obra.countByLugar(hijo)
//                            println "lugar " + hijo.tipo + " " + hijo.id + " " + hijo.descripcion + "    o: " + obras
                            extra = "data-obras='${obras}'"

                        }
                    }
                    break;
                case "departamento_material":
                case "departamento_equipo":
                    hijosH = []

                    def tipoLista = hijo.tipoLista
                    if (precios) {
                        if (ignore) {
                            hijosH = ["Todos"]
                        } else {

                            if (tipoLista) {
                                hijosH = Lugar.findAllByTipoLista(tipoLista)
                            }
                        }
                    }
                    desc = hijo.codigo + " " + hijo.nombre
                    def parts = tipo.split("_")
                    rel = "item_" + parts[1]
                    liId = "it" + "_" + hijo.id
                    break;
                case "item_manoObra":
//                    hijosH = []
//                    if (precios) {
//                        hijosH = []
//                        if (ignore) {
//                            desc = "mo4  " + "Todos los lugares"
//                            rel = "lugar_all"
//                            liId = "lg_" + id + "_all"
//                        } else {
//
////                            println("entro")
//
//                            if (all) {
//                                desc = hijo.descripcion + " (" + hijo.tipo + ")"
//                            } else {
//                                desc = hijo.descripcion
//                            }
////                            rel = "lugar_" + hijo.tipo
//                            rel = "lugar"
//                            liId = "lg_" + id + "_" + hijo.id
//
//                            def obras = Obra.countByLugar(hijo)
////                            println "lugar " + hijo.tipo + " " + hijo.id + " " + hijo.descripcion + "    o: " + obras
//                            extra = "data-obras='${obras}'"
//
//                        }
//                    }
//                    break;
                case "item_material":
                case "item_equipo":
                    if (precios) {
                        hijosH = []
                        if (ignore) {
                            desc = "Todos los lugares"
                            rel = "lugar_all"
                            liId = "lg_" + id + "_all"
                        } else {

//                            println("entro")

                            if (all) {
                                desc = hijo.descripcion + " (" + hijo.tipo + ")"
                            } else {
                                desc = hijo.descripcion
                            }
//                            rel = "lugar_" + hijo.tipo
                            rel = "lugar"
                            liId = "lg_" + id + "_" + hijo.id

                            def obras = Obra.countByLugar(hijo)
//                            println "lugar " + hijo.tipo + " " + hijo.id + " " + hijo.descripcion + "    o: " + obras
                            extra = "data-obras='${obras}'"

                        }
                    }
                    break;
            }

            clase = (hijosH.size() > 0) ? "jstree-closed hasChildren" : ""

            tree += "<li id='" + liId + "' class='" + clase + "' rel='" + rel + "' " + extra + ">"
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

    def moveNode_ajax() {
//        println params

        def node = params.node
        def newParent = params.newParent

        def parts = node.split("_")
        def tipoNode = parts[0]
        def idNode = parts[1]

        parts = newParent.split("_")
        def tipoParent = parts[0]
        def idParent = parts[1]

        switch (tipoNode) {
            case "it":
                def item = Item.get(idNode.toLong())
                def departamento = DepartamentoItem.get(idParent.toLong())
                item.departamento = departamento

                def cod = item.codigo
                def codItem = cod.split("\\.")[2]
                println "codigo anterior: " + cod
                cod = "" + item.departamento.subgrupo.codigo.toString().padLeft(3, '0') + "." + item.departamento.codigo.toString().padLeft(3, '0') + "." + codItem.toString().padLeft(3, '0')
                println "codigo nuevo: " + cod

                if (item.save(flush: true)) {
                    def tipo
                    def a
                    switch (item.departamento.subgrupo.grupoId) {
                        case 1:
                            tipo = "Material"
                            a = "o"
                            break;
                        case 2:
                            tipo = "Mano de obra"
                            a = "a"
                            break;
                        case 3:
                            tipo = "Equipo"
                            a = "o"
                            break;
                    }
                    render "OK_" + tipo + " movid" + a + " correctamente"
                } else {
                    render "NO_Ha ocurrid un error al mover"
                }
                break;
            case "dp":
                def departamento = DepartamentoItem.get(idNode.toLong())
                def subgrupo = SubgrupoItems.get(idParent.toLong())
                departamento.subgrupo = subgrupo
                if (departamento.save(flush: true)) {
                    render "OK_Subgrupo movido correctamente"
                } else {
                    render "NO_Ha ocurrid un error al mover"
                }
                break;
            default:
                render "NO"
                break;
        }
    }

    def loadLugarPorTipo() {
        params.tipo = params.tipo.toString().toUpperCase()
        def lugares = Lugar.findAllByTipo(params.tipo, [sort: 'descripcion'])
        def sel = g.select(name: "lugar", from: lugares, optionKey: "id", optionValue: { it.descripcion + ' (' + it.tipo + ')' })
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
        println "showIt_ajax" + params
        def itemInstance = Item.get(params.id)
        return [itemInstance: itemInstance]
    }

    def formIt_ajax() {
        def departamento = DepartamentoItem.get(params.departamento)
        def itemInstance = new Item()
        if (params.id) {
            itemInstance = Item.get(params.id)
        }
        return [departamento: departamento, itemInstance: itemInstance, grupo: params.grupo]
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


    def infoItems() {

        def item = Item.get(params.id)

        def rubro = Rubro.findAllByItem(item)

        def precios = PrecioRubrosItems.findAllByItem(item)

        def fpItems = ItemsFormulaPolinomica.findAllByItem(item)


        return [item: item, rubro: rubro, precios: precios, fpItems: fpItems, delete: params.delete]

    }



    def saveIt_ajax() {
        def dep = DepartamentoItem.get(params.departamento.id)
        params.tipoItem = TipoItem.findByCodigo("I")
        params.fechaModificacion = new Date()
        params.nombre = params.nombre.toString().toUpperCase()
        params.campo = params.campo.toString().toUpperCase()
        params.observaciones = params.observaciones.toString().toUpperCase()
        if (!params.codigo.contains(".")) {
            params.codigo = dep.subgrupo.codigo.toString().padLeft(3, '0') + "." + dep.codigo.toString().padLeft(3, '0') + "." + params.codigo
        }
        if (params.fecha) {
            params.fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        } else {
            params.fecha = new Date()

        }

        if (!params.tipoLista) {

            params.tipoLista = TipoLista.get(6)


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

    def checkFcPr_ajax() {
//        println params
        if (!params.lugar) {
            render true
        } else {
            def precios = PrecioRubrosItems.withCriteria {
                and {
                    eq("lugar", Lugar.get(params.lugar))
                    eq("fecha", new Date().parse("dd-MM-yyyy", params.fecha))
                    eq("item", Item.get(params.item))
                }
            }
            if (precios.size() == 0) {
                render true
            } else {
                render false
            }
        }
    }

    def savePrecio_ajax() {
//        println params
        def item = Item.get(params.item.id)
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
//            def tipo = ["C"]
            if (params.ignore == "true") {
//                if (params.all == "true") {
//                    tipo.add("V")
//                }
                def error = 0
                Lugar.findAllByTipoLista(item.tipoLista).each { lugar ->
                    def precios = PrecioRubrosItems.withCriteria {
                        and {
                            eq("lugar", lugar)
                            eq("fecha", params.fecha)
                            eq("item", item)
                        }
                    }
                    if (precios.size() == 0) {
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
        def ok = true
        if (params.auto) {
            def usu = Persona.get(session.usuario.id)
            if (params.auto.toString().encodeAsMD5() != usu.autorizacion) {
                ok = false
                render "Ha ocurrido un error en la autorización."
            }
        }
        if (ok) {
            try {
                rubroPrecioInstance.delete(flush: true)
                render "OK"
            }
            catch (DataIntegrityViolationException e) {
                println e
                render "No se pudo eliminar el precio."
            }
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

    def calcPrecEq() {
        def anio = new Date().format("yyyy").toInteger()
        def item = Item.get(params.item.toLong())
        def valoresAnuales = ValoresAnuales.findByAnio(anio)
        return [item: item, valoresAnuales: valoresAnuales]
    }

    def calcPrecioRef_ajax() {
        render formatNumber(number: calcPrecioRef(params.precio.toDouble()), maxFractionDigits: 5, minFractionDigits: 5)
    }

    def calcPrecioRef(precioAnt) {

        def anio = new Date().format("yyyy").toInteger()
        def sbuAct = ValoresAnuales.findByAnio(anio).sueldoBasicoUnificado
        def sbuAnt = ValoresAnuales.findByAnio(anio - 1).sueldoBasicoUnificado

        def delta = sbuAct / sbuAnt
        def nuevoCosto = precioAnt * delta

//        println precioAnt + " " + anio + " " + sbuAct + " " + sbuAnt + " " + delta + " " + nuevoCosto

        return nuevoCosto
    }

    def calcPrecio(params) {
//        println params
        def lugar = []
        def precios = []
        def precioRef = false
        def fecha = params.fecha

        if (params.fecha == "all") {
            params.todasLasFechas = "true"
        } else {
            params.todasLasFechas = "false"
            params.fecha = new Date().parse("dd-MM-yyyy", fecha)
        }
        if (params.todasLasFechas == "true") {
            fecha = null
        }

        if (params.lugarId == "all") {
            lugar = Lugar.list([sort: "descripcion"])
        } else {
            lugar.add(Lugar.get(params.lugarId))
        }

//        println ">>> " + fecha + "   " + params.itemId + "    " + params.operador
        lugar.each {
            def tmp = preciosService.getPrecioRubroItemOperador(fecha, it, params.itemId, params.operador)
            if (tmp.size() > 0)
                precios += tmp
        }
        def res = []
        precios.each {
            res.add(PrecioRubrosItems.get(it))
        }
        precios = res

        def anio = new Date().format("yyyy").toInteger()
        def anioRef = anio - 1
        def precioActual = precios.findAll { it.fecha >= new Date().parse("dd-MM-yyyy", "01-01-${anio}") && it.fecha <= new Date().parse("dd-MM-yyyy", "31-12-${anio}") }
        precioActual = precioActual.sort { it.fecha }
        if (precioActual) {
            def newest = precioActual[precioActual.size() - 1]
//            println "Precio ${anio} al " + newest.fecha + " " + newest.precioUnitario + ": se muestra este?"
            precioRef = newest.precioUnitario
        } else {
//            println "no hay precio de este anio (${anio})"
            def precioAnterior = precios.findAll { it.fecha >= new Date().parse("dd-MM-yyyy", "01-01-${anioRef}") && it.fecha <= new Date().parse("dd-MM-yyyy", "31-12-${anioRef}") }
            if (precioAnterior) {
                def newest = precioAnterior[precioAnterior.size() - 1]
//                println "Precio ${anioRef} al " + newest.fecha + " " + newest.precioUnitario + ": se calcula"

//                def sbuAct = ValoresAnuales.findByAnio(anio).sueldoBasicoUnificado
//                def sbuAnt = ValoresAnuales.findByAnio(anioRef).sueldoBasicoUnificado
//
//                def delta = sbuAct / sbuAnt
//                def nuevoCosto = newest.precioUnitario * delta
//                precioRef = nuevoCosto
                precioRef = calcPrecioRef(newest.precioUnitario)
            } else {
//                println "no hay precio del anio pasado (${anioRef}): hay q pedir"
            }
        }

        return [precios: precios, precioRef: precioRef, anioRef: anioRef]
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
        def operador = params.operador
        def fecha = params.fecha

        def lugarNombre

        if (params.todasLasFechas == "true") {
            fecha = null
        }
        if (lugarId == "all") {
            lugarNombre = "todos los lugares"
        } else {
            def l = Lugar.get(lugarId)
            lugarNombre = l.descripcion + " <i>(" + (l.tipoLista ? l.tipoLista?.descripcion : 'sin tipo') + ")</i>"
        }

//        println "parametros busqueda " + fecha + " - " + itemId + " - " + operador
        def r = calcPrecio([
                lugarId: lugarId,
                fecha: params.fecha,
                operador: operador,
                todasLasFechas: params.todasLasFechas,
                itemId: itemId
        ])

        return [item: item, lugarNombre: lugarNombre, lugarId: lugarId, precios: r.precios, lgar: lugarId == "all", fecha: operador == "=" ? fecha.format("dd-MM-yyyy") : null,
                params: params, precioRef: r.precioRef, anioRef: r.anioRef]
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
        params.descripcion = params.descripcion.toString().toUpperCase()
        if (params.id) {
            lugar = Lugar.get(params.id)
            accion = "edit"
        }
        lugar.properties = params
        if (lugar.save(flush: true)) {
            render "OK_" + accion + "_" + lugar.id + "_" + (lugar.descripcion + (params.all.toString().toBoolean() ? " (" + lugar.tipo + ")" : "")) + "_c"
        } else {
            println lugar.errors
            def errores = g.renderErrors(bean: lugar)
            render "NO_" + errores
        }
    }

    def deleteLg_ajax() {
        println "DELETE LUGAR "
        println params
        def lugar = Lugar.get(params.id)

        def precios = PrecioRubrosItems.findAllByLugar(lugar)
        def cant = 0
        precios.each { p ->
            try {
                p.delete(flush: true)
                println "p deleted " + p.id
                cant++
            } catch (DataIntegrityViolationException e) {
                println e
                println "p not deleted " + p.id
            }
        }

        try {
            lugar.delete(flush: true)
            render "OK"
        } catch (DataIntegrityViolationException e) {
            println e
            render "NO"
        }
    }
}
