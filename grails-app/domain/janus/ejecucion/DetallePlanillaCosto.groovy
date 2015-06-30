package janus.ejecucion

import janus.Unidad

class DetallePlanillaCosto {

    Planilla planilla
    String factura
    String rubro
    Unidad unidad
//    double indirectos       // porcentaje de costos indirectos
    double monto            // monto del rubro sin iva
    double montoIva         // monto del rubro incluido iva
    double montoIndirectos  // monto de los costos indirectos
    double cantidad         // monto de los costos indirectos

    static mapping = {
        table 'dpcs'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dpcs__id'
        id generator: 'identity'
        version false
        columns {
            factura column: 'dpcsfctr'
            planilla column: 'plnl__id'
            rubro column: 'dpcsrbro'
            unidad column: 'undd__id'
//            indirectos column: 'dpcsindr'
            monto column: 'dpcsmnto'
            montoIva column: 'dpcsmniv'
            montoIndirectos column: 'dpcsmnin'
            cantidad column: 'dpcscntd'
        }
    }

    static constraints = {

    }
}
