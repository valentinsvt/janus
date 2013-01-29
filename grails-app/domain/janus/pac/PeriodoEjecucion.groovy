package janus.pac

import janus.Obra

class PeriodoEjecucion {

    Obra obra
    Integer numero
    String tipo
    Date fechaInicio
    Date fechaFin

    static mapping = {
        table 'prej'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prej__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prej__id'
            obra column: "obra__id"
            numero column: 'prejnmro'
            tipo column: 'prejtipo'
            fechaInicio column: 'prejfcin'
            fechaFin column: 'prejfcfn'
        }
    }
    static constraints = {
        numero(blank: false, nullable: false, attributes: [title: 'periodo'])
        tipo(blank: false, nullable: false, inList: ['P', 'S'], attributes: [title: 'tipo'])
        fechaInicio(blank: false, nullable: false, attributes: [title: 'fecha inicio'])
        fechaFin(blank: false, nullable: false, attributes: [title: 'fecha fin'])
    }

}
