<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 11/22/12
  Time: 12:59 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>PAC</title>
        %{--<link href="../../css/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css"/>--}%
        %{--<link href="../../css/bootstrap/css/bootstrap-responsive.css" rel="stylesheet" type="text/css"/>--}%
        %{--<link href="../../font/open/stylesheet.css" rel="stylesheet" type="text/css"/>--}%
        %{--<link href="../../font/tulpen/stylesheet.css" rel="stylesheet" type="text/css"/>--}%
        %{--<link href="../../css/custom.css" rel="stylesheet" type="text/css"/>--}%
        %{--<link href="../../css/font-awesome.css" rel="stylesheet" type="text/css"/>--}%
        %{--<link href="../css/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css"/>--}%
        <link href="../font/open/stylesheet.css" rel="stylesheet" type="text/css"/>
        <link href="../font/tulpen/stylesheet.css" rel="stylesheet" type="text/css"/>
        <link href="../css/custom.css" rel="stylesheet" type="text/css"/>
        <link href="../css/font-awesome.css" rel="stylesheet" type="text/css"/>
        <style type="text/css">
        @page {
            size   : 21cm 29.7cm;  /*width height */
            margin : 2cm;
        }

        body {
            background : none !important;
        }

        .hoja {
            /*background  : #e6e6fa;*/
            height      : 24.7cm; /*29.7-(1.5*2)*/
            font-family : arial;
            font-size   : 10px;
            width       : 16cm;
        }

        .tituloPdf {
            height        : 100px;
            font-size     : 11px;
            /*font-weight   : bold;*/
            text-align    : center;
            margin-bottom : 5px;
            width         : 95%;
            /*font-family       : 'Tulpen One', cursive !important;*/
            /*font-family : "Open Sans Condensed" !important;*/
        }

        .totales {
            font-weight : bold;
        }

        .num {
            text-align : right;
        }

        .header {
            background : #333333 !important;
            color      : #AAAAAA;
        }

            /*th {*/
            /*background : #cccccc;*/
            /*}*/

            /*tbody tr:nth-child(2n+1) {*/
            /*background : none repeat scroll 0 0 #E1F1F7;*/
            /*}*/

            /*tbody tr:nth-child(2n) {*/
            /*background : none repeat scroll 0 0 #F5F5F5;*/
            /*}*/
        thead tr {
            margin : 0px
        }

        th, td {
            font-size : 10px !important;
        }

        .row-fluid {
            width  : 100%;
            height : 20px;
        }

        .span3 {
            width  : 29%;
            float  : left;
            height : 100%;
        }

        .span8 {
            width  : 79%;
            float  : left;
            height : 100%;
        }

        .span7 {
            width  : 69%;
            float  : left;
            height : 100%;
        }

        .even {
            background-color : #bbb;
        }

        td {
            margin : 0px !important;
            border : none !important;

        }

        </style>
    </head>

    <body>
        <div class="hoja">

            <div class="tituloPdf">
                <p>
                    <b>SEP - G.A.D. PROVINCIA DE PICHINCHA</b>
                </p>

                <p>
                    COORDINACIÓN DE COMPRAS PÚBLICAS
                </p>

                <p>
                    PLAN ANUAL DE COMPRAS PÚBLICAS
                </p>
            </div>

            <div class="row-fluid" style="margin-left: 0px;margin-bottom: 10px;">

                <div class="span4">
                    <b>Coordinación:</b> ${dep}
                </div>

                <div class="span2">
                    <b>Año:</b> ${anio}
                </div>

            </div>
            <table class="table table-bordered table-striped table-condensed table-hover" style="border: 0px solid black">
                <thead>
                    <tr style="font-size: 10px !important;">
                        <th style="width: 30px">#</th>
                        <th style="width: 40px">Año</th>
                        <th style="width: 100px">Partida</th>
                        <th style="width: 50px;">CCP</th>
                        <th style="width: 50px;">Tipo <br/>Compra</th>
                        <th style="width: 300px;">Descripción</th>
                        <th style="width: 40px;">Cant.</th>
                        <th style="width: 40px">Unidad</th>
                        <th style="width: 60px;">Costo <br/>Unitario</th>
                        <th style="width: 60px">Costo <br/>Total</th>
                        <th style="width: 35px;">C1</th>
                        <th style="width: 35px;">C2</th>
                        <th style="width: 35px;">C3</th>
                    </tr>
                </thead>
                <tbody id="tabla_pac">
                    <g:set var="total" value="${0}"></g:set>
                    <g:each in="${pac}" var="p" status="i">

                        <tr class="item_row ${(i % 2 == 0) ? 'even' : 'odd'}">
                            <td style="text-align: center;">${i + 1}</td>
                            <td style="width: 40px;" class="anio">${p.anio.anio}</td>
                            <td class="prsp">${p.presupuesto.numero}</td>
                            <td class="cpac">${p.cpp?.numero}</td>
                            <td class="tipo">${p.tipoCompra.descripcion}</td>
                            <td class="desc">${p.descripcion}</td>
                            <td style="text-align: right" class="cant">
                                <g:formatNumber number="${p.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                            </td>
                            <td style="width: 40px !important;text-align: center" class="unidad">${p.unidad.codigo}</td>
                            <td style="text-align: right" class="costo"><g:formatNumber number="${p.costo}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
                            <td style="text-align: right" class="total"><g:formatNumber number="${p.cantidad * p.costo}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
                            <g:set var="total" value="${total + p.cantidad * p.costo}"></g:set>
                            <td style="text-align: center" class="c1">${p.c1}</td>
                            <td style="text-align: center" class="c2">${p.c2}</td>
                            <td style="text-align: center" class="c3">${p.c3}</td>
                        </tr>

                    </g:each>
                </tbody>
            </table>

            <div style="width: 99.7%;height: 30px;overflow-y: auto;float: right;text-align: right;margin-top: 10px" id="total">
                <b>TOTAL:</b>

                <div id="divTotal" style="width: 150px;float: right;height: 30px;font-weight: bold;font-size: 12px;margin-right: 20px">
                    <g:formatNumber number="${total}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                </div>
            </div>

        </div>
    </body>
</html>