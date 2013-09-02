package janus

class DiaLaborable {
    Date fecha
    Integer ordinal
    String observaciones

    static mapping = {
        table 'ddlb'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'ddlb__id'
        id generator: 'identity'
        version false
        columns {
            fecha column: "ddlbfcha"
            ordinal column: "ddlbordn"
            observaciones column: 'ddlbobsr'
        }
    }
    static constraints = {
        observaciones(blank: true, nullable: true, maxSize: 511)
    }
}
