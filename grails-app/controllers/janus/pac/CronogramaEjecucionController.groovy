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
                        obra: obra,
                        numero: num,
                        tipo: "S",
                        fechaInicio: ini,
                        fechaFin: finSusp
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
                            obra: obra,
                            numero: per.numero,
                            tipo: "P",
                            fechaInicio: nuevoIni2,
                            fechaFin: nuevoFin2
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
                                periodo: periodo2,
                                precio: precio2,
                                porcentaje: porcentaje2,
                                cantidad: cantidad2
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

    def ampliacion_ajax() {}

    def ampliacion() {
//        println params
        def dias = params.dias.toInteger()
        def obra = Obra.get(params.obra)

        def suspension = PeriodoEjecucion.findByObraAndTipo(obra, "S", [sort: 'fechaInicio', order: "desc"])

        def periodos

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
            def res = preciosService.presioUnitarioVolumenObra("sum(parcial)+sum(parcial_t) precio ", obra.id, vol.item.id)
//            precios.put(vol.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
            def precio = (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2)
            cronos.add([
                    codigo: vol.item.codigo,
                    nombre: vol.item.nombre,
                    unidad: vol.item.unidad.codigo,
                    cantidad: vol.cantidad,
                    precioU: precio,
                    parcial: precio * vol.cantidad,
                    volumen: vol
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
            row2 += " (" + (per.fechaFin - per.fechaInicio) + " días)"
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
            html += formatNumber(number: crono.cantidad, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
            html += "</td>"

            html += "<td class='num precioU'>"
            html += formatNumber(number: crono.precioU, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
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
                    filaDol += g.formatNumber(number: cronoPer.precio, minFractionDigits: 2, maxFractionDigits: 2, format:"##,##0", locale: "ec")
                    filaPor += g.formatNumber(number: cronoPer.porcentaje, minFractionDigits: 2, maxFractionDigits: 2, format:"##,##0", locale: "ec")
                    filaCan += g.formatNumber(number: cronoPer.cantidad, minFractionDigits: 2, maxFractionDigits: 2, format:"##,##0", locale: "ec")
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
            html += formatNumber(number: crono.parcial, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
            totalCosto += crono.parcial
            html += "</td>"
            html += '<td>$</td>'
            html += filaDol
            html += "<td class='num dol total totalRubro'>"
            html += formatNumber(number: totDol, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
            html += "</td>"
            html += "</tr>"

            html += "<tr class='item_prc'>"
            html += '<td colspan="6"> </td>'
            html += '<td>%</td>'
            html += filaPor
            html += "<td class='num prct total totalRubro'>"
            html += formatNumber(number: totPor, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
            html += "</td>"
            html += "</tr>"

            html += "<tr class='item_f'>"
            html += '<td colspan="6"> </td>'
            html += '<td>F</td>'
            html += filaCan
            html += "<td class='num fis total totalRubro'>"
            html += formatNumber(number: totCan, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
            html += "</td>"
            html += "</tr>"
        }
        html += "</tbody>"

        html += "<tfoot>"
        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='4'>TOTAL PARCIAL</td>"
        html += "<td class='num'>"
        html += formatNumber(number: totalCosto, minFractionDigits: 2, maxFractionDigits: 2, format:"##,##0", locale: "ec")
        html += "</td>"
        html += "<td>T</td>"
        def filaDolAcum = "", filaPor = "", filaPorAcum = "", sumaDol = 0, sumaPor = 0
        totalesDol.each {
            def por = (100 * it) / totalCosto
            sumaDol += it
            sumaPor += por
            html += "<td class='num'>"
            html += formatNumber(number: it, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
            html += "</td>"

            filaDolAcum += "<td class='num'>"
            filaDolAcum += formatNumber(number: sumaDol, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
            filaDolAcum += "</td>"

            filaPor += "<td class='num'>"
            filaPor += formatNumber(number: por, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
            filaPor += "</td>"

            filaPorAcum += "<td class='num'>"
            filaPorAcum += formatNumber(number: sumaPor, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
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
            def res = preciosService.presioUnitarioVolumenObra("sum(parcial)+sum(parcial_t) precio ", obra.id, vol.item.id)
//            precios.put(vol.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
            def precio = (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2)
            def mapaVol = [
                    codigo: vol.item.codigo,
                    nombre: vol.item.nombre,
                    unidad: vol.item.unidad.codigo,
                    cantidad: vol.cantidad,
                    precioU: precio,
                    parcial: precio * vol.cantidad,
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
                            ini: cc.fechaInicio,
                            fin: cc.fechaFin,
                            tipo: cc.tipo,
                            num: cc.periodo
                    ])
                }
                mapaVol.periodos.add([
                        tipo: cc.tipo,
                        num: cc.periodo,
                        cantidad: cc.cantidad,
                        precio: cc.precio,
                        porcentaje: cc.porcentaje,
                        ini: cc.fechaInicio,
                        fin: cc.fechaFin,
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
            html += formatNumber(number: crono.cantidad, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
            html += "</td>"

            html += "<td class='num precioU'>"
            html += formatNumber(number: crono.precioU, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
            html += "</td>"

            html += "<td class='num subtotal'>"
            html += formatNumber(number: crono.parcial, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
            totalCosto += crono.parcial
            html += "</td>"

            html += '<td>$</td>'

            crono.periodos = crono.periodos.sort { it.ini }

            def tot = 0
            periodos.eachWithIndex { per, i ->
                def cronoPer = crono.periodos.find { it.tipo == per.tipo && it.ini == per.ini && it.fin == per.fin }
                html += "<td class='dol num'>"
                if (cronoPer) {
                    html += g.formatNumber(number: cronoPer.precio, minFractionDigits: 2, maxFractionDigits: 2, format:"##,##0", locale: "ec")
                    tot += cronoPer.precio
                    totalesDol[i] += cronoPer.precio
                }
                html += "</td>"
            }
            html += "<td class='num dol total totalRubro'>"
            html += formatNumber(number: tot, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
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
                    html += g.formatNumber(number: cronoPer.porcentaje, minFractionDigits: 2, maxFractionDigits: 2, format:"##,##0", locale: "ec")
                    tot += cronoPer.porcentaje
                    totalesPor[i] += cronoPer.porcentaje
                }
                html += "</td>"
            }

            html += "<td class='num prct total totalRubro'>"
            html += formatNumber(number: tot, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
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
                    html += g.formatNumber(number: cronoPer.cantidad, minFractionDigits: 2, maxFractionDigits: 2, format:"##,##0", locale: "ec")
                    tot += cronoPer.cantidad
                    totalesCant[i] += cronoPer.cantidad
                }
                html += "</td>"
            }
            html += "<td class='num fis total totalRubro'>"
            html += formatNumber(number: tot, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
            html += "</td>"
            html += "</tr>"

        }

        html += "<tfoot>"

        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='4'>TOTAL PARCIAL</td>"
        html += "<td class='num'>"
        html += formatNumber(number: totalCosto, minFractionDigits: 2, maxFractionDigits: 2, format:"##,##0", locale: "ec")
        html += "</td>"
        html += "<td>T</td>"
        def filaDolAcum = "", filaPor = "", filaPorAcum = "", sumaDol = 0, sumaPor = 0
        totalesDol.each {
            def por = (100 * it) / totalCosto
            sumaDol += it
            sumaPor += por
            html += "<td class='num'>"
            html += formatNumber(number: it, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
            html += "</td>"

            filaDolAcum += "<td class='num'>"
            filaDolAcum += formatNumber(number: sumaDol, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
            filaDolAcum += "</td>"

            filaPor += "<td class='num'>"
            filaPor += formatNumber(number: por, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
            filaPor += "</td>"

            filaPorAcum += "<td class='num'>"
            filaPorAcum += formatNumber(number: sumaPor, maxFractionDigits: 2, minFractionDigits: 2, format:"##,##0", locale: "ec")
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
            flash.message = "La obra no tiene fecha de inicio. Por favor solucione el problema. " + obra.id
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

        def cronogramas = CronogramaEjecucion.countByVolumenObraInList(detalle)

        def continua = true

        if (cronogramas == 0) {
            detalle.each { vol ->
                CronogramaContrato.findAllByVolumenObra(vol).eachWithIndex { crono, cont ->

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

                    def periodo = PeriodoEjecucion.withCriteria {
                        eq("obra", obra)
                        eq("tipo", "P")
                        eq("numero", crono.periodo)
                        eq("fechaInicio", ini)
                        eq("fechaFin", fin)
                    }

                    if (periodo.size() == 0) {
                        println "crea el periodo"
                        periodo = new PeriodoEjecucion([
                                obra: obra,
                                numero: crono.periodo,
                                tipo: "P",
                                fechaInicio: ini,
                                fechaFin: fin
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
                                periodo: periodo,
                                precio: crono.precio,
                                porcentaje: crono.porcentaje,
                                cantidad: crono.cantidad
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

        return [obra: obra, contrato: contrato]
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
            flash.message = "La obra no tiene fecha de inicio. Por favor solucione el problema. " + obra.id
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
                        println "ok " + crono.id + "  =>  " + cronoEjecucion.id
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
} //fin controller
