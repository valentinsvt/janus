<%@ page import="janus.actas.Seccion" %>

<div id="create-Seccion" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave" action="save_ext">
        <g:hiddenField name="id" value="${seccionInstance?.id}"/>
        <g:hiddenField name="acta.id" value="${seccionInstance?.actaId}"/>
        <g:hiddenField name="numero" value="${seccionInstance?.numero}"/>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Número
                </span>
            </div>

            <div class="controls">
                ${fieldValue(bean: seccionInstance, field: 'numero')}
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Título
                </span>
            </div>

            <div class="controls">
                <g:textField name="titulo" maxlength="511" class="inputSeccion required" value="${seccionInstance?.titulo}"/>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
    </g:form>
</div>
<script type="text/javascript">
    $("#titulo").focus();

    $("#frmSave").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function (form) {
            $(".btn-success").replaceWith(spinner);
            form.submit();
        }
    });

    $("input").keydown(function (ev) {
        if (ev.keyCode == 13) {
            submitFormSeccion($("#btnSaveSeccion"));
            ev.preventDefault();
            return false;
        }
    });

</script>
