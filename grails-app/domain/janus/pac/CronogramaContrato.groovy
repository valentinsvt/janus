package janus.pac

import janus.Contrato
import janus.VolumenesObra

class CronogramaContrato {

    Contrato contrato

    VolumenesObra volumenObra
    Integer periodo
    Double precio
    Double porcentaje
    Double cantidad
    static auditable = true
    static mapping = {
        table 'crng'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'crng__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'crng__id'
            contrato column: 'cntr__id'
            volumenObra column: 'vlob__id'
            periodo column: 'crngprdo'
            precio column: 'crngprco'
            porcentaje column: 'crngprct'
            cantidad column: 'crngcntd'
        }
    }
    static constraints = {
        contrato(blank: false, nullable: false)
        volumenObra(blank: false, nullable: false, attributes: [title: 'volumen de obra'])
        periodo(blank: false, nullable: false, attributes: [title: 'periodo'])
        precio(blank: false, nullable: false, attributes: [title: 'precio'])
        porcentaje(blank: false, nullable: false, attributes: [title: 'porcentaje'])
        cantidad(blank: false, nullable: false, attributes: [title: 'cantidad'])
    }
}
