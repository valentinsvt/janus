<%@ page import="janus.ejecucion.TipoPlanilla" contentType="text/html;charset=UTF-8" %>
<html>
<head>

    <meta name="layout" content="main">
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">

    <script src="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.css')}" rel="stylesheet"/>
    <link href="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.customThemes.css')}" rel="stylesheet"/>

    <style type="text/css">

    .formato {
        font-weight : bolder;
    }

    tr.info td {
        background : rgba(91, 123, 179, 0.46) !important;
    }

    .navbar .nav > li > a {
        padding : 10px 10px !important;
    }
    </style>

    <title>Ver Contrato</title>
</head>

<body>

<g:if test="${flash.message}">
    <div class="span12" style="margin-bottom: 10px;">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </div>
</g:if>

<div class="span12 hide" style="margin-bottom: 10px;" id="divError">
    <div class="alert alert-error" role="status">
        <a class="close" data-dismiss="alert" href="#">×</a>
        <span id="spanError"></span>
    </div>
</div>

<div class="span12 hide" style="margin-bottom: 10px;" id="divOk">
    <div class="alert alert-info" role="status">
        <a class="close" data-dismiss="alert" href="#">×</a>
        <span id="spanOk"></span>
    </div>
</div>


<div class="row">
    <div class="span12 btn-group" role="navigation" style="margin-left: 0;width: 100%;height: 35px;">
        <button class="btn" id="btn-lista"><i class="icon-book"></i> Lista</button>
        <g:if test="${contrato?.id}">
            <g:link controller="documentoProceso" class="btn" action="list" id="${contrato?.oferta?.concursoId}" params="[contrato: contrato?.id]">
                <i class="icon-book"></i> Biblioteca
            </g:link>

            <g:link controller="garantia" class="btn" action="garantiasContrato" id="${contrato?.id}">
                <i class="icon-pencil"></i> Garantías
            </g:link>
            <a href="${g.createLink(controller: 'contrato', action: 'polinomicaContrato', id: contrato?.id)}" class="btn">
                <i class="icon-superscript"></i> Fórmula Polinómica
            </a>

        </g:if>

    %{--
                    <g:if test="${janus.ejecucion.Planilla.countByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo('A')) > 0 && contrato.oferta.concurso.obra.fechaInicio}">
                        <g:link controller="cronogramaEjecucion" class="btn" action="creaCronogramaEjec" id="${contrato?.id}">
                            <i class="icon-th"></i> Cronograma
                        </g:link>
                    </g:if>
    --}%

        <g:if test="${janus.ejecucion.Planilla.countByContratoAndTipoPlanilla(contrato, TipoPlanilla.findByCodigo('A')) > 0 && contrato.oferta.concurso.obra.fechaInicio}">
            <g:link controller="cronogramaEjecucion" class="btn btn-info" action="creaCrngEjecNuevo" id="${contrato?.id}">
                <i class="icon-th"></i> Cronograma
            </g:link>
        </g:if>

        <g:if test="${contrato?.id}">
            <a href="#" class="btn  " id="imprimir_sub">
                <i class="icon-print"></i>
                Imp. Presupuesto
            </a>
            <a href="#" class="btn  " id="btnRubros">
                <i class="icon-print"></i>
                Imp. Rubros y VAE
            </a>
            <g:if test="${contrato?.anticipo == 0}">
                <a href="#" class="btn btn-warning" id="inicioObra">
                    <i class="icon-check text_info"></i>
                    Inicio de Obra
                </a>
            </g:if>
        </g:if>

    </div>
</div>


