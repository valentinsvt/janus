
<%@ page import="janus.Comunidad" %>

<div id="show-comunidad" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${comunidadInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${comunidadInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${comunidadInstance?.numero}">
        <div class="control-group">
            <div>
                <span id="numero-label" class="control-label label label-inverse">
                    Numero
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="numero-label">
                    <g:fieldValue bean="${comunidadInstance}" field="numero"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${comunidadInstance?.parroquia}">
        <div class="control-group">
            <div>
                <span id="parroquia-label" class="control-label label label-inverse">
                    Parroquia
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="parroquia-label">
        %{--<g:link controller="parroquia" action="show" id="${comunidadInstance?.parroquia?.id}">--}%
                    ${comunidadInstance?.parroquia?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${comunidadInstance?.latitud}">
        <div class="control-group">
            <div>
                <span id="latitud-label" class="control-label label label-inverse">
                    Latitud
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="latitud-label">
                    <g:fieldValue bean="${comunidadInstance}" field="latitud"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${comunidadInstance?.longitud}">
        <div class="control-group">
            <div>
                <span id="longitud-label" class="control-label label label-inverse">
                    Longitud
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="longitud-label">
                    <g:fieldValue bean="${comunidadInstance}" field="longitud"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
