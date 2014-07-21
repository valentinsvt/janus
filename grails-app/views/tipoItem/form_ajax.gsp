
<%@ page import="janus.TipoItem" %>

<div id="create-tipoItemInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-tipoItemInstance" action="save">
        <g:hiddenField name="id" value="${tipoItemInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <g:if test="${tipoItemInstance?.id}">
                <div class="controls">
                    <g:textField name="codigo" maxlength="1" style="width: 20px" class=" required" value="${tipoItemInstance?.codigo}" readonly="readonly"/>
                    <span class="mandatory">*</span>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </g:if>
            <g:else>
                <div class="controls">
                    <g:textField name="codigo" maxlength="1" style="width: 20px" class=" required allCaps" value="${tipoItemInstance?.codigo}" />
                    <span class="mandatory">*</span>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </g:else>

        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripción
                </span>
            </div>

            <div class="controls">
                <g:textField name="descripcion" maxlength="20" style="width: 200px" class=" required" value="${tipoItemInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-tipoItemInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-tipoItemInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
