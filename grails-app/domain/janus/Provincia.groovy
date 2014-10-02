package janus
class Provincia implements Serializable {
    String numero
    String nombre
    double longitud
    double latitud
    static auditable = true
    static mapping = {
        table 'prov'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prov__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prov__id'

            numero column: 'provnmro'
            nombre column: 'provnmbr'
            longitud column: 'provlong'
            latitud column: 'provlatt'
        }
    }
    static constraints = {
        nombre(size: 1..63, blank: false, nullable: false, attributes: [title: 'nombre'])
        numero(maxSize: 2, blank: false, nullable: false, attributes: [title: 'numero'])
                latitud(blank: true, nullable: true, attributes: [title: 'latitud'])
        longitud(blank: true, nullable: true, attributes: [title: 'longitud'])

    }
}