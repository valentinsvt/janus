
<%@ page import="janus.ejecucion.TipoMulta" %>

<div id="create-TipoMulta" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-TipoMulta" action="save">
        <g:hiddenField name="id" value="${tipoMultaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>

            <div class="controls">
                <g:textField name="descripcion" maxlength="63" class=" required" value="${tipoMultaInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Porcentaje
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="porcentaje" class=" required" value="${fieldValue(bean: tipoMultaInstance, field: 'porcentaje')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-TipoMulta").validate({
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
