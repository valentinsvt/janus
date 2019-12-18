<%@ page import="janus.Departamento" %>
<fieldset>
    <legend>Memorando  de Autorización de Obras Adicionales</legend>

    <div class="alert alert-error hide" id="divError">
    </div>

%{--<g:form class="registroContrato" name="frmaIndi" action="guardarAdicionales">--}%
<g:form class="registroContrato" name="frmAdicionales" action="guardarAdicionales">
    <g:hiddenField name="cntr" value="${cntr.id}"/>
    <div class="span6" style="margin-top: 10px; width: 500px">

        <div class="span3 formato" style="width: 240px">Memorando número:</div>

        <div>
            <g:textField name="adicionales" class="number allCaps" style="width: 200px"
                         value="${cntr?.adicionales}"/>
        </div>
    </div>
</g:form>

</fieldset>

