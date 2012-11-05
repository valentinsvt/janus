
<%@ page import="janus.Persona" %>

<div id="create-personaInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-personaInstance" action="save">
        <g:hiddenField name="id" value="${personaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Cédula
                </span>
            </div>

            <div class="controls">
                <g:textField name="cedula" maxlength="10" style="width: 100px" class="" value="${personaInstance?.cedula}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nombre
                </span>
            </div>

            <div class="controls">
                <g:textField name="nombre" maxlength="30" style="width: 310px" class="" value="${personaInstance?.nombre}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Apellido
                </span>
            </div>

            <div class="controls">
                <g:textField name="apellido" maxlength="30" style="width: 310px" class="" value="${personaInstance?.apellido}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="codigo" class=" required" value="${fieldValue(bean: personaInstance, field: 'codigo')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Nacimiento
                </span>
            </div>

            <div class="controls">
                <g:textField name="fechaNacimiento" class="datepicker" style="width: 90px" value="${personaInstance?.fechaNacimiento}"/>
<script type="text/javascript">
$("#fechaNacimiento").datepicker({
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
                    Departamento
                </span>
            </div>

            <div class="controls">
                <g:select id="departamento" name="departamento.id" from="${janus.Departamento.list()}" optionKey="id" class="many-to-one " value="${personaInstance?.departamento?.id}" noSelection="['null': '']"/>
                
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
                <g:textField name="fechaInicio" class="datepicker" style="width: 90px" value="${personaInstance?.fechaInicio}"/>
<script type="text/javascript">
$("#fechaInicio").datepicker({
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
                    Fecha Fin
                </span>
            </div>

            <div class="controls">
                <g:textField name="fechaFin" class="datepicker" style="width: 90px" value="${personaInstance?.fechaFin}"/>
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
                    Sigla
                </span>
            </div>

            <div class="controls">
                <g:textField name="sigla"  maxlength="3" style="width: 40px" class="" value="${personaInstance?.sigla}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Título
                </span>
            </div>

            <div class="controls">
                <g:textField name="titulo" maxlength="4" style="width: 40px" class="" value="${personaInstance?.titulo}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Cargo
                </span>
            </div>

            <div class="controls">
                <g:textArea cols="5" rows="2" style="resize: none;" name="cargo" maxlength="50" class="" value="${personaInstance?.cargo}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-personaInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-personaInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
