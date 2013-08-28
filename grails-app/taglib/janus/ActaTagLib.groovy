package janus

import janus.actas.Acta
import janus.ejecucion.DetallePlanilla
import janus.ejecucion.DetallePlanillaCosto
import janus.ejecucion.PeriodoPlanilla
import janus.ejecucion.Planilla
import janus.ejecucion.TipoPlanilla

class ActaTagLib {

    static namespace = "acta"

    def preciosService

    Closure clean = { attrs ->
        def replace = [
                "&aacute;": "á",
                "&eacute;": "é",
                "&iacute;": "í",
                "&oacute;": "ó",
                "&uacute;": "ú",
                "&ntilde;": "ñ",
                "&Aacute;": "Á",
                "&Eacute;": "É",
                "&Iacute;": "Í",
                "&Oacute;": "Ó",
                "&Uacute;": "Ú",
                "&Ntilde;": "Ñ",
        ]
        def str = attrs.str

        replace.each { busca, nuevo ->
            str = str.replaceAll(busca, nuevo)
        }
        out << str
    }

    Closure numero = { attrs ->
        def decimales = attrs.decimales ?: 2
        def cero = attrs.cero ?: "hide"
        def num = attrs.numero

        if (num == 0 && cero.toString().toLowerCase() == "hide") {
            out << " ";
        }
        if (decimales == 0) {
            out << formatNumber(number: num, minFractionDigits: decimales, maxFractionDigits: decimales, locale: "ec")
        } else {
            def format = "##,##0"
            out << formatNumber(number: num, minFractionDigits: decimales, maxFractionDigits: decimales, locale: "ec", format: format)
        }
    }


    Closure tabla = { attrs ->
        def str = ""
        def acta = attrs.acta
        if (attrs.tipo && acta) {
            def tipo = attrs.tipo.toLowerCase()
            str += "$tipo"(acta)
        }
        out << str
    }

    def rbr(Acta acta) {
        def contrato = acta.contrato
        def obra = contrato.oferta.concurso.obra

        def planillasAvance = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
            order("fechaInicio", "asc")
        }

        def indirecto = obra.totales / 100
        preciosService.ac_rbroObra(obra.id)
        def detalles = [:]
        def volumenes = VolumenesObra.findAllByObra(obra, [sort: "subPresupuesto"])

        volumenes.each { vol ->
            vol.refresh()
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, vol.item.id)
            def precio = (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2)

