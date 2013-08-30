<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 8/29/13
  Time: 11:32 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
        <title>Subir archivo excel PAC</title>

        <style type="text/css">
        .error {
            color            : darkred !important;
            background-color : #DDC2BE !important;
        }
        </style>

    </head>

    <body>

        <g:if test="${flash.message}">
            <div class="alert alert-error">
                ${flash.message}
            </div>
        </g:if>

        <g:uploadForm action="uploadFile" method="post" name="frmUpload">
            <div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: 0px">
                <div class="row-fluid" style="margin-left: 0px">
                    <div class="span4">
                        <div class="span3"><b>Requirente:</b></div>
                        <g:textField class="required" name="requirente" maxlength="100" style="width: 250px; font-size: 12px;"/>
                    </div>

                    <div class="span3">
                        <b>Memorando:</b>
                        <g:textField class="allCaps required" name="memo" maxlength="20" style="width: 156px; font-size: 12px;"/>
                    </div>

                    <div class="span4">
                        <b>Coordinación:</b>
                        <input type="hidden" id="item_id">
                        <g:select name="coordinacion" from="${janus.Departamento.list([order: 'descripcion'])}" optionKey="id" optionValue="descripcion" style="width: 250px;font-size: 12px;"/>
                    </div>
                </div>

                <div class="row-fluid" style="margin-left: 0px">
                    <div class="span4">
                        <div class="span3"><b>Archivo:</b></div>
                        <input type="file" class="required" id="file" name="file"/>
                    </div>
                </div>
            </div>

            <div class="row-fluid" style="margin-left: 0px">
                <div class="span4">
                    <a href="#" class="btn btn-success" id="btnSubmit">Subir</a>
                </div>
            </div>
        </g:uploadForm>

        <script type="text/javascript">
            $(function () {
                $("#frmUpload").validate({

                });

                $("#btnSubmit").click(function () {
                    if ($("#frmUpload").valid()) {
                        $(this).replaceWith(spinner);
                        $("#frmUpload").submit();
                    }
                });
            });
        </script>

    </body>
</html>