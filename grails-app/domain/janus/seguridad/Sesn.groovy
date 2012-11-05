package janus.seguridad

import janus.Persona

class Sesn implements Serializable {
    Persona usuario
    Prfl perfil

    static auditable = [ignore: []]

    static mapping = {
        table 'sesn'
        cache usage: 'read-write', include: 'non-lazy'
        version false
        id generator: 'identity'
        sort "perfil"
        columns {
            id column: 'sesn__id'
            perfil column: 'prfl__id'
            usuario column: 'prsn__id'
        }
    }


    static constraints = {

    }

    String toString() {
        return "${this.perfil}"
    }

}
