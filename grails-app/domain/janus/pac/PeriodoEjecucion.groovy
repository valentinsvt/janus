package janus.pac

import janus.Contrato
import janus.Obra

class PeriodoEjecucion {

    Obra obra
    Integer numero
    String tipo
    Date fechaInicio
    Date fechaFin
    Contrato contrato
    double parcialCronograma=0
    double parcialContrato=0
    double parcialCmpl=0

    static auditable = true
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
            contrato column: 'cntr__id'
            parcialCronograma column: 'prejcrpa'
            parcialContrato column: 'prejcntr'
            parcialCmpl column: 'prejcmpl'
        }
    }
    static constraints = {
        numero(blank: false, nullable: false, attributes: [title: 'periodo'])
        tipo(blank: false, nullable: false, inList: ['P', 'S', 'A', 'C'], attributes: [title: 'tipo'])
        fechaInicio(blank: false, nullable: false, attributes: [title: 'fecha inicio'])
        fechaFin(blank: false, nullable: false, attributes: [title: 'fecha fin'])
        contrato(blank: false, nullable: false, attributes: [title: 'contrato'])
        parcialCronograma(blank: false, nullable: false)
        parcialContrato(blank: false, nullable: false)
        parcialCmpl(blank: false, nullable: false)
    }

}
