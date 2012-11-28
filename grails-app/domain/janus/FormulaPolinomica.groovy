package janus
class FormulaPolinomica implements Serializable {
    Obra obra
    Indice indice
    String numero
    double valor
    static mapping = {
        table 'fpob'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'fpob__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'fpob__id'
            obra column: 'obra__id'
            indice column: 'indc__id'
            numero column: 'frplnmro'
            valor column: 'frplvlor'
        }
    }
    static constraints = {
        obra(blank: true, nullable: true, attributes: [title: 'obra'])
        numero(size: 1..3, blank: false, attributes: [title: 'numero'])
        valor(blank: true, nullable: true, attributes: [title: 'valor'])
        indice(blank: true, nullable: true, attributes: [title: 'indice'])

    }
}