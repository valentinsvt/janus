package janus.pac

import groovy.json.JsonBuilder
import janus.Contrato
import org.springframework.dao.DataIntegrityViolationException

class GarantiaController extends janus.seguridad.Shield {

    def buscadorService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    /*
     * * Las garantias editables son las q tienen estado "Vigente" o "Pedido de cobro"
     * * Las q no son editables al dar doble click no pasa nada
     * * Las "vigentes" tienen botones Devolver y Pedir cobro
     * * Las "pedido de cobro" tienen boton Efectivizar
     * * Las observaciones se setean al momento de cambiar el estado con uno de estos botones
     * * Al cambiar el estado y escribir una observacion se remplaza la existente
     * * La observacion esta puesta como title del tr correspondiente
     */

    def cambiarEstado() {
        def garantia = Garantia.get(params.id)
        def estado = EstadoGarantia.findByCodigo(params.estado)
        garantia.estado = estado
        garantia.observaciones = params.obs
        if (!garantia.save(flush: true)) {
            render "NO"
        } else {
            def data = [
                    id: garantia.id,
                    contrato: garantia.contrato.id,
                    tipoGarantiaTxt: garantia.tipoGarantia.descripcion,
                    tipoGarantia: garantia.tipoGarantiaId,
                    codigo: garantia.codigo.trim(),
                    aseguradoraTxt: garantia.aseguradora.nombre,
                    aseguradora: garantia.aseguradoraId,
                    tipoDocumentoGarantiaTxt: garantia.tipoDocumentoGarantia.descripcion,
                    tipoDocumentoGarantia: garantia.tipoDocumentoGarantiaId,
                    monto: garantia.monto,
                    monedaTxt: garantia.moneda.codigo,
                    moneda: garantia.monedaId,
                    fechaInicio: formatDate(date: garantia.fechaInicio, format: "dd-MM-yyyy"),
                    fechaFinalizacion: formatDate(date: garantia.fechaFinalizacion, format: "dd-MM-yyyy"),
                    diasGarantizados: garantia.diasGarantizados,
                    estadoTxt: garantia.estado.descripcion,
                    estadoCdgo: garantia.estado.codigo,
                    estado: garantia.estadoId,
                    padre: garantia.padre ? garantia.padre.codigo.trim() : "",
                    observaciones: garantia.observaciones
            ]
            def json = new JsonBuilder(data)
            render json
        }
    }

    def garantiasContrato() {
//        if (!params.id) {
//            params.id = "5"
//        }
        def contrato = Contrato.get(params.id)
        def campos = ["nombre": ["Nombre", "string"]]

        def garantias = []
//        Garantia.findAllByContrato(contrato, [sort: "tipoGarantia"])

        Garantia.withCriteria {
            eq("contrato", contrato)
            and {
                order("tipoGarantia", "asc")
                estado {
                    order("id", "asc")
                }
                order("fechaInicio", "desc")
            }
        }.each { garantia ->
            garantias.add([
                    id: garantia.id,
                    contrato: contrato.id,
                    tipoGarantiaTxt: garantia.tipoGarantia.descripcion,
                    tipoGarantia: garantia.tipoGarantiaId,
                    codigo: garantia.codigo.trim(),
                    aseguradoraTxt: garantia.aseguradora.nombre,
                    aseguradora: garantia.aseguradoraId,
                    tipoDocumentoGarantiaTxt: garantia.tipoDocumentoGarantia.descripcion,
                    tipoDocumentoGarantia: garantia.tipoDocumentoGarantiaId,
                    monto: garantia.monto,
                    monedaTxt: garantia.moneda.codigo,
                    moneda: garantia.monedaId,
                    fechaInicio: formatDate(date: garantia.fechaInicio, format: "dd-MM-yyyy"),
                    fechaFinalizacion: formatDate(date: garantia.fechaFinalizacion, format: "dd-MM-yyyy"),
                    diasGarantizados: garantia.diasGarantizados,
                    estadoTxt: garantia.estado.descripcion,
                    estadoCdgo: garantia.estado.codigo,
                    estado: garantia.estadoId,
                    padre: garantia.padre ? garantia.padre.codigo.trim() : "",
                    observaciones: garantia.observaciones
            ])
        }
        def json = new JsonBuilder(garantias)

        def estadosGarantia = EstadoGarantia.withCriteria {
            ne("codigo", "1")
            ne("codigo", "6")
            ne("codigo", "4")
        }

        return [contrato: contrato, campos: campos, garantias: json, estadosGarantia: estadosGarantia]
    }

