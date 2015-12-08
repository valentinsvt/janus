package janus.ejecucion

import janus.Contrato
import janus.Indice
import janus.SubPresupuesto

class FormulaPolinomicaContractual implements Serializable {

    Contrato contrato
    TipoFormulaPolinomica tipoFormulaPolinomica
    Indice indice
    String numero
    double valor
//    int codigo = 0
    FormulaPolinomicaReajuste reajuste
//    SubPresupuesto subPresupuesto

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
//            subPresupuesto column: 'sbpr__id'
//            codigo column: 'frplcdgo'
            reajuste column: 'fprj__id'
        }
    }

    static constraints = {
        numero(blank: true, nullable: true)
        valor(blank: true, nullable: true)
        indice(blank: true, nullable: true)
        tipoFormulaPolinomica(blank: true, nullable: true)
//        codigo(blank: false, nullable: false)
    }

    String toString(){
        "${numero}:${valor}"
    }
}
