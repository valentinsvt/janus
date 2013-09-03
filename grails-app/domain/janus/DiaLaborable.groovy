package janus

class DiaLaborable {
    Date fecha
    String dia              //lun:1, mar:2, mie:3, jue:4, vie:5, sab:6, dom:0
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
            dia column: "ddlbddia"
            ordinal column: "ddlbordn"
            observaciones column: 'ddlbobsr'
        }
    }
    static constraints = {
        dia(blank: false, nullable: false, maxSize: 3)
        observaciones(blank: true, nullable: true, maxSize: 511)
    }
}
