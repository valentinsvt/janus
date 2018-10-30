package janus.seguridad

import janus.Persona

class Usos implements Serializable{

    Persona persona
    Prfl perfil
    Accn accion
    Accn accionDesde
    Date entra
    Date sale
    String sesion

    static auditable = [ignore:[]]
	
    static mapping = {
        table 'usst'
        cache usage:'read-write', include:'non-lazy'
        version false
        id generator: 'identity'
        
        columns {
            id column: 'usst__id'
            persona column: 'prsn__id'
            perfil column: 'prfl__id'
            accion column:'accn__id'
            accionDesde column:'accndsde'
            entra column:'usstfcen'
            sale column:'usstfcsa'
            sesion column:'usstsesn'
        }
    }
	
    static constraints = {
        accionDesde(blank: true, nullable: true)
        accion(blank:false, nullable: false)
        sale(blank: true, nullable: true)
    }
}
