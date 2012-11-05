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
    </head>

    <body>

        <g:form class="well form-horizontal span" action="savePer" name="frmLogin">

            <fieldset>
                <legend>Ingreso</legend>

                <g:if test="${flash.message}">
                    <div class="alert alert-info" role="status">
                        <a class="close" data-dismiss="alert" href="#">×</a>
                        ${flash.message}
                    </div>
                </g:if>

                <div class="control-group">
                    <label class="control-label" for="perfiles">Perfil:</label>

                    <div class="controls">
                        <g:select name="perfiles" from="${perfilesUsr}" class="span2" required="" optionKey="id"/>
                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="btnLogin">&nbsp;</label>
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
                    errorClass     : "label label-important"
                });

            });
        </script>

    </body>
</html>