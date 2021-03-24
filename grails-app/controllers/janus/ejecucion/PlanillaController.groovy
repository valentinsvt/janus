package janus.ejecucion

import groovy.json.JsonBuilder
import janus.*
import janus.actas.Acta
import janus.pac.CrngEjecucionObra
import janus.pac.CronogramaContratado
import janus.pac.CronogramaContrato
import janus.pac.CronogramaEjecucion
import janus.pac.DocumentoProceso
import janus.pac.Garantia
import janus.pac.PeriodoEjecucion

import java.text.DecimalFormat

class PlanillaController extends janus.seguridad.Shield {

    def preciosService
    def buscadorService
    def diasLaborablesService
    def dbConnectionService
    def planillasService

/*
    def tests() {
        println rep.capitalize(string: "Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
        println rep.capitalize { "Lorem ipsum dolor sit amet, consectetur adipiscing elit." }
    }
*/

    def configPedidoPagoAnticipo() {
        def planilla = Planilla.get(params.id)
        def contrato = planilla.contrato
        def obra = contrato.obra
        def rjpl = ReajustePlanilla.findByPlanilla(planilla)

        def texto = Pdfs.findAllByPlanilla(planilla)
        def textos = []

        def tabla = "<table border='0'>"
        tabla += "<tr>"
        if (planilla.tipoPlanilla.codigo == 'A') {
            tabla += "<th class='tl'>${numero(contrato?.porcentajeAnticipo, 0)}% de anticipo</t>"
        } else {
            tabla += "<th class='tl'>Valor planilla</t>"
        }
        tabla += "<td class='tr'>${numero(planilla.valor, 2)}</td>"
        tabla += "</tr>"
        tabla += "<tr>"
        tabla += "<th class='tl'>(+) Reajuste provisional ${planilla.tipoPlanilla.codigo == 'A' ? 'del anticipo' : ''}</th>"
        tabla += "<td class='tr'>${numero((rjpl?.valorReajustado ?: 0), 2)}</td>"
        tabla += "</tr>"
        tabla += "<tr>"
        tabla += "<th class='tl'>SUMA</th>"
        tabla += "<td class='tr'>${numero(planilla.valor + (rjpl?.valorReajustado ?: 0), 2)}</td>"
        tabla += "</tr>"
        tabla += "<tr>"
        tabla += "<th class='tl'>A FAVOR DEL CONTRATISTA</th>"
        tabla += "<td class='tr'>${numero(planilla.valor + (rjpl?.valorReajustado ?: 0), 2)}</td>"
        tabla += "</tr>"
        tabla += "</table>"

//        println "valor: " + numero(planilla.valor + planilla.reajuste, 2).replaceAll(',','').toDouble()

        if (texto.size() == 0) {

            def numerosALetras = NumberToLetterConverter.convertNumberToLetter(planilla?.valor + (rjpl?.valorReajustado ?: 0))

            def strParrafo1 = "De acuerdo al Contrato N° ${contrato?.codigo}, suscrito el " +
                    "${fechaConFormato(contrato?.fechaSubscripcion, 'dd-MM-yyyy')}, por el valor de " +
                    "USD ${numero(contrato?.monto, 2)}  sin incluir IVA, para realizar ${contrato?.objeto}, " +
                    "ubicada en el Barrio ${contrato?.oferta?.concurso?.obra?.barrio}, " +
                    "Parroquia ${contrato?.oferta?.concurso?.obra?.parroquia}, " +
                    "Cantón ${contrato?.oferta?.concurso?.obra?.parroquia?.canton}, de la Provincia de " +
                    "${contrato?.oferta?.concurso?.obra?.parroquia?.canton?.provincia?.nombre}"

            def strParrafo2 = "Sírvase disponer el trámite respectivo para el pago del ${numero(contrato?.porcentajeAnticipo, 0)}% " +
                    "del anticipo, a favor de ${contrato?.oferta?.proveedor.pagarNombre} "
            def editParrafo2 = "según claúsula sexta, literal a) del citado documento. El detalle es el siguiente:"

            def strParrafo3 = "Son ${numerosALetras}"

            def strParrafo4 = "A fin de en forma oportuna dar al contratista la orden de inicio de la obra, informar " +
                    "a esta Dirección la fecha de transferencia del anticipo a la cuenta del contratista."

            textos[0] = [
                    [tipo: "S", string: strParrafo1]
            ]
            textos[1] = [
                    [tipo: "S", string: strParrafo2],
                    [tipo: "E", string: editParrafo2, w: "940px", h: "25px"]
            ]
            textos[2] = [
                    [tipo: "S", string: tabla]
            ]
            textos[3] = [
                    [tipo: "S", string: strParrafo3]
            ]
            textos[4] = [
                    [tipo: "E", string: strParrafo4, w: "940px", h: "50px"]
            ]
        } else if (texto.size() > 1) {
            println "Se encontraron ${texto.size()} textos para la obra ${obra.id}: ${texto.id}"
            texto = texto.first()
        } else {
            texto = texto.first()
        }

        return [planilla: planilla, obra: obra, contrato: contrato, textos: textos, texto: texto, tabla: tabla]
    }

    def configPedidoPago() {
        def planilla = Planilla.get(params.id)
        def contrato = planilla.contrato
        def obra = contrato.obra
        def costo = Planilla.findByPadreCosto(planilla)?.valor?:0

        def plnlAnterior = Planilla.findAllByContratoAndTipoPlanillaInListAndFechaPresentacionLessThan(planilla.contrato,
                TipoPlanilla.findAllByCodigoInList(['A', 'P']), planilla.fechaPresentacion, [sort: 'fechaPresentacion']).last()
        def rjplAnterior = ReajustePlanilla.executeQuery("select sum(valorReajustado) from ReajustePlanilla where planilla = :p", [p: plnlAnterior])
        def rjpl = ReajustePlanilla.executeQuery("select sum(valorReajustado) from ReajustePlanilla where planilla = :p", [p: planilla])
        def reajuste = rjpl[0] - rjplAnterior[0]
//        println "---reajuste de la planillas: $rjplAnterior, actual $rjpl"

        def texto = Pdfs.findAllByPlanilla(planilla)
        def textos = []

        def multas = 0
        // TODO: revisar si estos valores afectan al valor a pagar, si si, tomar de MLPL
//        multas = planilla.multaDisposiciones + planilla.multaIncumplimiento + planilla.multaPlanilla + planilla.multaRetraso
        multas = MultasPlanilla.executeQuery("select sum(monto) from MultasPlanilla where planilla = :p", [p: planilla])[0]?:0
        multas += planilla.multaEspecial?:0

//        println "multas: ${multas}, espacial: ${planilla.multaEspecial}"

        def tabla = "<table border='0'>"
        tabla += "<tr>"
        if (planilla.tipoPlanilla.codigo == 'A') {
            tabla += "<th class='tl'>${numero(contrato?.porcentajeAnticipo, 0)}% de anticipo</t>"
        } else {
            tabla += "<th class='tl'>Valor planilla</t>"
        }
        tabla += "<td class='tr'>${numero(planilla.valor, 2)}</td>"
        tabla += "</tr>"
        tabla += "<tr>"
        tabla += "<th class='tl'>(+) Reajuste provisional ${planilla.tipoPlanilla.codigo == 'A' ? 'del anticipo' : ''}</th>"
        tabla += "<td class='tr'>${numero(reajuste, 2)}</td>"
        tabla += "</tr>"
        tabla += "<tr>"
        tabla += "<th class='tl'>(-) Anticipo</th>"
        tabla += "<td class='tr'>${numero(planilla.descuentos, 2)}</td>"
        tabla += "</tr>"
        tabla += "<tr>"
        tabla += "<th class='tl'>(-) Multas</th>"
        tabla += "<td class='tr'>${numero(multas, 2)}</td>"
        tabla += "</tr>"
        tabla += "<tr>"
        tabla += "<th class='tl'>SUMA</th>"
        tabla += "<td class='tr'>${numero(planilla.valor + reajuste - planilla.descuentos - multas, 2)}</td>"
        tabla += "</tr>"

        if(planilla.noPagoValor > 0) {
            tabla += "<tr>"
            tabla += "<th class='tl'>${planilla.noPago}</th>"
            tabla += "<td class='tr'>${numero(planilla.noPagoValor, 2)}</td>"
            tabla += "</tr>"
        }

        if(costo) {
            tabla += "<tr>"
            tabla += "<th class='tl'>Costo + Porcentaje</th>"
            tabla += "<td class='tr'>${numero(costo, 2)}</td>"
            tabla += "</tr>"
        }

        tabla += "<tr>"
        tabla += "<th class='tl'>A FAVOR DEL CONTRATISTA</th>"
        tabla += "<td class='tr'>${numero(planilla.valor + reajuste - planilla.descuentos - multas - planilla.noPagoValor + costo, 2)}</td>"
        tabla += "</tr>"
        tabla += "</table>"

        if (texto.size() == 0) {

            def totalLetras = planilla.valor + reajuste - planilla.descuentos - multas - planilla.noPagoValor + costo
            def neg = ""
            if (totalLetras < 0) {
                totalLetras = totalLetras * -1
                neg = "MENOS "
            }
            def numerosALetras = NumberToLetterConverter.convertNumberToLetter(totalLetras)

            def strParrafo1 = "De acuerdo al Contrato N° ${contrato?.codigo}, suscrito el ${fechaConFormato(contrato?.fechaSubscripcion, 'dd-MM-yyyy')}, por el valor de " +
                    "USD ${numero(contrato?.monto, 2)}  sin incluir IVA, para realizar ${contrato?.objeto}, " +
                    "ubicada en el Barrio ${contrato?.oferta?.concurso?.obra?.barrio}, Parroquia ${contrato?.oferta?.concurso?.obra?.parroquia}, " +
                    "Cantón ${contrato?.oferta?.concurso?.obra?.parroquia?.canton}, de la Provincia de ${contrato?.oferta?.concurso?.obra?.parroquia?.canton?.provincia?.nombre}"

//            def strParrafo2 = "Sírvase disponer el trámite respectivo para el pago de la planilla, a favor de ${nombrePersona(contrato?.oferta?.proveedor, 'prov')} "
            def strParrafo2 = "Sírvase disponer el trámite respectivo para el pago de la planilla, a favor de ${contrato?.oferta?.proveedor.pagarNombre} "
            def editParrafo2 = "según claúsula sexta, literal a) del citado documento. El detalle es el siguiente:"

            def strParrafo3 = "Son ${neg}${numerosALetras}"

            def strParrafo4 = "A fin de en forma oportuna dar al contratista la orden de inicio de la obra, informar a esta Dirección la fecha de transferencia del valor a pagar a la cuenta del contratista."

            textos[0] = [
                    [tipo: "S", string: strParrafo1]
            ]
            textos[1] = [
                    [tipo: "S", string: strParrafo2],
                    [tipo: "E", string: editParrafo2, w: "940px", h: "25px"]
            ]
            textos[2] = [
                    [tipo: "S", string: tabla]
            ]
            textos[3] = [
                    [tipo: "S", string: strParrafo3]
            ]
            textos[4] = [
                    [tipo: "E", string: strParrafo4, w: "940px", h: "50px"]
            ]

        } else if (texto.size() > 1) {
            println "Se encontraron ${texto.size()} textos para la obra ${obra.id}: ${texto.id}"
            texto = texto.first()
        } else {
            texto = texto.first()
        }

        return [planilla: planilla, obra: obra, contrato: contrato, textos: textos, texto: texto, tabla: tabla]
    }

    def configPedidoPagoComp() {
        def planilla = Planilla.get(params.id)
        def contrato = planilla.contrato
        def obra = contrato.obra
        def costo = Planilla.findByPadreCosto(planilla)?.valor?:0

        def plnlAnterior = Planilla.findAllByContratoAndTipoPlanillaInListAndFechaPresentacionLessThan(planilla.contrato,
                TipoPlanilla.findAllByCodigoInList(['A', 'P']), planilla.fechaPresentacion, [sort: 'fechaPresentacion']).last()
        def rjplAnterior = ReajustePlanilla.executeQuery("select sum(valorReajustado) from ReajustePlanilla where planilla = :p", [p: plnlAnterior])
        def rjpl = ReajustePlanilla.executeQuery("select sum(valorReajustado) from ReajustePlanilla where planilla = :p", [p: planilla])
        def rjplComp = ReajustePlanilla.executeQuery("select sum(valorReajustado) from ReajustePlanilla where planilla = :p", [p: planilla.planillaCmpl])
        def reajuste = rjpl[0] - rjplAnterior[0]

        def rjplAntr = planillasService.reajusteAnterior(planilla)
        def rjplAcml = planillasService.reajusteAcumulado(planilla)
        def rjplActl = rjplAcml - rjplAntr

        def rjplAntrCp = planillasService.reajusteAnterior(planilla.planillaCmpl)
        def rjplAcmlCp = planillasService.reajusteAcumulado(planilla.planillaCmpl)
        rjplAntr += rjplAntrCp
        rjplAcml += rjplAcmlCp
        rjplActl = rjplAcml - rjplAntr

//        println "---reajuste de la planillas: $rjplAnterior, actual $rjpl"

        def texto = Pdfs.findAllByPlanilla(planilla)
        def textos = []

        def multas = 0
        // TODO: revisar si estos valores afectan al valor a pagar, si si, tomar de MLPL
//        multas = planilla.multaDisposiciones + planilla.multaIncumplimiento + planilla.multaPlanilla + planilla.multaRetraso
        multas = MultasPlanilla.executeQuery("select sum(monto) from MultasPlanilla where planilla = :p", [p: planilla])[0]?:0
        multas += planilla.multaEspecial?:0

//        println "multas: ${multas}, espacial: ${planilla.multaEspecial}"

        def tabla = "<table border='0'>"
        tabla += "<tr>"
        if (planilla.tipoPlanilla.codigo == 'A') {
            tabla += "<th class='tl'>${numero(contrato?.porcentajeAnticipo, 0)}% de anticipo</t>"
        } else {
            tabla += "<th class='tl'>Valor planilla</t>"
        }
        tabla += "<td class='tr'>${numero(planilla.valor + planilla.planillaCmpl.valor, 2)}</td>"
        tabla += "</tr>"
        tabla += "<tr>"
        tabla += "<th class='tl'>(+) Reajuste provisional ${planilla.tipoPlanilla.codigo == 'A' ? 'del anticipo' : ''}</th>"
        tabla += "<td class='tr'>${numero(rjplActl, 2)}</td>"
        tabla += "</tr>"
        tabla += "<tr>"
        tabla += "<th class='tl'>(-) Anticipo</th>"
        tabla += "<td class='tr'>${numero(planilla.descuentos, 2)}</td>"
        tabla += "</tr>"
        tabla += "<tr>"
        tabla += "<th class='tl'>(-) Multas</th>"
        tabla += "<td class='tr'>${numero(multas, 2)}</td>"
        tabla += "</tr>"
        tabla += "<tr>"
        tabla += "<th class='tl'>SUMA</th>"
        tabla += "<td class='tr'>${numero(planilla.valor + planilla.planillaCmpl.valor + rjplActl - planilla.descuentos - multas, 2)}</td>"
        tabla += "</tr>"

        if(planilla.noPagoValor > 0) {
            tabla += "<tr>"
            tabla += "<th class='tl'>${planilla.noPago}</th>"
            tabla += "<td class='tr'>${numero(planilla.noPagoValor, 2)}</td>"
            tabla += "</tr>"
        }

        if(costo) {
            tabla += "<tr>"
            tabla += "<th class='tl'>Costo + Porcentaje</th>"
            tabla += "<td class='tr'>${numero(costo, 2)}</td>"
            tabla += "</tr>"
        }

        tabla += "<tr>"
        tabla += "<th class='tl'>A FAVOR DEL CONTRATISTA</th>"
        tabla += "<td class='tr'>${numero(planilla.valor + planilla.planillaCmpl.valor + rjplActl - planilla.descuentos - multas - planilla.noPagoValor + costo, 2)}</td>"
        tabla += "</tr>"
        tabla += "</table>"

        if (texto.size() == 0) {
//            println "totalLetras: ${planilla.valor + reajuste - planilla.descuentos - multas - planilla.noPagoValor + costo}"
//            def totalLetras = planilla.valor + reajuste - planilla.descuentos - multas - planilla.noPagoValor + costo
            def totalLetras = planilla.valor + planilla.planillaCmpl.valor + rjplActl - planilla.descuentos - multas - planilla.noPagoValor + costo
            def neg = ""
            if (totalLetras < 0) {
                totalLetras = totalLetras * -1
                neg = "MENOS "
            }
            def numerosALetras = NumberToLetterConverter.convertNumberToLetter(totalLetras)

            def strParrafo1 = "De acuerdo al Contrato N° ${contrato?.codigo}, suscrito el ${fechaConFormato(contrato?.fechaSubscripcion, 'dd-MM-yyyy')}, por el valor de " +
                    "USD ${numero(contrato?.monto, 2)}  sin incluir IVA, para realizar ${contrato?.objeto}, " +
                    "ubicada en el Barrio ${contrato?.oferta?.concurso?.obra?.barrio}, Parroquia ${contrato?.oferta?.concurso?.obra?.parroquia}, " +
                    "Cantón ${contrato?.oferta?.concurso?.obra?.parroquia?.canton}, de la Provincia de ${contrato?.oferta?.concurso?.obra?.parroquia?.canton?.provincia?.nombre}"

//            def strParrafo2 = "Sírvase disponer el trámite respectivo para el pago de la planilla, a favor de ${nombrePersona(contrato?.oferta?.proveedor, 'prov')} "
            def strParrafo2 = "Sírvase disponer el trámite respectivo para el pago de la planilla, a favor de ${contrato?.oferta?.proveedor.pagarNombre} "
            def editParrafo2 = "según claúsula sexta, literal a) del citado documento. El detalle es el siguiente:"

            def strParrafo3 = "Son ${neg}${numerosALetras}"

            def strParrafo4 = "A fin de en forma oportuna dar al contratista la orden de inicio de la obra, informar a esta Dirección la fecha de transferencia del valor a pagar a la cuenta del contratista."

            textos[0] = [
                    [tipo: "S", string: strParrafo1]
            ]
            textos[1] = [
                    [tipo: "S", string: strParrafo2],
                    [tipo: "E", string: editParrafo2, w: "940px", h: "25px"]
            ]
            textos[2] = [
                    [tipo: "S", string: tabla]
            ]
            textos[3] = [
                    [tipo: "S", string: strParrafo3]
            ]
            textos[4] = [
                    [tipo: "E", string: strParrafo4, w: "940px", h: "50px"]
            ]

        } else if (texto.size() > 1) {
            println "Se encontraron ${texto.size()} textos para la obra ${obra.id}: ${texto.id}"
            texto = texto.first()
        } else {
            texto = texto.first()
        }

        return [planilla: planilla, obra: obra, contrato: contrato, textos: textos, texto: texto, tabla: tabla]
    }

    def configOrdenInicioObra() {
        def obra = Obra.get(params.id)
        def concurso = janus.pac.Concurso.findByObra(obra)
        def oferta = janus.pac.Oferta.findByConcurso(concurso)
        def contrato = Contrato.findByOferta(oferta)

        def texto = Pdfs.findAllByObra(obra)
        def textos = []

        if (texto.size() == 0) {

            def planillaDesc = Planilla.findByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigoIlike("A"))

            def edit11 = "Para los fines consiguientes me permito indicarle que la fecha de inicio del contrato N° "
            def str11 = contrato?.codigo.trim()
            def edit12 = ", para la construcción de "
            def str12 = obra?.descripcion.trim()
            def edit13 = ", ubicada en la Parroquia "
            def str13 = obra?.parroquia?.nombre.trim() + ", cantón " + obra?.parroquia?.canton?.nombre.trim()
            def edit14 = ", de la Provincia de Pichincha, por un valor de US\$ "
            def str14 = g.formatNumber(number: contrato?.monto, format: "##,##0", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2)
            def edit15 = " sin incluir IVA, consta de la cláusula octava, numeral 8.01, que señala que el plazo total " +
                    "que el contratista tiene para ejecutar, terminar y entregar a entera satisfacción es de "
            def str15 = NumberToLetterConverter.numberToLetter(contrato?.plazo).toLowerCase() + " días calendario (" +
                    g.formatNumber(number: contrato?.plazo, format: "##,##0", locale: "ec", maxFractionDigits: 0, minFractionDigits: 0) + "), "
            def edit16 = "contados a partir de la fecha de efectivización del anticipo y, en el numeral 8.02 se dice " +
                    "que se entenderá entregado el anticipo una vez transcurridas veinte y cuatro (24) horas de realizada "
            edit16 += "la transferencia de fondos a la cuenta bancaria que para el efecto indique el contratista. "

            def edit21 = "Tesorería de la Corporación, remite a la "
            def str21 = obra?.departamento?.direccion?.nombre.trim()
            def edit22 = ", copia del reporte de pago del "
            def str22 = contrato?.porcentajeAnticipo + "%"
            def edit23 = " del anticipo, por un valor de US\$ "
            def str23 = g.formatNumber(number: (planillaDesc?.valor + planillaDesc.reajuste), format: "##,##0", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2)
            def edit24 = ", menos los descuentos de ley, fue acreditado el "
            def str24 = fechaConFormato(planillaDesc?.fechaMemoPagoPlanilla, "dd MMMM yyyy").toLowerCase()

            def edit31 = "Por las razones indicadas la fecha de inicio de la obra, del contrato N° "
            def str31 = contrato?.codigo.trim()
            def edit32 = ", será el "
            def str32 = fechaConFormato(obra?.fechaInicio, "dd MMMM yyyy").toLowerCase() + "."

            textos[0] = [
                    [tipo: "E", string: edit11, w: "580px", h: "20px"],
                    [tipo: "S", string: str11],
                    [tipo: "E", string: edit12, w: "210px", h: "20px"],
                    [tipo: "S", string: str12],
                    [tipo: "E", string: edit13, w: "280px", h: "20px"],
                    [tipo: "S", string: str13],
                    [tipo: "E", string: edit14, w: "560px", h: "20px"],
                    [tipo: "S", string: str14],
                    [tipo: "E", string: edit15, w: "940px", h: "20px"],
                    [tipo: "S", string: str15],
                    [tipo: "E", string: edit16, w: "940px", h: "40px"]
            ]
            textos[1] = [
                    [tipo: "E", string: edit21, w: "300px", h: "20px"],
                    [tipo: "S", string: str21],
                    [tipo: "E", string: edit22, w: "225px", h: "20px"],
                    [tipo: "S", string: str22],
                    [tipo: "E", string: edit23, w: "240px", h: "20px"],
                    [tipo: "S", string: str23],
                    [tipo: "E", string: edit24, w: "330px", h: "20px"],
                    [tipo: "S", string: str24]
            ]
            textos[2] = [
                    [tipo: "E", string: edit31, w: "485px", h: "20px"],
                    [tipo: "S", string: str31],
                    [tipo: "E", string: edit32, w: "90px", h: "20px"],
                    [tipo: "S", string: str32]
            ]
        } else if (texto.size() > 1) {
            println "Se encontraron ${texto.size()} textos para la obra ${obra.id}: ${texto.id}"
            texto = texto.first()
        } else {
            texto = texto.first()
        }
        return [obra: obra, contrato: contrato, textos: textos, texto: texto]
    }

    def saveInicioObra() {
        def obra = Obra.get(params.id)
        def concurso = janus.pac.Concurso.findByObra(obra)
        def oferta = janus.pac.Oferta.findByConcurso(concurso)
        def contrato = Contrato.findByOferta(oferta)
        def planillaDesc = Planilla.findByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigoIlike("A"))
        def texto = new Pdfs()
        texto.obra = obra
        texto.fecha = new Date()

        def str11 = contrato?.codigo.trim()
        def str12 = obra?.descripcion.trim()
        def str13 = obra?.parroquia?.nombre.trim() + ", cantón " + obra?.parroquia?.canton?.nombre.trim()
        def str14 = g.formatNumber(number: contrato?.monto, format: "##,##0", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2)
        def str15 = NumberToLetterConverter.numberToLetter(contrato?.plazo).toLowerCase() + " días calendario (" +
                g.formatNumber(number: contrato?.plazo, format: "##,##0", locale: "ec", maxFractionDigits: 0, minFractionDigits: 0) + "), "

        def str21 = obra?.departamento?.direccion?.nombre.trim()
        def str22 = contrato?.porcentajeAnticipo + "%"
        def str23 = g.formatNumber(number: (planillaDesc?.valor + planillaDesc.reajuste), format: "##,##0", locale: "ec", maxFractionDigits: 2, minFractionDigits: 2)
        def str24 = fechaConFormato(planillaDesc?.fechaMemoPagoPlanilla, "dd MMMM yyyy").toLowerCase()

        def str31 = contrato?.codigo.trim()
        def str32 = fechaConFormato(obra?.fechaInicio, "dd MMMM yyyy").toLowerCase() + "."

        def textos = []

        textos[0] = [
                "edit11",  // 0    1
                str11,     // 1
                "edit12",  // 2    2
                str12,     // 3
                "edit13",  // 4    3
                str13,     // 5
                "edit14",  // 6    4
                str14,     // 7
                "edit15",  // 8    5
                str15,     // 9
                "edit16"   // 10   6
        ]
        textos[1] = [
                "edit21",  // 0    1
                str21,     // 1
                "edit22",  // 2    2
                str22,     // 3
                "edit23",  // 4    3
                str23,     // 5
                "edit24",  // 6    4
                str24      // 7
        ]
        textos[2] = [
                "edit31",  // 0    1
                str31,     // 1
                "edit32",  // 2    2
                str32      // 3
        ]

        params.each { k, v ->
            if (k.contains("edit")) {
                def parts = k.split("_")
                def parrafo = parts[1].toInteger()
                def edit = parts[2].toInteger()
                def np = parrafo - 1
                def ne = edit + (edit - 2)
//                println "" + parrafo + "(" + (parrafo - 1 + ")" + "   " + edit + "(" + (edit + (edit - 2))) + ")" + "    " + v
                textos[np][ne] = v
            }
        }
        def parrafos = []
        textos.eachWithIndex { p, i ->
            if (!parrafos[i]) {
                parrafos[i] = ""
            }
            p.each { str ->
                parrafos[i] += str
            }
        }
        texto.parrafo1 = parrafos[0]
        texto.parrafo2 = parrafos[1]
        texto.parrafo3 = parrafos[2]
        texto.parrafo4 = params.extra.trim()

        if (texto.save([flush: true])) {
            flash.clase = "alert-success"
            flash.message = "Orden de inicio de obra guardado exitosamente."
        } else {
            flash.clase = "alert-error"
            flash.message = "Ha ocurrido un error al guardar la orden de inicio de obra."
        }
        redirect(action: "configOrdenInicioObra", id: obra.id)
    }

    def savePedidoPagoAnticipo() {
        println "savePedidoPagoAnticipo textos "+params
        def planilla = Planilla.get(params.id)
        def contrato = planilla.contrato
        def obra = contrato.obra
        def texto = new Pdfs()
        texto.planilla = planilla
        texto.fecha = new Date()
        def reajuste = ReajustePlanilla.findByPlanilla(planilla).valorReajustado

//        def numerosALetras = NumberToLetterConverter.convertNumberToLetter(planilla?.valor + planilla?.reajuste)
        def numerosALetras = NumberToLetterConverter.convertNumberToLetter(planilla?.valor + reajuste)
//        def numerosALetras = NumberToLetterConverter.convertNumberToLetter(numero(planilla.valor + planilla.reajuste, 2).replaceAll(',','').toDouble())

        def strParrafo1 = "De acuerdo al Contrato N° ${contrato?.codigo}, suscrito el ${fechaConFormato(contrato?.fechaSubscripcion, 'dd-MM-yyyy')}, por el valor de " +
                "USD ${numero(contrato?.monto, 2)}  sin incluir IVA, para realizar ${contrato?.objeto}, " +
                "ubicada en el Barrio ${contrato?.oferta?.concurso?.obra?.barrio}, Parroquia ${contrato?.oferta?.concurso?.obra?.parroquia}, " +
                "Cantón ${contrato?.oferta?.concurso?.obra?.parroquia?.canton}, de la Provincia de ${contrato?.oferta?.concurso?.obra?.parroquia?.canton?.provincia?.nombre}"

//        def strParrafo2 = "Sírvase disponer el trámite respectivo para el pago del ${numero(contrato?.porcentajeAnticipo, 0)}% del anticipo, a favor de ${nombrePersona(contrato?.oferta?.proveedor, 'prov')} "
        def strParrafo2 = "Sírvase disponer el trámite respectivo para el pago del ${numero(contrato?.porcentajeAnticipo, 0)}% del " +
                "anticipo, a favor de ${contrato?.oferta?.proveedor.pagarNombre} "

        def strParrafo3 = "Son ${numerosALetras}"

        def textos = []
        textos[0] = [
                strParrafo1
        ]
        textos[1] = [
                strParrafo2,
                params["edit_2_1"]
        ]
        textos[2] = [
                // tabla
        ]
        textos[3] = [
                strParrafo3
        ]
        textos[4] = [
                params["edit_5_1"]
        ]

        texto.parrafo1 = strParrafo1
        texto.parrafo2 = strParrafo2 + " " + params["edit_2_1"]
        texto.parrafo3 = strParrafo3
        texto.parrafo4 = params["edit_5_1"]
        texto.parrafo5 = params.extra?.trim()
        texto.copia=params.copia

        if (texto.save([flush: true])) {
            flash.clase = "alert-success"
            flash.message = "Pedido de pago del anticipo guardado exitosamente."
        } else {
            flash.clase = "alert-error"
            flash.message = "Ha ocurrido un error al guardar el pedido de pago del anticipo."
        }
        redirect(action: "configPedidoPagoAnticipo", id: planilla.id)
    }

    def savePedidoPago() {
        def planilla = Planilla.get(params.id)
        def contrato = planilla.contrato
        def obra = contrato.obra
        def texto = new Pdfs()
        def costo = Planilla.findByPadreCosto(planilla)?.valor?:0
        texto.planilla = planilla
        texto.fecha = new Date()

        def cn = dbConnectionService.getConnection()
        def sql= "select sum(rjplvlor) suma from rjpl where plnl__id = (select max(plnlrjst) from rjpl " +
                "where plnl__id = ${planilla.id} and plnlrjst < plnl__id)"
        println "--sql: $sql"
        def reajusteAnterior = cn.rows(sql.toString())[0].suma
        def reajuste = ReajustePlanilla.findAllByPlanilla(planilla).sum{ it.valorReajustado} - reajusteAnterior


        def multas = 0
        // TODO: actualizar esta línea al igual que en la 122 (configPedidoPago)
//        multas = planilla.multaDisposiciones + planilla.multaIncumplimiento + planilla.multaPlanilla + planilla.multaRetraso
        multas = MultasPlanilla.executeQuery("select sum(monto) from MultasPlanilla where planilla = :p", [p: planilla])[0]?:0
        multas += planilla.multaEspecial?:0

//        def totalLetras = planilla.valor + planilla.reajuste - planilla.descuentos - multas - planilla.noPagoValor
        def totalLetras = planilla.valor + reajuste - planilla.descuentos - multas - planilla.noPagoValor + costo
        def neg = ""
        if (totalLetras < 0) {
            totalLetras = totalLetras * -1
            neg = "MENOS "
        }
        def numerosALetras = NumberToLetterConverter.convertNumberToLetter(totalLetras)

        def strParrafo1 = "De acuerdo al Contrato N° ${contrato?.codigo}, suscrito el ${fechaConFormato(contrato?.fechaSubscripcion, 'dd-MM-yyyy')}, por el valor de " +
                "USD ${numero(contrato?.monto, 2)}  sin incluir IVA, para realizar ${contrato?.objeto}, " +
                "ubicada en el Barrio ${contrato?.oferta?.concurso?.obra?.barrio}, Parroquia ${contrato?.oferta?.concurso?.obra?.parroquia}, " +
                "Cantón ${contrato?.oferta?.concurso?.obra?.parroquia?.canton}, de la Provincia de ${contrato?.oferta?.concurso?.obra?.parroquia?.canton?.provincia?.nombre}"

        def strParrafo2 = "Sírvase disponer el trámite respectivo para el pago de la planilla, a favor de ${nombrePersona(contrato?.oferta?.proveedor, 'prov')} "

        def strParrafo3 = "Son ${neg}${numerosALetras}"

        def textos = []
        textos[0] = [
                strParrafo1
        ]
        textos[1] = [
                strParrafo2,
                params["edit_2_1"]
        ]
        textos[2] = [
                // tabla
        ]
        textos[3] = [
                strParrafo3
        ]
        textos[4] = [
                params["edit_5_1"]
        ]

        texto.parrafo1 = strParrafo1
        texto.parrafo2 = strParrafo2 + " " + params["edit_2_1"]
        texto.parrafo3 = strParrafo3
        texto.parrafo4 = params["edit_5_1"]
        texto.parrafo5 = params.extra?.trim()

        if (texto.save([flush: true])) {
            flash.clase = "alert-success"
            flash.message = "Pedido de pago de la planilla guardado exitosamente."
        } else {
            flash.clase = "alert-error"
            flash.message = "Ha ocurrido un error al guardar el pedido de pago de la planilla."
        }
        redirect(action: "configPedidoPago", id: planilla.id)
    }

    def savePedidoPagoComp() {
        def planilla = Planilla.get(params.id)
        def contrato = planilla.contrato
        def obra = contrato.obra
        def texto = new Pdfs()
        def costo = Planilla.findByPadreCosto(planilla)?.valor?:0
        texto.planilla = planilla
        texto.fecha = new Date()

        def cn = dbConnectionService.getConnection()
        def sql= "select sum(rjplvlor) suma from rjpl where plnl__id = (select max(plnlrjst) from rjpl " +
                "where plnl__id = ${planilla.id} and plnlrjst < plnl__id)"
        println "--sql: $sql"
        def reajusteAnterior = cn.rows(sql.toString())[0].suma
        def reajuste = ReajustePlanilla.findAllByPlanilla(planilla).sum{ it.valorReajustado} - reajusteAnterior


        def multas = 0
        // TODO: actualizar esta línea al igual que en la 122 (configPedidoPago)
//        multas = planilla.multaDisposiciones + planilla.multaIncumplimiento + planilla.multaPlanilla + planilla.multaRetraso
        multas = MultasPlanilla.executeQuery("select sum(monto) from MultasPlanilla where planilla = :p", [p: planilla])[0]?:0
        multas += planilla.multaEspecial?:0

        def rjplAntr = planillasService.reajusteAnterior(planilla)
        def rjplAcml = planillasService.reajusteAcumulado(planilla)
        def rjplActl = rjplAcml - rjplAntr

        def rjplAntrCp = planillasService.reajusteAnterior(planilla.planillaCmpl)
        def rjplAcmlCp = planillasService.reajusteAcumulado(planilla.planillaCmpl)
        rjplAntr += rjplAntrCp
        rjplAcml += rjplAcmlCp
        rjplActl = rjplAcml - rjplAntr


//        def totalLetras = planilla.valor + planilla.reajuste - planilla.descuentos - multas - planilla.noPagoValor
        def totalLetras = planilla.valor + planilla.planillaCmpl.valor + rjplActl - planilla.descuentos - multas - planilla.noPagoValor + costo
        def neg = ""
        if (totalLetras < 0) {
            totalLetras = totalLetras * -1
            neg = "MENOS "
        }
        def numerosALetras = NumberToLetterConverter.convertNumberToLetter(totalLetras)

        def strParrafo1 = "De acuerdo al Contrato N° ${contrato?.codigo}, suscrito el ${fechaConFormato(contrato?.fechaSubscripcion, 'dd-MM-yyyy')}, por el valor de " +
                "USD ${numero(contrato?.monto, 2)}  sin incluir IVA, para realizar ${contrato?.objeto}, " +
                "ubicada en el Barrio ${contrato?.oferta?.concurso?.obra?.barrio}, Parroquia ${contrato?.oferta?.concurso?.obra?.parroquia}, " +
                "Cantón ${contrato?.oferta?.concurso?.obra?.parroquia?.canton}, de la Provincia de ${contrato?.oferta?.concurso?.obra?.parroquia?.canton?.provincia?.nombre}"

        def strParrafo2 = "Sírvase disponer el trámite respectivo para el pago de la planilla, a favor de ${nombrePersona(contrato?.oferta?.proveedor, 'prov')} "

        def strParrafo3 = "Son ${neg}${numerosALetras}"

        def textos = []
        textos[0] = [
                strParrafo1
        ]
        textos[1] = [
                strParrafo2,
                params["edit_2_1"]
        ]
        textos[2] = [
                // tabla
        ]
        textos[3] = [
                strParrafo3
        ]
        textos[4] = [
                params["edit_5_1"]
        ]

        texto.parrafo1 = strParrafo1
        texto.parrafo2 = strParrafo2 + " " + params["edit_2_1"]
        texto.parrafo3 = strParrafo3
        texto.parrafo4 = params["edit_5_1"]
        texto.parrafo5 = params.extra?.trim()

        if (texto.save([flush: true])) {
            flash.clase = "alert-success"
            flash.message = "Pedido de pago de la planilla guardado exitosamente."
        } else {
            flash.clase = "alert-error"
            flash.message = "Ha ocurrido un error al guardar el pedido de pago de la planilla."
        }
        redirect(action: "configPedidoPago", id: planilla.id)
    }

    def errorIndice() {
        def planilla = Planilla.get(params.id)
        return [planilla: planilla, errores: params.errores, alertas: params.alertas]
    }

    def list() {
        def codigoPerfil = session.perfil.codigo
//        println codigoPerfil
        switch (codigoPerfil) {
            case "FINA":
                redirect(action: 'listFinanciero', id: params.id)
                return
                break;
            case "ADCT":
                redirect(action: 'listAdmin', id: params.id)
                return
                break;
            case "FISC":
                redirect(action: 'listFiscalizador', id: params.id)
                return
                break;
        }

        def contrato = Contrato.get(params.id)
        def obra = contrato.oferta.concurso.obra

//        def fp = janus.FormulaPolinomica.findAllByObra(obra)
        def planillaInstanceList = Planilla.findAllByContrato(contrato, [sort: 'id'])
        return [contrato: contrato, obra: contrato.oferta.concurso.obra, planillaInstanceList: planillaInstanceList]
    }

    def listFiscalizador() {
        println "listFiscalizador: $params"
//        params.max = 15 //controlao por javascript $(".paginate").paginate(...
        def codigoPerfil = session.perfil.codigo
//        println codigoPerfil
        switch (codigoPerfil) {
            case "FINA":
                redirect(action: 'listFinanciero', id: params.id)
                return
                break;
            case "ADCT":
                redirect(action: 'listAdmin', id: params.id)
                return
                break;
            case "FISC":
//                redirect(action: 'listFiscalizador', id: params.id)
//                return
                break;
            default:
                redirect(action: 'list', id: params.id)
                return
        }
        def contrato = Contrato.get(params.id)
        def obra = contrato.oferta.concurso.obra

//        def fp = janus.FormulaPolinomica.findAllByObra(obra)
//        println fp
        def firma = Persona.findAllByCargoIlike("Direct%");
//        def planillaInstanceList = Planilla.findAllByContrato(contrato, [sort: 'id'], [max: 25])
        def planillaInstanceList = Planilla.findAllByContrato(contrato, [sort: 'id'])

//        def tipoAvance = TipoPlanilla.findByCodigo('P')
        def liquidacion = Planilla.findByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo('Q'))?.id > 0

