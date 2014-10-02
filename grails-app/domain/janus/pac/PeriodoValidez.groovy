package janus.pac

class PeriodoValidez {

    String descripcion
    Date fechaInicio
    Date fechaFin
    String cierre
    static auditable = true
    static mapping = {
        table 'prin'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prin__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prin__id'
            descripcion column: 'prindscr'
            fechaInicio column: 'prinfcin'
            fechaFin column: 'prinfcfn'
            cierre column: 'princrre'
        }
    }
    static constraints = {

        descripcion(nullable: true,blank: true,size:1..32)
        cierre(nullable: true,blank: true,size:1..1)
    }
}
