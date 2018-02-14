package janus.ejecucion

import janus.VolumenContrato

class DetallePlanillaEjecucion {

    Planilla planilla
    VolumenContrato volumenContrato
    double cantidad
    double monto

    static mapping = {
        table 'dtpe'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dtpe__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dtpe__id'
            planilla column: 'plnl__id'
            volumenContrato column: 'vocr__id'
            cantidad column: 'dtpecntd'
            monto column: 'dtpemnto'
        }
    }

    static constraints = {
        planilla(blank: true, nullable: true)
        volumenContrato(blank: false, nullable: false)
    }
}
