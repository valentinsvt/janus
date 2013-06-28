<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 6/28/13
  Time: 3:22 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Errores de índices</title>
    </head>

    <body>
        <g:if test="${alertas != ''}">
            <div class="alert alert-warning alert-block">
                <h3>Se han generado alertas al generar la planilla</h3>
                ${alertas}
            </div>
        </g:if>
        <g:if test="${errores != ''}">
            <div class="alert alert-error alert-block">
                <h3>Han ocurrido errores graves al generar la planilla</h3>
                ${errores}
            </div>
        </g:if>
    </body>
</html>