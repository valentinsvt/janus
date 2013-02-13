
<%@ page import="janus.ejecucion.TipoDescuentoPlanilla" %>

<div id="show-tipoDescuentoPlanilla" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${tipoDescuentoPlanillaInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${tipoDescuentoPlanillaInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoDescuentoPlanillaInstance?.porcentaje}">
        <div class="control-group">
            <div>
                <span id="porcentaje-label" class="control-label label label-inverse">
                    Porcentaje
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="porcentaje-label">
                    <g:fieldValue bean="${tipoDescuentoPlanillaInstance}" field="porcentaje"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tipoDescuentoPlanillaInstance?.valor}">
        <div class="control-group">
            <div>
                <span id="valor-label" class="control-label label label-inverse">
                    Valor
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="valor-label">
                    <g:fieldValue bean="${tipoDescuentoPlanillaInstance}" field="valor"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
