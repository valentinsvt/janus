<%@ page import="janus.ejecucion.FormulaPolinomicaReajuste" %>

<input type="hidden" id="cntr" value="${cntr}">

<div class="fieldcontain ${hasErrors(bean: formulaPolinomicaReajusteInstance, field: 'tipoFormulaPolinomica', 'error')} required">
	<label for="tipoFormulaPolinomica" class="span2">
		<g:message code="formulaPolinomicaReajuste.tipoFormulaPolinomica.label" default="Tipo de Formula" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="tipoFormulaPolinomica" name="tipoFormulaPolinomica.id" from="${janus.ejecucion.TipoFormulaPolinomica.list()}"
			  optionKey="id" class="many-to-one  required span2" value="${formulaPolinomicaReajusteInstance?.tipoFormulaPolinomica?.id}"
			  optionValue="descripcion"/>
</div>

<div class="fieldcontain ${hasErrors(bean: formulaPolinomicaReajusteInstance, field: 'descripcion', 'error')} required">
	<label for="descripcion" class="span2">
		<g:message code="formulaPolinomicaReajuste.descripcion.label" default="Nombre de la fórmula" />
		<span class="required-indicator">*</span>
	</label>

	<g:textField name="descripcion" class="span4" value="${formulaPolinomicaReajusteInstance?.descripcion}"/>
</div>


