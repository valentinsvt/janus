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
            color : #000000 !important;
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

                <a href="#" class="btn btn-ajax btn-new" id="imprimir" title="Imprimir">
                    <i class="icon-print"></i>
                    Imprimir
                </a>
                <a href="#" class="btn btn-ajax btn-new" id="excel" title="Imprimir">
                    <i class="icon-print"></i>
                    Excel
                </a>
            </div>
        </div>


        <elm:headerPlanilla planilla="${planilla}"/>

        <div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: -10px">

            <div class="area">

                <p class="css-vertical-text">Cálculo de B<sub>0</sub></p>

                <div class="linea" style="height: 100%;"></div>
                <table class="table table-bordered table-striped table-condensed table-hover" style="width: ${150 * periodos.size() + 150}px">
                    <thead>
                        <tr>
                            <th colspan="2">Cuadrilla Tipo</th>
                            <th colspan="2">Oferta: ${oferta.fechaEntrega.format("MMM-yy")}</th>
                            <th colspan="2">Variación: Anticipo <br>${planilla.fechaPresentacion.format("MMM-yy")}</th>
                            <g:if test="${periodos.size() > 2}">
                                <g:each in="${2..periodos.size() - 1}" var="per">
                                    <th colspan="2">Variación: ${periodos[per].fechaInicio.format("MMM-yy")}</th>
                                </g:each>
                            </g:if>
                        </tr>
                    </thead>
                    <tbody>
                        <g:set var="totCs" value="${0}"></g:set>
                        <g:set var="totales" value="${[]}"></g:set>
                        <g:each in="${cs}" var="c" status="i">
                            <tr>
                                <td>${c.indice.descripcion} (${c.numero})</td>
                                <td class="number">${c.valor}</td>
                                <g:set var="totCs" value="${totCs + c.valor}"></g:set>
                                <g:set var="parcial" value="${0}"></g:set>
                                <g:each in="${cant}" var="ca">
                                    <td class="number">${datos[ca][c.numero].round(2)}</td>
                                    <td class="number">${(datos[ca][c.numero] * c.valor).round(3)}</td>
                                </g:each>

                            </tr>
                        </g:each>
                        <tr>
                            <th>TOTALES</th>
                            <td class="number">${totCs.round(3)}</td>
                            <g:each in="${datos}" var="dato">
                                <td></td>
                                <td class="number">${dato["tot"].round(3)}</td>
                            </g:each>
                        </tr>
                    </tbody>
                </table>
            </div> <!-- B0 -->
            <div class="area">

                <p class="css-vertical-text">Cálculo de P<sub>0</sub></p>

                <div class="linea" style="height: 100%;"></div>

                <table class="table table-bordered table-striped table-condensed table-hover" style="width: ${150 * periodos.size() + 150}px; margin-top: 10px;">
                    <thead>
                        <tr>
                            <th colspan="2" rowspan="2">Mes y año</th>
                            <th colspan="2">Cronograma</th>
                            <th colspan="2">Planillado</th>
                            <th colspan="2" rowspan="2">Valor P<sub>0</sub></th>
                        </tr>
                        <tr>
                            <th>Parcial</th>
                            <th>Acumulado</th>
                            <th>Parcial</th>
                            <th>Acumulado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <th>Anticipo</th>
                            <th>${planilla.fechaPresentacion.format("MMM-yy")}</th>
                            <td colspan="4"></td>
                            <td class="number">
                                <g:formatNumber number="${planilla.valor}" format="##,##0" locale="ec" maxFractionDigits="2" minFractionDigits="2"/>
                            </td>
                        </tr>
                        <g:if test="${periodos.size() > 2}">
                            <g:each in="${2..periodos.size() - 1}" var="per">
                                <tr>
                                    <th>${periodos[per].fechaInicio.format("MMM-yy")}</th>
                                </tr>
                            </g:each>
                        </g:if>
                    </tbody>
                </table>

            </div> <!-- P0 -->

            <div class="area" style="min-height: 190px; margin-bottom: 30px;">

                <p class="css-vertical-text">Cálculo de F<sub>r</sub> y P<sub>r</sub></p>

                <div class="linea" style="height: 100%;"></div>

                <table class="table table-bordered table-striped table-condensed table-hover" style="width: ${150 * periodos.size() + 150}px; margin-top: 10px;">
                    <thead>
                        <tr>
                            <th rowspan="2">Componentes</th>
                            <th>Oferta</th>
                            %{--<th></th>--}%
                            <th>Periodo de variación y aplicación de fórmula polinómica</th>
                        </tr>
                        <tr>
                            <th>${oferta.fechaEntrega.format("MMM-yy")}</th>
                            %{--<th></th>--}%
                            <th>Anticipo <br>${planilla.fechaPresentacion.format("MMM-yy")}</th>
                            <g:if test="${periodos.size() > 2}">
                                <g:each in="${2..periodos.size() - 1}" var="per">
                                    <th rowspan="2">${periodos[per].fechaInicio.format("MMM-yy")}</th>
                                </g:each>
                            </g:if>
                        </tr>
                        <tr>
                            <th>Anticipo</th>
                            <th>
                                <g:formatNumber number="${contrato.porcentajeAnticipo}" minFractionDigits="0" maxFractionDigits="0"/>%
                            </th>
                            %{--<th>A</th>--}%
                            <th>Anticipo</th>
                        </tr>
                    </thead>
                    <tbody>
                        <g:each in="${ps}" var="c" status="i">
                            <tr>
                                <td class="number">${c.indice.descripcion} (${c.numero})</td>
                                <g:each in="${cantP}" var="ca">
                                    <td class="number">${datosP[ca][c.numero].round(2)}</td>
                                    <td class="number">${(datosP[ca][c.numero] * c.valor).round(3)}</td>
                                </g:each>

                            </tr>
                        </g:each>
                    </tbody>
                </table>
            </div><!-- Fr y Pr -->

        </div>
    </body>
</html>