package janus

import janus.pac.Anio

class Presupuesto implements Serializable {
    Anio anio;
    FuenteFinanciamiento fuente;
    String numero
    String descripcion
    String programa
    String subPrograma
    String proyecto
    int nivel=0;
    static auditable = true
    static mapping = {
        table 'prsp'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prsp__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prsp__id'
            numero column: 'prspnmro'
            descripcion column: 'prspdscr'
            nivel column: 'prspnvel'
            anio column: 'anio__id'
            fuente column: 'fnfn__id'
            programa column: 'prspprgm'
            subPrograma column: 'prspsbpr'
            proyecto column: 'prspproy'

        }
    }
    static constraints = {
        numero(size: 1..50, blank: false, attributes: [title: 'numero'])
        nivel(blank: true, attributes: [title: 'nivel'])
        descripcion(size: 1..255, blank: false, attributes: [title: 'descripcion'])
        anio(nullable: true,blank:true)
        fuente(nullable: true,blank:true)
        programa(nullable: true,blank: true,size: 1..255);
        subPrograma(nullable: true,blank: true,size: 1..255);
        proyecto(nullable: true,blank: true,size: 1..255);
    }

    String toString() {
        return this.descripcion
    }
}