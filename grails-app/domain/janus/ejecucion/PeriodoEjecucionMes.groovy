package janus.ejecucion

import janus.Contrato
import janus.Obra
import janus.pac.PeriodoEjecucion

class PeriodoEjecucionMes {
    Contrato contrato
    Obra obra
    PeriodoEjecucion periodoEjecucion
    Date fechaInicio
    Date fechaFin
    double parcialCronograma=0

    static mapping = {

        table 'pems'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'pems__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'pems__id'
            contrato column: 'cntr__id'
            obra column: 'obra__id'
            periodoEjecucion column: 'prej__id'
            fechaInicio column: 'pemsfcin'
            fechaFin column: 'pemsfcfn'
            parcialCronograma column: 'pemscrpa'
            parcialPlanilla column: 'prplprpl'
        }

    }
    static constraints = {
        contrato(blank:false, nullable: false)
        obra(blank:false, nullable: false)
        periodoEjecucion(blank:false, nullable: false)
        fechaInicio(blank:false, nullable: false)
        fechaFin(blank:false, nullable: false)
        parcialCronograma(blank:false, nullable: false)
    }

}
