<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 4/30/13
  Time: 4:39 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="janus.PersonasTramite; janus.RolTramite; janus.Tramite" %>
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

        <style type="text/css">
        .activo {
            font-weight : bold;
        }
        </style>

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
            <table class="table table-bordered table-condensed table-hover">
                <thead>
                    <tr>
                        <th></th>
                        <th>Estado</th>
                        <th>Tipo</th>
                        <th>Envía</th>
                        <th>Recibe</th>
                        <th>CC</th>
                        <th>Asunto</th>
                        <th>Fecha</th>
                        <th>Tiempo</th>
                        <th>Fecha Tope</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${tramites}" var="tramite">
                        <g:set var="hoy" value="${new Date().clearTime()}"/>

                        <g:set var="fechaTope" value="${tramite.fecha.plus(tramite.tipoTramite.tiempo).clearTime()}"/>
                        <g:set var="personaDe" value="${PersonasTramite.findByRolTramiteAndTramite(RolTramite.findByCodigo('DE'), tramite).persona}"/>
                        <g:set var="personaPara" value="${PersonasTramite.findByRolTramiteAndTramite(RolTramite.findByCodigo('PARA'), tramite).persona}"/>
                        <g:set var="personasCC" value="${PersonasTramite.findAllByRolTramiteAndTramite(RolTramite.findByCodigo('CC'), tramite).persona}"/>

                        <g:set var="fechaAmarillo" value="${fechaTope.minus(1).clearTime()}"/>
                        <g:set var="fechaRojo" value="${fechaTope.plus(1).clearTime()}"/>

                        <g:set var="clase" value="success"/>

                        <g:if test="${hoy >= fechaAmarillo && hoy <= fechaTope}">
                            <g:set var="clase" value="warning"/>
                        </g:if>
                        <g:elseif test="${hoy >= fechaRojo}">
                            <g:set var="clase" value="error"/>
                        </g:elseif>

                        <tr class="${clase}">
                            <td class="alerta"></td>
                            <td>${tramite.estado.descripcion}</td>
                            <td>${tramite.tipoTramite.descripcion}</td>
                            <td class="${personaDe == usu ? 'activo' : ''}">${personaDe.nombre + " " + personaDe.apellido} (${personaDe.departamento.descripcion})</td>
                            <td class="${personaPara == usu ? 'activo' : ''}">${personaPara.nombre + " " + personaPara.apellido} (${personaPara.departamento.descripcion})</td>
                            <td>
                                <ul>
                                    <g:each in="${personasCC}" var="p">
                                        <li class="${p == usu ? 'activo' : ''}">${p.nombre + " " + p.apellido} (${p.departamento.descripcion})</li>
                                    </g:each>
                                </ul>
                            </td>
                            <td>${tramite.descripcion}</td>
                            <td><g:formatDate date="${tramite.fecha}" format="dd-MM-yyyy"/></td>
                            <td>${tramite.tipoTramite.tiempo} días</td>
                            <td><g:formatDate date="${fechaTope}" format="dd-MM-yyyy"/></td>
                            <td>
                            %{--<g:if test="${personaDe == usu}">--}%
                                <g:if test="${tramite.estado.codigo == 'P'}">
                                    <a href="#" class="btn btnEstado" data-tipo="F" data-id="${tramite.id}">Finalizar</a>
                                </g:if>
                                <g:elseif test="${tramite.estado.codigo == 'C'}">
                                    <a href="#" class="btn btnEstado" data-tipo="I" data-id="${tramite.id}">Enviar</a>
                                </g:elseif>
                                <g:elseif test="${tramite.estado.codigo == 'P'}">
                                    <g:if test="${tramite.tipoTramite.requiereRespuesta == 'N'}">
                                        <a href="#" class="btn btnEstado" data-tipo="F" data-id="${tramite.id}">Finalizar</a>
                                    </g:if>
                                </g:elseif>
                                <g:elseif test="${tramite.estado.codigo == 'R'}">
                                    <a href="#" class="btn btnEstado" data-tipo="F" data-id="${tramite.id}">Finalizar</a>
                                </g:elseif>
                            %{--</g:if>--}%
                            %{--<g:elseif test="${personaPara == usu}">--}%
                            %{--<g:if test="${tramite.estado.codigo == 'I'}">--}%
                            %{--<a href="#" class="btn btnEstado" data-tipo="P" data-id="${tramite.id}">Recibir</a>--}%
                            %{--</g:if>--}%
                            %{--<g:elseif test="${tramite.estado.codigo == 'P'}">--}%
                            %{--<g:if test="${tramite.tipoTramite.requiereRespuesta == 'S'}">--}%
                            %{--<a href="#" class="btn btnEstado" data-tipo="R" data-id="${tramite.id}">Responder</a>--}%
                            %{--</g:if>--}%
                            %{--</g:elseif>--}%
                            %{--</g:elseif>--}%
                            </td>
                        </tr>
                    </g:each>
                </tbody>
            </table>
        </div>

        <div class="modal hide fade" id="modal-confirm">
            <div class="modal-header" id="modalHeader-confirm">
                <button type="button" class="close darker" data-dismiss="modal">
                    <i class="icon-remove-circle"></i>
                </button>

                <h3 id="modalTitle-confirm"></h3>
            </div>

            <div class="modal-body" id="modalBody-confirm">
            </div>

            <div class="modal-footer" id="modalFooter-confirm">
            </div>
        </div>

        <script type="text/javascript">
            $(function () {
                $(".btnEstado").click(function () {
                    var tipo = $(this).data("tipo");
                    var id = $(this).data("id");
                    var txt = "", ttl = "";
                    switch (tipo) {
                        case "I":
                            ttl = "Iniciar trámite";
                            txt = "Esto cambiará el estado del trámite a 'Iniciado'.<br/>Esto significa que los documentos han sido enviados físicamente.<br/>Desea continuar?";
                            break;
                        case "P":
                            ttl = "Recibir trámite";
                            txt = "Esto cambiará el estado del trámite a 'En Proceso'.<br/>Esto significa que ha recibido la documentación necesaria.<br/>Desea continuar?";
                            break;
                        case "F":
                            ttl = "Finalizar trámite";
                            txt = "Esto cambiará el estado del trámite a 'Finalizado'.<br/>Desea continuar?";
                            break;
                    }

                    var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Aceptar</a>');

                    btnSave.click(function () {
                        $(this).replaceWith(spinner);
                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(action:'updateEstado')}",
                            data    : {
                                id   : id,
                                tipo : tipo
                            },
                            success : function () {
                                location.reload(true);
                            }
                        });

                        return false;
                    });

                    $("#modalTitle-confirm").html(ttl);
                    $("#modalBody-confirm").html(txt);
                    $("#modalFooter-confirm").html("").append(btnCancel).append(btnSave);
                    $("#modal-confirm").modal("show");

                    return false;
                });
            });
        </script>

    </body>
</html>