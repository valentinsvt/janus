
<%@ page import="janus.Administracion" %>

<div id="show-administracion" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${administracionInstance?.nombrePrefecto}">
        <div class="control-group">
            <div>
                <span id="nombrePrefecto-label" class="control-label label label-inverse">
                    Nombre del Prefecto
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombrePrefecto-label">
                    <g:fieldValue bean="${administracionInstance}" field="nombrePrefecto"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${administracionInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripción
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${administracionInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${administracionInstance?.fechaInicio}">
        <div class="control-group">
            <div>
                <span id="fechaInicio-label" class="control-label label label-inverse">
                    Fecha Inicio
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaInicio-label">
                    <g:formatDate date="${administracionInstance?.fechaInicio}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${administracionInstance?.fechaFin}">
        <div class="control-group">
            <div>
                <span id="fechaFin-label" class="control-label label label-inverse">
                    Fecha Fin
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaFin-label">
                    <g:formatDate date="${administracionInstance?.fechaFin}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
