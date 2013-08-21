
<%@ page import="janus.pac.Oferta" %>

<div id="show-oferta" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${ofertaInstance?.concurso}">
        <div class="control-group">
            <div>
                <span id="concurso-label" class="control-label label label-inverse">
                    Proceso
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="concurso-label">
        %{--<g:link controller="concurso" action="show" id="${ofertaInstance?.concurso?.id}">--}%
                    ${ofertaInstance?.concurso?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertaInstance?.proveedor}">
        <div class="control-group">
            <div>
                <span id="proveedor-label" class="control-label label label-inverse">
                    Proveedor
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="proveedor-label">
        %{--<g:link controller="proveedor" action="show" id="${ofertaInstance?.proveedor?.id}">--}%
                    ${ofertaInstance?.proveedor?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertaInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${ofertaInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertaInstance?.monto}">
        <div class="control-group">
            <div>
                <span id="monto-label" class="control-label label label-inverse">
                    Monto
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="monto-label">
                    <g:fieldValue bean="${ofertaInstance}" field="monto"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertaInstance?.fechaEntrega}">
        <div class="control-group">
            <div>
                <span id="fechaEntrega-label" class="control-label label label-inverse">
                    Fecha Entrega
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaEntrega-label">
                    <g:formatDate date="${ofertaInstance?.fechaEntrega}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertaInstance?.plazo}">
        <div class="control-group">
            <div>
                <span id="plazo-label" class="control-label label label-inverse">
                    Plazo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="plazo-label">
                    <g:fieldValue bean="${ofertaInstance}" field="plazo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertaInstance?.calificado}">
        <div class="control-group">
            <div>
                <span id="calificado-label" class="control-label label label-inverse">
                    Calificado
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="calificado-label">
                    <g:fieldValue bean="${ofertaInstance}" field="calificado"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertaInstance?.hoja}">
        <div class="control-group">
            <div>
                <span id="hoja-label" class="control-label label label-inverse">
                    Hoja
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="hoja-label">
                    <g:fieldValue bean="${ofertaInstance}" field="hoja"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertaInstance?.subsecretario}">
        <div class="control-group">
            <div>
                <span id="subsecretario-label" class="control-label label label-inverse">
                    Subsecretario
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="subsecretario-label">
                    <g:fieldValue bean="${ofertaInstance}" field="subsecretario"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertaInstance?.garantia}">
        <div class="control-group">
            <div>
                <span id="garantia-label" class="control-label label label-inverse">
                    Garantia
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="garantia-label">
                    <g:fieldValue bean="${ofertaInstance}" field="garantia"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertaInstance?.estado}">
        <div class="control-group">
            <div>
                <span id="estado-label" class="control-label label label-inverse">
                    Estado
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="estado-label">
                    <g:fieldValue bean="${ofertaInstance}" field="estado"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertaInstance?.observaciones}">
        <div class="control-group">
            <div>
                <span id="observaciones-label" class="control-label label label-inverse">
                    Observaciones
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="observaciones-label">
                    <g:fieldValue bean="${ofertaInstance}" field="observaciones"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
