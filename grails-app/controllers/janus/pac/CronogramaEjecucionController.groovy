package janus.pac

import groovy.time.TimeCategory
import janus.Contrato
import janus.Modificaciones
import janus.Obra
import janus.VolumenContrato
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
        def max = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaInicio']).last().fechaFin
        def minDate = min.format("yyyy") + "," + (min.format("MM").toInteger() - 1) + "," + min.format("dd")
        def maxDate = max.format("yyyy") + "," + (max.format("MM").toInteger() - 1) + "," + max.format("dd")
        return [min: minDate, max: maxDate]
    }


    def terminaSuspension_ajax() {
        println "terminaSuspension_ajax: $params"
        def obra = Obra.get(params.obra)
        def fi = Modificaciones.withCriteria {
            eq("obra", obra)
            eq("tipo", "S")
            isNull("fechaFin")
            projections {
                max("fechaInicio")
            }
        }
        println "suspensión en curso: ${fi}"
        def suspension = "Suspensión iniciada el ${fi.first().format('dd-MM-yyyy'.toString())}"
        def minDate = fi.first().format("yyyy") + "," + (fi.first().format("MM").toInteger() - 1) + "," + fi.first().format("dd")

        return [min: minDate, suspension: suspension]
    }

    /* Agregado el 25-03-2015 xq quieren hacer suspensiones sin fecha de fin: se divide el proceso en 2 partes:
            primero hace una modificacion tipo suspension sin fecha de fin y con -1 en dias
            segundo el action terminaSuspension modifica la modificacino para ponerle fecha fin y dias y recalcula las fechas de periodos del cronograma
     */

    def suspensionNueva() {
        println "suspensionNueva, params: $params"
        def cntr = Contrato.get(params.cntr)
        def obra = cntr.obra
        def periodos = PeriodoEjecucion.findAllByContratoAndObra(cntr, obra, [sort: 'fechaInicio'])
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
                finSusp = fin - 1
                dias = finSusp - ini + 1
            }
            println "fcha fin: $finSusp"

            /* crea la modificación */
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
                    params.fcfn = fin.format("dd-MM-yyyy")
                    println "registra suspesión e invoca a terminaSuspensionTemp con $params.fcfn"
                    terminaSuspensionNuevo()
                }
                render "OK"
            }
        }
    }

    /* Suspensiones sin fecha de fin: se divide el proceso en 2 partes:
       1. action suspensionNueva: hace una modificacion tipo suspension sin fecha de fin y con -1 en dias
       2. terminaSuspension: pone fecha fin y dias a la suspensión y recalcula las fechas de periodos del cronograma
     */

    def suspension() {
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

        def num = 1

        def anterior = null
        def moved = false

        periodos.eachWithIndex { PeriodoEjecucion per, int i ->
            def nuevoIni, nuevoFin
            if (per.fechaInicio <= ini && per.fechaFin >= ini) {
                def dias1 = ini - per.fechaInicio
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

                if (dias1 == 0) {
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
                    anterior = nuevoFin

                } else {
                    def diasPeriodo = per.fechaFin - per.fechaInicio
                    nuevoIni = per.fechaInicio
                    use(TimeCategory) {
                        nuevoFin = ini - 1.days
                    }
                    def diasParte1 = nuevoFin - nuevoIni
                    per.fechaInicio = nuevoIni
                    per.fechaFin = nuevoFin
                    if (!per.save(flush: true)) {
                        println "error 2: " + per.errors
                    }

                    def diasParte2 = diasPeriodo - diasParte1
                    def nuevoIni2 = fin
                    def nuevoFin2
                    use(TimeCategory) {
                        nuevoFin2 = nuevoIni2 + diasParte2.days
                    }
                    anterior = nuevoFin2

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

                    CronogramaEjecucion.findAllByPeriodo(per).eachWithIndex { CronogramaEjecucion crono, int j ->
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
                    def diasPeriodo = per.fechaFin - per.fechaInicio
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
                    anterior = nuevoFin
                } else {
                }
            }
        }
        render "OK"
    }

    def modificacion_ajax() {
        def contrato = Contrato.get(params.contrato.toLong())
        def obra = contrato.obra
        def vol = VolumenesObra.get(params.vol.toLong())
        def totlDol = 0, totlCan = 0


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

        totlCan = vol.cantidad
        totlDol = vol.cantidad * precio


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

//        println "cantModificable0: " + cantModificable

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
//            println "cantModificable1: " + cantModificable
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
//                println "cantModificable2: " + cantModificable
//                totalesDol[i] += cronosPer.precio
            }
            filaDol += "</td>"
            filaPor += "</td>"
            filaCan += "</td>"
        }
//        println "cantModificable3: " + cantModificable
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
//            println "cantModificable4[${i}]: " + cantModificable[i]

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

