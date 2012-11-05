package janus

class Contrato implements Serializable {


    static mapping = {

        table 'cntr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'cntr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'cntr__id'

        }
    }

    static constraints = {

    }
}
