
<%@ page import="janus.Tramite" %>

<div id="show-tramite" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${tramiteInstance?.obra}">
        <div class="control-group">
            <div>
                <span id="obra-label" class="control-label label label-inverse">
                    Obra
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="obra-label">
        %{--<g:link controller="obra" action="show" id="${tramiteInstance?.obra?.id}">--}%
                    ${tramiteInstance?.obra?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tramiteInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Código
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${tramiteInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tramiteInstance?.tipoTramite}">
        <div class="control-group">
            <div>
                <span id="tipoTramite-label" class="control-label label label-inverse">
                    Tipo Trámite
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipoTramite-label">
        %{--<g:link controller="tipoTramite" action="show" id="${tramiteInstance?.tipoTramite?.id}">--}%
                    ${tramiteInstance?.tipoTramite?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tramiteInstance?.contrato}">
        <div class="control-group">
            <div>
                <span id="contrato-label" class="control-label label label-inverse">
                    Contrato
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="contrato-label">
        %{--<g:link controller="contrato" action="show" id="${tramiteInstance?.contrato?.id}">--}%
                    ${tramiteInstance?.contrato?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tramiteInstance?.tramitePadre}">
        <div class="control-group">
            <div>
                <span id="tramitePadre-label" class="control-label label label-inverse">
                    Trámite Padre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tramitePadre-label">
                    <g:fieldValue bean="${tramiteInstance}" field="tramitePadre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tramiteInstance?.fecha}">
        <div class="control-group">
            <div>
                <span id="fecha-label" class="control-label label label-inverse">
                    Fecha
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fecha-label">
                    <g:formatDate date="${tramiteInstance?.fecha}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tramiteInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripción
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${tramiteInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tramiteInstance?.fechaRecepcion}">
        <div class="control-group">
            <div>
                <span id="fechaRecepcion-label" class="control-label label label-inverse">
                    Fecha Recepción
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaRecepcion-label">
                    <g:formatDate date="${tramiteInstance?.fechaRecepcion}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${tramiteInstance?.documentosAdjuntos}">
        <div class="control-group">
            <div>
                <span id="documentosAdjuntos-label" class="control-label label label-inverse">
                    Documentos Adjuntos
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="documentosAdjuntos-label">
                    <g:fieldValue bean="${tramiteInstance}" field="documentosAdjuntos"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
