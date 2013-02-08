
<%@ page import="janus.ejecucion.TipoMulta" %>

<div id="show-tipoMulta" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${tipoMultaInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${tipoMultaInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoMultaInstance?.porcentaje}">
        <div class="control-group">
            <div>
                <span id="porcentaje-label" class="control-label label label-inverse">
                    Porcentaje
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="porcentaje-label">
                    <g:fieldValue bean="${tipoMultaInstance}" field="porcentaje"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
