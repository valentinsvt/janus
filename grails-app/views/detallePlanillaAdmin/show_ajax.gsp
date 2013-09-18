
<%@ page import="janus.ejecucion.DetallePlanillaAdmin" %>

<div id="show-detallePlanillaAdmin" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${detallePlanillaAdminInstance?.planilla}">
        <div class="control-group">
            <div>
                <span id="planilla-label" class="control-label label label-inverse">
                    Planilla
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="planilla-label">
        %{--<g:link controller="planillaAdmin" action="show" id="${detallePlanillaAdminInstance?.planilla?.id}">--}%
                    ${detallePlanillaAdminInstance?.planilla?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${detallePlanillaAdminInstance?.volumenObra}">
        <div class="control-group">
            <div>
                <span id="volumenObra-label" class="control-label label label-inverse">
                    Volumen Obra
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="volumenObra-label">
        %{--<g:link controller="volumenesObra" action="show" id="${detallePlanillaAdminInstance?.volumenObra?.id}">--}%
                    ${detallePlanillaAdminInstance?.volumenObra?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${detallePlanillaAdminInstance?.item}">
        <div class="control-group">
            <div>
                <span id="item-label" class="control-label label label-inverse">
                    Item
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="item-label">
        %{--<g:link controller="item" action="show" id="${detallePlanillaAdminInstance?.item?.id}">--}%
                    ${detallePlanillaAdminInstance?.item?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${detallePlanillaAdminInstance?.cantidad}">
        <div class="control-group">
            <div>
                <span id="cantidad-label" class="control-label label label-inverse">
                    Cantidad
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="cantidad-label">
                    <g:fieldValue bean="${detallePlanillaAdminInstance}" field="cantidad"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${detallePlanillaAdminInstance?.monto}">
        <div class="control-group">
            <div>
                <span id="monto-label" class="control-label label label-inverse">
                    Monto
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="monto-label">
                    <g:fieldValue bean="${detallePlanillaAdminInstance}" field="monto"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${detallePlanillaAdminInstance?.observaciones}">
        <div class="control-group">
            <div>
                <span id="observaciones-label" class="control-label label label-inverse">
                    Observaciones
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="observaciones-label">
                    <g:fieldValue bean="${detallePlanillaAdminInstance}" field="observaciones"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
