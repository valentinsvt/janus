package janus
class Base implements Serializable {
    PrecioVenta precioVenta
    Concurso2 concurso
    Date fecha
    double monto
    static auditable = true
    static mapping = {
        table 'base'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'base__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'base__id'
            precioVenta column: 'prve__id'
            concurso column: 'cncr__id'
            fecha column: 'basefcha'
            monto column: 'basemnto'
        }
    }
    static constraints = {
        concurso(blank: true, nullable: true, attributes: [title: 'concurso'])
        monto(blank: true, nullable: true, attributes: [title: 'monto'])
        precioVenta(blank: true, nullable: true, attributes: [title: 'precioVenta'])
        fecha(blank: true, nullable: true, attributes: [title: 'fecha'])
    }
}