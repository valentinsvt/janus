package janus

class FuenteFinanciamiento {


    String descripcion
    static auditable = true
    static mapping = {
        table 'fnfn'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'fnfn__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'fnfn__id'
            descripcion column: 'fnfndscr'

        }
    }
    static constraints = {
        descripcion(size: 1..100, blank: false, attributes: [title: 'descripcion'])
    }

    String toString() {
        return this.descripcion
    }
}
