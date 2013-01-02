
<%@ page import="janus.pac.Anio" %>

<div id="show-anio" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${anioInstance?.anio}">
        <div class="control-group">
            <div>
                <span id="anio-label" class="control-label label label-inverse">
                    Anio
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="anio-label">
                    <g:fieldValue bean="${anioInstance}" field="anio"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${anioInstance?.estado}">
        <div class="control-group">
            <div>
                <span id="estado-label" class="control-label label label-inverse">
                    Estado
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="estado-label">
                    <g:fieldValue bean="${anioInstance}" field="estado"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
