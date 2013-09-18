
<%@ page import="janus.ejecucion.DetallePlanillaCostoAdmin" %>

<div id="create-DetallePlanillaCostoAdmin" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-DetallePlanillaCostoAdmin" action="save">
        <g:hiddenField name="id" value="${detallePlanillaCostoAdminInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Factura
                </span>
            </div>

            <div class="controls">
                <g:textField name="factura" class="" value="${detallePlanillaCostoAdminInstance?.factura}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indirectos
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indirectos" class=" required" value="${fieldValue(bean: detallePlanillaCostoAdminInstance, field: 'indirectos')}"/>
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
                <g:field type="number" name="monto" class=" required" value="${fieldValue(bean: detallePlanillaCostoAdminInstance, field: 'monto')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Monto Indirectos
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="montoIndirectos" class=" required" value="${fieldValue(bean: detallePlanillaCostoAdminInstance, field: 'montoIndirectos')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Monto Iva
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="montoIva" class=" required" value="${fieldValue(bean: detallePlanillaCostoAdminInstance, field: 'montoIva')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Planilla
                </span>
            </div>

            <div class="controls">
                <g:select id="planilla" name="planilla.id" from="${janus.ejecucion.PlanillaAdmin.list()}" optionKey="id" class="many-to-one  required" value="${detallePlanillaCostoAdminInstance?.planilla?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Rubro
                </span>
            </div>

            <div class="controls">
                <g:textField name="rubro" class="" value="${detallePlanillaCostoAdminInstance?.rubro}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Unidad
                </span>
            </div>

            <div class="controls">
                <g:select id="unidad" name="unidad.id" from="${janus.Unidad.list()}" optionKey="id" class="many-to-one  required" value="${detallePlanillaCostoAdminInstance?.unidad?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-DetallePlanillaCostoAdmin").validate({
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
