
<%@ page import="janus.PersonaRol" %>

<div id="show-personaRol" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${personaRolInstance?.persona}">
        <div class="control-group">
            <div>
                <span id="persona-label" class="control-label label label-inverse">
                    Persona
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="persona-label">
        %{--<g:link controller="persona" action="show" id="${personaRolInstance?.persona?.id}">--}%
                    ${personaRolInstance?.persona?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${personaRolInstance?.funcion}">
        <div class="control-group">
            <div>
                <span id="funcion-label" class="control-label label label-inverse">
                    Funcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="funcion-label">
        %{--<g:link controller="funcion" action="show" id="${personaRolInstance?.funcion?.id}">--}%
                    ${personaRolInstance?.funcion?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
