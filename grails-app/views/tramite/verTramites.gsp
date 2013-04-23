<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
    <style type="text/css">

    .formato {
        font-weight : bolder;
    }

    .campo {
        font-weight: bold;
    }

    .error {
        background : #c17474;
    }
    </style>
    <title>Tramites</title>
</head>
<body>
<g:if test="${flash.message}">
    <div class="span12" style="height: 35px;margin-bottom: 10px;">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </div>
</g:if>
<div style="border-bottom: 1px solid black;padding-left: 50px;position: relative;margin-top: 20px;">
    <p class="css-vertical-text">Memo</p>
    <div class="linea" style="height: 100px;"></div>
    <div class="row-fluid">
        <div class="span1 campo">
            Documento:
        </div>
        <div class="span2">
            ${header["NMASTER"]}
        </div>
        <div class="span1 campo">
            Fecha:
        </div>
        <div class="span2">
            ${header["MFECHA"]}
        </div>
        <div class="span1 campo">
            Prioridad:
        </div>
        <div class="span2">
            ${header["MPRIORI"]}
        </div>
    </div>

    <div class="row-fluid">
        <div class="span1 campo">
            De:
        </div>
        <div class="span2">
            ${header["MDE"]}
        </div>
        <div class="span1 campo">
            Para:
        </div>
        <div class="span2">
            ${header["MPARA"]}
        </div>
    </div>
    <div class="row-fluid">
        <div class="span1 campo">
            Asunto:
        </div>
        <div class="span10">
            ${header["MASUNTO"]}
        </div>

    </div>

</div>
<div style="border-bottom: 1px solid black;padding-left: 50px;position: relative;margin-top: 20px;min-height: 150px;">
    <p class="css-vertical-text">Tramites</p>
    <div class="linea" style="height: 100px;"></div>
    <table  class="table table-bordered table-striped table-condensed table-hover" style="font-size: 10px !important;">
        <thead>
        <tr>
            <th>Tramite</th>
            <th>Fecha</th>
            <th>Limite</th>
            <th>Asunto</th>
            <th>Recibido</th>
            <th>Fec. Recibido</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${tramites}" var="t">
            <tr>
                <td>${t["NTRAMITE"]}</td>
                <td>${t["TFECHA"]}</td>
                <td>${t["TFLIMITE"]}</td>
                <td>${t["TASUNTO"]}</td>
                <td>${(t["TRECIBIDO"].toString()=="1")?"Si":"No"}</td>
                <td>${(t["TFRECEP"])?t["TFRECEP"]:""}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
%{--${memo}--}%
%{--${header}--}%
%{--${tramites}--}%
</body>
</html>