package janus.ejecucion

class TipoMulta {

    String descripcion
    double porcentaje

    static mapping = {
        table 'tpml'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpml__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpml__id'
            descripcion column: 'tpmldscr'
            porcentaje column: 'tpmlpcnt'
        }
    }

    static constraints = {
        descripcion(maxSize: 63, blank: false, nullable: false)
        porcentaje(blank: false, nullable: false)
    }
}
