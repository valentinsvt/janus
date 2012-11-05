package janus
class ObraRubro implements Serializable {
    String codigo
    String rubro
    String unidad
    double cantidad
    int orden
    static mapping = {
        table 'obrb_xxxx'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'obrb__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'obrb__id'
            codigo column: 'numero'
            rubro column: 'rubro'
            unidad column: 'unidad'
            cantidad column: 'cantidad'
            orden column: 'orden'
        }
    }
    static constraints = {
        rubro(size: 1..60, blank: true, nullable: true, attributes: [title: 'numero'])
        unidad(size: 1..5, blank: true, nullable: true, attributes: [title: 'unidad'])
        cantidad(blank: true, nullable: true, attributes: [title: 'cantidad'])
        orden(blank: true, nullable: true, attributes: [title: 'orden'])
    }
}