//        println "---> $params"
        def listaAdicionales = []

        planillaInstanceList.each{
            def cn = dbConnectionService.getConnection()
            def sql = "select rbrocdgo, rbronmbr, unddcdgo, vocrcntd, cntdacml - cntdantr - vocrcntd diff, vocrpcun, vloracml-vlorantr-vocrsbtt vlor from detalle(${it.contrato.id}, ${it.contrato.obra.id}, ${it.id}, 'P') where (cntdacml - cntdantr) > vocrcntd ;"
            def res = cn.rows(sql.toString())

            if(res){
                listaAdicionales.add(it.id)
            }

        }

        return [contrato: contrato, obra: contrato.oferta.concurso.obra, planillaInstanceList: planillaInstanceList,
                firma: firma, liquidacion: liquidacion, adicionales: listaAdicionales]
    }

    def listAdmin() {
        def codigoPerfil = session.perfil.codigo
//        println codigoPerfil
        switch (codigoPerfil) {
            case "FINA":
                redirect(action: 'listFinanciero', id: params.id)
                return
                break;
            case "ADCT":
//                redirect(action: 'listAdmin', id: params.id)
//                return
                break;
            case "FISC":
                redirect(action: 'listFiscalizador', id: params.id)
                return
                break;
            default:
                redirect(action: 'list', id: params.id)
                return
        }
        def contrato = Contrato.get(params.id)
        def cmpl = Contrato.findByPadre(contrato)
        def obra = contrato.oferta.concurso.obra

//        def fp = janus.FormulaPolinomica.findAllByObra(obra)

        def garantia
        def garantias = Garantia.findAllByContrato(contrato, [sort: 'fechaFinalizacion']);

        if(contrato.contratista.tipo != 'E') {
            garantia = garantias? garantias.last().fechaFinalizacion : null
        } else {
            garantia = new Date()
        }

        def firma = Persona.findAllByCargoIlike("Direct%");
        def planillaInstanceList = Planilla.findAllByContrato(contrato, [sort: 'id'])
        return [contrato: contrato, obra: contrato.oferta.concurso.obra, planillaInstanceList: planillaInstanceList,
                firma: firma, garantia: garantia, cmpl: cmpl?.id?:0]
    }


    def listFinanciero() {
        def codigoPerfil = session.perfil.codigo
//        println codigoPerfil
        switch (codigoPerfil) {
            case "FINA":
//                redirect(action: 'listFinanciero', id: params.id)
//                return
                break;
            case "ADCT":
                redirect(action: 'listAdmin', id: params.id)
                return
                break;
            case "FISC":
                redirect(action: 'listFiscalizador', id: params.id)
                return
                break;
            default:
                redirect(action: 'list', id: params.id)
                return
        }
        def contrato = Contrato.get(params.id)
//        def obra = contrato.oferta.concurso.obra
        def obra = contrato?.obra

//        def fp = janus.FormulaPolinomica.findAllByObra(obra)
//        println fp

        def planillaInstanceList = Planilla.findAllByContrato(contrato, [sort: 'id'])
        return [contrato: contrato, obra: obra, planillaInstanceList: planillaInstanceList]
    }

    def pagar() {
        def planilla = Planilla.get(params.id)
        return [planillaInstance: planilla]
    }

    def ordenPago() {
        def planilla = Planilla.get(params.id)
        return [planillaInstance: planilla]
    }

    def saveOrdenPago() {
//        println "save orden pago!! "+params
        def planilla = Planilla.get(params.id)
        planilla.fechaOrdenPago = new Date().parse("dd-MM-yyyy", params.fechaOrdenPago)
        planilla.memoOrdenPago = params.memoOrdenPago
        if (planilla.save(flush: true)) {
            flash.message = "Orden de pago registrada"
            redirect(action: "list", id: planilla.contrato.id)
        } else {
            flash.message = "Error al registrar la orden de pago"
            redirect(action: "ordenPago", id: params.id)
        }
    }

    def savePago() {
//        println("params" + params)
        def planilla = Planilla.get(params.id)
        planilla.fechaPago = new Date().parse("dd-MM-yyyy", params.fechaPago)

        planilla.memoPago = params.memoPago

        flash.message = ""
        if (!planilla.save(flush: true)) {
            println "ERROR al guardar el pago de la planilla " + planilla.errors
            flash.message = "Ha ocurrido un error al efectuar el pago:"
            flash.message += g.renderErrors(bean: planilla)
        } else {
            if (planilla.tipoPlanilla.codigo == "A") {
                def obra = Obra.get(planilla.contrato.oferta.concurso.obraId)
                obra.fechaInicio = new Date().parse("dd-MM-yyyy", params.fechaPago)
                if (!obra.save(flush: true)) {
                    println "ERROR al guardar el pago de la planilla (fecha inicio obra) " + obra.errors
                    flash.message = "Ha ocurrido un error al efectuar el pago:"
                    flash.message += g.renderErrors(bean: obra)
                }
            }
        }
        if (flash.message == "") {
            flash.clase = "alert-success"
            redirect(controller: "cronogramaEjecucion", action: "index", id: planilla.contratoId)
        } else {
            flash.clase = "alert-error"
            redirect(action: "pagar", id: planilla.id)
        }
    }

    def devolver_ajax() {
//        println params
        def fechaMin, fechaMax, fecha
        def planilla = Planilla.get(params.id)
        def tipo = params.tipo
        def lblMemo, lblFecha, extra, nombres = ""
        def tipoTramite
        def tramitePadre

        def tramite = new Tramite()

        switch (tipo) {
            case "3":
                lblMemo = "Memo de devolución"
                lblFecha = "Fecha de memo de devolución"
                fecha = new Date()
                extra = "Fecha de memo de salida: " + planilla.fechaMemoSalidaPlanilla.format("dd-MM-yyyy")
                tipoTramite = TipoTramite.findByCodigo("ENRJ")
                break;
            case "4":
                lblMemo = "Memo de devolución"
                lblFecha = "Fecha de memo de devolución"
                fecha = new Date()
                extra = "Fecha de memo de pedido de pago: " + planilla.fechaMemoPedidoPagoPlanilla.format("dd-MM-yyyy")
                tipoTramite = TipoTramite.findByCodigo("PDPG")
                break;
        }
//        println "??? " + Tramite.findAllByPlanillaAndTipoTramite(planilla, tipoTramite, [sort: 'fecha', order: "desc"]).descripcion
//        println "??? " + Tramite.findAllByPlanillaAndTipoTramite(planilla, tipoTramite, [sort: 'fecha', order: "desc"]).fecha
        tramitePadre = Tramite.findAllByPlanillaAndTipoTramite(planilla, tipoTramite, [sort: 'fechaEnvio', order: "desc"])[0]

        tramite.planilla = planilla
        tramite.tramitePadre = tramitePadre
        tramite.tipoTramite = tipoTramite
        tramite.estado = EstadoTramite.findByCodigo("C")
//        println ">>" + tramitePadre.descripcion
//        tramite.descripcion = "Devolución de " + tramitePadre.descripcion
        tramite.descripcion = tramitePadre.descripcion
//        println "<<" + tramite.descripcion

        PersonasTramite.findAllByTramite(tramitePadre).each { p ->
            def per = new PersonasTramite()
            per.persona = p.persona
            per.departamento = p.persona.departamento
            per.tramite = tramite
            if ((p.rolTramite.codigo).trim() == "DE") {
                per.rolTramite = RolTramite.findByCodigo("PARA")
            } else if ((p.rolTramite.codigo).trim() == "PARA") {
                per.rolTramite = RolTramite.findByCodigo("DE")
            } else {
                per.rolTramite = p.rolTramite
            }

            nombres += '<div class="row">'
            nombres += '<div class="span2 formato">'
            nombres += per.rolTramite.descripcion
            def hidden = g.hiddenField(name: "persona_" + per.rolTramite.id, value: per.personaId)
            nombres += '</div>'
            nombres += '<div class="span3">' + hidden + ((per.persona.titulo ?: "") + " " + per.persona.nombre + " " + per.persona.apellido) + '</div>'
            nombres += '<div class="span2 dpto">(Dpto. de ' + per.departamento.descripcion + ')</div>'
            nombres += '</div>'
        }
        nombres += '<div class="row">'
        nombres += '<div class="span2 formato">Asunto</div>'
        nombres += '<div class="span4">' + g.textArea(name: 'asunto', style: "width:410px;", value: tramite.descripcion) + '</div>'
        nombres += '</div>'

        [planilla: planilla, tipo: tipo, lblMemo: lblMemo, lblFecha: lblFecha, fechaMin: fechaMin, fechaMax: fechaMax, extra: extra, fecha: fecha, nombres: nombres]
    }

    def saveDevolucionPlanilla() {
        def planilla = Planilla.get(params.id)
        def tipo = params.tipo
        def memo = params.memo.toString().toUpperCase()
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)

        // borra pdfs de pedido de pago de la planilla
        def texto = Pdfs.findAllByPlanilla(planilla)
        if (texto.size() == 0) {
            println "No hay pdf que borrar"
        } else if (texto.size() > 0) {
            println "Hay ${texto.size()} pdf que borrar"
            texto.id.each {
                def pdfDel = Pdfs.get(it)
                pdfDel.delete(flush: true)
            }
        }

        def tipoTramite, tramitePadre

        def str = "", str2 = ""

        switch (tipo) {
            case "3":
                planilla.fechaMemoSalidaPlanilla = null
                str = "Devolución exitosa"
                str2 = "No se pudo efectuar la devolución"
                tipoTramite = TipoTramite.findByCodigo("ENRJ")
                break;
            case "4":
                planilla.fechaMemoPedidoPagoPlanilla = null
                str = "Devolución exitosa"
                str2 = "No se pudo efectuar la devolución"
                tipoTramite = TipoTramite.findByCodigo("PDPG")
                break;
        }
        tramitePadre = Tramite.findAllByPlanillaAndTipoTramite(planilla, tipoTramite, [sort: 'fechaEnvio', order: "desc"])[0]

        if (planilla.save(flush: true)) {
            flash.clase = "alert-success"
            flash.message = str

            def estadoTramite = EstadoTramite.findByCodigo("C")
            def tramite = new Tramite([
                    planilla    : planilla,
                    tipoTramite : tipoTramite,
                    tramitePadre: tramitePadre,
                    estado      : estadoTramite,
                    descripcion : params.asunto,
                    memo        : memo,
                    fecha       : fecha,
                    fechaEnvio  : new Date()
            ])
            if (!tramite.save(flush: true)) {
                println "Error al guardar el tramite: "
                println tramite.errors
                str2 += "<li>Ha ocurrido un error al guardar el trámite</li>"

                flash.clase = "alert-error"
                str = "<h4>" + str2 + "</h4>"
                str += g.renderErrors(bean: tramite)
                flash.message = str
            } else {

                if (tramitePadre) {
                    tramitePadre.fechaRespuesta = fecha
                    if (!tramitePadre.save(flush: true)) {
                        println "error al guardar la fecha de respuesta del tramite padre...."
                        println tramitePadre.errors
                    }
                }

                def personas = params.findAll { it.key.contains("persona") }
                personas.each { key, val ->
                    def parts = key.split("_")
                    def rolId = parts[1]
                    def rol = RolTramite.get(rolId.toLong())
                    def pers = Persona.get(val.toLong())
                    def personaTramite = new PersonasTramite([
                            tramite     : tramite,
                            rolTramite  : rol,
                            persona     : pers,
                            departamento: pers.departamento
                    ])
                    if (!personaTramite.save(flush: true)) {
                        println "Error al guardar persona tramite: "
                        println personaTramite.errors
                        str += "<li>Ha ocurrido un error al guardar el trámite</li>"

                        flash.clase = "alert-error"
                        str = "<h4>" + str2 + "</h4>"
                        str += g.renderErrors(bean: tramite)
                    }
                }
            }
            redirect(action: "list", id: planilla.contratoId)
        } else {
            println "Error al grabar la fecha y el memo en la planilla: " + planilla.errors
            flash.clase = "alert-error"
            str = "<h4>" + str2 + "</h4>"

            str += g.renderErrors(bean: planilla)

            flash.message = str
            redirect(action: "list", id: planilla.contratoId)
        }
    }

    def pago_ajax() {
        println "pago_ajax: params: " + params

        def fechaMin, fechaMax, fecha
        def planilla = Planilla.get(params.id)
        def contrato = planilla.contrato
        def obra = contrato.oferta.concurso.obra
        def tipo = params.tipo
        def lblMemo, lblFecha, extra, nombres = ""
        def tiposTramite, tipoTramite
        def tramite

//        def obraDpto = obra.departamento
        def adminContrato = contrato.administrador
        def fiscContrato = contrato.fiscalizador
//        fiscContrato = null
        if (!adminContrato) {
            render "No se encontró el administrador del contrato. Por favor asegúrese de que existe un administrador " +
                    "activo para continuar con el trámite."
            return
        }
        if (!fiscContrato && tipo == "2") {
            render "No se encontró el fiscalizador del contrato. Por favor asegúrese de que existe un fiscalizador " +
                    "activo para continuar con el trámite."
            return
        }
        def obraDpto = adminContrato.departamento

        def especial = "DE"
        def fiscalizador = null

        def errores = ""

//        println planilla.fechaOficioEntradaPlanilla
//        println planilla.fechaMemoSalidaPlanilla
//        println planilla.fechaMemoPedidoPagoPlanilla
//        println planilla.fechaMemoPagoPlanilla

        def dptoFiscalizacion = Departamento.findAllByCodigo("FISC")
//        def dptoDirFinanciera = Departamento.findAllByCodigo("FINA")
        def dptoDirFinanciera
        if(params.tipo in ['4', '5']) {
            dptoDirFinanciera = Departamento.findAllByCodigo("FINA")
        } else {
            dptoDirFinanciera = Departamento.findAllByCodigo("AP")
        }
        println "params.tipo: ${params.tipo}, dpto: ${dptoDirFinanciera[0]}, size: ${dptoDirFinanciera.size()}"

        if (dptoFiscalizacion.size() == 1) {
            dptoFiscalizacion = dptoFiscalizacion[0]
        } else if (dptoFiscalizacion.size() == 0) {
            render "No se encontró el departamento de Fiscalización con código FISC. Por favor asegúrese de que exista " +
                    "para continuar con el trámite."
            return
        } else {
            render "Se encontraron ${dptoFiscalizacion.size()} departamentos de Fiscalización con código FISC. Por " +
                    "favor asegúrese de que exista para sólo uno continuar con el trámite."
            return
        }
        if (dptoDirFinanciera.size() == 1) {
            dptoDirFinanciera = dptoDirFinanciera[0]
            println "ok: ${dptoDirFinanciera} ----1"
        } else if (dptoDirFinanciera.size() == 0) {
            render "No se encontró el departamento de Dirección Financiera con código FINA. Por favor asegúrese de " +
                    "que exista para continuar con el trámite."
            return
        } else {
            render "Se encontraron ${dptoDirFinanciera.size()} departamentos de Dirección Financiera con código AP. " +
                    "Por favor asegúrese de que exista para sólo uno continuar con el trámite."
            return
        }

        switch (tipo) {
            case "2":
                lblMemo = "Memo de salida"
                lblFecha = "Fecha de memo de salida"
                fechaMin = planilla.fechaOficioEntradaPlanilla
                fecha = planilla.fechaOficioEntradaPlanilla
                extra = "Fecha de oficio de entrada: " + fechaMin.format("dd-MM-yyyy")
//                tipoTramite = TipoTramite.findByCodigo("ENRJ")
                tiposTramite = TipoTramite.findAllByCodigo("ENRJ")
                tiposTramite.each { tt ->
//                    def dptoDe = DepartamentoTramite.findByTipoTramiteAndRolTramite(tt, RolTramite.findByCodigo("DE"))
                    def dptoPara = DepartamentoTramite.findByTipoTramiteAndRolTramite(tt, RolTramite.findByCodigo("PARA"))
                    if (dptoPara?.departamento == obraDpto) {
//                        println "SIP"
                        tipoTramite = tt
                    }
                }
                especial = "PARA"
                fiscalizador = "DE"
                if (!tipoTramite) {
                    println "NOP: crear un tipo de tramite con codigo ENRJ, para: " + obraDpto
                    tipoTramite = new TipoTramite([
                            padre            : null,
                            codigo           : "ENRJ",
                            tiempo           : 5,
                            descripcion      : "Enviar reajuste (fiscalización a " + obraDpto.descripcion.toLowerCase() + ")",
                            tipo             : "P",
                            requiereRespuesta: "S"
                    ])
                    if (tipoTramite.save(flush: true)) {
                        println "Creado tipo de tramite OK, creando dptos"
                        def dDe = new DepartamentoTramite([
                                tipoTramite : tipoTramite,
                                rolTramite  : RolTramite.findByCodigo("DE"),
                                departamento: dptoFiscalizacion
//                                departamento: Departamento.get(1) //Fiscalizacion
                        ])
                        if (!dDe.save(flush: true)) {
                            println "error al guardar DE: " + dDe.errors
                        }
                        def dPara = new DepartamentoTramite([
                                tipoTramite : tipoTramite,
                                rolTramite  : RolTramite.findByCodigo("PARA"),
                                departamento: obraDpto
                        ])
                        if (!dPara.save(flush: true)) {
                            println "error al guardar PARA: " + dPara.errors
                        }
                    } else {
                        println "error al guardar tipo tramite: " + tipoTramite.errors
                    }
                }
                def dDe = DepartamentoTramite.withCriteria {
                    eq("tipoTramite", tipoTramite)
                    eq("rolTramite", RolTramite.findByCodigo("DE"))
                    eq("departamento", dptoFiscalizacion)
                }
                def dPara = DepartamentoTramite.withCriteria {
                    eq("tipoTramite", tipoTramite)
                    eq("rolTramite", RolTramite.findByCodigo("PARA"))
                    eq("departamento", obraDpto)
                }
                if (!dDe) {
                    render "No se encontró el departamento que envía el trámite. Por favor asegúrese de que el tipo de trámite " +
                            tipoTramite.descripcion + " tenga como departamento" + " que envía a ${dptoFiscalizacion}"
                    return
                }
                if (!dPara) {
                    render "No se encontró el departamento que recibe el trámite. Por favor asegúrese de que el tipo de trámite " +
                            tipoTramite.descripcion + " tenga como departamento" + " que recibe a ${obraDpto}"
                    return
                }
                break;
            case "3":
                lblMemo = "Memo de pedido de pago"
                lblFecha = "Fecha de memo de pedido de pago"
                fechaMin = planilla.fechaMemoSalidaPlanilla
                fecha = planilla.fechaMemoSalidaPlanilla

                if (!fechaMin) {
                    fechaMin = planilla.fechaOficioEntradaPlanilla
                    fecha = planilla.fechaOficioEntradaPlanilla
                    planilla.fechaMemoSalidaPlanilla = planilla.fechaOficioEntradaPlanilla
                    if (!planilla.save(flush: true)) {
                        println "error aqui: planilla controller l.505: " + planilla.errors
                    }
                }

                extra = "Fecha de memo de salida: " + fechaMin.format("dd-MM-yyyy")
//                tipoTramite = TipoTramite.findByCodigo("PDPG")
                tiposTramite = TipoTramite.findAllByCodigo("PDPG")
                tiposTramite.each { tt ->
                    def dptoDe = DepartamentoTramite.findByTipoTramiteAndRolTramite(tt, RolTramite.findByCodigo("DE"))
//                    def dptoPara = DepartamentoTramite.findByTipoTramiteAndRolTramite(tt, RolTramite.findByCodigo("PARA"))
                    if (dptoDe?.departamento == obraDpto) {
//                        println "SIP"
                        tipoTramite = tt
                    }
                }
                especial = "DE"
                if (!tipoTramite) {
                    println "NOP: crear un tipo de tramite con codigo PDPG, de: " + obraDpto
                    //////////////////////////////////
                    def tiposTramitePadre = TipoTramite.findAllByCodigo("ENRJ")
                    def tipoTramitePadre
                    tiposTramitePadre.each { tt ->
//                        def dptoDe = DepartamentoTramite.findByTipoTramiteAndRolTramite(tt, RolTramite.findByCodigo("DE"))
                        def dptoPara = DepartamentoTramite.findByTipoTramiteAndRolTramite(tt, RolTramite.findByCodigo("PARA"))
                        if (dptoPara.departamento == obraDpto) {
//                        println "SIP"
                            tipoTramitePadre = tt
                        }
                    }
                    //////////////////////////////////
                    tipoTramite = new TipoTramite([
                            padre            : tipoTramitePadre,
                            codigo           : "PDPG",
                            tiempo           : 2,
                            descripcion      : "Pedir pago planilla (" + (obraDpto.descripcion.size() <= 22 ? obraDpto.descripcion.toLowerCase() : obraDpto.descripcion.toLowerCase()[0..22]) + " a dir. financiera)",
                            tipo             : "P",
                            requiereRespuesta: "S"
                    ])
                    if (tipoTramite.save(flush: true)) {
                        println "Creado tipo de tramite OK, creando dptos"
                        def dDe = new DepartamentoTramite([
                                tipoTramite : tipoTramite,
                                rolTramite  : RolTramite.findByCodigo("DE"),
                                departamento: obraDpto
                        ])
                        if (!dDe.save(flush: true)) {
                            println "error al guardar DE: " + dDe.errors
                        }
                        def dPara = new DepartamentoTramite([
                                tipoTramite : tipoTramite,
                                rolTramite  : RolTramite.findByCodigo("PARA"),
                                departamento: dptoDirFinanciera //dir. financiera
//                                departamento: Departamento.get(11) //dir. financiera
                        ])
                        if (!dPara.save(flush: true)) {
                            println "error al guardar PARA: " + dPara.errors
                        }
                    } else {
                        println "error al guardar tipo tramite: " + tipoTramite.errors
                    }
                }
                def dDe = DepartamentoTramite.withCriteria {
                    eq("tipoTramite", tipoTramite)
                    eq("rolTramite", RolTramite.findByCodigo("DE"))
                    eq("departamento", obraDpto)
                }
                def dPara = DepartamentoTramite.withCriteria {
                    eq("tipoTramite", tipoTramite)
                    eq("rolTramite", RolTramite.findByCodigo("PARA"))
                    eq("departamento", dptoDirFinanciera)
                }
                if (!dDe) {
                    render "No se encontró el departamento que envía el trámite. Por favor asegúrese de que el tipo de trámite " + tipoTramite.descripcion + " tenga como departamento" +
                            "que envía a ${obraDpto}"
                    return
                }
                if (!dPara) {
                    render "No se encontró el departamento que recibe el trámite. Por favor asegúrese de que el tipo de trámite " + tipoTramite.descripcion + " tenga como departamento" +
                            "que recibe a ${dptoDirFinanciera}"
                    return
                }
                break;
            case "4":
                lblMemo = "Memo de pago"
                lblFecha = "Fecha acreditación del pago"
                fechaMin = planilla.fechaMemoPedidoPagoPlanilla
                if(planilla.fechaPago) {
                    fecha = planilla.fechaPago
                } else {
                    fecha = planilla.fechaMemoPedidoPagoPlanilla
                }
                extra = "Fecha de memo de pedido de pago: " + fechaMin.format("dd-MM-yyyy")
//                tipoTramite = TipoTramite.findByCodigo("INPG")
                tiposTramite = TipoTramite.findAllByCodigo("INPG")
                tiposTramite.each { tt ->
//                    def dptoDe = DepartamentoTramite.findByTipoTramiteAndRolTramite(tt, RolTramite.findByCodigo("DE"))
                    def dptoPara = DepartamentoTramite.findByTipoTramiteAndRolTramite(tt, RolTramite.findByCodigo("PARA"))
                    if (dptoPara?.departamento == obraDpto) {
//                        println "SIP"
                        tipoTramite = tt
                    }
                }
                especial = "PARA"
                if (!tipoTramite) {
                    println "NOP: crear un tipo de tramite con codigo INPG, para: " + obraDpto
                    //////////////////////////////////
                    def tiposTramitePadre = TipoTramite.findAllByCodigo("PDPG")
                    def tipoTramitePadre
                    tiposTramitePadre.each { tt ->
                        def dptoDe = DepartamentoTramite.findByTipoTramiteAndRolTramite(tt, RolTramite.findByCodigo("DE"))
                        println "dptode "+dptoDe +"   "+tt
//                    def dptoPara = DepartamentoTramite.findByTipoTramiteAndRolTramite(tt, RolTramite.findByCodigo("PARA"))
                        if (dptoDe?.departamento == obraDpto) {
//                        println "SIP"
                            tipoTramitePadre = tt
                        }
                    }
                    println " asdasd "+tipoTramitePadre
                    //////////////////////////////////
                    tipoTramite = new TipoTramite([
                            padre            : tipoTramitePadre,
                            codigo           : "INPG",
                            tiempo           : 15,
                            descripcion      : "Informar pago planilla (" + (obraDpto.descripcion.size() <= 19 ? obraDpto.descripcion.toLowerCase() : obraDpto.descripcion.toLowerCase()[0..19]) + " a dir. financiera)",
                            tipo             : "P",
                            requiereRespuesta: "N"
                    ])
                    if (tipoTramite.save(flush: true)) {
                        println "Creado tipo de tramite OK, creando dptos"
                        def dDe = new DepartamentoTramite([
                                tipoTramite : tipoTramite,
                                rolTramite  : RolTramite.findByCodigo("DE"),
                                departamento: dptoDirFinanciera //dir. financiera
//                                departamento: Departamento.get(11) //dir. financiera
                        ])
                        if (!dDe.save(flush: true)) {
                            println "error al guardar DE: " + dDe.errors
                        }
                        def dPara = new DepartamentoTramite([
                                tipoTramite : tipoTramite,
                                rolTramite  : RolTramite.findByCodigo("PARA"),
                                departamento: obraDpto
                        ])
                        if (!dPara.save(flush: true)) {
                            println "error al guardar PARA: " + dPara.errors
                        }
                    } else {
                        println "error al guardar tipo tramite: " + tipoTramite.errors
                    }
                }
                def dDe = DepartamentoTramite.withCriteria {
                    eq("tipoTramite", tipoTramite)
                    eq("rolTramite", RolTramite.findByCodigo("DE"))
                    eq("departamento", dptoDirFinanciera)
                }
                def dPara = DepartamentoTramite.withCriteria {
                    eq("tipoTramite", tipoTramite)
                    eq("rolTramite", RolTramite.findByCodigo("PARA"))
                    eq("departamento", obraDpto)
                }
                if (!dDe) {
                    render "No se encontró el departamento que envía el trámite. Por favor asegúrese de que el tipo de trámite " + tipoTramite.descripcion + " tenga como departamento" +
                            "que envía a ${dptoDirFinanciera}"
                    return
                }
                if (!dPara) {
                    render "No se encontró el departamento que recibe el trámite. Por favor asegúrese de que el tipo de trámite " + tipoTramite.descripcion + " tenga como departamento" +
                            "que recibe a ${obraDpto}"
                    return
                }
                break;
            case '5':
                redirect(action: 'inicioObra_ajax', params: params)
                return
//                lblFecha = "Fecha de inicio de obra"
//                fechaMin = planilla.fechaMemoPagoPlanilla
//                fecha = planilla.fechaMemoPagoPlanilla
//                extra = "Fecha de memo de pago: " + fechaMin.format("dd-MM-yyyy")
//                tipoTramite = TipoTramite.findByCodigo("INOB")
                break;
        }

        tramite = Tramite.findByPlanillaAndTipoTramite(planilla, TipoTramite.findByCodigo('INPG'))
        def envia = PersonasTramite.findByTramiteAndRolTramite(tramite, RolTramite.findByCodigo('DE'))
        println "pago_ajax: persona que envía: ${envia?.persona?.id}, trmaite: ${tramite?.id}"


        def roles = DepartamentoTramite.findAllByTipoTramite(tipoTramite)
//        println "roles: $roles"
        roles.each { rol ->    // procesa para quien envia y quien recibe
            def personas = Persona.findAllByDepartamento(rol.departamento)

            if (rol.rolTramite.codigo.trim() == especial.trim()) {
                personas = [adminContrato]
            }
            if (rol.rolTramite.codigo.trim() == fiscalizador?.trim()) {
                personas = [fiscContrato]
            }


            def sel = g.select(from: personas, class: "span3", optionKey: "id", optionValue: {
                it.nombre + " " + it.apellido
            }, name: "persona_" + rol.rolTramite.id, value: envia?.persona?.id)

            nombres += '<div class="row">'
            nombres += '<div class="span2 formato">'
            nombres += rol.rolTramite.descripcion
            nombres += '</div>'
            nombres += '<div class="span3">' + sel + '</div>'
            nombres += '<div class="span2 dpto">(Dpto. de ' + rol.departamento.descripcion + ')</div>'
            nombres += '</div>'
        }

        nombres += '<div class="row">'
        nombres += '<div class="span2 formato">Asunto</div>'
        nombres += '<div class="span4">' + g.textArea(name: 'asunto', value: tramite?.descripcion, style: "width:410px;") + '</div>'
        nombres += '</div>'

//        println tipo + "  " + fechaMin
        def y = fechaMin.format("yyyy").toInteger()
        def m = fechaMin.format("MM").toInteger() - 1
        def d = fechaMin.format("dd").toInteger()
        //js: new Date(year, month, day, hours, minutes, seconds, milliseconds)
        fechaMin = "new Date(${y},${m},${d})"
        fechaMax = "new Date(${y + 2},${m},${d})"

        [planilla: planilla, tipo: tipo, lblMemo: lblMemo, lblFecha: lblFecha, fechaMin: fechaMin, fechaMax: fechaMax,
         extra: extra, fecha: fecha, nombres: nombres]
    }

    def inicioObra_ajax() {
        println "inicioObra_ajax: " + params

        def fechaMin, fechaMax, fecha
        def planilla = Planilla.get(params.id)
        //def obra = planilla.contrato.oferta.concurso.obra
        def tipo = params.tipo
        def lblMemo, lblFecha, extra, nombres = ""

        lblFecha = "Fecha de inicio de obra"
        lblMemo = "Oficio de inicio de obra"
        fechaMin = planilla.fechaMemoPagoPlanilla
        fecha = planilla.fechaMemoPagoPlanilla
        extra = "Fecha de oficio de pago: " + fechaMin.format("dd-MM-yyyy")

        def y = fechaMin.format("yyyy").toInteger()
        def m = fechaMin.format("MM").toInteger() - 1
        def d = fechaMin.format("dd").toInteger()

        fechaMin = "new Date(${y},${m},${d})"
        fechaMax = "new Date(${y + 2},${m},${d})"

        def firma = Persona.findAllByCargoIlike("Direct%");
        println "fache min: $fechaMin"

        [planilla: planilla, tipo: tipo, lblMemo: lblMemo, lblFecha: lblFecha, fechaMin: fechaMin, fechaMax: fechaMax, extra: extra, fecha: fecha, firma: firma]
    }

    def iniObraNoReajuste() {
        println "iniObraNoReajuste: " + params

        def fechaMin, fechaMax, fecha
        def contrato = Contrato.get(params.id)

        def tipo = params.tipo
        def lblMemo, lblFecha, extra, nombres = ""

        lblFecha = "Fecha de inicio de obra"
        lblMemo = "Oficio de inicio de obra"
        fechaMin = contrato.fechaSubscripcion
        fecha = contrato.fechaFirma

        def y = fechaMin.format("yyyy").toInteger()
        def m = fechaMin.format("MM").toInteger() - 1
        def d = fechaMin.format("dd").toInteger()

        fechaMin = "new Date(${y},${m},${d})"
        fechaMax = "new Date(${y + 2},${m},${d})"

        def firma = Persona.findAllByCargoIlike("Direct%");
        println "fache min: $fechaMin"

        [cntr: contrato.id, tipo: tipo, lblMemo: lblMemo, lblFecha: lblFecha, fechaMin: fechaMin, fechaMax: fechaMax, extra: extra, fecha: fecha, firma: firma]
    }

    def savePagoPlanilla() {
//        println "savePagoPlanilla params: $params"
        def planilla = Planilla.get(params.id)
        def tipo = params.tipo
        def memo = params.memo.toString().toUpperCase()
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)

        def tipoTramite, tramitePadre
        def str = "", str2 = ""
        def hoy = new Date()
        def lgpl = "Corrige pago ${hoy.format('dd-MM-yyyy HH:mm')} anterior: ${planilla.memoPagoPlanilla} - " +
                "${planilla.fechaMemoPagoPlanilla?.format('dd-MM-yyyy')} - ${planilla.fechaPago?.format('dd-MM-yyyy')}"

        switch (tipo) {
            case "2":
                planilla.memoSalidaPlanilla = memo
                planilla.fechaMemoSalidaPlanilla = fecha
                str = "Reajuste enviado exitosamente"
                str2 = "No se pudo enviar el reajuste"
                tipoTramite = TipoTramite.findByCodigo("ENRJ")
                tramitePadre = null
                break;
            case "3":  //pedir pago
                planilla.memoPedidoPagoPlanilla = memo
                planilla.fechaMemoPedidoPagoPlanilla = fecha
                if (planilla.tipoPlanilla.codigo == 'A') {
                    planilla.fechaMemoSalidaPlanilla = fecha
                }
                str = "Pago pedido existosamente"
                str2 = "No se pudo pedir el pago"
                tipoTramite = TipoTramite.findByCodigo("PDPG")
//                tramitePadre = Tramite.findByPlanillaAndTipoTramite(planilla, TipoTramite.findByCodigo("ENRJ"))
                tramitePadre = Tramite.findAllByPlanillaAndTipoTramite(planilla, TipoTramite.findByCodigo("ENRJ"), [sort: 'fechaEnvio', order: "desc"])[0]
                break;
            case "4":  //informar pago
                planilla.memoPagoPlanilla = memo
                planilla.fechaMemoPagoPlanilla = fecha
                planilla.fechaPago = fecha
                str = "Pago informado exitosamente"
                str2 = "No se pudo informar el pago"
                tipoTramite = TipoTramite.findByCodigo("INPG")
                tramitePadre = Tramite.findAllByPlanillaAndTipoTramite(planilla, TipoTramite.findByCodigo("PDPG"), [sort: 'fechaEnvio', order: "desc"])[0]
                break;

            case "5":
                def obra = Obra.get(planilla.contrato.obra.id)
                obra.fechaInicio = fecha
                if (!obra.save(flush: true)) {
                    println "Error al guardar la fecha de la obra desde el boton azul: " + obra.errors
                }
                str = "Obra iniciada exitosamente"
                str2 = "No se pudo iniciar la obra"
//                tipoTramite = TipoTramite.findByCodigo("INOB")
//                tramitePadre = Tramite.findByPlanillaAndTipoTramite(planilla, TipoTramite.findByCodigo("INPG"))
//                tramitePadre = Tramite.findAllByPlanillaAndTipoTramite(planilla, TipoTramite.findByCodigo("INPG"), [sort: 'fechaEnvio', order: "desc"])[0]
                break;
        }
        if (planilla.save(flush: true)) {
            flash.clase = "alert-success"
            flash.message = str

            def trmt = Tramite.findByPlanillaAndTipoTramite(planilla, TipoTramite.findByCodigo('INPG'))

            def estadoTramite = EstadoTramite.findByCodigo("C")
            if(trmt) {
                // TODO: actualiza el trami te existente y no cre otro
                println "trmite id: ${trmt.id}"
                trmt.descripcion = params.asunto
                trmt.fecha = fecha   //params.fecha corregido
                trmt.fechaEnvio = new Date()   //ahora
                trmt.memo = memo
                trmt.save(flush: true)
                if(planilla.logPagos) {
                    planilla.logPagos = lgpl + "|| " + planilla.logPagos
                } else {
                    planilla.logPagos = lgpl
                }
                planilla.save(flush: true)

                def envia = PersonasTramite.findByTramiteAndRolTramite(trmt, RolTramite.findByCodigo('DE'))
                envia.persona  = Persona.get(params.persona_2)
                envia.save(flush: true)

            } else {
                def tramite = new Tramite([
                        planilla    : planilla,
                        tipoTramite : tipoTramite,
                        tramitePadre: tramitePadre,
                        estado      : estadoTramite,
                        descripcion : params.asunto,
                        memo        : memo,
                        fecha       : fecha,
                        fechaEnvio  : new Date()
                ])
                if (!tramite.save(flush: true)) {
                    println "Error al guardar el tramite: "
                    println tramite.errors
                    str2 += "<li>Ha ocurrido un error al guardar el trámite</li>"

                    flash.clase = "alert-error"
                    str = "<h4>" + str2 + "</h4>"
                    str += g.renderErrors(bean: tramite)
                    flash.message = str
                } else {

                    if (tramitePadre) {
                        tramitePadre.fechaRespuesta = fecha
                        if (!tramitePadre.save(flush: true)) {
                            println "error al guardar la fecha de respuesta del tramite padre...."
                            println tramitePadre.errors
                        }
                    }

                    def personas = params.findAll { it.key.contains("persona") }
                    personas.each { key, val ->
                        def parts = key.split("_")
                        def rolId = parts[1]
                        def rol = RolTramite.get(rolId.toLong())
                        def pers = Persona.get(val.toLong())
                        def personaTramite = new PersonasTramite([
                                tramite     : tramite,
                                rolTramite  : rol,
                                persona     : pers,
                                departamento: pers.departamento
                        ])
                        if (!personaTramite.save(flush: true)) {
                            println "Error al guardar persona tramite: "
                            println personaTramite.errors
                            str += "<li>Ha ocurrido un error al guardar el trámite</li>"

                            flash.clase = "alert-error"
                            str = "<h4>" + str2 + "</h4>"
                            str += g.renderErrors(bean: tramite)
                        }
                    }
                }
            }
            if (tipo == "5") {
                redirect(controller: "cronogramaEjecucion", action: "index", id: planilla.contratoId)
            } else {
                redirect(action: "list", id: planilla.contratoId)
            }
        } else {
            println "Error al grabar la fecha y el memo en la planilla: " + planilla.errors
            flash.clase = "alert-error"
            str = "<h4>" + str2 + "</h4>"

            str += g.renderErrors(bean: planilla)

            flash.message = str
            redirect(action: "list", id: planilla.contratoId)
        }
    }

    /** Guarda fecha de inicio de obra **/
    def iniciarObra() {
        println "iniciarObra: $params"

        def memo = params.memo.toString().toUpperCase()
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)

        def planilla
        def contrato
        if(params.id){
            planilla = Planilla.get(params.id)
            contrato = planilla.contrato
        } else {
            contrato = Contrato.get(params.cntr)
        }

        println "contrato: $contrato"
        contrato.numeralPlazo = params.numeralPlazo
        contrato.numeralAnticipo = params.numeralAnticipo
        contrato.clausula = params.clausula
        if (!contrato.save(flush: true)) {
            flash.message = "No se pudo iniciar la obra"
            println "Error al guardar datos del contrato desde el boton azul: " + contrato.errors
            redirect(action: "list", id: contrato.id)
            return
        }

        def obra = Obra.get(contrato.obra.id)
        println "obra a iniciar: ${obra.id}"
        obra.fechaInicio = fecha
        obra.memoInicioObra = memo
        obra.fechaImpresionInicioObra = new Date()
        if (!obra.save(flush: true)) {
            flash.message = "No se pudo iniciar la obra"
            println "Error al guardar la fecha de la obra desde el boton azul: " + obra.errors
            redirect(action: "list", id: contrato.id)
        } else {
            flash.message = "Obra iniciada exitosamente"
//            redirect(controller: "cronogramaEjecucion", action: "creaCronogramaEjec", id: planilla.contratoId)
            if(params.cntr) {
                render "ok"
            } else {
                redirect(controller: "cronogramaEjecucion", action: "creaCrngEjecNuevo", id: contrato.id)
            }
        }
    }


    def form() {
        println "form... planillas: $params"
        def contrato = Contrato.get(params.contrato)
        def obra = contrato.obra
        def liquidado = false

        /* aqui se valida que haya cronograma de contrato y formula polinomica de contrato */
        def existeCrng = CronogramaContratado.findAllByContrato(contrato).size()
        def pcs = FormulaPolinomicaContractual.findAllByContrato(contrato).size()

        if ((existeCrng == 0 || pcs < 8 ) && (contrato.aplicaReajuste == 1)) {
            flash.message = "<h3>No es posible crear planillas</h3><ul>"
            if (existeCrng == 0) {
                flash.message += "<li>No se ha generado el cronograma de contrato.</li>"
            }
            if (pcs < 8) {
                flash.message += "<li>No se ha generado la fórmula polinómica contractual.</li>"
            }
            flash.message += "</ul>"
            redirect(action: "errores")
            return
        }

        def planillaInstance = new Planilla(params)
        planillaInstance.contrato = contrato
        if (params.id) {
            planillaInstance = Planilla.get(params.id)
        }

        /*** si no hay planilla de este contrato se presenta solo TPPL: Anticipo **/
        def tiposPlanilla = []
        tiposPlanilla = TipoPlanilla.findAllByCodigoInList(["A", "P","C", "Q", "L", "R"], [sort: 'codigo', order: "asc"])

        /***/
        def anticipo = TipoPlanilla.findByCodigo('A')
        def avance = TipoPlanilla.findByCodigo('P')
        def liquidacion = TipoPlanilla.findByCodigo('Q')
        def liquidacionReajuste = TipoPlanilla.findByCodigo('L')
        def costoPorcentaje = TipoPlanilla.findByCodigo('C')
        def anticipoCmpl = TipoPlanilla.findByCodigo('B')
//        def obrasAdicionales  = TipoPlanilla.findByCodigo('O')
        def cmpl

        def pla = Planilla.findByContratoAndTipoPlanilla(contrato, anticipo)

        def anticipoPagado = false
        def esAnticipo = false
        if (!pla) {
            esAnticipo = false
        } else {
//            if (pla.fechaMemoPagoPlanilla) {
                anticipoPagado = true
                tiposPlanilla -= pla.tipoPlanilla
//            }
        }

        def cPlanillas = Planilla.findAllByContrato(contrato, [sort: 'fechaInicio']).size()
        def planillasAvance = Planilla.findAllByContratoAndTipoPlanilla(contrato, avance, [sort: "fechaInicio"])

        println "---- cPlanillas: $cPlanillas, planillasAvance: $planillasAvance"

        if (cPlanillas == 0) {
            tiposPlanilla = TipoPlanilla.findAllByCodigo('A')
//            println "3: " + tiposPlanilla.codigo
            esAnticipo = true
        } else {
            cmpl = Contrato.findByPadre(contrato)
            println "hay complementario: $cmpl"
            if(cmpl) {
                def plaC = Planilla.findByContratoAndTipoPlanillaInList(contrato, [anticipoCmpl])
                if(!plaC){
                    tiposPlanilla = TipoPlanilla.findAllByCodigo('B')
                    esAnticipo = true
                } else {
                    esAnticipo = planillaInstance?.tipoPlanilla?.codigo == 'B'
                    /* Otros tipos de complementario*/
                }
            } else {
                def pll = Planilla.findByContratoAndTipoPlanilla(contrato, liquidacion)
                if (pll) {
                    tiposPlanilla -= avance
                    tiposPlanilla -= liquidacion
                }
                def plr = Planilla.findByContratoAndTipoPlanilla(contrato, liquidacionReajuste)
                if (plr && !params.id) {
                    tiposPlanilla -= plr.tipoPlanilla
                    liquidado = true
                }
//            println "7...: " + tiposPlanilla.codigo
                def plc = Planilla.findByContratoAndTipoPlanilla(contrato, costoPorcentaje)
                if (plc) {
                    def plcs = Planilla.findAllByContratoAndTipoPlanilla(contrato, costoPorcentaje)
                    def tt = plcs.sum { it.valor }
                    println "total planillas C + %: ${tt}"
                    if (tt >= (contrato.monto * 0.1).round(2)) {  /* TODO: validar Porcentaje */
                        tiposPlanilla -= plc.tipoPlanilla
                    }
                }
            }


/*
            def poa = Planilla.findByContratoAndTipoPlanilla(contrato, obrasAdicionales)
            if (poa) {
                tiposPlanilla -= poa.tipoPlanilla
            }
*/
        }

//        println "pasa filtros...: ${tiposPlanilla.codigo}, .. planillasAvance: ${planillasAvance.size()}"
//        println "contrato: $contrato"

        def periodosEjec
        if(planillasAvance.size() == 0) {
            periodosEjec = PeriodoEjecucion.findAllByContratoAndTipo(contrato, 'P', [sort: "fechaFin"])
        } else {
            def hasta = planillasAvance[-1].fechaFin
//            println "fecha hasta: $hasta"
            periodosEjec = PeriodoEjecucion.findAllByContratoAndTipoInListAndFechaInicioGreaterThan(contrato, ['P', 'A', 'C'], hasta, [sort: "fechaFin"])
        }
        def finalObra = null
//        println "periodosEjec: ${periodosEjec.size()}"
        if (periodosEjec.size() > 0) {
            finalObra = periodosEjec.last().fechaFin
        }
//        println "fecha de fin de obra: $finalObra, periodos: ${periodosEjec.size()}"


        def costo = false
        if (planillasAvance.size() > 0) {
            if (planillasAvance.last().fechaFin == finalObra) {
                def plp = Planilla.findByContratoAndTipoPlanilla(contrato, avance)
                tiposPlanilla -= plp.tipoPlanilla
//                println "10: " + tiposPlanilla.codigo
            }
            planillasAvance.each { pa ->
//                println "busca planillas de avance sin fecha pagado, fecha pago: $pa.fechaMemoPagoPlanilla"
                if (pa.fechaMemoPagoPlanilla == null) {
//                    println "busca padre sin pago $pa.id"
                    def costos = Planilla.findAllByPadreCosto(pa)
                    if (costos.size() == 0) {
                        costo = true
                    }
                }
            }
        }

        if (tiposPlanilla.find { it.codigo == "P" }) {
            tiposPlanilla -= liquidacionReajuste
//            println "9: " + tiposPlanilla.codigo
        }

        def periodos = []
        if(!params.id){
            planillaInstance.numero = cPlanillas + 1
            periodos = ponePeriodos(tiposPlanilla, contrato, anticipo, periodosEjec, finalObra)
            println "retorna periodos: $periodos"
        }

        def now = new Date()
        def maxDatePres = "new Date(${now.format('yyyy')},${now.format('MM').toInteger() - 1},"
        if (now.format("dd").toInteger() > 14) {
            maxDatePres += "14"
        } else {
            maxDatePres += now.format("dd")
        }
        maxDatePres += ")"

        def minDatePres = "new Date(${now.format('yyyy')},${now.format('MM').toInteger() - 1},1)"
        def fiscalizadorAnterior
        if (planillasAvance.size() > 0) {
            fiscalizadorAnterior = planillasAvance.last().fiscalizadorId
        }

//        liquidado = false

        def fechaMax
        if (contrato.fechaSubscripcion)
            fechaMax = contrato.fechaSubscripcion.plus(720)
        else
            fechaMax = new Date()
//        println "fecha max " + fechaMax

        def suspensiones = Modificaciones.withCriteria {
            eq("obra", obra)
            eq("tipo", "S")
            le("fechaInicio", new Date().clearTime())
            or {
                isNull("fechaFin")
                gt("fechaFin", new Date().clearTime())
            }
        }
        def ini = Modificaciones.withCriteria {
            eq("obra", obra)
            eq("tipo", "S")
            le("fechaInicio", new Date().clearTime())
            or {
                isNull("fechaFin")
                gt("fechaFin", new Date().clearTime())
            }
            projections {
                min("fechaInicio")
            }
        }
//        println "periodos: $periodos"
//        println "12: ${tiposPlanilla.codigo}. liquidado: $liquidado, anticipoPagado: $anticipoPagado"
//        println "es anticipo: $esAnticipo"
        tiposPlanilla = tiposPlanilla.sort{it.nombre}
//        println "final..: ${tiposPlanilla.codigo} contrato: $contrato"

        //planilla asociada
        def tiposPlan = TipoPlanilla.findAllByCodigoInList(["P", "Q", "R", ])
        def planillasAvanceAsociada = Planilla.findAllByContratoAndTipoPlanillaInList(contrato, tiposPlan, [sort: 'fechaInicio'])

        def formulasVarias = FormulaPolinomicaReajuste.findAllByContrato(contrato)

        def anticipoN


        println "esAnticipo: $esAnticipo, tipos: $tiposPlanilla, periodos: $periodos"

        return [planillaInstance: planillaInstance, contrato: contrato, tipos: tiposPlanilla, obra: contrato.oferta.concurso.obra,
                periodos        : periodos, esAnticipo: esAnticipo, anticipoPagado: anticipoPagado, maxDatePres: maxDatePres,
                minDatePres     : minDatePres, fiscalizadorAnterior: fiscalizadorAnterior, liquidado: liquidado, fechaMax: fechaMax,
                suspensiones:suspensiones, ini:ini, planillas: planillasAvanceAsociada, formulas: formulasVarias,
                hayCmpl: (cmpl? true : false)]
    }

    /* este tipod eobras no requieren anticipo ni FP:
    *  es contratentrega, sin reajuste de precios
    *  no hay control de planillas ni periodos*/
    def sinAnticipo() {
        println "sinAnticipo planillas: $params"
        def contrato = Contrato.get(params.contrato)
        def obra = contrato.obra

        def planillaInstance = new Planilla(params)
        planillaInstance.contrato = contrato
        if (params.id) {
            planillaInstance = Planilla.get(params.id)
        }

        /*** solo existe planillas P **/
        def tiposPlanilla = TipoPlanilla.findAllByCodigoInList(["E"])

        def now = new Date()
        def maxDatePres = "new Date(${now.format('yyyy')},${now.format('MM').toInteger() - 1},"

        if (now.format("dd").toInteger() > 14) {
            maxDatePres += "14"
        } else {
            maxDatePres += now.format("dd")
        }
        maxDatePres += ")"

        def minDatePres = "new Date(${now.format('yyyy')},${now.format('MM').toInteger() - 1},1)"

        def fechaMax
        if (contrato.fechaSubscripcion)
            fechaMax = contrato.fechaSubscripcion.plus(720)
        else
            fechaMax = new Date()

        return [planillaInstance: planillaInstance, contrato: contrato, tipos: tiposPlanilla, obra: contrato.oferta.concurso.obra,
                maxDatePres: maxDatePres, minDatePres: minDatePres, fechaMax: fechaMax]
    }


    def getLastDayOfMonth(fecha) {
        Date today = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);

        Date firstDayOfMonth = calendar.getTime();

        return firstDayOfMonth
    }

    def save() {  /* guarda planilla */
        println "save " + params
        def cntr = Contrato.get(params.contrato.id)
        def tipo
        def fechaInicio = ""
        def generaCmpl = false

        if (params.id) {
            tipo = Planilla.get(params.id).tipoPlanilla
        } else {
            tipo = TipoPlanilla.get(params.tipoPlanilla.id.toLong())
        }
        if (tipo.codigo == "A") {   // anticipo
            def fp = FormulaPolinomicaReajuste.findByContratoAndDescripcionIlike(cntr, '%princ%')
            params.avanceFisico = 0
            params."formulaPolinomicaReajuste.id" = fp.id
        }
        if (tipo.codigo in ["C", "B"]) {   // costo+%, anticipo de complementario
            params.avanceFisico = Planilla.executeQuery("select max(avanceFisico) from Planilla where contrato = :c", [c: cntr])[0]
            if(tipo.codigo == 'B') {
                def fprj = FormulaPolinomicaReajuste.findByContratoAndDescripcionIlike(cntr, '%comple%')
                params."formulaPolinomicaReajuste.id" = fprj.id
            }
        }

        println "params.formulaPolinomicaReajuste.id:" + params."formulaPolinomicaReajuste.id"

//        println "avance físico: ${params.avanceFisico}"
        // liquidación
        if (tipo.codigo in ['L', 'R']) {
            def contrato = Contrato.get(params.contrato.id)
            def tipoAvance = TipoPlanilla.findAllByCodigoInList(['P', 'Q'])
//            println "tipoAvance: $tipoAvance, contrato: ${contrato.id}"
            def planillasAvance = Planilla.findAllByContratoAndTipoPlanillaInList(contrato, tipoAvance, [sort: "id", order: "asc"])
//            println "... $planillasAvance"
            def ultimaAvance = planillasAvance.last()
            params.avanceFisico = ultimaAvance.avanceFisico
            fechaInicio = new Date().parse("dd-MM-yyyy", params.fechaOficioEntradaPlanilla)
        }

        println "fecha inicio: ${params.fechaInicio}"

        if (!params.diasMultaDisposiciones) params.diasMultaDisposiciones = 0

        if (params.fechaPresentacion) params.fechaPresentacion = new Date().parse("dd-MM-yyyy", params.fechaPresentacion)

        if (params.fechaIngreso) params.fechaIngreso = new Date().parse("dd-MM-yyyy", params.fechaIngreso)

        if (params.fechaOficioSalida) params.fechaOficioSalida = new Date().parse("dd-MM-yyyy", params.fechaOficioSalida)

        if (params.fechaMemoSalida) params.fechaMemoSalida = new Date().parse("dd-MM-yyyy", params.fechaMemoSalida)

        if (params.fechaOficioEntradaPlanilla) params.fechaOficioEntradaPlanilla = new Date().parse("dd-MM-yyyy", params.fechaOficioEntradaPlanilla)

        if(params.fechaInicio && !fechaInicio) {
            params.fechaInicio = new Date().parse("dd-MM-yyyy", params.fechaInicio)
        } else if(fechaInicio) {
            params.fechaInicio = fechaInicio
        }

        if (params.fechaFin) {
            params.fechaFin = new Date().parse("dd-MM-yyyy", params.fechaFin)
        } else if(tipo.codigo == 'R') {
            params.fechaFin = fechaInicio + 1
        }

        if (!params.fechaPresentacion) params.fechaPresentacion = params.fechaIngreso

        if (params.oficioEntradaPlanilla) params.oficioEntradaPlanilla = params.oficioEntradaPlanilla.toString().toUpperCase()

        if (params.numero) params.numero = params.numero.toString().toUpperCase()

        def planillaInstance
//        session.override = false
        if (params.id) {
//            println("entro")
//            params.fechaPresentacion = params.fechaIngreso

            if (!params.fechaPresentacion) {
                params.fechaPresentacion = params.fechaIngreso
            }

            def planillaPorAsociar
            if(params.asociada != 'null') {
                planillaPorAsociar = Planilla.get(params.asociada)
            }

            planillaInstance = Planilla.get(params.id)
            if (!planillaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Planilla con id " + params.id
                params.contrato = params.contrato.id
                redirect(action: 'form', params: params)
                return
            }//no existe el objeto
//            println "es update "+params.valor_multa+"  "+params.multaDescripcion
//            println "es update "+params
            planillaInstance.properties = params
            if(planillaInstance.tipoPlanilla.codigo == 'C'){
                planillaInstance.padreCosto = planillaPorAsociar
                planillaInstance.fechaInicio = planillaPorAsociar?.fechaInicio
                planillaInstance.fechaFin = planillaPorAsociar?.fechaFin
            }
            planillaInstance.periodoIndices = PeriodosInec.get(params.periodoIndices.id)

            if(!params.noPagoValor || (params.noPagoValor == "")) params.noPagoValor = 0
//            println "nopagoValor: '${params.noPagoValor}'"

            planillaInstance.noPago = params.noPago
            planillaInstance.noPagoValor = params.noPagoValor.toDouble()

            if(planillaInstance.tipoPlanilla.codigo == 'O') { //pone fecha del último periodo planillado
                def pl = Planilla.findByContratoAndTipoPlanilla(cntr, TipoPlanilla.findByCodigo('Q'))
                def poAnterior = ReajustePlanilla.findAllByPlanilla(pl, [sort: 'periodo']).last()
                planillaInstance.fechaInicio = preciosService.primerDiaDelMes(poAnterior.fechaInicio)
                planillaInstance.fechaFin = cntr.fechaPedidoRecepcionFiscalizador
            }

//            session.override = true
        }//es edit
        else {
            println "params luego de override: ${params}"

            planillaInstance = new Planilla(params)
            println " creando planilla: ${params.id} con ${params.descripcionMulta}"

            switch (planillaInstance.tipoPlanilla.codigo) {
                case 'A':
                    //es anticipo hay q ingresar el valor de la planilla
                    planillaInstance.valor = planillaInstance.contrato.anticipo
                    println "llego al save !!!!!  "+params.periodoPlan
//                    planillaInstance.periodoAnticipo = PeriodosInec.get(params.periodoPlan)
                    break;
                case ['P', 'Q']:

                    def periodoPlanilla = params.periodoPlanilla
                    def fechas = periodoPlanilla.split("_")
                    planillaInstance.fechaInicio = new Date().parse("dd-MM-yyyy", fechas[0])
                    planillaInstance.fechaFin = new Date().parse("dd-MM-yyyy", fechas[1])
                    break;
                case ['O']:  //obras adicionales pone ultimo mes de la obra <-- prej
                    def pl = Planilla.findByContratoAndTipoPlanilla(cntr, TipoPlanilla.findByCodigo('Q'))
                    def poAnterior = ReajustePlanilla.findAllByPlanilla(pl, [sort: 'periodo']).last()
                    planillaInstance.fechaInicio = preciosService.primerDiaDelMes(poAnterior.fechaInicio)
                    planillaInstance.fechaFin = cntr.fechaPedidoRecepcionFiscalizador
                    break;
                case "L":
                    //es de liquidacion

                    break;
                case "C":
                    //es de costo y porcentaje: fecha inicio y fecha fin se ponen la fecha de presentacion (?)
                    planillaInstance.fechaInicio = planillaInstance.fechaPresentacion
                    planillaInstance.fechaFin = planillaInstance.fechaPresentacion

                    def planillaPorAsociar = Planilla.get(params.asociada)
                    planillaInstance.padreCosto = planillaPorAsociar
                    println("params asociada " + params.asociada)
                    println("padre " + planillaInstance.padreCosto)
                    break;
            }

            def contrato = Contrato.get(params.contrato.id)
            planillaInstance.fiscalizador = contrato.fiscalizador

            if(planillaInstance.tipoPlanilla.codigo == 'A'){
                planillaInstance.tipoContrato = 'P'
            } else {
                if(planillaInstance.tipoPlanilla.codigo == 'B'){
                    planillaInstance.tipoContrato = 'C'
                } else {
                    planillaInstance.tipoContrato = 'P'
                    if(params."complementario" == 'S'){
                        generaCmpl = true
                    }
                }
            }


        } //es create

        planillaInstance.formulaPolinomicaReajuste = FormulaPolinomicaReajuste.get(params."formulaPolinomicaReajuste.id")


        //tipoContrato


        if (!planillaInstance.save(flush: true)) {
            println planillaInstance.errors
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Planilla " + (planillaInstance.id ? planillaInstance.id : "") + "</h4>"

            str += g.renderErrors(bean: planillaInstance)

            flash.message = str
            params.contrato = params.contrato.id
            redirect(action: 'form', params: params)
            return
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Planilla " + planillaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Planilla " + planillaInstance.id
        }

        if(generaCmpl){
            def plnlCmpl = new Planilla()
            def fp = FormulaPolinomicaReajuste.findByContratoAndDescripcionIlike(cntr, '%compl%')
            if(!fp) println "Error: no se ha definido la FP para complementario"
            plnlCmpl.properties = planillaInstance.properties
            plnlCmpl.tipoContrato = 'C'
            plnlCmpl.numero += '-C'
            plnlCmpl.descripcion += "Contrato Complementario"
            plnlCmpl.formulaPolinomicaReajuste = fp
//            plnlCmpl.save(flush: true)
            if (!plnlCmpl.save(flush: true)) {
                println "Error al crear plnlCmpl: ${plnlCmpl.errors}"
            } else {
                plnlCmpl.refresh()
                planillaInstance.planillaCmpl = plnlCmpl
                planillaInstance.save(flush: true)
            }
        }

        switch (planillaInstance.tipoPlanilla.codigo) {
            case 'A':
                redirect(controller: 'planilla', action: 'listAdmin', id: planillaInstance.contrato.id)
                break;
            case 'L':
//                redirect(controller: 'planilla2', action: 'liquidacion', id: planillaInstance.id)
                redirect(action: 'listFiscalizador', id: planillaInstance.contrato.id)
                break;
            case ['P', 'Q', 'O']:  //avance, liquidación y obras adicionales
                def hayCmpl = Contrato.findByPadre(planillaInstance.contrato)
                if(hayCmpl && planillaInstance.tipoContrato in ['P', 'C']) {
                    redirect(action: 'detalleNuevo', id: planillaInstance.id, params: [contrato: planillaInstance.contratoId])
                } else {
                    redirect(action: 'detalle', id: planillaInstance.id, params: [contrato: planillaInstance.contratoId])
                }
                break;
            case 'C':
                redirect(action: 'detalleCosto', id: planillaInstance.id, params: [contrato: planillaInstance.contratoId])
                break;
            default:
                redirect(action: 'list', id: planillaInstance.contratoId)
        }


    }

    def saveSinAntc() {  /* guarda planilla */
        println "saveSinAntc " + params
        def cntr = Contrato.get(params.contrato.id)
        def tipo
        def fechaInicio = ""
        def generaCmpl = false

        if (params.id) {
            tipo = Planilla.get(params.id).tipoPlanilla
        } else {
            tipo = TipoPlanilla.get(params.tipoPlanilla.id.toLong())
        }

        if (!params.diasMultaDisposiciones) params.diasMultaDisposiciones = 0
        if (params.fechaPresentacion) params.fechaPresentacion = new Date().parse("dd-MM-yyyy", params.fechaPresentacion)
        if (params.fechaIngreso) params.fechaIngreso = new Date().parse("dd-MM-yyyy", params.fechaIngreso)
        if (params.fechaOficioSalida) params.fechaOficioSalida = new Date().parse("dd-MM-yyyy", params.fechaOficioSalida)
        if (params.fechaMemoSalida) params.fechaMemoSalida = new Date().parse("dd-MM-yyyy", params.fechaMemoSalida)
        if (params.fechaOficioEntradaPlanilla) params.fechaOficioEntradaPlanilla = new Date().parse("dd-MM-yyyy", params.fechaOficioEntradaPlanilla)

        println "params.formulaPolinomicaReajuste.id:" + params."formulaPolinomicaReajuste.id"
        println "fecha inicio: ${params.fechaInicio}"

        if(params.fechaInicio && !fechaInicio) {
            params.fechaInicio = new Date().parse("dd-MM-yyyy", params.fechaInicio)
        } else if(fechaInicio) {
            params.fechaInicio = fechaInicio
        }

        if (params.fechaFin) {
            params.fechaFin = new Date().parse("dd-MM-yyyy", params.fechaFin)
        } else if(tipo.codigo == 'R') {
            params.fechaFin = fechaInicio + 1
        }

        if (!params.fechaPresentacion) params.fechaPresentacion = params.fechaIngreso
        params.tipoContrato = 'E'

        if (params.oficioEntradaPlanilla) params.oficioEntradaPlanilla = params.oficioEntradaPlanilla.toString().toUpperCase()
        if (params.numero) params.numero = params.numero.toString().toUpperCase()

        def planillaInstance

        if (params.id) {
//            println("entro")
            params.fechaPresentacion = params.fechaIngreso
            def planillaPorAsociar
            if(params.asociada != 'null') {
                planillaPorAsociar = Planilla.get(params.asociada)
            }

            planillaInstance = Planilla.get(params.id)
            if (!planillaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Planilla con id " + params.id
                params.contrato = params.contrato.id
                redirect(action: 'form', params: params)
                return
            }//no existe el objeto
            planillaInstance.properties = params

            if(!params.noPagoValor || (params.noPagoValor == "")) params.noPagoValor = 0

            planillaInstance.noPago = params.noPago
            planillaInstance.noPagoValor = params.noPagoValor.toDouble()
        }//es edit
        else {
            println "params luego de override: ${params}"

            planillaInstance = new Planilla(params)
            println " creando planilla: ${params.id} con ${params.descripcionMulta}"

            def contrato = Contrato.get(params.contrato.id)
            planillaInstance.fiscalizador = contrato.fiscalizador

        } //es create

        planillaInstance.formulaPolinomicaReajuste = FormulaPolinomicaReajuste.get(params."formulaPolinomicaReajuste.id")

        if (!planillaInstance.save(flush: true)) {
            println planillaInstance.errors
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Planilla " + (planillaInstance.id ? planillaInstance.id : "") + "</h4>"

            str += g.renderErrors(bean: planillaInstance)

            flash.message = str
            params.contrato = params.contrato.id
            redirect(action: 'form', params: params)
            return
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Planilla " + planillaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Planilla " + planillaInstance.id
        }

        redirect(action: 'dtEntrega', id: planillaInstance.id, params: [contrato: planillaInstance.contratoId])

    }


    private String fechaConFormato(fecha, formato) {
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
                case "dd MMMM yyyy":
                    strFecha = "" + fecha.format("dd") + " de " + mesesLargo[fecha.format("MM").toInteger()] + " de " + fecha.format("yyyy")
                    break;
                default:
                    strFecha = "Formato " + formato + " no reconocido"
                    break;
            }
        }
