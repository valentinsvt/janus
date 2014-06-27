<%@ page import="janus.Grupo" %>

<div id="create-Grupo" class="span" role="main">
<g:form class="form-horizontal" name="frmSave-Grupo" action="save">
    <g:hiddenField name="id" value="${grupoInstance?.id}"/>

    <div class="control-group">

            <div >
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

        <g:if test="${grupoInstance?.id}">
            <div class="controls">
                <g:textField name="codigo" maxlength="3" class=" required" value="${grupoInstance?.codigo}" readonly="readonly"/>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </g:if>
        <g:else>
            <div class="controls">
                <g:textField name="codigo" maxlength="3" class=" required" value="${grupoInstance?.codigo}"/>
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
            <g:textField name="descripcion" maxlength="31" class=" required" value="${grupoInstance?.descripcion}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Dirección
            </span>
        </div>

        <div class="controls">
            <g:select id="direccion" name="direccion.id" from="${janus.Direccion.list()}" optionKey="id"
                      class="many-to-one  required" value="${grupoInstance?.direccion?.id}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    $("#frmSave-Grupo").validate({
        errorPlacement: function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success: function (label) {
            label.parent().hide();
        },
        errorClass: "label label-important",
        submitHandler: function (form) {
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
