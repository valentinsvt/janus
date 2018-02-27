package janus.ejecucion

import com.lowagie.text.Element
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPTable
import janus.Contrato
import janus.FormulaPolinomica
import janus.Obra
import janus.pac.CronogramaEjecucion
import janus.pac.PeriodoEjecucion

import java.awt.Color

class Planilla2Controller extends janus.seguridad.Shield {

    def buscadorService
    def diasLaborablesService
    def preciosService
    def planillasService
    def dbConnectionService

    def errores() {
    }

    def errores2() {
        return [params: params]
    }

    def pagos() {
        def campos = ["id": ["Obra", "string"], "descripcion": ["Descripción", "string"], "fechaPresentacion": ["Fecha Presentación", "string"], "memoPagoPlanilla": ["Memo pago", "string"]]
        [campos: campos]
    }


    def buscarPlanilla() {

        def extraObra = ""
        if (params.campos instanceof java.lang.String) {
            if (params.campos == "id") {
                if (params.criterios != "") {
                    def obras = Obra.findAll("from Obra where nombre like '%${params.criterios.toUpperCase()}%' or codigo like '%${params.criterios.toUpperCase()}%' ")
                    params.criterios = ""
                    obras.eachWithIndex { p, i ->
                        def concursos = janus.pac.Concurso.findAllByObraAndEstado(p, "R")
                        concursos.each { co ->
                            def ofertas = janus.pac.Oferta.findAllByConcurso(co)
                            ofertas.each { o ->
                                def contratos = Contrato.findAllByOferta(o)
                                contratos.eachWithIndex { cn, k ->
                                    extraObra += "" + cn.id
                                    if (k < contratos.size() - 1)
                                        extraObra += ","
                                }
                            }

                        }

                    }
                    if (extraObra.size() < 1)
                        extraObra = "-1"
                }

                params.campos = ""
                params.operadores = ""
            }
        } else {
            def remove = []
            params.campos.eachWithIndex { p, i ->
                if (p == "id") {
                    if (params.criterios != "") {
                        def obras = Obra.findAll("from Obra where nombre like '%${params.criterios.toUpperCase()}%' or codigo like '%${params.criterios.toUpperCase()}%' ")

                        obras.eachWithIndex { ob, j ->
                            def concursos = janus.pac.Concurso.findAllByObraAndEstado(ob, "R")
                            concursos.each { co ->
                                def ofertas = janus.pac.Oferta.findAllByConcurso(co)
                                ofertas.each { o ->
                                    def contratos = Contrato.findAllByOferta(o)
                                    contratos.eachWithIndex { cn, k ->
                                        extraObra += "" + cn.id
                                        if (k < contratos.size() - 1)
                                            extraObra += ","
                                    }
                                }

                            }

                        }
                        if (extraObra.size() < 1)
                            extraObra = "-1"
                    }


                }
            }
            remove.each {
                params.criterios[it] = null
                params.campos[it] = null
                params.operadores[it] = null
            }
        }

//        println "extra obra "+extraObra

        def codObra = { pla ->
            return pla.contrato?.oferta?.concurso?.obra?.codigo
        }
        def contr = { pla ->
            return pla.contrato?.codigo
        }

        def listaTitulos = ["OBRA", "CONTRATO", "DESCRIPCION", "FECHA PRESENTACION", "MEMO PAGO", "MONTO"]
        def listaCampos = ["id", "contrato", "descripcion", "fechaPresentacion", "memoPagoPlanilla", "valor"]
        def funciones = [["closure": [codObra, "&"]], ["closure": [contr, "&"]], null, null, null, null]
        def url = g.createLink(action: "buscarPlanilla", controller: "planilla2")
        def funcionJs = "function(){"
//        funcionJs += '$("#modal-busqueda").modal("hide");'
//        funcionJs += 'location.href="' + g.createLink(action: 'verContrato', controller: 'contrato') + '/"+$(this).attr("regId");'
        funcionJs += '}'
        def numRegistros = 20
        def extras = " and fechaPago is not null "
        if (extraObra.size() > 0)
            extras += " and contrato in (${extraObra}) "
//        println "extras "+extras
//        println "params "+params
        if (!params.reporte) {
            if (params.excel) {
                session.dominio = Planilla
                session.funciones = funciones
                def anchos = [35, 35, 60, 35, 35, 30]
                /*anchos para el set column view en excel (no son porcentajes)*/
                redirect(controller: "reportes", action: "reporteBuscadorExcel", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Planilla", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "REPORTE DE PAGOS", anchos: anchos, extras: extras, landscape: true])
            } else {
                def lista = buscadorService.buscar(Planilla, "Planilla", "excluyente", params, true, extras)
                /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
                lista.pop()
                render(view: '../tablaBuscador2', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "planilla2", numRegistros: numRegistros, funcionJs: funcionJs, width: 1800, paginas: 12])
            }

        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Planilla
            session.funciones = funciones
            def anchos = [15, 15, 40, 10, 10, 10]
            /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Planilla", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "REPORTE DE CHEQUES PAGADOS", anchos: anchos, extras: extras, landscape: true])
        }
    }


    def liquidacion() {
        def planilla = Planilla.get(params.id)
        def override = false
        def obra = planilla.contrato.obra
        def contrato = planilla.contrato

        def planillasAnterioresTot = Planilla.withCriteria {
            eq("contrato", contrato)
            and {
                or {
                    lt("fechaInicio", planilla.fechaInicio)
                    isNull("fechaInicio")
                }
                or {
                    eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
                    eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
                }
            }
        }

        def planillasAnteriores = Planilla.withCriteria {
            eq("contrato", contrato)
            or {
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("Q"))
            }
            order("id", "asc")
        }

//        def planillaAnterior = planillasAnteriores[planillasAnteriores.size() - 1]
        def planillaAnterior = planillasAnteriores[-1]

        def periodosEjec = PeriodoEjecucion.findAllByObra(contrato.oferta.concurso.obra, [sort: "fechaFin"])
        def finalObraCrono = periodosEjec.pop().fechaFin
        def tarde = false
        def periodos = PeriodoPlanilla.findAllByPlanillaInList(planillasAnteriores, [sort: "id"])
        def fechaFinObra = obra.fechaFin

