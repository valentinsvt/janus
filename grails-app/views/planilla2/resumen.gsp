<%@ page import="janus.Grupo" %>
<!doctype html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="main">
    <title>
        Cálculo de B0 y P0
    </title>
%{--
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
--}%

    <style type="text/css">
    th {
        vertical-align : middle !important;
    }

    tbody th {
        background : #ECECEC !important;
        color      : #000000 !important;
    }

    .number {
        text-align : right !important;
    }

    .area {
        border-bottom : 1px solid black;
        padding-left  : 50px;
        position      : relative;
        overflow-x    : auto;
        min-height    : 150px;
    }

    .nb {
        border-left : none !important;
    }

    .bold {
        font-weight : bold;
    }

    .tal {
        text-align : left !important;
    }
    </style>

</head>

<body>

<g:if test="${flash.message}">
    <div class="row">
        <div class="span12">
            <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                <a class="close" data-dismiss="alert" href="#">×</a>
                ${flash.message}
            </div>
        </div>
    </div>
</g:if>

<div class="row" style="margin-bottom: 10px;">
    <div class="span5 btn-group" role="navigation">
        <g:link controller="contrato" action="verContrato" params="[contrato: planilla.contrato?.id]" class="btn btn-ajax btn-new" title="Regresar al contrato">
            <i class="icon-double-angle-left"></i>
            Contrato
        </g:link>
        <g:link controller="planilla" action="list" params="[id: planilla.contrato?.id]" class="btn btn-ajax btn-new" title="Regresar a las planillas del contrato">
            <i class="icon-angle-left"></i>
            Planillas
        </g:link>

        <g:link controller="reportePlanillas3" action="reportePlanilla" id="${planilla.id}" class="btn  btn-ajax" rel="tooltip" title="Imprimir">
            <i class="icon-print"></i> Imprimir
        </g:link>


    </div>
    <g:set var="anticipoVarios" value="${(janus.ejecucion.ReajustePlanilla.findAllByPlanilla(planilla).size() > 1) && (planilla.tipoPlanilla.codigo == 'A')}"/>
    <g:if test="${anticipoVarios}">
        Reajuste con la fórmula:
        <g:select name="reajuste" from="${janus.ejecucion.ReajustePlanilla.findAllByPlanilla(planilla)}" optionKey="id"
                  id="reajustePl" class="span4" title="Rejustes adicionales" value="${params.rjpl}"/>
        <button id="verReajuste" style="height: 30px; margin-top: -10px;">Ver Reajuste</button>
    </g:if>
</div>

<g:if test="${anticipoVarios}">
    <h5 style="width: 100%; text-align: center; font-family: 'Bookman', 'Georgia', 'Times New Roman', 'serif'">
        Planilla del Anticipo reajustada con la Fórmula Polinómica: "${formula}"</h5>
</g:if>

<elm:headerPlanilla planilla="${planilla}"/>

<div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: -10px">
    <div class="area">

        <p class="css-vertical-text">Cálculo de B<sub>0</sub></p>

        <div class="linea" style="height: 100%;"></div>

        ${tablaBo}
    </div> <!-- B0 -->

    <div class="area">

        <p class="css-vertical-text">Cálculo de P<sub>0</sub></p>

        <div class="linea" style="height: 100%;"></div>

        ${tablaP0}
    </div> <!-- P0 -->

    <div class="area" style="min-height: 190px; margin-bottom: 30px;">

        <p class="css-vertical-text">Cálculo de F<sub>r</sub> y P<sub>r</sub></p>

        <div class="linea" style="height: 100%;"></div>

        ${tablaFr}
    </div> <!-- Fr y Pr -->

    <g:if test="${planilla.tipoPlanilla.codigo != 'A'}">
        <div class="area" style="min-height: 190px; margin-bottom: 30px;">

            <p class="css-vertical-text">Multas</p>

            <div class="linea" style="height: 100%;"></div>

            <div class="tituloTree">Multa por retraso en la presentación de planilla</div>
            ${pMl}

            <div class="tituloTree">Multa por incumplimiento del cronograma</div>
            ${tablaMl}

            <div class="tituloTree">Multa por no acatar disposiciones del fiscalizador</div>
            ${tablaMlFs}

        </div> <!-- Multas -->
    </g:if>
    %{--<div class="area" style="min-height: 50px; margin-bottom: 10px;">--}%
        %{--Nota: Los índices utilizados para el reajuste son del periodo: ${planilla.periodoAnticipo}--}%
    %{--</div>--}%
</div>

<div class="modal hide fade" id="modal-tree">
    <div class="modal-header" id="modalHeader">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle"></h3>
    </div>

    <div class="modal-body" id="modalBody">
    </div>

    <div class="modal-footer" id="modalFooter">
    </div>
</div>

<div id="dlg_reporte">
    Desea imprimir con el detalle?
</div>

<script type="text/javascript">
    $("#dlg_reporte").dialog({
        modal     : true,
        width     : 340,
        height    : 150,
        title     : "Reporte",
        resizable : false,
        autoOpen  : false,
        buttons   : {
            "Si" : function () {
                $("#dlg_reporte").dialog("close")

                var actionUrl = "${createLink(controller:'pdf',action:'pdfLink')}?filename=planilla.pdf&url=${createLink(controller: 'reportes2', action: 'tablasPlanilla')}";
                location.href = actionUrl + "?id=${planilla.id}WimprimeDetalle=1";

                var btnClose = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                $("#dlg_reporte").dialog("close")
            },
            "No" : function () {

                var actionUrl = "${createLink(controller:'pdf',action:'pdfLink')}?filename=planilla.pdf&url=${createLink(controller: 'reportes2', action: 'tablasPlanilla')}";
                location.href = actionUrl + "?id=${planilla.id}WimprimeDetalle=0";

                var btnClose = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                $("#dlg_reporte").dialog("close")
            }
        }
    })
    $("#btnImprimir").click(function () {
        $("#dlg_reporte").dialog("open")
    });

    $("#verReajuste").click(function(){
        var rj = $("#reajustePl").val();
        var url = "${createLink(action:'resumen')}?id=${planilla.id}&rjpl=" + rj;
        location.href = url;
    });
</script>

</body>
</html>