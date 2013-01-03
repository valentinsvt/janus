
<%@ page import="janus.Concurso2" %>

<div id="create-concursoInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-concursoInstance" action="save">
        <g:hiddenField name="id" value="${concursoInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Obra
                </span>
            </div>

            <div class="controls">
                <g:select id="obra" name="obra.id" from="${janus.Obra.list()}" optionKey="id" class="many-to-one " value="${concursoInstance?.obra?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Administración
                </span>
            </div>

            <div class="controls">
                <g:select id="administracion" name="administracion.id" from="${janus.Administracion.list()}" optionKey="id" class="many-to-one " value="${concursoInstance?.administracion?.id}" noSelection="['null': '']"/>
                
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
                <g:textField name="codigo" maxlength="15" style="width: 140px" class=" required" value="${concursoInstance?.codigo}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Objetivo
                </span>
            </div>

            <div class="controls">
                <g:textArea name="objetivo" cols="5" rows="5" style="resize: none" maxlength="255" class="" value="${concursoInstance?.objetivo}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Base
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="base" class=" required" max="14" style="width: 140px" value="${fieldValue(bean: concursoInstance, field: 'base')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo Cuenta
                </span>
            </div>

            <div class="controls">
                <g:select id="tipoCuenta" name="tipoCuenta.id" from="${janus.TipoCuenta.list()}" optionKey="id" class="many-to-one " value="${concursoInstance?.tipoCuenta?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Estado
                </span>
            </div>

            <div class="controls">
                <g:textField name="estado" maxlength="1" style="width: 20px" class="" value="${concursoInstance?.estado}"/>
                
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
                <g:textField name="fechaInicio" class="datepicker" style="width: 90px" value="${concursoInstance?.fechaInicio}"/>
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
                    Fecha Cierre
                </span>
            </div>

            <div class="controls">
                <g:textField name="fechaCierre" class="datepicker" style="width: 90px" value="${concursoInstance?.fechaCierre}"/>
<script type="text/javascript">
$("#fechaCierre").datepicker({
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
                    Observaciones
                </span>
            </div>

            <div class="controls">
                <g:textArea cols="5" rows="5" style="resize: none;" name="observaciones" maxlength="127" class="" value="${concursoInstance?.observaciones}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-concursoInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-concursoInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
