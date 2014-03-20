
<%@ page import="janus.ValoresAnuales" %>

<div id="create-ValoresAnuales" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-ValoresAnuales" action="save">
        <g:hiddenField name="id" value="${valoresAnualesInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Año
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="anio" class=" required" value="${fieldValue(bean: valoresAnualesInstance, field: 'anio')}" style="width:60px;" />
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Costo Diesel
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="costoDiesel" class=" required" value="${fieldValue(bean: valoresAnualesInstance, field: 'costoDiesel')}" style="width:60px;"/>
                <span class="mandatory">*</span> Galón
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Costo Grasa
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="costoGrasa" class=" required" value="${fieldValue(bean: valoresAnualesInstance, field: 'costoGrasa')}" style="width:60px;"/>
                <span class="mandatory">*</span> Kilo
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Costo Lubricante
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="costoLubricante" class=" required" value="${fieldValue(bean: valoresAnualesInstance, field: 'costoLubricante')}" style="width:60px;"/>
                <span class="mandatory">*</span> Galón
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Factor CRR
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="factorCostoRepuestosReparaciones" class=" required" value="${fieldValue(bean: valoresAnualesInstance,
                        field: 'factorCostoRepuestosReparaciones')}" style="width:60px;"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Sueldo Básico Unificado
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="sueldoBasicoUnificado" class=" required" value="${fieldValue(bean: valoresAnualesInstance,
                        field: 'sueldoBasicoUnificado')}" style="width:60px;"/>
                <span class="mandatory">*</span> Dólares
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tasa Interés Anual
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="tasaInteresAnual" class=" required" value="${fieldValue(bean: valoresAnualesInstance,
                        field: 'tasaInteresAnual')}" style="width:60px;"/>
                <span class="mandatory">*</span> %. Ej: 13
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Seguro
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="seguro" class=" required" value="${fieldValue(bean: valoresAnualesInstance,
                        field: 'seguro')}" style="width:60px;"/>
                <span class="mandatory">*</span> Prima anual: Ej: 0.03
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>


        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Inflación
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="inflacion" class=" required" value="${fieldValue(bean: valoresAnualesInstance,
                        field: 'inflacion')}" style="width:60px;"/>
                <span class="mandatory">*</span> % Anual
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

                
    </g:form>

<script type="text/javascript">
    $("#frmSave-ValoresAnuales").validate({
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
