package janus

import org.springframework.dao.DataIntegrityViolationException

class CronogramaController extends janus.seguridad.Shield {

    def preciosService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def saveCrono_ajax() {
        println ">>>>>>>>>>>>>>>>>"
        println params
        def saved = ""
        def ok = ""
        if (params.crono.class == java.lang.String) {
            params.crono = [params.crono]
        }
        params.crono.each { str ->
            def parts = str.split("_")
            println parts
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
            def crono = Cronograma.findAllByVolumenObraAndPeriodo(vol, per)
            if (crono.size() == 1) {
                crono = crono[0]
            } else if (crono.size() == 0) {
                crono = new Cronograma()
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

    def deleteCronograma_ajax() {
        def ok = 0, no = 0
        def obra = Obra.get(params.obra)
        VolumenesObra.findAllByObra(obra, [sort: "orden"]).each { vo ->
            Cronograma.findAllByVolumenObra(vo).each { cr ->
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

    def graficos() {

    }

    def cronogramaObra() {
        def obra = Obra.get(params.id)

        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])

        def precios = [:]
        def fecha = obra.fechaPreciosRubros
        def dsps = obra.distanciaPeso
        def dsvl = obra.distanciaVolumen
        def lugar = obra.lugar
        def prch = 0
        def prvl = 0
        if (obra.chofer) {
            prch = preciosService.getPrecioItems(fecha, lugar, [obra.chofer])
            prch = prch["${obra.chofer.id}"]
            prvl = preciosService.getPrecioItems(fecha, lugar, [obra.volquete])
            prvl = prvl["${obra.volquete.id}"]
        }
//        println "PARAMETROS!= "+fecha+" "+dsps+" "+dsvl+" "+lugar+" "+obra.chofer+ " "+obra.volquete+" "+prch+" "+prvl
        def rendimientos = preciosService.rendimientoTranposrte(dsps, dsvl, prch, prvl)
//        println "rends "+rendimientos
        if (rendimientos["rdps"].toString() == "NaN")
            rendimientos["rdps"] = 0
        if (rendimientos["rdvl"].toString() == "NaN")
            rendimientos["rdvl"] = 0
        def indirecto = obra.indiceCostosIndirectosCostosFinancieros + obra.indiceCostosIndirectosGarantias + obra.indiceCostosIndirectosMantenimiento + obra.indiceCostosIndirectosObra + obra.indiceCostosIndirectosTimbresProvinciales + obra.indiceCostosIndirectosVehiculos
//        println "indirecto "+indirecto

        detalle.each {
            def parametros = "" + it.item.id + "," + lugar.id + ",'" + fecha.format("yyyy-MM-dd") + "'," + dsps.toDouble() + "," + dsvl.toDouble() + "," + rendimientos["rdps"] + "," + rendimientos["rdvl"]
            def res = preciosService.rb_precios("sum(parcial)+sum(parcial_t) precio ", parametros, "")
            precios.put(it.id.toString(), res["precio"][0] + res["precio"][0] * indirecto)
        }
//
//        println "precios "+precios


        [detalle: detalle, precios: precios, obra: obra]
    }

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [cronogramaInstanceList: Cronograma.list(params), cronogramaInstanceTotal: Cronograma.count(), params: params]
    } //list

    def form_ajax() {
        def cronogramaInstance = new Cronograma(params)
        if (params.id) {
            cronogramaInstance = Cronograma.get(params.id)
            if (!cronogramaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Cronograma con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [cronogramaInstance: cronogramaInstance]
    } //form_ajax

    def save() {
        def cronogramaInstance
        if (params.id) {
            cronogramaInstance = Cronograma.get(params.id)
            if (!cronogramaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Cronograma con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            cronogramaInstance.properties = params
        }//es edit
        else {
            cronogramaInstance = new Cronograma(params)
        } //es create
        if (!cronogramaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Cronograma " + (cronogramaInstance.id ? cronogramaInstance.id : "") + "</h4>"

            str += "<ul>"
            cronogramaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamete Cronograma " + cronogramaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamete Cronograma " + cronogramaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def cronogramaInstance = Cronograma.get(params.id)
        if (!cronogramaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Cronograma con id " + params.id
            redirect(action: "list")
            return
        }
        [cronogramaInstance: cronogramaInstance]
    } //show

    def delete() {
        def cronogramaInstance = Cronograma.get(params.id)
        if (!cronogramaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Cronograma con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            cronogramaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamete Cronograma " + cronogramaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Cronograma " + (cronogramaInstance.id ? cronogramaInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