//            println "\t\ttotalPlanilla=" + totalPlanilla + "   porPla=" + porPla + "   canPla=" + canPla

            if (modificable) {
                def dol = cantModificable[i].dol - totalPlanilla
                def por = cantModificable[i].por - porPla
                def can = cantModificable[i].can - canPla
                def maxDol = numero(dol.toDouble().round(2))
                def maxPor = numero(por.toDouble().round(2))
                def maxCan = numero(can.toDouble().round(2))

//                println "cantModificable6[${i}]: " + cantModificable[i]

                maxDolAcu += dol.toDouble()
                maxPrctAcu += por.toDouble()
                maxCanAcu += can.toDouble()

//                println "\tdol=" + dol + "  por=" + por + "  can=" + can + "  maxDol=" + maxDol + "   maxPor=" + maxPor + "   maxCan=" + maxCan +
//                        "   maxDolAcu=" + maxDolAcu + "  maxPrctAcu=" + maxPrctAcu + "  maxCanAcu=" + maxCanAcu


                filaDolMod += "<input type='text' class='input-mini tiny dol p${i}' value='" + "0.00" +/* maxDol +*/
                        "' data-tipo='dol' data-total='${totlDol}' data-periodo='${i}' data-max='" + maxDolAcu + "' " +
                        " data-id='${cantModificable[i].crono}' data-id2='${periodo.id}' data-id3='${cronos.volumen.id}' " +
                        " data-val1='${totalPlanilla.toDouble().round(2)}' /> " /*+
                        "(max. ${maxDolAcu.toDouble().round(2)})"*/
                filaPorMod += "<input type='text' class='input-mini tiny prct p${i}' value='" + "0.00" + /*maxPor +*/
                        "' data-tipo='prct' data-total='${totPor}' data-periodo='${i}' data-max='" + maxPrctAcu + "' " +
                        " data-id='${cantModificable[i].crono}' data-id2='${periodo.id}' data-id3='${cronos.volumen.id}' " +
                        " data-val1='${porPla.toDouble().round(2)}'  /> " /*+
                        "(max. ${maxPrctAcu.toDouble().round(2)})"*/
                filaCanMod += "<input type='text' class='input-mini tiny fis p${i}' value='" + "0.00" + /*maxCan +*/
                        "' data-tipo='fis' data-total='${totlCan}' data-periodo='${i}' data-max='" + maxCanAcu + "' " +
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
        html += numero(totDol)
        html += "</td>"
        html += "</tr>"

        html += "<tr class='click item_prc'>"
        html += '<td>%</td>'
        html += filaPor
        html += "<td class='num prct total totalRubro'>"
        html += numero(totPor)
        html += "</td>"
        html += "</tr>"

        html += "<tr class='click item_f '>"
        html += '<td>F</td>'
        html += filaCan
        html += "<td class='num fis total totalRubro'>"
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
//            println ".. " + periodo + "\t" + mod
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
            ultimoPeriodo.fechaFin = fcfn + dias
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
                fcfn = fcin + dias
                dias -= (fcfn - fcin)
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
//        println ".....modificarVolumen" + crejs
    }

    def tabla() {
        def inicio = new Date()
        def obra = Obra.get(params.id)
        def html = ""

        println "tabla: $params"
        def desde = params?.desde?.toInteger() ?: 1
        def hasta = params?.hasta?.toInteger() ?: 10
        def offset = desde - 1


        def precios = [:]
        def indirecto = obra.totales / 100

        def periodos = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaInicio'])
        def cronos = []

//        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden", max: hasta - desde + 1, offset: offset])

//        println "detalle: $detalle"

        def res = preciosService.rbro_pcun_v2(obra.id)

        detalle.each { vol ->
//            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, vol.item.id)
//            def precio = (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2)
            cronos.add([
                    codigo  : vol.item.codigo,
                    nombre  : vol.item.nombre,
                    unidad  : vol.item.unidad.codigo,
                    cantidad: vol.cantidad,
                    precioU : res.find { it.vlob__id == vol.id }.pcun,
                    parcial : res.find { it.vlob__id == vol.id }.totl,
                    volumen : vol
            ])
        }//detalles.each

        def fin = new Date()
//        println "cronogramaObraEjec: detalles --> ${TimeCategory.minus(fin, inicio)}"

        def row2 = ""

        def totalCosto = 0, totalesDol = [], totalesPor = [], totalesCant = []

        html += '<table class="table table-bordered table-condensed table-hover">'
        html += '<thead>'
        html += '<tr>'
        html += '<th rowspan="2" style="width:70px;">'
        html += 'Código'
        html += '</th>'
        html += '<th rowspan="2" style="width:220px;">'
        html += 'Rubro'
        html += '</th>'
        html += '<th rowspan="2" style="width:26px;">'
//        html += 'Unidad'
        html += 'U.'
        html += '</th>'
        html += '<th rowspan="2" style="width:60px;">'
        html += 'Cantidad Unitario Total'
        html += '</th>'
/*
        html += '<th rowspan="2" style="width:60px;">'
        html += 'Unitario'
        html += '</th>'
        html += '<th rowspan="2" style="width:60px;">'
        html += 'C.Total'
        html += '</th>'
*/
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

        fin = new Date()
        println "cronogramaObraEjec: inicia tabla --> ${TimeCategory.minus(fin, inicio)}"


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
            html += numero(crono.cantidad)
            html += "</td>"

/*
            html += "<td class='num precioU'>"
            html += numero(crono.precioU)
            html += "</td>"
*/

            def filaDol = "", filaPor = "", filaCan = ""
            def totDol = 0, totPor = 0, totCan = 0
            periodos.eachWithIndex { periodo, i ->
                def cronoPer = CronogramaEjecucion.findAllByVolumenObraAndPeriodo(crono.volumen, periodo)
                filaDol += "<td class='dol num ${periodo.tipo}'>"
                filaPor += "<td class='prct num ${periodo.tipo}'>"
                filaCan += "<td class='fis num ${periodo.tipo}'>"
                if (cronoPer.size() == 1) {
                    cronoPer = cronoPer[0]
                    filaDol += numero(cronoPer.precio)
                    filaPor += numero(cronoPer.porcentaje)
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

//            html += "<td class='num subtotal'>"
            crono.parcial = Math.round(crono.parcial.toDouble() * 100) / 100
//            html += numero(crono.parcial) + "#"
            totalCosto += crono.parcial
//            html += "</td>"
            html += '<td>$</td>'
            html += filaDol
            html += "<td class='num dol total totalRubro'>"
            html += numero(totDol)
            html += "</td>"
            html += "</tr>"

            html += "<tr class='click item_prc ${crono.volumen.rutaCritica == 'S' ? 'rutaCritica' : ''}'>"
            html += '<td colspan="3"> </td>'
            html += "<td class='num precioU'>"
            html += numero(crono.precioU)
            html += "</td>"
//            html += '<td colspan="2"> </td>'


            html += '<td>%</td>'
            html += filaPor
            html += "<td class='num prct total totalRubro'>"
            if (totPor != 100) {
                println "****** ${crono}"
            }
            html += numero(totPor)
            html += "</td>"
            html += "</tr>"

            html += "<tr class='click item_f ${crono.volumen.rutaCritica == 'S' ? 'rutaCritica' : ''}'>"
//            html += '<td colspan="6"> </td>'
            html += '<td colspan="3"> </td>'
            html += "<td class='num subtotal'>"
            crono.parcial = Math.round(crono.parcial.toDouble() * 100) / 100
            html += numero(crono.parcial) + "</td>"
//            html += '<td colspan="2"> </td>'


            html += '<td>F</td>'
            html += filaCan
            html += "<td class='num fis total totalRubro'>"
            html += numero(totCan)
            html += "</td>"
            html += "</tr>"
        }
        html += "</tbody>"

        html += "<tfoot>"
        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='2'>TOTAL PARCIAL</td>"
        html += "<td class='num'>"
        totalCosto = totalCosto.toDouble().round(2)
        html += numero(totalCosto)
        html += "</td>"
        html += "<td>T</td>"

        fin = new Date()
        println "cronogramaObraEjec: totales --> ${TimeCategory.minus(fin, inicio)}"


        def filaDolAcum = "", filaPor = "", filaPorAcum = "", sumaDol = 0, sumaPor = 0
        totalesDol.each {
//            println "valor: $it  --> ${it.toDouble().round(2)}"
            it = it.toDouble().round(2)
            def por = ((100 * it) / totalCosto).round(2)
            sumaDol += it
            sumaPor += por
            html += "<td class='num'>"
            html += numero(it)
            html += "</td>"

            filaDolAcum += "<td class='num'>"
            filaDolAcum += numero(sumaDol)
            filaDolAcum += "</td>"

            filaPor += "<td class='num'>"
            filaPor += numero(por)
            filaPor += "</td>"

            filaPorAcum += "<td class='num'>"
            filaPorAcum += numero(sumaPor)
            filaPorAcum += "</td>"
        }
        html += "<td></td>"
        html += "</tr>"

        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='2'>TOTAL ACUMULADO</td>"
        html += "<td class='num'>"
        html += "</td>"
        html += "<td>T</td>"
        html += filaDolAcum
        html += "<td></td>"
        html += "</tr>"

        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='2'>% PARCIAL</td>"
        html += "<td class='num'>"
        html += "</td>"
        html += "<td>T</td>"
        html += filaPor
        html += "<td></td>"
        html += "</tr>"

        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='2'>% ACUMULADO</td>"
        html += "<td class='num'>"
        html += "</td>"
        html += "<td>T</td>"
        html += filaPorAcum
        html += "<td></td>"
        html += "</tr>"
        html += "</tfoot>"

        html += "</table>"

/*
        fin = new Date()
        println "cronogramaObraEjec: fin"
        println "${TimeCategory.minus(fin, inicio)}"
*/

//        println "retorna.... ${html.size()} bytes"
        return [detalle: detalle, precios: precios, obra: obra, tabla: html]
    }

    def tablaNueva() {
        def inicio = new Date()
        def cntr = Contrato.get(params.id)
        def obra = cntr.obra
        def html = ""

        println "tablaNueva: $params"
        def desde = params?.desde?.toInteger() ?: 1
        def hasta = params?.hasta?.toInteger() ?: 10
        def offset = desde - 1


        def precios = [:]
        def indirecto = obra.totales / 100

        def periodos = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaInicio'])
        def cronos = []

//        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden", max: hasta - desde + 1, offset: offset])
        def detalle = VolumenContrato.findAllByContrato(cntr, [sort: "volumenOrden", max: hasta - desde + 1, offset: offset])
        def clase = ''

        println "detalle: ${detalle.size()}"

        detalle.each { vol ->
            cronos.add([
                    codigo  : vol.item.codigo,
                    nombre  : vol.item.nombre,
                    unidad  : vol.item.unidad.codigo,
                    cantidad: vol.volumenCantidad,
                    precioU : vol.volumenPrecio,
                    parcial : vol.volumenSubtotal,
                    volumen : vol,
                    compl   : vol.contratoComplementario ?: 0
            ])
/*
            if (vol.contratoComplementario) {
                println "CC cant: ${vol.volumenCantidad}, pcun: ${vol.volumenPrecio}, parcial: ${vol.volumenSubtotal}"
            }
*/

        }//detalles.each


        def fin = new Date()
        def row2 = ""

        def totalCosto = 0, totalesDol = [], totalesPor = [], totalesCant = []

        html += '<table class="table table-bordered table-condensed table-hover">'
        html += '<thead>'
        html += '<tr>'
        html += '<th rowspan="2" style="width:70px;">'
        html += 'Código'
        html += '</th>'
        html += '<th rowspan="2" style="width:220px;">'
        html += 'Rubro'
        html += '</th>'
        html += '<th rowspan="2" style="width:26px;">'
//        html += 'Unidad'
        html += '*'
        html += '</th>'
        html += '<th rowspan="2" style="width:60px;">'
        html += 'Cantidad Unitario Total'
        html += '</th>'
/*
        html += '<th rowspan="2" style="width:60px;">'
        html += 'Unitario'
        html += '</th>'
        html += '<th rowspan="2" style="width:60px;">'
        html += 'C.Total'
        html += '</th>'
*/
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

        fin = new Date()
        println "cronogramaObraEjec: inicia tabla --> ${TimeCategory.minus(fin, inicio)}"


        cronos.each { crono ->
            clase = crono.compl != 0 ? 'cmplcss' : ''
            html += "<tr class='click item_row ${crono.volumen.rutaCritica == 'S' ? 'rutaCritica' : ''} ${clase}' data-vol='" +
                    crono.volumen.id + "'>"

            html += "<td class='codigo'>"
            html += crono.codigo
            html += "</td>"

            html += "<td class=''nombre>"
            html += crono.nombre
            html += "</td>"

            html += "<td class='unidad' style='text-align: center;'>"
//            html += crono.unidad
            html += "Subtt"
            html += "</td>"

            html += "<td class='num cantidad'>"
            crono.parcial = Math.round(crono.parcial.toDouble() * 100) / 100
            html += numero(crono.parcial) + "</td>"     /* subtotal  **/
            html += "</td>"

/*
            html += "<td class='num precioU'>"
            html += numero(crono.precioU)
            html += "</td>"
*/

            def filaDol = "", filaPor = "", filaCan = ""
            def totDol = 0, totPor = 0, totCan = 0
            periodos.eachWithIndex { periodo, i ->
//                def cronoPer = CronogramaEjecucion.findAllByVolumenObraAndPeriodo(crono.volumen, periodo)
                def cronoPer = CrngEjecucionObra.findAllByVolumenObraAndPeriodo(crono.volumen, periodo)
                if ((periodo.id == 628) && crono.compl) {
                    println "---- periodo: $periodo, vocr: ${crono.volumen.id} --> ${cronoPer.size()}"
                }

                filaDol += "<td class='dol num ${periodo.tipo}'>"
                filaPor += "<td class='prct num ${periodo.tipo}'>"
                filaCan += "<td class='fis num ${periodo.tipo}'>"
                if (cronoPer.size() == 1) {
                    cronoPer = cronoPer[0]
                    filaDol += numero(cronoPer.precio)
                    filaPor += numero(cronoPer.porcentaje)
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

//            html += "<td class='num subtotal'>"
            crono.parcial = Math.round(crono.parcial.toDouble() * 100) / 100
//            html += numero(crono.parcial) + "#"
            totalCosto += crono.parcial
//            html += "</td>"
            html += '<td>$</td>'
            html += filaDol
            html += "<td class='num dol total totalRubro'>"
            html += numero(totDol)
            html += "</td>"
            html += "</tr>"

            html += "<tr class='click item_prc ${crono.volumen.rutaCritica == 'S' ? 'rutaCritica' : ''}'>"
            html += '<td> </td>'
            html += "<td>Unidad: ${crono.unidad}</td>"
            html += '<td>P.U:</td>'
            html += "<td class='num precioU'>"
            html += numero(crono.precioU) /**** porcentaje ***/
            html += "</td>"
//            html += '<td colspan="2"> </td>'


            html += '<td>%</td>'
            html += filaPor
            html += "<td class='num prct total totalRubro'>"
            if (totPor != 100) {
//                println "--- cronograma: ${crono}"
            }
            html += numero(totPor)
            html += "</td>"
            html += "</tr>"

            html += "<tr class='click item_f ${crono.volumen.rutaCritica == 'S' ? 'rutaCritica' : ''}'>"
//            html += '<td colspan="6"> </td>'
            html += '<td colspan="2"> </td>'
            html += '<td>Cant.</td>'
            html += "<td class='num subtotal'>"
            html += numero(crono.cantidad) + "A"
//            crono.parcial = Math.round(crono.parcial.toDouble() * 100) / 100
//            html += numero(crono.parcial)+ "C" + "</td>"     /* cantidad  **/


            html += '<td>F</td>'
            html += filaCan
            html += "<td class='num fis total totalRubro'>"
            html += numero(totCan)
            html += "</td>"
            html += "</tr>"
        }
        html += "</tbody>"

        html += "<tfoot>"
        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='2'>TOTAL PARCIAL</td>"
        html += "<td class='num'>"
        totalCosto = totalCosto.toDouble().round(2)
        html += numero(totalCosto)
        html += "</td>"
        html += "<td>T</td>"

//        println "html: $html"

        fin = new Date()
        println "cronogramaObraEjec: totales --> ${TimeCategory.minus(fin, inicio)}"


        def filaDolAcum = "", filaPor = "", filaPorAcum = "", sumaDol = 0, sumaPor = 0
        totalesDol.each {
//            println "valor: $it  --> ${it.toDouble().round(2)}"
            it = it.toDouble().round(2)
            def por = ((100 * it) / totalCosto).round(2)
            sumaDol += it
            sumaPor += por
            html += "<td class='num'>"
            html += numero(it)
            html += "</td>"

            filaDolAcum += "<td class='num'>"
            filaDolAcum += numero(sumaDol)
            filaDolAcum += "</td>"

            filaPor += "<td class='num'>"
            filaPor += numero(por)
            filaPor += "</td>"

            filaPorAcum += "<td class='num'>"
            filaPorAcum += numero(sumaPor)
            filaPorAcum += "</td>"
        }
        html += "<td></td>"
        html += "</tr>"

        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='2'>TOTAL ACUMULADO</td>"
        html += "<td class='num'>"
        html += "</td>"
        html += "<td>T</td>"
        html += filaDolAcum
        html += "<td></td>"
        html += "</tr>"

        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='2'>% PARCIAL</td>"
        html += "<td class='num'>"
        html += "</td>"
        html += "<td>T</td>"
        html += filaPor
        html += "<td></td>"
        html += "</tr>"

        html += "<tr>"
        html += "<td></td>"
        html += "<td colspan='2'>% ACUMULADO</td>"
        html += "<td class='num'>"
        html += "</td>"
        html += "<td>T</td>"
        html += filaPorAcum
        html += "<td></td>"
        html += "</tr>"
        html += "</tfoot>"

        html += "</table>"

/*
        fin = new Date()
        println "cronogramaObraEjec: fin"
        println "${TimeCategory.minus(fin, inicio)}"
*/

//        println "retorna.... ${html.size()} bytes"
        return [detalle: detalle, precios: precios, obra: obra, tabla: html]
    }


    def index() {

        println "tabla: $params"
        def desde = params.desde ?: 1
        def hasta = params.hasta?.toInteger() ?: 10
//        hasta = Math.min(hasta, 10)

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

        def comp = Contrato.findByPadreAndTipoContrato(contrato, TipoContrato.findByCodigo('C'))

        return [obra : obra, contrato: contrato, suspensiones: suspensiones, ini: ini, desde: desde.toInteger(),
                hasta: hasta.toInteger(), maximo: 100, complementario: comp?.id ?: 0]
    }

    def indexNuevo() {

        println "indexNuevo: $params"
        def desde = params.desde ?: 1
        def hasta = params.hasta?.toInteger() ?: 10
//        hasta = Math.min(hasta, 10)

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
            isNull("fechaFin")
/*
            or {
                isNull("fechaFin")
                and {
                    le("fechaInicio", new Date().clearTime())
                    gt("fechaFin", new Date().clearTime())
                }
            }
*/
        }

        println "obra: ${obra.id}, suspensiones: ${suspensiones}"

        def ini = Modificaciones.withCriteria {
            eq("obra", obra)
            eq("tipo", "S")
            isNull("fechaFin")
            projections {
                min("fechaInicio")
            }
        }
        println "--suspensiones+: ${ini}"
//        println "..... 2....."

        def comp = Contrato.findByPadreAndTipoContrato(contrato, TipoContrato.findByCodigo('C'))

        return [obra : obra, contrato: contrato, suspensiones: suspensiones, ini: ini.last(), desde: desde.toInteger(),
                hasta: hasta.toInteger(), maximo: 100, complementario: comp?.id ?: 0]
    }


    def actualizaPrej() {
        /** en base a prej ingresa o actualiza dato en prej **/
        println "actualizaPrej params: $params"
        def cntr = Contrato.get(params.cntr)
        def cn = dbConnectionService.getConnection()
        def prej = PeriodoEjecucion.findAllByContratoAndTipoNotEqual(cntr, 'S')
        def vlor
        def cmpl = Contrato.findByPadre(cntr)
        def sql = "update prej set prejcrpa = (select coalesce(sum(creoprco),0) from creo " +
                "where creo.prej__id = prej.prej__id) where cntr__id = ${cntr.id} and prejtipo <> 'S'"
        cn.execute(sql.toString())

        sql = "update prej set prejcntr = (select coalesce(sum(creoprco),0) from creo " +
                "where creo.prej__id = prej.prej__id and vocr__id in (select vocr__id from vocr " +
                "where cntr__id = ${cntr.id} and cntrcmpl is null)) where cntr__id = ${cntr.id} and prejtipo <> 'S'"
        def cnta = cn.executeUpdate(sql.toString())
        if (cmpl) {
            sql = "update prej set prejcmpl = (select coalesce(sum(creoprco),0) from creo " +
                    "where creo.prej__id = prej.prej__id and vocr__id in (select vocr__id from vocr " +
                    "where cntr__id = ${cntr.id} and cntrcmpl is not null)) where cntr__id = ${cntr.id} and " +
                    "prejtipo <> 'S'"
//            println "--> prejcmpl $sql"
            cn.execute(sql.toString())
        }

        if (cnta > 0) {
            flash.message = "Se actualizador ${cnta} registros en períodos de ejecución"
        } else {
            flash.message = "No se pudo actualizar los períodos de ejecución"
            flash.clase = "alert-error"
        }

//        direct(action: "indexNuevo", params: [id: params.contrato])
        render "ok"
    }


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
        println "Id contrato: $contrato"
        println "Id obra: $obra"

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
//                println "crear periodo... pr: $pr, dias: $dias, plazo: ${contrato.plazo} fcha: ${fcha}"

                if ((dias + 30) > contrato.plazo) {
                    prdo = contrato.plazo - dias
                } else {
                    prdo = 30
                }

                fcin = fcha ? fcha + 1 : obra.fechaInicio
                fcfn = fcin + (prdo - 1).toInteger()      // 30 - 1 para contar el dia inicial
                fcfm = preciosService.ultimoDiaDelMes(fcin)
//                println "--------- fcfm: ${fcfm} fcfn: ${fcfn}"
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
//                            println "se ha creado el prej: ${pr} para ${fcin} a ${fcfn}"
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
//                            println "se ha creado el prej: ${pr} para ${fcin} a ${fcfn}"
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
//                            println "se ha creado el prej: ${pr} para ${fcin} a ${fcfn}"
                            flash.message = "Prej actualizado exitosamente"
                        }
                    }
                    fcha = fcfn
                }
            }
        }