//        println ">>>>>>" + strFecha
        return strFecha
    }

    private String fechaConFormato(fecha) {
        return fechaConFormato(fecha, "MMM-yy")
    }

    private String cap(str) {
        return str.replaceAll(/[a-zA-Z_0-9áéíóúÁÉÍÓÚñÑüÜ]+/, {
            it[0].toUpperCase() + ((it.size() > 1) ? it[1..-1].toLowerCase() : '')
        })
    }

    private String nombrePersona(persona, tipo) {
//        println "nombrePersona" + persona
//        println tipo
        def str = ""
        if (persona) {
            switch (tipo) {
                case "pers":
                    str = cap((persona.titulo ? persona.titulo + " " : "") + persona.nombre + " " + persona.apellido)
                    break;
                case "prov":
                    str = cap((persona.titulo ? persona.titulo + " " : "") + persona.nombreContacto + " " + persona.apellidoContacto)
                    break;
            }
        }
        return str
    }

    private String nombrePersona(persona) {
        return nombrePersona(persona, "pers")
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
        return numero(num, 3)
    }

    def resumen() {
        //para volver a generar los valores de reajuste poner esta variable en true!!
        def override = false

        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.obra
        def contrato = planilla.contrato
//        def planillas = Planilla.findAllByContrato(contrato, [sort: "id"])

        def planillas = Planilla.withCriteria {
            and {
                eq("contrato", contrato)
                or {
                    lt("fechaInicio", planilla.fechaFin)
                    isNull("fechaInicio")
                }
                order("id", "asc")
            }
        }

        def fp = FormulaPolinomica.findAllByObra(obra)
        def fr = FormulaPolinomicaContractual.findAllByContrato(contrato)
        def tipo = TipoFormulaPolinomica.get(1)
        def oferta = contrato.oferta
        def fechaEntregaOferta = oferta.fechaEntrega - 30

        def periodoOferta = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(fechaEntregaOferta, fechaEntregaOferta)

//        println "================" + oferta.id + "  " + oferta.fechaEntrega + "  " + (oferta.fechaEntrega - 30) + "   " + periodoOferta


        def planillaAnticipo = Planilla.findByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo("A"))
