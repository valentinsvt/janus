package janus.actas

class FraseClima {

    Date fecha
    String manana
    String tarde
    static belongsTo = [avance: Avance]
    static auditable = true
    static mapping = {
        table 'avfr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'avfr__id'
        id generator: 'identity'
        version false
        columns {
            avance column: 'avnc__id'
            fecha column: 'avfrfcha'
            manana column: 'avfrmnna'
            tarde column: 'avfrtrde'
        }
    }

    static constraints = {
        fecha(blank: true, nullable: true)
        manana(blank: true, nullable: true)
        tarde(blank: true, nullable: true)
    }
}
