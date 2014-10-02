package janus
class Canton implements Serializable {
    Provincia provincia
    String numero
    String nombre
    double longitud
    double latitud
    static auditable = true
    static mapping = {
        table 'cntn'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'cntn__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'cntn__id'
            provincia column: 'prov__id'
            numero column: 'cntnnmro'
            nombre column: 'cntnnmbr'
            longitud column: 'cntnlong'
            latitud column: 'cntnlatt'
        }
    }
    static constraints = {
        nombre(size: 1..63, blank: false, attributes: [title: 'nombre'])
        numero(size: 1..6, blank: false, attributes: [title: 'numero'])
        provincia(blank: true, nullable: true, attributes: [title: 'provincia'])
        latitud(blank: true, nullable: true, attributes: [title: 'latitud'])
        longitud(blank: true, nullable: true, attributes: [title: 'longitud'])


    }

    String toString(){
        return "${this.nombre}"
    }
}