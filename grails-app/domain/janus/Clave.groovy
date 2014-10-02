package janus
class Clave implements Serializable {
    String clave
    String dlgd
    static auditable = true
    static mapping = {
        table 'clve'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'clve__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'clve__id'
            clave column: 'clveclve'
            dlgd column: 'clvedlgd'
        }
    }
    static constraints = {
        clave(size: 1..10, blank: true, nullable: true, attributes: [title: 'clave'])
        dlgd(size: 1..10, blank: true, nullable: true, attributes: [title: 'dlgd'])
    }
}