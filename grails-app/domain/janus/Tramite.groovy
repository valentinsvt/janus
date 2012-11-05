package janus

class Tramite implements Serializable {

    Obra obra
    TipoTramite tipoTramite
    Contrato contrato
    int tramitePadre
    String codigo
    Date fecha
    String descripcion
    Date fechaRecepcion
    String documentosAdjuntos

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
            codigo column: 'trmtcdgo'
            fecha column: 'trmtfcha'
            descripcion column: 'trmtdscr'
            fechaRecepcion column: 'trmtfcrc'
            documentosAdjuntos column: 'trmtadjn'

        }

    }

    static constraints = {
        obra(blank: true, nullable: true, attributes: [title: 'obra'])
        codigo(size: 1..31, blank: true, nullable: true, attributes: [title: 'tramiteCodigo'])
        tipoTramite(blank: true, nullable: true, attributes: [title: 'tipoTramite'])
        contrato(blank: true, nullable: true, attributes: [title: 'contrato'])
        tramitePadre(blank: true, nullable: true, attributes: [title: 'tramitePadre'])
        fecha(blank: true, nullable: true, attributes: [title: 'tramiteFecha'])
        descripcion(size: 1..4095, blank: true, nullable: true, attributes: [title: 'tramiteDescripcion'])
        fechaRecepcion(blank: true, nullable: true, attributes: [title: 'tipoFechaRecepcion'])
        documentosAdjuntos(size: 1..127, blank: true, nullable: true, attributes: [title: 'tramiteDocsAdjuntos'])


    }
}
