package janus.ejecucion

import janus.Item
import janus.VolumenesObra

class DetallePlanillaAdmin {

    PlanillaAdmin planilla
    VolumenesObra volumenObra
    Item item
    double cantidad
    double monto
    String observaciones

    static mapping = {
        table 'dtpa'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dtpa__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dtpa__id'
            planilla column: 'plad__id'
            volumenObra column: 'vlob__id'
            item column: 'item__id'
            cantidad column: 'dtpacntd'
            monto column: 'dtpamnto'
            observaciones column: 'dtpaobsr'
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
