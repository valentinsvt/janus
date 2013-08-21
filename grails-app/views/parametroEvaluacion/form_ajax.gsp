
<%@ page import="janus.pac.ParametroEvaluacion" %>

<div id="create-ParametroEvaluacion" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-ParametroEvaluacion" action="save">
        <g:hiddenField name="id" value="${parametroEvaluacionInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Padre
                </span>
            </div>

            <div class="controls">
                <g:select id="padre" name="padre.id" from="${janus.pac.ParametroEvaluacion.list()}" optionKey="id" class="many-to-one " value="${parametroEvaluacionInstance?.padre?.id}" noSelection="['null': '']"/>
                
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
                <g:textArea name="descripcion" cols="40" rows="5" maxlength="1023" class=" required" value="${parametroEvaluacionInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Proceso
                </span>
            </div>

            <div class="controls">
                <g:select id="concurso" name="concurso.id" from="${janus.pac.Concurso.list()}" optionKey="id" class="many-to-one  required" value="${parametroEvaluacionInstance?.concurso?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Minimo
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="minimo" class=" required" value="${fieldValue(bean: parametroEvaluacionInstance, field: 'minimo')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Orden
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="orden" class=" required" value="${fieldValue(bean: parametroEvaluacionInstance, field: 'orden')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Puntaje
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="puntaje" class=" required" value="${fieldValue(bean: parametroEvaluacionInstance, field: 'puntaje')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-ParametroEvaluacion").validate({
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
