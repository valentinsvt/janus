
<%@ page import="janus.TipoObjetivo" %>

<div id="create-tipoObjetivoInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-tipoObjetivoInstance" action="save">
        <g:hiddenField name="id" value="${tipoObjetivoInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <div class="controls">
                <g:textField name="codigo" maxlength="1" style="width: 20px" class=" required" value="${tipoObjetivoInstance?.codigo}"/>
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
                <g:textArea cols="5" rows="2" style="resize: none; height: 65px" name="descripcion" maxlength="63" class=" required" value="${tipoObjetivoInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-tipoObjetivoInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-tipoObjetivoInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
