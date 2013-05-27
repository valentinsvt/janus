
<%@ page import="janus.PersonasTramite" %>

<div id="show-personasTramite" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${personasTramiteInstance?.persona}">
        <div class="control-group">
            <div>
                <span id="persona-label" class="control-label label label-inverse">
                    Persona
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="persona-label">
        %{--<g:link controller="persona" action="show" id="${personasTramiteInstance?.persona?.id}">--}%
                    ${personasTramiteInstance?.persona?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personasTramiteInstance?.tramite}">
        <div class="control-group">
            <div>
                <span id="tramite-label" class="control-label label label-inverse">
                    Tramite
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tramite-label">
        %{--<g:link controller="tramite" action="show" id="${personasTramiteInstance?.tramite?.id}">--}%
                    ${personasTramiteInstance?.tramite?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personasTramiteInstance?.rolTramite}">
        <div class="control-group">
            <div>
                <span id="rolTramite-label" class="control-label label label-inverse">
                    Rol Tramite
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="rolTramite-label">
        %{--<g:link controller="rolTramite" action="show" id="${personasTramiteInstance?.rolTramite?.id}">--}%
                    ${personasTramiteInstance?.rolTramite?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personasTramiteInstance?.departamento}">
        <div class="control-group">
            <div>
                <span id="departamento-label" class="control-label label label-inverse">
                    Coordinación
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="departamento-label">
        %{--<g:link controller="departamento" action="show" id="${personasTramiteInstance?.departamento?.id}">--}%
                    ${personasTramiteInstance?.departamento?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
