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
        <link href='${resource(dir: "css", file: "custom.css")}' rel='stylesheet' type='text/css'>
        <link href='${resource(dir: "font/open", file: "stylesheet.css")}' rel='stylesheet' type='text/css'>
        <link href='${resource(dir: "font/tulpen", file: "stylesheet.css")}' rel='stylesheet' type='text/css'>
        %{--TODO descargar estas fuentes--}%
    </head>

    <body>

        <g:form class="well form-horizontal span " action="validar" name="frmLogin" style="border: 5px solid #525E67;background: #202328;color: #939Aa2;width: 300px;position: relative;padding-left: 60px">
            <p class="css-vertical-text tituloGrande" style="left: 12px;;font-family: 'Tulpen One',cursive;font-weight: bold;font-size: 35px">Sistema Janus</p>

            <div class="linea" style="height: 95%;left: 45px"></div>
            <fieldset style="">
                <legend style="color: white;border:none;font-family: 'Open Sans Condensed', serif;font-weight: bolder;font-size: 25px">Ingreso</legend>

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
                    <a href="#" id="btnOlvidoPass" style="color: #ffffff;margin-left: 70px;text-decoration: none;font-family: 'Open Sans Condensed', serif;font-weight: bold">
                        Olvidó su contraseña?
                    </a>
                </div>
            </fieldset>
        </g:form>


    <!-- Modal -->
        <div class="modal hide fade" id="modal-pass">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle"></h3>
            </div>

            <div class="modal-body" id="modalBody">
            </div>

            <div class="modal-footer" id="modalFooter">
                <a href="#" data-dismiss="modal" class="btn">Cerrar</a>
                <a href="#" class="btn btn-success" id="btnSend"><i class="icon-ok"></i> Enviar</a>
            </div>
        </div>

        <script type="text/javascript">
            $(function () {

                $("#btnOlvidoPass").click(function () {
                    var p = $("<p>Ingrese el email registrado a su usuario y se le enviará una nueva contraseña para ingresar al sistema.</p>");
                    var div = $('<div class="control-group" id="divMail"/>');
                    var input = $('<input type="text" class="" id="email" placeholder="Email"/>');
                    div.append(input);

                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                    var btnSend = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Enviar</a>');

                    var send = function () {
                        var email = $.trim(input.val());
                        if (email != "") {
                            btnSend.replaceWith(spinner);
                            $.ajax({
                                type    : "POST",
                                url     : "${createLink(action:'olvidoPass')}",
                                data    : {
                                    email : email
                                },
                                success : function (msg) {
                                    var parts = msg.split("*");
                                    if (parts[0] == "OK") {
                                        $("#modalBody").addClass("alert alert-success");
                                    } else {
                                        $("#modalBody").addClass("alert alert-error");
                                    }
                                    $("#modalBody").html(parts[1]);
                                    spinner.remove();
                                }
                            });
                        } else {
                            $("#divMail").addClass("error");
                        }
                    };

                    btnSend.click(function () {
                        send();
                        return false;
                    });
                    input.keyup(function (ev) {
                        if (ev.keyCode == 13) {
                            send();
                        }
                    });

                    $("#modalTitle").html("Olvidó su contraseña?");
                    $("#modalBody").html("").append(p).append(div);
                    $("#modalFooter").html("").append(btnOk).append(btnSend);
                    $("#modal-pass").modal("show");
                });

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