
<%@ page import="janus.Provincia" %>

<div id="show-provincia" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${provinciaInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${provinciaInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${provinciaInstance?.numero}">
        <div class="control-group">
            <div>
                <span id="numero-label" class="control-label label label-inverse">
                    Número
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="numero-label">
                    <g:fieldValue bean="${provinciaInstance}" field="numero"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${provinciaInstance?.latitud}">
        <div class="control-group">
            <div>
                <span id="latitud-label" class="control-label label label-inverse">
                    Latitud
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="latitud-label">
                    <g:fieldValue bean="${provinciaInstance}" field="latitud"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${provinciaInstance?.longitud}">
        <div class="control-group">
            <div>
                <span id="longitud-label" class="control-label label label-inverse">
                    Longitud
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="longitud-label">
                    <g:fieldValue bean="${provinciaInstance}" field="longitud"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
