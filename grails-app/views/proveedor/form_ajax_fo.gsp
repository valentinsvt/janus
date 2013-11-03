<%@ page import="janus.pac.Proveedor" %>

<div id="create-Proveedor" class="span" role="main">
<g:form class="form-horizontal" name="frmSave-Proveedor-fo" action="saveFo">
    <g:hiddenField name="id" value="${proveedorInstance?.id}"/>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Especialidad
            </span>
        </div>

        <div class="controls">
            <g:select id="especialidad" name="especialidad.id" from="${janus.EspecialidadProveedor.list()}" optionKey="id" class="many-to-one "
                      value="${proveedorInstance?.especialidad?.id}" noSelection="['null': '']" optionValue="descripcion"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Tipo proveedor
            </span>
        </div>

        <div class="controls">
            %{--<g:textField name="tipo" maxlength="1" class="" value="${proveedorInstance?.tipo}"/>--}%
            <g:select name="tipo" from="${proveedorInstance.constraints.tipo.inList}" class=" required" value="${proveedorInstance?.tipo}" valueMessagePrefix="proveedor.tipo" style="width: 100px;"/>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                RUC
            </span>
        </div>

        <div class="controls">
            <g:textField name="ruc" maxlength="13" class="" value="${proveedorInstance?.ruc}" style="width: 130px;"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Nombre
            </span>
        </div>

        <div class="controls">
            <g:textField name="nombre" maxlength="63" class="" value="${proveedorInstance?.nombre}" style="width: 400px;"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Nombre Contacto
            </span>
        </div>

        <div class="controls">
            <g:textField name="nombreContacto" maxlength="31" class="" value="${proveedorInstance?.nombreContacto}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Apellido Contacto
            </span>
        </div>

        <div class="controls">
            <g:textField name="apellidoContacto" maxlength="31" class="" value="${proveedorInstance?.apellidoContacto}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Gerente
            </span>
        </div>

        <div class="controls">
            <g:textField name="garante" maxlength="40" class="" value="${proveedorInstance?.garante}" style="width: 300px;"/>

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
            <g:textField name="direccion" maxlength="60" class="" value="${proveedorInstance?.direccion}" style="width: 400px;"/>

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
            <g:textField name="fax" maxlength="11" class="" value="${proveedorInstance?.fax}" style="width: 120px;"/>

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
            <g:textField name="telefonos" maxlength="40" class="" value="${proveedorInstance?.telefonos}" style="width: 220px;"/>

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
            <elm:datepicker name="fechaContacto" class="" value="${proveedorInstance?.fechaContacto}"/>


            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Email
            </span>
        </div>

        <div class="controls">
            <g:textField name="email" maxlength="40" class="" value="${proveedorInstance?.email}" style="width: 300px;"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Origen
            </span>
        </div>

        <div class="controls">
            <g:select name="origen" from="${proveedorInstance.constraints.origen.inList}" class=" required" value="${proveedorInstance?.origen}" valueMessagePrefix="proveedor.origen"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

%{--<div class="control-group">--}%
%{--<div>--}%
%{--<span class="control-label label label-inverse">--}%
%{--Licencia--}%
%{--</span>--}%
%{--</div>--}%

%{--<div class="controls">--}%
%{--<g:textField name="licencia" maxlength="10" class="" value="${proveedorInstance?.licencia}"/>--}%

%{--<p class="help-block ui-helper-hidden"></p>--}%
%{--</div>--}%
%{--</div>--}%

%{--<div class="control-group">--}%
%{--<div>--}%
%{--<span class="control-label label label-inverse">--}%
%{--Registro--}%
%{--</span>--}%
%{--</div>--}%

%{--<div class="controls">--}%
%{--<g:textField name="registro" maxlength="7" class="" value="${proveedorInstance?.registro}"/>--}%

%{--<p class="help-block ui-helper-hidden"></p>--}%
%{--</div>--}%
%{--</div>--}%

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Título
            </span>
        </div>

        <div class="controls">
            <g:textField name="titulo" maxlength="4" class="" value="${proveedorInstance?.titulo}" style="width: 40px;"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

%{--<div class="control-group">--}%
%{--<div>--}%
%{--<span class="control-label label label-inverse">--}%
%{--Estado--}%
%{--</span>--}%
%{--</div>--}%

%{--<div class="controls">--}%
%{--<g:textField name="estado" maxlength="1" class="" value="${proveedorInstance?.estado}"/>--}%

%{--<p class="help-block ui-helper-hidden"></p>--}%
%{--</div>--}%
%{--</div>--}%

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Observaciones
            </span>
        </div>

        <div class="controls">
            <g:textField name="observaciones" maxlength="127" class="" value="${proveedorInstance?.observaciones}" style="width: 400px;"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    $("#frmSave-Proveedor").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function (form) {
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
