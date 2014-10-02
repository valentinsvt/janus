package janus

class FiscalizadorContrato {

    Contrato contrato
    Persona fiscalizador
    Date fechaInicio
    Date fechaFin
    static auditable = true
    static mapping = {
        table 'fscr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'fscr__id'
        id generator: 'identity'
        version false
        columns {
            contrato column: 'cntr__id'
            fiscalizador column: 'prsn__id'
            fechaInicio column: 'fscrfcin'
            fechaFin column: 'fscrfcfn'
        }
    }

    static constraints = {
        fechaInicio(blank: true, nullable: true)
        fechaFin(blank: true, nullable: true)
    }
}
