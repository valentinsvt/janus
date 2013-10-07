
<%@ page import="janus.pac.Proveedor" %>

<div id="show-proveedor" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${proveedorInstance?.especialidad}">
        <div class="control-group">
            <div>
                <span id="especialidad-label" class="control-label label label-inverse">
                    Especialidad
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="especialidad-label">
        %{--<g:link controller="especialidadProveedor" action="show" id="${proveedorInstance?.especialidad?.id}">--}%
                    ${proveedorInstance?.especialidad?.descripcion?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.tipo}">
        <div class="control-group">
            <div>
                <span id="tipo-label" class="control-label label label-inverse">
                    Tipo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipo-label">
                    <g:fieldValue bean="${proveedorInstance}" field="tipo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.ruc}">
        <div class="control-group">
            <div>
                <span id="ruc-label" class="control-label label label-inverse">
                    Ruc
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="ruc-label">
                    <g:fieldValue bean="${proveedorInstance}" field="ruc"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${proveedorInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.nombreContacto}">
        <div class="control-group">
            <div>
                <span id="nombreContacto-label" class="control-label label label-inverse">
                    Nombre Contacto
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombreContacto-label">
                    <g:fieldValue bean="${proveedorInstance}" field="nombreContacto"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.apellidoContacto}">
        <div class="control-group">
            <div>
                <span id="apellidoContacto-label" class="control-label label label-inverse">
                    Apellido Contacto
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="apellidoContacto-label">
                    <g:fieldValue bean="${proveedorInstance}" field="apellidoContacto"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.garante}">
        <div class="control-group">
            <div>
                <span id="garante-label" class="control-label label label-inverse">
                    Garante
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="garante-label">
                    <g:fieldValue bean="${proveedorInstance}" field="garante"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.direccion}">
        <div class="control-group">
            <div>
                <span id="direccion-label" class="control-label label label-inverse">
                    Direccion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="direccion-label">
                    <g:fieldValue bean="${proveedorInstance}" field="direccion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.fax}">
        <div class="control-group">
            <div>
                <span id="fax-label" class="control-label label label-inverse">
                    Fax
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fax-label">
                    <g:fieldValue bean="${proveedorInstance}" field="fax"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.telefonos}">
        <div class="control-group">
            <div>
                <span id="telefonos-label" class="control-label label label-inverse">
                    Telefonos
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="telefonos-label">
                    <g:fieldValue bean="${proveedorInstance}" field="telefonos"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.fechaContacto}">
        <div class="control-group">
            <div>
                <span id="fechaContacto-label" class="control-label label label-inverse">
                    Fecha Contacto
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaContacto-label">
                    <g:formatDate date="${proveedorInstance?.fechaContacto}" format="dd-MM-yyyy" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.email}">
        <div class="control-group">
            <div>
                <span id="email-label" class="control-label label label-inverse">
                    Email
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="email-label">
                    <g:fieldValue bean="${proveedorInstance}" field="email"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.licencia}">
        <div class="control-group">
            <div>
                <span id="licencia-label" class="control-label label label-inverse">
                    Licencia
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="licencia-label">
                    <g:fieldValue bean="${proveedorInstance}" field="licencia"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.registro}">
        <div class="control-group">
            <div>
                <span id="registro-label" class="control-label label label-inverse">
                    Registro
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="registro-label">
                    <g:fieldValue bean="${proveedorInstance}" field="registro"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.titulo}">
        <div class="control-group">
            <div>
                <span id="titulo-label" class="control-label label label-inverse">
                    Titulo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="titulo-label">
                    <g:fieldValue bean="${proveedorInstance}" field="titulo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.estado}">
        <div class="control-group">
            <div>
                <span id="estado-label" class="control-label label label-inverse">
                    Estado
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="estado-label">
                    <g:fieldValue bean="${proveedorInstance}" field="estado"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${proveedorInstance?.observaciones}">
        <div class="control-group">
            <div>
                <span id="observaciones-label" class="control-label label label-inverse">
                    Observaciones
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="observaciones-label">
                    <g:fieldValue bean="${proveedorInstance}" field="observaciones"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
