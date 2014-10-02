package janus

class PersonasTramite implements Serializable{
    Tramite tramite
    RolTramite rolTramite
    Persona persona
    Departamento departamento
    static auditable = true
    static mapping = {
        table 'prtr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prtr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prtr__id'
            tramite column: 'trmt__id'
            rolTramite column: 'rltr__id'
            persona column: 'prsn__id'
            departamento column: 'dpto__id'
        }
    }


    static constraints = {
        persona(blank: false, nullable: false, attributes: [title: 'persona'])
        tramite(blank: false, nullable: false, attributes: [title: 'tramite'])
        rolTramite(blank: true, nullable: true, attributes: [title: 'rolTramite'])
        departamento(blank: true, nullable: true, attributes: [title: 'departamento'])
  }
}
