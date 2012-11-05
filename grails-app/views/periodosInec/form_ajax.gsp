
<%@ page import="janus.PeriodosInec" %>

<div id="create-periodosInecInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-periodosInecInstance" action="save">
        <g:hiddenField name="id" value="${periodosInecInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripción
                </span>
            </div>

            <div class="controls">
                <g:textField name="descripcion" maxlength="31" style="width: 300px" class=" required" value="${periodosInecInstance?.descripcion}"/>
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
                <g:textField name="fechaInicio" class="datepicker required" style="width: 90px" value="${periodosInecInstance?.fechaInicio}"/>
<script type="text/javascript">
$("#fechaInicio").datepicker({
changeMonth: true,
changeYear: true,
showOn: "both",
buttonImage: "${resource(dir:'images', file:'calendar.png')}",
buttonImageOnly: true
});
</script>
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
                <g:textField name="fechaFin" class="datepicker" style="width: 90px" value="${periodosInecInstance?.fechaFin}"/>
<script type="text/javascript">
$("#fechaFin").datepicker({
changeMonth: true,
changeYear: true,
showOn: "both",
buttonImage: "${resource(dir:'images', file:'calendar.png')}",
buttonImageOnly: true
});
</script>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Período Cerrado
                </span>
            </div>

            <div class="controls">
                <g:textField name="periodoCerrado" maxlength="1" style="width: 20px" class=" required" value="${periodosInecInstance?.periodoCerrado}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-periodosInecInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-periodosInecInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
