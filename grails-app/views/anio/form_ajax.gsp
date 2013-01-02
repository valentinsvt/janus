
<%@ page import="janus.pac.Anio" %>

<div id="create-Anio" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-Anio" action="save">
        <g:hiddenField name="id" value="${anioInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Anio
                </span>
            </div>

            <div class="controls">
                <g:textField name="anio" maxlength="4" class="" value="${anioInstance?.anio}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Estado
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="estado" class=" required" value="${fieldValue(bean: anioInstance, field: 'estado')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-Anio").validate({
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
