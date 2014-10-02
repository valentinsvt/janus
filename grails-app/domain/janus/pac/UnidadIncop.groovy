package janus.pac

class UnidadIncop {
    String codigo
    String descripcion
    static auditable = true
    static mapping = {
        table 'uncp'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'uncp__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'uncp__id'
            codigo column: 'uncpcgdo'
            descripcion column: 'uncpdscr'
        }
    }
    static constraints = {
        codigo(nullable: true,blank: true,size: 1..7,unique: true)
        descripcion(nullable: true,blank: true,size:1..32)
    }
}
