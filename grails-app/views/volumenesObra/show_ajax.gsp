
<%@ page import="janus.VolumenesObra" %>

<div id="show-volumenesObra" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${volumenesObraInstance?.obra}">
        <div class="control-group">
            <div>
                <span id="obra-label" class="control-label label label-inverse">
                    Obra
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="obra-label">
        %{--<g:link controller="obra" action="show" id="${volumenesObraInstance?.obra?.id}">--}%
                    ${volumenesObraInstance?.obra?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${volumenesObraInstance?.item}">
        <div class="control-group">
            <div>
                <span id="item-label" class="control-label label label-inverse">
                    Item
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="item-label">
        %{--<g:link controller="item" action="show" id="${volumenesObraInstance?.item?.id}">--}%
                    ${volumenesObraInstance?.item?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${volumenesObraInstance?.cantidad}">
        <div class="control-group">
            <div>
                <span id="cantidad-label" class="control-label label label-inverse">
                    Cantidad
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="cantidad-label">
                    <g:fieldValue bean="${volumenesObraInstance}" field="cantidad"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${volumenesObraInstance?.subPresupuesto}">
        <div class="control-group">
            <div>
                <span id="subPresupuesto-label" class="control-label label label-inverse">
                    Sub Programa
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="subPresupuesto-label">
        %{--<g:link controller="subPresupuesto" action="show" id="${volumenesObraInstance?.subPresupuesto?.id}">--}%
                    ${volumenesObraInstance?.subPresupuesto?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
