
<%@ page import="janus.pac.ParametroEvaluacion" %>

<div id="show-parametroEvaluacion" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${parametroEvaluacionInstance?.padre}">
        <div class="control-group">
            <div>
                <span id="padre-label" class="control-label label label-inverse">
                    Padre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="padre-label">
        %{--<g:link controller="parametroEvaluacion" action="show" id="${parametroEvaluacionInstance?.padre?.id}">--}%
                    ${parametroEvaluacionInstance?.padre?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametroEvaluacionInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${parametroEvaluacionInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametroEvaluacionInstance?.concurso}">
        <div class="control-group">
            <div>
                <span id="concurso-label" class="control-label label label-inverse">
                    Proceso
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="concurso-label">
        %{--<g:link controller="concurso" action="show" id="${parametroEvaluacionInstance?.concurso?.id}">--}%
                    ${parametroEvaluacionInstance?.concurso?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametroEvaluacionInstance?.minimo}">
        <div class="control-group">
            <div>
                <span id="minimo-label" class="control-label label label-inverse">
                    Minimo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="minimo-label">
                    <g:fieldValue bean="${parametroEvaluacionInstance}" field="minimo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametroEvaluacionInstance?.orden}">
        <div class="control-group">
            <div>
                <span id="orden-label" class="control-label label label-inverse">
                    Orden
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="orden-label">
                    <g:fieldValue bean="${parametroEvaluacionInstance}" field="orden"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametroEvaluacionInstance?.puntaje}">
        <div class="control-group">
            <div>
                <span id="puntaje-label" class="control-label label label-inverse">
                    Puntaje
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="puntaje-label">
                    <g:fieldValue bean="${parametroEvaluacionInstance}" field="puntaje"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
