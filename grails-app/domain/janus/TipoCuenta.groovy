package janus
class TipoCuenta implements Serializable {
    String codigo
    String descripcion
    static auditable = true
    static mapping = {
        table 'tpcn'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpcn__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpcn__id'
            codigo column: 'tpcncdgo'
            descripcion column: 'tpcndscr'
        }
    }
    static constraints = {
        codigo(size: 1..1, blank: false, attributes: [title: 'numero'])
        descripcion(size: 1..63, blank: false, nullable: false, attributes: [title: 'descripcion'])
    }
}