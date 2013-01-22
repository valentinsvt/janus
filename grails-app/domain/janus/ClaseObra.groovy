package janus
class ClaseObra implements Serializable {
    int codigo
    String descripcion
    String tipo
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
        }
    }
    static constraints = {
        codigo(blank: false, attributes: [title: 'numero'])
        descripcion(size: 1..63, blank: false, attributes: [title: 'descripcion'])
        tipo(size: 1..1, blank: true,nullable:true, attributes:[title: 'tipo'])
    }
    String toString(){
        return "${this.descripcion}"
    }
}