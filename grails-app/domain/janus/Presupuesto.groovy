package janus

class Presupuesto implements Serializable {
    String numero
    String descripcion
    int nivel

    static mapping = {
        table 'prsp'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prsp__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prsp__id'
            numero column: 'prspnmro'
            descripcion column: 'prspdscr'
            nivel column: 'prspnvel'

        }
    }
    static constraints = {
        numero(size: 1..31, blank: false, attributes: [title: 'numero'])
        nivel(blank: true, attributes: [title: 'nivel'])
        descripcion(size: 1..255, blank: false, attributes: [title: 'descripcion'])
    }

    String toString() {
        return this.descripcion
    }
}