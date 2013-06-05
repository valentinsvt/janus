package janus.pac

class PacController extends janus.seguridad.Shield{

    def buscadorService

    def index() {}

    def registrarPac(){
        def campos = ["numero": ["Código", "string"], "descripcion": ["Descripción", "string"]]

        [campos:campos]
    }

    def buscaCpac(){
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
            def lista = buscadorService.buscar(CodigoComprasPublicas, "CodigoComprasPublicas", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
            lista.pop()
            render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = CodigoComprasPublicas
            session.funciones = funciones
            def anchos = [20, 80] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "CodigoComprasPublicas", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Código compras publcias", anchos: anchos, extras: extras, landscape: false])
        }
    }

    def buscaPrsp(){
        def listaTitulos = ["Código", "Descripción"]
        def listaCampos = ["numero", "descripcion"]
        def funciones = [null, null]
        def url = g.createLink(action: "buscaCpac", controller: "pac")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-ccp").modal("hide");'
        funcionJs += '$("#item_prsp").val($(this).attr("regId"));$("#item_presupuesto").val($(this).attr("prop_numero"));$("#item_presupuesto").attr("title",$(this).attr("prop_descripcion"));cargarTecho();'
        funcionJs += '}'
        def numRegistros = 20
        def extras = ""
        if (!params.reporte) {
            def lista = buscadorService.buscar(janus.Presupuesto, "Presupuesto", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
            lista.pop()
            render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
        } else {
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Presupuesto
            session.funciones = funciones
            def anchos = [20, 80] /*el ancho de las columnas en porcentajes... solo enteros*/
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
        def pacs = Pac.findAllByPresupuestoAndAnio(prsp,anio)
        def usado = 0
        pacs.each {
            usado+=it.costo*it.cantidad
        }
        render ""+techo+";"+usado
    }


    def tabla(){

        def pac
        def dep
        def anio
        if (!params.todos){
            anio=Anio.get(params.anio)
            if (params.dpto){
                dep = janus.Departamento.get(params.dpto)
                pac = Pac.findAllByDepartamentoAndAnio(dep,anio,[sort: "id"])
                dep = dep.descripcion
                anio=anio.anio
            } else{
                pac = Pac.findAllByAnio(Anio.get(params.anio),[sort: "id"])
                dep = "Todos"
                anio=anio.anio
            }
        }else{
            dep = "Todos"
            anio = "Todos"
            pac = Pac.list([sort: "id"])
        }

        [pac:pac,todos:params.todos,dep:dep,anio:anio]

    }


    def regPac(){
//        println params

        def pac
        if (params.id!="" && params.id)
            pac = Pac.get(params.id)
        else
            pac = new Pac()
        pac.properties=params
        if (!pac.save(flush: true))
            println "errors "+pac.errors
        else
            render "ok"


    }

    def eliminarPac(){
//        println "eliminar pac "+params
        def pac = Pac.get(params.id)
        pac.delete(flush: true)
        render "ok"
    }


}
