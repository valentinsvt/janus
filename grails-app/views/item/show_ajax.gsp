
<%@ page import="janus.Item" %>

<div id="show-item" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${itemInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${itemInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Código
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${itemInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.unidad}">
        <div class="control-group">
            <div>
                <span id="unidad-label" class="control-label label label-inverse">
                    Unidad
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="unidad-label">
        %{--<g:link controller="unidad" action="show" id="${itemInstance?.unidad?.id}">--}%
                    ${itemInstance?.unidad?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.tipoItem}">
        <div class="control-group">
            <div>
                <span id="tipoItem-label" class="control-label label label-inverse">
                    Tipo de Item
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipoItem-label">
        %{--<g:link controller="tipoItem" action="show" id="${itemInstance?.tipoItem?.id}">--}%
                    ${itemInstance?.tipoItem?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.peso}">
        <div class="control-group">
            <div>
                <span id="peso-label" class="control-label label label-inverse">
                    Peso
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="peso-label">
                    <g:fieldValue bean="${itemInstance}" field="peso"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.departamento}">
        <div class="control-group">
            <div>
                <span id="departamento-label" class="control-label label label-inverse">
                    Coordinación
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="departamento-label">
        %{--<g:link controller="departamentoItem" action="show" id="${itemInstance?.departamento?.id}">--}%
                    ${itemInstance?.departamento?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.estado}">
        <div class="control-group">
            <div>
                <span id="estado-label" class="control-label label label-inverse">
                    Estado
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="estado-label">
                    <g:fieldValue bean="${itemInstance}" field="estado"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.fecha}">
        <div class="control-group">
            <div>
                <span id="fecha-label" class="control-label label label-inverse">
                    Fecha
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fecha-label">
                    <g:formatDate date="${itemInstance?.fecha}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.transportePeso}">
        <div class="control-group">
            <div>
                <span id="transportePeso-label" class="control-label label label-inverse">
                    Transporte Peso
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="transportePeso-label">
                    <g:fieldValue bean="${itemInstance}" field="transportePeso"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.transporteVolumen}">
        <div class="control-group">
            <div>
                <span id="transporteVolumen-label" class="control-label label label-inverse">
                    Transporte Volumen
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="transporteVolumen-label">
                    <g:fieldValue bean="${itemInstance}" field="transporteVolumen"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.padre}">
        <div class="control-group">
            <div>
                <span id="padre-label" class="control-label label label-inverse">
                    Padre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="padre-label">
                    <g:fieldValue bean="${itemInstance}" field="padre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.inec}">
        <div class="control-group">
            <div>
                <span id="inec-label" class="control-label label label-inverse">
                    Inec
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="inec-label">
                    <g:fieldValue bean="${itemInstance}" field="inec"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.rendimiento}">
        <div class="control-group">
            <div>
                <span id="rendimiento-label" class="control-label label label-inverse">
                    Rendimiento
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="rendimiento-label">
                    <g:fieldValue bean="${itemInstance}" field="rendimiento"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.tipo}">
        <div class="control-group">
            <div>
                <span id="tipo-label" class="control-label label label-inverse">
                    Tipo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipo-label">
                    <g:fieldValue bean="${itemInstance}" field="tipo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.campo}">
        <div class="control-group">
            <div>
                <span id="campo-label" class="control-label label label-inverse">
                    Campo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="campo-label">
                    <g:fieldValue bean="${itemInstance}" field="campo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.registro}">
        <div class="control-group">
            <div>
                <span id="registro-label" class="control-label label label-inverse">
                    Registro
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="registro-label">
                    <g:fieldValue bean="${itemInstance}" field="registro"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.transporte}">
        <div class="control-group">
            <div>
                <span id="transporte-label" class="control-label label label-inverse">
                    Transporte
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="transporte-label">
                    <g:fieldValue bean="${itemInstance}" field="transporte"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.combustible}">
        <div class="control-group">
            <div>
                <span id="combustible-label" class="control-label label label-inverse">
                    Combustible
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="combustible-label">
                    <g:fieldValue bean="${itemInstance}" field="combustible"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemInstance?.observaciones}">
        <div class="control-group">
            <div>
                <span id="observaciones-label" class="control-label label label-inverse">
                    Observaciones
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="observaciones-label">
                    <g:fieldValue bean="${itemInstance}" field="observaciones"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
