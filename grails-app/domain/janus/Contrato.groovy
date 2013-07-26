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
    Double plazo
    String estado
    String responsableTecnico
    Date fechaFirma
    String cuentaContable
    String prorroga
    String observaciones
    String memo

    Double multaRetraso
    Double multaPlanilla

    Date fechaPedidoRecepcionContratista
    Date fechaPedidoRecepcionFiscalizador

    Persona administrador

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
            anticipo column: 'cntrantc'
            multas column: 'cntrmlta'
            estado column: 'cntretdo'
            responsableTecnico column: 'cntrrptc'
            fechaFirma column: 'cntrfcfr'
            cuentaContable column: 'cntrcnta'
            prorroga column: 'cntrprrg'
            observaciones column: 'cntrobsr'
            memo column: 'cntrmemo'
            plazo column: 'cntrplzo'

            multaRetraso column: "cntrmlrt"
            multaPlanilla column: "cntrmlpl"

            fechaPedidoRecepcionContratista column: 'cntrfccn'
            fechaPedidoRecepcionFiscalizador column: 'cntrfcfs'

            administrador column: 'prsnadmn'
        }
    }

    static constraints = {
        oferta(blank: true, nullable: true)
        tipoContrato(blank: true, nullable: true)
        tipoPlazo(blank: true, nullable: true)
        padre(blank: true, nullable: true)
        periodoValidez(blank: true, nullable: true)
        codigo(blank: true, nullable: true)
        objeto(size: 1..1023, blank: true, nullable: true)
        fechaSubscripcion(blank: true, nullable: true)
        fechaIngreso(blank: true, nullable: true)
        fechaInicio(blank: true, nullable: true)
        fechaFin(blank: true, nullable: true)
        monto(blank: true, nullable: true)
        financiamiento(blank: true, nullable: true)
        porcentajeAnticipo(blank: true, nullable: true)
        anticipo(blank: true, nullable: true)
        multas(blank: true, nullable: true)
        estado(blank: true, nullable: true)
        responsableTecnico(blank: true, nullable: true)
        fechaFirma(blank: true, nullable: true)
        cuentaContable(blank: true, nullable: true)
        prorroga(blank: true, nullable: true)
        observaciones(blank: true, nullable: true)
        memo(blank: true, nullable: true)
        plazo(blank: true, nullable: true)

        fechaPedidoRecepcionContratista(blank: true, nullable: true)
        fechaPedidoRecepcionFiscalizador(blank: true, nullable: true)

        administrador(blank: true, nullable: true)
    }

    def getObra() {
        return this.oferta?.concurso?.obra
    }
}
