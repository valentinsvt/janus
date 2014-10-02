package janus.pac

class TipoCompra {

    String descripcion
    static auditable = true
    static mapping = {
        table 'tpcp'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpcp__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpcp__id'
            descripcion column: 'tpcpdscr'
        }
    }
    static constraints = {
        descripcion(nullable: true,blank: true,size: 1..64)
    }
}
