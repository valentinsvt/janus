
<%@ page import="janus.Direccion" %>

<div id="show-direccion" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${direccionInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${direccionInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
