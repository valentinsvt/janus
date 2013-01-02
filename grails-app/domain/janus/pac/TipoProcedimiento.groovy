package janus.pac

class TipoProcedimiento {

    String descripcion

    static mapping = {
        table 'tppc'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tppc__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tppc__id'
            descripcion column: 'tppcdscr'
        }
    }
    static constraints = {
        descripcion(nullable: true,blank: true,size: 1..64)
    }

}
