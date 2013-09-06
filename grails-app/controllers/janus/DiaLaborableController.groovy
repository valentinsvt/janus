package janus

import groovy.json.JsonBuilder
import org.springframework.dao.DataIntegrityViolationException

class DiaLaborableController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def diasLaborablesService

    def calculador() {

    }

    def calcEntre() {
        def fecha1 = new Date().parse("dd-MM-yyyy", params.fecha1)
        def fecha2 = new Date().parse("dd-MM-yyyy", params.fecha2)

        def ret = diasLaborablesService.diasLaborablesEntre(fecha1, fecha2)
        def json = new JsonBuilder(ret)
        render json
    }

    def calcDias() {
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        def dias = params.dias.toInteger()

        def ret = diasLaborablesService.diasLaborablesDesde(fecha, dias)
        def json = new JsonBuilder(ret)
        render json
    }

    def saveCalendario() {
        def errores = 0
        params.dia.each { dia ->
            def parts = dia.split(":")
            if (parts.size() == 3) {
                def id = parts[0].toLong()
                def fecha = new Date().parse("dd-MM-yyyy", parts[1])
                def cont = parts[2].toInteger()
//                println id + "     " + fecha.format("dd-MM-yyyy") + "    " + cont
                def diaLaborable = DiaLaborable.get(id)
                if (diaLaborable.fecha == fecha && cont != diaLaborable.ordinal) {
                    diaLaborable.ordinal = cont
                    if (!diaLaborable.save(flush: true)) {
                        errores++
                        println "error al guardar dia laborable ${id}: " + diaLaborable.errors
                    } /*else {
                        println "saved ${id}"
                    }*/
                }
            }
        }
        if (errores == 0) {
            render "OK"
        } else {
            render "NO_Ha${errores == 1 ? '' : 'n'} ocurrido ${errores} error${errores == 1 ? '' : 'es'}"
        }
    }

    def calendario() {
        def anio = new Date().format('yyyy').toInteger()

        if (!params.anio) {
            params.anio = anio
        }
        def meses = ["", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"]
        def enero01 = new Date().parse("dd-MM-yyyy", "01-01-" + params.anio)
        def diciembre31 = new Date().parse("dd-MM-yyyy", "31-12-" + params.anio)

        def dias = DiaLaborable.withCriteria {
            ge("fecha", enero01)
            le("fecha", diciembre31)
            order("fecha", "asc")
        }

        if (dias.size() < 365) {
            println "No hay todos los dias para ${params.anio}: hay " + dias.size()

            def fecha = enero01
            def cont = 1
            def fds = ["sat", "sun"]
            def fmt = new java.text.SimpleDateFormat("EEE", new Locale("en"))

            def diasSem = [
                    "mon": 1,
                    "tue": 2,
                    "wed": 3,
                    "thu": 4,
                    "fri": 5,
                    "sat": 6,
                    "sun": 0,
            ]

            while (fecha <= diciembre31) {
                def dia = fmt.format(fecha).toLowerCase()
                def ordinal = 0
                if (!fds.contains(dia)) {
                    ordinal = cont
                    cont++
                }
                def diaExiste = DiaLaborable.withCriteria {
                    eq("fecha", fecha)
                }
                if (!diaExiste) {
                    def diaLaborable = new DiaLaborable([
                            fecha: fecha,
                            dia: diasSem[dia],
                            anio: fecha.format("yyyy").toInteger(),
                            ordinal: ordinal
                    ])
                    if (!diaLaborable.save(flush: true)) {
                        println "error al guardar el dia laborable ${fecha.format('dd-MM-yyyy')}: " + diaLaborable.errors
                    } else {
//                    println "guardado: " + fecha.format("dd-MM-yyyy") + "   " + dia + " ordinal:" + ordinal
                    }
                }
                fecha++
            }
            dias = DiaLaborable.withCriteria {
                ge("fecha", enero01)
                le("fecha", diciembre31)
                order("fecha", "asc")
            }
            println "Guardados ${dias.size()} dias"
        }

        return [anio: anio, dias: dias, meses: meses, params: params]
    }

    def index() {
        redirect(action: "calendario", params: params)
    } //index

    def list() {
        [diaLaborableInstanceList: DiaLaborable.list(params), params: params]
    } //list

    def form_ajax() {
        def diaLaborableInstance = new DiaLaborable(params)
        if (params.id) {
            diaLaborableInstance = DiaLaborable.get(params.id)
            if (!diaLaborableInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Dia Laborable con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [diaLaborableInstance: diaLaborableInstance]
    } //form_ajax

    def save() {
        def diaLaborableInstance
        if (params.id) {
            diaLaborableInstance = DiaLaborable.get(params.id)
            if (!diaLaborableInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Dia Laborable con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            diaLaborableInstance.properties = params
        }//es edit
        else {
            diaLaborableInstance = new DiaLaborable(params)
        } //es create
        if (!diaLaborableInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Dia Laborable " + (diaLaborableInstance.id ? diaLaborableInstance.id : "") + "</h4>"

            str += "<ul>"
            diaLaborableInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Dia Laborable " + diaLaborableInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Dia Laborable " + diaLaborableInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def diaLaborableInstance = DiaLaborable.get(params.id)
        if (!diaLaborableInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Dia Laborable con id " + params.id
            redirect(action: "list")
            return
        }
        [diaLaborableInstance: diaLaborableInstance]
    } //show

    def delete() {
        def diaLaborableInstance = DiaLaborable.get(params.id)
        if (!diaLaborableInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Dia Laborable con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            diaLaborableInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Dia Laborable " + diaLaborableInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Dia Laborable " + (diaLaborableInstance.id ? diaLaborableInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
