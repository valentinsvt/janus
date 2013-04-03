
<%@ page import="janus.Parametros" %>

<div id="create-Parametros" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-Parametros" action="save">
        <g:hiddenField name="id" value="${parametrosInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Factor Reduccion
                </span>
            </div>

            <div class="controls">
                <g:textField name="factorReduccion" maxlength="6" class="" value="${parametrosInstance?.factorReduccion}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Factor Velocidad
                </span>
            </div>

            <div class="controls">
                <g:textField name="factorVelocidad" maxlength="6" class="" value="${parametrosInstance?.factorVelocidad}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Capacidad Volquete
                </span>
            </div>

            <div class="controls">
                <g:textField name="capacidadVolquete" maxlength="6" class="" value="${parametrosInstance?.capacidadVolquete}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Factor Volumen
                </span>
            </div>

            <div class="controls">
                <g:textField name="factorVolumen" maxlength="6" class="" value="${parametrosInstance?.factorVolumen}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Factor Reduccion Tiempo
                </span>
            </div>

            <div class="controls">
                <g:textField name="factorReduccionTiempo" maxlength="6" class="" value="${parametrosInstance?.factorReduccionTiempo}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Factor Peso
                </span>
            </div>

            <div class="controls">
                <g:textField name="factorPeso" maxlength="6" class="" value="${parametrosInstance?.factorPeso}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Imprevistos
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="impreso" class=" required" value="${fieldValue(bean: parametrosInstance, field: 'impreso')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Utilidad
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceUtilidad" class=" required" value="${fieldValue(bean: parametrosInstance, field: 'indiceUtilidad')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Contrato
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="contrato" class=" required" value="${fieldValue(bean: parametrosInstance, field: 'contrato')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Totales
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="totales" class=" required" value="${fieldValue(bean: parametrosInstance, field: 'totales')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Gastos Generales
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceGastosGenerales" class=" required" value="${fieldValue(bean: parametrosInstance, field: 'indiceGastosGenerales')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Costos Indirectos Obra
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceCostosIndirectosObra" class=" required" value="${fieldValue(bean: parametrosInstance, field: 'indiceCostosIndirectosObra')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Costos Indirectos Mantenimiento
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceCostosIndirectosMantenimiento" class=" required" value="${fieldValue(bean: parametrosInstance, field: 'indiceCostosIndirectosMantenimiento')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Administracion
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="administracion" class=" required" value="${fieldValue(bean: parametrosInstance, field: 'administracion')}"/>
                <span class="mandatory">*</span>
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
                <g:field type="number" name="indiceCostosIndirectosGarantias" class=" required" value="${fieldValue(bean: parametrosInstance, field: 'indiceCostosIndirectosGarantias')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Costos Indirectos Costos Financieros
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceCostosIndirectosCostosFinancieros" class=" required" value="${fieldValue(bean: parametrosInstance, field: 'indiceCostosIndirectosCostosFinancieros')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Costos Indirectos Vehiculos
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceCostosIndirectosVehiculos" class=" required" value="${fieldValue(bean: parametrosInstance, field: 'indiceCostosIndirectosVehiculos')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Costos Indirectos Promocion
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceCostosIndirectosPromocion" class=" required" value="${fieldValue(bean: parametrosInstance, field: 'indiceCostosIndirectosPromocion')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Costos Indirectos Timbres Provinciales
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceCostosIndirectosTimbresProvinciales" class=" required" value="${fieldValue(bean: parametrosInstance, field: 'indiceCostosIndirectosTimbresProvinciales')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Chofer
                </span>
            </div>

            <div class="controls">
                <g:select id="chofer" name="chofer.id" from="${janus.Item.findAll('from Item where departamento=1')}" optionKey="id" class="many-to-one " value="${parametrosInstance?.chofer?.id}" />
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Volquete
                </span>
            </div>

            <div class="controls">
                <g:select id="volquete" name="volquete.id" from="${janus.Item.findAll('from Item where departamento=157')}" optionKey="id" class="many-to-one " value="${parametrosInstance?.volquete?.id}" />
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-Parametros").validate({
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
