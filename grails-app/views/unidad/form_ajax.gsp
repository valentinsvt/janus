
<%@ page import="janus.Unidad" %>

<div id="create-unidadInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-unidadInstance" action="save">
        <g:hiddenField name="id" value="${unidadInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <div class="controls">
                <g:textField name="codigo" maxlength="5" style="width: 60px" class=" required" value="${unidadInstance?.codigo}"  disabled="${unidadInstance.id?'true':'false'}"  />
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripción
                </span>
            </div>

            <div class="controls">
                <g:textField name="descripcion" style="width: 310px" maxlength="31" class=" required" value="${unidadInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-unidadInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-unidadInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
