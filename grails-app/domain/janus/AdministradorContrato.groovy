package janus

class AdministradorContrato {

    Contrato contrato
    Persona administrador
    Date fechaInicio
    Date fechaFin
    static auditable = true
    static mapping = {
        table 'adcr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'adcr__id'
        id generator: 'identity'
        version false
        columns {
            contrato column: 'cntr__id'
            administrador column: 'prsn__id'
            fechaInicio column: 'adcrfcin'
            fechaFin column: 'adcrfcfn'
        }
    }

    static constraints = {
        fechaInicio(blank: true, nullable: true)
        fechaFin(blank: true, nullable: true)
    }
}
