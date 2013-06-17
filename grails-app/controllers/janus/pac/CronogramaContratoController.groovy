package janus.pac

import janus.Contrato
import janus.Cronograma
import janus.Obra
import janus.VolumenesObra
import org.springframework.dao.DataIntegrityViolationException

class CronogramaContratoController extends janus.seguridad.Shield {

    def preciosService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {

//        if (!params.id) {
//            params.id = "5"
//        }

        def contrato = Contrato.get(params.id)
        if (!contrato) {
            flash.message = "No se encontró el contrato"
            flash.clase = "alert-error"
            redirect(controller: 'contrato', action: "registroContrato", params: [contrato: params.id])
            return
        }
        def obra = contrato?.oferta?.concurso?.obra
        if (!obra) {
            flash.message = "No se encontró la obra"
            flash.clase = "alert-error"
            redirect(controller: 'contrato', action: "registroContrato", params: [contrato: params.id])
            return
        }

        //copia el cronograma de la obra a la tabla cronograma contrato (crng)
        /**
         * TODO: esto hay q cambiar cuando haya el modulo de oferente ganador:
         *  no se copia el cronograma de la obra sino del oferente ganador
         */

        //solo copia si esta vacio el cronograma del contrato
        def cronoCntr = CronogramaContrato.countByContrato(contrato)
        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])

        def plazoDiasContrato = contrato.plazo
        def plazoMesesContrato = Math.ceil(plazoDiasContrato / 30);

        def plazoObra = obra.plazoEjecucionMeses + (obra.plazoEjecucionDias > 0 ? 1 : 0)

//        println plazoDiasContrato + "/30 = " + plazoMesesContrato
//        println "plazoMesesContrato: " + plazoMesesContrato + "    plazoObra: " + plazoObra

        if (cronoCntr == 0) {
            detalle.each { vol ->
//            def resto = 100
                def c = Cronograma.findAllByVolumenObra(vol)
                def resto = c.sum { it.porcentaje }
                c.eachWithIndex { crono, cont ->
                    if (cont < plazoMesesContrato) {
                        if (CronogramaContrato.countByVolumenObraAndPeriodo(crono.volumenObra, crono.periodo) == 0) {
                            def cronoContrato = new CronogramaContrato()
                            cronoContrato.properties = crono.properties
                            def pf, cf, df
//                        println "resto... " + resto
                            if (cont < c.size() - 1) {
                                pf = Math.floor(crono.porcentaje)
                                resto -= pf
                            } else {
                                pf = resto
                                resto -= pf
                            }
//                        println "resto... " + resto
                            cf = (pf * cronoContrato.cantidad) / crono.porcentaje
                            df = (pf * cronoContrato.precio) / crono.porcentaje

                            cronoContrato.porcentaje = pf
                            cronoContrato.cantidad = cf
                            cronoContrato.precio = df

//                        println "arreglando los decimales:::::"
//                        println "porcentaje: " + crono.porcentaje + " --> " + cronoContrato.porcentaje
//                        println "cantidad: " + crono.cantidad + " --> " + cronoContrato.cantidad
//                        println "precio: " + crono.precio + " --> " + cronoContrato.precio

                            cronoContrato.contrato = contrato

                            if (!cronoContrato.save(flush: true)) {
                                println "Error al guardar el crono contrato del crono " + crono.id
                                println cronoContrato.errors
                            }/* else {
                    println "ok " + crono.id + "  =>  " + cronoContrato.id

                }*/
                        } else {
//                        println "no guarda, solo actualiza el porcentaje"
//                        println "resto... " + resto
                            def pf = Math.floor(crono.porcentaje)
                            resto -= pf
//                        println "resto... " + resto
                        }
                    }
                }
            }
            if (plazoMesesContrato > plazoObra) {
//                println ">>>AQUI"
                ((plazoObra + 1)..plazoMesesContrato).each { extra ->
                    detalle.each { vol ->
                        def cronoContrato = new CronogramaContrato([
                                contrato: contrato,
                                volumenObra: vol,
                                periodo: extra,
                                precio: 0,
                                porcentaje: 0,
                                cantidad: 0,
                        ])
                        if (!cronoContrato.save(flush: true)) {
                            println "Error al guardar el crono contrato extra " + extra
                            println cronoContrato.errors
                        }
                    }
                }
            }
        }

        def precios = [:]
        def indirecto = obra.totales / 100

        preciosService.ac_rbroObra(obra.id)

        detalle.each {
            def res = preciosService.presioUnitarioVolumenObra("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
        }

        return [detalle: detalle, precios: precios, obra: obra, contrato: contrato]

    } //index

    def saveCrono_ajax() {
//        println ">>>>>>>>>>>>>>>>>"
//        println params
        def saved = ""
        def ok = ""
        if (params.crono.class == java.lang.String) {
            params.crono = [params.crono]
        }
        params.crono.each { str ->
            def parts = str.split("_")
//            println parts
            def per = parts[1].toString().toInteger()
            def vol = VolumenesObra.get(parts[0].toString().toLong())
            /*
            VolumenesObra volumenObra
            Integer periodo
            Double precio
            Double porcentaje
            Double cantidad
             */
            def cont = true
            def crono = CronogramaContrato.findAllByVolumenObraAndPeriodo(vol, per)
            if (crono.size() == 1) {
                crono = crono[0]
            } else if (crono.size() == 0) {
                crono = new CronogramaContrato()
            } else {
                println "WTF MAS DE UN CRONOGRAMA volumen obra " + vol.id + " periodo " + per + " hay " + crono.size()
                cont = false
            }

            if (cont) {
                crono.volumenObra = vol
                crono.periodo = per
                crono.precio = parts[2].toString().toDouble()
                crono.porcentaje = parts[3].toString().toDouble()
                crono.cantidad = parts[4].toString().toDouble()
                if (crono.save(flush: true)) {
                    saved += parts[1] + ":" + crono.id + ";"
                    ok = "OK"
                } else {
                    ok = "NO"
                    println crono.errors
                }
            }
        }
        render ok + "_" + saved
    }

    def deleteRubro_ajax() {
        def ok = 0, no = 0
        def vol = VolumenesObra.get(params.id)
        CronogramaContrato.findAllByVolumenObra(vol).each { cr ->
            try {
                cr.delete(flush: true)
                ok++
            } catch (DataIntegrityViolationException e) {
                no++
            }
        }
        render "ok:" + ok + "_no:" + no
    }

    def deleteCronograma_ajax() {
        def ok = 0, no = 0
        def obra = Obra.get(params.obra)
        VolumenesObra.findAllByObra(obra, [sort: "orden"]).each { vo ->
            CronogramaContrato.findAllByVolumenObra(vo).each { cr ->
                try {
                    cr.delete(flush: true)
                    ok++
                } catch (DataIntegrityViolationException e) {
                    no++
                }
            }

        }
        render "ok:" + ok + "_no:" + no
    }

    def graficos2() {
        params.each {
//            println it
        }
        def obra = Obra.get(params.obra)
        def contrato = Contrato.get(params.contrato)
        return [params: params, contrato: contrato, obra: obra]
    }

