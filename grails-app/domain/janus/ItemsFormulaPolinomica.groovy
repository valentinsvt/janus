package janus

class ItemsFormulaPolinomica implements Serializable {
    FormulaPolinomica formulaPolinomica
    Item item
    Double valor
    static auditable = true
    static mapping = {
        table 'itfp'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'itfp__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'itfp__id'
            formulaPolinomica column: 'fpob__id'
            item column: 'item__id'
            valor column: 'itfpvlor'
        }
    }
    static constraints = {
        item(blank: false, attributes: [title: 'item'])
        formulaPolinomica(blank: false, attributes: [title: 'formulaPolinomica'])
        valor(blank: false, nullable: false)
    }
}