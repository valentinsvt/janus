<%@ page import="janus.pac.Concurso; janus.Contrato; janus.pac.Oferta"%>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Procesos
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins', file: 'jquery.livequery.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.ui.position.js')}" type="text/javascript"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.js')}" type="text/javascript"></script>
        <link href="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.css')}" rel="stylesheet" type="text/css"/>
        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">

        <script src="${resource(dir: 'js/jquery/plugins/jquery-timepicker/js', file: 'jquery-ui-timepicker-addon.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-timepicker/i18n', file: 'jquery.ui.datetimepicker-es.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/jquery-timepicker/css', file: 'jquery-ui-timepicker-addon.css')}" rel="stylesheet">

        <style>
        td {
            line-height : 12px !important;
        }

        .row {
            height : 35px;;
        }

        .tab-content {
            padding : 10px;
            border  : solid 1px #555;
        }

        #myTab {
            margin-bottom : 0;
            border        : solid 1px #555;
        }
        </style>
    </head>

    <body>

        <g:if test="${flash.message}">
            <div class="span12">
                <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                    <a class="close" data-dismiss="alert" href="#">×</a>
                    ${flash.message}
                </div>
            </div>
        </g:if>
        <input type="hidden" id="con_id" value="${concursoInstance?.id}">

        <div class="row" style="margin-bottom: 10px;">
            <div class="span9 btn-group" role="navigation">
                <g:link controller="concurso" action="list" class="btn">
                    <i class="icon-angle-left"></i> Regresar
                </g:link>
            %{--<input type="SUBMIT" value="Guardar" class="btn btn-primary">--}%
                <g:if test="${concursoInstance.estado != 'R'}">
                    <a href="#" class="btn btn-success" id="btnSave">
                        <i class="icon-save"></i> Guardar
                    </a>
                </g:if>

                <g:if test="${Oferta.countByConcurso(concursoInstance) == 0}">
                        <a href="#" class="btn" id="btnRegi"><i class="icon-exchange"></i> Cambiar Estado</a>
                </g:if>
            </div>
        </div>


        <div style="border-bottom: 1px solid black;padding-left: 50px;position: relative;height: 150px;margin-bottom: 10px;">
            <p class="css-vertical-text" style="font-size: 30px;margin-left: -7px"><i class="icon-arrow-left active" id="min" style="cursor: pointer" title="Ocultar"></i> <span id="msg" title="Ocultar" style="cursor: pointer">P.A.C.</span>
            </p>

            <div class="linea" style="height: 100%"></div>

            <div class="row " id="mostrar" style="display: none;cursor: pointer">
                <div class="span10">
                    <b>Ver P.A.C.</b>
                </div>

            </div>

            <div class="row header">
                <div class="span10">
                    <span class="control-label label label-inverse span2" style="width: 135px;">
                        Tipo Procedimiento:
                    </span>

                    <div class="controls span2">
                        ${concursoInstance?.pac?.tipoProcedimiento?.descripcion}
                    </div>
                    <span class="control-label label label-inverse span2" style="width: 100px;">
                        Tipo Compra:
                    </span>

                    <div class="controls span1">
                        ${concursoInstance?.pac?.tipoCompra?.descripcion}
                    </div>
                    <span class="control-label label label-inverse span1">
                        Código cp:
                    </span>

                    <div class="controls span1" title=" ${concursoInstance?.pac?.cpp?.descripcion}">
                        ${concursoInstance?.pac?.cpp?.numero}
                    </div>

                </div>
            </div>

            <div class="row header">
                <div class="span10">
                    <span class="control-label label label-inverse span2" style="width: 135px;">
                        Partida:
                    </span>

                    <div class="controls span7" title="">
                        ${concursoInstance?.pac?.presupuesto?.numero} (${concursoInstance?.pac?.presupuesto?.descripcion})
                    </div>
                </div>

            </div>

            <div class="row header">
                <div class="span10">
                    <span class="control-label label label-inverse span1">
                        Descripción:
                    </span>

                    <div class="controls span8" title="">
                        ${concursoInstance?.pac?.descripcion}
                    </div>

                </div>

            </div>

            <div class="row header">
                <div class="span10">
                    <span class="control-label label label-inverse span1">
                        Cantidad:
                    </span>

                    <div class="controls span1" title="">
                        ${concursoInstance?.pac?.cantidad}
                    </div>
                    <span class="control-label label label-inverse span1">
                        Unidad:
                    </span>

                    <div class="controls span1" title="">
                        ${concursoInstance?.pac?.unidad?.descripcion}
                    </div>
                    <span class="control-label label label-inverse span1">
                        Precio U.:
                    </span>

                    <div class="controls span1" title="">
                        <g:formatNumber number="${concursoInstance?.pac?.costo.round(2)}" type="currency"></g:formatNumber>

                    </div>
                    <span class="control-label label label-inverse span1">
                        Total:
                    </span>

                    <div class="controls span1" title="">
                        <g:formatNumber number="${(concursoInstance?.pac?.costo * concursoInstance?.pac?.cantidad).round(2)}" type="currency"></g:formatNumber>

                    </div>
                </div>
            </div>
        </div>

        <div style="border-bottom: 1px solid black;padding-left: 50px;position: relative;margin-bottom: 10px;">
            <p class="css-vertical-text">Proceso de contratación</p>

            <div class="linea" style="height: 100%"></div>

            <g:form class="form-horizontal" name="frmSave-Concurso" action="save" id="${concursoInstance?.id}">
                <ul class="nav nav-pills ui-corner-top" id="myTab">
                    <li class="active"><a href="#datos">Datos proceso</a></li>
                    <li><a href="#fechas">Fechas del proceso</a></li>
                    <li><a href="#fechas2">Fechas de control del trámite</a></li>
                </ul>

                <div class="tab-content ui-corner-bottom">
                    <div class="tab-pane active" id="datos">
                        <div class="row">

                            <div class="span5" style="width: 600px;">
                                <span class="control-label label label-inverse">
                                    Prefecto
                                </span>

                                <div style="width: 400px;" class="controls">
                                    <g:hiddenField name="administracion.id" value="${concursoInstance?.administracion?.id}"/>
                                    ${concursoInstance?.administracion?.nombrePrefecto}
                                </div>
                            </div>

                            <div class="span5">
                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Estado
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <g:hiddenField name="estado" value="${concursoInstance?.estado}"/>
                                        ${concursoInstance?.estado == 'R' ? 'Registrado' : 'No registrado'}
                                    </div>
                                </div>
                            </div>


                            <div class="span10">
                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Objeto
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <g:textArea name="objeto" class="span8" value="${concursoInstance?.objeto}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>
                            </div>

                            <div class="span5" style="width: 600px;">

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Número de certificación:
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <g:textField name="numeroCertificacion" value="${concursoInstance?.numeroCertificacion ?: '0000'}" style="width: 50px;"
                                                     maxlength="4"/> (Número de certificación de disponibilidad de fondos)
                                    </div>
                                </div>




                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Memo Certificación Fondos
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <div class="input-append">
                                            <g:textField name="memoCertificacionFondos" value="${concursoInstance?.memoCertificacionFondos}" style="text-align: left;width: 180px;" class="allCaps"/>
                                        </div>

                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>


                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Memo SIF.
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <g:textField name="memoSif" value="${concursoInstance?.memoSif}" class="allCaps"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Obra requerida
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <input type="hidden" id="obra_id" name="obra.id" value="${concursoInstance?.obra?.id}">
                                        <input type="text" id="obra_busqueda" value="${concursoInstance?.obra?.codigo}" title="${concursoInstance?.obra?.nombre}" style="width: 400px;">
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                            </div> <!-- fin col 1-->

                            <div class="span5">

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Código
                                        </span>
                                    </div>

                                    <div class="controls">
                                        %{--
                                        <g:textField name="codigo" class="" value="${concursoInstance?.codigo}"/>
                                        --}%
                                        %{--<span class="uneditable-input">${concursoInstance?.codigo}</span>--}%
                                        <g:textField name="codigo" value="${concursoInstance?.codigo}" maxlength="20" class="allCaps required"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Costo Bases
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <input type="radio" name="costo" id="rbt_costoBases"
                                            ${(concursoInstance.costoBases > 0 || (concursoInstance.costoBases == 0 && concursoInstance.porMilBases == 0) || !concursoInstance.costoBases || !concursoInstance.porMilBases) ? "checked" : ""}/>
                                        <g:field type="number" name="costoBases" class="input-mini" value="${concursoInstance.costoBases == null ? 200 : concursoInstance.costoBases}"/>
                                        <input type="radio" name="costo" id="rbt_porMilBases" ${concursoInstance.porMilBases > 0 ? "checked" : ""}/>
                                        <g:field type="number" name="porMilBases" class="input-mini" value="${concursoInstance.porMilBases ?: 0}"/> x 1000
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                            <div class="control-group">
                                <div>
                                    <span class="control-label label label-inverse">
                                        Memo de requerimiento
                                    </span>
                                </div>

                                <div class="controls">
                                    <g:textField name="memoRequerimiento" value="${concursoInstance?.memoRequerimiento}" class="allCaps"/>
                                    <p class="help-block ui-helper-hidden allCaps"></p>
                                </div>
                            </div>


                            <div class="control-group">
                                <div>
                                    <span class="control-label label label-inverse">
                                        Monto referencial
                                    </span>
                                </div>

                                <div class="controls">
                                    <div class="input-append">
                                        <g:field type="text" name="presupuestoReferencial" class="required number" value="${g.formatNumber(number:concursoInstance?.presupuestoReferencial ?: 0, format: '##0.00',maxFractionDigits: 2,minFractionDigits: 2)}" style="text-align: right;width: 180px;"/>
                                        <span class="add-on">$</span>
                                    </div>

                                    <p class="help-block ui-helper-hidden"></p>
                                </div>
                            </div>



                            </div> <!-- fin col 2-->

                            <div class="span10">
                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Observaciones
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <g:textField name="observaciones" class="span8" value="${concursoInstance?.observaciones}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div> <!-- fin tab datos -->

                    <div class="tab-pane" id="fechas">
                        %{--<div id="cols" style="float: left;">--}%

                        <g:set var="minHour" value="${8}"/>
                        <g:set var="maxHour" value="${17}"/>
                        <g:set var="stepMin" value="${10}"/>

                        <div class="row">
                            <div class="span5">
                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Publicación
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false" name="fechaPublicacion" class="" value="${concursoInstance?.fechaPublicacion}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Aceptación proveedor
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false"name="fechaAceptacionProveedor" class="" value="${concursoInstance?.fechaAceptacionProveedor}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Límite Preguntas
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false"name="fechaLimitePreguntas" class="" value="${concursoInstance?.fechaLimitePreguntas}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Límite Respuestas
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false"name="fechaLimiteRespuestas" class="" value="${concursoInstance?.fechaLimiteRespuestas}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Límite Entrega Ofertas
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false"name="fechaLimiteEntregaOfertas" class="" value="${concursoInstance?.fechaLimiteEntregaOfertas}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Apertura Ofertas
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false"name="fechaAperturaOfertas" class="" value="${concursoInstance?.fechaAperturaOfertas}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Solicitar Convalidación
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false"name="fechaLimiteSolicitarConvalidacion" class="" value="${concursoInstance?.fechaLimiteSolicitarConvalidacion}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Recibir Convalidación
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false"name="fechaLimiteRespuestaConvalidacion" class="" value="${concursoInstance?.fechaLimiteRespuestaConvalidacion}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>
                            </div> <!-- fin col 1 -->

                            <div class="span5">

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Inicio Evaluación Oferta
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false"name="fechaInicioEvaluacionOferta" class="" value="${concursoInstance?.fechaInicioEvaluacionOferta}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Límite resultados finales
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false"name="fechaLimiteResultadosFinales" class="" value="${concursoInstance?.fechaLimiteResultadosFinales}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Adjudicación
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false"name="fechaAdjudicacion" class="" value="${concursoInstance?.fechaAdjudicacion}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Inicio
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false"name="fechaInicio" class="" value="${concursoInstance?.fechaInicio}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Calificación
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false"name="fechaCalificacion" class="" value="${concursoInstance?.fechaCalificacion}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Inicio Puja
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false"name="fechaInicioPuja" class="" value="${concursoInstance?.fechaInicioPuja}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Fin Puja
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false"name="fechaFinPuja" class="" value="${concursoInstance?.fechaFinPuja}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Notificación adjudicación
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datetimepicker showTime="false"name="fechaNotificacionAdjudicacion" class="" value="${concursoInstance?.fechaNotificacionAdjudicacion}" controlType="select" minHour="${minHour}" maxHour="${maxHour}" stepMinute="${stepMin}"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                            </div> <!-- fin col 2-->
                        </div>
                    </div> <!-- fin tab fechas -->

                    <div class="tab-pane" id="fechas2">
                        %{--<div id="cols" style="float: left;">--}%
                        <div class="row" style="margin: 10px;">
                            <div class="span5">
                                <a href="#" id="tramites" class="btn btn-primary">
                                    <i class="icon-search"></i> Ver tramites S.A.D.
                                </a>
                            </div>
                        </div>

                        <fielset id="desc_prep" style="padding-bottom: 10px;border: 1px solid #000000;width: 95%;float: left;margin-left: 33px;padding: 10px;margin-bottom: 20px;" class="ui-corner-all">
                            <legend style="color:#0088CC;border-color: #0088CC;cursor: pointer" id="label_prep" class="active" title="Minimizar">Etapa Preparatoria</legend>

                            <div class="row" style="">
                                <div class="span6">
                                    <div class="control-group">
                                        <div>
                                            <span class="control-label label label-inverse">
                                                Fecha Inicio Preparatorio
                                            </span>
                                        </div>

                                        <div class="controls">
                                            <elm:datepicker name="fechaInicioPreparatorio" class="" value="${concursoInstance?.fechaInicioPreparatorio ?: concursoInstance?.fechaPublicacion}" style="width:130px;float: left"/>
                                            <g:if test="${concursoInstance?.fechaInicioPreparatorio == null}">
                                                <a class="btn btn-small btn-primary btn-ajax" href="#" rel="tooltip" title="Empezar preparatorio" id="inicio_prep" style="margin-left: 5px;">
                                                    <i class="icon-check"></i>
                                                </a>
                                            </g:if>
                                            <div id="info_prep" style="width: 200px;float: left;margin-left: 10px;">
                                                <span style="color: ${(duracionPrep < maxPrep) ? 'green' : 'red'}">
                                                    <g:if test="${concursoInstance.fechaInicioPreparatorio != null}">

                                                        <g:if test="${duracionPrep < maxPrep}">
                                                            <g:if test="${maxPrep - duracionPrep < 2}">
                                                                <div class="amarillo"></div>
                                                            </g:if>
                                                            <g:else>
                                                                <div class="verde"></div>
                                                            </g:else>
                                                        </g:if>
                                                        <g:else>
                                                            <g:set var="retraso" value="- ${((new Date()) - (concursoInstance?.fechaInicioPreparatorio + maxPrep))} días de retraso"></g:set>
                                                            <div class="rojo" title="Retrasado"></div>
                                                        </g:else>
                                                        <g:if test="${concursoInstance?.fechaFinPreparatorio == null}">
                                                            En curso ${retraso}
                                                        </g:if>
                                                        <g:else>
                                                            Terminado
                                                        </g:else>

                                                    </g:if>
                                                </span>
                                            </div>

                                            <p class="help-block ui-helper-hidden"></p>
                                        </div>
                                    </div>
                                </div>

                                <div class="span4">
                                    <div class="control-group">
                                        <div>
                                            <span class="control-label label label-inverse">
                                                Fecha Fin Preparatorio
                                            </span>
                                        </div>

                                        <div class="controls">
                                            <elm:datepicker name="fechaFinPreparatorio" class="" value="${concursoInstance?.fechaFinPreparatorio}" style="width:130px;"/>
                                            <p class="help-block ui-helper-hidden"></p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="span11 hide_prep" style="height: 20px;margin: 10px;margin-left: 30px;margin-bottom: 20px;">
                                <div class="span3" style="background: ${(concursoInstance?.fechaEtapa1 != null) ? '#feff6d' : 'gray'};margin: 0px;height: 15px;border-right: 2px solid black;text-align: center;font-weight: bold">Etapa 1 ${concursoInstance?.fechaEtapa1?.format("dd-MM-yyyy")}</div>

                                <div class="span5" style="background:  ${(concursoInstance?.fechaEtapa2 != null) ? '#feff6d' : 'gray'};margin: 0px;height: 15px;border-right: 2px solid black;text-align: center;font-weight: bold">Etapa 2 ${concursoInstance?.fechaEtapa2?.format("dd-MM-yyyy")}</div>

                                <div class="span3" style="background:  ${(concursoInstance?.fechaEtapa3 != null) ? '#feff6d' : 'gray'};margin: 0px;height: 15px;border-right: 2px solid black;text-align: center;font-weight: bold">Etapa 3 ${concursoInstance?.fechaEtapa3?.format("dd-MM-yyyy")}</div>
                            </div>
                            <fieldset class="span11 ui-corner-all hide_prep" id="seguimiento" style="padding: 0px;margin-left: 0px;">
                                <legend style="border:none;background: none;">Seguimiento del tramite</legend>
                            </fieldset>

                        </fielset>

                        <div class="row">
                            <div class="span6">

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Fecha Inicio Precontractual
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datepicker name="fechaInicioPrecontractual" class="" value="${concursoInstance?.fechaInicioPrecontractual}" style="width:130px;"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Fecha Inicio Contractual
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datepicker name="fechaInicioContractual" class="" value="${concursoInstance?.fechaInicioContractual}" style="width:130px;"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                            </div> <!-- fin col 1 -->

                            <div class="span5">

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Fecha Fin Precontractual
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datepicker name="fechaFinPrecontractual" class="" value="${concursoInstance?.fechaFinPrecontractual}" style="width:130px;"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <div>
                                        <span class="control-label label label-inverse">
                                            Fecha Fin Contractual
                                        </span>
                                    </div>

                                    <div class="controls">
                                        <elm:datepicker name="fechaFinContractual" class="" value="${concursoInstance?.fechaFinContractual}" style="width:130px;"/>
                                        <p class="help-block ui-helper-hidden"></p>
                                    </div>
                                </div>

                            </div> <!-- fin col 2-->

                        </div>
                    </div> <!-- fin tab fechas2 -->

                </div>
            </g:form>

        </div>

        <div class="modal grandote hide fade" id="modal-busqueda" style="overflow: hidden">
            <div class="modal-header btn-info">
                <button type="button" class="close" data-dismiss="modal">x</button>

                <h3 id="modalTitle_busqueda"></h3>

            </div>

            <div class="modal-body" id="modalBody">
                <bsc:buscador name="obras" value="" accion="buscarObra" controlador="concurso" campos="${campos}" label="Obras" tipo="lista"/>

            </div>

            <div class="modal-footer" id="modalFooter_busqueda">

            </div>

        </div>

        <div class="modal grandote hide fade " id="modal-tramite" style=";overflow: hidden;">
            <div class="modal-header btn-info">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle_tramite">Tramites</h3>
            </div>

            <div class="modal-body" id="modal-tramite-body">

            </div>

            <div class="modal-footer" id="modalTramite_busqueda">
            </div>
        </div>

        <script type="text/javascript">
            function cargarDatos() {

                $.ajax({type : "POST", url : "${g.createLink(controller: 'concurso',action:'datosObra')}",
                    data     : "obra=" + $("#obra_id").val(),
                    success  : function (msg) {
                        //console.log(msg)
                        var parts = msg.split("&&")
                        //console.log(parts,number_format(parts[3], 2, ".", " "))
                        $("#presupuestoReferencial").val(parts[3])
                        $("#obra_busqueda").val(parts[0]).attr("title", parts[1])

                    }
                });
            }

