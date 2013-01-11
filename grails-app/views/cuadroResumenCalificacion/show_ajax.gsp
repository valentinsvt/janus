
<%@ page import="janus.pac.CuadroResumenCalificacion" %>

<div id="show-cuadroResumenCalificacion" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${cuadroResumenCalificacionInstance?.oferta}">
        <div class="control-group">
            <div>
                <span id="oferta-label" class="control-label label label-inverse">
                    Oferta
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="oferta-label">
        %{--<g:link controller="oferta" action="show" id="${cuadroResumenCalificacionInstance?.oferta?.id}">--}%
                    ${cuadroResumenCalificacionInstance?.oferta?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cuadroResumenCalificacionInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${cuadroResumenCalificacionInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cuadroResumenCalificacionInstance?.valor}">
        <div class="control-group">
            <div>
                <span id="valor-label" class="control-label label label-inverse">
                    Valor
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="valor-label">
                    <g:fieldValue bean="${cuadroResumenCalificacionInstance}" field="valor"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
