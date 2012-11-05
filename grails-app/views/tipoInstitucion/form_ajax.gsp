
<%@ page import="janus.TipoInstitucion" %>

<div id="create-tipoInstitucionInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-tipoInstitucionInstance" action="save">
        <g:hiddenField name="id" value="${tipoInstitucionInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <div class="controls">
                <g:textField name="codigo" maxlength="1" style="width: 20px" class=" required" value="${tipoInstitucionInstance?.codigo}"/>
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
                <g:textField style="width: 310px" name="descripcion" maxlength="31" class=" required" value="${tipoInstitucionInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-tipoInstitucionInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-tipoInstitucionInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
