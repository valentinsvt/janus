
<%@ page import="janus.ejecucion.TipoPlanilla" %>

<div id="show-tipoPlanilla" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${tipoPlanillaInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Codigo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${tipoPlanillaInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoPlanillaInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${tipoPlanillaInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
