<%@ page import="janus.SubPresupuesto" %>

<div id="create-SubPresupuesto" class="span" role="main">
<g:form class="form-horizontal" name="frmSave-SubPresupuesto" action="save">
    <g:hiddenField name="id" value="${subPresupuestoInstance?.id}"/>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Tipo
            </span>
        </div>

        <div class="controls">
            <g:textField name="tipo" maxlength="1" class="allCaps" value="${subPresupuestoInstance?.tipo}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Descripcion
            </span>
        </div>

        <div class="controls">
            <g:textArea cols="15" rows="3" name="descripcion" maxlength="127" class=" required allCaps" value="${subPresupuestoInstance?.descripcion}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    $(".allCaps").keyup(function () {
        this.value = this.value.toUpperCase();
    });

    $("#frmSave-SubPresupuesto").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function (form) {
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
