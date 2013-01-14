
<%@ page import="janus.pac.PeriodoValidez" %>

<div id="show-periodoValidez" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${periodoValidezInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${periodoValidezInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${periodoValidezInstance?.cierre}">
        <div class="control-group">
            <div>
                <span id="cierre-label" class="control-label label label-inverse">
                    Cierre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="cierre-label">
                    <g:fieldValue bean="${periodoValidezInstance}" field="cierre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${periodoValidezInstance?.fechaFin}">
        <div class="control-group">
            <div>
                <span id="fechaFin-label" class="control-label label label-inverse">
                    Fecha Fin
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaFin-label">
                    <g:formatDate date="${periodoValidezInstance?.fechaFin}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${periodoValidezInstance?.fechaInicio}">
        <div class="control-group">
            <div>
                <span id="fechaInicio-label" class="control-label label label-inverse">
                    Fecha Inicio
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaInicio-label">
                    <g:formatDate date="${periodoValidezInstance?.fechaInicio}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
