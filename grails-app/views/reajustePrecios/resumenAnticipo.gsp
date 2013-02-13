<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <title>
        Rubros
    </title>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
</head>

<body>

<div class="span12">
    <g:if test="${flash.message}">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </g:if>
</div>

<div class="span12 btn-group" role="navigation">

    <a href="#" class="btn btn-ajax btn-new" id="imprimir" title="Imprimir">
        <i class="icon-print"></i>
        Imprimir
    </a>
    <a href="#" class="btn btn-ajax btn-new" id="excel" title="Imprimir">
        <i class="icon-print"></i>
        Excel
    </a>

</div>


<div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: -10px">

    <div style="border-bottom: 1px solid black;padding-left: 50px;position: relative;">

        <p class="css-vertical-text">Calculo de Bo</p>
        <div class="linea" style="height: 100px;"></div>
        <table class="table table-bordered table-striped table-condensed table-hover">
            <thead>
            <tr>
                <th colspan="2">Cuadrilla Tipo</th>
                <th colspan="2"> Oferta: ${fechaOferta}</th>
                <th colspan="2"> Variación: Anticipo <br>${fechaAnticipo}</th>
            </tr>
            </thead>
            <tbody>
                   %{--<g:each in="${datos}" var="dato">--}%
                       %{--<tr>--}%

                       %{--</tr>--}%
                   %{--</g:each>--}%
            </tbody>
        </table>
    </div>


</div>
</body>
</html>