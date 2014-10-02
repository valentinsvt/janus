package janus.pac

import janus.Persona

class Oferta {

    Concurso concurso
    Proveedor proveedor
    String descripcion
    Double monto
    Date fechaEntrega
    Integer plazo
    String calificado
    Integer hoja
    String subsecretario
    String garantia
    String estado
    String observaciones

    Persona responsableProceso
    static auditable = true
    static mapping = {
        table 'ofrt'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'ofrt__id'
        id generator: 'identity'
        version false
        columns {
            concurso column: 'cncr__id'
            proveedor column: 'prve__id'
            descripcion column: 'ofrtdscr'
            monto column: 'ofrtmnto'
            fechaEntrega column: 'ofrtfcen'
            plazo column: 'ofrtplzo'
            calificado column: 'ofrtcalf'
            hoja column: 'ofrthoja'
            subsecretario column: 'ofrtsbsc'
            garantia column: 'ofrtgrnt'
            estado column: 'ofrtetdo'
            observaciones column: 'ofrtobsr'

            responsableProceso column: 'prsn__id'
        }
    }
    static constraints = {
        concurso(blank: false, nullable: false)
        proveedor(blank: false, nullable: false)
        descripcion(blank: false, nullable: false, maxSize: 255)
        monto(blank: false, nullable: false)
        fechaEntrega(blank: false, nullable: false)
        plazo(blank: true, nullable: true)
        calificado(blank: true, nullable: true, maxSize: 1)
        hoja(blank: true, nullable: true)
        subsecretario(blank: true, nullable: true, maxSize: 40)
        garantia(blank: true, nullable: true, maxSize: 1)
        estado(blank: true, nullable: true, maxSize: 1)
        observaciones(blank: true, nullable: true, maxSize: 127)

        responsableProceso(blank: true, nullable: true)
    }
}
