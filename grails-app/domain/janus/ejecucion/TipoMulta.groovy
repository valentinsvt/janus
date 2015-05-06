package janus.ejecucion

class TipoMulta {

    String descripcion
    static auditable = true
    static mapping = {
        table 'tpml'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpml__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpml__id'
            descripcion column: 'tpmldscr'
        }
    }

    static constraints = {
        descripcion(maxSize: 63, blank: false, nullable: false)
    }
}
