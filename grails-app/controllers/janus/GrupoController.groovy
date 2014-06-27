package janus

import org.springframework.dao.DataIntegrityViolationException

class GrupoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "arbol", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        [grupoInstanceList: Grupo.findAll("from Grupo  where codigo not in ('1', '2', '3')"), grupoInstanceTotal: Grupo.count(), params: params]
    } //list

    def arbol() {
        def aux = Parametros.get(1)
        def volquetes = []
        def volquetes2 = []
        def choferes = []
        def grupoTransporte = DepartamentoItem.findAllByTransporteIsNotNull()
        grupoTransporte.each {
            if (it.transporte.codigo == "H") {
                choferes = Item.findAllByDepartamento(it)
            }
            if (it.transporte.codigo == "T") {
                volquetes = Item.findAllByDepartamento(it)
            }
            volquetes2 += volquetes
        }
        return [volquetes2: volquetes2, choferes: choferes, aux: aux]
    }

    def showRb_ajax() {
        def rubro = Item.get(params.id)
        def items = Rubro.findAllByRubro(rubro)
        items.sort { it.item.codigo }
        return [rubro: rubro, items: items]
    }

    def showDp_ajax() {
        def departamentoItemInstance = DepartamentoItem.get(params.id)
        return [departamentoItemInstance: departamentoItemInstance]
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

    def formSg_gr_ajax() {
        def grupo = Grupo.get(params.grupo)
        def subgrupoItemsInstance = new SubgrupoItems()
        if (params.id) {
            subgrupoItemsInstance = SubgrupoItems.get(params.id)
        }
        return [grupo: grupo, subgrupoItemsInstance: subgrupoItemsInstance]
    }


    def formGr_ajax() {

        println(">>>>" + params)
        def grupo = Grupo.get(params.id)
//        def direcciones = Dir
        def subgrupoItemsInstance = new SubgrupoItems()
        if (params.id) {
            subgrupoItemsInstance = SubgrupoItems.get(params.id)
        }
        return [grupo: grupo, subgrupoItemsInstance: subgrupoItemsInstance]
    }


    def checkGr_ajax() {
        if (params.id) {
            def grupo = Grupo.get(params.id)
            if (params.descripcion == grupo.descripcion) {
                render true
            } else {
                def grupos = Grupo.findAllByDescripcion(params.descripcion)
                if (grupos.size() == 0) {
                    render true
                } else {
                    render false
                }
            }
        } else {
            def grupos = Grupo.findAllByDescripcion(params.descripcion)
            if (grupos.size() == 0) {
                render true
            } else {
                render false
            }
        }
    }


    def saveGr_ajax() {
        println("params-->>" + params)

        def accion = "create"
        def grupo = new Grupo()
        if (params.codigo) {
            params.codigo = params.codigo.toString().toUpperCase()
        }
        if (params.descripcion) {
            params.descripcion = params.descripcion.toString().toUpperCase()
        }
        if (params.id) {
            grupo = Grupo.get(params.id)
            accion = "edit"
        }
        grupo.properties = params
        if (grupo.save(flush: true)) {
            render "OK_" + accion + "_" + grupo.id + "_" + grupo.descripcion
        } else {
            def errores = g.renderErrors(bean: grupo)
            render "NO_" + errores
        }
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
            println "grupo controller l 172: " + e
            render "NO"
        }
    }


    def deleteGr_ajax() {
        def grupo = Grupo.get(params.id)
        try {
            grupo.delete(flush: true)
            render "OK"
        }
        catch (DataIntegrityViolationException e) {
            println "grupo controller l 185: " + e
            render "NO"
        }
    }




    def formDp_ajax() {
        def subgrupo = SubgrupoItems.get(params.subgrupo)
        def departamentoItemInstance = new DepartamentoItem()
        if (params.id) {
            departamentoItemInstance = DepartamentoItem.get(params.id)
        }
        return [subgrupo: subgrupo, departamentoItemInstance: departamentoItemInstance]
    }

    def formDp_gr_ajax() {
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
            render "OK_" + accion + "_" + departamento.id + "_" + departamento.descripcion
        } else {
            println "grupo controller l 275: " + departamento.errors
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
            println "grupo controller l 288: " + e
            render "NO"
        }
    }


    String makeBasicTree(params) {

        def id = params.id.toLong()
        def tipo = params.tipo
//        println(params)

//        println "all:" + all + "     ignore:" + ignore

//        println id
//        println tipo

        def hijos = []

        switch (tipo) {

            case "root":
                hijos = Grupo.findAll("from Grupo where id>3")
//                println "\nhijos root: "+hijos+"\n"
                break;

            case "grupo":
                hijos = SubgrupoItems.findAllByGrupo(Grupo.get(id), [sort: 'codigo'])
//                println "grupo" + hijos.descripcion
                break;
            case "subgrupo":
                hijos = DepartamentoItem.findAllBySubgrupo(SubgrupoItems.get(id), [sort: 'codigo'])
                break;
            case "departamento":
                hijos = Item.findAllByDepartamento(DepartamentoItem.get(id), [sort: 'nombre'])
                break;
        }

        String tree = "", clase = "", rel = "", extra = ""

        tree += "<ul>"
        hijos.each { hijo ->
            def hijosH, desc, liId
            switch (tipo) {

                case "root":
//                    hijosH = SubgrupoItems.findAllByGrupo(hijo)
                    hijosH = SubgrupoItems.findAllByGrupo(hijo)
                    desc = hijo.descripcion
                    rel = "grupo"
                    liId = "gr" + "_" + hijo.id

                    break;
                case "grupo":
                    hijosH = DepartamentoItem.findAllBySubgrupo(hijo, [sort: 'codigo'])
                    desc = hijo.codigo.toString().padLeft(3, '0') + " " + hijo.descripcion
                    rel = "subgrupo"
                    liId = "sg" + "_" + hijo.id
                    break;

                case "subgrupo":
                    hijosH = Item.findAllByDepartamento(hijo)
                    desc = hijo.descripcion
                    rel = "departamento"
                    liId = "dp" + "_" + hijo.id
                    break;
                case "departamento":
//                    println("entro sub")
                    hijosH = []
                    desc = hijo.nombre
                    rel = "rubro"
                    liId = "rb" + "_" + hijo.id
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
//            def id = params.tipo
//            def find = Item.withCriteria {
//                departamento {
//                    subgrupo {
//                        grupo {
//                            eq("id", id.toLong())
//                        }
//                    }
//                }
//                ilike("nombre", "%" + search + "%")
//            }
//            def departamentos = [], subgrupos = [], grupos = []
//            find.each { item ->
//                if (!departamentos.contains(item.departamento))
//                    departamentos.add(item.departamento)
//                if (!subgrupos.contains(item.departamento.subgrupo))
//                    subgrupos.add(item.departamento.subgrupo)
//                if (!grupos.contains(item.departamento.subgrupo.grupo))
//                    grupos.add(item.departamento.subgrupo.grupo)
//            }
//
//            def ids = "["
//
//            if (find.size() > 0) {
//                ids += "\"#materiales_1\","
//
//                grupos.each { gr ->
//                    ids += "\"#gr_" + gr.id + "\","
//                }
//                subgrupos.each { sg ->
//                    ids += "\"#sg_" + sg.id + "\","
//                }
//                departamentos.each { dp ->
//                    ids += "\"#dp_" + dp.id + "\","
//                }
//                ids = ids[0..-2]
//            }
//            ids += "]"

            def grupos = Grupo.withCriteria {
                or {
                    ilike("codigo", "%" + search + "%")
                    ilike("descripcion", "%" + search + "%")
                }
            }
            def subgrupos = SubgrupoItems.withCriteria {
                or {
                    ilike("codigo", "%" + search + "%")
                    ilike("descripcion", "%" + search + "%")
                }
            }
            def departamentos = DepartamentoItem.withCriteria {
                or {
                    ilike("codigo", "%" + search + "%")
                    ilike("descripcion", "%" + search + "%")
                }
            }
            def rubros = Item.withCriteria {
                and {
                    eq("tipoItem", TipoItem.get(2))
                    or {
                        ilike("codigo", "%" + search + "%")
                        ilike("nombre", "%" + search + "%")
                    }
                }
            }



            def ids = "["
            if (grupos.size() > 0 || subgrupos.size() > 0 || departamentos.size() > 0 || rubros.size() > 0) {
                ids += "\"#root\","
            }
            rubros.each { rb ->
                def dep = rb.departamento
                if (!departamentos.contains(dep)) {
                    departamentos.add(dep)
                }
                ids += "\"#rb_" + rb.id + "\","
            }
            departamentos.each { dp ->
                def subg = dp.subgrupo
                if (!subgrupos.contains(subg)) {
                    subgrupos.add(subg)
                }
                ids += "\"#dp_" + dp.id + "\","
            }
            subgrupos.each { sg ->
                def grp = sg.grupo
                if (!grupos.contains(grp)) {
                    grupos.add(grp)
                }
                ids += "\"#sg_" + sg.id + "\","
            }
            grupos.each { gr ->
                ids += "\"#gr_" + gr.id + "\","
            }
            ids = ids[0..-2]
            ids += "]"

//            println grupos
//            println subgrupos
//            println departamentos
//            println rubros

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

        def arr = []

        def grupos = Grupo.withCriteria {
            or {
                ilike("codigo", "%" + search + "%")
                ilike("descripcion", "%" + search + "%")
            }
        }
        def subgrupos = SubgrupoItems.withCriteria {
            or {
                ilike("codigo", "%" + search + "%")
                ilike("descripcion", "%" + search + "%")
            }
        }
        def departamentos = DepartamentoItem.withCriteria {
            or {
                ilike("codigo", "%" + search + "%")
                ilike("descripcion", "%" + search + "%")
            }
        }
        def rubros = Item.withCriteria {
            and {
                eq("tipoItem", TipoItem.get(2))
                or {
                    ilike("codigo", "%" + search + "%")
                    ilike("nombre", "%" + search + "%")
                }
            }
        }
        arr += grupos.descripcion
        arr += subgrupos.descripcion
        arr += departamentos.descripcion
        arr += rubros.nombre
        arr = arr.unique()

        def json = "["
        arr.each { item ->
            if (json != "[") {
                json += ","
            }
            json += "\"" + item + "\""
        }
        json += "]"
        render json
    }

    def form_ajax() {
        def grupoInstance = new Grupo(params)
        if (params.id) {
            grupoInstance = Grupo.get(params.id)
            if (!grupoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Grupo con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [grupoInstance: grupoInstance]
    } //form_ajax

    def save() {

//        println("params " + params)

        def grupos = Grupo.list()
        def codigos = []
        def existe  = 0
        grupos.each {
            codigos += it?.codigo
        }

//        println("codigos " + codigos)

        def grupoInstance
        if (params.id) {
            grupoInstance = Grupo.get(params.id)
            if (!grupoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Grupo con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            grupoInstance.properties = params
        }//es edit
        else {
             codigos.each {
                 if(it == params.codigo){
                  existe = 1
                 }
             }

            if(existe != 1){
                grupoInstance = new Grupo(params)
            }else{
                flash.clase = "alert-error"
                flash.message = "No se pudo guardar el grupo, el código ya existe!!"
                redirect(action: 'list')
                return
            }


        } //es create
        if (!grupoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Grupo " + (grupoInstance.id ? grupoInstance.id : "") + "</h4>"

            str += "<ul>"
            grupoInstance.errors.allErrors.each { err ->
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
//            flash.message = "Se ha actualizado correctamente Grupo " + grupoInstance.id
            flash.message = "Se ha actualizado correctamente Grupo: " + grupoInstance?.descripcion
        } else {
            flash.clase = "alert-success"
//            flash.message = "Se ha creado correctamente Grupo " + grupoInstance.id
            flash.message = "Se ha creado correctamente Grupo: " + grupoInstance?.descripcion
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def grupoInstance = Grupo.get(params.id)
        if (!grupoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Grupo con id " + params.id
            redirect(action: "list")
            return
        }
        [grupoInstance: grupoInstance]
    } //show

    def delete() {
        def grupoInstance = Grupo.get(params.id)
        if (!grupoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Grupo con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            grupoInstance.delete(flush: true)
            flash.clase = "alert-success"
//            flash.message = "Se ha eliminado correctamente Grupo " + grupoInstance.id
            flash.message = "Se ha eliminado correctamente Grupo: " + grupoInstance?.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Grupo " + (grupoInstance.id ? grupoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
