package janus

import org.springframework.dao.DataIntegrityViolationException

class FiscalizadorContratoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def addFisc() {
//        println params
        def contrato = Contrato.get(params.contrato)
        def persona = Persona.get(params.fisc)
        def desde = new Date().parse("dd-MM-yyyy", params.desde)
        def error = ""

        def nuevo = new FiscalizadorContrato([
                contrato: contrato,
                fiscalizador: persona,
                fechaInicio: desde
        ])
        def fiscs = FiscalizadorContrato.findAllByContrato(contrato, [sort: 'fechaInicio', order: "desc"])

        if (fiscs.size() > 0) {
//            def newest = contrato.administradorContrato
            def newest = fiscs.first()

//            println "CURRENT: " + newest
//            println newest.class
            if (newest.id) {
//                println newest.fechaInicio
//                println desde
                if (newest.fechaInicio < desde) {
                    newest.fechaFin = desde
                    if (!newest.save(flush: true)) {
                        println "error al poner fecha fin de newest " + newest + " " + newest.errors
                    }
                } else {
//                    error = "NO_No puede asignar una fecha de inicio inferior a " + newest.fechaInicio.format("dd-MM-yyyy")
                    def overlap = FiscalizadorContrato.withCriteria {
                        eq("contrato", contrato)
                        le("fechaInicio", desde)
                        or {
                            ge("fechaFin", desde)
                            isNull("fechaFin")
                        }
                    }
//                    println "** "
//                    overlap.each {
//                        println it.fechaInicio.format("dd-MM-yyyy") + "    ->    " + it.fechaFin?.format("dd-MM-yyyy")
//                    }
                    if (overlap.size() > 0) {
                        error = "NO_No puede asignar una fecha de inicio entre ${overlap.fechaInicio*.format('dd-MM-yyyy')} y ${overlap.fechaFin*.format('dd-MM-yyyy')}"
                    }
                    nuevo.fechaFin = newest.fechaInicio - 1
                }
            }
        }
        if (error == "") {
            if (!nuevo.save(flush: true)) {
                println "error al crear nuevo admin: " + nuevo.errors
            }
            render "OK"
        } else {
            render error
        }
    }

    def tabla() {
        def contrato = Contrato.get(params.contrato)
        def lista = FiscalizadorContrato.findAllByContrato(contrato, [sort: "fechaInicio", order: "desc"])
        [fiscalizadorContratoInstanceList: lista, params: params]
    }

    def list_ext() {
        [administradorContratoInstanceList: AdministradorContrato.list(params), params: params]
    } //list

    def indirectos() {
//        println "params: $params"
        def cntr = Contrato.get(params.contrato)
        [cntr: cntr]
    } //list

    def guardarIndirectos() {
//        println "guardar indirectos, params: $params"
        def cntr = Contrato.get(params.cntr)
        def pcnt = params.indirectos.trim().toDouble()
        if((pcnt >= 0.0) && (pcnt < 100.0)) {
            cntr.indirectos = pcnt
            cntr.save(flush: true)
        } else {
            flash.clase = "alert-error"
            flash.message = "Valor no válido: $params.indirectos"
        }
        render "ok"
    }

    def adicionales() {
//        println "params: $params"
        def cntr = Contrato.get(params.contrato)
        [cntr: cntr]
    } //list


    def guardarAdicionales() {
        println "guardar adicionales, params: $params"
        def cntr = Contrato.get(params.cntr)
        def adicionales = params.adicionales.trim().toUpperCase()
        if(adicionales) {
            cntr.adicionales = adicionales
            cntr.save(flush: true)
        } else {
            flash.clase = "alert-error"
            flash.message = "Valor no válido: $params.adicionales"
        }
        render "ok"
    }



    def list() {
        [fiscalizadorContratoInstanceList: FiscalizadorContrato.list(params), params: params]
    } //list

    def form_ajax() {
        def fiscalizadorContratoInstance = new FiscalizadorContrato(params)
        if (params.id) {
            fiscalizadorContratoInstance = FiscalizadorContrato.get(params.id)
            if (!fiscalizadorContratoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Fiscalizador Contrato con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [fiscalizadorContratoInstance: fiscalizadorContratoInstance]
    } //form_ajax

    def save() {
        def fiscalizadorContratoInstance
        if (params.id) {
            fiscalizadorContratoInstance = FiscalizadorContrato.get(params.id)
            if (!fiscalizadorContratoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Fiscalizador Contrato con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            fiscalizadorContratoInstance.properties = params
        }//es edit
        else {
            fiscalizadorContratoInstance = new FiscalizadorContrato(params)
        } //es create
        if (!fiscalizadorContratoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Fiscalizador Contrato " + (fiscalizadorContratoInstance.id ? fiscalizadorContratoInstance.id : "") + "</h4>"

            str += "<ul>"
            fiscalizadorContratoInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Fiscalizador Contrato " + fiscalizadorContratoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Fiscalizador Contrato " + fiscalizadorContratoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def fiscalizadorContratoInstance = FiscalizadorContrato.get(params.id)
        if (!fiscalizadorContratoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Fiscalizador Contrato con id " + params.id
            redirect(action: "list")
            return
        }
        [fiscalizadorContratoInstance: fiscalizadorContratoInstance]
    } //show

    def delete() {
        def fiscalizadorContratoInstance = FiscalizadorContrato.get(params.id)
        if (!fiscalizadorContratoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Fiscalizador Contrato con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            fiscalizadorContratoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Fiscalizador Contrato " + fiscalizadorContratoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Fiscalizador Contrato " + (fiscalizadorContratoInstance.id ? fiscalizadorContratoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
