package janus
class Cronograma implements Serializable {

    Obra obra
    String orden
    String codigo
    String nombre
    String codigoCronograma
    String cantidad
    String precioUnitario
    String sbtl
    String tipoCronograma
    String tipoPeriodo
    String periodo
    String crno__p1
    String crno__p2
    String crno_p50
    String ttal

    static mapping = {
        table 'crno'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'crno__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'crno__id'
            obra column: 'obra__id'
            orden column: 'vlobordn'
            codigo column: 'rbrocdgo'
            nombre column: 'rbronmbr'
            codigoCronograma column: 'unddcdgo'
            cantidad column: 'vlobcntd'
            precioUnitario column: 'vlobpcun'
            sbtl column: 'vlobsbtl'
            tipoCronograma column: 'crnotipo'
            tipoPeriodo column: 'prdotipo'
            periodo column: 'crnoprdo'
            crno__p1 column: 'crno__p1'
            crno__p2 column: 'crno__p2'
            crno_p50 column: 'crno_p50'
            ttal column: 'crnottal'

        }
    }
    static constraints = {
        nombre(size: 1..10, attributes: [title: 'nombre'])
        codigo(size: 1..10, blank: true, nullable: true, attributes: [title: 'numero'])
        obra(blank: true, nullable: true, attributes: [title: 'obra'])
        orden(size: 1..10, blank: true, nullable: true, attributes: [title: 'orden'])
          codigoCronograma(size: 1..10, blank: true, nullable: true, attributes: [title: 'numero'])
        cantidad(size: 1..10, blank: true, nullable: true, attributes: [title: 'cantidad'])
        precioUnitario(size: 1..10, blank: true, nullable: true, attributes: [title: 'precioUnitario'])
        sbtl(size: 1..10, blank: true, nullable: true, attributes: [title: 'sbtl'])
        tipoCronograma(size: 1..10, blank: true, nullable: true, attributes: [title: 'tipo'])
        tipoPeriodo(size: 1..10, blank: true, nullable: true, attributes: [title: 'tipo'])
        periodo(size: 1..10, blank: true, nullable: true, attributes: [title: 'periodo'])
        crno__p1(size: 1..10, blank: true, nullable: true, attributes: [title: 'crno__p1'])
        crno__p2(size: 1..10, blank: true, nullable: true, attributes: [title: 'crno__p2'])
        crno_p50(size: 1..10, blank: true, nullable: true, attributes: [title: '_p50'])
        ttal(size: 1..10, blank: true, nullable: true, attributes: [title: 'ttal'])

    }
}