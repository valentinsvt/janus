package janus.ejecucion

import groovy.json.JsonBuilder
import janus.*
import janus.pac.CronogramaEjecucion
import janus.pac.PeriodoEjecucion

class PlanillaController extends janus.seguridad.Shield {

    def preciosService, buscadorService

    def errorIndice() {
        def planilla = Planilla.get(params.id)
        return [planilla: planilla, errores: params.errores, alertas: params.alertas]
    }

    def list() {
        def codigoPerfil = session.perfil.codigo
        println codigoPerfil
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

        def fp = janus.FormulaPolinomica.findAllByObra(obra)
//        println fp

        def planillaInstanceList = Planilla.findAllByContrato(contrato, [sort: 'numero'])
        return [contrato: contrato, obra: contrato.oferta.concurso.obra, planillaInstanceList: planillaInstanceList]
    }

    def listFiscalizador() {
        def contrato = Contrato.get(params.id)
        def obra = contrato.oferta.concurso.obra

        def fp = janus.FormulaPolinomica.findAllByObra(obra)
//        println fp

        def planillaInstanceList = Planilla.findAllByContrato(contrato, [sort: 'numero'])
        return [contrato: contrato, obra: contrato.oferta.concurso.obra, planillaInstanceList: planillaInstanceList]
    }

    def listAdmin() {

        def contrato = Contrato.get(params.id)
        def obra = contrato.oferta.concurso.obra

        def fp = janus.FormulaPolinomica.findAllByObra(obra)
//        println fp

        def planillaInstanceList = Planilla.findAllByContrato(contrato, [sort: 'numero'])
        return [contrato: contrato, obra: contrato.oferta.concurso.obra, planillaInstanceList: planillaInstanceList]

    }


