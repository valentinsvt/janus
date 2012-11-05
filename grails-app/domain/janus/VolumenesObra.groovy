package janus
class VolumenesObra implements Serializable {
    SubPrograma subPrograma
    Item item
    Obra obra
    double cantidad
    static mapping = {
        table 'vlob'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'vlob__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'vlob__id'
            subPrograma column: 'sbpr__id'
            item column: 'item__id'
            obra column: 'obra__id'
            cantidad column: 'vlobcntd'
        }
    }
    static constraints = {
        obra(blank: false, attributes: [title: 'revisor'])
        item(blank: false, attributes: [title: 'item'])
        cantidad(blank: false, attributes: [title: 'cantidad'])
        subPrograma(blank: false, attributes: [title: 'subPrograma'])


    }
}