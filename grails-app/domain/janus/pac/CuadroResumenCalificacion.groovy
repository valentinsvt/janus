package janus.pac

class CuadroResumenCalificacion {

    Oferta oferta
    String descripcion
    Double valor
    static auditable = true
    static mapping = {
        table 'calf'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'calf__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'calf__id'
            oferta column: 'ofrt__id'
            descripcion column: 'calfdscr'
            valor column: 'calfvlor'
        }
    }
    static constraints = {
        oferta(blank: true, nullable: true)
        descripcion(blank: true, nullable: true, maxSize: 255)
        valor(blank: true, nullable: true)
    }
}
