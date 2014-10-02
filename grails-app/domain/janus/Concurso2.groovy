package janus
class Concurso2 implements Serializable {

    Obra obra
    TipoCuenta tipoCuenta
    Administracion administracion
    String codigo
    String objetivo
    double base
    Date fechaInicio
    Date fechaCierre
    String estado
    String observaciones

    static auditable = true


    static mapping = {
        table 'cncr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'cncr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'cncr__id'
            obra column: 'obra__id'
            tipoCuenta column: 'tpcn__id'
            administracion column: 'admn__id'
            codigo column: 'cncrcdgo'
            objetivo column: 'cncrobjt'
            base column: 'cncrbase'
            fechaInicio column: 'cncrfcin'
            fechaCierre column: 'cncrfccr'
            estado column: 'cncretdo'
            observaciones column: 'cncrobsr'
        }
    }
    static constraints = {
        obra(blank: true, nullable: true, attributes: [title: 'obra'])
        administracion(blank: true, nullable: true, attributes: [title: 'administracion'])
        codigo(size: 1..15, blank: false, attributes: [title: 'numero'])
        objetivo(size: 1..255, blank: true, nullable: true, attributes: [title: 'objetivo'])
        base(blank: true, nullable: true, attributes: [title: 'base'])
        tipoCuenta(blank: true, nullable: true, attributes: [title: 'tipoCuenta'])
        estado(size: 1..1, blank: true, nullable: true, attributes: [title: 'estado'])
        fechaInicio(blank: true, nullable: true, attributes: [title: 'fechaInicio'])
        fechaCierre(blank: true, nullable: true, attributes: [title: 'fechaCierre'])
        observaciones(size: 1..127, blank: true, nullable: true, attributes: [title: 'observaciones'])
    }
}