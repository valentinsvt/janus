<%@ page import="janus.seguridad.Usro" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName"
           value="${message(code: 'usro.label', default: 'Usro')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>

<div class="dialog" title="${title}">
<div id="" class="toolbar ui-widget-header ui-corner-all">
    <g:link class="button list" action="list"><g:message code="usro.list" default="Lista de Usuarios"/></g:link>
    <g:link class="button create" action="create">Nuevo Usuario</g:link>
</div> <!-- toolbar -->

<div class="body">
<g:if test="${flash.message}">
    <div class="message ui-state-highlight ui-corner-all">${flash.message}</div>
</g:if>
<div>

    <fieldset class="ui-corner-all" style="width: 600px;">
        <legend class="ui-widget ui-widget-header ui-corner-all">
            <g:message code="usro.show.legend" default="Detalles de Usuario"/>
        </legend>
        <div class="prop">
            <label><g:message code="usro.id.label" default="Id"/></label>
            <div class="campo">${fieldValue(bean: usroInstance, field: "id")}</div>
        </div> <!-- prop -->
        <div class="prop">
            <label><g:message code="usro.persona.label" default="Persona"/></label>
            <div class="campo"><g:link controller="persona" action="show" id="${usroInstance?.persona?.id}">
                    ${usroInstance?.persona?.encodeAsHTML()}</g:link>
            </div> <!-- campo -->
        </div> <!-- prop -->
        <div class="prop"><label><g:message code="usro.cargoPersonal.label" default="Cargo"/></label>
            <div class="campo"><g:link controller="cargoPersonal" action="show"
                        id="${usroInstance?.cargoPersonal?.id}">${usroInstance?.cargoPersonal?.encodeAsHTML()}</g:link>
            </div> <!-- campo -->
        </div> <!-- prop -->

        <div class="prop">
            <label><g:message code="usro.usroLogin.label" default="Login"/></label>
            <div class="campo">${fieldValue(bean: usroInstance, field: "usroLogin")}</div> <!-- campo -->
        </div> <!-- prop -->
        <div class="prop"><label><g:message code="usro.sigla.label" default="Sigla"/></label>
            <div class="campo">${fieldValue(bean: usroInstance, field: "sigla")}</div> <!-- campo -->
        </div> <!-- prop -->
        <div class="prop"><label><g:message code="usro.usroActivo.label" default="Activo"/></label>
            <div class="campo">${fieldValue(bean: usroInstance, field: "usroActivo")}</div> <!-- campo -->
        </div> <!-- prop -->
        <div class="prop"><label><g:message code="usro.fechaPass.label" default="Fecha de Contraseña"/></label>
            <div class="campo"><g:formatDate date="${usroInstance?.fechaPass}" format="dd-MM-yyyy HH:mm"/></div> <!-- campo -->
        </div> <!-- prop -->
        <div class="prop"><label><g:message code="usro.observaciones.label" default="Observaciones"/></label>
            <div class="campo">${fieldValue(bean: usroInstance, field: "observaciones")}</div> <!-- campo -->
        </div> <!-- prop -->
%{--
        <div class="prop"><label><g:message code="usro.accesos.label" default="Accesos"/></label>
            <div class="campo"><ul>
                    <g:each in="${usroInstance.accesos}" var="a">
                        <li><g:link controller="accs" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
                    </g:each></ul></div> <!-- campo -->
        </div> <!-- prop -->
--}%

        <div class="prop"><label><g:message code="usro.alertas.label" default="Alertas"/></label>
            <div class="campo">
                <ul style="margin-left: 120px; margin-top: -20px; ">
                    <g:each in="${usroInstance.alertas}" var="a">
                        <li><g:link controller="alerta" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
                    </g:each>
                </ul>
            </div> <!-- campo -->
        </div> <!-- prop -->

        <div class="prop"><label><g:message code="usro.sesiones.label" default="Permisos de Usuario"/></label>
            <div class="campo"><ul style="margin-left: 120px; margin-top: -20px;">
                    <g:each in="${usroInstance.sesiones}" var="s">
                        <li><g:link controller="sesn" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></li>
                    </g:each></ul>
            </div> <!-- campo -->
        </div> <!-- prop -->

        <div class="buttons">
            <g:link class="button edit" action="edit" id="${usroInstance?.id}">
                <g:message code="default.button.update.label" default="Edit"/>
            </g:link>
            <g:link class="button delete" action="delete" id="${usroInstance?.id}">
                <g:message code="default.button.delete.label" default="Delete"/>
            </g:link>
        </div>

    </fieldset>
</div>
</div> <!-- body -->
</div> <!-- dialog -->

<script type="text/javascript">
    $(function() {
        $(".button").button();
        $(".home").button("option", "icons", {primary:'ui-icon-home'});
        $(".list").button("option", "icons", {primary:'ui-icon-clipboard'});
        $(".create").button("option", "icons", {primary:'ui-icon-document'});

        $(".edit").button("option", "icons", {primary:'ui-icon-pencil'});
        $(".delete").button("option", "icons", {primary:'ui-icon-trash'}).click(function() {
            if (confirm("${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}")) {
                return true;
            }
            return false;
        });
    });
</script>

</body>
</html>
