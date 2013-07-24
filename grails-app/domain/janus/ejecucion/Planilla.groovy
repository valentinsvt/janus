package janus.ejecucion

import janus.Contrato
import janus.Persona

class Planilla {

    Contrato contrato
    TipoPlanilla tipoPlanilla
    EstadoPlanilla estadoPlanilla
    PeriodosInec periodoIndices
    String numero
    String numeroFactura
    Date fechaPresentacion
    Date fechaIngreso
    String descripcion
    double valor
    double descuentos
    String reajustada
    double reajuste
    double reajusteLiq = 0
    Date fechaReajuste
    double diferenciaReajuste
    String observaciones
    Date fechaInicio
    Date fechaFin
    String aprobado

    Double multaRetraso = 0
    Double multaPlanilla = 0

    String memoSalida
    String memoOrdenPago
    String memoPago
    String oficioSalida
    String oficioPago

    Date fechaOficioPago
    Date fechaMemoSalida
    Date fechaPago
    Date fechaOficioSalida
    Date fechaOrdenPago

    String oficioEntradaPlanilla
    String memoSalidaPlanilla
    String memoPedidoPagoPlanilla
    String memoPagoPlanilla

    Date fechaOficioEntradaPlanilla
    Date fechaMemoSalidaPlanilla
    Date fechaMemoPedidoPagoPlanilla
    Date fechaMemoPagoPlanilla

    Persona fiscalizador

    Planilla padreCosto

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
            fechaOrdenPago column: 'plnlfcod'
            descripcion column: 'plnldscr'
            valor column: 'plnlmnto'
            descuentos column: 'plnldsct'
            reajustada column: 'plnlrjtd'
            reajuste column: 'plnlrjst'
            reajusteLiq column: 'plnlrjlq'
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
            memoOrdenPago column: 'plnlmmop'
            memoPago column: 'plnlmmpg'
            fechaMemoSalida column: 'plnlfcms'

            multaRetraso column: 'plnlmlrt'
            multaPlanilla column: 'plnlmlpl'

            oficioEntradaPlanilla column: 'plnlofen'
            memoSalidaPlanilla column: 'plnlmmad'
            memoPedidoPagoPlanilla column: 'plnlmmpp'
            memoPagoPlanilla column: 'plnlmmfi'

            fechaOficioEntradaPlanilla column: 'plnlfcen'
            fechaMemoSalidaPlanilla column: 'plnlfcad'
            fechaMemoPedidoPagoPlanilla column: 'plnlfcpp'
            fechaMemoPagoPlanilla column: 'plnlfcfi'

            fiscalizador column: 'prsnfscl'

            padreCosto column: 'plnlpdcs'
        }
    }

    static constraints = {
        contrato(blank: true, nullable: true)
        tipoPlanilla(blank: true, nullable: true)
        estadoPlanilla(blank: true, nullable: true)
        periodoIndices(blank: true, nullable: true)

        numero(blank: true, nullable: true, maxSize: 20)
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
        fechaOrdenPago(blank: true, nullable: true)
        memoSalida(blank: true, nullable: true)
        fechaMemoSalida(blank: true, nullable: true)

        memoOrdenPago(maxSize: 20, blank: true, nullable: true)
        memoPago(maxSize: 20, blank: true, nullable: true)

        oficioEntradaPlanilla(maxSize: 20, blank: true, nullable: true)
        memoSalidaPlanilla(maxSize: 20, blank: true, nullable: true)
        memoPedidoPagoPlanilla(maxSize: 20, blank: true, nullable: true)
        memoPagoPlanilla(maxSize: 20, blank: true, nullable: true)

        fechaOficioEntradaPlanilla(blank: true, nullable: true)
        fechaMemoSalidaPlanilla(blank: true, nullable: true)
        fechaMemoPedidoPagoPlanilla(blank: true, nullable: true)
        fechaMemoPagoPlanilla(blank: true, nullable: true)

        fiscalizador(blank: true, nullable: true)

        padreCosto(blank: true, nullable: true)
    }
}
