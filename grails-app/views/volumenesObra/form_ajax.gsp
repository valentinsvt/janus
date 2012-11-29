
<%@ page import="janus.VolumenesObra" %>

<div id="create-volumenesObraInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-volumenesObraInstance" action="save">
        <g:hiddenField name="id" value="${volumenesObraInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Obra
                </span>
            </div>

            <div class="controls">
                <g:select id="obra" name="obra.id" from="${janus.Obra.list()}" optionKey="id" class="many-to-one  required" value="${volumenesObraInstance?.obra?.id}"/>
                <span class="mandatory">*</span>
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
                <g:select id="item" name="item.id" from="${janus.Item.list()}" optionKey="id" class="many-to-one  required" value="${volumenesObraInstance?.item?.id}"/>
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
                <g:field type="number" name="cantidad" class=" required" style="width: 150px" maxlength="14" value="${fieldValue(bean: volumenesObraInstance, field: 'cantidad')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Sub Programa
                </span>
            </div>

            <div class="controls">
                <g:select id="subPresupuesto" name="subPresupuesto.id" from="${janus.subPresupuesto.list()}" optionKey="id" class="many-to-one  required" value="${volumenesObraInstance?.subPresupuesto?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-volumenesObraInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-volumenesObraInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
