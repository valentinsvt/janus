package janus
class Ofertas implements Serializable {
    String base__id
    PeriodosInec preguntasIniciales
    String descripcion
    double monto
    Date fechaEntrega
    int plazo
    String calificacion
    int hojas
    String subsecretario
    String indiceCostosIndirectosGarantias
    String estado
    String observaciones
    static mapping = {
        table 'ofrt'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'ofrt__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'ofrt__id'
            base__id column: 'base__id'
            preguntasIniciales column: 'prin__id'
            descripcion column: 'ofrtdscr'
            monto column: 'ofrtmnto'
            fechaEntrega column: 'ofrtfcen'
            plazo column: 'ofrtplzo'
            calificacion column: 'ofrtcalf'
            hojas column: 'ofrthoja'
            subsecretario column: 'ofrtsbsc'
            indiceCostosIndirectosGarantias column: 'ofrtgrnt'
            estado column: 'ofrtetdo'
            observaciones column: 'ofrtobsr'
        }
    }
    static constraints = {
        base__id(size: 1..10, blank: true, nullable: true, attributes: [title: 'base__id'])
        descripcion(size: 1..255, attributes: [title: 'descripcion'])
        monto(blank: true, nullable: true, attributes: [title: 'monto'])
        preguntasIniciales(blank: true, nullable: true, attributes: [title: 'preguntasIniciales'])
        plazo(blank: true, nullable: true, attributes: [title: 'plazo'])
        fechaEntrega(blank: true, nullable: true, attributes: [title: 'fechaEntrega'])
        calificacion(size: 1..1, blank: true, nullable: true, attributes: [title: 'calificacion'])
        hojas(blank: true, nullable: true, attributes: [title: 'hojas'])
        subsecretario(size: 1..40, blank: true, nullable: true, attributes: [title: 'subsecretario'])
        indiceCostosIndirectosGarantias(size: 1..1, blank: true, nullable: true, attributes: [title: 'indiceCostosIndirectosGarantias'])
        estado(size: 1..1, blank: true, nullable: true, attributes: [title: 'estado'])
        observaciones(size: 1..127, blank: true, nullable: true, attributes: [title: 'observaciones'])
    }
}