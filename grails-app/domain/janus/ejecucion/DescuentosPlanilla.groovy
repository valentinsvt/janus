package janus.ejecucion

class DescuentosPlanilla {

    Planilla planilla
    DescuentoTipoPlanilla descuentoTipoPlanilla
    double monto
    static auditable = true
    static mapping = {
        table 'dspl'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dspl__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dspl__id'
            planilla column: 'plnl__id'
            descuentoTipoPlanilla column: 'dstp__id'
            monto column: 'dsplmnto'
        }
    }

    static constraints = {
        planilla(blank: true, nullable: true)
        descuentoTipoPlanilla(blank: true, nullable: true)
        monto(blank: false, nullable: false)
    }
}
