package janus.pac

class TipoContrato {

    String codigo
    String descripcion

    static mapping = {
        table 'tpcr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpcr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpcr__id'
            codigo column: 'tpcrcgdo'
            descripcion column: 'tpcrdscr'
        }
    }
    static constraints = {
        codigo(nullable: true,blank: true,size: 1..7)
        descripcion(nullable: true,blank: true,size:1..32)
    }
}
