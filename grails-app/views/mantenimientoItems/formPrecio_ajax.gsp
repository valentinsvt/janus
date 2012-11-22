<%@ page import="janus.PrecioRubrosItems" %>

<div id="create-precioRubrosItemsInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave" action="savePrecio_ajax">
        <g:hiddenField name="id" value="${precioRubrosItemsInstance?.id}"/>
        <g:hiddenField id="lugar" name="lugar.id" value="${lugar ? precioRubrosItemsInstance?.lugar?.id : -1}"/>
        <g:hiddenField id="item" name="item.id" value="${precioRubrosItemsInstance?.item?.id}"/>
        <g:hiddenField name="all" value="${params.all}"/>
        <g:hiddenField name="ignore" value="${params.ignore}"/>

        <div class="tituloTree">
            Nuevo precio de ${precioRubrosItemsInstance.item.nombre} en ${lugarNombre}%{--${lugar ? precioRubrosItemsInstance.lugar.descripcion : "todos los lugares"}--}%
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha
                </span>
            </div>

            <div class="controls">
                <g:if test="${fecha}">
                    ${fecha}
                    <g:hiddenField name="fehca" value="${fecha}"/>
                </g:if>
                <g:else>
                    <elm:datepicker name="fecha" id="fechaPrecio" class="datepicker required" style="width: 90px" value="${precioRubrosItemsInstance?.fecha}"
                                    yearRange="${(new Date().format('yyyy').toInteger() - 40).toString() + ':' + new Date().format('yyyy')}"
                                    maxDate="new Date()"/>
                </g:else>

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

    </g:form>
</div>

<script type="text/javascript">
    $("#frmSave").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important"
    });
</script>
