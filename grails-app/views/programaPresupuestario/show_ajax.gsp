
<%@ page import="janus.pac.ProgramaPresupuestario" %>

<div id="show-programaPresupuestario" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${programaPresupuestarioInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${programaPresupuestarioInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${programaPresupuestarioInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Codigo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${programaPresupuestarioInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
