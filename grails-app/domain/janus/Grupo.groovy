package janus

class Grupo implements Serializable {

    String codigo
    String descripcion
    Direccion direccion
    static auditable = true
    static mapping = {
        table 'grpo'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'grpo__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'grpo__id'
            codigo column: 'grpocdgo'
            descripcion column: 'grpodscr'
            direccion column: 'dire__id'
        }
    }
    static constraints = {
        codigo(size: 1..3, blank: false, attributes: [title: 'numero'])
        descripcion(size: 1..31, blank: false, attributes: [title: 'descripcion'])
//        direccion(blank: false, attributes: [title: 'direccion'])
    }
    String toString() {
        descripcion
    }
}