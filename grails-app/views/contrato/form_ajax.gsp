
<%@ page import="janus.Contrato" %>

<div id="create-Contrato" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-Contrato" action="save">
        <g:hiddenField name="id" value="${contratoInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Oferta
                </span>
            </div>

            <div class="controls">
                <g:select id="oferta" name="oferta.id" from="${janus.pac.Oferta.list()}" optionKey="id" class="many-to-one  required" value="${contratoInstance?.oferta?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo Contrato
                </span>
            </div>

            <div class="controls">
                <g:select id="tipoContrato" name="tipoContrato.id" from="${janus.pac.TipoContrato.list()}" optionKey="id" class="many-to-one  required" value="${contratoInstance?.tipoContrato?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo Plazo
                </span>
            </div>

            <div class="controls">
                <g:select id="tipoPlazo" name="tipoPlazo.id" from="${janus.pac.TipoPlazo.list()}" optionKey="id" class="many-to-one  required" value="${contratoInstance?.tipoPlazo?.id}"/>
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
                <g:select id="padre" name="padre.id" from="${janus.Contrato.list()}" optionKey="id" class="many-to-one  required" value="${contratoInstance?.padre?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Periodo Validez
                </span>
            </div>

            <div class="controls">
                <g:select id="periodoValidez" name="periodoValidez.id" from="${janus.pac.PeriodoValidez.list()}" optionKey="id" class="many-to-one  required" value="${contratoInstance?.periodoInec?.id}"/>
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
                <g:textField name="codigo" class=" required" value="${contratoInstance?.codigo}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Objeto
                </span>
            </div>

            <div class="controls">
                <g:textField name="objeto" class=" required" value="${contratoInstance?.objeto}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Subscripcion
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaSubscripcion" class=" required" value="${contratoInstance?.fechaSubscripcion}"/>

                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Ingreso
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaIngreso" class=" required" value="${contratoInstance?.fechaIngreso}"/>

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
                <elm:datepicker name="fechaInicio" class=" required" value="${contratoInstance?.fechaInicio}"/>

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
                <elm:datepicker name="fechaFin" class=" required" value="${contratoInstance?.fechaFin}"/>

                <span class="mandatory">*</span>
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
                <g:field type="number" name="monto" class=" required" value="${fieldValue(bean: contratoInstance, field: 'monto')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Financiamiento
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="financiamiento" class=" required" value="${fieldValue(bean: contratoInstance, field: 'financiamiento')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Anticipo
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="anticipo" class=" required" value="${fieldValue(bean: contratoInstance, field: 'anticipo')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Multas
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="multas" class=" required" value="${fieldValue(bean: contratoInstance, field: 'multas')}"/>
                <span class="mandatory">*</span>
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
                <g:textField name="estado" class=" required" value="${contratoInstance?.estado}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Responsable Tecnico
                </span>
            </div>

            <div class="controls">
                <g:textField name="responsableTecnico" class=" required" value="${contratoInstance?.responsableTecnico}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Firma
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaFirma" class=" required" value="${contratoInstance?.fechaFirma}"/>

                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Cuenta Contable
                </span>
            </div>

            <div class="controls">
                <g:textField name="cuentaContable" class=" required" value="${contratoInstance?.cuentaContable}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Prorroga
                </span>
            </div>

            <div class="controls">
                <g:textField name="prorroga" class=" required" value="${contratoInstance?.prorroga}"/>
                <span class="mandatory">*</span>
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
                <g:textField name="observaciones" class=" required" value="${contratoInstance?.observaciones}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Memo
                </span>
            </div>

            <div class="controls">
                <g:textField name="memo" class=" required" value="${contratoInstance?.memo}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-Contrato").validate({
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
