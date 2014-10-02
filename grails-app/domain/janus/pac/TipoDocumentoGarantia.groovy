package janus.pac

class TipoDocumentoGarantia {


    String codigo
    String descripcion
    static auditable = true

    static  mapping = {


          table 'tdgr'
          cache usage: 'read-write', include: 'non-lazy'
          id column: 'tdgr__id'
          id generator: 'identity'
          version false
          columns {
              id column: 'tdgr__id'
              codigo column: 'tdgrcdgo'
              descripcion column: 'tdgrdscr'

          }


      }



    static constraints = {



        codigo(size: 1..2, blank: true, nullable: true, attributes: [title: 'código'])
        descripcion(size: 1..31, blank: true, nullable: true, attributes: [title: 'descripción'])

    }
}
