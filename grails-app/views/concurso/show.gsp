<%@ page import="janus.pac.Concurso" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Proceso
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins', file: 'jquery.livequery.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.ui.position.js')}" type="text/javascript"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.js')}" type="text/javascript"></script>
        <link href="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.css')}" rel="stylesheet" type="text/css"/>
        <style>
        td {
            line-height : 12px !important;
        }

            .largo{
                width: 250px;
            }
        </style>
    </head>

    <body>
        <div class="span12 btn-group" role="navigation" style="margin-bottom: 10px;">
            <div class="row">
                <div class="span9 btn-group" role="navigation">
                    <g:link controller="concurso" action="list" class="btn">
                        <i class="icon-angle-left"></i> Proceso
                    </g:link>
                </div>
            </div>
        </div>

        <div id="show-concurso" class="span12" role="main">
            <form class="form-horizontal">
                <g:if test="${concursoInstance?.obra}">
                    <div class="control-group">
                        <div>
                            <span id="obra-label" class="control-label label label-inverse" style="width: 250px">
                                Obra
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="obra-label">
                                %{--<g:link controller="obra" action="show" id="${concursoInstance?.obra?.id}">--}%
                                ${concursoInstance?.obra?.descripcion}
                                %{--</g:link>--}%
                            </span>

                        </div>
                    </div>
                </g:if>

                <g:if test="${concursoInstance?.administracion}">
                    <div class="control-group">
                        <div>
                            <span id="administracion-label" class="control-label label label-inverse" style="width: 250px">
                                Administracion
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="administracion-label">
                                %{--<g:link controller="administracion" action="show" id="${concursoInstance?.administracion?.id}">--}%
                                ${concursoInstance?.administracion?.descripcion}
                                %{--</g:link>--}%
                            </span>

                        </div>
                    </div>
                </g:if>

                <g:if test="${concursoInstance?.pac}">
                    <div class="control-group">
                        <div>
                            <span id="pac-label" class="control-label label label-inverse" style="width: 250px">
                                Pac
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="pac-label">
                                %{--<g:link controller="pac" action="show" id="${concursoInstance?.pac?.id}">--}%
                                ${concursoInstance?.pac?.descripcion}
                                %{--</g:link>--}%
                            </span>

                        </div>
                    </div>
                </g:if>

                <g:if test="${concursoInstance?.codigo}">
                    <div class="control-group">
                        <div>
                            <span id="codigo-label" class="control-label label label-inverse" style="width: 250px">
                                Codigo
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="codigo-label">
                                <g:fieldValue bean="${concursoInstance}" field="codigo"/>
                            </span>

                        </div>
                    </div>
                </g:if>

                <g:if test="${concursoInstance?.objeto}">
                    <div class="control-group">
                        <div>
                            <span id="objeto-label" class="control-label label label-inverse" style="width: 250px" >
                                Objeto
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="objeto-label">
                                <g:fieldValue bean="${concursoInstance}" field="objeto"/>
                            </span>

                        </div>
                    </div>
                </g:if>

                <g:if test="${concursoInstance?.costoBases}">
                    <div class="control-group">
                        <div>
                            <span id="costoBases-label" class="control-label label label-inverse" style="width: 250px">
                                Costo Bases
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="costoBases-label">
                                <g:fieldValue bean="${concursoInstance}" field="costoBases"/>
                            </span>

                        </div>
                    </div>
                </g:if>
                <g:if test="${concursoInstance?.fechaInicio}">
                    <div class="control-group">
                        <div>
                            <span id="fechaInicio-label" class="control-label label label-inverse" style="width: 250px">
                                Fecha Inicio
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="fechaInicio-label">
                                <g:formatDate date="${concursoInstance?.fechaInicio}"/>
                            </span>

                        </div>
                    </div>
                </g:if>

                <g:if test="${concursoInstance?.fechaPublicacion}">
                    <div class="control-group">
                        <div>
                            <span id="fechaPublicacion-label" class="control-label label label-inverse" style="width: 250px">
                                Fecha Publicacion
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="fechaPublicacion-label">
                                <g:formatDate date="${concursoInstance?.fechaPublicacion}"/>
                            </span>

                        </div>
                    </div>
                </g:if>

                <g:if test="${concursoInstance?.fechaLimitePreguntas}">
                    <div class="control-group">
                        <div>
                            <span id="fechaLimitePreguntas-label" class="control-label label label-inverse" style="width: 250px">
                                Fecha Limite Preguntas
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="fechaLimitePreguntas-label">
                                <g:formatDate date="${concursoInstance?.fechaLimitePreguntas}" format="dd-MM-yyyy"/>
                            </span>

                        </div>
                    </div>
                </g:if>

                <g:if test="${concursoInstance?.fechaLimiteRespuestas}">
                    <div class="control-group">
                        <div>
                            <span id="fechaLimiteRespuestas-label" class="control-label label label-inverse" style="width: 250px">
                                Fecha Limite Respuestas
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="fechaLimiteRespuestas-label">
                                <g:formatDate date="${concursoInstance?.fechaLimiteRespuestas}" format="dd-MM-yyyy"/>
                            </span>

                        </div>
                    </div>
                </g:if>

                <g:if test="${concursoInstance?.fechaLimiteEntregaOfertas}">
                    <div class="control-group">
                        <div>
                            <span id="fechaLimiteEntregaOfertas-label" class="control-label label label-inverse" style="width: 250px">
                                Fecha Limite Entrega Ofertas
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="fechaLimiteEntregaOfertas-label">
                                <g:formatDate date="${concursoInstance?.fechaLimiteEntregaOfertas}" format="dd-MM-yyyy"/>
                            </span>

                        </div>
                    </div>
                </g:if>

                <g:if test="${concursoInstance?.fechaLimiteSolicitarConvalidacion}">
                    <div class="control-group">
                        <div>
                            <span id="fechaLimiteSolicitarConvalidacion-label" class="control-label label label-inverse" style="width: 250px">
                                Fecha Limite Solicitar Convalidacion
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="fechaLimiteSolicitarConvalidacion-label">
                                <g:formatDate date="${concursoInstance?.fechaLimiteSolicitarConvalidacion}" format="dd-MM-yyyy"/>
                            </span>

                        </div>
                    </div>
                </g:if>
                <g:if test="${concursoInstance?.fechaLimiteRespuestaConvalidacion}">
                    <div class="control-group">
                        <div>
                            <span id="fechaLimiteRespuestaConvalidacion-label" class="control-label label label-inverse largo" style="width: 250px">
                                Fecha Limite Respuesta Convalidacion
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="fechaLimiteRespuestaConvalidacion-label">
                                <g:formatDate date="${concursoInstance?.fechaLimiteRespuestaConvalidacion}" format="dd-MM-yyyy"/>
                            </span>

                        </div>
                    </div>
                </g:if>
                <g:if test="${concursoInstance?.fechaCalificacion}">
                    <div class="control-group">
                        <div>
                            <span id="fechaCalificacion-label" class="control-label label label-inverse" style="width: 250px">
                                Fecha Calificacion
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="fechaCalificacion-label">
                                <g:formatDate date="${concursoInstance?.fechaCalificacion}" format="dd-MM-yyyy"/>
                            </span>

                        </div>
                    </div>
                </g:if>

                <g:if test="${concursoInstance?.fechaInicioPuja}">
                    <div class="control-group">
                        <div>
                            <span id="fechaInicioPuja-label" class="control-label label label-inverse" style="width: 250px">
                                Fecha Inicio Puja
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="fechaInicioPuja-label">
                                <g:formatDate date="${concursoInstance?.fechaInicioPuja}" format="dd-MM-yyyy"/>
                            </span>

                        </div>
                    </div>
                </g:if>

                <g:if test="${concursoInstance?.fechaFinPuja}">
                    <div class="control-group">
                        <div>
                            <span id="fechaFinPuja-label" class="control-label label label-inverse" style="width: 250px">
                                Fecha Fin Puja
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="fechaFinPuja-label">
                                <g:formatDate date="${concursoInstance?.fechaFinPuja}" format="dd-MM-yyyy"/>
                            </span>

                        </div>
                    </div>
                </g:if>

                <g:if test="${concursoInstance?.fechaAdjudicacion}">
                    <div class="control-group">
                        <div>
                            <span id="fechaAdjudicacion-label" class="control-label label label-inverse" style="width: 250px">
                                Fecha Adjudicacion
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="fechaAdjudicacion-label">
                                <g:formatDate date="${concursoInstance?.fechaAdjudicacion}" format="dd-MM-yyyy"/>
                            </span>

                        </div>
                    </div>
                </g:if>

                <g:if test="${concursoInstance?.estado}">
                    <div class="control-group">
                        <div>
                            <span id="estado-label" class="control-label label label-inverse" style="width: 250px">
                                Estado
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="estado-label">
                                <g:fieldValue bean="${concursoInstance}" field="estado"/>
                            </span>

                        </div>
                    </div>
                </g:if>

                <g:if test="${concursoInstance?.observaciones}">
                    <div class="control-group">
                        <div>
                            <span id="observaciones-label" class="control-label label label-inverse" style="width: 250px">
                                Observaciones
                            </span>
                        </div>

                        <div class="controls">

                            <span aria-labelledby="observaciones-label">
                                <g:fieldValue bean="${concursoInstance}" field="observaciones"/>
                            </span>

                        </div>
                    </div>
                </g:if>

            </form>
        </div>
    </body>

</html>