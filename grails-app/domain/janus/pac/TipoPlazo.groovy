package janus.pac

class TipoPlazo {

    String codigo
    String descripcion
    static auditable = true
    static mapping = {
        table 'tppz'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tppz__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tppz__id'
            codigo column: 'tppzcgdo'
            descripcion column: 'tppzdscr'
        }
    }
    static constraints = {
        codigo(nullable: true,blank: true,size: 1..7)
        descripcion(nullable: true,blank: true,size:1..32)
    }
}
