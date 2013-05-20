
<%@ page import="janus.Indice" %>

<div id="show-indice" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${indiceInstance?.tipoIndice}">
        <div class="control-group">
            <div>
                <span id="tipoIndice-label" class="control-label label label-inverse">
                    Tipo Indice
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipoIndice-label">
        %{--<g:link controller="tipoIndice" action="show" id="${indiceInstance?.tipoIndice?.id}">--}%
                    ${indiceInstance?.tipoIndice?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${indiceInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Codigo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${indiceInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${indiceInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${indiceInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
