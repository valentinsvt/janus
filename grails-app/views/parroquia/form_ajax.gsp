
<%@ page import="janus.Parroquia" %>

<div id="create-parroquiaInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-parroquiaInstance" action="save">
        <g:hiddenField name="id" value="${parroquiaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nombre
                </span>
            </div>

            <div class="controls">
                <g:textField name="nombre" maxlength="63" style="width: 300px" class=" required" value="${parroquiaInstance?.nombre}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <div class="controls">
                <g:textField name="codigo" maxlength="6" style="width: 50px" class=" required" value="${parroquiaInstance?.codigo}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Cantón
                </span>
            </div>

            <div class="controls">
                <g:select id="canton" name="canton.id" from="${janus.Canton.list()}" optionKey="id" class="many-to-one " value="${parroquiaInstance?.canton?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Urbana
                </span>
            </div>

            <div class="controls">
                <g:textField name="urbana" maxlength="1" style="width: 20px" class="" value="${parroquiaInstance?.urbana}"/>
                
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
                <g:field type="number" name="latitud" class=" required" value="${fieldValue(bean: parroquiaInstance, field: 'latitud')}"/>
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
                <g:field type="number" name="longitud" class=" required" value="${fieldValue(bean: parroquiaInstance, field: 'longitud')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-parroquiaInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-parroquiaInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
