package janus
class PersonaRol implements Serializable {
    Persona persona
    Funcion funcion
    static mapping = {
        table 'prrl'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prrl__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prrl__id'
            persona column: 'prsn__id'
            funcion column: 'func__id'
        }
    }
    static constraints = {
        persona(blank: false, nullable: false, attributes: [title: 'responsableObra'])
        funcion(blank: false, nullable: false, attributes: [title: 'funcion'])
    }
}