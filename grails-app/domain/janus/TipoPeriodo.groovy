package janus

class TipoPeriodo {


    String codigo
    String descripcion
    static auditable = true
    static mapping = {

        table 'tppr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tppr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tppr__id'
            codigo column: 'tpprcdgo'
            descripcion column: 'tpprdscr'
        }


    }

    static constraints = {

        codigo(size: 1..1, blank: false, attributes: [title: 'codigo'])
        descripcion(size: 1..31, blank: false, attributes: [title: 'descripcion'])

    }
}
