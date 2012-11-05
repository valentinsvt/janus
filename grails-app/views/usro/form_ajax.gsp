<%@ page import="janus.seguridad.Usro" %>

<div id="create-Usro" class="span" role="main">
<g:form class="form-horizontal" name="frmSave-Usro" action="save">
    <g:hiddenField name="id" value="${usroInstance?.id}"/>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Persona
            </span>
        </div>

        <div class="controls">
            <g:select id="persona" name="persona.id" from="${janus.Persona.list()}" optionKey="id"
                      class="many-to-one  required" value="${usroInstance?.persona?.id}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Login
            </span>
        </div>

        <div class="controls">
            <g:textField name="login" maxlength="15" class=" required" value="${usroInstance?.login}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Password
            </span>
        </div>

        <div class="controls">
            <g:field type="password" name="password" maxlength="64" class=" required"
                     value="${usroInstance?.password}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Autorizacion
            </span>
        </div>

        <div class="controls">
            <g:field type="password" name="autorizacion" maxlength="255" class=" required"
                     value="${usroInstance?.autorizacion}"/>
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
            <g:textField name="sigla" maxlength="8" class=" required" value="${usroInstance?.sigla}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Activo
            </span>
        </div>

        <div class="controls">
            <g:field type="number" name="activo" class=" required"
                     value="${fieldValue(bean: usroInstance, field: 'activo')}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Fecha Pass
            </span>
        </div>

        <div class="controls">
            <g:textField name="fechaPass" class="datepicker" value="${usroInstance?.fechaPass}"/>
            <script type="text/javascript">
                $("#fechaPass").datepicker({
                    changeMonth:true,
                    changeYear:true,
                    showOn:"both",
                    buttonImage:"${resource(dir:'images', file:'calendar.png')}",
                    buttonImageOnly:true
                });
            </script>

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
            <g:textArea name="observaciones" cols="40" rows="5" maxlength="255" class=""
                        value="${usroInstance?.observaciones}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    $("#frmSave-Usro").validate({
        errorPlacement:function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success:function (label) {
            label.parent().hide();
        },
        errorClass:"label label-important",
        submitHandler:function (form) {
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
