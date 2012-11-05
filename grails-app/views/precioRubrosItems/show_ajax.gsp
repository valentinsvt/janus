
<%@ page import="janus.PrecioRubrosItems" %>

<div id="show-precioRubrosItems" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${precioRubrosItemsInstance?.item}">
        <div class="control-group">
            <div>
                <span id="item-label" class="control-label label label-inverse">
                    Item
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="item-label">
        %{--<g:link controller="item" action="show" id="${precioRubrosItemsInstance?.item?.id}">--}%
                    ${precioRubrosItemsInstance?.item?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioRubrosItemsInstance?.precioUnitario}">
        <div class="control-group">
            <div>
                <span id="precioUnitario-label" class="control-label label label-inverse">
                    Precio Unitario
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="precioUnitario-label">
                    <g:fieldValue bean="${precioRubrosItemsInstance}" field="precioUnitario"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioRubrosItemsInstance?.lugar}">
        <div class="control-group">
            <div>
                <span id="lugar-label" class="control-label label label-inverse">
                    Lugar
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="lugar-label">
        %{--<g:link controller="lugar" action="show" id="${precioRubrosItemsInstance?.lugar?.id}">--}%
                    ${precioRubrosItemsInstance?.lugar?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioRubrosItemsInstance?.fecha}">
        <div class="control-group">
            <div>
                <span id="fecha-label" class="control-label label label-inverse">
                    Fecha
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fecha-label">
                    <g:formatDate date="${precioRubrosItemsInstance?.fecha}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
