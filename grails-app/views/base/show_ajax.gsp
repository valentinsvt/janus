
<%@ page import="janus.Base" %>

<div id="show-base" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${baseInstance?.concurso}">
        <div class="control-group">
            <div>
                <span id="concurso-label" class="control-label label label-inverse">
                    Proceso
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="concurso-label">
        %{--<g:link controller="concurso" action="show" id="${baseInstance?.concurso?.id}">--}%
                    ${baseInstance?.concurso?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${baseInstance?.monto}">
        <div class="control-group">
            <div>
                <span id="monto-label" class="control-label label label-inverse">
                    Monto
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="monto-label">
                    <g:fieldValue bean="${baseInstance}" field="monto"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${baseInstance?.precioVenta}">
        <div class="control-group">
            <div>
                <span id="precioVenta-label" class="control-label label label-inverse">
                    Precio Venta
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="precioVenta-label">
        %{--<g:link controller="precioVenta" action="show" id="${baseInstance?.precioVenta?.id}">--}%
                    ${baseInstance?.precioVenta?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${baseInstance?.fecha}">
        <div class="control-group">
            <div>
                <span id="fecha-label" class="control-label label label-inverse">
                    Fecha
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fecha-label">
                    <g:formatDate date="${baseInstance?.fecha}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
