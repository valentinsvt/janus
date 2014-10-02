package janus
class TipoObra implements Serializable {
    String codigo
    String descripcion
    Grupo grupo
    static auditable = true
    static mapping = {
        table 'tpob'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpob__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpob__id'
            codigo column: 'tpobcdgo'
            descripcion column: 'tpobdscr'
            grupo column: 'grpo__id'
        }
    }
    static constraints = {
        codigo(size: 1..4, unique: true, blank: false, attributes: [title: 'numero'])
        descripcion(size: 1..63, unique: true, blank: false, attributes: [title: 'descripcion'])
        grupo(blank: true, nullable: true, attributes: [title: 'grupo'])
    }
}