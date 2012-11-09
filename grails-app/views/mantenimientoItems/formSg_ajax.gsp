<%@ page import="janus.SubgrupoItems" %>

<div id="create" class="span" role="main">
<g:form class="form-horizontal" name="frmSave" action="saveSg_ajax">
    <g:hiddenField name="id" value="${subgrupoItemsInstance?.id}"/>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Grupo
            </span>
        </div>

        <div class="controls">
            ${grupo.descripcion}
            <g:hiddenField name="grupo.id" value="${grupo.id}"/>
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
