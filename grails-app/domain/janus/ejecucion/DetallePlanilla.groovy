package janus.ejecucion

import janus.Item
import janus.VolumenesObra

class DetallePlanilla {

    Planilla planilla
    VolumenesObra volumenObra
    Item item
    double cantidad
    double monto
    String observaciones

    static mapping = {
        table 'dtpl'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dtpl__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dtpl__id'
            planilla column: 'plnl__id'
            volumenObra column: 'vlob__id'
            item column: 'item__id'
            cantidad column: 'dtplcntd'
            monto column: 'dtplmnto'
            observaciones column: 'dtplobsr'
        }
    }

    static constraints = {
        planilla(blank: true, nullable: true)
        volumenObra(blank: true, nullable: true)
        item(blank: true, nullable: true)
        cantidad(blank: true, nullable: true)
        monto(blank: true, nullable: true)
        observaciones(maxSize: 127, blank: true, nullable: true)
    }
}