        def obraLiquidacion = Obra.withCriteria {
            eq("liquidacion", 1)
            eq("codigo", obra.codigo + "LQ")
        }

//        if (obraLiquidacion.size() == 1) {
        if (obra) {
            obraLiquidacion = obra
            def pcs = FormulaPolinomica.withCriteria {
                and {
                    eq("obra", obraLiquidacion)
                    or {
                        ilike("numero", "c%")
                        and {
                            ne("numero", "P0")
                            ne("numero", "p01")
                            ilike("numero", "p%")
                        }
                    }
                    order("numero", "asc")
                }
            }

            def cs = FormulaPolinomica.withCriteria {
                eq("obra", obraLiquidacion)
                ilike("numero", "C%")
                order("numero")
            }
            def ps = FormulaPolinomica.withCriteria {
                and {
                    eq("obra", obraLiquidacion)
                    and {
                        ne("numero", "P0")
                        ilike("numero", "p%")
                    }
                    order("numero", "asc")
                }
            }

            def cMayor0 = cs.findAll { it.valor > 0 }
            def pMayor0 = ps.findAll { it.valor > 0 }

            if (cMayor0.size() == 0 || pMayor0.size() == 1) {
                println "No hay la FP de liquidación"
                println obraLiquidacion.id
                def url = g.createLink(controller: "formulaPolinomica", action: "coeficientes", id: obraLiquidacion.id)
                def link = "<a href='${url}' class='btn btn-danger'>Fórmula polinómica de liquidación</a>"
                flash.message = "No se encontró la fórmula polinómica de liquidación. Por favor ingrésela para realizar la planilla de liquidación<br/><br/>" + link
                redirect(action: "errores")
                return
            }

            if (!fechaFinObra) {
                println "Error fecha fin obra"
                flash.message = "Ingrese las fechas de recepción antes de generar la planilla de liquidación de reajuste"
                flash.clase = "alert-error"
                redirect(controller: "contrato", action: "fechasPedidoRecepcion", id: contrato.id)
                return
            }

            if (fechaFinObra > finalObraCrono) {
                tarde = true
            }

            def existe = null
            def error = false
            def errorNull = ""
            flash.message = ""
            planillasAnteriores.each { pl ->
                def pers = PeriodoPlanilla.findAllByPlanilla(pl)
                pers.each { p ->
                    def periodo
                    if (tarde) {
                        periodo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(p.fechaIncio, p.fechaFin)
                    } else {
                        periodo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(pl.fechaPago, pl.fechaPago)
                    }
                    p.periodoLiquidacion = periodo
                    p.save(flush: true)
                    existe = verificaIndices(pcs, periodo, 0)
                    if (!periodo || !existe) {
                        errorNull = true
                    }
                    if (periodo != existe) {
                        error = true
                    }
                }
            }

            if (errorNull) {
                println "**ERROR Null"
                flash.message = "<ul><li>Ha ocurrido un error grave</li>></ul>"
                redirect(action: "errores")
                return
            }

            if (error) {
                println "**ERROR"
                flash.message = "<ul>" + flash.message + "</ul>"
                redirect(action: "errores")
                return
            }

            def tableWidth = 150 * periodos.size() + 400
            ///////////////////////////////////////////////////**********planillas*****************////////////////////////////////////////////////////////////

            def planillaAnticipo = Planilla.withCriteria {
                eq("contrato", contrato)
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
            }
            planillaAnticipo = planillaAnticipo.pop()

            def periodosAnticipo = PeriodoPlanilla.findAllByPlanilla(planillaAnticipo, [sort: "id"])
            def planillasAvances = Planilla.withCriteria {
                eq("contrato", contrato)
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
            }
            def periodosAvances = PeriodoPlanilla.findAllByPlanillaInList(planillasAvances, [sort: "id"])

            def tablaBo = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:&&px;'>"
            tablaBo += "<thead>"
            tablaBo += "<tr>"
            tablaBo += "<th colspan=\"2\">Cuadrilla Tipo</th>"
            tablaBo += "<th>*** </th>"
            tablaBo += "<th class='nb'> ***</th>"
            periodosAnticipo.each {
                it.planilla.reajusteLiq = 0
                it.totalLiq = 0
                it.frLiq = 0
                tablaBo += "<th>${it.titulo}</th>"
                tablaBo += "<th class='nb'>" + fechaConFormato(it.fechaIncio) + "</th>"
            }
            periodosAvances.each {
                it.planilla.reajusteLiq = 0
                it.totalLiq = 0
                it.frLiq = 0
                tablaBo += "<th>${it.titulo}</th>"
                tablaBo += "<th class='nb'>" + fechaConFormato(it.fechaIncio) + "</th>"
            }

            tablaBo += "</tr>"
            tablaBo += "</thead>"
            tablaBo += "<tbody>"
            def totalCoef = 0

            cs.each { c ->
                if (c.valor > 0) {
                    tablaBo += "<tr>"
                    tablaBo += "<th class='tal'>" + c.indice.descripcion + " (${c.numero})</th>"
                    tablaBo += "<th class='number'>" + numero(c.valor) + "</th>"
                    totalCoef += c.valor
                    periodosAnticipo.each { p ->
                        def valor = ValorIndice.findByPeriodoAndIndice(p.periodoLiquidacion, c.indice).valor
                        if (!valor) {
                            println "wtf no valor " + p.periodoLiquidacion + "  " + c.indice
                            valor = 0
                        }

                        p.totalLiq += (valor * c.valor).round(3)
                        p.save(flush: true)

                        tablaBo += "<td class='number'>" + numero(valor, 2) + "</td>"
                        valor = (valor * c.valor).round(3)
                        def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${p.periodoLiquidacion.id} and formulaPolinomicaLiq=${c.id} and planillaLiq = ${planillaAnticipo.id} ")
                        if (vlrj.size() > 0) {
                            vlrj = vlrj.pop()
                            if (vlrj != valor) {
                                vlrj.valor = valor
                                if (!vlrj.save(flush: true)) {
                                    println "error vlrj update " + vlrj.errors
                                }
                            }
                        } else {
                            vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: p.periodoLiquidacion, formulaPolinomicaLiq: c, valor: valor, planillaLiq: planillaAnticipo])
                            if (!vlrj.save(flush: true)) {
                                println "--error vlrj insert " + vlrj.errors
                            }
                        }
                        tablaBo += "<td class='number'>" + numero(valor) + "</td>"
                    }

                    periodosAvances.each { p ->
                        def valor = ValorIndice.findByPeriodoAndIndice(p.periodoLiquidacion, c.indice).valor
                        if (!valor) {
                            println "wtf no valor liq " + p.periodoLiquidacion + "  " + c.indice
                            valor = 0
                        }

                        p.totalLiq += (valor * c.valor).round(3)
                        p.save(flush: true)

                        tablaBo += "<td class='number'>" + numero(valor, 2) + "</td>"
                        valor = (valor * c.valor).round(3)
                        def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${p.periodoLiquidacion.id} and formulaPolinomicaLiq=${c.id} and planillaLiq=${p.planilla.id}")
                        if (vlrj.size() > 0) {
                            vlrj = vlrj.pop()
                            if (vlrj != valor) {
                                vlrj.valor = valor
                                if (!vlrj.save(flush: true)) {
                                    println "error vlrj update " + vlrj.errors
                                }
                            }
                        } else {
                            vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: p.periodoLiquidacion, formulaPolinomicaLiq: c, valor: valor, planillaLiq: p.planilla])
                            if (!vlrj.save(flush: true)) {
                                println "++error vlrj insert " + vlrj.errors
                            }
                        }
                        tablaBo += "<td class='number'>" + numero(valor) + "</td>"
                    }

                    tablaBo += "</tr>"
                }
            }

            tablaBo += "</tbody><tfoot>"
            tablaBo += "<tr>" + "<th>TOTALES</th><th class='number'>${numero(totalCoef)}</th>"



            periodosAnticipo.each { p ->
                tablaBo += "<td></td><th class='number'>${numero(p.totalLiq)}</th>"
            }
            periodosAvances.each { p ->
                tablaBo += "<td></td><th class='number'>${numero(p.totalLiq)}</th>"
            }

            tablaBo += "</tr></tfoot></table>"

            //////////////////////////**********************P0*********************///////////////////////////
            def tablaP0 = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:&&px; margin-top:10px;' >"
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
            tablaP0 += '<tbody>'
            def act = 0
            def act2 = 0
            def diasTot = 0, totCrono = 0, totPlan = 0, totalMultaRetraso = 0
            (periodosAnticipo + periodosAvances).each { p ->
                if (p.titulo != "OFERTA") {
                    tablaP0 += '<tr>'
                    if (p.titulo == "ANTICIPO") {
                        tablaP0 += "<th>${p.titulo}</th>"
                        tablaP0 += "<th>${fechaConFormato(p.fechaIncio, 'MMM-yy')}</th>"
                        tablaP0 += "<td></td>"
                        tablaP0 += "<td></td>"
                        tablaP0 += "<td></td>"
                        tablaP0 += "<td></td>"
                        tablaP0 += "<td class='number'>${numero(p.p0, 2)}</td>"
                    } else {
                        def dias = p.fechaFin - p.fechaIncio + 1
                        diasTot += dias
                        tablaP0 += "<th>${fechaConFormato(p.fechaIncio, 'MMM-yy')}</th>"
                        tablaP0 += "<th>(${dias})</th>"
                        tablaP0 += "<td class='number'>${numero(p.parcialCronograma, 2)}</td>"
                        act += p.parcialCronograma
                        totCrono += p.parcialCronograma
                        tablaP0 += "<td class='number'>${numero(act, 2)}</td>"
                        tablaP0 += "<td class='number'>${numero(p.parcialPlanilla, 2)}</td>"
                        act2 += p.parcialPlanilla
                        totPlan += p.parcialPlanilla
                        tablaP0 += "<td class='number'>${numero(act2, 2)}</td>"
                        tablaP0 += "<td class='number'>${numero(p.p0, 2)}</td>"
                    }
                }
            }
            tablaP0 += '</tbody>'
            tablaP0 += '<tfoot>'
            //totales aqui
            tablaP0 += "<th>TOTAL</th>"
            tablaP0 += "<th>(${diasTot})</th>"
            tablaP0 += "<td class='number'></td>"
            tablaP0 += "<th class='number'>${numero(totCrono, 2)}</th>"
            tablaP0 += "<td class='number'></td>"
            tablaP0 += "<th class='number'>${numero(totPlan, 2)}</th>"
            tablaP0 += "<td class='number'></td>"
            tablaP0 += '</tfoot>'
            tablaP0 += '</table>'

            //////////////////////////////////////////////*****************FR*********************************/////////////////

            def tr1 = "<tr>"
            def tr2 = "<tr>"
            def tr3 = "<tr>"
            def tablaFr = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:&&px; margin-top:10px;'>"
            tablaFr += '<thead>'
            tr1 = '<tr>'
            tr1 += '<th rowspan="2">Componentes</th>'
            tr3 += "<th>Anticipo</th>"

            def tdRowSpan = '<th colspan="' + (periodosAvances.size() + periodosAnticipo.size() - 1) + '">Periodo de variación y aplicación de fórmula polinómica</th>'
            (periodosAnticipo + periodosAvances).eachWithIndex { p, i ->
                if (i == 0) { //oferta
                    tr1 += "<th>" + p.titulo + "</th>"
                    tr2 += "<th>" + fechaConFormato(p.fechaIncio, "MMM-yy") + "</th>"
                    tr3 += "<th class='number'>" + planilla.contrato.porcentajeAnticipo + "%</th>"
                } //oferta
                else if (i == 1) { //anticipo
                    tr1 += tdRowSpan
                    tr2 += "<th>" + p.titulo + "<br/>" + fechaConFormato(p.fechaIncio, "MMM-yy") + "</th>"
                    tr3 += "<th>" + p.titulo + "</th>"
                } //anticipo
                else { //otros
                    tr2 += "<th rowspan='2'>" + fechaConFormato(p.fechaIncio, "MMM-yy") + "</th>"
                }//otros
            }  // periodos.each para el header
            tr1 += "</tr>"
            tr2 += "</tr>"
            tr3 += "</tr>"
            tablaFr += tr1 + tr2 + tr3
            tablaFr += "</thead>"
            tablaFr += "<tbody>"

            def totalFr = 0

