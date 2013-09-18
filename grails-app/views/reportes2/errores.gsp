<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 8/30/13
  Time: 3:40 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>ERRORES</title>
    </head>

    <body>
        <div class="alert alert-error">
            <h3>Error</h3>
            ${flash.message}
        </div>
    </body>
</html>