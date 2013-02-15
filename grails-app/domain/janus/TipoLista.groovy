package janus

class TipoLista {
    String codigo
    String descripcion
    static mapping = {
        table 'tpls'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpls__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpls__id'
            codigo column: 'tplscdgo'
            descripcion column: 'tplsdscr'
        }
    }
    static constraints = {
        codigo(blank: false, attributes: [title: 'codigo'],size: 1..2)
        descripcion(size: 1..63, blank: false, attributes: [title: 'descripcion'])
    }
}
