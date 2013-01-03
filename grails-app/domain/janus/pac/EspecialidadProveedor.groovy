package janus.pac

class EspecialidadProveedor {

    String descripcion

    static mapping = {
        table 'espc'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'espc__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'espc__id'
            descripcion column: 'espcdscr'
        }
    }
    static constraints = {
        descripcion(nullable: true,blank: true,size: 1..4)
    }
}
