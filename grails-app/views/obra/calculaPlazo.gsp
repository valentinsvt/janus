<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 3/11/13
  Time: 11:43 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Plazo de la obra</title>

        <style type="text/css">
        .num {
            text-align : right !important;
        }

        .area {
            margin-bottom : 20px;
        }

        table {
            font-size : 12px;
            width     : auto !important;
        }

        th {
            font-size   : 11px;
            height      : 15px !important;
            line-height : 15px !important;
            padding     : 3px !important;
        }

        td {
            height      : 14px !important;
            line-height : 14px !important;
            padding     : 3px !important;
        }
        </style>
    </head>

    <body>
        <div class="tituloChevere">Plazo de la obra: ${obra.nombre}</div>

        <div class="btn-toolbar" style="margin-top: 15px;">
            <div class="btn-group">
                <a href="${g.createLink(controller: 'obra', action: 'registroObra', params: [obra: obra?.id])}" class="btn " title="Regresar a la obra">
                    <i class="icon-arrow-left"></i>
                    Obra
                </a>
            </div>

            <div class="btn-group">
                <a href="#" class="btn btn-success" id="btn-saveParams">
                    <i class="icon-save"></i>
                    Guardar
                </a>
            </div>
        </div>

        <g:form action="calculaPlazo" method="POST" name="frmParams" id="${obra.id}">
            <g:hiddenField name="save" value="0"/>

            <fieldset style="margin-top: 25px;">
                %{--
                                <legend>Ajustar plazo de la obra</legend>
                --}%

                <span style="font-size: 16px; color:#1066A0;">Plazo actual de la obra:</span>

                <div style="height: 35px; margin-top: -25px; margin-left: 200px;">
                    <div class="input-append" style="float: left;margin-right: 10px;">
                        <g:textField name="plazoMeses" class="input-mini orig" value="${obra.plazoEjecucionMeses}" data-original="${obra.plazoEjecucionMeses}"/>
                        <span class="add-on">meses</span>
                    </div>

                    <div class="input-append" style="float: left;">
                        <g:textField name="plazoDias" class="input-mini orig" value="${obra.plazoEjecucionDias}" data-original="${obra.plazoEjecucionDias}"/>
                        <span class="add-on">días</span>
                    </div>

                    %{--<div class="btn-group" style="margin-left: 10px;">--}%
                    %{--<a href="#" class="btn btn-success" id="btn-save">--}%
                    %{--<i class="icon-save"></i>--}%
                    %{--Guardar--}%
                    %{--</a>--}%
                    %{--</div>--}%
                </div>
            </fieldset>
            <fieldset style="margin-top: 15px;">
                <span style="font-size: 16px; color:#1066A0;">Ajustar parámetros de cálculo del plazo a:</span>

                <div style="height: 35px; margin-top: -25px; margin-left: 320px;">

                    <div class="input-append" style="float: left; margin-right: 10px;">
                        <g:textField name="personas" class="input-mini orig" value="${params.personas}" data-original="${obra.plazoPersonas}"/>
                        <span class="add-on">integrantes de cuadrilla</span>
                    </div>

                    <div class="input-append" style="float: left;">
                        <g:textField name="maquinas" class="input-mini orig" value="${params.maquinas}" data-original="${obra.plazoMaquinas}"/>
                        <span class="add-on">equipos completos de maquinaria</span>
                    </div>

                    <div class="btn-group" style="margin-left: 10px;">
                        <a href="#" class="btn btn-info" id="btn-preview">
                            <i class="icon-eye-open"></i>
                            Vista Previa
                        </a>
                    </div>
                </div>

            </fieldset>

        %{--
                    <fieldset>
                        <legend>Ajustar parámetros de cálculo de plazo</legend>

                        <div style="height: 35px;">
                            <div class="input-append" style="float: left;margin-right: 10px;">
                                <g:textField name="personas" class="input-mini orig" value="${params.personas}" data-original="${obra.plazoPersonas}"/>
                                <span class="add-on">integrantes de cuadrilla</span>
                            </div>

                            <div class="input-append" style="float: left;">
                                <g:textField name="maquinas" class="input-mini orig" value="${params.maquinas}" data-original="${obra.plazoMaquinas}"/>
                                <span class="add-on">equipos completos de maquinaria</span>
                            </div>

                            <div class="btn-group" style="margin-left: 10px;">
                                <a href="#" class="btn btn-info" id="btn-preview">
                                    <i class="icon-eye-open"></i>
                                    Vista Previa
                                </a>
                            </div>
                        </div>
                    </fieldset>
        --}%
        </g:form>

        <div class="area">
            <div class="tituloTree">Plazos en base a la duración de cada rubro
                <span style="font-size: small; color: #222; font-weight: normal"> &nbsp; Los valores de los plazos en días corresponden a jornadas de trabajo de 8 horas, en días calendario.</span></div>


            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>
                        <th style="width: 20px;">#</th>
                        <th style="width: 80px;">Código</th>
                        <th style="width: 600px;">Rubro</th>
                        <th style="width: 55px;">Unidad</th>
                        <th style="width: 70px;">Cantidad</th>
                        <th style="width: 45px;">Días</th>
                    </tr>
                </thead>
                <tbody>
                    <g:set var="sum" value="${0}"/>
                    <g:each in="${resultR}" var="res" status="i">
                        <g:set var="val" value="${res?.dias ?: 0}"/>
                        <tr>
                            <td>${i + 1}</td>
                            <td>${res.itemcdgo.trim()}</td>
                            <td>${res.itemnmbr.trim()}</td>
                            <td>${res.unddcdgo.trim()}</td>
                            <td class="num"><g:formatNumber number="${res.rbrocntd}" locale="ec" minFractionDigits="1" maxFractionDigits="1"/></td>
                            <td class="num"><b><g:formatNumber number="${res.dias}" locale="ec" minFractionDigits="2" maxFractionDigits="2"/></b>
                            </td>
                            <g:set var="sum" value="${sum + val}"/>
                        </tr>
                    </g:each>
                </tbody>
            </table>

            <div style="margin-top:10px;">

                <g:set var="meses" value="${Math.floor(sum / 30).toInteger()}"/>
                <g:set var="dias" value="${Math.floor(sum - (meses * 30)).toInteger() + (((sum - Math.floor(sum)) > 0) ? 1 : 0)}"/>

                <g:if test="${dias == 30}">
                    <g:set var="meses" value="${meses + 1}"/>
                    <g:set var="dias" value="${0}"/>
                </g:if>


                <b>Plazo recomendado: <g:formatNumber locale="ec" number="${sum}" maxFractionDigits="1"/> días,
                es decir, ${meses} meses y ${dias} días</b>
                <a href="#" class="btn btn-info apply" data-meses="${meses}" data-dias="${dias}">
                    <i class="icon-ok"></i> Utilizar
                </a>
            </div>
        </div>

        <div class="area">
            <div class="tituloTree" style="margin-top: 10px;">Plazos en base a componentes de Mano de Obra</div>

            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>
                        <th style="width: 20px;">#</th>
                        <th style="width: 80px;">Código</th>
                        <th style="width: 600px;">Componente de Mano de Obra</th>
                        <th style="width: 70px;">Cantidad</th>
                        <th style="width: 45px;">Días</th>
                    </tr>
                </thead>
                <tbody>
                    <g:set var="max" value="${0}"/>
                    <g:each in="${resultM}" var="res" status="i">
                        <tr>
                            <td>${i + 1}</td>
                            <td>${res.itemcdgo.trim()}</td>
                            <td>${res.itemnmbr.trim()}</td>
                            <td class="num"><g:formatNumber number="${res.itemcntd}" locale="ec" minFractionDigits="1" maxFractionDigits="1"/></td>
                            <td class="num"><b><g:formatNumber number="${res.dias}" locale="ec" minFractionDigits="1" maxFractionDigits="1"/></b>
                            </td>
                            <g:if test="${res.dias > max}">
                                <g:set var="max" value="${res.dias}"/>
                            </g:if>
                        </tr>
                    </g:each>
                </tbody>
            </table>
            %{--<g:set var="meses" value="${Math.floor(max / 30).toInteger()}"/>--}%
            %{--<g:set var="dias" value="${Math.floor(max - (meses * 30)).toInteger() + (((max - Math.floor(max)) > 0) ? 1 : 0)}"/>--}%
            %{--Plazo recomendado: <g:formatNumber locale="ec" number="${max}" maxFractionDigits="1"/> días,--}%
            %{--es decir, <b>${meses} meses y ${dias} días</b>--}%
            %{--<a href="#" class="btn btn-info apply" data-meses="${meses}" data-dias="${dias}">--}%
            %{--<i class="icon-ok"></i> Utilizar--}%
            %{--</a>--}%
        </div>

        <script type="text/javascript">
            $(function () {
                $("#btn-preview").click(function () {
                    $("#save").val("0");
                    $(this).replaceWith(spinner);
                    $("#frmParams").submit();
                    return false;
                });

                $("#btn-save").click(function () {
                    $(this).replaceWith(spinner);
                    $("#frmPlazo").submit();
                    return false;
                });

                $("#btn-saveParams").click(function () {
                    $("#save").val("1");
                    $(this).replaceWith(spinner);
                    $("#frmParams").submit();
                    return false;
                });

                $(".apply").click(function () {
                    $("#plazoMeses").val($(this).data("meses"));
                    $("#plazoDias").val($(this).data("dias"));
                    return false;
                });

                $(".table").each(function () {
                    var maxHeight = 250;
                    var $table = $(this);
                    var $head = $table.children("thead");
                    var $body = $table.children("tbody");
                    var widths = [];
                    var $h = $head.find("th");
                    var widthFinal = 0;
                    if ($h.length == 0) {
                        var $h = $head.find("td");
                    }
                    $h.each(function () {
                        var w = $(this).width();
                        widths.push(w);
                        widthFinal += w;
                    });
                    var i = 0;
                    var add = 0;
                    $body.children().first().children().each(function () {
                        $(this).width(widths[i]);
                        add += 10;
                        i++;
                    });

                    widthFinal += add;

                    $h.children().last().width($h.children().last().width() + add);

                    var $t1 = $("<table class='" + $table.attr("class") + "'/>").append($head).css("margin-bottom", 0);
                    var $t2 = $("<table class='" + $table.attr("class") + "'/>").append($body).css("margin-bottom", 0);
                    var $div = $("<div/>").css({
                        width     : widthFinal,
                        maxHeight : maxHeight,
                        overflowX : "hidden",
                        overflowY : "auto"
                    }).append($t2);

                    var $final = $("<div/>");
                    $final.append($t1);
                    $final.append($div);

                    $table.replaceWith($final);
                });

            });
        </script>

    </body>
</html>