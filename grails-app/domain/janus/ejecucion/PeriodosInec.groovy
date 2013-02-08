package janus.ejecucion
class PeriodosInec implements Serializable {
    String descripcion
    Date fechaInicio
    Date fechaFin
    String periodoCerrado
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
            periodoCerrado column: 'princrre'
        }
    }
    static constraints = {
        descripcion(size: 1..31, blank: false, attributes: [title: 'descripcion'])
        fechaInicio(blank: false, attributes: [title: 'fechaInicio'])
        fechaFin(blank: true, nullable: true, attributes: [title: 'fechaFin'])
        periodoCerrado(size: 1..1, blank: false, attributes: [title: 'periodoCerrado'])
    }
}