
<%@ page import="janus.pac.Garantia" %>

<div id="show-garantia" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${garantiaInstance?.tipoGarantia}">
        <div class="control-group">
            <div>
                <span id="tipoGarantia-label" class="control-label label label-inverse">
                    Tipo Garantia
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipoGarantia-label">
        %{--<g:link controller="tipoGarantia" action="show" id="${garantiaInstance?.tipoGarantia?.id}">--}%
                    ${garantiaInstance?.tipoGarantia?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${garantiaInstance?.tipoDocumentoGarantia}">
        <div class="control-group">
            <div>
                <span id="tipoDocumentoGarantia-label" class="control-label label label-inverse">
                    Tipo Documento Garantia
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipoDocumentoGarantia-label">
        %{--<g:link controller="tipoDocumentoGarantia" action="show" id="${garantiaInstance?.tipoDocumentoGarantia?.id}">--}%
                    ${garantiaInstance?.tipoDocumentoGarantia?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${garantiaInstance?.estado}">
        <div class="control-group">
            <div>
                <span id="estado-label" class="control-label label label-inverse">
                    Estado
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="estado-label">
        %{--<g:link controller="estadoGarantia" action="show" id="${garantiaInstance?.estado?.id}">--}%
                    ${garantiaInstance?.estado?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${garantiaInstance?.moneda}">
        <div class="control-group">
            <div>
                <span id="moneda-label" class="control-label label label-inverse">
                    Moneda
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="moneda-label">
        %{--<g:link controller="moneda" action="show" id="${garantiaInstance?.moneda?.id}">--}%
                    ${garantiaInstance?.moneda?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${garantiaInstance?.aseguradora}">
        <div class="control-group">
            <div>
                <span id="aseguradora-label" class="control-label label label-inverse">
                    Aseguradora
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="aseguradora-label">
        %{--<g:link controller="aseguradora" action="show" id="${garantiaInstance?.aseguradora?.id}">--}%
                    ${garantiaInstance?.aseguradora?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${garantiaInstance?.contrato}">
        <div class="control-group">
            <div>
                <span id="contrato-label" class="control-label label label-inverse">
                    Contrato
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="contrato-label">
        %{--<g:link controller="contrato" action="show" id="${garantiaInstance?.contrato?.id}">--}%
                    ${garantiaInstance?.contrato?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${garantiaInstance?.padre}">
        <div class="control-group">
            <div>
                <span id="padre-label" class="control-label label label-inverse">
                    Padre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="padre-label">
                    <g:fieldValue bean="${garantiaInstance}" field="padre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${garantiaInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Codigo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${garantiaInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${garantiaInstance?.numeroRenovaciones}">
        <div class="control-group">
            <div>
                <span id="numeroRenovaciones-label" class="control-label label label-inverse">
                    Numero Renovaciones
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="numeroRenovaciones-label">
                    <g:fieldValue bean="${garantiaInstance}" field="numeroRenovaciones"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${garantiaInstance?.estadoGarantia}">
        <div class="control-group">
            <div>
                <span id="estadoGarantia-label" class="control-label label label-inverse">
                    Estado Garantia
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="estadoGarantia-label">
                    <g:fieldValue bean="${garantiaInstance}" field="estadoGarantia"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${garantiaInstance?.monto}">
        <div class="control-group">
            <div>
                <span id="monto-label" class="control-label label label-inverse">
                    Monto
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="monto-label">
                    <g:fieldValue bean="${garantiaInstance}" field="monto"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${garantiaInstance?.fechaInicio}">
        <div class="control-group">
            <div>
                <span id="fechaInicio-label" class="control-label label label-inverse">
                    Fecha Inicio
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaInicio-label">
                    <g:formatDate date="${garantiaInstance?.fechaInicio}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${garantiaInstance?.fechaFinalizacion}">
        <div class="control-group">
            <div>
                <span id="fechaFinalizacion-label" class="control-label label label-inverse">
                    Fecha Finalizacion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaFinalizacion-label">
                    <g:formatDate date="${garantiaInstance?.fechaFinalizacion}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${garantiaInstance?.diasGarantizados}">
        <div class="control-group">
            <div>
                <span id="diasGarantizados-label" class="control-label label label-inverse">
                    Dias Garantizados
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="diasGarantizados-label">
                    <g:fieldValue bean="${garantiaInstance}" field="diasGarantizados"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${garantiaInstance?.cancelada}">
        <div class="control-group">
            <div>
                <span id="cancelada-label" class="control-label label label-inverse">
                    Cancelada
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="cancelada-label">
                    <g:fieldValue bean="${garantiaInstance}" field="cancelada"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${garantiaInstance?.pedido}">
        <div class="control-group">
            <div>
                <span id="pedido-label" class="control-label label label-inverse">
                    Pedido
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="pedido-label">
                    <g:fieldValue bean="${garantiaInstance}" field="pedido"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
