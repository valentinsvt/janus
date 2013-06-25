<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 2/22/13
  Time: 5:05 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Planilla</title>
        <style type="text/css">
        @page {
            size   : 21cm 29.7cm ;  /*width height */
            margin : 1.5cm;
        }

        html {
            font-family : Verdana, Arial, sans-serif;
            font-size   : 8px;
        }

        .hoja {
            width      : 17.5cm;
            /*background : #ffebcd;*/
            /*border     : solid 1px black;*/
            min-height : 200px;
        }

        h1, h2, h3 {
            text-align : center;
        }

        h1 {
            font-size : 14px;
        }

        h2 {
            font-size : 12px;
        }

        table {
            border-collapse : collapse;
            width           : 100%;
            border          : solid 1px black;
        }

        td, th {
            border : solid 1px black;
        }

        th, td {
            vertical-align : middle;
        }

        th {
            background : #bbb;
            font-size  : 10px;
        }

        td {
            font-size : 8px;
        }

        .even {
            background : #ddd;
        }

        .odd {
            background : #efefef;
        }

        .strong {
            font-weight : bold;
        }

        table {
            border-collapse : collapse;
        }

        .tright {
            text-align : right;
        }

        .tcenter {
            text-align : center;
        }

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
            /*padding-left  : 50px;*/
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

        .row {
            margin-left : -20px;
        }

        .span5 {
            width : 250px;
            float : left;
        }

        .span3 {
            width : 120px;
            float : left;
        }

        .span2 {
            width : 70px;
            float : left;
        }

        .span1 {
            width : 60px;
            float : left;
        }

        .well {
            min-height       : 20px;
            padding          : 19px;
            margin-bottom    : 20px;
            background-color : #f5f5f5;
            border           : 1px solid #e3e3e3;
        }

        .noborder, table.noborder, table.noborder td, table.noborder th {
            border : none !important;
        }
        </style>
    </head>

    <body>
        <div class="hoja">
            <elm:headerPlanillaReporte planilla="${planilla}"/>

            <div class="area">

                <h2>Cálculo de B<sub>0</sub></h2>

                <table border="1" style="">
                    <thead>
                        <tr>
                            <th colspan="2">Cuadrilla Tipo</th>
                            <th>Oferta</th>
                            <th class="nb">${oferta.fechaEntrega.format("MMM-yy")}</th>
                            <th>Variación</th>
                            <th class="nb">Anticipo <br/>${planilla.fechaPresentacion.format("MMM-yy")}</th>
                            <g:if test="${periodos.size() > 2}">
                                <g:each in="${2..periodos.size() - 1}" var="per">
                                    <th>Variación</th>
                                    <th class="nb">${periodos[per].fechaInicio.format("MMM-yy")}</th>
                                </g:each>
                            </g:if>
                        </tr>
                    </thead>
                    ${tbodyB0}
                </table>
            </div> <!-- B0 -->
            <div class="area">

                <h2>Cálculo de P<sub>0</sub></h2>

                <table border="1" style="margin-top: 10px;">
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
                    ${tbodyP0}
                </table>
            </div> <!-- P0 -->

            <div class="area" style="min-height: 190px; margin-bottom: 30px; border: none;">

                <h2>Cálculo de F<sub>r</sub> y P<sub>r</sub></h2>

                <table border="1" style=" margin-top: 10px; margin-bottom: 10px;">
                    <thead>
                        <tr>
                            <th rowspan="2">Componentes</th>
                            <th>Oferta</th>
                            <th colspan="${periodos.size() - 1}">Periodo de variación y aplicación de fórmula polinómica</th>
                        </tr>
                        <tr>
                            <th>${oferta.fechaEntrega.format("MMM-yy")}</th>
                            <th>Anticipo <br/>${planilla.fechaPresentacion.format("MMM-yy")}</th>
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
                    ${tbodyFr}
                </table>

            </div> <!-- Fr y Pr -->

        </div>
    </body>
</html>