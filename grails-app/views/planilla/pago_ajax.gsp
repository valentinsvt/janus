<style type="text/css">
.formato {
    font-weight : bold;
}

.dpto {
    font-size: smaller;
    font-style: italic;
}
input[readonly] {
    cursor: pointer !important;
    background-color: #ebebe5 !important;
}

</style>
<g:form name="frmSave-Planilla" action="savePagoPlanilla" id="${planilla.id}">
    <g:hiddenField name="tipo" value="${tipo}"/>
    <fieldset>
        <div class="row">
            <div class="span5">
                ${extra}
            </div>
        </div>

        <g:if test="${tipo.toInteger() < 5}">
            <div class="row">
                <div class="span2 formato">
                    ${lblMemo}
                </div>

                <div class="span4">
                    <g:textField name="memo" class="span3 required allCaps" maxlength="20" value="${planilla.memoPagoPlanilla}"/>
                    <span class="mandatory">*</span>

                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>
        </g:if>
        <div class="row">
            <div class="span2 formato">
                ${lblFecha}
            </div>

            <div class="span4">
                <elm:datepicker name="fecha" id="fc" class=" span3 required" maxDate="${fechaMax}" minDate="${fechaMin}" value="${fecha}"/>
                %{--<elm:datepicker name="fecha" class=" span3 required"/>--}%
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        ${nombres}

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