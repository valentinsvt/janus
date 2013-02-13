
<%@ page import="janus.ejecucion.EstadoPlanilla" %>

<div id="show-estadoPlanilla" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${estadoPlanillaInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Codigo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${estadoPlanillaInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${estadoPlanillaInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${estadoPlanillaInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
