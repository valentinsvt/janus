<%@ page import="janus.seguridad.Sesn" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'sesn.label', default: 'Sesn')}"/>
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
    <g:hasErrors bean="${sesnInstance}">
        <div class="errors">
            <g:renderErrors bean="${sesnInstance}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <g:hiddenField name="id" value="${sesnInstance?.id}"/>
        <g:hiddenField name="version" value="${sesnInstance?.version}"/>
        <div class="dialog">
            <table>
                <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="perfil"><g:message code="sesn.perfil.label" default="Perfil"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: sesnInstance, field: 'perfil', 'errors')}">
                        <g:select class="10 field ui-widget-content ui-corner-all" name="perfil.id" title="Perfil"
                                  from="${janus.seguridad.Prfl.list()}" optionKey="id"
                                  value="${sesnInstance?.perfil?.id}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="usuario"><g:message code="sesn.usuario.label" default="Usuario"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: sesnInstance, field: 'usuario', 'errors')}">
                        <g:select class="10 field ui-widget-content ui-corner-all" name="usuario.id" title="Usuario"
                                  from="${janus.seguridad.Usro.list()}" optionKey="id"
                                  value="${sesnInstance?.usuario?.id}"/>
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
