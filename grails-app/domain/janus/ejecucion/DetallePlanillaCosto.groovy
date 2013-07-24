package janus.ejecucion

import janus.Unidad

class DetallePlanillaCosto {

    Planilla planilla
    String rubro
    Unidad unidad
    double cantidad
    double monto
    double indirectos

    static mapping = {
        table 'dpcs'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dpcs__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dtpl__id'
            planilla column: 'plnl__id'
            rubro column: 'dpcsrbro'
            unidad column: 'undd__id'
            cantidad column: 'dpcscntd'
            monto column: 'dpcsmnto'
            indirectos column: 'dpcsindr'
        }
    }

    static constraints = {

    }
}
