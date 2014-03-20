
<%@ page import="janus.Direccion" %>

<div id="create-Direccion" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-Direccion" action="save">
        <g:hiddenField name="id" value="${direccionInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nombre
                </span>
            </div>

            <div class="controls">
                <g:textField name="nombre" maxlength="63" class=" required" value="${direccionInstance?.nombre}" style="width:440px;"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Jefatura
                </span>
            </div>

            <div class="controls">
                <g:textField name="jefatura" maxlength="63" class=" required" value="${direccionInstance?.jefatura}" style="width:400px;" />
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-Direccion").validate({
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
