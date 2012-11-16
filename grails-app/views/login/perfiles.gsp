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
    </head>

    <body>

        <g:form class="well form-horizontal span" action="savePer" name="frmLogin" style="border: 5px solid #525E67;background: #202328;color: #939Aa2;width: 300px;position: relative;padding-left: 70px">
            <p class="css-vertical-text tituloGrande" style="left: 12px;;font-family: 'Tulpen One',cursive;font-weight: bold;font-size: 35px">Sistema Janus</p>

            <div class="linea" style="height: 95%;left: 45px"></div>
            <fieldset>
                <legend style="color: white;border:none;font-family: 'Open Sans Condensed', serif;font-weight: bolder;font-size: 25px">Ingreso</legend>

                <g:if test="${flash.message}">
                    <div class="alert alert-info" role="status">
                        <a class="close" data-dismiss="alert" href="#">×</a>
                        ${flash.message}
                    </div>
                </g:if>

                <div class="control-group">
                    <label class="control-label" style="width: 50px;text-align: left;font-size: 25px;font-family: 'Tulpen One',cursive;font-weight: bolder;float: left;">Perfil:</label>

                    <div class="controls" style="width: 150px;margin-left: 5px;float: right;margin-right: 60px">
                        <g:select name="perfiles" from="${perfilesUsr}" class="span2" required="" optionKey="id"/>
                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="control-group">

                    <a href="#" class="btn btn-primary" id="btnLogin">Entrar</a>
                </div>
            </fieldset>
        </g:form>

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