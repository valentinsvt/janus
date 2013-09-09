
<%@ page import="janus.Departamento" %>

<div id="create-Departamento" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-Departamento" action="save">
        <g:hiddenField name="id" value="${departamentoInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripción
                </span>
            </div>

            <div class="controls">
                <g:textField name="descripcion" maxlength="63" class=" required" value="${departamentoInstance?.descripcion}"
                style="width: 300px;"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Dirección
                </span>
            </div>

            <div class="controls">
                <g:select id="direccion" name="direccion.id" from="${janus.Direccion.list()}" optionKey="id" class="many-to-one  required" value="${departamentoInstance?.direccion?.id}"
                          style="width: 300px;"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Permisos
                </span>
            </div>

            <div class="controls">
                <g:textField name="permisos" maxlength="124" class="" value="${departamentoInstance?.permisos}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <div class="controls">
                <g:textField name="codigo" maxlength="4" class=" required" value="${departamentoInstance?.codigo}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-Departamento").validate({
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
