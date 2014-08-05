
<%@ page import="janus.pac.UnidadIncop" %>

<div id="create-UnidadIncop" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-UnidadIncop" action="save">
        <g:hiddenField name="id" value="${unidadIncopInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <g:if test="${unidadIncopInstance?.id}">


                <div class="controls">
                    <g:textField name="codigo" maxlength="7" class="required allCaps" value="${unidadIncopInstance?.codigo}" readonly="readonly"/>
                    <span class="mandatory">*</span>
                    <p class="help-block ui-helper-hidden"></p>
                </div>

            </g:if>
            <g:else>
                <div class="controls">
                    <g:textField name="codigo" maxlength="7" class="required allCaps" value="${unidadIncopInstance?.codigo}"/>
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
                <g:textField name="descripcion" maxlength="32" class="required" value="${unidadIncopInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-UnidadIncop").validate({
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
