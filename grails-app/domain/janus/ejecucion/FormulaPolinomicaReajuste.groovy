package janus.ejecucion

import janus.Contrato

class FormulaPolinomicaReajuste implements Serializable {

    Contrato contrato
    TipoFormulaPolinomica tipoFormulaPolinomica
    String descripcion

    static mapping = {

        table 'fprj'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'fprj__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'fprj__id'
            contrato column: 'cntr__id'
            tipoFormulaPolinomica column: 'tpfp__id'
            descripcion column: 'frpldscr'
        }
    }

    static constraints = {
//        descripcion(blank: true, nullable: true)
    }
}