//            println "\n\n\n\t\tAQUI\n\n\n\n"

            ps.eachWithIndex { p, i ->
                tablaFr += "<tr>"
                tablaFr += "<th class='tal'>" + p?.indice?.descripcion + " (${p?.numero})</th>"
                def vlinOferta = 0
                (periodosAnticipo + periodosAvances).eachWithIndex { per, j ->
                    if (j == 0) { // es la oferta
                        def valor = 0
                        if (i == 0) { //es mano de obra
                            vlinOferta = per.totalLiq
                            tablaFr += "<td class='number'><div>${numero(p.valor, 3)}</div><div class='bold'>${numero(per.totalLiq, 3)}</div></td>"
                            valor = per.totalLiq
                        } else {
                            vlinOferta = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodoLiquidacion).valor
                            tablaFr += "<td class='number'><div>${numero(p.valor, 3)}</div><div class='bold'>${numero(vlinOferta, 2)}</div></td>"
                            valor = vlinOferta
                        }
                        def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${per.periodo.id} and formulaPolinomicaLiq=${p.id} and planillaLiq=${per.planilla.id}")
                        if (vlrj.size() > 0) {
                            vlrj = vlrj.pop()
                            if (vlrj != valor) {
                                vlrj.valor = valor
                                if (!vlrj.save(flush: true)) {
                                    println "error vlrj update " + vlrj.errors
                                }
                            }
                        } else {
                            vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: per.periodoLiquidacion, formulaPolinomicaLiq: p, valor: valor, planillaLiq: per.planilla])
                            if (!vlrj.save(flush: true)) {
                                println "**error vlrj insert " + vlrj.errors
                            }
                        }

                    } else {
                        def vlin
                        def dec
                        if (i == 0) {
                            vlin = per.totalLiq
                            dec = 3
                        } else {
                            vlin = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodoLiquidacion).valor
                            dec = 2
                        }
                        def valor = (vlin / vlinOferta * p.valor).round(3)

                        def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${per.periodo.id} and formulaPolinomicaLiq=${p.id} and planillaLiq=${per.planilla.id}")
                        if (vlrj.size() > 0) {
                            vlrj = vlrj.pop()
                            if (vlrj != valor) {
                                vlrj.valor = valor
                                if (!vlrj.save(flush: true)) {
                                    println "error vlrj update " + vlrj.errors
                                }
                            }
                        } else {
                            vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: per.periodoLiquidacion, formulaPolinomicaLiq: p, valor: valor, planillaLiq: per.planilla])
                            if (!vlrj.save(flush: true)) {
                                println "error vlrj insert " + vlrj.errors
                            }
                        }

                        per.frLiq += valor
                        if (!per.save(flush: true)) {
                            println "error fr " + per.errors
                        }
                        tablaFr += "<td class='number'><div>${numero(vlin, dec)}</div><div class='bold'>${numero(valor)}</div></td>"
                    }

                }
                tablaFr += "</tr>"
            }

            (periodosAnticipo + periodosAvances).each { p ->
                def fr1 = (p.frLiq - 1).round(3)
                p.planilla.reajusteLiq += (p.p0 * fr1).round(2)
                p.planilla.save(flush: true)
            }

            planillasAnteriores.eachWithIndex { p, i ->
                if (i > 0) {
                    p.reajusteLiq += planillasAnteriores[i - 1].reajusteLiq
                    p.save(flush: true)
                }
            }

            tr1 = "<tr>"
            tr2 = "<tr>"
            tr3 = "<tr>"
            def tr4 = "<tr>"
            def tr5 = "<tr>"
            def tr6 = "<tr>"
            def tr7 = "<tr>"

            tr1 += "<th rowspan='4'>1.000</th>"
            tr1 += "<th>F<sub>r</sub></th>"
            tr2 += "<th>F<sub>r</sub>-1</th>"
            tr3 += "<th>P<sub>0</sub></th>"
            tr4 += "<th>P<sub>r</sub>-P</th>"

            def reajusteTotal = 0

            (periodosAnticipo + periodosAvances).eachWithIndex { per, i ->
                if (i > 0) {
                    def fr1 = (per.frLiq - 1).round(3)
                    tr1 += "<th class='number'>${numero(per.frLiq, 2)}</th>"
                    tr2 += "<th class='number'>${numero(fr1, 2)}</th>"
                    tr3 += "<th class='number'>${numero(per.p0, 2)}</th>"
                    def t = (per.p0 * fr1).round(2)
                    tr4 += "<th class='number'>${numero(t, 2)}</th>"
                    reajusteTotal += t
                }
            }

            reajusteTotal = reajusteTotal.toDouble().round(2)

            def reajusteAnterior = (planillaAnterior.reajusteLiq).toDouble().round(2)

            tr5 += "<th colspan='2'>REAJUSTE TOTAL</th><th colspan='${periodosAnticipo.size() + periodosAvances.size() - 1}' class='number'>" + reajusteTotal + "</th>"
            tr6 += "<th colspan='2'>REAJUSTE ANTERIOR</th><th colspan='${periodosAnticipo.size() + periodosAvances.size() - 1}' class='number'>" + reajusteAnterior + "</th>"
            tr7 += "<th colspan='2'>REAJUSTE A PLANILLAR</th><th colspan='${periodosAnticipo.size() + periodosAvances.size() - 1}' class='number'>" + ((reajusteTotal - reajusteAnterior).toDouble().round(2)) + "</th>"

            tr1 += "</tr>"
            tr2 += "</tr>"
            tr3 += "</tr>"
            tr4 += "</tr>"
            tr5 += "</tr>"
            tr6 += "</tr>"
            tr7 += "</tr>"

            tablaFr += "<tfoot>"
            tablaFr += tr1 + tr2 + tr3 + tr4 + tr5 + tr6 + tr7
            tablaFr += "</tfoot></table>"

            planilla.reajuste = reajusteTotal
            if (!planilla.save(flush: true)) {
                println "error planilla reajuste " + planilla.id
            }

            //////////////////////////////////////////fin anticipo//////////////////////////////////////////////////////////////////////////////////////////////////

            def tablaMl = ""

            tablaBo.replaceAll("&&", "" + tableWidth)
            tablaP0.replaceAll("&&", "" + tableWidth)
            tablaFr.replaceAll("&&", "" + tableWidth)

            return [tablaBo: tablaBo, planilla: planilla, tablaP0: tablaP0, tablaFr: tablaFr, tablaMl: tablaMl]
        }
    }

    def avance() {
        def planilla = Planilla.get(params.id)
//        def override = false
//        if (session.override == "true" || session.override == true) {
//            override = true
//            session.override = false
//        }
//        def obra = planilla.contrato.oferta.concurso.obra
        def obra = planilla.contrato.obra
        def contrato = planilla.contrato

        def prej = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaFin', order: 'desc'])
//        def liquidacion = planilla.fechaFin >= prej[0].fechaFin
        def liquidacion = planilla.tipoPlanilla.codigo == 'Q'
        def planillaDeAnticipo = Planilla.findByContratoAndTipoPlanilla(contrato,TipoPlanilla.findByCodigo("A"))
        println "anticipo  "+planillaDeAnticipo+"  fecha de pago " + planillaDeAnticipo.fechaPago
        def perAnticipo
        planilla.periodoAnticipo=null
//        if(!planilla.periodoAnticipo){
//            perAnticipo=PeriodosInec.findByFechaInicioLessThanAndFechaFinGreaterThan(planillaDeAnticipo.fechaPago,planillaDeAnticipo.fechaPago)
//            println "encontro periodo "+perAnticipo
//            if(!perAnticipo){
//                planilla.periodoAnticipo=planillaDeAnticipo.periodoAnticipo
//            }else{
//                def res = preciosService.verificaIndicesPeriodo(contrato,perAnticipo)
//                if(res.size()==0){
//                    planilla.periodoAnticipo=perAnticipo
//                }else{
//                    planilla.periodoAnticipo=planillaDeAnticipo.periodoAnticipo
//                }
//            }
//
//        }
        println "periodo de anticipo $planilla.periodoAnticipo id_planilla de anticipo: $planillaDeAnticipo.id, " +
                "liquidatcion: $liquidacion"
        if (liquidacion) {
            if (!contrato.fechaPedidoRecepcionContratista || !contrato.fechaPedidoRecepcionFiscalizador) {
                flash.message = "Por favor ingrese las fechas de pedido de recepción para generar la planilla final de avance (liquidación)"
                flash.clase = "alert-error"
                redirect(controller: "contrato", action: "fechasPedidoRecepcion", id: contrato.id)
                return
            }
        }

        def costo = Planilla.findByTipoPlanillaAndFechaIngresoLessThan(TipoPlanilla.findByCodigo("C"), planilla.fechaIngreso)
        if (costo) {
            if (costo.padreCosto == null) {
                costo.padreCosto = planilla
                costo.save(flush: true)
            }
        }
        def planillasAnteriores = Planilla.withCriteria {
            eq("contrato", contrato)
            and {
                or {
                    lt("fechaInicio", planilla.fechaInicio)
                    isNull("fechaInicio")
                }
                or {
                    eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
                    eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
                }
            }
            order("id", "asc")
        }

        println"planillas anteriores....... $planillasAnteriores"

        def avanceAnteriores = Planilla.withCriteria {
            eq("contrato", contrato)
            and {
                or {
                    lt("fechaInicio", planilla.fechaInicio)
                }
                or {
                    eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
                }
            }
            order("id", "asc")
        }
        println"avance anteriores....... $avanceAnteriores"

        def planillaAnterior = planillasAnteriores.get(planillasAnteriores.size() - 1)
        println "planillas " + planillasAnteriores

//        println periodos.id
//        println planillasAnteriores
//        println periodos.periodo.descripcion

        def periodos2 = []
//        println "periodos "+periodos
        def cs = FormulaPolinomicaContractual.findAllByContratoAndNumeroIlike(contrato, "C%", [sort: "numero"])
        def ps = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                and {
                    ne("numero", "P0")
                    ilike("numero", "p%")
                }
                order("numero", "asc")
            }
        }

        println("p " + ps)
        def pcs = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                or {
                    ilike("numero", "c%")
                    and {
                        ne("numero", "P0")
                        ne("numero", "p01")
                        ilike("numero", "p%")
                    }
                }
                order("numero", "asc")
            }
        }

        // TODO: eliminar la tabla y refencias a PeriodoPlanilla

        def periodos = PeriodoPlanilla.findAllByPlanillaInList(planillasAnteriores, [sort: "id"])
        periodos = periodos.sort { it.fechaIncio }
        periodos.each {
//            it.periodoReajuste=null
            if(!it.periodoReajuste){
                if(it.planilla.tipoPlanilla.codigo=="A"){
                    if(it.fechaIncio<=planillaDeAnticipo.fechaPresentacion && it.fechaFin>=planillaDeAnticipo.fechaPresentacion) {
                        perAnticipo=PeriodosInec.findByFechaInicioLessThanAndFechaFinGreaterThan(planillaDeAnticipo.fechaPago,planillaDeAnticipo.fechaPago)
                        println "encontro periodo anticipo"+perAnticipo
                        if(perAnticipo){
                            def res = preciosService.verificaIndicesPeriodo(contrato,perAnticipo)
                            println "verficacion de indices "+perAnticipo.descripcion
                            println "res "+res
                            if(res.size()==0){
                                //planilla.periodoAnticipo=perAnticipo
//                                it.periodoReajuste=perAnticipo
//                                it.planilla.imprimeReajueste=planilla
                                planillasService.calculaValores(it,cs,ps,pcs,obra,planillaDeAnticipo)

                            }
                        }
                    }
                }
                if(it.planilla.tipoPlanilla.codigo=="P"){      //avance de obra
                    def perReajuste=PeriodosInec.findByFechaInicioLessThanAndFechaFinGreaterThan(it.planilla.fechaPresentacion,it.planilla.fechaPresentacion)
                    println "encontro periodo avance $perReajuste"
                    if(perReajuste){
                        def res = preciosService.verificaIndicesPeriodo(contrato,perReajuste)
                        println "verficacion de indices "+perReajuste.descripcion
                        println "res "+res
                        if(res.size()==0){
                            //planilla.periodoAnticipo=perAnticipo
//                            it.periodoReajuste=perReajuste
//                            it.planilla.imprimeReajueste=planilla
                            planillasService.calculaValores(it,cs,ps,pcs,obra,planillaDeAnticipo)

                        }
                    }
                }
            }


        }


        def mensaje = " Se usó el periodo: "
        def bodyMultaRetraso = ""

        def totalContrato = contrato.monto
        def prmlMultaPlanilla = contrato.multaPlanilla
        def prmlMultaIncumplimiento = contrato.multaIncumplimiento
        def prmlMultaDisposiciones = contrato.multaDisposiciones
        def prmlMultaRetraso = contrato.multaRetraso

