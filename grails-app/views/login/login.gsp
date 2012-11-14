<%@ page import="janus.Unidad" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>

    <head>
        <meta name="layout" content="login">
        <title>Janus -Ingreso-</title>

        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
        %{--<link href='http://fonts.googleapis.com/css?family=Tulpen+One' rel='stylesheet' type='text/css'>--}%
        %{--<link href='http://fonts.googleapis.com/css?family=Open+Sans+Condensed:300' rel='stylesheet' type='text/css'>--}%
        <link href='${resource(dir:"css",file: "custom.css" )}' rel='stylesheet' type='text/css'>
        <link href='${resource(dir:"font/open",file: "stylesheet.css" )}' rel='stylesheet' type='text/css'>
        <link href='${resource(dir:"font/tulpen",file: "stylesheet.css" )}' rel='stylesheet' type='text/css'>
        %{--TODO descargar estas fuentes--}%
    </head>

    <body>

        <g:form class="well form-horizontal span " action="validar" name="frmLogin" style="border: 5px solid #525E67;background: #202328;color: #939Aa2;width: 300px;position: relative;padding-left: 60px">
            <p class="css-vertical-text tituloGrande" style="left: 12px;;font-family: 'Tulpen One',cursive;font-weight: bold;font-size: 35px">Sistema Janus</p>
            <div class="linea" style="height: 95%;left: 45px"></div>
            <fieldset style="">
                <legend style="color: white;border:none;font-family: 'Open Sans Condensed', serif;font-weight: bolder;font-size: 25px" >Ingreso</legend>

                <g:if test="${flash.message}">
                    <div class="alert alert-info" role="status">
                        <a class="close" data-dismiss="alert" href="#">×</a>
                        ${flash.message}
                    </div>
                </g:if>

                <div class="control-group" style="margin-top: 0">
                    <label class="control-label" for="login" style="width: 100%;text-align: left;font-size: 25px;font-family: 'Tulpen One',cursive;font-weight: bolder">Usuario:</label>

                    <div class="controls" style="width: 100%;margin-left: 5px">
                        <g:textField name="login" class="span2" style="width: 90%"/>
                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="login" style="width: 100%;text-align: left;font-size: 25px;font-family: 'Tulpen One',cursive;font-weight: bolder">Password:</label>

                    <div class="controls" style="width: 100%;margin-left: 5px">
                        <g:passwordField name="pass" class="span2" style="width: 90%"/>
                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="control-group">

                    <a href="#" class="btn btn-primary" id="btnLogin">Continuar</a>
                    <a href="" style="color: #ffffff;margin-left: 70px;text-decoration: none;font-family: 'Open Sans Condensed', serif;font-weight: bold">Olvidó su contraseña?</a>
                </div>
            </fieldset>
        </g:form>
    %{--<g:form class="well form-horizontal span" action="validar" name="frmLogin">--}%

        %{--<fieldset>--}%
            %{--<legend>Ingreso</legend>--}%

            %{--<g:if test="${flash.message}">--}%
                %{--<div class="alert alert-info" role="status">--}%
                    %{--<a class="close" data-dismiss="alert" href="#">×</a>--}%
                    %{--${flash.message}--}%
                %{--</div>--}%
            %{--</g:if>--}%

            %{--<div class="control-group">--}%
                %{--<label class="control-label" for="login">Usuario:</label>--}%

                %{--<div class="controls">--}%
                    %{--<g:textField name="login" class="span2" required=""/>--}%
                    %{--<p class="help-block ui-helper-hidden"></p>--}%
                %{--</div>--}%
            %{--</div>--}%

            %{--<div class="control-group">--}%
                %{--<label class="control-label" for="pass">Password:</label>--}%

                %{--<div class="controls">--}%
                    %{--<g:passwordField name="pass" class="span2" required=""/>--}%
                    %{--<p class="help-block ui-helper-hidden"></p>--}%
                %{--</div>--}%
            %{--</div>--}%

            %{--<div class="control-group">--}%
                %{--<label class="control-label" for="btnLogin">&nbsp;</label>--}%
                %{--<a href="#" class="btn btn-primary" id="btnLogin">Continuar</a>--}%
            %{--</div>--}%
        %{--</fieldset>--}%
    %{--</g:form>--}%

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
                    errorClass     : "label label-important"
                });

            });
        </script>

    </body>
</html>