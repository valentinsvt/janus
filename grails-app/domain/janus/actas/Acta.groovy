package janus.actas

import janus.Contrato

class Acta {

    String numero
    Contrato contrato
    String descripcion
    String nombre
    String tipo             //provisional:P o definitiva:D

    static hasMany = [secciones: Seccion]

    static mapping = {
        secciones sort: "numero"
        table 'acta'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'acta__id'
        id generator: 'identity'
        version false
        columns {
            numero column: "actanmro"
            contrato column: "cntr__id"
            descripcion column: "actadscr"
            descripcion type: "text"
            nombre column: "actanmbr"
            tipo column: "actatipo"
        }
    }

    static constraints = {
        numero(blank: false, nullable: false, maxSize: 20)
        contrato(blank: false, nullable: false)
        descripcion(blank: false, nullable: false)
        nombre(blank: false, nullable: false, maxSize: 20)
        tipo(blank: false, nullable: false, inList: ["P", "D"])
    }
}
