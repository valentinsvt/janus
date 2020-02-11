<%@ page import="janus.Persona" %>

<g:form class="form-horizontal" name="frmSave-Persona" action="save">
    <g:hiddenField name="id" value="${personaInstance?.id}"/>
    <table cellpadding="5">
        <tr>
            <td width="100px">
                <span class="control-label label label-inverse">
                    Cédula
                </span>
            </td>
            <td width="250px">
                <g:textField name="cedula" maxlength="13" class="span2 required" value="${personaInstance?.cedula}"/>
                <p class="help-block ui-helper-hidden"></p>
                <span class="mandatory">*</span>
            </td>
            <td>
                <span class="control-label label label-inverse">
                    Nombre
                </span>
            </td>
            <td>
                <g:textField name="nombre" maxlength="30" class="span2 required" value="${personaInstance?.nombre}"/>
                <p class="help-block ui-helper-hidden"></p>
                <span class="mandatory">*</span>
            </td>
        </tr>
        <tr>
            <td>
                <span class="control-label label label-inverse">
                    Apellido
                </span>
            </td>
            <td>
                <g:textField name="apellido" maxlength="30" class="span2 required" value="${personaInstance?.apellido}"/>
                <p class="help-block ui-helper-hidden"></p>
                <span class="mandatory">*</span>
            </td>
            <td>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </td>
            <td>
                %{--<g:field type="number" name="codigo"  class="span2 required"  value="${fieldValue(bean: personaInstance, field: 'codigo')}"/>--}%
                <g:textField type="number" name="codigo required"  class="span2"  value="${personaInstance?.codigo}"/>

                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </td>
        </tr>
        <tr>
            <td>
                <span class="control-label label label-inverse">
                    Fecha Nacimiento
                </span>
            </td>
            <td>
                <elm:datepicker name="fechaNacimiento" class="span2" value="${personaInstance?.fechaNacimiento}"/>
                <p class="help-block ui-helper-hidden"></p>
            </td>
            <td>
                <span class="control-label label label-inverse">
                    Coordinación
                </span>
            </td>
            <td>
                <g:select id="departamento" name="departamento.id" from="${janus.Departamento.list([sort:'descripcion'])}" optionKey="id" class="many-to-one span2 required"
                          value="${personaInstance?.departamento?.id}" noSelection="['': '']" optionValue="descripcion"/>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </td>
        </tr>
        <tr>
            <td>
                <span class="control-label label label-inverse">
                    Fecha Inicio
                </span>
            </td>
            <td>
                <elm:datepicker name="fechaInicio" class="span2" value="${personaInstance?.fechaInicio}"/>


                <p class="help-block ui-helper-hidden"></p>
            </td>
            <td>
                <span class="control-label label label-inverse">
                    Fecha Fin
                </span>
            </td>
            <td>
                <elm:datepicker name="fechaFin" class="span2" value="${personaInstance?.fechaFin}"/>


                <p class="help-block ui-helper-hidden"></p>
            </td>
        </tr>
        <tr>
            <td>
                <span class="control-label label label-inverse">
                    Sigla
                </span>
            </td>
            <td>
                <g:textField name="sigla" maxlength="3" class="span2" value="${personaInstance?.sigla}"/>

                <p class="help-block ui-helper-hidden"></p>
            </td>
            <td>
                <span class="control-label label label-inverse">
                    Título
                </span>
            </td>
            <td>
                <g:textField name="titulo" maxlength="4" class="span2" value="${personaInstance?.titulo}"/>

                <p class="help-block ui-helper-hidden"></p>
            </td>
        </tr>
        <tr>
            <td>
                <span class="control-label label label-inverse">
                    Cargo
                </span>
            </td>
            <td colspan="3">
                <g:textField name="cargo" maxlength="50" class="span2" value="${personaInstance?.cargo}" style="width: 500px;"/>

                <p class="help-block ui-helper-hidden"></p>
            </td>
        </tr>
        <tr>
            <td>
                <span class="control-label label label-inverse">
                    Sexo
                </span>
            </td>
