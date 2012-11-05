
<%@ page import="janus.Canton" %>

<div id="show-canton" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${cantonInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${cantonInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cantonInstance?.numero}">
        <div class="control-group">
            <div>
                <span id="numero-label" class="control-label label label-inverse">
                    Número
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="numero-label">
                    <g:fieldValue bean="${cantonInstance}" field="numero"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cantonInstance?.provincia}">
        <div class="control-group">
            <div>
                <span id="provincia-label" class="control-label label label-inverse">
                    Provincia
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="provincia-label">
        %{--<g:link controller="provincia" action="show" id="${cantonInstance?.provincia?.id}">--}%
                    ${cantonInstance?.provincia?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cantonInstance?.latitud}">
        <div class="control-group">
            <div>
                <span id="latitud-label" class="control-label label label-inverse">
                    Latitud
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="latitud-label">
                    <g:fieldValue bean="${cantonInstance}" field="latitud"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cantonInstance?.longitud}">
        <div class="control-group">
            <div>
                <span id="longitud-label" class="control-label label label-inverse">
                    Longitud
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="longitud-label">
                    <g:fieldValue bean="${cantonInstance}" field="longitud"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
