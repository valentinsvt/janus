package janus.ejecucion

import janus.Indice
import janus.pac.PeriodoValidez


class ValorIndice {

    Indice indice
    double  valor
    PeriodosInec periodo

    static mapping = {

        table 'vlin'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'vlin__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'vlin__id'
            indice column: 'indc__id'
            valor column: 'vlinvalr'
            periodo column: 'prin__id'


        }

    }

    static constraints = {

     valor(blank: true, nullable: true)


    }
}
