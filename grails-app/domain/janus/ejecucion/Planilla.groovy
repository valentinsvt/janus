package janus.ejecucion

import janus.Contrato
import janus.pac.PeriodoValidez

class Planilla {

    Contrato contrato
    TipoPlanilla tipoPlanilla
    EstadoPlanilla estadoPlanilla
    PeriodoValidez periodoIndices

    int numero
    String numeroFactura
    Date fechaPresentacion
    Date fechaIngreso
    Date fechaPago
    String descripcion
    double valor
    double descuentos
    String reajustada
    double reajuste
    Date fechaReajuste
    double diferenciaReajuste
    String observaciones
    Date fechaInicio
    Date fechaFin
    String oficioSalida
    Date fechaOficioSalida
    String oficioPago
    Date fechaOficioPago
    String aprobado

    String memoSalida
    Date fechaMemoSalida

    static mapping = {
        table 'plnl'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'plnl__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'plnl__id'

            contrato column: 'cntr__id'
            tipoPlanilla column: 'tppl__id'
            estadoPlanilla column: 'edpl__id'
            periodoIndices column: 'prin__id'

            numero column: 'plnlnmro'
            numeroFactura column: 'plnlfctr'
            fechaPresentacion column: 'plnlfcpr'
            fechaIngreso column: 'plnlfcig'
            fechaPago column: 'plnlfcpg'
            descripcion column: 'plnldscr'
            valor column: 'plnlmnto'
            descuentos column: 'plnldsct'
            reajustada column: 'plnlrjtd'
            reajuste column: 'plnlrjst'
            fechaReajuste column: 'plnlfcrj'
            diferenciaReajuste column: 'plnldfrj'
            observaciones column: 'plnlobsr'
            fechaInicio column: 'plnlfcin'
            fechaFin column: 'plnlfcfn'
            oficioSalida column: 'plnlofsl'
            fechaOficioSalida column: 'plnlfcsl'
            oficioPago column: 'plnlofpg'
            fechaOficioPago column: 'plnlfcop'
            aprobado column: 'plnlaprb'

            memoSalida column: 'plnlmmsl'
            fechaMemoSalida column: 'plnlfcms'
        }
    }

    static constraints = {
        contrato(blank: true, nullable: true)
        tipoPlanilla(blank: true, nullable: true)
        estadoPlanilla(blank: true, nullable: true)
        periodoIndices(blank: true, nullable: true)

        numero(blank: true, nullable: true)
        numeroFactura(maxSize: 15, blank: true, nullable: true)
        fechaPresentacion(blank: true, nullable: true)
        fechaIngreso(blank: true, nullable: true)
        fechaPago(blank: true, nullable: true)
        descripcion(maxSize: 254, blank: true, nullable: true)
        valor(blank: true, nullable: true)
        descuentos(blank: true, nullable: true)
        reajustada(blank: true, nullable: true)
        reajuste(blank: true, nullable: true)
        fechaReajuste(blank: true, nullable: true)
        diferenciaReajuste(blank: true, nullable: true)
        observaciones(maxSize: 127, blank: true, nullable: true)
        fechaInicio(blank: true, nullable: true)
        fechaFin(blank: true, nullable: true)
        oficioSalida(maxSize: 12, blank: true, nullable: true)
        fechaOficioSalida(blank: true, nullable: true)
        oficioPago(maxSize: 12, blank: true, nullable: true)
        fechaOficioPago(blank: true, nullable: true)
        aprobado(blank: true, nullable: true)

        memoSalida(blank: true, nullable: true)
        fechaMemoSalida(blank: true, nullable: true)
    }
}
