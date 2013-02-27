<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Cálculo de B0 y P0
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">

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
            <div class="span12 btn-group" role="navigation">
                <g:link controller="contrato" action="registroContrato" params="[contrato: contrato?.id]" class="btn btn-ajax btn-new" title="Regresar al contrato">
                    <i class="icon-double-angle-left"></i>
                    Contrato
                </g:link>
                <g:link controller="planilla" action="list" params="[id: contrato?.id]" class="btn btn-ajax btn-new" title="Regresar a las planillas del contrato">
                    <i class="icon-angle-left"></i>
                    Planillas
                </g:link>
                <a href="#" class="btn btn-ajax btn-new" id="btnImprimir" title="Imprimir">
                    <i class="icon-print"></i>
                    Imprimir
                </a>
                %{--<a href="#" class="btn btn-ajax btn-new" id="excel" title="Imprimir">--}%
                %{--<i class="icon-table"></i>--}%
                %{--Excel--}%
                %{--</a>--}%
            </div>
        </div>

        <elm:headerPlanilla planilla="${planilla}"/>

        <div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: -10px">
            <div class="area">

                <p class="css-vertical-text">Cálculo de B<sub>0</sub></p>

                <div class="linea" style="height: 100%;"></div>

                ${tablaB0}
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

                    <div class="tituloTree">Multa por no presentación de planilla</div>
                    ${pMl}

                    <div class="tituloTree">Multa por retraso de obra</div>
                    ${tablaMl}
                </div> <!-- Multas -->
            </g:if>
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

        <script type="text/javascript">
            $("#btnImprimir").click(function () {
                var actionUrl = "${createLink(controller:'pdf',action:'pdfLink')}?filename=planilla.pdf&url=${createLink(controller: 'reportes2', action: 'tablasPlanilla')}";
                location.href = actionUrl + "?id=${planilla.id}";

                var wait = $("<div style='text-align: center;'> Estamos procesando su reporte......Por favor espere......</div>");
                wait.prepend(spinnerBg);

                var btnClose = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                $("#modalTitle").html("Procesando");
                $("#modalBody").html(wait);
                $("#modalFooter").html("").append(btnClose);
            });
        </script>

    </body>
</html>