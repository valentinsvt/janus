<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 2/7/13
  Time: 3:57 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Planillas</title>
    </head>

    <body>
        <div class="tituloTree">
            Planillas del contrato de la obra ${obra.descripcion}
        </div>

        <div class="btn-toolbar">
            <div class="btn-group">
                <a href="${g.createLink(controller: 'contrato', action: 'registroContrato', params: [contrato: contrato?.id])}" class="btn btn-ajax btn-new" id="atras" title="Regresar al contrato">
                    <i class="icon-arrow-left"></i>
                    Regresar
                </a>
                <g:link action="form" class="btn" params="[contrato: contrato.id]">
                    <i class="icon-file"></i>
                    Nueva planilla
                </g:link>
            </div>
        </div>


        <table class="table table-bordered table-striped table-condensed table-hover">
            <thead>
                <tr>

                </tr>
            </thead>
            <tbody>
                <g:each in="${janus.ejecucion.Planilla.findAllByContrato(contrato)}" var="planilla">
                    <tr>
                        <td>
                            ${planilla.tipoPlanilla.nombre}
                        </td>
                    </tr>
                </g:each>
            </tbody>
        </table>

    </body>
</html>