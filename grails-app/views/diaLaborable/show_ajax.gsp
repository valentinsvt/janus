
<%@ page import="janus.DiaLaborable" %>

<div id="show-diaLaborable" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${diaLaborableInstance?.observaciones}">
        <div class="control-group">
            <div>
                <span id="observaciones-label" class="control-label label label-inverse">
                    Observaciones
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="observaciones-label">
                    <g:fieldValue bean="${diaLaborableInstance}" field="observaciones"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${diaLaborableInstance?.fecha}">
        <div class="control-group">
            <div>
                <span id="fecha-label" class="control-label label label-inverse">
                    Fecha
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fecha-label">
                    <g:formatDate date="${diaLaborableInstance?.fecha}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${diaLaborableInstance?.ordinal}">
        <div class="control-group">
            <div>
                <span id="ordinal-label" class="control-label label label-inverse">
                    Ordinal
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="ordinal-label">
                    <g:fieldValue bean="${diaLaborableInstance}" field="ordinal"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
