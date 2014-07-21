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

        th {
            vertical-align : middle !important;
        }

        .center {
            text-align     : center !important;
            vertical-align : middle !important;
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

        <div class="row btn-group" role="navigation" style="margin-left: 30px">
            <a href="#" class="btn btn-ajax btn-new" id="imprimirProcesos">
                <i class="icon-print"></i>
                Imprimir Trámites en Proceso
            </a>
            <a href="#" class="btn btn-ajax btn-new" id="imprimirTodos">
                <i class="icon-print"></i>
                Imprimir Trámites por Obra
            </a>

            <g:if test="${finalizados == 'S'}">
                <g:link action="list" params="[finalizados: 'N']" class="btn btn-ajax btn-new">
                    <i class="icon-ok"></i>
                    Ocultar finalizados
                </g:link>
            </g:if>
            <g:else>
                <g:link action="list" params="[finalizados: 'S']" class="btn btn-ajax btn-new">
                    <i class="icon-ok"></i>
                    Mostrar finalizados
                </g:link>
            </g:else>
            <a href="#" class="btn btn-ajax btn-new" id="btnNew">
                <i class="icon-file"></i>
                Nuevo trámite
            </a>
        </div>


        <div id="lista-tramite" class="span12" role="main" style="margin-top: 10px;">
            <table class="table table-bordered table-condensed table-hover">
                <thead>
                    <tr>
                        <th></th>
                        <th>Estado</th>
                        <th>Tipo</th>
                        <th>Memo</th>
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

                        <g:if test="${tramite.estado.codigo == 'F'}">
                            <g:set var="clase" value="info"/>
                        </g:if>

                        <tr class="${clase}">
                            <td class="alerta"></td>
                            <td>${tramite.estado.descripcion}</td>
                            <td>${tramite.tipoTramite.descripcion}</td>
                            <td>${tramite.memo}</td>
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
                            <td class="center">
                                <a class="btn btn-small btn-show btn-ajax btnVer" href="#" rel="tooltip" title="Ver" data-id="${tramite.id}" style="margin-bottom: 5px;">
                                    <i class="icon-zoom-in"></i>
                                </a>

                                <g:if test="${personaDe == usu}">
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
                                </g:if>
                                <g:elseif test="${personaPara == usu}">
                                    <g:if test="${tramite.estado.codigo == 'I'}">
                                        <a href="#" class="btn btnEstado" data-tipo="P" data-id="${tramite.id}">Recibir</a>
                                    </g:if>
                                    <g:elseif test="${tramite.estado.codigo == 'P'}">
                                        <g:if test="${tramite.tipoTramite.requiereRespuesta == 'S'}">
                                            <a href="#" class="btn btnEstado" data-tipo="R" data-id="${tramite.id}">Responder</a>
                                        </g:if>
                                    </g:elseif>
                                </g:elseif>
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

        <div class="modal big hide fade" id="modal-new">
            <div class="modal-header" id="modalHeader-new">
                <button type="button" class="close darker" data-dismiss="modal">
                    <i class="icon-remove-circle"></i>
                </button>

                <h3 id="modalTitle-new"></h3>
            </div>

            <div class="modal-body" id="modalBody-new">
            </div>

            <div class="modal-footer" id="modalFooter-new">
            </div>
        </div>

        <div class="modal grandote hide fade " id="modal-busca" style=";overflow: hidden;">
            <div class="modal-header btn-info">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle-busca"></h3>
            </div>

            <div class="modal-body" id="modalBody-busca">
                <bsc:buscador name="obra" value="" accion="buscaObra" campos="${campos}" label="Obra" tipo="lista"/>
            </div>

            <div class="modal-footer" id="modalFooter-busca">
            </div>
        </div>


        <div id="tramiteDialog">
            <fieldset>
                <div class="span3">
                    Elija la Obra:
                </div>

                <div class="span3">
                    <g:select name="selectObra" from="${janus.Tramite.findAllByObraIsNotNull().obra.unique()}" optionKey="id" optionValue="descripcion" style="width: 370px"/>
                </div>
            </fieldset>
        </div>

        <script type="text/javascript">
            $(function () {

                $("#btnNew").click(function () {
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'registro_ajax')}",
                        success : function (msg) {
                            $("#modal-confirm").modal("hide");
                            var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                if ($("#frmRegistrar-tramite").valid()) {
                                    $(this).replaceWith(spinner);
                                    $.ajax({
                                        type    : "POST",
                                        url     : "${createLink(action:'registrar')}",
                                        data    : $("#frmRegistrar-tramite").serialize(),
                                        success : function (msg) {
                                            if (msg == "OK") {
                                                location.reload(true);
                                            }
                                        }
                                    });
                                }
                            });

                            $("#modalTitle-new").html("Crear trámite");
                            $("#modalBody-new").html(msg);
                            $("#modalFooter-new").html("").append(btnCancel).append(btnSave);
                            $("#modal-new").modal("show");
                        }
                    });
                    return false;
                });
                $(".btnVer").click(function () {
                    var id = $(this).data("id");
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'show_ajax')}",
                        data    : {
                            id : id
                        },
                        success : function (msg) {
                            $("#modal-confirm").modal("hide");
                            var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Aceptar</a>');

                            $("#modalTitle-new").html("Ver trámite");
                            $("#modalBody-new").html(msg);
                            $("#modalFooter-new").html("").append(btnCancel);
                            $("#modal-new").modal("show");
                        }
                    });
                    return false;
                });

                $(".btnEstado").click(function () {
                    $(this).replaceWith(spinner);
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
                        case "R":
                            ttl = "Responder trámite";
                            txt = "Esto cambiará el estado del trámite a 'Respondido' y mostrará la pantalla de creación de trámite.<br/>Desea continuar?";
                            break;
                    }

                    var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnContinue = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Aceptar</a>');

                    btnContinue.click(function () {
                        $(this).replaceWith(spinner);
                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(action:'updateEstado')}",
                            data    : {
                                id   : id,
                                tipo : tipo
                            },
                            success : function (msg) {
                                if (msg == "OK") {
                                    location.reload(true);
                                } else if (msg == "R") {
                                    $.ajax({
                                        type    : "POST",
                                        url     : "${createLink(action:'registro_ajax')}",
                                        data    : {
                                            padre : id
                                        },
                                        success : function (msg) {
                                            $("#modal-confirm").modal("hide");
                                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                                            btnSave.click(function () {
                                                if ($("#frmRegistrar-tramite").valid()) {
                                                    $(this).replaceWith(spinner);
                                                    $.ajax({
                                                        type    : "POST",
                                                        url     : "${createLink(action:'registrar')}",
                                                        data    : $("#frmRegistrar-tramite").serialize(),
                                                        success : function (msg) {
                                                            if (msg == "OK") {
                                                                location.reload(true);
                                                            }
                                                        }
                                                    });
                                                }
                                            });

                                            $("#modalTitle-new").html("Responder trámite");
                                            $("#modalBody-new").html(msg);
                                            $("#modalFooter-new").html("").append(btnCancel).append(btnSave);
                                            $("#modal-new").modal("show");
                                        }
                                    });
                                }
                            }
                        });
                        return false;
                    });

                    $("#modalTitle-confirm").html(ttl);
                    $("#modalBody-confirm").html(txt);
                    $("#modalFooter-confirm").html("").append(btnCancel).append(btnContinue);
                    $("#modal-confirm").modal("show");

                    return false;
                });
            });

            $("#imprimirProcesos").click(function () {

                location.href = "${g.createLink(controller: 'reportes', action: 'reporteRegistroTramite')}";

            });

            $("#imprimirTodos").click(function () {

                $("#tramiteDialog").dialog("open");

            });

            $("#tramiteDialog").dialog({

                autoOpen  : false,
                resizable : false,
                modal     : true,
                draggable : false,
                width     : 450,
                height    : 200,
                position  : 'center',
                title     : 'Seleccione una Obra',
                buttons   : {
                    "Aceptar"     : function () {

                        var obra = $("#selectObra").val();

                        if(obra){
                            location.href = "${g.createLink(controller: 'reportes', action: 'reporteRegistroTramitexObra')}?idObra=" + obra
                            $("#tramiteDialog").dialog("close");
                        }else{
                            $("#tramiteDialog").dialog("close");
                        }

                    }, "Cancelar" : function () {

                        $("#tramiteDialog").dialog("close");

                    }
                }

            });



        </script>

    </body>
</html>