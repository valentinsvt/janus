
<%@ page import="janus.Rubro" %>

<div id="show-rubro" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${rubroInstance?.item}">
        <div class="control-group">
            <div>
                <span id="item-label" class="control-label label label-inverse">
                    Item
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="item-label">
        %{--<g:link controller="item" action="show" id="${rubroInstance?.item?.id}">--}%
                    ${rubroInstance?.item?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${rubroInstance?.cantidad}">
        <div class="control-group">
            <div>
                <span id="cantidad-label" class="control-label label label-inverse">
                    Cantidad
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="cantidad-label">
                    <g:fieldValue bean="${rubroInstance}" field="cantidad"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${rubroInstance?.rubro}">
        <div class="control-group">
            <div>
                <span id="rubro-label" class="control-label label label-inverse">
                    Rubro
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="rubro-label">
        %{--<g:link controller="item" action="show" id="${rubroInstance?.rubro?.id}">--}%
                    ${rubroInstance?.rubro?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${rubroInstance?.fecha}">
        <div class="control-group">
            <div>
                <span id="fecha-label" class="control-label label label-inverse">
                    Fecha
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fecha-label">
                    <g:formatDate date="${rubroInstance?.fecha}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
