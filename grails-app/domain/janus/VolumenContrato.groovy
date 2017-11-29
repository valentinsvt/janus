package janus

class VolumenContrato implements Serializable{

    Contrato contrato
    Obra obra
    Contrato contratoComplementario
    SubPresupuesto subPresupuesto
    Item item
    double volumenCantidad
    int volumenOrden
    double volumenPrecio
    double volumenSubtotal
    String rutaCritica
    double cantidadComplementaria

    static auditable = true
    static mapping = {
        table 'vocr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'vocr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'vocr__id'
            contrato column: 'cntr__id'
            obra column: 'obra__id'
            contratoComplementario column: 'cntrcmpl'
            subPresupuesto column: 'sbpr__id'
            item column: 'item__id'
            volumenCantidad column: 'vocrcntd'
            volumenOrden column: 'vocrordn'
            volumenPrecio column: 'vocrpcun'
            volumenSubtotal column: 'vocrsbtt'
            rutaCritica column: 'vocrrtcr'
            cantidadComplementaria column: 'vocrcncp'
        }
    }

    static constraints = {
        contrato(blank: false, attributes: [title: 'contrato'])
        obra(blank: false, attributes: [title: 'obra'])
        contratoComplementario(blank: true, nullable: true, attributes: [title: 'complementario'])
        item(blank: false, attributes: [title: 'item'])
        volumenCantidad(blank: false, attributes: [title: 'cantidad'])
        subPresupuesto(blank: false, attributes: [title: 'subPresupuesto'])
        rutaCritica(blank: true, nullable: true, maxSize: 1, inList: ['S', 'N'], attributes: [title: 'ruta critica'])
    }
}
