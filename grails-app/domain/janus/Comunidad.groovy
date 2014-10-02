package janus
class Comunidad implements Serializable {
    Parroquia parroquia
    String numero
    String nombre
    double latitud
    double longitud
    static auditable = true
    static mapping = {
        table 'cmnd'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'cmnd__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'cmnd__id'
            parroquia column: 'parr__id'
            numero column: 'cmndnmro'
            nombre column: 'cmndnmbr'
            latitud column: 'cmndlatt'
            longitud column: 'cmndlong'

        }
    }
    static constraints = {
        nombre(size: 1..63, blank: false, attributes: [title: 'nombre'])
        numero(size: 1..8, blank: false, attributes: [title: 'numero'])
        parroquia(blank: true, nullable: true, attributes: [title: 'parroquia'])
        latitud(blank: false, nullable: true, attributes: [title: 'latitud'])
        longitud(blank: false,nullable: true, attributes: [title: 'longitud'])
    }
}