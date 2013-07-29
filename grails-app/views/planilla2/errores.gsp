<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 7/22/13
  Time: 12:28 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>ERROR</title>
    </head>

    <body>
        <div class="alert alert-error" style="min-height: 100px;">
            <h3>Ha ocurrido un error</h3>

            %{--<p>--}%
            <i class="icon-warning-sign icon-2x pull-left"></i>
            <g:if test="${flash.message}">
                ${flash.message}
            </g:if>
            %{--</p>--}%
        </div>
    </body>
</html>