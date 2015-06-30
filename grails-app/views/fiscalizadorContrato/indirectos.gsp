<%@ page import="janus.Departamento" %>
<fieldset>
    <legend>Costos Indirectos para planillas de Costo + %</legend>

    <div class="alert alert-error hide" id="divError">
    </div>

<g:form class="registroContrato" name="frmaIndi" action="guardarIndirectos">
    <g:hiddenField name="cntr" value="${cntr.id}"/>
    <div class="span6" style="margin-top: 10px; width: 500px">

        <div class="span3 formato" style="width: 240px">Porcentaje de costos indirectos</div>

        <div>
            <g:textField name="indirectos" class="number" style="width: 50px"
                         value="${g.formatNumber(number: cntr?.indirectos, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/> %
        </div>
    </div>
</g:form>

</fieldset>