//        def pa = PeriodoPlanilla.findAllByPlanilla(planilla)
//        if (override && pa.size() > 0) {
//            println "Borrando periodos por alguna razon "
//            pa.each {
//                it.delete(flush: true)
//            }
//            pa = []
//        }
        def fechaFinPlanilla = planilla.fechaFin


     /*   if (pa.size() == 0) {   // crea el periodo de la planilla
            println "creando periodos de la planilla"
            def inicio = planilla.fechaInicio


            def fin = getLastDayOfMonth(inicio)

            println "inicio: ${inicio.format("dd-MM-yyyy")} fin: ${fin.format("dd-MM-yyyy")}  planilla: ${fechaFinPlanilla.format("dd-MM-yyyy")}"
            if (fin > fechaFinPlanilla) {
                fin = fechaFinPlanilla
                while (fin <= fechaFinPlanilla) {
                    def perInec = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(inicio, fin)
                    println "----------!!!perinec " + perInec + " inicio " + inicio.format("dd-MM-yyyy") + "  " + fin.format("dd-MM-yyyy")
                    def per = verificaIndicesAvance(pcs, perInec, 0,inicio,fin,contrato)
                    println "per despues de verficiar "+per
                    mensaje+=" "+per
                    def periodo = new PeriodoPlanilla([planilla: planilla, periodo: per, fechaIncio: inicio, fechaFin: fin, titulo: "AVANCE"])
                    if (!periodo.save(flush: true)) {
                        println "error al crear periodo avance " + periodo.errors
                    } else {
                        periodos2.add(periodo)
                    }
                    inicio = fin + 1
                    fin = getLastDayOfMonth(inicio)
                }
            } else {
                while (fin <= fechaFinPlanilla) {
                    def perInec = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(inicio, fin)
                    println "----------!!!perinec $perInec, inicio: ${inicio.format('dd-MM-yyyy')}   fin: ${fin.format('dd-MM-yyyy')}"
                    def per = verificaIndicesAvance(pcs, perInec, 0,inicio,fin,contrato)
                    println "despues del verificaIndices: cuando el finde periodo coincide con fin de planilla: $per"
                    mensaje+=" "+per
                    def periodo = new PeriodoPlanilla([planilla: planilla, periodo: per, fechaIncio: inicio, fechaFin: fin, titulo: "AVANCE"])

                    if (!periodo.save(flush: true)) {
                        println "error al crear periodo avance " + periodo.errors
                    } else {
                        periodos2.add(periodo)
                    }
                    inicio = fin + 1
                    fin = getLastDayOfMonth(inicio)
                }
                if (fin > fechaFinPlanilla && (fin.format("MM") == fechaFinPlanilla.format("MM"))) {
                    def perInec = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(inicio, fechaFinPlanilla)
                    def per = verificaIndices(pcs, perInec, 0)
                    def periodo = new PeriodoPlanilla([planilla: planilla, periodo: per, fechaIncio: inicio, fechaFin: fechaFinPlanilla, titulo: "AVANCE"])
                    if (!periodo.save(flush: true)) {
                        println "error al crear periodo avance " + periodo.errors
                    } else {
                        periodos2.add(periodo)
                    }
                }
            }
        } else {
            pa.each {perPl->
                //println " msn "+mensaje
                mensaje+=" "+perPl.periodo+", "
                println "********** mensaje: $mensaje"
            }
            def detalle = DetallePlanilla.findAllByPlanilla(planilla).sum { it.monto }
            println "monto detalle " + detalle + "   planilla " + planilla.valor
            if (detalle && detalle.toDouble().round(2) != planilla.valor) {
                println "son diferentes "
                pa.each {
                    it.fr = 0
                    it.p0 = 0
                    it.parcialPlanilla = 0
                    it.total = 0
                }
                periodos2 = pa
                println "periodos2 " + periodos2
                println "periodos " + periodos
                planilla.valor = detalle
                planilla.save(flush: true)
            } else {
                periodos += pa
            }


        }*/

        def tableWidth = 150 * (periodos.size() + periodos2.size()) + 100
        def smallTableWidth = 400

//        println "periodos  " + periodos.periodo
//        println "periodos  2v " + periodos2.periodo
        /////////////////*************************b0****************************/////////////////////////


        def reajustesPlanilla = ReajustePlanilla.findAllByPlanilla(planilla)


        def periodosNuevos = [:]
        def pagos = [:]
        def datos = [:]
        def datosFr = [:]
        def tams = [30, 8]
        def tamsFr = [30]

        reajustesPlanilla.each { rj ->
            def key = rj.periodo
            if(!periodosNuevos[key]) {
                periodosNuevos[key] = []
                pagos[key] = [:]
                pagos[key].indice = rj.periodoInec.fechaInicio.format("MMM-yyyy")
                pagos[key].valor = rj.valorReajustado
            }
            if(rj.periodo == 0) {
                periodosNuevos[key] += contrato.periodoInec.descripcion
                tams.add(10)
                tams.add(10)
                tamsFr.add(10)
            }
            if(rj.planillaReajustadaId != rj.planillaId) {
                pagos[key].fecha = rj.planillaReajustada?.fechaPago ? "Pago: "+rj.planillaReajustada.fechaPago.format("dd-MM-yyyy") : ""
            }

            periodosNuevos[key] += rj.mes
            tams.add(10)
            tams.add(10)
            tamsFr.add(10)
        }

        cs.each { c ->
            def key = c.id
            if(!datos[key]) {
                datos[key] = [:]
                datos[key].fp = c
                datos[key].detalles = [:]
            }
            reajustesPlanilla.each { rj ->
                def det = DetalleReajuste.findAllByReajustePlanillaAndFpContractual(rj, c)
                datos[key].detalles[rj.periodo] = det
            }
        }


        def totalesFr = [:]

        def totalesPeriodoFr = [:]
        def totalesCoeficientes = [:]

        ps.each { c ->
            def key = c.id
            if(!datosFr[key]) {
                datosFr[key] = [:]
                datosFr[key].fp = c
                datosFr[key].detalles = [:]
            }
            reajustesPlanilla.each { rj ->
                def det = DetalleReajuste.findAllByReajustePlanillaAndFpContractual(rj, c)
                datosFr[key].detalles[rj.periodo] = det

                if(!totalesFr[rj.periodo]) {
                    totalesFr[rj.periodo] = 0
                }

                if(!totalesPeriodoFr[rj.periodo]){
                    totalesPeriodoFr[rj.periodo] = 0
                }

                if(!totalesCoeficientes[rj.periodo]){
                    totalesCoeficientes[rj.periodo] = 0
                }
            }
        }

        def tablaBo = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px;'>"
        tablaBo += "<thead>"
        tablaBo += "<tr>"
        tablaBo += "<th colspan=\"2\">Cuadrilla Tipo</th>"

        periodosNuevos.each { per, meses->
            if(per == 0){
                tablaBo += "<th> OFERTA </th>"
                tablaBo += "<th class='nb'>" + meses[0] + "</th>"

                tablaBo += "<th> ANTICIPO </th>"
                tablaBo += "<th class='nb'>" + meses[1] + "</th>"
            }else{
                tablaBo += "<th> AVANCE </th>"
                tablaBo += "<th class='nb'>" + meses[0] + "</th>"
            }

        }

        tablaBo += "</tr>"
        tablaBo += "<tr>"
        tablaBo += "<th colspan='2'>Periodo utilizado</th>"

        pagos.each { per, pago ->
            if(per == 0) {
                tablaBo += "<th> </th>"
                tablaBo += "<th> </th>"
            }
            if(pago.fecha){
                tablaBo += "<th>" + pago.fecha ?: '' +  "</th>"
            }else{
                tablaBo += "<th> </th>"
            }
            tablaBo += "<th>" + "Indice: " + pago.indice ?: '' +  "</th>"
        }


        tablaBo += "</tr>"
        tablaBo += "</thead>"
        tablaBo += "<tbody>"

        def totalCoef = 0
        def totalAnticipo=0

        def coeficientes = 0
        def totalOferta = 0
        def totalAnticipoN = 0
        def totalAvance = 0
        def totalNuevoAvance = new double[30]

        tablaBo += "</tr>"
        tablaBo += "</thead>"
        tablaBo += "<tbody>"

        datos.each { k, v ->
            def c = v.fp
            def det = v.detalles

            tablaBo += "<tr>"
            tablaBo += "<th class='tal'>" + c.indice.descripcion + " (${c.numero})</th>"
            tablaBo += "<th class='number'>" + numero(c.valor) + "</th>"

            coeficientes += c.valor

            det.each { per, dt ->
                if (dt.size == 1) {
                    dt = dt.first()
                    if (per == 0) {
                        tablaBo += "<th class='number'>" + numero(dt.indiceOferta) + "</th>"
                        tablaBo += "<th class='number'>" + numero(dt.valorIndcOfrt) + "</th>"

                        totalOferta += dt.valorIndcOfrt
                        totalAnticipoN += dt.valorIndcPrdo
                    }else{
                        totalNuevoAvance[per] = (totalNuevoAvance[per] + dt.valorIndcPrdo)
                    }
                    tablaBo += "<th class='number'>" + numero(dt.indicePeriodo) + "</th>"
                    tablaBo += "<th class='number'>" + numero(dt.valorIndcPrdo) + "</th>"

                    totalAvance += dt.valorIndcPrdo
                } else {
                    println "Hay mas de 1 detalle para la fp ${c} periodo ${per}"
                }
            }
        }


        tablaBo += "</tbody><tfoot>"
        tablaBo += "<tr>" + "<th>TOTALES</th><th class='number'>${numero(coeficientes)}</th>"


        pagos.each {per, pago->
            if(per == 0){
                tablaBo += "<td></td><th class='number'>${numero(totalOferta)}</th>"
                tablaBo += "<td></td><th class='number'>${numero(totalAnticipoN)}</th>"
            }else{
                tablaBo += "<td></td><th class='number'>${numero(totalNuevoAvance[per])}</th>"
            }

        }

        tablaBo += "</tr></tfoot></table>"

        //////////////////////////**********************P0*********************///////////////////////////
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
        tablaP0 += '<tbody>'


        def totCrono = 0
        def totPlan = 0

        reajustesPlanilla.each {
            if(it.periodo == 0){
                tablaP0 += '<tr>'
                tablaP0 += "<th>ANTICIPO</th>"
                tablaP0 += "<th>${it.mes}</th>"
                tablaP0 += "<td></td>"
                tablaP0 += "<td></td>"
                tablaP0 += "<td></td>"
                tablaP0 += "<td></td>"
                tablaP0 += "<td class='number'>${numero(it.valorPo, 2)}</td>"
                tablaP0 += '</tr>'
            } else{
                tablaP0 += '<tr>'
                tablaP0 += "<th>${it.planilla?.tipoPlanilla?.nombre}</th>"
                tablaP0 += "<th>${it.mes}</th>"
                tablaP0 += "<td class='number'>${numero(it.parcialCronograma, 2)}</td>"
                tablaP0 += "<td class='number'>${numero(it.acumuladoCronograma, 2)}</td>"
                totCrono += it.acumuladoCronograma
                tablaP0 += "<td class='number'>${numero(it.parcialPlanillas, 2)}</td>"
                tablaP0 += "<td class='number'>${numero(it.acumuladoPlanillas, 2)}</td>"
                totPlan = it.acumuladoPlanillas
                tablaP0 += "<td class='number'>${numero(it.valorPo, 2)}</td>"
                tablaP0 += '</tr>'
            }

        }


        def multaInc = 0


        tablaP0 += '</tbody>'
        tablaP0 += '<tfoot>'
        //totales aqui
        tablaP0 += "<th>TOTAL</th>"
        tablaP0 += "<th> </th>"
        tablaP0 += "<td class='number'></td>"
        tablaP0 += "<td class='number'></td>"
        tablaP0 += "<td class='number'></td>"
        tablaP0 += "<th class='number'>${numero(totPlan, 2)}</th>"
        tablaP0 += "<td class='number'></td>"
        tablaP0 += '</tfoot>'
        tablaP0 += '</table>'

        //////////////////////////////////////////////***************** FR *********************************/////////////////

        def tr1 = "<tr>"
        def tr2 = "<tr>"
        def tr3 = "<tr>"
        def tablaFr = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;'>"
        tablaFr += '<thead>'
        tr1 = '<tr>'
        tr1 += '<th rowspan="2">Componentes</th>'
        tr3 += "<th>Anticipo</th>"

        def tdRowSpan = '<th colspan="' + (periodosNuevos.size()) + '">Periodo de variación y aplicación de fórmula polinómica</th>'

        periodosNuevos.each {per, meses ->
            if(per == 0){
                tr1 += "<th>OFERTA</th>"
                tr2 += "<th>" + meses[0] +"</th>"
                tr3 += "<th class='number'>" + numero(planilla.contrato.porcentajeAnticipo, 0) + "%</th>"

                tr1 += tdRowSpan
                tr2 += "<th>" + "ANTICIPO" + "<br/>" + meses[1] +"</th>"
                tr3 += "<th>ANTICIPO</th>"
            }else{
//                tr1 += "<th></th>"
                tr2 += "<th>" + meses[0] +"</th>"
                tr3 += "<th></th>"
            }
        }

        tr1 += "</tr>"
        tr2 += "</tr>"
        tr3 += "</tr>"
        tablaFr += tr1 + tr2 + tr3
        tablaFr += "</thead>"
        tablaFr += "<tbody>"


        def totalFr = 0

        def totalCoeficientesPr = 0

        datosFr.each { k, v ->
//            println("entro-->")
            def c = v.fp
            def det = v.detalles
            tablaFr += "<tr>"
            tablaFr += "<th class='tal'>" + c?.indice?.descripcion + " (${c?.numero})</th>"

            det.each { per, dt ->
                if (dt.size == 1) {
                    dt = dt.first()
                    if (per == 0) {
                        tablaFr += "<td class='number'><div>${numero(c.valor, 3)}</div><div class='bold'>${numero(dt.indiceOferta)}</div></td>"
                        tablaFr += "<td class='number'><div>${numero(dt.indicePeriodo, 3)}</div><div class='bold'>${numero(dt.valor)}</div></td>"

                        totalCoeficientesPr += c.valor
                        totalesPeriodoFr[per] +=  dt.indicePeriodo
                        totalesCoeficientes[per] += dt.valor
                    }else{
                        tablaFr += "<td class='number'><div>${numero(dt.indicePeriodo, 3)}</div><div class='bold'>${numero(dt.valor)}</div></td>"

                        totalesPeriodoFr[per] +=  dt.indicePeriodo
                        totalesCoeficientes[per] += dt.valor
                        totalesFr[per] += c.valor
                    }
                } else {
                    println "Hay mas de 1 detalle para la fp ${c} periodo ${per}"
                }
            }
            tablaFr += "</tr>"
        }

        tablaFr += "</tbody>"

        tr1 = "<tr>"
        tr2 = "<tr>"
        tr3 = "<tr>"
        def tr4 = "<tr>"
        def tr5 = "<tr>"
        def tr6 = "<tr>"
        def tr7 = "<tr>"

        tr1 += "<th rowspan='4'>1.000</th>"
        tr1 += "<th>F<sub>r</sub></th>"
        tr2 += "<th>F<sub>r</sub>-1</th>"
        tr3 += "<th>P<sub>0</sub></th>"
        tr4 += "<th>P<sub>r</sub>-P</th>"

        def reajusteTotal = 0

        periodosNuevos.eachWithIndex {per3, i->
            if(per3.key == 0){
                def fr1 = (totalesCoeficientes[per3.key] - 1).round(3)
                def frm = (totalesCoeficientes[per3.key]).round(3)
//                tr1 += "<th class='number'>${totalesCoeficientes[per3.key]}</th>"
                tr1 += "<th class='number'>${numero(frm,3)}</th>"
                tr2 += "<th class='number'>${numero(fr1,3)}</th>"
                tr3 += "<th class='number'>${numero(reajustesPlanilla[0].valorPo, 2)}</th>"
                def t = (reajustesPlanilla[0].valorPo * fr1).round(2)
                tr4 += "<th class='number'>${numero(t, 2)}</th>"
            }else{
                def fr1 = (totalesCoeficientes[per3.key] - 1).round(3)
                def frm = (totalesCoeficientes[per3.key]).round(3)
//                tr1 += "<th class='number'>${totalesCoeficientes[per3.key]}</th>"
                tr1 += "<th class='number'>${numero(frm,3)}</th>"
                tr2 += "<th class='number'>${numero(fr1,3)}</th>"
                tr3 += "<th class='number'>${numero(reajustesPlanilla[per3.key].valorPo, 2)}</th>"
                def t = (reajustesPlanilla[per3.key].valorPo * fr1).round(2)
                tr4 += "<th class='number'>${numero(t, 2)}</th>"
                reajusteTotal += t
            }
        }

        reajusteTotal = reajusteTotal.toDouble().round(2)

        def reajusteAnterior = (planillaAnterior.reajuste).toDouble().round(2)




        def rjTotalAnteriorD2 = 0
        def promedioActualD2 = 0
        def totalProcesadoD2 = 0

        pagos.each{ per, pago ->
            promedioActualD2 += pago.valor
        }

        def ultimoReajuste = reajustesPlanilla.last().planillaReajustada
        def planillasReajuste = []
        def valoresAnteriores = []
        def totalAnteriores = 0

