
<%@ page import="janus.Concurso2; janus.Base" %>

<div id="create-baseInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-baseInstance" action="save">
        <g:hiddenField name="id" value="${baseInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Concurso
                </span>
            </div>

            <div class="controls">
                <g:select id="concurso" name="concurso.id" from="${Concurso2.list()}" optionKey="id" class="many-to-one " value="${baseInstance?.concurso?.id}" noSelection="['null': '']"/>
                
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
                <g:field type="number" name="monto" class=" required" value="${fieldValue(bean: baseInstance, field: 'monto')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Precio Venta
                </span>
            </div>

            <div class="controls">
                <g:select id="precioVenta" name="precioVenta.id" from="${janus.PrecioVenta.list()}" optionKey="id" class="many-to-one " value="${baseInstance?.precioVenta?.id}" noSelection="['null': '']"/>
                
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
                <g:textField name="fecha" class="datepicker" style="width: 90px" value="${baseInstance?.fecha}"/>
<script type="text/javascript">
$("#fecha").datepicker({
changeMonth: true,
changeYear: true,
showOn: "both",
buttonImage: "${resource(dir:'images', file:'calendar.png')}",
buttonImageOnly: true
});
</script>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-baseInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-baseInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
