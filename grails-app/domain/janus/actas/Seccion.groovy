package janus.actas

class Seccion {

    Integer numero
    String titulo

    static belongsTo = [acta: Acta]
    static hasMany = [parrafos: Parrafo]
    static auditable = true
    static mapping = {
        parrafos sort: "numero"
        table 'sccn'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'sccn__id'
        id generator: 'identity'
        version false
        columns {
            numero column: "sccnnmro"
            titulo column: "sccnttlo"
            acta column: "acta__id"
        }
    }

    static constraints = {
        numero(blank: false, nullable: false)
        titulo(blank: false, nullable: false, maxSize: 511)
    }
}
