
<%@ page import="janus.EstadoObra" %>

<div id="create-estadoObraInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-estadoObraInstance" action="save">
        <g:hiddenField name="id" value="${estadoObraInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <g:if test="${estadoObraInstance?.id}">
                <div class="controls">
                    <g:textField name="codigo" style="width: 20px" readonly="readonly" maxlength="1" class=" required" value="${estadoObraInstance?.codigo}"/>
                    <span class="mandatory">*</span>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </g:if>
            <g:else>
                <div class="controls">
                    <g:textField name="codigo" style="width: 20px" maxlength="1" class=" required allCaps" value="${estadoObraInstance?.codigo}"/>
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
                <g:textField name="descripcion" maxlength="31" style="width: 310px" class=" required" value="${estadoObraInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-estadoObraInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-estadoObraInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
