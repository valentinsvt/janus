
<%@ page import="janus.TipoCuenta" %>

<div id="create-tipoCuentaInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-tipoCuentaInstance" action="save">
        <g:hiddenField name="id" value="${tipoCuentaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <div class="controls">
                <g:textField name="codigo" maxlength="1" style="width: 20px" class=" required" value="${tipoCuentaInstance?.codigo}"/>
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
                <g:textArea cols="5" rows="3" style="resize: none;height: 65px" name="descripcion" maxlength="63" class=" required" value="${tipoCuentaInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-tipoCuentaInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-tipoCuentaInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
