package janus.pac

import janus.Contrato
import janus.Obra

class PeriodoEjecucionTmp {

    Obra obra
    Integer numero
    String tipo
    Date fechaInicio
    Date fechaFin
    Contrato contrato

    static auditable = true
    static mapping = {
        table 'prej_t'
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
            contrato column: 'cntr__id'
        }
    }
    static constraints = {
        numero(blank: false, nullable: false, attributes: [title: 'periodo'])
        tipo(blank: false, nullable: false, inList: ['P', 'S', 'A'], attributes: [title: 'tipo'])
        fechaInicio(blank: false, nullable: false, attributes: [title: 'fecha inicio'])
        fechaFin(blank: false, nullable: false, attributes: [title: 'fecha fin'])
        contrato(blank: false, nullable: false, attributes: [title: 'contrato'])
    }

}
