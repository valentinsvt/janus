package janus.pac

class ProgramaPresupuestario {

    String descripcion
    String codigo
    static auditable = true
    static mapping = {
        table 'pgps'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'pgps__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'pgps__id'
            descripcion column: 'pgpsdscr'
            codigo column: 'pgpscdgo'
        }
    }
    static constraints = {
        descripcion(nullable: true,blank: true,size: 1..256)
        codigo(nullable: false,blank: false,size: 1..30)
    }
}
