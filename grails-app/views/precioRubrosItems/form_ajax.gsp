
<%@ page import="janus.PrecioRubrosItems" %>

<div id="create-precioRubrosItemsInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-precioRubrosItemsInstance" action="save">
        <g:hiddenField name="id" value="${precioRubrosItemsInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Item
                </span>
            </div>

            <div class="controls">
                <g:select id="item" name="item.id" from="${janus.Item.list()}" optionKey="id" class="many-to-one  required" value="${precioRubrosItemsInstance?.item?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Precio Unitario
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="precioUnitario" class=" required" value="${fieldValue(bean: precioRubrosItemsInstance, field: 'precioUnitario')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Lugar
                </span>
            </div>

            <div class="controls">
                <g:select id="lugar" name="lugar.id" from="${janus.Lugar.list()}" optionKey="id" class="many-to-one " value="${precioRubrosItemsInstance?.lugar?.id}" noSelection="['null': '']"/>
                
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
                <g:textField name="fecha" class="datepicker required" style="width: 90px" value="${precioRubrosItemsInstance?.fecha}"/>
<script type="text/javascript">
$("#fecha").datepicker({
changeMonth: true,
changeYear: true,
showOn: "both",
buttonImage: "${resource(dir:'images', file:'calendar.png')}",
buttonImageOnly: true
});
</script>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-precioRubrosItemsInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-precioRubrosItemsInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
