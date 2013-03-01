<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/24/13
  Time: 3:55 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Valor de los Índices (2013)</title>
    </head>

    <body>
%{--
        <div class="span4" style="margin-left: 200px; margin-top: 20px">
            <fieldset class="span4" style="position: relative; height: 120px; float: left;padding: 10px;border-bottom: 1px solid black; border-top: 1px solid black">
--}%

            ${params.valorIndices}
            </fieldset>
%{--
        </div>
--}%

        <script type="text/javascript">


            $("#aceptar").click(function () {
                $("#frmUpload").submit();

                %{--var file = $("#file").val()--}%

                %{--//        console.log("---->>>"+file)--}%

                %{--$.ajax({--}%
                %{--type    : "POST",--}%
                %{--url     : "${createLink(action: 'uploadFile')}",--}%
                %{--data    :  {file: file},--}%
                %{--success : function (msg) {--}%


                %{--}--}%
                %{--});--}%

            });



        </script>

    </body>
</html>