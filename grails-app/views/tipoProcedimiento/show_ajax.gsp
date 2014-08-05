
<%@ page import="janus.pac.TipoProcedimiento" %>

<div id="show-tipoProcedimiento" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${tipoProcedimientoInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripción
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${tipoProcedimientoInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoProcedimientoInstance?.sigla}">
        <div class="control-group">
            <div>
                <span id="sigla-label" class="control-label label label-inverse">
                    Sigla
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="sigla-label">
                    <g:fieldValue bean="${tipoProcedimientoInstance}" field="sigla"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoProcedimientoInstance?.fuente}">
        <div class="control-group">
            <div>
                <span id="fuente-label" class="control-label label label-inverse">
                    Fuente
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fuente-label">
                    <g:fieldValue bean="${tipoProcedimientoInstance}" field="fuente"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoProcedimientoInstance?.bases}">
        <div class="control-group">
            <div>
                <span id="bases-label" class="control-label label label-inverse">
                    Bases
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="bases-label">
                    <g:fieldValue bean="${tipoProcedimientoInstance}" field="bases"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoProcedimientoInstance?.contractual}">
        <div class="control-group">
            <div>
                <span id="contractual-label" class="control-label label label-inverse">
                    Contractual
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="contractual-label">
                    <g:fieldValue bean="${tipoProcedimientoInstance}" field="contractual"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoProcedimientoInstance?.precontractual}">
        <div class="control-group">
            <div>
                <span id="precontractual-label" class="control-label label label-inverse">
                    Precontractual
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="precontractual-label">
                    <g:fieldValue bean="${tipoProcedimientoInstance}" field="precontractual"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoProcedimientoInstance?.preparatorio}">
        <div class="control-group">
            <div>
                <span id="preparatorio-label" class="control-label label label-inverse">
                    Preparatorio
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="preparatorio-label">
                    <g:fieldValue bean="${tipoProcedimientoInstance}" field="preparatorio"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoProcedimientoInstance?.techo}">
        <div class="control-group">
            <div>
                <span id="techo-label" class="control-label label label-inverse">
                    Techo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="techo-label">
                    <g:fieldValue bean="${tipoProcedimientoInstance}" field="techo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
