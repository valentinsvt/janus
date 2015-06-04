<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Aseguradoras</title>
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

    <div class="hoja">

        <div class="tituloPdf">
            <p>
                <b>SEP - G.A.D. PROVINCIA DE PICHINCHA</b>
            </p>

            <p>
                COORDINACIÓN DE COMPRAS PÚBLICAS
            </p>

            <p>
                LISTA DE ASEGURADORAS
            </p>
        </div>

        <table class="table table-bordered table-striped table-condensed table-hover" style="border: 0px solid black">
            <thead>
                <tr style="font-size: 10px !important;">
                    <th>Nombre</th>
                    <th>Tipo</th>
                    <th>Fax</th>
                    <th>Teléfono</th>
                    <th>Mail</th>
                    <th>Responsable</th>
                    <th>Direccion</th>
                </tr>
            </thead>
            <tbody id="tabla">
                <g:each in="${asg}" var="a" status="i">

                    <tr class="item_row ${(i % 2 == 0) ? 'even' : 'odd'}">

                        <td>${a?.nombre}</td>
                        <td>${a.tipo?.descripcion}</td>
                        <td>${a.fax}</td>
                        <td>${a.telefonos}</td>
                        <td>${a.mail}</td>
                        <td>${a.responsable}</td>
                        <td>${a.direccion}</td>

                    </tr>

                </g:each>
            </tbody>
        </table>

    </div>
</html>