<%@ page import="janus.Lugar" contentType="text/html;charset=UTF-8" %>
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
        width: 120px;
        float: left;
        height: 20px;
        line-height: 20px;
    }
    .dato{

        float: left;
        height: 20px;
        text-align: left;
        line-height: 20px;;
    }
    .large{
        width: 240px;
    }
    .small{
        width: 120px;

    }


    .small2 {
        width: 150px;
    }
    .half{
        width: 50px;
    }

    </style>
</head>
<body>
<div class="hoja">
    <div class="titulo" style="text-align: center;margin-bottom: 15px;">
        SEP - G.A.D. PROVINCIA DE PICHINCHA <br/>
        GESTIÓN DE PRESUPUESTOS <br/>
        ANÁLISIS DE PRECIOS UNITARIOS  <br/>
    </div>
    <div style="height: 192px;border-bottom: 1px solid black;margin-bottom: 10px;">

        <div class="label">Fecha Act. P.U: </div> <div class="dato large">${fecha.format("dd-MM-yyyy")}</div>
        <div class="label">% costos indirectos: </div> <div class="dato small">${indi}</div>
        <div class="dato" style="width: 100%;font-weight: bold;border-bottom: black solid 1px;margin-bottom: 5px;border-top: 1px solid black">Listas de precios y distancias</div>
        <div class="label">Mano de obra y Equipos: </div> <div class="dato large">${listas[5]}</div>
        <div class="label"></div> <div class="dato small"></div>
        <div class="label">Canton: </div> <div class="dato large ">${listas[0]}</div>
        <div class="label half">Distancia: </div> <div class="dato small">${params.dsp0}</div>
        <div class="label">Especial: </div> <div class="dato large">${listas[1]}</div>
        <div class="label half">Distancia: </div> <div class="dato small ">${params.dsp1}</div>
        <g:if test="${listas[3]}">
            <div class="label">Mejoramiento: </div> <div class="dato large">${listas[3]}</div>
        </g:if>
        <g:else>
            <div class="label">Mejoramiento: </div> <div class="dato large">No seleccionó Mejoramiento</div>
        </g:else>
        <div class="label half">Distancia: </div> <div class="dato small">${params.dsv0}</div>
        <g:if test="${listas[2]}">
            <div class="label">Petreos Hormigones:</div> <div class="dato large">${listas[2]}</div>
        </g:if>
        <g:else>
            <div class="label">Petreos Hormigones:</div> <div class="dato large">No seleccionó Petreos Hormigones</div>
        </g:else>
        <div class="label half">Distancia: </div> <div class="dato small">${params.dsv1}</div>
        <g:if test="${listas[4]}">
            <div class="label">Carpeta Asfáltica: </div> <div class="dato large">${listas[4]}</div>
        </g:if>
        <g:else>
            <div class="label">Carpeta Asfáltica: </div> <div class="dato large">No seleccionó carpeta asfáltica</div>
        </g:else>
        <div class="label half">Distancia: </div> <div class="dato small">${params.dsv2}</div>
        <div class="dato" style="width: 100%;font-weight: bold;border-top: 1px solid black;height: 1px"></div>
        <g:if test="${params.chof != '-1'}">
            <div class="label">Chofer: </div> <div class="dato large">${janus.Item.get(params.chof).nombre} <b>($${params.prch.toDouble().round(2)})</b></div>
        </g:if>
        <g:else>
            <div class="label">Chofer: </div> <div class="dato large"> No seleccionado Chofer <b>($${params.prch.toDouble().round(2)})</b></div>
        </g:else>
        <g:if test="${params.volq != '-1'}">
            <div class="label half">Volquete: </div> <div class="dato small2">${janus.Item.get(params.volq).nombre} <b>($${params.prvl.toDouble().round(2)})</b></div>
        </g:if>
        <g:else>
            <div class="label half">Volquete: </div> <div class="dato small2"> No seleccionada Volqueta <b>($${params.prvl.toDouble().round(2)})</b></div>
        </g:else>



        %{--<div class="label">Distancia 2:</div> <div class="dato">${indi}</div>--}%

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
        <g:each in="${datos}" var="rubro" status="j">
            <tr>
                <td>${rubro["codigo"]}</td>
                %{--<td>${rubro["nombre"]}</td>--}%
                <td>${nombres[j]}</td>
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