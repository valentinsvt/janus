package janus

class VolumenObraController extends janus.seguridad.Shield{
    def buscadorService
    def preciosService

    def volObra(){

        def grupoFiltrado = Grupo.findAllByCodigoNotIlikeAndCodigoNotIlikeAndCodigoNotIlike('1','2', '3');
        def subpreFiltrado = []
        def var

//        def grupos = Grupo.list([sort: "descripcion"])
        subpreFiltrado=SubPresupuesto.findAllByGrupo(grupoFiltrado[0])
//        println("-->>" + subpreFiltrado)


        def usuario = session.usuario.id

        def persona = Persona.get(usuario)

        def direccion = Direccion.get(persona?.departamento?.direccion?.id)

        def grupo = Grupo.findAllByDireccion(direccion)

        def subPresupuesto1 = SubPresupuesto.findAllByGrupoInList(grupo)

        def obra = Obra.get(params.id)
        def volumenes = VolumenesObra.findAllByObra(obra)




        def campos = ["codigo": ["Código", "string"], "nombre": ["Descripción", "string"]]

        [obra:obra, volumenes:volumenes, campos:campos, subPresupuesto1: subPresupuesto1, grupoFiltrado: grupoFiltrado, subpreFiltrado: subpreFiltrado,grupos:grupoFiltrado]
    }

    def cargarSubpres(){
        def grupo = Grupo.get(params.grupo)
        def subs = SubPresupuesto.findAllByGrupo(grupo)
        [subs:subs]
    }

    def cargaCombosEditar(){

        def sub = SubPresupuesto.get(params.id)
        def grupo=sub?.grupo
        def subs= SubPresupuesto.findAllByGrupo(grupo)
        [subs:subs,sub:sub]
    }


    def buscarRubroCodigo(){
        def rubro = Item.findByCodigoAndTipoItem(params.codigo?.trim(),TipoItem.get(2))
        if (rubro){
            render ""+rubro.id+"&&"+rubro.tipoLista?.id+"&&"+rubro.nombre+"&&"+rubro.unidad?.codigo
            return
        } else{
            render "-1"
            return
        }
    }


    def addItem(){
//        println "addItem " + params
        def obra = Obra.get(params.obra)
//        def rubro2 = Item.get(params.rubro)
//        def rubro = Item.get(params.id)
        def rubro = Item.findByCodigo(params.cod)
        def sbpr = SubPresupuesto.get(params.sub)
        def volumen
//        if (params.vlob_id)
        if (params.id)
            volumen = VolumenesObra.get(params.id)
        else {

                volumen=new VolumenesObra()
        }
//        println "volumn :" + volumen
        volumen.cantidad = params.cantidad.toDouble()
        volumen.orden    = params.orden.toInteger()
        volumen.subPresupuesto = SubPresupuesto.get(params.sub)
        volumen.obra = obra
        volumen.item = rubro
        if (!volumen.save(flush: true)){
            println "error volumen obra " + volumen.errors
            render "error"
        }else{
            preciosService.actualizaOrden(volumen,"insert")
            redirect(action: "tabla",params: [obra:obra.id,sub:volumen.subPresupuesto.id,ord: 1])
        }
    }

    def copiarItem () {

//        println "copiarItem "+params
        def obra= Obra.get(params.obra)
        def rubro = Item.get(params.rubro)
        def sbprDest = SubPresupuesto.get(params.subDest)
        def sbpr = SubPresupuesto.get(params.sub)

        def itemVolumen = VolumenesObra.findByItemAndSubPresupuesto(rubro,sbpr)
        def itemVolumenDest = VolumenesObra.findByItemAndSubPresupuestoAndObra(rubro,sbprDest,obra)

        def volumen

        def volu = VolumenesObra.list()

        if (params.id)
            volumen = VolumenesObra.get(params.id)
        else {
            if (itemVolumenDest){

                flash.clase = "alert-error"
                flash.message = "No se puede copiar el rubro " + rubro.nombre
                redirect(action: "tablaCopiarRubro", params: [obra: obra.id])
                return

            } else {
            volumen = VolumenesObra.findByObraAndItemAndSubPresupuesto(obra, rubro, sbprDest)


            if (volumen == null)
                volumen=new VolumenesObra()

            }
        }

        volumen.cantidad=itemVolumen.cantidad.toDouble()
        volumen.orden=(volu.orden.size().toInteger())+1
        volumen.subPresupuesto=SubPresupuesto.get(params.subDest)
        volumen.obra=obra
        volumen.item=rubro

        if (!volumen.save(flush: true)){
//            println "error volumen obra "+volumen.errors

            flash.clase = "alert-error"
            flash.message = "Error, no es posible completar la acción solicitada "

            redirect(action: "tablaCopiarRubro",params: [obra:obra.id])

//            render "error"
        }else{
            preciosService.actualizaOrden(volumen,"insert")

            flash.clase = "alert-success"
            flash.message = "Copiado rubro " + rubro.nombre

            redirect(action: "tablaCopiarRubro",params: [obra:obra.id,sub:volumen.subPresupuesto.id])
        }


    }



