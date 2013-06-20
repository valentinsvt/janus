
<%@ page import="janus.ejecucion.PeriodosInec" %>

<div id="show-periodosInec" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${periodosInecInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripción
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${periodosInecInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${periodosInecInstance?.fechaInicio}">
        <div class="control-group">
            <div>
                <span id="fechaInicio-label" class="control-label label label-inverse">
                    Fecha Inicio
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaInicio-label">
                    <g:formatDate date="${periodosInecInstance?.fechaInicio}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${periodosInecInstance?.fechaFin}">
        <div class="control-group">
            <div>
                <span id="fechaFin-label" class="control-label label label-inverse">
                    Fecha Fin
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaFin-label">
                    <g:formatDate date="${periodosInecInstance?.fechaFin}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${periodosInecInstance?.periodoCerrado}">
        <div class="control-group">
            <div>
                <span id="periodoCerrado-label" class="control-label label label-inverse">
                    Periodo Cerrado
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="periodoCerrado-label">
                    <g:fieldValue bean="${periodosInecInstance}" field="periodoCerrado"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
