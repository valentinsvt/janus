package janus
class SubPresupuesto implements Serializable {
    String descripcion
    String tipo
    Grupo grupo
    static auditable = true
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
            grupo column: 'grpo__id'
        }
    }
    static constraints = {
        tipo(size: 1..1, blank: true, nullable: true, attributes: [title: 'tipo'])
        descripcion(size: 1..127, blank: false, attributes: [title: 'descripcion'])
//        grupo(blank: false, attributes: [title: 'grupo'])

    }
}