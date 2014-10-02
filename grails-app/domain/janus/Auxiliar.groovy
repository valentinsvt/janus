package janus
class Auxiliar implements Serializable {
//    String sbdr
//    String prcr
//    String drtr
    String subPrograma
    String general
    String baseCont
    String presupuestoRef
    String retencion
    String notaAuxiliar
    String nota
    String nota1
    String nota2
    String memo1
    String notaFormula
    String titulo
    String memo2
    String notaMemoAd
    String notaPieAd
    static auditable = true
    static mapping = {
        table 'auxl'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'auxl__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'auxl__id'
//
//              drtr column: 'fscldrtr'
//              sbdr column: 'fsclsbdr'
//            prcr column: 'sindprcr'
            subPrograma column: 'sindsbpr'
            general column: 'prspcbcr'
            baseCont column: 'prspbsct'
            presupuestoRef column: 'prsppsrf'
            retencion column: 'prsprete'
            notaAuxiliar column: 'prspnota'
            nota1 column: 'prspnta1'
            nota2 column: 'prspnta2'
            memo1 column: 'prspmem1'
            nota column: 'frplnota'
            titulo column: 'prspttlo'
            memo2 column: 'prspmem2'
            notaMemoAd column: 'notammad'
            notaPieAd column: 'notantad'
        }
    }
    static constraints = {
//        sbdr(size: 1..40, blank: true, nullable: true, attributes: [title: 'sbdr'])
//        prcr(size: 1..40, blank: true, nullable: true, attributes: [title: 'prcr'])
        subPrograma(size: 1..40, blank: true, nullable: true, attributes: [title: 'subPrograma'])
        general(size: 1..200, blank: true, nullable: true, attributes: [title: 'cbcr'])
        baseCont(size: 1..200, blank: true, nullable: true, attributes: [title: 'bsct'])
        presupuestoRef(size: 1..200, blank: true, nullable: true, attributes: [title: 'psrf'])
        retencion(size: 1..200, blank: true, nullable: true, attributes: [title: 'retencion'])
        notaAuxiliar(size: 1..3071, blank: true, nullable: true, attributes: [title: 'nota'])
        nota(size: 1..200, blank: true, nullable: true, attributes: [title: 'nota'])
        nota1(size: 1..200, blank: true, nullable: true, attributes: [title: 'nota1'])
        nota2(size: 1..200, blank: true, nullable: true, attributes: [title: 'nota2'])
        memo1(size: 1..200, blank: true, nullable: true, attributes: [title: 'memo1'])
        notaFormula(size: 1..200, blank: true, nullable: true, attributes: [title: 'notaFormula'])
        titulo(size: 1..100, blank: true, nullable: true, attributes: [title: 'titulo'])
        memo2(size: 1..200, blank: true, nullable: true, attributes: [title: 'memo2'])
        notaMemoAd(size: 1..255, blank: true, nullable: true, attributes: [title: 'notammad'])
        notaPieAd(size: 1..255, blank: true, nullable: true, attributtes: [title: 'notantad'])
    }
}