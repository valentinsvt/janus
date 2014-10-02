package janus.pac

class Asignacion {

    janus.Presupuesto prespuesto
    Anio anio
    Double valor = 0
    static auditable = true
    static mapping = {
        table 'asgn'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'asgn__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'asgn__id'
            prespuesto column: 'prsp__id'
            anio column: 'anio__id'
            valor column: 'asgnvlor'
        }
    }


    static constraints = {
        prespuesto(nullable: false,blank:false)
        anio(nullable: false,blank:false)
    }
}
