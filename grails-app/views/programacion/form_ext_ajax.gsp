<%@ page import="janus.Programacion" %>

<div id="create-Programacion" class="span" role="main">
<g:form class="form-horizontal" name="frmSave-Programacion" action="save">
    <g:hiddenField name="id" value="${programacionInstance?.id}"/>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Descripción
            </span>
        </div>

        <div class="controls">
            <g:textField name="descripcion" maxlength="40" class=" required"
                         value="${programacionInstance?.descripcion}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Fecha Inicio
            </span>
        </div>

        <div class="controls">
            <elm:datepicker name="fechaInicio" class="" value="${programacionInstance?.fechaInicio}"/>


            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Fecha Fin
            </span>
        </div>

        <div class="controls">
            <elm:datepicker name="fechaFin" class="" value="${programacionInstance?.fechaFin}"/>


            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Grupo
            </span>
        </div>

        <div class="controls">
            <g:select id="grupo" name="grupo.id" from="${janus.Grupo.list()}" optionKey="id" class="many-to-one "
                      value="${programacionInstance?.grupo?.id}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    $("#frmSave-Programacion").validate({
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
