package janus.ejecucion

import janus.Contrato
import janus.Indice

class FormulaPolinomicaContractual implements Serializable {

    Contrato contrato
    TipoFormulaPolinomica tipoFormulaPolinomica
    Indice indice
    String numero
    double valor


    static mapping = {

        table 'frpl'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'frpl__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'frpl__id'
            contrato column: 'cntr__id'
            tipoFormulaPolinomica column: 'tpfp__id'
            indice column: 'indc__id'
            numero column: 'frplnmro'
            valor column: 'frplvlor'
        }
    }

    static constraints = {
        numero(blank: true, nullable: true)
        valor(blank: true, nullable: true)
        indice(blank: true, nullable: true)
    }
}
