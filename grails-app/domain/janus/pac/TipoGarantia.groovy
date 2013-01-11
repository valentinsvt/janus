package janus.pac

class TipoGarantia {

    String codigo
    String descripcion


    static mapping = {


        table 'tpgr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpgr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpgr__id'
            codigo column: 'tpgrcdgo'
            descripcion column: 'tpgrdscr'

        }





}

    static constraints = {



        codigo(size: 1..1, blank: true, nullable: true, attributes: [title: 'código'])
        descripcion(size: 1..30, blank: true, nullable: true, attributes: [title: 'descripción'])

    }
}
