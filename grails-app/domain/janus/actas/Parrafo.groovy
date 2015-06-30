package janus.actas

class Parrafo {

    int numero
    String contenido
    String tipoTabla
    // RBR: Rubros (4.1), DTP: Detalle planillas (4.2), OAD: Obras adicionales (4.3) OCP: costo y porcentaje (4.4),
    // RRP:resumen reajuste precios (4.5), RGV: resumen general valores (4.6), DTA: detalle ampliaciones (5.5)
    // DTS: detalle suspensiones (5.6), RPR: resumen reajuste (8.1)a

    static belongsTo = [seccion: Seccion]

    static mapping = {
        table 'prrf'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prrf__id'
        id generator: 'identity'
        version false
        columns {
            numero column: "prrfnmro"
            contenido column: "prrfcont"
            contenido type: "text"
            tipoTabla column: "prrftptb"
            seccion column: "sccn__id"
        }
    }

    static constraints = {
        numero(blank: false, nullable: false)
        contenido(blank: true, nullable: true)
        tipoTabla(blank: true, nullable: true, inList: ["RBR", "DTP", "OAD", "OCP", "RRP", "RGV", "DTA", "DTS", "RPR"])
    }
}
