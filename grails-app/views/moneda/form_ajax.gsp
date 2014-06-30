
<%@ page import="janus.pac.Moneda" %>

<div id="create-Moneda" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-Moneda" action="save">
        <g:hiddenField name="id" value="${monedaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <g:if test="${monedaInstance?.id}">
                <div class="controls">
                    <g:textField name="codigo" maxlength="4" class="required" value="${monedaInstance?.codigo}" readonly="readonly"/>

                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </g:if>
            <g:else>
                <div class="controls">
                    <g:textField name="codigo" maxlength="4" class="required allCaps" value="${monedaInstance?.codigo}" />
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
                <g:textField name="descripcion" maxlength="31" class="required" value="${monedaInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-Moneda").validate({
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
