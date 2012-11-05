<%@ page import="janus.seguridad.Prfl" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'prfl.label', default: 'Prfl')}"/>
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
    <g:hasErrors bean="${prflInstance}">
        <div class="errors">
            <g:renderErrors bean="${prflInstance}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <g:hiddenField name="id" value="${prflInstance?.id}"/>
        <g:hiddenField name="version" value="${prflInstance?.version}"/>
        <div class="dialog">
            <table>
                <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="descripcion"><g:message code="prfl.descripcion.label"
                                                            default="Descripcion"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: prflInstance, field: 'descripcion', 'errors')}">
                        <g:textField name="descripcion" id="descripcion" title="Descripcion"
                                     class="6 field ui-widget-content ui-corner-all"
                                     value="${prflInstance?.descripcion}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="nombre"><g:message code="prfl.nombre.label" default="Nombre"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: prflInstance, field: 'nombre', 'errors')}">
                        <g:textField name="nombre" id="nombre" title="Nombre"
                                     class="6 field ui-widget-content ui-corner-all" value="${prflInstance?.nombre}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="observaciones"><g:message code="prfl.observaciones.label"
                                                              default="Observaciones"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: prflInstance, field: 'observaciones', 'errors')}">
                        <g:textField name="observaciones" id="observaciones" title="Observaciones"
                                     class="6 field ui-widget-content ui-corner-all"
                                     value="${prflInstance?.observaciones}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="padre"><g:message code="prfl.padre.label" default="Padre"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: prflInstance, field: 'padre', 'errors')}">
                        <g:select class="10 field ui-widget-content ui-corner-all" name="padre.id" title="Padre"
                                  from="${janus.seguridad.Prfl.list()}" optionKey="id"
                                  value="${prflInstance?.padre?.id}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="perfiles"><g:message code="prfl.perfiles.label" default="Perfiles"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: prflInstance, field: 'perfiles', 'errors')}">

                        <ul>
                            <g:each in="${prflInstance?.perfiles?}" var="p">
                                <li><g:link controller="prfl" action="show"
                                            id="${p.id}">${p?.encodeAsHTML()}</g:link></li>
                            </g:each>
                        </ul>
                        <g:link controller="prfl" action="create"
                                params="['prfl.id': prflInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'prfl.label', default: 'Prfl')])}</g:link>

                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="permisos"><g:message code="prfl.permisos.label" default="Permisos"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: prflInstance, field: 'permisos', 'errors')}">

                        <ul>
                            <g:each in="${prflInstance?.permisos?}" var="p">
                                <li><g:link controller="prms" action="show"
                                            id="${p.id}">${p?.encodeAsHTML()}</g:link></li>
                            </g:each>
                        </ul>
                        <g:link controller="prms" action="create"
                                params="['prfl.id': prflInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'prms.label', default: 'Prms')])}</g:link>

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
