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

                <a href="#" class="btn btn-ajax btn-new" id="imprimir" title="Imprimir">
                    <i class="icon-print"></i>
                    Imprimir
                </a>
                <a href="#" class="btn btn-ajax btn-new" id="excel" title="Imprimir">
                    <i class="icon-table"></i>
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
                            <th>Oferta</th>
                            <th class="nb">${oferta.fechaEntrega.format("MMM-yy")}</th>
                            <th>Variación</th>
                            <th class="nb">Anticipo <br>${planilla.fechaPresentacion.format("MMM-yy")}</th>
                            <g:if test="${periodos.size() > 2}">
                                <g:each in="${2..periodos.size() - 1}" var="per">
                                    <th>Variación</th>
                                    <th class="nb">${periodos[per].fechaInicio.format("MMM-yy")}</th>
                                </g:each>
                            </g:if>
                        </tr>
                    </thead>
                    <tbody>
                        <g:set var="totC" value="${0}"/>
                        <g:each in="${pcs.findAll { it.numero.contains('c') }}" var="c">
                            <tr>
                                <td>${c.indice.descripcion} (${c.numero})</td>
                                <td class="number">
                                    <elm:numero number="${c.valor}" decimales="3"/>
                                    <g:set var="totC" value="${totC + c.valor}"/>
                                </td>
                                <g:each in="${data.c}" var="cp">
                                    <g:set var="val" value="${cp.value.valores.find { it.formulaPolinomica.indice == c.indice }.valorReajuste.valor}"/>
                                    <td class="number">
                                        <elm:numero number="${val}"/>
                                    </td>
                                    <td class="number">
                                        <elm:numero number="${c.valor * val}" decimales="3"/>
                                    </td>
                                </g:each>
                            </tr>
                        </g:each>

                        <tr>
                            <th>TOTALES</th>
                            <td class="number">
                                <elm:numero number="${totC}" decimales="3"/>
                            </td>
                            <g:each in="${data.c}" var="cp">
                                <td></td>
                                <td class="number">
                                    <elm:numero number="${cp.value.total}" decimales="3"/>
                                </td>
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
                                <elm:numero number="${planilla.valor}"/>
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
                            <th colspan="${periodos.size() - 1}">Periodo de variación y aplicación de fórmula polinómica</th>
                        </tr>
                        <tr>
                            <th>${oferta.fechaEntrega.format("MMM-yy")}</th>
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
                                <elm:numero number="${contrato.porcentajeAnticipo}" decimales="0"/>%
                            </th>
                            <th>Anticipo</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${tbodyFr}
                    </tbody>
                </table>

            </div> <!-- Fr y Pr -->

        </div>

    </body>
</html>