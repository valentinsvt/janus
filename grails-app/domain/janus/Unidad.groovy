package janus
class Unidad implements Serializable {
    String codigo
    String descripcion
    static auditable = true
    static mapping = {
        table 'undd'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'undd__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'undd__id'
            codigo column: 'unddcdgo'
            descripcion column: 'undddscr'
        }
    }
    static constraints = {
        codigo(size: 1..5, blank: false, attributes: [title: 'numero'],unique:true)
        descripcion(size: 1..31, blank: false, nullable: false, attributes: [title: 'descripcion'])
    }

    String toString(){
        return "${this.codigo}"
    }
}