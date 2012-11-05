
<%@ page import="janus.Rubro" %>

<div id="create-rubroInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-rubroInstance" action="save">
        <g:hiddenField name="id" value="${rubroInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Item
                </span>
            </div>

            <div class="controls">
                <g:select id="item" name="item.id" from="${janus.Item.list()}" optionKey="id" class="many-to-one  required" value="${rubroInstance?.item?.id}"/>
                <span class="mandatory">*</span>
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
                <g:field type="number" name="cantidad" class=" required" value="${fieldValue(bean: rubroInstance, field: 'cantidad')}"/>
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
                <g:select id="rubro" name="rubro.id" from="${janus.Item.list()}" optionKey="id" class="many-to-one  required" value="${rubroInstance?.rubro?.id}"/>
                <span class="mandatory">*</span>
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
                <g:textField name="fecha" class="datepicker"  style="width: 90px" value="${rubroInstance?.fecha}"/>
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

    $("#frmSave-rubroInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-rubroInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
