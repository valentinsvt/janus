
<%@ page import="janus.PrecioRubrosItemsTemporal" %>

<div id="show-precioRubrosItemsTemporal" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${precioRubrosItemsTemporalInstance?.item}">
        <div class="control-group">
            <div>
                <span id="item-label" class="control-label label label-inverse">
                    Item
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="item-label">
        %{--<g:link controller="item" action="show" id="${precioRubrosItemsTemporalInstance?.item?.id}">--}%
                    ${precioRubrosItemsTemporalInstance?.item?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioRubrosItemsTemporalInstance?.precioUnitario}">
        <div class="control-group">
            <div>
                <span id="precioUnitario-label" class="control-label label label-inverse">
                    Precio Unitario
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="precioUnitario-label">
                    <g:fieldValue bean="${precioRubrosItemsTemporalInstance}" field="precioUnitario"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioRubrosItemsTemporalInstance?.lugar}">
        <div class="control-group">
            <div>
                <span id="lugar-label" class="control-label label label-inverse">
                    Lugar
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="lugar-label">
        %{--<g:link controller="lugar" action="show" id="${precioRubrosItemsTemporalInstance?.lugar?.id}">--}%
                    ${precioRubrosItemsTemporalInstance?.lugar?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioRubrosItemsTemporalInstance?.fecha}">
        <div class="control-group">
            <div>
                <span id="fecha-label" class="control-label label label-inverse">
                    Fecha
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fecha-label">
                    <g:formatDate date="${precioRubrosItemsTemporalInstance?.fecha}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
