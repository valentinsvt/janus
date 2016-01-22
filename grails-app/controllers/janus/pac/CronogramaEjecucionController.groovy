package janus.pac

import groovy.time.TimeCategory
import janus.Contrato
import janus.Modificaciones
import janus.Obra
import janus.VolumenesObra
import janus.ejecucion.MultasPlanilla
//import janus.ejecucion.PeriodoEjecucionMes
import janus.ejecucion.PeriodoPlanilla
import janus.ejecucion.Planilla
import janus.ejecucion.TipoPlanilla

import javax.management.Query

class CronogramaEjecucionController extends janus.seguridad.Shield {

    def preciosService
    def arreglosService
    def dbConnectionService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def fixCrono() {
        def obra = Obra.get(params.id)
        def res = arreglosService.fixCronoEjecucion(obra)
        render res
    }

    def errores() {

    }

//    def delSuspension() {
//        def susp = PeriodoEjecucion.get(params.id)
//    }

    def cambioFecha_ajax() {
        def obra = Obra.get(params.obra)
        def periodoFinal = PeriodoEjecucion.findByObra(obra, [sort: 'fechaInicio', order: 'desc'])

        def minDate = periodoFinal.fechaInicio.format("yyyy") + "," + (periodoFinal.fechaInicio.format("MM").toInteger() - 1) + "," + (periodoFinal.fechaInicio.format("dd").toInteger() + 1)
        def maxDate = periodoFinal.fechaFin.format("yyyy") + "," + (periodoFinal.fechaFin.format("MM").toInteger() - 1) + "," + periodoFinal.fechaFin.format("dd")

        return [min: minDate, max: maxDate]
    }

    def cambioFecha() {
        def obra = Obra.get(params.obra)
        def periodoFinal = PeriodoEjecucion.findByObra(obra, [sort: 'fechaInicio', order: 'desc'])
        def fechaFin = new Date().parse("dd-MM-yyyy", params.fecha)

        periodoFinal.fechaFin = fechaFin
        if (!periodoFinal.save(flush: true)) {
            render "NO"
        } else {
            render "OK"
        }
    }


    def suspension_ajax() {
        def obra = Obra.get(params.obra)
        def min = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaInicio'])[0].fechaInicio
        def minDate = min.format("yyyy") + "," + (min.format("MM").toInteger() - 1) + "," + min.format("dd")
        return [min: minDate]
    }


    def terminaSuspension_ajax() {
        def obra = Obra.get(params.obra)
        def fi = Modificaciones.withCriteria {
            eq("obra", obra)
            eq("tipo", "S")
            isNull("fechaFin")
            projections {
                max("fechaInicio")
            }
        }
        def suspension = "Suspensión iniciada el ${fi.first().format('dd-MM-yyyy'.toString())}"
        def minDate = fi.first().format("yyyy") + "," + (fi.first().format("MM").toInteger() - 1) + "," + fi.first().format("dd")

        return [min: minDate, suspension: suspension]
    }

    /* Agregado el 25-03-2015 xq quieren hacer suspensiones sin fecha de fin: se divide el proceso en 2 partes:
            primero hace una modificacion tipo suspension sin fecha de fin y con -1 en dias
            segundo el action terminaSuspension modifica la modificacino para ponerle fecha fin y dias y recalcula las fechas de periodos del cronograma
     */

    def suspensionNueva() {
        println "suspensiones, params: $params"
        def cntr = Contrato.get(params.cntr)
        def obra = cntr.obra
        def periodos = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaInicio'])
        def fin = null
        def finSusp = null
        def dias = -1

        def ini = new Date().parse("dd-MM-yyyy", params.ini)

        def plnl = Planilla.findAllByContratoAndFechaFinGreaterThan(cntr, ini, [sort: 'fechaInicio'])
        println "planillas: ${plnl.fechaInicio} - ${plnl.fechaFin}"


        if (plnl.size() > 0) {
            flash.message = "La fecha ingresada corresponde a un periodo planillado con la planilla: ${plnl.numero} del " +
                    "periodo: " + plnl[0].fechaInicio.format("dd-MM-yyyy") + " al " + plnl[0].fechaFin.format("dd-MM-yyyy")
            render "Error"
        } else {

            if (params.fin) {
                fin = new Date().parse("dd-MM-yyyy", params.fin)
                finSusp = fin
                dias = finSusp - ini + 1
            }

            def modificacion = new Modificaciones([
                    contrato     : cntr,
                    obra         : obra,
                    tipo         : "S",
                    dias         : dias,
                    fecha        : new Date(),
                    fechaInicio  : ini,
                    fechaFin     : finSusp,
                    motivo       : params.motivo,
                    observaciones: params.observaciones,
                    memo         : params.memo.toUpperCase()
            ])

            if (!modificacion.save(flush: true)) {
                println "error modificacion: " + modificacion.errors
                render "Error"
            } else {
                println "modificacion $modificacion.id ini: $modificacion.fechaInicio fin: $modificacion.fechaFin dias: $modificacion.dias"
                if (finSusp) { /** si existe fecha de finaliazazcion **/
                    params.cntr = cntr.id
                    params.suspension = modificacion.id
                    params.fcfn = finSusp.format("dd-MM-yyyy")
                    println "registra suspesión e invoca a terminaSuspensionTemp con $params.fcfn"
                    terminaSuspensionTemp()
                }
                render "OK"
            }
        }
    }

    /* Suspensiones sin fecha de fin: se divide el proceso en 2 partes:
       1. action suspensionNueva: hace una modificacion tipo suspension sin fecha de fin y con -1 en dias
       2. terminaSuspension: pone fecha fin y dias a la suspensión y recalcula las fechas de periodos del cronograma
     */

    def terminaSuspension() {     /** no se usa **/
        println "termina suspension params: $params"
        def cntr = Contrato.get(params.cntr)
        def obra = cntr.obra
        def periodos = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaInicio'])

        def suspension = Modificaciones.findAllByContratoAndTipoAndFechaFinIsNull(cntr, "S")

        println "suspensión: ${suspension.size()}, inicio en ${suspension[0].fechaInicio.format('dd-MMM-yyyy'.toString())}"

        def fin = new Date().parse("dd-MM-yyyy", params.fin)

        def finSusp = fin
        use(TimeCategory) {
            finSusp = fin - 1.days
        }
        def errores = ""

        if (suspension.size() > 1) {
            return "Error... existe mas de una suspension en curso"
        } else {
            def dias = finSusp - suspension[0].fechaInicio + 1
            suspension[0].dias = dias
            suspension[0].fechaFin = finSusp
            if (params.observaciones && params.observaciones.trim() != "") {
                suspension[0].observaciones = params.observaciones + "       " + suspension[0].observaciones
            }
/*
            if (!suspension.save(flush: true)) {
                errores += renderErrors(bean: suspension)
                println "EEOR EN TEMINAR SUSSPENSION: " + suspension.errors
            }
*/
        }

        println "Fin de suspensión actualizada a: ${suspension[0].fechaFin}, dias: ${suspension[0].dias}"
        //1ro cambia fecha fin y cant dias para todas las suspensiones

        //2do recalcula tdod lo q hacia antes la suspension
        def num = 1

        def anterior = null
        def moved = false

        periodos.eachWithIndex { PeriodoEjecucion per, int i ->
//            println per
            def nuevoIni, nuevoFin
            if (per.fechaInicio <= ini && per.fechaFin >= ini) {
//                println "\tEste es el q hay q dividir en 2 partes"
//                println "\t" + per.fechaInicio + " " + per.fechaFin

                def dias1 = ini - per.fechaInicio

                //crea el periodo de tipo suspension con fecha inicio y fecha fin
                def suspen = new PeriodoEjecucion([
                        obra       : obra,
                        numero     : num,
                        tipo       : "S",
                        fechaInicio: ini,
                        fechaFin   : finSusp
                ])
/*
                if (!suspen.save(flush: true)) {
                    println "Error al guardar la suspension: " + suspension.errors
                }
*/

//                println "\t\tPARTE 1"
//                println "\t\tdias: " + dias1
                if (dias1 == 0) {
//                    println "\t\tson 0 dias de diferencia: la suspension es antes de q empiece la obra: se mueven las fechas y no se divide en 2"
                    def diasPeriodo = per.fechaFin - per.fechaInicio
                    nuevoIni = fin
                    use(TimeCategory) {
                        nuevoFin = nuevoIni + diasPeriodo.days
                    }

                    per.fechaInicio = nuevoIni
                    per.fechaFin = nuevoFin
/*
                    if (!per.save(flush: true)) {
                        println "error 1: " + per.errors
                    }
*/
//                    println "\t\tSUSPENSION: "
//                    println "\t\t inicio: " + ini
//                    println "\t\t fin: " + finSusp
//                    println "\t\tPERIODO:"
//                    println "\t\t inicio: " + nuevoIni
//                    println "\t\t fin: " + nuevoFin
                    anterior = nuevoFin

                } else {
                    // numero de dias que tiene el periodo: este numero tiene que quedarse uigual, pero separado
                    def diasPeriodo = per.fechaFin - per.fechaInicio
//                    println "\t\tes al menos 1 dia de diferencia: se divide el periodo en 2, con la suspension en medio"

                    // primera parte: la fecha de inicio se queda igual, la fecha de fin es un dia antes de la suspension. se calculan los dias para dividir los valores proporcionalemente
//                    println "\t\t\tPARTE 1:"
                    nuevoIni = per.fechaInicio
                    use(TimeCategory) {
                        nuevoFin = ini - 1.days
                    }
                    def diasParte1 = nuevoFin - nuevoIni
//                    println "\t\t inicio: " + nuevoIni
//                    println "\t\t fin: " + nuevoFin
//                    println "\t\t dias: " + diasParte1

                    per.fechaInicio = nuevoIni
                    per.fechaFin = nuevoFin
/*
                    if (!per.save(flush: true)) {
                        println "error 2: " + per.errors
                    }
*/

                    // aqui va la suspension
//                    println "\t\t\tSUSPENSION: "
//                    println "\t\t inicio: " + ini
//                    println "\t\t fin: " + finSusp

                    def diasParte2 = diasPeriodo - diasParte1

                    // segunda parte: la fecha de inicio es la del fin de la suspension, para la de fin se suman los dias q le faltan
//                    println "\t\t\tPARTE 2: "
                    def nuevoIni2 = fin
                    def nuevoFin2
                    use(TimeCategory) {
                        nuevoFin2 = nuevoIni2 + diasParte2.days
                    }
//                    println "\t\t inicio: " + nuevoIni2
//                    println "\t\t fin: " + nuevoFin2
//                    println "\t\t dias: " + diasParte2

//                    println "\t\t\tTOTAL: " + (diasParte1 + diasParte2)
                    anterior = nuevoFin2

                    //crea el periodo de tipo periodo con fecha inicio y fecha fin: la otra parte del periodo recortado
                    def periodo2 = new PeriodoEjecucion([
                            obra       : obra,
                            numero     : per.numero,
                            tipo       : "P",
                            fechaInicio: nuevoIni2,
                            fechaFin   : nuevoFin2
                    ])
/*
                    if (!periodo2.save(flush: true)) {
                        println "Error al guardar el periodo2: " + periodo2.errors
                    }
*/

//                    println "CRONOGRAMAS AFECTADOS POR LA DIVISION"
                    // itera sobre los cronogramaEjecucion afectados por per: hay q dividir proporcionalmente los valoresy crear otro cronogramaEjecucion con la otra parte
                    CronogramaEjecucion.findAllByPeriodo(per).eachWithIndex { CronogramaEjecucion crono, int j ->
//                        println crono
//                        println "   ..original.."
//                        println "   cantidad: " + crono.cantidad
//                        println "   porcentaje: " + crono.porcentaje
//                        println "   precio: " + crono.precio
//                        println "   en " + diasPeriodo + " dias"

                        def cantidad1, cantidad2, porcentaje1, porcentaje2, precio1, precio2
                        cantidad1 = (crono.cantidad * diasParte1) / diasPeriodo
                        cantidad2 = (crono.cantidad * diasParte2) / diasPeriodo
                        porcentaje1 = (crono.porcentaje * diasParte1) / diasPeriodo
                        porcentaje2 = (crono.porcentaje * diasParte2) / diasPeriodo
                        precio1 = (crono.precio * diasParte1) / diasPeriodo
                        precio2 = (crono.precio * diasParte2) / diasPeriodo

                        crono.cantidad = cantidad1
                        crono.porcentaje = porcentaje1
                        crono.precio = precio1
/*
                        if (!crono.save(flush: true)) {
                            println "error 3: " + crono.errors
                        }
*/

                        def crono2 = new CronogramaEjecucion([
                                volumenObra: crono.volumenObra,
                                periodo    : periodo2,
                                precio     : precio2,
                                porcentaje : porcentaje2,
                                cantidad   : cantidad2
                        ])
/*
                        if (!crono2.save(flush: true)) {
                            println "error 4: " + crono2.errors
                        }
*/

                    }

                }
                moved = true
            } else {
                if (!moved) {
                    if (per.tipo == "S") {
                        num++
                    }
                }
                if (moved) {
                    //                    println "\tEste solo se recorren las fechas de inicio y de fin"
                    def diasPeriodo = per.fechaFin - per.fechaInicio
//                    println "......" + anterior
                    use(TimeCategory) {
                        nuevoIni = anterior + 1.days
                    }
                    use(TimeCategory) {
                        nuevoFin = nuevoIni + diasPeriodo.days
                    }

                    per.fechaInicio = nuevoIni
                    per.fechaFin = nuevoFin
/*
                    if (!per.save(flush: true)) {
                        println "error 1: " + per.errors
                    }
*/
//                    println "\t de " + per.fechaInicio + " " + per.fechaFin
//                    println "\t a " + nuevoIni + " " + nuevoFin
                    anterior = nuevoFin
                } else {
//                    println "\tEste periodo esta antes de la suspension: no se le hace nada"
                }
            }
//            println "____________________________________________________________________________________"
        }

        render "OK"
    }


