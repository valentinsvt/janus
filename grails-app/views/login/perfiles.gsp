<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 2/10/12
  Time: 4:18 PM
  To change this template use File | Settings | File Templates.
--%>


<%@ page import="janus.seguridad.Sesn" contentType="text/html;charset=UTF-8" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>

<head>
    <meta name="layout" content="login">
    <title>Ingreso</title>

    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <link href='${resource(dir: "css", file: "custom.css")}' rel='stylesheet' type='text/css'>

    <style type="text/css">
    .titl {
        font-family: 'open sans condensed';
        font-weight: bold;
        text-shadow: -2px 2px 1px rgba(0, 0, 0, 0.25);
    <g:if test="${janus.Parametros.findByEmpresaLike(message(code: 'ambiente2'))}">
        color: #1a7031;
    </g:if>
    <g:else>
        color: #0088CC;
    </g:else>

        margin-top: 20px;
    }

    .borde{
    <g:if test="${janus.Parametros.findByEmpresaLike(message(code: 'ambiente2'))}">
        border: 5px solid #1a7031;
    </g:if>
    <g:else>
        border: 5px solid #2080B0;
    </g:else>


    }



    </style>

</head>

<body style="background-color: #d7d7d7">
<div class="dialog ui-corner-all" style="height: 595px;background: #d9d9d9;;padding: 10px;width: 910px; margin: auto;">
    %{--<g:form class="well form-horizontal span" action="savePer" name="frmLogin" style="border: 5px solid #2080B0; background:#c7c7c5;color: #939Aa2; width: 300px; margin-left: 240px; margin-top: 180px; position: relative; padding-left: 100px">--}%
    <div style="text-align: center;"><h1 class="titl" style="font-size: 32px;">${empr.empresa}</h1>
        <h1 class="titl" style="font-size: 24px;">${empr.nombre}</h1>
    </div>

    <g:form class="well form-horizontal span borde" action="savePer" name="frmLogin" style="background:#c7c7c5;color: #939Aa2; width: 300px; margin-left: 240px; margin-top: 80px; position: relative; padding-left: 100px">
        <p class="css-vertical-text" style="left: 12px;;font-family: 'Tulpen One',cursive;font-weight: bold;font-size: 35px; color:#334;">Sistema SEP - GADPP</p>

        <div class="linea" style="height: 95%;left: 45px; border-left-color: #334"></div>
        <fieldset>
            <legend style="color: white;border:none;font-family: 'Open Sans Condensed', serif;font-weight: bolder;font-size: 25px; color:#334;">Ingreso</legend>

            <g:if test="${flash.message}">
                <div class="alert alert-info" role="status">
                    <a class="close" data-dismiss="alert" href="#">×</a>
                    ${flash.message}
                </div>
            </g:if>

            <div class="control-group">
                <label class="control-label" style="width: 50px;text-align: left;font-size: 25px;font-family: 'Tulpen One',cursive;font-weight: bolder;float: left; color:#334">Perfil:</label>

                <div class="controls" style="width: 150px;margin-left: 5px;float: right;margin-right: 60px">
                    <g:select name="perfiles" from="${perfilesUsr}" class="span2" required="" optionKey="id" style="width: 180px;"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">

                <a href="#" class="btn btn-primary" id="btnLogin">Entrar</a>
            </div>
        </fieldset>
    </g:form>
</div>
<script type="text/javascript">
    $(function () {

        $("input").keypress(function (ev) {
            if (ev.keyCode == 13) {
                $("#frmLogin").submit();
            }
        });

        $("#btnLogin").click(function () {
            $("#frmLogin").submit();
            return false;
        });

        $("#frmLogin").validate({
            errorPlacement : function (error, element) {
                element.parent().find(".help-block").html(error).show();
            },
            success        : function (label) {
                label.parent().hide();
            },
            errorClass     : "label label-important",
            submitHandler  : function (form) {
                $("#btnLogin").replaceWith(spinnerLogin);
                form.submit();
            }
        });

    });
</script>

</body>
</html>