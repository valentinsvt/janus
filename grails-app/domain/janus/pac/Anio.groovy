package janus.pac

class Anio {

    String anio
    int estado=0
    static auditable = true
    static mapping = {
        table 'anio'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'anio__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'anio__id'
            anio column: 'anioanio'
            estado column: 'anioetdo'
        }
    }
    static constraints = {
        anio(nullable: true,blank: true,size: 1..4)
    }

    String toString() {
        anio
    }
}

