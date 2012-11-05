
<%@ page import="janus.Indice" %>

<div id="create-indiceInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-indiceInstance" action="save">
        <g:hiddenField name="id" value="${indiceInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo de Institución
                </span>
            </div>

            <div class="controls">
                <g:select id="tipoInstitucion" name="tipoInstitucion.id" from="${janus.TipoInstitucion.list()}" optionKey="id" class="many-to-one " value="${indiceInstance?.tipoInstitucion?.id}" noSelection="['null': '']"/>
                
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
                <g:textField name="codigo" maxlength="20" style="width: 170px" class=" required" value="${indiceInstance?.codigo}"/>
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
                <g:textArea cols="5" rows="2" name="descripcion" maxlength="40" style="resize: none;height: 50px" class=" required" value="${indiceInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-indiceInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-indiceInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
