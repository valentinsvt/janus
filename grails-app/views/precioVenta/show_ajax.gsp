
<%@ page import="janus.PrecioVenta" %>

<div id="show-precioVenta" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${precioVentaInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${precioVentaInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioVentaInstance?.sigla}">
        <div class="control-group">
            <div>
                <span id="sigla-label" class="control-label label label-inverse">
                    Sigla
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="sigla-label">
                    <g:fieldValue bean="${precioVentaInstance}" field="sigla"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioVentaInstance?.ruc}">
        <div class="control-group">
            <div>
                <span id="ruc-label" class="control-label label label-inverse">
                    Ruc
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="ruc-label">
                    <g:fieldValue bean="${precioVentaInstance}" field="ruc"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioVentaInstance?.responsable}">
        <div class="control-group">
            <div>
                <span id="responsable-label" class="control-label label label-inverse">
                    Responsable
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="responsable-label">
                    <g:fieldValue bean="${precioVentaInstance}" field="responsable"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioVentaInstance?.especialidadProveedor}">
        <div class="control-group">
            <div>
                <span id="especialidadProveedor-label" class="control-label label label-inverse">
                    Especialidad Proveedor
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="especialidadProveedor-label">
        %{--<g:link controller="especialidadProveedor" action="show" id="${precioVentaInstance?.especialidadProveedor?.id}">--}%
                    ${precioVentaInstance?.especialidadProveedor?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioVentaInstance?.direccion}">
        <div class="control-group">
            <div>
                <span id="direccion-label" class="control-label label label-inverse">
                    Dirección
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="direccion-label">
                    <g:fieldValue bean="${precioVentaInstance}" field="direccion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioVentaInstance?.fax}">
        <div class="control-group">
            <div>
                <span id="fax-label" class="control-label label label-inverse">
                    Fax
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fax-label">
                    <g:fieldValue bean="${precioVentaInstance}" field="fax"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioVentaInstance?.telefono}">
        <div class="control-group">
            <div>
                <span id="telefono-label" class="control-label label label-inverse">
                    Teléfono
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="telefono-label">
                    <g:fieldValue bean="${precioVentaInstance}" field="telefono"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioVentaInstance?.email}">
        <div class="control-group">
            <div>
                <span id="email-label" class="control-label label label-inverse">
                    Email
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="email-label">
                    <g:fieldValue bean="${precioVentaInstance}" field="email"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioVentaInstance?.fechaContacto}">
        <div class="control-group">
            <div>
                <span id="fechaContacto-label" class="control-label label label-inverse">
                    Fecha Contacto
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaContacto-label">
                    <g:formatDate date="${precioVentaInstance?.fechaContacto}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioVentaInstance?.indiceCostosIndirectosGarantias}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosGarantias-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Garantías
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosGarantias-label">
                    <g:fieldValue bean="${precioVentaInstance}" field="indiceCostosIndirectosGarantias"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioVentaInstance?.licenciaColegioIngenieros}">
        <div class="control-group">
            <div>
                <span id="licenciaColegioIngenieros-label" class="control-label label label-inverse">
                    Licencia Colegio Ingenieros
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="licenciaColegioIngenieros-label">
                    <g:fieldValue bean="${precioVentaInstance}" field="licenciaColegioIngenieros"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioVentaInstance?.registroCamaraConstruccion}">
        <div class="control-group">
            <div>
                <span id="registroCamaraConstruccion-label" class="control-label label label-inverse">
                    Registro Cámara Construcción
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="registroCamaraConstruccion-label">
                    <g:fieldValue bean="${precioVentaInstance}" field="registroCamaraConstruccion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioVentaInstance?.tituloProfecionalTitular}">
        <div class="control-group">
            <div>
                <span id="tituloProfecionalTitular-label" class="control-label label label-inverse">
                    Título Profesional
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tituloProfecionalTitular-label">
                    <g:fieldValue bean="${precioVentaInstance}" field="tituloProfecionalTitular"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${precioVentaInstance?.observaciones}">
        <div class="control-group">
            <div>
                <span id="observaciones-label" class="control-label label label-inverse">
                    Observaciones
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="observaciones-label">
                    <g:fieldValue bean="${precioVentaInstance}" field="observaciones"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
