package janus

import janus.pac.Anio
import janus.pac.CodigoComprasPublicas
import janus.seguridad.Shield
import org.springframework.dao.DataIntegrityViolationException

import java.text.DecimalFormat

class MantenimientoItemsController extends Shield {

    def preciosService
    def oferentesService
    def dbConnectionService

    def buscadorService

    def index() {
        redirect(action: "registro", params: params)
    } //index

    String makeBasicTree(params) {
        println "PARAMS  "+params
        def id = params.id
        def tipo = params.tipo
        def precios = params.precios
        def all = params.all ? params.all.toBoolean() : false
        def ignore = params.ignore ? params.ignore.toBoolean() : false
        def vae = params.vae

        def hijos = []

        switch (tipo) {
            case "grupo_manoObra":
            case "grupo_consultoria":
                hijos = DepartamentoItem.findAllBySubgrupo(SubgrupoItems.get(id), [sort: 'codigo'])
                break;
            case "grupo_material":
            case "grupo_equipo":
                hijos = SubgrupoItems.findAllByGrupo(Grupo.get(id), [sort: 'codigo'])
                break;
            case "subgrupo_manoObra":
            case "subgrupo_consultoria":
                hijos = Item.findAllByDepartamento(DepartamentoItem.get(id), [sort: 'codigo'])
                break;
            case "subgrupo_material":
            case "subgrupo_equipo":
                hijos = DepartamentoItem.findAllBySubgrupo(SubgrupoItems.get(id), [sort: 'codigo'])
                break;
            case "departamento_manoObra":
            case "departamento_consultoria":
            case "departamento_material":
            case "departamento_equipo":
                hijos = Item.findAllByDepartamento(DepartamentoItem.get(id), [sort: 'codigo'])
                break;
            case "item_manoObra":
            case "item_consultoria":
            case "item_material":
            case "item_equipo":
                def tipoLista = Item.get(id).tipoLista
                if (precios) {
                    if (ignore) {
                        hijos = ["Todos"]
                    } else {
                        hijos = []
                        if (tipoLista) {
                            hijos = Lugar.findAllByTipoLista(tipoLista)
                        }
                    }
                } else if(vae){
                    hijos = VaeItems.findAllByItem(Item.get(params.id),[max:1])
                }
                break;
        }

        String tree = "", clase = "", rel = "", extra = ""

        tree += "<ul>"
        hijos.each { hijo ->
            def hijosH, desc, liId
//            println "hijo ... "+tipo
            switch (tipo) {
                case "grupo_manoObra":
                    hijosH = Item.findAllByDepartamento(hijo, [sort: 'codigo'])
                    desc = hijo.codigo.toString().padLeft(3, '0') + " " + hijo.descripcion
                    def parts = tipo.split("_")
                    rel = "departamento_" + parts[1]
                    liId = "dp" + "_" + hijo.id
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
                    def tipoLista = hijo.tipoLista
                    if (precios) {
                        if (ignore) {
                            hijosH = ["Todos"]
                        } else {
                            if (tipoLista) {
                                hijosH = Lugar.findAllByTipoLista(tipoLista)
                            }
                        }
                    } else if(vae){
                        hijosH = VaeItems.findAllByItem(hijo,[max:1])
                    }
                    desc = hijo.codigo + " " + hijo.nombre
                    def parts = tipo.split("_")
                    rel = "item_" + parts[1]
                    liId = "it" + "_" + hijo.id
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
                    } else if(vae){
                        hijosH = VaeItems.findAllByItem(hijo,[max:1])
                    }
                    desc = hijo.codigo + " " + hijo.nombre
                    def parts = tipo.split("_")
                    rel = "item_" + parts[1]
                    liId = "it" + "_" + hijo.id
                    break;
                case "item_manoObra":
                    hijosH = []
                    if (precios) {
                        hijosH = []
                        if (ignore) {
                            desc = "mo4  " + "Todos los lugares"
                            rel = "lugar_all"
                            liId = "lg_" + id + "_all"
                        } else {
                            if (all) {
                                desc = hijo.descripcion + " (" + hijo.tipo + ")"
                            } else {
                                desc = hijo.descripcion
                            }
                            rel = "lugar"
                            liId = "lg_" + id + "_" + hijo.id

                            def obras = Obra.countByLugar(hijo)
                            extra = "data-obras='${obras}'"
                        }
                    } else if(vae && hijo){
                        hijosH = []
                        desc = "VAE"
                        rel = "vae"
                        liId = "vae_"+id+"_"+hijo.id
                    }
                    break;
                case "item_material":
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
                            rel = "lugar"
                            liId = "lg_" + id + "_" + hijo.id

                            def obras = Obra.countByLugar(hijo)
                            extra = "data-obras='${obras}'"
                        }
                    }  else if(vae){
                        hijosH = []
                        desc = "VAE"
                        rel = "vae"
                        liId = "vae_"+id+"_"+hijo.id
                    }
                    break;
            }

            if (!hijosH) {
                hijosH = []
            }
            clase = (hijosH?.size() > 0) ? "jstree-closed hasChildren" : ""

            tree += "<li id='" + liId + "' class='" + clase + "' rel='" + rel + "' " + extra + ">"
            tree += "<a href='#' class='label_arbol'>" + desc + "</a>"
            tree += "</li>"
        }
        tree += "</ul>"
        return tree
    }

    String makeBasicTree_bck(params) {
//        println "PARAMS: "+params
        def id = params.id
        def tipo = params.tipo
        def precios = params.precios
        def all = params.all ? params.all.toBoolean() : false
        def ignore = params.ignore ? params.ignore.toBoolean() : false
        def vae = params.vae

//        println "all:" + all + "     ignore:" + ignore
//        println id
//        println tipo

        def hijos = []

        switch (tipo) {
            case "grupo_manoObra":
            case "grupo_consultoria":
//                hijos = SubgrupoItems.findAllByGrupo(Grupo.get(id), [sort: 'codigo'])[0].id
                hijos = DepartamentoItem.findAllBySubgrupo(SubgrupoItems.get(id), [sort: 'codigo'])
                break;
//            case "grupo_consultoria":
//                hijos = SubgrupoItems.findAllByGrupo(Grupo.get(id), [sort: 'codigo'])
//                if (hijos.size() == 2) {
//                    hijos = hijos[1].id
//                }
//                hijos = DepartamentoItem.findAllBySubgrutipoLispo(SubgrupoItems.get(hijos), [sort: 'codigo'])
//                break;
            case "grupo_material":
            case "grupo_equipo":
                hijos = SubgrupoItems.findAllByGrupo(Grupo.get(id), [sort: 'codigo'])
                break;
            case "subgrupo_manoObra":
            case "subgrupo_consultoria":
                hijos = Item.findAllByDepartamento(DepartamentoItem.get(id), [sort: 'codigo'])
//                println hijos.nombre
                break;
            case "subgrupo_material":
            case "subgrupo_equipo":
                hijos = DepartamentoItem.findAllBySubgrupo(SubgrupoItems.get(id), [sort: 'codigo'])
                break;
            case "departamento_manoObra":
            case "departamento_consultoria":
            case "departamento_material":
            case "departamento_equipo":
                hijos = Item.findAllByDepartamento(DepartamentoItem.get(id), [sort: 'codigo'])
                break;
            case "item_manoObra":
            case "item_consultoria":
            case "item_material":
            case "item_equipo":
                println "ITEMS vae "+vae+"   params="+params
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
                } else if(vae){
                    hijos = [VaeItems.findByItem(Item.get(params.id))]
                }
                break;
        }

        String tree = "", clase = "", rel = "", extra = ""

        tree += "<ul>"
        println "hijos:" + hijos
        hijos.each { hijo ->
            def hijosH, desc, liId
            switch (tipo) {
                case "grupo_manoObra":
//                    println("entro grupo")
                    hijosH = Item.findAllByDepartamento(hijo, [sort: 'codigo'])
                    desc = hijo.codigo.toString().padLeft(3, '0') + " " + hijo.descripcion
                    def parts = tipo.split("_")
                    rel = "departamento_" + parts[1]
                    liId = "dp" + "_" + hijo.id
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
//                    println("entro sub")

//                    hijosH = []
//
////                    hijosH = Item.findAllByDepartamento(hijo,[sort: 'codigo'])
//
//                    def tipoLista = hijo.tipoLista
//                    if (precios) {
//                        if (ignore) {
//                            hijosH = ["Todos"]
//                        } else {
//                            if (tipoLista) {
//                                hijosH = Lugar.findAllByTipoLista(tipoLista)
//                            }
//                        }
//                    }
//                    desc = "2 " + hijo.codigo + " " + hijo.nombre
//
//                    def parts = tipo.split("_")
//                    rel = "item_" + parts[1]
//                    liId = "it" + "_" + hijo.id
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
                    //                    println("entro sub")

                    hijosH = []

//                    hijosH = Item.findAllByDepartamento(hijo,[sort: 'codigo'])

                    def tipoLista = hijo.tipoLista
                    if (precios) {
                        if (ignore) {
                            hijosH = ["Todos"]
                        } else {
                            if (tipoLista) {
                                hijosH = Lugar.findAllByTipoLista(tipoLista)
                            }
                        }
                    } else if(vae){
                        hijosH = [VaeItems.findByItem(hijo)]
                    }


                    desc = hijo.codigo + " " + hijo.nombre

                    def parts = tipo.split("_")
                    rel = "item_" + parts[1]
                    liId = "it" + "_" + hijo.id
//                    hijosH = []
//                    if (precios) {
//                        hijosH = []
//                        if (ignore) {
//                            desc = "Todos los lugares"
//                            rel = "lugar_all"
//                            liId = "lg_" + id + "_all"
//                        } else {
//
////                            println("entro")
//
//                            if (all) {
//                                desc = hijo.nombre + " (" + hijo.tipo + ")"
//                            } else {
//                                desc = hijo.nombre
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
                    } else if(vae){
                        hijosH = VaeItems.findAllByItem(hijo)
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
                    if(vae && hijo){
                        hijosH = []
                        desc = "VAE"
                        rel = "vae"
                        liId = "vae_"+id+"_"+hijo.id
                    }
                    break;
                case "item_material":
                case "item_equipo":
                    println "AQUI hijo="+hijo
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
                    }  else if(vae){
                        hijosH = []
                        desc = "VAE"
                        rel = "vae"
                        liId = "vae_"+id+"_"+hijo.id
                    }
                    break;
            }

            if (!hijosH) {
                hijosH = []
            }
            println "hijosH " + hijosH
            clase = (hijosH?.size() > 0) ? "jstree-closed hasChildren" : ""

            tree += "<li id='" + liId + "' class='" + clase + "' rel='" + rel + "' " + extra + ">"
            tree += "<a href='#' class='label_arbol'>" + desc + "</a>"
            tree += "</li>"
        }
        tree += "</ul>"
        return tree
    }

    def loadMO() {
        def hijos = SubgrupoItems.findAllByGrupo(Grupo.get(2), [sort: 'codigo'])
        def html = ""
        def open = ""
        hijos.eachWithIndex { h, i ->
            def hijosH = DepartamentoItem.findAllBySubgrupo(h, [sort: 'codigo'])
            def cl = ""
            if (hijosH.size() > 0) {
                cl = "hasChildren jstree-closed"
                open = "manoObra_${h.id}"
            }
            html += "<li id='manoObra_${h.id}' class='root ${cl}' rel='grupo_manoObra'>"
            html += "<a href='#' class='label_arbol'>"
            html += h.descripcion
            html += "</a>"
            html += "</li>"
        }
        render html + "*" + open
    }

    def loadTreePart() {
        println "loadTreePart ----"
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
//                println "codigo anterior: " + cod
                cod = "" + item.departamento.subgrupo.codigo.toString().padLeft(3, '0') + "." + item.departamento.codigo.toString().padLeft(3, '0') + "." + codItem.toString().padLeft(3, '0')
//                println "codigo nuevo: " + cod

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
        def sel = g.select(name: "lugar", from: lugares, optionKey: "id", optionValue: {
            it.descripcion + ' (' + it.tipo + ')'
        })
        render sel
    }

    def reportePreciosUI() {
        def lugares = Lugar.list()
        def grupo = Grupo.get(params.grupo)
        return [lugares: lugares, grupo: grupo]
    }

    def precios() {
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
        if (params.codigo) {
            params.codigo = params.codigo.toString().toUpperCase()
        }
        if (params.descripcion) {
            params.descripcion = params.descripcion.toString().toUpperCase()
        }
        if (params.id) {
            subgrupo = SubgrupoItems.get(params.id)
            accion = "edit"
        }
        subgrupo.properties = params
        if (subgrupo.save(flush: true)) {
            render "OK_" + accion + "_" + subgrupo.id + "_" + subgrupo.codigo + " " + subgrupo.descripcion
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
            println "mantenimiento items controller l 524: " + e
            render "NO"
        }
    }

    def showDp_ajax() {
        def departamentoItemInstance = DepartamentoItem.get(params.id)
        return [departamentoItemInstance: departamentoItemInstance]
    }

    def formDp_ajax() {
        def mos = SubgrupoItems.findAllByGrupo(Grupo.get(2), [sort: 'codigo']).id

        def subgrupo = SubgrupoItems.get(params.subgrupo)
        def departamentoItemInstance = new DepartamentoItem()
        if (params.id) {
            departamentoItemInstance = DepartamentoItem.get(params.id)
        }
        return [subgrupo: subgrupo, departamentoItemInstance: departamentoItemInstance, mos: mos]
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
        if (params.codigo) {
            params.codigo = params.codigo.toString().toUpperCase()
        }
        if (params.descripcion) {
            params.descripcion = params.descripcion.toString().toUpperCase()
        }
        if (params.id) {
//            println "EDIT!!!!"
            departamento = DepartamentoItem.get(params.id)
//            println "\t\t" + departamento.codigo
            accion = "edit"
        }
        departamento.properties = params
        if (departamento.save(flush: true)) {
            render "OK_" + accion + "_" + departamento.id + "_" + departamento.subgrupo.codigo + "." + departamento.codigo + " " + departamento.descripcion
        } else {
            println "mantenimiento items controller l 617: " + departamento.errors
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
            println "mantenimiento items controller l 630: " + e
            render "NO"
        }
    }

    def showIt_ajax() {
//        println "showIt_ajax" + params
        def itemInstance = Item.get(params.id)
        return [itemInstance: itemInstance]
    }

    def formIt_ajax() {
        def departamento = DepartamentoItem.get(params.departamento)
        def itemInstance = new Item()
        if (params.id) {
            itemInstance = Item.get(params.id)
        }

        def campos = ["numero": ["Código", "string"], "descripcion": ["Descripción", "string"]]

        return [departamento: departamento, itemInstance: itemInstance, grupo: params.grupo, campos: campos]
    }

    def buscaCpac() {
        println("params Cpac" + params)
        def listaTitulos = ["Código", "Descripción"]
        def listaCampos = ["numero", "descripcion"]
        def funciones = [null, null]
        def url = g.createLink(action: "buscaCpac", controller: "pac")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-ccp").modal("hide");'
        funcionJs += '$("#item_cpac").val($(this).attr("regId"));$("#item_codigo").val($(this).attr("prop_numero"));$("#item_codigo").attr("title",$(this).attr("prop_descripcion"))'
        funcionJs += '}'
        def numRegistros = 20
        def extras = " and movimiento=1"
        if (!params.reporte) {
            if(params.excel){
//                println("entro")
                session.dominio = CodigoComprasPublicas
                session.funciones = funciones
                def anchos = [15, 50, 70, 20, 20, 20, 20]
                /*anchos para el set column view en excel (no son porcentajes)*/
                redirect(controller: "reportes", action: "reporteBuscadorExcel", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "CodigoComprasPublicas", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "CodigoComprasPublicas", anchos: anchos, extras: extras, landscape: true])

            }else{
                def lista = buscadorService.buscar(CodigoComprasPublicas, "CodigoComprasPublicas", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
                lista.pop()
                render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
            }
        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = CodigoComprasPublicas
            session.funciones = funciones
            def anchos = [20, 80] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "CodigoComprasPublicas", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Código compras publcias", anchos: anchos, extras: extras, landscape: false])
        }
    }


    def checkCdIt_ajax() {
        def dep = DepartamentoItem.get(params.dep)
        if (dep.subgrupo.grupo.id != 2)
            params.codigo = dep.subgrupo.codigo.toString().padLeft(3, '0') + "." + dep.codigo.toString().padLeft(3, '0') + "." + params.codigo
        else
            params.codigo = dep.codigo.toString().padLeft(3, '0') + "." + params.codigo
        //println params
        //println dep.subgrupo.grupo.id
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

    def copiarOferentes() {
        def item = Item.get(params.id)
        def res=null
        res = oferentesService.exportDominio(janus.Item, "itemjnid", item, null, "ofrt__id",null, "ofrt__id","select * from item where itemcdgo='${item.codigo}'")

//        render "NO_Ha ocurrido un error"
        if(res)
            render "OK"
        else
            render "NO_Ha ocurrido un error"
    }

    def saveIt_ajax() {
        println 'SAVE ITEM: ' + params
        def dep = DepartamentoItem.get(params.departamento.id)
        params.tipoItem = TipoItem.findByCodigo("I")
        params.fechaModificacion = new Date()
        params.nombre = params.nombre.toString().toUpperCase()
        params.campo = params.campo.toString().toUpperCase()
        params.observaciones = params.observaciones.toString().toUpperCase()
        params.codigo = params.codigo.toString().toUpperCase()
        if (!params.id) {
            if (!params.codigo.contains(".")) {
                if (dep.subgrupo.grupoId == 2) {
//                println "?"
                    params.codigo = dep.codigo.toString().padLeft(3, '0') + "." + params.codigo
//                println params.codigo
                } else {
                    params.codigo = dep.subgrupo.codigo.toString().padLeft(3, '0') + "." + dep.codigo.toString().padLeft(3, '0') + "." + params.codigo
                }
            }
        } else {
            params.remove("codigo")
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
            render "OK_" + accion + "_" + item.id + "_" + item.codigo + " " + item.nombre
        } else {
            println "mantenimiento items controller l 784: " + item.errors
            def errores = g.renderErrors(bean: item)
            render "NO_" + errores
        }
    }

    def deleteIt_ajax() {
        def item = Item.get(params.id)
        try {
            item.delete(flush: true)
            render 'OK'
        }
        catch (Exception e) {
            println "Error: mantenimiento items: " + e
            render "NO_existen datos dependientes"
        }
    }

    def formPrecio_ajax() {
        def item = Item.get(params.item)
        def lugar = null
//        println "formPrecio_ajax" + params
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
                println "mantenimiento items controller l 846: " + precioRubrosItemsInstance.errors
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
//                            println "OK"
                        } else {
                            println "mantenimiento items controller l 873: " + precioRubrosItemsInstance.errors
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
                println "mantenimiento items controller l 903: " + e
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

//            println parts

            def rubroId = parts[0]
            def nuevoPrecio = parts[1]

            def rubroPrecioInstance = PrecioRubrosItems.get(rubroId);
            rubroPrecioInstance.precioUnitario = nuevoPrecio.toDouble();
//            println rubroPrecioInstance.precioUnitario
            if (!rubroPrecioInstance.save(flush: true)) {
                println "mantenimiento items controller l 928: " + "error " + parts
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
//        def sbuAct = ValoresAnuales.findByAnio(anio).sueldoBasicoUnificado
//        def sbuAnt = ValoresAnuales.findByAnio(anio - 1).sueldoBasicoUnificado
//
//        def delta = sbuAct / sbuAnt
//        def nuevoCosto = precioAnt * delta

//        println precioAnt + " " + anio + " " + sbuAct + " " + sbuAnt + " " + delta + " " + nuevoCosto

        def u = ValoresAnuales.findByAnio(anio).sueldoBasicoUnificado
        def b = precioAnt

        def ap = b * 12 * 0.1215
        ap = new DecimalFormat("#.##").format(ap).toDouble()
        def ta = 14 * b + u + ap
        def jr = ta / 235

        def nuevoCosto = jr / 8
        nuevoCosto = new DecimalFormat("#.##").format(nuevoCosto).toDouble()

        return nuevoCosto
    }

    def calcPrecio(params) {
//        println ">>" + params
//        println params.fecha
//        println params.fecha.class

        //TODO: WTF la fecha llega como Date y no como String??????
        def lugar = []
        def precios = []
        def precioRef = false
        def fecha = params.fecha

        if (params.fecha == "all") {
            params.todasLasFechas = "true"
        } else {
            params.todasLasFechas = "false"
            params.fecha = new Date()/*.parse("dd-MM-yyyy", fecha)*/
        }
        if (params.todasLasFechas == "true") {
            fecha = null
        }

        if (params.lugarId == "all") {
//            lugar = Lugar.list([sort: "descripcion"])
            def item = Item.get(params.itemId)
            def tipoLista = item.tipoLista
            lugar = Lugar.findAllByTipoLista(tipoLista, [sort: "descripcion"])
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
        def precioActual = precios.findAll {
            it.fecha >= new Date().parse("dd-MM-yyyy", "01-01-${anio}") && it.fecha <= new Date().parse("dd-MM-yyyy", "31-12-${anio}")
        }
        precioActual = precioActual.sort { it.fecha }
        if (precioActual) {
            def newest = precioActual[precioActual.size() - 1]
//            println "Precio ${anio} al " + newest.fecha + " " + newest.precioUnitario + ": se muestra este?"
            precioRef = newest.precioUnitario
        } else {
//            println "no hay precio de este anio (${anio})"
            def precioAnterior = precios.findAll {
                it.fecha >= new Date().parse("dd-MM-yyyy", "01-01-${anioRef}") && it.fecha <= new Date().parse("dd-MM-yyyy", "31-12-${anioRef}")
            }
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
//        println "showLg_ajax... params: $params"
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

//        println("-->>" + item + " id:" + item.id)
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
            lugarNombre = l.descripcion + " (" + (l.tipoLista ? l.tipoLista?.descripcion : 'sin tipo') + ")"
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
        def codigos = []

        def sql

        sql = "select lgarcdgo from lgar "
        sql += "order by lgarcdgo"

        def cn = dbConnectionService.getConnection()

        cn.eachRow(sql.toString()) {row->
            codigos += row[0]
        }

//        println(sql);
//        println(codigos)

        def ultimo = codigos.last()


        return [lugarInstance: lugarInstance, all: params.all, tipo: tipo, ultimo: ultimo]
    }


    def formLge_ajax() {


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
            println "mantenimiento items controller l 1158: " + lugar.errors
            def errores = g.renderErrors(bean: lugar)
            render "NO_" + errores
        }
    }

    def deleteLg_ajax() {
//        println "DELETE LUGAR "
//        println params
        def lugar = Lugar.get(params.id)

        def seUsa = Obra.countByListaPeso1(lugar)
        seUsa += Obra.countByListaVolumen0(lugar)
        seUsa += Obra.countByListaVolumen1(lugar)
        seUsa += Obra.countByListaVolumen2(lugar)
        seUsa += Obra.countByListaManoObra(lugar)

//        println "esta lis si se usa... $seUsa"
        if (seUsa > 0) {
//            render "NO_No esposible borrar la lista, ya está utilizada en Obras"
            render "NO_No esposible borrar la lista, ya está utilizada en Obras"
        } else {

            def precios = PrecioRubrosItems.findAllByLugar(lugar)
            def cant = 0
            precios.each { p ->
                try {
                    p.delete(flush: true)
//                println "p deleted " + p.id
                    cant++
                } catch (DataIntegrityViolationException e) {
                    println "mantenimiento items controller l 1177: " + e
                    println "\tp not deleted " + p.id
                }
            }

            try {
                lugar.delete(flush: true)
                render "OK"
            } catch (DataIntegrityViolationException e) {
                println "mantenimiento items controller l 1186: " + e
                render "NO"
            }
        }
    }

    def vae() {
        //<!--grpo--><!--sbgr -> Grupo--><!--dprt -> Subgrupo--><!--item-->
        //materiales = 1
        //mano de obra = 2
        //equipo = 3
    }

    def showVa_ajax() {
        def parts = params.id.split("_")
        def idMaterial = parts[0]
        def idVae=parts[1]
        def item = Item.get(parts[0])
        def vaeItems = VaeItems.findAllByItem(item, [sort: 'fecha'])
        return [params:params, item:item, vaeItems: vaeItems]
    }

    def formVa_ajax() {
        println " vae: " + params
        def vaeInstance = new VaeItems()
        if (params.fechaVae)
            vaeInstance.fecha = new Date().parse("dd-MM-yyyy", params.fechaVae)
        println vaeInstance.fecha
        def itemInstance = Item.get(params.item)
        if (params.id) {
            vaeInstance = VaeItems.get(params.id)
        }

        return [vaeInstance: vaeInstance, itemInstance: itemInstance]

    }

    def saveVa_ajax() {
        println "saveVa_ajax" +  params
        def accion = "create"
        def vaeItems = new VaeItems()
        if (params.fecha)
            params.fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        params.fechaIngreso = new Date()
        vaeItems.properties = params
        if (vaeItems.save(flush: true)) {
            println "OK_"+ accion + "_" + vaeItems.id + "_" + vaeItems.porcentaje
            render "OK"
        } else {
//            println "Vae items: " + vaeItems.errors
            def errores = g.renderErrors(bean: vaeItems)
            render "NO_" + errores
        }
    }

    def actualizarVae_ajax() {
        if (params.item instanceof java.lang.String) {
            params.item = [params.item]
        }

        def oks = "", nos = ""

        params.item.each {
            def parts = it.split("_")

//            println "actualiza vae, parts:" + parts

            def id_itva = parts[0]
            def nuevoVae = parts[1]

            def vaeItems = VaeItems.get(id_itva);
            vaeItems.porcentaje = nuevoVae.toDouble();
//            println "nuevo vae: " + vaeItems.porcentaje

            if (!vaeItems.save(flush: true)) {
                println "mantenimiento items controller l 928: " + "error " + parts
                if (nos != "") {
                    nos += ","
                }
                nos += "#" + id_itva
            } else {
                if (oks != "") {
                    oks += ","
                }
                oks += "#" + id_itva
            }

        }
        render oks + "_" + nos
    }

    def deleteVae_ajax() {
//        println "delete vae..."  + params
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
                def vaeItems = VaeItems.get(params.id);
                vaeItems.delete(flush: true)
                render "OK"
            }
            catch (DataIntegrityViolationException e) {
                println "mantenimiento items controller l 903: " + e
                render "No se pudo eliminar el vae."
            }
        }
    }

    def itemsUso () {

    }

    def tablaItemsUso_ajax() {

        def sql = "select * from item_uso()"
        def cn = dbConnectionService.getConnection()
        def res = cn.rows(sql.toString())

        return[items: res]
    }


    def borrarItem() {
        def cn = dbConnectionService.getConnection()
        def sql1 = "delete from ares where item__id = "
        def sql2 = "delete from itva where item__id = "
        def sql3 = "delete from rbpc where item__id = "
        def sql4 = "delete from item where item__id = "
        def sql = ""
        def res = true
        println "borrarItem: $params"
        //item=145629_0.12601_true&item=478_0.11000_true&item=650_0.29000_false
        //      idRubroPrecio_precio_registrado
        //          0           1       2
        if (params.item instanceof java.lang.String) {
            params.item = [params.item]
        }
        def oks = "", nos = ""
        params.item.each {it ->
//            println ">> ${it.toInteger()}"
            try {
                cn.execute("begin")
                cn.execute("$sql1 ${it.toInteger()}".toString())
                cn.execute("$sql2 ${it.toInteger()}".toString())
                cn.execute("$sql3 ${it.toInteger()}".toString())
                cn.execute("$sql4 ${it.toInteger()}".toString())
                println "borrado id: $it"
                flash.clase = "alert-success"
                flash.message = "Se ha borrado el item con éxito"
//                println "$sql1 ${it.toInteger()};"
//                println "$sql2 ${it.toInteger()};"
//                println "$sql3 ${it.toInteger()};"
//                println "$sql4 ${it.toInteger()};"
//                cn.execute("rollback")
                cn.execute("commit")
            } catch (e) {
                println "error: $e"
                flash.clase = "alert-error"
                flash.message = "No se puede borrar el item"
            }
        }
        render "ok_"
    }


}
