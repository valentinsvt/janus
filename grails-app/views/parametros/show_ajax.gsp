
<%@ page import="janus.Parametros" %>

<div id="show-parametros" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${parametrosInstance?.factorReduccion}">
        <div class="control-group">
            <div>
                <span id="factorReduccion-label" class="control-label label label-inverse">
                    Factor Reduccion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="factorReduccion-label">
                    <g:fieldValue bean="${parametrosInstance}" field="factorReduccion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.factorVelocidad}">
        <div class="control-group">
            <div>
                <span id="factorVelocidad-label" class="control-label label label-inverse">
                    Factor Velocidad
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="factorVelocidad-label">
                    <g:fieldValue bean="${parametrosInstance}" field="factorVelocidad"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.capacidadVolquete}">
        <div class="control-group">
            <div>
                <span id="capacidadVolquete-label" class="control-label label label-inverse">
                    Capacidad Volquete
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="capacidadVolquete-label">
                    <g:fieldValue bean="${parametrosInstance}" field="capacidadVolquete"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.factorVolumen}">
        <div class="control-group">
            <div>
                <span id="factorVolumen-label" class="control-label label label-inverse">
                    Factor Volumen
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="factorVolumen-label">
                    <g:fieldValue bean="${parametrosInstance}" field="factorVolumen"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.factorReduccionTiempo}">
        <div class="control-group">
            <div>
                <span id="factorReduccionTiempo-label" class="control-label label label-inverse">
                    Factor Reduccion Tiempo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="factorReduccionTiempo-label">
                    <g:fieldValue bean="${parametrosInstance}" field="factorReduccionTiempo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.factorPeso}">
        <div class="control-group">
            <div>
                <span id="factorPeso-label" class="control-label label label-inverse">
                    Factor Peso
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="factorPeso-label">
                    <g:fieldValue bean="${parametrosInstance}" field="factorPeso"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.impreso}">
        <div class="control-group">
            <div>
                <span id="impreso-label" class="control-label label label-inverse">
                    Impreso
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="impreso-label">
                    <g:fieldValue bean="${parametrosInstance}" field="impreso"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.indiceUtilidad}">
        <div class="control-group">
            <div>
                <span id="indiceUtilidad-label" class="control-label label label-inverse">
                    Indice Utilidad
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceUtilidad-label">
                    <g:fieldValue bean="${parametrosInstance}" field="indiceUtilidad"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.contrato}">
        <div class="control-group">
            <div>
                <span id="contrato-label" class="control-label label label-inverse">
                    Contrato
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="contrato-label">
                    <g:fieldValue bean="${parametrosInstance}" field="contrato"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.totales}">
        <div class="control-group">
            <div>
                <span id="totales-label" class="control-label label label-inverse">
                    Totales
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="totales-label">
                    <g:fieldValue bean="${parametrosInstance}" field="totales"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.indiceGastosGenerales}">
        <div class="control-group">
            <div>
                <span id="indiceGastosGenerales-label" class="control-label label label-inverse">
                    Indice Gastos Generales
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceGastosGenerales-label">
                    <g:fieldValue bean="${parametrosInstance}" field="indiceGastosGenerales"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.indiceCostosIndirectosObra}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosObra-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Obra
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosObra-label">
                    <g:fieldValue bean="${parametrosInstance}" field="indiceCostosIndirectosObra"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.indiceCostosIndirectosMantenimiento}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosMantenimiento-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Mantenimiento
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosMantenimiento-label">
                    <g:fieldValue bean="${parametrosInstance}" field="indiceCostosIndirectosMantenimiento"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.administracion}">
        <div class="control-group">
            <div>
                <span id="administracion-label" class="control-label label label-inverse">
                    Administracion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="administracion-label">
                    <g:fieldValue bean="${parametrosInstance}" field="administracion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.indiceCostosIndirectosGarantias}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosGarantias-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Garantias
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosGarantias-label">
                    <g:fieldValue bean="${parametrosInstance}" field="indiceCostosIndirectosGarantias"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.indiceCostosIndirectosCostosFinancieros}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosCostosFinancieros-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Costos Financieros
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosCostosFinancieros-label">
                    <g:fieldValue bean="${parametrosInstance}" field="indiceCostosIndirectosCostosFinancieros"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.indiceCostosIndirectosVehiculos}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosVehiculos-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Vehiculos
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosVehiculos-label">
                    <g:fieldValue bean="${parametrosInstance}" field="indiceCostosIndirectosVehiculos"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.indiceCostosIndirectosPromocion}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosPromocion-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Promocion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosPromocion-label">
                    <g:fieldValue bean="${parametrosInstance}" field="indiceCostosIndirectosPromocion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.indiceCostosIndirectosTimbresProvinciales}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosTimbresProvinciales-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Timbres Provinciales
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosTimbresProvinciales-label">
                    <g:fieldValue bean="${parametrosInstance}" field="indiceCostosIndirectosTimbresProvinciales"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.chofer}">
        <div class="control-group">
            <div>
                <span id="chofer-label" class="control-label label label-inverse">
                    Chofer
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="chofer-label">
        %{--<g:link controller="item" action="show" id="${parametrosInstance?.chofer?.id}">--}%
                    ${parametrosInstance?.chofer?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.volquete}">
        <div class="control-group">
            <div>
                <span id="volquete-label" class="control-label label label-inverse">
                    Volquete
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="volquete-label">
        %{--<g:link controller="item" action="show" id="${parametrosInstance?.volquete?.id}">--}%
                    ${parametrosInstance?.volquete?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
