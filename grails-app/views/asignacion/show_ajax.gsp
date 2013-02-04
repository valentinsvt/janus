
<%@ page import="janus.pac.Asignacion" %>

<div id="show-asignacion" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${asignacionInstance?.prespuesto}">
        <div class="control-group">
            <div>
                <span id="prespuesto-label" class="control-label label label-inverse">
                    Prespuesto
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="prespuesto-label">
        %{--<g:link controller="presupuesto" action="show" id="${asignacionInstance?.prespuesto?.id}">--}%
                    ${asignacionInstance?.prespuesto?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${asignacionInstance?.anio}">
        <div class="control-group">
            <div>
                <span id="anio-label" class="control-label label label-inverse">
                    Anio
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="anio-label">
        %{--<g:link controller="anio" action="show" id="${asignacionInstance?.anio?.id}">--}%
                    ${asignacionInstance?.anio?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${asignacionInstance?.valor}">
        <div class="control-group">
            <div>
                <span id="valor-label" class="control-label label label-inverse">
                    Valor
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="valor-label">
                    <g:fieldValue bean="${asignacionInstance}" field="valor"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
