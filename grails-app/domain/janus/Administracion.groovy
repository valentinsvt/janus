package janus
class Administracion implements Serializable {
    String nombrePrefecto
    String descripcion
    Date fechaInicio
    Date fechaFin
    static auditable = true
    static mapping = {
        table 'admn'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'admn__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'admn__id'
            descripcion column: 'admndscr'
            fechaInicio column: 'admnfcin'
            fechaFin column: 'admnfcfn'
            nombrePrefecto column: 'admnprfc'
        }
    }
    static constraints = {
        nombrePrefecto(size: 1..63, blank: false, attributes: [title: 'nombrePrefecto'])
        descripcion(size: 1..63, blank: false, attributes: [title: 'descripcion'])
        fechaInicio(blank: false, attributes: [title: 'fechaInicio'])
        fechaFin(blank: true, nullable: true, attributes: [title: 'fechaFin'])

    }
    String toString() {
          nombrePrefecto
    }
}