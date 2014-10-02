package janus
class Transporte implements Serializable {
    String codigo
    String descripcion
    static auditable = true
    static mapping = {
        table 'trnp'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'trnp__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'trnp__id'
            codigo column: 'trnpcdgo'
            descripcion column: 'trnpdscr'
        }
    }
    static constraints = {
        codigo(size: 1..1, blank: false, attributes: [title: 'numero'])
        descripcion(size: 1..31, blank: false, attributes: [title: 'descripcion'])
    }
}