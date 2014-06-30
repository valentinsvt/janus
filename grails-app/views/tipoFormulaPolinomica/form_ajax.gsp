
<%@ page import="janus.ejecucion.TipoFormulaPolinomica" %>

<div id="create-TipoFormulaPolinomica" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-TipoFormulaPolinomica" action="save">
        <g:hiddenField name="id" value="${tipoFormulaPolinomicaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <g:if test="${tipoFormulaPolinomicaInstance?.id}">
                <div class="controls">
                    <g:textField name="codigo" class="" value="${tipoFormulaPolinomicaInstance?.codigo}" readonly="readonly"/>

                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </g:if>
            <g:else>
                <div class="controls">
                    <g:textField name="codigo" class="allCaps required" value="${tipoFormulaPolinomicaInstance?.codigo}" maxlength="1"/>
                    <span class="mandatory">*</span>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </g:else>


        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripción
                </span>
            </div>

            <div class="controls">
                <g:textField name="descripcion" class="required" value="${tipoFormulaPolinomicaInstance?.descripcion}" maxlength="15"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-TipoFormulaPolinomica").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $(".btn-success").replaceWith(spinner);
            form.submit();
        }
    });

    $("input").keyup(function (ev) {
        if (ev.keyCode == 13) {
            submitForm($(".btn-success"));
        }
    });
</script>
