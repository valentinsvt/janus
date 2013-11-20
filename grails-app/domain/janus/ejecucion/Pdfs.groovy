package janus.ejecucion

import janus.Obra

class Pdfs {

    Obra obra
    Planilla planilla
    String parrafo1
    String parrafo2
    String parrafo3
    String parrafo4
    Date fecha = new Date()

    static mapping = {
        table 'pdfs'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'pdfs__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'pdfs__id'
            planilla column: 'plnl__id'
            obra column: 'obra__id'
            parrafo1 column: 'pdfsprr1'
            parrafo2 column: 'pdfsprr2'
            parrafo3 column: 'pdfsprr3'
            parrafo4 column: 'pdfsprr4'
            fecha column: 'pdfsfcha'
        }
    }

    static constraints = {
        obra blank: true, nullable: true
        planilla blank: true, nullable: true
        parrafo4 blank: true, nullable: true
    }
}
