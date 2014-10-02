package janus

class DepartamentoTramite {
    TipoTramite tipoTramite
    RolTramite rolTramite
    Departamento departamento
    static auditable = true
    static mapping = {
        table 'dptr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dptr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dptr__id'
            tipoTramite   column: 'tptr__id'
            rolTramite    column: 'rltr__id'
            departamento  column: 'dpto__id'
        }
    }
    static constraints = {
        tipoTramite(blank: false, attributes: [title: 'tipo de trámite'])
        rolTramite(blank: false, attributes: [title: 'rol en el trámite'])
        departamento(blank: false, attributes: [title: 'departamento'])
    }
}
