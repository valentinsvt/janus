package janus
class Lugar implements Serializable {
    int codigo
    String descripcion
    String tipo
    static mapping = {
        table 'lgar'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'lgar__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'lgar__id'
            codigo column: 'lgarcdgo'
            descripcion column: 'lgardscr'
            tipo column: 'obratipo'
        }
    }
    static constraints = {
        codigo(blank: false, attributes: [title: 'numero'])
        tipo(size: 1..1, blank: true, nullable: true, attributes: [title: 'tipo'])
        descripcion(size: 1..40, blank: false, attributes: [title: 'descripcion'])

    }
}