package janus

class RolTramite implements Serializable{

    String codigo
    String descripcion
    static auditable = true
    static mapping = {

        table 'rltr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'rltr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'rltr__id'
           codigo column: 'rltrcdgo'
           descripcion column: 'rltrdscr'

        }
    }

    static constraints = {
        codigo(size: 1..4, blank: true, nullable: true, attributes: [title: 'numero'])
        descripcion(size: 1..63, blank: false, nullable: false, attributes: [title: 'descripcion'])
    }

    String toString() {
        "${descripcion}"
    }

}
