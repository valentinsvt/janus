package janus.pac

class TipoAseguradora {


    String codigo
    String descripcion

    static auditable = true
    static mapping = {


        table 'tpas'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpas__id'
        id generator: 'identity'
        version false
        columns {
              id column: 'tpas__id'
             codigo column: 'tpascdgo'
            descripcion column: 'tpasdscr'

        }


    }


    static constraints = {

        codigo(size: 1..1, blank: true, nullable: true, attributes: [title: 'código'])
        descripcion(size: 1..31, blank: true, nullable: true, attributes: [title: 'descripción'])


    }
}
