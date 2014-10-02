package janus

class Tramite implements Serializable {
    janus.ejecucion.Planilla planilla
    Obra obra
    TipoTramite tipoTramite
    Contrato contrato
    Tramite tramitePadre

    EstadoTramite estado

    String codigo
    String descripcion
    String documentosAdjuntos
    String memo

    Date fecha              //fecha creacion del tramite
    Date fechaEnvio         //fecha de envio fisico de los documentos
    Date fechaRecepcion     //fecha de recepcion
    Date fechaRespuesta     //fecha de respuesta
    Date fechaFinalizacion  //fecha de finalizacion
    static auditable = true
    static mapping = {

        table 'trmt'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'trmt__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'trmt__id'
            obra column: 'obra__id'
            tipoTramite column: 'tptr__id'
            contrato column: 'cntr__id'
            tramitePadre column: 'trmtpdre'
            estado column: 'ettr__id'
            codigo column: 'trmtcdgo'
            descripcion column: 'trmtdscr'
            documentosAdjuntos column: 'trmtadjn'
            memo column: 'trmtmemo'
            planilla column: 'plnl__id'
            fecha column: 'trmtfcha'
            fechaEnvio column: 'trmtfcen'
            fechaRecepcion column: 'trmtfcrc'
            fechaRespuesta column: 'trmtfcrs'
            fechaFinalizacion column: 'trmtfcfn'
        }
    }

    static constraints = {
        obra(blank: true, nullable: true, attributes: [title: 'obra'])
        codigo(size: 1..31, blank: true, nullable: true, attributes: [title: 'tramiteCodigo'])
        tipoTramite(blank: true, nullable: true, attributes: [title: 'tipoTramite'])
        estado(blank: false, nullable: false, attributes: [title: 'estado tramite'])
        contrato(blank: true, nullable: true, attributes: [title: 'contrato'])
        tramitePadre(blank: true, nullable: true, attributes: [title: 'tramitePadre'])
        fecha(blank: true, nullable: true, attributes: [title: 'tramiteFecha'])
        descripcion(size: 1..4095, blank: true, nullable: true, attributes: [title: 'tramiteDescripcion'])
        fechaRecepcion(blank: true, nullable: true, attributes: [title: 'tipoFechaRecepcion'])
        documentosAdjuntos(size: 1..127, blank: true, nullable: true, attributes: [title: 'tramiteDocsAdjuntos'])
        fechaEnvio(blank: true, nullable: true, attributes: [title: 'Fecha de envío'])
        memo(maxSize: 20, blank: true, nullable: true, attributes: [title: 'numero de memo'])
        planilla(blank:true,nullable: true)
        fechaRespuesta(blank: true, nullable: true, attributes: [title: 'tipoFechaRecepcion'])
        fechaFinalizacion(blank: true, nullable: true, attributes: [title: 'tipoFechaRecepcion'])
    }
}
