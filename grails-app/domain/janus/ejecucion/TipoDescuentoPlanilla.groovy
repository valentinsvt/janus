package janus.ejecucion

class TipoDescuentoPlanilla implements Serializable{

     String nombre
     double porcentaje
     String valor

    static auditable = true
    static mapping = {

        table 'tpds'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpds__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpds__id'
            nombre column: 'tpdsdscr'
            porcentaje column: 'tpdspcnt'
            valor column: 'tpdsedit'
    }

}

    static constraints = {

        nombre(blank: true, nullable: true)
        porcentaje(blank: true, nullable: true)
        valor(blank: true, nullable: true)

    }
}
