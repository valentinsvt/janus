package janus

class Grupo implements Serializable {

    String codigo
    String descripcion

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
        }
    }
    static constraints = {
        codigo(size: 1..3, blank: false, attributes: [title: 'numero'])
        descripcion(size: 1..31, blank: false, attributes: [title: 'descripcion'])
    }
    String toString() {
        descripcion
    }
}