//        println("ultimo " + ultimoReajuste)

        if(reajustesPlanilla.size() > 1){
            reajustesPlanilla.each { pl ->
                if(pl.planillaReajustada != ultimoReajuste){
                    planillasReajuste += pl.planillaReajustada
                }
            }
        }else{
            planillasReajuste += -1
        }

//        println("planilla reajuste " + planillasReajuste.last())

        if(planillasReajuste.last() != -1){
            valoresAnteriores = ReajustePlanilla.findAllByPlanilla(planillasReajuste.last())
            valoresAnteriores.each {
                totalAnteriores += it.valorReajustado
            }
        }else{
            println("anticipo")
        }

//        println("anteriores " + valoresAnteriores)
//        println("total " + totalAnteriores)

        totalProcesadoD2 = promedioActualD2 - totalAnteriores

        tr6 += "<th colspan='2'>REAJUSTE ANTERIOR</th>"
        tr7 +="<th colspan='2'>REAJUSTE TOTAL</th>"

        tr6 += "<th  class='number' colspan='${periodosNuevos.size()}'>" + numero(totalAnteriores, 2) + "</th>"
        tr7 += "<th  class='number' colspan='${periodosNuevos.size()}'>" + numero(promedioActualD2, 2) + "</th>"

        def tr8=""
        tr8 += "<tr><th colspan='2'>REAJUSTE A PLANILLAR</th><th colspan='${periodosNuevos.size()}' class='number'>" + numero(totalProcesadoD2,2) + "</th>"

        tr1 += "</tr>"
        tr2 += "</tr>"
        tr3 += "</tr>"
        tr4 += "</tr>"
        tr5 += "</tr>"
        tr6 += "</tr>"
        tr7 += "</tr>"
        tr7 += "</tr>"

        tablaFr += "<tfoot>"
        tablaFr += tr1 + tr2 + tr3 + tr4 + tr5 + tr6 + tr7+tr8
        tablaFr += "</tfoot></table>"
        ///////////////////////////////////////////************************************ multa retraso **********************////////////////////////////

        def multaPlanilla = MultasPlanilla.findAllByPlanilla(planilla)
        def pMl
        def tablaMlFs
        def tablaMl

        if(multaPlanilla.size() != 0 || (planilla.multaEspecial != 0 && planilla.multaEspecial != null)){


            multaPlanilla.each { mt ->


                if(mt.tipoMulta.id == 1){

                    pMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
                    pMl += "<tr>"
                    pMl += '<th class="tal">Fecha presentación planilla</th><td>' + planilla?.fechaPresentacion?.format("dd-MM-yyyy") + ' </td>'
                    pMl += "</tr>"
                    pMl += "<tr>"
                    pMl += '<th class="tal">Periodo planilla</th><td>' + planilla?.fechaInicio?.format("dd-MM-yyyy") + " a " + planilla?.fechaFin?.format("dd-MM-yyyy") + ' </td>'
                    pMl += "</tr>"
                    pMl += "<tr>"
                    pMl += '<th class="tal">Fecha máximo presentación</th> <td>' + mt.fechaMaxima.format("dd-MM-yyyy") + ' </td>'
                    pMl += "</tr>"
                    pMl += "<tr>"
                    pMl += '<th class="tal">Días de retraso</th> <td>' + numero(mt.dias, 0) + "</td>"
                    pMl += "</tr>"
                    pMl += "<tr>"
                    pMl += '<th class="tal">Multa</th> <td>' + mt.descripcion + "</td>"
                    pMl += "</tr>"
                    pMl += "<tr>"
                    pMl += '<th class="tal">Valor de la multa</th> <td>$'  + numero(mt.monto, 2) + "</td>"
                    pMl += "</tr>"
                    pMl += '</table>'
                }

                if(mt.tipoMulta.id == 2){

                    tablaMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
                    tablaMl += "<tr>"
                    tablaMl += '<th class="tal">Mes y Año</th><td>' + mt.periodo + ' </td>'
                    tablaMl += "</tr>"
                    tablaMl += "<tr>"
                    tablaMl += '<th class="tal">Cronograma</th><td>' + numero(mt.valorCronograma,2) + ' </td>'
                    tablaMl += "</tr>"
                    tablaMl += "<tr>"
                    tablaMl += '<th class="tal">Planillado</th> <td>' + numero(mt.planilla.valor,2) + ' </td>'
                    tablaMl += "</tr>"
                    tablaMl += "<tr>"
                    tablaMl += '<th class="tal">Multa</th> <td>' + mt.descripcion + "</td>"
                    tablaMl += "</tr>"
                    tablaMl += "<tr>"
                    tablaMl += '<th class="tal">Valor</th> <td>$'  + numero(mt.monto, 2) + "</td>"
                    tablaMl += "</tr>"
                    tablaMl += '</table>'


                }

                if(mt.tipoMulta.id == 3){

                    tablaMlFs = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
                    tablaMlFs += "<tr>"
                    tablaMlFs += '<th class="tal">Días</th> <td>' + numero(mt.dias, 0) + "</td>"
                    tablaMlFs += "</tr>"
                    tablaMlFs += "<tr>"
                    tablaMlFs += '<th class="tal">Multa</th> <td>' + mt.descripcion + "</td>"
                    tablaMlFs += "</tr>"
                    tablaMlFs += "<tr>"
                    tablaMlFs += '<th class="tal">Valor de la multa</th> <td>$' + '$' + numero(mt.monto, 2) + "</td>"
                    tablaMlFs += "</tr>"
                    tablaMlFs += '</table>'

                }
            }

        }


