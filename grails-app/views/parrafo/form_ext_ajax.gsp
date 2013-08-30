<%@ page import="janus.actas.Parrafo" %>

<div id="create-Parrafo" class="span" role="main">
<g:form class="form-horizontal" name="frmSave" action="save_ext">
    <g:hiddenField name="id" value="${parrafoInstance?.id}"/>
    <g:hiddenField id="seccion" name="seccion.id" value="${parrafoInstance?.seccion?.id}"/>
    <g:hiddenField name="numero" value="${fieldValue(bean: parrafoInstance, field: 'numero')}"/>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Sección
            </span>
        </div>

        <div class="controls">
            ${parrafoInstance.seccion.titulo}
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Número
            </span>
        </div>

        <div class="controls">
            ${parrafoInstance.seccion.numero}.${fieldValue(bean: parrafoInstance, field: 'numero')}
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Tipo Tabla
            </span>
        </div>

        <div class="controls">
            <g:select name="tipoTabla" from="${parrafoInstance.constraints.tipoTabla.inList}" class="" value="${parrafoInstance?.tipoTabla}" valueMessagePrefix="parrafo.tipoTabla" noSelection="['': '']"/>

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
        errorClass     : "label label-important",
        submitHandler  : function (form) {
            $(".btn-success").replaceWith(spinner);
            form.submit();
        }
    });

    $("input").keydown(function (ev) {
        if (ev.keyCode == 13) {
            ev.preventDefault();
            return false;
        }
    });

</script>
