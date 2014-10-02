package janus

class EstadoTramite {

    String codigo
    String descripcion
    static auditable = true
    static constraints = {
        codigo(size: 1..3, blank: false, nullable: false, attributes: [title: 'codigo'])
        descripcion(size: 1..31, blank: false, nullable: false, attributes: [title: 'descripcion'])
    }

    static mapping = {
        table 'ettr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'ettr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'ettr__id'
            codigo column: 'ettrcdgo'
            descripcion column: 'ettrdscr'
        }
    }
}