//        [tablaBo: tablaBo, planilla: planilla, tablaP0: tablaP0, tablaFr: tablaFr, tablaMl: tablaMl, pMl: pMl, tablaMlFs: tablaMlFs, liquidacion: liquidacion, mensaje:mensaje,tablaMultaUsuario:tablaMultaUsuario]
        [tablaBo: tablaBo, planilla: planilla, tablaP0: tablaP0, tablaFr: tablaFr,  liquidacion: liquidacion, mensaje:mensaje, pMl: pMl, tablaMlFs: tablaMlFs, tablaMl: tablaMl]
    }

    def deletePeriodosPlanilla() {
        println "delete periodos: " + params
        def planilla = Planilla.get(params.id)
        def periodos = PeriodoPlanilla.findAllByPlanilla(planilla, [sort: "id"])
        def cont = 0
        periodos.each {
            try {
                println "\tEliminando ${it.id}"
                it.delete(flush: true)
                cont++
            } catch (e) {
                println "error al eliminar: " + e.printStackTrace()
            }
        }
        render "Eliminados ${cont} periodos"
    }

    def anticipo() {
        println "anticipo.."
        def planilla = Planilla.get(params.id)
//        def override = false
//        if (session.override == "true" || session.override == true) {
//            override = true
//            session.override = false
//        }
//        println "over.... ${session.override}, override: $override"

        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def fechaOferta = planilla.contrato.oferta.fechaEntrega - 30


        //todo: revisar esto
        fechaOferta = planilla.contrato.periodoInec.fechaInicio

        def fechaAnticipo = planilla.fechaPresentacion
//        def periodos = PeriodoPlanilla.findAllByPlanilla(planilla, [sort: "id"])

        def perOferta = PeriodosInec.get(contrato.periodoInec.id)
        println "periodo oferta "+perOferta+"  "+contrato.periodoInec
        def perAnticipo
        if(!planilla.periodoAnticipo) {
            perAnticipo = PeriodosInec.list([sort: "fechaFin", order: "desc", "limit": 3]).first()

            planilla.periodoAnticipo
            planilla.save()
        } else
            perAnticipo=planilla.periodoAnticipo


//        println "planilla "+planilla+"  "+fechaOferta+"  "+fechaAnticipo+" pers "+periodos+"  "+perOferta+"  "+perAnticipo
        def pcs = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                or {
                    ilike("numero", "c%")
                    and {
                        ne("numero", "P0")
                        ne("numero", "p01")
                        ilike("numero", "p%")
                    }
                }
                order("numero", "asc")
            }
        }

        def cs = FormulaPolinomicaContractual.findAllByContratoAndNumeroIlike(contrato, "C%", [sort: "numero"])
//        def ps=  FormulaPolinomicaContractual.findAllByContratoAndNumeroIlike(contrato,"P%",[sort:"numero"])
        def ps = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                and {
                    ne("numero", "P0")
                    ilike("numero", "p%")
                }
                order("numero", "asc")
            }
        }

        if (ps.size() == 0 && cs.size() == 0 && pcs.size() == 0) {
            flash.message = "No se encontró la fórmula polinómica contractual. No se puede generar la planilla de anticipo."
            def link = createLink(controller: "contrato", action: "copiarPolinomica", id: contrato.id)
            flash.message += "<div style='margin-top:20px;'><a href='${link}' class='btn btn-danger'>Generar fórmula polinómica contractual</a></div>"
            redirect(action: "errores")
            return
        }

//        println "contrato: " + contrato
//        println "pcs: " + pcs
//        println "cs: " + cs
//        println "ps: " + ps


/*
        if (override && periodos.size() > 0) {
            println "Borrando periodos por alguna razon "
            periodos.each {
                it.delete(flush: true)
            }
            periodos = []
        }
*/

//        def erroresPeriodos = false
//        println "periodo oferta  1 "+perOferta
//        println "periodo anticipo  1 "+perAnticipo
        perOferta = verificaIndices(pcs, perOferta, 0)
        perAnticipo = verificaIndices(pcs, perAnticipo, 0)
//        println "periodo oferta "+perOferta
//        println "periodo anticipo "+perAnticipo

//        println "verifica oferta "
//        def res = preciosService.verificaIndicesPeriodo(contrato, perOferta)

//        def res = preciosService.verificaIndicesPeriodo(planilla.id, perOferta)
//        println "res "+res
//        def res2 = preciosService.verificaIndicesPeriodoTodo(planilla.contrato.id, perAnticipo.id)
//        if(res.size()+res2.size()>0){
//            res +=res2
//            def mesg ="<p>No se puede generar la planilla</p><br><ul style='margin-left:80px'>"
//            res.each {r->
//                mesg+="<li>No existe valor para: ${r['indcdscr']}, en el periodo: ${r['prindscr']}</li>"
//            }
//            mesg+="</ul><br/><a href='${g.createLink(controller: 'planilla',action: 'list',id:contrato.id)}' class='btn btn-default'>Volver</a>"
//            flash.message = mesg
//            redirect(action: "errores")
//            return
//        }


/*
        if (!(perOferta && perAnticipo)) {
            erroresPeriodos = false;
        }
*/

/*
        if (periodos.size() == 0 && perOferta && perAnticipo) {
            println "creando periodos "
//            pcs.each {c->
//            println " per o " + perOferta + "  per a " + perAnticipo
//            println " per o " + perOferta + "  per a " + perAnticipo

            if (perOferta) {
                def p1 = new PeriodoPlanilla([planilla: planilla, periodo: perOferta, fechaIncio: fechaOferta, fechaFin: getLastDayOfMonth(fechaOferta), titulo: "OFERTA"])
                if (!p1.save(flush: true)) {
                    println "p1 " + p1.errors
                }
                periodos.add(p1)
            } else {
                erroresPeriodos = true
            }
            if (perAnticipo) {
                def p2 = new PeriodoPlanilla([planilla: planilla, periodo: perAnticipo, fechaIncio: fechaAnticipo, fechaFin: getLastDayOfMonth(fechaAnticipo), titulo: "ANTICIPO"])
                if (!p2.save(flush: true)) {
                    println "p2 " + p2.errors
                }
                periodos.add(p2)
            } else {
                erroresPeriodos = true
            }
        }

        if (erroresPeriodos) {
            def link = g.link(controller: 'planilla', action: 'list', id: contrato.id, class: 'btn btn-danger') {
                "Regresar"
            }
            flash.message = "<p><ul>" + flash.message + "</ul></p><p>" + link + "</p>"
            redirect(action: "errores")
            return
        }
*/



        //***************************** B0 ****************************
        def reajustesPlanilla = ReajustePlanilla.findAllByPlanilla(planilla)

        def periodosNuevos = [:]
        def pagos = [:]
        def datos = [:]
        def datosFr = [:]
        def tams = [30, 8]
        def tamsFr = [30]

        reajustesPlanilla.each { rj ->
            def key = rj.periodo
            if(!periodosNuevos[key]) {
                periodosNuevos[key] = []
//                pagos[key] = ""
                pagos[key] = [:]
                pagos[key].indice = rj.periodoInec.fechaInicio.format("MMM-yyyy")
                pagos[key].valor = rj.valorReajustado
            }
            if(rj.periodo == 0) {
                periodosNuevos[key] += contrato.periodoInec.descripcion
                tams.add(10)
                tams.add(10)
                tamsFr.add(10)
            }
            if(rj.planillaReajustadaId != rj.planillaId) {
                pagos[key] = rj.planillaReajustada?.fechaPago ? "Pago: "+rj.planillaReajustada.fechaPago.format("dd-MM-yyyy") : ""
            }

            periodosNuevos[key] += rj.mes
            tams.add(10)
            tams.add(10)
            tamsFr.add(10)
        }
        println "... $periodosNuevos"
        cs.each { c ->
            def key = c.id
            if(!datos[key]) {
                datos[key] = [:]
                datos[key].fp = c
                datos[key].detalles = [:]
            }
            reajustesPlanilla.each { rj ->
                def det = DetalleReajuste.findAllByReajustePlanillaAndFpContractual(rj, c)
                datos[key].detalles[rj.periodo] = det
            }
        }


        def totalesFr = [:]

        def totalesPeriodoFr = [:]
        def totalesCoeficientes = [:]

        ps.each { c ->
            def key = c.id
            if(!datosFr[key]) {
                datosFr[key] = [:]
                datosFr[key].fp = c
                datosFr[key].detalles = [:]
            }
            reajustesPlanilla.each { rj ->
                def det = DetalleReajuste.findAllByReajustePlanillaAndFpContractual(rj, c)
                datosFr[key].detalles[rj.periodo] = det

                if(!totalesFr[rj.periodo]) {
                    totalesFr[rj.periodo] = 0
                }

                if(!totalesPeriodoFr[rj.periodo]){
                    totalesPeriodoFr[rj.periodo] = 0
                }

                if(!totalesCoeficientes[rj.periodo]){
                    totalesCoeficientes[rj.periodo] = 0
                }
            }
        }



        def tableWidth = 150 * periodosNuevos.size() + 400

        def tablaBo = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px;'>"
        tablaBo += "<thead>"
        tablaBo += "<tr>"
        tablaBo += "<th colspan=\"2\">Cuadrilla Tipo</th>"
