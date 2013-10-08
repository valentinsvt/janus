
<%@ page import="janus.EspecialidadProveedor" %>

<div id="show-especialidadProveedor" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${especialidadProveedorInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Especialidad
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${especialidadProveedorInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
