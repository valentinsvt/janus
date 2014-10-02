package janus

class TipoUsuario {
    String descripcion
    static auditable = true
    static mapping = {

        table 'tpus'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpus__id'
        id generator: 'identity'
        version false
        columns {

            id column: 'tpus__id'
            descripcion column: 'tpusdscr'


        }


        }

    static constraints = {

        descripcion(size: 1..31, blank: false, attributes: [title: 'descripcion'])

    }
}