<g:form class="registroContrato" name="frm-registroContrato" action="save">
    <g:hiddenField name="id" value="${contrato?.id}"/>
    <fieldset class="" style="position: relative; border-bottom: 1px solid black; width: 100%;">
        <div class="span12" style="margin-top: 10px">
            <g:if test="${contrato?.codigo != null}">
                <div class="span2 formato">Contrato N°</div>

                <div class="span3">${contrato?.codigo}</div>

                <div class="span2 formato">Memo de Distribución</div>

                <div class="span3">${contrato?.memo}</div>
            %{--</div>--}%
            </g:if>

            <g:else>
                <div class="span2 formato">Contrato N°</div>

                <div class="span3">${contrato?.codigo}</div>

                <div class="span2 formato">Memo de Distribución</div>

                <div class="span3">${contrato?.memo}</div>

            </g:else>
        </div> <!--DSAFSD-->
    </fieldset>


    <fieldset class="" style="position: relative; padding: 10px;border-bottom: 1px solid black;">
        <p class="css-vertical-text">Contratación</p>

        <div class="linea" style="height: 85%;"></div>

    %{--<g:hiddenField name="oferta" class="oferta" value="${contrato?.oferta?.id}"/>--}%

        <g:if test="${contrato?.codigo != null}">
            <div class="span12">
                <div class="span1 formato">Obra</div>

                <div class="span10 formato">${contrato?.oferta?.concurso?.obra?.codigo} - ${contrato?.oferta?.concurso?.obra?.nombre}
                (${contrato?.oferta?.concurso?.obra?.departamento?.direccion?.nombre})</div>
            </div>

            <div class="span12" style="margin-top: 5px">
                <div class="span1 formato">Parroquia</div>

                <div class="span4">${contrato?.oferta?.concurso?.obra?.parroquia?.nombre}</div>

                <div class="span1 formato">Cantón</div>

                <div class="span2">${contrato?.oferta?.concurso?.obra?.parroquia?.canton?.nombre}</div>
            </div>

            <div class="span12" style="margin-top: 5px">
                <div class="span1 formato">Comunidad</div>

                <div class="span4">${contrato?.oferta?.concurso?.obra?.comunidad?.nombre}</div>

                <div class="span1 formato">Sitio</div>

                <div class="span5">${contrato?.oferta?.concurso?.obra?.sitio}</div>
            </div>

            <div class="span12" style="margin-top: 5px">
                <div class="span1 formato">Localidad</div>

                <div class="span4">${contrato?.oferta?.concurso?.obra?.barrio}</div>

            </div>

            <div class="span12" style="margin-top: 5px">
                <div class="span1 formato">Clase Obra</div>

                <div class="span4">${contrato?.oferta?.concurso?.obra?.claseObra?.descripcion}</div>
            </div>

            <div class="span12" style="margin-top: 5px">
                <div class="span1 formato">Contratista</div>

                <div class="span4">${contrato?.oferta?.proveedor?.nombre}</div>

                <div class="span1 formato">Estado</div>

                <div class="span2">${(contrato.estado == "R") ? "Registrado" : "No registrado"}</div>
            </div>
        </g:if>

        <g:else>
            <div class="span12" style="margin-top: 5px">
                <div class="span2 formato">Obra</div>

                <div class="span3">
                    ${contrato?.oferta?.concurso?.obra?.codigo}
                </div>

                <div class="span1 formato">Nombre</div>

                <div class="span5">
                    ${contrato?.oferta?.concurso?.obra?.nombre}
                </div>
            </div>

            <div class="span12" style="margin-top: 5px">
                <div class="span2 formato">Oferta</div>

                <div class="span3" id="div_ofertas">
                    ${contrato?.oferta?.descripcion}
                </div>
            </div>

            <div class="span12" style="margin-top: 5px">
                <div class="span2 formato">Contratista</div>

                <div class="span3">
                    ${contrato?.oferta?.proveedor?.nombreContacto ? (contrato?.oferta?.proveedor?.nombreContacto + " " + contrato?.oferta?.proveedor?.apellidoContacto) : contrato?.oferta?.proveedor?.nombre}
                </div>
            </div>

            <div class="span12" style="margin-top: 5px">
                <div class="span2 formato">Parroquia</div>

                <div class="span3">${contrato?.oferta?.concurso?.obra?.parroquia?.nombre}</div>

                <div class="span1 formato">Cantón</div>

                <div class="span2">${contrato?.oferta?.concurso?.obra?.parroquia?.canton?.nombre}</div>
            </div>

            <div class="span12" style="margin-top: 5px">
                <div class="span2 formato">Clase Obra</div>

                <div class="span3">${contrato?.oferta?.concurso?.obra?.claseObra?.descripcion}</div>
            </div>

            <div class="span12" style="margin-top: 5px">
            </div>
        </g:else>
    </fieldset>

    <fieldset class="" style="position: relative;  border-bottom: 1px solid black;padding: 10px;">
        <div class="span12" style="margin-top: 10px">
            <g:if test="${contrato?.tipoContrato?.codigo?.trim() == 'C'}">
                <div class="span1 formato text-info">Tipo</div>
                <div class="span4 text-info">${contrato?.tipoContrato?.descripcion}</div>
            </g:if>
            <g:else>
                <div class="span1 formato">Tipo</div>
                <g:if test="${complementario}">
                    <div class="span2">${contrato?.tipoContrato?.descripcion}</div>
                </g:if>
                <g:else>
                    <div class="span4">${contrato?.tipoContrato?.descripcion}</div>
                </g:else>
            </g:else>
            <g:if test="${complementario}">
                <div class="text-info span3" style="margin-left: -40px">
                    <strong>Complementario: ${complementario?.codigo}</strong>
                </div>
            </g:if>

            <div class="span2 formato">Fecha de Suscripción</div>
            <div class="span2">${contrato?.fechaSubscripcion?.format("dd-MM-yyyy")}</div>
            <div class="span1 formato">Indirectos</div>
            <div class="span1">${contrato?.indirectos?:'__'}%</div>
        </div>

        <div class="span12" style="margin-top: 5px">
            <g:if test="${contrato?.tipoContrato?.codigo?.trim() == 'C'}">
                <div class="span2 formato text-info" style="margin-left:0px;">Objeto del Contrato</div>
                <div class="span10 text-info" style="margin-left: -40px">${contrato?.objeto}</div>
            </g:if>
            <g:else>
                <div class="span2 formato" style="margin-left:0px;">Objeto del Contrato</div>
                <div class="span10" style="margin-left: -40px">${contrato?.objeto}</div>
            </g:else>
        </div>

        <div class="col-md-5" style="margin-top: 5px">
            <div class="span2 formato" style="margin-left:60px;">Aplica Reajuste</div>
            <div class="span2" style="margin-left: -60px">${contrato?.aplicaReajuste == 1 ? 'SI' : 'NO'}</div>
        </div>

        <div class="col-md-5" style="margin-top: 5px">
            <div class="span4 formato" style="margin-left:10px;">La multa por retraso de obra incluye el valor del reajuste</div>
            <div class="span2" style="margin-left: -30px">${contrato?.conReajuste == 1 ? 'SI' : 'NO'}</div>
        </div>

        <div class="col-md-5" style="margin-top: 5px">
            <div class="span3 formato" style="margin-left:10px;">Aplica multa al saldo por planillar</div>
            <div class="span1" style="margin-left: -60px">${contrato?.saldoMulta == 1 ? 'SI' : 'NO'}</div>
        </div>

    </fieldset>

    <fieldset class="" style="position: relative;  padding: 10px;border-bottom: 1px solid black;">
        <div class="span12" style="margin-top: 10px">
            <div class="span3 formato">Multa por retraso</div>

            <div class="span2">
                ${g.formatNumber(number: contrato?.multaRetraso, maxFractionDigits: 0, minFractionDigits: 0, format: '##,##0', locale: 'ec')} x 1000
            </div>

            <div class="span4 formato">Multa por no presentación de planilla</div>

            <div class="span2">
                ${g.formatNumber(number: contrato?.multaPlanilla, maxFractionDigits: 0, minFractionDigits: 0, format: '##,##0', locale: 'ec')} x 1000
            </div>
        </div>

        <div class="span12" style="margin-top: 10px">
            <div class="span3 formato">Multa por incumplimiento del cronograma</div>

            <div class="span2">
                ${g.formatNumber(number: contrato?.multaIncumplimiento, maxFractionDigits: 0, minFractionDigits: 0, format: '##,##0', locale: 'ec')} x 1000
            </div>

            <div class="span4 formato">Multa por no acatar disposiciones del fiscalizador</div>

            <div class="span2">
                ${g.formatNumber(number: contrato?.multaDisposiciones, maxFractionDigits: 0, minFractionDigits: 0, format: '##,##0', locale: 'ec')} x 1000
            </div>
        </div>

        <div class="span12" style="margin-top: 10px">
            <div class="span2 formato">Monto</div>

            <div class="span3">${g.formatNumber(number: contrato?.monto, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}</div>

            <div class="span4 formato">Plazo</div>

            <div class="span2">${g.formatNumber(number: contrato?.plazo, maxFractionDigits: 0, minFractionDigits: 0, format: '##,##0', locale: 'ec')} Días</div>
        </div>

        <div class="span12" style="margin-top: 10px">
            <div class="span2 formato">Anticipo</div>

            <div class="span1">
                ${g.formatNumber(number: contrato?.porcentajeAnticipo, maxFractionDigits: 0, minFractionDigits: 0, format: '##,##0', locale: 'ec')} %
            </div>

            <div class="span2">
                ${g.formatNumber(number: contrato?.anticipo, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}
            </div>

            <div class="span4 formato">Indices 30 días antes de la presentación de la oferta</div>

            <div class="span2">${contrato?.periodoInec?.descripcion}</div>
        </div>

        <g:if test="${contrato?.adicionales}">
            <div class="span12" style="margin-top: 10px">
                <div class="span3 formato">Memo de autorización de Obras Adicionales</div>

                <div class="span3">
                    ${contrato?.adicionales}
                </div>
            </div>
        </g:if>


    </fieldset>
    <fieldset class="" style="position: relative; padding: 10px;border-bottom: 1px solid black; margin-top: -10px">

        <div class="span12" style="margin-top: 10px">
            <div class="span2 formato">Administrador delegado</div>

            <div class="span3">${contrato?.administrador?.titulo} ${contrato?.administrador?.nombre} ${contrato?.administrador?.apellido}</div>

            <div class="span3 formato">Fiscalizador delegado</div>

            <div class="span3">${contrato?.fiscalizador?.titulo} ${contrato?.fiscalizador?.nombre} ${contrato?.fiscalizador?.apellido}</div>
        </div>


        <div class="span12" style="margin-top: 10px">
            <div class="span2 formato">Delegado del prefecto</div>

            <div class="span3">${contrato?.delegadoPrefecto?.titulo} ${contrato?.delegadoPrefecto?.nombre} ${contrato?.delegadoPrefecto?.apellido}</div>

            <div class="span3 formato">Delegado de fiscalización </div>

            <div class="span3">${contrato?.delegadoFiscalizacion?.titulo} ${contrato?.delegadoFiscalizacion?.nombre} ${contrato?.delegadoFiscalizacion?.apellido}</div>
        </div>

    </fieldset>

</g:form>



<g:if test="${contrato && contrato.tipoContrato?.codigo.trim() != 'C'}">
    <div class="navbar navbar-inverse" style="margin-top: 20px;padding-left: 5px;">

        <div class="navbar-inner">
            <div class="botones">

                <ul class="nav">

                    <li>
                        <g:if test="${contrato.obra?.tipo != 'D'}">
                        %{--<g:if test="${contrato.tipoContrato?.codigo.trim() != 'C'}">--}%
                            <g:link controller="planilla" action="list" id="${contrato?.id}">
                                <i class=" icon-file-alt"></i> Planillas
                            </g:link>
                        </g:if>
                    </li>

                    <li>
                        <g:link action="fechasPedidoRecepcion" id="${contrato?.id}">
                            <i class=" icon-calendar-empty"></i> Pedido de recepción
                        </g:link>
                    </li>

                    %{--
                                                <li>
                                                    <a href="#" id="liquidacion">
                                                        <i class=" icon-paperclip"></i> Generar FP Liquidación
                                                    </a>
                                                </li>
                    --}%

                    <li>
                        <g:link controller="reportesPlanillas" action="reporteAvanceUI" id="${contrato?.id}">
                            <i class=" icon-paperclip"></i> Informe de avance
                        </g:link>
                    </li>

                    %{--
                                                <li>
                                                    <g:if test="${esDirector == 'S'}">
                                                        <a href="#" id="btnAdmin">
                                                            <i class="icon-user"></i> Administrador
                                                        </a>
                                                    </g:if>
                                                </li>
                    --}%

                    <li>
                        <g:if test="${esDirFis == 'S'}">
                            <a href="#" id="btnFisc">
                                <i class="icon-user"></i> Fiscalizador
                            </a>
                        </g:if>
                    </li>

                    <li>
                        %{--<g:if test="${esDirFis == 'S'}">--}%
                        <a href="#" id="btnDelFisc">
                            <i class="icon-user"></i> Delegado fiscalización
                        </a>
                        %{--</g:if>--}%
                    </li>

                    <li>
                    %{--<g:if test="${esDirector == 'S'}">--}%
                        <g:if test="${contrato.fiscalizador?.id == session.usuario.id}">
                            <a href="#" id="btnPref">
                                <i class="icon-user"></i> Delegado del Prefecto
                            </a>
                        </g:if>
                    </li>

                    <li>
                        <g:if test="${contrato.fiscalizador?.id == session.usuario.id}">
                        %{--<g:if test="${esDirector == 'N'}">--}%
                            <a href="#" id="btnIndi">
                                <i class="icon-file"></i> % de Indirectos
                            </a>
                        </g:if>
                    </li>
                    <li>
                        <g:if test="${contrato.fiscalizador?.id == session.usuario.id}">
                        %{--<g:if test="${esDirector == 'N'}">--}%
                        <a href="#" id="btnAdicionales">
                            <i class="icon-file"></i> Autorización C + %
                        </a>
                        </g:if>
                    </li>

                </ul>

            </div>
        </div>

    </div>
</g:if>


<div class="modal hide fade" id="modal-fecha" style="overflow: hidden">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">x</button>

        <h3 id="modal_tittle_fecha">
            Fecha
        </h3>
    </div>

    <div class="modal-body" id="modal_body_fecha">
        <p>Ingrese la fecha hasta la cual desea ver el reporte de avance.</p>
        <elm:datepicker name="fechaAvance" value="${new Date()}"/>
    </div>

    <div class="modal-footer" id="modal_footer_fecha">
        <a href="#" class="btn btn-success" id="btnVerAvance">Ver</a>
    </div>
</div>


<div class="modal hide fade mediumModal" id="modal-var" style="overflow: hidden">
    <div class="modal-header btn-primary">
        <button type="button" class="close" data-dismiss="modal">x</button>

        <h3 id="modal_tittle_var">

        </h3>

    </div>

    <div class="modal-body" id="modal_body_var">

    </div>

    <div class="modal-footer" id="modal_footer_var">
        registro
    </div>

</div>

<div class="modal grandote hide fade" id="modal-busqueda" style="overflow: hidden">
    <div class="modal-header btn-primary">
        <button type="button" class="close" data-dismiss="modal">x</button>

        <h3 id="modalTitle_busqueda"></h3>

    </div>

    <div class="modal-body" id="modalBody">
        <bsc:buscador name="contratos" value="" accion="buscarContrato2" controlador="contrato" campos="${campos}" label="Contrato" tipo="lista"/>

    </div>

    <div class="modal-footer" id="modalFooter_busqueda">

    </div>

</div>

%{--<div id="imprimirDialog">--}%

%{--<fieldset>--}%

%{--<div class="span3">--}%

%{--No existe una fecha de inicio de obra, no se puede imprimir el contrato!--}%

%{--</div>--}%

%{--</fieldset>--}%

%{--</div>--}%

<div id="LQDialogo">

    <fieldset>
        <div class="span3">
            Está seguro de querer iniciar el proceso para generar la Fórmula Polinómica de Liquidación
        </div>
    </fieldset>
</div>



<script type="text/javascript">

    function log(msg, error) {
        var sticky = false;
        var theme = "success";
        if (error) {
            sticky = true;
            theme = "error";
        }
        $.jGrowl(msg, {
            speed          : 'slow',
            sticky         : sticky,
            theme          : theme,
            closerTemplate : '<div>[ cerrar todos ]</div>',
            themeState     : ''
        });
    }

    function updateAnticipo() {
        var porcentaje = $("#porcentajeAnticipo").val();
        var monto = $("#monto").val().replace(",", "");
        var anticipoValor = (porcentaje * (monto)) / 100;
        $("#anticipo").val(number_format(anticipoValor, 2, ".", ","));
    }

    $("#frm-registroContrato").validate();

    function validarNum(ev) {
        /*
         48-57      -> numeros
         96-105     -> teclado numerico
         188        -> , (coma)
         190        -> . (punto) teclado
         110        -> . (punto) teclado numerico
         8          -> backspace
         46         -> delete
         9          -> tab
         37         -> flecha izq
         39         -> flecha der
         */
        return ((ev.keyCode >= 48 && ev.keyCode <= 57) ||
        (ev.keyCode >= 96 && ev.keyCode <= 105) ||
        ev.keyCode == 190 || ev.keyCode == 110 ||
        ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
        ev.keyCode == 37 || ev.keyCode == 39);
    }

    function validarInt(ev) {
        /*
         48-57      -> numeros
         96-105     -> teclado numerico
         188        -> , (coma)
         190        -> . (punto) teclado
         110        -> . (punto) teclado numerico
         8          -> backspace
         46         -> delete
         9          -> tab
         37         -> flecha izq
         39         -> flecha der
         */
        return ((ev.keyCode >= 48 && ev.keyCode <= 57) ||
        (ev.keyCode >= 96 && ev.keyCode <= 105) ||
        ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
        ev.keyCode == 37 || ev.keyCode == 39);
    }

    $("#btnAdmin").click(function () {
        $.ajax({
            type    : "POST",
            url     : "${createLink(controller: 'administradorContrato', action: 'list_ext')}",
            data    : {
                contrato : "${contrato?.id}"
            },
            success : function (msg) {
                var $btnOk = $('<a href="#" class="btn">Aceptar</a>');
                $btnOk.click(function () {
                    $(this).replaceWith(spinner);
                    location.reload(true);
                });
                $("#modal_tittle_var").text("Administradores");
                $("#modal_body_var").html(msg);
                $("#administrador").data("contrato", "${contrato?.id}");
                $("#modal_footer_var").html($btnOk);
                $("#modal-var").modal("show");
            }
        });
        return false;
    });

    $("#btnFisc").click(function () {
        $.ajax({
            type    : "POST",
            url     : "${createLink(controller: 'fiscalizadorContrato', action: 'list_ext')}",
            data    : {
                contrato : "${contrato?.id}"
            },
            success : function (msg) {
                var $btnOk = $('<a href="#" class="btn">Aceptar</a>');
                $btnOk.click(function () {
                    $(this).replaceWith(spinner);
                    location.reload(true);
                });
                $("#modal_tittle_var").text("Fiscalizadores");
                $("#modal_body_var").html(msg);
                $("#fiscalizador").data("contrato", "${contrato?.id}");
                $("#modal_footer_var").html($btnOk);
                $("#modal-var").modal("show");
            }
        });
        return false;
    });

    $("#btnPref").click(function () {
        $.ajax({
            type    : "POST",
            url     : "${createLink(action: 'delegadoPrefecto')}",
            data    : {
                id : "${contrato?.id}"
            },
            success : function (msg) {
                var $btnSave = $('<a href="#" class="btn btn-success"><i class="icon icon-save"></i> Guardar</a>');
                var $btnCerrar = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                $btnSave.click(function () {
                    $(this).replaceWith(spinner);
                    var pref = $("#delegadoPrefecto").val();
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'saveDelegado')}",
                        data    : {
                            id   : "${contrato?.id}",
                            pref : pref
                        },
                        success : function (msg) {
                            location.reload(true);
                        }
                    });
                });
                $("#modal_tittle_var").text("Delegado del Prefecto");
                $("#modal_body_var").html(msg);
                $("#modal_footer_var").html($btnCerrar).append($btnSave);
                $("#modal-var").modal("show");
            }
        });
        return false;
    });

    $("#btnDelFisc").click(function () {
        $.ajax({
            type    : "POST",
            url     : "${createLink(action: 'delegadoFiscalizacion')}",
            data    : {
                id : "${contrato?.id}"
            },
            success : function (msg) {
                var $btnSave = $('<a href="#" class="btn btn-success"><i class="icon icon-save"></i> Guardar</a>');
                var $btnCerrar = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                $btnSave.click(function () {
                    $(this).replaceWith(spinner);
                    var pref = $("#delegadoFisc").val();
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'saveDelegadoFisc')}",
                        data    : {
                            id   : "${contrato?.id}",
                            pref : pref
                        },
                        success : function (msg) {
                            location.reload(true);
                        }
                    });
                });
                $("#modal_tittle_var").text("Delegado de fiscalización");
                $("#modal_body_var").html(msg);
                $("#modal_footer_var").html($btnCerrar).append($btnSave);
                $("#modal-var").modal("show");
            }
        });
        return false;
    });

    $("#btnIndi").click(function () {
        $.ajax({
            type    : "POST",
            url     : "${createLink(controller: 'fiscalizadorContrato', action: 'indirectos')}",
            data    : {
                contrato : "${contrato?.id}"
            },
            success : function (msg) {
                var $btnOk = $('<a href="#" class="btn">Actualizar Valorobras</a>');
                var $btnCerrar = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                $btnOk.click(function () {
                    $(this).replaceWith(spinner);
                    $("#frmaIndi").submit();
                    location.reload(true);
                });
                $("#modal_tittle_var").text("Costos Indirectos");
                $("#modal_body_var").html(msg);
                $("#fiscalizador").data("contrato", "${contrato?.id}");
                $("#modal_footer_var").html($btnCerrar).append($btnOk);
//                        $("#modal_footer_var").html($btnOk);
                $("#modal-var").modal("show");
            }
        });
        return false;
    });

    $("#btnAdicionales").click(function () {
        $.ajax({
            type    : "POST",
            url     : "${createLink(controller: 'fiscalizadorContrato', action: 'adicionales')}",
            data    : {
                contrato : "${contrato?.id}"
            },
            success : function (msg) {
                var $btnOkA = $('<a href="#" class="btn">Aceptar</a>');
                var $btnCerrarA = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                $btnOkA.click(function () {
                    $(this).replaceWith(spinner);
                    submitFormAdicionales();
                });
                $("#modal_tittle_var").text("Costos Indirectos");
                $("#modal_body_var").html(msg);
                $("#fiscalizador").data("contrato", "${contrato?.id}");
                $("#modal_footer_var").html($btnCerrarA).append($btnOkA);
                $("#modal-var").modal("show");
            }
        });
        return false;
    });

    function submitFormAdicionales() {
        var $form = $("#frmAdicionales");
        var data = $form.serialize();
        $.ajax({
            type    : "POST",
            url     : $form.attr("action"),
            data    : data,
            success : function (msg) {
                if(msg == 'ok'){
                    log("Guardardo correctamente", false);
                    setTimeout(function () {
                        location.reload(true);
                    }, 1000);
                }else{
                    log("Error al guardar", true);
                    return false;
                }
            }
        });
    }

    $("#btnVerAvance").click(function () {
        $(this).replaceWith(spinner);
        location.href = "${createLink(controller: 'reportesPlanillas', action: 'reporteAvance', id:contrato?.id)}?fecha=" +
            $("#fechaAvance").val();
    });

    $(".number").keydown(function (ev) {
        return validarInt(ev);
    });

    $("#plazo").keydown(function (ev) {
        return validarInt(ev);
    }).keyup(function () {
        var enteros = $(this).val();
    });

    $("#monto").keydown(function (ev) {
        return validarNum(ev);
    }).keyup(function () {
        var enteros = $(this).val();
    });

    $("#porcentajeAnticipo").keydown(function (ev) {
        return validarNum(ev);
    }).keyup(function () {
        var enteros = $(this).val();
        if (parseFloat(enteros) > 100) {
            $(this).val(100)
        }
        updateAnticipo();
    });

    $("#anticipo").keydown(function (ev) {
        return validarNum(ev);
    }).keyup(function () {
        var enteros = $(this).val();
        updateAnticipo();
//                        var porcentaje = $("#porcentajeAnticipo").val();
//                        var monto = $("#monto").val();
//                        var anticipoValor = (porcentaje * (monto)) / 100;
//                        $("#anticipo").val(number_format(anticipoValor, 2, ".", ""));
    }).click(function () {
        updateAnticipo();
//                        var porcentaje = $("#porcentajeAnticipo").val();
//                        var monto = $("#monto").val();
//                        var anticipoValor = (porcentaje * (monto)) / 100;
//                        $("#anticipo").val(number_format(anticipoValor, 2, ".", ","));
    });

    $("#financiamiento").keydown(function (ev) {
        return validarNum(ev);
    }).keyup(function () {
        var enteros = $(this).val();
    });

    $("#codigo").click(function () {
        $("#btn-aceptar").attr("disabled", false)
    });

    $("#objeto").click(function () {
        $("#btn-aceptar").attr("disabled", false)
    });

    $("#tipoContrato").change(function () {
        $("#btn-aceptar").attr("disabled", false)
    });

    $("#monto").click(function () {
        $("#btn-aceptar").attr("disabled", false)
    });

    $("#financiamiento").click(function () {
        $("#btn-aceptar").attr("disabled", false)
    });

    function enviarObra() {
        var data = "";
        $("#buscarDialog").hide();
        $("#spinner").show();
        $(".crit").each(function () {
            data += "&campos=" + $(this).attr("campo");
            data += "&operadores=" + $(this).attr("operador");
            data += "&criterios=" + $(this).attr("criterio");
        });
        if (data.length < 2) {
            data = "tc=" + $("#tipoCampo").val() + "&campos=" + $("#campo :selected").val() + "&operadores=" + $("#operador :selected").val() + "&criterios=" + $("#criterio").val()
        }
        data += "&ordenado=" + $("#campoOrdn :selected").val() + "&orden=" + $("#orden :selected").val();
        $.ajax({
            type    : "POST", url : "${g.createLink(controller: 'contrato',action:'buscarObra')}",
            data    : data,
            success : function (msg) {
                $("#spinner").hide();
                $("#buscarDialog").show();
                $(".contenidoBuscador").html(msg).show("slide");
            }
        });

    }

    function cargarCombo() {
        if ($("#obraId").val() * 1 > 0) {
            $.ajax({
                type    : "POST", url : "${g.createLink(controller: 'contrato',action:'cargarOfertas')}",
                data    : "id=" + $("#obraId").val(),
                success : function (msg) {
                    $("#div_ofertas").html(msg)
                }
            });
        }
    }

    function cargarCanton() {
        if ($("#obraId").val() * 1 > 0) {
            $.ajax({
                type    : "POST", url : "${g.createLink(controller: 'contrato',action:'cargarCanton')}",
                data    : "id=" + $("#obraId").val(),
                success : function (msg) {
                    $("#canton").val(msg)
                }
            });
        }
    }

    $("#obraCodigo").dblclick(function () {
        var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
        $("#modalTitle_busqueda").html("Lista de obras");
        $("#modalFooter_busqueda").html("").append(btnOk);
        $("#modal-busqueda").modal("show");
        $("#contenidoBuscador").html("")
        $("#buscarDialog").unbind("click")
        $("#buscarDialog").bind("click", enviarObra)
    });

    $("#btn-lista").click(function () {
        $("#btn-cancelar").attr("disabled", true);
//        $("#btn-aceptar").attr("disabled", true);
        var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
        $("#modalTitle_busqueda").html("Lista de Contratos");
        $("#modalFooter_busqueda").html("").append(btnOk);
        $("#buscarDialog").unbind("click")
        $("#buscarDialog").bind("click", enviar)
        $("#contenidoBuscador").html("")
        $("#modal-busqueda").modal("show");
    });

    $("#btn-nuevo").click(function () {
        location.href = "${createLink(action: 'registroContrato')}"
    });

    $("#obraCodigo").focus(function () {
        var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
        $("#modalTitle_busquedaOferta").html("Lista de Obras");
        $("#modalFooter_busquedaOferta").html("").append(btnOk);
        $("#modal-busquedaOferta").modal("show");

    });

    if (${contrato?.codigo != null}) {
//                $("#btn-imprimir").attr("disabled", false);
        $(".activo").focus(function () {
            $("#btn-aceptar").attr("disabled", false)
        })
    }

    $("#btn-salir").click(function () {
        location.href = "${g.createLink(action: 'index', controller: "inicio")}";
    });

    $("#btn-aceptar").click(function () {
        $("#frm-registroContrato").submit();
    });

    $("#btn-cancelar").click(function () {
        if (${contrato?.id == null}) {
            location.href = "${g.createLink(action: 'registroContrato')}";
        } else {
            location.href = "${g.createLink(action: 'registroContrato')}" + "?contrato=" + "${contrato?.id}";
        }
    });

    $("#btn-borrar").click(function () {
        if (${contrato?.codigo != null}) {
            $("#borrarContrato").dialog("open")
        }
    });

    $("#borrarContrato").dialog({
        autoOpen  : false,
        resizable : false,
        modal     : true,
        draggable : false,
        width     : 350,
        height    : 220,
        position  : 'center',
        title     : 'Eliminar Contrato',
        buttons   : {
            "Aceptar"  : function () {
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(action: 'delete')}",
                    data    : "id=${contrato?.id}",
                    success : function (msg) {
                        $("#borrarContrato").dialog("close");
                        location.href = "${g.createLink(action: 'registroContrato')}";
                    }
                });
            },
            "Cancelar" : function () {
                $("#borrarContrato").dialog("close");
            }
        }
    });

    $("#LQDialogo").dialog({
        autoOpen  : false,
        resizable : false,
        modal     : true,
        draggable : false,
        width     : 350,
        height    : 220,
        position  : 'center',
        title     : 'Generar Liquidación de Obra',
        buttons   : {
            "Aceptar"  : function () {
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(action: 'obraLiquidacion')}",
                    data    : "id=${contrato?.id}",
                    success : function (msg) {
                        alert(msg);
                    }
                });
                $("#LQDialogo").dialog("close")
            },
            "Cancelar" : function () {
                $("#LQDialogo").dialog("close")
            }
        }
    });

    $("#liquidacion").click(function () {
        if (${contrato?.id != null}) {
            $("#LQDialogo").dialog("open")
        }
    });

    %{--$("#btn-imprimir").click(function () {--}%
    %{--if (${contrato?.fechaInicio != null}) {--}%
    %{--location.href = "${g.createLink(controller: 'reportes3', action: 'reporteContrato', id: contrato?.id)}"--}%
    %{--} else {--}%
    %{--$("#imprimirDialog").dialog("open");--}%
    %{--}--}%
    %{--});--}%
    //            $("#imprimirDialog").dialog({
    //                autoOpen  : false,
    //                resizable : false,
    //                modal     : true,
    //                draggable : false,
    //                width     : 350,
    //                height    : 180,
    //                position  : 'center',
    //                title     : 'Imprimir Contrato',
    //                buttons   : {
    //                    "Aceptar" : function () {
    //                        $("#imprimirDialog").dialog("close");
    //                    }
    //                }
    //            })

    $("#imprimir_sub").click(function () {

        var subpre = -1
        var datos = "?obra=${contrato?.oferta?.concurso?.obra?.id}Wsub=" + subpre
        var url = "${g.createLink(controller: 'reportes3',action: 'imprimirTablaSub')}" + datos
        location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + url

    });

    //            $("#inicioObra").click(function () {
    %{--var cntr = "${contrato.id}"--}%
    %{--if ("${contrato.id}") {--}%
    //                    $.ajax({
    %{--type    : "POST", url : "${g.createLink(controller: 'planilla', action:'iniObraNoReajuste')}",--}%
    //                        data    : "id=" + cntr,
    //                        success : function (msg) {
    //                            $("#canton").val(msg)
    //                        }
    //                    });
    //                }
    //            });

    $("#inicioObra").click(function () {
        $.ajax({
            type    : "POST",
            url     : "${g.createLink(controller: 'planilla', action:'iniObraNoReajuste')}",
            data    : {
                id : "${contrato?.id}"
            },
            success : function (msg) {
                var $btnSave = $('<a href="#" class="btn btn-success"><i class="icon icon-save"></i> Guardar</a>');
                var $btnCerrar = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                $btnSave.click(function () {
                    $(this).replaceWith(spinner);
                    var fcha = $("#fecha").val()
                    var memo = $("#memo").val()
                    console.log('...va inicio',fcha, memo)
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(controller: 'planilla', action:'iniciarObra')}",
                        data    : {
                            cntr  : "${contrato?.id}",
                            fecha  : fcha,
                            memo  : memo
                        },
                        success : function (msg) {
                            if(msg == "ok") {
                                location.reload(true);
                            } else {
                                log("Un error ha ocurrido al iniciar la obra", success)
                            }
                        }
                    });
                });
                $("#modal_tittle_var").text("Inicio de Obra sin Reajustes");
                $("#modal_body_var").html(msg);
                $("#modal_footer_var").html($btnCerrar).append($btnSave);
                $("#modal-var").modal("show");
            }
        });
        return false;
    });

    $("#btnRubros").click(function () {

        var url = "${createLink(controller:'reportes', action:'imprimirRubros')}?obra=${contrato?.oferta?.concurso?.obra?.id}Wdesglose=";
        var urlVae = "${createLink(controller:'reportes3', action:'reporteRubrosVaeReg')}?obra=${contrato?.oferta?.concurso?.obra?.id}Wdesglose=";
        $.box({
            imageClass : "box_info",
            text       : "Imprimir los análisis de precios unitarios de los rubros usados en la obra<br><span style='margin-left: 42px;'>Ilustraciones y Especificaciones</span>",
            title      : "Imprimir Rubros de la Obra",
            iconClose  : true,
            dialog     : {
                resizable : false,
                draggable : false,
                width     : 600,
                height    : 280,
                buttons   : {

                    "Con desglose de Trans."     : function () {
                        url += "1";
                        location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + url
                    },
                    "Sin desglose de Trans."     : function () {
                        url += "0";
                        location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + url
                    },
                    "Exportar Rubros a Excel"    : function () {
                        var url = "${createLink(controller:'reportes', action:'imprimirRubrosExcel')}?obra=${contrato?.oferta?.concurso?.obra?.id}&transporte=";
                        url += "1";
                        location.href = url;
                    },
                    "VAE con desglose de Trans." : function () {
                        urlVae += "1";
                        location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + urlVae
                    },
                    "VAE sin desglose de Trans." : function () {
                        urlVae += "0";
                        location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + urlVae
                    },
                    "Exportar VAE a Excel"       : function () {
                        var urlVaeEx = "${createLink(controller:'reportes3', action:'imprimirRubrosVaeExcel')}?obra=${contrato?.oferta?.concurso?.obra?.id}&transporte=";
                        urlVaeEx += "1";
                        location.href = urlVaeEx;
                    },

                    %{--"Imprimir las Ilustraciones y las Especificaciones de los Rubros utilizados en la Obra" : function () {--}%
                    %{--var url = "${createLink(controller:'reportes2', action:'reporteRubroIlustracion')}?id=${contrato?.oferta?.concurso?.obra?.id}&tipo=ie";--}%
                    %{--location.href = url;--}%
                    %{--},--}%


                    "Imprimir las Ilustraciones y las Especificaciones de los Rubros utilizados en la Obra": function () {
                        %{--var url = "${createLink(controller:'reportes2', action:'reporteRubroIlustracion')}?id=${obra?.id}&tipo=ie";--}%
                        %{--location.href = url;--}%
                        var idObra;

                        %{--if(${contrato}){--}%

                        idObra = ${contrato?.obra?.id}

                            $.ajax({
                                type: "POST",
                                url: "${createLink(controller:'reportes2', action:'comprobarIlustracion')}",
                                data: {
                                    id: idObra,
                                    tipo: "ie"
                                },
                                success: function (msg) {

                                    var parts = msg.split('*');

                                    if (parts[0] == 'SI') {
                                        $("#divError").hide();
                                        %{--var url = "${createLink(controller:'reportes2', action:'reporteRubroIlustracion')}?id=${obra?.id}&tipo=ie";--}%
                                        var url = "${createLink(controller:'reportes2', action:'reporteRubroIlustracion')}?id=${contrato?.obra?.id}&tipo=ie";
                                        location.href = url;
                                    } else {
                                        $("#spanError").html("El archivo  '" + parts[1] + "'  no ha sido encontrado");
                                        $("#divError").show()
                                    }

                                }
                            });
//                              }


                    },

                    "Cancelar" : function () {

                    }
                }
            }
        });
        return false;
    });


</script>
</body>
</html>