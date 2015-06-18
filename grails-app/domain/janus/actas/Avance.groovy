package janus.actas

import janus.Contrato
import janus.ejecucion.Planilla

class Avance {

    Contrato contrato
//    Date fecha
    Planilla planilla

    String frase01
    String frase02
    String frase03
    String frase04
    String frase05
    String frase06
    String frase07
    String frase08
    String frase09
    String frase10
    String frase11
    String frase12
    String frase13
    String frase14

    static hasMany = [frases: FraseClima]
    static auditable = true
    static mapping = {
        table 'avnc'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'avnc__id'
        id generator: 'identity'
        version false
        columns {
            contrato column: 'cntr__id'
            planilla column: 'plnl__id'
//            fecha column: 'avncfcha'
            frase01 column: 'avncfr01'
            frase02 column: 'avncfr02'
            frase03 column: 'avncfr03'
            frase04 column: 'avncfr04'
            frase05 column: 'avncfr05'
            frase06 column: 'avncfr06'
            frase07 column: 'avncfr07'
            frase08 column: 'avncfr08'
            frase09 column: 'avncfr09'
            frase10 column: 'avncfr10'
            frase11 column: 'avncfr11'
            frase12 column: 'avncfr12'
            frase13 column: 'avncfr13'
            frase14 column: 'avncfr14'
        }
    }

    static constraints = {
        frase01(blank: true, nullable: true)
        frase02(blank: true, nullable: true)
        frase03(blank: true, nullable: true)
        frase04(blank: true, nullable: true)
        frase05(blank: true, nullable: true)
        frase06(blank: true, nullable: true)
        frase07(blank: true, nullable: true)
        frase08(blank: true, nullable: true)
        frase09(blank: true, nullable: true)
        frase10(blank: true, nullable: true)
        frase11(blank: true, nullable: true)
        frase12(blank: true, nullable: true)
        frase13(blank: true, nullable: true)
        frase14(blank: true, nullable: true)
    }
}
