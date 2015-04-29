package janus.ejecucion

class ReajustePlanilla {

    Planilla planilla
    Planilla planillaReajustada
    PeriodosInec periodoInec
    double valorReajustado
    Date fechaReajuste
    double factor

    static mapping = {

        table 'rjpl'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'rjpl__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'rjpl__id'
            planilla column: 'plnl__id'
            planillaReajustada column: 'plnlrjst'
            periodoInec column: 'prin__id'
            valorReajustado column: 'rjplvlor'
            fechaReajuste column: 'rjplfcha'
            factor column: 'rjplfctr'

        }

    }
    static constraints = {
        planilla(blank:false, nullable: false)
        planillaReajustada(blank:false, nullable: false)
        periodoInec(blank:false, nullable: false)
        valorReajustado(blank:true, nullable: true)
        fechaReajuste(blank:true, nullable: true)
        factor(blank:true, nullable: true)
    }

}
