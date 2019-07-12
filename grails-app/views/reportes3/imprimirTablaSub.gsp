<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <g:if test="${subPre == -1}">
        <title>Todos los Subpresupuestos</title>
    </g:if>
    <g:else>
        <title>Sub presupuesto ${subPre}</title>
    </g:else>

    <link href="../font/open/stylesheet.css" rel="stylesheet" type="text/css"/>
    <link href="../font/tulpen/stylesheet.css" rel="stylesheet" type="text/css"/>
    <link href="../css/custom.css" rel="stylesheet" type="text/css"/>
    <link href="../css/font-awesome.css" rel="stylesheet" type="text/css"/>
    <style type="text/css">
    @page {
        size   : 21cm 29.7cm;  /*width height */
        margin : 2cm;
        margin-left: 2.0cm;
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

    table {
        border-collapse: collapse;
    }

    .theader{

        border-bottom: 1px solid #000000 !important;
        border-top: 1px solid #000000 !important;

    }

    .theaderBot th, .theaderBot td {
        border-bottom: 1px solid #000000 !important;
    }

    .theaderup th, .theaderup td {
        border-top: 1px solid #000000 !important;
    }

    .padTopBot th, .padTopBot td{
        padding-top: 7px !important;
        padding-bottom: 7px !important;
    }

    thead th{

        background : #FFFFFF !important;
        color: #000000 !important;
    }


    .num {
        text-align : right;
    }

    .header {
        background : #333333 !important;
        color      : #AAAAAA;
    }



    .total {
        /*background : #000000 !important;*/
        /*color      : #FFFFFF !important;*/
    }

    thead tr {
        margin : 0px
    }

    th, td {
        font-size : 10px !important;
        /*float: left;*/
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

    .fijo {

        table-layout: fixed;

    }

    .blanco{
       background: #ffffff !important;
       color: #000000 !important;
    }


    </style>
</head>

<body>
<div class="hoja">

<div class="tituloPdf">

    <p style="font-size: 18px">
        <b>SEP - G.A.D. PROVINCIA DE PICHINCHA</b>
    </p>

    %{--<p style="font-size: 14px">--}%
        %{--<b> ${obra?.departamento?.direccion?.nombre}</b>--}%
    %{--</p>--}%

    <p style="font-size: 14px">
        <b>DGCP - COORDINACIÓN DE FIJACIÓN DE PRECIOS UNITARIOS</b>
    </p>

    <p style="font-size: 14px">
        <g:if test="${subPre == -1}">
            <b>PRESUPUESTO</b>
        </g:if>
        <g:else>
            <b>SUBPRESUPUESTO: ${subPre.toUpperCase()}</b>
        </g:else>

    </p>
</div>

<div style="margin-top: 20px">

    <div class="row-fluid">
        <div class="span6" style="margin-right: 195px !important;">
            <b>Requirente:</b> ${obra?.departamento?.direccion?.nombre}
        </div>

    </div>

    <div class="row-fluid">
        <div class="span3" style="margin-right: 195px !important;">
            <b>Fecha:</b> ${fechaNueva}
        </div>

        <div class="span4">
            <b>Fecha Act. P.U:</b> ${fechaPU}
        </div>
    </div>
    <div class="row-fluid">
        <div class="span12" style="margin-right: 100px !important;">
            <b>Nombre:</b> ${obra?.nombre}
        </div>
    </div>

    <div class="row-fluid">
        <div class="span6" style="margin-right: 195px !important;">
            <b>Memorando de Cant. Obra:</b> ${obra?.memoCantidadObra}
        </div>
    </div>

    <div class="row-fluid">
        <div class="span6" style="margin-right: 195px !important;">
            <b>Código Obra:</b> ${obra?.codigo}
        </div>
    </div>
    <div class="row-fluid">
        <div class="span12" style="margin-right: 195px !important;">
            <b>Doc. Referencia:</b> ${obra?.referencia}
        </div>
    </div>

    <div class="row-fluid">

    </div>

</div>


<g:set var="total1" value="${0}"></g:set>
<g:set var="total2" value="${0}"></g:set>
<g:set var="totalPrueba" value="${0}"></g:set>
<g:set var="totales" value="${0}"></g:set>
<g:set var="totalPresupuesto" value="${0}"></g:set>
<g:if test="${subPre == -1}">
    <table class="table table-bordered table-striped table-condensed table-hover" style="width: 600px !important">
        <thead >
        <tr class="theaderBot theaderup padTopBot">
            %{--<th colspan="7">--}%

            <th style="width: 20px; text-align: center">
                N°
            </th>
            <th style="width: 90px; text-align: center" >
                RUBRO
            </th>

            <th style="width: 210px;">
                DESCRIPCIÓN
            </th>
            <th style="width: 40px; text-align: right">
                UNIDAD
            </th>
            <th style="width: 80px; text-align: right">
                CANTIDAD
            </th>
            <th class="col_precio " style="width:80px ; text-align: right">P. U.</th>
            <th class="col_total " style="width:80px; text-align: right">C.TOTAL</th>
        %{--</th>--}%
        </tr>
        </thead>
    </table>


    <g:each in="${subPres}" var="sp" status="sub">
        <table class="table table-bordered table-striped table-condensed table-hover" style="width: 600px !important">
            <thead >

            <tr>
                <th colspan="7" style="font-size: 14px; font-weight: bold; text-align: left">
                   ${sp.descripcion}
                </th>
            </tr>

            </thead>
            <tbody id="tabla_material">
            <g:set var="total" value="${0}"></g:set>

            <g:each in="${valores}" var="val" status="j">
                <g:if test="${val.sbpr__id == sp.id}">
                    <tr class="item_row" id="${val.item__id}" item="${val}" sub="${val.sbpr__id}">

                    %{--<td colspan="7">--}%
                    <td style="width: 20px; text-align: left" class="orden">${val.vlobordn}</td>

                    <td class="cdgo" style="width: 90px; text-align: left">
                        <g:if test="${val.rbrocdgo.size() > 15}">
                            ${val.rbrocdgo.trim().substring(0,11)}  <br/>${val.rbrocdgo.trim().substring(12,val.rbrocdgo.size()-1)}
                        </g:if>
                        <g:else>
                            ${val.rbrocdgo.trim()}
                        </g:else>
                    </td>

                        %{--<g:set var="nombre" value="${val?.rbronmbr?.trim()?.replaceAll('<', '(menor)')}"/>--}%

                        %{--<td>${nombre}</td>--}%

                        <td class="nombre">${val.rbronmbr}</td>

                        <td style="width: 40px;text-align: right" class="col_unidad">${val.unddcdgo.trim()}</td>
                    <td style="text-align: right; width: 80px" class="cant">
                        <g:formatNumber number="${val.vlobcntd}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                    </td>
                    <td class="col_precio" style="text-align: right; width: 80px" id="i_${val.item__id}"><g:formatNumber number="${val.pcun}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
                    <td class="col_total total" style="text-align: right; width: 80px"><g:formatNumber number="${val.totl}" format="##,##0"  minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
                    %{--</td>--}%


                    <g:set var="total" value="${total.toDouble() + val.totl}"></g:set>

                    <g:hiddenField name="totales" value="${totales = val.totl}"/>
                    <g:hiddenField name="totalPrueba" value="${totalPrueba = total2+=totales}"/>
                    <g:hiddenField name="totalPresupuesto" value="${totalPresupuesto = total1 += totales}"/>

                    </tr>
                </g:if>
            </g:each>
            <tr>
                <td style="text-align: right" colspan="6"><b>SUBTOTAL: </b></td>
                <td style="text-align: right"><b><g:formatNumber number="${total}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></b></td>
            </tr>

            </tbody>
        </table>
    </g:each>
    <table style="margin-top: 10px; font-size: 12px !important; width: 600px!important">
        <thead>

        </thead>
        <tbody>
        <tr>
            <td colspan="7" class="theaderBot theaderup padTopBot">
            <td style="text-align: right; width: 500px"><b>TOTAL PRESUPUESTO:  </b></td>
            <td style="text-align: right; width: 100px "><b><g:formatNumber number="${totalPresupuesto}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></b></td>
        </td>

        </tr>
        </tbody>
    </table>


    <div style="margin-top: 20px">
        <div class="row-fluid">
            <div class="span12" style="margin-right: 100px !important; font-size: 12px; margin-bottom: 15px">
                <b>  CONDICIONES DEL CONTRATO </b>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span6" style="margin-right: 100px !important;">
                <b>  Plazo de Ejecución: </b> <g:formatNumber number="${obra?.plazoEjecucionMeses}" format="##" locale="ec"/> mes(es)
            </div>
        </div>

        <div class="row-fluid">
            <div class="span6" style="margin-right: 195px !important;">
                <b> Anticipo: </b> <g:formatNumber number="${obra?.porcentajeAnticipo}" format="##,##0" locale="ec"/> %
            </div>
        </div>

        <div class="row-fluid">
            <div class="span6" style="margin-right: 195px !important;">
                <b>Elaboró: </b> ${obra?.responsableObra?.titulo ?: ''} ${obra?.responsableObra?.nombre ?: ''} ${obra?.responsableObra?.apellido ?: ''}
            </div>
        </div>
    </div>




</g:if>

<g:else>

    <table class="table table-bordered table-striped table-condensed table-hover">
        <thead>
        <tr class="theaderBot theaderup padTopBot">
            %{--<th colspan="7" >--}%
            <th style="width: 20px;">
                N°
            </th>
            <th style="width: 80px;">
                RUBRO
            </th>
            <th style="width: 550px;">
                DESCRIPCIÓN
            </th>
            <th style="width: 35px;">
                UNIDAD
            </th>
            <th style="width: 80px;">
                CANTIDAD
            </th>
            <th class="col_precio %{--theaderBot theaderup padTopBot--}%" style="width:80px ; text-align: right">P. U.</th>
            <th class="col_total %{-- theaderBot theaderup padTopBot--}%" style="width:80px; text-align: right">C.Total</th>
        %{--</th>--}%
        </tr>
        </thead>

        <tr class="item-row">
            <th colspan="7" style="font-size: 14px; font-weight: bold; text-align: left" class="blanco">
                ${subPre}
            </th>
        </tr>

        <tbody id="tabla_material">
        <g:set var="total" value="${0}"></g:set>
        <g:each in="${valores}" var="val" status="j">

            <tr class="item_row" id="${val.item__id}" item="${val}" sub="${val.sbpr__id}">

                <td style="width: 20px" class="orden">${val.vlobordn}</td>
                %{--<td></td>--}%
                %{--<td style="width: 200px" class="sub">${val.sbprdscr.trim()}</td>--}%
                <td class="cdgo">${val.rbrocdgo.trim()}</td>

                %{--<g:set var="nombre" value="${val?.rbronmbr?.trim()?.replaceAll('<', '(menor)')}"/>--}%

                %{--<td>${nombre}</td>--}%

                <td class="nombre">${val.rbronmbr}</td>

                <td style="width: 60px !important;text-align: center" class="col_unidad">${val.unddcdgo.trim()}</td>
                <td style="text-align: right" class="cant">
                    <g:formatNumber number="${val.vlobcntd}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                </td>
                <td class="col_precio" style="text-align: right; width: 80px" id="i_${val.item__id}"><g:formatNumber number="${val.pcun}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
                <td class="col_total total" style="text-align: right; width: 80px"><g:formatNumber number="${val.totl}" format="##,##0"  minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>

                <g:set var="total" value="${total.toDouble() + val.totl}"/>
            </tr>

        </g:each>

        <tr>
            <td colspan="5"></td>
            <td style="text-align: right"><b>TOTAL PRESUPUESTO:</b></td>
            <td style="text-align: right"><b><g:formatNumber number="${total}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></b></td>
        </tr>
        </tbody>
    </table>


</g:else>
</div>

<script type="text/javascript">

    console.log(${valores.rbrocdgo});

    //        cadena = 'pedro@hotmail.com';
    //        cadena = cadena.split('@');
    //        document.write(cadena[0]+'<br/>@'+cadena[1]);





</script>

</body>
</html>