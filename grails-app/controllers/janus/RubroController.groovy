package janus

import janus.apus.ArchivoEspecificacion

//import org.apache.tools.ant.types.resources.comparators.Date
import org.springframework.dao.DataIntegrityViolationException

class RubroController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def buscadorService
    def preciosService

    def index() {
        redirect(action: "rubroPrincipal", params: params)
    } //index


    def gruposPorClase() {
        def clase = Grupo.get(params.id)
        def grupos = SubgrupoItems.findAllByGrupo(clase)
        def sel = g.select(id: "selGrupo", name: "rubro.suggrupoItem.id", from: grupos, "class": "span12", optionKey: "id", optionValue: "descripcion", noSelection: ["": "--Seleccione--"])
        def js = "<script type='text/javascript'>"
        js += '$("#selGrupo").change(function () {'
        js += 'var grupo = $(this).val();'
        js += '$.ajax({'
        js += 'type    : "POST",'
        js += 'url     : "' + createLink(action: 'subgruposPorGrupo') + '",'
        js += 'data    : {'
        js += 'id : grupo'
        js += '},'
        js += 'success : function (msg) {'
        js += '$("#selSubgrupo").replaceWith(msg);'
        js += '}'
        js += '});'
        js += '});'
        js += "</script>"
        render sel + js
    }

    def subgruposPorGrupo() {
        def grupo = SubgrupoItems.get(params.id)
        def subgrupos = DepartamentoItem.findAllBySubgrupo(grupo)
        def sel = g.select(id: "selSubgrupo", name: "rubro.departamento.id", from: subgrupos, "class": "span12", optionKey: "id", optionValue: "descripcion", noSelection: ["": "--Seleccione--"])
        render sel
    }

    def ciudadesPorTipo() {
        def tipo = params.id
        def ciudades = Lugar.findAllByTipo(tipo)
        def sel = g.select(id: "ciudad", name: "item.ciudad.id", from: ciudades, "class": "span10", optionKey: "id", optionValue: "descripcion")
        render sel
    }

    def saveEspc() {
        def rubro = Item.get(params.id)
        rubro.especificaciones = params.espc
        if (rubro.save(flush: true))
            render "ok"
        else
            render "no"
    }

    def rubroPrincipal() {
//        println "rubroPrincipal params: $params"
        def rubro
        def campos = ["codigo": ["Código", "string"], "nombre": ["Descripción", "string"]]
        def grupos = []
        def volquetes = []
        def volquetes2 = []
        def choferes = []
        def aux = Parametros.get(1)
        def grupoTransporte = DepartamentoItem.findAllByTransporteIsNotNull()
        def dpto = Departamento.findAllByPermisosIlike("APU")
        def resps = Persona.findAllByDepartamentoInList(dpto)


//        println "depto "+dpto
        def dptoUser = Persona.get(session.usuario.id).departamento
        def modifica = false
        if (dpto.size()>0) {
            dpto.each {d->
                if (d.id.toInteger() == dptoUser.id.toInteger())
                    modifica = true
            }

        }

        grupoTransporte.each {
            if (it.transporte.codigo == "H")
                choferes = Item.findAllByDepartamento(it)
            if (it.transporte.codigo == "T")
                volquetes = Item.findAllByDepartamento(it)

            volquetes2 += volquetes

        }

        grupos=Grupo.findAll("from Grupo  where id>3")
        if (params.idRubro) {
            rubro = Item.get(params.idRubro)
            def items = Rubro.findAllByRubro(rubro)
            items.sort { it.item.codigo }
            resps = rubro.responsable
//            println "responsable: $resps, ${resps?.id} rubro: ${rubro.id} ${rubro.codigo} ${rubro.responsable}"
            [campos: campos, rubro: rubro, grupos: grupos, items: items, choferes: choferes, volquetes: volquetes,
             aux: aux, volquetes2: volquetes2, dpto: dpto, modifica: modifica, resps: resps]
        } else {

//            println "Nuevo .... responsable: ${resps?.id} ${resps}"

            [campos: campos, grupos: grupos, choferes: choferes, volquetes: volquetes, aux: aux,
             volquetes2: volquetes2, dpto: dpto, modifica: modifica, resps: resps]
        }
    }



    def getDatosItem() {
//        println "get datos items "+params
        def item = Item.get(params.id)
        def nombre = item.nombre
       // println "nombre antes de "+item.nombre
        nombre = nombre.replaceAll(/>/, / mayor que /)
        nombre = nombre.replaceAll(/</, / menor que /)
        nombre = nombre.replaceAll(/&gt;/, / mayor que /)
        nombre = nombre.replaceAll(/&lt;/, / menor que /)
       // println "nombre despues de "+nombre
        //println "render "+  item.id + "&" + item.codigo + "&" + nombre + "&" + item.unidad.codigo + "&" + item.rendimiento+"&"+((item.tipoLista)?item.tipoLista?.id:"0")
        render "" + item.id + "&" + item.codigo + "&" + nombre + "&" + item.unidad?.codigo?.trim() + "&" + item.rendimiento + "&" + ((item.tipoLista) ? item.tipoLista?.id : "0")+"&"+item.departamento.subgrupo.grupo.id
    }

    def addItem() {
//        println "add item " + params
        def rubro = Item.get(params.rubro)
        def item = Item.get(params.item)
        def detalle
        detalle = Rubro.findByItemAndRubro(item, rubro)
        if (!detalle)
            detalle = new Rubro()
        detalle.rubro = rubro
        detalle.item = item
        detalle.cantidad = params.cantidad.toDouble()
        if (detalle.item.codigo=~"103.001.00") {
            detalle.cantidad = 1
            detalle.rendimiento = 1
        } else {
            detalle.rendimiento = params.rendimiento.toDouble()
        }
        if (detalle.item.departamento.subgrupo.grupo.id == 2)
            detalle.cantidad = Math.ceil(detalle.cantidad)
        detalle.fecha = new Date()
        if (detalle.item.departamento.subgrupo.grupo.id == 1)
            detalle.rendimiento = 1
        if (!detalle.save(flush: true)) {
//            println "detalle " + detalle.errors
        } else {
            rubro.fechaModificacion = new Date()
            rubro.save(flush: true)
            render "" + item.departamento.subgrupo.grupo.id + ";" + detalle.id + ";" + detalle.item.id + ";" + detalle.cantidad + ";" + detalle.rendimiento + ";" + ((item.tipoLista) ? item.tipoLista?.id : "0")
        }
    }

    def buscaItem() {
        //println "busca item "+params
        def listaTitulos = ["Código", "Descripción"]
        def listaCampos = ["codigo", "nombre"]
        def funciones = [null, null]
        def url = g.createLink(action: "buscaItem", controller: "rubro")
        def funcionJs = "function(){"
        funcionJs += 'var idReg = $(this).attr("regId");'
        funcionJs += '$.ajax({type: "POST",url: "' + g.createLink(controller: 'rubro', action: 'getDatosItem') + '",'
        funcionJs += ' data: "id="+idReg,'
        funcionJs += ' success: function(msg){'
       // funcionJs += 'console.log("desc " +msg);'
        funcionJs += 'var parts = msg.split("&");'
        funcionJs += ' $("#item_id").val(parts[0]);'
        funcionJs += ' $("#item_id").attr("tipo",parts[6]);'
        funcionJs += '$("#cdgo_buscar").val(parts[1]);'
        funcionJs += 'var desc =parts[2]; '
        //funcionJs += 'console.log("desc "+desc);'
        funcionJs += 'desc =desc.replace(/>/g, " mayor "); '
        funcionJs += 'desc =desc.replace(/</g, " menor "); '
       // funcionJs += 'console.log("desc "+desc);'
        funcionJs += '$("#item_desc").val(parts[2]);'
        funcionJs += '$("#item_unidad").val(parts[3]);'
        funcionJs += '$("#item_tipoLista").val(parts[5]);'
        funcionJs += '$("#modal-rubro").modal("hide");'
        funcionJs += '}'
        funcionJs += '});'
        funcionJs += '}'
        def numRegistros = 20

        def tipo=params.tipo
        def extras = " and tipoItem = 1 and departamento in ("

        SubgrupoItems.findAllByGrupo(Grupo.get(tipo)).each {
            DepartamentoItem.findAllBySubgrupo(it).each{dp->
                extras+=dp.id+","
            }
        }
        extras+="-1)";
//        println "extras "+extras

        if (!params.reporte) {
            def lista = buscadorService.buscar(Item, "Item", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
            lista.pop()
            render(view: '../tablaBuscador', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Item
            session.funciones = funciones
            def anchos = [20, 80] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Item", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Rubros", anchos: anchos, extras: extras, landscape: true])
        }
    }

    def buscaRubro() {

        def listaTitulos = ["Código", "Descripción", "Unidad"]
        def listaCampos = ["codigo", "nombre", "unidad"]
        def funciones = [null, null]
        def url = g.createLink(action: "buscaRubro", controller: "rubro")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-rubro").modal("hide");'
        funcionJs += 'location.href="' + g.createLink(action: 'rubroPrincipal', controller: 'rubro') + '?idRubro="+$(this).attr("regId");'
        funcionJs += '}'
        def numRegistros = 20
        def extras = " and tipoItem = 2"
        if (!params.reporte) {
            if (params.excel) {
                session.dominio = Item
                session.funciones = funciones
                def anchos = [40,100,30] /*anchos para el set column view en excel (no son porcentajes)*/
                redirect(controller: "reportes", action: "reporteBuscadorExcel", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Item", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Rubros", anchos: anchos, extras: extras, landscape: true])
            } else {
                def lista = buscadorService.buscar(Item, "Item", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
                lista.pop()
                render(view: '../tablaBuscador', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
            }

        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Item
            session.funciones = funciones
            def anchos = [20, 70,10] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Item", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Rubros", anchos: anchos, extras: extras, landscape: false])
        }
    }

    def buscaRubroComp() {
        def listaTitulos = ["Código", "Descripción"]
        def listaCampos = ["codigo", "nombre"]
        def funciones = [null, null]
        def url = g.createLink(action: "buscaRubro", controller: "rubro")
        def funcionJs = "function(){"
        funcionJs += 'if($("#rubro__id").val()*1>0){ '
        funcionJs += '   if(confirm("Esta seguro?")){'
        funcionJs += '        $("#rub_select").val($(this).attr("regId"));'
        funcionJs += '        $("#copiar_dlg").dialog("open");$("#modal-rubro").modal("hide");'
        funcionJs += '    } '
        funcionJs += '}else{ '
        funcionJs += '    $.box({ '
        funcionJs += '       imageClass: "box_info",'
        funcionJs += '        text      : "Primero guarde el rubro o escoja un de la lista",'
        funcionJs += '       title     : "Alerta", '
        funcionJs += '        iconClose : false,'
        funcionJs += '       dialog    : {'
        funcionJs += '           resizable    : false,'
        funcionJs += '            draggable    : false,'
        funcionJs += '           buttons      : {'
        funcionJs += '                "Aceptar" : function () {}'
        funcionJs += '           }'
        funcionJs += '        }'
        funcionJs += '    });'
        funcionJs += '}'
        funcionJs += '}'
        def numRegistros = 20
        def extras = " and tipoItem = 2"
        if (!params.reporte) {
            def lista = buscadorService.buscar(Item, "Item", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
            lista.pop()
            render(view: '../tablaBuscador', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Item
            session.funciones = funciones
            def anchos = [20, 80] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Item", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Rubros", anchos: anchos, extras: extras, landscape: true])
        }
    }

    def copiarComposicion() {
        //println "copiar!!! " + params + "  " + request.method
        if (request.method == "POST") {
            def rubro = Item.get(params.rubro)
            def copiar = Item.get(params.copiar)
            def detalles = Rubro.findAllByRubro(copiar)

            def factor
            if (!params.factor)
                params.factor = "1"
            factor = params.factor?.toDouble()
            detalles.each {
                println ""+it.item.departamento.subgrupo.grupo.descripcion+"  "+it.item.departamento.subgrupo.grupo.codigo
                def tmp = Rubro.findByRubroAndItem(rubro, it.item)
                if (!tmp) {
//                    println "no temnp "
                    def nuevo = new Rubro()
                    nuevo.rubro = rubro
                    nuevo.item = it.item
//                    println " asd "  +it.item.nombre

                    if(it.item.departamento.subgrupo.grupo.id.toInteger()==1){
                        nuevo.cantidad = it.cantidad * factor
                    }else{
                        if (!(it.item.nombre =~ "HERRAMIENTA MENOR")) {
                            nuevo.rendimiento = it.rendimiento * factor
                            nuevo.cantidad=it.cantidad
                        }
                    }

                    nuevo.fecha = new Date()
                    if (!nuevo.save(flush: true)) {
                        println "Error: copiar composicion " + nuevo.errors
                    }
                    rubro.fecha = new Date()
                    rubro.save(flush: true)

                } else {
//                    println "else si hay "
                    if (!(it.item.nombre =~ "HERRAMIENTA MENOR")) {
//                        println "entro 2 "+factor
                        if(it.item.departamento.subgrupo.grupo.id.toInteger()==2){
                            //println "es mano de obra"
                            def maxCant = Math.max(tmp.cantidad,it.cantidad)
                            def sum = tmp.cantidad*tmp.rendimiento+(it.cantidad*factor*it.rendimiento)
                            //println "maxcant "+maxCant+" sum "+sum
                            def rend = sum/maxCant
                            tmp.cantidad = maxCant
                            tmp.rendimiento=rend
                            tmp.fecha = new Date()
                            tmp.save(flush: true)


                        }else{
                            if(it.item.departamento.subgrupo.grupo.id.toInteger()==3){
                                //println "es mano de obra"
                                def maxCant = Math.max(tmp.cantidad,it.cantidad)
                                def sum = tmp.cantidad*tmp.rendimiento+(it.cantidad*it.rendimiento)
                                //println "maxcant "+maxCant+" sum "+sum
                                def rend = sum/maxCant
                                tmp.cantidad = maxCant
                                tmp.rendimiento=rend
                                tmp.fecha = new Date()
                                tmp.save(flush: true)


                            }else{
                                tmp.cantidad = tmp.cantidad + it.cantidad * factor
                                tmp.fecha = new Date()
                                tmp.save(flush: true)
                            }

                        }


                    }
                }
            }
            render "ok"
            return

        } else {
            response.sendError(403)
        }
    }


    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [rubroInstanceList: Rubro.list(params), rubroInstanceTotal: Rubro.count(), params: params]
    } //list

    def form_ajax() {
        def rubroInstance = new Rubro(params)
        if (params.id) {
            rubroInstance = Rubro.get(params.id)
            if (!rubroInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Rubro con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [rubroInstance: rubroInstance]
    } //form_ajax

    def save() {
//        println "save rubro " + params.rubro
//        println("params " +  params)
        params.rubro.codigo = params.rubro.codigo.toUpperCase()
        params.rubro.codigoEspecificacion = params.rubro.codigoEspecificacion.toUpperCase()

        def rubro
        if (params.rubro.id) {
            rubro = Item.get(params.rubro.id)
            params.remove("rubro.fecha")
            rubro.tipoItem = TipoItem.get(2)
            rubro.fechaModificacion = new Date()
        } else {
            rubro = new Item(params)
            params.rubro.fecha = new Date()
            rubro.tipoItem = TipoItem.get(2)
        }

        if (params.rubro.registro != "R") {
            params.rubro.registro = "N"
            rubro.fechaRegistro = null
        } else {
            rubro.fechaRegistro = new Date()
        }
        if (params.responsable && params.responsable != "-1") {
            rubro.responsable = Persona.get(params.responsable)
        }

        rubro.properties = params.rubro
        rubro.tipoItem = TipoItem.get(2)
//        println "ren " + rubro.rendimiento
        if (!rubro.save(flush: true)) {
            println "error " + rubro.errors
        }else{
            if(rubro.codigoEspecificacion!="" && rubro.codigoEspecificacion){
                def rubros = Item.findByCodigoNotEqualAndCodigoEspecificacion(rubro.codigo,rubro.codigoEspecificacion,[sort:"codigo"])
//                println "mismo codigo "+rubros
                if(rubros){
//                    println "actualizando "+rubros.especificaciones+"  "+rubros.foto
                    rubro.especificaciones=rubros.especificaciones
                    rubro.save(flush: true)
                }

            }

        }

        redirect(action: 'rubroPrincipal', params: [idRubro: rubro.id])
    } //save

    def repetido = {
        // verifica codigo
//        println "Repetido:" + params
        if (!params.id) {
            def hayOtros = Item.findAllByCodigo(params.codigo?.toUpperCase()).size() > 0
//        println "repetido: " + hayOtros
            render hayOtros ? "repetido" : "ok"
        } else
            render "ok"
    }

    def show_ajax() {
        def rubroInstance = Rubro.get(params.id)
        if (!rubroInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Rubro con id " + params.id
            redirect(action: "list")
            return
        }
        [rubroInstance: rubroInstance]
    } //show

    def eliminarRubroDetalle() {
//        println "eliminarRubroDetalle "+params
        if (request.method == "POST") {
            def rubro = Rubro.get(params.id)
            try {
                rubro.delete(flush: true)
                render "Registro eliminado"
            }
            catch (DataIntegrityViolationException e) {
                render "No se pudo eliminar el rubro"
            }
        } else {
            response.sendError(403)
        }

    }


    def borrarRubro() {
//        println "borrar rubro "+params
        def rubroInstance = Item.get(params.id)
        if (!rubroInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Rubro con id " + params.id
            redirect(action: "list")
            return
        }

        def vo = VolumenesObra.findAllByItem(rubroInstance)
        def obras = Obra.findAllByChoferOrVolquete(rubroInstance, rubroInstance)
//        println "vo "+vo
//        println "obras "+obras
        def ob = [:]
        if (vo.size() + obras.size() > 0) {
            vo.each { v ->

                ob.put(v.obra.codigo, v.obra.nombre)

            }
            obras.each { o ->
                ob.put(o.codigo, o.nombre)
            }
            render "" + ob.collect { "<span class='label-azul'>" + it.key + "</span>: " + it.value }.join('<br>')
            return
        } else {
            try {
                def comp = Rubro.findAllByRubro(rubroInstance)
                comp.each {
                    it.delete(flush: true)
                }
                rubroInstance.delete(flush: true)
                PrecioRubrosItems.findAllByItem(rubroInstance).each {
                    it.delete(flush: true)
                }
                render "ok"
                return
            }
            catch (DataIntegrityViolationException e) {
                println "error del rubro " + e
                render "Error"
                return
            }
        }


    } //delete

    def getPrecios() {
//        println "get precios " + params.fecha
        def lugar = Lugar.get(params.ciudad)
//        println ".........1"
        def fecha = new Date().parse('dd-MM-yyyy', params.fecha)
//        println "frecha convertida: $fecha"
        def tipo = params.tipo
        def items = []
        def parts = params.ids.split("#")
        def listas = []
        def conLista = []
        listas = params.listas.split("#")
//        println "listas...: " + listas
        parts.each {
            if (it.size() > 0) {
                def item = Rubro.get(it).item
                if (item.tipoLista) {
                    conLista.add(item)
//                    println "con lista "+item.tipoLista
                } else {
                    items.add(item)
                }

            }

        }
        def precios = ""
//        println "items " + items + "  con lista " + conLista+"  fecha "+fecha
        if (items.size() > 0) {
            precios = preciosService.getPrecioItemsString(fecha, lugar, items)
        }
//        println "precios "+precios


        conLista.each {
//            println "tipo "+ it.tipoLista.id.toInteger()
            precios += preciosService.getPrecioItemStringListaDefinida(fecha, listas[it.tipoLista.id.toInteger() - 1], it.id)
        }

//        println "precios final !! " + precios
//        println "--------------------------------------------------------------------------"
        render precios
    }

    def getPreciosItem() {
//        println "get precios item " + params
        def lugar = Lugar.get(params.ciudad)
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        def tipo = params.tipo
        def items = []
        def parts = params.ids.split("#")
        def listas = []
        def conLista = []
        listas = params.listas.split("#")
//        println "listas " + listas
        parts.each {
            if (it.size() > 0) {
                def item = Item.get(it)
                if (item.tipoLista) {
                    conLista.add(item)
//                    println "con lista "+item.tipoLista
                } else {
                    items.add(item)
                }

            }

        }
        def precios = ""
//        println "items " + items + "  con lista " + conLista
        if (items.size() > 0) {
            precios = preciosService.getPrecioItemsString(fecha, lugar, items)
        }
//        println "precios "+precios


        conLista.each {
//            println "tipo "+ it.tipoLista.id.toInteger()
            precios += preciosService.getPrecioItemStringListaDefinida(fecha, listas[it.tipoLista.id.toInteger() - 1], it.id)
        }

//        println "precios final " + precios
//        println "--------------------------------------------------------------------------"
        render precios
    }

    def getPreciosTransporte() {
//       println "get precios fecha: "+params.fecha
        def lugar = Lugar.get(params.ciudad)
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        def tipo = params.tipo
        def items = []
        def parts = params.ids.split("#")
        parts.each {
            if (it.size() > 0)
                items.add(Item.get(it))
        }
        def precios = preciosService.getPrecioItemsString(fecha, lugar, items)
//        println "precios " + precios
        render precios
    }

    def getUnidad () {

        def item = Item.get(params.id)

        render item.unidad


    }


    def buscarRubroCodigo() {
//        println "buscar rubro "+params
        def rubro = Item.findByCodigoAndTipoItem(params.codigo?.trim(), TipoItem.get(1))
        if (rubro) {
            def nombre = rubro.nombre
            nombre = nombre.replaceAll(/>/, / mayor /)
            nombre = nombre.replaceAll(/</, / menor /)
            nombre = nombre.replaceAll(/&gt;/, / mayor /)
            nombre = nombre.replaceAll(/&lt;/, / menor /)
            render "" + rubro.id + "&&" + rubro.tipoLista?.id + "&&" + nombre + "&&" + rubro.unidad?.codigo
            return
        } else {
            render "-1"
            return
        }
    }


    def transporte() {
//        println "transporte "+params
        def idRubro = params.id
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        def listas = params.listas
        def parametros = "" + idRubro + ",'" + fecha.format("yyyy-MM-dd") + "'," + listas + "," + params.dsp0 + "," + params.dsp1 + "," + params.dsv0 + "," + params.dsv1 + "," + params.dsv2 + "," + params.chof + "," + params.volq
//        println "paramtros " +parametros
        def res = preciosService.rb_precios(parametros, "")

        def tabla = '<table class="table table-bordered table-striped table-condensed table-hover"> '
        def total = 0
        tabla += "<thead><tr><th colspan=8>TRANSPORTE</th></tr><tr><th style='width: 80px;'>CODIGO</th><th style='width:610px'>DESCRIPCION</th><th>PESO</th><th>VOL</th><th>CANTIDAD</th><th>DISTANCIA</th><th>TARIFA</th><th>C.TOTAL</th></thead><tbody>"
//        println "rends "+rendimientos

//        println "res "+res
        res.each { r ->
            if (r["grpocdgo"] == 1) {
//                println "en tabla "+r
                tabla += "<tr>"
                tabla += "<td style='width: 80px;'>" + r["itemcdgo"] + "</td>"
                tabla += "<td>" + r["itemnmbr"] + "</td>"
                if (r["tplscdgo"] =~ "P") {
                    tabla += "<td style='width: 50px;text-align: right'>" + r["itempeso"] + "</td>"
                    tabla += "<td></td>"
                }
                if (r["tplscdgo"] =~ "V") {
                    tabla += "<td></td>"
                    tabla += "<td style='width: 50px;text-align: right'>" + r["itempeso"] + "</td>"
                }

                tabla += "<td style='width: 50px;text-align: right'>" + r["rbrocntd"] + "</td>"
                tabla += "<td style='width: 50px;text-align: right'>" + r["distancia"] + "</td>"
                tabla += "<td style='width: 50px;text-align: right'>" + g.formatNumber(number: r["tarifa"], format: "##,#####0", minFractionDigits: 5, maxFractionDigits: 5, locale: "ec") + "</td>"
                tabla += "<td style='width: 50px;text-align: right'>" + g.formatNumber(number: r["parcial_t"], format: "##,#####0", minFractionDigits: 5, maxFractionDigits: 5, locale: "ec") + "</td>"
                total += r["parcial_t"]
                tabla += "</tr>"
            }
//            <g:formatNumber number="${rub.cantidad}" format="##,#####0" minFractionDigits="5" maxFractionDigits="7"  locale="ec"  />
        }
        tabla += "<tr><td><b>SUBTOTAL</b></td><td></td><td></td><td></td><td></td><td></td><td></td><td style='width: 50px;text-align: right;font-weight: bold' class='valor_total'>${g.formatNumber(number: total, format: "##,#####0", minFractionDigits: 5, maxFractionDigits: 5, locale: "ec")}</td>"
        tabla += "</tbody></table>"

        render(tabla)
//
//        pg: select * from rb_precios(293, 4, '1-feb-2008', 50, 70, 0.1015477897561282, 0.1710401760227313);
    }

    def showFoto() {
        // para habilitar el ares, quitar los comentarios sangrados y cambiar el return //

        def rubro = Item.get(params.id)
        def tipo = params.tipo
        def ares = ArchivoEspecificacion.findByCodigo(rubro.codigoEspecificacion)
//        println("rubro " + rubro)
//        println("ares111 " + ares?.id)
        def ret

        if(ares){
            ret = ares?.item
        } else {
            ret = rubro
        }

        def filePath
        def titulo
        switch (tipo) {
            case "il":
                titulo = "Ilustración"
                filePath = rubro.foto
                break;
            case "dt":
                titulo = "Especificaciones"
                filePath = ares?.ruta
//                filePath = rubro.especificaciones
                break;
        }

        def ext = ""

        if (filePath) {
            ext = filePath.split("\\.")
            ext = ext[ext.size() - 1]
        }
        return [rubro: rubro, ext: ext, tipo: tipo, titulo: titulo, filePath: filePath, ares: ares?.id]
    }

    def downloadFile() {
        def rubro = Item.get(params.id)

        def tipo = params.tipo
        def filePath

        switch (tipo) {
            case "il":
                filePath = rubro.foto
                break;
            case "dt":
                filePath = rubro.especificaciones
                break;
        }

        def ext = filePath.split("\\.")
        ext = ext[ext.size() - 1]
        def folder = "rubros"
        def path = servletContext.getRealPath("/") + folder + File.separatorChar + filePath
        println "path "+path
        def file = new File(path)
        if(file.exists()){
            def b = file.getBytes()
            response.setContentType(ext == 'pdf' ? "application/pdf" : "image/" + ext)
            response.setHeader("Content-disposition", "attachment; filename=" + filePath)
            response.setContentLength(b.length)
            response.getOutputStream().write(b)
        }else{
            flash.message="El archivo seleccionado no se encuentra en el servidor."
            redirect(action: "showFoto",params: [id:rubro.id,tipo: "dt"])
        }

    }


    def downloadFileAres() {
//        println "downloadFileAres: $params"
        def ares = ArchivoEspecificacion.get(params.id)

        def tipo = params.tipo
        def filePath = ares.ruta

        def ext = filePath.split("\\.")
        ext = ext[ext.size() - 1]
        def folder = "rubros"
        def path = servletContext.getRealPath("/") + folder + File.separatorChar + filePath
//        println "path "+path
        def file = new File(path)
        if(file.exists()){
            def b = file.getBytes()
            response.setContentType(ext == 'pdf' ? "application/pdf" : "image/" + ext)
            response.setHeader("Content-disposition", "attachment; filename=" + filePath)
            response.setContentLength(b.length)
            response.getOutputStream().write(b)
        }else{
            flash.message="El archivo seleccionado no se encuentra en el servidor."
            redirect(action: "showFoto",params: [id: params.rubro, tipo: "dt"])
        }

    }

    def uploadFile() {
        println "upload "+params

        def acceptedExt = ["jpg", "png", "gif", "jpeg", "pdf"]

        def tipo = params.tipo

        def path = servletContext.getRealPath("/") + "rubros/"   //web-app/rubros
        new File(path).mkdirs()
        def rubro = Item.get(params.rubro)
        def archivEsp
        if(ArchivoEspecificacion.findByCodigo(rubro.codigoEspecificacion)) {
            archivEsp = ArchivoEspecificacion.findByCodigo(rubro.codigoEspecificacion)
        } else {
//            println("entro")
            archivEsp = new ArchivoEspecificacion()
            archivEsp.item = rubro
            archivEsp.codigo = rubro.codigoEspecificacion
        }


        def f = request.getFile('file')  //archivo = name del input type file
        if (f && !f.empty) {
            def fileName = f.getOriginalFilename() //nombre original del archivo
            def ext
            def parts = fileName.split("\\.")
            fileName = ""
            parts.eachWithIndex { obj, i ->
                if (i < parts.size() - 1) {
                    fileName += obj
                } else {
                    ext = obj
                }
            }
            if (acceptedExt.contains(ext.toLowerCase())) {
                def ahora = new Date()
                fileName = "r_" + tipo + "_" + rubro.id + "_" + ahora.format("dd_MM_yyyy_hh_mm_ss")
                fileName = fileName + "." + ext
                def pathFile = path + fileName
                def file = new File(pathFile)
                println "subiendo archivo: $fileName"

                f.transferTo(file)

                def old = tipo == "il" ? rubro.foto : archivEsp?.ruta
                if (old && old.trim() != "") {
                    def oldPath = servletContext.getRealPath("/") + "rubros/" + old
                    def oldFile = new File(oldPath)
                    if (oldFile.exists()) {
                        oldFile.delete()
                    }
                }

                switch (tipo) {
                    case "il":
                        rubro.foto = /*g.resource(dir: "rubros") + "/" + */ fileName
                        rubro.save(flush: true)
                        break;
                    case "dt":
//                        rubro.especificaciones = /*g.resource(dir: "rubros") + "/" + */ fileName
                        archivEsp?.ruta = /*g.resource(dir: "rubros") + "/" + */ fileName
                        break;
                }

//
//                if(rubro.save(flush: true)){
//                    if(rubro.codigoEspecificacion!="" && rubro.codigoEspecificacion){
//                        def rubros = Item.findAllByCodigoNotEqualAndCodigoEspecificacion(rubro.codigo,rubro.codigoEspecificacion)
//                        rubros.each {
//                            it.especificaciones=rubro.especificaciones
//                            it.save(flush: true)
//                        }
//                    }
//                }



                if(archivEsp.save(flush: true)){
                    rubro.especificaciones = archivEsp?.ruta
                    rubro.save(flush: true)
                }
            } else {
                flash.clase = "alert-error"
                flash.message = "Error: Los formatos permitidos son: JPG, JPEG, GIF, PNG y PDF"
            }
        } else {
            flash.clase = "alert-error"
            flash.message = "Error: Seleccione un archivo JPG, JPEG, GIF, PNG ó PDF"
        }

        redirect(action: "showFoto", id: rubro.id, params: [tipo: tipo])
        return
    }


    def verificaRubro(){
//        println "verifica rubro "+params
        def rubro = Item.get(params.id)
        def volumenes = VolumenesObra.findAllByItem(rubro);
        def obras = volumenes.obra.nombre.unique()
        def respuesta = "<ul>"
//        println "vol... obras:  ${obras}"
        if(volumenes.size()>0) {
            obras.each {
                respuesta += "<li>$it </li>"
            }
            respuesta += "</ul>"
//            println ">>>> 1_${respuesta}"
            render "1_${respuesta}"
        } else {
            render "0"
        }
    }

    def copiaRubro(){
//        println "copia rubro "+params
        def rubro = Item.get(params.id)
        def nuevo = new Item()
        nuevo.properties=rubro.properties
        def codigo ="H"
        def copias = Item.findAllByCodigo(codigo+rubro.codigo)
        def error =false
        if(copias.size() > 0){
            while(copias.size()!= 0){
                codigo=codigo+"H"
                copias = Item.findAllByCodigo(codigo+rubro.codigo)
            }
        }


        rubro.codigo=codigo+rubro.codigo;
        rubro.fechaModificacion=new Date()
        rubro.save(flush: true)
        nuevo.fecha=new Date()
        nuevo.fechaModificacion=null
        if(!nuevo.save(flush: true)){
            println "erro copiar rubro "+nuevo.errors
            error=true
        }else{
//            println "nuevo "+nuevo.id
            Rubro.findAllByRubro(rubro).each{
//                println "copia comp "+it
                def r = new Rubro()
                r.rubro=nuevo
                r.item=it.item
                r.cantidad=it.cantidad
                r.fecha=it.fecha
                r.rendimiento=it.rendimiento
                if(!r.save(flush: true)){
                    println "error copiar comp "+r.errors
                    error=true
                }
            }
        }
        if(error==false)
            error=nuevo.id
        render error
    }

    def borrarArchivo(){

//        println("params " + params)
        def errores = ''

        switch (params.tipo) {
            case "il":
                def rubro = Item.get(params.rubro)
                rubro.foto = null
                if(!rubro.save(flush: true)){
                    errores += rubro.errors
                }else{

                }
                break;
            case "dt":
//                def ares = ArchivoEspecificacion.get(params.id)
//                if(!ares.delete(flush: true)){
//                    errores += ares.errors
//                }else{
//
//                }
                break;
        }

        if(errores == ''){
            render "ok"
        }else{
            render "no"
        }
    }

} //fin controller
