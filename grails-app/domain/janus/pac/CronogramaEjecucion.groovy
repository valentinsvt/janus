package janus.pac

import janus.VolumenesObra

class CronogramaEjecucion {

    VolumenesObra volumenObra
    PeriodoEjecucion periodo
//    Integer periodo
//    String tipo
//    Date fechaInicio
//    Date fechaFin
    Double precio
    Double porcentaje
    Double cantidad
    static auditable = true
    static mapping = {
        table 'crej'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'crej__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'crej__id'
            volumenObra column: 'vlob__id'
            periodo column: 'prej__id'
//            tipo column: 'crejtipo'
//            fechaInicio column: 'crejfcin'
//            fechaFin column: 'crejfcfn'
            precio column: 'crejprco'
            porcentaje column: 'crejprct'
            cantidad column: 'crejcntd'
        }
    }
    static constraints = {
        volumenObra(blank: false, nullable: false, attributes: [title: 'volumen de obra'])
        periodo(blank: false, nullable: false, attributes: [title: 'periodo'])
//        tipo(blank: false, nullable: false, inList: ['P', 'S'], attributes: [title: 'tipo'])
//        fechaInicio(blank: false, nullable: false, attributes: [title: 'fecha inicio'])
//        fechaFin(blank: false, nullable: false, attributes: [title: 'fecha fin'])
        precio(blank: false, nullable: false, attributes: [title: 'precio'])
        porcentaje(blank: false, nullable: false, attributes: [title: 'porcentaje'])
        cantidad(blank: false, nullable: false, attributes: [title: 'cantidad'])
    }
}
