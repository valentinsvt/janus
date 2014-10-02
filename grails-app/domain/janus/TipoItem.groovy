package janus
class TipoItem implements Serializable {
    String codigo
    String descripcion
    static auditable = true
    static mapping = {
        table 'tpit'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpit__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpit__id'
            codigo column: 'tpitcdgo'
            descripcion column: 'tpitdscr'
        }
    }
    static constraints = {
        codigo(size: 1..1, blank: false, attributes: [title: 'numero'])
        descripcion(size: 1..20, blank: false, nullable: false, attributes: [title: 'descripcion'])
    }
}