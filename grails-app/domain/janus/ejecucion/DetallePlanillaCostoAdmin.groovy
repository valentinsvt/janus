package janus.ejecucion

import janus.Unidad

class DetallePlanillaCostoAdmin {

    PlanillaAdmin planilla
    String factura
    String rubro
    Unidad unidad
    double indirectos       // porcentaje de costos indirectos
    double monto            // monto del rubro sin iva
    double montoIva         // monto del rubro incluido iva
    double montoIndirectos  // monto de los costos indirectos

    static mapping = {
        table 'dpca'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dpca__id'
        id generator: 'identity'
        version false
        columns {
            factura column: 'dpcafctr'
            planilla column: 'plad__id'
            rubro column: 'dpcarbro'
            unidad column: 'undd__id'
            indirectos column: 'dpcaindr'
            monto column: 'dpcamnto'
            montoIva column: 'dpcamniv'
            montoIndirectos column: 'dpcamnin'
        }
    }

    static constraints = {
        factura(blank: true, nullable: true)
    }
}
