
<%@ page import="janus.seguridad.Usro" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'usro.label', default: 'Usro')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-usro" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-usro" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list usro">
			
				<g:if test="${usroInstance?.persona}">
				<li class="fieldcontain">
					<span id="persona-label" class="property-label"><g:message code="usro.persona.label" default="Persona" /></span>
					
						<span class="property-value" aria-labelledby="persona-label"><g:link controller="persona" action="show" id="${usroInstance?.persona?.id}">${usroInstance?.persona?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${usroInstance?.login}">
				<li class="fieldcontain">
					<span id="login-label" class="property-label"><g:message code="usro.login.label" default="Login" /></span>
					
						<span class="property-value" aria-labelledby="login-label"><g:fieldValue bean="${usroInstance}" field="login"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${usroInstance?.password}">
				<li class="fieldcontain">
					<span id="password-label" class="property-label"><g:message code="usro.password.label" default="Password" /></span>
					
						<span class="property-value" aria-labelledby="password-label"><g:fieldValue bean="${usroInstance}" field="password"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${usroInstance?.autorizacion}">
				<li class="fieldcontain">
					<span id="autorizacion-label" class="property-label"><g:message code="usro.autorizacion.label" default="Autorizacion" /></span>
					
						<span class="property-value" aria-labelledby="autorizacion-label"><g:fieldValue bean="${usroInstance}" field="autorizacion"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${usroInstance?.sigla}">
				<li class="fieldcontain">
					<span id="sigla-label" class="property-label"><g:message code="usro.sigla.label" default="Sigla" /></span>
					
						<span class="property-value" aria-labelledby="sigla-label"><g:fieldValue bean="${usroInstance}" field="sigla"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${usroInstance?.activo}">
				<li class="fieldcontain">
					<span id="activo-label" class="property-label"><g:message code="usro.activo.label" default="Activo" /></span>
					
						<span class="property-value" aria-labelledby="activo-label"><g:fieldValue bean="${usroInstance}" field="activo"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${usroInstance?.fechaPass}">
				<li class="fieldcontain">
					<span id="fechaPass-label" class="property-label"><g:message code="usro.fechaPass.label" default="Fecha Pass" /></span>
					
						<span class="property-value" aria-labelledby="fechaPass-label"><g:formatDate date="${usroInstance?.fechaPass}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${usroInstance?.observaciones}">
				<li class="fieldcontain">
					<span id="observaciones-label" class="property-label"><g:message code="usro.observaciones.label" default="Observaciones" /></span>
					
						<span class="property-value" aria-labelledby="observaciones-label"><g:fieldValue bean="${usroInstance}" field="observaciones"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${usroInstance?.id}" />
					<g:link class="edit" action="edit" id="${usroInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
