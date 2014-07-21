
<%@ page import="janus.Funcion" %>

<div id="create-funcionInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-funcionInstance" action="save">
        <g:hiddenField name="id" value="${funcionInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <g:if test="${funcionInstance?.id}">
                <div class="controls">
                    <g:textField name="codigo" maxlength="1" style="width: 20px" class=" required" value="${funcionInstance?.codigo}" readonly="readonly"/>
                    <span class="mandatory">*</span>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </g:if><g:else>
            <div class="controls">
                <g:textField name="codigo" maxlength="1" style="width: 20px" class=" required allCaps" value="${funcionInstance?.codigo}" />
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
                <g:textField name="descripcion" maxlength="31" class=" required" value="${funcionInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-funcionInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-funcionInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
