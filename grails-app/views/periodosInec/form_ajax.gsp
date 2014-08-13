
<%@ page import="janus.ejecucion.PeriodosInec" %>

<div id="create-PeriodosInec" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-PeriodosInec" action="save">
        <g:hiddenField name="id" value="${periodosInecInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripción
                </span>
            </div>

            <div class="controls">
                <g:textField name="descripcion" maxlength="31" class=" required" value="${periodosInecInstance?.descripcion}"/>
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
                <elm:datepicker name="fechaInicio" class=" required" value="${periodosInecInstance?.fechaInicio}" style="width: 120px;"/>

                <span class="mandatory">*</span>
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
                <elm:datepicker name="fechaFin" class=" required" value="${periodosInecInstance?.fechaFin}" style="width: 120px;"/>
                <span class="mandatory">*</span>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Periodo Cerrado
                </span>
            </div>

            <div class="controls">
                <g:select name="periodoCerrado" from="${periodosInecInstance.constraints.periodoCerrado.inList}" class=" required" value="${periodosInecInstance?.periodoCerrado}" valueMessagePrefix="periodosInec.periodoCerrado" style="width: 60px;"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-PeriodosInec").validate({
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
