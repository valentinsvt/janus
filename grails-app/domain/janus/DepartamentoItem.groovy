package janus
class DepartamentoItem implements Serializable {

    SubgrupoItems subgrupo
    Transporte transporte
    Integer codigo
    String descripcion


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
        descripcion(size: 1..31, blank: false, attributes: [title: 'descripcion'])
        transporte(blank: true, nullable: true)
        codigo(blank: false, unique: true)
        subgrupo(blank: false)
    }
}
