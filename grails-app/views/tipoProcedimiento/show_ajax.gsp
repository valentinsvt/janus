
<%@ page import="janus.pac.TipoProcedimiento" %>

<div id="show-tipoProcedimiento" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${tipoProcedimientoInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
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
