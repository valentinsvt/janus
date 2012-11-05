
<%@ page import="janus.SubPrograma" %>

<div id="create-subProgramaInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-subProgramaInstance" action="save">
        <g:hiddenField name="id" value="${subProgramaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo
                </span>
            </div>

            <div class="controls">
                <g:textField name="tipo" maxlength="1" style="width: 20px" class=" required" value="${subProgramaInstance?.tipo}"/>
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
                <g:textArea cols="5" rows="6" style="resize: none; height: 110px" name="descripcion" maxlength="127" class=" required" value="${subProgramaInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-subProgramaInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-subProgramaInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
