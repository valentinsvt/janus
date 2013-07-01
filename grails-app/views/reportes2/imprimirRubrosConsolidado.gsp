<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>
        Reporte de rubros consolidado
    </title>
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
        font-family : serif;
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

    .theader {

        /*border: 1px solid #000000;*/
        border-bottom: 1px solid #000000;


    }

    .theaderup {

        /*border: 1px solid #000000;*/
        border-top: 1px solid #000000;


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

    .label{
        font-weight: bold;
        width: 110px;
        float: left;
        height: 20px;
    }
    .dato{
        width: 430px;
        float: left;
        height: 20px;
        text-align: left;
    }

    </style>
</head>
<body>
<div class="hoja">
    <div class="titulo" style="text-align: center;margin-bottom: 15px;">
        G.A.D. PROVINCIA DE PICHINCHA <br/>
        GESTIÓN DE PRESUPUESTOS <br/>
        ANÁLISIS DE PRECIOS UNITARIOS  <br/>
    </div>
    <div style="height: 45px">
        <div class="label">Fecha Act. P.U: </div> <div class="dato">${fecha.format("dd-MM-yyyy")}</div>
        <div class="label">% costos indirectos: </div> <div class="dato">${indi}</div>

    </div>
    <table class="table table-bordered table-striped table-condensed table-hover" style="width: 100%;">
        <thead>
        <tr>
            <th>CODIGO</th>
            <th>NOMBRE</th>
            <th>UNIDAD</th>
            <th>PRECIO</th>
        </tr>
        </thead>
        <tbody>
        <g:set var="total" value="${0}"></g:set>
        <g:each in="${datos}" var="rubro">
            <tr>
                <td>${rubro["codigo"]}</td>
                <td>${rubro["nombre"]}</td>
                <td>${rubro["unidad"]}</td>
                <td style="text-align: right"><g:formatNumber number="${rubro["total"]}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
                <g:set var="total" value="${total+rubro["total"]}"></g:set>
            </tr>
        </g:each>
        %{--<tr >--}%
            %{--<td colspan="3" style="border-top: 1px solid black !important;"><b>TOTAL</b></td>--}%
            %{--<td style="text-align: right;border-top: 1px solid black !important;"><b><g:formatNumber number="${total}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></b></td>--}%
        %{--</tr>--}%
        </tbody>
    </table>
</div>
</body>
</html>