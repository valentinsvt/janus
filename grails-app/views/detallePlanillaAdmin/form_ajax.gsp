
<%@ page import="janus.ejecucion.DetallePlanillaAdmin" %>

<div id="create-DetallePlanillaAdmin" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-DetallePlanillaAdmin" action="save">
        <g:hiddenField name="id" value="${detallePlanillaAdminInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Planilla
                </span>
            </div>

            <div class="controls">
                <g:select id="planilla" name="planilla.id" from="${janus.ejecucion.PlanillaAdmin.list()}" optionKey="id" class="many-to-one " value="${detallePlanillaAdminInstance?.planilla?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Volumen Obra
                </span>
            </div>

            <div class="controls">
                <g:select id="volumenObra" name="volumenObra.id" from="${janus.VolumenesObra.list()}" optionKey="id" class="many-to-one " value="${detallePlanillaAdminInstance?.volumenObra?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Item
                </span>
            </div>

            <div class="controls">
                <g:select id="item" name="item.id" from="${janus.Item.list()}" optionKey="id" class="many-to-one " value="${detallePlanillaAdminInstance?.item?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Cantidad
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="cantidad" class=" required" value="${fieldValue(bean: detallePlanillaAdminInstance, field: 'cantidad')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Monto
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="monto" class=" required" value="${fieldValue(bean: detallePlanillaAdminInstance, field: 'monto')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Observaciones
                </span>
            </div>

            <div class="controls">
                <g:textField name="observaciones" maxlength="127" class="" value="${detallePlanillaAdminInstance?.observaciones}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-DetallePlanillaAdmin").validate({
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
