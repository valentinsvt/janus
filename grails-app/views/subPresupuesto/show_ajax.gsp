
<%@ page import="janus.SubPresupuesto" %>

<div id="show-subPresupuesto" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${subPresupuestoInstance?.tipo}">
        <div class="control-group">
            <div>
                <span id="tipo-label" class="control-label label label-inverse">
                    Tipo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipo-label">
                    <g:fieldValue bean="${subPresupuestoInstance}" field="tipo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${subPresupuestoInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${subPresupuestoInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
