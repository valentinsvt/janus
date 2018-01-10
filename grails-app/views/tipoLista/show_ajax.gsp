
<%@ page import="janus.TipoLista" %>

<div id="show-tipoLista" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${tipoListaInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Código
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${tipoListaInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoListaInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripción
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${tipoListaInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoListaInstance?.unidad}">
        <div class="control-group">
            <div>
                <span id="unidad-label" class="control-label label label-inverse">
                    Unidad
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="unidad-label">
                    <g:fieldValue bean="${tipoListaInstance}" field="unidad"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
