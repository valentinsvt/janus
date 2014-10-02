package janus
class EstadoObra implements Serializable {
    String codigo
    String descripcion
    static auditable = true
    static mapping = {
        table 'edob'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'edob__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'edob__id'
            codigo column: 'edobcdgo'
            descripcion column: 'edobdscr'
        }
    }
    static constraints = {
        codigo(size: 1..1, blank: false, attributes: [title: 'numero'])
        descripcion(size: 1..31, blank: false, attributes: [title: 'descripcion'])
    }

   String toString(){
       return "${this.descripcion}"
   }
}