<%@ page import="janus.TipoTramite" %>



<div class="fieldcontain ${hasErrors(bean: tipoTramiteInstance, field: 'codigo', 'error')} required">
	<label for="codigo">
		<g:message code="tipoTramite.codigo.label" default="Codigo" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="codigo" maxlength="4" class=" required" value="${tipoTramiteInstance?.codigo}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tipoTramiteInstance, field: 'descripcion', 'error')} required">
	<label for="descripcion">
		<g:message code="tipoTramite.descripcion.label" default="Descripcion" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="descripcion" maxlength="63" class=" required" value="${tipoTramiteInstance?.descripcion}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tipoTramiteInstance, field: 'padre', 'error')} ">
	<label for="padre">
		<g:message code="tipoTramite.padre.label" default="Padre" />
		
	</label>
	<g:select id="padre" name="padre.id" from="${janus.TipoTramite.list()}" optionKey="id" class="many-to-one " value="${tipoTramiteInstance?.padre?.id}" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tipoTramiteInstance, field: 'tiempo', 'error')} required">
	<label for="tiempo">
		<g:message code="tipoTramite.tiempo.label" default="Tiempo" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="tiempo" class=" required" value="${fieldValue(bean: tipoTramiteInstance, field: 'tiempo')}"/>
</div>