    def suspension() {
//        println params

        def obra = Obra.get(params.obra)
        def periodos = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaInicio'])

        def ini = new Date().parse("dd-MM-yyyy", params.ini)
        def fin = new Date().parse("dd-MM-yyyy", params.fin)

        def finSusp = fin
        use(TimeCategory) {
            finSusp = fin - 1.days
        }

        def modificacion = new Modificaciones([
                obra         : obra,
                tipo         : "S",
                dias         : finSusp - ini + 1,
                fecha        : new Date(),
                fechaInicio  : ini,
                fechaFin     : finSusp,
                motivo       : params.motivo,
                observaciones: params.observaciones,
                memo         : params.memo.toUpperCase()
        ])

        if (!modificacion.save(flush: true)) {
            println "error modificacion: " + modificacion.errors
        }

//        println ini
//        println fin
//        println "\n"

        def num = 1

        def anterior = null
        def moved = false

        periodos.eachWithIndex { PeriodoEjecucion per, int i ->
//            println per
            def nuevoIni, nuevoFin
            if (per.fechaInicio <= ini && per.fechaFin >= ini) {
//                println "\tEste es el q hay q dividir en 2 partes"
//                println "\t" + per.fechaInicio + " " + per.fechaFin

                def dias1 = ini - per.fechaInicio

                //crea el periodo de tipo suspension con fecha inicio y fecha fin
                def suspension = new PeriodoEjecucion([
                        obra       : obra,
                        numero     : num,
                        tipo       : "S",
                        fechaInicio: ini,
                        fechaFin   : finSusp
                ])
                if (!suspension.save(flush: true)) {
                    println "Error al guardar la suspension: " + suspension.errors
                }

//                println "\t\tPARTE 1"
//                println "\t\tdias: " + dias1
                if (dias1 == 0) {
//                    println "\t\tson 0 dias de diferencia: la suspension es antes de q empiece la obra: se mueven las fechas y no se divide en 2"
                    def diasPeriodo = per.fechaFin - per.fechaInicio
                    nuevoIni = fin
                    use(TimeCategory) {
                        nuevoFin = nuevoIni + diasPeriodo.days
                    }

                    per.fechaInicio = nuevoIni
                    per.fechaFin = nuevoFin
                    if (!per.save(flush: true)) {
                        println "error 1: " + per.errors
                    }
//                    println "\t\tSUSPENSION: "
//                    println "\t\t inicio: " + ini
//                    println "\t\t fin: " + finSusp
//                    println "\t\tPERIODO:"
//                    println "\t\t inicio: " + nuevoIni
//                    println "\t\t fin: " + nuevoFin
                    anterior = nuevoFin

                } else {
                    // numero de dias que tiene el periodo: este numero tiene que quedarse uigual, pero separado
                    def diasPeriodo = per.fechaFin - per.fechaInicio
//                    println "\t\tes al menos 1 dia de diferencia: se divide el periodo en 2, con la suspension en medio"

                    // primera parte: la fecha de inicio se queda igual, la fecha de fin es un dia antes de la suspension. se calculan los dias para dividir los valores proporcionalemente
//                    println "\t\t\tPARTE 1:"
                    nuevoIni = per.fechaInicio
                    use(TimeCategory) {
                        nuevoFin = ini - 1.days
                    }
                    def diasParte1 = nuevoFin - nuevoIni
//                    println "\t\t inicio: " + nuevoIni
//                    println "\t\t fin: " + nuevoFin
//                    println "\t\t dias: " + diasParte1

                    per.fechaInicio = nuevoIni
                    per.fechaFin = nuevoFin
                    if (!per.save(flush: true)) {
                        println "error 2: " + per.errors
                    }

                    // aqui va la suspension
//                    println "\t\t\tSUSPENSION: "
//                    println "\t\t inicio: " + ini
//                    println "\t\t fin: " + finSusp

                    def diasParte2 = diasPeriodo - diasParte1

                    // segunda parte: la fecha de inicio es la del fin de la suspension, para la de fin se suman los dias q le faltan
//                    println "\t\t\tPARTE 2: "
                    def nuevoIni2 = fin
                    def nuevoFin2
                    use(TimeCategory) {
                        nuevoFin2 = nuevoIni2 + diasParte2.days
                    }
//                    println "\t\t inicio: " + nuevoIni2
//                    println "\t\t fin: " + nuevoFin2
//                    println "\t\t dias: " + diasParte2

//                    println "\t\t\tTOTAL: " + (diasParte1 + diasParte2)
                    anterior = nuevoFin2

                    //crea el periodo de tipo periodo con fecha inicio y fecha fin: la otra parte del periodo recortado
                    def periodo2 = new PeriodoEjecucion([
                            obra       : obra,
                            numero     : per.numero,
                            tipo       : "P",
                            fechaInicio: nuevoIni2,
                            fechaFin   : nuevoFin2
                    ])
                    if (!periodo2.save(flush: true)) {
                        println "Error al guardar el periodo2: " + periodo2.errors
                    }

//                    println "CRONOGRAMAS AFECTADOS POR LA DIVISION"
                    // itera sobre los cronogramaEjecucion afectados por per: hay q dividir proporcionalmente los valoresy crear otro cronogramaEjecucion con la otra parte
                    CronogramaEjecucion.findAllByPeriodo(per).eachWithIndex { CronogramaEjecucion crono, int j ->
//                        println crono
//                        println "   ..original.."
//                        println "   cantidad: " + crono.cantidad
//                        println "   porcentaje: " + crono.porcentaje
//                        println "   precio: " + crono.precio
//                        println "   en " + diasPeriodo + " dias"

                        def cantidad1, cantidad2, porcentaje1, porcentaje2, precio1, precio2
                        cantidad1 = (crono.cantidad * diasParte1) / diasPeriodo
                        cantidad2 = (crono.cantidad * diasParte2) / diasPeriodo
                        porcentaje1 = (crono.porcentaje * diasParte1) / diasPeriodo
                        porcentaje2 = (crono.porcentaje * diasParte2) / diasPeriodo
                        precio1 = (crono.precio * diasParte1) / diasPeriodo
                        precio2 = (crono.precio * diasParte2) / diasPeriodo

                        crono.cantidad = cantidad1
                        crono.porcentaje = porcentaje1
                        crono.precio = precio1
                        if (!crono.save(flush: true)) {
                            println "error 3: " + crono.errors
                        }

                        def crono2 = new CronogramaEjecucion([
                                volumenObra: crono.volumenObra,
                                periodo    : periodo2,
                                precio     : precio2,
                                porcentaje : porcentaje2,
                                cantidad   : cantidad2
                        ])
                        if (!crono2.save(flush: true)) {
                            println "error 4: " + crono2.errors
                        }

//                        println "   >> parte 1 <<"
//                        println "   en " + diasParte1 + " dias"
//                        println "   cantidad: " + cantidad1
//                        println "   porcentaje: " + porcentaje1
//                        println "   precio: " + precio1
//                        println "   >> parte 2 <<"
//                        println "   en " + diasParte2 + " dias"
//                        println "   cantidad: " + cantidad2
//                        println "   porcentaje: " + porcentaje2
//                        println "   precio: " + precio2
//                        println "   == total =="
//                        println "   en " + (diasParte1 + diasParte2) + " dias"
//                        println "   cantidad: " + (cantidad1 + cantidad2)
//                        println "   porcentaje: " + (porcentaje1 + porcentaje2)
//                        println "   precio: " + (precio1 + precio2)
                    }

                }
                moved = true
            } else {
                if (!moved) {
                    if (per.tipo == "S") {
                        num++
                    }
                }
                if (moved) {
                    //                    println "\tEste solo se recorren las fechas de inicio y de fin"
                    def diasPeriodo = per.fechaFin - per.fechaInicio
//                    println "......" + anterior
                    use(TimeCategory) {
                        nuevoIni = anterior + 1.days
                    }
                    use(TimeCategory) {
                        nuevoFin = nuevoIni + diasPeriodo.days
                    }

                    per.fechaInicio = nuevoIni
                    per.fechaFin = nuevoFin
                    if (!per.save(flush: true)) {
                        println "error 1: " + per.errors
                    }
//                    println "\t de " + per.fechaInicio + " " + per.fechaFin
//                    println "\t a " + nuevoIni + " " + nuevoFin
                    anterior = nuevoFin
                } else {
//                    println "\tEste periodo esta antes de la suspension: no se le hace nada"
                }
            }
//            println "____________________________________________________________________________________"
        }

        render "OK"
    }

