
<%@ page import="janus.Programacion" %>

<div id="create-programacionInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-programacionInstance" action="save">
        <g:hiddenField name="id" value="${programacionInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripción
                </span>
            </div>

            <div class="controls">
                <g:textArea cols="5" rows="2" style="resize: none; height: 40px" name="descripcion" maxlength="40" class=" required" value="${programacionInstance?.descripcion}"/>
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
                <g:textField name="fechaInicio" class="datepicker" style="width: 90px" value="${programacionInstance?.fechaInicio}"/>
<script type="text/javascript">
$("#fechaInicio").datepicker({
changeMonth: true,
changeYear: true,
//showOn: "both",
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
                    Fecha Fin
                </span>
            </div>

            <div class="controls">
                <g:textField name="fechaFin" class="datepicker" style="width: 90px" value="${programacionInstance?.fechaFin}"/>
<script type="text/javascript">
$("#fechaFin").datepicker({
changeMonth: true,
changeYear: true,
//showOn: "both",
buttonImage: "${resource(dir:'images', file:'calendar.png')}",
buttonImageOnly: true
});
</script>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-programacionInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-programacionInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
