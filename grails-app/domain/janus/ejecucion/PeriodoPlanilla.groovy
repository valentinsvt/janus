package janus.ejecucion

class PeriodoPlanilla {

    Planilla planilla
    Date fechaIncio
    Date fechaFin
    PeriodosInec periodo
    PeriodosInec periodoLiquidacion
    String titulo
    double total  =0 /*Mano de obra (b0 por periodo)*/
    double totalLiq=0
    double p0=0
    double p0Liq=0
    double fr=0
    double frLiq=0
    double parcialCronograma=0
    double parcialCronogramaLiq=0
    double parcialPlanilla=0
    double parcialPlanillaLiq=0
    int dias

    /*Reajuste al periodo que le toca*/
    Double p0Reajuste=0
    Double frReajuste=0
    Double b0Reajuste = 0
    PeriodosInec periodoReajuste

    static mapping = {

        table 'prpl'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prpl__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prpl__id'
            fechaIncio column: 'prplfcin'
            fechaFin column: 'prplfcfn'
            periodo column: 'prin__id'
            titulo column: 'prplttlo'
            total column: "prpltotl"
            p0 column: "prpl__p0"
            fr column: "prpl__fr"
            planilla column: 'plnl__id'
            parcialCronograma column: 'prplprcr'
            parcialPlanilla column: 'prplprpl'
            dias column: 'prpldias'
            periodoLiquidacion column: 'prinlqdc'
            totalLiq column: "prplttlq"
            p0Liq column: "prplp0lq"
            frLiq column: "prplfrlq"
            parcialCronogramaLiq column: 'prplpclq'
            parcialPlanillaLiq column: 'prplpplq'
            p0Reajuste column: 'prplp0rj'
            frReajuste column: 'prplfrrj'
            periodoReajuste column: 'prinrjid'
            b0Reajuste column: 'prplb0rj'
        }

    }
    static constraints = {
        periodoLiquidacion(blank:true,nullable: true)
        periodoReajuste(blank:true,nullable: true)
        b0Reajuste(blank:true,nullable: true)
    }

}
