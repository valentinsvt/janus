<style type="text/css">
.formato {
    font-weight : bold;
}

.dpto {
    font-size  : smaller;
    font-style : italic;
}
</style>
<g:form name="frmSave-Planilla" action="iniciarObra" id="${planilla.id}">
    <g:hiddenField name="tipo" value="${tipo}"/>
    <fieldset>
        <div class="row">
            <div class="span5">
                ${extra}
            </div>
        </div>

        <div class="row">
            <div class="span2 formato">
                ${lblMemo}
            </div>

            <div class="span4">
                <g:textField name="memo" class="span3 required allCaps" maxlength="20"/>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        %{--<div class="row">--}%
            %{--<div class="span2 formato">--}%
                %{--Firma para el oficio--}%
            %{--</div>--}%

            %{--<div class="span4">--}%
                %{--<g:select name="firma" from="${firma}" optionKey="id" optionValue="${{--}%
                    %{--it.nombre + ' ' + it.apellido + ' (' + it.cargo + ')'--}%
                %{--}}" id="firma"/>--}%
                %{--<span class="mandatory">*</span>--}%

                %{--<p class="help-block ui-helper-hidden"></p>--}%
            %{--</div>--}%
        %{--</div>--}%

        %{--<div class="row">--}%
            %{--<div class="span2 formato">--}%
                %{--Cláusula--}%
            %{--</div>--}%

            %{--<div class="span4">--}%
                %{--<g:textField name="clausula" maxlength="20" class="required input-small" value="octava"/>--}%
                %{--<span class="mandatory">*</span>--}%

                %{--<p class="help-block ui-helper-hidden"></p>--}%
            %{--</div>--}%
        %{--</div>--}%

        %{--<div class="row">--}%
            %{--<div class="span2 formato">--}%
                %{--Numeral plazo--}%
            %{--</div>--}%

            %{--<div class="span4">--}%
                %{--<g:textField name="numeralPlazo" maxlength="10" class="required input-mini" value="8.01"/>--}%
                %{--<span class="mandatory">*</span>--}%
                %{--<small>que señala que el plazo total que el contratista....</small>--}%

                %{--<p class="help-block ui-helper-hidden"></p>--}%
            %{--</div>--}%
        %{--</div>--}%

        %{--<div class="row">--}%
            %{--<div class="span2 formato">--}%
                %{--Numeral anticipo--}%
            %{--</div>--}%

            %{--<div class="span4">--}%
                %{--<g:textField name="numeralAnticipo" maxlength="10" class="required input-mini" value="8.02"/>--}%
                %{--<span class="mandatory">*</span>--}%
                %{--<small>se entenderá entregado el anticipo una vez transcurridas...</small>--}%

                %{--<p class="help-block ui-helper-hidden"></p>--}%
            %{--</div>--}%
        %{--</div>--}%

        <div class="row">
            <div class="span2 formato">
                ${lblFecha}
            </div>

            <div class="span4">
                <elm:datepicker name="fecha" class=" span3 required" maxDate="${fechaMax}" minDate="${fechaMin}" value="${fecha}"/>
                %{--<elm:datepicker name="fecha" class=" span3 required"/>--}%
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="alert alert-danger" style="font-size: large;">
            <strong>
                Tenga en cuenta que los datos ingresados para la impresión del oficio son definitivos. Una vez guardados no podrán ser modificados.
            </strong>
        </div>
        %{--<g:if test="${tipo == '4'}">--}%
        %{--<div class="row">--}%
        %{--<div class="span2 formato">--}%
        %{--Fecha de inicio de obra--}%
        %{--</div>--}%

        %{--<div class="span4">--}%
        %{--<elm:datepicker name="fechaObra" class=" span3 required" maxDate="${fechaMax}" minDate="${fechaMin}" value="${fecha}"/>--}%
        %{--<elm:datepicker name="fecha" class=" span3 required"/>--}%
        %{--<span class="mandatory">*</span>--}%

        %{--<p class="help-block ui-helper-hidden"></p>--}%
        %{--</div>--}%
        %{--</div>--}%
        %{--</g:if>--}%

    </fieldset>
</g:form>

<script type="text/javascript">
    $("#frmSave-Planilla").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function (form) {
            $("[name=btnSave-rubroInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>