
<%@ page import="janus.DepartamentoTramite" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'departamentoTramite.label', default: 'DepartamentoTramite')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-departamentoTramite" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-departamentoTramite" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list departamentoTramite">
			
				<g:if test="${departamentoTramiteInstance?.tipoTramite}">
				<li class="fieldcontain">
					<span id="tipoTramite-label" class="property-label"><g:message code="departamentoTramite.tipoTramite.label" default="Tipo Tramite" /></span>
					
						<span class="property-value" aria-labelledby="tipoTramite-label"><g:link controller="tipoTramite" action="show" id="${departamentoTramiteInstance?.tipoTramite?.id}">${departamentoTramiteInstance?.tipoTramite?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${departamentoTramiteInstance?.rolTramite}">
				<li class="fieldcontain">
					<span id="rolTramite-label" class="property-label"><g:message code="departamentoTramite.rolTramite.label" default="Rol Tramite" /></span>
					
						<span class="property-value" aria-labelledby="rolTramite-label"><g:link controller="rolTramite" action="show" id="${departamentoTramiteInstance?.rolTramite?.id}">${departamentoTramiteInstance?.rolTramite?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${departamentoTramiteInstance?.departamento}">
				<li class="fieldcontain">
					<span id="departamento-label" class="property-label"><g:message code="departamentoTramite.departamento.label" default="Departamento" /></span>
					
						<span class="property-value" aria-labelledby="departamento-label"><g:link controller="departamento" action="show" id="${departamentoTramiteInstance?.departamento?.id}">${departamentoTramiteInstance?.departamento?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${departamentoTramiteInstance?.id}" />
					<g:link class="edit" action="edit" id="${departamentoTramiteInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
