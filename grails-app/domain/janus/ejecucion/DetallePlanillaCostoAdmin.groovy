package janus.ejecucion

import janus.Unidad

class DetallePlanillaCostoAdmin {

    PlanillaAdmin planilla
    String rubro
    Unidad unidad
    double cantidad
    double valor             // valor del rubro sin iva
    double valorIva         // valor del rubro incluido iva

    static mapping = {
        table 'dpca'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dpca__id'
        id generator: 'identity'
        version false
        columns {
            planilla column: 'plad__id'
            rubro column: 'dpcarbro'
            unidad column: 'undd__id'
            valor column: 'dpcavlor'
            valorIva column: 'dpcavliv'
            cantidad column: 'dpcacntd'
        }
    }
}
