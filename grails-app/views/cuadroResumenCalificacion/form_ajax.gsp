
<%@ page import="janus.pac.CuadroResumenCalificacion" %>

<div id="create-CuadroResumenCalificacion" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-CuadroResumenCalificacion" action="save">
        <g:hiddenField name="id" value="${cuadroResumenCalificacionInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Oferta
                </span>
            </div>

            <div class="controls">
                <g:select id="oferta" name="oferta.id" from="${janus.pac.Oferta.list()}" optionKey="id" class="many-to-one " value="${cuadroResumenCalificacionInstance?.oferta?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>

            <div class="controls">
                <g:textArea name="descripcion" cols="40" rows="5" maxlength="255" class="" value="${cuadroResumenCalificacionInstance?.descripcion}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Valor
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="valor" class="" value="${fieldValue(bean: cuadroResumenCalificacionInstance, field: 'valor')}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-CuadroResumenCalificacion").validate({
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
