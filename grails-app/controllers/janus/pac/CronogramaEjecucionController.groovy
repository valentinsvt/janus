package janus.pac

import groovy.time.TimeCategory
import janus.Contrato
import janus.Modificaciones
import janus.Obra
import janus.VolumenesObra
import janus.ejecucion.PeriodoEjecucionMes
import janus.ejecucion.PeriodoPlanilla
import janus.ejecucion.Planilla
import janus.ejecucion.TipoPlanilla

import javax.management.Query

class CronogramaEjecucionController extends janus.seguridad.Shield {

    def preciosService
    def arreglosService

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

        def minDate = fi.first().format("yyyy") + "," + (fi.first().format("MM").toInteger() - 1) + "," + fi.first().format("dd")
        return [min: minDate]
    }

    /* Agregado el 25-03-2015 xq quieren hacer suspensiones sin fecha de fin: se divide el proceso en 2 partes:
            primero hace una modificacion tipo suspension sin fecha de fin y con -1 en dias
            segundo el action terminaSuspension modifica la modificacino para ponerle fecha fin y dias y recalcula las fechas de periodos del cronograma
     */

    def suspensionNueva() {
        println "AQUIIIIII"
        def obra = Obra.get(params.obra)
        def periodos = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaInicio'])

        def fin = null, finSusp = null, dias = -1


        def ini = new Date().parse("dd-MM-yyyy", params.ini)
        if (params.fin) {
            fin = new Date().parse("dd-MM-yyyy", params.fin)

            finSusp = fin
            use(TimeCategory) {
                finSusp = fin - 1.days
            }
            dias = finSusp - ini + 1
        }

        def modificacion = new Modificaciones([
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
        } else {
            println "modificacion " + modificacion.id + "   ini: " + modificacion.fechaInicio + "    fin: " + modificacion.fechaFin + "     dias: " + modificacion.dias
        }
        render "OK"
    }

    /* Agregado el 25-03-2015 xq quieren hacer suspensiones sin fecha de fin: se divide el proceso en 2 partes:
            primero el action suspensionNueva hace una modificacion tipo suspension sin fecha de fin y con -1 en dias
            segundo modifica la modificacino para ponerle fecha fin y dias y recalcula las fechas de periodos del cronograma
     */

    def terminaSuspension() {
        def obra = Obra.get(params.obra)
        def periodos = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaInicio'])
        def ini = Modificaciones.withCriteria {
            eq("obra", obra)
            eq("tipo", "S")
            or {
                isNull("fechaFin")
                gt("fechaFin", new Date().clearTime())
            }
            projections {
                min("fechaInicio")
            }
        }.first()
        def suspensiones = Modificaciones.withCriteria {
            eq("obra", obra)
            eq("tipo", "S")
            isNull("fechaFin")
        }

        def fin = new Date().parse("dd-MM-yyyy", params.fin)

        def finSusp = fin
        use(TimeCategory) {
            finSusp = fin - 1.days
        }
        def errores = ""

        //1ro cambia fecha fin y cant dias para todas las suspensiones
        suspensiones.each { sus ->
            def dias = finSusp - sus.fechaInicio + 1
            sus.dias = dias
            sus.fechaFin = finSusp
            if (params.observaciones && params.observaciones.trim() != "") {
                sus.observaciones = params.observaciones + "       " + sus.observaciones
            }
            if (!sus.save(flush: true)) {
                errores += renderErrors(bean: sus)
                println "EEOR EN TEMINAR SUSSPENSION: " + sus.errors
            }
        }

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
        def obra = Obra.get(params.obra.toLong())
        def contrato = Contrato.get(params.contrato.toLong())
        def vol = VolumenesObra.get(params.vol.toLong())

        def html = "", row2 = ""

        def liquidacionReajuste = Planilla.findAllByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo("L"))
        if (liquidacionReajuste.size() > 0) {
            return [msg: "Ya se ha realizado la liquidación del reajuste, ya no puede realizar modificaciones"]
        }

//        def planillas = Planilla.findAllByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo("P"))
        def periodos = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaInicio'])

        def indirecto = obra.totales / 100
        preciosService.ac_rbroObra(obra.id)

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

            planillasPeriodo.each { pla ->
                println "periodo: " + periodo.fechaInicio.format("dd-MM-yyyy") + " - " + periodo.fechaFin.format("dd-MM-yyyy")
                println "\tplanilla: " + pla.fechaInicio.format("dd-MM-yyyy") + " - " + pla.fechaFin.format("dd-MM-yyyy")
                if (pla.fechaFin >= periodo.fechaFin) {
                    modificable = false
//                    cantModificable[i] = [
//                            dol: 0,
//                            por: 0,
//                            can: 0
//                    ]
                    cantModificable[i].dol = 0
                    cantModificable[i].por = 0
                    cantModificable[i].can = 0

                }
                println "cantModificable5[${i}]: " + cantModificable[i]
                def diasPlanilla = pla.fechaFin - pla.fechaInicio + 1
                totalPlanilla += (diasPlanilla * cantDia)
            }

            def porPla = (totalPlanilla * 100) / totDol
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

    def ampliacion() {
//        println "AMPLIACION"
//        println params

        def dias = params.dias.toInteger()
        def obra = Obra.get(params.obra)

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

        def periodosEj = PeriodoEjecucion.findAllByObra(obra, [sort: "fechaInicio"])

        def ultimoPeriodo = periodosEj.last()

        def fechaIniPer = ultimoPeriodo.fechaInicio
        def fechaFinPer = ultimoPeriodo.fechaFin
        def diasPer = fechaFinPer - fechaIniPer + 1
        def ultimoPer = ultimoPeriodo.numero
        def errores = false

        if (diasPer < 30) {
            def diasAddPer = 30 - diasPer
            if (diasAddPer > dias) {
                diasAddPer = dias
            }
            dias -= diasAddPer
            def nuevoFin = fechaFinPer + diasAddPer

            ultimoPeriodo.fechaFin = nuevoFin
            if (!ultimoPeriodo.save(flush: true)) {
                errores = true
                println "ERROR!!! ** " + ultimoPeriodo.errors
            }
//            println "dias agregados al ultimo per: ${diasAddPer}  " + ultimoPeriodo.numero + "  " + ultimoPeriodo.fechaInicio.format("dd-MM-yyyy") + "  ->  " + nuevoFin.format("dd-MM-yyyy") + " (dias: " + (nuevoFin - ultimoPeriodo.fechaInicio + 1) + ")"
            fechaFinPer = nuevoFin
        }

        def periodosAdd = Math.ceil(dias / 30)
        def diasToAdd = dias

//        println "dias add:" + dias
//        println "periodos add: " + periodosAdd
//        println "fin: " + fechaFinPer

        def inicio = fechaFinPer + 1
        def fin = fechaFinPer + 30
        def next = ultimoPer + 1

//        println "Periodos anteriores: "
//        periodosEj.each {
//            println it.numero + "  " + it.fechaInicio.format("dd-MM-yyyy") + "  ->  " + it.fechaFin.format("dd-MM-yyyy") + " (dias: " + (it.fechaFin - it.fechaInicio + 1) + ")"
//        }
//        println "\nPeriodos nuevos"
        periodosAdd.times {
            if (!errores) {
                def add
                if (diasToAdd > 30) {
                    add = 30
                } else {
                    add = diasToAdd
                }
                if (add > 0) {
                    fin = inicio + (add - 1)
                    def newPer = new PeriodoEjecucion([
                            obra       : obra,
                            numero     : next,
                            tipo       : "P",
                            fechaInicio: inicio,
                            fechaFin   : fin
                    ])
                    if (!newPer.save(flush: true)) {
                        errores = true
                        println "ERROR!!!!: " + newPer.errors
                    }
//                println newPer.numero + "  " + newPer.fechaInicio.format("dd-MM-yyyy") + "  ->  " + newPer.fechaFin.format("dd-MM-yyyy") + " (dias: " + (newPer.fechaFin - newPer.fechaInicio + 1) + ")"
                    inicio = fin + 1
                    next++
                    diasToAdd -= add
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

//        if (!params.id) {
//            params.id = "5"
//        }
//println params
        def contrato = Contrato.get(params.id)
//println contrato
        if (!contrato) {
            flash.message = "No se encontró el contrato"
            flash.clase = "alert-error"
//            println flash.message
            redirect(action: "errores", params: [contrato: params.id])
            return
        }
//        def obra = contrato?.oferta?.concurso?.obra

        def obraOld = contrato?.oferta?.concurso?.obra
//        println "oblraOld:::: $obraOld"

        if (!obraOld) {
            flash.message = "No se encontró la obra"
            flash.clase = "alert-error"
            redirect(controller: 'contrato', action: "registroContrato", params: [contrato: params.id])
            return
        }

        def obra = Obra.findByCodigo(obraOld.codigo+"-OF")
        if(!obra) {
            obra = obraOld
        }

        if (!obra) {
            flash.message = "No se encontró la obra"
            flash.clase = "alert-error"
//            println flash.message
            redirect(action: "errores", params: [contrato: params.id])
//            redirect(controller: 'contrato', action: "registroContrato", params: [contrato: params.id])
            return
        }
        if (!obra.fechaInicio) {
            flash.message = "La obra no tiene fecha de inicio. Por favor solucione el problema. " + obra.id
            flash.clase = "alert-error"
//            println flash.message
            redirect(action: "errores", params: [contrato: params.id])
//            redirect(controller: 'contrato', action: "registroContrato", params: [contrato: params.id])
            return
        }
//        println "Index contrato: $contrato"
//        println "Index obra: $obra"
        //copia el cronograma del contrato (crng) a la tabla cronograma de ejecucion (crej)

        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def inicioObra = obra.fechaInicio

        def cronogramas = CronogramaEjecucion.countByVolumenObraInList(detalle)

        def continua = true
//        println "datos de cronograma $cronogramas"
        if (cronogramas == 0) {
//            println "no hay datos de cronograma ... inicia cargado"
            detalle.each { vol ->
                def cronoCon = CronogramaContrato.findAllByVolumenObra(vol)
                cronoCon.eachWithIndex { crono, cont ->

                    def dias = (crono.periodo - 1) * 30 //+ (crono.periodo - 1)
                    def prdo = 0
                    if((dias + 30) > contrato.plazo){
                        prdo = contrato.plazo - dias
                    }else {
                        prdo = 30
                    }

//                    println ">>>" + dias
                    def ini
                    def fin
                    use(TimeCategory) {
                        ini = inicioObra + dias.days
                    }
                    use(TimeCategory) {
//                        fin = ini + 29.days        // 30 - 1 para contar el dia inicial
                        fin = ini + (prdo - 1).toInteger().days        // 30 - 1 para contar el dia inicial
                    }

                    def periodo = PeriodoEjecucion.withCriteria {
                        eq("obra", obra)
                        eq("tipo", "P")
                        eq("numero", crono.periodo)
                        eq("fechaInicio", ini)
                        eq("fechaFin", fin)
                    }

                    if (periodo.size() == 0) {
//                        println "crea el periodo con inicio: $ini, fin: $fin, dias: $prdo, plazo: ${contrato.plazo}"
                        periodo = new PeriodoEjecucion([
                                obra       : obra,
                                numero     : crono.periodo,
                                tipo       : "P",
                                fechaInicio: ini,
                                fechaFin   : fin,
                                contrato   :  contrato
                        ])
                        if (!periodo.save(flush: true)) {
                            println "Error al guardar el periodo " + periodo.errors
                            continua = false
                        }
                    } else if (periodo.size() == 1) {
                        println "existe un periodo"
                        periodo = periodo[0]
                    } else {
                        println "WTF existe mas de un periodo"
                        continua = false
                    }
                    if (continua) {
                        def cronoEjecucion = new CronogramaEjecucion([
                                volumenObra: vol,
                                periodo    : periodo,
                                precio     : crono.precio,
                                porcentaje : crono.porcentaje,
                                cantidad   : crono.cantidad
                        ])
                        if (!cronoEjecucion.save(flush: true)) {
                            println "Error al guardar el crono ejecucion del crono " + crono.id
                            println cronoEjecucion.errors
                        } else {
                            println "ok " + crono.id + "  =>  " + cronoEjecucion.id
                        }
                    }//if continua
                } //cronogramaContrato.each
            } //detalles.each
        } //if cronogramas == 0

        /*
          def modificacion = new Modificaciones([
                obra: obra,
                tipo: "S",
                dias: dias,
                fecha: new Date(),
                fechaInicio: ini,
                fechaFin: finSusp,
                motivo: params.motivo,
                observaciones: params.observaciones,
                memo: params.memo.toUpperCase()
        ])
         */
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
//        println "suspensiones "+suspensiones
        return [obra: obra, contrato: contrato, suspensiones: suspensiones, ini: ini]
    }

    def index_old() {
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
//            println flash.message
            redirect(action: "errores", params: [contrato: params.id])
            return
        }
        def obra = contrato?.oferta?.concurso?.obra
        if (!obra) {
            flash.message = "No se encontró la obra"
            flash.clase = "alert-error"
//            println flash.message
            redirect(action: "errores", params: [contrato: params.id])
//            redirect(controller: 'contrato', action: "registroContrato", params: [contrato: params.id])
            return
        }
        if (!obra.fechaInicio) {
            flash.message = "La obra no tiene fecha de inicio. Por favor solucione el problema. " + obra.id
            flash.clase = "alert-error"
//            println flash.message
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
                    def dias = (crono.periodo - 1) * 30 + (crono.periodo - 1)
//                    println ">>>" + dias
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
//                        println "ok " + crono.id + "  =>  " + cronoEjecucion.id
                    }
                } else {
//                    println "Ya hay"
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

    def actualizaPems() {
        /** en base a prej ingresa o actualiza dato en pems **/
        println "actualizaPems params: $params"
        def cntr = Contrato.get(params.contrato)
        def prej = PeriodoEjecucion.findAllByContrato(cntr)
        def pems
        def fcin     //fecha de incio periodo pems
        def fcfn     //fecha de fin periodo pems
        def fcim     //fecha de inicio de mes
        def fcfm     //fecha de fin de mes
        def vlor = 0.0
        def parcial = 0.0
        prej.each {pe ->

            fcin = pe.fechaInicio
            fcfm = preciosService.ultimoDiaDelMes(fcin)
            fcim = fcfm + 1
            fcfn = pe.fechaFin
            println "$pe: fcin: $fcin, fcfn: $fcfn, fcfm: $fcfm, fcim: $fcim"

            if (fcfm < pe.fechaFin) {
                vlor = CronogramaEjecucion.executeQuery("select sum(precio) from CronogramaEjecucion where periodo = :p", [p: pe])
                parcial = vlor /(pe.fechaFin - pe.fechaInicio) * (fcfm - fcin)
                println "parcial: $parcial"
                pems = PeriodoEjecucionMes.findAllByContratoAndObraAndPeriodoEjecucionAndFechaInicioAndFechaFin(cntr, cntr.obra, pe, fcin, fcfm)
                if(pems){
                    //* actualiza el periodo actual **/
                    pems.parcialCronograma = parcial
                } else {
                    pems = new PeriodoEjecucionMes()
                    pems.contrato = cntr
                    pems.obra = cntr.obra
                    pems.periodoEjecucion = pe
                    pems.fechaInicio = fcin
                    pems.fechaFin = fcfm
                }
                if (!pems.save(flush: true)) {
                    flash.message = "No se pudo actualizar pems"
                    println "Error al actualizar pems: " + pems.errors
                } else {
                    flash.message = "Pems actualizado exitosamente"
                    redirect(controller: "cronogramaEjecucion", action: "index", id: cntr.id)
                }

                fcin = fcfm + 1
                fcfn = pe.fechaFin
                parcial = vlor - parcial

                pems = PeriodoEjecucionMes.findAllByContratoAndObraAndPeriodoEjecucionAndFechaInicioAndFechaFin(cntr, cntr.obra, pe, fcin, fcfn)
                if(pems){
                    //* actualiza el periodo actual **/
                    pems.parcialCronograma = parcial
                } else {
                    pems = new PeriodoEjecucionMes()
                    pems.contrato = cntr
                    pems.obra = cntr.obra
                    pems.periodoEjecucion = pe
                    pems.fechaInicio = fcin
                    pems.fechaFin = fcfn
                }
                if (!pems.save(flush: true)) {
                    flash.message = "No se pudo actualizar pems"
                    println "Error al actualizar pems: " + pems.errors
                } else {
                    flash.message = "Pems actualizado exitosamente"
                    redirect(controller: "cronogramaEjecucion", action: "index", id: cntr.id)
                }

            } else {
                /** ingresar solo los dias restantes ver: ../Documentos/pems.sql **/
            }
        }
//        def pems = PeriodoEjecucionMes.


        render "ok"
    }

} //fin controller
