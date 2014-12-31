package janus

class TipoDeBien implements Serializable {
    String codigo
    String descripcion
    int porcentaje
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
            porcentaje column: 'tpbnpcnt'
        }
    }
    static constraints = {
        codigo(size: 1..2, blank: false, attributes: [title: 'numero'])
        descripcion(size: 1..63, blank: false, attributes: [title: 'descripcion'])
        porcentaje(blank: false, attributes: [title: 'porcentaje'])
    }
    String toString(){
        "${descripcion}"
    }
}