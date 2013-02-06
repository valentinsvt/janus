
<%@ page import="janus.pac.TipoProcedimiento" %>

<div id="create-TipoProcedimiento" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-TipoProcedimiento" action="save">
        <g:hiddenField name="id" value="${tipoProcedimientoInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>

            <div class="controls">
                <g:textField name="descripcion" maxlength="64" class="" value="${tipoProcedimientoInstance?.descripcion}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Sigla
                </span>
            </div>

            <div class="controls">
                <g:textField name="sigla" maxlength="5" class=" required" value="${tipoProcedimientoInstance?.sigla}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Bases
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="bases" class=" required" value="${fieldValue(bean: tipoProcedimientoInstance, field: 'bases')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Techo
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="techo" class=" required" value="${fieldValue(bean: tipoProcedimientoInstance, field: 'techo')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-TipoProcedimiento").validate({
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