    def modificacion_ajax() {
        def contrato = Contrato.get(params.contrato.toLong())
        def obra = contrato.obra
        def vol = VolumenesObra.get(params.vol.toLong())

        def html = "", row2 = ""

        def liquidacionReajuste = Planilla.findAllByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo("L"))
        if (liquidacionReajuste.size() > 0) {
            return [msg: "Ya se ha realizado la liquidación del reajuste, ya no puede realizar modificaciones"]
        }

//        def planillas = Planilla.findAllByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo("P"))
        def periodos = PeriodoEjecucion.findAllByContratoAndObra(contrato, obra, [sort: 'fechaInicio'])

        def indirecto = obra.totales / 100

        def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, vol.item.id)
//            precios.put(vol.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
//            println indirecto
//            println res
        def precio = (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2)
        def cronos = [
                codigo  : vol.item.codigo,
                nombre  : vol.item.nombre,
                unidad  : vol.item.unidad.codigo,
                cantidad: vol.cantidad,
                precioU : precio,
                parcial : precio * vol.cantidad,
                volumen : vol
        ]

        html += "<table class='table table-condensed'>"
        html += "<tr>"
        html += "<th>Rubro</th>"
        html += "<td>" + cronos.codigo + " " + cronos.nombre + "</td>"
        html += "<th>Unidad</th>"
        html += "<td>" + cronos.unidad + "</td>"
        html += "</tr>"
        html += "<tr>"
        html += "<th>Cantidad</th>"
        html += "<td>" + numero(cronos.cantidad) + "</td>"
        html += "<th>Unitario</th>"
        html += "<td>" + numero(cronos.precioU) + "</td>"
        html += "</tr>"
        html += "<tr>"
        html += "<th>C. total</th>"
        html += "<td>" + numero(cronos.parcial) + "</td>"
        html += "</tr>"
        html += "</table>"

        html += '<table class="table table-bordered table-condensed table-hover">'
        html += '<thead>'
        html += '<tr>'
        html += '<th rowspan="2" style="width:50px;"></th>'
        html += '<th rowspan="2" style="width:12px;">'
        html += 'T.'
        html += '</th>'
        periodos.eachWithIndex { per, i ->
            html += "<th class='${per.tipo}'>"
            html += formatDate(date: per.fechaInicio, format: "dd-MM-yyyy") + " a " + formatDate(date: per.fechaFin, format: "dd-MM-yyyy")
            html += "</th>"

            row2 += "<th class='${per.tipo}' data-periodo='${per.id}'>"
            row2 += (per.tipo == 'P' ? 'Periodo' : (per.tipo == 'S' ? 'Susp.' : '')) + " " + per.numero
            row2 += " (" + (per.fechaFin - per.fechaInicio + 1) + " días)"
            row2 += "</th>"
        }
        html += '<th rowspan="2">'
        html += 'Total rubro'
        html += '</th>'
        html += "</tr>"

        html += "<tr>"
        html += row2
        html += "</tr>"
        html += "</thead>"

        html += "<tbody>"

        def cantModificable = []

        println "cantModificable0: " + cantModificable

        def filaDol = "", filaPor = "", filaCan = ""
        def totDol = 0, totPor = 0, totCan = 0
        periodos.eachWithIndex { periodo, i ->
            def cronosPer = CronogramaEjecucion.findAllByVolumenObraAndPeriodo(cronos.volumen, periodo)
            filaDol += "<td class='dol num ${periodo.tipo}'>"
            filaPor += "<td class='prct num ${periodo.tipo}'>"
            filaCan += "<td class='fis num ${periodo.tipo}'>"

            cantModificable[i] = [
                    dol  : 0,
                    por  : 0,
                    can  : 0,
                    crono: null
            ]
            println "cantModificable1: " + cantModificable
            if (cronosPer.size() == 1) {
                cronosPer = cronosPer[0]
//                    println cronosPer.id
//                    filaDol += g.formatNumber(number: cronosPer.precio, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec")
                filaDol += numero(cronosPer.precio)
//                    filaPor += g.formatNumber(number: cronosPer.porcentaje, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec")
                filaPor += numero(cronosPer.porcentaje)
//                    filaCan += g.formatNumber(number: cronosPer.cantidad, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec")
                filaCan += numero(cronosPer.cantidad)
                totDol += cronosPer.precio
                totPor += cronosPer.porcentaje
                totCan += cronosPer.cantidad

                cantModificable[i] = [
                        dol  : cronosPer.precio,
                        por  : cronosPer.porcentaje,
                        can  : cronosPer.cantidad,
                        crono: cronosPer.id
                ]
                println "cantModificable2: " + cantModificable
//                totalesDol[i] += cronosPer.precio
            }
            filaDol += "</td>"
            filaPor += "</td>"
            filaCan += "</td>"
        }
        println "cantModificable3: " + cantModificable
//        println cantModificable

        def filaDolPla = "", filaPorPla = "", filaCanPla = ""
        def filaDolMod = "", filaPorMod = "", filaCanMod = ""
        def totDolPla = 0, totPorPla = 0, totCanPla = 0
        def maxDolAcu = 0, maxPrctAcu = 0, maxCanAcu = 0

        periodos.eachWithIndex { periodo, i ->
            filaDolPla += "<td class='dol planilla num ${periodo.tipo}'>"
            filaPorPla += "<td class='prct planilla num ${periodo.tipo}'>"
            filaCanPla += "<td class='fis planilla num ${periodo.tipo}'>"
            filaDolMod += "<td class='dol modificacion num ${periodo.tipo}'>"
            filaPorMod += "<td class='prct modificacion num ${periodo.tipo}'>"
            filaCanMod += "<td class='fis modificacion num ${periodo.tipo}'>"

            def planillasPeriodo = Planilla.withCriteria {
                and {
                    eq("contrato", contrato)
                    or {
                        between("fechaInicio", periodo.fechaInicio, periodo.fechaFin)
                        between("fechaFin", periodo.fechaInicio, periodo.fechaFin)
                    }
                }
            }

            def diasPeriodo = periodo.fechaFin - periodo.fechaInicio + 1
            def cantDia = cronos.cantidad / diasPeriodo
            def totalPlanilla = 0
            def modificable = true
//            println cantModificable[i]
            println "cantModificable4[${i}]: " + cantModificable[i]

/*
            planillasPeriodo.each { pla ->
                println "periodo: " + periodo.fechaInicio.format("dd-MM-yyyy") + " - " + periodo.fechaFin.format("dd-MM-yyyy")
                println "\tplanilla: " + pla.fechaInicio.format("dd-MM-yyyy") + " - " + pla.fechaFin.format("dd-MM-yyyy")
                if (pla.fechaFin >= periodo.fechaFin) {
                    modificable = false
                    cantModificable[i].dol = 0
                    cantModificable[i].por = 0
                    cantModificable[i].can = 0

                }
                println "cantModificable5[${i}]: " + cantModificable[i]
                def diasPlanilla = pla.fechaFin - pla.fechaInicio + 1
                totalPlanilla += (diasPlanilla * cantDia)
            }
*/

//            def porPla = (totalPlanilla * 100) / totDol
            def porPla = totDol == 0 ? 0 : (totalPlanilla * 100) / totDol
            def canPla = (totCan * (porPla / 100))

            filaDolPla += numero(totalPlanilla, 2, "hide")
            filaPorPla += numero(porPla, 2, "hide")
            filaCanPla += numero(canPla, 2, "hide")

            totDolPla += totalPlanilla
            totPorPla += porPla
            totCanPla += canPla

            println "\t\ttotalPlanilla=" + totalPlanilla + "   porPla=" + porPla + "   canPla=" + canPla

            if (modificable) {
                def dol = cantModificable[i].dol - totalPlanilla
                def por = cantModificable[i].por - porPla
                def can = cantModificable[i].can - canPla
                def maxDol = numero(dol.toDouble().round(2))
                def maxPor = numero(por.toDouble().round(2))
                def maxCan = numero(can.toDouble().round(2))

                println "cantModificable6[${i}]: " + cantModificable[i]

                maxDolAcu += dol.toDouble()
                maxPrctAcu += por.toDouble()
                maxCanAcu += can.toDouble()

                println "\tdol=" + dol + "  por=" + por + "  can=" + can + "  maxDol=" + maxDol + "   maxPor=" + maxPor + "   maxCan=" + maxCan +
                        "   maxDolAcu=" + maxDolAcu + "  maxPrctAcu=" + maxPrctAcu + "  maxCanAcu=" + maxCanAcu


                filaDolMod += "<input type='text' class='input-mini tiny dol p${i}' value='" + "0.00" +/* maxDol +*/
                        "' data-tipo='dol' data-total='${totDol}' data-periodo='${i}' data-max='" + maxDolAcu + "' " +
                        " data-id='${cantModificable[i].crono}' data-id2='${periodo.id}' data-id3='${cronos.volumen.id}' " +
                        " data-val1='${totalPlanilla.toDouble().round(2)}' /> " /*+
                        "(max. ${maxDolAcu.toDouble().round(2)})"*/
                filaPorMod += "<input type='text' class='input-mini tiny prct p${i}' value='" + "0.00" + /*maxPor +*/
                        "' data-tipo='prct' data-total='${totPor}' data-periodo='${i}' data-max='" + maxPrctAcu + "' " +
                        " data-id='${cantModificable[i].crono}' data-id2='${periodo.id}' data-id3='${cronos.volumen.id}' " +
                        " data-val1='${porPla.toDouble().round(2)}'  /> " /*+
                        "(max. ${maxPrctAcu.toDouble().round(2)})"*/
                filaCanMod += "<input type='text' class='input-mini tiny fis p${i}' value='" + "0.00" + /*maxCan +*/
                        "' data-tipo='fis' data-total='${totCan}' data-periodo='${i}' data-max='" + maxCanAcu + "' " +
                        " data-id='${cantModificable[i].crono}' data-id2='${periodo.id}' data-id3='${cronos.volumen.id}' " +
                        " data-val1='${canPla.toDouble().round(2)}' /> " /*+
                        "(max. ${maxCanAcu.toDouble().round(2)})"*/
            }

            filaDolPla += "</td>"
            filaPorPla += "</td>"
            filaCanPla += "</td>"
            filaDolMod += "</td>"
            filaPorMod += "</td>"
            filaCanMod += "</td>"
        }

        html += "<tr class='item_row ' data-vol='" + cronos.volumen.id + "'>"
        html += '<th rowspan="3">Cronograma</th>'
        html += '<td>$</td>'
        html += filaDol
        html += "<td class='num dol total totalRubro'>"
//            html += formatNumber(number: totDol, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
        html += numero(totDol)
        html += "</td>"
        html += "</tr>"

        html += "<tr class='click item_prc'>"