//        println "datos de cronograma $cronogramas"
        def cronogramas = CronogramaEjecucion.countByVolumenObraInList(detalle)

        if (cronogramas == 0) {
            println "no hay datos de cronograma ... inicia cargado"
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
                            if ((prco == 0) && (contrato.plazo - 30 * (crono.periodo - 1)) <= ultimo) {
                                mes = ultimo
                            } else {
                                mes = 30
                            }
                            println "dias: $dias, restan: ${contrato.plazo - 30 * (crono.periodo - 1)}, mes = $mes, ultimo: $ultimo"
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

    def creaCrngEjecNuevo() {
        println "creaCrngEjecNuevo para contrato: $params.id"
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
//        println "Id contrato: $contrato, Id obra: $obra"

        /** copia el CronogramaContratado (CRCR) a la tabla cronograma de ejecucion (CREO) CrngEjecucionObra
         *  --Se debe crear primero los prej y luego insertar cada rubro desde
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

//        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def detalle = VolumenContrato.findAllByObra(obra, [sort: "volumenOrden"])
        def periodos = CronogramaEjecucion.executeQuery("select max(periodo) from CronogramaContratado where contrato = :c", [c: contrato])
        def hayPrej = PeriodoEjecucion.findAllByContrato(contrato)
        println "periodos: $periodos --- hayPrej: $hayPrej, obra: ${obra.id}"

        if (!hayPrej) {
            fcin = obra.fechaInicio
            for (pr in (1..periodos[0])) {
                def dias = (pr - 1) * 30 //+ (crono.periodo - 1)
                def prdo = 0
//                println "crear periodo... pr: $pr, dias: $dias, plazo: ${contrato.plazo} fcha: ${fcha}"

                if ((dias + 30) > contrato.plazo) {
                    prdo = contrato.plazo - dias
                } else {
                    prdo = 30
                }

                fcin = fcha ? fcha + 1 : obra.fechaInicio
                fcfn = fcin + (prdo - 1).toInteger()      // 30 - 1 para contar el dia inicial
                fcfm = preciosService.ultimoDiaDelMes(fcin)
//                println "--------- fcfm: ${fcfm} fcfn: ${fcfn}"
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
//                            println "se ha creado el prej: ${pr} para ${fcin} a ${fcfn}"
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
//                            println "se ha creado el prej: ${pr} para ${fcin} a ${fcfn}"
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
//                            println "se ha creado el prej: ${pr} para ${fcin} a ${fcfn}"
                            flash.message = "Prej actualizado exitosamente"
                        }
                    }
                    fcha = fcfn
                }
            }
        }

         def cronogramas = CrngEjecucionObra.countByVolumenObraInList(detalle)
//        println "datos de cronograma $cronogramas"

        if (cronogramas == 0) {
//            println "no hay datos de cronograma ... inicia cargado"
            detalle.each { vol ->
                def cronoCntr = CronogramaContratado.findAllByVolumenContrato(vol, [sort: 'periodo'])
                cronoCntr.each { crono ->

//                    vlor = CronogramaContrato.executeQuery("select sum(precio), sum(porcentaje), sum(cantidad)  from CronogramaContrato where contrato = :c and periodo = :p", [c: contrato, p: crono.periodo])
                    vlor = CronogramaContratado.findByContratoAndVolumenContratoAndPeriodo(contrato, vol, crono.periodo)
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
                            if ((prco == 0) && (contrato.plazo - 30 * (crono.periodo - 1)) <= ultimo) {
                                mes = ultimo
                            } else {
                                mes = 30
                            }
                            println "dias: $dias, restan: ${contrato.plazo - 30 * (crono.periodo - 1)}, mes = $mes, ultimo: $ultimo"
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
                        def cronoEjecucion = new CrngEjecucionObra([
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
            params.cntr = contrato?.id
            actualizaPrej()  /** pone para cada prej los valores de cronograma **/
        } //if cronogramas == 0

//        println "finalizado creaCrngEjec"
        redirect(action: "indexNuevo", params: [obra: obra, id: contrato.id, ini: fcin])
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
        if (params.fcfn) {
            fcfn = new Date().parse("dd-MM-yyyy", params.fcfn)
        }
        if (fcfn) {
            suspensiones = Modificaciones.findAllByContratoAndTipoAndFechaFin(cntr, "S", fcfn)
        } else {
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
        def fechaFinalObra
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
        fechaFinal = iniPrej + plazoActual + diasSusp - 1 // no cuenta fin de suspension y dia final
        fechaFinalObra = iniPrej + plazoActual + diasSusp - 2 // no cuenta fin de suspension y dia final
        println "fecha final de la obra: ${fechaFinal.format('dd-MMM-yyyy')}"

        def cambiar = false
        def fcha = iniPrej

        def i = 0
        println "periodos: $periodos, fcha: $fcha, fechaFinal: $fechaFinal"
        while (fcha < fechaFinal) {   /* puede existir un periodo de 1 día --nuevo*/
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

            println "procesa peridodo: $pr.id, cambiar = $cambiar, desde ${pr.fechaInicio.format('dd-MMM-yyyy')} a " +
                    "${pr.fechaFin.format('dd-MMM-yyyy')}, fcha: ${fcha.format('dd-MMM-yyyy')}"
            if (!cambiar) {
                if (pr.fechaFin >= suspension.fechaInicio) {
                    def prej = PeriodoEjecucion.get(pe.id)
                    prej.fechaFin = suspension.fechaInicio - 1
                    if (!prej.save(flush: true)) {
                        println "Error al crear prej de suspension: " + prej.errors
                    }
                    cambiar = true
                    println "++++inserta perdiodo de suspension"
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
                println "fechaFinal: $fechaFinal,  ultimo día del mes: ${preciosService.ultimoDiaDelMes(fcha)}"
//                if (fechaFinal > preciosService.ultimoDiaDelMes(fcha)) {
                if (fechaFinalObra > preciosService.ultimoDiaDelMes(fcha)) {
                    println "hace 1...."
                    prej.fechaFin = preciosService.ultimoDiaDelMes(fcha)
                    fcha = prej.fechaFin + 1
                } else {
                    println "hace 2...."
//                    prej.fechaFin = fechaFinal
                    prej.fechaFin = fechaFinalObra
//                    fcha = fechaFinal
                    fcha = fechaFinal
                }
                if (!prej.save(flush: true)) {
                    println "Error al crear prej de suspension: " + prej.errors
                }
            }
            if (!cambiar) i++
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
                    if (fraccionContinua) {
                        fraccion = 1 - fraccion
                        fraccionContinua = false
                    } else {
                        if (dias > (prTmp.fechaFin - prTmp.fechaInicio + 1)) {
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
                        if (crej.size() > 1) {
                            pritln "--------------------Error... existe mas de un regisro de vlob: ${c.volumenObra} en prej: ${prNuevos[actual].id} "
                        }
//                        println "periodo actual a procesar: ${prNuevos[actual]} con crej: $crej"
                        if (!crej) {
//                            println "inserta valores .... --> ${c.precio * fraccion}"
                            crejNuevo = new CronogramaEjecucion()
                            crejNuevo.periodo = prNuevos[actual]
                            crejNuevo.volumenObra = c.volumenObra
                            crejNuevo.cantidad = Math.round(c.cantidad * fraccion * 1000) / 1000
                            crejNuevo.porcentaje = Math.round(c.porcentaje * fraccion * 100) / 100
                            crejNuevo.precio = Math.round(c.precio * fraccion * 100) / 100
                            crejNuevo.save(flush: true)
                        } else {
//                            println "incrementa valores .... --> ${c.precio * fraccion}, al vlob: ${c.volumenObra}, actual ${prNuevos[actual]}"
                            def ac_crej = CronogramaEjecucion.get(crej.first().id)
                            ac_crej.cantidad += Math.round((c.cantidad * fraccion).toDouble() * 1000) / 1000
                            ac_crej.porcentaje += Math.round((c.porcentaje * fraccion).toDouble() * 100) / 100
                            ac_crej.precio += Math.round((c.precio * fraccion).toDouble() * 100) / 100
                            ac_crej.save(flush: true)
                        }
                    }
                    if (dias < (prTmp.fechaFin - prTmp.fechaInicio + 1)) {
                        if ((fraccion < 1) && (fraccion > 0)) {
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
    /**
     * copia PREJ actual a prej_t y CREJ actual a crej_t
     * crea nuevos periodos e inserta los valores correspondientes en el cronograma de acuerdo
     * a lo que se ejecuta en el periodo correspondiente
     */

    def terminaSuspensionNuevo() {
        println "termina suspension nuevo (2018) params: $params"
        def cn = dbConnectionService.getConnection()
        def cntr = Contrato.get(params.cntr)
        def prejActual = PeriodoEjecucion.findAllByContrato(cntr)
        def tx1 = ""
        def tx2 = ""

        /** Cargar en prej_t y crej_t el CREJ actual, borrando el anterior **/

        //================== rspalda en Xrej_t valores de cronograma y periodo
        tx1 = "delete from creo_t where prej__id in (select prej__id from prej_t where cntr__id = ${cntr.id})"
        tx2 = "delete from prej_t where cntr__id = ${cntr.id}"
//        println "tx1: $tx1"
//        println "tx2: $tx2"
        cn.execute(tx1.toString())
        cn.execute(tx2.toString())
        println "ha borrado registros temporales, prejActual: ${prejActual.id}"

        tx1 = "insert into prej_t(prej__id, cntr__id, obra__id, prejfcfn, prejfcin, prejnmro, prejtipo, " +
                "prejcrpa, prejcntr, prejcmpl) " +
                "select prej__id, cntr__id, obra__id, prejfcfn, prejfcin, prejnmro, prejtipo, " +
                "prejcrpa, prejcntr, prejcmpl from prej where cntr__id = ${cntr.id}"
        cn.execute(tx1.toString())

        tx1 = "insert into creo_t(creo__id, prej__id, vocr__id, creocntd, creoprct, creoprco) " +
                "select creo__id, prej__id, vocr__id, creocntd, creoprct, creoprco " +
                "from creo where prej__id in (select prej__id from prej where cntr__id = ${cntr.id})"
        cn.execute(tx1.toString())
        println "se ha creado registros temporales en prej_t y creo_t"


        /** borra crej y prej actuales **/
        cn.execute("delete from crej where prej__id in (select prej__id from prej where cntr__id = ${cntr.id})".toString())
        cn.execute("delete from creo where prej__id in (select prej__id from prej where cntr__id = ${cntr.id})".toString())
        cn.execute("delete from prej where cntr__id = ${cntr.id}".toString())
        println "borrado de datos actuales de prej y crej"

        def suspension
        def diasSusp = 0
        def suspensiones

        def fcfn

        def fcin
        if(params.ini) {
            fcin = new Date().parse("dd-MM-yyyy", params.ini)
        }

        if (params.fcfn) {
            fcfn = new Date().parse("dd-MM-yyyy", params.fcfn) - 1
        }
        if (fcfn) {
            suspensiones = Modificaciones.findAllByContratoAndTipoAndFechaFinIsNull(cntr, "S")
        }
//        println "suspensión: ${suspensiones.size()}, inicio en ${suspensiones[0]?.fechaInicio?.format('dd-MMM-yyyy'.toString())}"

        def finSusp = fcfn
        cn.close()

        if (suspensiones.size() > 1) {
            return "Error... existe mas de una suspension en curso"
        } else if (suspensiones.size() == 1) {
            suspension = suspensiones[0]
            diasSusp = finSusp - suspension.fechaInicio + 1
            suspension.dias = diasSusp
            suspension.fechaFin = finSusp
            if (params.observaciones && params.observaciones.trim() != "") {
                suspension.observaciones = params.observaciones + "||" + suspension.observaciones
            }
            if (!suspension.save(flush: true)) {
                println "ERROR EN TEMINAR SUSSPENSION: " + suspension.errors
                render "NO"
            }
/*
            else {
                def prejOk = insertaSuspensionNuevo(cntr, suspension.fechaInicio, suspension.fechaFin, 'S')

                if (prejOk) {
                    println "Fin de suspensión actualizada a: ${suspension.fechaFin}, dias: ${suspension.dias}"
                    params.contrato = cntr.id
                    actualizaPrej()
                    render "OK"
                }
            }
*/
        } else {
            suspension = Modificaciones.findByContratoAndTipoAndFechaInicio(cntr, 'S', fcin)
        }
        def prejOk = insertaSuspensionNuevo(cntr, suspension.fechaInicio, suspension.fechaFin, 'S')

        if (prejOk) {
            println "Fin de suspensión actualizada a: ${suspension.fechaFin}, dias: ${suspension.dias}"
            params.contrato = cntr.id
            actualizaPrej()
            render "OK"
        }


    }

    def insertaSuspensionNuevo(cntr, pfcin, pfcfn, tipo) {
        println "insertaSuspensionNuevo: contrato: $cntr, modificación inicio: $pfcin fin: $pfcfn tipo: $tipo"

        def cn = dbConnectionService.getConnection()
        def obra = cntr.obra
        def sql = ""
        def errores = false

        /* modificación en curso: mdfc */
        def mdfc = Modificaciones.findByContratoAndTipoAndFechaInicio(cntr, tipo, pfcin)

        /* los periodos hasta la susupensión quedan igual */
        sql = "insert into prej(prej__id, cntr__id, obra__id, prejfcfn, prejfcin, prejnmro, prejtipo, " +
                "prejcrpa, prejcntr, prejcmpl) " +
                "select prej__id, cntr__id, obra__id, prejfcfn, prejfcin, prejnmro, prejtipo, " +
                "prejcrpa, prejcntr, prejcmpl from prej_t where cntr__id = ${cntr.id} and " +
                //"prejfcfn <= '${pfcin.format('yyyy-MM-dd')}'"
                "prejfcfn <= '${pfcin.format('yyyy-MM-dd')}' and prejfcin < '${pfcin.format('yyyy-MM-dd')}'"
//        println "--> $sql"
        cn.execute(sql.toString())

        sql = "insert into creo(creo__id, prej__id, vocr__id, creocntd, creoprct, creoprco) " +
                "select creo__id, prej__id, vocr__id, creocntd, creoprct, creoprco " +
                "from creo_t where prej__id in (select prej__id from prej where cntr__id = ${cntr.id})"
//        println "-- creo> $sql"
        cn.execute(sql.toString())

        /* borra los periodos cargados */
        sql = "delete from creo_t where prej__id in (select prej__id from prej where cntr__id = ${cntr.id})"
        cn.execute(sql.toString())

        sql = "delete from prej_t where prej__id in (select prej__id from prej where cntr__id = ${cntr.id})"
        cn.execute(sql.toString())
        println "Insertados los periodos hasta la suspensión: ${mdfc.id}, anteriores a ${pfcin.format('yyyy-MM-dd')}"

        /* se procesa el primer periodo luego de la suspensión */
        sql = "select prej__id, prejfcin, prejfcfn, prejnmro, prejfcfn::date - prejfcin::date dias from prej_t " +
                "where cntr__id = ${cntr.id} and prejtipo in ('P', 'C', 'A') and " +
//                "prejfcin <= '${pfcin.format('yyyy-MM-dd')}' order by prejfcin limit 1"
                "prejfcin < '${pfcin.format('yyyy-MM-dd')}' order by prejfcin limit 1"
        println "1 ---> $sql"

        def fcin = pfcin
        def fcfn = pfcin
        def prdo = 0
        def diasPrdo = 0
        def prej_id = 0
        def fctr = 0
        def fcha = pfcin

        cn.eachRow(sql.toString()) {d ->
            prej_id = d.prej__id
            fcin = d.prejfcin
            fcfn = d.prejfcfn
            prdo = d.prejnmro
            diasPrdo = d.dias +1
        }

        def dias = pfcin - fcin
        def diasrsto = fcfn - pfcin + 1
        def fcfm = preciosService.ultimoDiaDelMes(pfcfn)

        println "procesa primer periodo: ${prej_id} desde ${pfcin} a ${fcfn}, inicia en: $fcin"

        def nuevoId
        /* se proceso solo el periodo hasta antes de la suspensión */
        if((fcfn > pfcin) && (fcin < pfcin)){
            diasrsto = fcfn - pfcin + 1
            fctr = 1 - (diasrsto / diasPrdo)
            fcha = (pfcin - 1).format('yyyy-MM-dd')
            println "diasrsto: $diasrsto, fctr: $fctr"
            sql = "insert into prej(prej__id, cntr__id, obra__id, prejfcfn, prejfcin, prejnmro, prejtipo, " +
                    "prejcrpa, prejcntr, prejcmpl) " +
                    "select prej__id, cntr__id, obra__id, '${fcha}', prejfcin, prejnmro, prejtipo, " +
                    "prejcrpa, prejcntr, prejcmpl from prej_t where prej__id = ${prej_id}"
            println "--1> $sql"
            cn.execute(sql.toString())
            sql = "insert into creo(creo__id, prej__id, vocr__id, creocntd, creoprct, creoprco) " +
                    "select creo__id, prej__id, vocr__id, creocntd*${fctr}, creoprct*${fctr}, creoprco * ${fctr} " +
                    "from creo_t where prej__id = ${prej_id} "
//            println "--2> $sql"
            cn.execute(sql.toString())
            println "se ha modificado el último periodo antes de la susp."

            /* inserta el periodo de suspensión */
            nuevoId = insertaPrejNuevo(obra, prdo, 'S', pfcin, pfcfn, cntr)
            if (nuevoId) {
                if(fcfm > pfcfn) {
                    dias = fcfm - pfcfn
                    fcin = pfcfn + 1

                    if(dias < diasrsto) {
                        diasrsto -=  dias
                        fctr = dias / diasPrdo
//                        println "----crea nuevo periodo hasta ${fcfm} --> diasPrdo $diasPrdo"
                        nuevoId = insertaPrejNuevo(obra, prdo, 'P', fcin, fcfm, cntr)
//                        println "----creado con id: ${nuevoId}"
                        sql = "insert into creo(prej__id, vocr__id, creocntd, creoprct, creoprco) " +
                                "select ${nuevoId}, vocr__id, creocntd*${fctr}, creoprct*${fctr}, creoprco * ${fctr} " +
                                "from creo_t where prej__id = ${prej_id} "
//                        println "inserta nuevo con $sql"
                        cn.execute(sql.toString())
//                        println "---------insertado"
                        /* se debe crear otro periodo con el mismo número y factor */
                        fcin = fcfm + 1
                        fcfn = fcin + diasrsto - 1
                        fctr = diasrsto / diasPrdo
//                        println "----crea periodo adicional hasta ${fcfn}"
                        nuevoId = insertaPrejNuevo(obra, prdo, 'P', fcin, fcfn, cntr)
//                        println "----creado con id: ${nuevoId}"

                        sql = "insert into creo(prej__id, vocr__id, creocntd, creoprct, creoprco) " +
                                "select ${nuevoId}, vocr__id, creocntd*${fctr}, creoprct*${fctr}, creoprco * ${fctr} " +
                                "from creo_t where prej__id = ${prej_id} "
//                        println "-- inserta con: $sql"
                        cn.execute(sql.toString())
//                        println "---------insertadoAdicional $nuevoId"

                    } else {
                        fctr = diasrsto / diasPrdo
                        fcfn = pfcfn + diasrsto
                        nuevoId = insertaPrejNuevo(obra, prdo, 'P', fcin, fcfn, cntr)

                        sql = "insert into creo(prej__id, vocr__id, creocntd, creoprct, creoprco) " +
                                "select ${nuevoId}, vocr__id, creocntd*${fctr}, creoprct*${fctr}, creoprco * ${fctr} " +
                                "from creo_t where prej__id = ${prej_id} "
//                        println "--2r> $sql"
                        cn.execute(sql.toString())
                    }
                } else {
                    println "+++ se supera los días restantes"
                    fcin = fcfm + 1
                    fcfn = fcin + diasrsto
                    fctr = diasrsto / diasPrdo
                    nuevoId = insertaPrejNuevo(obra, prdo, 'P', fcin, fcfn, cntr)

                    sql = "insert into creo(prej__id, vocr__id, creocntd, creoprct, creoprco) " +
                            "select ${nuevoId}, vocr__id, creocntd*${fctr}, creoprct*${fctr}, creoprco * ${fctr} " +
                            "from creo_t where prej__id = ${prej_id} "
                    println "====> $sql"
                    cn.execute(sql.toString())
                }

            }
        } else {
            println "---> obra: $obra, prdo: $prdo, 'S', de: $pfcin, a. $pfcfn, cntr: $cntr"
            nuevoId = insertaPrejNuevo(obra, prdo, 'S', pfcin, pfcfn, cntr)
            fcfn = pfcfn
            prej_id = 0
        }
        /** ELSE: la suspensión coincide con el inicio de un PREJ *
         * hay que procesar sólo los eriodos siguientes           **/

        /* insertar siguientes periodos recalculando las partes */
        fcin = fcfn + 1
//        fcin = fcfn
        println "**** procesa periodos posteriores: $fcin, prej__id: $prej_id"
        fcfm = preciosService.ultimoDiaDelMes(fcin)

        sql = "delete from creo_t where prej__id = ${prej_id}"
        cn.execute(sql.toString())
        sql = "delete from prej_t where prej__id = ${prej_id}"
        cn.execute(sql.toString())
        sql = "select prejnmro, sum((prejfcfn::date - prejfcin::date) + 1) dias, prejtipo from prej_t " +
                "where cntr__id = ${cntr.id} and prejtipo in ('P', 'C', 'A') " +
                "group by prejnmro, prejtipo order by 1"
//        sql = "select prejnmro, sum((prejfcfn::date - prejfcin::date) + 1) dias from prej_t " +
//                "where cntr__id = ${cntr.id} and prejtipo in ('P', 'C', 'A') " +
//                "group by prejnmro order by 1"
        println "2 ---> $sql"

        def periodos = cn.rows(sql.toString())
        def prejtipo = ''
        periodos.each { pr ->
            prdo = pr.prejnmro
            diasPrdo = pr.dias
            prejtipo = pr.prejtipo
            println "....reprogramar periodo $prdo +++ rsto: $diasrsto, diasPrdo: $diasPrdo"
            while(diasPrdo > 0) {
                diasrsto = (fcfm - fcin + 1)
                println "-------diasresto: $diasrsto"
                if(diasrsto < diasPrdo) {
                    fctr = diasrsto / diasPrdo
                    println "+++++ se debe dividir en otro mes ++++++"
//                    sql = "select prej__id from prej_t where cntr__id = ${cntr.id} and prejtipo in ('P', 'C', 'A') and " +
//                            "prejnmro = ${prdo} order by 1;"
                    sql = "select prej__id from prej_t where cntr__id = ${cntr.id} and prejtipo = '${prejtipo}' and " +
                            "prejnmro = ${prdo} order by 1;"
                    def prej = ""
                    cn.eachRow(sql.toString()) {d ->
                        prej += prej? ",${d.prej__id}" : "${d.prej__id}"
                    }
                    //....

                    nuevoId = insertaPrejNuevo(obra, prdo, 'P', fcin, fcfm, cntr)

                    sql = "insert into creo(prej__id, vocr__id, creocntd, creoprct, creoprco) " +
                            "select ${nuevoId}, vocr__id, sum(creocntd)*${fctr}, sum(creoprct)*${fctr}, " +
                            "sum(creoprco) * ${fctr} " +
                            "from creo_t where prej__id in (${prej}) group by vocr__id"
                    println "--2r> $sql"
                    cn.execute(sql.toString())
                    diasPrdo -= diasrsto
                    fcin = fcfm + 1
                    fcfm = preciosService.ultimoDiaDelMes(fcin)

                } else {
                    println "+++++ completa el periodo, diasresto: $diasrsto, fctr: $fctr"
                    fctr = 1 - fctr
                    fcfn = fcin + diasPrdo.toInteger() - 1
                    println "NuevoPrej: ${obra.id}, $prdo, $prejtipo, $fcin, $fcfn, ${cntr.id}"
                    nuevoId = insertaPrejNuevo(obra, prdo, prejtipo, fcin, fcfn, cntr)

//                    sql = "select prej__id from prej_t where cntr__id = ${cntr.id} and prejtipo in ('P', 'C', 'A') and " +
//                            "prejnmro = ${prdo} order by 1;"
                    sql = "select prej__id from prej_t where cntr__id = ${cntr.id} and prejtipo = '${prejtipo}' and " +
                            "prejnmro = ${prdo} order by 1;"
                    def prej = ""
                    cn.eachRow(sql.toString()) {d ->
                        prej += prej? ",${d.prej__id}" : "${d.prej__id}"
                    }

                    sql = "insert into creo(prej__id, vocr__id, creocntd, creoprct, creoprco) " +
                            "select ${nuevoId}, vocr__id, sum(creocntd)*${fctr}, sum(creoprct)*${fctr}, " +
                            "sum(creoprco) * ${fctr} " +
                            "from creo_t where prej__id in (${prej}) group by vocr__id"
                    println "--suma> $sql"
                    cn.execute(sql.toString())

                    diasPrdo = 0
                    fcin = fcfn + 1
                    fcfm = preciosService.ultimoDiaDelMes(fcin)
                    fctr = 0
                }
            }
        }


        println "fin de periodos posteriores a la suspensión"
        return true

    }

    /** Se debe incluir en el cronograma de jecución los periodos adicionales por el complementario
     * 1. Crear nuevos PREJ
     * 2. Ingresar valores prorrateados en PREJ nuevos
     * **/
    def armaCrcrComp = {
        println "params armaCrcrComp: $params"
        def cn = dbConnectionService.getConnection()
        def cntr = Contrato.get(params.contrato)
        def comp = Contrato.findByPadreAndTipoContrato(cntr, TipoContrato.findByCodigo('C'))
        def diasPlzo = comp.plazo

        def prejOk = insertaSuspension(cntr, diasPlzo, 'C')

        if (prejOk) {
//        if (true) {
            def sql = "select max(prejnmro) nmro from prej where cntr__id = ${cntr.id} and prejfcin <= " +
                    "(select min(prejfcin) from prej where cntr__id = ${cntr.id} and prejtipo = 'C') and prejtipo <> 'C'"
            def prdo = cn.rows(sql.toString())[0].nmro

            def fraccion = 0.0
            def dias = 0
            def mess = 30
            def cont = 1
            def creoNuevo

            while (diasPlzo > 0) {
                def crcr = CronogramaContratado.findAllByContratoAndPeriodo(cntr, cont + prdo)
//                println "procesa periodo: $cont"
                mess = diasPlzo >= 30 ? 30 : diasPlzo % 30
                crcr.each { cr ->
//                    println "dias: $diasPlzo"
                    def prej = PeriodoEjecucion.findAllByContratoAndTipoAndNumero(cntr, 'C', prdo + cont, [sort: 'fechaInicio'])
                    prej.each { pe ->
                        dias = pe.fechaFin - pe.fechaInicio + 1
                        fraccion = dias / mess
//                        println "dias: $dias, diasPlzo: $mess, fracción: $fraccion, diasPlzo: $diasPlzo"
                        creoNuevo = new CrngEjecucionObra()
                        creoNuevo.periodo = pe
                        creoNuevo.volumenObra = cr.volumenContrato
                        creoNuevo.cantidad = Math.round(cr.cantidad * fraccion * 1000) / 1000
                        creoNuevo.porcentaje = Math.round(cr.porcentaje * fraccion * 100) / 100
                        creoNuevo.precio = Math.round(cr.precio * fraccion * 100) / 100
                        creoNuevo.save(flush: true)
                    }
                }
                diasPlzo -= 30
                cont++
            }
            cn.close()
        }

        if (prejOk) {
//        if (true) {
            render "OK"
        } else {
            render "NO"
        }


    }

    def insertaSuspension(cntr, dias, tipo) {
        println "params armaCrcrComp: $params"
        def cn = dbConnectionService.getConnection()
//        def cntr = Contrato.get(params.contrato)
        def comp = Contrato.findByPadreAndTipoContrato(cntr, TipoContrato.findByCodigo(tipo))
//        def dias = comp.plazo
        def obra = cntr.obra
        def sql = ""
        def errores = false

//        def mdfc = Modificaciones.findByContratoAndTipo(cntr, 'C')
        def mdfc = Modificaciones.findByContratoAndTipo(cntr, tipo)
        /* si no existe la modificación por complementario se la crea y registra CRCR */
        if (!mdfc) {
            def modificacion = new Modificaciones([
                    obra    : obra,
                    tipo    : tipo,
                    dias    : dias,
                    fecha   : new Date(),
                    motivo  : "contrato complementario ${comp.codigo}",
                    contrato: cntr
            ])
            if (!modificacion.save(flush: true)) {
//            if (false) {
                println "error modificacion: " + modificacion.errors
            } else {
                /* crea los periodos de ejecución */
                def ultimoPeriodo = PeriodoEjecucion.findAllByObra(obra, [sort: "fechaInicio"]).last()
                def fcin = ultimoPeriodo.fechaInicio
                def fcfn = ultimoPeriodo.fechaFin
                def prdo = ultimoPeriodo.numero
                sql = "select sum(prejfcfn::date - prejfcin::date + 1) dias from prej " +
                        "where cntr__id = ${cntr.id} and prejtipo in ('P', 'A', 'C') and prejnmro = ${prdo}"
                def diasPrdo = cn.rows(sql.toString())[0].dias
                def fcfm

                println "ultimoperiodo: $prdo, dias: ${diasPrdo}"
                fcfm = preciosService.ultimoDiaDelMes(fcin)
                def diasMes
                def diasNuevo = dias >= 30 ? 30 : dias

//                println "dias: $dias, diasPrdo: $diasPrdo, fcfm: $fcfm, final: ${fcfn}"
                if ((dias + diasPrdo) <= 30 && (fcfm >= (fcfn + dias))) {
                    ultimoPeriodo.fechaFin = fcfn + dias
                    println "modifica ultimo periodo"
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

                prdo++
                println "inicio: $fcin, dias: $dias, diasPeriodo: $diasNuevo"
                while (dias > 0) {
                    println "----dias: $dias--- fcfn: $fcfn"
                    diasMes = fcfm - fcin + 1
                    if (diasMes > diasNuevo) {
                        if (insertaPrejNuevo(obra, prdo, tipo, fcin, fcin + diasNuevo.toInteger(), cntr)) {
                            println "++++crea nuevo periodo hasta ${fcfm}"
                            dias -= diasNuevo
                            if (dias) {
                                prdo++
                            }
                            println "......10 diasNuevo: $diasNuevo"
                            diasNuevo = dias > 30 ? 30 : dias        //aqui...
                            println "......11: dd: $diasNuevo"
                            fcin = fcin + diasNuevo.toInteger() + 1
                            println "......12"
                            fcfm = preciosService.ultimoDiaDelMes(fcin)
                        }
//                        diasNuevo = 30
                        println "resto del mes: ${diasMes - diasNuevo}"
                    } else {
                        /* inserta periodo parcial por diasMes */
                        if (insertaPrejNuevo(obra, prdo, tipo, fcin, fcfm, cntr)) {
                            println "----crea nuevo periodo hasta ${fcfm}"
                            dias -= diasMes + 1
                            diasNuevo -= diasMes + 1
                            fcin = fcfm + 1
                            println "....2"
                            fcfm = preciosService.ultimoDiaDelMes(fcin)
                        }
                    }
                }
            }

            if (!errores) {
                return true
            } else {
                return false
            }
        }
    }

    def insertaPrejNuevo(obra, prdo, tipo, fcin, fcfn, cntr) {
        println "insertaPrejNuevo: obra: ${obra.id}, prdo: $prdo, tipo: $tipo, de ${fcin.format('yyyy-MM-dd')} a " +
                "${fcfn.format('yyyy-MM-dd')}, cntr: ${cntr.id}"
        def periodo = new PeriodoEjecucion([
                obra       : obra,
                numero     : prdo,
                tipo       : tipo,
                fechaInicio: fcin,
                fechaFin   : fcfn,
                contrato   : cntr,
                parcialCronograma: 0,
                parcialContrato: 0,
                parcialCmpl: 0
        ])
        if (!periodo.save(flush: true)) {
            println "ERROR!!!!: " + periodo.errors
            return false
        } else {
//            println "+++ insertado periodo"
            return periodo.refresh().id
        }
//        return true
    }


    def modificacionNuevo() {

        println("params grabar mod " + params)

        def obra = Obra.get(params.obra.toLong())
        def modificaciones = [:]

        params.fis.each { p ->
            def parts = p.split("_")
            if (parts.size() == 4) {
                def val = parts[0]
                def periodo = PeriodoEjecucion.get(parts[1].toLong())
                def vol = VolumenContrato.get(parts[2].toLong())
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
                def vol = VolumenContrato.get(parts[2].toLong())
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
//
        params.dol.each { p ->
            def parts = p.split("_")
            if (parts.size() == 4) {
                def val = parts[0]
                def periodo = PeriodoEjecucion.get(parts[1].toLong())
                def vol = VolumenContrato.get(parts[2].toLong())
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
//
        def ok = "OK"
//
        modificaciones.each { periodo, mod ->
            def fis = mod.fis
            def prc = mod.prct
            def dol = mod.dol
            CrngEjecucionObra crono
            if (fis.crono != "null") {
                crono = CrngEjecucionObra.get(fis.crono.toLong())
            } else {
                crono = new CrngEjecucionObra()
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


    def modificacionNuevo_ajax() {
//        println("params modajax " + params)
        def contrato = Contrato.get(params.contrato.toLong())
        def obra = contrato.obra
        def vol = VolumenContrato.get(params.vol.toLong())
        def totlDol = 0, totlCan = 0

        def plAvance = Planilla.findAllByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo("P"))
        def plLiquidacion = Planilla.findAllByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo("Q"))

        println("pl 1 " + plAvance.id)
        println("pl 2 " + plLiquidacion.id)

        def sql1
        def resP = []
        def cn = dbConnectionService.getConnection()

        plAvance.each {
            sql1 = "select max(plnlfcfn) from plnl where plnl__id = ${it.id}"
            resP += (cn.rows(sql1.toString()))
        }

        plLiquidacion.each {
            sql1 = "select max(plnlfcfn) from plnl where plnl__id = ${it.id}"
            resP += (cn.rows(sql1.toString()))
        }

        def fechasFinPlanilla = []

        resP.each{
            fechasFinPlanilla += formatDate(date: it.max, format: "yyyy-MM-dd")
        }

        fechasFinPlanilla.sort(true)

        println("fec " + fechasFinPlanilla)

        def html = "", row2 = ""

        def liquidacionReajuste = Planilla.findAllByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo("L"))
        if (liquidacionReajuste.size() > 0) {
            return [msg: "Ya se ha realizado la liquidación del reajuste, ya no puede realizar modificaciones"]
        }

        def periodos = PeriodoEjecucion.findAllByContratoAndObra(contrato, obra, [sort: 'fechaInicio'])
        def indirecto = obra.totales / 100
        def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, vol.item.id)
        def precio = ((res["precio"][0] ?: 0) + (res["precio"][0] ?: 0) * (indirecto ?: 0)).toDouble().round(2)
        def cronos = [
                codigo  : vol.item.codigo,
                nombre  : vol.item.nombre,
                unidad  : vol.item.unidad.codigo,
                cantidad: vol.volumenCantidad,
                precioU : vol.volumenPrecio,
                parcial : vol.volumenSubtotal,
                volumen : vol
        ]

        totlCan = vol.volumenCantidad
        totlDol = vol.volumenSubtotal


        html += "<table class='table table-condensed'>"
        html += "<tr>"
        html += "<th>Rubro</th>"
        html += "<td>" + cronos.codigo + " " + cronos.nombre + "</td>"
        html += "</tr>"
        html += "<tr>"
        html += "<th>Unidad</th>"
        html += "<td>" + cronos.unidad + "</td>"
        html += "<th>Cantidad</th>"
        html += "<td>" + numero(cronos.cantidad) + "</td>"
        html += "<th>Unitario</th>"
        html += "<td>" + numero(cronos.precioU) + "</td>"
        html += "<th>C. total</th>"
        html += "<td>" + numero(cronos.parcial) + "</td>"
        html += "</tr>"
//        html += "<tr>"
//
//        html += "</tr>"
        html += "</table>"

        html += '<table class="table table-bordered table-condensed table-hover">'
        html += '<thead>'
        html += '<tr>'
        html += '<th rowspan="2" style="width:50px;"></th>'
        html += '<th rowspan="2" style="width:12px;">'
        html += 'T.'
        html += '</th>'


        periodos.sort{it.fechaFin}

        periodos.eachWithIndex { per, i ->

//            println("fecha per " + per.fechaFin)
//            println("fecha sin planilla " + fechasFinPlanilla)

//            if(formatDate(date: per.fechaFin, format: "yyyy-MM-dd") in fechasFinPlanilla){

            if(fechasFinPlanilla){
                if(formatDate(date: per.fechaFin, format: "yyyy-MM-dd") <= fechasFinPlanilla.last()){
                    html += "<th class='${per.tipo}' style='color: #cf0e21'>"
                }else{
                    html += "<th class='${per.tipo}'>"
                }
            }else{
                html += "<th class='${per.tipo}'>"
            }

//            html += "<th class='${per.tipo}'>"
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

        def filaDol = "", filaPor = "", filaCan = ""
        def totDol = 0, totPor = 0, totCan = 0
        periodos.eachWithIndex { periodo, i ->
            def cronosPer = CrngEjecucionObra.findAllByVolumenObraAndPeriodo(cronos.volumen, periodo)
            filaDol += "<td class='dol num ${periodo.tipo}'>"
            filaPor += "<td class='prct num ${periodo.tipo}'>"
            filaCan += "<td class='fis num ${periodo.tipo}'>"

            cantModificable[i] = [
                    dol  : 0,
                    por  : 0,
                    can  : 0,
                    crono: null
            ]
            if (cronosPer.size() == 1) {
                cronosPer = cronosPer[0]
                filaDol += numero(cronosPer.precio)
                filaPor += numero(cronosPer.porcentaje)
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
            }
            filaDol += "</td>"
            filaPor += "</td>"
            filaCan += "</td>"
        }

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
            def porPla = totDol == 0 ? 0 : (totalPlanilla * 100) / totDol
            def canPla = (totCan * (porPla / 100))

            filaDolPla += numero(totalPlanilla, 2, "hide")
            filaPorPla += numero(porPla, 2, "hide")
            filaCanPla += numero(canPla, 2, "hide")

            totDolPla += totalPlanilla
            totPorPla += porPla
            totCanPla += canPla

            def cronosPer = CrngEjecucionObra.findAllByVolumenObraAndPeriodo(cronos.volumen, periodo)

            if (modificable) {
                def dol = cantModificable[i].dol - totalPlanilla
                def por = cantModificable[i].por - porPla
                def can = cantModificable[i].can - canPla
                def maxDol = numero(dol.toDouble().round(2))
                def maxPor = numero(por.toDouble().round(2))
                def maxCan = numero(can.toDouble().round(2))

                maxDolAcu += dol.toDouble()
                maxPrctAcu += por.toDouble()
                maxCanAcu += can.toDouble()

                if(fechasFinPlanilla){
                    if(formatDate(date: periodo.fechaFin, format: "yyyy-MM-dd") <= fechasFinPlanilla.last()){

                        filaDolMod += "<input type='text' readonly='' class='input-mini tiny dol p${i}' value='${cronosPer[0]?.precio ?: 0.00}' data-tipo='dol' data-total='${totlDol}' data-periodo='${i}' data-max='" + maxDolAcu + "' " +
                                " data-id='${cantModificable[i].crono}' data-id2='${periodo.id}' data-id3='${cronos.volumen.id}' " +
                                " data-val1='${totalPlanilla.toDouble().round(2)}' /> "
                        filaPorMod += "<input type='text' readonly='' class='input-mini tiny prct p${i}' value='${cronosPer[0]?.porcentaje ?: 0.00}' data-tipo='prct' data-total='${totPor}' data-periodo='${i}' data-max='" + maxPrctAcu + "' " +
                                " data-id='${cantModificable[i].crono}' data-id2='${periodo.id}' data-id3='${cronos.volumen.id}' " +
                                " data-val1='${porPla.toDouble().round(2)}'  /> "
                        filaCanMod += "<input type='text' readonly='' class='input-mini tiny fis p${i}' value='${cronosPer[0]?.cantidad ?: 0.00}' data-tipo='fis' data-total='${totlCan}' data-periodo='${i}' data-max='" + maxCanAcu + "' " +
                                " data-id='${cantModificable[i].crono}' data-id2='${periodo.id}' data-id3='${cronos.volumen.id}' " +
                                " data-val1='${canPla.toDouble().round(2)}' /> "
                    }else{

                        filaDolMod += "<input type='text' class='input-mini tiny dol p${i}' value='" + "0.00" +
                                "' data-tipo='dol' data-total='${totlDol}' data-periodo='${i}' data-max='" + maxDolAcu + "' " +
                                " data-id='${cantModificable[i].crono}' data-id2='${periodo.id}' data-id3='${cronos.volumen.id}' " +
                                " data-val1='${totalPlanilla.toDouble().round(2)}' /> "
                        filaPorMod += "<input type='text' class='input-mini tiny prct p${i}' value='" + "0.00" +
                                "' data-tipo='prct' data-total='${totPor}' data-periodo='${i}' data-max='" + maxPrctAcu + "' " +
                                " data-id='${cantModificable[i].crono}' data-id2='${periodo.id}' data-id3='${cronos.volumen.id}' " +
                                " data-val1='${porPla.toDouble().round(2)}'  /> "
                        filaCanMod += "<input type='text' class='input-mini tiny fis p${i}' value='" + "0.00" +
                                "' data-tipo='fis' data-total='${totlCan}' data-periodo='${i}' data-max='" + maxCanAcu + "' " +
                                " data-id='${cantModificable[i].crono}' data-id2='${periodo.id}' data-id3='${cronos.volumen.id}' " +
                                " data-val1='${canPla.toDouble().round(2)}' /> "
                    }
                }else{
                    filaDolMod += "<input type='text' class='input-mini tiny dol p${i}' value='" + "0.00" +
                            "' data-tipo='dol' data-total='${totlDol}' data-periodo='${i}' data-max='" + maxDolAcu + "' " +
                            " data-id='${cantModificable[i].crono}' data-id2='${periodo.id}' data-id3='${cronos.volumen.id}' " +
                            " data-val1='${totalPlanilla.toDouble().round(2)}' /> "
                    filaPorMod += "<input type='text' class='input-mini tiny prct p${i}' value='" + "0.00" +
                            "' data-tipo='prct' data-total='${totPor}' data-periodo='${i}' data-max='" + maxPrctAcu + "' " +
                            " data-id='${cantModificable[i].crono}' data-id2='${periodo.id}' data-id3='${cronos.volumen.id}' " +
                            " data-val1='${porPla.toDouble().round(2)}'  /> "
                    filaCanMod += "<input type='text' class='input-mini tiny fis p${i}' value='" + "0.00" +
                            "' data-tipo='fis' data-total='${totlCan}' data-periodo='${i}' data-max='" + maxCanAcu + "' " +
                            " data-id='${cantModificable[i].crono}' data-id2='${periodo.id}' data-id3='${cronos.volumen.id}' " +
                            " data-val1='${canPla.toDouble().round(2)}' /> "
                }




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
        html += numero(totDol)
        html += "</td>"
        html += "</tr>"

        html += "<tr class='click item_prc'>"
        html += '<td>%</td>'
        html += filaPor
        html += "<td class='num prct total totalRubro'>"
        html += numero(totPor)
        html += "</td>"
        html += "</tr>"

        html += "<tr class='click item_f '>"
        html += '<td>F</td>'
        html += filaCan
        html += "<td class='num fis total totalRubro'>"
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


} //fin controller
