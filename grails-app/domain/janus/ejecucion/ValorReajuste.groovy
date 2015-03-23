package janus.ejecucion

import janus.FormulaPolinomica

//import janus.Indice
//import janus.pac.PeriodoValidez

class ValorReajuste {

    janus.Obra obra
    Planilla planilla
    PeriodosInec periodoIndice
//    Indice indice
    FormulaPolinomicaContractual formulaPolinomica
    FormulaPolinomica formulaPolinomicaLiq
    Planilla planillaLiq

    double valor

    /*Reajuste al periodo que le toca*/
    Double valorReajuste=0
    PeriodosInec periodoReajuste


    static mapping = {
        table 'vlrj'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'vlrj__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'vlrj__id'
            planilla column: 'plnl__id'
            periodoIndice column: 'prin__id'
//            indice column: 'indc__id'
            formulaPolinomica column: 'frpl__id'
            formulaPolinomicaLiq column: 'fpob__id'
            valor column: 'vlrjvlor'
            obra column: 'obra__id'
            planillaLiq column: 'plnllqui'
            valorReajuste column: 'vlrjvlrj'
            periodoReajuste column: 'prinrjid'
        }
    }
    static constraints = {
        planilla(blank: true, nullable: true)
        periodoIndice(blank: true, nullable: true)
//        indice(blank: true, nullable: true)
        formulaPolinomica(blank: true, nullable: true)
        formulaPolinomicaLiq(blank: true, nullable: true)
        valor(blank: true, nullable: true)
        obra(blank: false, nullable: false)
        planillaLiq(blank: true, nullable: true)
        periodoReajuste(blank: true, nullable: true)
    }
}
