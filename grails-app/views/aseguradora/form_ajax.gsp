
<%@ page import="janus.pac.Aseguradora" %>

<div id="create-Aseguradora" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-Aseguradora" action="save">
        <g:hiddenField name="id" value="${aseguradoraInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nombre
                </span>
            </div>

            <div class="controls">
                <g:textField name="nombre" maxlength="61" class="" value="${aseguradoraInstance?.nombre}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fax
                </span>
            </div>

            <div class="controls">
                <g:textField name="fax" maxlength="15" class="" value="${aseguradoraInstance?.fax}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Telefonos
                </span>
            </div>

            <div class="controls">
                <g:textField name="telefonos" maxlength="63" class="" value="${aseguradoraInstance?.telefonos}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Mail
                </span>
            </div>

            <div class="controls">
                <g:textField name="mail" maxlength="63" class="" value="${aseguradoraInstance?.mail}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Responsable
                </span>
            </div>

            <div class="controls">
                <g:textField name="responsable" maxlength="63" class="" value="${aseguradoraInstance?.responsable}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Contacto
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaContacto" class="" value="${aseguradoraInstance?.fechaContacto}"/>

                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Direccion
                </span>
            </div>

            <div class="controls">
                <g:textField name="direccion" maxlength="127" class="" value="${aseguradoraInstance?.direccion}"/>
                
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
                <g:textField name="observaciones" maxlength="127" class="" value="${aseguradoraInstance?.observaciones}"/>
                
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
                <g:select id="tipo" name="tipo.id" from="${janus.pac.TipoAseguradora.list()}" optionKey="id" class="many-to-one " optionValue="descripcion" value="${aseguradoraInstance?.tipo?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-Aseguradora").validate({
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
