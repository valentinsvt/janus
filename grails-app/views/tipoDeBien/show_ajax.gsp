
<%@ page import="janus.TipoDeBien" %>

<div id="show-tipoDeBien" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${tipoDeBienInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Codigo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${tipoDeBienInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoDeBienInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${tipoDeBienInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoDeBienInstance?.porcentaje}">
        <div class="control-group">
            <div>
                <span id="porcentaje-label" class="control-label label label-inverse">
                    Porcentaje
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="porcentaje-label">
                    <g:fieldValue bean="${tipoDeBienInstance}" field="porcentaje"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
