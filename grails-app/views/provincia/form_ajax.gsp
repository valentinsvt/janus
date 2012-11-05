
<%@ page import="janus.Provincia" %>

<div id="create-provinciaInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-provinciaInstance" action="save">
        <g:hiddenField name="id" value="${provinciaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nombre
                </span>
            </div>

            <div class="controls">
                <g:textField name="nombre" maxlength="63" style="width: 310px" class=" required" value="${provinciaInstance?.nombre}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Número
                </span>
            </div>

            <div class="controls">
                <g:textField name="numero" maxlength="2" style="width: 20px" class=" required" value="${provinciaInstance?.numero}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Latitud
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="latitud" class=" required" value="${fieldValue(bean: provinciaInstance, field: 'latitud')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Longitud
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="longitud" class=" required" value="${fieldValue(bean: provinciaInstance, field: 'longitud')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-provinciaInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-provinciaInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
