package janus
class PrecioRubrosItems implements Serializable {
    Item item
    Lugar lugar
    Date fecha
    double precioUnitario
    static mapping = {
        table 'rbpc'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'rbpc__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'rbpc__id'
            item column: 'item__id'
            lugar column: 'lgar__id'
            fecha column: 'rbpcfcha'
            precioUnitario column: 'rbpcpcun'
        }
    }
    static constraints = {
        item(blank: false, nullable: false, attributes: [title: 'item'])
        precioUnitario(blank: false, attributes: [title: 'precioUnitario'])
        lugar(blank: true, nullable: true, attributes: [title: 'lugar'])
        fecha(blank: false, attributes: [title: 'fecha'])

    }
}