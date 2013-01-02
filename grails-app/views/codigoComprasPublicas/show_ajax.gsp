
<%@ page import="janus.pac.CodigoComprasPublicas" %>

<div id="show-codigoComprasPublicas" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${codigoComprasPublicasInstance?.numero}">
        <div class="control-group">
            <div>
                <span id="numero-label" class="control-label label label-inverse">
                    Numero
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="numero-label">
                    <g:fieldValue bean="${codigoComprasPublicasInstance}" field="numero"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${codigoComprasPublicasInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${codigoComprasPublicasInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${codigoComprasPublicasInstance?.fecha}">
        <div class="control-group">
            <div>
                <span id="fecha-label" class="control-label label label-inverse">
                    Fecha
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fecha-label">
                    <g:formatDate date="${codigoComprasPublicasInstance?.fecha}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${codigoComprasPublicasInstance?.nivel}">
        <div class="control-group">
            <div>
                <span id="nivel-label" class="control-label label label-inverse">
                    Nivel
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nivel-label">
                    <g:fieldValue bean="${codigoComprasPublicasInstance}" field="nivel"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${codigoComprasPublicasInstance?.padre}">
        <div class="control-group">
            <div>
                <span id="padre-label" class="control-label label label-inverse">
                    Padre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="padre-label">
        %{--<g:link controller="codigoComprasPublicas" action="show" id="${codigoComprasPublicasInstance?.padre?.id}">--}%
                    ${codigoComprasPublicasInstance?.padre?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
