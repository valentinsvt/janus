package janus

class DepartamentoItem implements Serializable {

    SubgrupoItems subgrupo
    Transporte transporte
    String codigo
    String descripcion
    static auditable = true
    static mapping = {
        table 'dprt'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dprt__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dprt__id'
            codigo column: 'dprtcdgo'
            descripcion column: 'dprtdscr'
            transporte column: 'trnp__id'
            subgrupo column: 'sbgr__id'
        }
    }
    static constraints = {
        descripcion(size: 1..63, blank: false, attributes: [title: 'descripcion'])
        transporte(blank: true, nullable: true)
        codigo(size: 1..20, blank: false, attributes: [title: 'numero'])
        subgrupo(blank: false)
    }
}
