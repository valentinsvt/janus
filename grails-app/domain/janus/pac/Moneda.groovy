package janus.pac

class Moneda {

    String codigo
    String descripcion
    static auditable = true
    static mapping = {
        table 'mnda'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'mnda__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'mnda__id'
            codigo column: 'mndacdgo'
            descripcion column: 'mndadscr'
        }
    }


    static constraints = {
        codigo(size: 1..4, blank: true, nullable: true, attributes: [title: 'código'])
        descripcion(size: 1..31, blank: true, nullable: true, attributes: [title: 'descripción'])
    }
}
