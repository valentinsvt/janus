package janus
class Rubro implements Serializable {
    Item rubro
    Item item
    Date fecha
    double cantidad
    static mapping = {
        table 'rbro'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'rbro__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'rbro__id'
            rubro column: 'rbrocdgo'
            item column: 'item__id'
            fecha column: 'rbrofcha'
            cantidad column: 'rbrocntd'
        }
    }
    static constraints = {
        item(blank: false, nullable: false, attributes: [title: 'item'])
        cantidad(blank: true, attributes: [title: 'cantidad'])
        rubro(blank: false, nullable: false, attributes: [title: 'rubro'])
        fecha(blank: true, nullable: true, attributes: [title: 'fecha'])

    }
}