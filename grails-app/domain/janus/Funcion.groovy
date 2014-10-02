package janus
class Funcion implements Serializable {
    String codigo
    String descripcion
    static auditable = true
    static mapping = {
        table 'func'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'func__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'func__id'
            codigo column: 'funccdgo'
            descripcion column: 'funcdscr'
        }
    }
    static constraints = {
        codigo(size: 1..1, blank: false, attributes: [title: 'numero'])
        descripcion(size: 1..31, blank: false, attributes: [title: 'descripcion'])
    }
}