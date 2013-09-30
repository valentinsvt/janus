<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 9/27/13
  Time: 11:40 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Reporte de avance</title>

        <script src="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.css')}" rel="stylesheet"/>
        <link href="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.customThemes.css')}" rel="stylesheet"/>

        <style type="text/css">
        .tabla {
            border-collapse : collapse;
        }

        .tabla td {
            vertical-align : middle;
            padding        : 9px 9px 0 9px;
        }
        </style>
    </head>

    <body>

        <g:if test="${flash.message}">
            <div class="row">
                <div class="span12">
                    <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                        <a class="close" data-dismiss="alert" href="#">×</a>
                        ${flash.message}
                    </div>
                </div>
            </div>
        </g:if>
        <div class="row">
            <div class="span9 btn-group" role="navigation">
                <g:link controller="contrato" action="verContrato" params="[contrato: contrato?.id]" class="btn" title="Regresar al contrato">
                    <i class="icon-arrow-left"></i>
                    Contrato
                </g:link>
            </div>
        </div>

        <fieldset>
            <legend>
                Fecha
            </legend>
            <elm:datepicker name="fecha" value="${new Date()}"/>
            <g:link class="btn btnVer" action="tablaAvance" id="${contrato.id}" style="margin-bottom:9px;">Ver</g:link>
        </fieldset>

        <fieldset class="hide" id="fsTextos">
            <legend>
                Textos
            </legend>

            <div id="divTextos"></div>
        </fieldset>

        <script type="text/javascript">
            $.jGrowl.defaults.closerTemplate = '<div>[ cerrar todo ]</div>';

            function log(msg, error) {
                var sticky = false;
                var theme = "success";
                if (error) {
                    sticky = true;
                    theme = "error";
                }
                $.jGrowl(msg, {
                    speed      : 'slow',
                    sticky     : sticky,
                    theme      : theme,
                    themeState : ''
                });
            }
            $(function () {
                $(".btnVer").click(function () {
                    var $fs = $("#fsTextos");
                    var $div = $("#divTextos");
                    $div.html("");
                    $fs.hide();
                    var url = $(this).attr("href");
                    $.ajax({
                        type    : "POST",
                        url     : url,
                        data    : {
                            fecha : $("#fecha").val()
                        },
                        success : function (msg) {
                            $div.html(msg);
                            $fs.show();
                        }
                    });
                    return false;
                });
            });
        </script>

    </body>
</html>