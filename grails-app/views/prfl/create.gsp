<%@ page import="janus.seguridad.Prfl" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'prfl.label', default: 'Prfl')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a>
    </span>
    <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label"
                                                                           args="[entityName]"/></g:link></span>
</div>

<div class="body">
    <h1><g:message code="default.create.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${prflInstance}">
        <div class="errors">
            <g:renderErrors bean="${prflInstance}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="save">
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

                </tbody>
            </table>
        </div>

        <div class="buttons">
            <span class="button"><g:submitButton name="create" class="save"
                                                 value="${message(code: 'default.button.create.label', default: 'Create')}"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
