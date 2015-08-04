package janus

import janus.ejecucion.DetalleReajuste
import janus.ejecucion.Planilla
import janus.ejecucion.ReajustePlanilla

class ReajustePlanillaController {

    def index() {
        def planilla = Planilla.get(params.id)
        return [planilla: planilla]
    }

    def tablaReajuste() {
        def reajuste = ReajustePlanilla.get(params.id)
        def detalles = DetalleReajuste.findAllByReajustePlanilla(reajuste, [sort: "id"])
        return [reajuste: reajuste, detalles: detalles]
    }

    def guardarCambios() {
//        println params
        /*
            504_vo:110.643,
            504_v:10.000,
            506_vp:11.658,
            505_vo:111.137,
            506_ip:128.580,
            504_io:124.080,
            504_ip:125.430,
            507_ip:137.350,
            507_io:134.850,
            505_ip:125.740,
            507_v:10.000,
            505_io:124.370,
            506_v:10.000,
            504_vp:111.240
         */
        def errores = ""
        def datos = [:]
        params.each { k, v ->
            def parts = k.split("_")
            if (parts.size() == 2) {
                def id = "" + parts[0]
                def tipo = "" + parts[1]
                if (!datos[id]) {
                    datos[id] = [:]
                }
                datos[id][tipo] = v;
            }
        }
        datos.each { id, valores ->
            def detalle = DetalleReajuste.get(id.toLong())
            if (valores.io) {
                detalle.indiceOferta = valores.io.toDouble()
            }
            if (valores.ip) {
                detalle.indicePeriodo = valores.ip.toDouble()
            }
            if (valores.vo) {
                detalle.valorIndcOfrt = valores.vo.toDouble()
            }
            if (valores.vp) {
                detalle.valorIndcPrdo = valores.vp.toDouble()
            }
            if (valores.v) {
                detalle.valor = valores.v.toDouble()
            }
            if (!detalle.save(flush: true)) {
                errores += renderErrors(bean: detalle)
            }
        }
        if (errores == "") {
            render "OK*Datos guardados"
        } else {
            render "NO*" + errores
        }
    }
}