//    def list() {
//        [cronogramaContratoInstanceList: CronogramaContrato.list(params), params: params]
//    } //list
//
//    def form_ajax() {
//        def cronogramaContratoInstance = new CronogramaContrato(params)
//        if (params.id) {
//            cronogramaContratoInstance = CronogramaContrato.get(params.id)
//            if (!cronogramaContratoInstance) {
//                flash.clase = "alert-error"
//                flash.message = "No se encontró Cronograma Contrato con id " + params.id
//                redirect(action: "list")
//                return
//            } //no existe el objeto
//        } //es edit
//        return [cronogramaContratoInstance: cronogramaContratoInstance]
//    } //form_ajax
//
//    def save() {
//        def cronogramaContratoInstance
//        if (params.id) {
//            cronogramaContratoInstance = CronogramaContrato.get(params.id)
//            if (!cronogramaContratoInstance) {
//                flash.clase = "alert-error"
//                flash.message = "No se encontró Cronograma Contrato con id " + params.id
//                redirect(action: 'list')
//                return
//            }//no existe el objeto
//            cronogramaContratoInstance.properties = params
//        }//es edit
//        else {
//            cronogramaContratoInstance = new CronogramaContrato(params)
//        } //es create
//        if (!cronogramaContratoInstance.save(flush: true)) {
//            flash.clase = "alert-error"
//            def str = "<h4>No se pudo guardar Cronograma Contrato " + (cronogramaContratoInstance.id ? cronogramaContratoInstance.id : "") + "</h4>"
//
//            str += "<ul>"
//            cronogramaContratoInstance.errors.allErrors.each { err ->
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
//            flash.message = "Se ha actualizado correctamente Cronograma Contrato " + cronogramaContratoInstance.id
//        } else {
//            flash.clase = "alert-success"
//            flash.message = "Se ha creado correctamente Cronograma Contrato " + cronogramaContratoInstance.id
//        }
//        redirect(action: 'list')
//    } //save
//
//    def show_ajax() {
//        def cronogramaContratoInstance = CronogramaContrato.get(params.id)
//        if (!cronogramaContratoInstance) {
//            flash.clase = "alert-error"
//            flash.message = "No se encontró Cronograma Contrato con id " + params.id
//            redirect(action: "list")
//            return
//        }
//        [cronogramaContratoInstance: cronogramaContratoInstance]
//    } //show
//
//    def delete() {
//        def cronogramaContratoInstance = CronogramaContrato.get(params.id)
//        if (!cronogramaContratoInstance) {
//            flash.clase = "alert-error"
//            flash.message = "No se encontró Cronograma Contrato con id " + params.id
//            redirect(action: "list")
//            return
//        }
//
//        try {
//            cronogramaContratoInstance.delete(flush: true)
//            flash.clase = "alert-success"
//            flash.message = "Se ha eliminado correctamente Cronograma Contrato " + cronogramaContratoInstance.id
//            redirect(action: "list")
//        }
//        catch (DataIntegrityViolationException e) {
//            flash.clase = "alert-error"
//            flash.message = "No se pudo eliminar Cronograma Contrato " + (cronogramaContratoInstance.id ? cronogramaContratoInstance.id : "")
//            redirect(action: "list")
//        }
//    } //delete
} //fin controller
