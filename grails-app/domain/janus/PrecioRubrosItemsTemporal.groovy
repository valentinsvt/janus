package janus
class PrecioRubrosItemsTemporal implements Serializable {
    Item item
    Lugar lugar
    Date fecha
    double precioUnitario
    static mapping = {
        table 'rbpc1'
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
        item(blank: false, attributes: [title: 'item__id'])
        precioUnitario(blank: false, attributes: [title: 'precioUnitario'])
        lugar(blank: false, attributes: [title: 'lgar__id'])
        fecha(blank: false, attributes: [title: 'fecha'])

    }
}