
<%@ page import="janus.TipoTramite" %>

<div id="show-tipoTramite" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${tipoTramiteInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripción
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${tipoTramiteInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
