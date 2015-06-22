<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/17/13
  Time: 4:34 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>

        <meta name="layout" content="main">
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

        <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>

        <style type="text/css">

        .formato {
            font-weight : bolder;
        }

        </style>


        <title>Formula Polinómica</title>
    </head>

    <body>

        <div class="row" style="margin-bottom: 10px;">
            <div class="span9 btn-group" role="navigation">
                <g:link controller="contrato" action="verContrato" params="[contrato: contrato?.id]" class="btn btn-ajax btn-new" title="Regresar al contrato">
                    <i class="icon-double-angle-left"></i>
                    Contrato
                </g:link>
            </div>
        </div>

        <div id="tabs" style="width: 900px; height: 600px; text-align: center">

            <ul>
                <li><a href="#tab-formulaPolinomica">Fórmula Polinómica</a></li>
                <li><a href="#tab-cuadrillaTipo">Cuadrilla Tipo</a></li>

            </ul>

            <div id="tab-formulaPolinomica" class="tab">
                <div class="formula">
                    <fieldset class="borde">
                        <legend>Fórmula Polinómica</legend>
                        <table class="table table-bordered table-striped table-hover table-condensed" id="tablaPoliContrato" width="600px">
                            <thead>
                                <tr>
                                    %{--<th style="width: 260px; text-align: center">Subpresupuesto</th>--}%
                                    <th style="width: 70px; text-align: center">Coeficiente</th>
                                    <th style="width: 400px">Nombre del Indice (INEC)</th>
                                    <th style="width: 70px">Valor</th>
                                </tr>
                            </thead>
                            <tbody id="bodyPoliContrato">
                                <g:set var="total" value="${0}"/>
                                <g:each in="${ps}" var="i">
                                    <tr>
                                        %{--<td>${i?.subPresupuesto.descripcion}</td>--}%
                                        <td>${i?.numero}</td>
                                        <td>${i?.indice?.descripcion}</td>
                                        <td style="text-align: right; width: 40px">${g.formatNumber(number: i?.valor, maxFractionDigits: 3, minFractionDigits: 3)}</td>
                                        <g:set var="total" value="${total + (i?.valor ?: 0)}"/>
                                    </tr>
                                </g:each>
                            </tbody>
                            <tfoot>
                                <tr>
                                    <th colspan="2">TOTAL</th>
                                    <th style="text-align: right;">${g.formatNumber(number: total, maxFractionDigits: 3, minFractionDigits: 3)}</th>
                                </tr>
                            </tfoot>
                        </table>
                    </fieldset>
                </div>
            </div>

            <div id="tab-cuadrillaTipo" class="tab">

                <fieldset class="borde">
                    <legend>Cuadrilla Tipo</legend>
                    <table class="table table-bordered table-striped table-hover table-condensed" id="tablaCuadrilla">
                        <thead>
                            <tr>
                                %{--<th style="width: 260px; text-align: center">Subpresupuesto</th>--}%
                                <th style="width: 70px; text-align: center">Coeficiente</th>
                                <th style="width: 400px">Nombre del Indice (INEC)</th>
                                <th style="width: 70px">Valor</th>
                            </tr>
                        </thead>
                        <tbody id="bodyCuadrilla">
                            <g:set var="total" value="${0}"/>
                            <g:each in="${cuadrilla}" var="i">
                                <tr>
                                    %{--<td>${i?.subPresupuesto.descripcion}</td>--}%
                                    <td>${i?.numero}</td>
                                    <td>${i?.indice?.descripcion}</td>
                                    <td style="text-align: right; width: 40px">${g.formatNumber(number: i?.valor, maxFractionDigits: 3, minFractionDigits: 3)}</td>
                                    <g:set var="total" value="${total + (i?.valor ?: 0)}"/>
                                </tr>
                            </g:each>
                        </tbody>
                        <tfoot>
                            <tr>
                                <th colspan="2">TOTAL</th>
                                <th style="text-align: right;">${g.formatNumber(number: total, maxFractionDigits: 3, minFractionDigits: 3)}</th>
                            </tr>
                        </tfoot>
                    </table>
                </fieldset>
            </div>
        </div>
        <script type="text/javascript">
            $("#tabs").tabs({
                heightStyle : "fill"
            });
        </script>
    </body>
</html>