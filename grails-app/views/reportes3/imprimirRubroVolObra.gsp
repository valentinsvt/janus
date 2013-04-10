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
        <title>Rubro :${rubro.codigo}</title>
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

        .total {
            background : #000000 !important;
            color      : #FFFFFF !important;
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


        </style>
    </head>

    <body>
        <div class="hoja">

            <div class="tituloPdf">
                <p>
                    <b>GOBIERNO DE LA PROVINCIA DE PICHINCHA</b>
                </p>

                <p>
                    DEPARTAMENTO DE COSTOS
                </p>

                <p>
                    ANALISIS DE PRECIOS UNITARIOS DE PRESUPUESTO
                </p>
            </div>

            <div style="margin-top: 20px">
                <div class="row-fluid">
                    <div class="span7">
                        <b>Fecha:</b> ${new java.util.Date().format("dd-MM-yyyy")}
                    </div>

                    <div class="span3">
                        <b>Fecha Act. P.U:</b> ${fechaPrecios?.format("dd-MM-yyyy")}
                    </div>
                </div>

                <div class="row-fluid">
                    <div class="span7">
                        <b>Código:</b> ${rubro.codigo}
                    </div>

                    <div class="span3">
                        <b>Unidad:</b> ${rubro.unidad.codigo}
                    </div>
                </div>

                <div class="row-fluid">
                    <div class="span8">
                        <g:set var="nombre" value="${rubro.nombre.replaceAll('<', '(menor)')}"></g:set>
                        <g:set var="nombre" value="${rubro.nombre.replaceAll('<', '(mayor)')}"></g:set>
                        <b>Descripción:</b> ${nombre}
                    </div>
                </div>
            </div>

            <div style="width: 100%">

                ${tablaHer}
                ${tablaMano}
                ${tablaMat}
                ${tablaTrans}
                ${tablaIndi}
                <table class="table table-bordered table-striped table-condensed table-hover" style="margin-top: 40px;width: 50%;float: right;">
                    <tbody>
                        <tr>
                            <td style="width: 350px;">
                                <b>Costo unitario directo</b>
                            </td>
                            <td style="text-align: right">
                                <g:formatNumber number="${totalRubro}" format="##,#####0" minFractionDigits="5" maxFractionDigits="7" locale="ec"/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <b>Costos indirectos</b>
                            </td>
                            <td style="text-align: right">
                                <g:formatNumber number="${totalIndi}" format="##,#####0" minFractionDigits="5" maxFractionDigits="7" locale="ec"/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <b>Costo total del rubro</b>
                            </td>
                            <td style="text-align: right">
                                <g:formatNumber number="${totalRubro + totalIndi}" format="##,#####0" minFractionDigits="5" maxFractionDigits="7" locale="ec"/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <b>Precio unitario</b>
                            </td>
                            <td style="text-align: right"><g:formatNumber number="${(totalRubro + totalIndi).toDouble().round(2)}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>

                            </td>
                        </tr>

                    </tbody>
                </table>
            </div>

        </div>
    </body>
</html>