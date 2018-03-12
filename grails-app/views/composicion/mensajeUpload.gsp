<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Carga de archivo</title>
    </head>

    <body>
        <div class="well">
            <g:link class="btn btn-primary" controller="composicion" action="tabla" id="${obra}">Regresar a la composición de la obra</g:link>
            ${flash.message}
            <g:link class="btn btn-primary" controller="composicion" action="tabla" id="${obra}">Regresar a la composición de la obra</g:link>
        </div>
    </body>
</html>