            if (!detalles[vol.subPresupuesto]) {
                detalles[vol.subPresupuesto] = [:]
            }
            if (!detalles[vol.subPresupuesto][vol.item]) {
                detalles[vol.subPresupuesto][vol.item] = [
                        codigo: vol.item.codigo,
                        nombre: vol.item.nombre,
                        unidad: vol.item.unidad.codigo,
                        precio: precio,
                        cantidad: [
                                contratado: 0,
                                ejecutado: 0
                        ],
                        valor: [
                                contratado: 0,
                                ejecutado: 0
                        ]
                ]
            }
            detalles[vol.subPresupuesto][vol.item].cantidad.contratado += vol.cantidad
            detalles[vol.subPresupuesto][vol.item].valor.contratado += ((vol.cantidad * precio).toDouble().round(2))
        }

        planillasAvance.each { pla ->
            def det = DetallePlanilla.findAllByPlanilla(pla)
            det.each { dt ->
                if (detalles[dt.volumenObra.subPresupuesto][dt.volumenObra.item]) {
                    detalles[dt.volumenObra.subPresupuesto][dt.volumenObra.item].cantidad.ejecutado += dt.cantidad
                    detalles[dt.volumenObra.subPresupuesto][dt.volumenObra.item].valor.ejecutado += dt.monto
                } else {
                    println "no existe detalle para " + dt.volumenObra.item + "???"
                }
            }
        }

        def maxLength = 60

        def tabla = "<table class='table table-bordered2 table-condensed'>"
        tabla += "<thead>"
        tabla += "<tr>"
        tabla += "<th style='width:50px;'>N.</th>"
        tabla += "<th style='width:300px;'>Descripción del rubro</th>"
        tabla += "<th>U.</th>"
        tabla += "<th>Precio unitario</th>"
        tabla += "<th>Volumen contratado</th>"
        tabla += "<th>Cantidad total ejecutada</th>"
        tabla += "<th>Valor total ejecutado</th>"
        tabla += "</tr>"
        tabla += "</thead>"

        tabla += "<tbody>"

        def total = 0
        detalles.each { subpres, det ->
            tabla += "<tr>"
            tabla += "<th colspan='4'>${subpres.descripcion}</th>"
            tabla += "<th colspan='3'></th>"
            tabla += "</tr>"
            det.each { k, elem ->
                def nombre = elem.nombre.size() > maxLength ? elem.nombre[0..maxLength] : elem.nombre
                total += elem.valor.ejecutado
                tabla += "<tr>"
                tabla += "<td>${elem.codigo}</td>"
                tabla += "<td>${nombre}</td>"
                tabla += "<td>${elem.unidad}</td>"
                tabla += "<td class='tar'>${numero(numero: elem.precio)}</td>"
                tabla += "<td class='tar'>${numero(numero: elem.cantidad.contratado)}</td>"
                tabla += "<td class='tar'>${numero(numero: elem.cantidad.ejecutado)}</td>"
                tabla += "<td class='tar'>${numero(numero: elem.valor.ejecutado)}</td>"
                tabla += "</tr>"
            }
        }
        tabla += "</tbody>"

        tabla += "<tfoot>"
        tabla += "<tr>"
        tabla += "<th colspan='6' class='tal'>TOTAL OBRA EJECUTADA</th>"
        tabla += "<th class='tar'>${numero(numero: total)}</th>"
        tabla += "</tr>"
        tabla += "</tfoot>"

        tabla += "</table>"
        return tabla
    }

    def dtp(Acta acta) {
        def contrato = acta.contrato

        def planillas = Planilla.withCriteria {
            eq("contrato", contrato)
            or {
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
            }
            order("fechaIngreso", "asc")
        }

        def tabla = "<table class='table table-bordered table-condensed'>"
        tabla += "<thead>"
        tabla += "<tr>"
        tabla += "<th rowspan='2'>Fecha</th>"
        tabla += "<th rowspan='2'>N. planilla</th>"
        tabla += "<th rowspan='2'>Periodo</th>"
        tabla += "<th rowspan='2'>Valor</th>"
        tabla += "<th colspan='2'>Descuentos</th>"
        tabla += "</tr>"

        tabla += "<tr>"
        tabla += "<th>Anticipo</th>"
        tabla += "<th>Multas</th>"
        tabla += "</tr>"
        tabla += "</thead>"

        tabla += "<tbody>"

        def totalValor = 0, totalAnticipo = 0, totalMultas = 0
        planillas.each { planilla ->
            def periodo, valor, anticipo, multas
            if (planilla.tipoPlanilla.codigo == "A") {
                periodo = "ANTICIPO"
                valor = planilla.reajuste
            } else {
                periodo = "DEL " + fechaConFormato(fecha: planilla.fechaInicio, formato: "dd-MM-yyyy") +
                        " AL " + fechaConFormato(fecha: planilla.fechaFin, formato: "dd-MM-yyyy")
                valor = planilla.valor + planilla.reajuste
            }
            anticipo = planilla.descuentos
            multas = planilla.multaPlanilla + planilla.multaRetraso

            totalValor += valor
            totalAnticipo += anticipo
            totalMultas += multas

            tabla += "<tr>"
            tabla += "<td>${fechaConFormato(fecha: planilla.fechaIngreso)}</td>"
            tabla += "<td>${planilla.numero}</td>"
            tabla += "<td>${periodo}</td>"
            tabla += "<td class='tar'>${numero(numero: valor)}</td>"
            tabla += "<td class='tar'>${numero(numero: anticipo)}</td>"
            tabla += "<td class='tar'>${numero(numero: multas)}</td>"
            tabla += "</tr>"
        }
        tabla += "</tbody>"

        tabla += "<tfoot>"
        tabla += "<tr>"
        tabla += "<th colspan='3' class='tal'>TOTALES</th>"
        tabla += "<th class='tar'>${numero(numero: totalValor)}</th>"
        tabla += "<th class='tar'>${numero(numero: totalAnticipo)}</th>"
        tabla += "<th class='tar'>${numero(numero: totalMultas)}</th>"
        tabla += "</tr>"
        tabla += "</tfoot>"

        tabla += "</table>"
        return tabla
    }

    def oad(Acta acta) {
        def contrato = acta.contrato

        def planillaAnticipo = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
            order("fechaIngreso", "asc")
        }

        def planillasAvance = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
            order("fechaIngreso", "asc")
        }

        def planillasCosto = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("C"))
            order("fechaIngreso", "asc")
        }

        def tabla = "<table class='table table-bordered table-condensed'>"

        tabla += "<thead>"
        tabla += "<tr>"
        tabla += "<th>Valor de obras adicionales contractuales</th>"
        tabla += "<th>%</th>"
        tabla += "<th>Valor de obras adicionales costo + porcentaje</th>"
        tabla += "<th>%</th>"
        tabla += "<th>Memorando de autorización</th>"
        tabla += "<th>Memorando a Dir. Financiera</th>"
        tabla += "<th>Memorando a partida presupuestaria</th>"
        tabla += "<th>% total</th>"
        tabla += "</tr>"
        tabla += "</thead>"