//        html += '<td colspan="5"> </td>'
//        html += '<td>Cronograma</td>'
        html += '<td>%</td>'
        html += filaPor
        html += "<td class='num prct total totalRubro'>"
//            html += formatNumber(number: totPor, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
        html += numero(totPor)
        html += "</td>"
        html += "</tr>"

        html += "<tr class='click item_f '>"
//        html += '<td colspan="5"> </td>'
//        html += '<td>Cronograma</td>'
        html += '<td>F</td>'
        html += filaCan
        html += "<td class='num fis total totalRubro'>"
//            html += formatNumber(number: totCan, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
        html += numero(totCan)
        html += "</td>"
        html += "</tr>"

        html += "<tr class='item_row ' data-vol='" + cronos.volumen.id + "'>"
        html += '<th rowspan="3">Planillado</th>'
        html += '<td>$</td>'
        html += filaDolPla
        html += "<td class='num dol total totalRubro'>"
        html += numero(totDolPla)
        html += "</td>"
        html += "</tr>"

        html += "<tr class='click item_prc '>"
        html += '<td>%</td>'
        html += filaPorPla
        html += "<td class='num prct total totalRubro'>"
        html += numero(totPorPla)
        html += "</td>"
        html += "</tr>"

        html += "<tr class='click item_f '>"
        html += '<td>F</td>'
        html += filaCanPla
        html += "<td class='num fis total totalRubro'>"
        html += numero(totCanPla)
        html += "</td>"
        html += "</tr>"

        html += "<tr class='item_row ' data-vol='" + cronos.volumen.id + "'>"
        html += '<th rowspan="3">Modificaciones</th>'
        html += '<td>$</td>'
        html += filaDolMod
        html += "<td class='num dol total totalRubro totalModif'>"
        html += numero(0)
        html += "</td>"
        html += "</tr>"

        html += "<tr class='click item_prc '>"
        html += '<td>%</td>'
        html += filaPorMod
        html += "<td class='num prct total totalRubro totalModif'>"
        html += numero(0)
        html += "</td>"
        html += "</tr>"

        html += "<tr class='click item_f '>"
        html += '<td>F</td>'
        html += filaCanMod
        html += "<td class='num fis total totalRubro totalModif'>"
        html += numero(0)
        html += "</td>"
        html += "</tr>"

        html += "</tbody>"
        html += "</table>"

        return [html: html]
    }

    def modificacion() {

        def obra = Obra.get(params.obra.toLong())
        def modificaciones = [:]

        params.fis.each { p ->
            def parts = p.split("_")
            if (parts.size() == 4) {
                def val = parts[0]
                def periodo = PeriodoEjecucion.get(parts[1].toLong())
                def vol = VolumenesObra.get(parts[2].toLong())
                def crono = parts[3]
                if (!modificaciones[periodo]) {
                    modificaciones[periodo] = [
                            fis : [:],
                            dol : [:],
                            prct: [:]
                    ]
                }
                modificaciones[periodo]["fis"].val = val
                modificaciones[periodo]["fis"].vol = vol
                modificaciones[periodo]["fis"].crono = crono
            }
        }

        params.prct.each { p ->
            def parts = p.split("_")
            if (parts.size() == 4) {
                def val = parts[0]
                def periodo = PeriodoEjecucion.get(parts[1].toLong())
                def vol = VolumenesObra.get(parts[2].toLong())
                def crono = parts[3]
                if (!modificaciones[periodo]) {
                    modificaciones[periodo] = [
                            fis : [:],
                            dol : [:],
                            prct: [:]
                    ]
                }
                modificaciones[periodo]["prct"].val = val
                modificaciones[periodo]["prct"].vol = vol
                modificaciones[periodo]["prct"].crono = crono
            }
        }

        params.dol.each { p ->
            def parts = p.split("_")
            if (parts.size() == 4) {
                def val = parts[0]
                def periodo = PeriodoEjecucion.get(parts[1].toLong())
                def vol = VolumenesObra.get(parts[2].toLong())
                def crono = parts[3]
                if (!modificaciones[periodo]) {
                    modificaciones[periodo] = [
                            fis : [:],
                            dol : [:],
                            prct: [:]
                    ]
                }
                modificaciones[periodo]["dol"].val = val
                modificaciones[periodo]["dol"].vol = vol
                modificaciones[periodo]["dol"].crono = crono
            }
        }

        def ok = "OK"

        modificaciones.each { periodo, mod ->
            println ".. " + periodo + "\t" + mod
            def fis = mod.fis
            def prc = mod.prct
            def dol = mod.dol
            CronogramaEjecucion crono
            if (fis.crono != "null") {
                crono = CronogramaEjecucion.get(fis.crono.toLong())
            } else {
                crono = new CronogramaEjecucion()
                crono.volumenObra = fis.vol
                crono.periodo = periodo
            }
            crono.precio = dol.val.toDouble()
            crono.porcentaje = prc.val.toDouble()
            crono.cantidad = fis.val.toDouble()
            if (!crono.save(flush: true)) {
                println "Error al guardar: " + crono.errors
                ok = "NO"
            }
        }

        render ok
    }

    def ampliacion_ajax() {}

    def ampliacion() {  /** ampliacion de plazo **/
        println "params ampliacion de plazo: $params"
        def dias = params.dias.toInteger()
        def obra = Obra.get(params.obra)
        def cntr = Contrato.get(params.contrato)

        def modificacion = new Modificaciones([
                obra         : obra,
                tipo         : "A",
                dias         : dias,
                fecha        : new Date(),
                motivo       : params.motivo,
                observaciones: params.observaciones,
                memo         : params.memo.toUpperCase(),
                contrato     : cntr
        ])
        if (!modificacion.save(flush: true)) {
            println "error modificacion: " + modificacion.errors
        }

        def ultimoPeriodo = PeriodoEjecucion.findAllByObra(obra, [sort: "fechaInicio"]).last()

        def fcin = ultimoPeriodo.fechaInicio
        def fcfn = ultimoPeriodo.fechaFin
        def prdo = ultimoPeriodo.numero
        def fcfm

        fcfm = preciosService.ultimoDiaDelMes(fcin)
        def diasMes
        def errores = false

        if (((fcfm - fcfn + 1) >= dias && (fcfm != fcfn))) {
            ultimoPeriodo.fechaFin = fcfn + dias - 1
            if (!ultimoPeriodo.save(flush: true)) {
                errores = true
                println "ERROR!!!!: " + ultimoPeriodo.errors
            } else {
                println " se ha modificado el último prej"
                dias = 0
            }
        }

        if (fcfm == fcfn) {
            println "cambia fecha de inicio"
            fcin = fcfn + 1
            fcfn = fcin
            fcfm = preciosService.ultimoDiaDelMes(fcin)
        } else {
            println "pone fecha de inicio"
            fcin = fcfn + 1
            fcfm = preciosService.ultimoDiaDelMes(fcin)
        }

        println "inicio de otros periodos con dias: $dias"

        while (dias > 0) {
            diasMes = fcfm - fcin + 1
            if (dias > diasMes) {
                /** nuevo periodo **/
                def periodo = new PeriodoEjecucion([
                        obra       : obra,
                        numero     : prdo++,
                        tipo       : "A",
                        fechaInicio: fcin,
                        fechaFin   : fcfm,
                        contrato   : cntr
                ])
                if (!periodo.save(flush: true)) {
                    errores = true
                    println "ERROR!!!!: " + periodo.errors
                } else {
                    println "crea nuevo periodo completo"
                    dias -= diasMes
                    fcin = fcfm + 1
                    fcfm = preciosService.ultimoDiaDelMes(fcin)
                }

            } else {
                fcfn = fcin + dias - 1
                dias -= (fcfn - fcin + 1)
                /** alarga el periodo inicial **/
                def periodo = new PeriodoEjecucion([
                        obra       : obra,
                        numero     : prdo++,
                        tipo       : "A",
                        fechaInicio: fcin,
                        fechaFin   : fcfn,
                        contrato   : cntr
                ])
                if (!periodo.save(flush: true)) {
                    errores = true
                    println "ERROR!!!!: " + periodo.errors
                } else {
                    fcin = fcfn + 1
                    dias -= (fcfn - fcin + 1)
                    println "crea nuevo periodo parcial, queda: fcfm: $fcfm, fcin: $fcin, dias: $dias"
                }
            }
        }

        if (!errores) {
            render "OK"
        } else {
            render "NO"
        }
    }

    def ampliacion__old() {
//        println "AMPLIACION"
//        println params
        def dias = params.dias.toInteger()
        def obra = Obra.get(params.obra)

        def suspension = PeriodoEjecucion.findByObraAndTipo(obra, "S", [sort: 'fechaInicio', order: "desc"])

        def periodos

        def modificacion = new Modificaciones([
                obra         : obra,
                tipo         : "A",
                dias         : dias,
                fecha        : new Date(),
                motivo       : params.motivo,
                observaciones: params.observaciones,
                memo         : params.memo.toUpperCase()
        ])
        if (!modificacion.save(flush: true)) {
            println "error modificacion: " + modificacion.errors
        }

        if (suspension) {
            //hace la ampliacion solo en los periodos tipo P q esten despues de la ultima suspension
            periodos = PeriodoEjecucion.withCriteria {
                eq("obra", obra)
                eq("tipo", "P")
                gt("fechaInicio", suspension.fechaFin)
            }
        } else {
            //si no hay suspension hace en todos los periodos tipo P
            periodos = PeriodoEjecucion.findAllByObraAndTipo(obra, "P", [sort: 'fechaInicio'])
        }
        def diasAdd = Math.floor(dias / periodos.size())
        def resto = dias % periodos.size()
//        println diasAdd
//        println resto
//        println "\n"
        def ok = true
        def ini = periodos[0].fechaInicio
        periodos.eachWithIndex { per, i ->
            def fin = per.fechaFin
            def dif = fin - per.fechaInicio
            def agrega = dif + diasAdd
            if (i == periodos.size() - 1) {
                agrega += resto
            }
            agrega = agrega.toInteger()
            def nuevoFin, nuevoIni
            use(TimeCategory) {
                nuevoFin = ini + agrega.days
            }
            use(TimeCategory) {
                nuevoIni = nuevoFin + 1.days
            }

            per.fechaInicio = ini
            per.fechaFin = nuevoFin
            if (!per.save(flush: true)) {
                println "ERROR al guardar periodo " + per.id
                println per.errors
                ok = false
            }

//            println "================="
//            println "inicio " + ini
////            println "fin " + fin
////            println "dias periodo " + dif
//            println "nuevo dias periodo " + agrega
//            println "fin " + nuevoFin
////            println "nuevo ini " + nuevoIni
//            println "================="
            ini = nuevoIni
        }
        render ok ? "OK" : "NO"
    }

    private String numero(num, decimales, cero) {
        if (num == 0 && cero.toString().toLowerCase() == "hide") {
            return " ";
        }
        if (decimales == 0) {
            return formatNumber(number: num, minFractionDigits: decimales, maxFractionDigits: decimales, locale: "ec")
        } else {
            def format
            if (decimales == 2) {
                format = "##,##0"
            } else if (decimales == 3) {
                format = "##,###0"
            }
            return formatNumber(number: num, minFractionDigits: decimales, maxFractionDigits: decimales, locale: "ec", format: format)
        }
    }

    private String numero(num, decimales) {
        return numero(num, decimales, "show")
    }

    private String numero(num) {
        return numero(num, 2)
    }

    def modificarVolumen() {
        println params
        def vol = VolumenesObra.get(params.vol)
        def crejs = CronogramaEjecucion.findAllByVolumenObra(vol)
        println crejs
    }

    def tabla() {
        def obra = Obra.get(params.id)
        def html = ""

        def precios = [:]
        def indirecto = obra.totales / 100

        def periodos = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaInicio'])
        def cronos = []

        preciosService.ac_rbroObra(obra.id)
        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])

        detalle.each { vol ->
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, vol.item.id)
//            precios.put(vol.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
//            println indirecto
//            println res
            def precio = (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2)
            cronos.add([
                    codigo  : vol.item.codigo,
                    nombre  : vol.item.nombre,
                    unidad  : vol.item.unidad.codigo,
                    cantidad: vol.cantidad,
                    precioU : precio,
                    parcial : precio * vol.cantidad,
                    volumen : vol
            ])
        }//detalles.each

        def row2 = ""

        def totalCosto = 0, totalesDol = [], totalesPor = [], totalesCant = []

        html += '<table class="table table-bordered table-condensed table-hover">'
        html += '<thead>'
        html += '<tr>'
        html += '<th rowspan="2" style="width:70px;">'
        html += 'Código'
        html += '</th>'
        html += '<th rowspan="2" style="width:200px;">'
        html += 'Rubro'
        html += '</th>'
        html += '<th rowspan="2" style="width:46px;">'
        html += 'Unidad'
        html += '</th>'
        html += '<th rowspan="2" style="width:60px;">'
        html += 'Cantidad'
        html += '</th>'
        html += '<th rowspan="2" style="width:60px;">'
        html += 'Unitario'
        html += '</th>'
        html += '<th rowspan="2" style="width:60px;">'
        html += 'C.Total'
        html += '</th>'
        html += '<th rowspan="2" style="width:12px;">'
        html += 'T.'
        html += '</th>'
        periodos.eachWithIndex { per, i ->
            html += "<th class='${per.tipo}'>"
            html += formatDate(date: per.fechaInicio, format: "dd-MM-yyyy") + " a " + formatDate(date: per.fechaFin, format: "dd-MM-yyyy")
            html += "</th>"

            row2 += "<th class='${per.tipo} click' data-periodo='${per.id}'>"
            row2 += (per.tipo == 'P' ? 'Periodo' : (per.tipo == 'S' ? 'Susp.' : '')) + " " + per.numero
            row2 += " (" + (per.fechaFin - per.fechaInicio + 1) + " días)"
            row2 += "</th>"

            totalesDol[i] = 0
            totalesPor[i] = 0
            totalesCant[i] = 0
        }
        html += '<th rowspan="2">'
        html += 'Total rubro'
        html += '</th>'
        html += "</tr>"

        html += "<tr>"
        html += row2
        html += "</tr>"
        html += "</thead>"

        html += "<tbody>"
        cronos.each { crono ->
            html += "<tr class='click item_row ${crono.volumen.rutaCritica == 'S' ? 'rutaCritica' : ''}' data-vol='" + crono.volumen.id + "'>"

            html += "<td class='codigo'>"
            html += crono.codigo
            html += "</td>"

            html += "<td class=''nombre>"
            html += crono.nombre
            html += "</td>"

            html += "<td class='unidad' style='text-align: center;'>"
            html += crono.unidad
            html += "</td>"

            html += "<td class='num cantidad'>"
//            html += formatNumber(number: crono.cantidad, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            html += numero(crono.cantidad)
            html += "</td>"

            html += "<td class='num precioU'>"
//            html += formatNumber(number: crono.precioU, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            html += numero(crono.precioU)
            html += "</td>"

            def filaDol = "", filaPor = "", filaCan = ""
            def totDol = 0, totPor = 0, totCan = 0
            periodos.eachWithIndex { periodo, i ->
                def cronoPer = CronogramaEjecucion.findAllByVolumenObraAndPeriodo(crono.volumen, periodo)
                filaDol += "<td class='dol num ${periodo.tipo}'>"
                filaPor += "<td class='prct num ${periodo.tipo}'>"
                filaCan += "<td class='fis num ${periodo.tipo}'>"
                if (cronoPer.size() == 1) {
                    cronoPer = cronoPer[0]
//                    println cronoPer.id
//                    filaDol += g.formatNumber(number: cronoPer.precio, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec")
                    filaDol += numero(cronoPer.precio)
//                    filaPor += g.formatNumber(number: cronoPer.porcentaje, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec")
                    filaPor += numero(cronoPer.porcentaje)
//                    filaCan += g.formatNumber(number: cronoPer.cantidad, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec")
                    filaCan += numero(cronoPer.cantidad)
                    totDol += cronoPer.precio
                    totPor += cronoPer.porcentaje
                    totCan += cronoPer.cantidad
                    totalesDol[i] += cronoPer.precio
                }
                filaDol += "</td>"
                filaPor += "</td>"
                filaCan += "</td>"
            }

            html += "<td class='num subtotal'>"
//            html += formatNumber(number: crono.parcial, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            crono.parcial = crono.parcial.round(2)
            html += numero(crono.parcial.round(2))
            totalCosto += crono.parcial
            html += "</td>"
            html += '<td>$</td>'
            html += filaDol
            html += "<td class='num dol total totalRubro'>"
//            html += formatNumber(number: totDol, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            html += numero(totDol)
            html += "</td>"
            html += "</tr>"

            html += "<tr class='click item_prc ${crono.volumen.rutaCritica == 'S' ? 'rutaCritica' : ''}'>"
            html += '<td colspan="6"> </td>'
            html += '<td>%</td>'
            html += filaPor
            html += "<td class='num prct total totalRubro'>"
//            html += formatNumber(number: totPor, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            html += numero(totPor)
            html += "</td>"
            html += "</tr>"

            html += "<tr class='click item_f ${crono.volumen.rutaCritica == 'S' ? 'rutaCritica' : ''}'>"
            html += '<td colspan="6"> </td>'
            html += '<td>F</td>'
            html += filaCan
            html += "<td class='num fis total totalRubro'>"
//            html += formatNumber(number: totCan, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            html += numero(totCan)
            html += "</td>"
            html += "</tr>"
        }
        html += "</tbody>"

        html += "<tfoot>"
        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='4'>TOTAL PARCIAL</td>"
        html += "<td class='num'>"
