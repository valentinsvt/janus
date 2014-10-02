package janus
class ClaseObra implements Serializable {
    int codigo
    String descripcion
    String tipo
    Grupo grupo
    static auditable = true
    static mapping = {
        table 'csob'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'csob__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'csob__id'
            codigo column: 'csobcdgo'
            descripcion column: 'csobdscr'
            tipo column: 'csobtipo'
            grupo column: 'grpo__id'
        }
    }
    static constraints = {
        codigo(blank: false, attributes: [title: 'numero'])
        descripcion(size: 1..63, blank: false, attributes: [title: 'descripcion'])
        tipo(size: 1..1, blank: true,nullable:true, attributes:[title: 'tipo'])
        grupo(blank: true,nullable:true, attributes:[title: 'grupo'])
    }
    String toString(){
        return "${this.descripcion}"
    }
}