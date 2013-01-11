
<%@ page import="janus.pac.Etapa" %>

<div id="show-etapa" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${etapaInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${etapaInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
