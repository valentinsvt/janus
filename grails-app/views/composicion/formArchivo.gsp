<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 9/11/13
  Time: 3:17 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
        <title>Subir archivo excel composición</title>
    </head>

    <body>
        <g:if test="${flash.message}">
            <div class="alert alert-error">
                ${flash.message}
            </div>
        </g:if>

        <g:uploadForm action="uploadFile" method="post" name="frmUpload" id="${obra}">
            <div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: 0px">
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