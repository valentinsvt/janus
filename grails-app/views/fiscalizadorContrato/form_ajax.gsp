
<%@ page import="janus.FiscalizadorContrato" %>

<div id="create-FiscalizadorContrato" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-FiscalizadorContrato" action="save">
        <g:hiddenField name="id" value="${fiscalizadorContratoInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Inicio
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaInicio" class="" value="${fiscalizadorContratoInstance?.fechaInicio}"/>

                
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
                <elm:datepicker name="fechaFin" class="" value="${fiscalizadorContratoInstance?.fechaFin}"/>

                
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
                <g:select id="contrato" name="contrato.id" from="${janus.Contrato.list()}" optionKey="id" class="many-to-one  required" value="${fiscalizadorContratoInstance?.contrato?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fiscalizador
                </span>
            </div>

            <div class="controls">
                <g:select id="fiscalizador" name="fiscalizador.id" from="${janus.Persona.list()}" optionKey="id" class="many-to-one  required" value="${fiscalizadorContratoInstance?.fiscalizador?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-FiscalizadorContrato").validate({
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