    def deleteGarantia() {
        def garantia = Garantia.get(params.id)
        if (Garantia.countByPadre(garantia) == 0) {
            garantia.delete()
            render "OK"
        } else {
            render "No puede eliminar una garantía de la cual dependen otras"
        }
    }

    def addGarantiaContrato() {
//        println params
        def garantia, datos = true, padre = null

        switch (params.tipo.toString().trim().toLowerCase()) {
            case "add":
                garantia = new Garantia()
                break;
            case "edit":
                garantia = Garantia.get(params.id)
                break;
            case "renew":
                garantia = new Garantia()
                padre = Garantia.get(params.id)
                padre.estado = EstadoGarantia.findByCodigo("6") //renovada
                if (!padre.save(flush: true)) {
                    println "error save padre" + padre.errors
                    println renderErrors(bean: padre)
                }
                break;
        }
        garantia.monto = params.monto.toDouble()
        garantia.fechaInicio = new Date().parse("dd-MM-yyyy", params.fechaInicio)
        garantia.fechaFinalizacion = new Date().parse("dd-MM-yyyy", params.fechaFinalizacion)
        garantia.diasGarantizados = params.diasGarantizados.toInteger()
        garantia.contrato = Contrato.get(params.contrato)
        garantia.aseguradora = Aseguradora.get(params.aseguradora)
        garantia.moneda = Moneda.get(params.moneda)
        garantia.tipoGarantia = TipoGarantia.get(params.tipoGarantia)
        garantia.tipoDocumentoGarantia = TipoDocumentoGarantia.get(params.tipoDocumentoGarantia)
        garantia.estado = EstadoGarantia.findByCodigo("1")  //Vigente
        garantia.codigo = params.codigo.trim()
        garantia.numeroRenovaciones = 0
        garantia.estadoGarantia = "N" //registrado o no
        garantia.padre = padre
        garantia.observaciones = params.observaciones

        if (!garantia.save(flush: true)) {
            println "Errores: " + garantia.errors
            render "NO"
        } else {
            render "OK_" + garantia.id
        }
    }

    def buscaAseguradora() {
        def listaTitulos = ["Nombre"]
        def listaCampos = ["nombre"]
        def funciones = [null]
        def url = g.createLink(action: "buscaAseguradora", controller: "rubro")
//        def funcionJs = ""
        def funcionJs = "function(){"
        funcionJs += '$("#modal-busqueda").modal("hide");'
        funcionJs += '$("#aseguradora").val($(this).attr("regId"));$("#aseguradoraTxt").val($(this).attr("prop_nombre"));'
        funcionJs += '}'
        def numRegistros = 20
        def extras = ""
//        def extras = " and tipoItem = 2"
        if (!params.reporte) {
            def lista = buscadorService.buscar(Aseguradora, "Aseguradora", "excluyente", params, true, extras)
            /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
            lista.pop()
            render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Aseguradora
            session.funciones = funciones
            def anchos = [20, 80] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Aseguradora", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Aseguradoras", anchos: anchos, extras: extras, landscape: true])
        }
    }


    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [garantiaInstanceList: Garantia.list(params), params: params]
    } //list

    def form_ajax() {
        def garantiaInstance = new Garantia(params)
        if (params.id) {
            garantiaInstance = Garantia.get(params.id)
            if (!garantiaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Garantia con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [garantiaInstance: garantiaInstance]
    } //form_ajax

    def save() {
        def garantiaInstance
        if (params.id) {
            garantiaInstance = Garantia.get(params.id)
            if (!garantiaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Garantia con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            garantiaInstance.properties = params
        }//es edit
        else {
            garantiaInstance = new Garantia(params)
        } //es create
        if (!garantiaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Garantia " + (garantiaInstance.id ? garantiaInstance.id : "") + "</h4>"

            str += "<ul>"
            garantiaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Garantia " + garantiaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Garantia " + garantiaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def garantiaInstance = Garantia.get(params.id)
        if (!garantiaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Garantia con id " + params.id
            redirect(action: "list")
            return
        }
        [garantiaInstance: garantiaInstance]
    } //show

    def delete() {
        def garantiaInstance = Garantia.get(params.id)
        if (!garantiaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Garantia con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            garantiaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Garantia " + garantiaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Garantia " + (garantiaInstance.id ? garantiaInstance.id : "")
            redirect(action: "list")
        }
    } //delete


    def fechas_ajax () {

    }


} //fin controller
