
<%@ page import="janus.ejecucion.DescuentoTipoPlanilla" %>

<div id="create-DescuentoTipoPlanilla" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-DescuentoTipoPlanilla" action="save">
        <g:hiddenField name="id" value="${descuentoTipoPlanillaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo Descuento Planilla
                </span>
            </div>

            <div class="controls">
                <g:select id="tipoDescuentoPlanilla" name="tipoDescuentoPlanilla.id" from="${janus.ejecucion.TipoDescuentoPlanilla.list()}" optionKey="id" class="many-to-one  required" value="${descuentoTipoPlanillaInstance?.tipoDescuentoPlanilla?.id}" optionValue="nombre"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo Planilla
                </span>
            </div>

            <div class="controls">
                <g:select id="tipoPlanilla" name="tipoPlanilla.id" from="${janus.ejecucion.TipoPlanilla.list()}" optionKey="id" class="many-to-one  required" value="${descuentoTipoPlanillaInstance?.tipoPlanilla?.id}" optionValue="nombre"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-DescuentoTipoPlanilla").validate({
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
