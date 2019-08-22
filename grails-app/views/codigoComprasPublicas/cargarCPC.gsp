<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 20/08/19
  Time: 13:32
--%>

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
    <title>Subir archivo CPC</title>

    <style type="text/css">
    .error {
        color            : darkred !important;
        background-color : #DDC2BE !important;
    }
    </style>

</head>

<body>

<g:if test="${flash.message}">
    <div class="alert alert-info">
        ${flash.message}
    </div>
</g:if>

<g:uploadForm action="uploadFile" method="post" name="frmUpload">
    <div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: 0px">
        <div class="row-fluid" style="margin-left: 0px">
            <div class="span6">
                <div class="span3"><b>Fecha de Registro:</b></div>
                <div class="span3">
                    <elm:datepicker name="fecha" class="fechaRegistro datepicker input-small required"
                                    value="${new java.util.Date()}" title="Fecha Registro"/>
                </div>

            </div>
        </div>

        <div style="font-weight: bold">
            *Formato del archivo EXCEL
        </div>

        <div class="row-fluid" style="margin-left: 0px">
            <div class="span12">
                <div class="span6">
                    <table class="table table-bordered table-condensed">
                        <thead>
                        <tr>
                            <th>A</th>
                            <th>B</th>
                        </tr>
                        </thead>
                        <tbody class="centrado">
                        <tr style="text-align: center">
                            <td style="text-align: center">CÓDIGO</td>
                            <td style="text-align: center">DESCRIPCIÓN</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
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