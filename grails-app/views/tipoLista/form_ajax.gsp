

<%@ page import="janus.TipoLista" %>

<div id="create-TipoLista" class="span" role="main">
<g:form class="form-horizontal" name="frmSave-TipoLista" action="save">
    <g:hiddenField name="id" value="${tipoListaInstance?.id}"/>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Código
            </span>
        </div>

        <g:if test="${tipoListaInstance?.id}">
            <div class="controls">
                <g:textField name="codigo" class="" value="${tipoListaInstance?.codigo}" readonly="readonly"/>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </g:if>
        <g:else>
            <div class="controls">
                <g:textField name="codigo" class="allCaps required" value="${tipoListaInstance?.codigo}" maxlength="2"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </g:else>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Descripción
            </span>
        </div>

        <div class="controls">
            <g:textField name="descripcion" class="required" value="${tipoListaInstance?.descripcion}" maxlength="63"/>
            <span class="mandatory">*</span>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Unidad
            </span>
        </div>

        <div class="controls">
            <g:select name="unidad" from="${['Ton', 'm3']}" noSelection="${["no" : 'Sin unidad']}" value="${tipoListaInstance?.unidad}"/>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    $("#frmSave-TipoLista").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $(".btn-success").replaceWith(spinner);
            form.submit();
        }
    });

    $("input").keyup(function (ev) {
        if (ev.keyCode == 13) {
            submitForm($(".btn-success"));
        }
    });
</script>
