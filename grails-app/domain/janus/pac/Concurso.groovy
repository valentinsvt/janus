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
        }
    }
    static constraints = {
        obra(blank: true, nullable: true)
        administracion(blank: true, nullable: true)
        pac(blank: true, nullable: true)
        codigo(blank: true, nullable: true, maxSize: 15)
        objeto(blank: true, nullable: true, maxSize: 255)
        costoBases(blank: true, nullable: true)
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
    }
}
