
<%@ page import="janus.Presupuesto" %>

<div id="show-presupuesto" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${presupuestoInstance?.numero}">
        <div class="control-group">
            <div>
                <span id="numero-label" class="control-label label label-inverse">
                    Número
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="numero-label">
                    <g:fieldValue bean="${presupuestoInstance}" field="numero"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${presupuestoInstance?.nivel}">
        <div class="control-group">
            <div>
                <span id="nivel-label" class="control-label label label-inverse">
                    Nivel
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nivel-label">
                    <g:fieldValue bean="${presupuestoInstance}" field="nivel"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${presupuestoInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripción
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${presupuestoInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
