package janus.pac

import janus.VolumenContrato

class CrngEjecucionObra {

    VolumenContrato volumenObra
    PeriodoEjecucion periodo
    Double precio
    Double porcentaje
    Double cantidad
    static auditable = true
    static mapping = {
        table 'creo'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'creo__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'creo__id'
            volumenObra column: 'vocr__id'
            periodo column: 'prej__id'
            precio column: 'creoprco'
            porcentaje column: 'creoprct'
            cantidad column: 'creocntd'
        }
    }
    static constraints = {
        volumenObra(blank: false, nullable: false, attributes: [title: 'volumen de obra'])
        periodo(blank: false, nullable: false, attributes: [title: 'periodo'])
        precio(blank: false, nullable: false, attributes: [title: 'precio'])
        porcentaje(blank: false, nullable: false, attributes: [title: 'porcentaje'])
        cantidad(blank: false, nullable: false, attributes: [title: 'cantidad'])
    }
}
