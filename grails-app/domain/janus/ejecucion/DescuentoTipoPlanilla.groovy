package janus.ejecucion

class DescuentoTipoPlanilla implements Serializable {

     TipoPlanilla tipoPlanilla
     TipoDescuentoPlanilla tipoDescuentoPlanilla
    static auditable = true

    static mapping = {

        table 'dstp'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dstp__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dstp__id'
            tipoPlanilla column: 'tppl__id'
            tipoDescuentoPlanilla column: 'tpds__id'
        }

    }

    static constraints = {
    }
}
