package janus.pac

class DocumentoProceso {

    Etapa etapa
    Concurso concurso
    String descripcion
    String palabrasClave
    String resumen
    String nombre
    String path
    static auditable = true
    static mapping = {
        table 'dcmt'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dcmt__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dcmt__id'

            etapa column: 'etpa__id'
            concurso column: 'cncr__id'
            descripcion column: 'dcmtdscr'
            palabrasClave column: 'dcmtclve'
            resumen column: 'dcmtrsmn'
            nombre column: 'dcmtdcmt'
            path column: 'dcmtpath'
        }
    }
    static constraints = {
        etapa(blank: true, nullable: true)
        concurso(blank: true, nullable: true)
        descripcion(blank: true, nullable: true, maxSize: 63)
        palabrasClave(blank: true, nullable: true, maxSize: 63)
        resumen(blank: true, nullable: true, maxSize: 1024)
        nombre(blank: true, nullable: true, maxSize: 255)
        path(blank: true, nullable: true, maxSize: 1024)
    }
}
