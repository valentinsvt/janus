package janus.ejecucion

import janus.Obra
import janus.VolumenesObra

class PlanillasAdminController extends janus.seguridad.Shield {
    def preciosService

    def list() {

        def obra = Obra.get(params.id)

        def fp = janus.FormulaPolinomica.findAllByObra(obra)
//        println fp

        def planillaInstanceList = PlanillaAdmin.findAllByObra(obra, [sort: 'id'])
        return [obra: obra, list: planillaInstanceList]
    }

    def form() {
        def obra = Obra.get(params.obra)
        def planillaInstance = new PlanillaAdmin(params)
        planillaInstance.obra = obra
        if (params.id) {
            planillaInstance = PlanillaAdmin.get(params.id)
        }
        def existe = []

//        def avance = TipoPlanilla.findByCodigo('P')
        def avance = TipoPlanilla.findByCodigo('D')
        def resumenMateriales = TipoPlanilla.findByCodigo('M')
        def tiposPlanilla = [:]
//        println avance
//        tiposPlanilla.put(avance.id, avance.nombre)
//        existe.add("P")

        if (PlanillaAdmin.countByObraAndTipoPlanilla(obra, resumenMateriales) == 0) {
            tiposPlanilla.put(resumenMateriales.id, resumenMateriales.nombre)
            existe.add("M")
        }
        if (PlanillaAdmin.countByObraAndTipoPlanilla(obra, avance) == 0) {
            tiposPlanilla.put(avance.id, avance.nombre)
            existe.add("D")
        }

        def planillas = PlanillaAdmin.findAllByObra(obra, [sort: 'fechaIngreso', order: "asc"])

        def cPlanillas = planillas.size()

        if (!params.id) {
            planillaInstance.numero = cPlanillas + 1
        }

        def fechaMin
        if (obra.fechaInicio)
//            fechaMin = obra.fechaInicio.plus(600)   // antes 366
            fechaMin = obra.fechaInicio
        else
            fechaMin = new Date()
        println "fecha max... " + obra.fechaInicio.plus(600) + "  Mínimo: " + obra.fechaInicio

        return [planillaInstance: planillaInstance, obra: obra, tiposPlanilla: tiposPlanilla, fechaMin: fechaMin, existe: existe]

    }


    def save() {
        println "save  " + params
        def obra = Obra.get(params.obra.id)
        if (params.fechaIngreso) {
            params.fechaIngreso = new Date().parse("dd-MM-yyyy", params.fechaIngreso)
        }

        if (params.fechaOficioEntradaPlanilla) {
            params.fechaOficioEntradaPlanilla = new Date().parse("dd-MM-yyyy", params.fechaOficioEntradaPlanilla)
        }

        if (params.oficioEntradaPlanilla) {
            params.oficioEntradaPlanilla = params.oficioEntradaPlanilla.toString().toUpperCase()
        }
        if (params.numero) {
            params.numero = params.numero.toString().toUpperCase()
        }
        def planillaInstance

        if (params.id) {
            planillaInstance = PlanillaAdmin.get(params.id)
            if (!planillaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Planilla con id " + params.id
                params.obra = params.obra.id
                redirect(action: 'form', params: params)
                return
            }//no existe el objeto
            planillaInstance.properties = params
        }//es edit
        else {
            planillaInstance = new PlanillaAdmin(params)
        } //es create
        planillaInstance.fechaIngreso = new Date()
        planillaInstance.usuario = session.usuario
        planillaInstance.estadoPlanilla = EstadoPlanilla.findByCodigo("I")
        planillaInstance.tipoPlanilla = TipoPlanilla.get(params.tipoPlanilla.id)
        planillaInstance.descripcion = params.descripcion
        if (!planillaInstance.save(flush: true)) {
            println planillaInstance.errors
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Planilla " + (planillaInstance.id ? planillaInstance.id : "") + "</h4>"

            str += g.renderErrors(bean: planillaInstance)
            params.obra = obra.id
            flash.message = str
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
            case 'P':
                redirect(action: 'detalle', id: planillaInstance.id)
                break;
            case 'M':
                redirect(controller: "detallePlanillaCostoAdmin", action: 'detalleCosto', id: planillaInstance.id)
                break;
            default:
                redirect(action: 'list', id: planillaInstance.obra.id)
        }
    }

    def detalle() {
        def planilla = PlanillaAdmin.get(params.id)
        if (planilla.tipoPlanilla.codigo == "C") {
            redirect(controller: "detallePlanillaCostoAdmin", action: "detalleCosto", id: planilla.id)
            return
        }
        def obra = planilla.obra
        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])

        def precios = [:]
        def indirecto = obra.totales / 100

        preciosService.ac_rbroObra(obra.id)

        detalle.each {
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
        }

        def planillasAnteriores = PlanillaAdmin.withCriteria {
            eq("obra", obra)
            lt("fechaIngreso", planilla.fechaIngreso)
        }
//        println planillasAnteriores

        def editable = planilla.estadoPlanilla.codigo == "I"
//        editable = PeriodoPlanilla.findAllByPlanilla(planilla).size() == 0
//        println editable

        def codigoPerfil = session.perfil.codigo
//        println codigoPerfil
        switch (codigoPerfil) {
            case "FINA":
            case "ADCT":
                editable = false
                break;
            case "FISC":
//                editable = editable
                break;
            default:
                editable = false
        }
        editable = true
        return [planilla: planilla, detalle: detalle, precios: precios, obra: obra, planillasAnteriores: planillasAnteriores, editable: editable]
    }

    def saveDetalle() {
        def pln = PlanillaAdmin.get(params.id)
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

                def detalle = new DetallePlanillaAdmin([
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

                def detalle = DetallePlanillaAdmin.get(parts[3])
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
            redirect(action: "list", id: pln.obra.id)
            return


        }
    }


}