//        println pcs.numero

        def fechaPlanillaAnticipo
        // utilizamos siempre la fecha de presentacion del anticipo y se hace una planilla de reajuste final
        // para reajustar el anticipo a la fecha de pago en las planillas de avnace descomentar esta parte
//        if (planilla.tipoPlanilla.codigo == "A") {
        fechaPlanillaAnticipo = planillaAnticipo.fechaPresentacion
//        } else {
//            fechaPlanillaAnticipo = planillaAnticipo.fechaPago
//        }
        def periodoAnticipo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(fechaPlanillaAnticipo, fechaPlanillaAnticipo)

        def periodos = [], periodosTmp = [], periodosPlanilla = []
        def data2 = [
                c: [:],
                p: [:]
        ]
        def fpB0

        //copia la formula polinomica a la formula polinomica contractual si esta no existe
        if (fr.size() < 5) {
            fr.each {
                it.delete(flush: true)
            }
            fp.each {
                if (it.valor > 0) {
                    def frpl = new FormulaPolinomicaContractual()
                    frpl.valor = it.valor
                    frpl.contrato = contrato
                    frpl.indice = it.indice
                    frpl.tipoFormulaPolinomica = tipo
                    frpl.numero = it.numero
                    if (!frpl.save(flush: true)) {
                        println "error frpl" + frpl.errors
                    }
                }
            }
            def fpP0 = new FormulaPolinomicaContractual()
            fpP0.valor = 0
            fpP0.contrato = contrato
            fpP0.indice = null
            fpP0.tipoFormulaPolinomica = tipo
            fpP0.numero = "P0"
            if (!fpP0.save(flush: true)) {
                println "error fpP0" + fpP0.errors
            }
            fpB0 = new FormulaPolinomicaContractual()
            fpB0.valor = 0
            fpB0.contrato = contrato
            fpB0.indice = null
            fpB0.tipoFormulaPolinomica = tipo
            fpB0.numero = "B0"
            if (!fpB0.save(flush: true)) {
                println "error fpB0" + fpB0.errors
            }
            def fpFr = new FormulaPolinomicaContractual()
            fpFr.valor = 0
            fpFr.contrato = contrato
            fpFr.indice = null
            fpFr.tipoFormulaPolinomica = tipo
            fpFr.numero = "Fr"
            if (!fpFr.save(flush: true)) {
                println "error fpFr" + fpFr.errors
            }
        } else {
            fpB0 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "B0")
        }

        def pcs = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                or {
                    ilike("numero", "c%")
                    and {
                        ne("numero", "P0")
                        ilike("numero", "p%")
                    }
                }
                order("numero", "asc")
            }
        }

        //llena el arreglo de periodos
        //el periodo que corresponde a la fecha de entrega de la oferta
        periodos.add(periodoOferta)
        //el periodo que corresponde a la fecha del anticipo
        periodos.add(periodoAnticipo)

        planillas.each { pl ->
            if (pl.tipoPlanilla.codigo == 'A') {
//                //si es anticipo: el periodo q corresponde a la fecha del anticipo

//                if (planilla.tipoPlanilla.codigo == "A") {
//                    fechaPlanillaAnticipo = pl.fechaPresentacion
//                } else {
//                    fechaPlanillaAnticipo = pl.fechaPago
//                }
//                println pl.fechaPresentacion.format("dd-MM-yyyy") + "   " + pl.fechaPago.format("dd-MM-yyyy") + "   " + fechaPlanillaAnticipo.format("dd-MM-yyyy")
//                def prin = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(fechaPlanillaAnticipo, fechaPlanillaAnticipo)
//                periodos.add(prin)
            } else {
//                println pl.id + "   " + pl.fechaInicio.format("dd-MM-yyyy") + " a " + pl.fechaFin.format("dd-MM-yyyy")
                def m1 = pl.fechaInicio.format("MM")
                def m2 = pl.fechaFin.format("MM")
//                println "\t" + m1 + "   " + m2
                if (m1 == m2) {
                    if (pl.periodoIndices) {
//                        println "++ " + pl.periodoIndices
                        periodosTmp.add(pl.periodoIndices)
                    }
                    if (pl == planilla) {
//                        println "-- " + pl
                        periodosPlanilla.add(pl)
                    }
                } else {
                    def y = pl.fechaInicio.format("yyyy")
                    (m1.toInteger()..m2.toInteger()).each {
//                        println "\t\t" + it + " - " + y
                        def fi = new Date().parse("dd-MM-yyyy", "01-" + it + "-" + y)
//                        if (pl.periodoIndices.fechaFin >= fi) {
                        def prin = PeriodosInec.findByFechaInicio(fi)
                        if (prin) {
                            periodosTmp.add(prin)
                        }
                        if (pl == planilla) {
                            periodosPlanilla.add(prin)
                        }
                        if (it == 12) {
                            y = pl.fechaFin.format("yyyy")
                        }
//                        }
                    }
                }
            }
        }
        periodosTmp = periodosTmp.unique()
        periodos = periodos + periodosTmp
//        periodos = periodos.unique()
//        println periodos
//        render periodos.descripcion

        def erroresIndice = "", alertasIndice = ""
        def failIndice = false
//        println "Aqui empieza las inserciones " + periodos
        periodos.eachWithIndex { per, perNum ->
            def pl = planilla
            if (perNum == 0) {
                pl = null
            }
//            def valRea = ValorReajuste.findAllByObraAndPeriodoIndice(obra, per)

//            println ">>>>>" + per
//            println ">>>Periodo " + per?.descripcion + " (${perNum}) hay " + valRea.size() + " valRea: " + valRea.id + " (obra: ${obra.id})"

            def tot = [c: 0, p: 0]
            //si no existen valores de reajuste, se crean

//            println "" + per + "   " + valRea

//            if (valRea.size() == 0) {
            pcs.each { c ->
                def failed = false, alert = ""
//                def val = ValorIndice.findByPeriodoAndIndice(per, c.indice)?.valor
                def val = DetalleReajuste.findByPeriodoAndIndice(per, c.indice)?.valor
                if (!val) {
                    if (c.numero == "p01") {
                        val = 1
                    } else {
//                    println "--------------------------"
//                    println "\t\tval: " + val + "   per:" + per + "   indice: " + c.indice
                        def valores = ValorIndice.findAllByIndice(c.indice, [sort: "periodo"])
//                    println "\t\tvalores: " + valores.id + "  " + valores.periodo + "  " + valores.valor
                        if (valores.size() > 0) {
                            val = valores.last().valor
                            alert = "ALERTA - No se encontró un valor de índice para " + per?.descripcion + " para " + c.indice.descripcion + ", se utilizó el de " + valores.last().periodo.descripcion + "<br/>"
                        } else {
                            println "ERROR!!!!!"
                            erroresIndice += "ERROR - No se encontró un valor de índice para " + c.indice.descripcion + "<br/>"
                            failIndice = true
                            failed = true
                        }
                    }
                } else {
//                    println "\t\tSi val: " + val + "   " + per + "   " + c.indice
                }
                if (!failed) {
//                    println "Per: " + perNum + "    planilla: " + pl + "     val: " + val
                    def formulaTmp = FormulaPolinomicaContractual.findByIndiceAndContrato(c.indice, contrato)
                    def vr = ValorReajuste.withCriteria {
                        eq("formulaPolinomica", formulaTmp)
                        eq("obra", obra)
                        eq("periodoIndice", per)
//                        if (pl) {
//                            eq("planilla", pl)
//                        } else {
//                            isNull("planilla")
//                        }
                    }
//                    println
//                    println "vr: " + vr.id + "   pl " + vr.planilla.id + "   >" + vr.valor + "<\t" + val + " * " + c.valor + " = " + (val * c.valor) + "    planilla " + pl?.id + "   obra: " + obra.id + "   per:" + per.id
                    if (vr.size() == 0) {
//                        println "???? " + vr
                        if (alert != "") {
                            alertasIndice += alert
                        }
                        vr = new ValorReajuste([
                                valor            : val * c.valor,
                                formulaPolinomica: formulaTmp,
                                obra             : obra,
                                periodoIndice    : per,
                                planilla         : pl
                        ])
                        if (!vr.save(flush: true)) {
                            println "vr errors " + vr.errors
                        } else {
//                            println "\tcrea vr ${vr.id}"
                        }
                    } else if (vr.size() == 1) {
                        vr = vr[0]
                        if (override) {
                            if (vr.planilla == planilla) {
//                                println "Planillas iguales " + vr.id
                                vr.valor = val * c.valor
                                if (!vr.save(flush: true)) {
                                    println "vr errors " + vr.errors
                                } else {
//                                        println "\tactualiza vr ${vr.id} valor: " + vr.valor
                                }
                            }
                        }
                    } else if (vr.size() > 1) {
                        erroresIndice += "Se encontró más de un valor de reajuste "
                        failIndice = true
                    }
                    def pos = "p"
                    if (c.numero.contains("c")) {
                        pos = "c"
                    }
                    tot[pos] += (vr.valor /** c.valor*/).round(3)
//                    println "\t\t" + pos + "   " + (vr.valor /** c.valor*/)
                    if (!data2[pos][perNum]) {
//                        println "\t\tCrea data2[${pos}][${perNum}]"
                        data2[pos][perNum] = [valores: [], total: 0, periodo: per]
                    }
//                    data2[pos][perNum]["valores"].add([formulaPolinomica: c, valorReajuste: vr, valorTabla: vr.valor])
                    data2[pos][perNum]["valores"].add([formulaPolinomica: c, valorReajuste: vr, valorTabla: val])
                }
            } //pcs.each
//            } //valRea.size == 0
//            else {
//                valRea.each { v ->
//                    def failed = false
//                    def c = pcs.find { it.indice == v.formulaPolinomica.indice }
//                    if (c) {
//                        def val = ValorIndice.findByPeriodoAndIndice(per, c.indice)?.valor
//                        if (!val) {
////                            val = 1
//                            def valores = ValorIndice.findAllByIndice(c.indice, [sort: "periodo"])
//                            if (valores.size() > 0) {
//                                val = valores.last().valor
//                                erroresIndice += "No se encontró un valor de índice para " + per.descripcion + " para " + c.indice.descripcion + ", se utilizó el de " + valores.last().periodo.descripcion + "<br/>"
//                            } else {
//                                erroresIndice += "No se encontró un valor de índice para " + c.indice.descripcion + "<br/>"
//                                failIndice = true
//                                failed = true
//                            }
//                        }
//                        if (!failed) {
////                        println "\t\t"+per.descripcion+"   "+c.indice+"   "+val+"   "+c.id
//                            def pos = "p"
//                            if (c.numero.contains("c")) {
//                                pos = "c"
//                            }
//                            tot[pos] += (val * c.valor).round(3)
////                        println "\t\t" + pos + "   " + (v.valor * c.valor)
//                            if (!data2[pos][perNum]) {
////                            println "\t\tCrea data2[${pos}][${perNum}]"
//                                data2[pos][perNum] = [valores: [], total: 0, periodo: per]
//                            }
//
//                            data2[pos][perNum]["valores"].add([formulaPolinomica: c, valorReajuste: v, valorTabla: val])
//                        }
//                    }
//                }
//            } //valRea.size == 0

//            println "data[c][${perNum}][total]=${tot['c']}"
            data2["c"][perNum]["total"] = tot["c"]
//            println "data[p][${perNum}][total]=${tot['p']}"
            data2["p"][perNum]["total"] = tot["p"]

//            println "------" + per + "   " + fpB0
//            println per.class
//            println fpB0.class
//            println "\n\n"
            def vrB0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, fpB0)
            if (!vrB0) {
                vrB0 = new ValorReajuste([
                        obra             : obra,
                        planilla         : pl,
                        periodoIndice    : per,
                        formulaPolinomica: fpB0
                ])
            }
            vrB0.valor = tot["c"]
            if (!vrB0.save(flush: true)) {
                println "error al guardar valor de B0: " + tot["c"] + "\n" + vrB0.errors
            } else {
//                println "valor bo " + vrB0.valor + " per!!! " + per
            }
            def p01 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "p01")
//            println "p01 " + p01.valor
            if (p01) {
                def vrP01 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p01)
                if (vrP01) {
                    vrP01.valor = tot["c"]
                    if (!vrP01.save(flush: true)) {
                        println "error al guardar valor de p01: " + tot["c"] + "\n" + vrP01.errors
                    }
                }
//                println "p01 2 " + vrP01.valor + " " + vrP01.id
            }

        }

        if (failIndice) {
            redirect(action: "errorIndice", params: [id: planilla.id, errores: erroresIndice, alertas: alertasIndice])
            return
        }

        def tableWidth = 150 * periodos.size() + 400

        def tablaBo = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px;'>"
        tablaBo += "<thead>"
        tablaBo += "<tr>"
        tablaBo += "<th colspan=\"2\">Cuadrilla Tipo</th>"
        tablaBo += "<th>Oferta</th>"
        tablaBo += "<th class='nb'>" + fechaConFormato(fechaEntregaOferta, "MMM-yy") + "</th>"
        tablaBo += "<th>Variación</th>"
        tablaBo += "<th class='nb'>Anticipo (" + fechaConFormato(fechaPlanillaAnticipo, "MMM-yy") + ")</th>"
        if (periodos.size() > 2) {
            periodos.eachWithIndex { per, i ->
                if (i > 1) {
                    tablaBo += "<th>Variación</th>"
                    tablaBo += "<th class='nb'>" + per?.descripcion + "</th>"
                }
            }
        }
        tablaBo += "</tr>"
        tablaBo += "</thead>"


        def tbodyB0 = "<tbody>"
        def totC = 0
        pcs.findAll { it.numero.contains("c") }.each { c ->
            tbodyB0 += "<tr>"
            tbodyB0 += "<td>" + c.indice.descripcion + " (" + c.numero + ")</td>"
            tbodyB0 += "<td class='number'>" + elm.numero(number: c.valor, decimales: 3) + "</td>"
            totC += (c.valor).round(3)
            data2.c.each { cp ->
                def act = cp.value.valores.find { it.formulaPolinomica.indice == c.indice }
//                def val = act.valorReajuste.valor
                def val = act.valorTabla
                def valReajuste = act.valorReajuste.valor
                tbodyB0 += "<td class='number'>" + elm.numero(number: val, decimales: 3) + "</td>"
                tbodyB0 += "<td class='number'>" + elm.numero(number: valReajuste, decimales: 3) + "</td>"
            }
            tbodyB0 += "</tr>"
        }
        tbodyB0 += "</tbody>"
        tbodyB0 += "<tfoot>"
        tbodyB0 += "<tr>"
        tbodyB0 += "<th>TOTALES</th>"
        tbodyB0 += "<td class='number bold'>" + elm.numero(number: totC, decimales: 3) + "</td>"
        data2.c.each { cp ->
            tbodyB0 += "<td></td>"
            tbodyB0 += "<td class='number bold'>" + elm.numero(number: cp.value.total, decimales: 3) + "</td>"
        }
        tbodyB0 += "</tr>"
        tbodyB0 += "</tfoot>"

        tablaBo += tbodyB0
        tablaBo += "</table>"

        def tablaP0 = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;' >"
        tablaP0 += '<thead>'
        tablaP0 += '<tr>'
        tablaP0 += '<th colspan="2" rowspan="2">Mes y año</th>'
        tablaP0 += '<th colspan="2">Cronograma</th>'
        tablaP0 += '<th colspan="2">Planillado</th>'
        tablaP0 += '<th colspan="2" rowspan="2">Valor P<sub>0</sub></th>'
        tablaP0 += '</tr>'
        tablaP0 += '<tr>'
        tablaP0 += '<th>Parcial</th>'
        tablaP0 += '<th>Acumulado</th>'
        tablaP0 += '<th>Parcial</th>'
        tablaP0 += '<th>Acumulado</th>'
        tablaP0 += '</tr>'
        tablaP0 += '</thead>'

        def p0s = []
        def tbodyP0 = "<tbody>"
        def tbodyMl = "<tbody>"

        def diasPlanilla = 0

        if (planilla.tipoPlanilla.codigo != "A") {
            diasPlanilla = planilla.fechaFin - planilla.fechaInicio + 1
        }
        def valorPlanilla = planilla.valor

        def acumuladoCrono = 0, acumuladoPlan = 0

        def diasAll = 0

        def totalMultaRetraso = 0, valorTotalPeriodoActual = 0

