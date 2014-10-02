package janus.pac

class Etapa {

    String descripcion
    static auditable = true
    static mapping = {
        table 'etpa'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'etpa__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'etpa__id'
            descripcion column: 'etpadscr'
        }
    }
    static constraints = {
        descripcion(size: 1..31, blank: false, nullable: false, attributes: [title: 'descripcion'])
    }
}
