package janus

import org.springframework.dao.DataIntegrityViolationException

class ContratoController extends janus.seguridad.Shield {

    def buscadorService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [contratoInstanceList: Contrato.list(params), params: params]
    } //list


    def registroContrato () {

     def contrato

        def obra

        if (params.contrato) {

//            def camposObra = ["codigo": ["Código", "string"], "nombre": ["Nombre", "string"], "descripcion": ["Descripción", "string"], "oficioIngreso": ["Memo ingreso", "string"], "oficioSalida": ["Memo salida", "string"], "sitio": ["Sitio", "string"], "plazo": ["Plazo", "int"], "parroquia": ["Parroquia", "string"], "comunidad": ["Comunidad", "string"]]

           contrato = Contrato.get(params.contrato)
//           obra = Obra.get(params.obra)




            def campos = ["codigo": ["Código", "string"]]

            [campos:campos, contrato: contrato]


        } else {



            def campos = ["codigo": ["Código", "string"]]

            [campos:campos]

        }





        /**
         * TODO: buscar por nombre de la obra tambien
         */

    }


    def polinomicaContrato () {

        println(params)


    def obra = Obra.get(params.id)

    def formula = FormulaPolinomica.findAllByObra(obra)


        def p01 = FormulaPolinomica.findByObraAndNumero(obra, 'p01')
        def p02 = FormulaPolinomica.findByObraAndNumero(obra, 'p02')
        def p03 = FormulaPolinomica.findByObraAndNumero(obra, 'p03')
        def p04 = FormulaPolinomica.findByObraAndNumero(obra, 'p04')
        def p05 = FormulaPolinomica.findByObraAndNumero(obra, 'p05')
        def p06 = FormulaPolinomica.findByObraAndNumero(obra, 'p06')
        def p07 = FormulaPolinomica.findByObraAndNumero(obra, 'p07')
        def p08 = FormulaPolinomica.findByObraAndNumero(obra, 'p08')
        def p09 = FormulaPolinomica.findByObraAndNumero(obra, 'p09')
        def p10 = FormulaPolinomica.findByObraAndNumero(obra, 'p10')
        def px = FormulaPolinomica.findByObraAndNumero(obra, 'px')


    [p01: p01, p02: p02, p03: p03, p04: p04, p05: p05, p06: p06,
            p07: p07, p08: p08, p09: p09, p10: p10, px: px ]




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

        def codObra =  { contrato ->
            return contrato?.oferta?.concurso?.obra?.codigo
        }
        def provObra =  { contrato ->
            return contrato?.oferta?.proveedor?.nombre
        }
        def plazObra =  { contrato ->
            return contrato?.oferta?.concurso?.obra?.plazo
        }

        def listaTitulos = ["N. Contrato", "Nombre Obra", "Código Obra", "Monto", "% Anticipo", "Anticipo", "Contratista", "Fecha contrato", "Plazo"]
        def listaCampos = ["codigo", "obra", "codigoObra", "monto", "porcentajeAnticipo", "anticipo", "proveedorObra", "fechaInicio", "plazo"]
        def funciones = [null, null, ["closure": [codObra, "&"]], null, null, null, ["closure": [provObra, "&"]], ["format": ["dd/MM/yyyy hh:mm"]], null]
        def url = g.createLink(action:"buscarContrato", controller: "contrato")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-busqueda").modal("hide");'
        funcionJs += 'location.href="' + g.createLink(action: 'registroContrato', controller: 'contrato') + '?contrato="+$(this).attr("regId");'
        funcionJs += '}'
        def numRegistros = 20
        def extras = ""
//        def extras = " and tipoItem = 2"
        if (!params.reporte) {
            def lista = buscadorService.buscar(Contrato, "Contrato", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */


            lista.pop()

            render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
        } else {
            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Item
            session.funciones = funciones
            def anchos = [20, 80] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Item", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Rubros", anchos: anchos, extras: extras, landscape: true])
        }
    }

    def buscarObra () {


//        def parr = {p ->
//            return p.parroquia?.nombre
//        }
//        def comu = {c ->
//            return c.comunidad?.nombre
//        }
//
//        def listaTitulos = ["Código", "Nombre","Descripción","Fecha Reg.","M. ingreso","M. salida","Sitio","Plazo","Parroquia","Comunidad","Inspector","Revisor","Responsable","Estado Obra"]
//        def listaCampos = ["codigo", "nombre","descripcion","fechaCreacionObra","oficioIngreso","oficioSalida","sitio","plazo","parroquia","comunidad","inspector","revisor","responsable","estadoObra"]
//        def funciones = [null, null,null,["format": ["dd/MM/yyyy hh:mm"]],null, null,null,null, ["closure": [parr, "&"]],["closure": [comu, "&"]],null,null,null,null]
//        def url = g.createLink(action: "buscarObra", controller: "obra")
//        def funcionJs = "function(){"
//        funcionJs += '$("#modal-busquedaOferta").modal("hide");'
//        funcionJs += 'location.href="' + g.createLink(action: 'registroContrato', controller: 'contrato') + '?obra="+$(this).attr("regId");'
//        funcionJs += '}'
//        def numRegistros = 20
//        def extras = ""
//
//        if (!params.reporte) {
//            def lista = buscadorService.buscar(Obra, "Obra", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
//            lista.pop()
//            render(view: '../tablaBuscador', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs,width:1800,paginas:12])
//        } else {
////            println "entro reporte"
//            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
//            session.dominio = Obra
//            session.funciones = funciones
//            def anchos = [7,10,7,7,7,7,7,4,7,7,7,7,7,7 ] /*el ancho de las columnas en porcentajes... solo enteros*/
//            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Obra", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Obras", anchos: anchos, extras: extras, landscape: true])
//        }

    }


    def form_ajax() {
        def contratoInstance = new Contrato(params)
        if (params.id) {
            contratoInstance = Contrato.get(params.id)
            if (!contratoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Contrato con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [contratoInstance: contratoInstance]
    } //form_ajax

    def save() {
        def contratoInstance
        if (params.id) {
            contratoInstance = Contrato.get(params.id)
            if (!contratoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Contrato con id " + params.id
                redirect(action: 'list')
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
            redirect(action: 'list')
            return
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Contrato " + contratoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Contrato " + contratoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def contratoInstance = Contrato.get(params.id)
        if (!contratoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Contrato con id " + params.id
            redirect(action: "list")
            return
        }
        [contratoInstance: contratoInstance]
    } //show

    def delete() {
        def contratoInstance = Contrato.get(params.id)
        if (!contratoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Contrato con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            contratoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Contrato " + contratoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Contrato " + (contratoInstance.id ? contratoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
