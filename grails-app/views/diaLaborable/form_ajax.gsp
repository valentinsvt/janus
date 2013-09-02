
<%@ page import="janus.DiaLaborable" %>

<div id="create-DiaLaborable" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-DiaLaborable" action="save">
        <g:hiddenField name="id" value="${diaLaborableInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Observaciones
                </span>
            </div>

            <div class="controls">
                <g:textArea name="observaciones" cols="40" rows="5" maxlength="511" class="" value="${diaLaborableInstance?.observaciones}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fecha" class=" required" value="${diaLaborableInstance?.fecha}"/>

                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Ordinal
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="ordinal" class=" required" value="${fieldValue(bean: diaLaborableInstance, field: 'ordinal')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-DiaLaborable").validate({
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
