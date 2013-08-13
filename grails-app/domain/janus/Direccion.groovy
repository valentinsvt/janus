package janus

class Direccion {
    String nombre
    static mapping = {
        table 'dire'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dire__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dire__id'
            nombre column: 'diredscr'
        }
    }
    static constraints = {
        nombre(size: 1..63, blank: false, attributes: [title: 'Nombre de la Dirección'])
    }

    String toString() {
        nombre
    }
}
