package janus

class TipoDeBien implements Serializable {
    String codigo
    String descripcion
    static auditable = true
    static mapping = {
        table 'tpbn'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpbn__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpbn__id'
            codigo column: 'tpbncdgo'
            descripcion column: 'tpbndscr'
        }
    }
    static constraints = {
        codigo(size: 1..2, blank: false, attributes: [title: 'numero'])
        descripcion(size: 1..31, blank: false, attributes: [title: 'descripcion'])
    }
    String toString(){
        "${descripcion}"
    }
}