package janus.ejecucion

import janus.Indice
import janus.pac.PeriodoValidez

class ValorReajuste {

    Planilla planilla
    PeriodoValidez periodoIndice
    Indice indice
    double valor

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
            indice column: 'indc__id'
            valor column: 'vlrjvlor'
        }
    }
    static constraints = {
        planilla(blank: true, nullable: true)
        periodoIndice(blank: true, nullable: true)
        indice(blank: true, nullable: true)
        valor(blank: true, nullable: true)
    }
}