    def listFinanciero() {

        def contrato = Contrato.get(params.id)
        def obra = contrato.oferta.concurso.obra

        def fp = janus.FormulaPolinomica.findAllByObra(obra)
//        println fp

        def planillaInstanceList = Planilla.findAllByContrato(contrato, [sort: 'numero'])
        return [contrato: contrato, obra: contrato.oferta.concurso.obra, planillaInstanceList: planillaInstanceList]

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

    def pago_ajax() {
//        println "PARAMS: " + params

        def fechaMin, fechaMax, fecha
        def planilla = Planilla.get(params.id)
        def tipo = params.tipo
        def lblMemo, lblFecha, extra, nombres = ""
        def tipoTramite

//        println planilla.fechaOficioEntradaPlanilla
//        println planilla.fechaMemoSalidaPlanilla
//        println planilla.fechaMemoPedidoPagoPlanilla
//        println planilla.fechaMemoPagoPlanilla

        switch (tipo) {
            case "2":
                lblMemo = "Memo de salida"
                lblFecha = "Fecha de memo de salida"
                fechaMin = planilla.fechaOficioEntradaPlanilla
                fecha = planilla.fechaOficioEntradaPlanilla
                extra = "Fecha de oficio de entrada: " + fechaMin.format("dd-MM-yyyy")
                tipoTramite = TipoTramite.findByCodigo("ENRJ")
                break;
            case "3":
                lblMemo = "Memo de pedido de pago"
                lblFecha = "Fecha de memo de pedido de pago"
                fechaMin = planilla.fechaMemoSalidaPlanilla
                fecha = planilla.fechaMemoSalidaPlanilla
                extra = "Fecha de memo de salida: " + fechaMin.format("dd-MM-yyyy")
                tipoTramite = TipoTramite.findByCodigo("PDPG")
                break;
            case "4":
                lblMemo = "Memo de pago"
                lblFecha = "Fecha de memo de pago"
                fechaMin = planilla.fechaMemoPedidoPagoPlanilla
                fecha = planilla.fechaMemoPedidoPagoPlanilla
                extra = "Fecha de memo de pedido de pago: " + fechaMin.format("dd-MM-yyyy")
                tipoTramite = TipoTramite.findByCodigo("INPG")
                break;
            case '5':
                lblFecha = "Fecha de inicio de obra"
                fechaMin = planilla.fechaMemoPagoPlanilla
                fecha = planilla.fechaMemoPagoPlanilla
                extra = "Fecha de memo de pago: " + fechaMin.format("dd-MM-yyyy")
                tipoTramite = TipoTramite.findByCodigo("INOB")
                break;
        }

        def roles = DepartamentoTramite.findAllByTipoTramite(tipoTramite)

        roles.each { rol ->
            def personas = Persona.findAllByDepartamento(rol.departamento)

            def sel = g.select(from: personas, class: "span3", optionKey: "id", optionValue: { it.nombre + " " + it.apellido }, name: "persona_" + rol.rolTramite.id)

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
        nombres += '<div class="span4">' + g.textArea(name: 'asunto', style: "width:410px;") + '</div>'
        nombres += '</div>'

//        println tipo + "  " + fechaMin
        def y = fechaMin.format("yyyy").toInteger()
        def m = fechaMin.format("MM").toInteger() - 1
        def d = fechaMin.format("dd").toInteger()
        //js: new Date(year, month, day, hours, minutes, seconds, milliseconds)
        fechaMin = "new Date(${y},${m},${d})"
        fechaMax = "new Date(${y + 2},${m},${d})"

        [planilla: planilla, tipo: tipo, lblMemo: lblMemo, lblFecha: lblFecha, fechaMin: fechaMin, fechaMax: fechaMax, extra: extra, fecha: fecha, nombres: nombres]
    }

    def savePagoPlanilla() {
        def planilla = Planilla.get(params.id)
        def tipo = params.tipo
        def memo = params.memo.toString().toUpperCase()
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)

        def tipoTramite, tramitePadre

        def str = "", str2 = ""

        switch (tipo) {
            case "2":
                planilla.memoSalidaPlanilla = memo
                planilla.fechaMemoSalidaPlanilla = fecha
                str = "Reajuste enviado exitosamente"
                str2 = "No se pudo enviar el reajuste"
                tipoTramite = TipoTramite.findByCodigo("ENRJ")
                tramitePadre = null
                break;
            case "3":
                planilla.memoPedidoPagoPlanilla = memo
                planilla.fechaMemoPedidoPagoPlanilla = fecha
                str = "Pago pedido existosamente"
                str2 = "No se pudo pedir el pago"
                tipoTramite = TipoTramite.findByCodigo("PDPG")
                tramitePadre = Tramite.findByPlanillaAndTipoTramite(planilla, TipoTramite.findByCodigo("ENRJ"))
                break;
            case "4":
                planilla.memoPagoPlanilla = memo
                planilla.fechaMemoPagoPlanilla = fecha
//                def obra = Obra.get(planilla.contrato.obra.id)
//                obra.fechaInicio = fechaObra
//                if (!obra.save(flush: true)) {
//                    println "Error al guardar la fecha de la obra desde el boton azul: " + obra.errors
//                }
                str = "Pago informado exitosamente"
                str2 = "No se pudo informar el pago"
                tipoTramite = TipoTramite.findByCodigo("INPG")
                tramitePadre = Tramite.findByPlanillaAndTipoTramite(planilla, TipoTramite.findByCodigo("PDPG"))
                break;
            case "5":
                def obra = Obra.get(planilla.contrato.obra.id)
                obra.fechaInicio = fecha
                if (!obra.save(flush: true)) {
                    println "Error al guardar la fecha de la obra desde el boton azul: " + obra.errors
                }
                str = "Obra iniciada exitosamente"
                str2 = "No se pudo iniciar la obra"
                tipoTramite = TipoTramite.findByCodigo("INOB")
                tramitePadre = Tramite.findByPlanillaAndTipoTramite(planilla, TipoTramite.findByCodigo("INPG"))
                break;
        }
        if (planilla.save(flush: true)) {
            flash.clase = "alert-success"
            flash.message = str

            def estadoTramite = EstadoTramite.findByCodigo("C")
            def tramite = new Tramite([
                    planilla: planilla,
                    tipoTramite: tipoTramite,
                    tramitePadre: tramitePadre,
                    estado: estadoTramite,
                    descripcion: params.asunto,
                    memo: memo,
                    fecha: fecha
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
                    def personaTramite = new PersonasTramite([
                            tramite: tramite,
                            rolTramite: rol,
                            persona: Persona.get(val.toLong())
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
            if(tipo == 5) {
                redirect(controller: "cronogramaEjecucion", action: "index", id: planilla.contratoId)
            }   else {
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

    def form() {
//        println params
        def contrato = Contrato.get(params.contrato)
        def planillaInstance = new Planilla(params)
        planillaInstance.contrato = contrato
        if (params.id) {
            planillaInstance = Planilla.get(params.id)
        }

        def anticipo = TipoPlanilla.findByCodigo('A')
        def avance = TipoPlanilla.findByCodigo('P')
        def liquidacion = TipoPlanilla.findByCodigo('L')
        def reajusteDefinitivo = TipoPlanilla.findByCodigo('R')
        def costoPorcentaje = TipoPlanilla.findByCodigo('C')

        def tiposPlanilla = TipoPlanilla.list([sort: 'nombre'])

        def periodosEjec = PeriodoEjecucion.findAllByObra(contrato.oferta.concurso.obra, [sort: "fechaFin"])
        def finalObra = periodosEjec.pop().fechaFin

        def pla = Planilla.findByContratoAndTipoPlanilla(contrato, anticipo)
        def anticipoPagado = false
        if (!pla) {
            anticipoPagado = true
        } else {
            if (pla.fechaMemoPagoPlanilla) {
                anticipoPagado = true
            }
        }

        def planillas = Planilla.findAllByContrato(contrato, [sort: 'fechaInicio', order: "asc"])
        def planillasAvance = Planilla.withCriteria {
            eq("contrato", contrato)
            tipoPlanilla {
                eq("codigo", "P")
            }
            order("fechaInicio", "asc")
        }
        def cPlanillas = planillas.size()

        def esAnticipo = false
        if (cPlanillas == 0) {
            tiposPlanilla = TipoPlanilla.findAllByCodigo('A')
            esAnticipo = true
        } else {
            if (pla) {
                tiposPlanilla -= pla.tipoPlanilla
            }
            def pll = Planilla.findByContratoAndTipoPlanilla(contrato, liquidacion)
            if (pll) {
                tiposPlanilla -= pll.tipoPlanilla
            }
            def plr = Planilla.findByContratoAndTipoPlanilla(contrato, reajusteDefinitivo)
            if (plr) {
                tiposPlanilla -= plr.tipoPlanilla
            }
            def plc = Planilla.findByContratoAndTipoPlanilla(contrato, costoPorcentaje)
            if (plc) {
                tiposPlanilla -= plc.tipoPlanilla
            }
        }

        if (planillasAvance.size() > 0) {
            if (planillasAvance.last().fechaFin == finalObra) {
                def plp = Planilla.findByContratoAndTipoPlanilla(contrato, avance)
                tiposPlanilla -= plp.tipoPlanilla
            }
        }

        if (!params.id) {
            planillaInstance.numero = cPlanillas + 1
        }

        def periodos = [:]
//        if (!esAnticipo) {
////            println "********************* " + planillasAvance
////            println "********************* " + planillas.last().fechaPresentacion
//            def ultimoPeriodo
//            if (planillasAvance.size() > 0) {
//                ultimoPeriodo = planillasAvance.last().fechaFin
////            if (!ultimoPeriodo) {
////                ultimoPeriodo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(planillas.last().fechaPresentacion, planillas.last().fechaPresentacion).fechaFin
////            }
////            println planillasAvance
//            } else {
////                ultimoPeriodo = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(planillas.last().fechaPresentacion, planillas.last().fechaPresentacion).fechaFin
//                ultimoPeriodo = null
//            }
//            println ultimoPeriodo
//            PeriodoEjecucion.findAllByObra(contrato.oferta.concurso.obra, [sort: 'fechaInicio']).each { pe ->
//                if (pe.tipo == "P") {
//                    periodos += PeriodosInec.withCriteria {
//                        or {
//                            between("fechaInicio", pe.fechaInicio, pe.fechaFin)
//                            between("fechaFin", pe.fechaInicio, pe.fechaFin)
//                        }
//                        if (ultimoPeriodo) {
//                            and {
//                                gt("fechaInicio", ultimoPeriodo)
//                            }
//                        }
//                    }
//                }
//            }
//            println periodos
//            periodos = periodos.unique().sort { it.fechaInicio }
//        }
           println "avance "+planillasAvance
        if (planillasAvance.size() == 0) {
            /* cuando es la primera planilla de avance:
                    si la fecha de inicio de obra < 15: debe hacer una planilla con fecha inicio=inicio de obra, fecha fin = fin de mes
                    si la fecha de inicio de obra >=15: puede hacer una planilla con fecha inicio=inicio de obra, fecha fin = fin de mes
                                                     o  puede hacer una planilla con fecha inicio=inicio de obra, fecha fin = fin del mes siguiente
            */

            def inicioObra = contrato.oferta.concurso.obra.fechaInicio
            def finMes = getLastDayOfMonth(inicioObra)
            def dias = inicioObra.format("dd").toInteger()

            periodos.put((inicioObra.format("dd-MM-yyyy") + "_" + finMes.format("dd-MM-yyyy")), inicioObra.format("dd-MM-yyyy") + " a " + finMes.format("dd-MM-yyyy"))
            if (dias >= 15) {
                def inicio = finMes.plus(1)
                finMes = getLastDayOfMonth(inicio)
                periodos.put((inicioObra.format("dd-MM-yyyy") + "_" + finMes.format("dd-MM-yyyy")), inicioObra.format("dd-MM-yyyy") + " a " + finMes.format("dd-MM-yyyy"))
            }

        } else {
            def planillasAnt = Planilla.findAllByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo("P"), [sort: "fechaFin"])

            def inicio, fin
            println "planillas ant "+planillasAnt+" per ejec "+periodosEjec
            if (planillasAnt.size() > 0) {
                inicio = planillasAnt.pop().fechaFin + 1
                println "inicio "+inicio
                fin = getLastDayOfMonth(inicio)
                println "fin "+fin
                if (fin > finalObra) {
                    periodos.put((inicio.format("dd-MM-yyyy") + "_" + finalObra.format("dd-MM-yyyy")), inicio.format("dd-MM-yyyy") + " a " + finalObra.format("dd-MM-yyyy"))
                } else {
                    if (finalObra - fin <= 30) {
                        periodos.put((inicio.format("dd-MM-yyyy") + "_" + finalObra.format("dd-MM-yyyy")), inicio.format("dd-MM-yyyy") + " a " + finalObra.format("dd-MM-yyyy"))
                        periodos.put((inicio.format("dd-MM-yyyy") + "_" + fin.format("dd-MM-yyyy")), inicio.format("dd-MM-yyyy") + " a " + fin.format("dd-MM-yyyy"))
                    } else {
                        periodos.put((inicio.format("dd-MM-yyyy") + "_" + fin.format("dd-MM-yyyy")), inicio.format("dd-MM-yyyy") + " a " + fin.format("dd-MM-yyyy"))
                    }
                }

            } else {
                println "error wtf no hay planillas"
            }
        }

//        println "PERIODOS: "+periodos

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
        if( planillas.size()>0){
            fiscalizadorAnterior = planillas.last().fiscalizadorId
        }




        return [planillaInstance: planillaInstance, contrato: contrato, tipos: tiposPlanilla, obra: contrato.oferta.concurso.obra, periodos: periodos, esAnticipo: esAnticipo, anticipoPagado: anticipoPagado, maxDatePres: maxDatePres, minDatePres: minDatePres, fiscalizadorAnterior: fiscalizadorAnterior]
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

    def save() {
        if (params.fechaPresentacion) {
            params.fechaPresentacion = new Date().parse("dd-MM-yyyy", params.fechaPresentacion)
        }
        if (params.fechaIngreso) {
            params.fechaIngreso = new Date().parse("dd-MM-yyyy", params.fechaIngreso)
        }
        if (params.fechaOficioSalida) {
            params.fechaOficioSalida = new Date().parse("dd-MM-yyyy", params.fechaOficioSalida)
        }
        if (params.fechaMemoSalida) {
            params.fechaMemoSalida = new Date().parse("dd-MM-yyyy", params.fechaMemoSalida)
        }
        if (params.fechaOficioEntradaPlanilla) {
            params.fechaOficioEntradaPlanilla = new Date().parse("dd-MM-yyyy", params.fechaOficioEntradaPlanilla)
        }

        if (!params.fechaPresentacion) {
            params.fechaPresentacion = params.fechaIngreso
        }
        if (params.oficioEntradaPlanilla) {
            params.oficioEntradaPlanilla = params.oficioEntradaPlanilla.toString().toUpperCase()
        }
        def planillaInstance
        if (params.id) {
            planillaInstance = Planilla.get(params.id)
            if (!planillaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Planilla con id " + params.id
                params.contrato = params.contrato.id
                redirect(action: 'form', params: params)
                return
            }//no existe el objeto
            planillaInstance.properties = params
        }//es edit
        else {
            planillaInstance = new Planilla(params)

            switch (planillaInstance.tipoPlanilla.codigo) {
                case 'A':
                    //es anticipo hay q ingresar el valor de la planilla
                    planillaInstance.valor = planillaInstance.contrato.anticipo
                    break;
                case 'P':
                    //avance de obra: hay q poner fecha inicio y fecha fin

//                    //las planillas q no son de avance para ver cual es el ultimo periodo planillado
//                    def otrasPlanillas = Planilla.findAllByContratoAndTipoPlanillaNotEqual(planillaInstance.contrato, TipoPlanilla.findByCodigo("A"), [sort: 'periodoIndices', order: 'asc'])
//
//                    def ini
//                    if (otrasPlanillas.size() > 0) {
//                        def ultimoPeriodo = otrasPlanillas?.last().fechaFin
//                        use(TimeCategory) {
//                            ini = ultimoPeriodo + 1.days
//                        }
//                    } else {
//                        ini = planillaInstance.contrato.oferta.concurso.obra.fechaInicio
//                    }
//                    def fin = planillaInstance.periodoIndices.fechaFin
//
//                    planillaInstance.fechaInicio = ini
//                    planillaInstance.fechaFin = fin
//
//                    /* ************** La fecha inicio y fecha fin se vuelven las fechas del periodo ***************************/
//                    planillaInstance.fechaInicio = planillaInstance.periodoIndices.fechaInicio
//                    planillaInstance.fechaFin = planillaInstance.periodoIndices.fechaFin

                    def periodoPlanilla = params.periodoPlanilla
                    def fechas = periodoPlanilla.split("_")
                    planillaInstance.fechaInicio = new Date().parse("dd-MM-yyyy", fechas[0])
                    planillaInstance.fechaFin = new Date().parse("dd-MM-yyyy", fechas[1])
                    break;
                case "L":

                    break;
                case "C":
                    //es de costo y porcentaje: fecha inicio y fecha fin se ponen la fecha de presentacion (?)
                    planillaInstance.fechaInicio = planillaInstance.fechaPresentacion
                    planillaInstance.fechaFin = planillaInstance.fechaPresentacion
                    break;
            }

        } //es create

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

        switch (planillaInstance.tipoPlanilla.codigo) {
            case 'A':
                redirect(action: 'anticipo', controller: 'planilla2', id: planillaInstance.id)
                break;
            case 'L':
                redirect(action: 'list', id: planillaInstance.contratoId)
                break;
            case 'P':
                redirect(action: 'detalle', id: planillaInstance.id, params: [contrato: planillaInstance.contratoId])
                break;
            case 'C':
                redirect(action: 'detalleCosto', id: planillaInstance.id, params: [contrato: planillaInstance.contratoId])
                break;
            default:
                redirect(action: 'list', id: planillaInstance.contratoId)
        }
    }

    def deleteReajuste() {
        /*
         *  esta parte es solo para pruebas!!!
         *  borra los valorReajuste de la planilla
         *
         */
        def planilla = Planilla.get(params.id)
        def html = ""
        def valorReajusteBorrar = ValorReajuste.findAllByPlanilla(planilla)
        def cont = 0
        valorReajusteBorrar.each {
            html += "Eliminando " + it.id + "<br/>"
            try {
                it.delete(flush: true)
                cont++
            } catch (e) {
                html += "&nbsp;&nbsp;&nbsp;Error al eliminar: " + e.printStackTrace()
            }
        }
        html += "Terminó de borrar " + cont + " valorReajuste"
        render html
    }

    def fechaConFormato(fecha, formato) {
        def meses = ["", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"]
        def strFecha
        switch (formato) {
            case "MMM-yy":
                strFecha = meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yy")
                break;
        }
        return strFecha
    }





    def resumen() {
        //para volver a generar los valores de reajuste poner esta variable en true!!
        def override = false

        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
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
                def val = ValorIndice.findByPeriodoAndIndice(per, c.indice)?.valor
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
                                valor: val * c.valor,
                                formulaPolinomica: formulaTmp,
                                obra: obra,
                                periodoIndice: per,
                                planilla: pl
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
                        obra: obra,
                        planilla: pl,
                        periodoIndice: per,
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
                                obra: obra,
                                planilla: planillaAnticipo,
                                periodoIndice: per,
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
                            def crono = CronogramaEjecucion.findAllByPeriodo(pe)
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
                                obra: obra,
                                planilla: planillaActual,
                                periodoIndice: per,
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
                                per: cp.value.periodo,
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
                        obra: obra,
                        planilla: planilla,
                        periodoIndice: t.per,
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

            def noLaborables = ["Sat", "Sun"]

//            println fechaMax
            diasMax.times {
                fechaMax++
//                println fechaMax
                def fmt = new java.text.SimpleDateFormat("EEE", new Locale("en"))
                while (noLaborables.contains(fmt.format(fechaMax))) {
//                    println fmt.format(fechaMax)
                    fechaMax++
//                    println fechaMax
                }
            }
//            println "***** "+fechaMax

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

    def resumenPlanilla() {
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def oferta = contrato.oferta

        def tablaB0, tablaP0, tablaFr, tablaMl, pMl
        def planillaAnticipo = Planilla.findByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo("A"))
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
        def fp = FormulaPolinomica.findAllByObra(obra)
        def fr = FormulaPolinomicaContractual.findAllByContrato(contrato)
        def tipo = TipoFormulaPolinomica.get(1)
        def periodoOferta = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(oferta.fechaEntrega, oferta.fechaEntrega)

        def periodos = [], periodosPlanilla = []
        def data2 = [
                c: [:],
                p: [:]
        ]
        def fpB0, fpP0

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
            fpP0 = new FormulaPolinomicaContractual()
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

        //llena el arreglo de periodos
        //el periodo que corresponde a la fecha de entrega de la oferta
        periodos.add(periodoOferta)
        planillas.each { pl ->
            if (pl.tipoPlanilla.codigo == 'A') {
                //si es anticipo: el periodo q corresponde a la fecha del anticipo
                def prin = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(pl.fechaPresentacion, pl.fechaPresentacion)
                periodos.add(prin)
            } else {
//                println pl.id + "   " + pl.fechaInicio.format("dd-MM-yyyy") + " a " + pl.fechaFin.format("dd-MM-yyyy")
                def m1 = pl.fechaInicio.format("MM")
                def m2 = pl.fechaFin.format("MM")
//                println "\t" + m1 + "   " + m2
                if (m1 == m2) {
                    if (pl.periodoIndices) {
                        println "++ " + pl.periodoIndices
                        periodos.add(pl.periodoIndices)
                    }
                    if (pl == planilla) {
                        println "-- " + pl
                        periodosPlanilla.add(pl)
                    }
                } else {
                    def y = pl.fechaInicio.format("yyyy")
                    (m1.toInteger()..m2.toInteger()).each {
//                        println "\t\t" + it + " - " + y
                        def fi = new Date().parse("dd-MM-yyyy", "01-" + it + "-" + y)
                        def prin = PeriodosInec.findByFechaInicio(fi)
                        if (prin) {
                            periodos.add(prin)
                        }
                        if (pl == planilla) {
                            periodosPlanilla.add(prin)
                        }
                        if (it == 12) {
                            y = pl.fechaFin.format("yyyy")
                        }
                    }
                }
            }
        }
        periodos = periodos.unique()
//        println periodos

//        println "Aqui empieza las inserciones"
        periodos.eachWithIndex { per, perNum ->
            def pl = planilla
            if (perNum == 0) {
                pl = null
            }
            def valRea = ValorReajuste.findAllByObraAndPeriodoIndice(obra, per)

//            println ">>>Periodo " + per.descripcion + " (${perNum}) hay " + valRea.size() + " valRea: " + valRea.id + " (obra: ${obra.id})"

            def tot = [c: 0, p: 0]
            //si no existen valores de reajuste, se crean

            if (valRea.size() == 0) {
                pcs.each { c ->
                    def val = ValorIndice.findByPeriodoAndIndice(per, c.indice)?.valor
                    if (!val) {
                        val = 1
                    }
                    def vr = new ValorReajuste([
                            valor: val * c.valor,
                            formulaPolinomica: FormulaPolinomicaContractual.findByIndiceAndContrato(c.indice, contrato),
                            obra: obra,
                            periodoIndice: per,
                            planilla: pl
                    ])
                    if (!vr.save(flush: true)) {
                        println "vr errors " + vr.errors
                    } else {
//                        println "crea vr ${vr.id}"
                    }
                    def pos = "p"
                    if (c.numero.contains("c")) {
                        pos = "c"
                    }
                    tot[pos] += (vr.valor * c.valor).round(3)
//                    println "\t\t" + pos + "   " + (vr.valor * c.valor)
                    if (!data2[pos][perNum]) {
//                        println "\t\tCrea data2[${pos}][${perNum}]"
                        data2[pos][perNum] = [valores: [], total: 0, periodo: per]
                    }
                    data2[pos][perNum]["valores"].add([formulaPolinomica: c, valorReajuste: vr, valorTabla: vr.valor])
                } //pcs.each
            } //valRea.size == 0
            else {
                valRea.each { v ->
                    def c = pcs.find { it.indice == v.formulaPolinomica.indice }
                    if (c) {
                        def val = ValorIndice.findByPeriodoAndIndice(per, c.indice)?.valor
                        if (!val) {
                            val = 1
                        }
//                        println "\t\t"+per.descripcion+"   "+c.indice+"   "+val+"   "+c.id
                        def pos = "p"
                        if (c.numero.contains("c")) {
                            pos = "c"
                        }
                        tot[pos] += (val * c.valor).round(3)
//                        println "\t\t" + pos + "   " + (v.valor * c.valor)
                        if (!data2[pos][perNum]) {
//                            println "\t\tCrea data2[${pos}][${perNum}]"
                            data2[pos][perNum] = [valores: [], total: 0, periodo: per]
                        }

                        data2[pos][perNum]["valores"].add([formulaPolinomica: c, valorReajuste: v, valorTabla: val])
                    }
                }
            } //valRea.size == 0

//            println "data[c][${perNum}][total]=${tot['c']}"
            data2["c"][perNum]["total"] = tot["c"]
//            println "data[p][${perNum}][total]=${tot['p']}"
            data2["p"][perNum]["total"] = tot["p"]
            def vrB0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, fpB0)
            if (!vrB0) {
                vrB0 = new ValorReajuste([
                        obra: obra,
                        planilla: pl,
                        periodoIndice: per,
                        formulaPolinomica: fpB0
                ])
            }
            vrB0.valor = tot["c"]
            if (!vrB0.save(flush: true)) {
                println "error al guardar valor de B0: " + tot["c"] + "\n" + vrB0.errors
            }
            def p01 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "p01")
            if (p01) {
                def vrP01 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p01)
                vrP01.valor = tot["c"]
                if (!vrP01.save(flush: true)) {
                    println "error al guardar valor de p01: " + tot["c"] + "\n" + vrP01.errors
                }
            }
        }

        def tableWidth = 150 * periodos.size() + 400

        tablaB0 = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px;'>"
        tablaB0 += "<thead>"
        tablaB0 += "<tr>"
        tablaB0 += "<th colspan=\"2\">Cuadrilla Tipo</th>"
        tablaB0 += "<th>Oferta</th>"
        tablaB0 += "<th class='nb'>" + formatoFecha(oferta.fechaEntrega, "MMM-yy") + "</th>"
        tablaB0 += "<th>Variación</th>"
        tablaB0 += "<th class='nb'>Anticipo (" + formatoFecha(planillaAnticipo.fechaPresentacion, "MMM-yy") + ")</th>"
        if (periodos.size() > 2) {
            periodos.eachWithIndex { per, i ->
                if (i > 1) {
                    tablaB0 += "<th>Variación</th>"
                    tablaB0 += "<th class='nb'>" + per?.descripcion + "</th>"
                }
            }
        }
        tablaB0 += "</tr>"
        tablaB0 += "</thead>"

        def tbodyB0 = "<tbody>"
        def totC = 0
        pcs.findAll { it.numero.contains("c") }.each { c ->
            tbodyB0 += "<tr>"
            tbodyB0 += "<td>" + c.indice.descripcion + " (" + c.numero + ")</td>"
            tbodyB0 += "<td class='number'>" + elm.numero(number: c.valor, decimales: 3) + "</td>"
            totC += c.valor
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

        tablaB0 += tbodyB0
        tablaB0 += "</table>"

        tablaP0 = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;' >"
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
                    tbodyP0 += planillaAnticipo.fechaPresentacion.format("MMM-yy")
                    tbodyP0 += "</th>"
                    tbodyP0 += "<td colspan='4'></td>"
                    tbodyP0 += "<td class='number'>"
                    tbodyP0 += elm.numero(number: planillaAnticipo.valor)
                    tbodyP0 += "</td>"

                    def p0 = FormulaPolinomicaContractual.findByContratoAndNumero(contrato, "P0")
                    def vrP0 = ValorReajuste.findByPeriodoIndiceAndFormulaPolinomica(per, p0)
                    if (!vrP0) {
                        vrP0 = new ValorReajuste([
                                obra: obra,
                                planilla: planillaAnticipo,
                                periodoIndice: per,
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
//                    println "planilla fin: "+planilla.fechaFin

                    def periodosEjecucion = PeriodoEjecucion.withCriteria {
                        and {
                            eq("obra", obra)
                            or {
                                between("fechaInicio", per.fechaInicio, per.fechaFin)
                                between("fechaFin", per.fechaInicio, per.fechaFin)
                            }
                            le("fechaFin", planilla.fechaFin)
                            order("fechaInicio")
                        }
                    }


                    def diasTotal = 0, valorTotal = 0
//                    println "\t\t++ "+per.fechaInicio.format("dd-MM-yyyy") + "  " + per.fechaFin.format("dd-MM-yyyy")
//                    println "\t\t-- "+periodosEjecucion

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
                            def crono = CronogramaEjecucion.findAllByPeriodo(pe)
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
                                obra: obra,
                                planilla: planillaActual,
                                periodoIndice: per,
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

        tablaFr = "<table class=\"table table-bordered table-striped table-condensed table-hover\" style='width:${tableWidth}px; margin-top:10px;'>"
        tablaFr += '<thead>'
        tablaFr += '<tr>'
        tablaFr += '<th rowspan="2">Componentes</th>'
        tablaFr += '<th>Oferta</th>'
        tablaFr += '<th colspan="' + (periodos.size() - 1) + '">Periodo de variación y aplicación de fórmula polinómica</th>'
        tablaFr += '</tr>'
        tablaFr += '<tr>'
        tablaFr += '<th>' + (oferta.fechaEntrega.format("MMM-yy")) + '</th>'
        tablaFr += '<th>Anticipo <br/>' + planillaAnticipo.fechaPresentacion.format("MMM-yy") + '</th>'
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
                    a = act.valorReajuste.valor
                    if (i > 0) {
                        a = act.valorTabla
                    }
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
                                per: cp.value.periodo,
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
                        obra: obra,
                        planilla: planilla,
                        periodoIndice: t.per,
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

        tablaMl += tbodyMl

        tablaMl += "<tfoot>"
        tablaMl += '<th >Total</th>'
        tablaMl += '<td colspan="3"></td>'
        tablaMl += '<td class="bold number">'
        tablaMl += elm.numero(number: totalMultaRetraso)
        tablaMl += '</td>'
        tablaMl += "</tfoot>"

        tablaMl += "</table>"

        if (planilla.periodoIndices) {
            def diasMax = 5
            def fechaFinPer = planilla.periodoIndices?.fechaFin
            def fechaMax = fechaFinPer
//            use(TimeCategory) {
//                fechaMax = fechaFinPer + diasMax.days
//            }

            def noLaborables = ["Sat", "Sun"]

//            println fechaMax
            diasMax.times {
                fechaMax++
//                println fechaMax
                def fmt = new java.text.SimpleDateFormat("EEE", new Locale("en"))
                while (noLaborables.contains(fmt.format(fechaMax))) {
//                    println fmt.format(fechaMax)
                    fechaMax++
//                    println fechaMax
                }
            }
//            println "***** "+fechaMax

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

        return [planilla: planilla, contrato: contrato, tablaB0: tablaB0, tablaP0: tablaP0, tablaFr: tablaFr, tablaMl: tablaMl, pMl: pMl]
    }

    def resumen2() {
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def planillas = Planilla.findAllByContrato(contrato, [sort: "id"])
        def fp = janus.FormulaPolinomica.findAllByObra(obra)
        def fr = FormulaPolinomicaContractual.findAllByContrato(contrato)
        def tipo = TipoFormulaPolinomica.get(1)
        def oferta = contrato.oferta

        //copia la formula polinomica a la formula polinomica contractual si esta no existe
        if (fr.size() < 4) {
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
                        println "error " + frpl.errors
                    }
                }
            }
            def frpl = new FormulaPolinomicaContractual()
            frpl.valor = 0
            frpl.contrato = contrato
            frpl.indice = null
            frpl.tipoFormulaPolinomica = tipo
            frpl.numero = "P0"
            if (!frpl.save(flush: true)) {
                println "error " + frpl.errors
            }
            frpl = new FormulaPolinomicaContractual()
            frpl.valor = 0
            frpl.contrato = contrato
            frpl.indice = null
            frpl.tipoFormulaPolinomica = tipo
            frpl.numero = "Fr"
            if (!frpl.save(flush: true)) {
                println "error " + frpl.errors
            }
        }

        // para B0: los indices de mano de obra: los c
        def cs = FormulaPolinomicaContractual.findAllByContratoAndNumeroLike(contrato, "c%", [sort: "numero"])
//        def ps = FormulaPolinomicaContractual.findAllByContratoAndNumeroLike(contrato, "p%", [sort: "numero"])
        //Para Fr y Pr: los p
        def ps = FormulaPolinomicaContractual.withCriteria {
            and {
                eq("contrato", contrato)
                ne("numero", "P0")
                ilike("numero", "p%")
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
                        ilike("numero", "p%")
                    }
                }
                order("numero", "asc")
            }
        }
//        println pcs.numero

        def datos = [], datosP = [], periodos = []
        def periodoOferta = PeriodosInec.findAllByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(oferta.fechaEntrega, oferta.fechaEntrega)

        periodos.add(periodoOferta[0])

        planillas.each { pl ->
            if (pl.tipoPlanilla.codigo == 'A') {
                def prin = PeriodosInec.findByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(pl.fechaPresentacion, pl.fechaPresentacion)
                periodos.add(prin)
            } else {
                periodos.add(pl.periodoIndices)
            }
        }

        def tot = 0, totP = 0

        periodos.each { per ->
            def vlin = ValorReajuste.findAllByObraAndPeriodoIndice(obra, per)
//            println ">>>>" + vlin.formulaPolinomica.numero

            if (vlin.size() == 0) {
                def tmp = [:], tmpP = [:]
                tot = 0
                totP = 0
                pcs.each { c ->
                    def val = ValorIndice.findByPeriodoAndIndice(per, c.indice)?.valor
                    if (!val) {
                        val = 1
                    }
                    def vr = new ValorReajuste([
                            valor: val * c.valor,
                            formulaPolinomica: FormulaPolinomicaContractual.findByIndiceAndContrato(c.indice, contrato),
                            obra: obra,
                            periodoIndice: per,
                            planilla: planilla
                    ])
                    if (!vr.save(flush: true)) {
                        println "vr errors " + vr.errors
                    }
                    if (c.numero.contains("c")) {
                        tmp.put(c.numero, vr.valor)
                        tot += vr.valor * c.valor
                    } else if (c.numero.contains("p")) {
//                        println "\t\t" + c + "\t" + val
                        tmpP.put(c.numero, vr.valor)
                        totP += vr.valor * c.valor
                    }
                } //cs.each
                if (tmp.size() > 0) {
                    tmp.put("tot", tot)
                    datos.add(tmp)
                }
                if (tmpP.size() > 0) {
                    tmpP.put("tot", tot)
                    datosP.add(tmpP)
                }
            } // if(vlin.size=0
            else {
                def tmp = [:], tmpP = [:]
                tot = 0
                totP = 0
                vlin.each { v ->
                    pcs.each { c ->
//                        println "\t" + c.numero + " :: " + c.indiceId + " " + v.formulaPolinomica.indiceId
                        if (c.indiceId.toInteger() == v.formulaPolinomica?.indiceId?.toInteger()) {
                            if (c.numero.contains("c")) {
                                tmp.put(c.numero, v.valor)
                                tot += v.valor * c.valor
                            } else if (c.numero.contains("p")) {
//                                println "\t\t" + c + "\t" + v
                                tmpP.put(c.numero, v.valor)
                                totP += v.valor * c.valor
                            }
                        }
                    }
//                    println "tmp "+tmp
                }
                if (tmp.size() > 0) {
                    tmp.put("tot", tot)
                    datos.add(tmp)
                }
                if (tmpP.size() > 0) {
                    tmpP.put("tot", tot)
                    datosP.add(tmpP)
                }
            } //else
        } //periodos.each
//        println "DATOS:"
//        datos.each {
//            println "it " + it
//        }
//        println "DATOSP:"
//        datosP.each {
//            println "it " + it
//        }

        def cant = []
        0.upto(datos.size() - 1) {
            cant.add(it)
        }

        def cantP = []
        0.upto(datosP.size() - 1) {
            cantP.add(it)
        }
//        println "cant " + cant
//        println "cantP " + cantP

        return [datos: datos, datosP: datosP, cs: cs, ps: ps, cant: cant, cantP: cantP, periodos: periodos, planilla: planilla, oferta: oferta, contrato: contrato]
    }

    def detalle() {
        def planilla = Planilla.get(params.id)
        def contrato = Contrato.get(params.contrato)

        def obra = contrato.oferta.concurso.obra
        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])

        def precios = [:]
        def indirecto = obra.totales / 100

        preciosService.ac_rbroObra(obra.id)

        detalle.each {
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
        }

        def planillasAnteriores = Planilla.withCriteria {
            eq("contrato", contrato)
            lt("fechaFin", planilla.fechaInicio)
        }
//        println planillasAnteriores

        def editable = planilla.fechaMemoSalidaPlanilla == null
        editable = PeriodoPlanilla.findAllByPlanilla(planilla).size() == 0
//        println editable

        return [planilla: planilla, detalle: detalle, precios: precios, obra: obra, planillasAnteriores: planillasAnteriores, contrato: contrato, editable: editable]

    }

    def detalleCosto() {
        def planilla = Planilla.get(params.id)
        def contrato = Contrato.get(params.contrato)
        def obra = contrato.oferta.concurso.obra
        def editable = planilla.fechaMemoSalidaPlanilla == null
        def dets = []

        def iva = Parametros.get(1).iva

        def json = new JsonBuilder(dets)
//        println json.toPrettyString()
        return [planilla: planilla, obra: obra, contrato: contrato, editable: editable, detalles: json, iva: iva]

    }

    def detalleCosto_old() {
        def planilla = Planilla.get(params.id)
        def contrato = Contrato.get(params.contrato)

        def obra = contrato.oferta.concurso.obra

        def editable = planilla.fechaMemoPagoPlanilla == null
        def campos = ["codigo": ["Código", "string"], "nombre": ["Descripción", "string"]]

        def dets = []
        DetallePlanilla.findAllByPlanillaAndItemIsNotNull(planilla).each { det ->
            dets.add([
                    edit: true,
                    id: det.id,
                    item: det.itemId,
                    unidad: det.item.unidad.codigo,
                    nombre: det.item.nombre,
                    codigo: det.item.codigo,
                    cantidad: det.cantidad,
                    precio: det.monto,
                    total: det.cantidad * det.monto
            ])
        }
        def json = new JsonBuilder(dets)
//        println json.toPrettyString()

        return [planilla: planilla, obra: obra, contrato: contrato, editable: editable, campos: campos, detalles: json]

    }

    def deleteDetalleCosto() {
        def item = Item.get(params.item)
        def planilla = Planilla.get(params.pln)
        def detalle = DetallePlanilla.findByPlanillaAndItem(planilla, item)
        detalle.delete(flush: true)
        render "OK"
    }

    def addDetalleCosto() {
        def planilla = Planilla.get(params.pln)
        def item = Item.get(params.item)
        def cant = params.cant.toDouble()
        def prec = params.prec.toDouble()

        def detalle
        if (params.id) {
            detalle = DetallePlanilla.get(params.id)
            detalle.cantidad = cant
            detalle.monto = prec
        } else {
            detalle = new DetallePlanilla([
                    planilla: planilla,
                    item: item,
                    cantidad: cant,
                    monto: prec
            ])
        }
        if (!detalle.save(flush: true)) {
            println "ERROR: " + detalle.errors
            render "NO"
        } else {
            planilla.valor = params.totalPl.toDouble()
            if (!planilla.save(flush: true)) {
                println "ERROR save planilla " + planilla.errors
            }
            render "OK_" + detalle.id
        }
    }

    def buscaRubro() {
        def listaTitulos = ["Código", "Descripción", "Unidad"]
        def listaCampos = ["codigo", "nombre", "unidad"]
        def funciones = [null, null]
        def url = g.createLink(action: "buscaRubro", controller: "rubro")
        def funcionJs = "function(){"
        funcionJs += 'clickBuscar($(this));'
        funcionJs += '}'
        def numRegistros = 20
        def extras = " and tipoItem = 2"
        if (!params.reporte) {
            def lista = buscadorService.buscar(Item, "Item", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
            lista.pop()
            render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Item
            session.funciones = funciones
            def anchos = [20, 80] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Item", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Rubros", anchos: anchos, extras: extras, landscape: true])
        }
    }

    def saveDetalle() {
//        println params
        def pln = Planilla.get(params.id)
        def err = 0

        if (params.d.class == java.lang.String) {
            params.d = [params.d]
        }
//
//        println params

        params.d.each { p ->
            def parts = p.split("_")
            if (parts.size() == 3) {
                //create
//                println "CREATE"
                def vol = VolumenesObra.get(parts[0])
                def cant = parts[1].toDouble()
                def val = parts[2].toDouble()

                def detalle = new DetallePlanilla([
                        planilla: pln,
                        volumenObra: vol,
                        cantidad: cant,
                        monto: val
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
            pln.valor = params.total.toDouble()
            if (!pln.save(flush: true)) {
                flash.clase = "alert-error"
                flash.message = "Ocurrió un error al guardar la planilla"
            } else {
                flash.clase = "alert-success"
                flash.message = "Planilla guardada exitosamente"
            }
        }
        redirect(controller: "planilla", action: "list", id: pln.contratoId)
//        render params
    }

}