//        println "periodos "+periodos

        periodos.eachWithIndex { per, i ->
            if (i > 0) {
                def planillaActual = Planilla.findByPeriodoIndicesAndContrato(per, contrato)
                tbodyP0 += "<tr>"
                if (i == 1) {
                    tbodyP0 += "<th>ANTICIPO</th>"
                    tbodyP0 += "<th>"
                    tbodyP0 += fechaConFormato(fechaPlanillaAnticipo, "MMM-yy")
                    tbodyP0 += "</th>"
                    tbodyP0 += "<td colspan='4'></td>"
                    tbodyP0 += "<td class='number'>"
                    tbodyP0 += elm.numero(number: planillaAnticipo.valor)
                    tbodyP0 += "</td>"

                    def p0 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "P0")
                    def vrP0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p0)
                    if (!vrP0) {
                        vrP0 = new ValorReajuste([
                                obra             : obra,
                                planilla         : planillaAnticipo,
                                periodoIndice    : per,
                                formulaPolinomica: p0
                        ])
                    }
                    vrP0.valor = planillaAnticipo.valor
                    if (!vrP0.save(flush: true)) {
                        println "ERROR guardando P0: " + vrP0.errors
                    }
                    data2["p"][i]["p0"] = vrP0.valor
                    p0s[i - 1] = vrP0.valor
                } else {
//                    def periodosEjecucion = PeriodoEjecucion.findAllByObra(obra)
//                    def periodosEjecucion = PeriodoEjecucion.withCriteria {
//                        and {
//                            eq("obra", obra)
//                                or {
//                                    between("fechaInicio", per.fechaInicio, per.fechaFin)
//                                    between("fechaFin", per.fechaInicio, per.fechaFin)
//                                }
//                            order("fechaInicio")
//                        }
//                    }
//                    println "obra: " + obra.id
//                    println "per " + per.fechaInicio.format("dd-MM-yyyy") + "  " + per.fechaFin.format("dd-MM-yyyy")
//                    println "planilla fin: " + planilla.fechaFin

                    /*
                    obra: 1430
                    per 01-04-2012  30-04-2012
                    planilla fin: 2012-04-30 00:00:00.0
                     */


                    def periodosEjecucion = PeriodoEjecucion.withCriteria {
                        and {
                            eq("obra", obra)
                            or {
                                between("fechaInicio", per.fechaInicio, per.fechaFin)
                                between("fechaFin", per.fechaInicio, per.fechaFin)
                            }
//                            le("fechaFin", planilla.fechaFin)
                            order("fechaInicio", "asc")
                        }
                    }

                    def diasTotal = 0, valorTotal = 0
//                    println "\t\t-- " + periodosEjecucion

                    tbodyP0 += "<th>"
                    tbodyP0 += per?.descripcion
                    tbodyP0 += "</th>"
//                    println "????"
                    periodosEjecucion.each { pe ->
//                        println "\t" + pe.tipo + "  " + pe.fechaInicio.format("dd-MM-yyyy") + "   " + pe.fechaFin.format("dd-MM-yyyy")
                        if (pe.tipo == "P") {
                            def diasUsados = 0
                            def diasPeriodo = pe.fechaFin - pe.fechaInicio + 1
//                            println "\t\tdias periodo: " + diasPeriodo
//                            def crono = CronogramaEjecucion.findAllByPeriodo(pe)
                            def crono = CrngEjecucionObra.findAllByPeriodo(pe)
                            def valorPeriodo = crono.sum { it.precio }
//                            println "\t\tvalor periodo: " + valorPeriodo
                            if (per) {
                                if (pe.fechaInicio <= per.fechaInicio) {
                                    diasUsados = pe.fechaFin - per.fechaInicio + 1
//                                    println "\t\t1 dias usados: " + diasUsados
                                } else if (pe.fechaInicio > per.fechaInicio && pe.fechaFin < per.fechaFin) {
                                    diasUsados = pe.fechaFin - pe.fechaInicio + 1
//                                    println "\t\t2 dias usados: " + diasUsados
                                } else if (pe.fechaFin >= per.fechaFin) {
                                    diasUsados = per.fechaFin - pe.fechaInicio + 1
//                                    println "\t\t3 dias usados: " + diasUsados
                                }
                                diasTotal += diasUsados
                            }
                            def valorUsado = (valorPeriodo / diasPeriodo) * diasUsados
                            valorTotal += valorUsado
//                            println "\t\tvalor usado: " + valorUsado
                        }
                    }
                    acumuladoCrono += valorTotal
//                    println "***** " + valorPlanilla + "  " + diasPlanilla + "  " + diasTotal
                    def planillado = (valorPlanilla / diasPlanilla) * diasTotal
                    acumuladoPlan += planillado
//                    println "TOTAL: " + diasTotal + " dias"
//                    println "PLANILLADO: " + planillado

                    def p0 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "P0")
                    def vrP0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p0)
                    if (!vrP0) {
                        vrP0 = new ValorReajuste([
                                obra             : obra,
                                planilla         : planillaActual,
                                periodoIndice    : per,
                                formulaPolinomica: p0
                        ])
                    }
                    vrP0.valor = Math.max(valorTotal, planillado)
                    if (!vrP0.save(flush: true)) {
                        println "ERROR guardando P0: " + vrP0.errors
                    }
                    data2["p"][i]["p0"] = vrP0.valor
                    p0s[i - 1] = vrP0.valor
                    diasAll += diasTotal
                    tbodyP0 += "<th>"
                    tbodyP0 += "(" + diasTotal + ")"
                    tbodyP0 += "</th>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: valorTotal) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: acumuladoCrono) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: planillado) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: acumuladoPlan) + "</td>"
                    tbodyP0 += "<td class='number'>" + elm.numero(number: vrP0.valor) + "</td>"

                    if (periodosPlanilla.contains(per)) {
                        def multa = 0
                        def retraso = 0
                        if (valorTotal > planillado) {
                            def totalContrato = contrato.monto
                            def prmlMulta = contrato.multaPlanilla
                            def valorDia = valorTotal / diasTotal
                            retraso = (valorTotal - planillado) / valorDia
                            multa = (totalContrato) * (prmlMulta / 1000) * retraso
                        }
                        if (per == planilla.periodoIndices) {
                            valorTotalPeriodoActual = valorTotal
                        }
                        totalMultaRetraso += multa
                        tbodyMl += "<tr>"
                        tbodyMl += "<th>"
                        tbodyMl += per.descripcion
                        tbodyMl += "</th>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: valorTotal) + "</td>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: planillado) + "</td>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: retraso) + "</td>"
                        tbodyMl += "<td class='number'>" + elm.numero(number: multa) + "</td>"
                        tbodyMl += "</tr>"
