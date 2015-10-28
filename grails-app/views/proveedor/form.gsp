<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>
        Proveedores
    </title>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
</head>
<body>

<%@ page import="janus.pac.Proveedor" %>
<div class="span12 btn-group" role="navigation">
    <a href="${g.createLink(action: 'list')}" class="btn  " id="btn_lista">
        <i class="icon-list-ul"></i>
        Regresar
    </a>

    <a href="#" class="btn btn-ajax btn-new" id="save">
        <i class="icon-save"></i>
        Guardar
    </a>
</div>
<div class="tituloTree" style="margin-top: 15px;height: 25px;float: left;width: 95%">
    <g:if test="${proveedorInstance.id}">
        Proveedor: ${proveedorInstance}
    </g:if>
    <g:else>
        Nuevo proveedor
    </g:else>
</div>

<div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: -10px">
<g:form class="form-horizontal" name="frmSave-Proveedor" action="save">
<g:hiddenField name="id" value="${proveedorInstance?.id}"/>
<div style="width: 45%;height: 600px;float: left;margin:10px;">
    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Especialidad
            </span>
        </div>

        <div class="controls">
            <g:select id="especialidad" name="especialidad.id" from="${janus.EspecialidadProveedor.list()}" optionKey="id" optionValue="descripcion" class="many-to-one " value="${proveedorInstance?.especialidad?.id}" />

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
            %{--<g:textField name="tipo" maxlength="1" class="" value="${proveedorInstance?.tipo}"/>--}%
            <select name="tipo">
                <option value="N" ${(proveedorInstance?.tipo=="N")?"selected":""}>Natural</option>
                <option value="J" ${(proveedorInstance?.tipo=="J")?"selected":""}>Jurídica</option>
                <option value="E" ${(proveedorInstance?.tipo=="E")?"selected":""}>Empresa Pública</option>
            </select>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Ruc
            </span>
        </div>

        <div class="controls">
            <g:textField name="ruc" maxlength="13" class=" required" value="${proveedorInstance?.ruc}" style="width: 130px;"/>
            <span class="mandatory">*</span>
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
            <g:textField name="nombre" maxlength="63" class=" required" value="${proveedorInstance?.nombre}"/>
            <span class="mandatory">*</span>
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
            <g:textField name="nombreContacto" maxlength="31" class="required" value="${proveedorInstance?.nombreContacto}"/>
            <span class="mandatory">*</span>
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
            <g:textField name="apellidoContacto" maxlength="31" class="required" value="${proveedorInstance?.apellidoContacto}"/>
            <span class="mandatory">*</span>
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
            <g:textField name="garante" maxlength="40" class="" value="${proveedorInstance?.garante}"/>

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
            <g:textField name="direccion" maxlength="60" class=" required" value="${proveedorInstance?.direccion}"/>
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
            <g:textField name="telefonos" maxlength="40" class="required " value="${proveedorInstance?.telefonos}" style="width: 120px;"/>
            <span class="mandatory">*</span>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>
</div>
<div style="width: 45%;height: 600px;float: left;margin:10px;">
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
            <g:textField name="email" maxlength="40" class="required" value="${proveedorInstance?.email}"/>
            <span class="mandatory">*</span>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Licencia
            </span>
        </div>

        <div class="controls">
            <g:textField name="licencia" maxlength="10" class="" value="${proveedorInstance?.licencia}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Registro
            </span>
        </div>

        <div class="controls">
            <g:textField name="registro" maxlength="7" class="" value="${proveedorInstance?.registro}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Titulo
            </span>
        </div>

        <div class="controls">
            <g:textField name="titulo" maxlength="4" class="" value="${proveedorInstance?.titulo}"/>

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
            <g:textField name="observaciones" maxlength="127" class="" value="${proveedorInstance?.observaciones}" style="width: 400px;"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Pagar a nombre de:
            </span>
        </div>

        <div class="controls">
            <g:textField name="pagarNombre" maxlength="127" class="" value="${proveedorInstance?.pagarNombre}" style="width: 400px;"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>
</div>




</g:form>

</div>
<script type="text/javascript">
    $("#frmSave-Proveedor").validate({
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
    function submitForm(btn) {
        if ($("#frmSave-Proveedor").valid()) {
            btn.replaceWith(spinner);
        }
        $("#frmSave-Proveedor").submit();
    }

    $("#save").click(function(){
        submitForm($("#save"));
        return false;
    });
</script>

</body>
</html>