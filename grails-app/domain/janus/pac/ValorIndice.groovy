package janus.pac

import janus.Indice


class ValorIndice {

    Indice indice
    double  valor
    PeriodoValidez fecha

    static mapping = {

        table 'vain'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'vain__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'vain__id'
            indice column: 'indc__id'
            valor column: 'vainvalr'
            fecha column: 'prin__id'


        }

    }



    static constraints = {




    }
}
