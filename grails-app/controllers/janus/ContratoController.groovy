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

        /**
         * TODO: buscar por nombre de la obra tambien
         */
        def campos = ["codigo": ["Código", "string"]]

        [campos:campos]
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
        def listaCampos = ["codigo", "obra", "codigoObra", "monto", "porcentajeAnticipo", "anticipo", "proveedorObra", "fechaInicio", "plazoObra"]
        def funciones = [null, null, ["closure": [codObra, "&"]], null, null, null, ["closure": [provObra, "&"]], ["format": ["dd/MM/yyyy hh:mm"]], ["closure": [plazObra, "&"]]]
        def url = g.createLink(action: "buscaContrato", controller: "rubro")
        def funcionJs = ""
//        def funcionJs = "function(){"
//        funcionJs += '$("#modal-rubro").modal("hide");'
//        funcionJs += '$("#item_id").val($(this).attr("regId"));$("#item_codigo").val($(this).attr("prop_codigo"));$("#item_nombre").val($(this).attr("prop_nombre"))'
//        funcionJs += '}'
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
