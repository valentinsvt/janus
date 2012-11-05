
<%@ page import="janus.Ofertas" %>

<div id="create-ofertasInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-ofertasInstance" action="save">
        <g:hiddenField name="id" value="${ofertasInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Baseid
                </span>
            </div>

            <div class="controls">
                <g:textField name="base__id"  maxlength="10" style="width: 100px" class="" value="${ofertasInstance?.base__id}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripción
                </span>
            </div>

            <div class="controls">
                <g:textArea name="descripcion" cols="5" rows="8" style="resize: none;" maxlength="255" class="" value="${ofertasInstance?.descripcion}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Monto
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="monto" class=" required" value="${fieldValue(bean: ofertasInstance, field: 'monto')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Preguntas Iniciales
                </span>
            </div>

            <div class="controls">
                <g:select id="preguntasIniciales" name="preguntasIniciales.id" from="${janus.PeriodosInec.list()}" optionKey="id" class="many-to-one " value="${ofertasInstance?.preguntasIniciales?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Plazo
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="plazo" class=" required" value="${fieldValue(bean: ofertasInstance, field: 'plazo')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Entrega
                </span>
            </div>

            <div class="controls">
                <g:textField name="fechaEntrega" class="datepicker" style="width: 90px" value="${ofertasInstance?.fechaEntrega}"/>
<script type="text/javascript">
$("#fechaEntrega").datepicker({
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
                    Calificación
                </span>
            </div>

            <div class="controls">
                <g:textField name="calificacion" maxlength="1" style="width: 20px" class="" value="${ofertasInstance?.calificacion}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Hojas
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="hojas" class=" required" value="${fieldValue(bean: ofertasInstance, field: 'hojas')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Subsecretario
                </span>
            </div>

            <div class="controls">
                <g:textField style="width: 310px" name="subsecretario" maxlength="40" class="" value="${ofertasInstance?.subsecretario}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Costos Indirectos Garantias
                </span>
            </div>

            <div class="controls">
                <g:textField name="indiceCostosIndirectosGarantias" maxlength="1" style="width: 20px" class="" value="${ofertasInstance?.indiceCostosIndirectosGarantias}"/>
                
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
                <g:textField name="estado" maxlength="1" style="width: 20px" class="" value="${ofertasInstance?.estado}"/>
                
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
                <g:textArea  cols="5" rows="5" style="resize: none" name="observaciones" maxlength="127" class="" value="${ofertasInstance?.observaciones}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-ofertasInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-ofertasInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
