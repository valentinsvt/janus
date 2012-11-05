package janus
class Auxiliar implements Serializable {
//    String sbdr
//    String prcr
    String subPrograma
//    String cbcr
//    String bsct
//    String psrf
//    String rete
    String nota
    String nota1
    String nota2
    String memo1
    String notaFormula
    String titulo
    String memo2
    static mapping = {
        table 'auxl'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'fscldrtr'
        id generator: 'identity'
        version false
        columns {
            id column: 'fscldrtr'
//            sbdr column: 'fsclsbdr'
//            prcr column: 'sindprcr'
            subPrograma column: 'sindsbpr'
//            cbcr column: 'prspcbcr'
//            bsct column: 'prspbsct'
//            psrf column: 'prsppsrf'
//            rete column: 'prsprete'
            nota column: 'prspnota'
            nota1 column: 'prspnta1'
            nota2 column: 'prspnta2'
            memo1 column: 'prspmem1'
            nota column: 'frplnota'
            titulo column: 'prspttlo'
            memo2 column: 'prspmem2'
        }
    }
    static constraints = {
//        sbdr(size: 1..40, blank: true, nullable: true, attributes: [title: 'sbdr'])
//        prcr(size: 1..40, blank: true, nullable: true, attributes: [title: 'prcr'])
        subPrograma(size: 1..40, blank: true, nullable: true, attributes: [title: 'subPrograma'])
//        cbcr(size: 1..200, blank: true, nullable: true, attributes: [title: 'cbcr'])
//        bsct(size: 1..200, blank: true, nullable: true, attributes: [title: 'bsct'])
//        psrf(size: 1..200, blank: true, nullable: true, attributes: [title: 'psrf'])
//        rete(size: 1..200, blank: true, nullable: true, attributes: [title: 'rete'])
        nota(size: 1..3071, blank: true, nullable: true, attributes: [title: 'nota'])
        nota1(size: 1..200, blank: true, nullable: true, attributes: [title: 'nota1'])
        nota2(size: 1..200, blank: true, nullable: true, attributes: [title: 'nota2'])
        memo1(size: 1..200, blank: true, nullable: true, attributes: [title: 'memo1'])
        notaFormula(size: 1..200, blank: true, nullable: true, attributes: [title: 'notaFormula'])
        titulo(size: 1..100, blank: true, nullable: true, attributes: [title: 'titulo'])
        memo2(size: 1..200, blank: true, nullable: true, attributes: [title: 'memo2'])
    }
}