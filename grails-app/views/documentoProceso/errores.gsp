<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 9/9/13
  Time: 11:43 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Ha ocurrido un error</title>
    </head>

    <body>
        <div class="alert alert-error alert-block">
            ${flash.message}

            <br/><br/>${params.link ?: ""}
        </div>
    </body>
</html>