    def tabla(){

        def usuario = session.usuario.id
        def persona = Persona.get(usuario)
        def direccion = Direccion.get(persona?.departamento?.direccion?.id)
        def grupo = Grupo.findAllByDireccion(direccion)
        def subPresupuesto1 = SubPresupuesto.findAllByGrupoInList(grupo)
//
//        println "params --->>>> "+params
        def obra = Obra.get(params.obra)
        def detalle
        def valores
        def orden

        if (params.ord == '1'){
            orden = 'asc'
        } else {
             orden = 'desc'
        }

        if (params.sub && params.sub != "null") {
//            println("entro1")
//        detalle= VolumenesObra.findAllByObraAndSubPresupuesto(obra,SubPresupuesto.get(params.sub),[sort:"orden"])
         valores = preciosService.rbro_pcun_v5(obra.id,params.sub,orden)
//          detalle= VolumenesObra.findAllBySubPresupuesto(SubPresupuesto.get(params.sub))
//            println("detalle" + detalle)
        }
        else  {
//            println("entro2")
//        detalle= VolumenesObra.findAllByObra(obra,[sort:"orden"])
        valores = preciosService.rbro_pcun_v4(obra.id,orden)
        }

        def subPres = VolumenesObra.findAllByObra(obra,[sort:"orden"]).subPresupuesto.unique()

        def precios = [:]
        def fecha = obra.fechaPreciosRubros
        def dsps = obra.distanciaPeso
        def dsvl = obra.distanciaVolumen
        def lugar = obra.lugar
        def prch = 0
        def prvl = 0

//        /*Todo ver como mismo es esta suma*/
        def indirecto = obra.totales/100

        preciosService.ac_rbroObra(obra.id)

        [subPres:subPres, subPre:params.sub, obra: obra, precioVol:prch, precioChof:prvl, indirectos:indirecto*100, valores: valores, subPresupuesto1: subPresupuesto1]

    }

    def eliminarRubro(){
        def vol = VolumenesObra.get(params.id)
        def obra = vol.obra
        def orden = vol.orden
        preciosService.actualizaOrden(vol,"delete")
        vol.delete()
        redirect(action: "tabla",params: [obra:obra.id, sub: vol.subPresupuesto.id, ord: 1])


    }

    def copiarRubros () {

        def obra = Obra.get(params.obra)
        def volumenes = VolumenesObra.findAllByObra(obra)

        return[obra: obra, volumenes:  volumenes]

    }

    def tablaCopiarRubro () {


        def usuario = session.usuario.id

        def persona = Persona.get(usuario)

        def direccion = Direccion.get(persona?.departamento?.direccion?.id)

        def grupo = Grupo.findAllByDireccion(direccion)


        def subPresupuesto1 = SubPresupuesto.findAllByGrupoInList(grupo)


        def obra = Obra.get(params.obra)

        def valores
        if (params.sub && params.sub != "null") {
            valores = preciosService.rbro_pcun_v3(obra.id, params.sub)

        }
        else{
            valores = preciosService.rbro_pcun_v2(obra.id)

        }

        def subPres = VolumenesObra.findAllByObra(obra,[sort:"orden"]).subPresupuesto.unique()


        def precios = [:]
        def fecha = obra.fechaPreciosRubros
        def dsps = obra.distanciaPeso
        def dsvl = obra.distanciaVolumen
        def lugar = obra.lugar
        def prch = 0
        def prvl = 0
        def indirecto = obra.totales/100

        preciosService.ac_rbroObra(obra.id)

        [precios:precios,subPres:subPres,subPre:params.sub,obra: obra,precioVol:prch,precioChof:prvl,indirectos:indirecto*100, valores: valores, subPresupuesto1: subPresupuesto1]




    }


    def buscaRubro() {

        def listaTitulos = ["Código", "Descripción","Unidad"]
        def listaCampos = ["codigo", "nombre","unidad"]
        def funciones = [null, null]
        def url = g.createLink(action: "buscaRubro", controller: "rubro")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-rubro").modal("hide");'
        funcionJs += '$("#item_id").val($(this).attr("regId"));$("#item_codigo").val($(this).attr("prop_codigo"));$("#item_nombre").val($(this).attr("prop_nombre"))'
        funcionJs += '}'
        def numRegistros = 20
        def extras = " and tipoItem = 2"
        if (!params.reporte) {
            def lista = buscadorService.buscar(Item, "Item", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
            lista.pop()
            render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Item
            session.funciones = funciones
            def anchos = [20, 80] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Item", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Rubros", anchos: anchos, extras: extras, landscape: true])
        }
    }
}
