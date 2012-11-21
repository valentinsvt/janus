
<%@ page import="janus.Lugar" %>

<div id="show-lugar" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${lugarInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Codigo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${lugarInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${lugarInstance?.tipo}">
        <div class="control-group">
            <div>
                <span id="tipo-label" class="control-label label label-inverse">
                    Tipo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipo-label">
                    <g:fieldValue bean="${lugarInstance}" field="tipo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${lugarInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${lugarInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
