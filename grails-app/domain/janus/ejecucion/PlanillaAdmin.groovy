package janus.ejecucion

import janus.Obra
import janus.Persona

class PlanillaAdmin {

    Persona usuario
    Obra obra
    Date fechaIngreso
    String numero
    String descripcion
    String numeroFactura
    EstadoPlanilla estadoPlanilla
    TipoPlanilla tipoPlanilla
    double valor=0
    String observaciones

    String oficioEntradaPlanilla
    Date fechaOficioEntradaPlanilla
    static auditable = true


    static mapping = {
        table 'plad'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'plad__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'plad__id'
            obra column: 'obra__id'
            fechaIngreso column: 'pladfcin'
            numero column: 'pladnmro'
            numeroFactura column: 'pladnmfc'
            estadoPlanilla column: 'edpl__id'
            valor column: 'pladvlor'
            observaciones column: 'pladobsr'
            oficioEntradaPlanilla column: 'pladofep'
            fechaOficioEntradaPlanilla column: 'pladfcoe'
            usuario column: 'prsn__id'
            tipoPlanilla column: 'tppl__id'
            descripcion column: 'pladdscr'

        }
    }

    static constraints = {
        numero(nullable: true,blank: true,size: 1..30)
        numeroFactura (nullable: true,blank: true,size: 1..10)
        observaciones(nullable: true,blank: true,size: 1..255)
        oficioEntradaPlanilla(nullable: true,blank: true,size: 1..55)
        fechaOficioEntradaPlanilla(nullable: true,blank: true)
        descripcion(nullable: true,blank: true,size: 1..255)
    }
}
