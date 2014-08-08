
<%@ page import="janus.SubgrupoItems" %>

<div id="create-subgrupoItemsInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-subgrupoItemsInstance" action="save">
        <g:hiddenField name="id" value="${subgrupoItemsInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Grupo
                </span>
            </div>

            <div class="controls">
                <g:select id="grupo" name="grupo.id" from="${janus.Grupo.list()}" optionKey="id" class="many-to-one  required" value="${subgrupoItemsInstance?.grupo?.id}"/>
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
                <g:field type="number" name="codigo" class=" required" value="${fieldValue(bean: subgrupoItemsInstance, field: 'codigo')}"/>
                %{--<g:textField name="codigo" class=" required" maxlength="3" value="${subgrupoItemsInstance?.codigo}"/>--}%
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
                <g:textArea cols="5" rows="3" style="height: 65px; resize: none;" name="descripcion" maxlength="63" class=" required" value="${subgrupoItemsInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-subgrupoItemsInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-subgrupoItemsInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
