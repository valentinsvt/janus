package janus.pac

import groovy.time.TimeCategory
import janus.Contrato
import janus.Obra
import janus.VolumenesObra

class CronogramaEjecucionController extends janus.seguridad.Shield {

    def preciosService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def errores() {

    }

    def tabla() {
        def obra = Obra.get(params.id)

        def precios = [:]
        def indirecto = obra.totales / 100

        preciosService.ac_rbroObra(obra.id)
        def periodos = []
        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        detalle.each {
            def res = preciosService.presioUnitarioVolumenObra("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
        }

        return [detalle: detalle, precios: precios, obra: obra]
    }

    def index() {
        /**
         * TODO: se entra por contrato? por obra?
         */
        if (!params.id) {
            params.id = "5"
        }
//println params
        def contrato = Contrato.get(params.id)
//println contrato
        if (!contrato) {
            flash.message = "No se encontró el contrato"
            flash.clase = "alert-error"
            println flash.message
            redirect(action: "errores", params: [contrato: params.id])
            return
        }
        def obra = contrato?.oferta?.concurso?.obra
        if (!obra) {
            flash.message = "No se encontró la obra"
            flash.clase = "alert-error"
            println flash.message
            redirect(action: "errores", params: [contrato: params.id])
//            redirect(controller: 'contrato', action: "registroContrato", params: [contrato: params.id])
            return
        }
        if (!obra.fechaInicio) {
            flash.message = "La obra no tiene fecha de inicio. Por favor solucione el problema."
            flash.clase = "alert-error"
            println flash.message
            redirect(action: "errores", params: [contrato: params.id])
//            redirect(controller: 'contrato', action: "registroContrato", params: [contrato: params.id])
            return
        }
//println contrato
//println obra
        //copia el cronograma del contrato (crng) a la tabla cronograma de ejecucion (crej)

        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def inicioObra = obra.fechaInicio

        detalle.each { vol ->
            CronogramaContrato.findAllByVolumenObra(vol).eachWithIndex { crono, cont ->
                def c = CronogramaEjecucion.withCriteria {
                    eq("volumenObra", crono.volumenObra)
                    eq("periodo", crono.periodo)
                    eq("tipo", "P")
                }
                if (c.size() == 0) {
                    def dias = (crono.periodo - 1) * 30
                    if (crono.periodo > 1) {
                        dias++
                    }
                    def ini
                    def fin
                    use(TimeCategory) {
                        ini = inicioObra + dias.days
                    }
                    use(TimeCategory) {
                        fin = ini + 30.days
                    }

                    def cronoEjecucion = new CronogramaEjecucion()
                    cronoEjecucion.properties = crono.properties
                    cronoEjecucion.fechaInicio = ini
                    cronoEjecucion.fechaFin = fin
                    cronoEjecucion.tipo = "P"



                    if (!cronoEjecucion.save(flush: true)) {
                        println "Error al guardar el crono ejecucion del crono " + crono.id
                        println cronoEjecucion.errors
                    } else {
                        println "ok " + crono.id + "  =>  " + cronoEjecucion.id
                    }
                } else {
                    println "Ya hay"
                }
            }
        } //detalle.each


        return [obra: obra, contrato: contrato]

    } //index

//    def list() {
//        [cronogramaEjecucionInstanceList: CronogramaEjecucion.list(params), params: params]
//    } //list
//
//    def form_ajax() {
//        def cronogramaEjecucionInstance = new CronogramaEjecucion(params)
//        if (params.id) {
//            cronogramaEjecucionInstance = CronogramaEjecucion.get(params.id)
//            if (!cronogramaEjecucionInstance) {
//                flash.clase = "alert-error"
//                flash.message = "No se encontró Cronograma Ejecucion con id " + params.id
//                redirect(action: "list")
//                return
//            } //no existe el objeto
//        } //es edit
//        return [cronogramaEjecucionInstance: cronogramaEjecucionInstance]
//    } //form_ajax
//
//    def save() {
//        def cronogramaEjecucionInstance
//        if (params.id) {
//            cronogramaEjecucionInstance = CronogramaEjecucion.get(params.id)
//            if (!cronogramaEjecucionInstance) {
//                flash.clase = "alert-error"
//                flash.message = "No se encontró Cronograma Ejecucion con id " + params.id
//                redirect(action: 'list')
//                return
//            }//no existe el objeto
//            cronogramaEjecucionInstance.properties = params
//        }//es edit
//        else {
//            cronogramaEjecucionInstance = new CronogramaEjecucion(params)
//        } //es create
//        if (!cronogramaEjecucionInstance.save(flush: true)) {
//            flash.clase = "alert-error"
//            def str = "<h4>No se pudo guardar Cronograma Ejecucion " + (cronogramaEjecucionInstance.id ? cronogramaEjecucionInstance.id : "") + "</h4>"
//
//            str += "<ul>"
//            cronogramaEjecucionInstance.errors.allErrors.each { err ->
//                def msg = err.defaultMessage
//                err.arguments.eachWithIndex { arg, i ->
//                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
//                }
//                str += "<li>" + msg + "</li>"
//            }
//            str += "</ul>"
//
//            flash.message = str
//            redirect(action: 'list')
//            return
//        }
//
//        if (params.id) {
//            flash.clase = "alert-success"
//            flash.message = "Se ha actualizado correctamente Cronograma Ejecucion " + cronogramaEjecucionInstance.id
//        } else {
//            flash.clase = "alert-success"
//            flash.message = "Se ha creado correctamente Cronograma Ejecucion " + cronogramaEjecucionInstance.id
//        }
//        redirect(action: 'list')
//    } //save
//
//    def show_ajax() {
//        def cronogramaEjecucionInstance = CronogramaEjecucion.get(params.id)
//        if (!cronogramaEjecucionInstance) {
//            flash.clase = "alert-error"
//            flash.message = "No se encontró Cronograma Ejecucion con id " + params.id
//            redirect(action: "list")
//            return
//        }
//        [cronogramaEjecucionInstance: cronogramaEjecucionInstance]
//    } //show
//
//    def delete() {
//        def cronogramaEjecucionInstance = CronogramaEjecucion.get(params.id)
//        if (!cronogramaEjecucionInstance) {
//            flash.clase = "alert-error"
//            flash.message = "No se encontró Cronograma Ejecucion con id " + params.id
//            redirect(action: "list")
//            return
//        }
//
//        try {
//            cronogramaEjecucionInstance.delete(flush: true)
//            flash.clase = "alert-success"
//            flash.message = "Se ha eliminado correctamente Cronograma Ejecucion " + cronogramaEjecucionInstance.id
//            redirect(action: "list")
//        }
//        catch (DataIntegrityViolationException e) {
//            flash.clase = "alert-error"
//            flash.message = "No se pudo eliminar Cronograma Ejecucion " + (cronogramaEjecucionInstance.id ? cronogramaEjecucionInstance.id : "")
//            redirect(action: "list")
//        }
//    } //delete
} //fin controller
