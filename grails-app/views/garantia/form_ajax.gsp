
<%@ page import="janus.pac.Garantia" %>

<div id="create-Garantia" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-Garantia" action="save">
        <g:hiddenField name="id" value="${garantiaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo Garantia
                </span>
            </div>

            <div class="controls">
                <g:select id="tipoGarantia" name="tipoGarantia.id" from="${janus.pac.TipoGarantia.list()}" optionKey="id" class="many-to-one " value="${garantiaInstance?.tipoGarantia?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo Documento Garantia
                </span>
            </div>

            <div class="controls">
                <g:select id="tipoDocumentoGarantia" name="tipoDocumentoGarantia.id" from="${janus.pac.TipoDocumentoGarantia.list()}" optionKey="id" class="many-to-one " value="${garantiaInstance?.tipoDocumentoGarantia?.id}" noSelection="['null': '']"/>
                
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
                <g:select id="estado" name="estado.id" from="${janus.pac.EstadoGarantia.list()}" optionKey="id" class="many-to-one " value="${garantiaInstance?.estado?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Moneda
                </span>
            </div>

            <div class="controls">
                <g:select id="moneda" name="moneda.id" from="${janus.pac.Moneda.list()}" optionKey="id" class="many-to-one " value="${garantiaInstance?.moneda?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Aseguradora
                </span>
            </div>

            <div class="controls">
                <g:select id="aseguradora" name="aseguradora.id" from="${janus.pac.Aseguradora.list()}" optionKey="id" class="many-to-one " value="${garantiaInstance?.aseguradora?.id}" noSelection="['null': '']"/>
                
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
                <g:select id="contrato" name="contrato.id" from="${janus.Contrato.list()}" optionKey="id" class="many-to-one " value="${garantiaInstance?.contrato?.id}" noSelection="['null': '']"/>
                
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
                <g:field type="number" name="padre" class=" required" value="${fieldValue(bean: garantiaInstance, field: 'padre')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Codigo
                </span>
            </div>

            <div class="controls">
                <g:textField name="codigo" maxlength="15" class="" value="${garantiaInstance?.codigo}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Numero Renovaciones
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="numeroRenovaciones" class=" required" value="${fieldValue(bean: garantiaInstance, field: 'numeroRenovaciones')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Estado Garantia
                </span>
            </div>

            <div class="controls">
                <g:textField name="estadoGarantia" maxlength="1" class="" value="${garantiaInstance?.estadoGarantia}"/>
                
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
                <g:field type="number" name="monto" class=" required" value="${fieldValue(bean: garantiaInstance, field: 'monto')}"/>
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
                <elm:datepicker name="fechaInicio" class="" value="${garantiaInstance?.fechaInicio}"/>

                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Finalizacion
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaFinalizacion" class="" value="${garantiaInstance?.fechaFinalizacion}"/>

                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Dias Garantizados
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="diasGarantizados" class=" required" value="${fieldValue(bean: garantiaInstance, field: 'diasGarantizados')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Cancelada
                </span>
            </div>

            <div class="controls">
                <g:textField name="cancelada" maxlength="1" class="" value="${garantiaInstance?.cancelada}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Pedido
                </span>
            </div>

            <div class="controls">
                <g:textField name="pedido" maxlength="1" class="" value="${garantiaInstance?.pedido}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-Garantia").validate({
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
