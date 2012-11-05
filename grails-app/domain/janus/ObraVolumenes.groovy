package janus
class ObraVolumenes implements Serializable {
    String columnaCodigo
    String codigo
    double valor
    static mapping = {
        table 'obvl_xxxx'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'obvl__id'
        id generator: 'identity'
        version false
        columns {
            columnaCodigo column: 'clmncdgo'
            codigo column: 'numero'
            valor column: 'valor'
        }
    }
    static constraints = {
        codigo(size: 1..20, blank: false, attributes: [title: 'numero'])
        valor(blank: true, nullable: true, attributes: [title: 'valor'])
    }
}