//        println "periodos "+periodos
//        periodos.each {
//            it.total = 0
//            it.fr = 0
//            tablaBo += "<th>${it.titulo}</th>"
//            tablaBo += "<th class='nb'>" + fechaConFormato(it.fechaIncio, "MMM-yy") + "</th>"
//        }


        periodosNuevos.each { per, meses->
            if(per == 0){
                tablaBo += "<th> OFERTA </th>"
                tablaBo += "<th class='nb'>" + meses[0] + "</th>"

                tablaBo += "<th> ANTICIPO </th>"
                tablaBo += "<th class='nb'>" + meses[1] + "</th>"
            }else{
                tablaBo += "<th> AVANCE </th>"
                tablaBo += "<th class='nb'>" + meses[0] + "</th>"
            }

        }

        def coeficientes = 0
        def totalIndiceOferta = 0
        def totalAvance = 0
        def tAvance = []

        tablaBo += "</tr>"
        tablaBo += "</thead>"
        tablaBo += "<tbody>"

        datos.each { k, v ->
            def c = v.fp
            def det = v.detalles

            tablaBo += "<tr>"
                tablaBo += "<th class='tal'>" + c.indice.descripcion + " (${c.numero})</th>"
                tablaBo += "<th class='number'>" + numero(c.valor) + "</th>"

            coeficientes += c.valor

            det.each { per, dt ->
                if (dt.size == 1) {
                    dt = dt.first()
                    if (per == 0) {
                        tablaBo += "<th class='number'>" + numero(dt.indiceOferta) + "</th>"
                        tablaBo += "<th class='number'>" + numero(dt.valorIndcOfrt) + "</th>"

                        totalIndiceOferta += dt.valorIndcOfrt
                    }else{
                    }

                    tablaBo += "<th class='number'>" + numero(dt.indicePeriodo) + "</th>"
                    tablaBo += "<th class='number'>" + numero(dt.valorIndcPrdo) + "</th>"

                    totalAvance += dt.valorIndcPrdo
                } else {
                    println "Hay mas de 1 detalle para la fp ${c} periodo ${per}"
                }
            }
        }

        println("tavance " + tAvance)

        tablaBo += "</tbody><tfoot>"
        tablaBo += "<tr>" + "<th>TOTALES</th><th class='number'>${numero(coeficientes)}</th>"

        tablaBo += "<td></td><th class='number'>${numero(totalIndiceOferta)}</th>"
        tablaBo += "<td></td><th class='number'>${numero(totalAvance)}</th>"



        tablaBo += "</tr></tfoot></table>"


        //////////////////////////**********************P0*********************///////////////////////////




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
        tablaP0 += '<tbody>'


        reajustesPlanilla.each{
            if(it.periodo == 0){
                tablaP0 += "<th>ANTICIPO</th>"
                tablaP0 += "<th>${it.mes}</th>"
                tablaP0 += "<td></td>"
                tablaP0 += "<td></td>"
                tablaP0 += "<td></td>"
                tablaP0 += "<td></td>"
                tablaP0 += "<td class='number'>${numero(it.valorPo, 2)}</td>"
            }else{

            }
        }

        tablaP0 += '</tbody>'
        tablaP0 += '<tfoot>'
        //totales aqui
        tablaP0 += '</tfoot>'
        tablaP0 += '</table>'

        //////////////////////////////////////////////*****************FR*********************************/////////////////

        def tr1 = "<tr>"
        def tr2 = "<tr>"
        def tr3 = "<tr>"
        def tablaFr = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;'>"
        tablaFr += '<thead>'
        tr1 = '<tr>'
        tr1 += '<th rowspan="2">Componentes</th>'
        tr3 += "<th>Anticipo</th>"

        def tdRowSpan = '<th colspan="' + (2) + '">Periodo de variación y aplicación de fórmula polinómica</th>'

        periodosNuevos.each {per, meses ->
            if(per == 0){
                tr1 += "<th>OFERTA</th>"
                tr2 += "<th>" + meses[0] +"</th>"
                tr3 += "<th class='number'>" + numero(planilla.contrato.porcentajeAnticipo, 0) + "%</th>"

                tr1 += tdRowSpan
                tr2 += "<th>" + "ANTICIPO" + "<br/>" + meses[1] +"</th>"
                tr3 += "<th>ANTICIPO</th>"
            }else{
                println("error de columnas en impresion anticipo!")
            }
        }

        tr1 += "</tr>"
        tr2 += "</tr>"
        tr3 += "</tr>"
        tablaFr += tr1 + tr2 + tr3
        tablaFr += "</thead>"
        tablaFr += "<tbody>"

        def totalFr = 0

        def totalCoeficientesPr = 0

        datosFr.each { k, v ->
//            println("entro-->")
            def c = v.fp
            def det = v.detalles
            tablaFr += "<tr>"
            tablaFr += "<th class='tal'>" + c?.indice?.descripcion + " (${c?.numero})</th>"

            det.each { per, dt ->
                if (dt.size == 1) {
                    dt = dt.first()
                    if (per == 0) {
                        tablaFr += "<td class='number'><div>${numero(c.valor, 3)}</div><div class='bold'>${numero(dt.indiceOferta)}</div></td>"
                        tablaFr += "<td class='number'><div>${numero(dt.indicePeriodo, 3)}</div><div class='bold'>${numero(dt.valor)}</div></td>"

                        totalCoeficientesPr += c.valor
                        totalesPeriodoFr[per] +=  dt.indicePeriodo
                        totalesCoeficientes[per] += dt.valor
                    }else{
                        tablaFr += "<td class='number'><div>${numero(dt.indicePeriodo, 3)}</div><div class='bold'>${numero(dt.valor)}</div></td>"

                        totalesPeriodoFr[per] +=  dt.indicePeriodo
                        totalesCoeficientes[per] += dt.valor
                        totalesFr[per] += c.valor
                    }
                } else {
                    println "Hay mas de 1 detalle para la fp ${c} periodo ${per}"
                }
            }
            tablaFr += "</tr>"
        }

        tablaFr += "</tbody>"

        tr1 = "<tr>"
        tr2 = "<tr>"
        tr3 = "<tr>"
        def tr4 = "<tr>"
        def tr5 = "<tr>"

        tr1 += "<th rowspan='4'>1.000</th>"
        tr1 += "<th>F<sub>r</sub></th>"
        tr2 += "<th>F<sub>r</sub> -1</th>"
        tr3 += "<th>P<sub>0</sub></th>"
        tr4 += "<th>P<sub>r</sub> -Po</th>"

        def reajusteTotal = 0

        periodosNuevos.eachWithIndex {per3, i->
            if(per3.key == 0){
                def fr1 = (totalesCoeficientes[per3.key] - 1).round(3)
                def fr = (totalesCoeficientes[per3.key]).round(3)
                tr1 += "<th class='number'>${numero(fr,3)}</th>"
                tr2 += "<th class='number'>${numero(fr1)}</th>"
                tr3 += "<th class='number'>${numero(reajustesPlanilla[0].valorPo, 2)}</th>"
                def t = (reajustesPlanilla[0].valorPo * fr1).round(2)
                tr4 += "<th class='number'>${numero(t, 2)}</th>"
            }else{

            }
        }


        def num =reajustesPlanilla.first().id
        def tt = num - (pagos.size()-1)
        def tg = []

        while (tt != num){
            tg += tt
            tt++
        }
//
//        println("planilla actual " + reajustesPlanilla.first().id)
//        println("planillas Anteriores " + tg)

        def rjTotalAnterior = 0
        def promedioActual = 0
        def totalProcesado = 0

        tg.each{
            rjTotalAnterior += ReajustePlanilla.get(it).valorReajustado
        }

        pagos.each{ per, pago ->
            promedioActual += pago.valor
        }

        totalProcesado = promedioActual - rjTotalAnterior

        planilla.reajuste = reajusteTotal
        if (!planilla.save(flush: true)) {
            println "error planilla reajuste " + planilla.id
        }

