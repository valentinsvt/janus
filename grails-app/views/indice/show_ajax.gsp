
<%@ page import="janus.Indice" %>

<div id="show-indice" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${indiceInstance?.tipoInstitucion}">
        <div class="control-group">
            <div>
                <span id="tipoInstitucion-label" class="control-label label label-inverse">
                    Tipo Institución
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipoInstitucion-label">
        %{--<g:link controller="tipoInstitucion" action="show" id="${indiceInstance?.tipoInstitucion?.id}">--}%
                    ${indiceInstance?.tipoInstitucion?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${indiceInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Código
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
                    Descripción
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
