package janus.pac

class ParametroEvaluacion {

    Concurso concurso
    ParametroEvaluacion padre
    Integer orden
    String descripcion
    Double puntaje
    Double minimo = 0
    static auditable = true
    static mapping = {
        table 'prev'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prev__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prev__id'
            concurso column: 'cncr__id'
            padre column: 'prevpdre'
            orden column: 'prevordn'
            descripcion column: 'prevdscr'
            puntaje column: 'prevpnto'
            minimo column: 'prevmnmo'
        }
    }
    static constraints = {
        padre(blank: true, nullable: true)
        descripcion(size: 1..1023, blank: false, nullable: false, attributes: [title: 'descripcion'])
    }
}