//                        tbodyMl += "<tr>"
//                        tbodyMl += "<th>Mes y año</th>"
//                        tbodyMl += "<td>${per.descripcion}</td>"
//                        tbodyMl += "</tr>"
//                        tbodyMl += "<tr>"
//                        tbodyMl += "<th>Cronograma</th>"
//                        tbodyMl += "<td class='number'>" + elm.numero(number: valorTotal) + "</td>"
//                        tbodyMl += "</tr>"
//                        tbodyMl += "<tr>"
//                        tbodyMl += "<th>Planillado</th>"
//                        tbodyMl += "<td class='number'>" + elm.numero(number: planillado) + "</td>"
//                        tbodyMl += "</tr>"
//                        tbodyMl += "<tr>"
//                        tbodyMl += "<th>Retraso</th>"
//                        tbodyMl += "<td class='number'>" + elm.numero(number: retraso) + "</td>"
//                        tbodyMl += "</tr>"
//                        tbodyMl += "<tr>"
//                        tbodyMl += "<th>Multa</th>"
//                        tbodyMl += "<td class='number'>" + elm.numero(number: multa) + "</td>"
//                        tbodyMl += "</tr>"


                    }
                }
                tbodyP0 += "</tr>"
            }
        }
        tbodyMl += "</tbody>"
        tbodyP0 += "</tbody>"
        if (planilla.tipoPlanilla.codigo != "A") {
            tbodyP0 += "<tfoot>"
            tbodyP0 += "<tr>"
            tbodyP0 += "<th>TOTAL</th>"
            tbodyP0 += "<th>(" + diasAll + ")</th>"
            tbodyP0 += "<td></td>"
            tbodyP0 += "<td class='number bold'>" + elm.numero(number: acumuladoCrono) + "</td>"
            tbodyP0 += "<td></td>"
            tbodyP0 += "<td class='number bold'>" + elm.numero(number: acumuladoPlan) + "</td>"
            tbodyP0 += "<td></td>"
            tbodyP0 += "</tr>"
            tbodyP0 += "</tfoot>"
        }

        tablaP0 += tbodyP0
        tablaP0 += "</table>"

        def tablaFr = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;'>"
        tablaFr += '<thead>'
        tablaFr += '<tr>'
        tablaFr += '<th rowspan="2">Componentes</th>'
        tablaFr += '<th>Oferta</th>'
        tablaFr += '<th colspan="' + (periodos.size() - 1) + '">Periodo de variación y aplicación de fórmula polinómica</th>'
        tablaFr += '</tr>'
        tablaFr += '<tr>'
        tablaFr += '<th>' + fechaConFormato(fechaEntregaOferta, "MMM-yy") + '</th>'
        tablaFr += '<th>Anticipo <br/>' + fechaConFormato(fechaPlanillaAnticipo, "MMM-yy") + '</th>'
        if (periodos.size() > 2) {
            periodos.eachWithIndex { per, i ->
                if (i > 1) {
                    tablaFr += '<th rowspan="2">' + per?.descripcion + '</th>'
                }
            }
        }
        tablaFr += '</tr>'
        tablaFr += '<tr>'
        tablaFr += '<th>Anticipo</th>'
        tablaFr += '<th>'
        tablaFr += elm.numero(number: contrato.porcentajeAnticipo, decimales: 0) + "%"
        tablaFr += '</th>'
        tablaFr += '<th>Anticipo</th>'
        tablaFr += '</tr>'
        tablaFr += '</thead>'

        def a = 0, b = 0, c = 0, d = 0, tots = []
        def tbodyFr = "<tbody>"
        pcs.findAll { it.numero.contains('p') }.eachWithIndex { p, i ->
            tbodyFr += "<tr>"
            tbodyFr += "<td>" + p.indice.descripcion + " (" + p.numero + ")</td>"

            data2.p.eachWithIndex { cp, j ->
                def act = cp.value.valores.find { it.formulaPolinomica.indice == p.indice }
                if (j == 0) {
                    c = act.formulaPolinomica.valor
                    b = act.valorTabla
                    if (i == 0) {
                        b = act.valorReajuste.valor
                    }
                    tbodyFr += "<td class='number'>"
                    tbodyFr += "<div>"
                    tbodyFr += elm.numero(number: c, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "<div class='bold'>"
                    tbodyFr += elm.numero(number: b, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "</td>"
                } //j==0
                else {
//                    println i + "  -  " + act.valorReajuste.id + "  " + act.valorReajuste.valor + "    " + act.valorTabla
                    a = act.valorTabla
                    if (i == 0) {
                        a = act.valorReajuste.valor
                    }
//                    println "\t\t" + a
                    d = (a / b) * c
                    tbodyFr += "<td class='number'>"
                    tbodyFr += "<div>"
                    tbodyFr += elm.numero(number: a, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "<div class='bold'>"
                    tbodyFr += elm.numero(number: d, decimales: 3)
                    tbodyFr += "</div>"
                    tbodyFr += "</td>"
                    if (!tots[j - 1]) {
                        tots[j - 1] = [
                                per  : cp.value.periodo,
                                total: 0
                        ]
                    }
                    tots[j - 1].total += d
                }
            } //data.p.each
            tbodyFr += "</tr>"
        } //pcs.p.each

        def filaFr = "", filaFr1 = "", filaP0 = "", filaPr = ""
//        println ">>>"
//        println p0s

        def totalReajuste = 0

        tots.eachWithIndex { t, i ->
            def fpFr = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "Fr")
            def vrFr1 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(t.per, fpFr)
            if (!vrFr1) {
                vrFr1 = new ValorReajuste([
                        obra             : obra,
                        planilla         : planilla,
                        periodoIndice    : t.per,
                        formulaPolinomica: fpFr
                ])
            }
            vrFr1.valor = t.total - 1
            if (!vrFr1.save(flush: true)) {
                println "ERROR guardando Fr-1: " + vrFr1.errors
            }
            def pr = (t.total - 1).round(3) * p0s[i]

            totalReajuste += pr
            filaFr += "<td class='number'>" + elm.numero(number: t.total, decimales: 3) + "</td>"
            filaFr1 += "<td class='number'>" + elm.numero(number: t.total - 1, decimales: 3) + "</td>"
            filaP0 += "<td class='number'>" + elm.numero(number: p0s[i]) + "</td>"
            filaPr += "<td class='number'>" + elm.numero(number: pr) + "</td>"
        }

        planilla.reajuste = totalReajuste
        if (!planilla.save(flush: true)) {
            println "ERROR al guardar reajuste de la planilla " + planilla.id + " " + totalReajuste + "\n" + planilla.errors
        }

        tbodyFr += "</tbody>"
        tbodyFr += "<tfoot>"

        tbodyFr += "<tr>"
        tbodyFr += "<th rowspan='4'>1.000</th>"
        tbodyFr += "<th>F<sub>r</sub></th>"
        tbodyFr += filaFr
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th>F<sub>r</sub>-1</th>"
        tbodyFr += filaFr1
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th>P<sub>0</sub></th>"
        tbodyFr += filaP0
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th>P<sub>r</sub>-P</th>"
        tbodyFr += filaPr
        tbodyFr += "</tr>"

        tbodyFr += "<tr>"
        tbodyFr += "<th colspan='2'>REAJUSTE TOTAL</th>"
        tbodyFr += "<td colspan='${periodos.size()}' class='number bold'>"
        tbodyFr += elm.numero(number: totalReajuste)
        tbodyFr += "</td>"
        tbodyFr += "</tr>"

        tbodyFr += "</tfoot>"


        tablaFr += tbodyFr
        tablaFr += "</table>"

        def smallTableWidth = 400

        def tablaMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
        tablaMl += '<thead>'
        tablaMl += '<tr>'
        tablaMl += '<th>Mes y año</th>'
        tablaMl += '<th>Cronograma</th>'
        tablaMl += '<th>Planillado</th>'
        tablaMl += '<th>Retraso</th>'
        tablaMl += '<th>Multa</th>'
        tablaMl += '</tr>'
        tablaMl += '</thead>'

        tablaMl += tbodyMl

        tablaMl += "<tfoot>"
        tablaMl += '<th >Total</th>'
        tablaMl += '<td colspan="3"></td>'
        tablaMl += '<td class="bold number">'
        tablaMl += elm.numero(number: totalMultaRetraso)
        tablaMl += '</td>'
        tablaMl += "</tfoot>"

        tablaMl += "</table>"

        def pMl = ""

        if (planilla.periodoIndices) {
            def diasMax = 5
            def fechaFinPer = planilla.periodoIndices?.fechaFin
            def fechaMax = fechaFinPer
//            use(TimeCategory) {
//                fechaMax = fechaFinPer + diasMax.days
//            }

//            def noLaborables = ["Sat", "Sun"]
//
////            println fechaMax
//            diasMax.times {
//                fechaMax++
////                println fechaMax
//                def fmt = new java.text.SimpleDateFormat("EEE", new Locale("en"))
//                while (noLaborables.contains(fmt.format(fechaMax))) {
////                    println fmt.format(fechaMax)
//                    fechaMax++
////                    println fechaMax
//                }
//            }
//            println "***** "+fechaMax

            /* aqui esta con el nuevo service para calcular dias laborables con la tabla */
            def res = diasLaborablesService.diasLaborablesDesde(fechaFinPer, diasMax)
            if (res[0]) {
                fechaMax = res[1]
            } else {
                fechaMax = null
            }

            if (!fechaMax) {
//                redirect(action: "errores")
                def url = g.createLink(controller: "planilla", action: "list", id: contrato.id)
                def url2 = g.createLink(controller: "diaLaborable", action: "calendario", params: [anio: res[2] ?: ""])
                def link = "<a href='${url}' class='btn btn-danger'>Lista de planillas</a>"
                link += "&nbsp;&nbsp;&nbsp;"
                link += "<a href='${url2}' class='btn btn-primary'>Configurar días laborables</a>"
                flash.message = res[1]
                redirect(action: "errores", params: [link: link])
                return
            }

            def fechaPresentacion = planilla.fechaPresentacion
            def retraso = fechaPresentacion - fechaMax

            def totalMulta = 0

            def totalContrato = contrato.monto
            def prmlMulta = contrato.multaPlanilla
            if (retraso > 0) {
//            totalMulta = (totalContrato) * (prmlMulta / 1000) * retraso
                totalMulta = (valorTotalPeriodoActual) * (prmlMulta / 1000) * retraso
            } else {
                retraso = 0
            }

            pMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
            pMl += "<tr>"
            pMl += '<th class="tal">Fecha presentación planilla</th><td>' + fechaPresentacion.format("dd-MM-yyyy") + ' </td>'
            pMl += "</tr>"
            pMl += "<tr>"
            pMl += '<th class="tal">Periodo planilla</th><td>' + planilla.fechaInicio.format("dd-MM-yyyy") + " a " + planilla.fechaFin.format("dd-MM-yyyy") + ' </td>'
            pMl += "</tr>"
            pMl += "<tr>"
            pMl += '<th class="tal">Fecha máximo presentación</th> <td>' + fechaMax.format("dd-MM-yyyy") + ' </td>'
            pMl += "</tr>"
            pMl += "<tr>"
            pMl += '<th class="tal">Días de retraso</th> <td>' + retraso + "</td>"
            pMl += "</tr>"
            pMl += "<tr>"
            pMl += '<th class="tal">Multa</th> <td>' + elm.numero(number: prmlMulta) + "&#8240; de \$" + elm.numero(number: totalContrato) + "</td>"
            pMl += "</tr>"
            pMl += "<tr>"
            pMl += '<th class="tal">Total multa</th> <td>$' + elm.numero(number: totalMulta) + "</td>"
            pMl += "</tr>"
            pMl += '</table>'
        }
        return [tablaB0: tablaBo, tablaP0: tablaP0, tablaFr: tablaFr, tablaMl: tablaMl, pMl: pMl, planilla: planilla, obra: obra, oferta: oferta, contrato: contrato, errores: erroresIndice, alertas: alertasIndice]
    }

    private String formatoFecha(Date fecha, String format) {
        def meses = ["", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"]
        def str = ""
        switch (format) {
            case "MMM-yy":
                str = meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yy")
                break;
        }
        return str
    }


    def detalle() {
        def planilla = Planilla.get(params.id)
        def contrato = Contrato.get(params.contrato)
        def cmpl = Contrato.findByPadre(contrato)

        redirect (action: "detalleNuevo", params: params)

//        if(cmpl) redirect (action: "detalleNuevo", params: params)

        def obra = contrato.obra
        def detalle = [:]
        def obrasAdicionales = 1.25
        def respaldo = DocumentoProceso.findByConcursoAndDescripcionIlike(contrato.oferta.concurso, '%respaldo%adicio%')

        println "repaldo: $respaldo"
        /**
         *   máximo valor a considerar de obras adicionales desde el 20-mar-2017 --> 5%
         */
        if(contrato.fechaSubscripcion > Date.parse('dd-MM-yyy', '20-03-2017')) {
            obrasAdicionales = 1.05
        }

        def fpsp = FormulaSubpresupuesto.findAllByReajuste(planilla.formulaPolinomicaReajuste)
        def sbpr = []
        fpsp.each {
            sbpr.add(it.subPresupuesto)
        }
        println "subpresupuestos de la obra con esta FP: $sbpr"
        detalle = VolumenesObra.findAllByObraAndSubPresupuestoInList(obra, sbpr, [sort: "orden"])

        def precios = [:]

        detalle.each {
            def res
            res = preciosService.precioVlob(obra.id, it.item.id)
            precios.put(it.id.toString(), res["precio"][0])
        }

        def planillasAnteriores

        if(planilla.tipoPlanilla.codigo.trim() == "O"){
            planillasAnteriores = Planilla.findAllByContratoAndTipoPlanillaInList(contrato, TipoPlanilla.findAllByCodigoInList(['P', 'Q']))
        } else {
            planillasAnteriores = Planilla.withCriteria {
                eq("contrato", contrato)
                lt("fechaFin", planilla.fechaInicio)
            }
        }

        def editable = planilla.fechaMemoSalidaPlanilla == null && contrato.fiscalizador.id == session.usuario.id

        if(!respaldo) obrasAdicionales = 0
//        obrasAdicionales = 0

        println "adicionales: $obrasAdicionales"
        println "planillasAnteriores: ${planillasAnteriores.id}"
        return [planilla: planilla, detalle: detalle, precios: precios, obra: obra, adicionales: obrasAdicionales,
                planillasAnteriores: planillasAnteriores, contrato: contrato, editable: editable]
    }

    def detalleNuevo() {
        def planilla = Planilla.get(params.id)
        def contrato = Contrato.get(params.contrato)
        def obra = contrato.obra
        def cmpl = Contrato.findByPadre(contrato)?.monto
        cmpl = cmpl?:0
        def detalle = [:]
        def obrasAdicionales = 1.25
        def respaldo = DocumentoProceso.findByConcursoAndDescripcionIlike(contrato.oferta.concurso, '%respaldo%adicio%')

        /** máximo valor a considerar de obras adicionales desde el 20-mar-2017 --> 5% */
        if(contrato.fechaSubscripcion > Date.parse('dd-MM-yyy', '20-03-2017')) {
            obrasAdicionales = 1.05
        }

        def fpsp = FormulaSubpresupuesto.findAllByReajuste(planilla.formulaPolinomicaReajuste)
        def sbpr = []
        fpsp.each {
            sbpr.add(it.subPresupuesto)
        }
        println "subpresupuestos de la obra con esta FP: $sbpr"
//        detalle = VolumenesObra.findAllByObraAndSubPresupuestoInList(obra, sbpr, [sort: "orden"])
        detalle = VolumenContrato.findAllByContratoAndObraAndSubPresupuestoInList(contrato, obra, sbpr, [sort: "volumenOrden"])

        def precios = [:]

        detalle.each {
            def res
//            res = preciosService.precioVlob(obra.id, it.item.id)
//            precios.put(it.id.toString(), res["precio"][0])
            precios.put(it.id.toString(), it.volumenPrecio)
        }

        def planillasAnteriores

        if(planilla.tipoPlanilla.codigo == "O"){
            planillasAnteriores = Planilla.findAllByContratoAndTipoPlanillaInList(contrato, TipoPlanilla.findAllByCodigoInList(['P', 'Q']))
        } else {
            planillasAnteriores = Planilla.withCriteria {
                eq("contrato", contrato)
                lt("fechaFin", planilla.fechaInicio)
            }
        }

        def editable = planilla.fechaMemoSalidaPlanilla == null && contrato.fiscalizador.id == session.usuario.id

        if(!respaldo) obrasAdicionales = 0
//        obrasAdicionales = 0

        println "adicionales: $obrasAdicionales, complementario: $cmpl"
        return [planilla: planilla, detalle: detalle, precios: precios, obra: obra, adicionales: obrasAdicionales,
                planillasAnteriores: planillasAnteriores, contrato: contrato, editable: editable, cmpl: cmpl]
    }

    def dtEntrega() {
        def planilla = Planilla.get(params.id)
        def contrato = Contrato.get(params.contrato)
        def obra = contrato.obra
        def detalle = [:]
        def obrasAdicionales = 1.25
        def respaldo = DocumentoProceso.findByConcursoAndDescripcionIlike(contrato.oferta.concurso, '%respaldo%adicio%')

        /** máximo valor a considerar de obras adicionales desde el 20-mar-2017 --> 5% */
        if(contrato.fechaSubscripcion > Date.parse('dd-MM-yyy', '20-03-2017')) {
            obrasAdicionales = 1.05
        }

        def sbpr = []
        detalle = VolumenContrato.findAllByContratoAndObra(contrato, obra, [sort: "volumenOrden"])

        def precios = [:]

        detalle.each {
            def res
            precios.put(it.id.toString(), it.volumenPrecio)
            sbpr.add(it.subPresupuesto)
        }
        sbpr = sbpr.unique()
        println "detalle: $detalle"

        def planillasAnteriores

        planillasAnteriores = Planilla.withCriteria {
            eq("contrato", contrato)
            lt("fechaFin", planilla.fechaInicio)
        }

        println "anteriores: ${planillasAnteriores.id}"
        def editable = planilla.fechaMemoSalidaPlanilla == null && contrato.fiscalizador.id == session.usuario.id

        if(!respaldo) obrasAdicionales = 0
//        obrasAdicionales = 0

        println "adicionales: $obrasAdicionales"
        return [planilla: planilla, detalle: detalle, precios: precios, obra: obra, adicionales: obrasAdicionales,
                planillasAnteriores: planillasAnteriores, contrato: contrato, editable: editable]
    }

    private boolean updatePlanilla(planilla) {
        def detalles = DetallePlanillaCosto.findAllByPlanilla(planilla)
//        def totalMonto = detalles.size() > 0 ? detalles.sum { it.montoIva } : 0
        def totalMonto = detalles.size() > 0 ? detalles.sum { it.monto} : 0
        def totalIndi = detalles.size() > 0 ? detalles.sum { it.montoIndirectos } : 0
        def total = totalMonto + totalIndi
//        println "total monto " + total
        planilla.valor = total
        if (!planilla.save(flush: true)) {
            println "error al actualizar el valor de la planilla " + planilla.errors
            return false
        }
        return true
    }

    def addDetalleCosto() {
//        println "params detalle costo" + params
        def detalle = new DetallePlanillaCosto()
        if (params.id) {
            detalle = DetallePlanillaCosto.get(params.id)
        }
        detalle.properties = params
        if (detalle.save(flush: true)) {
            def planilla = Planilla.get(params.planilla.id)
            updatePlanilla(planilla)
            render "OK_" + detalle.id
        } else {
            println "ERROR: " + detalle.errors
            render "NO_Ha ocurrido un error al guardar el rubro"
        }
    }

    def deleteDetalleCosto() {
        def detalle = DetallePlanillaCosto.get(params.id)
        def planilla = Planilla.get(detalle.planillaId)
        detalle.delete(flush: true)
        updatePlanilla(planilla)
        render "OK"
    }

    def detalleCosto() {
        def planilla = Planilla.get(params.id)
        def contrato = Contrato.get(params.contrato)
        def obra = contrato.oferta.concurso.obra
        def editable = planilla.fechaMemoSalidaPlanilla == null
        def iva = Parametros.get(1).iva
        def dets = []
        def respaldo = DocumentoProceso.findByConcursoAndDescripcionIlike(contrato.oferta.concurso, '%respaldo%costo%')

        def detalles = DetallePlanillaCosto.findAllByPlanilla(planilla , [sort: "id", order: 'desc'])

        detalles.each { dp ->
            dets.add([
                    id             : dp.id,
                    "planilla.id"  : planilla.id,
                    factura        : dp.factura,
                    rubro          : dp.rubro,
                    "unidad.id"    : dp.unidadId,
                    unidadText     : dp.unidad.codigo,
                    cantidad       : dp.cantidad,
                    monto          : dp.monto,
                    montoIva       : dp.montoIva,
                    montoIndirectos: dp.montoIndirectos,
//                    indirectos     : dp.indirectos,
//                    total          : dp.montoIva + dp.montoIndirectos
                    total          : dp.monto + dp.montoIndirectos
            ])
        }

        def anteriores = Planilla.withCriteria {
            eq("contrato", contrato)
            eq("tipoPlanilla", TipoPlanilla.findByCodigo("C"))
            ne("id", planilla.id)
        }

        def totalAnterior = anteriores.size() > 0 ? anteriores.sum { it.valor } : 0

//        def indirectos = detalles.size() > 0 ? detalles.first().indirectos : 21
        def max = contrato.monto * 0.1

        /**
         *   máximo valor a considerar de las planillas costo + porcentaje desde el 20-mar-2017 --> 2%
         */
//        if(new Date() > Date.parse('dd-MM-yyy', '20-03-2017')) {
        if(contrato.fechaSubscripcion > Date.parse('dd-MM-yyy', '20-03-2017')) {
            max = contrato.monto * 0.02
        }

        if(!respaldo) max = 0

        max -= totalAnterior
        max = Math.round(max*100)/100

        def json = new JsonBuilder(dets)
//        println json.toPrettyString()
        println "max: $max, totalAnterior: $totalAnterior, anteriores: ${anteriores.valor} "



        return [planilla: planilla, obra: obra, contrato: contrato,
                editable: editable, detalles: json, iva: iva, detallesSize: detalles.size(), max: max]
    }



    def saveDetalle() {
//        println "saveDetalle $params"
        def pln = Planilla.get(params.id)
        def err = 0

        if (params.d.class == java.lang.String) {
            params.d = [params.d]
        }
//
        params.d.each { p ->
            def parts = p.split("_")
            if (parts.size() == 3) {
                //create
//                println "CREATE"
                def vol = VolumenesObra.get(parts[0])
                def cant = parts[1].toDouble()
                def val = parts[2].toDouble()

                def detalle = new DetallePlanilla([
                        planilla   : pln,
                        volumenObra: vol,
                        cantidad   : cant,
                        monto      : val
                ])
                if (!detalle.save(flush: true)) {
                    println "error guardando detalle (create) " + detalle.errors
                    err++
                }
            } else if (parts.size() == 4) {
                //update
//                println "UPDATE"
                def cant = parts[1].toDouble()
                def val = parts[2].toDouble()

                def detalle = DetallePlanilla.get(parts[3])
                detalle.cantidad = cant
                detalle.monto = val
                if (!detalle.save(flush: true)) {
                    println "error guardando detalle (update) " + detalle.errors
                    err++
                }
            }
        }
        if (err > 0) {
            flash.clase = "alert-error"
            flash.message = "Ocurrieron " + err + " errores"
        } else {

            def pers = PeriodoPlanilla.findAllByPlanilla(pln)
            if (pers.size() == 0) {

                pln.valor = params.total.toDouble()
                if (!pln.save(flush: true)) {
                    flash.clase = "alert-error"
                    flash.message = "Ocurrió un error al guardar la planilla"
                } else {
                    flash.clase = "alert-success"
                    flash.message = "Planilla guardada exitosamente"
                }
                redirect(controller: "planilla", action: "list", id: pln.contratoId)
                return
            } else {
                redirect(controller: "planilla2", action: "avance", id: pln.id)
                return
            }
        }
    }

    def saveDetalleNuevo() {
//        println "saveDetalleNuevo $params"
        def pln = Planilla.get(params.id)
        def err = 0

        if (params.d.class == java.lang.String) {
            params.d = [params.d]
        }

        params.d.each { p ->
            def parts = p.split("_")
            if (parts.size() == 3) {
//                def vol = VolumenesObra.get(parts[0])
                def vol = VolumenContrato.get(parts[0])
                def cant = parts[1].toDouble()
                def val = parts[2].toDouble()

                println "guarda: vocr: ${vol.id}, plnl: ${pln.id}, cant: $cant, monto: $val"
                def detalle = new DetallePlanillaEjecucion([
                        planilla   : pln,
                        volumenContrato: vol,
                        cantidad   : cant,
                        monto      : val
                ])
                println "---> ${detalle.monto}"
                if (!detalle.save(flush: true)) {
                    println "error guardando detalle (create) " + detalle.errors
                    err++
                }
            } else if (parts.size() == 4) {
                //update
                def cant = parts[1].toDouble()
                def val = parts[2].toDouble()

                def detalle = DetallePlanillaEjecucion.get(parts[3])
                detalle.cantidad = cant
                detalle.monto = val
                if (!detalle.save(flush: true)) {
                    println "error guardando detalle (update) " + detalle.errors
                    err++
                }
            }
        }
        if (err > 0) {
            flash.clase = "alert-error"
            flash.message = "Ocurrieron " + err + " errores"
        } else {

            pln.valor = params.total.toDouble()
            if (!pln.save(flush: true)) {
                flash.clase = "alert-error"
                flash.message = "Ocurrió un error al guardar la planilla"
            } else {
                flash.clase = "alert-success"
                flash.message = "Planilla guardada exitosamente"
            }
            redirect(controller: "planilla", action: "list", id: pln.contratoId)
            return
        }
    }

    def errores() {
        return [params: params]
    }

    def letras() {
        def nn = new Random(777)
        def nx = 0.0
        100.times {
            nx = nn.nextInt(50005)/101
            println NumberToLetterConverter.convertNumberToLetter(numero(nx, 2).replaceAll(',','').toDouble()) +
                    " valor original : $nx"
        }
    }

    def procesarLq() {
        println "Inicio de procesar planilla Lq, params: $params"
        //TODO: hacer una funcion para que aplique descuentos a las planillas
        def plnl = Planilla.get(params.id)
        def conReajuste = plnl.contrato.aplicaReajuste == 1

        println "---tipo planilla: ${plnl.tipoPlanilla.toString()}"
        if(plnl.tipoPlanilla.codigo == 'O') {
            procesaAdicionales(plnl)
            flash.message = "Procesa obras adicionales"
            detalleReajuste(params.id) /** inserta valores del detalle del reajuste --> dtrj **/
        }
        else {
            if(plnl.tipoPlanilla.toString() == 'R') {  // reliquidación de la obra
                if (!plnl.fechaFin) {
                    def lq = Planilla.findByContratoAndTipoPlanilla(plnl.contrato, TipoPlanilla.findByCodigo('Q'))
                    plnl.fechaFin = lq.fechaFin
                    plnl.save(flush: true)
                }
            }
            procesaReajusteLq(params.id) /** inserta valores de reajuste --> rjpl **/
            println "1.completa procesaReajuste"
            detalleReajuste(params.id) /** inserta valores del detalle del reajuste --> dtrj **/

            if(Planilla.get(params.id).tipoPlanilla.codigo in ['P', 'Q']){
                println "---multas con reajuste ${plnl.contrato.aplicaReajuste}, --> $conReajuste"
                if(conReajuste) {
                    procesaMultas(params.id)  /* multas */
                } else {
                    procesaMultasSinRj(params.id)  /* multas --actualmeente es lo mismo que multas 18-ene-2019 */
                }

            }

            if(plnl.tipoPlanilla.toString() in ['P']) {
                // el Po puede superar el valor del anticipo.
//                println "calcula descuentos planilla P"




                def totDsct = Planilla.executeQuery("select sum(descuentos) from Planilla where contrato = :c and id <> :p" +
                        " and fechaPresentacion < :f and tipoContrato = :t",
                        [c: plnl.contrato, p: plnl.id, f: plnl.fechaPresentacion, t: plnl.tipoContrato])

//                def dsct   = Math.round(plnl.valor*(1 - plnl.contrato.porcentajeAnticipo/100)*100)/100

                /* aplica el descuento del anticipo */
                def dsct = 0
                def cmpl = Contrato.findByPadre(plnl.contrato)
                if(cmpl && plnl.tipoContrato == 'C') {
                    dsct   = Math.round(plnl.valor*(cmpl.porcentajeAnticipo/100)*100)/100
                } else {
                    dsct   = Math.round(plnl.valor*(plnl.contrato.porcentajeAnticipo/100)*100)/100
                }

//                dsct   = Math.round(plnl.valor*(plnl.contrato.porcentajeAnticipo/100)*100)/100
                def resto  = Math.round((plnl.contrato.anticipo - totDsct[0])*100)/100
                println "totDsct[0]: ${totDsct[0]}, resto: ${resto}, dsct: $dsct"

                if(dsct > resto) {
                    plnl.descuentos = resto
                } else if(plnl.tipoPlanilla.codigo == 'Q') {
                    plnl.descuentos = resto
                } else {
                    plnl.descuentos = dsct
                }
                plnl.save(flush: true)
            }

            if(plnl.tipoPlanilla.toString() == 'Q') {  //se aplica el total del desceunto al anticipo
                if (!plnl.contrato.fechaPedidoRecepcionContratista || !plnl.contrato.fechaPedidoRecepcionFiscalizador) {
                    flash.message = "Por favor ingrese las fechas de pedido de recepción para procesar la planilla de liquidación"
                    flash.clase = "alert-error"
//                    redirect(controller: "contrato", action: "fechasPedidoRecepcion", id: plnl.contrato.id)
                    render "fechas"
                    return
                }

                def anteriores = Planilla.findAllByContratoAndTipoContratoAndTipoPlanilla(plnl.contrato,
                        plnl.tipoContrato, TipoPlanilla.findByCodigo('P')).sum{it.descuentos}

                println "anteriores: $anteriores, ${plnl.contrato.anticipo}, C: ${Contrato.findByPadre(plnl.contrato)?.anticipo?:0} "
                if(anteriores == null) anteriores = 0
                if(plnl.tipoContrato == 'P'){
                    plnl.descuentos = plnl.contrato.anticipo - anteriores
                } else {
                    def dsct = (Contrato.findByPadre(plnl.contrato)?.anticipo?:0) - anteriores
                    println "Aplica dscta compl: ${Contrato.findByPadre(plnl.contrato)?.anticipo?:0} - ${anteriores} : $dsct"
                    plnl.descuentos = dsct
                }
                plnl.save(flush: true)
            }



            if(plnl.tipoPlanilla.toString() == 'L') {  //se aplica el total del desceunto al anticipo
                if (!Acta.findByContrato(plnl.contrato)) {
                    flash.message = "No se ingresado el acta de entrega recepción de esta obra, por lo que no procede " +
                            "la lanilla de liquidación del reajuuste"
                    flash.clase = "alert-error"
                    return
                }
            }
//          println "2.completa procesaReajuste"
        }
        poneTotalReajuste(plnl) // actualiza en plnlrjst el valor a reconocer de reajuste de esta planilla: actual - diferencias de anteriores
        render "ok" //debe retornar a planillas y habilitar botón de resumen.
    }

    def procEntrega() {
        println "Inicio de procesar contra entrega, params: $params"
        //TODO: hacer una funcion para que aplique descuentos a las planillas
        def plnl = Planilla.get(params.id)

        if(plnl.tipoPlanilla.codigo == 'E') {
            multasEntrega(params.id)
            flash.message = "Procesa planilla contra entrega"
//            detalleReajuste(params.id) /** inserta valores del detalle del reajuste --> dtrj **/
        }
/*
        else {
            if(plnl.tipoPlanilla.toString() == 'R') {  // reliquidación de la obra
                if (!plnl.fechaFin) {
                    def lq = Planilla.findByContratoAndTipoPlanilla(plnl.contrato, TipoPlanilla.findByCodigo('Q'))
                    plnl.fechaFin = lq.fechaFin
                    plnl.save(flush: true)
                }
            }
            procesaReajusteLq(params.id) */
        /** inserta valores de reajuste --> rjpl **//*

            detalleReajuste(params.id) */
        /** inserta valores del detalle del reajuste --> dtrj **//*


            if(Planilla.get(params.id).tipoPlanilla.codigo in ['P', 'Q']){
                procesaMultas(params.id)  */
        /* multas *//*

    }

    if(plnl.tipoPlanilla.toString() in ['P']) {
        def totDsct = Planilla.executeQuery("select sum(descuentos) from Planilla where contrato = :c and id <> :p" +
                " and fechaPresentacion < :f and tipoContrato = :t",
                [c: plnl.contrato, p: plnl.id, f: plnl.fechaPresentacion, t: plnl.tipoContrato])

        def dsct   = Math.round(plnl.valor*(1 - plnl.contrato.porcentajeAnticipo/100)*100)/100
        def resto  = Math.round((plnl.contrato.anticipo - totDsct[0])*100)/100

        println "totDsct[0]: ${totDsct[0]}, resto: ${resto}"

        if(dsct > resto) {
            plnl.descuentos = resto
        } else if(plnl.tipoPlanilla.codigo == 'Q') {
            plnl.descuentos = resto
        } else {
            plnl.descuentos = dsct
        }
        plnl.save(flush: true)
    }
}
*/
//        poneTotalReajuste(plnl) // actualiza en plnlrjst el valor a reconocer de reajuste de esta planilla: actual - diferencias de anteriores
        render "ok" //debe retornar a planillas y habilitar botón de resumen.
    }


    /** retorna el id del periodoInec mas reciente respecto del parámetro fcha
     * debe haber todos los indices de todas las fórmulas del contrato
     * si tp = 'R' se trata de reajuste a la fecha de pago o presentación, si no
     * están disponibles los ínidices se retorna null **/
    def indicesDisponiblesAnticipo(plnl, fcha, tp) {
        //* todo: verficar que todos los ínidices de todas las FPRJ del contrato
        println "indicesDisponiblesAnticipo para fecha: ${fcha}, con tp: $tp"
        def existe = false
        def prin
        prin = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(fcha, fcha)
        if (!tp){   /** se refiere al primer pago **/
            println "periodo de indices para fcha: ${prin}"
            if(prin){
                existe = preciosService.verificaIndicesPeriodoTodo(plnl.contrato.id, prin.id).size() == 0
            }
            if(existe) {
//                println "si existe el prin: ${plnl.periodoIndices}"
                return prin
            } else {
                def max = 10
                def fecha = fcha
                while(!existe){
//                    println "periodo actual...: $prin, fcha: ${fcha}, fecha: ${fecha}"
                    fecha = preciosService.primerDiaDelMes(fecha) - 15
                    prin = PeriodosInec.findByFechaInicioLessThanAndFechaFinGreaterThan(fecha, fecha)
                    existe = preciosService.verificaIndicesPeriodoTodo(plnl.contrato.id, prin.id).size() == 0
                    if(!max--){
                        return null
                    }
                }
                return prin
            }

        } else if(tp == 'R') {
            // si no hay ídices a la fecha de pago se retorma los más recientes
            if(prin){
                existe = preciosService.verificaIndicesPeriodoTodo(plnl.contrato.id, prin.id).size() == 0
            }
            if(existe) {
//                println "si existe el prin: ${plnl.periodoIndices}"
                return prin
            } else {
                def max = 10
                def fecha = fcha
                while(!existe){
                    println "periodo actual para recalculo de reajuste...: $prin, fcha: ${fcha}, fecha: ${fecha}"
                    fecha = preciosService.primerDiaDelMes(fecha) - 15
                    prin = PeriodosInec.findByFechaInicioLessThanAndFechaFinGreaterThan(fecha, fecha)
                    existe = preciosService.verificaIndicesPeriodoTodo(plnl.contrato.id, prin.id).size() == 0
                    if(!max--){
                        return null
                    }
                }
                return prin
            }
//            return preciosService.verificaIndicesPeriodoTodo(plnl.contrato.id, prin.id).size() == 0 ? prin : null
        }
    }



    /** retorna el id del periodoInec mas reciente respecto del parámetro fcha
     * si tp = 'R' se trata de reajuste a la fecha de pago o presentación, si no
     * están disponibles los ínidices se retorna null **/
    def indicesDisponibles(plnl, fcha, tp) {
//        println "indicesDisponibles para plnl: ${plnl.id} fecha: ${fcha}, con tp: $tp"
        def existe = false
        def prin
        prin = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(fcha, fcha)
        if (!tp){   /** se refiere al primer pago **/
//            println "periodo de indices para fcha: ${prin}"
            if(prin){
//                existe = preciosService.verificaIndicesPeriodo(plnl.contrato, prin).size() == 0
                existe = preciosService.verificaIndicesPeriodo(plnl.id, prin).size() == 0
            }
            if(existe) {
//                println "si existe el prin: ${plnl.periodoIndices}"
                return prin
            } else {
                def max = 10
                def fecha = fcha
                while(!existe){
//                    println "periodo actual...: $prin, fcha: ${fcha}, fecha: ${fecha}"
                    fecha = preciosService.primerDiaDelMes(fecha) - 15
                    prin = PeriodosInec.findByFechaInicioLessThanAndFechaFinGreaterThan(fecha, fecha)
//                    existe = preciosService.verificaIndicesPeriodo(plnl.contrato, prin).size() == 0
                    existe = preciosService.verificaIndicesPeriodo(plnl.id, prin).size() == 0
                    if(!max--){
                        return null
                    }
                }
                return prin
            }

        } else if(tp == 'R') {
//            println "-------------RRR Recalcula periodo de reajuste para plnl: ${plnl.id}, $plnl"
//            println "Recalcula periodo de reajuste para plnl avance: ${plnl}, tipo ${plnl.tipoPlanilla.codigo}"
//            println "--va a verificaIndicesPeriodo con: plnl: $fcha --> prin: $prin"
            return preciosService.verificaIndicesPeriodo(plnl.id, prin).size() == 0 ? prin : null
        }
    }

    def insertaRjpl(prmt) {
        def rjpl = new ReajustePlanilla()
        println "inserta reajuste planilla : ${prmt}"
        def rjpl_an = ReajustePlanilla.findByPlanillaAndPlanillaReajustadaAndPeriodoAndFpReajuste(prmt.planilla,
                prmt.planillaReajustada, prmt.periodo, prmt.fpReajuste)
        if (rjpl_an) {
            rjpl = ReajustePlanilla.get(rjpl_an.id)
            rjpl.periodoInec = prmt.periodoInec
            rjpl.parcialCronograma = prmt.parcialCronograma
            rjpl.acumuladoCronograma = prmt.acumuladoCronograma
            rjpl.parcialPlanillas = prmt.parcialPlanillas
            rjpl.acumuladoPlanillas = prmt.acumuladoPlanillas
            rjpl.valorPo = prmt.valorPo
            rjpl.periodo = prmt.periodo
            rjpl.mes = prmt.mes
            rjpl.fechaInicio = prmt.fechaInicio
            rjpl.fechaFin = prmt.fechaFin
            rjpl.fpReajuste = prmt.fpReajuste


//            println "actualiza valores de: $prmt"
        } else {
            rjpl.planilla = prmt.planilla
            rjpl.planillaReajustada = prmt.planillaReajustada
            rjpl.periodoInec = prmt.periodoInec

            rjpl.parcialCronograma = prmt.parcialCronograma
            rjpl.acumuladoCronograma = prmt.acumuladoCronograma
            rjpl.parcialPlanillas = prmt.parcialPlanillas
            rjpl.acumuladoPlanillas = prmt.acumuladoPlanillas
            rjpl.valorPo = prmt.valorPo
            rjpl.periodo = prmt.periodo
            rjpl.mes = prmt.mes
            rjpl.fechaInicio = prmt.fechaInicio
            rjpl.fechaFin = prmt.fechaFin
            rjpl.fpReajuste = prmt.fpReajuste

//            println "inserta valores de: $prmt"
        }
        if (rjpl.save([flush: true])) {
            flash.clase = "alert-success"
            flash.message = "Reajuste guardado exitosamente."
            return 1
        } else {
            flash.clase = "alert-error"
            flash.message = "Ha ocurrido un error al guardar reajuste."
            println "errores insertaRjpl con prmt: $prmt \n" + rjpl.errors
            return 0
        }
    }


    /** calcula rejuste de anticipo: usa todas las FPRJ y prorratea de acuerdo a FPSP
     * se crea un registro en RJPL para cada FPRJ que exista en el contrato.
     * - inserta índices en dtrj
     * - calcula valores respecto de cntr.prin y se inserta en dtrj para cada rjpl
     * - calcular Bo, Fr y Fr-1:
     *   - insertar en dtrj
     *   - actualizar rjpl.rjplvlor --valor reajuste
     *   - actualizar rjpl.rjplfcha --fecha de cálculo
     *   - actualizar rjpl.rjplfctr --factor -- ya está en dtrj */

    def detalleReajusteAnticipo(id) {
        println "inicia detalleReajuste Anticipo"
        def prmt = [:]
        def plnl = Planilla.get(id)
        def tpfp = TipoFormulaPolinomica.findByCodigo('C')  /* FP contractual */
        def frpl
        def inof = 0.0
        def inpr = 0.0
        def vlof = 0.0
        def vlpr = 0.0
        def valor = 0.0
        def valorBoOf = 0.0
        def valorBoPr = 0.0
        def valorFr = 0.0

        def rjpl = ReajustePlanilla.findAllByPlanilla(plnl, [sort: 'planillaReajustada'])

        rjpl.each { rj ->
//            println "Bo: Reajuste de planilla ${plnl.id}, reajustando: ${rjpl.planillaReajustada.id}"
            frpl = FormulaPolinomicaContractual.findAllByContratoAndTipoFormulaPolinomicaAndNumeroIlikeAndReajuste(plnl.contrato,
                    tpfp, 'c%', plnl.formulaPolinomicaReajuste,  [sort: 'numero'])
            valor = 0.0
            valorBoOf = 0.0
            valorBoPr = 0.0

            frpl.each {fp ->
                /** calcula valores para halla Bo **/
                inof = valorIndice(fp.indice , plnl.contrato.periodoInec)
                inpr = valorIndice(fp.indice , rj.periodoInec)
                vlof = Math.round(inof * fp.valor *1000)/1000
                vlpr = Math.round(inpr * fp.valor *1000)/1000
                valorBoOf += vlof
                valorBoPr += vlpr
//                println "${fp} coef: ${fp.valor} oferta: $vlof, actual: $vlpr, factor: $valor"

                /* valore a almacenar */
                prmt = [:]
                prmt.fpContractual = fp
                prmt.reajustePlanilla = rj
                prmt.indiceOferta = inof
                prmt.indicePeriodo = inpr
                prmt.valorIndcOfrt = vlof
                prmt.valorIndcPrdo = vlpr
                prmt.valor = valor
                insertaDtrj(prmt)
            }

//            println "Fr: valor BoOf: $valorBoOf, periodo: $valorBoPr"
            frpl = FormulaPolinomicaContractual.findAllByContratoAndTipoFormulaPolinomicaAndNumeroLikeAndReajuste(plnl.contrato,
                    tpfp, 'p%', plnl.formulaPolinomicaReajuste, [sort: 'numero'])
//            println "lista: $frpl"
/*
            def indc = frpl.iterator()
            while (indc.hasNext()) {
                if (indc.next().numero == 'p01') {
                    indc.remove()
                    break
                }
            }
            println "lista: $frpl sin p01"
*/
            valorFr = 0.0
            frpl.each {fp ->
                if(fp.numero == 'p01'){
                    inof = valorBoOf
                    inpr = valorBoPr
                } else {
                    inof = valorIndice(fp.indice , plnl.contrato.periodoInec)
                    inpr = valorIndice(fp.indice , rj.periodoInec)
                }
                vlof = Math.round(inof * fp.valor *1000)/1000
                vlpr = Math.round(inpr * fp.valor *1000)/1000
                valor = Math.round(vlpr / vlof * fp.valor * 1000)/1000
                valorFr += valor
//                println "${fp} coef: ${fp.valor} oferta: $vlof, actual: $vlpr, factor: $valor"

                prmt = [:]
                prmt.fpContractual = fp
                prmt.reajustePlanilla = rj
                prmt.indiceOferta = inof
                prmt.indicePeriodo = inpr
                prmt.valorIndcOfrt = vlof
                prmt.valorIndcPrdo = vlpr
                prmt.valor = valor
                insertaDtrj(prmt)
            }
//            println "valores BoOf: $valorBoOf, periodo: $valorBoPr, Fr: $valorFr"
            /** calcular valores de reajuste y actualizar rjpl **/
//            rj.valor  = PlanillaPo.findByPlanillaAndPeriodo()
            rj.factor = valorFr
            rj.valorReajustado = Math.round((valorFr * rj.valorPo - rj.valorPo)*100)/100
            rj.fechaReajuste = new Date()
            rj.save(flush: true)
        }

    }



    /** para cada Po:
     * - inserta índices en dtrj
     * - calcula valores respecto de cntr.prin y se inserta en dtrj para cada rjpl
     * - calcular Bo, Fr y Fr-1:
     *   - insertar en dtrj
     *   - actualizar rjpl.rjplvlor --valor reajuste
     *   - actualizar rjpl.rjplfcha --fecha de cálculo
     *   - actualizar rjpl.rjplfctr --factor -- ya está en dtrj */

    def detalleReajuste(id) {
        println "inicia detalleReajuste.... para $id"
        def prmt = [:]
        def plnl = Planilla.get(id)
        def frpl
        def inof = 0.0
        def inpr = 0.0
        def vlof = 0.0
        def vlpr = 0.0
        def valor = 0.0
        def valorBoOf = 0.0
        def valorBoPr = 0.0
        def valorFr = 0.0

        def rjpl = ReajustePlanilla.findAllByPlanilla(plnl, [sort: 'planillaReajustada'])
        println "rjpl para plnl__id = ${plnl.id}: $rjpl"
        rjpl.each { rj ->
//            println "Bo: Reajuste de planilla ${plnl.id}, reajustando: ${rjpl.planillaReajustada.id}"

            frpl = FormulaPolinomicaContractual.findAllByContratoAndNumeroIlikeAndReajuste(plnl.contrato,
                    'c%', rj.fpReajuste,  [sort: 'numero'])
            valor = 0.0
            valorBoOf = 0.0
            valorBoPr = 0.0

            frpl.each {fp ->
                /** calcula valores para halla Bo **/
//                println "indice: ${fp.indice.id}, periodo: ${plnl.contrato.periodoInec.id}"
                if(plnl.tipoContrato == 'C') {
                    def comp = Contrato.findByPadre(plnl.contrato)
                    inof = valorIndice(fp.indice , comp.periodoInec)
//                    println "contrato cmpl: ${comp.codigo}, perdiodo: ${comp.periodoInec.id}:${comp.periodoInec.descripcion}"
                } else {
                    inof = valorIndice(fp.indice , plnl.contrato.periodoInec)
                }

                inpr = valorIndice(fp.indice , rj.periodoInec)
                vlof = Math.round(inof * fp.valor *1000)/1000
                vlpr = Math.round(inpr * fp.valor *1000)/1000
                valorBoOf += vlof
                valorBoPr += vlpr
//                println "${fp} coef: ${fp.valor} oferta: $vlof, actual: $vlpr, factor: $valor"

                /* valore a almacenar */
                prmt = [:]
                prmt.fpContractual = fp
                prmt.reajustePlanilla = rj
                prmt.indiceOferta = inof
                prmt.indicePeriodo = inpr
                prmt.valorIndcOfrt = vlof
                prmt.valorIndcPrdo = vlpr
                prmt.valor = valor
                insertaDtrj(prmt)
            }

//            println "Fr: valor BoOf: $valorBoOf, periodo: $valorBoPr"
            frpl = FormulaPolinomicaContractual.findAllByContratoAndNumeroLikeAndReajuste(plnl.contrato,
                    'p%', rj.fpReajuste, [sort: 'numero'])

            valorFr = 0.0
            frpl.each {fp ->
                if(fp.numero == 'p01'){
                    inof = valorBoOf
                    inpr = valorBoPr
                } else {
                    if(plnl.tipoContrato == 'C') {
                        def comp = Contrato.findByPadre(plnl.contrato)
                        inof = valorIndice(fp.indice , comp.periodoInec)
                    } else {
                        inof = valorIndice(fp.indice , plnl.contrato.periodoInec)
                    }
                    inpr = valorIndice(fp.indice , rj.periodoInec)
                }

                vlof = Math.round(inof * fp.valor *1000)/1000
                vlpr = Math.round(inpr * fp.valor *1000)/1000
                valor = Math.round(vlpr / vlof * fp.valor * 1000)/1000
                valorFr += valor

//                println "${fp} coef: ${fp.valor} oferta: $vlof, actual: $vlpr, factor: $valor, Fr: $valorFr"

                prmt = [:]
                prmt.fpContractual = fp
                prmt.reajustePlanilla = rj
                prmt.indiceOferta = inof
                prmt.indicePeriodo = inpr
                prmt.valorIndcOfrt = vlof
                prmt.valorIndcPrdo = vlpr
                prmt.valor = valor
                insertaDtrj(prmt)
            }
//            println "valores BoOf: $valorBoOf, periodo: $valorBoPr, Fr: $valorFr"
            /** calcular valores de reajuste y actualizar rjpl **/
//            rj.valor  = PlanillaPo.findByPlanillaAndPeriodo()
            rj.factor = valorFr
            rj.valorReajustado = Math.round((valorFr * rj.valorPo - rj.valorPo)*100)/100
            rj.fechaReajuste = new Date()
            rj.save(flush: true)
        }

    }

    def insertaDtrj(prmt) {
        def dtrj = new DetalleReajuste()
//        println "  reajuste planilla : ${prmt}"
        def dtrj_an = DetalleReajuste.findByFpContractualAndReajustePlanilla(prmt.fpContractual, prmt.reajustePlanilla)
        if (dtrj_an) {
            dtrj = DetalleReajuste.get(dtrj_an.id)
            dtrj.indiceOferta = prmt.indiceOferta
            dtrj.indicePeriodo = prmt.indicePeriodo
            dtrj.valorIndcOfrt = prmt.valorIndcOfrt
            dtrj.valorIndcPrdo = prmt.valorIndcPrdo
            dtrj.valor = prmt.valor
//            println "actualiza valores de: $prmt"
        } else {
            dtrj.fpContractual = prmt.fpContractual
            dtrj.reajustePlanilla = prmt.reajustePlanilla
            dtrj.indiceOferta = prmt.indiceOferta
            dtrj.indicePeriodo = prmt.indicePeriodo
            dtrj.valorIndcOfrt = prmt.valorIndcOfrt
            dtrj.valorIndcPrdo = prmt.valorIndcPrdo
            dtrj.valor = prmt.valor
//            println "inserta valores de: $prmt"
        }
        if (dtrj.save([flush: true])) {
            flash.clase = "alert-success"
            flash.message = "Detalle de Reajuste guardado exitosamente."
            return 1
        } else {
            flash.clase = "alert-error"
            flash.message = "Ha ocurrido un error al guardar detalle de reajuste."
            println "errores: " + dtrj.errors
            return 0
        }
    }

    /** halla el valor del índice en PRIN de la oferta**/
    def valorIndice(indc, prin) {
//        println "valor Indice de: $indc : ${indc.id} periodo: $prin"
        ValorIndice.findByIndiceAndPeriodo(indc, prin).valor
    }

    /** calcula multas se aplica sólo a planillas de avance **/
    def procesaMultas(id){
        println "procesaMultas"
        def plnl = Planilla.get(id)
        def cmpl = Contrato.findByPadre(plnl.contrato)
        def diasMax = 5
        def fechaFinPer = plnl.fechaFin
        def fechaPresentacion = plnl.fechaPresentacion
        def fechaMax = fechaFinPer
        def retraso = 0
        def multaPlanilla = 0.0
        def prmt = [:]
        def dias = 0

        /*** No presentación de planilla */
        def fcfm = preciosService.ultimoDiaDelMes(plnl.fechaFin)
        def anio =  new Date().format("yyyy").toInteger()
        anio -= 1
        def diciembre31 = new Date().parse("dd-MM-yyyy", "31-12-" + anio)
//        println "fcfm: $fcfm, diciembre31: $diciembre31"

        if(plnl.tipoPlanilla.codigo == 'Q') {
            fcfm = plnl.fechaFin
        } else {
            fcfm = preciosService.ultimoDiaDelMes(plnl.fechaFin)
        }

        if(fcfm == diciembre31) {
            fcfm++
            diasMax--
        }
        def res = diasLaborablesService.diasLaborablesDesde(fcfm, diasMax)
        println "No presentación de planilla --> fcfm: $fcfm, multas: $res, diasMax: ${diasMax}"
        /* si hay error, res[0] = false */
        if (!res[0]) {
            errorDiasLaborables(plnl.contrato.id, res[2], res[1])
        } else {
            fechaMax = res[1]
        }
//        println "fechaPresentacion: $fechaPresentacion, fechaMax: $fechaMax "

        res = diasLaborablesService.diasLaborablesEntre(fechaPresentacion, fechaMax)
        if (!res[0]) {
            errorDiasLaborables(plnl.contrato.id, res[2], res[1])
        } else {
            retraso = res[1]
        }

        if (fechaPresentacion < fechaMax) {
            retraso *= -1
        }

        if (retraso > 0 || plnl.valor == 0) {
            retraso = 1
        } else {
            retraso = 0
        }

        /** manejar datos de:  * tipo de multa, * dias de retraso, * multa descripción y * valor de la multa */
        def rjpl = ReajustePlanilla.findAllByPlanillaAndPlanillaReajustada(plnl, plnl, [sort: 'periodo'])
        println "rjpl ---> ${rjpl.size()}, plnlrjst: ${plnl.id}"

        def baseMulta = 0
        def formatoNum = new DecimalFormat("#,###.##")

        def rj_cr = ReajustePlanilla.executeQuery("select sum(valorReajustado) from ReajustePlanilla where planilla = :p", [p: plnl])
        def acCronograma = rj_cr[0] > 0 ? rj_cr[0] : 0

        /********* retraso en presentación de la planilla *************/
        baseMulta = plnl.valor > 0 ? plnl.valor : rjpl.last().parcialCronograma
        multaPlanilla = Math.round((plnl.contrato.multaPlanilla / 1000) * (baseMulta)*retraso*100)/100
        prmt = [:]
        prmt.planilla = plnl
        prmt.tipoMulta = TipoMulta.get(1) ///// 1 retraso en presentación
        prmt.descripcion = "${plnl.contrato.multaPlanilla} x 1000 de ${formatoNum.format(baseMulta)}"
        if(rjpl) {
            prmt.valorCronograma = rjpl.last().parcialCronograma
        } else {
            prmt.valorCronograma = 0
        }
        prmt.dias = retraso
        prmt.monto = multaPlanilla
        prmt.monto = multaPlanilla
        prmt.fechaMaxima = fechaMax
        insertaMulta(prmt)

        /********  multa por incumplimiento cronograma -- no se aplica a Liquidación "Q" **********/
        /* se cambia el criterio del 80% por el 100% de ejecutado */
        if(plnl.tipoPlanilla.codigo == 'P'){
//            multaPlanilla = Math.round(((rjpl.last().acumuladoPlanillas / rjpl.last().acumuladoCronograma < 0.80) ? plnl.contrato.monto / 1000 : 0) * 100) / 100
            dias = 0
            println "dias_eje: ${plnl.fechaFin} - ${plnl.contrato.obra.fechaInicio}"
            def dias_eje = plnl.fechaFin - plnl.contrato.obra.fechaInicio + 1
            dias = (rjpl.last().acumuladoCronograma - rjpl.last().acumuladoPlanillas) / ( (rjpl.last().acumuladoCronograma)/dias_eje )
            dias = Math.round(dias)
            dias = (dias > 0) ? dias : 0
            println "dias_eje: $dias_eje dias: $dias"
            multaPlanilla = Math.round(((rjpl.last().acumuladoPlanillas < rjpl.last().acumuladoCronograma) ?
                    (plnl.contrato.monto - rjpl.last().acumuladoPlanillas) / 1000 * dias : 0) * 100) / 100
            prmt = [:]
            prmt.planilla = plnl
            prmt.tipoMulta = TipoMulta.get(2) ////// 2 multa por incumplimiento cronograma
            if(plnl.tipoContrato == 'P') {
                prmt.descripcion = "${plnl.contrato.multaIncumplimiento} x 1000 x ${dias} de " +
                        "${formatoNum.format(plnl.contrato.monto - rjpl.last().acumuladoPlanillas)}"
            } else {
                prmt.descripcion = "${cmpl.multaIncumplimiento} x 1000 de ${formatoNum.format(cmpl.monto)}"
            }
            prmt.valorCronograma = rjpl.last().parcialCronograma
            prmt.monto = multaPlanilla
            prmt.periodo = "${rjpl.last().mes}"
            prmt.dias = dias
            insertaMulta(prmt)
        }

        /********* multa por no acatar disposiciones del fiscalizador  **********/
        multaPlanilla = Math.round(plnl.contrato.monto * plnl.diasMultaDisposiciones * (plnl.contrato.multaDisposiciones / 1000)*100)/100
        prmt = [:]
        prmt.planilla = plnl
        prmt.tipoMulta = TipoMulta.get(3) //// 3 multa por por no aactar disposiciones del fiscalizador
        if(plnl.tipoContrato == 'P') {
            prmt.descripcion = "${plnl.contrato.multaDisposiciones} x 1000 de ${formatoNum.format(plnl.contrato.monto.toDouble())} por día"
        } else {
            prmt.descripcion = "${cmpl.multaDisposiciones} x 1000 de ${formatoNum.format(cmpl.monto.toDouble())} por día"
        }

        prmt.dias = plnl.diasMultaDisposiciones
        prmt.monto = multaPlanilla
        insertaMulta(prmt)


        /** multa por retraso de obra: fechaFinObra - FechaCronograma * valor de multa **/
        if(plnl.tipoPlanilla.codigo == 'Q'){
            def valor = ReajustePlanilla.executeQuery("select sum(valorReajustado) from ReajustePlanilla where planilla = :p", [p: plnl])
            def reajustado = valor[0] > 0 ? valor[0] : 0
            def total = plnl.contrato.monto.toDouble()
            if(plnl.contrato.conReajuste) {
                total += reajustado
            }

            def tpml = TipoMulta.get(4)  /** 4 retraso de obra **/

            def cn = dbConnectionService.getConnection()
//            def sql = "select sum(cast(to_char(prejfcfn - prejfcin, 'dd') as integer) + 1) dias from prej where cntr__id = ${plnl.contrato.id} and prejtipo = 'P'"
            def sql = "select sum((prejfcfn - prejfcin) + 1) dias from prej where cntr__id = ${plnl.contrato.id} and prejtipo in ('P', 'C')"
            println "sql: $sql"
            dias  = (int) cn.rows(sql.toString())[0].dias
            sql = "select sum(mdcedias) dias from mdce where cntr__id = ${plnl.contrato.id} and mdcetipo = 'A'"
            def ampliacion = (int) cn.rows(sql.toString())[0].dias?:0

            println "...dias: $dias, ampliacion: $ampliacion, plazo: ${plnl.contrato.plazo}"

            dias -= plnl.contrato.plazo + ampliacion

//            sql = "select cast(to_char(plnlfcfn - max(prejfcfn), 'dd') as integer) cntd from plnl, prej " +
//                    "where plnl__id = ${plnl.id} and plnl.cntr__id = prej.cntr__id group by plnlfcfn"
            sql = "select plnlfcfn - max(prejfcfn) cntd from plnl, prej " +
                    "where plnl__id = ${plnl.id} and plnl.cntr__id = prej.cntr__id group by plnlfcfn"

            println "retraso sql: $sql"
            def retrasoObra = cn.rows(sql.toString())[0].cntd

            dias += retrasoObra

            println "retraso de obra: ${dias} dias"

//            dias = 4
            if(dias > 0) {
                prmt = [:]

                if(plnl.tipoContrato == 'P') {
                    multaPlanilla = Math.round(dias * (plnl.contrato.multaRetraso * total / 1000)*100)/100
                    prmt.descripcion = "${plnl.contrato.multaRetraso} x 1000 de ${formatoNum.format(total)} por día"
                } else {
                    def cntrCmpl = Contrato.findByPadre(plnl.contrato)
                    multaPlanilla = Math.round(dias * (cntrCmpl?.multaRetraso?:0) * cntrCmpl.monto / 1000*100)/100
                    prmt.descripcion = "${cntrCmpl.multaRetraso} x 1000 de ${formatoNum.format(cntrCmpl.monto)} por día"
                }
                prmt.planilla = plnl
                prmt.tipoMulta = tpml

                prmt.dias = dias
                prmt.monto = multaPlanilla
                insertaMulta(prmt)
            } else {
                MultasPlanilla.findAllByPlanillaAndTipoMulta(plnl, tpml).each {
                    it.delete()
                }
            }
        }
    }

    def procesaMultasSinRj(id){
        def plnl = Planilla.get(id)
        def cmpl = Contrato.findByPadre(plnl.contrato)
        def diasMax = 5
        def fechaFinPer = plnl.fechaFin
        def fechaPresentacion = plnl.fechaPresentacion
        def fechaMax = fechaFinPer
        def retraso = 0
        def multaPlanilla = 0.0
        def prmt = [:]
        def dias = 0

        /*** No presentación de planilla */
        def fcfm = preciosService.ultimoDiaDelMes(plnl.fechaFin)
        def anio =  new Date().format("yyyy").toInteger()
        anio -= 1
        def diciembre31 = new Date().parse("dd-MM-yyyy", "31-12-" + anio)
//        println "fcfm: $fcfm, diciembre31: $diciembre31"
        if(fcfm == diciembre31) {
            fcfm++
            diasMax--
        }
        def res = diasLaborablesService.diasLaborablesDesde(fcfm, diasMax)
//        println "No presentación de planilla --> fcfm: $fcfm, multas: $res"
        /* si hay error, res[0] = false */
        if (!res[0]) {
            errorDiasLaborables(plnl.contrato.id, res[2], res[1])
        } else {
            fechaMax = res[1]
        }
//        println "fechaPresentacion: $fechaPresentacion, fechaMax: $fechaMax "

        res = diasLaborablesService.diasLaborablesEntre(fechaPresentacion, fechaMax)
        if (!res[0]) {
            errorDiasLaborables(plnl.contrato.id, res[2], res[1])
        } else {
            retraso = res[1]
        }

        if (fechaPresentacion < fechaMax) {
            retraso *= -1
        }

        if (retraso > 0 || plnl.valor == 0) {
            retraso = 1
        } else {
            retraso = 0
        }

        /** manejar datos de:  * tipo de multa, * dias de retraso, * multa descripción y * valor de la multa */
        def rjpl = ReajustePlanilla.findAllByPlanillaAndPlanillaReajustada(plnl, plnl, [sort: 'periodo'])
//        println "rjpl --- multas: ${rjpl.size()}"

        def baseMulta = 0
        def formatoNum = new DecimalFormat("#,###.##")

        def rj_cr = ReajustePlanilla.executeQuery("select sum(valorReajustado) from ReajustePlanilla where planilla = :p", [p: plnl])
        def acCronograma = rj_cr[0] > 0 ? rj_cr[0] : 0

        /********* retraso en presentación de la planilla *************/
        baseMulta = plnl.valor > 0 ? plnl.valor : rjpl.last().parcialCronograma
        multaPlanilla = Math.round((plnl.contrato.multaPlanilla / 1000) * (baseMulta)*retraso*100)/100
        prmt = [:]
        prmt.planilla = plnl
        prmt.tipoMulta = TipoMulta.get(1) ///// 1 retraso en presentación
        prmt.descripcion = "${plnl.contrato.multaPlanilla} x 1000 de ${formatoNum.format(baseMulta)}"
        if(rjpl) {
            prmt.valorCronograma = rjpl.last().parcialCronograma
        } else {
            prmt.valorCronograma = 0
        }

        prmt.dias = retraso
        prmt.monto = multaPlanilla
        prmt.monto = multaPlanilla
        prmt.fechaMaxima = fechaMax
        insertaMulta(prmt)

        /********  multa por incumplimiento cronograma -- no se aplica a Liquidación "Q" **********/
        if(plnl.tipoPlanilla.codigo == 'P'){
            multaPlanilla = Math.round(((rjpl.last().acumuladoPlanillas / rjpl.last().acumuladoCronograma < 0.80) ? plnl.contrato.monto / 1000 : 0) * 100) / 100
            dias = 0
            dias = (plnl?.fechaFin - plnl?.fechaInicio) - plnl.valor / rjpl.last().parcialCronograma * (plnl?.fechaFin - plnl?.fechaInicio)
            dias = (dias > 0) ? dias : 0
            prmt = [:]
            prmt.planilla = plnl
            prmt.tipoMulta = TipoMulta.get(2) ////// 2 multa por incumplimiento cronograma
            if(plnl.tipoContrato == 'P') {
                prmt.descripcion = "${plnl.contrato.multaIncumplimiento} x 1000 de ${formatoNum.format(plnl.contrato.monto)}"
            } else {
                prmt.descripcion = "${cmpl.multaIncumplimiento} x 1000 de ${formatoNum.format(cmpl.monto)}"
            }
            prmt.valorCronograma = rjpl.last().parcialCronograma
            prmt.monto = multaPlanilla
            prmt.periodo = "${rjpl.last().mes}"
            insertaMulta(prmt)
        }

        /********* multa por no acatar disposiciones del fiscalizador  **********/
        multaPlanilla = Math.round(plnl.contrato.monto * plnl.diasMultaDisposiciones * (plnl.contrato.multaDisposiciones / 1000)*100)/100
        prmt = [:]
        prmt.planilla = plnl
        prmt.tipoMulta = TipoMulta.get(3) //// 3 multa por por no aactar disposiciones del fiscalizador
        if(plnl.tipoContrato == 'P') {
            prmt.descripcion = "${plnl.contrato.multaDisposiciones} x 1000 de ${formatoNum.format(plnl.contrato.monto.toDouble())} por día"
        } else {
            prmt.descripcion = "${cmpl.multaDisposiciones} x 1000 de ${formatoNum.format(cmpl.monto.toDouble())} por día"
        }

        prmt.dias = plnl.diasMultaDisposiciones
        prmt.monto = multaPlanilla
        insertaMulta(prmt)


        /** multa por retraso de obra: fechaFinObra - FechaCronograma * valor de multa **/
        if(plnl.tipoPlanilla.codigo == 'Q'){
            def valor = ReajustePlanilla.executeQuery("select sum(valorReajustado) from ReajustePlanilla where planilla = :p", [p: plnl])
            def reajustado = valor[0] > 0 ? valor[0] : 0
            def total = plnl.contrato.monto.toDouble() + reajustado
            def tpml = TipoMulta.get(4)  /** 4 retraso de obra **/

            def cn = dbConnectionService.getConnection()
//            def sql = "select sum(cast(to_char(prejfcfn - prejfcin, 'dd') as integer) + 1) dias from prej where cntr__id = ${plnl.contrato.id} and prejtipo = 'P'"
            def sql = "select sum((prejfcfn - prejfcin) + 1) dias from prej where cntr__id = ${plnl.contrato.id} and prejtipo in ('P', 'C')"
            println "sql: $sql"
            dias  = (int) cn.rows(sql.toString())[0].dias
            sql = "select sum(mdcedias) dias from mdce where cntr__id = ${plnl.contrato.id} and mdcetipo = 'A'"
            def ampliacion = (int) cn.rows(sql.toString())[0].dias?:0

            println "...dias: $dias, ampliacion: $ampliacion, plazo: ${plnl.contrato.plazo}"

            dias -= plnl.contrato.plazo + ampliacion

//            sql = "select cast(to_char(plnlfcfn - max(prejfcfn), 'dd') as integer) cntd from plnl, prej " +
//                    "where plnl__id = ${plnl.id} and plnl.cntr__id = prej.cntr__id group by plnlfcfn"
            sql = "select plnlfcfn - max(prejfcfn) cntd from plnl, prej " +
                    "where plnl__id = ${plnl.id} and plnl.cntr__id = prej.cntr__id group by plnlfcfn"

            println "retraso sql: $sql"
            def retrasoObra = cn.rows(sql.toString())[0].cntd

            dias += retrasoObra

            println "retraso de obra: ${dias} dias"

//            dias = 4
            if(dias > 0) {
                prmt = [:]

                if(plnl.tipoContrato == 'P') {
                    multaPlanilla = Math.round(dias * (plnl.contrato.multaRetraso * total / 1000)*100)/100
                    prmt.descripcion = "${plnl.contrato.multaRetraso} x 1000 de ${formatoNum.format(total)} por día"
                } else {
                    def cntrCmpl = Contrato.findByPadre(plnl.contrato)
                    multaPlanilla = Math.round(dias * (cntrCmpl?.multaRetraso?:0) * cntrCmpl.monto / 1000*100)/100
                    prmt.descripcion = "${cntrCmpl.multaRetraso} x 1000 de ${formatoNum.format(cntrCmpl.monto)} por día"
                }
                prmt.planilla = plnl
                prmt.tipoMulta = tpml

                prmt.dias = dias
                prmt.monto = multaPlanilla
                insertaMulta(prmt)
            } else {
                MultasPlanilla.findAllByPlanillaAndTipoMulta(plnl, tpml).each {
                    it.delete()
                }
            }
        }
    }

    /** calcula multas se aplica sólo a planillas de avance **/
    def multasEntrega(id){
        def plnl = Planilla.get(id)
        def cmpl = Contrato.findByPadre(plnl.contrato)
        def retraso = 0
        def multaPlanilla = 0.0
        def prmt = [:]
        def dias = 0
        def formatoNum = new DecimalFormat("#,###.##")

        /********* multa por no acatar disposiciones del fiscalizador  **********/
        multaPlanilla = Math.round(plnl.contrato.monto * plnl.diasMultaDisposiciones * (plnl.contrato.multaDisposiciones / 1000)*100)/100
        prmt = [:]
        prmt.planilla = plnl
        prmt.tipoMulta = TipoMulta.get(3) //// 3 multa por por no aactar disposiciones del fiscalizador
        prmt.descripcion = "${plnl.contrato.multaDisposiciones} x 1000 de ${formatoNum.format(plnl.contrato.monto.toDouble())} por día"
        prmt.dias = plnl.diasMultaDisposiciones
        prmt.monto = multaPlanilla
        insertaMulta(prmt)
    }

    def errorDiasLaborables(cntr, anio, mnsj){
        /** muestra el rror de no definidos diaas laborables **/
        def url = g.createLink(controller: "planilla", action: "list", id: cntr.id)
        def url2 = g.createLink(controller: "diaLaborable", action: "calendario", params: [anio: anio ?: ""])
        def link = "<a href='${url}' class='btn btn-danger'>Lista de planillas</a>"
        link += "&nbsp;&nbsp;&nbsp;"
        link += "<a href='${url2}' class='btn btn-primary'>Configurar días laborables</a>"
        flash.message = mnsj
        redirect(action: "errores", params: [link: link])
        return
    }

    def insertaMulta(prmt) {
        def mlpl = new MultasPlanilla()
//        println "inserta multas de la planilla : ${prmt}"
        def mlpl_an = MultasPlanilla.findByPlanillaAndTipoMulta(prmt.planilla, prmt.tipoMulta)
        if (mlpl_an) {
            mlpl = MultasPlanilla.get(mlpl_an.id)
            mlpl.descripcion = prmt.descripcion?:""
            mlpl.valorCronograma = prmt.valorCronograma?:0
            mlpl.dias = prmt.dias?:0
            mlpl.monto = prmt.monto
            mlpl.fechaMaxima = prmt.fechaMaxima?:null
            mlpl.periodo = prmt.periodo?:""
//            println "actualiza valores de: $prmt"
        } else {
            mlpl.planilla = prmt.planilla
            mlpl.tipoMulta = prmt.tipoMulta
            mlpl.descripcion = prmt.descripcion?:""
            mlpl.valorCronograma = prmt.valorCronograma?: 0
            mlpl.dias = prmt.dias?: 0
            mlpl.monto = prmt.monto
            mlpl.fechaMaxima = prmt.fechaMaxima?:null
            mlpl.periodo = prmt.periodo?:""
//            println "inserta valores de: $prmt"
        }
        if (mlpl.save([flush: true])) {
            flash.clase = "alert-success"
            flash.message = "Reajuste guardado exitosamente."
            return 1
        } else {
            flash.clase = "alert-error"
            flash.message = "Ha ocurrido un error al guardar reajuste."
            println "errores: " + mlpl.errors
            return 0
        }
    }

    def procesaReajusteLq(id) {
//        println "inicia procesaReajuste...."
        def prmt = [:]
        def plnl = Planilla.get(id)
        def cntr = plnl.contrato
        def prdoInec
        def dsct = 1 - cntr.porcentajeAnticipo/100

        def prdo = 0
        def plParaPo = 0.0
        def plAcumulado = 0.0
        def parcial = 0.0
        def totalCr = 0.0
        def aDescontar = 0.0
        def ttDescontar = 0.0
        def hayAnteriores = false
        def fcha

//        println "procesa Reajuste: planilla ${plnl.id}, tipo: ${plnl.tipoPlanilla}"
        if(plnl.tipoPlanilla.codigo == 'A') {
            /** no hay planillas a recalcular reajuste **/
            prmt = [:]
            prmt.planilla = plnl
            prmt.planillaReajustada = plnl
            fcha = plnl.periodoIndices? plnl.periodoIndices.fechaInicio : plnl.fechaIngreso
//            println "es anticipo -- $fcha"
            def prin = indicesDisponiblesAnticipo(plnl, fcha, null)
            /** para cada FPRJ crea un registro en RJPL **/
            FormulaPolinomicaReajuste.findAllByContrato(plnl.contrato).each {
//                println "++++indice disponible: $prin, fprj: ${it.id}"
                prmt.periodoInec = prin
                prmt.parcialCronograma = 0
                prmt.acumuladoCronograma = 0
                prmt.parcialPlanillas = 0
                prmt.acumuladoPlanillas = 0
                prmt.valorPo = prorrateaPo(it.id, cntr, plnl.valor)   //calcula el total sbpr que apica a esta FP
                prmt.mes = preciosService.componeMes(plnl.fechaIngreso.format('MMM-yyyy'))
                prmt.periodo = 0
                prmt.periodo = 0
                prmt.fpReajuste = it
                insertaRjpl(prmt)
            }

        } else if(plnl.tipoPlanilla.codigo == 'B') { /** planillas de avance y liquidacion**/
            prmt = [:]
            prmt.planilla = plnl
            prmt.planillaReajustada = plnl
            fcha = plnl.periodoIndices? plnl.periodoIndices.fechaInicio : plnl.fechaIngreso
            def prin = indicesDisponiblesAnticipo(plnl, fcha, null)
            def comp = Contrato.findByPadre(plnl.contrato)
            prmt.periodoInec = prin
            prmt.parcialCronograma = 0
            prmt.acumuladoCronograma = 0
            prmt.parcialPlanillas = 0
            prmt.acumuladoPlanillas = 0
            prmt.valorPo = comp.anticipo
            prmt.mes = preciosService.componeMes(plnl.fechaIngreso.format('MMM-yyyy'))
            prmt.periodo = 0
            prmt.periodo = 0
            prmt.fpReajuste = plnl.formulaPolinomicaReajuste
            insertaRjpl(prmt)

        } else if(plnl.tipoPlanilla.toString() in ['P', 'Q', 'R']) { /** planillas de avance y liquidacion**/
            def listPl = ['A', 'B']
            if(plnl.tipoPlanilla.toString() == 'R') listPl.add('Q')
//            println "----------planillas anteriores----------... ${listPl}"

            def pl = Planilla.findAllByContratoAndTipoPlanillaInListAndFechaPresentacionLessThanAndTipoContrato(plnl.contrato,
                    TipoPlanilla.findAllByCodigoInList(listPl), plnl.fechaPresentacion, plnl.tipoContrato,
                    [sort: 'fechaPresentacion'])
            println "planillas AB por procesar: ${pl.size()}: ${pl.id}"
            println "${plnl.contrato}, ${TipoPlanilla.findAllByCodigoInList(listPl)}, ${plnl.fechaPresentacion}, ${plnl.tipoContrato}"
            pl.each { p ->   /** las planillas anteriores solo son A o B **/
                println "procesa planilla ${p.id} tipo: ${p.tipoPlanilla}"
                prmt = [:]
                if(p.tipoPlanilla.toString() in ['A', 'B']){
                    fcha = p.periodoIndices? p.periodoIndices.fechaInicio : plnl.fechaIngreso
                    if(p.fechaPago) {
                        prdoInec = indicesDisponiblesAnticipo(p, p.fechaPago, 'R') // para recalcular reajuste
                        prmt.periodoInec = prdoInec ?: indicesDisponibles(p, fcha, '')
                        prmt.mes = preciosService.componeMes(p.fechaPago.format('MMM-yyyy'))
                    } else {
                        prmt.periodoInec = indicesDisponiblesAnticipo(p, fcha, null)
                        prmt.mes = preciosService.componeMes(p.fechaIngreso.format('MMM-yyyy'))
                    }
//                    println "hay que recalcular reajuste de plnl: ${p.id}, tipo: ${p.tipoPlanilla}, pagada: ${p?.fechaPago}, --> $prdoInec"

                    prmt.periodoInec = prdoInec ?: indicesDisponibles(p, fcha, '')
                    prmt.planilla = plnl
                    prmt.planillaReajustada = p
                    prmt.parcialCronograma = 0
                    prmt.acumuladoCronograma = 0
                    prmt.parcialPlanillas = 0
                    prmt.acumuladoPlanillas = 0
                    prmt.valorPo = p.valor
                    prmt.periodo = 0
                    prmt.fpReajuste = p.formulaPolinomicaReajuste
                    insertaRjpl(prmt)
                }
            } // fin recálculo de reajuste de A y B

            listPl = ['P', 'Q']
            pl = Planilla.findAllByContratoAndTipoPlanillaInListAndFechaPresentacionLessThanAndTipoContrato(plnl.contrato,
                    TipoPlanilla.findAllByCodigoInList(listPl), plnl.fechaPresentacion, plnl.tipoContrato, [sort: 'fechaPresentacion'])
            println "planillas P por procesar: ${pl.size()}: ${pl.id}"
            pl.each { p ->   /** las planillas anteriores de avance P o Q **/
                if((p == pl.last())) {  // reajusta sólo planillas de avance
                    /** pone Po en base a lo recalculado de la planilla anterior **/
                    if (p.fechaInicio < plnl.fechaInicio) {    //verifica que es de un periodo anterior
                        prdo++
//                        println "anterior: ${p.id}, periodo: $prdo"
                        def poAnteriores = ReajustePlanilla.findAllByPlanillaAndPeriodoGreaterThan(p, 0, [sort: 'periodo'])
//                        println "valores de Po anteriores--: ${poAnteriores.valorPo}"

                        /* procesa planillas anteriores con los mismos valores de rjpl ya alamcenados **/
                        poAnteriores.each { po ->
                            prmt = [:]
                            prmt.planilla = plnl
                            prmt.planillaReajustada = po.planillaReajustada
                            prmt.parcialCronograma = po.parcialCronograma
                            prmt.acumuladoCronograma = po.acumuladoCronograma

                            if(po.acumuladoCronograma > po.acumuladoPlanillas) {
                                if((po.acumuladoCronograma - po.acumuladoPlanillas)*dsct <= (plnl.valor - plParaPo)) {
                                    prmt.valorPo = Math.round(po.parcialCronograma * dsct * 100)/100
                                    plParaPo += prmt.valorPo - po.valorPo
                                } else {
                                    prmt.valorPo = po.valorPo  //queda igual
                                }
                            } else { // esta adelantado en el cronograma queda igual
                                prmt.valorPo = po.valorPo
                            }

                            prmt.parcialPlanillas = po.parcialPlanillas      //igual
                            prmt.acumuladoPlanillas = po.acumuladoPlanillas  //igual
                            prmt.periodo = po.periodo                        //igual
                            prmt.mes = po.mes                                //igual
                            prmt.fechaInicio = po.fechaInicio                //igual
                            prmt.fechaFin = po.fechaFin                      //igual
                            prmt.fpReajuste = po.fpReajuste

//                            println "ecalcula reajuste de plnl avance: ${po.planillaReajustada.id}, tipo: ${po.planillaReajustada.tipoPlanilla}, Po: $po.valorPo"

                            /**para recalcular el prin se requiere la fecha a la que corresponde el reajuste po.fechaInicio, po.fechaFin ok **/
                            prdoInec = indicesDisponibles(po.planillaReajustada, po.planillaReajustada.fechaPago, 'R') /* para recalcular reajuste */
//                            println "********** para plnl: ${po.id} con pr: ${po.periodoInec} se retorna de indicesDisponibles: $prdoInec, fcha: $po.fechaInicio "
                            prmt.periodoInec = prdoInec?: indicesDisponibles(po.planillaReajustada, po.fechaInicio, '')

//                            println "  inserta avance RR... si hay indices actuales $prmt"
                            insertaRjpl(prmt)
                            prdo = po.periodo
                            plAcumulado = po.acumuladoPlanillas
                            totalCr = po.acumuladoCronograma
                        }
                    }
                }
            } //fin planillas anteriores

            /** -------------------------------------- planilla actual --------------------------------------- **/
            /* si la planilla se refiere a mas de un periodo planillado, por ejemplo al inicio o en la liquidacion:
               1. se debe insertar en RJPL un registro por cada periodo
             */

            def fcfm
            def pems
            def diasEsteMes = 0
            def esteMes = 0.0
            def restoMes = 0.0
            def planilladoEsteMes = 0.0
            def diasPlanillados = preciosService.diasPlanillados(plnl.id)
            def fchaFinPlanillado = plnl.fechaInicio
            def total = 0.0
            def totalCmpl = 0.0
            if(plnl.tipoPlanilla.codigo == 'R') {
                prdo = ReajustePlanilla.executeQuery("select max(periodo) from ReajustePlanilla where planilla = :c", [c: pl?.last()])[0]
                println "Planilla de Reliquidación, periodo: $prdo"
            }
            println "dias planillados: $diasPlanillados, fchaFinPlanillado: $fchaFinPlanillado, periodo: $prdo, plnl: ${plnl.id}"
            println "antes del while total: $totalCr"
            total = totalCr
            while(fchaFinPlanillado < plnl.fechaFin){
                prdo++
                if(pl) println "fchaFinPlanillado: ${fchaFinPlanillado.format('yyyy-MMM-dd')} periodo: $prdo, pl: ${pl?.last()?.id}"
                else println "fchaFinPlanillado: ${fchaFinPlanillado.format('yyyy-MMM-dd')} periodo: $prdo"

                fcfm = preciosService.ultimoDiaDelMes(fchaFinPlanillado)

                println "(0)fecha fin Planillado: $fchaFinPlanillado, fchaFin: ${plnl.fechaFin}, fcfm: $fcfm"
                if(plnl.fechaFin > fcfm)   /*** la planilla sobrepasa el mes: tiene dos o mas Po  **/
                {
                    diasEsteMes = preciosService.diasEsteMes(plnl.contrato.id, fchaFinPlanillado.format('yyyy-MM-dd'), fcfm.format('yyyy-MM-dd'))
                    println "dias este mes: ${fchaFinPlanillado.format('yyyy-MMM-dd')}  dias: ${diasEsteMes}"
                    esteMes = Math.round(plnl.valor * diasEsteMes / diasPlanillados * 100) / 100
                    plAcumulado += esteMes
                    planilladoEsteMes += esteMes

                    println "(1)fecha fin Planillado: $fchaFinPlanillado, esteMes: $esteMes, fcfm: $fcfm"

                    pems = PeriodoEjecucion.findAllByContratoAndFechaInicioGreaterThanEqualsAndFechaFinLessThanEqualsAndTipoInList(plnl.contrato,
                            fchaFinPlanillado, fcfm, ['P', 'A', 'C'])
                    parcial = 0.0
                    totalCmpl = 0.0
                    pems.each {ms ->
                        if(plnl.tipoContrato == 'P') {
                            parcial += ms.parcialContrato
                            total += ms.parcialContrato
                        } else {
                            parcial += ms.parcialCmpl
                            total += ms.parcialCmpl
                        }
                    }
//                    println "**-- fin Planillado: $fchaFinPlanillado, esteMes: $esteMes, plAcumulado: $plAcumulado, cr: $parcial -- $total"
                    /** manejo especial de la planilla 217 no reajusto en todos los periodos borrar y dejar solo el ELSE **/
                    if(plnl.id == 217){
                        registraRjpl(prdo, esteMes, plAcumulado, plnl.contrato, plnl, fchaFinPlanillado, fcfm, parcial, total, true)
                        fchaFinPlanillado = plnl.fechaFin
                    } else {
                        registraRjpl(prdo, esteMes, plAcumulado, plnl.contrato, plnl, fchaFinPlanillado, fcfm, parcial, total, false)
                        fchaFinPlanillado =  preciosService.sumaUnDia(fcfm)
                    }
                } else {  // se crea el último periodo en rjpl
                    println "------------ fechaFin: ${plnl.fechaFin} > finMes: ${fcfm}, fchaFinPlanillado $fchaFinPlanillado"
                    diasEsteMes = preciosService.diasEsteMes(plnl.contrato.id, fchaFinPlanillado.format('yyyy-MM-dd'), plnl.fechaFin.format('yyyy-MM-dd'))
                    esteMes = plnl.valor - planilladoEsteMes
                    plAcumulado += esteMes

                    if(plnl.tipoPlanilla.codigo in ['Q', 'R']){
                        pems = PeriodoEjecucion.findAllByContratoAndFechaInicioGreaterThanEqualsAndTipoInList(plnl.contrato, plnl.fechaInicio, ['P', 'A', 'C'])
                        println "------ pems: ${pems}"
                    } else if(plnl.tipoPlanilla.codigo == 'P') {
                        println "ejecuta con: ${plnl.contrato.id}, $fchaFinPlanillado, ${plnl.fechaFin}"
                        pems = PeriodoEjecucion.findAllByContratoAndFechaInicioGreaterThanEqualsAndFechaFinLessThanEqualsAndTipoInList(plnl.contrato,
                                fchaFinPlanillado, plnl.fechaFin, ['P', 'A', 'C'])
                    }

                    parcial = 0.0

//                    total = totalCr /* revisar TODO */
//                    if((totalCmpl > 0) && pems[0].fechaFin < fchaFinPlanillado) total += totalCmpl
//                    println "---- pems: ${pems.parcialContrato}"
                    pems.each { ms ->
//                        println "ms.parcialCronograma ${ms.parcialCmpl}, ${ms.fechaFin} >= ${fchaFinPlanillado}"
                        if(ms.fechaFin >= fchaFinPlanillado) {
                            if(plnl.tipoContrato == 'P') {
                                parcial += ms.parcialContrato
                                total += ms.parcialContrato
                            } else {
                                parcial += ms.parcialCmpl
                                total += ms.parcialCmpl
                            }
                        } //else parcial = 0
//                        println ".....total: $total"
                    }

                    println "**fecha fin Planillado: $fchaFinPlanillado, esteMes: $esteMes, plAcumulado: $plAcumulado, cr: $parcial -- $total, prdo: $prdo"
                    registraRjpl(prdo, esteMes, plAcumulado, plnl.contrato, plnl, fchaFinPlanillado, plnl.fechaFin, parcial, total, true)

                    fchaFinPlanillado = plnl.fechaFin
                }
            }
        }

        if(plnl.tipoPlanilla.toString() in ['L']) { /** planillas de liquidacion del reajuste **/
            println "----porcesa planillas anteriores----------..."
            def pl = Planilla.findAllByContratoAndTipoPlanillaInListAndFechaPresentacionLessThan(plnl.contrato,
                    TipoPlanilla.findAllByCodigoInList(['A', 'P', 'Q']), plnl.fechaPresentacion, [sort: 'fechaPresentacion'])
            pl.each { p ->   /** las planillas anteriores solo son A o P **/
                println "procesa planilla ${p.id} tipo: ${p.tipoPlanilla}"
                prmt = [:]
                if(p.tipoPlanilla.toString() == 'A'){
                    fcha = p.periodoIndices? p.periodoIndices.fechaInicio : plnl.fechaIngreso
                    prdoInec = indicesDisponiblesAnticipo(p, p.fechaPago, 'R') // para recalcular reajuste
                    println "P_Q:+++ existe indices para prdo: $prdoInec"
                    FormulaPolinomicaReajuste.findAllByContrato(p.contrato).each {
                        /** debe reajustar con índices de la fecha de pago si ya esá apgada y si existen los índices **/
                        println "hay que recalcular reajuste de plnl: ${p.id}, tipo: ${p.tipoPlanilla}, pagada: ${p.fechaPago}"
                        prmt.periodoInec = prdoInec ?: indicesDisponibles(p, fcha, '')
                        prmt.planilla = plnl
                        prmt.planillaReajustada = p
                        prmt.parcialCronograma = 0
                        prmt.acumuladoCronograma = 0
                        prmt.parcialPlanillas = 0
                        prmt.acumuladoPlanillas = 0
                        prmt.valorPo = p.valor
                        prmt.periodo = 0
                        prmt.mes = preciosService.componeMes(p.fechaPago.format('MMM-yyyy'))
//                    prmt.fpReajuste = pl.formulaPolinomicaReajuste
                        prmt.fpReajuste = it
//                        println "  inserta ... rjpl: ${it.id}, prmt: $prmt"
                        insertaRjpl(prmt)
//                        println "inserta ok..."
                    }

//                } else if((p.tipoPlanilla.toString() == 'P') && (p == pl.last())) {  // reajusta sólo planillas de avance
                } else if((p.tipoPlanilla.toString() in ['P', 'Q'])) {
                    /** recalcula Po en base a lo recalculado de la planilla anterior **/
                    println "planilla de avance anterior -------------------- ${p.id}"
//                    plAcumulado += p.valor
                    /** para cada planilla anterior se debe saber el Po aplicado antes para ingresar la diferencia de aplicarse **/
                    if (p.fechaInicio < plnl.fechaInicio) {    //verifica que es de un periodo anterior
                        prdo++
//                        println "anterior: ${p.id}, periodo: $prdo"
                        def poAnteriores = ReajustePlanilla.findAllByPlanillaAndPeriodoGreaterThan(p, 0, [sort: 'periodo'])
//                        println "valores de Po anteriores--: ${poAnteriores}"

                        /* procesa planillas anteriores con los mismos valores de rjpl ya alamcenados **/
                        poAnteriores.each { po ->
                            prmt = [:]
                            prmt.planilla = plnl
                            prmt.planillaReajustada = po.planillaReajustada
                            prmt.parcialCronograma = po.parcialCronograma
                            prmt.acumuladoCronograma = po.acumuladoCronograma

                            // si acPlanillas > acCronograma => Po queda igual, sino, Po = parcialCr*% si alcanza
//                            println "++++++++acCr: $po.acumuladoCronograma, acPl: $po.acumuladoPlanillas, valor: ${plnl.valor}, dsct: $dsct"
                            if(po.acumuladoCronograma > po.acumuladoPlanillas) {
                                if((po.acumuladoCronograma - po.acumuladoPlanillas)*dsct <= (plnl.valor - plParaPo)) {
//                                    println "pone el nuevo Po a: ${Math.round(po.parcialCronograma * dsct * 100)/100}"
                                    prmt.valorPo = Math.round(po.parcialCronograma * dsct * 100)/100
                                    plParaPo += prmt.valorPo - po.valorPo
                                } else {
                                    prmt.valorPo = po.valorPo  //queda igual
                                }
                            } else { // esta adelantado en el cronograma queda igual
                                prmt.valorPo = po.valorPo
                            }

                            prmt.parcialPlanillas = po.parcialPlanillas      //igual
                            prmt.acumuladoPlanillas = po.acumuladoPlanillas  //igual
                            prmt.periodo = po.periodo                        //igual
                            prmt.mes = po.mes                                //igual
                            prmt.fechaInicio = po.fechaInicio                //igual
                            prmt.fechaFin = po.fechaFin                      //igual
                            prmt.fpReajuste = po.fpReajuste

//                            println "ecalcula reajuste de plnl avance: ${po.planillaReajustada.id}, tipo: ${po.planillaReajustada.tipoPlanilla}, Po: $po.valorPo"

                            /**para recalcular el prin se requiere la fecha a la que corresponde el reajuste po.fechaInicio, po.fechaFin ok **/
                            prdoInec = indicesDisponibles(po.planillaReajustada, po.planillaReajustada.fechaPago, 'R') /* para recalcular reajuste */
//                            println "****L***** para plnl: ${po.id} con pr: ${po.periodoInec} se retorna de indicesDisponibles: $prdoInec, fcha: $po.fechaInicio "
                            println "**** plnl: ${po.planillaReajustada.id} fecha de pago: ${po.planillaReajustada.fechaPago} --> $prdoInec"
                            prmt.periodoInec = prdoInec?: indicesDisponibles(po.planillaReajustada, po.fechaInicio, '')

//                            println "  inserta avance RR... si hay indices actuales $prmt"
                            insertaRjpl(prmt)
                            prdo = po.periodo
                            plAcumulado = po.acumuladoPlanillas
                            totalCr = po.acumuladoCronograma
                        }
                    }
                }
//                println "planilla++: ${p.id} tipo: ${p.tipoPlanilla}"
            } //fin planillas anteriores

        }
    }




    def calculaPo(id, vlor, plFinal, prdo) { /** calcula Po para cada periodo -- uno o varios meses **/
        /** hasta el valor contratado al Po se le debe descontar la proporción del anticipo, si ya se supera este
         ** valor el valor, el Po es el valorplanillado completo                                                  */
        println "calculaPo recibe: id: $id, vlor: $vlor, plFinal: $plFinal"
        def plnl = Planilla.get(id)
        def valorPo = 0.0

        if(plnl.tipoPlanilla.codigo == 'R') {
            return plnl.valor
        }

        def cntr = plnl.contrato
        def cmpl = Contrato.findByPadre(cntr)
        def estePo = 0
        if(plnl.tipoContrato == 'C'){
            estePo = Math.round(vlor*(1 - cmpl.porcentajeAnticipo/100)*100)/100
        } else {
            estePo = Math.round(vlor*(1 - cntr.porcentajeAnticipo/100)*100)/100
        }

        def totPo = ReajustePlanilla.executeQuery("select sum(valorPo) from ReajustePlanilla where planilla = :p and " +
                "planillaReajustada <> :p and periodo > 0", [p: plnl])[0]?:0
        def totPoAc = ReajustePlanilla.executeQuery("select sum(valorPo) from ReajustePlanilla where planilla = :p and " +
                "planillaReajustada = :p and periodo < :pr ", [p: plnl, pr: prdo])[0]?:0

//        println ".....1"
        def totPlnl = ReajustePlanilla.executeQuery("select max(acumuladoPlanillas) from ReajustePlanilla where planilla = :p and " +
                "planillaReajustada = :p", [p: plnl])[0]?:0
        def anteriores = ReajustePlanilla.executeQuery("select max(acumuladoPlanillas) from ReajustePlanilla " +
                "where planilla = :p and planillaReajustada != :p", [p: plnl])[0]?:0
        if(!anteriores) {
            totPlnl = plnl.valor  /** no hay planilla anteriores y puede ser liquidación **/
        }
        def totPlnlCmpl = ReajustePlanilla.executeQuery("select max(acumuladoPlanillas) from ReajustePlanilla where planilla = :p and " +
                "planillaReajustada = :p", [p: plnl])[0]?:0
        println ".....2 $totPlnl"

//        def resto  = Math.round((plnl.contrato.anticipo - totPo - totPoAc)*100)/100
        /* el resto de Po debería ser respecto del monto del contrato - lo aplicado en Po */
        def resto  = Math.round((plnl.contrato.monto - totPo - totPoAc)*100)/100

        if(plnl.tipoContrato == 'C'){
            resto = Math.round((cmpl.monto - cmpl.anticipo - totPo - totPoAc)*100)/100
        }

//        println "------------resto: $resto, monto: ${plnl.contrato.monto} - totPo: $totPo - totPoAc: $totPoAc"
        if (resto < 0) resto = 0  // ya nop se aplica deducción de anticipo


        println "totalPo --> totPlnl: $totPlnl, vlor: $vlor, anterior: ${totPo}, actual: ${totPoAc}, resto: $resto, estePo: $estePo"

        if((estePo > resto) && (plnl.tipoPlanilla.codigo != 'Q')) {
            valorPo = resto   //nunca existe
        } else if((plnl.tipoPlanilla.codigo == 'Q') && (plFinal || (plnl.valor == vlor))) {
            if(plnl.tipoContrato == 'P') {
                println "---------+++++++++++--------- $totPlnl, ${cntr.anticipo}, $totPo, $totPoAc"
                valorPo = totPlnl - cntr.anticipo - totPo - totPoAc
//                valorPo = estePo
            } else {
                valorPo = totPlnl - cmpl.anticipo - totPo - totPoAc
            }
//            valorPo = cntr.anticipo - resto
        } else {
            println "------- $estePo"
            valorPo = estePo
        }
        println "valor de Po a aplicar: $valorPo"
        valorPo
    }


    def procesaAdicionales(plnl) {
//        println "inicia procesa adicionales...."
        def prmt = [:]
        def prdoInec

        def prdo = 0
        def plAcumulado = 0.0
        def parcial = 0.0
        def total = 0.0
        def aDescontar = 0.0
        def ttDescontar = 0.0
//        def hayAnteriores = false

//        println "----------planillas anteriores----Adicionales------"
        /* los valores de Po son los mismos de la planilla anterior: la de liquidación */
        def pl = Planilla.findByContratoAndTipoPlanilla(plnl.contrato, TipoPlanilla.findAllByCodigo('Q'))
//        println "planilla de liquidación: $pl.id"
        def poAnteriores = ReajustePlanilla.findAllByPlanilla(pl, [sort: 'periodo'])

        poAnteriores.each { polq ->
            prmt = [:]
            prmt.planilla = plnl
            prmt.planillaReajustada = polq.planillaReajustada
            prmt.parcialCronograma = polq.parcialCronograma
            prmt.acumuladoCronograma = polq.acumuladoCronograma
            prmt.parcialPlanillas = polq.parcialPlanillas
            prmt.acumuladoPlanillas = polq.acumuladoPlanillas
            prmt.valorPo = polq.valorPo
            prmt.periodo = polq.periodo
            prmt.mes = polq.mes
            prmt.fechaInicio = polq.fechaInicio
            prmt.fechaFin = polq.fechaFin
//          hayAnteriores = true

//            println "hay que recalcular reajuste de plnl avance: ${polq.planillaReajustada.id}, tipo: ${polq.planillaReajustada.tipoPlanilla}"
            if(polq.planillaReajustada.tipoPlanilla.codigo == "A") {
                prdoInec = indicesDisponibles(polq.planillaReajustada, polq.planillaReajustada.fechaPago, 'R') // para recalcular reajuste
            } else {
                prdoInec = indicesDisponibles(polq.planillaReajustada, polq.fechaInicio, 'R') /* para recalcular reajuste */
            }
            prmt.periodoInec = prdoInec?: indicesDisponibles(polq.planillaReajustada, polq.fechaInicio, '')
//            println "  inserta avance RR... si hay indices actuales $prmt"
            insertaRjpl(prmt)

            parcial = polq.parcialCronograma
            total = polq.acumuladoCronograma
            prdo = polq.periodo
            plAcumulado = polq.acumuladoPlanillas
        }
        prdo++

//        println "------planilla actual ${plnl.id} tipo: ${plnl.tipoPlanilla} -----"
        /* la planilla de obras adicionales siempre se refiere sólo al último periodo de ejecución */
        //todo: ingresar Po y datos de la planilla de obras adicionales
        def esteMes = 0.0
        def restoMes = 0.0
        plAcumulado += plnl.valor

//        println "<<<<<<<<<<<<< plnl: ${plnl.id} tipo: ${plnl.tipoPlanilla}, " +
//                "cronogrmama: $total, $parcial, planillado: ${plnl.valor}, ac: ${plAcumulado}"

        prmt = [:]
        prdoInec = indicesDisponibles(plnl, plnl.fechaInicio, null)
        /* para recalcular reajuste */
        prmt.periodoInec = prdoInec
        prmt.planilla = plnl
        prmt.planillaReajustada = plnl


        prmt.parcialCronograma = parcial
        prmt.acumuladoCronograma = total
        prmt.parcialPlanillas = plnl.valor
        prmt.acumuladoPlanillas = plAcumulado
        prmt.periodo = prdo
        prmt.mes = preciosService.componeMes(plnl.fechaInicio.format('MMM-yyyy'))
        prmt.fechaInicio = plnl.fechaInicio
        prmt.fechaFin = plnl.fechaFin

        prmt.valorPo = plnl.valor
//            println "inserta ... $prmt"
        insertaRjpl(prmt)
    }

    def poneTotalReajuste(plnl) {
        if(plnl.tipoPlanilla.codigo in ['A', 'P', 'Q', 'O']) {
            def valorAnt = 0.0
//            def anteriores = ReajustePlanilla.findAllByPlanilla(plnl, [sort: 'periodo'])
            def anteriores = ReajustePlanilla.findAllByPlanillaAndPlanillaReajustadaNotEqual(plnl, plnl, [sort: 'periodo'])
            def anterior
//            println "anteriores: antes .. ${anteriores.size()}, planilla: ${anteriores}"

            if(anteriores.size() > 0) {
                anterior = anteriores.pop().planillaReajustada
                valorAnt = ReajustePlanilla.executeQuery("select sum(valorReajustado) from ReajustePlanilla " +
                        "where planilla = :p", [p: anterior])[0]
            }

            def valor = ReajustePlanilla.executeQuery("select sum(valorReajustado) from ReajustePlanilla where planilla = :p", [p: plnl])
            def reajustado = valor[0] > 0 ? valor[0] : 0
//            println "reajustado .. $reajustado"

            def pl = Planilla.get(plnl.id)
//            println "actual: ${valor[0]}, anterior:$valorAnt, diferencia: ${valor[0] - valorAnt}"
            pl.reajuste = reajustado - valorAnt
            pl.save(flush: true)
        }
    }

    /**
     * se presenta el o los periodos a planillarse, si no se ajuste el mes se crea un periodo opcional que
     * abarca el mes inicial y el siguiente
     * @param tipos         tipos de planilla
     * @param cntr          contrato
     * @param antc          tipo de planilla anticipo
     * @param periodosEjec  periodos de ejecución del cronograma
     * @param finalObra     fecha de fin de obra
     * @return              periodos
     */

    def ponePeriodos(tipos, cntr, antc, periodosEjec, finalObra) {

        def periodos = [:]
        def dias = 0
        def diasMes = 0
        def fcim
        def fcfm
        def opcional = false
        def lleno = false
        def texto = ""
        def ultimo = ""
        def inicio
        def inicioPr
        def avance = TipoPlanilla.findAllByCodigoInList(['P', 'Q'])
//        println "planillas de avance: $avance"
//        println "periodosEjec: $periodosEjec"
        def planillasAnt = Planilla.findAllByContratoAndTipoPlanillaInList(cntr, avance, [sort: 'fechaPresentacion'])
        if (planillasAnt) {
            inicio = planillasAnt.pop().fechaFin + 1
        }
        // hay periodos para tipos de planilla: P, Q, D
//        println "ponePeriodos finalObra: $finalObra, tipos: ${tipos.codigo}"
        if (tipos.codigo.contains("P") || tipos.codigo.contains("Q") || tipos.codigo.contains("D")) {
//            println "inicia .. 1"
            periodosEjec.each { pe ->
                ultimo = texto

//                println "xxprocesa ... periodo: ${pe.fechaInicio.format('dd-MM-yyyy') + '_' + pe.fechaFin.format('dd-MM-yyyy')} " +
//                        "fin de obra: $finalObra, inicio: $inicio"

                fcim = preciosService.primerDiaDelMes(pe.fechaInicio)
                fcfm = preciosService.ultimoDiaDelMes(pe.fechaInicio)
                diasMes = fcfm -fcim + 1

//                println "xxel mes de ${pe.fechaInicio} tiene $diasMes dias, fechaFin: ${pe.fechaFin}"

                if(!lleno) {  // ya se completó los días del mes en curso
                    if (pe.fechaFin == fcfm) {
                        if (inicio) { // viene de un periodo anterior
                            println "xxsi hay inicio, dias: $dias"
                            texto = inicio.format("dd-MM-yyyy") + "_" + pe.fechaFin.format("dd-MM-yyyy")
                        } else {
//                            println "xxno hay inicio, dias: $dias"
                            texto = pe.fechaInicio.format("dd-MM-yyyy") + "_" + pe.fechaFin.format("dd-MM-yyyy")
                            inicio = pe.fechaInicio
                        }

                        if (opcional) {
                            periodos.put(texto, texto.replaceAll('_', ' a '))
                            opcional = false
                        } else {
                            periodos.put(texto, texto.replaceAll('_', ' a '))
                        }

                        dias = fcfm - inicio + 1
                        if (dias < diasMes) {
                            opcional = true
                            lleno = false
                        } else {
                            lleno = true
                            //no deben procesarse mas periodos
                        }
                    } else { // pe.fechaFin no es fin de mes
//                        println "opcional: $opcional, último: $ultimo, inicioPr: $inicioPr, inicio: $inicio"
                        if(opcional){
                            texto = inicio.format("dd-MM-yyyy") + "_" + pe.fechaFin.format("dd-MM-yyyy")
                        } else {
                            texto = pe.fechaInicio.format("dd-MM-yyyy") + "_" + pe.fechaFin.format("dd-MM-yyyy")
                            if(!inicioPr) {
                                inicio = pe.fechaInicio
                                inicioPr = inicio.format("dd-MM-yyyy")
                            }
                        }

//                        periodos.put(texto, texto.replaceAll('_', ' a '))

//                        println "periodos. $periodos, pe.fechaFin: ${pe.fechaFin}, final: ${finalObra} inicio: $inicio"
                        // si es el último periodo también es opcional
                        if (pe.fechaFin == finalObra) {
                            if (ultimo) {
                                def desde = ""
                                if(periodos[ultimo]){    /*** trata el ultimo trama de la obra ****/
                                    desde = periodos[ultimo].split(' a ')[0]
                                } else {
                                    desde = inicioPr?: inicio.format("dd-MM-yyyy")
                                }
                                texto = desde + "_" + pe.fechaFin.format("dd-MM-yyyy")
                                periodos.put(texto, texto.replaceAll('_', ' a '))
                            } else if(periodos){
                                def desde = periodos[texto].split(' a ')[0]
                                texto = desde + "_" + pe.fechaFin.format("dd-MM-yyyy")
                                periodos.put(texto, texto.replaceAll('_', ' a '))
                            } else {
                                periodos.put(texto, texto.replaceAll('_', ' a '))
                            }
                        }
//                        println "xxperiodos: $periodos, ultimo: $ultimo"
//                        println "xxperiodos ultimo: ${periodos[ultimo]}"
                    }
                }
            }
        }
        return periodos
    }

    //calcula el total sbpr que apica a esta FP
    def prorrateaPo(fprj, cntr, plnl) {
        println "prorrateaPo: fprj: $fprj, obra: ${cntr.obraContratada.id}, valor: $plnl"
        def cn = dbConnectionService.getConnection()
        def sql= "select sum(vlobsbtt) suma from fpsp, vlob where vlob.sbpr__id = fpsp.sbpr__id and " +
                "fprj__id = ${fprj} and obra__id = ${cntr.obraContratada.id}"
        println "prorrateaPo--sql: $sql"
        def valor = cn.rows(sql.toString())[0].suma * plnl/cntr.monto
        println "valor: $valor"
        return valor
    }

    /**
     * @param prdo --> columna rjplprdo
     * @param vlor --> valor de la planilla
     * @param esteMes --> columna rjplvlpo
     * @return
     */
    def registraRjpl(prdo, esteMes, plAcumulado, contrato, planilla, fcin, fcfn, crpa, crac, planillaFinal) {
        println "***prdo: $prdo, esteMes: $esteMes, fcin: $fcin, fcfn: $fcfn,  crpa: $crpa, crac: $crac"
        def prmt = [:]
        def prdoInec = indicesDisponibles(planilla, fcin, null) /* para recalcular reajuste */
        prmt.periodoInec = prdoInec
        prmt.planilla = planilla
        prmt.planillaReajustada = planilla


        prmt.parcialCronograma = crpa
        prmt.acumuladoCronograma = crac
        prmt.parcialPlanillas = esteMes
        prmt.acumuladoPlanillas = plAcumulado
        prmt.periodo = prdo
        prmt.mes = preciosService.componeMes(fcin.format('MMM-yyyy'))
        prmt.fechaInicio = fcin
        prmt.fechaFin = fcfn
        prmt.fpReajuste = planilla.formulaPolinomicaReajuste

        def dsct1 = calculaPo(planilla.id, esteMes, planillaFinal, prmt.periodo)
//                println "+++2: ${calculaPo(plnl.id, 0, false, prmt.periodo)}, anterior: $dsct1"
        prmt.valorPo = dsct1
        println "inserta segunda parte Po: $dsct1"

        if(Math.abs(dsct1) > 0.001 && planilla.valor > 0) {
            insertaRjpl(prmt)
        } else if(planilla.valor == 0) {
            prmt.valorPo = 0
            insertaRjpl(prmt)
        }
    }

    def anticipo_ajax () {
//        println("params " + params)
        def contrato = Contrato.get(params.contrato)
        def tipoPlanilla = TipoPlanilla.get(params.tipo)
        def com
        if(tipoPlanilla.codigo == 'B'){
            com = Contrato.findByPadre(contrato)
            return [monto: com.monto, contrato: com]
        }else{
            return [monto: contrato.monto, contrato: contrato]
        }

    }

    def ordenCambio_ajax () {
        def planilla = Planilla.get(params.id)
        return [planilla: planilla]
    }

    def ordenTrabajo_ajax () {
        def planilla = Planilla.get(params.id)
        return [planilla: planilla]
    }

    def saveOrdenCambio () {
//        println("parmas oc " + params)

        def planilla = Planilla.get(params.id)

        planilla.memoOrden = params."memo_name"
        planilla.numeroOrden = params."numero_name"
        if(params."fechaSuscripcion_name"){
            planilla.fechaSuscripcionOrden = new Date().parse("dd-MM-yyyy", params."fechaSuscripcion_name")
        }
        planilla.garantiaOrden = params."garantia_name"
        planilla.numeroCertificacionOrden = params."certificacion_name"
        if(params."fechaCertificacion_name"){
            planilla.fechaCertificacionOrden = new Date().parse("dd-MM-yyyy", params."fechaCertificacion_name")
        }

        if(planilla.save(flush: true)){
            flash.clase = "alert-success"
            flash.message = "Se guardado correctamente la Orden de Cambio para la:" + " PLANILLA: " + planilla?.numero + " - TIPO: " + planilla?.tipoPlanilla?.nombre
        }else{
            flash.clase = "alert-danger"
            flash.message = "Error al guardar la Orden de Cambio"
        }


        if(params."adi_name" == '1'){
            redirect(controller: 'reportes6', action: 'reporteOrdenCambio' , id: planilla.id)
        }else{
//            redirect(action: 'listFiscalizador' , id: planilla.contrato.id)
            render "ok"
        }


    }

    def saveOrdenTrabajo () {
//        println("params ot " + params)

        def planilla = Planilla.get(params.id)

        planilla.memoTrabajo = params."memoT_name"
        planilla.numeroTrabajo = params."numeroT_name"
        if(params."fechaSuscripcionT_name"){
            planilla.fechaSuscripcionTrabajo = new Date().parse("dd-MM-yyyy", params."fechaSuscripcionT_name")
        }
        planilla.garantiaTrabajo = params."garantiaT_name"
        planilla.numeroCertificacionTrabajo = params."certificacionT_name"
        if(params."fechaCertificacionT_name"){
            planilla.fechaCertificacionTrabajo = new Date().parse("dd-MM-yyyy", params."fechaCertificacionT_name")
        }

        if(planilla.save(flush: true)){
            flash.clase = "alert-success"
            flash.message = "Se guardado correctamente la Orden de Trabajo para la:" + " PLANILLA: " + planilla?.numero + " - TIPO: " + planilla?.tipoPlanilla?.nombre
        }else{
            flash.clase = "alert-danger"
            flash.message = "Error al guardar la Orden de Trabajo"
        }

        if(params."adi2_name" == '1'){
            redirect(controller: 'reportes6', action: 'reporteOrdenDeTrabajo' , id: planilla.id)
        }else{
//            redirect(action: 'listFiscalizador' , id: planilla.contrato.id)
            render "ok"
        }
    }

}