
<%@ page import="janus.TipoTramite" %>

<div id="show-tipoTramite" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${tipoTramiteInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Codigo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${tipoTramiteInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoTramiteInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${tipoTramiteInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoTramiteInstance?.padre}">
        <div class="control-group">
            <div>
                <span id="padre-label" class="control-label label label-inverse">
                    Padre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="padre-label">
        %{--<g:link controller="tipoTramite" action="show" id="${tipoTramiteInstance?.padre?.id}">--}%
                    ${tipoTramiteInstance?.padre?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoTramiteInstance?.tiempo}">
        <div class="control-group">
            <div>
                <span id="tiempo-label" class="control-label label label-inverse">
                    Tiempo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tiempo-label">
                    <g:fieldValue bean="${tipoTramiteInstance}" field="tiempo"/>
                </span>
        
            </div>
        </div>
    </g:if>

    <g:if test="${tipoTramiteInstance?.tipo}">
        <div class="control-group">
            <div>
                <span id="tipo-label" class="control-label label label-inverse">
                    Tipo
                </span>
            </div>
            <div class="controls">

                <span aria-labelledby="tiempo-label">
                    <g:fieldValue bean="${tipoTramiteInstance}" field="tipo"/>
                </span>

            </div>
        </div>
    </g:if>
    
    </form>
</div>
