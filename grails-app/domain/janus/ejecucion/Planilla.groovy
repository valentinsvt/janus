package janus.ejecucion

import janus.Contrato
import janus.Persona

class Planilla {

    Contrato contrato
    TipoPlanilla tipoPlanilla
    PeriodosInec periodoIndices
    String numero
    Date fechaPresentacion
    Date fechaIngreso
    String descripcion
    double valor
    double descuentos
    double reajuste

//    Date fechaReajuste
//    double diferenciaReajuste
//    String reajustada

    String observaciones
    Date fechaInicio
    Date fechaFin




//    String aprobado
//    String oficioSalida
//    Date fechaOficioSalida
//    String oficioPago
//    Date fechaOficioPago
//    Date fechaMemoSalida

//    String memoSalida
//    Double multaRetraso = 0                 //multa por retraso de obra (solo en la ultima planilla de avance)
//    Double multaPlanilla = 0                //multa por no presentacion de la planilla (retraso en la presentacion)
//    Date fechaOrdenPago
//    String memoOrdenPago
//    String memoPago

//    double reajusteLiq = 0
//    Double multaIncumplimiento = 0          //multa por incumplimiento del cronograma (retraso de obra en las planillas de avance)
//    Double multaDisposiciones = 0           //multa por no acatar las disposiciones del fiscalizador


    Integer diasMultaDisposiciones = 0          //dias de multa por no acatar las disposiciones del fiscalizador
    Date fechaPago

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
    Double avanceFisico

    /* multa especial y nota de descuento (NOPG) */
    String descripcionMulta
    Double multaEspecial = 0
    String noPago
    Double noPagoValor = 0

    String logPagos
    FormulaPolinomicaReajuste formulaPolinomicaReajuste

    String tipoContrato
    Planilla planillaCmpl

    /*Orden de Cambio - Trabajo*/

    String numeroOrden
    String memoOrden
    String numeroCertificacionOrden
    Date fechaCertificacionOrden
    String garantiaOrden
    Date fechaSuscripcionOrden

    String numeroTrabajo
    String memoTrabajo
    String numeroCertificacionTrabajo
    Date fechaCertificacionTrabajo
    String garantiaTrabajo
    Date fechaSuscripcionTrabajo



    static auditable = true
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
            periodoIndices column: 'prin__id'

            numero column: 'plnlnmro'
            fechaPresentacion column: 'plnlfcpr'
            fechaIngreso column: 'plnlfcig'
            fechaPago column: 'plnlfcpg'
            descripcion column: 'plnldscr'
            valor column: 'plnlmnto'
            descuentos column: 'plnldsct'
            reajuste column: 'plnlrjst'

//            estadoPlanilla column: 'edpl__id'
//            numeroFactura column: 'plnlfctr'
//            fechaReajuste column: 'plnlfcrj'
//            diferenciaReajuste column: 'plnldfrj'
//            reajustada column: 'plnlrjtd'

//            aprobado column: 'plnlaprb'
//            oficioSalida column: 'plnlofsl'
//            fechaOficioSalida column: 'plnlfcsl'
//            oficioPago column: 'plnlofpg'
//            fechaOficioPago column: 'plnlfcop'
//            fechaMemoSalida column: 'plnlfcms'


            observaciones column: 'plnlobsr'
            fechaInicio column: 'plnlfcin'
            fechaFin column: 'plnlfcfn'

//            memoSalida column: 'plnlmmsl'
//            multaRetraso column: 'plnlmlrt'
//            multaPlanilla column: 'plnlmlpl'
//            fechaOrdenPago column: 'plnlfcod'
//            memoOrdenPago column: 'plnlmmop'
//            memoPago column: 'plnlmmpg'

//            reajusteLiq column: 'plnlrjlq'
//            multaIncumplimiento column: 'plnlmlin'
//            multaDisposiciones column: 'plnlmlds'



            diasMultaDisposiciones column: 'plnldsmd'

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
            avanceFisico column: 'plnlavfs'

            logPagos column: 'plnl_log'
            formulaPolinomicaReajuste column: 'fprj__id'

