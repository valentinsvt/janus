<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 4/30/13
  Time: 4:39 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="janus.RolTramite; janus.Tramite" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Trámites
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
    </head>

    <body>

        <g:if test="${flash.message}">
            <div class="row">
                <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                    <a class="close" data-dismiss="alert" href="#">×</a>
                    ${flash.message}
                </div>
            </div>
        </g:if>

    %{--<div class="row btn-group" role="navigation">--}%
    %{--<a href="#" class="btn btn-ajax btn-new">--}%
    %{--<i class="icon-file"></i>--}%
    %{--Nuevo Trámite--}%
    %{--</a>--}%
    %{--<a href="#" class="btn btn-ajax btn-new" id="btnSave">--}%
    %{--<i class="icon-save"></i>--}%
    %{--Guardar--}%
    %{--</a>--}%
    %{--</div>--}%

        <div id="lista-tramite" class="span12" role="main" style="margin-top: 10px;">
            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>
                        <th>Estado</th>
                        <th>Tipo</th>
                        <th>Envía</th>
                        <th>Recibe</th>
                        <th>Asunto</th>
                        <th>Tiempo</th>
                        <th>Fecha</th>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${tramites}" var="tramite">
                        <tr>
                            <td>..</td>
                            <td>${tramite.tipoTramite.descripcion}</td>
                            <g:set var="persona" value="${janus.PersonasTramite.findByRolTramiteAndTramite(RolTramite.get(2), tramite).persona}"/>
                            <td>${persona.nombre + " " + persona.apellido}</td>
                            <g:set var="persona" value="${janus.PersonasTramite.findByRolTramiteAndTramite(RolTramite.get(3), tramite).persona}"/>
                            <td>${persona.nombre + " " + persona.apellido}</td>
                            <td>${tramite.descripcion}</td>
                            <td>${tramite.tipoTramite.tiempo} días</td>
                            <td><g:formatDate date="${tramite.fecha}" format="dd-MM-yyyy"/></td>
                        </tr>
                    </g:each>
                </tbody>
            </table>
        </div>
    </body>
</html>