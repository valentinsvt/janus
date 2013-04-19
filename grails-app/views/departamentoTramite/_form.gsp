<%@ page import="janus.DepartamentoTramite" %>



<div class="fieldcontain ${hasErrors(bean: departamentoTramiteInstance, field: 'tipoTramite', 'error')} required">
	<label for="tipoTramite">
		<g:message code="departamentoTramite.tipoTramite.label" default="Tipo Tramite" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="tipoTramite" name="tipoTramite.id" from="${janus.TipoTramite.list()}" optionKey="id" class="many-to-one  required" value="${departamentoTramiteInstance?.tipoTramite?.id}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: departamentoTramiteInstance, field: 'rolTramite', 'error')} required">
	<label for="rolTramite">
		<g:message code="departamentoTramite.rolTramite.label" default="Rol Tramite" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="rolTramite" name="rolTramite.id" from="${janus.RolTramite.list()}" optionKey="id" class="many-to-one  required" value="${departamentoTramiteInstance?.rolTramite?.id}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: departamentoTramiteInstance, field: 'departamento', 'error')} required">
	<label for="departamento">
		<g:message code="departamentoTramite.departamento.label" default="Departamento" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="departamento" name="departamento.id" from="${janus.Departamento.list()}" optionKey="id" class="many-to-one  required" value="${departamentoTramiteInstance?.departamento?.id}"/>
</div>

