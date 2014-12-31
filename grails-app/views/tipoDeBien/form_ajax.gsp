
<%@ page import="janus.TipoDeBien" %>

<div id="create-TipoDeBien" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-TipoDeBien" action="save">
        <g:hiddenField name="id" value="${tipoDeBienInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <div class="controls">
                <g:textField name="codigo" maxlength="2" class=" required" value="${tipoDeBienInstance?.codigo}" style="width: 30px;"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripción
                </span>
            </div>

            <div class="controls" style="width: 330px;">
                <g:textField name="descripcion" maxlength="63" class=" required" value="${tipoDeBienInstance?.descripcion}" style="width: 300px;"/>
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
                <g:field type="number" name="porcentaje" class=" required" value="${fieldValue(bean: tipoDeBienInstance, field: 'porcentaje')}" style="width: 60px;"/>
                <span class="mandatory">*</span> Ej: 40 para 40%
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-TipoDeBien").validate({
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
