<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Sub presupuesto ${subPre}</title>
    <link href="../font/open/stylesheet.css" rel="stylesheet" type="text/css"/>
    <link href="../font/tulpen/stylesheet.css" rel="stylesheet" type="text/css"/>
    <link href="../css/custom.css" rel="stylesheet" type="text/css"/>
    <link href="../css/font-awesome.css" rel="stylesheet" type="text/css"/>
    <style type="text/css">
    @page {
        size   : 21cm 29.7cm;  /*width height */
        margin : 2cm;
    }
    .hoja {
        /*background  : #e6e6fa;*/
        height      : 24.7cm; /*29.7-(1.5*2)*/
        font-family : arial;
        font-size   : 10px;
        width : 16cm;
    }

    .tituloPdf {
        height        : 100px;
        font-size     : 11px;
        /*font-weight   : bold;*/
        text-align    : center;
        margin-bottom : 5px;
        width: 95%;
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
        /*background : #000000 !important;*/
        /*color      : #FFFFFF !important;*/
    }

    thead tr{
        margin: 0px
    }
    th,td{
        font-size: 10px !important;
    }
    .row-fluid{
        width: 100%;
        height: 20px;
    }
    .span3{
        width: 29%;
        float: left;
        height: 100%;
    }
    .span8{
        width: 79%;
        float: left;
        height: 100%;
    }
    .span7{
        width: 69%;
        float: left;
        height: 100%;
    }


    </style>
</head>
<body>
<div class="hoja">

    <div class="tituloPdf">
        <p>
            <b>GOBIERNO DE LA PROVINCIA DE PICHINCHA </b>
        </p>
        <p>
            DEPARTAMENTO DE COSTOS
        </p>
        <p>
            ANALISIS DE PRECIOS UNITARIOS DEL SUBPRESUPUESTO: ${subPre.toUpperCase()}
        </p>
    </div>
    <div style="margin-top: 20px">
        <div class="row-fluid">
            <div class="span7">
                <b>Fecha:</b> ${new java.util.Date().format("dd-MM-yyyy")}
            </div>
            <div class="span3">
                <b>Fecha Act. P.U:</b> ${ obra.fechaPreciosRubros?.format("dd-MM-yyyy")}
            </div>
        </div>

    </div>
    <table class="table table-bordered table-striped table-condensed table-hover">
        <thead>
        <tr>
            <th style="width: 20px;">
                #
            </th>
            <th style="width: 80px;">
                Código
            </th>
            <th style="width: 600px;">
                Rubro
            </th>
            <th style="width: 60px" class="col_unidad">
                Unidad
            </th>
            <th style="width: 80px">
                Cantidad
            </th>
            <th class="col_precio" style=";">Unitario</th>
            <th class="col_total" style=";">C.Total</th>
        </tr>
        </thead>
        <tbody id="tabla_material">
        <g:set var="total" value="${0}"></g:set>
        <g:each in="${detalle}" var="vol" status="i">

            <tr class="item_row" id="${vol.id}" item="${vol.item.id}" sub="${vol.subPresupuesto.id}">
                <td style="width: 20px" class="orden">${vol.orden}</td>
                <td class="cdgo">${vol.item.codigo}</td>
                <td class="nombre">${vol.item.nombre.replaceAll("<","(Menor)").replaceAll(">","(Mayor)")}</td>
                <td style="width: 60px !important;text-align: center" class="col_unidad">${vol.item.unidad.codigo}</td>
                <td style="text-align: right" class="cant">
                    <g:formatNumber number="${vol.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                </td>
                <td class="col_precio" style=";text-align: right" id="i_${vol.item.id}"><g:formatNumber number="${precios[vol.id.toString()]}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
                <td class="col_total total" style=";text-align: right"><g:formatNumber number="${precios[vol.id.toString()]*vol.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
                <g:set var="total" value="${total.toDouble()+(precios[vol.id.toString()]*vol.cantidad)}"></g:set>
            </tr>

        </g:each>
        <tr>
            <td colspan="5"></td>
            <td><b>Total:</b></td>
            <td style="text-align: right"><g:formatNumber number="${total}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
        </tr>
        </tbody>
    </table>
</div>
</body>
</html>