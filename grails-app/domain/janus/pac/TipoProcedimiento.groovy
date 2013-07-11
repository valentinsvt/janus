package janus.pac

class TipoProcedimiento {

    String descripcion
    String sigla
    double bases = 0
    double techo = 0

    int preparatorio = 0
    int precontractual = 0
    int contractual = 0

    String fuente

    static mapping = {
        table 'tppc'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tppc__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tppc__id'
            descripcion column: 'tppcdscr'
            sigla column: 'tppcsgla'
            bases column: 'tppcbase'
            techo column: 'tppctcho'
            fuente column: 'tppcfnte'
            preparatorio column: 'tppcprpt'
            precontractual column: 'tppcprct'
            contractual column: 'tppccntr'
        }
    }
    static constraints = {
        descripcion(nullable: true, blank: true, size: 1..64)
        sigla(nullable: false, blank: false, size: 1..5)
        fuente(size: 2..2, inList: ['OF', 'OB'])
    }

}
