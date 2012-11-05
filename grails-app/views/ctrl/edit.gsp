<%@ page import="janus.seguridad.Ctrl" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'ctrl.label', default: 'Ctrl')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a>
    </span>
    <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label"
                                                                           args="[entityName]"/></g:link></span>
    <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label"
                                                                               args="[entityName]"/></g:link></span>
</div>

<div class="body">
    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${ctrlInstance}">
        <div class="errors">
            <g:renderErrors bean="${ctrlInstance}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <g:hiddenField name="id" value="${ctrlInstance?.id}"/>
        <g:hiddenField name="version" value="${ctrlInstance?.version}"/>
        <div class="dialog">
            <table>
                <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="ctrlNombre"><g:message code="ctrl.ctrlNombre.label" default="Ctrl Nombre"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: ctrlInstance, field: 'ctrlNombre', 'errors')}">
                        <g:textField name="ctrlNombre" id="ctrlNombre" title="CtrlNombre"
                                     class="6 field required ui-widget-content ui-corner-all" maxLenght="50"
                                     value="${ctrlInstance?.ctrlNombre}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="acciones"><g:message code="ctrl.acciones.label" default="Acciones"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: ctrlInstance, field: 'acciones', 'errors')}">

                        <ul>
                            <g:each in="${ctrlInstance?.acciones?}" var="a">
                                <li><g:link controller="accn" action="show"
                                            id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
                            </g:each>
                        </ul>
                        <g:link controller="accn" action="create"
                                params="['ctrl.id': ctrlInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'accn.label', default: 'Accn')])}</g:link>

                    </td>
                </tr>

                </tbody>
            </table>
        </div>

        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" action="update"
                                                 value="${message(code: 'default.button.update.label', default: 'Update')}"/></span>
            <span class="button"><g:actionSubmit class="delete" action="delete"
                                                 value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                                                 onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