//        tr5 += "<th colspan='2'>REAJUSTE TOTAL</th><th class='number'>" + numero(reajusteTotal, 2) + "</th>"
        tr5 += "<th colspan='2'>REAJUSTE TOTAL</th><th class='number'>" + numero(promedioActual, 2) + "</th>"

        tablaFr += "<tfoot>"
        tablaFr += tr1 + tr2 + tr3 + tr4 + tr5
        tablaFr += "</tfoot></table>"

        [tablaBo: tablaBo, planilla: planilla, tablaP0: tablaP0, tablaFr: tablaFr]
    }


    /** llamar con parametro de fprj.id */
    def resumen() {
        println "resumen de planillas.. params: $params"
        def planilla = Planilla.get(params.id)
        def rjpl
        def formula
        if(params.fprj) {
            rjpl = ReajustePlanilla.findAllByPlanillaAndFpReajuste(planilla, FormulaPolinomicaReajuste.get(params.fprj))
        } else if(params.rjpl) {
            rjpl = ReajustePlanilla.get(params.rjpl)
            formula = rjpl.fpReajuste.descripcion
        } else {
            rjpl = ReajustePlanilla.findAllByPlanilla(planilla).first()
//            println "pone valor en params.rjpl a: ${rjpl.id}"
            params.rjpl = rjpl.id
            formula = rjpl.fpReajuste.descripcion
        }

        def reajustesPlanilla = ReajustePlanilla.findAllByPlanillaAndFpReajuste(planilla, rjpl.fpReajuste, [sort: "periodo", order: "asc"])

        //***************************** B0 **************************** //anticipo

        def tbBo = planillasService.armaTablaFr(rjpl.planilla.id, rjpl.fpReajuste.id, 'c')
        def titlIndices = tbBo.pop()
        def titulos = tbBo.pop()
        println "resumen titulos: $titulos"

        def tablaBo = "<table class=\"table table-bordered table-striped table-condensed table-hover\">"
        def tr1 = "<thead><tr><th colspan='2'>Cuadrilla Tipo</th>"
        def tr2 = "<tr><th colspan='2'></th>"
        def separador = "nb"
        for(i in 0..titulos.size()-1){
            separador = separador == "nb" ? "" : "nb"
            tr1 += "<th ${(separador == 'nb') ? 'class = \'nb\'' : ''}  > ${titulos[i]}</th>"
            tr2 += "<th ${(separador == 'nb') ? 'class = \'nb\'' : ''}  > ${titlIndices[i]}</th>"
        }

        def coeficientes = 0.0
        def totalIndiceOferta = 0.0
        def totalAvance = []


        tablaBo += tr1 + "</tr>" + tr2


        tablaBo += "</tr>"
        tablaBo += "</thead>"
        tablaBo += "<tbody>"

        def planillas = (tbBo[0].size() - 5)/2
        tbBo.each { d ->
            tablaBo += "<tr>"
            tablaBo += "<td class='tal'> ${d.descripcion} (${d.numero})</th>"
            tablaBo += "<td class='number'> ${numero(d.coeficiente)} </td>"
            tablaBo += "<td class='number'> ${numero(d.indice)} </td>"
            tablaBo += "<td class='number'> ${numero(d.valor)} </td>"
            for(i in 1..planillas){
                if(!totalAvance[i-1]) totalAvance[i-1] = 0
                tablaBo += "<td class='number'> ${numero(d["indc$i"])} </td>"
                tablaBo += "<td class='number'> ${numero(d["vlor$i"])} </td>"
                totalAvance[i-1] += d["vlor$i"]
            }
            totalIndiceOferta += d.valor
            coeficientes += d.coeficiente
        }

        tablaBo += "</tbody><tfoot>"
        tablaBo += "<tr>" + "<th>TOTALES</th><th class='number'>${numero(coeficientes)}</th>"
        tablaBo += "<th></th><th class='number'>${numero(totalIndiceOferta)}</th>"
        for(i in 1..planillas) {
            tablaBo += "<th></th><th class='number'>${numero(totalAvance[i-1])}</th>"
        }
        tablaBo += "</tr></tfoot></table>"


        //////////////////////////**********************P0*********************///////////////////////////

//        def tablaP0 = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;' >"
        def tablaP0 = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:60%; margin-top:10px;' >"
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
        tablaP0 += '<tbody><tr>'

        def tbPo = planillasService.armaTablaPo(rjpl.planilla.id, rjpl.fpReajuste.id)
//        println "tbPo[0]---: ${tbPo[0].size()}, ${tbPo}"

        for(i in 0..tbPo.size()-1 ){
            if(tbPo[i].tipo.indexOf(' ') > 0) {
                tablaP0 += "<th>${tbPo[i].tipo.substring(0, tbPo[i].tipo.indexOf(' '))}</th>"
            } else {
                tablaP0 += "<th>${tbPo[i].tipo}</th>"
            }
            tablaP0 += "<th>${tbPo[i].mes}</th>"
            tablaP0 += "<td class='number'>${tbPo[i].crpa > 0 ? numero(tbPo[i].crpa, 2) : ''}</td>"
            tablaP0 += "<td class='number'>${tbPo[i].crac > 0 ? numero(tbPo[i].crac, 2) : ''}</td>"
            tablaP0 += "<td class='number'>${tbPo[i].plpa > 0 ? numero(tbPo[i].plpa, 2) : ''}</td>"
            tablaP0 += "<td class='number'>${tbPo[i].plac > 0 ? numero(tbPo[i].plac, 2) : ''}</td>"
            tablaP0 += "<td class='number'>${numero(tbPo[i].po, 2)}</td></tr>"
        }

        tablaP0 += '</tbody>'
        tablaP0 += '<tfoot>'
        //totales aqui
        tablaP0 += '</tfoot>'
        tablaP0 += '</table>'

        //////////////////////////////////////////////*****************FR*********************************/////////////////

        def tbFr = planillasService.armaTablaFr(rjpl.planilla.id, rjpl.fpReajuste.id, 'p')
        tbFr.pop()  // elimina los titulos de indices
        tbFr.pop()  // elimina los titulos

        def tr3 = "<tr>"
        def tablaFr = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='margin-top:10px;'>"
        tablaFr += '<thead>'
        tr1 = '<tr>'
        tr2 = '<tr><th></th>'
        tr1 += '<th>Fórmula Polinómica - Componentes</th>'

//        def tdRowSpan = "<th colspan='${titulos.size()/2}'>Periodo de variación y aplicación de fórmula polinómica</th>"
//        println "$titulosFr, size:${titulosFr.size()} 0... ${titulosFr.size()/2 - 1}"
/*
        for(i in 0..titulos.size()-1){
            separador = separador == "nb" ? "" : "nb"
            tr1 += "<th ${(separador == 'nb') ? 'class = \'nb\'' : ''}  > ${titulos[i]}</th>"
            tr2 += "<th ${(separador == 'nb') ? 'class = \'nb\'' : ''}  > ${titlIndices[i]}</th>"
        }
*/


        for(i in (0..(titulos.size() - 1)).step(2)){
            separador = separador == "nb" ? "" : "nb"
            tr1 += "<th>${titulos[i]},   ${titulos[i+1]}</th>"
            tr2 += "<th>${titlIndices[i]} &nbsp;&nbsp; ${titlIndices[i+1]}</th>"
        }

        tr1 += "</tr>"
        tr2 += "</tr>"

        println "... tr1: $tr1"
        println "... tr2: $tr2"

        tablaFr += tr1 + tr2
        tablaFr += "</thead>"
        tablaFr += "<tbody>"

        def total = []
        planillas = (tbFr[0].size() - 5)/2
        tbFr.each { d ->
            tablaFr += "<tr>"
            tablaFr += "<td class='tal'> ${d.descripcion} (${d.numero})</th>"
            tablaFr += "<td class='number'><div>${numero(d.coeficiente, 3)}</div><div class='bold'>${numero(d.indice)}</div></td>"
            for(i in 1..planillas){
                if(!total[i-1]) total[i-1] = 0.0
                tablaFr += "<td class='number'><div>${numero(d["indc$i"])}</div><div class='bold'>${numero(d["indc$i"]/d.indice*d.coeficiente)} </td>"
                total[i-1] += d["indc$i"]/d.indice*d.coeficiente
            }
        }


        tablaFr += "</tbody>"

        tr1 = "<tr>"
        tr2 = "<tr>"
        tr3 = "<tr>"
        def tr4 = "<tr>"
        def tr5 = "<tr>"

        tr1 += "<th rowspan='4'>1.000</th>"
        tr1 += "<th>F<sub>r</sub></th>"
        tr2 += "<th>F<sub>r</sub> -1</th>"
        tr3 += "<th>P<sub>0</sub></th>"
        tr4 += "<th>P<sub>r</sub> -Po</th>"

        def reajusteTotal = 0

        for (i in 0..planillas-1){
            tr1 += "<th class='number'>${numero(reajustesPlanilla[i].factor)}</th>"
            tr2 += "<th class='number'>${numero(reajustesPlanilla[i].factor - 1)}</th>"
            tr3 += "<th class='number'>${numero(reajustesPlanilla[i].valorPo, 2)}</th>"
            reajusteTotal += reajustesPlanilla[i].valorReajustado
            tr4 += "<th class='number'>${numero(reajustesPlanilla[i].valorReajustado, 2)}</th>"
        }

        def anterior = planillasService.reajusteAnterior(rjpl.planilla.id, rjpl.fpReajuste.id)
        tr5 += "<th colspan='${planillas + 1}'>REAJUSTE TOTAL</th><th class='number'>" + numero(reajusteTotal, 2) + "</th></tr>"
        if(planillas > 1){ // hay anticipo y otras planillas
            tr5 += "<tr><th colspan='${planillas + 1}'>REAJUSTE ANTERIOR</th><th class='number'>" +
                    numero(anterior, 2) + "</th>"
            tr5 += "<tr><th colspan='${planillas + 1}'>REAJUSTE A PLANILLAR</th><th class='number'>" +
                    numero((reajusteTotal - anterior), 2) + "</th>"
        }

        tablaFr += "<tfoot>"
        tablaFr += tr1 + tr2 + tr3 + tr4 + tr5
        tablaFr += "</tfoot></table>"

        /* ------------------------- multas ----------------------------------*/
        def multaPlanilla = MultasPlanilla.findAllByPlanilla(planilla)
        def pMl
        def tablaMlFs
        def tablaMl
//        println "multas ..... $multaPlanilla"

        if(multaPlanilla.size() > 0){
            multaPlanilla.each { mt ->
                if(mt.tipoMulta.id == 1){
                    pMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:50%; margin-top:10px;'>"
                    pMl += "<tr>"
                    pMl += '<th class="tal">Fecha presentación planilla</th><td>' + planilla?.fechaPresentacion?.format("dd-MM-yyyy") + ' </td>'
                    pMl += "</tr>"
                    pMl += "<tr>"
                    pMl += '<th class="tal">Periodo planilla</th><td>' + planilla?.fechaInicio?.format("dd-MM-yyyy") + " a " + planilla?.fechaFin?.format("dd-MM-yyyy") + ' </td>'
                    pMl += "</tr>"
                    pMl += "<tr>"
                    pMl += '<th class="tal">Fecha máximo presentación</th> <td>' + mt.fechaMaxima.format("dd-MM-yyyy") + ' </td>'
                    pMl += "</tr>"
                    pMl += "<tr>"
                    pMl += '<th class="tal">Días de retraso</th> <td>' + numero(mt.dias, 0) + "</td>"
                    pMl += "</tr>"
                    pMl += "<tr>"
                    pMl += '<th class="tal">Multa</th> <td>' + mt.descripcion + "</td>"
                    pMl += "</tr>"
                    pMl += "<tr>"
                    pMl += '<th class="tal">Valor de la multa</th> <td>$'  + numero(mt.monto, 2) + "</td>"
                    pMl += "</tr>"
                    pMl += '</table>'
                }

                if(mt.tipoMulta.id == 2){

                    tablaMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:50%; margin-top:10px;'>"
                    tablaMl += "<tr>"
                    tablaMl += '<th class="tal">Mes y Año</th><td>' + mt.periodo + ' </td>'
                    tablaMl += "</tr>"
                    tablaMl += "<tr>"
                    tablaMl += '<th class="tal">Cronograma</th><td>' + numero(mt.valorCronograma,2) + ' </td>'
                    tablaMl += "</tr>"
                    tablaMl += "<tr>"
                    tablaMl += '<th class="tal">Planillado</th> <td>' + numero(mt.planilla.valor,2) + ' </td>'
                    tablaMl += "</tr>"
                    tablaMl += "<tr>"
                    tablaMl += '<th class="tal">Multa</th> <td>' + mt.descripcion + "</td>"
                    tablaMl += "</tr>"
                    tablaMl += "<tr>"
                    tablaMl += '<th class="tal">Valor</th> <td>$'  + numero(mt.monto, 2) + "</td>"
                    tablaMl += "</tr>"
                    tablaMl += '</table>'
                }

                if(mt.tipoMulta.id == 3){

                    tablaMlFs = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:50%; margin-top:10px;'>"
                    tablaMlFs += "<tr>"
                    tablaMlFs += '<th class="tal">Días</th> <td>' + numero(mt.dias, 0) + "</td>"
                    tablaMlFs += "</tr>"
                    tablaMlFs += "<tr>"
                    tablaMlFs += '<th class="tal">Multa</th> <td>' + mt.descripcion + "</td>"
                    tablaMlFs += "</tr>"
                    tablaMlFs += "<tr>"
                    tablaMlFs += '<th class="tal">Valor de la multa</th> <td>$' + '$' + numero(mt.monto, 2) + "</td>"
                    tablaMlFs += "</tr>"
                    tablaMlFs += '</table>'
                }
            }

        }

        [tablaBo: tablaBo, planilla: planilla, tablaP0: tablaP0, tablaFr: tablaFr, pMl: pMl, tablaMl: tablaMl,
         tablaMlFs: tablaMlFs, formula: formula, params: params]
    }


    def verificaIndices(pcs, per, i) {
        if (!flash.message) {
            flash.message = ""
        }
        println "verifica indices!!! "
        def perNuevo = per
        if (per != null) {

            def val = ValorIndice.findAllByPeriodo(per)
            println "val para per "+per+" ==> "+val.size()
            if (val.size()<10) {
                perNuevo = PeriodosInec.findAllByFechaFinLessThan(per.fechaInicio,[sort:"fechaFin",limit: 3,"order":"desc"])
                println "no hay indices en "+per
                println "periodos anteriores "+perNuevo
                if(perNuevo.size()>0)
                    perNuevo=perNuevo.first()
                else
                    perNuevo=null
                println "per nuevo "+perNuevo
            }
            if(!perNuevo)
                return null

            if (per != perNuevo) {
                i++
                perNuevo = verificaIndices(pcs, perNuevo, i)
            }
        }
        return perNuevo
    }

    def verificaIndicesAvance(pcs, per, i,Date inicio,Date fin,contrato) {
        println "verificaIndicesAvance: pcs: $pcs, per: $per, inicio: ${inicio.format('dd-MM-yyyy')}, fin: $fin"
        if(inicio.date != 1){
            inicio = new Date().parse('dd-MM-yyyy', '1-' + (inicio.month + 1) + '-' + (inicio.year + 1900))
        }
        println "nuevo inicio: ${inicio.format('dd-MM-yyyy')}"
        if (!flash.message) {
            flash.message = ""
        }
        i=i+1
        if(i>10)
            return null
          println "verifica indices!!! periodo "+per
        def perNuevo = per
        if (per == null) {
//            inicio = inicio.minus(30)
            println "final: ${getLastDayOfMonth(inicio).date}, mes: ${inicio.month + 1}"
            def mesAnterior = inicio - 15
            inicio = new Date().parse('dd-MM-yyyy', "${getLastDayOfMonth(mesAnterior).date}-${mesAnterior.month + 1}-${(mesAnterior.year + 1900)}")
            fin = new Date().parse('dd-MM-yyyy', "1-${mesAnterior.month + 1}-${(mesAnterior.year + 1900)}")
            println "inicio: ${inicio.format('dd-MM-yyyy')}, fin: ${fin.format('dd-MM-yyyy')}"

            per =  PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(inicio, fin)
            per = verificaIndicesAvance(pcs,per,i,inicio,fin,contrato)
        }else {
            def res = preciosService.verificaIndicesPeriodo(contrato,per)
            //  println "res "+res
            if(res.size()>0){
                //    println "no paso la verificacion "
                per = verificaIndicesAvance(pcs,null,i,inicio,fin,contrato)
            }
        }
        return per
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

    private String fechaConFormato(fecha, formato) {
        def meses = ["", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"]
        def strFecha
        switch (formato) {
            case "MMM-yy":
                strFecha = meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yy")
                break;
            case "MMM-yyyy":
                strFecha = meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yyyy")
                break;
        }
        return strFecha
    }

    private String fechaConFormato(fecha) {
        return fechaConFormato(fecha, "MMM-yyyy")
    }

    private String numero(num, decimales) {
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

    private String numero(num) {
        return numero(num, 3)
    }


}
