package janus.ejecucion

import janus.Obra
import janus.pac.PeriodoEjecucion

class PlanillaPo {

    Planilla planilla
    PeriodoEjecucionMes periodoEjecucionMes
    double parcialCronograma
    double acumuladoCronograma
    double parcialPlanillas
    double acumuladoPlanillas
    double valorPo
    String mes
    int periodo

    static mapping = {

        table 'plpo'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'plpo__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'plpo__id'
            planilla column: 'plnl__id'
            periodoEjecucionMes column: 'pems__id'
            parcialCronograma column: 'plpocrpa'
            acumuladoCronograma column: 'plpocrac'
            parcialPlanillas column: 'plpoplpa'
            acumuladoPlanillas column: 'plpoplac'
            valorPo column: 'plpovlpo'
            periodo column: 'plpoprdo'
            mes column: 'plpo_mes'
        }

    }
    static constraints = {
        planilla(blank:false, nullable: false)
        periodoEjecucionMes(blank:true, nullable: true)
        parcialCronograma(blank:true, nullable: true)
        acumuladoCronograma(blank:true, nullable: true)
        parcialPlanillas(blank:true, nullable: true)
        acumuladoPlanillas(blank:true, nullable: true)
        acumuladoPlanillas(blank:true, nullable: true)
        periodo(blank:false, nullable: false)
        mes(blank:false, nullable: false)
    }

}
