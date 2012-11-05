
<%@ page import="janus.Canton" %>

<div id="create-cantonInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-cantonInstance" action="save">
        <g:hiddenField name="id" value="${cantonInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nombre
                </span>
            </div>

            <div class="controls">
                <g:textField style="width: 310px" name="nombre" maxlength="63" class=" required" value="${cantonInstance?.nombre}"/>
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
                <g:textField name="numero" maxlength="4" style="width: 50px" class=" required" value="${cantonInstance?.numero}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Provincia
                </span>
            </div>

            <div class="controls">
                <g:select id="provincia" name="provincia.id" from="${janus.Provincia.list()}" optionKey="id" class="many-to-one " value="${cantonInstance?.provincia?.id}" noSelection="['null': '']"/>
                
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
                <g:field type="number" name="latitud" class=" required" value="${fieldValue(bean: cantonInstance, field: 'latitud')}"/>
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
                <g:field type="number" name="longitud" class=" required" value="${fieldValue(bean: cantonInstance, field: 'longitud')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-cantonInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-cantonInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
