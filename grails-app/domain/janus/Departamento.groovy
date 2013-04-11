package janus

class Departamento implements Serializable {
    String descripcion
    Direccion direccion
    static mapping = {
        table 'dpto'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dpto__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dpto__id'
            descripcion column: 'dptodscr'
            direccion column: 'dire__id'
        }
    }
    static constraints = {
        descripcion(size: 1..31, blank: false, attributes: [title: 'descripcion'])
        direccion(blank: true, attributes: [title: 'Direccion'])
    }

    String toString() {
        "${direccion.nombre} - ${descripcion}"
    }
}
