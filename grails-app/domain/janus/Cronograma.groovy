package janus

class Cronograma implements Serializable {

    VolumenesObra volumenObra
    Integer periodo
    Double precio
    Double porcentaje
    Double cantidad
    static auditable = true
    static mapping = {
        table 'crno'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'crno__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'crno__id'
            volumenObra column: 'vlob__id'
            periodo column: 'crnoprdo'
            precio column: 'crnoprco'
            porcentaje column: 'crnoprct'
            cantidad column: 'crnocntd'
        }
    }
    static constraints = {
        volumenObra(blank: false, nullable: false, attributes: [title: 'volumen de obra'])
        periodo(blank: false, nullable: false, attributes: [title: 'periodo'])
        precio(blank: false, nullable: false, attributes: [title: 'precio'])
        porcentaje(blank: false, nullable: false, attributes: [title: 'porcentaje'])
        cantidad(blank: false, nullable: false, attributes: [title: 'cantidad'])
    }
}