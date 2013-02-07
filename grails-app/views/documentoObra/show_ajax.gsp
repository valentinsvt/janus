
<%@ page import="janus.pac.DocumentoObra" %>

<div id="show-documentoProceso" class="span5" role="main">

    <form class="form-horizontal">

    <g:if test="${documentoObraInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${documentoObraInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${documentoObraInstance?.palabrasClave}">
        <div class="control-group">
            <div>
                <span id="palabrasClave-label" class="control-label label label-inverse">
                    Palabras Clave
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="palabrasClave-label">
                    <g:fieldValue bean="${documentoObraInstance}" field="palabrasClave"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${documentoObraInstance?.resumen}">
        <div class="control-group">
            <div>
                <span id="resumen-label" class="control-label label label-inverse">
                    Resumen
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="resumen-label">
                    <g:fieldValue bean="${documentoObraInstance}" field="resumen"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${documentoObraInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${documentoObraInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
