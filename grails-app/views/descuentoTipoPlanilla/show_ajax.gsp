
<%@ page import="janus.ejecucion.DescuentoTipoPlanilla" %>

<div id="show-descuentoTipoPlanilla" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${descuentoTipoPlanillaInstance?.tipoDescuentoPlanilla}">
        <div class="control-group">
            <div>
                <span id="tipoDescuentoPlanilla-label" class="control-label label label-inverse">
                    Tipo Descuento Planilla
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipoDescuentoPlanilla-label">
        %{--<g:link controller="tipoDescuentoPlanilla" action="show" id="${descuentoTipoPlanillaInstance?.tipoDescuentoPlanilla?.id}">--}%
                    ${descuentoTipoPlanillaInstance?.tipoDescuentoPlanilla?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${descuentoTipoPlanillaInstance?.tipoPlanilla}">
        <div class="control-group">
            <div>
                <span id="tipoPlanilla-label" class="control-label label label-inverse">
                    Tipo Planilla
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipoPlanilla-label">
        %{--<g:link controller="tipoPlanilla" action="show" id="${descuentoTipoPlanillaInstance?.tipoPlanilla?.id}">--}%
                    ${descuentoTipoPlanillaInstance?.tipoPlanilla?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
