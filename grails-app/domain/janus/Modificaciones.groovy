package janus

class Modificaciones implements Serializable {

    Obra obra
    String tipo
    int dias
    Date fechaInicio
    Date fechaFin
    Date fechaMemo
    Date fecha
    String memo
    String motivo
    String observaciones
    VolumenesObra volObra
    Contrato contrato

    static auditable = true
    static mapping = {

        table 'mdce'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'mdce__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'mdce__id'
            obra column: 'obra__id'
            tipo column: 'mdcetipo'
            dias column: 'mdcedias'
            fechaInicio column: 'mdcefcin'
            fechaFin column: 'mdcefcfn'
            fechaMemo column: 'mdcefcme'
            fecha column: 'mdcefcha'
            memo column: 'mdcememo'
            motivo column: 'mdcemtvo'
            observaciones column: 'mdceobsv'
            volObra column: 'vlob__id'
            contrato column: 'cntr__id'
        }

    }

    static constraints = {
        tipo(blank: true, nullable: true, inList: ['A', 'S', 'C']) //ampliacion, suspension, complementario
        dias(blank: true, nullable: true)
        fechaInicio(blank: true, nullable: true)
        fechaFin(blank: true, nullable: true)
        fechaMemo(blank: true, nullable: true)
        fecha(blank: true, nullable: true)
        memo(blank: true, nullable: true)
        motivo(blank: true, nullable: true)
        observaciones(blank: true, nullable: true)
        volObra(blank: true, nullable: true)
        contrato(blank: false, nullable: false)
    }
}
