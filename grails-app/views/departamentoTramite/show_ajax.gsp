
<%@ page import="janus.DepartamentoTramite" %>

<div id="show-departamentoTramite" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${departamentoTramiteInstance?.tipoTramite}">
        <div class="control-group">
            <div>
                <span id="tipoTramite-label" class="control-label label label-inverse">
                    Tipo Tramite
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipoTramite-label">
        %{--<g:link controller="tipoTramite" action="show" id="${departamentoTramiteInstance?.tipoTramite?.id}">--}%
                    ${departamentoTramiteInstance?.tipoTramite?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${departamentoTramiteInstance?.rolTramite}">
        <div class="control-group">
            <div>
                <span id="rolTramite-label" class="control-label label label-inverse">
                    Rol Tramite
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="rolTramite-label">
        %{--<g:link controller="rolTramite" action="show" id="${departamentoTramiteInstance?.rolTramite?.id}">--}%
                    ${departamentoTramiteInstance?.rolTramite?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${departamentoTramiteInstance?.departamento}">
        <div class="control-group">
            <div>
                <span id="departamento-label" class="control-label label label-inverse">
                    Coordinación
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="departamento-label">
        %{--<g:link controller="departamento" action="show" id="${departamentoTramiteInstance?.departamento?.id}">--}%
                    ${departamentoTramiteInstance?.departamento?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