//        html += formatNumber(number: totalCosto, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec")
        totalCosto = totalCosto.toDouble().round(2)
        html += numero(totalCosto)
        html += "</td>"
        html += "<td>T</td>"
        def filaDolAcum = "", filaPor = "", filaPorAcum = "", sumaDol = 0, sumaPor = 0
        totalesDol.each {
            it = it.toDouble().round(2)
            def por = ((100 * it) / totalCosto).round(2)
            sumaDol += it
            sumaPor += por
            html += "<td class='num'>"
//            html += formatNumber(number: it, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            html += numero(it)
            html += "</td>"

            filaDolAcum += "<td class='num'>"
//            filaDolAcum += formatNumber(number: sumaDol, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            filaDolAcum += numero(sumaDol)
            filaDolAcum += "</td>"

            filaPor += "<td class='num'>"
//            filaPor += formatNumber(number: por, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            filaPor += numero(por)
            filaPor += "</td>"

            filaPorAcum += "<td class='num'>"
//            filaPorAcum += formatNumber(number: sumaPor, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            filaPorAcum += numero(sumaPor)
            filaPorAcum += "</td>"
        }
        html += "<td></td>"
        html += "</tr>"

        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='4'>TOTAL ACUMULADO</td>"
        html += "<td class='num'>"
        html += "</td>"
        html += "<td>T</td>"
        html += filaDolAcum
        html += "<td></td>"
        html += "</tr>"

        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='4'>% PARCIAL</td>"
        html += "<td class='num'>"
        html += "</td>"
        html += "<td>T</td>"
        html += filaPor
        html += "<td></td>"
        html += "</tr>"

        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='4'>% ACUMULADO</td>"
        html += "<td class='num'>"
        html += "</td>"
        html += "<td>T</td>"
        html += filaPorAcum
        html += "<td></td>"
        html += "</tr>"
        html += "</tfoot>"

        html += "</table>"

        return [detalle: detalle, precios: precios, obra: obra, tabla: html]
    }

    def tabla_old() {
        def obra = Obra.get(params.id)

        def precios = [:]
        def indirecto = obra.totales / 100

        def max = 0
        def periodos = []
        def cronos = []
        def sum = 0

        preciosService.ac_rbroObra(obra.id)

        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        detalle.each { vol ->
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, vol.item.id)
//            precios.put(vol.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
            def precio = (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2)
            def mapaVol = [
                    codigo  : vol.item.codigo,
                    nombre  : vol.item.nombre,
                    unidad  : vol.item.unidad.codigo,
                    cantidad: vol.cantidad,
                    precioU : precio,
                    parcial : precio * vol.cantidad,
                    periodos: []
            ]
            sum += mapaVol.parcial
            def c = CronogramaEjecucion.findAllByVolumenObra(vol)

            def b = false
            if (c.size() > max) {
                periodos = []
                max = c.size()
                b = true
            }
            c.each { cc ->
                if (b) {
                    periodos.add([
                            ini : cc.fechaInicio,
                            fin : cc.fechaFin,
                            tipo: cc.tipo,
                            num : cc.periodo
                    ])
                }
                mapaVol.periodos.add([
                        tipo      : cc.tipo,
                        num       : cc.periodo,
                        cantidad  : cc.cantidad,
                        precio    : cc.precio,
                        porcentaje: cc.porcentaje,
                        ini       : cc.fechaInicio,
                        fin       : cc.fechaFin,
                ])
            }
            cronos.add(mapaVol)
        } //detalle.each
        periodos = periodos.sort { it.ini }

        def html = ""
        def row2 = ""

        def totalCosto = 0, totalesDol = [], totalesPor = [], totalesCant = []

        html += '<table class="table table-bordered table-condensed table-hover">'
        html += '<thead>'
        html += '<tr>'
        html += '<th rowspan="2">'
        html += 'Código'
        html += '</th>'
        html += '<th rowspan="2">'
        html += 'Rubro'
        html += '</th>'
        html += '<th rowspan="2">'
        html += 'Unidad'
        html += '</th>'
        html += '<th rowspan="2">'
        html += 'Cantidad'
        html += '</th>'
        html += '<th rowspan="2">'
        html += 'Unitario'
        html += '</th>'
        html += '<th rowspan="2">'
        html += 'C.Total'
        html += '</th>'
        html += '<th rowspan="2">'
        html += 'T.'
        html += '</th>'
        periodos.eachWithIndex { per, i ->
            html += "<th class='${per.tipo}'>"
            html += formatDate(date: per.ini, format: "dd-MM-yyyy") + " a " + formatDate(date: per.fin, format: "dd-MM-yyyy")
            html += "</th>"

            row2 += "<th class='${per.tipo}'>"
            row2 += (per.tipo == 'P' ? 'Periodo' : (per.tipo == 'S' ? 'Suspensión' : '')) + " " + per.num
            row2 += "</th>"

            totalesDol[i] = 0
            totalesPor[i] = 0
            totalesCant[i] = 0
        }
        html += '<th rowspan="2">'
        html += 'Total rubro'
        html += '</th>'
        html += "</tr>"

        html += "<tr>"
        html += row2
        html += "</tr>"


        cronos.each { crono ->
            html += "<tr class='item_row'>"

            html += "<td class='codigo'>"
            html += crono.codigo
            html += "</td>"

            html += "<td class=''nombre>"
            html += crono.nombre
            html += "</td>"

            html += "<td class='unidad' style='text-align: center;'>"
            html += crono.unidad
            html += "</td>"

            html += "<td class='num cantidad'>"
            html += formatNumber(number: crono.cantidad, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            html += "</td>"

            html += "<td class='num precioU'>"
            html += formatNumber(number: crono.precioU, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            html += "</td>"

            html += "<td class='num subtotal'>"
            html += formatNumber(number: crono.parcial, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            totalCosto += crono.parcial
            html += "</td>"

            html += '<td>$</td>'

            crono.periodos = crono.periodos.sort { it.ini }

            def tot = 0
            periodos.eachWithIndex { per, i ->
                def cronoPer = crono.periodos.find { it.tipo == per.tipo && it.ini == per.ini && it.fin == per.fin }
                html += "<td class='dol num'>"
                if (cronoPer) {
                    html += g.formatNumber(number: cronoPer.precio, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec")
                    tot += cronoPer.precio
                    totalesDol[i] += cronoPer.precio
                }
                html += "</td>"
            }
            html += "<td class='num dol total totalRubro'>"
            html += formatNumber(number: tot, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            html += "</td>"
            html += "</tr>"


            html += "<tr class='item_prc'>"
            html += '<td colspan="6"> </td>'
            html += '<td>%</td>'

            tot = 0
            periodos.eachWithIndex { per, i ->
                def cronoPer = crono.periodos.find { it.tipo == per.tipo && it.ini == per.ini && it.fin == per.fin }
                html += "<td class='prct num'>"
                if (cronoPer) {
                    html += g.formatNumber(number: cronoPer.porcentaje, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec")
                    tot += cronoPer.porcentaje
                    totalesPor[i] += cronoPer.porcentaje
                }
                html += "</td>"
            }

            html += "<td class='num prct total totalRubro'>"
            html += formatNumber(number: tot, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            html += "</td>"
            html += "</tr>"


            html += "<tr class='item_f'>"
            html += '<td colspan="6"> </td>'
            html += '<td>F</td>'

            tot = 0
            periodos.eachWithIndex { per, i ->
                def cronoPer = crono.periodos.find { it.tipo == per.tipo && it.ini == per.ini && it.fin == per.fin }
                html += "<td class='fis num'>"
                if (cronoPer) {
                    html += g.formatNumber(number: cronoPer.cantidad, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec")
                    tot += cronoPer.cantidad
                    totalesCant[i] += cronoPer.cantidad
                }
                html += "</td>"
            }
            html += "<td class='num fis total totalRubro'>"
            html += formatNumber(number: tot, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            html += "</td>"
            html += "</tr>"

        }

        html += "<tfoot>"

        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='4'>TOTAL PARCIAL</td>"
        html += "<td class='num'>"
        html += formatNumber(number: totalCosto, minFractionDigits: 2, maxFractionDigits: 2, format: "##,##0", locale: "ec")
        html += "</td>"
        html += "<td>T</td>"
        def filaDolAcum = "", filaPor = "", filaPorAcum = "", sumaDol = 0, sumaPor = 0
        totalesDol.each {
            def por = (100 * it) / totalCosto
            sumaDol += it
            sumaPor += por
            html += "<td class='num'>"
            html += formatNumber(number: it, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            html += "</td>"

            filaDolAcum += "<td class='num'>"
            filaDolAcum += formatNumber(number: sumaDol, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            filaDolAcum += "</td>"

            filaPor += "<td class='num'>"
            filaPor += formatNumber(number: por, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            filaPor += "</td>"

            filaPorAcum += "<td class='num'>"
            filaPorAcum += formatNumber(number: sumaPor, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            filaPorAcum += "</td>"
        }
        html += "<td></td>"
        html += "</tr>"

        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='4'>TOTAL ACUMULADO</td>"
        html += "<td class='num'>"
        html += "</td>"
        html += "<td>T</td>"
        html += filaDolAcum
        html += "<td></td>"
        html += "</tr>"

        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='4'>% PARCIAL</td>"
        html += "<td class='num'>"
        html += "</td>"
        html += "<td>T</td>"
        html += filaPor
        html += "<td></td>"
        html += "</tr>"

        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='4'>% ACUMULADO</td>"
        html += "<td class='num'>"
        html += "</td>"
        html += "<td>T</td>"
        html += filaPorAcum
        html += "<td></td>"
        html += "</tr>"

        html += "</tfoot>"

        html += "</table>"

        return [detalle: detalle, precios: precios, obra: obra, tabla: html]
    }

    def index() {

        def contrato = Contrato.get(params.id)
        if (!contrato) {
            flash.message = "No se encontró el contrato"
            flash.clase = "alert-error"
            redirect(action: "errores", params: [contrato: params.id])
            return
        }
        def obra = contrato?.obra

        if (!obra) {
            flash.message = "No se encontró la obra"
            flash.clase = "alert-error"
            redirect(action: "errores", params: [contrato: params.id])
            return
        }
        if (!obra.fechaInicio) {
            flash.message = "La obra no tiene fecha de inicio. Por favor solucione el problema. " + obra.id
            flash.clase = "alert-error"
            redirect(action: "errores", params: [contrato: params.id])
            return
        }

        def suspensiones = Modificaciones.withCriteria {
            eq("obra", obra)
            eq("tipo", "S")
            or {
                isNull("fechaFin")
                and {
                    le("fechaInicio", new Date().clearTime())
                    gt("fechaFin", new Date().clearTime())
                }
            }
        }
        def ini = Modificaciones.withCriteria {
            eq("obra", obra)
            eq("tipo", "S")
            isNull("fechaFin")
            projections {
                min("fechaInicio")
            }
        }
        return [obra: obra, contrato: contrato, suspensiones: suspensiones, ini: ini]
    }


/*
    def actualizaPems() {
        */
/** en base a prej ingresa o actualiza dato en pems **//*

        println "actualizaPems params: $params"
        def pems
        def fcin     //fecha de incio periodo pems
        def fcfn     //fecha de fin periodo pems
        def fcim     //fecha de inicio de mes
        def fcfm     //fecha de fin de mes
        def vlor = 0.0
        def parcial = 0.0
        def prmt = [:]

        def cntr = Contrato.get(params.contrato)
        def prej = PeriodoEjecucion.findAllByContrato(cntr)

        prej.each { pe ->
            fcin = pe.fechaInicio
            fcfm = preciosService.ultimoDiaDelMes(fcin)
            fcim = fcfm + 1
            fcfn = pe.fechaFin
            println "$pe: fcin: $fcin, fcfn: $fcfn, fcfm: $fcfm, fcim: $fcim"

            if (fcfm < pe.fechaFin) {
                vlor = CronogramaEjecucion.executeQuery("select sum(precio) from CronogramaEjecucion where periodo = :p", [p: pe])
                println "valor: ${vlor[0]}"
                parcial = vlor[0] / (pe.fechaFin - pe.fechaInicio) * (fcfm - fcin)
                println "parcial: $parcial"
                prmt = [:]
                prmt.contrato = cntr
                prmt.obra = cntr.obra
                prmt.periodoEjecucion = pe
                prmt.fechaInicio = fcin
                prmt.fechaFin = fcfm
                prmt.parcialCronograma = parcial
                insertaPems(prmt)

                fcin = fcfm + 1
                fcfn = pe.fechaFin
                parcial = vlor[0] - parcial

                prmt = [:]
                prmt.contrato = cntr
                prmt.obra = cntr.obra
                prmt.periodoEjecucion = pe
                prmt.fechaInicio = fcin
                prmt.fechaFin = fcfn
                prmt.parcialCronograma = parcial
                insertaPems(prmt)

            } else { */
/** ingresar solo los dias restantes ver: ../Documentos/pems.sql **//*

                vlor = CronogramaEjecucion.executeQuery("select sum(precio) from CronogramaEjecucion where periodo = :p", [p: pe])
                parcial = vlor[0]
                println "parcial: $parcial"
                prmt = [:]
                prmt.contrato = cntr
                prmt.obra = cntr.obra
                prmt.periodoEjecucion = pe
                prmt.fechaInicio = fcin
                prmt.fechaFin = fcfn
                prmt.parcialCronograma = parcial
                insertaPems(prmt)

            }
        }
//        def pems = PeriodoEjecucionMes.


        render "ok"
    }
*/

    def actualizaPrej() {
        /** en base a prej ingresa o actualiza dato en prej **/
        println "actualizaPrej params: $params"
        def cntr = Contrato.get(params.contrato)
        def prej = PeriodoEjecucion.findAllByContratoAndTipoNotEqual(cntr, 'S')
        def vlor

        prej.each { pe ->
            vlor = CronogramaEjecucion.executeQuery("select sum(precio) from CronogramaEjecucion where periodo = :p", [p: pe])
            def pr = PeriodoEjecucion.get(pe.id)
            pr.parcialCronograma = vlor[0]
            if (!pr.save(flush: true)) {
                flash.message = "No se pudo actualizar prej"
                println "Error al actualizar prej: " + prej.errors
            } else {
                flash.message = "Prej actualizado exitosamente"
            }
        }
        render "ok"
    }

/*
    def insertaPems(prmt) {
        def pems = new PeriodoEjecucionMes()
        println "inserta pems del contrato : ${prmt}"
        def pems_an = PeriodoEjecucionMes.findByContratoAndFechaInicioAndFechaFin(prmt.contrato, prmt.fechaInicio, prmt.fechaFin)
        if (pems_an) {
            pems = PeriodoEjecucionMes.get(pems_an.id)
            pems.obra = prmt.obra
            pems.periodoEjecucion = prmt.periodoEjecucion
            pems.parcialCronograma = prmt.parcialCronograma
            println "actualiza valores de: $prmt"
        } else {
            pems.contrato = prmt.contrato
            pems.obra = prmt.obra
            pems.periodoEjecucion = prmt.periodoEjecucion
            pems.fechaInicio = prmt.fechaInicio
            pems.fechaFin = prmt.fechaFin
            pems.parcialCronograma = prmt.parcialCronograma
            println "inserta valores de: $prmt"
        }

        if (!pems.save(flush: true)) {
            flash.message = "No se pudo actualizar pems"
            println "Error al actualizar pems: " + pems.errors
        } else {
            flash.message = "Pems actualizado exitosamente"
        }
    }
*/


    def creaCronogramaEjec() {
        println "creaCronogramaEjec para contrato: $params.id"
        def contrato = Contrato.get(params.id)
        if (!contrato) {
            flash.message = "No se encontró el contrato"
            flash.clase = "alert-error"
//            println flash.message
            redirect(action: "errores", params: [contrato: params.id])
            return
        }
        def obra = contrato.obra
        if (!obra) {
            flash.message = "No se encontró la obra"
            flash.clase = "alert-error"
            redirect(action: "errores", params: [contrato: params.id])
            return
        }
        if (!obra.fechaInicio) {
            flash.message = "La obra no tiene fecha de inicio. Por favor solucione el problema. " + obra.id
            flash.clase = "alert-error"
            redirect(action: "errores", params: [contrato: params.id])
            return
        }
        println "Index contrato: $contrato"
        println "Index obra: $obra"

        /** copia el cronograma del contrato (crng) a la tabla cronograma de ejecucion (crej)
         *  --Se debe crear primero los prej y luego insertar cada rubro desde cronogramaContrato
         * **/
        def prej
        def continua = true
        def fcin
        def fcfn
        def fcfm
        def fcha
        def parcial = 0.0
        def parcial1 = 0.0
        def parcial2 = 0.0
        def vlor
        def precio2 = 0.0
        def porcentaje2 = 0.0
        def cantidad2 = 0.0
        def prej2
        def vol2

        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def periodos = CronogramaEjecucion.executeQuery("select max(periodo) from CronogramaContrato where contrato = :c", [c: contrato])
        println "periodos: $periodos"

        def hayPrej = PeriodoEjecucion.findAllByContrato(contrato)
        println "hayPrej: $hayPrej"
        if (!hayPrej) {
            fcin = obra.fechaInicio
            for (pr in (1..periodos[0])) {
                def dias = (pr - 1) * 30 //+ (crono.periodo - 1)
                def prdo = 0
                println "crear periodo... pr: $pr, dias: $dias, plazo: ${contrato.plazo}"

                if ((dias + 30) > contrato.plazo) {
                    prdo = contrato.plazo - dias
                } else {
                    prdo = 30
                }

                fcin = fcha ? fcha + 1 : obra.fechaInicio
                fcfn = fcin + (prdo - 1).toInteger()      // 30 - 1 para contar el dia inicial
                fcfm = preciosService.ultimoDiaDelMes(fcin)
                if (fcfm < fcfn) {   /** sobrepasa el mes --> 2 periodos **/
                    prej = PeriodoEjecucion.findByContratoAndFechaInicioAndFechaFin(contrato, fcin, fcfm)
                    if (!prej) {
                        prej = new PeriodoEjecucion()
                        prej.contrato = contrato
                        prej.obra = obra
                        prej.fechaInicio = fcin
                        prej.fechaFin = fcfm
                        prej.numero = pr
                        prej.tipo = 'P'
                        if (!prej.save(flush: true)) {
                            flash.message = "No se pudo crear prej"
                            println "Error al crear prej*******: " + prej.errors
                        } else {
                            flash.message = "Prej actualizado exitosamente"
                        }
                    }

                    fcin = fcfm + 1
                    fcha = fcfn
                    prej = PeriodoEjecucion.findByContratoAndFechaInicioAndFechaFin(contrato, fcin, fcfn)
                    if (!prej) {
                        prej = new PeriodoEjecucion()
                        prej.contrato = contrato
                        prej.obra = obra
                        prej.fechaInicio = fcin
                        prej.fechaFin = fcfn
                        prej.numero = pr
                        prej.tipo = 'P'
                        if (!prej.save(flush: true)) {
                            flash.message = "No se pudo crear prej"
                            println "Error al crear prej*******: " + prej.errors
                        } else {
                            flash.message = "Prej actualizado exitosamente"
                        }
                    }
                } else {
                    prej = PeriodoEjecucion.findByContratoAndFechaInicioAndFechaFin(contrato, fcin, fcfn)
                    if (!prej) {
                        prej = new PeriodoEjecucion()
                        prej.contrato = contrato
                        prej.obra = obra
                        prej.fechaInicio = fcin
                        prej.fechaFin = fcfn
                        prej.numero = pr
                        prej.tipo = 'P'
                        if (!prej.save(flush: true)) {
                            flash.message = "No se pudo crear prej"
                            println "Error al crear prej*******: " + prej.errors
                        } else {
                            flash.message = "Prej actualizado exitosamente"
                        }
                    }
                }
            }
        }

//        println "datos de cronograma $cronogramas"
        def cronogramas = CronogramaEjecucion.countByVolumenObraInList(detalle)

        if (cronogramas == 0) {
//            println "no hay datos de cronograma ... inicia cargado"
            detalle.each { vol ->
                def cronoCntr = CronogramaContrato.findAllByVolumenObra(vol, [sort: 'periodo'])
                cronoCntr.each { crono ->

//                    vlor = CronogramaContrato.executeQuery("select sum(precio), sum(porcentaje), sum(cantidad)  from CronogramaContrato where contrato = :c and periodo = :p", [c: contrato, p: crono.periodo])
                    vlor = CronogramaContrato.findByContratoAndVolumenObraAndPeriodo(contrato, vol, crono.periodo)
                    prej = PeriodoEjecucion.findAllByContratoAndNumero(contrato, crono.periodo)
                    /** ingresar la proporcion en los prej existentes conform el número de días **/
//                    println "valores cronograma: ${vlor}, prej: $prej"
                    def prco = 0.0
                    def pcnt = 0.0
                    def cntd = 0.0
                    def dias = 0
                    def mes = 30
                    def ultimo = contrato.plazo % 30 > 0 ? contrato.plazo % 30 : 30
                    prej.each { pe ->
                        /** se debe definir cuantos dias tiene el periodo actual **/
                        if (ultimo != 30) {
                            dias += (pe.fechaFin - pe.fechaInicio + 1)
                            if((prco == 0) && (contrato.plazo - 30 * (crono.periodo -1)) <= ultimo) {
                                mes = ultimo
                            } else {
                                mes = 30
                            }
                            println "dias: $dias, restan: ${contrato.plazo - 30 * (crono.periodo -1)}, mes = $mes, ultimo: $ultimo"
                        }

                        if (prco > 0) {
                            prco = vlor.precio - prco
                            pcnt = vlor.porcentaje - pcnt
                            cntd = vlor.cantidad - cntd
                        } else {
                            prco = Math.round(vlor.precio / mes * (pe.fechaFin - pe.fechaInicio + 1) * 100) / 100
                            pcnt = Math.round(vlor.porcentaje / mes * (pe.fechaFin - pe.fechaInicio + 1) * 100) / 100
                            cntd = Math.round(vlor.cantidad / mes * (pe.fechaFin - pe.fechaInicio + 1) * 100) / 100
                        }
//                        println "crear crej con: ${vol.id}, ${pe.numero}, $prco, $pcnt, $cntd"
                        def cronoEjecucion = new CronogramaEjecucion([
                                volumenObra: vol,
                                periodo    : pe,
                                precio     : prco,
                                porcentaje : pcnt,
                                cantidad   : cntd
                        ])
                        if (!cronoEjecucion.save(flush: true)) {
                            println "Error al guardar el crono ejecucion del crono " + crono.id
                            println cronoEjecucion.errors
                        } else {
//                            println "ok " + crono.id + "  =>  " + cronoEjecucion.id
                        }
                    }

                } //cronogramaContrato.each
            } //detalles.each
            /** una vez cargado el cronograma ejecuta la creacion de periods mensuales, lo cual puede asimilarse dentro de PREJ **/
            params.contrato = contrato?.id
            actualizaPrej()  /** pone para cada prej los valores de cronograma **/
        } //if cronogramas == 0

        redirect(action: "index", params: [obra: obra, id: contrato.id, ini: fcin])
    }

    def insertaPrej(prmt) {
        def prej = new PeriodoEjecucion()
        println "inserta prej del contrato : ${prmt}"
        def prej_an = PeriodoEjecucion.findByContratoAndFechaInicioAndFechaFin(prmt.contrato, prmt.fechaInicio, prmt.fechaFin)
        if (prej_an) {
            prej_an.obra = prmt.obra
            prej_an.periodoEjecucion = prmt.periodoEjecucion
            prej_an.parcialCronograma = prmt.parcialCronograma
            println "actualiza valores de: $prmt"
        } else {
            prej.contrato = prmt.contrato
            prej.obra = prmt.obra
            prej.periodoEjecucion = prmt.periodoEjecucion
            prej.fechaInicio = prmt.fechaInicio
            prej.fechaFin = prmt.fechaFin
            prej.parcialCronograma = prmt.parcialCronograma
            println "inserta valores de: $prmt"
        }

        if (!prej.save(flush: true)) {
            flash.message = "No se pudo actualizar prej"
            println "Error al actualizar prej: " + prej.errors
        } else {
            flash.message = "Pems actualizado exitosamente"
        }
    }

    /**
     * copia PREJ actual a prej_t y CREJ actual a crej_t
     * crea nuevos periodos e inserta los valores correspondientes en el cronograma de acuerdo
     * a lo que se ejecuta en el periodo correspondiente
     */
    def terminaSuspensionTemp() {
        println "termina suspension temp params: $params"
        def cn = dbConnectionService.getConnection()
        def cntr = Contrato.get(params.cntr)
        def prejActual = PeriodoEjecucion.findAllByContrato(cntr)
        def tx1 = ""
        def tx2 = ""

        /** Cargar en prej_t y crej_t el CREJ actual, borrando el anterior **/

        //================== rspalda en Xrej_t valores de cronograma y periodo
        tx1 = "delete from crej_t where prej__id in (select prej__id from prej_t where cntr__id = ${cntr.id})"
        tx2 = "delete from prej_t where cntr__id = ${cntr.id}"
//        println "tx1: $tx1"
//        println "tx2: $tx2"
        cn.execute(tx1.toString())
        cn.execute(tx2.toString())

        println "ha borrado registros temporales"
        prejActual.each { pr ->
            def prejTemp = new PeriodoEjecucionTmp()
            prejTemp.contrato = pr.contrato
            prejTemp.obra = pr.obra
            prejTemp.fechaInicio = pr.fechaInicio
            prejTemp.fechaFin = pr.fechaFin
            prejTemp.numero = pr.numero
            prejTemp.tipo = pr.tipo
            if (!prejTemp.save(flush: true)) {
                println "Error al crear prej de suspension: " + prejTemp.errors
            }
            def crej = CronogramaEjecucion.findAllByPeriodo(pr)
            crej.each { ce ->
                def crejTemp = new CronogramaEjecucionTmp()
                crejTemp.periodo = prejTemp
                crejTemp.precio = ce.precio
                crejTemp.cantidad = ce.cantidad
                crejTemp.porcentaje = ce.porcentaje
                crejTemp.volumenObra = ce.volumenObra
                if (!crejTemp.save(flush: true)) {
                    println "Error al crear prej de suspension: " + crejTemp.errors
                }
            }
        }
        println "se ha creado registros temporales en prej_t y crej_t"
        //=================

        /** borra crej y prej actuales **/
        cn.execute("delete from crej where prej__id in (select prej__id from prej where cntr__id = ${cntr.id})".toString())
        cn.execute("delete from prej where cntr__id = ${cntr.id}".toString())

        println "borrado de datos actuales de prej y crej"

        def suspension
        def diasSusp = 0
        def suspensiones

        def fcfn
        if(params.fcfn) {
            fcfn = new Date().parse("dd-MM-yyyy", params.fcfn)
        }
        if(fcfn) {
            suspensiones = Modificaciones.findAllByContratoAndTipoAndFechaFin(cntr, "S", fcfn)
        }  else {
            suspensiones = Modificaciones.findAllByContratoAndTipoAndFechaFinIsNull(cntr, "S")
        }
//        println "suspensión: ${suspensiones.size()}, inicio en ${suspensiones[0].fechaInicio.format('dd-MMM-yyyy'.toString())}"

        def fin = new Date().parse("dd-MM-yyyy", params.fin)
        def finSusp = fin

        if (suspensiones.size() > 1) {
            return "Error... existe mas de una suspension en curso"
        } else {
            suspension = suspensiones[0]
            diasSusp = finSusp - suspension.fechaInicio + 1
            suspension.dias = diasSusp
            suspension.fechaFin = finSusp
            if (params.observaciones && params.observaciones.trim() != "") {
                suspension.observaciones = params.observaciones + "||" + suspension.observaciones
            }
            if (!suspension.save(flush: true)) {
                println "EEOR EN TEMINAR SUSSPENSION: " + suspension.errors
            }
        }

        println "Fin de suspensión actualizada a: ${suspension.fechaFin}, dias: ${suspension.dias}"

        // Los periodos anteriores a la fecha de suspension quedan igual
        //// arreglar fechas de periodos y numero de dias

        // para cada periodo afectado:
        // redefine fechas de fin e inicio de los periodos existentes en funcion del plazo actual de la obra.

        def periodos = PeriodoEjecucionTmp.findAllByContrato(cntr, [sort: 'fechaInicio'])
        // cambia los periodos e inserta la suspension
        def iniPrej
        def finPrej
        def plazoActual = 0
        def fechaFinal
        /** calcula fecha final de la obra **/
        periodos.each { pr ->
            if (iniPrej == null) {
                iniPrej = pr.fechaInicio
            } else {
                if (iniPrej > pr.fechaInicio) iniPrej = pr.fechaInicio
            }
            if (finPrej == null) {
                finPrej = pr.fechaFin
            } else {
                if (finPrej < pr.fechaFin) finPrej = pr.fechaFin
            }
            plazoActual = finPrej - iniPrej + 1
        }
        fechaFinal = iniPrej + plazoActual + diasSusp - 2 // no cuenta fin de suspension y dia final
        println "fecha final de la obra: ${fechaFinal.format('dd-MMM-yyyy')}"

        def cambiar = false
        def fcha = iniPrej

        def i = 0
        while (fcha < fechaFinal) {
            def pr = periodos[i]
            def pe = new PeriodoEjecucion()
            pe.contrato = pr.contrato
            pe.obra = pr.obra
            pe.fechaInicio = pr.fechaInicio
            pe.fechaFin = pr.fechaFin
            pe.numero = pr.numero
            pe.tipo = pr.tipo
            if (!pe.save(flush: true)) {
                println "Error al crear prej de suspension: " + pe.errors
            }

//            println "procesa peridodo: $pr.id, cambiar = $cambiar, desde ${pr.fechaInicio.format('dd-MMM-yyyy')} a " +
//                    "${pr.fechaFin.format('dd-MMM-yyyy')}"
            if (!cambiar) {
                if (pr.fechaFin > suspension.fechaInicio) {
                    def prej = PeriodoEjecucion.get(pe.id)
                    prej.fechaFin = suspension.fechaInicio - 1
//                    println "cambia fecha de fin a: ${pr.fechaFin.format('dd-MMM-yyyy')}"
                    if (!prej.save(flush: true)) {
                        println "Error al crear prej de suspension: " + prej.errors
                    }
                    cambiar = true
                    // inserta perdiodo de suspension
                    def prdoNuevo = new PeriodoEjecucion()
                    prdoNuevo.fechaInicio = suspension.fechaInicio
                    prdoNuevo.fechaFin = suspension.fechaFin - 1
                    prdoNuevo.contrato = cntr
                    prdoNuevo.obra = cntr.obra
                    prdoNuevo.numero = pr.numero
                    prdoNuevo.tipo = "S"
                    if (!prdoNuevo.save(flush: true)) {
                        println "Error al crear prej de suspension: " + prdoNuevo.errors
                    }
                    fcha = suspension.fechaFin
                }
            } else {
                // cambiar periodos
                def prej = PeriodoEjecucion.get(pe.id)
                prej.fechaInicio = fcha
                if (fechaFinal > preciosService.ultimoDiaDelMes(fcha)) {
                    prej.fechaFin = preciosService.ultimoDiaDelMes(fcha)
                    fcha = prej.fechaFin + 1
                } else {
                    prej.fechaFin = fechaFinal
                    fcha = fechaFinal
                }
                if (!prej.save(flush: true)) {
                    println "Error al crear prej de suspension: " + prej.errors
                }
            }
            i++
        }

        /** ingresar en crej los valores en base a crej_t y periodo por periodo prej **/


        def nuevoPrej = PeriodoEjecucion.findAllByContratoAndTipoNotEqual(cntr, 'S', [sort: 'fechaInicio'])
        def periodoTmpProcesado = 0
        def fraccionContinua = false
        def fraccion = 0.0
        nuevoPrej.each { pe ->
            //* si existe este periodo igual antes de la suspensión se lo copia **/
            def prAntes = PeriodoEjecucionTmp.findByContratoAndFechaInicioAndFechaFin(cntr, pe.fechaInicio, pe.fechaFin)
            if (prAntes) { /** si es el mismo periodo que el original se copia el CREJ **/
                tx1 = "insert into crej (crejcntd, crejprct, crejprco, vlob__id, prej__id) " +
                        "select crejcntd, crejprct, crejprco, vlob__id, ${pe.id} " +
                        "from crej_t where prej__id = ${prAntes.id}"
//                println "inserta periodos iguales: $tx1"
                cn.execute(tx1.toString())
                periodoTmpProcesado = prAntes.id
            } else {
                println "procesar la suspension.... periodoTmpProcesado: $periodoTmpProcesado"
                def prParciales = PeriodoEjecucionTmp.findAllByContratoAndIdGreaterThanAndTipoNotEqualAndFechaFinLessThanEquals(cntr,
                        periodoTmpProcesado, 'S', pe.fechaFin + diasSusp)
//                println "prparciales: $prParciales"
                def dias = pe.fechaFin - pe.fechaInicio + 1
                def cont = prParciales.size()
                def j = 0
                def crejNuevo
//                println "****inicia while con dias: $dias y prParciales: ${prParciales.size()} registros"
                def actual = 0
                while (j < cont) { //** para cada periodo restante hasta cubrir los "días": inserta proporcion
                    def prTmp = prParciales[j]
                    actual = 0
                    def prNuevos = PeriodoEjecucion.findAllByContratoAndFechaInicioGreaterThanEqualsAndTipoNotEqual(cntr, pe.fechaInicio, 'S')
//                    println ">>>procesa periodo ${prTmp.id} con dias: $dias, prTMP: $prTmp.fechaFin - $prTmp.fechaInicio, continua: $fraccionContinua"
                    if(fraccionContinua){
                        fraccion = 1 - fraccion
                        fraccionContinua = false
                    } else {
                        if(dias > (prTmp.fechaFin - prTmp.fechaInicio + 1)) {
//                            println "es mayor $dias que ${prTmp.fechaFin - prTmp.fechaInicio + 1}"
                            fraccion = 1
                        } else {
//                            println "es menor $dias que ${prTmp.fechaFin - prTmp.fechaInicio + 1}"
                            fraccion = dias / (prTmp.fechaFin - prTmp.fechaInicio + 1)
                        }
//                        fraccion = dias > (prTmp.fechaFin - prTmp.fechaInicio + 1) ? 1 : dias / (prTmp.fechaFin - prTmp.fechaInicio + 1)
                    }
//                    println "valor de la fraccion: $fraccion"
                    // si no existe el vlob se inserta caso contrario se añade
                    def cr = CronogramaEjecucionTmp.findAllByPeriodo(prTmp)
                    cr.each { c ->
//                        println "si existe se incrementa si no se inserta, procesa $c.id"
                        def crej = CronogramaEjecucion.findAllByPeriodoAndVolumenObra(prNuevos[actual], c.volumenObra)
                        if(crej.size() > 1){
                            pritln "--------------------Error... existe mas de un regisro de vlob: ${c.volumenObra} en prej: ${prNuevos[actual].id} "
                        }
//                        println "periodo actual a procesar: ${prNuevos[actual]} con crej: $crej"
                        if (!crej) {
//                            println "inserta valores .... --> ${c.precio * fraccion}"
                            crejNuevo = new CronogramaEjecucion()
                            crejNuevo.periodo = prNuevos[actual]
                            crejNuevo.volumenObra = c.volumenObra
                            crejNuevo.cantidad = Math.round(c.cantidad * fraccion *1000) /1000
                            crejNuevo.porcentaje = Math.round(c.porcentaje * fraccion *100) / 100
                            crejNuevo.precio = Math.round(c.precio * fraccion *100) / 100
                            crejNuevo.save(flush: true)
                        } else {
//                            println "incrementa valores .... --> ${c.precio * fraccion}, al vlob: ${c.volumenObra}, actual ${prNuevos[actual]}"
                            def ac_crej = CronogramaEjecucion.get(crej.first().id)
                            ac_crej.cantidad   += Math.round((c.cantidad * fraccion).toDouble() *1000) / 1000
                            ac_crej.porcentaje += Math.round((c.porcentaje * fraccion).toDouble() *100) / 100
                            ac_crej.precio     += Math.round((c.precio * fraccion).toDouble() * 100) / 100
                            ac_crej.save(flush: true)
                        }
                    }
                    if (dias < (prTmp.fechaFin - prTmp.fechaInicio + 1)) {
                        if((fraccion < 1) && (fraccion > 0)) {
                            fraccionContinua = true
                        } else {
                            fraccionContinua = false
                        }
//                        println "sale del while con fraccion: $fraccion y continua: $fraccionContinua"
                        break
                    } else {
                        periodoTmpProcesado = prParciales[j].id
//                        println "...else dias: $dias, j: $j, fraccionContinua $fraccionContinua"
                        dias -= (prTmp.fechaFin - prTmp.fechaInicio + 1) * fraccion
//                        println "continua proceso de este periodo: ${pe.fechaInicio.format('dd-MMM-yyyy')} - ${pe.fechaFin.format('dd-MMM-yyyy')} con dias: $dias, j: $j"
                        actual++
                    }
                    j++
                }
            }
        }
        cn.close()

        params.contrato = cntr.id
        actualizaPrej()
        render "OK"
    }


} //fin controller
