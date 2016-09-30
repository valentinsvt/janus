package janus

class VolumenesObra implements Serializable {
    SubPresupuesto subPresupuesto
    Item item
    Obra obra
    double cantidad
    int orden = 1

    double dias

    String rutaCritica = "N"
    String descripcion

    static auditable = [ignore: ["orden"]]
    static mapping = {
        table 'vlob'
//        cache usage: 'read-write', include: 'non-lazy'
        cache false
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

            dias column: 'vlobdias'

            rutaCritica column: 'vlobrtcr'
            descripcion column: 'vlobdscr'
        }
    }
    static constraints = {
        obra(blank: false, attributes: [title: 'obra'])
        item(blank: false, attributes: [title: 'item'])
        cantidad(blank: false, attributes: [title: 'cantidad'])
        subPresupuesto(blank: false, attributes: [title: 'subPresupuesto'])

        rutaCritica(blank: true, nullable: true, maxSize: 1, inList: ['S', 'N'], attributes: [title: 'ruta critica'])
        descripcion(blank: true, nullable: true, maxSize: 1023, attributes: [title: 'descripción del rubro de la obra'])
    }
}