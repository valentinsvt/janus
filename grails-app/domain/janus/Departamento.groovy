package janus

class Departamento implements Serializable {
    String descripcion
    Direccion direccion
    String permisos
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
            permisos column: 'dptoprms'
        }
    }

    static constraints = {
        descripcion(size: 1..31, blank: false, attributes: [title: 'descripcion'])
        direccion(blank: true, attributes: [title: 'Direccion'])
        permisos(blank: true,nullable: true, size: 1..124)
    }

    String toString() {
        "${direccion.nombre} - ${descripcion}"
    }

}
