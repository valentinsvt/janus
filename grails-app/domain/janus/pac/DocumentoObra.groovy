package janus.pac

import janus.Obra

class DocumentoObra {

    Obra obra
    String descripcion
    String palabrasClave
    String resumen
    String nombre
    String path
    static auditable = true
    static mapping = {
        table 'dcob'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dcob__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dcob__id'

            obra column: 'obra__id'
            descripcion column: 'dcobdscr'
            palabrasClave column: 'dcobclve'
            resumen column: 'dcobrsmn'
            nombre column: 'dcobdcmt'
            path column: 'dcobpath'
        }
    }
    static constraints = {
        obra(blank: true, nullable: true)
        descripcion(blank: true, nullable: true, maxSize: 63)
        palabrasClave(blank: true, nullable: true, maxSize: 63)
        resumen(blank: true, nullable: true, maxSize: 1024)
        nombre(blank: true, nullable: true, maxSize: 255)
        path(blank: true, nullable: true, maxSize: 1024)
    }
}
