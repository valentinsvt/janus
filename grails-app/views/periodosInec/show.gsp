<%@ page import="janus.ejecucion.PeriodosInec" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'periodosInec.label', default: 'PeriodosInec')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-periodosInec" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                   default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-periodosInec" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list periodosInec">

        <g:if test="${periodosInecInstance?.descripcion}">
            <li class="fieldcontain">
                <span id="descripcion-label" class="property-label"><g:message code="periodosInec.descripcion.label"
                                                                               default="Descripción"/></span>

                <span class="property-value" aria-labelledby="descripcion-label"><g:fieldValue
                        bean="${periodosInecInstance}" field="descripcion"/></span>

            </li>
        </g:if>

        <g:if test="${periodosInecInstance?.fechaInicio}">
            <li class="fieldcontain">
                <span id="fechaInicio-label" class="property-label"><g:message code="periodosInec.fechaInicio.label"
                                                                               default="Fecha Inicio"/></span>

                <span class="property-value" aria-labelledby="fechaInicio-label"><g:formatDate
                        date="${periodosInecInstance?.fechaInicio}"/></span>

            </li>
        </g:if>

        <g:if test="${periodosInecInstance?.fechaFin}">
            <li class="fieldcontain">
                <span id="fechaFin-label" class="property-label"><g:message code="periodosInec.fechaFin.label"
                                                                            default="Fecha Fin"/></span>

                <span class="property-value" aria-labelledby="fechaFin-label"><g:formatDate
                        date="${periodosInecInstance?.fechaFin}"/></span>

            </li>
        </g:if>

        <g:if test="${periodosInecInstance?.periodoCerrado}">
            <li class="fieldcontain">
                <span id="periodoCerrado-label" class="property-label"><g:message
                        code="periodosInec.periodoCerrado.label" default="Periodo Cerrado"/></span>

                <span class="property-value" aria-labelledby="periodoCerrado-label"><g:fieldValue
                        bean="${periodosInecInstance}" field="periodoCerrado"/></span>

            </li>
        </g:if>

    </ol>
    <g:form>
        <fieldset class="buttons">
            <g:hiddenField name="id" value="${periodosInecInstance?.id}"/>
            <g:link class="edit" action="edit" id="${periodosInecInstance?.id}"><g:message
                    code="default.button.edit.label" default="Edit"/></g:link>
            <g:actionSubmit class="delete" action="delete"
                            value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                            onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
