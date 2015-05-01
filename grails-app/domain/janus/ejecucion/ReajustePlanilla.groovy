package janus.ejecucion

class ReajustePlanilla {

    Planilla planilla
    Planilla planillaReajustada
    PeriodosInec periodoInec
    double parcialCronograma
    double acumuladoCronograma
    double parcialPlanillas
    double acumuladoPlanillas
    double valorPo
    String mes
    int periodo
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
            parcialCronograma column: 'rjplcrpa'
            acumuladoCronograma column: 'rjplcrac'
            parcialPlanillas column: 'rjplplpa'
            acumuladoPlanillas column: 'rjplplac'
            valorPo column: 'rjplvlpo'
            periodo column: 'rjplprdo'
            mes column: 'rjpl_mes'
            valorReajustado column: 'rjplvlor'
            fechaReajuste column: 'rjplfcha'
            factor column: 'rjplfctr'

        }

    }
    static constraints = {
        planilla(blank:false, nullable: false)
        planillaReajustada(blank:false, nullable: false)
        periodoInec(blank:false, nullable: false)
        valorReajustado(blank:false, nullable: false)
        fechaReajuste(blank:true, nullable: true)
        factor(blank:false, nullable: false)
        parcialCronograma(blank:false, nullable: false)
        acumuladoCronograma(blank:false, nullable: false)
        parcialPlanillas(blank:false, nullable: false)
        acumuladoPlanillas(blank:false, nullable: false)
        acumuladoPlanillas(blank:false, nullable: false)
        periodo(blank:false, nullable: false)
        mes(blank:false, nullable: false)

    }

}
