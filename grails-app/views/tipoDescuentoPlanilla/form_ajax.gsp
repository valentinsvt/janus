
<%@ page import="janus.ejecucion.TipoDescuentoPlanilla" %>

<div id="create-TipoDescuentoPlanilla" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-TipoDescuentoPlanilla" action="save">
        <g:hiddenField name="id" value="${tipoDescuentoPlanillaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nombre
                </span>
            </div>

            <div class="controls">
                <g:textField name="nombre" class="" value="${tipoDescuentoPlanillaInstance?.nombre}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Porcentaje
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="porcentaje" class=" required" value="${fieldValue(bean: tipoDescuentoPlanillaInstance, field: 'porcentaje')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Valor
                </span>
            </div>

            <div class="controls">
                <g:textField name="valor" class="" value="${tipoDescuentoPlanillaInstance?.valor}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-TipoDescuentoPlanilla").validate({
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
