package janus.pac

class Evaluacion {

    Oferta oferta
    ParametroEvaluacion parametroEvaluacion
    String descripcion
    Double puntaje
    Double valor
    static auditable = true
    static mapping = {
        table 'eval'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'eval__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'eval__id'
            oferta column: 'ofrt__id'
            parametroEvaluacion column: 'prev__id'
            descripcion column: 'evaldscr'
            puntaje column: 'evalpnto'
            valor column: 'evalvlor'
        }
    }
    static constraints = {
        descripcion(size: 1..1023, blank: false, nullable: false, attributes: [title: 'descripcion'])
    }
}
