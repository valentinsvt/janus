package janus.pac

import janus.Contrato

class Garantia {

    Garantia padre
    Contrato contrato
    Aseguradora aseguradora
    Moneda moneda
    TipoGarantia tipoGarantia
    TipoDocumentoGarantia tipoDocumentoGarantia
    EstadoGarantia estado
    String codigo
    int numeroRenovaciones
    String estadoGarantia //registrado o no
    double monto
    Date fechaInicio
    Date fechaFinalizacion
    int diasGarantizados
    String cancelada
    String pedido
    String observaciones

    static auditable = true
    static mapping = {


        table 'grnt'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'grnt__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'grnt__id'
            tipoGarantia column: 'tpgr__id'
            tipoDocumentoGarantia column: 'tdgr__id'
            estado column: 'edgr__id'
            moneda column: 'mnda__id'
            aseguradora column: 'asgr__id'
            contrato column: 'cntr__id'
            padre column: 'grntpdre'
            codigo column: 'grntcdgo'
            numeroRenovaciones column: 'grntnmrv'
            estadoGarantia column: 'grntetdo'
            monto column: 'grntmnto'
            fechaInicio column: 'grntfcin'
            fechaFinalizacion column: 'grntfcfn'
            diasGarantizados column: 'grntdias'
            cancelada column: 'grntcncl'
            pedido column: 'grntpddo'
            observaciones column: 'grntobsv'
        }

    }


    static constraints = {

        tipoGarantia(blank: true, nullable: true, attributes: [title: 'código'])
        tipoDocumentoGarantia(blank: true, nullable: true, attributes: [title: 'código'])
        estado(blank: true, nullable: true, attributes: [title: 'código'])
        moneda(blank: true, nullable: true, attributes: [title: 'código'])
        aseguradora(blank: true, nullable: true, attributes: [title: 'código'])
        contrato(blank: true, nullable: true, attributes: [title: 'código'])
        padre(blank: true, nullable: true, attributes: [title: 'código'])
        codigo(size: 1..15, blank: true, nullable: true, attributes: [title: 'código'])
        numeroRenovaciones(blank: true, nullable: true, attributes: [title: 'código'])
        estadoGarantia(size: 1..1, blank: true, nullable: true, attributes: [title: 'código'])
        monto(blank: true, nullable: true, attributes: [title: 'código'])
        fechaInicio(blank: true, nullable: true, attributes: [title: 'código'])
        fechaFinalizacion(blank: true, nullable: true, attributes: [title: 'código'])
        diasGarantizados(blank: true, nullable: true, attributes: [title: 'código'])
        cancelada(size: 1..1, blank: true, nullable: true, attributes: [title: 'código'])
        pedido(size: 1..1, blank: true, nullable: true, attributes: [title: 'código'])

        observaciones(maxSize: 511, blank: true, nullable: true, attributes: [title: 'observaciones'])
    }
}
