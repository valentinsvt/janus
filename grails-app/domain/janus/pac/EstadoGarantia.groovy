package janus.pac

class EstadoGarantia {

    String codigo
    String descripcion

    static auditable = true
    static mapping = {



        table 'edgr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'edgr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'edgr__id'
            codigo column: 'edgrcdgo'
            descripcion column: 'edgrdscr'

        }


    }

    static constraints = {


        codigo(size: 1..1, blank: true, nullable: true, attributes: [title: 'código'])
        descripcion(size: 1..31, blank: true, nullable: true, attributes: [title: 'descripción'])


    }
}
