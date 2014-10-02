package janus

class Parroquia implements Serializable {
    Canton canton
    String codigo
    String nombre
    String urbana
    double longitud
    double latitud
    static auditable = true
    static mapping = {
        table 'parr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'parr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'parr__id'
            canton column: 'cntn__id'
            codigo column: 'parrcdgo'
            nombre column: 'parrnmbr'
            urbana column: 'parrurbn'
            longitud column: 'parrlong'
            latitud column: 'parrlatt'
        }
    }
    static constraints = {
        nombre(size: 1..63, blank: false, attributes: [title: 'nombre'])
        codigo(size: 1..6, unique: true, blank: false, attributes: [title: 'numero'])
        canton(blank: true, nullable: true, attributes: [title: 'canton'])
        urbana(size: 1..1, blank: true, nullable: true, attributes: [title: 'urbana'])
        latitud(blank: true, nullable: true, attributes: [title: 'latitud'])
        longitud(blank: true, nullable: true, attributes: [title: 'longitud'])

    }

    String toString() {
        return "${this.nombre}"
    }
}