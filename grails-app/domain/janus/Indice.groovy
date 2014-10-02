package janus
class Indice implements Serializable {
    TipoIndice tipoIndice
    String codigo
    String descripcion
    static auditable = true
    static mapping = {
        table 'indc'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'indc__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'indc__id'
            tipoIndice column: 'tpin__id'
            codigo column: 'indccdgo'
            descripcion column: 'indcdscr'
        }
    }
    static constraints = {
        tipoIndice(blank: true, nullable: true, attributes: [title: 'tipo de Indice'])
        codigo(size: 1..20, blank: true, nullable: true, attributes: [title: 'código'])
        descripcion(size: 1..131, blank: false, attributes: [title: 'descripción'])
    }
    String toString(){
        descripcion
    }
}