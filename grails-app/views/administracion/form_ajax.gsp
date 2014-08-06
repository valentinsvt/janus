<%@ page import="janus.Administracion" %>

<div id="create-administracionInstance" class="span" role="main" %{--style="width: 600px"--}%>
<g:form class="form-horizontal" name="frmSave-administracionInstance" action="save">
    <g:hiddenField name="id" value="${administracionInstance?.id}"/>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Nombre Prefecto
            </span>
        </div>

        <div class="controls">
            <g:textField style="width: 290px" name="nombrePrefecto" maxlength="63" class=" required" value="${administracionInstance?.nombrePrefecto}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Descripción
            </span>
        </div>

        <div class="controls">
            <g:textArea cols="5" rows="3" name="descripcion" maxlength="63" style="resize: none; height: 70px" class=" required" value="${administracionInstance?.descripcion}"/>
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
            <elm:datepicker name="fechaInicio" class="datepicker required" style="width: 90px" value="${administracionInstance?.fechaInicio}"/>
            %{--<g:textField name="fechaInicio" class="datepicker required" style="width: 90px" value="${administracionInstance?.fechaInicio}"/>--}%
            %{--<script type="text/javascript">--}%
            %{--$("#fechaInicio").datepicker({--}%
            %{--changeMonth: true,--}%
            %{--changeYear: true,--}%
            %{--showOn: "both",--}%
            %{--buttonImage: "${resource(dir:'images', file:'calendar.png')}",--}%
            %{--buttonImageOnly: true--}%
            %{--});--}%
            %{--</script>--}%
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
            <elm:datepicker name="fechaFin" class="datepicker required" style="width: 90px" value="${administracionInstance?.fechaFin}"/>
            %{--<g:textField name="fechaFin" class="datepicker" style="width: 90px" value="${administracionInstance?.fechaFin}"/>--}%
            %{--<script type="text/javascript">--}%
                %{--$("#fechaFin").datepicker({--}%
                    %{--changeMonth     : true,--}%
                    %{--changeYear      : true,--}%
                    %{--showOn          : "both",--}%
                    %{--buttonImage     : "${resource(dir:'images', file:'calendar.png')}",--}%
                    %{--buttonImageOnly : true--}%
                %{--});--}%
            %{--</script>--}%

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-administracionInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function (form) {
            $("[name=btnSave-administracionInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
