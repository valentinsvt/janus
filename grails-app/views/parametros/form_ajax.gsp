
<%@ page import="janus.Parametros" %>

<div id="create-parametrosInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-parametrosInstance" action="save">
        <g:hiddenField name="id" value="${parametrosInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indicador
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indicador" class=" required" value="${fieldValue(bean: parametrosInstance, field: 'indicador')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Password
                </span>
            </div>

            <div class="controls">
                <g:textField name="password" maxlength="8" style="width: 50px" class="" value="${parametrosInstance?.password}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Dirección de Obras Civiles
                </span>
            </div>

            <div class="controls">
                <g:textField name="direccionObrasCiviles" maxlength="15" class="" value="${parametrosInstance?.direccionObrasCiviles}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Dirección Vialidad Concesiones
                </span>
            </div>

            <div class="controls">
                <g:textField name="direccionVialidadConcesiones" maxlength="15" class="" value="${parametrosInstance?.direccionVialidadConcesiones}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Factor Reducción
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
                    Impreso
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
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-parametrosInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-parametrosInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
