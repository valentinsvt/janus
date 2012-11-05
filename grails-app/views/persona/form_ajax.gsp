<%@ page import="janus.Persona" %>

<div id="create-Persona" class="span" role="main">
<g:form class="form-horizontal" name="frmSave-Persona" action="save">
    <g:hiddenField name="id" value="${personaInstance?.id}"/>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Cedula
            </span>
        </div>

        <div class="controls">
            <g:textField name="cedula" maxlength="10" class="" value="${personaInstance?.cedula}"/>

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
            <g:textField name="nombre" maxlength="30" class="" value="${personaInstance?.nombre}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Apellido
            </span>
        </div>

        <div class="controls">
            <g:textField name="apellido" maxlength="30" class="" value="${personaInstance?.apellido}"/>

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
            <g:field type="number" name="codigo" class=" required" value="${fieldValue(bean: personaInstance, field: 'codigo')}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Fecha Nacimiento
            </span>
        </div>

        <div class="controls">
            <elm:datepicker name="fechaNacimiento" class="" value="${personaInstance?.fechaNacimiento}"/>


            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Departamento
            </span>
        </div>

        <div class="controls">
            <g:select id="departamento" name="departamento.id" from="${janus.Departamento.list()}" optionKey="id" class="many-to-one "
                      value="${personaInstance?.departamento?.id}" noSelection="['null': '']" optionValue="descripcion"/>

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
            <elm:datepicker name="fechaInicio" class="" value="${personaInstance?.fechaInicio}"/>


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
            <elm:datepicker name="fechaFin" class="" value="${personaInstance?.fechaFin}"/>


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
            <g:textField name="sigla" maxlength="3" class="" value="${personaInstance?.sigla}"/>

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
            <g:textField name="titulo" maxlength="4" class="" value="${personaInstance?.titulo}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Cargo
            </span>
        </div>

        <div class="controls">
            <g:textField name="cargo" maxlength="50" class="" value="${personaInstance?.cargo}"/>

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
            <g:textField name="login" maxlength="16" class=" required" value="${personaInstance?.login}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <g:if test="${!personaInstance?.id}">
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Password
                </span>
            </div>

            <div class="controls">
                <g:passwordField name="password" maxlength="63" class=" required" value="${personaInstance?.password}"/>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Verificar Password
                </span>
            </div>

            <div class="controls">
                <g:passwordField name="passwordVerif" equalTo="#password" maxlength="63" class=" required" value="${personaInstance?.password}"/>
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
                <g:passwordField name="autorizacion" maxlength="63" class=" required" value="${personaInstance?.autorizacion}"/>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Verificar Autorizacion
                </span>
            </div>

            <div class="controls">
                <g:passwordField name="autorizacionVerif" equalTo="#autorizacion" maxlength="63" class=" required" value="${personaInstance?.password}"/>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
    </g:if>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Activo
            </span>
        </div>

        <div class="controls">
            <g:radioGroup name="activo" values="['1', '0']" labels="['Sí', 'No']" value="${personaInstance?.id ? personaInstance.activo : '0'}">
                ${it.label} ${it.radio}
            </g:radioGroup>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Fecha Actualizacion Pass
            </span>
        </div>

        <div class="controls">
            <elm:datepicker name="fechaActualizacionPass" value="${personaInstance?.fechaActualizacionPass}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    $("#frmSave-Persona").validate({
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
