
<%@ page import="janus.PrecioVenta" %>

<div id="create-precioVentaInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-precioVentaInstance" action="save">
        <g:hiddenField name="id" value="${precioVentaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nombre
                </span>
            </div>

            <div class="controls">
                <g:textField name="nombre" maxlength="20" class=" required" style="width: 250px" value="${precioVentaInstance?.nombre}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Sigla
                </span>
            </div>

            <div class="controls">
                <g:textField name="sigla" maxlength="6" style="width: 50px" class="" value="${precioVentaInstance?.sigla}"/>
                
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
                <g:textField name="ruc" maxlength="13" style="width: 110px" class=" required" value="${precioVentaInstance?.ruc}"/>
                <span class="mandatory">*</span>
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
                <g:textField name="responsable" maxlength="30" style="width: 300px" class=" required" value="${precioVentaInstance?.responsable}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Especialidad Proveedor
                </span>
            </div>

            <div class="controls">
                <g:select id="especialidadProveedor" name="especialidadProveedor.id" from="${janus.EspecialidadProveedor.list()}" optionKey="id" class="many-to-one " value="${precioVentaInstance?.especialidadProveedor?.id}" noSelection="['null': '']"/>
                
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
                <g:textArea cols="5" rows="3" style="resize: none" name="direccion" maxlength="60" class="" value="${precioVentaInstance?.direccion}"/>
                
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
                <g:textField name="fax" maxlength="11" style="width: 110px" class="" value="${precioVentaInstance?.fax}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Teléfono
                </span>
            </div>

            <div class="controls">
                <g:textField name="telefono" maxlength="40" style="width: 300px" class="" value="${precioVentaInstance?.telefono}"/>
                
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
                <g:textField name="email" maxlength="40" style="width: 300px" class="" value="${precioVentaInstance?.email}"/>
                
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
                <g:textField name="fechaContacto" class="datepicker" style="width: 90px" value="${precioVentaInstance?.fechaContacto}"/>
<script type="text/javascript">
$("#fechaContacto").datepicker({
changeMonth: true,
changeYear: true,
showOn: "both",
buttonImage: "${resource(dir:'images', file:'calendar.png')}",
buttonImageOnly: true
});
</script>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Costos Indirectos Garantías
                </span>
            </div>

            <div class="controls">
                <g:textField name="indiceCostosIndirectosGarantias" maxlength="40" style="width: 300px" class="" value="${precioVentaInstance?.indiceCostosIndirectosGarantias}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Licencia Colegio Ingenieros
                </span>
            </div>

            <div class="controls">
                <g:textField name="licenciaColegioIngenieros" maxlength="10" style="width: 110px" class="" value="${precioVentaInstance?.licenciaColegioIngenieros}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Registro Cámara Construcción
                </span>
            </div>

            <div class="controls">
                <g:textField name="registroCamaraConstruccion" maxlength="7" style="width: 110px" class="" value="${precioVentaInstance?.registroCamaraConstruccion}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Título Profesional Titular
                </span>
            </div>

            <div class="controls">
                <g:textField name="tituloProfecionalTitular" maxlength="4" class="" style="width: 50px" value="${precioVentaInstance?.tituloProfecionalTitular}"/>
                
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
                <g:textArea cols="5" rows="5" style="resize: none" name="observaciones" maxlength="60" class="" value="${precioVentaInstance?.observaciones}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-precioVentaInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-precioVentaInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