            /* multa especial y nota de descuento (NOPG) */
            descripcionMulta column: 'plnldsml'
            multaEspecial column: 'plnlmles'
            noPago column: 'plnlnopg'
            noPagoValor column: 'plnlnpvl'

            tipoContrato column: 'plnltipo'
            planillaCmpl column: 'plnlcmpl'

            /*Orden*/
            numeroOrden column: 'plnlordn'
            memoOrden column: 'plnlocmm'
            numeroCertificacionOrden column: 'plnloccp'
            fechaCertificacionOrden column: 'plnlocfp'
            garantiaOrden column: 'plnlocgr'
            fechaSuscripcionOrden column: 'plnlocfc'

            /*Trabajo*/
            numeroTrabajo column: 'plnlortb'
            memoTrabajo column: 'plnlotmm'
            numeroCertificacionTrabajo column: 'plnlotcp'
            fechaCertificacionTrabajo column: 'plnlotfp'
            garantiaTrabajo column: 'plnlotgr'
            fechaSuscripcionTrabajo column: 'plnlotfc'


        }
    }

    static constraints = {
        contrato(blank: true, nullable: true)
        tipoPlanilla(blank: true, nullable: true)
        periodoIndices(blank: true, nullable: true)

        numero(blank: true, nullable: true, maxSize: 30)
        fechaPresentacion(blank: true, nullable: true)
        fechaIngreso(blank: true, nullable: true)
        fechaPago(blank: true, nullable: true)
        descripcion(maxSize: 254, blank: true, nullable: true)
        valor(blank: true, nullable: true)
        descuentos(blank: true, nullable: true)
        reajuste(blank: true, nullable: true)

//        estadoPlanilla(blank: true, nullable: true)
//        numeroFactura(maxSize: 15, blank: true, nullable: true)
//        fechaReajuste(blank: true, nullable: true)
//        diferenciaReajuste(blank: true, nullable: true)
//        reajustada(blank: true, nullable: true)

//        aprobado(blank: true, nullable: true)
//        oficioSalida(maxSize: 12, blank: true, nullable: true)
//        fechaOficioSalida(blank: true, nullable: true)
//        oficioPago(maxSize: 12, blank: true, nullable: true)
//        fechaOficioPago(blank: true, nullable: true)
//        fechaMemoSalida(blank: true, nullable: true)

//        memoSalida(blank: true, nullable: true)
//        fechaOrdenPago(blank: true, nullable: true)
//        memoOrdenPago(maxSize: 20, blank: true, nullable: true)
//        memoPago(maxSize: 20, blank: true, nullable: true)

        observaciones(maxSize: 127, blank: true, nullable: true)
        fechaInicio(blank: true, nullable: true)
        fechaFin(blank: true, nullable: true)

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
//        periodoAnticipo(blank: true, nullable: true)

        multaEspecial(blank: true, nullable: true)
        descripcionMulta(blank: true, nullable: true,size: 1..255)
        logPagos(blank: true, nullable: true,size: 1..255)
        formulaPolinomicaReajuste(blank: true, nullable: true)
        noPago(blank: true, nullable: true)
        tipoContrato(blank: false, nullable: false)
        planillaCmpl(blank: true, nullable: true)

        numeroOrden(blank: true, nullable: true)
        memoOrden(blank: true, nullable: true)
        numeroCertificacionOrden(blank: true, nullable: true)
        fechaCertificacionOrden(blank: true, nullable: true)
        garantiaOrden(blank: true, nullable: true)
        fechaSuscripcionOrden(blank: true, nullable: true)

        numeroTrabajo(blank: true, nullable: true)
        memoTrabajo(blank: true, nullable: true)
        numeroCertificacionTrabajo(blank: true, nullable: true)
        fechaCertificacionTrabajo(blank: true, nullable: true)
        garantiaTrabajo(blank: true, nullable: true)
        fechaSuscripcionTrabajo(blank: true, nullable: true)

    }

    String toString() {
        "Planilla: ${this.numero} Del período: ${this.fechaInicio?.format("dd-MM-yyyy")} al ${this.fechaFin?.format("dd-MM-yyyy")}"
    }
}