%{--
            <td>
                <g:textField name="login" maxlength="16" class="span2 required" value="${personaInstance?.login}"/>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </td>
--}%
            <td>
                <g:select name="sexo" from="${personaInstance.constraints.sexo.inList}" class="" value="${personaInstance?.sexo}" style="width:60px;"/>
            </td>

            <td>
                <span class="control-label label label-inverse">
                    Login
                </span>
            </td>
            <td>
                <g:textField name="login" maxlength="16" class="span2 required" value="${personaInstance?.login}"/>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </td>
        </tr>
        %{--<g:if test="${!personaInstance?.id}">--}%
            <tr>
                <td>
                    <span class="control-label label label-inverse">
                        Password
                    </span>
                </td>
                <td>
                    <g:passwordField name="password" maxlength="63" class="span2 required" value="${personaInstance?.password}"/>

                    <span class="mandatory">*</span>

                    <p class="help-block ui-helper-hidden"></p>
                </td>
                <td>
                    <span class="control-label label label-inverse">
                        Verificar Password
                    </span>
                </td>
                <td>
                    <g:passwordField name="passwordVerif" equalTo="#password" maxlength="63" class="span2 required" value="${personaInstance?.password}"/>
                    <span class="mandatory">*</span>

                    <p class="help-block ui-helper-hidden"></p>
                </td>
            </tr>
            <tr>
                <td>
                    <span class="control-label label label-inverse">
                        Autorizacion
                    </span>
                </td>
                <td>
                    <g:passwordField name="autorizacion" maxlength="63" class="span2 required" value="${personaInstance?.autorizacion}"/>
                    <span class="mandatory">*</span>

                    <p class="help-block ui-helper-hidden"></p>
                </td>
                <td>
                    <span class="control-label label label-inverse">
                        Verificar Autorizacion
                    </span>
                </td>
                <td>
                    <g:passwordField name="autorizacionVerif" equalTo="#autorizacion" maxlength="63" class="span2 required" value="${personaInstance?.autorizacion}"/>
                    <span class="mandatory">*</span>

                    <p class="help-block ui-helper-hidden"></p>
                </td>
            </tr>
        %{--</g:if>--}%
        <tr>
            <td>
                <span class="control-label label label-inverse">
                    Activo
                </span>
            </td>
            <td>

                <g:radioGroup name="activo" values="['1', '0']" labels="['Sí', 'No']" value="${personaInstance?.id ? personaInstance.activo : '0'}" class="required">
                    ${it.label} ${it.radio}
                </g:radioGroup>
                <p class="help-block ui-helper-hidden"></p>
            </td>
            <td>
                <span class="control-label label label-inverse">
                    Fecha Actualizacion Pass
                </span>
            </td>
            <td>
                <elm:datepicker name="fechaActualizacionPass" class="span2" value="${personaInstance?.fechaActualizacionPass}"/>

                <p class="help-block ui-helper-hidden"></p>
            </td>
        </tr>
        <tr>
            <td>
                <span class="control-label label label-inverse">
                    Perfiles
                </span>
            </td>
            <td>
                %{--<g:select name="perfiles" class="span2" multiple="" from="${janus.seguridad.Prfl.list([sort: 'nombre'])}" optionKey="id" optionValue="nombre"--}%
                          %{--value="${personaInstance.id ? janus.seguridad.Sesn.findAllByUsuario(personaInstance)?.id : ''}"/>--}%


                %{--<g:select name="perfiles" class="span2" multiple="true" from="${janus.seguridad.Prfl.findAllByIdNotEqual(4)}" optionKey="id" optionValue="nombre"--}%
                          %{--value="${personaInstance.id ? janus.seguridad.Sesn.findAllByUsuario(personaInstance)?.id : ''}"  />--}%



                <g:select name="perfiles" class="span2 required" multiple="multiple" from="${janus.seguridad.Prfl.findAllByIdNotEqual(4)}" optionKey="id" optionValue="nombre"
                          value="${personaInstance?.sesiones*.perfilId}"  />
                <p class="help-block ui-helper-hidden"></p>
                %{--${personaInstance?.sesiones*.perfilId}--}%
                <span class="mandatory">*</span>
            </td>
            <td>
                <span class="control-label label label-inverse">
                    Mail
                </span>
            </td>
            <td>
                <g:textField name="email" maxlength="63" class="span2 required" value="${personaInstance?.email}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </td>
        </tr>
    </table>
</g:form>


<script type="text/javascript">



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
                ev.keyCode == 190 || ev.keyCode == 110 ||
                ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
                ev.keyCode == 37 || ev.keyCode == 39);
    }

    $("#cedula").keydown(function (ev) {
        if (ev.keyCode == 190 || ev.keyCode == 188 || ev.keyCode == 110) {
//            if ($(this).val().indexOf(".") > -1) {
//                return false
//            }
        }
        return validarNum(ev);
    });







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
