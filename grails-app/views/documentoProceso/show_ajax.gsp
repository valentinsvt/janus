
<%@ page import="janus.pac.DocumentoProceso" %>

<div id="show-documentoProceso" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${documentoProcesoInstance?.etapa}">
        <div class="control-group">
            <div>
                <span id="etapa-label" class="control-label label label-inverse">
                    Etapa
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="etapa-label">
        %{--<g:link controller="etapa" action="show" id="${documentoProcesoInstance?.etapa?.id}">--}%
                    ${documentoProcesoInstance?.etapa?.descripcion?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${documentoProcesoInstance?.concurso}">
        <div class="control-group">
            <div>
                <span id="concurso-label" class="control-label label label-inverse">
                    Proceso
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="concurso-label">
        %{--<g:link controller="concurso" action="show" id="${documentoProcesoInstance?.concurso?.id}">--}%
                    ${documentoProcesoInstance?.concurso?.id?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${documentoProcesoInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${documentoProcesoInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${documentoProcesoInstance?.palabrasClave}">
        <div class="control-group">
            <div>
                <span id="palabrasClave-label" class="control-label label label-inverse">
                    Palabras Clave
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="palabrasClave-label">
                    <g:fieldValue bean="${documentoProcesoInstance}" field="palabrasClave"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${documentoProcesoInstance?.resumen}">
        <div class="control-group">
            <div>
                <span id="resumen-label" class="control-label label label-inverse">
                    Resumen
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="resumen-label">
                    <g:fieldValue bean="${documentoProcesoInstance}" field="resumen"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${documentoProcesoInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${documentoProcesoInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
