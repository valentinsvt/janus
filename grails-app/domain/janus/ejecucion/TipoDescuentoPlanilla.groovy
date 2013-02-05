package janus.ejecucion

class TipoDescuentoPlanilla implements Serializable{

     String nombre
     double porcentaje
     String cuenta
     String valor


    static mapping = {

        table 'tpds'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpds__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpds__id'
            nombre column: 'tpdsdscr'
            cuenta column: 'tpdscnt'
            porcentaje column: 'tpdspcnt'
            valor column: 'tpdsedit'
    }

}

    static constraints = {

        nombre(blank: true, nullable: true)
        cuenta(blank: true, nullable: true)
        porcentaje(blank: true, nullable: true)
        valor(blank: true, nullable: true)

    }
}
