package janus
class Indice implements Serializable {
    TipoInstitucion tipoInstitucion
    String codigo
    String descripcion
    static mapping = {
        table 'indc'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'indc__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'indc__id'
            tipoInstitucion column: 'tpin__id'
            codigo column: 'indccdgo'
            descripcion column: 'indcdscr'
        }
    }
    static constraints = {
        tipoInstitucion(blank: true, nullable: true, attributes: [title: 'tipoInstitucion'])
        codigo(size: 1..20, blank: false, attributes: [title: 'numero'])
        descripcion(size: 1..40, blank: false, attributes: [title: 'descripcion'])
    }
}