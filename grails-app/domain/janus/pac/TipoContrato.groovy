package janus.pac

class TipoContrato {

    String codigo
    String descripcion
    static auditable = true
    static mapping = {
        table 'tpcr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpcr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpcr__id'
            codigo column: 'tpcrcdgo'
            descripcion column: 'tpcrdscr'
        }
    }
    static constraints = {
        codigo(nullable: true,blank: true,size: 1..2)
        descripcion(nullable: true,blank: true,size:1..63)
    }
}
