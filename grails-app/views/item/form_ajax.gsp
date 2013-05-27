
<%@ page import="janus.Item" %>

<div id="create-itemInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-itemInstance" action="save">
        <g:hiddenField name="id" value="${itemInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nombre
                </span>
            </div>

            <div class="controls">
                <g:textField name="nombre" maxlength="160" style="width: 300px" class=" required" value="${itemInstance?.nombre}"/>
                <span class="mandatory">*</span>
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
                <g:textField name="codigo" maxlength="20" style="width: 180px" class=" required" value="${itemInstance?.codigo}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Unidad
                </span>
            </div>

            <div class="controls">
                <g:select id="unidad" name="unidad.id" from="${janus.Unidad.list()}" optionKey="id" class="many-to-one " value="${itemInstance?.unidad?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo Item
                </span>
            </div>

            <div class="controls">
                <g:select id="tipoItem" name="tipoItem.id" from="${janus.TipoItem.list()}" optionKey="id" class="many-to-one " value="${itemInstance?.tipoItem?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Peso
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="peso" maxlength="20" class=" required" style="width: 180px" value="${fieldValue(bean: itemInstance, field: 'peso')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Coordinación
                </span>
            </div>

            <div class="controls">
                <g:select id="departamento" name="departamento.id" from="${janus.DepartamentoItem.list()}" optionKey="id" class="many-to-one " value="${itemInstance?.departamento?.id}" noSelection="['null': '']"/>
                
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
                <g:textField name="estado" maxlength="1" style="width: 20px" class="" value="${itemInstance?.estado}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha
                </span>
            </div>

            <div class="controls">
                <g:textField name="fecha" class="datepicker"  style="width: 90px" value="${itemInstance?.fecha}"/>
<script type="text/javascript">
$("#fecha").datepicker({
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
                    Transporte Peso
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="transportePeso"  maxlength="20" style="width: 180px"   class=" required" value="${fieldValue(bean: itemInstance, field: 'transportePeso')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Transporte Volumen
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="transporteVolumen"  maxlength="20"  style="width: 180px " class=" required" value="${fieldValue(bean: itemInstance, field: 'transporteVolumen')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Padre
                </span>
            </div>

            <div class="controls">
                <g:textField name="padre" maxlength="15" style="width: 140px" class="" value="${itemInstance?.padre}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Inec
                </span>
            </div>

            <div class="controls">
                <g:textField name="inec" maxlength="1" style="width: 20px" class="" value="${itemInstance?.inec}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Rendimiento
                </span>
            </div>

            <div class="controls">
                <g:textField name="rendimiento" class="" value="${itemInstance?.rendimiento}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo
                </span>
            </div>

            <div class="controls">
                <g:textField name="tipo" maxlength="1" style="width: 20px" class="" value="${itemInstance?.tipo}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Campo
                </span>
            </div>

            <div class="controls">
                <g:textField name="campo" maxlength="29" style="width: 300px" class="" value="${itemInstance?.campo}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Registro
                </span>
            </div>

            <div class="controls">
                <g:textField name="registro" maxlength="1" style="width: 20px" class="" value="${itemInstance?.registro}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Transporte
                </span>
            </div>

            <div class="controls">
                <g:textField name="transporte" maxlength="2" style="width: 20px" class="" value="${itemInstance?.transporte}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Combustible
                </span>
            </div>

            <div class="controls">
                <g:textField name="combustible" maxlength="1" style="width: 20px" class="" value="${itemInstance?.combustible}"/>
                
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
                <g:textArea cols="5" rows="4" style="resize: none" name="observaciones" maxlength="127" class="" value="${itemInstance?.observaciones}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-itemInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-itemInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
