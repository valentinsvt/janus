
<%@ page import="janus.AdministradorContrato" %>

<div id="show-administradorContrato" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${administradorContratoInstance?.administrador}">
        <div class="control-group">
            <div>
                <span id="administrador-label" class="control-label label label-inverse">
                    Administrador
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="administrador-label">
        %{--<g:link controller="persona" action="show" id="${administradorContratoInstance?.administrador?.id}">--}%
                    ${administradorContratoInstance?.administrador?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${administradorContratoInstance?.contrato}">
        <div class="control-group">
            <div>
                <span id="contrato-label" class="control-label label label-inverse">
                    Contrato
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="contrato-label">
        %{--<g:link controller="contrato" action="show" id="${administradorContratoInstance?.contrato?.id}">--}%
                    ${administradorContratoInstance?.contrato?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${administradorContratoInstance?.fechaFin}">
        <div class="control-group">
            <div>
                <span id="fechaFin-label" class="control-label label label-inverse">
                    Fecha Fin
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaFin-label">
                    <g:formatDate date="${administradorContratoInstance?.fechaFin}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${administradorContratoInstance?.fechaInicio}">
        <div class="control-group">
            <div>
                <span id="fechaInicio-label" class="control-label label label-inverse">
                    Fecha Inicio
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaInicio-label">
                    <g:formatDate date="${administradorContratoInstance?.fechaInicio}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
