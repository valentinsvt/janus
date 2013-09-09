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

            <br/><br/><g:link class="btn btn-danger" controller="planilla" action="list" id="${params.id}">Regresar</g:link>
        </div>
    </body>
</html>