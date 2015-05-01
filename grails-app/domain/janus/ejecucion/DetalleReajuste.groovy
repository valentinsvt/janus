package janus.ejecucion

class DetalleReajuste {

    FormulaPolinomicaContractual fpContractual
    ReajustePlanilla reajustePlanilla
    double indiceOferta
    double indicePeriodo
    double valorIndcOfrt
    double valorIndcPrdo
    double valor

    static mapping = {

        table 'dtrj'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dtrj__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dtrj__id'
            fpContractual column: 'frpl__id'
            reajustePlanilla column: 'rjpl__id'
            indiceOferta column: 'dtrjinof'
            indicePeriodo column: 'dtrjinpr'
            valorIndcOfrt column: 'dtrjvlof'
            valorIndcPrdo column: 'dtrjvlpr'
            valor column: 'dtrjvlor'
        }

    }
    static constraints = {
        fpContractual(blank:false, nullable: false)
        reajustePlanilla(blank:false, nullable: false)
        indiceOferta(blank:false, nullable: false)
        indicePeriodo(blank:false, nullable: false)
        valorIndcOfrt(blank:false, nullable: false)
        valorIndcPrdo(blank:false, nullable: false)
        valor(blank:false, nullable: false)
    }

}
