
<%@ page import="janus.Presupuesto" %>

<div id="create-presupuestoInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-presupuestoInstance" action="save">
        <g:hiddenField name="id" value="${presupuestoInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Número
                </span>
            </div>

            <div class="controls">
                <g:textField name="numero" maxlength="31" style="width: 208px" class=" required" value="${presupuestoInstance?.numero}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nivel
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="nivel" class=" required" value="${fieldValue(bean: presupuestoInstance, field: 'nivel')}"/>
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

            <div class="controls">
                <g:textArea name="descripcion" cols="40" rows="7" maxlength="255" class=" required" style="resize: none;" value="${presupuestoInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-presupuestoInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-presupuestoInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