//        def a = planillaAnticipo.sum { it.reajuste } ?: 0
//        a += planillasAvance.sum { it.valor + it.reajuste } ?: 0
        def a = planillasAvance.sum { it.valor } ?: 0
        def valor = (contrato.monto - a > 0) ? (contrato.monto - a) : 0
        def prct = (contrato.monto - a > 0) ? (a / contrato.monto) * 100 : 0
        def costo = planillasCosto.sum { it.valor } ?: 0
        def prctCosto = (costo / contrato.monto) * 100

        def prctTotal = ((valor + costo) / contrato.monto) * 100

        tabla += "<tbody>"
        tabla += "<tr>"
        tabla += "<td class='tar'>${numero(numero: valor)}</td>"
        tabla += "<td class='tar'>${numero(numero: prct)}</td>"
        tabla += "<td class='tar'>${numero(numero: costo)}</td>"
        tabla += "<td class='tar'>${numero(numero: prctCosto)}</td>"
        tabla += "<td class='tar'></td>"
        tabla += "<td class='tar'></td>"
        tabla += "<td class='tar'></td>"
        tabla += "<td class='tar'>${numero(numero: prctTotal)}</td>"
        tabla += "</tr>"
        tabla += "</tbody>"

//        tabla += "<tfoot>"
//        tabla += "</toot>"

        tabla += "</table>"
        return tabla
    }

    def ocp(Acta acta) {
        def contrato = acta.contrato

        def planillasCosto = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("C"))
            order("fechaIngreso", "asc")
        }

        def p1 = planillasCosto.size() > 0 ? planillasCosto.first() : null
        def prct = DetallePlanillaCosto.findByPlanilla(p1)?.indirectos

        def tabla = "<table class='table table-bordered table-condensed'>"

        tabla += "<thead>"
        tabla += "<tr>"
        tabla += "<th>Fecha</th>"
        tabla += "<th>N. planilla</th>"
        tabla += "<th>Periodo</th>"
        tabla += "<th>Valor neto</th>"
        tabla += "<th>% (${numero(numero: prct)})</th>"
        tabla += "<th>Valor total</th>"
        tabla += "</tr>"
        tabla += "</thead>"

        tabla += "<tbody>"

        def totalValor = 0, totalIndi = 0, total = 0
        planillasCosto.each { planilla ->
            def plaAv = planilla.padreCosto
            def periodos = PeriodoPlanilla.findAllByPlanilla(plaAv)
            def detalles = DetallePlanillaCosto.findAllByPlanilla(planilla)
            def valor = detalles.sum { it.montoIva } ?: 0
            def indi = detalles.sum { it.montoIndirectos } ?: 0

            totalValor += valor
            totalIndi += indi
            total += valor
            total += indi

            tabla += "<tr>"
            tabla += "<td>${fechaConFormato(fecha: planilla.fechaIngreso)}</td>"
            tabla += "<td>${planilla.numero}</td>"
            tabla += "<td>${periodos.last().periodo.descripcion}</td>"
            tabla += "<td class='tar'>${numero(numero: valor)}</td>"
            tabla += "<td class='tar'>${numero(numero: indi)}</td>"
            tabla += "<td class='tar'>${numero(numero: valor + indi)}</td>"
            tabla += "</tr>"
        }
        tabla += "</tbody>"

        tabla += "<tfoot>"
        tabla += "<tr>"
        tabla += "<th colspan='3' class='tal'>TOTAL</th>"
        tabla += "<th class='tar'>${numero(numero: totalValor)}</th>"
        tabla += "<th class='tar'>${numero(numero: totalIndi)}</th>"
        tabla += "<th class='tar'>${numero(numero: total)}</th>"
        tabla += "</tr>"
        tabla += "</tfoot>"

        tabla += "</table>"
        return tabla
    }

    def rrp(Acta acta) {
        def contrato = acta.contrato

        def planillas = Planilla.withCriteria {
            eq("contrato", contrato)
            or {
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
            }
            order("fechaIngreso", "asc")
        }

        def tabla = "<table class='table table-bordered table-condensed'>"

        tabla += "<thead>"
        tabla += "<tr>"
        tabla += "<th>Fecha</th>"
        tabla += "<th>N. planilla</th>"
        tabla += "<th>Periodo</th>"
        tabla += "<th>Valor</th>"
        tabla += "</tr>"
        tabla += "</thead>"

        tabla += "<tbody>"
        def total = 0
        planillas.each { planilla ->
            def periodo
            if (planilla.tipoPlanilla.codigo == "A") {
                periodo = "ANTICIPO"
            } else {
                periodo = "DEL " + fechaConFormato(fecha: planilla.fechaInicio, formato: "dd-MM-yyyy") +
                        " AL " + fechaConFormato(fecha: planilla.fechaFin, formato: "dd-MM-yyyy")
            }
            total += planilla.reajuste
            tabla += "<tr>"
            tabla += "<td>${fechaConFormato(fecha: planilla.fechaIngreso)}</td>"
            tabla += "<td>${planilla.numero}</td>"
            tabla += "<td>${periodo}</td>"
            tabla += "<td class='tar'>${numero(numero: planilla.reajuste)}</td>"
            tabla += "</tr>"
        }
        tabla += "</tbody>"

        tabla += "<tfoot>"
        tabla += "<tr>"
        tabla += "<th colspan='3' class='tal'>TOTALES</th>"
        tabla += "<th class='tar'>${numero(numero: total)}</th>"
        tabla += "</tr>"
        tabla += "</tfoot>"

        tabla += "</table>"
        return tabla
    }

    def rgv(Acta acta) {
        def contrato = acta.contrato

        def planillaAnticipo = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
            order("fechaIngreso", "asc")
        }
        def planillasAvance = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
            order("fechaIngreso", "asc")
        }
        def planillasCosto = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("C"))
            order("fechaIngreso", "asc")
        }

        def av = planillasAvance.sum { it.valor } ?: 0
        def cp = planillasCosto.sum { it.valor } ?: 0
        def total1 = av + cp

        def ran = planillaAnticipo.sum { it.reajuste } ?: 0
        def rav = planillasAvance.sum { it.reajuste } ?: 0
        def total2 = ran + rav

        def tabla = "<table class='table table-bordered table-condensed'>"
        tabla += "<thead>"

        tabla += "<tr>"
        tabla += "<th class='tal'>Total valor de liquidación de obra</th>"
        tabla += "<td class='tar'>${numero(numero: total1)}</td>"
        tabla += "</tr>"

        tabla += "<tr>"
        tabla += "<th class='tal'>Total valor de reajuste de precios</th>"
        tabla += "<td class='tar'>${numero(numero: total2)}</td>"
        tabla += "</tr>"

        tabla += "<tr>"
        tabla += "<th class='tal'>Total valor de la inversión</th>"
        tabla += "<td class='tar'>${numero(numero: total1 + total2)}</td>"
        tabla += "</tr>"

        tabla += "</thead>"

        tabla += "</table>"
        return tabla
    }

    def dta(Acta acta) {
        def contrato = acta.contrato
        def obra = contrato.oferta.concurso.obra
        def ampliaciones = Modificaciones.findAllByObraAndTipo(obra, 'A')

        def tabla = "<table class='table table-bordered table-condensed'>"

        tabla += "<thead>"
        tabla += "<tr>"
        tabla += "<th>N.</th>"
        tabla += "<th>N. de días</th>"
        tabla += "<th>Trámite</th>"
        tabla += "<th>Fecha</th>"
        tabla += "<th>Motivo</th>"
        tabla += "<th>Observaciones</th>"
        tabla += "</tr>"
        tabla += "</thead>"

        tabla += "<tbody>"
        ampliaciones.eachWithIndex { amp, i ->
            tabla += "<tr>"
            tabla += "<td>${i + 1}</td>"
            tabla += "<td class='tar'>${amp.dias}</td>"
            tabla += "<td>${amp.memo}</td>"
            tabla += "<td>${fechaConFormato(fecha: amp.fecha, formato: 'dd-MM-yyyy')}</td>"
            tabla += "<td>${amp.motivo}</td>"
            tabla += "<td>${amp.observaciones}</td>"
            tabla += "</tr>"
        }
        tabla += "</tbody>"

        tabla += "</table>"
        return tabla
    }

    def dts(Acta acta) {
        def contrato = acta.contrato
        def obra = contrato.oferta.concurso.obra
        def suspensiones = Modificaciones.findAllByObraAndTipo(obra, 'S')

        def tabla = "<table class='table table-bordered table-condensed'>"

        tabla += "<thead>"
        tabla += "<tr>"
        tabla += "<th>N.</th>"
        tabla += "<th>N. de días</th>"
        tabla += "<th>Periodo</th>"
        tabla += "<th>Trámite</th>"
        tabla += "<th>Fecha</th>"
        tabla += "<th>Motivo</th>"
        tabla += "</tr>"
        tabla += "</thead>"

        tabla += "<tbody>"
        suspensiones.eachWithIndex { susp, i ->
            tabla += "<tr>"
            tabla += "<td>${i + 1}</td>"
            tabla += "<td class='tar'>${susp.dias}</td>"
            tabla += "<td>del ${fechaConFormato(fecha: susp.fechaInicio, formato: 'dd-MM-yyyy')} al ${fechaConFormato(fecha: susp.fechaFin, formato: 'dd-MM-yyyy')}</td>"
            tabla += "<td>${susp.memo}</td>"
            tabla += "<td>${fechaConFormato(fecha: susp.fecha, formato: 'dd-MM-yyyy')}</td>"
            tabla += "<td>${susp.motivo}</td>"
            tabla += "</tr>"
        }
        tabla += "</tbody>"

        tabla += "</table>"
        return tabla
    }

    def rpr(Acta acta) {
        def tabla = "<table class='table table-bordered table-condensed'>"
        tabla += "<thead>"

        tabla += "<tr>"
        tabla += "<th>N.</th>"
        tabla += "<th>Periodo</th>"
        tabla += "<th>Valor provisional</th>"
        tabla += "<th>Valor definitivo</th>"
        tabla += "<th>Diferencia</th>"
        tabla += "</tr>"

        tabla += "</thead>"

        tabla += "</table>"
        return tabla
    }

    Closure fechaConFormato = { attrs ->
        def fecha = attrs.fecha
        def formato = attrs.formato ?: "dd-MMM-yy"
        def meses = ["", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"]
        def mesesLargo = ["", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"]
        def strFecha = ""
//        println ">>" + fecha + "    " + formato
        if (fecha) {
            switch (formato) {
                case "MMM-yy":
                    strFecha = meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yy")
                    break;
                case "dd-MM-yyyy":
                    strFecha = "" + fecha.format("dd-MM-yyyy")
                    break;
                case "dd-MMM-yyyy":
                    strFecha = "" + fecha.format("dd") + "-" + meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yyyy")
                    break;
                case "dd-MMM-yy":
                    strFecha = "" + fecha.format("dd") + "-" + meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yy")
                    break;
                case "dd MMMM yyyy":
                    strFecha = "" + fecha.format("dd") + " de " + mesesLargo[fecha.format("MM").toInteger()] + " de " + fecha.format("yyyy")
                    break;
                default:
                    strFecha = "Formato " + formato + " no reconocido"
                    break;
            }
        }
//        println ">>>>>>" + strFecha
        out << strFecha
    }
}
