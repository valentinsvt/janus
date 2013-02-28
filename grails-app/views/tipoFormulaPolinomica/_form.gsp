<%@ page import="janus.ejecucion.TipoFormulaPolinomica" %>



<div class="fieldcontain ${hasErrors(bean: tipoFormulaPolinomicaInstance, field: 'codigo', 'error')} ">
	<label for="codigo">
		<g:message code="tipoFormulaPolinomica.codigo.label" default="Codigo" />
		
	</label>
	<g:textField name="codigo" class="" value="${tipoFormulaPolinomicaInstance?.codigo}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tipoFormulaPolinomicaInstance, field: 'descripcion', 'error')} ">
	<label for="descripcion">
		<g:message code="tipoFormulaPolinomica.descripcion.label" default="Descripcion" />
		
	</label>
	<g:textField name="descripcion" class="" value="${tipoFormulaPolinomicaInstance?.descripcion}"/>
</div>

