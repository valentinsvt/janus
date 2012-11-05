<%@ page import="janus.seguridad.Usro" %>



<div class="fieldcontain ${hasErrors(bean: usroInstance, field: 'persona', 'error')} required">
    <label for="persona">
        <g:message code="usro.persona.label" default="Persona"/>
        <span class="required-indicator">*</span>
    </label>
    <g:select id="persona" name="persona.id" from="${janus.Persona.list()}" optionKey="id" class="many-to-one  required"
              value="${usroInstance?.persona?.id}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: usroInstance, field: 'login', 'error')} required">
    <label for="login">
        <g:message code="usro.login.label" default="Login"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="login" maxlength="15" class=" required" value="${usroInstance?.login}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: usroInstance, field: 'password', 'error')} required">
    <label for="password">
        <g:message code="usro.password.label" default="Password"/>
        <span class="required-indicator">*</span>
    </label>
    <g:field type="password" name="password" maxlength="64" class=" required" value="${usroInstance?.password}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: usroInstance, field: 'autorizacion', 'error')} required">
    <label for="autorizacion">
        <g:message code="usro.autorizacion.label" default="Autorizacion"/>
        <span class="required-indicator">*</span>
    </label>
    <g:field type="password" name="autorizacion" maxlength="255" class=" required"
             value="${usroInstance?.autorizacion}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: usroInstance, field: 'sigla', 'error')} required">
    <label for="sigla">
        <g:message code="usro.sigla.label" default="Sigla"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="sigla" maxlength="8" class=" required" value="${usroInstance?.sigla}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: usroInstance, field: 'activo', 'error')} required">
    <label for="activo">
        <g:message code="usro.activo.label" default="Activo"/>
        <span class="required-indicator">*</span>
    </label>
    <g:field type="number" name="activo" class=" required" value="${fieldValue(bean: usroInstance, field: 'activo')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: usroInstance, field: 'fechaPass', 'error')} ">
    <label for="fechaPass">
        <g:message code="usro.fechaPass.label" default="Fecha Pass"/>

    </label>
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
</div>

<div class="fieldcontain ${hasErrors(bean: usroInstance, field: 'observaciones', 'error')} ">
    <label for="observaciones">
        <g:message code="usro.observaciones.label" default="Observaciones"/>

    </label>
    <g:textArea name="observaciones" cols="40" rows="5" maxlength="255" class=""
                value="${usroInstance?.observaciones}"/>
</div>

