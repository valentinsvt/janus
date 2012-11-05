
<%@ page import="janus.Ofertas" %>

<div id="show-ofertas" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${ofertasInstance?.base__id}">
        <div class="control-group">
            <div>
                <span id="base__id-label" class="control-label label label-inverse">
                    Baseid
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="base__id-label">
                    <g:fieldValue bean="${ofertasInstance}" field="base__id"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertasInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripción
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${ofertasInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertasInstance?.monto}">
        <div class="control-group">
            <div>
                <span id="monto-label" class="control-label label label-inverse">
                    Monto
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="monto-label">
                    <g:fieldValue bean="${ofertasInstance}" field="monto"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertasInstance?.preguntasIniciales}">
        <div class="control-group">
            <div>
                <span id="preguntasIniciales-label" class="control-label label label-inverse">
                    Preguntas Iniciales
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="preguntasIniciales-label">
        %{--<g:link controller="periodosInec" action="show" id="${ofertasInstance?.preguntasIniciales?.id}">--}%
                    ${ofertasInstance?.preguntasIniciales?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertasInstance?.plazo}">
        <div class="control-group">
            <div>
                <span id="plazo-label" class="control-label label label-inverse">
                    Plazo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="plazo-label">
                    <g:fieldValue bean="${ofertasInstance}" field="plazo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertasInstance?.fechaEntrega}">
        <div class="control-group">
            <div>
                <span id="fechaEntrega-label" class="control-label label label-inverse">
                    Fecha Entrega
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaEntrega-label">
                    <g:formatDate date="${ofertasInstance?.fechaEntrega}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertasInstance?.calificacion}">
        <div class="control-group">
            <div>
                <span id="calificacion-label" class="control-label label label-inverse">
                    Calificación
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="calificacion-label">
                    <g:fieldValue bean="${ofertasInstance}" field="calificacion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertasInstance?.hojas}">
        <div class="control-group">
            <div>
                <span id="hojas-label" class="control-label label label-inverse">
                    Hojas
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="hojas-label">
                    <g:fieldValue bean="${ofertasInstance}" field="hojas"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertasInstance?.subsecretario}">
        <div class="control-group">
            <div>
                <span id="subsecretario-label" class="control-label label label-inverse">
                    Subsecretario
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="subsecretario-label">
                    <g:fieldValue bean="${ofertasInstance}" field="subsecretario"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertasInstance?.indiceCostosIndirectosGarantias}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosGarantias-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Garantias
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosGarantias-label">
                    <g:fieldValue bean="${ofertasInstance}" field="indiceCostosIndirectosGarantias"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertasInstance?.estado}">
        <div class="control-group">
            <div>
                <span id="estado-label" class="control-label label label-inverse">
                    Estado
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="estado-label">
                    <g:fieldValue bean="${ofertasInstance}" field="estado"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${ofertasInstance?.observaciones}">
        <div class="control-group">
            <div>
                <span id="observaciones-label" class="control-label label label-inverse">
                    Observaciones
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="observaciones-label">
                    <g:fieldValue bean="${ofertasInstance}" field="observaciones"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
