
<%@ page import="janus.Persona" %>

<div id="show-persona" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${personaInstance?.cedula}">
        <div class="control-group">
            <div>
                <span id="cedula-label" class="control-label label label-inverse">
                    Cédula
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="cedula-label">
                    <g:fieldValue bean="${personaInstance}" field="cedula"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personaInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${personaInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personaInstance?.apellido}">
        <div class="control-group">
            <div>
                <span id="apellido-label" class="control-label label label-inverse">
                    Apellido
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="apellido-label">
                    <g:fieldValue bean="${personaInstance}" field="apellido"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personaInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Codigo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${personaInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personaInstance?.fechaNacimiento}">
        <div class="control-group">
            <div>
                <span id="fechaNacimiento-label" class="control-label label label-inverse">
                    Fecha Nacimiento
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaNacimiento-label">
                    <g:formatDate date="${personaInstance?.fechaNacimiento}" format="dd-MM-yyyy" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personaInstance?.departamento}">
        <div class="control-group">
            <div>
                <span id="departamento-label" class="control-label label label-inverse">
                    Coordinación
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="departamento-label">
        %{--<g:link controller="departamento" action="show" id="${personaInstance?.departamento?.id}">--}%
                    ${personaInstance?.departamento?.descripcion}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personaInstance?.fechaInicio}">
        <div class="control-group">
            <div>
                <span id="fechaInicio-label" class="control-label label label-inverse">
                    Fecha Inicio
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaInicio-label">
                    <g:formatDate date="${personaInstance?.fechaInicio}" format="dd-MM-yyyy"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personaInstance?.fechaFin}">
        <div class="control-group">
            <div>
                <span id="fechaFin-label" class="control-label label label-inverse">
                    Fecha Fin
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaFin-label">
                    <g:formatDate date="${personaInstance?.fechaFin}" format="dd-MM-yyyy" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personaInstance?.sigla}">
        <div class="control-group">
            <div>
                <span id="sigla-label" class="control-label label label-inverse">
                    Sigla
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="sigla-label">
                    <g:fieldValue bean="${personaInstance}" field="sigla"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personaInstance?.titulo}">
        <div class="control-group">
            <div>
                <span id="titulo-label" class="control-label label label-inverse">
                    Titulo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="titulo-label">
                    <g:fieldValue bean="${personaInstance}" field="titulo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personaInstance?.cargo}">
        <div class="control-group">
            <div>
                <span id="cargo-label" class="control-label label label-inverse">
                    Cargo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="cargo-label">
                    <g:fieldValue bean="${personaInstance}" field="cargo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personaInstance?.login}">
        <div class="control-group">
            <div>
                <span id="login-label" class="control-label label label-inverse">
                    Login
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="login-label">
                    <g:fieldValue bean="${personaInstance}" field="login"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    %{--<g:if test="${personaInstance?.password}">--}%
        %{--<div class="control-group">--}%
            %{--<div>--}%
                %{--<span id="password-label" class="control-label label label-inverse">--}%
                    %{--Password--}%
                %{--</span>--}%
            %{--</div>--}%
            %{--<div class="controls">--}%
        %{----}%
                %{--<span aria-labelledby="password-label">--}%
                    %{--<g:fieldValue bean="${personaInstance}" field="password"/>--}%
                %{--</span>--}%
        %{----}%
            %{--</div>--}%
        %{--</div>--}%
    %{--</g:if>--}%
    
    %{--<g:if test="${personaInstance?.autorizacion}">--}%
        %{--<div class="control-group">--}%
            %{--<div>--}%
                %{--<span id="autorizacion-label" class="control-label label label-inverse">--}%
                    %{--Autorizacion--}%
                %{--</span>--}%
            %{--</div>--}%
            %{--<div class="controls">--}%
        %{----}%
                %{--<span aria-labelledby="autorizacion-label">--}%
                    %{--<g:fieldValue bean="${personaInstance}" field="autorizacion"/>--}%
                %{--</span>--}%
        %{----}%
            %{--</div>--}%
        %{--</div>--}%
    %{--</g:if>--}%
    
    <g:if test="${personaInstance?.activo}">
        <div class="control-group">
            <div>
                <span id="activo-label" class="control-label label label-inverse">
                    Activo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="activo-label">
                    <g:fieldValue bean="${personaInstance}" field="activo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personaInstance?.fechaActualizacionPass}">
        <div class="control-group">
            <div>
                <span id="fechaActualizacionPass-label" class="control-label label label-inverse">
                    Fecha Actualizacion Pass
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaActualizacionPass-label">
                    <g:formatDate date="${personaInstance?.fechaActualizacionPass}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personaInstance?.accesos}">
        <div class="control-group">
            <div>
                <span id="accesos-label" class="control-label label label-inverse">
                    Accesos
                </span>
            </div>
            <div class="controls">
        
                <g:each in="${personaInstance.accesos}" var="a">
                    <span aria-labelledby="accesos-label">
            %{--<g:link controller="accs" action="show" id="${a.id}">--}%
                        ${a?.encodeAsHTML()}
            %{--</g:link>--}%
                    </span>
                </g:each>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personaInstance?.sesiones}">
        <div class="control-group">
            <div>
                <span id="sesiones-label" class="control-label label label-inverse">
                    Sesiones
                </span>
            </div>
            <div class="controls">
        
                <g:each in="${personaInstance.sesiones}" var="s">
                    <span aria-labelledby="sesiones-label">
            %{--<g:link controller="sesn" action="show" id="${s.id}">--}%
                        ${s?.encodeAsHTML()}
            %{--</g:link>--}%
                    </span>
                </g:each>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