/*
            function seguimiento() {
                $.ajax({
                    type    : "POST",
                    url     : "${g.createLink(action:'seguimiento',controller: 'tramite',id: concursoInstance?.memoRequerimiento)}",
                    success : function (msg) {
                        $("#seguimiento").html(msg)
                    }
                });
            }
            seguimiento();
*/
            $('#myTab a').click(function (e) {
                e.preventDefault();
                $(this).tab('show');
            });

            $("#label_prep").click(function () {
                if ($(this).hasClass("active")) {
                    $(".hide_prep").hide()
                    $(this).removeClass("active")
                    $(this).attr("title", "Maximizar")
                } else {
                    $(".hide_prep").show("slide")
                    $(this).addClass("active")
                    $(this).attr("title", "Minimizar")
                }

            })

            $("[name=costo]").click(function () {
                var id = $(this).attr("id");
                var p = id.split("_");
                id = p[1];
                if (id == "porMilBases") {
                    $("#costoBases").val(0);
                } else {
                    $("#porMilBases").val(0);
                }
            });

            $("#inicio_prep").click(function () {
                var memo = $("#memoRequerimiento").val().trim()
                var fecha = $("#fechaInicioPreparatorio").val()
                var error = ""
                if (memo == "") {
                    error = "<br>Error: Primero ingrese un memorando de requerimiento"
                }
                if (fecha == "") {
                    error += "<br>Error: Seleccione una fecha de inicio"
                }

                if (error == "") {
                    $.ajax({
                        type    : "POST",
                        url     : "${g.createLink(action:'iniciarPreparatorio',controller: 'concurso')}",
                        data    : "id=${concursoInstance?.id}&memo=" + memo + "&fecha=" + fecha,
                        success : function (msg) {
                            if (msg == "ok") {
                                window.location.reload(true)
                            }
                        }
                    });
                } else {
                    $.box({
                        imageClass : "box_info",
                        text       : error,
                        title      : "Errores",
                        iconClose  : false,
                        dialog     : {
                            resizable : false,
                            draggable : false,
                            buttons   : {
                                "Aceptar" : function () {
                                }
                            },
                            width     : 500
                        }
                    });
                }

            });

            $("#frmSave-Concurso").validate({
                errorPlacement : function (error, element) {
//                    console.log(error)
                    element.parent().find(".help-block").html(error).show();
                },
                success        : function (label) {
                    label.parent().hide();
                },
                errorClass     : "label label-important",
                submitHandler  : function (form) {
                    $(".btn-success").replaceWith(spinner);
                    form.submit();
                }
            });

            $("#min").click(function () {
                if ($(this).hasClass("active")) {
                    $(".header").hide("slide");
                    $("#msg").hide();
                    $("#min").removeClass("icon-arrow-left").removeClass("active").addClass("icon-arrow-right");
                    $(this).attr("title", "Mostrar");
                    $(this).parent().parent().animate({
                        height : 35
                    });
                    $("#mostrar").show()
                } else {
                    $(".header").show("slide");
                    $("#msg").show();
                    $("#min").removeClass("icon-arrow-right").addClass("active").addClass("icon-arrow-left");
                    $(this).attr("title", "Ocultar");
                    $("#mostrar").hide("");
                    $(this).parent().parent().animate({
                        height : 150
                    })
                }

            });
            $("#msg").click(function () {
                $("#min").click();
            });
            $("#mostrar").click(function () {
                $("#min").click();
            });

            $("#obra_busqueda").dblclick(function () {
                var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                $("#modalTitle_busqueda").html("Lista de obras");
                $("#modalFooter_busqueda").html("").append(btnOk);
                $("#modal-busqueda").modal("show");
                $("#contenidoBuscador").html("")
            });

            $("#btnSave").click(function () {
//                console.log("aaa")
                $("#frmSave-Concurso").submit();
            });

            $("input").keyup(function (ev) {
                if (ev.keyCode == 13) {
                    submitForm($(".btn-success"));
                }
            });

            $("#btnRegi").click(function () {
                var obraId = $.trim($("#obra_id").val());
                if (obraId != "") {
                    var esta = $("#estado").val();
                    if (esta == 'R') {
                        $("#estado").val("N");
                    } else {
                        $("#estado").val("R");
                    }
                    $("#frmSave-Concurso").submit();
//                    console.log( $("#frmSave-Concurso"))
                } else {
                    alert("Seleccione una obra!");
                }

            });
            $("#tramites").click(function () {
                $.ajax({
                    type    : "POST",
                    url     : "${g.createLink(action:'verTramitesAjax',controller: 'tramite',id: concursoInstance?.memoRequerimiento)}",
                    success : function (msg) {
                        $("#modal-tramite-body").html(msg);
                        $("#modal-tramite").modal("show");
                    }
                });
            })

        </script>
    </body>
</html>
