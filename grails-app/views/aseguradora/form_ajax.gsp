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
            <g:textField name="nombre" maxlength="61" class="required" value="${aseguradoraInstance?.nombre}" />
            <span class="mandatory">*</span>
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
                Teléfonos
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
                Dirección
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
            <g:select id="tipo" name="tipo.id" from="${janus.pac.TipoAseguradora.list()}" optionKey="id" optionValue="descripcion"
                      class="many-to-one " value="${aseguradoraInstance?.tipo?.id}" noSelection="['null': '']"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    $("#frmSave-Aseguradora").validate({
        errorPlacement: function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success: function (label) {
            label.parent().hide();
        },
        errorClass: "label label-important",
        submitHandler: function (form) {
            $(".btn-success").replaceWith(spinner);
            form.submit();
        }
    });

    $("input").keyup(function (ev) {
        if (ev.keyCode == 13) {
            submitForm($(".btn-success"));
        }
    });

    function validarNum(ev) {
        /*
         48-57      -> numeros
         96-105     -> teclado numerico
         188        -> , (coma)
         190        -> . (punto) teclado
         110        -> . (punto) teclado numerico
         8          -> backspace
         46         -> delete
         9          -> tab
         37         -> flecha izq
         39         -> flecha der
         */
        return ((ev.keyCode >= 48 && ev.keyCode <= 57) ||
                (ev.keyCode >= 96 && ev.keyCode <= 105) ||
                ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
                ev.keyCode == 37 || ev.keyCode == 39);
    }


    $("#fax").keydown(function (ev){

        return validarNum(ev)
    })

    $("#telefonos").keydown(function (ev){

        return validarNum(ev)
    })


</script>
