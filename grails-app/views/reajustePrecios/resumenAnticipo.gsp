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
    </head>

    <body>

        <div class="span12">
            <g:if test="${flash.message}">
                <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                    <a class="close" data-dismiss="alert" href="#">×</a>
                    ${flash.message}
                </div>
            </g:if>
        </div>

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


        <div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: -10px">

            <div style="border-bottom: 1px solid black;padding-left: 50px;position: relative;overflow-x: auto">

                <p class="css-vertical-text">Calculo de B0</p>

                <div class="linea" style="height: 100%;"></div>
                <table class="table table-bordered table-striped table-condensed table-hover" style="width: ${150 * periodos.size() + 150}px">
                    <thead>
                        <tr>
                            <th colspan="2">Cuadrilla Tipo</th>
                            <th colspan="2">Oferta: ${fechaOferta.format("MMM-yy")}</th>
                            <th colspan="2">Variación: Anticipo <br>${fechaAnticipo.format("MMM-yy")}</th>
                            <g:each in="${2..periodos.size() - 1}" var="per">
                                <th colspan="2">Variación: ${periodos[per].fechaInicio.format("MMM-yy")}</th>
                            </g:each>
                        </tr>
                    </thead>
                    <tbody>
                        <g:set var="totCs" value="${0}"></g:set>
                        <g:set var="totales" value="${[]}"></g:set>
                        <g:each in="${cs}" var="c" status="i">
                            <tr>
                                <td>${c.numero}</td>
                                <td style="text-align: right">${c.valor}</td>
                                <g:set var="totCs" value="${totCs + c.valor}"></g:set>
                                <g:set var="parcial" value="${0}"></g:set>
                                <g:each in="${cant}" var="ca">
                                    <td style="text-align: right">${datos[ca][c.numero].round(2)}</td>
                                    <td style="text-align: right">${(datos[ca][c.numero] * c.valor).round(3)}</td>
                                </g:each>

                            </tr>
                        </g:each>
                        <tr>
                            <td style="font-weight: bold">TOTALES</td>
                            <td style="text-align: right">${totCs.round(3)}</td>
                            <g:each in="${datos}" var="dato">
                                <td></td>
                                <td style="text-align: right">${dato["tot"].round(3)}</td>
                            </g:each>
                        </tr>
                    </tbody>
                </table>
            </div>

        </div>
    </body>
</html>