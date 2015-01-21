package janus

class VaeItems implements Serializable {
    Item item
    Date fecha
    double porcentaje
    Date fechaIngreso = new Date()
    String registrado = "N"
    static auditable = true
    static mapping = {
        table 'itva'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'itva__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'itva__id'
            item column: 'item__id'
            fecha column: 'itvafcha'
            porcentaje column: 'itvapcnt'
            fechaIngreso column: 'itvafcin'
            registrado column: 'itvargst'
        }
    }
    static constraints = {
        item(blank: false, nullable: false, attributes: [title: 'item'])
        porcentaje(blank: false, attributes: [title: 'porcentaje del VAE'])
        fecha(blank: false, attributes: [title: 'fecha'])
        fechaIngreso(blank: false, attributes: [title: 'fecha'])
        registrado(blank: false, attributes: [title: 'Registrado'])
    }
}