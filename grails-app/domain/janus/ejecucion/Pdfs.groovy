package janus.ejecucion

import janus.Obra

class Pdfs {

    Obra obra
    Planilla planilla
    String parrafo1
    String parrafo2
    String parrafo3
    String parrafo4
    String parrafo5
    String copia
    Date fecha = new Date()
    static auditable = true
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
            parrafo5 column: 'pdfsprr5'
            fecha column: 'pdfsfcha'
            copia column: 'pdfscpia'
        }
    }

    static constraints = {
        obra blank: true, nullable: true
        planilla blank: true, nullable: true
        parrafo1 maxSize: 1023
        parrafo2 maxSize: 1023
        parrafo3 maxSize: 1023
        parrafo4 maxSize: 1023, blank: true, nullable: true
        parrafo5 maxSize: 1023, blank: true, nullable: true
        copia maxSize: 1023, blank: true, nullable: true
    }
}
