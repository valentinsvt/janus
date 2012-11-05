
<%@ page import="janus.Parroquia" %>

<div id="show-parroquia" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${parroquiaInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${parroquiaInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parroquiaInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Código
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${parroquiaInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parroquiaInstance?.canton}">
        <div class="control-group">
            <div>
                <span id="canton-label" class="control-label label label-inverse">
                    Cantón
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="canton-label">
        %{--<g:link controller="canton" action="show" id="${parroquiaInstance?.canton?.id}">--}%
                    ${parroquiaInstance?.canton?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parroquiaInstance?.urbana}">
        <div class="control-group">
            <div>
                <span id="urbana-label" class="control-label label label-inverse">
                    Urbana
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="urbana-label">
                    <g:fieldValue bean="${parroquiaInstance}" field="urbana"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parroquiaInstance?.latitud}">
        <div class="control-group">
            <div>
                <span id="latitud-label" class="control-label label label-inverse">
                    Latitud
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="latitud-label">
                    <g:fieldValue bean="${parroquiaInstance}" field="latitud"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parroquiaInstance?.longitud}">
        <div class="control-group">
            <div>
                <span id="longitud-label" class="control-label label label-inverse">
                    Longitud
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="longitud-label">
                    <g:fieldValue bean="${parroquiaInstance}" field="longitud"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
