package janus

import groovy.json.JsonBuilder
import org.springframework.dao.DataIntegrityViolationException

class ContratoController extends janus.seguridad.Shield {

    def buscadorService
    def preciosService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [contratoInstanceList: Contrato.list(params), params: params]
    } //list


    def registroContrato () {

     def contrato

        def obra = Obra.get(params.obra)


        if (params.contrato) {


           contrato = Contrato.get(params.contrato)





            def campos = ["codigo": ["Código", "string"],"nombre": ["Nombre", "string"]]

            [campos:campos, contrato: contrato]


        } else {



            def campos = ["codigo": ["Código", "string"],"nombre": ["Nombre", "string"]]

            [campos:campos]

        }


    }


    def polinomicaContrato () {

//        println(params)


    def obra = Obra.get(886)

        def ps = FormulaPolinomica.findAllByObraAndNumeroIlike(obra, 'p%')

    def formula = FormulaPolinomica.findAllByObra(obra)

//        println("formula" +  formula)
//        println("ps" + ps)

    def cuadrilla = FormulaPolinomica.findAllByObraAndNumeroIlike(obra, 'c%')


        [ps:ps, cuadrilla: cuadrilla]

    }

    def buscarContrato (){

        /*
        N. contrato        codigo
        nombre obra
        codigo obra
        monto
        % anticipo
        anticipo
        contratista
        fecha contrato
        plazo

        canton
        parroquia

         */


//        println "buscar contrato "+params

        def extraObra=""
        if(params.campos instanceof java.lang.String){
            if(params.campos=="nombre"){
                def obras = Obra.findAll("from Obra where nombre like '%${params.criterios.toUpperCase()}%'")
                params.criterios=""
                obras.eachWithIndex {p,i->
                    def concursos = janus.pac.Concurso.findAllByObraAndEstado(p,"R")
                    concursos.each {co->
                        def ofertas = janus.pac.Oferta.findAllByConcurso(co)
                        ofertas.eachWithIndex {o,k->
                            extraObra+=""+o.id
                            if(k <ofertas.size()-1)
                                extraObra+=","
                        }
                       
                    }
                    
                }
                if(extraObra.size()<1)
                    extraObra="-1"
                params.campos=""
                params.operadores=""
            }
        }else{
            def remove = []
            params.campos.eachWithIndex{p,i->
                if(p=="nombre"){
                    def obras = Obra.findAll("from Obra where nombre like '%${params.criterios[i].toUpperCase()}%'")

                    obras.eachWithIndex {ob,j->
                        def concursos = janus.pac.Concurso.findAllByObraAndEstado(ob,"R")
                        concursos.each {co->
                            def ofertas = janus.pac.Oferta.findAllByConcurso(co)
                            ofertas.eachWithIndex {o,k->
                                extraObra+=""+o.id
                                if(k <ofertas.size()-1)
                                    extraObra+=","
                                remove.add(i)
                            }

                        }

                    }
                    if(extraObra.size()<1)
                        extraObra="-1"
                    
                }
            }
            remove.each{
                params.criterios[it]=null
                params.campos[it]=null
                params.operadores[it]=null
            }
        }

//        println "extra obra "+extraObra

        def codObra =  { contrato ->
            return contrato?.oferta?.concurso?.obra?.codigo
        }
        def provObra =  { contrato ->
            return contrato?.oferta?.proveedor?.nombre
        }
        def plazObra =  { contrato ->
            return contrato?.oferta?.concurso?.obra?.plazo
        }
        def nombreObra =  { contrato ->
            return contrato?.oferta?.concurso?.obra?.nombre
        }

        def listaTitulos = ["N. Contrato", "Nombre Obra", "Código Obra", "Monto", "% Anticipo", "Anticipo", "Contratista", "Fecha contrato", "Plazo"]
        def listaCampos = ["codigo", "obra", "codigoObra", "monto", "porcentajeAnticipo", "anticipo", "proveedorObra", "fechaInicio", "plazo"]
        def funciones = [null, ["closure": [nombreObra, "&"]], ["closure": [codObra, "&"]], null, null, null, ["closure": [provObra, "&"]], ["format": ["dd/MM/yyyy hh:mm"]], null]
        def url = g.createLink(action:"buscarContrato", controller: "contrato")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-busqueda").modal("hide");'
        funcionJs += 'location.href="' + g.createLink(action: 'registroContrato', controller: 'contrato') + '?contrato="+$(this).attr("regId");'
        funcionJs += '}'
        def numRegistros = 20
        def extras = " "
        if (extraObra.size()>0)
            extras+=" and oferta in (${extraObra})"
//        println "extras "+extras

        if (!params.reporte) {
            def lista = buscadorService.buscar(Contrato, "Contrato", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */


            lista.pop()

            render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
        } else {
            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Contrato
            session.funciones = funciones
            def anchos = [10, 20,10,10,5,10,20,10,5] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Contrato", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Contratos", anchos: anchos, extras: extras, landscape: true])
        }
    }

    def buscarObra () {

        def extras = " "
        def parr = {p ->
            return p.parroquia?.nombre
        }
        def comu = {c ->
            return c.comunidad?.nombre
        }
        def listaTitulos = ["Código", "Nombre","Descripción","Fecha Reg.","M. ingreso","M. salida","Sitio","Plazo","Parroquia","Comunidad","Clase","Estado Obra"]
        def listaCampos = ["codigo", "nombre","descripcion","fechaCreacionObra","oficioIngreso","oficioSalida","sitio","plazo","parroquia","comunidad","claseObra","estadoObra"]
        def funciones = [null, null,null,["format": ["dd/MM/yyyy hh:mm"]],null, null,null,null, ["closure": [parr, "&"]],["closure": [comu, "&"]],null,null,null,null]
        def url = g.createLink(action: "buscarObra", controller: "contrato")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-busqueda").modal("hide");'
        funcionJs += '$("#obraId").val($(this).attr("regId"));'
        funcionJs += '$("#nombreObra").val($(this).parent().parent().find(".props").attr("prop_nombre"));'
        funcionJs += '$("#obraCodigo").val($(this).parent().parent().find(".props").attr("prop_codigo"));'
        funcionJs += '$("#parr").val($(this).parent().parent().find(".props").attr("prop_parroquia"));'
        funcionJs += '$("#canton").val($(this).parent().parent().find(".props").attr("prop_canton"));'
        funcionJs += '$("#clase").val($(this).parent().parent().find(".props").attr("prop_claseObra"));'

        funcionJs += '$("#contratista").val("");'
        funcionJs += 'cargarCombo();'
        funcionJs += 'cargarCanton();'
        funcionJs += '}'
        def numRegistros = 20

        if (!params.reporte) {
            def lista = buscadorService.buscar(Obra, "Obra", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
            lista.pop()
            render(view: '../tablaBuscador', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs,width:1800,paginas:12])
        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Obra
            session.funciones = funciones
            def anchos = [7,10,7,7,7,7,7,4,7,7,7,7,7,7 ] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Obra", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Obras", anchos: anchos, extras: extras, landscape: true])
        }

    }

    def cargarOfertas(){
        def obra = Obra.get(params.id)
        def concurso = janus.pac.Concurso.findByObraAndEstado(obra,"R")
        def ofertas  = janus.pac.Oferta.findAllByConcurso(concurso)
        [ofertas:ofertas]
    }


    def cargarCanton(){
        def obra = Obra.get(params.id)
        render obra?.parroquia?.canton?.nombre
    }

    def form_ajax() {
        def contratoInstance = new Contrato(params)
        if (params.id) {
            contratoInstance = Contrato.get(params.id)
            if (!contratoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Contrato con id " + params.id
                redirect(action: "registroContrato")
                return
            } //no existe el objeto
        } //es edit
        return [contratoInstance: contratoInstance]
    } //form_ajax

    def save() {
        def contratoInstance

        println("-->>"+params)

        if (params.id) {
            contratoInstance = Contrato.get(params.id)
            if (!contratoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Contrato con id " + params.id
                redirect(action: 'registroContrato')
                return
            }//no existe el objeto
            contratoInstance.properties = params
        }//es edit
        else {
            contratoInstance = new Contrato(params)

        } //es create
        if (!contratoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Contrato " + (contratoInstance.id ? contratoInstance.id : "") + "</h4>"

            str += "<ul>"
            contratoInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(action: 'registroContrato')
            return
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Contrato " + contratoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Contrato " + contratoInstance.id
        }
        redirect(action: 'registroContrato')
    } //save

    def show_ajax() {
        def contratoInstance = Contrato.get(params.id)
        if (!contratoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Contrato con id " + params.id
            redirect(action: "registroContrato")
            return
        }
        [contratoInstance: contratoInstance]
    } //show

    def delete() {
        def contratoInstance = Contrato.get(params.id)
        if (!contratoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Contrato con id " + params.id
            redirect(action: "registroContrato")
            return
        }

        try {
            contratoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Contrato " + contratoInstance.id
            redirect(action: "registroContrato")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Contrato " + (contratoInstance.id ? contratoInstance.id : "")
            redirect(action: "registroContrato")
        }
    } //delete
} //fin controller
