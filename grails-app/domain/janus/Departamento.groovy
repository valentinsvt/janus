package janus

class Departamento implements Serializable {
    String descripcion
    static mapping = {
        table 'dpto'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dpto__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dpto__id'
            descripcion column: 'dptodscr'
        }
    }
    static constraints = {
        descripcion(size: 1..31, blank: false, attributes: [title: 'descripcion'])
    }

    String toString() {
        return this.descripcion
    }
}
