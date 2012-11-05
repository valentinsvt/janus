<%@ page import="janus.seguridad.Usro" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName"
           value="${message(code: 'usro.label', default: 'Usro')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>

<div class="dialog" title="${title}">
    <div id="" class="toolbar ui-widget-header ui-corner-all">
        <g:link class="button create" action="create">Nuevo Usuario</g:link>
    </div> <!-- toolbar -->

    <div class="body">
        <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
        </g:if>
        <div class="list" style="width: 1000px;">
            <div class="fg-toolbar ui-toolbar ui-widget-header ui-corner-top ui-helper-clearfix">
            </div>
            <table style="width: 1000px;">
                <thead>
                <tr>
                    <tdn:sortableColumn property="persona" class="ui-state-default" title="Persona" />
                    <tdn:sortableColumn property="usroLogin" class="ui-state-default"
                                       title="${message(code: 'usro.usroLogin.label', default: 'Nombre de usuario')}"/>
                    <tdn:sortableColumn property="usroPassword" class="ui-state-default"
                                        title="${message(code: 'usro.sigla.label', default: 'Sigla')}"/>
                    <tdn:sortableColumn property="usroActivo" class="ui-state-default"
                                        title="${message(code: 'usro.usroActivo.label', default: 'Activo')}"/>
                    <th class="ui-state-default"><g:message code="usro.cargoPersonal.label" default="Cargo Personal"/></th>
                    <th class="ui-state-default"><g:message code="usro.observaciones.label" default="Observaciones"/></th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${usroInstanceList}" status="i" var="usroInstance">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                        <td><g:link action="show"
                                    id="${usroInstance.id}">${fieldValue(bean: usroInstance, field: "persona")}</g:link></td>
                        <td>${fieldValue(bean: usroInstance, field: "usroLogin")}</td>
                        <td>${fieldValue(bean: usroInstance, field: "sigla")}</td>
                        <td>${fieldValue(bean: usroInstance, field: "usroActivo")}</td>
                        <td>${fieldValue(bean: usroInstance, field: "cargoPersonal")}</td>
                        <td>${fieldValue(bean: usroInstance, field: "observaciones")}</td>

                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>

        <div class="fg-toolbar ui-toolbar ui-widget-header ui-corner-bottom ui-helper-clearfix paginateButtons">
            <tdn:paginate total="${usroInstanceTotal}"/>
        </div>
    </div> <!-- body -->
</div> <!-- dialog -->

<script type="text/javascript">
    $(function() {
        $(".button").button();
        $(".home").button("option", "icons", {primary:'ui-icon-home'});
    });
</script>

</body>
</html>
