<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Rubro :${rubro.codigo}</title>
    <link href="../font/open/stylesheet.css" rel="stylesheet" type="text/css"/>
    <link href="../font/tulpen/stylesheet.css" rel="stylesheet" type="text/css"/>
    <link href="../css/custom.css" rel="stylesheet" type="text/css"/>
    <link href="../css/font-awesome.css" rel="stylesheet" type="text/css"/>
    <style type="text/css">
    @page {
        /*size   : 21cm 29.7cm;  *//*width height */
        size   : 29.7cm 21cm;  /*width height */
        margin : 2cm;
        margin-left: 2.0cm;
    ;
    }

    body {
        background : none !important;
    }

    .hoja {
        /*background  : #e6e6fa;*/
        height      : 24.7cm; /*29.7-(1.5*2)*/
        font-family : arial;
        font-size   : 10px;
        width       : 25cm;
    }

    .tituloPdf {
        height        : 60px;
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


    thead tr {
        margin : 0px;


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

    .padTopBot{

        padding-top: 7px !important;
        padding-bottom: 7px !important;

    }

    .marginTop{

        margin-top:20px !important;
    }

    .tituloHeader{
        font-size: 14px !important;
    }




    thead th{

        background : #FFFFFF !important;
        color: #000000 !important;


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

    <div class="tituloPdf tituloHeader">
        <p style="font-size: 18px">
            <b>SEP - G.A.D. PROVINCIA DE PICHINCHA</b>
        </p>

        <p style="font-size: 14px; margin-top: -15px;">
            <b>DGCP - COORDINACIÓN DE FIJACIÓN DE PRECIOS UNITARIOS</b><br/>
            <b>ANÁLISIS DE PRECIOS UNITARIOS</b>
        </p>
    </div>

    <div style="margin-top: 0px">
        <div class="row-fluid">
            <div class="span3" style="margin-right: 195px !important; width: 500px;">
                <g:if test="${fechaPala}">
                    <b>Fecha:</b> ${fechaPala.format("dd-MM-yyyy")}

                </g:if>
                <g:else>
                    <b>Fecha:</b>
                </g:else>

            </div>
            <div class="span3" style="width: 200px;">
                <g:if test="${fechaPrecios}">
                    <b>Fecha Act. P.U:</b> ${fechaPrecios.format("dd-MM-yyyy")}
                </g:if>
                <g:else>
                    <b>Fecha Act. P.U:</b>
                </g:else>
            </div>
        </div>


        <div class="row-fluid">
            <div class="span3" style="margin-right: 0px !important; width: 400px;">
                <b>Código de rubro:</b> ${rubro?.codigo}
            </div>

            <div class="span3" style="margin-right: 0px !important; width: 300px;">
                <b>Código de especificación:</b> ${rubro?.codigoEspecificacion}
            </div>

            <div class="span3" style="width: 100px;">

                <b>Unidad:</b> ${rubro?.unidad?.codigo}
            </div>

        </div>


        <div class="row-fluid">
            <div class="span12">
                %{--<g:set var="nombre" value="${rubro.nombre.replaceAll('<', '(menor)')}"></g:set>--}%
                <b>Descripción:</b> ${rubro?.nombre}
            </div>
        </div>
    </div>

    <div style="width: 100%">

        ${tablaHer}
        ${tablaMano}
        ${tablaMat}
        <g:if test="${bandMat != 1}">
            ${tablaMat2}
        </g:if>
        ${tablaTrans}
        <g:if test="${band == 0 && bandTrans == '1'}">

            ${tablaTrans2}
        </g:if>
        ${tablaIndi}
        <table class="table table-bordered table-striped table-condensed table-hover" style="margin-top: 25px; width: 600px;float: right;  border-top: 1px solid #000000;  border-bottom: 1px solid #000000;">
            <tbody>
            <tr>
                <td style="width: 240px; border-bottom: #000000">
                    <b>COSTO UNITARIO DIRECTO</b>
                </td>
                <td style="text-align: right; width: 50px;">
                    <b> <g:formatNumber number="${totalRubro}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></b>
                </td>
                <td style="width: 120px; text-align: center">
                    <b> <g:formatNumber number="${totalRelativo}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></b>
                </td>
                <td style="width: 100px"> </td>
                <td style="text-align: right; width: 40px;">
                    <b> <g:formatNumber number="${totalVae}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></b>
                </td>
            </tr>
            <tr>
                <td>
                    <b>COSTOS INDIRECTOS</b>
                </td>
                <td style="text-align: right;">
                    <b> <g:formatNumber number="${totalIndi}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></b>
                </td>
                <td style="text-align: center"><b>TOTAL</b></td>
                <td> </td>
                <td style="text-align: center">
                    <b>TOTAL</b>
                </td>
            </tr>
            <tr>
                <td>
                    <b>COSTO TOTAL DEL RUBRO</b>
                </td>
                <td style="text-align: right">
                    <b>  <g:formatNumber number="${totalRubro + totalIndi}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></b>
                </td>
                <td style="text-align: center">
                    <b>PESO</b>
                </td>
                <td> </td>
                <td style="text-align: center">
                    <b>VAE</b>
                </td>

            </tr>
            <tr>
                <td>
                    <b>PRECIO UNITARIO $USD</b>
                </td>
                <td style="text-align: right">
                    <b><g:formatNumber number="${(totalRubro + totalIndi).toDouble().round(2)}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></b>

                </td>
                <td style="text-align: center">
                    <b>RELATIVO (%)</b>
                </td>
                <td> </td>
                <td style="text-align: center">
                    <b>(%)</b>
                </td>
            </tr>


            </tbody>
        </table>

    </div>
    <table style="margin-top: 130px">
        <tbody>
        <div>
            <g:set var="dist" value="${rubro?.codigo?.split("-")}"/>
            <g:if test="${dist[0] == 'TR'}">
                <b>Distancia a la escombrera:</b> D= ${obra?.distanciaDesalojo} KM
            </g:if>
        </div>
        <div>
            <b>Nota:</b> Los cálculos se hacen con todos los decimales y el resultado final se lo redondea a dos decimales.
        </div>
        </tbody>
    </table>
</div>
</body>

</html>