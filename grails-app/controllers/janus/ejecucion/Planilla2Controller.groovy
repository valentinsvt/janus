package janus.ejecucion

import janus.Contrato
import janus.FormulaPolinomica
import janus.Obra
import janus.pac.CronogramaEjecucion
import janus.pac.PeriodoEjecucion

class Planilla2Controller extends janus.seguridad.Shield {

    def buscadorService
    def diasLaborablesService

    def errores() {
    }


    def pagos(){
        def campos = ["id": ["Obra", "string"],"descripcion":["Descripción","string"],"fechaPresentacion":["Fecha Presentación","string"],"memoPagoPlanilla":["Memo pago","string"]]
        [campos:campos]
    }


    def buscarPlanilla(){

        def extraObra = ""
        if (params.campos instanceof java.lang.String) {
            if (params.campos == "id") {
                if( params.criterios != ""){
                    def obras = Obra.findAll("from Obra where nombre like '%${params.criterios.toUpperCase()}%' or codigo like '%${params.criterios.toUpperCase()}%' ")
                    params.criterios = ""
                    obras.eachWithIndex { p, i ->
                        def concursos = janus.pac.Concurso.findAllByObraAndEstado(p, "R")
                        concursos.each { co ->
                            def ofertas = janus.pac.Oferta.findAllByConcurso(co)
                            ofertas.each { o->
                                def contratos = Contrato.findAllByOferta(o)
                                contratos.eachWithIndex{ cn,k->
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
                    if( params.criterios != ""){
                        def obras = Obra.findAll("from Obra where nombre like '%${params.criterios.toUpperCase()}%' or codigo like '%${params.criterios.toUpperCase()}%' ")

                        obras.eachWithIndex { ob, j ->
                            def concursos = janus.pac.Concurso.findAllByObraAndEstado(ob, "R")
                            concursos.each { co ->
                                def ofertas = janus.pac.Oferta.findAllByConcurso(co)
                                ofertas.each { o->
                                    def contratos = Contrato.findAllByOferta(o)
                                    contratos.eachWithIndex{ cn,k->
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

        def listaTitulos = ["OBRA","CONTRATO","DECRIPCION","FECHA PRESENTACION","MEMO PAGO","MONTO"]
        def listaCampos = ["id","contrato","descripcion","fechaPresentacion","memoPagoPlanilla","valor"]
        def funciones = [ ["closure": [codObra, "&"]],["closure": [contr, "&"]],null,null,null,null]
        def url = g.createLink(action: "buscarPlanilla", controller: "planilla2")
        def funcionJs = "function(){"
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
                def anchos = [35,35,60,35,35,30]
                /*anchos para el set column view en excel (no son porcentajes)*/
                redirect(controller: "reportes", action: "reporteBuscadorExcel", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Planilla", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "REPORTE DE PAGOS", anchos: anchos, extras: extras, landscape: true])
            } else {
                def lista = buscadorService.buscar(Planilla, "Planilla", "excluyente", params, true, extras)
                /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
                lista.pop()
                render(view: '../tablaBuscador', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "planilla2", numRegistros: numRegistros, funcionJs: funcionJs, width: 1800, paginas: 12])
            }

        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Planilla
            session.funciones = funciones
            def anchos = [15,15,40,10,10,10]
            /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Planilla", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "REPORTE DE CHEQUES PAGADOS", anchos: anchos, extras: extras, landscape: true])
        }
    }


    def liquidacion() {
//        println "params " + params
        def planilla = Planilla.get(params.id)
        def override = false
//        println planilla
//        println planilla.contrato
//        println planilla.contrato.oferta
//        println planilla.contrato.oferta.concurso
//        println planilla.contrato.oferta.concurso.obra
        def obra = planilla.contrato.oferta.concurso.obra
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
            }
            order("id", "asc")
        }
        def planillaAnterior = planillasAnteriores[planillasAnteriores.size() - 1]
//        println "planilla 2 " + planillaAnterior
        def periodosEjec = PeriodoEjecucion.findAllByObra(contrato.oferta.concurso.obra, [sort: "fechaFin"])
        def finalObraCrono = periodosEjec.pop().fechaFin
        def tarde = false
        def periodos = PeriodoPlanilla.findAllByPlanillaInList(planillasAnteriores, [sort: "id"])
        def fechaFinObra = obra.fechaFin

        def obraLiquidacion = Obra.withCriteria {
            eq("liquidacion", 1)
            eq("codigo", obra.codigo + "LQ")
        }
        if (obraLiquidacion.size() == 0) {
            println "error 1"
            flash.message = "No se encontró la obra de liquidación"
            redirect(action: "errores")
            return
        } else if (obraLiquidacion.size() > 1) {
            println "error 2"
            flash.message = "Se encontró más de una obra de liquidación"
            redirect(action: "errores")
            return
        } else if (obraLiquidacion.size() == 1) {
            obraLiquidacion = obraLiquidacion[0]
//            def pcs = FormulaPolinomicaContractual.withCriteria {
//                and {
//                    eq("contrato", contrato)
//                    or {
//                        ilike("numero", "c%")
//                        and {
//                            ne("numero", "P0")
//                            ne("numero", "p01")
//                            ilike("numero", "p%")
//                        }
//                    }
//                    order("numero", "asc")
//                }
//            }
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

//            def cs = FormulaPolinomica.findAllByObraAndNumeroIlike(obraLiquidacion, "C%", [sort: "numero"])
            def cs = FormulaPolinomica.withCriteria {
                eq("obra", obraLiquidacion)
                ilike("numero", "C%")
//                gt("valor", 0)
                order("numero")
            }
//            def cs = FormulaPolinomicaContractual.findAllByContratoAndNumeroIlike(contrato, "C%", [sort: "numero"])
//        def ps=  FormulaPolinomicaContractual.findAllByContratoAndNumeroIlike(contrato,"P%",[sort:"numero"])
//            def ps = FormulaPolinomicaContractual.withCriteria {
//                and {
//                    eq("contrato", contrato)
//                    and {
//                        ne("numero", "P0")
//                        ilike("numero", "p%")
//                    }
//                    order("numero", "asc")
//                }
//            }
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

//            println "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
//            println cs
//            println cMayor0
//            println cMayor0.size()
//            println ps
//            println pMayor0
//            println pMayor0.size()
//            println "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"

            if (cMayor0.size() == 0 || pMayor0.size() == 1) {
                println "NO hay la fp de liq"
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
//                println pl
                def pers = PeriodoPlanilla.findAllByPlanilla(pl)
                pers.each { p ->
//                    println "\t" + p
                    def periodo
                    if (tarde) {
//                        println "\t\t1: " + p.fechaIncio + "    " + p.fechaFin
                        periodo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(p.fechaIncio, p.fechaFin)
//                        println "Periodo q incluye " + p.fechaIncio.format("dd-MM-yyyy") + " y " + p.fechaFin.format("dd-MM-yyyy") + ": " + periodo
                    } else {
//                        println "\t\t2: " + pl.fechaPago
                        periodo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(pl.fechaPago, pl.fechaPago)
//                        println "Periodo q incluye " + pl.fechaPago.format("dd-MM-yyyy") + " y " + pl.fechaPago.format("dd-MM-yyyy") + ": " + periodo
                    }
//                    println "\t\t" + periodo
                    p.periodoLiquidacion = periodo
                    p.save(flush: true)
                    existe = verificaIndices(pcs, periodo, 0)
//                    println "\t\t" + existe
                    if (!periodo || !existe) {
                        errorNull = true
                    }
                    if (periodo != existe) {
                        error = true
//                        if (!error.contains(periodo.toString())) {
//                            error += "<li>No se encontraron todos los valores de índice para el periodo " + periodo.toString() + "</li>"
////                            flash.message += "<li>No se encontraron todos los valores de índice para el periodo " + periodo.toString() + "</li>"
//                        }
                    }
//                    println "\t\t\t" + error
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
//            println "AQUI"

            def tableWidth = 150 * periodos.size() + 400
            ///////////////////////////////////////////////////**********planillas*****************////////////////////////////////////////////////////////////

            def planillaAnticipo = Planilla.withCriteria {
                eq("contrato", contrato)
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("A"))
            }
            planillaAnticipo = planillaAnticipo.pop()
            def fechaOferta = planillaAnticipo.contrato.oferta.fechaEntrega - 30
            def fechaAnticipo = planillaAnticipo.fechaPresentacion
            def periodosAnticipo = PeriodoPlanilla.findAllByPlanilla(planillaAnticipo, [sort: "id"])
            def planillasAvances = Planilla.withCriteria {
                eq("contrato", contrato)
                eq("tipoPlanilla", TipoPlanilla.findByCodigo("P"))
            }
            def periodosAvances = PeriodoPlanilla.findAllByPlanillaInList(planillasAvances, [sort: "id"])
            def perOferta = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(fechaOferta, fechaOferta)
            def perAnticipo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(fechaAnticipo, fechaAnticipo)
//        println "planilla "+planilla+"  "+fechaOferta+"  "+fechaAnticipo+" pers "+periodos+"  "+perOferta+"  "+perAnticipo


            def tablaBo = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:&&px;'>"
            tablaBo += "<thead>"
            tablaBo += "<tr>"
            tablaBo += "<th colspan=\"2\">Cuadrilla Tipo</th>"
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
//                println "\t\t" + c
//                println c.class
//                println "\n"
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
//                    def retraso=0, multa=0
//                    if (p.parcialCronograma > p.parcialPlanilla) {
//                        def totalContrato = contrato.monto
//                        def prmlMulta = contrato.multaPlanilla
//                        def valorDia = p.parcialCronograma / p.dias
//                        retraso = ((p.parcialCronograma - p.parcialPlanilla) / valorDia).round(2)
//                        multa = ((totalContrato) * (prmlMulta / 1000) * retraso).round(2)
//                    }
////                    totalMultaRetraso+=multa
//                    bodyMultaRetraso += "<tr>"
//                    bodyMultaRetraso += "<th class='tal'>${fechaConFormato(p.fechaIncio,'MMM-yy')}</th>"
//                    bodyMultaRetraso += "<td class='number'>${p.parcialCronograma}</td>"
//                    bodyMultaRetraso += "<td class='number'>${p.parcialPlanilla}</td>"
//                    bodyMultaRetraso += "<td class='number'>${retraso}</td>"
//                    bodyMultaRetraso += "<td class='number'>${multa}</td>"
//                    bodyMultaRetraso += "</tr>"
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
//                            println "*****************************************************"
//                            println p.indice
//                            println per.periodoLiquidacion
//                            println ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodoLiquidacion)
//                            println "*****************************************************"
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
//                    println "error "+p.indice+" "+p.indice.id+"  "+per.periodo.id+"   "+vlin+"  "+vlinOferta+"  "+p.valor+"  "+"  "+per.periodo.id
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

//        tr5+="<th colspan='2'>REAJUSTE TOTAL</th><th class='number'>"+reajusteTotal+"</th>"
//
//        tablaFr+="<tfoot>"
//        tablaFr+=tr1+tr2+tr3+tr4+tr5
//        tablaFr+="</tfoot></table>"

            //////////////////////////////////////////fin anticipo//////////////////////////////////////////////////////////////////////////////////////////////////

            def tablaMl = ""

            tablaBo.replaceAll("&&", "" + tableWidth)
            tablaP0.replaceAll("&&", "" + tableWidth)
            tablaFr.replaceAll("&&", "" + tableWidth)

            return [tablaBo: tablaBo, planilla: planilla, tablaP0: tablaP0, tablaFr: tablaFr, tablaMl: tablaMl]
        }
    }

    def avance() {
//        println "params " + params
        def planilla = Planilla.get(params.id)
        def override = false
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato

        def prej = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaFin', order: 'desc'])
        def liquidacion = planilla.fechaFin >= prej[0].fechaFin

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

        def planillaAnterior = planillasAnteriores.get(planillasAnteriores.size() - 1)
//        println "planillas " + planillasAnteriores
        def periodos = PeriodoPlanilla.findAllByPlanillaInList(planillasAnteriores, [sort: "id"])
        periodos = periodos.sort { it.fechaIncio }
//        println periodos.id
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

        def bodyMultaRetraso = ""

        def totalContrato = contrato.monto
        def prmlMultaPlanilla = contrato.multaPlanilla
        def prmlMultaIncumplimiento = contrato.multaIncumplimiento
        def prmlMultaDisposiciones = contrato.multaDisposiciones
        def prmlMultaRetraso = contrato.multaRetraso

        def pa = PeriodoPlanilla.findAllByPlanilla(planilla)

        if (pa.size() == 0) {
            println "creando periodos "
            def inicio = planilla.fechaInicio
            def fin = getLastDayOfMonth(planilla.fechaInicio)
//            println "inicio " + inicio + " fin " + fin + "  planilla " + planilla.fechaFin
            if (fin > planilla.fechaFin) {
                fin = planilla.fechaFin
                while (fin <= planilla.fechaFin) {
                    def perInec = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(inicio, fin)
//                    println "----------!!!perinec " + perInec + " inicio " + inicio + "  " + fin
                    def per = verificaIndices(pcs, perInec, 0)
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
                while (fin <= planilla.fechaFin) {
                    def perInec = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(inicio, fin)
//                    println "----------!!!perinec " + perInec + " inicio " + inicio + "  " + fin
                    def per = verificaIndices(pcs, perInec, 0)
                    def periodo = new PeriodoPlanilla([planilla: planilla, periodo: per, fechaIncio: inicio, fechaFin: fin, titulo: "AVANCE"])
                    if (!periodo.save(flush: true)) {
                        println "error al crear periodo avance " + periodo.errors
                    } else {
                        periodos2.add(periodo)
                    }
                    inicio = fin + 1
                    fin = getLastDayOfMonth(inicio)
                }
                if (fin > planilla.fechaFin && (fin.format("MM") == planilla.fechaFin.format("MM"))) {
                    def perInec = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(inicio, planilla.fechaFin)
                    def per = verificaIndices(pcs, perInec, 0)
                    def periodo = new PeriodoPlanilla([planilla: planilla, periodo: per, fechaIncio: inicio, fechaFin: planilla.fechaFin, titulo: "AVANCE"])
                    if (!periodo.save(flush: true)) {
                        println "error al crear periodo avance " + periodo.errors
                    } else {
                        periodos2.add(periodo)
                    }
                }
            }
        } else {
            def detalle = DetallePlanilla.findAllByPlanilla(planilla).sum { it.monto }
            println "monto detalle " + detalle + "   planilla " + planilla.valor
            if (detalle.toDouble().round(2) != planilla.valor) {
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


        }

        def tableWidth = 150 * (periodos.size() + periodos2.size()) + 100
        def smallTableWidth = 400

//        println "periodos  " + periodos
//        println "periodos  2v " + periodos2
        /////////////////*************************b0****************************/////////////////////////

        def tablaBo = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px;'>"
        tablaBo += "<thead>"
        tablaBo += "<tr>"
        tablaBo += "<th colspan=\"2\">Cuadrilla Tipo</th>"
        periodos.each {
            tablaBo += "<th>${it.titulo}</th>"
            tablaBo += "<th class='nb'>" + fechaConFormato(it.fechaIncio) + "</th>"
        }
        periodos2.each {
            tablaBo += "<th>${it.titulo}</th>"
            tablaBo += "<th class='nb'>" + fechaConFormato(it.fechaIncio) + "</th>"
        }

        tablaBo += "</tr>"
        tablaBo += "</thead>"
        tablaBo += "<tbody>"

        def totalCoef = 0
        cs.each { c ->
            tablaBo += "<tr>"
            tablaBo += "<th class='tal'>" + c.indice.descripcion + " (${c.numero})</th>"
            tablaBo += "<th class='number'>" + numero(c.valor) + "</th>"
            totalCoef += c.valor
            periodos.each { p ->
                def valor = ValorIndice.findByPeriodoAndIndice(p.periodo, c.indice).valor
                if (!valor) {
                    println "wtf no valor " + p.periodo + "  " + c.indice
                    valor = 0
                }
                tablaBo += "<td class='number'>" + numero(valor, 2) + "</td>"
//                valor = (valor*c.valor).round(3)
                def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${p.planilla.id} and periodoIndice =${p.periodo.id} and formulaPolinomica=${c.id}")
                if (vlrj.size() > 0) {
                    valor = vlrj.pop().valor
                } else {
                    println "error wtf no hay vlrj => from ValorReajuste where obra=${obra.id} and planilla=${p.planilla.id} and periodoIndice =${p.periodo.id} and formulaPolinomica=${c.id}"
                    valor = -1
                }
                tablaBo += "<td class='number'>" + numero(valor) + "</td>"
            }
            periodos2.each { p ->
                def valor = ValorIndice.findByPeriodoAndIndice(p.periodo, c.indice).valor
                if (!valor) {
                    println "wtf no valor " + p.periodo + "  " + c.indice
                    valor = 0
                }

                p.total += (valor * c.valor).round(3)
                p.save(flush: true)

                tablaBo += "<td class='number'>" + numero(valor) + "</td>"
                valor = (valor * c.valor).round(3)
                def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${p.periodo.id} and formulaPolinomica=${c.id}")
                if (vlrj.size() > 0) {
                    vlrj = vlrj.pop()
                    if (vlrj != valor) {
                        vlrj.valor = valor
                        if (!vlrj.save(flush: true)) {
                            println "error vlrj update " + vlrj.errors
                        }
                    }
                } else {
                    vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: p.periodo, formulaPolinomica: c, valor: valor])
                    if (!vlrj.save(flush: true)) {
                        println "error vlrj insert " + vlrj.errors
                    }
                }
                tablaBo += "<td class='number'>" + numero(valor) + "</td>"
            }

            tablaBo += "</tr>"
        }
        tablaBo += "</tbody><tfoot>"
        tablaBo += "<tr>" + "<th>TOTALES</th><th class='number'>${numero(totalCoef)}</th>"
        periodos.each { p ->
            tablaBo += "<td></td><th class='number'>${numero(p.total)}</th>"
        }
        periodos2.each { p ->
            tablaBo += "<td></td><th class='number'>${numero(p.total)}</th>"
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
        def periodosEjecucion = PeriodoEjecucion.withCriteria {
            and {
                eq("obra", obra)
                order("fechaInicio", "asc")
            }
        }

        periodos2.each { p ->
//            println "per "+p.id
            def dias = p.fechaFin - p.fechaIncio + 1
            def pers = periodosEjecucion.findAll {
                (it.fechaInicio <= p.fechaIncio && p.fechaIncio <= it.fechaFin) || (it.fechaInicio <= p.fechaFin && p.fechaFin <= it.fechaFin)
            }
//            println "pers ! "+pers+ " "+dias +" = "+p.fechaFin+" - "+p.fechaIncio+" +1"
            def parcialCronograma = 0, diasTotal = 0
            pers.each { pr ->
                def pc = CronogramaEjecucion.findAllByPeriodo(pr).sum { it.precio }
                def diasPr = pr.fechaFin - pr.fechaInicio + 1
                def dias2
                if (p.fechaIncio >= pr.fechaInicio && p.fechaFin <= pr.fechaFin) {
                    dias2 = dias
//                    println "dias 2 1 "+dias2+" == dias"
                } else {
                    if (p.fechaIncio < pr.fechaInicio) {
                        dias2 = p.fechaFin - pr.fechaInicio + 1
//                        println "dias 2 2 "+dias2+" = "+p.fechaFin+" - "+pr.fechaInicio+" +1"
                    } else {
                        dias2 = pr.fechaFin - p.fechaIncio + 1
//                        println "dias 2 3 "+dias2+" = "+pr.fechaFin+" - "+p.fechaIncio+" +1"
                    }
                }
                diasTotal += dias2
                parcialCronograma += ((pc / diasPr) * dias2).round(2)
//                println "parcial crono += "+((pc/diasPr)*dias2).round(2)+"   = "+parcialCronograma+"     ("+pc+")   "+diasPr

            }
            p.parcialCronograma = parcialCronograma.toDouble().round(2)
            p.dias = diasTotal
            def totalPlanilla = planilla.valor
//            println "valor planilla "+planilla.valor
            def diasPlanilla = planilla.fechaFin - planilla.fechaInicio + 1
            def totDiario = totalPlanilla / diasPlanilla
            p.parcialPlanilla = (totDiario * dias).toDouble().round(2)
            p.p0 = Math.max(p.parcialCronograma, p.parcialPlanilla)
//            println "p0 "+p.p0
            if (!p.save(flush: true)) {
                println "error calculo p0 " + p.errors
            }
        }
        def act = 0
        def act2 = 0
        def diasTot = 0, totCrono = 0, totPlan = 0, totalMultaRetraso = 0
        (periodos + periodos2).each { p ->
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

                    if (p.planilla == planilla) {
                        def retraso = 0, multa = 0
                        if (p.parcialCronograma > p.parcialPlanilla) {
                            def valorDia = p.parcialCronograma / p.dias
                            retraso = ((p.parcialCronograma - p.parcialPlanilla) / valorDia).round(2)
                            multa = ((totalContrato) * (prmlMultaIncumplimiento / 1000) * retraso).round(2)
                        }
                        totalMultaRetraso += multa
                        bodyMultaRetraso += "<tr>"
                        bodyMultaRetraso += "<th class='tal'>${fechaConFormato(p.fechaIncio, 'MMM-yy')}</th>"
                        bodyMultaRetraso += "<td class='number'>${numero(p.parcialCronograma, 2)}</td>"
                        bodyMultaRetraso += "<td class='number'>${numero(p.parcialPlanilla, 2)}</td>"
                        bodyMultaRetraso += "<td class='number'>${numero(retraso, 2)}</td>"
                        bodyMultaRetraso += "<td class='number'>${numero(multa, 2)}</td>"
                        bodyMultaRetraso += "</tr>"
                    }
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

        //////////////////////////////////////////////***************** FR *********************************/////////////////

        def tr1 = "<tr>"
        def tr2 = "<tr>"
        def tr3 = "<tr>"
        def tablaFr = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;'>"
        tablaFr += '<thead>'
        tr1 = '<tr>'
        tr1 += '<th rowspan="2">Componentes</th>'
        tr3 += "<th>Anticipo</th>"

        def tdRowSpan = '<th colspan="' + (periodos.size() + periodos2.size() - 1) + '">Periodo de variación y aplicación de fórmula polinómica</th>'
        (periodos + periodos2).eachWithIndex { p, i ->
            if (i == 0) { //oferta
                tr1 += "<th>" + p.titulo + "</th>"
                tr2 += "<th>" + fechaConFormato(p.fechaIncio, "MMM-yy") + "</th>"
                tr3 += "<th class='number'>" + numero(p.planilla.contrato.porcentajeAnticipo, 0) + "%</th>"
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

        ps.eachWithIndex { p, i ->
            tablaFr += "<tr>"
            tablaFr += "<th class='tal'>" + p?.indice?.descripcion + " (${p?.numero})</th>"
            def vlinOferta = null
            periodos.eachWithIndex { per, j ->
                if (j == 0) { // es la oferta
                    def valor = 0
                    if (i == 0) { //es mano de obra
                        vlinOferta = per.total
                        tablaFr += "<td class='number'><div>${numero(p.valor,3)}</div><div class='bold'>${numero(per.total)}</div></td>"
                        valor = per.total
                    } else {
                        vlinOferta = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
                        tablaFr += "<td class='number'><div>${numero(p.valor,3)}</div><div class='bold'>${numero(vlinOferta, 3)}</div></td>"
                        valor = vlinOferta
                    }


                } else {
                    def vlin, dec=3
                    if (i == 0) {
                        vlin = per.total
//                        dec = 3
                    } else {
                        vlin = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
//                        dec = 2
                    }
//                    println "error "+p.indice+" "+p.indice.id+"  "+per.periodo.id+"   "+vlin+"  "+vlinOferta+"  "+p.valor+"  "+"  "+per.periodo.id
                    def valor = (vlin / vlinOferta * p.valor).round(3)

                    tablaFr += "<td class='number'><div>${numero(vlin, dec)}</div><div class='bold'>${numero(valor)}</div></td>"
                }
            }

            periodos2.eachWithIndex { per, j ->

                def vlin
                if (i == 0) {
                    vlin = per.total
                } else {
                    vlin = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
                }
//                    println "error "+p.indice+" "+p.indice.id+"  "+per.periodo.id+"   "+vlin+"  "+vlinOferta+"  "+p.valor+"  "+"  "+per.periodo.id
                def valor = (vlin / vlinOferta * p.valor).round(3)

                def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${per.periodo.id} and formulaPolinomica=${p.id}")
                if (vlrj.size() > 0) {
                    vlrj = vlrj.pop()
                    if (vlrj != valor) {
                        vlrj.valor = valor
                        if (!vlrj.save(flush: true)) {
                            println "error vlrj update " + vlrj.errors
                        }
                    }
                } else {
                    vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: per.periodo, formulaPolinomica: p, valor: valor])
                    if (!vlrj.save(flush: true)) {
                        println "error vlrj insert " + vlrj.errors
                    }
                }
//                println "per "+per.fr
                per.fr += valor
                if (!per.save(flush: true)) {
                    println "error fr " + per.errors
                }
                tablaFr += "<td class='number'><div>${numero(vlin, 2)}</div><div class='bold'>${numero(valor)}</div></td>"

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

        (periodos + periodos2).eachWithIndex { per, i ->
            if (i > 0) {
                def fr1 = (per.fr - 1).round(3)
                tr1 += "<th class='number'>${numero(per.fr)}</th>"
                tr2 += "<th class='number'>${numero(fr1)}</th>"
                tr3 += "<th class='number'>${numero(per.p0, 2)}</th>"
                def t = (per.p0 * fr1).round(2)
                tr4 += "<th class='number'>${numero(t, 2)}</th>"
                reajusteTotal += t
            }
        }

        reajusteTotal = reajusteTotal.toDouble().round(2)

        def reajusteAnterior = (planillaAnterior.reajuste).toDouble().round(2)

        tr5 += "<th colspan='2'>REAJUSTE TOTAL</th><th colspan='${periodos.size() + periodos2.size() - 1}' class='number'>" + numero(reajusteTotal, 2) + "</th>"
        tr6 += "<th colspan='2'>REAJUSTE ANTERIOR</th><th colspan='${periodos.size() + periodos2.size() - 1}' class='number'>" + numero(reajusteAnterior, 2) + "</th>"
        tr7 += "<th colspan='2'>REAJUSTE A PLANILLAR</th><th colspan='${periodos.size() + periodos2.size() - 1}' class='number'>" + numero((reajusteTotal - reajusteAnterior).toDouble().round(2), 2) + "</th>"

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
        ///////////////////////////////////////////************************************ multa retraso **********************////////////////////////////

        def tablaMl

        def multaRetraso = 0, multaIncumplimiento = 0

        if (!liquidacion) {
            tablaMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
            tablaMl += '<thead>'
            tablaMl += '<tr>'
            tablaMl += '<th>Mes y año</th>'
            tablaMl += '<th>Cronograma</th>'
            tablaMl += '<th>Planillado</th>'
            tablaMl += '<th>Retraso</th>'
            tablaMl += '<th>Multa</th>'
            tablaMl += '</tr>'
            tablaMl += '</thead>'
            tablaMl += '<tbody>'
            tablaMl += bodyMultaRetraso
            tablaMl += '</tbody>'
            tablaMl += '<tfoot>'
            tablaMl += '<tr>'
            tablaMl += '<th>TOTAL</th>'
            tablaMl += '<td colspan="3"></td>'
            tablaMl += "<th class='number'>${numero(totalMultaRetraso, 2)}</th>"
            tablaMl += '</tr>'
            tablaMl += '</tfoot>'
            tablaMl += '</table>'
            multaIncumplimiento = totalMultaRetraso
        } else {
            def fechaFinFiscalizador = contrato.fechaPedidoRecepcionFiscalizador
            def retraso = fechaFinFiscalizador - prej[0].fechaFin + 1
            if (retraso < 0) {
                retraso = 0
            }
            totalMultaRetraso = retraso * ((prmlMultaRetraso / 1000) * totalContrato)
            tablaMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
            tablaMl += '<tr>'
            tablaMl += '<th class="tal">Fecha final de la obra (cronograma)</th> <td>' + prej[0].fechaFin.format("dd-MM-yyyy") + '</td>'
            tablaMl += '</tr>'
            tablaMl += '<tr>'
            tablaMl += '<th class="tal">Fecha pedido recepción fiscalizador</th> <td>' + fechaFinFiscalizador.format("dd-MM-yyyy") + '<td>'
            tablaMl += '</tr>'
            tablaMl += "<tr>"
            tablaMl += '<th class="tal">Días de retraso</th> <td>' + retraso + '</td>'
            tablaMl += "</tr>"
            tablaMl += "<tr>"
            tablaMl += '<th class="tal">Multa</th> <td>' + prmlMultaRetraso + "&#8240; de \$" + numero(totalContrato, 2) + "</td>"
            tablaMl += "</tr>"
            tablaMl += "<tr>"
            tablaMl += '<th class="tal">Total multa</th> <td>$' + numero(totalMultaRetraso, 2) + '</td>'
            tablaMl += "</tr>"
            tablaMl += '</table>'
            multaRetraso = totalMultaRetraso
        }

        ///////////////////////////////////////////************************************ multa no presentacion planilla **********************////////////////////////////

        def totalMultaPlanilla = 0
        def diasMax = 5
        def fechaFinPer = planilla.fechaFin
        def fechaMax = fechaFinPer

//        def noLaborables = ["Sat", "Sun"]
//
////            println fechaMax
//        diasMax.times {
//            fechaMax++
////                println fechaMax
//            def fmt = new java.text.SimpleDateFormat("EEE", new Locale("en"))
//            while (noLaborables.contains(fmt.format(fechaMax))) {
////                    println fmt.format(fechaMax)
//                fechaMax++
////                    println fechaMax
//            }
//        }
//            println "***** "+fechaMax

        /* aqui esta con el nuevo service para calcular dias laborables con la tabla */
        def res = diasLaborablesService.diasLaborablesDesde(fechaFinPer, diasMax)
        if (res[0]) {
            fechaMax = res[1]
        } else {
            fechaMax = null
        }

        def fechaPresentacion = planilla.fechaPresentacion
        def retraso = fechaPresentacion - fechaMax + 1

        def multaPlanilla = 0
        if (retraso > 0) {
//            totalMulta = (totalContrato) * (prmlMulta / 1000) * retraso
//            multaPlanilla = (PeriodoPlanilla.findAllByPlanilla(planilla).sum {
//                it.parcialCronograma
//            }) * (prmlMultaPlanilla / 1000) * retraso
            multaPlanilla = (prmlMultaPlanilla / 1000) * planilla.valor
        } else {
            retraso = 0
        }

        def pMl = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
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
        pMl += '<th class="tal">Multa</th> <td>' + prmlMultaPlanilla + "&#8240; de \$" + numero(planilla.valor, 2) + "</td>"
        pMl += "</tr>"
        pMl += "<tr>"
        pMl += '<th class="tal">Total multa</th> <td>$' + numero(multaPlanilla, 2) + "</td>"
        pMl += "</tr>"
        pMl += '</table>'
        ///////////////////////////////////////////************************************ multa no acatar disposiciones fiscalizador **********************////////////////////////////
        def diasNoAcatar = planilla.diasMultaDisposiciones
        def multaDisposiciones = totalContrato * diasNoAcatar * (prmlMultaDisposiciones / 1000)
        def tablaMlFs = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${smallTableWidth}px; margin-top:10px;'>"
        tablaMlFs += "<tr>"
        tablaMlFs += '<th class="tal">Días</th> <td>' + diasNoAcatar + "</td>"
        tablaMlFs += "</tr>"
        tablaMlFs += "<tr>"
        tablaMlFs += '<th class="tal">Multa</th> <td>' + prmlMultaDisposiciones + "&#8240; de \$" + numero(totalContrato, 2) + "</td>"
        tablaMlFs += "</tr>"
        tablaMlFs += "<tr>"
        tablaMlFs += '<th class="tal">Total multa</th> <td>$' + numero(multaDisposiciones, 2) + "</td>"
        tablaMlFs += "</tr>"
        tablaMlFs += '</table>'
        ///////////////////////////////////////////************************************fin**********************////////////////////////////

        planilla.reajuste = reajusteTotal
        planilla.multaPlanilla = multaPlanilla
        planilla.multaRetraso = multaRetraso
        planilla.multaIncumplimiento = multaIncumplimiento
        planilla.multaDisposiciones = multaDisposiciones

        def valorAnt = 0
        def anterior = 0
        if (avanceAnteriores.size() > 0) {
            valorAnt = avanceAnteriores.sum { it.valor }
            anterior = avanceAnteriores.sum { it.descuentos }
        }
        def prej2 = PeriodoEjecucion.findAllByObra(obra, [sort: 'fechaInicio', order: "asc"])

        if (planilla.fechaFin >= prej2.last().fechaFin) {
            planilla.descuentos = (contrato.anticipo - anterior).toDouble().round(2)
        } else {
            planilla.descuentos = (((valorAnt + planilla.valor) / contrato.monto) * contrato.anticipo - anterior).toDouble().round(2)
        }

        if (!planilla.save(flush: true)) {
            println "error planilla reajuste " + planilla.id
        }

        [tablaBo: tablaBo, planilla: planilla, tablaP0: tablaP0, tablaFr: tablaFr, tablaMl: tablaMl, pMl: pMl, tablaMlFs: tablaMlFs, liquidacion: liquidacion]
    }

    def anticipo() {

        def planilla = Planilla.get(params.id)
        def override = false
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def fechaOferta = planilla.contrato.oferta.fechaEntrega - 30
        def fechaAnticipo = planilla.fechaPresentacion
        def periodos = PeriodoPlanilla.findAllByPlanilla(planilla, [sort: "id"])

        def perOferta = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(fechaOferta, fechaOferta)
        def perAnticipo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(fechaAnticipo, fechaAnticipo)
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

        if (override && periodos.size() > 0) {
            println "Borrando periodos por alguna razon "
            periodos.each {
                it.delete(flush: true)
            }
            periodos = []
        }



        if (periodos.size() == 0) {
            println "creando periodos "
//            pcs.each {c->
            println " per o " + perOferta + "  per a " + perAnticipo
            perOferta = verificaIndices(pcs, perOferta, 0)
            perAnticipo = verificaIndices(pcs, perAnticipo, 0)
            println " per o " + perOferta + "  per a " + perAnticipo

            def p1 = new PeriodoPlanilla([planilla: planilla, periodo: perOferta, fechaIncio: fechaOferta, fechaFin: getLastDayOfMonth(fechaOferta), titulo: "OFERTA"])
            if (!p1.save(flush: true)) {
                println "p1 " + p1.errors
            }
            def p2 = new PeriodoPlanilla([planilla: planilla, periodo: perAnticipo, fechaIncio: fechaAnticipo, fechaFin: getLastDayOfMonth(fechaAnticipo), titulo: "ANTICIPO"])
            if (!p2.save(flush: true)) {
                println "p2 " + p2.errors
            }
            periodos.add(p1)
            periodos.add(p2)

        }


        def tableWidth = 150 * periodos.size() + 400

        def tablaBo = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px;'>"
        tablaBo += "<thead>"
        tablaBo += "<tr>"
        tablaBo += "<th colspan=\"2\">Cuadrilla Tipo</th>"
        periodos.each {
            it.total = 0
            it.fr = 0
            tablaBo += "<th>${it.titulo}</th>"
            tablaBo += "<th class='nb'>" + fechaConFormato(it.fechaIncio, "MMM-yy") + "</th>"
        }

        tablaBo += "</tr>"
        tablaBo += "</thead>"
        tablaBo += "<tbody>"
        def totalCoef = 0
        cs.each { c ->
            tablaBo += "<tr>"
            tablaBo += "<th class='tal'>" + c.indice.descripcion + " (${c.numero})</th>"
            tablaBo += "<th class='number'>" + numero(c.valor) + "</th>"
            totalCoef += c.valor
            periodos.each { p ->
                def valor = ValorIndice.findByPeriodoAndIndice(p.periodo, c.indice).valor
                if (!valor) {
                    println "wtf no valor " + p.periodo + "  " + c.indice
                    valor = 0
                }

                p.total += (valor * c.valor).round(3)
                p.save(flush: true)

                tablaBo += "<td class='number'>" + numero(valor, 2) + "</td>"
                valor = (valor * c.valor).round(3)
                def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${p.periodo.id} and formulaPolinomica=${c.id}")
                if (vlrj.size() > 0) {
                    vlrj = vlrj.pop()
                    if (vlrj != valor) {
                        vlrj.valor = valor
                        if (!vlrj.save(flush: true)) {
                            println "error vlrj update " + vlrj.errors
                        }
                    }
                } else {
                    vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: p.periodo, formulaPolinomica: c, valor: valor])
                    if (!vlrj.save(flush: true)) {
                        println "error vlrj insert " + vlrj.errors
                    }
                }
                tablaBo += "<td class='number'>" + numero(valor) + "</td>"
            }

            tablaBo += "</tr>"
        }
        tablaBo += "</tbody><tfoot>"
        tablaBo += "<tr>" + "<th>TOTALES</th><th class='number'>${numero(totalCoef)}</th>"
        periodos.each { p ->
            tablaBo += "<td></td><th class='number'>${numero(p.total)}</th>"
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
        periodos.each { p ->
            if (p.titulo != "OFERTA") {
                tablaP0 += '<tr>'
                if (p.titulo == "ANTICIPO") {
                    tablaP0 += "<th>${p.titulo}</th>"
                    tablaP0 += "<th>${fechaConFormato(p.fechaIncio, 'MMM-yy')}</th>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td></td>"
                    tablaP0 += "<td class='number'>${numero(planilla.valor, 2)}</td>"
                    if (p.p0 == 0 || override) {
                        p.p0 = planilla.valor
                        if (!p.save(flush: true)) {
                            println "error p0 " + p
                        }
                    }
                }

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

        def tdRowSpan = '<th colspan="' + (periodos.size() - 1) + '">Periodo de variación y aplicación de fórmula polinómica</th>'
        periodos.eachWithIndex { p, i ->
            if (i == 0) { //oferta
                tr1 += "<th>" + p.titulo + "</th>"
                tr2 += "<th>" + fechaConFormato(p.fechaIncio) + "</th>"
                tr3 += "<th class='number'>" + numero(planilla.contrato.porcentajeAnticipo, 0) + "%</th>"
            } //oferta
            else if (i == 1) { //anticipo
                tr1 += tdRowSpan
                tr2 += "<th>" + p.titulo + "<br/>" + fechaConFormato(p.fechaIncio) + "</th>"
                tr3 += "<th>" + p.titulo + "</th>"
            } //anticipo
            else { //otros
                tr2 += "<th rowspan='2'>" + fechaConFormato(p.fechaIncio) + "</th>"
            }//otros
        }  // periodos.each para el header
        tr1 += "</tr>"
        tr2 += "</tr>"
        tr3 += "</tr>"
        tablaFr += tr1 + tr2 + tr3
        tablaFr += "</thead>"
        tablaFr += "<tbody>"

        def totalFr = 0

        ps.eachWithIndex { p, i ->
            tablaFr += "<tr>"
            tablaFr += "<th class='tal'>" + p?.indice?.descripcion + " (${p?.numero})</th>"
            def vlinOferta
            periodos.eachWithIndex { per, j ->
                if (j == 0) { // es la oferta
                    def valor = 0
                    if (i == 0) { //es mano de obra
                        vlinOferta = per.total
                        tablaFr += "<td class='number'><div>${numero(p.valor, 3)}</div><div class='bold'>${numero(per.total)}</div></td>"
                        valor = per.total
                    } else {
                        vlinOferta = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
                        tablaFr += "<td class='number'><div>${numero(p.valor,3)}</div><div class='bold'>${numero(vlinOferta, 3)}</div></td>"
                        valor = vlinOferta
                    }
                    def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${per.periodo.id} and formulaPolinomica=${p.id}")
                    if (vlrj.size() > 0) {
                        vlrj = vlrj.pop()
                        if (vlrj != valor) {
                            vlrj.valor = valor
                            if (!vlrj.save(flush: true)) {
                                println "error vlrj update " + vlrj.errors
                            }
                        }
                    } else {
                        vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: per.periodo, formulaPolinomica: p, valor: valor])
                        if (!vlrj.save(flush: true)) {
                            println "error vlrj insert " + vlrj.errors
                        }
                    }

                } else {
                    def vlin
                    if (i == 0) {
                        vlin = per.total
                    } else {
                        vlin = ValorIndice.findByIndiceAndPeriodo(p.indice, per.periodo).valor
                    }
//                    println "error "+p.indice+" "+p.indice.id+"  "+per.periodo.id+"   "+vlin+"  "+vlinOferta+"  "+p.valor+"  "+"  "+per.periodo.id
                    def valor = (vlin / vlinOferta * p.valor).round(3)

                    def vlrj = ValorReajuste.findAll("from ValorReajuste where obra=${obra.id} and planilla=${planilla.id} and periodoIndice =${per.periodo.id} and formulaPolinomica=${p.id}")
                    if (vlrj.size() > 0) {
                        vlrj = vlrj.pop()
                        if (vlrj != valor) {
                            vlrj.valor = valor
                            if (!vlrj.save(flush: true)) {
                                println "error vlrj update " + vlrj.errors
                            }
                        }
                    } else {
                        vlrj = new ValorReajuste([obra: obra, planilla: planilla, periodoIndice: per.periodo, formulaPolinomica: p, valor: valor])
                        if (!vlrj.save(flush: true)) {
                            println "error vlrj insert " + vlrj.errors
                        }
                    }

                    per.fr += valor
                    if (!per.save(flush: true)) {
                        println "error fr " + per.errors
                    }
                    tablaFr += "<td class='number'><div>${numero(vlin, 3)}</div><div class='bold'>${numero(valor,3)}</div></td>"
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
        tr2 += "<th>F<sub>r</sub>-1</th>"
        tr3 += "<th>P<sub>0</sub></th>"
        tr4 += "<th>P<sub>r</sub>-P</th>"

        def reajusteTotal = 0

        periodos.eachWithIndex { per, i ->
            if (i > 0) {
                def fr1 = (per.fr - 1).round(3)
                tr1 += "<th class='number'>${numero(per.fr)}</th>"
                tr2 += "<th class='number'>${numero(fr1)}</th>"
                tr3 += "<th class='number'>${numero(per.p0, 2)}</th>"
                def t = (per.p0 * fr1).round(3)
                tr4 += "<th class='number'>${numero(t, 2)}</th>"
                reajusteTotal += t
            }
        }

        planilla.reajuste = reajusteTotal
        if (!planilla.save(flush: true)) {
            println "error planilla reajuste " + planilla.id
        }

        tr5 += "<th colspan='2'>REAJUSTE TOTAL</th><th class='number'>" + numero(reajusteTotal, 2) + "</th>"

        tablaFr += "<tfoot>"
        tablaFr += tr1 + tr2 + tr3 + tr4 + tr5
        tablaFr += "</tfoot></table>"

        [tablaBo: tablaBo, planilla: planilla, tablaP0: tablaP0, tablaFr: tablaFr]
    }

    def verificaIndices(pcs, per, i) {

//        println "\t"+i+"   "+per
        def perNuevo = per
        if (per != null) {
            pcs.each { c ->
                def val = ValorIndice.findByIndiceAndPeriodo(c.indice, per)
                if (!val) {
                    def vals = ValorIndice.withCriteria {
                        eq("indice", c.indice)
                        periodo {
                            le("fechaInicio", per.fechaInicio)
                            order("fechaInicio", "asc")
                        }
                    }
//                println "no hay val   ${c.indice.id}---- ${per} "+vals.periodo
                    if (vals.size() > 0) {
                        perNuevo = vals.pop().periodo
                    } else {
                        if (!flash.message.contains(c.indice.toString()) || !flash.message.contains(per.toString())) {
                            flash.message += "<li>No se encontró el valor de índice de  " + c.indice + " en el periodo " + per + "</li>"
                        }
                        println "error wtf  " + c.indice + " (" + c.indice.id + ") " + per + " (" + per.id + ")"
                        perNuevo = null
                    }
                }
            }

            if (i > 12 * 5) {
                return null
            }

            if (per != perNuevo) {
                i++
                perNuevo = verificaIndices(pcs, perNuevo, i)
            }
        }
        return perNuevo
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
        }
        return strFecha
    }

    private String fechaConFormato(fecha) {
        return fechaConFormato(fecha, "MMM-yy")
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
