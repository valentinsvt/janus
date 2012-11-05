
<%@ page import="janus.Departamento" %>

<div id="create-departamentoInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-departamentoInstance" action="save">
        <g:hiddenField name="id" value="${departamentoInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripción
                </span>
            </div>

            <div class="controls">
                <g:textField name="descripcion" maxlength="31" style="width: 310px" class=" required" value="${departamentoInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-departamentoInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-departamentoInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
