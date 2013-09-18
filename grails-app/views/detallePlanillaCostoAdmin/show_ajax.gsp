
<%@ page import="janus.ejecucion.DetallePlanillaCostoAdmin" %>

<div id="show-detallePlanillaCostoAdmin" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${detallePlanillaCostoAdminInstance?.factura}">
        <div class="control-group">
            <div>
                <span id="factura-label" class="control-label label label-inverse">
                    Factura
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="factura-label">
                    <g:fieldValue bean="${detallePlanillaCostoAdminInstance}" field="factura"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${detallePlanillaCostoAdminInstance?.indirectos}">
        <div class="control-group">
            <div>
                <span id="indirectos-label" class="control-label label label-inverse">
                    Indirectos
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indirectos-label">
                    <g:fieldValue bean="${detallePlanillaCostoAdminInstance}" field="indirectos"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${detallePlanillaCostoAdminInstance?.monto}">
        <div class="control-group">
            <div>
                <span id="monto-label" class="control-label label label-inverse">
                    Monto
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="monto-label">
                    <g:fieldValue bean="${detallePlanillaCostoAdminInstance}" field="monto"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${detallePlanillaCostoAdminInstance?.montoIndirectos}">
        <div class="control-group">
            <div>
                <span id="montoIndirectos-label" class="control-label label label-inverse">
                    Monto Indirectos
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="montoIndirectos-label">
                    <g:fieldValue bean="${detallePlanillaCostoAdminInstance}" field="montoIndirectos"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${detallePlanillaCostoAdminInstance?.montoIva}">
        <div class="control-group">
            <div>
                <span id="montoIva-label" class="control-label label label-inverse">
                    Monto Iva
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="montoIva-label">
                    <g:fieldValue bean="${detallePlanillaCostoAdminInstance}" field="montoIva"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${detallePlanillaCostoAdminInstance?.planilla}">
        <div class="control-group">
            <div>
                <span id="planilla-label" class="control-label label label-inverse">
                    Planilla
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="planilla-label">
        %{--<g:link controller="planillaAdmin" action="show" id="${detallePlanillaCostoAdminInstance?.planilla?.id}">--}%
                    ${detallePlanillaCostoAdminInstance?.planilla?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${detallePlanillaCostoAdminInstance?.rubro}">
        <div class="control-group">
            <div>
                <span id="rubro-label" class="control-label label label-inverse">
                    Rubro
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="rubro-label">
                    <g:fieldValue bean="${detallePlanillaCostoAdminInstance}" field="rubro"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${detallePlanillaCostoAdminInstance?.unidad}">
        <div class="control-group">
            <div>
                <span id="unidad-label" class="control-label label label-inverse">
                    Unidad
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="unidad-label">
        %{--<g:link controller="unidad" action="show" id="${detallePlanillaCostoAdminInstance?.unidad?.id}">--}%
                    ${detallePlanillaCostoAdminInstance?.unidad?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
