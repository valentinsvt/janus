<%--
  Created by IntelliJ IDEA.
  User: svt
  Date: 8/27/13
  Time: 4:12 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>

    <meta name="layout" content="main">
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">

    <script src="${resource(dir: 'js/jquery/plugins/editable/bootstrap-editable/js', file: 'bootstrap-editable.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/editable/bootstrap-editable/css', file: 'bootstrap-editable.css')}" rel="stylesheet"/>

    <script src="${resource(dir: 'js/jquery/plugins/editable/inputs-ext/coords', file: 'coords.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/editable/inputs-ext/coords', file: 'coords.css')}" rel="stylesheet"/>

    <style type="text/css">

    .formato {
        font-weight : bolder;
    }

    .titulo {
        font-size : 20px;
    }

    .error {
        background : #c17474;
    }

    .mover {

    }

    .editable {
        border-bottom : 1px dashed;
    }

    .error {
        background  : inherit !important;
        border      : solid 2px #C17474;
        font-weight : bold;
        padding     : 10px;
    }
    </style>

    <title>PAGOS</title>
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
<div class="row">
    <bsc:buscador name="planillas" value="" accion="buscarPlanilla" controlador="planilla2" campos="${campos}" label="Planillas" tipo="lista"/>
</div>

</body>
</html>