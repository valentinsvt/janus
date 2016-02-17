<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Rubros</title>
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
        font-family : serif;
        font-size   : 10px;
        width       : 16cm;
    }

    .tituloPdf {
        height        : 100px;
        font-size     : 11px;
        /*font-weight   : bold;*/
        text-align    : center !important;
        margin-bottom : 5px;
        width         : 95%;
        /*margin-top:    130px;*/

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
        border-bottom : 1px solid #000000;

    }

    .theaderup {

        /*border: 1px solid #000000;*/
        border-top : 1px solid #000000;

    }

    .padTopBot{

        padding-top: 7px !important;
        padding-bottom: 7px !important;

    }

    .tituloHeader{

        font-size: 14px !important;

    }

    .marginTop{

        margin-top:20px !important;
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

    .divRubro {
        page-break-after : always !important;
        /*display: block !important;*/
        /*margin-bottom: 300px !important;*/
        /*margin-top: 100px !important*/
    }
    .espacioFooter {

        /*margin-bottom: 500px !important;*/
    }


    </style>
</head>

<body>
<div class="hoja">
    ${html}
</div>
</body>
</html>
