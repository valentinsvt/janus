package janus

import janus.pac.Oferta
import janus.pac.PeriodoValidez
import janus.pac.TipoContrato
import janus.pac.TipoPlazo

class Contrato implements Serializable {

    Oferta oferta
    TipoContrato tipoContrato
    TipoPlazo tipoPlazo
    Contrato padre
    PeriodoValidez periodoValidez
    String codigo
    String objeto
    Date fechaSubscripcion
    Date fechaIngreso
    Date fechaInicio
    Date fechaFin
    Double monto
    Double financiamiento
    Double porcentajeAnticipo
    Double anticipo
    Double multas
    String estado
    String responsableTecnico
    Date fechaFirma
    String cuentaContable
    String prorroga
    String observaciones
    String memo

    static mapping = {

        table 'cntr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'cntr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'cntr__id'

            oferta column: 'ofrt__id'
            tipoContrato column: 'tpcr__id'
            tipoPlazo column: 'tppz__id'
            padre column: 'cntrpdre'
            periodoValidez column: 'prin__id'
            codigo column: 'cntrcdgo'
            objeto column: 'cntrobjt'
            fechaSubscripcion column: 'cntrfcsb'
            fechaIngreso column: 'cntrfcig'
            fechaInicio column: 'cntrfcin'
            fechaFin column: 'cntrfcfn'
            monto column: 'cntrmnto'
            financiamiento column: 'cntrfina'
            porcentajeAnticipo column: 'cntrpcan'
            anticipo column: 'cntrpcan'
            multas column: 'cntrmlta'
            estado column: 'cntretdo'
            responsableTecnico column: 'cntrrptc'
            fechaFirma column: 'cntrfcfr'
            cuentaContable column: 'cntrcnta'
            prorroga column: 'cntrprrg'
            observaciones column: 'cntrobsr'
            memo column: 'cntrmemo'

        }
    }

    static constraints = {
        oferta(blank: false, nullable: false)
        tipoContrato(blank: false, nullable: false)
        tipoPlazo(blank: false, nullable: false)
        padre(blank: false, nullable: false)
        periodoValidez(blank: false, nullable: false)
        codigo(blank: false, nullable: false)
        objeto(blank: false, nullable: false)
        fechaSubscripcion(blank: false, nullable: false)
        fechaIngreso(blank: false, nullable: false)
        fechaInicio(blank: false, nullable: false)
        fechaFin(blank: false, nullable: false)
        monto(blank: false, nullable: false)
        financiamiento(blank: false, nullable: false)
        porcentajeAnticipo(blank: false, nullable: false)
        anticipo(blank: false, nullable: false)
        multas(blank: false, nullable: false)
        estado(blank: false, nullable: false)
        responsableTecnico(blank: false, nullable: false)
        fechaFirma(blank: false, nullable: false)
        cuentaContable(blank: false, nullable: false)
        prorroga(blank: false, nullable: false)
        observaciones(blank: false, nullable: false)
        memo(blank: false, nullable: false)
    }
}
