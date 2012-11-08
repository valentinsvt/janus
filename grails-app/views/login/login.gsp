<%@ page import="janus.Unidad" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>

    <head>
        <meta name="layout" content="login">
        <title>Ingreso</title>

        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
        <style type="text/css">
            .titulo {
                text-align: center;
                font-size: 24px;
                font-style: italic;
                font-family: 'Times New Roman';
                padding-top: 20px;
                line-height: 30px;
            }
            .marco {
                width: 800px;
                height: 500px;
                margin: auto;
                margin-top:80px;
                background: #EDE8E3;
                border:medium solid #A5815F;
            }
            .texto {
                margin-left: 30px;
                margin-top: 30px;
                float: left;
                width: 400px;
                height: 400px;
                font-size: 14px;
                text-align: justify;
            }
        </style>
    </head>

    <body>

    <div class="esquinas marco" >
        <div class="titulo">Sistema de Control de Proyectos, Contratación, Ejecución y Seguimiento de Obras y Consultorías del GADPP</div>

        <div class="texto ui-corner-all">
            <ul><li><b>Precios unitarios y análisis de precios</b>: registro y mantenimiento de
                ítems y rubros. Análisis de precios, rendimientos y listas de precios...</li>
                <li><b>Obras</b> registro de Obras, georeferen-ciación, los volúmenes de obra,
                variables de transporte y costos indirectos ...</li>
                <li><b>Compras Públicas</b> plan anual de contrataciones, gestión de pliegos y
                control y seguimiento del PAC de obras ...</li>
                <li><b>Fiscalización</b> seguimiento a la ejecución de las obras: incio de obra,
                planillas, reajuste de precios, cronograma ...</li>
                <li><b>Reportes</b> formatos pdf, hoja de cálculo, texto plano y html.
                obras, concursos, contratos, contratistas, avance de obra...</li>
                <li><b>Oferentes ganadores</b> registro en línea los valores de precios unitarios,
                rubros, volúmenes de obra y cronograma de las ofertas</li>
            </ul>
            %{--<img src="${resource(dir: 'images', file: 'logo_app2.jpg')}" alt="Finix - Plan" >--}%
        </div>
        %{--    <div style=" width: 250px;height: 260px;margin-top: 60px;float: left;margin-left: 60px; ;background:#b1adaf"--}%
        <g:form class="well form-horizontal span" action="validar" name="frmLogin" style="margin-top: 80px;">

            <fieldset>
                <legend>Ingreso</legend>

                <g:if test="${flash.message}">
                    <div class="alert alert-info" role="status">
                        <a class="close" data-dismiss="alert" href="#">×</a>
                        ${flash.message}
                    </div>
                </g:if>

                <div class="control-group">
                    %{--<label class="control-label" for="login">Usuario:</label>--}%
                    <span style="width: 120px; text-align: right;">Usuario:</span>
                    <g:textField name="login" class="span2" required=""/>
                        <p class="help-block ui-helper-hidden"></p>
                </div>

                <div class="control-group">
                    Password:
                        <g:passwordField name="pass" class="span2" required=""/>
                        <p class="help-block ui-helper-hidden"></p>
                </div>

                <div class="control-group">
                    <label class="control-label" for="btnLogin">&nbsp;</label>
                    <a href="#" class="btn btn-primary" id="btnLogin">Continuar</a>
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
                    errorClass     : "label label-important"
                });

            });
        </script>

    </body>
</html>