package janus.pac

import janus.Contrato
import janus.VolumenContrato

class CronogramaContratado {

    Contrato contrato
    VolumenContrato volumenContrato
    Integer periodo
    Double precio
    Double porcentaje
    Double cantidad
    static auditable = true
    static mapping = {
        table 'crcr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'crcr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'crcr__id'
            contrato column: 'cntr__id'
            volumenContrato column: 'vocr__id'
            periodo column: 'crcrprdo'
            precio column: 'crcrprco'
            porcentaje column: 'crcrprct'
            cantidad column: 'crcrcntd'
        }
    }
    static constraints = {
        contrato(blank: false, nullable: false)
        volumenContrato(blank: false, nullable: false, attributes: [title: 'volumen de obra contratado'])
        periodo(blank: false, nullable: false, attributes: [title: 'periodo'])
        precio(blank: false, nullable: false, attributes: [title: 'precio'])
        porcentaje(blank: false, nullable: false, attributes: [title: 'porcentaje'])
        cantidad(blank: false, nullable: false, attributes: [title: 'cantidad'])
    }
}
