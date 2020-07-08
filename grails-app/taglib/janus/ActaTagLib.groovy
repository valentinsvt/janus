package janus

import janus.actas.Acta
import janus.ejecucion.DetallePlanilla
import janus.ejecucion.DetallePlanillaCosto
import janus.ejecucion.DetallePlanillaEjecucion
import janus.ejecucion.MultasPlanilla
import janus.ejecucion.PeriodoPlanilla
import janus.ejecucion.Planilla
import janus.ejecucion.ReajustePlanilla
import janus.ejecucion.TipoPlanilla
import janus.pac.DocumentoProceso

class ActaTagLib {

    static namespace = "acta"

    def preciosService
    def dbConnectionService

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
                "&deg;"   : "°",
                "&nbsp;"  : " ",
                "&acute;" : "",
                "&uuml;" : "ü",
                "&Uuml;" : "Ü"
//                ">" : "&gt;",   -- no usar en texto html
//                "<" : "&lt;",
        ]
        def str = attrs.str
//        println "attrs.... ${attrs.str}"
        replace.each { busca, nuevo ->
            if(str){
                str = str.replaceAll(busca, nuevo)
            }
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
//        println "closure --- ${attrs}"
        def str = ""
        def acta = attrs.acta
        if (attrs.tipo && acta) {
            def tipo = attrs.tipo.toLowerCase()
            str += "$tipo"(acta)     /* llama a las funciones tipoTabla(acta), ej: dtp(acta) **/
        }
        out << str
    }

    def rbr(Acta acta) {
        def contrato = acta.contrato
        def obra = contrato.oferta.concurso.obra

/*
        def planillasAvance = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
            order("fechaInicio", "asc")
        }
*/

        def planillasAvance = Planilla.findAllByContratoAndTipoPlanillaInList(contrato, TipoPlanilla.findAllByCodigoInList(['P', 'Q', 'O']), [sort: 'fechaInicio'])

        def indirecto = obra.totales / 100
        preciosService.ac_rbroObra(obra.id)
        def detalles = [:]
//        def volumenes = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def volumenes = VolumenContrato.findAllByObra(obra, [sort: "volumenOrden"])

        volumenes.each { vol ->
            vol.refresh()
//            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, vol.item.id)
//            def precio = (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2)

            if (!detalles[vol.subPresupuesto]) {
                detalles[vol.subPresupuesto] = [:]
            }
            if (!detalles[vol.subPresupuesto][vol.item]) {
                detalles[vol.subPresupuesto][vol.item] = [
                        codigo  : vol.item.codigo,
                        nombre  : vol.item.nombre,
                        unidad  : vol.item.unidad.codigo,
//                        precio  : vol.volumenSubtotal,
                        precio  : vol.volumenPrecio,
                        cantidad: [
                                contratado: 0,
                                ejecutado : 0
                        ],
                        valor   : [
                                contratado: 0,
                                ejecutado : 0
                        ]
                ]
            }
            detalles[vol.subPresupuesto][vol.item].cantidad.contratado += vol.volumenCantidad
            detalles[vol.subPresupuesto][vol.item].valor.contratado += vol.volumenSubtotal
        }

        planillasAvance.each { pla ->
//            def det = DetallePlanilla.findAllByPlanilla(pla)
            def det = DetallePlanillaEjecucion.findAllByPlanilla(pla)
            det.each { dt ->
                if (detalles[dt.volumenContrato.subPresupuesto][dt.volumenContrato.item]) {
                    detalles[dt.volumenContrato.subPresupuesto][dt.volumenContrato.item].cantidad.ejecutado += dt.cantidad
                    detalles[dt.volumenContrato.subPresupuesto][dt.volumenContrato.item].valor.ejecutado += dt.monto
                } else {
                    println "no existe detalle para " + dt.volumenContrato.item + "???"
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

                nombre = nombre.decodeHTML()
                nombre = nombre.replaceAll(/</, /&lt;/);    /** < como parte del nombre del rubro **/
                nombre = nombre.replaceAll(/>/, /&gt;/);
                nombre = nombre.replaceAll(/"/, /&quot;/);

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
//                println "++++ ${elem.nombre}"
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

//        println "tabla2:" + tabla
        return tabla
    }

    def dtp(Acta acta) {  // detalle de planillas  numeral 4.2
        def contrato = acta.contrato
        def cn = dbConnectionService.getConnection()

/*
        def planillas = Planilla.withCriteria {
            eq("contrato", contrato)
            or {
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
            }
            order("fechaIngreso", "asc")
        }
*/
        def planillas = Planilla.findAllByContratoAndTipoPlanillaInList(contrato,
                TipoPlanilla.findAllByCodigoInList(['A', 'P', 'Q', 'O', 'R', 'B', 'L']), [sort: 'fechaIngreso'])
//        println "planillas: ${planillas.valor}"

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
            if (planilla.tipoPlanilla.codigo in ["A", "B"]) {
                periodo = "ANTICIPO"
//                valor = planilla.reajuste
                valor = 0
            } else {
                periodo = "DEL " + fechaConFormato(fecha: planilla.fechaInicio, formato: "dd-MM-yyyy") +
                        " AL " + fechaConFormato(fecha: planilla.fechaFin, formato: "dd-MM-yyyy")
                valor = planilla.valor
            }
            // si existe Costo + % de la planilla se debe incluir en valor
            def c_mas_p = Planilla.findByPadreCosto(planilla)?.valor
            if(c_mas_p){
                valor += c_mas_p
            }

            if(planilla.tipoPlanilla.codigo in ["A", "B"]) {
                anticipo = -planilla.valor
            } else {
                anticipo = planilla.descuentos
            }
//            multas = planilla.multaPlanilla + planilla.multaRetraso
//            println ".... inicia multas con $multas"
            multas = MultasPlanilla.executeQuery("select sum(monto) from MultasPlanilla where planilla = :p", [p: planilla])[0]?:0
//            println "ml1.... inicia multas con $multas"
            multas += cn.rows("select coalesce(plnlmles,0) suma from plnl where plnl__id = ${planilla.id}")[0].suma
//            println ".... inicia multas con $multas"
            multas += cn.rows("select coalesce(plnlnpvl,0) suma from plnl where plnl__id = ${planilla.id}")[0].suma
//            println "plnl: ${planilla.id} multas: $multas"
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

    def oad(Acta acta) {   // detalle de obras adicionales
        def contrato = acta.contrato

/*
        def planillaAnticipo = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
            order("fechaIngreso", "asc")
        }
*/

        def planillasAvance = Planilla.findAllByContratoAndTipoPlanillaInList(contrato,
                TipoPlanilla.findAllByCodigoInList(['P', 'Q', 'R']))

        def planillasCosto = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("C"))
            order("fechaIngreso", "asc")
        }

        def tabla = "<table class='table table-bordered table-condensed tablaPq'>"

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
        def adicionales = (planillasAvance.sum { it.valor } ?: 0) - contrato.monto
//        println "&&& planillasAvance $adicionales"

        def valor = (adicionales > 0) ? adicionales : 0
        def prct =  (adicionales > 0) ? (adicionales / contrato.monto) * 100 : 0
        def costo = planillasCosto.sum { it.valor } ?: 0
        def prctCosto = (costo / contrato.monto) * 100

        def prctTotal = ((valor + costo) / contrato.monto) * 100
//        def respaldo = DocumentoProceso.findByConcursoAndDescripcionIlike(contrato.oferta.concurso, '%respaldo%adicio%')
        def respaldo = contrato.adicionales

        tabla += "<tbody>"
        tabla += "<tr>"
        tabla += "<td class='tar'>${numero(numero: valor)}</td>"
        tabla += "<td class='tar'>${numero(numero: prct)}</td>"
        tabla += "<td class='tar'>${numero(numero: costo)}</td>"
        tabla += "<td class='tar'>${numero(numero: prctCosto)}</td>"
        tabla += "<td class='tar'>${respaldo}</td>"
        tabla += "<td class='tar'></td>"
        tabla += "<td class='tar'></td>"
        tabla += "<td class='tar'>${numero(numero: prctTotal)}</td>"
        tabla += "</tr>"

        tabla += "<tr>"
        tabla += "<td colspan='6' class='tar'><strong>VALOR TOTAL DE OBRAS ADICIONALES</strong></td>"
        tabla += "<td colspan='2' class='tar'><strong>${numero(numero: (valor + costo))}</strong></td>"
        tabla += "</tr>"

        tabla += "</tbody>"

//        tabla += "<tfoot>"
//        tabla += "</toot>"

        tabla += "</table>"
        return tabla
    }

    def ocp(Acta acta) {    // 4.4 resmen de obra bajo la modalidad de costo + %
        def contrato = acta.contrato

        def planillasCosto = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("C"))
            order("fechaIngreso", "asc")
        }

        def tabla = ""
        def prct
        if (planillasCosto.size() > 0) {
//            prct = DetallePlanillaCosto.findByPlanilla(p1)?.indirectos
            prct = planillasCosto.first().contrato?.indirectos
            tabla = "<table class='table table-bordered table-condensed tabalPq'>"  // tablaPq hace que no se parta la tabla

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
                if (plaAv) {
                    def detalles = DetallePlanillaCosto.findAllByPlanilla(planilla)
                    def valor = detalles.sum { it.monto } ?: 0
                    def indi = detalles.sum { it.montoIndirectos } ?: 0

                    totalValor += valor
                    totalIndi += indi
                    total += valor
                    total += indi

                    tabla += "<tr>"
                    tabla += "<td>${fechaConFormato(fecha: planilla.fechaIngreso)}</td>"
                    tabla += "<td>${planilla.numero}</td>"
//                    tabla += "<td>${periodos.last().periodo.descripcion}</td>"
                    tabla += "<td>DEL ${planilla.padreCosto.fechaInicio.format('dd-MM-yyyy')} AL ${planilla.padreCosto.fechaFin.format('dd-MM-yyyy')}</td>"
                    tabla += "<td class='tar'>${numero(numero: valor)}</td>"
                    tabla += "<td class='tar'>${numero(numero: indi)}</td>"
                    tabla += "<td class='tar'>${numero(numero: valor + indi)}</td>"
                    tabla += "</tr>"
                }
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
        }
        return tabla
    }

    def rrp(Acta acta) {   // 4.5 resumen de reajuste de precios
        def contrato = acta.contrato

//        def planillas = Planilla.findAllByContratoAndTipoPlanillaInList(contrato, TipoPlanilla.findAllByCodigoInList(['A', 'P', 'Q', 'O']), [sort: 'fechaIngreso'])
        def ultimaPlnl = Planilla.findAllByContratoAndTipoPlanillaInListAndTipoContrato(contrato,
                TipoPlanilla.findAllByCodigoInList(['A', 'P', 'Q', 'O', 'R', 'L']), 'P', [sort: 'fechaIngreso']).last()
        def planillasCmpl = Planilla.findAllByContratoAndTipoPlanillaInListAndTipoContrato(contrato,
                TipoPlanilla.findAllByCodigoInList(['A', 'P', 'Q', 'O', 'R']), 'C', [sort: 'fechaIngreso'])
        def ultimaPlnlCmpl = planillasCmpl? planillasCmpl.last() : null
        println "---- principal: ${ultimaPlnl.id}"
        def rjpl = ReajustePlanilla.findAllByPlanilla(ultimaPlnl, [sort: 'periodo'])
        println "---- cmpl: ${ultimaPlnlCmpl?.id}"
        def rjplCmpl
        if(ultimaPlnlCmpl) rjplCmpl = ReajustePlanilla.findAllByPlanilla(ultimaPlnlCmpl, [sort: 'periodo'])

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
        rjpl.each { rj ->
            def periodo
            if (rj.planillaReajustada.tipoPlanilla.codigo in ["A", 'B']) {
                periodo = "ANTICIPO"
            } else {
//                periodo = "DEL " + fechaConFormato(fecha: rj.planillaReajustada.fechaInicio, formato: "dd-MM-yyyy") +
//                        " AL " + fechaConFormato(fecha: rj.planillaReajustada.fechaFin, formato: "dd-MM-yyyy")
                periodo = "DEL " + fechaConFormato(fecha: rj.fechaInicio, formato: "dd-MM-yyyy") +
                        " AL " + fechaConFormato(fecha: rj.fechaFin, formato: "dd-MM-yyyy")
            }
            total += rj.valorReajustado
            tabla += "<tr>"
            tabla += "<td>${fechaConFormato(fecha: rj.planillaReajustada.fechaIngreso)}</td>"
            tabla += "<td>${rj.planillaReajustada.numero}</td>"
            tabla += "<td>${periodo}</td>"
            tabla += "<td class='tar'>${numero(numero: rj.valorReajustado)}</td>"
            tabla += "</tr>"
        }

        if(rjplCmpl) {
            rjplCmpl.each { rj ->
                def periodo
                if (rj.planillaReajustada.tipoPlanilla.codigo in ['B']) {
                    periodo = "ANTICIPO COMPL."
                } else {
//                periodo = "DEL " + fechaConFormato(fecha: rj.planillaReajustada.fechaInicio, formato: "dd-MM-yyyy") +
//                        " AL " + fechaConFormato(fecha: rj.planillaReajustada.fechaFin, formato: "dd-MM-yyyy")
                    periodo = "DEL " + fechaConFormato(fecha: rj.fechaInicio, formato: "dd-MM-yyyy") +
                            " AL " + fechaConFormato(fecha: rj.fechaFin, formato: "dd-MM-yyyy")
                }
                total += rj.valorReajustado
                tabla += "<tr>"
                tabla += "<td>${fechaConFormato(fecha: rj.planillaReajustada.fechaIngreso)}</td>"
                tabla += "<td>${rj.planillaReajustada.numero}</td>"
                tabla += "<td>${periodo}</td>"
                tabla += "<td class='tar'>${numero(numero: rj.valorReajustado)}</td>"
                tabla += "</tr>"
            }
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

    def rgv(Acta acta) {  // 4.6 RESUMEN GENERAL DE VALORES
        def contrato = acta.contrato

        def planillaAnticipo = Planilla.findAllByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo('A'))
        def planillasCosto = Planilla.findAllByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo('C'))
        def planillasAvance = Planilla.findAllByContratoAndTipoPlanillaInList(contrato,
                TipoPlanilla.findAllByCodigoInList(['P', 'Q', 'R']), [sort: 'fechaIngreso'])

        def av = planillasAvance.sum { it.valor } ?: 0
        def cp = planillasCosto.sum { it.valor } ?: 0
        def total1 = av + cp

        def ultimaPlnl = Planilla.findAllByContratoAndTipoPlanillaInListAndTipoContrato(contrato,
                TipoPlanilla.findAllByCodigoInList(['A', 'P', 'Q', 'O', 'R', 'L']), 'P', [sort: 'fechaIngreso']).last()
        def planillasCmpl = Planilla.findAllByContratoAndTipoPlanillaInListAndTipoContrato(contrato,
                TipoPlanilla.findAllByCodigoInList(['A', 'P', 'Q', 'O', 'R']), 'C', [sort: 'fechaIngreso'])
        def ultimaPlnlCmpl = planillasCmpl? planillasCmpl.last() : null
//        def ultimaPlnl = Planilla.findAllByContratoAndTipoPlanillaInList(contrato,
//                TipoPlanilla.findAllByCodigoInList(['A', 'P', 'Q', 'R']), [sort: 'fechaIngreso']).last()

        def total2 = ReajustePlanilla.executeQuery("select sum(valorReajustado) from ReajustePlanilla " +
                "where planilla = :p", [p:ultimaPlnl])[0]?:0

        def total2Cmpl

        if(ultimaPlnlCmpl) total2Cmpl = ReajustePlanilla.executeQuery("select sum(valorReajustado) from ReajustePlanilla " +
                "where planilla = :p", [p:ultimaPlnlCmpl])[0]?:0

        if(total2Cmpl) {
            total2 += total2Cmpl
        }

//        def ran = planillaAnticipo.sum { it.reajuste } ?: 0
//        def rav = planillasAvance.sum { it.reajuste } ?: 0
//        def total2 = ran + rav

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

    def dta(Acta acta) {   // detalle de ampliaciones
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

    def dts(Acta acta) {   // detalle de suspensiones
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

    def rpr(Acta acta) {    // resumen de pago de reajustes  8.1
        def cn = dbConnectionService.getConnection()
        def contrato = acta.contrato
//        def planillas = Planilla.findAllByContrato(contrato, [sort: "numero"])
        def ultimaPlnl = Planilla.findAllByContratoAndTipoPlanillaInListAndTipoContrato(contrato,
                TipoPlanilla.findAllByCodigoInList(['A', 'P', 'Q', 'O', 'R', 'L']), 'P', [sort: 'fechaIngreso']).last()
        def planillasCmpl = Planilla.findAllByContratoAndTipoPlanillaInListAndTipoContrato(contrato,
                TipoPlanilla.findAllByCodigoInList(['A', 'P', 'Q', 'O', 'R']), 'C', [sort: 'fechaIngreso'])
        def ultimaPlnlCmpl = planillasCmpl? planillasCmpl.last() : null

        println "---- principal: ${ultimaPlnl?.id}"
        def rjpl = ReajustePlanilla.findAllByPlanilla(ultimaPlnl, [sort: 'periodo'])
        println "---- cmpl: ${ultimaPlnlCmpl?.id}"

        def rjplCmpl
        if(ultimaPlnlCmpl) rjplCmpl = ReajustePlanilla.findAllByPlanilla(ultimaPlnlCmpl, [sort: 'periodo'])

//        println "---------------------------------------------------------------"
//        println acta
//        println contrato
//        println planillas
//        println "---------------------------------------------------------------"

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

        tabla += "<tbody>"
        def totalProvisional = 0, totalDefinitivo = 0, totalDiferencia = 0
        def liquidacion = Planilla.findAllByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo('L'))
        if(liquidacion) {
            liquidacion = liquidacion.last()
        }
        def diferencia = 0
        def rj_lq = 0
        def sql = "select rjplvlor from rjpl where plnl__id = ${liquidacion.id} and plnlrjst = "


        rjpl.each { rj ->
            if (rj.planillaReajustada.tipoPlanilla.codigo != "L" && rj.planillaReajustada.tipoPlanilla.codigo != "M" && rj.planillaReajustada.tipoPlanilla.codigo != "C") {
                tabla += "<tr>"
                tabla += "<td style='text-align:center;'>${rj.planillaReajustada.numero}</td>"
                tabla += "<td style='text-align:center;'>"
                if (rj.planillaReajustada.tipoPlanilla.codigo == "A") {
                    tabla += "ANTICIPO"
                } else {
//                    tabla += "DEL ${rj.planillaReajustada.fechaInicio?.format('yyyy-MM-dd')} AL ${rj.planillaReajustada.fechaFin?.format('yyyy-MM-dd')}"
                    tabla += "DEL ${rj.fechaInicio?.format('yyyy-MM-dd')} AL ${rj.fechaFin?.format('yyyy-MM-dd')}"
                }
                tabla += "</td>"

                if(liquidacion) {
//                    println "liquidación.... ${ultimaPlnl.id}, lq: ${liquidacion.id}"
                    rj_lq = cn.rows(sql + " ${rj.planillaReajustada.id}".toString())[0].rjplvlor
                    diferencia = rj_lq - rj.valorReajustado
                    tabla += "<td style='text-align:center;'>${numero(numero: rj.valorReajustado)}</td>"
                    tabla += "<td style='text-align:center;'>${numero(numero: rj_lq)}</td>"
                    tabla += "<td style='text-align:center;'>${numero(numero: diferencia)}</td>"
                } else {
                    diferencia = 0
                    tabla += "<td style='text-align:center;'>${numero(numero: rj.valorReajustado)}</td>"
                    tabla += "<td style='text-align:center;'></td>"
                    tabla += "<td style='text-align:center;'></td>"
                }


                totalProvisional += rj.valorReajustado
                if(liquidacion) {
                    totalDefinitivo += rj_lq
                }
//                else {
//                    totalDefinitivo += rj.planillaReajustada.reajusteLiq
//                }
                totalDiferencia += diferencia

                tabla += "</tr>"
            } else if(rj.planillaReajustada.tipoPlanilla == 'L'){

                diferencia = rj.valorReajustado
                tabla += "<td style='text-align:center;'>${numero(numero: rj.valorReajustado)}</td>"
                tabla += "<td style='text-align:center;'></td>"
                tabla += "<td style='text-align:center;'></td>"
            }
        }

        if(rjplCmpl) {
            rjplCmpl.each { rj ->
                if (rj.planillaReajustada.tipoPlanilla.codigo != "L" && rj.planillaReajustada.tipoPlanilla.codigo != "M" && rj.planillaReajustada.tipoPlanilla.codigo != "C") {
                    tabla += "<tr>"
                    tabla += "<td style='text-align:center;'>${rj.planillaReajustada.numero}</td>"
                    tabla += "<td style='text-align:center;'>"
                    if (rj.planillaReajustada.tipoPlanilla.codigo == "A") {
                        tabla += "ANTICIPO"
                    } else {
//                    tabla += "DEL ${rj.planillaReajustada.fechaInicio?.format('yyyy-MM-dd')} AL ${rj.planillaReajustada.fechaFin?.format('yyyy-MM-dd')}"
                        tabla += "DEL ${rj.fechaInicio?.format('yyyy-MM-dd')} AL ${rj.fechaFin?.format('yyyy-MM-dd')}"
                    }
                    tabla += "</td>"

                    if(liquidacion) {
//                    println "liquidación.... ${ultimaPlnl.id}, lq: ${liquidacion.id}"
                        rj_lq = cn.rows(sql + " ${rj.planillaReajustada.id}".toString())[0].rjplvlor
                        diferencia = rj_lq - rj.valorReajustado
                        tabla += "<td style='text-align:center;'>${numero(numero: rj.valorReajustado)}</td>"
                        tabla += "<td style='text-align:center;'>${numero(numero: rj_lq)}</td>"
                        tabla += "<td style='text-align:center;'>${numero(numero: diferencia)}</td>"
                    } else {
                        diferencia = 0
                        tabla += "<td style='text-align:center;'>${numero(numero: rj.valorReajustado)}</td>"
                        tabla += "<td style='text-align:center;'></td>"
                        tabla += "<td style='text-align:center;'></td>"
                    }


                    totalProvisional += rj.valorReajustado
                    if(liquidacion) {
                        totalDefinitivo += rj_lq
                    }
/*
                    else {
                        totalDefinitivo += rj.planillaReajustada.reajusteLiq
                    }
*/
                    totalDiferencia += diferencia

                    tabla += "</tr>"
                } else if(rj.planillaReajustada.tipoPlanilla == 'L'){

                    diferencia = rj.valorReajustado
                    tabla += "<td style='text-align:center;'>${numero(numero: rj.valorReajustado)}</td>"
                    tabla += "<td style='text-align:center;'></td>"
                    tabla += "<td style='text-align:center;'></td>"
                }
            }
        }
        tabla += "</tbody>"

        tabla += "<tfoot>"
        tabla += "<tr>"
        tabla += "<th colspan='2' style='text-align:right;'>TOTAL</th>"
        tabla += "<th style='text-align:center;'>${numero(numero: totalProvisional)}</th>"
        tabla += "<th style='text-align:center;'>${numero(numero: totalDefinitivo)}</th>"
        tabla += "<th style='text-align:center;'>${numero(numero: totalDiferencia)}</th>"
        tabla += "</tr>"
        tabla += "</tfoot>"

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
