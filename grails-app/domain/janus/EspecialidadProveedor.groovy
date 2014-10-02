package janus
class EspecialidadProveedor implements Serializable {
    String descripcion
    static auditable = true
    static mapping = {
        table 'espc'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'espc__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'espc__id'
            descripcion column: 'espcdscr'
        }
    }
    static constraints = {
        descripcion(size: 1..63, blank: true, nullable: true, attributes: [title: 'descripcion'])
    }
}