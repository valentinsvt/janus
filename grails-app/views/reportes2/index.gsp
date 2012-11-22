<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 11/22/12
  Time: 12:56 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>reportes</title>
    </head>

    <body>
        <div id="list-concurso" class="span12" role="main" style="margin-top: 10px;">

            <a href="#" id="test">TEST</a>
        </div>

        <body>

            <script type="text/javascript">
                $("#test").click(function () {
                    var actionUrl = "${createLink(controller:'pdf',action:'pdfLink')}?filename=prueba.pdf&url=${createLink(action: 'test')}"
                    var url = actionUrl + "?cont=1Wemp=2";
                    location.href = url;
                });

            </script>
        </body>
</html>