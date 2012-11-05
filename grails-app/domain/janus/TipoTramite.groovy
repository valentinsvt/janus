package janus
class TipoTramite implements Serializable {
    String descripcion
    static mapping = {
        table 'tpto'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpto__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpto__id'
            descripcion column: 'tptodscr'
        }
    }
    static constraints = {
        descripcion(size: 1..63, blank: false, attributes: [title: 'descripcion'])
    }
}