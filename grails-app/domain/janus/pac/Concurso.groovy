package janus.pac

import janus.Administracion
import janus.Obra

class Concurso {

    Obra obra
    Administracion administracion
    Pac pac
    String codigo
    String objeto
    Double costoBases
    Double porMilBases = 0
    Date fechaInicio
    Date fechaPublicacion
    Date fechaLimitePreguntas
    Date fechaLimiteRespuestas
    Date fechaLimiteEntregaOfertas
    Date fechaLimiteSolicitarConvalidacion
    Date fechaLimiteRespuestaConvalidacion
    Date fechaCalificacion
    Date fechaInicioPuja
    Date fechaFinPuja
    Date fechaAdjudicacion
    String estado
    String observaciones

    Double presupuestoReferencial = 0

    Date fechaAceptacionProveedor
    String memoRequerimiento

    Date fechaAperturaOfertas
    Date fechaInicioEvaluacionOferta
    Date fechaLimiteResultadosFinales

    Date fechaInicioPreparatorio
    Date fechaEtapa1
    Date fechaEtapa2
    Date fechaEtapa3
    Date fechaFinPreparatorio
    Date fechaInicioPrecontractual
    Date fechaFinPrecontractual
    Date fechaInicioContractual
    Date fechaFinContractual
    String numeroCertificacion

    Date fechaNotificacionAdjudicacion

    String memoSif

    String memoCertificacionFondos
    static auditable=true
    static mapping = {
        table 'cncr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'cncr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'cncr__id'

            obra column: 'obra__id'
            administracion column: 'admn__id'
            pac column: 'pacp__id'
            codigo column: 'cncrcdgo'
            objeto column: 'cncrobjt'
            costoBases column: 'cncrbase'
            porMilBases column: 'cncrpmbs'

            fechaInicio column: 'cncrfcin'
            fechaPublicacion column: 'cncrfcpu'
            fechaLimitePreguntas column: 'cncrfcpg'
            fechaLimiteRespuestas column: 'cncrfcrp'
            fechaLimiteEntregaOfertas column: 'cncrfceo'
            fechaLimiteSolicitarConvalidacion column: 'cncrfcsc'
            fechaLimiteRespuestaConvalidacion column: 'cncrfcrc'
            fechaCalificacion column: 'cncrfccf'
            fechaInicioPuja column: 'cncrfcip'
            fechaFinPuja column: 'cncrfcfp'
            fechaAdjudicacion column: 'cncrfcad'
            estado column: 'cncretdo'
            observaciones column: 'cncrobsr'

            presupuestoReferencial column: 'cncrprrf'

            fechaAceptacionProveedor column: 'cncrfcap'
            memoRequerimiento column: 'cncrmmrq'

            fechaAperturaOfertas column: 'cncrfcao'
            fechaInicioEvaluacionOferta column: 'cncrfcvo'
            fechaLimiteResultadosFinales column: 'cncrfclr'

            fechaInicioPreparatorio column: 'cncrfipp'
            fechaFinPreparatorio column: 'cncrffpp'
            fechaInicioPrecontractual column: 'cncrfipc'
            fechaFinPrecontractual column: 'cncrffpc'
            fechaInicioContractual column: 'cncrfict'
            fechaFinContractual column: 'cncrffct'

            fechaEtapa1 column: 'cncrfce1'
            fechaEtapa2 column: 'cncrfce2'
            fechaEtapa3 column: 'cncrfce3'
            numeroCertificacion column: 'cncrnmct'

            fechaNotificacionAdjudicacion column: 'cncrfcna'


            memoSif column: 'cncrmmsf'
            memoCertificacionFondos column: 'cncrmmct'
        }
    }
    static constraints = {
        obra(blank: true, nullable: true)
        administracion(blank: true, nullable: true)
        pac(blank: true, nullable: true)
        codigo(blank: true, nullable: true, maxSize: 20)
        objeto(blank: true, nullable: true, maxSize: 255)
        costoBases(blank: true, nullable: true)
        porMilBases(blank: true, nullable: true)
        fechaInicio(blank: true, nullable: true)
        fechaPublicacion(blank: true, nullable: true)
        fechaLimitePreguntas(blank: true, nullable: true)
        fechaLimiteRespuestas(blank: true, nullable: true)
        fechaLimiteEntregaOfertas(blank: true, nullable: true)
        fechaLimiteSolicitarConvalidacion(blank: true, nullable: true)
        fechaLimiteRespuestaConvalidacion(blank: true, nullable: true)
        fechaCalificacion(blank: true, nullable: true)
        fechaInicioPuja(blank: true, nullable: true)
        fechaFinPuja(blank: true, nullable: true)
        fechaAdjudicacion(blank: true, nullable: true)
        estado(blank: true, nullable: true, maxSize: 1)
        observaciones(blank: true, nullable: true, maxSize: 127)

        fechaAceptacionProveedor(blank: true, nullable: true)
        memoRequerimiento(blank: true, nullable: true)

        fechaAperturaOfertas(blank: true, nullable: true)
        fechaInicioEvaluacionOferta(blank: true, nullable: true)
        fechaLimiteResultadosFinales(blank: true, nullable: true)

        fechaInicioPreparatorio(blank: true, nullable: true)
        fechaFinPreparatorio(blank: true, nullable: true)
        fechaInicioPrecontractual(blank: true, nullable: true)
        fechaFinPrecontractual(blank: true, nullable: true)
        fechaInicioContractual(blank: true, nullable: true)
        fechaFinContractual(blank: true, nullable: true)
        fechaEtapa1(blank: true, nullable: true)
        fechaEtapa2(blank: true, nullable: true)
        fechaEtapa3(blank: true, nullable: true)
        numeroCertificacion(blank: true, nullable: true, maxSize: 4)

        fechaNotificacionAdjudicacion(blank: true, nullable: true)
        memoSif(nullable: true,blank:true,size: 1..120)
        memoCertificacionFondos(nullable: true, blank: true, size: 1..20)
    }
}
