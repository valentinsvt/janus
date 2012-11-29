package janus
class VolumenesObra implements Serializable {
    SubPresupuesto subPresupuesto
    Item item
    Obra obra
    double cantidad
    int orden = 1
    static mapping = {
        table 'vlob'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'vlob__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'vlob__id'
            subPresupuesto column: 'sbpr__id'
            item column: 'item__id'
            obra column: 'obra__id'
            cantidad column: 'vlobcntd'
            orden column: 'vlobordn'
        }
    }
    static constraints = {
        obra(blank: false, attributes: [title: 'obra'])
        item(blank: false, attributes: [title: 'item'])
        cantidad(blank: false, attributes: [title: 'cantidad'])
        subPresupuesto(blank: false, attributes: [title: 'subPresupuesto'])


    }
}