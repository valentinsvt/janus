package janus
class SubPrograma implements Serializable {
    String descripcion
    String tipo
    static mapping = {
        table 'sbpr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'sbpr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'sbpr__id'
            descripcion column: 'sbprdscr'
            tipo column: 'sbprtipo'
        }
    }
    static constraints = {
        tipo(size: 1..1, blank: false, attributes: [title: 'tipo'])
        descripcion(size: 1..127, blank: false, attributes: [title: 'descripcion'])

    }
}