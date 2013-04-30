package janus

class TipoTramite implements Serializable {
    TipoTramite padre
    String codigo
    int tiempo
    String descripcion
    String tipo

    static mapping = {
        table 'tptr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tptr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tptr__id'
            codigo column: 'tptrcdgo'
            descripcion column: 'tptrdscr'
            padre column: 'tptrpdre'
            tiempo column: 'tptrtmpo'
            tipo column: 'tptrtipo'
        }
    }
    static constraints = {
        codigo(size: 4..4, blank: false, attributes: [title: 'código'])
        descripcion(size: 1..63, blank: false, attributes: [title: 'descripción'])
        padre(blank: true, nullable: true, attributes: [title: 'trámite principal'])
        tiempo(blank: false, attributes: [title: 'tiempo de ejecución del trámite en días'])
        tipo(blank: false, nullable: false, inList: ["O", "C"], attributes: [title: 'tipo de trámite: O: Obra, C:Contrato'])
    }

    String toString() {
        "${codigo}: ${descripcion}"
    }
}