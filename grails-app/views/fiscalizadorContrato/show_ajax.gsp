
<%@ page import="janus.FiscalizadorContrato" %>

<div id="show-fiscalizadorContrato" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${fiscalizadorContratoInstance?.fechaInicio}">
        <div class="control-group">
            <div>
                <span id="fechaInicio-label" class="control-label label label-inverse">
                    Fecha Inicio
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaInicio-label">
                    <g:formatDate date="${fiscalizadorContratoInstance?.fechaInicio}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${fiscalizadorContratoInstance?.fechaFin}">
        <div class="control-group">
            <div>
                <span id="fechaFin-label" class="control-label label label-inverse">
                    Fecha Fin
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaFin-label">
                    <g:formatDate date="${fiscalizadorContratoInstance?.fechaFin}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${fiscalizadorContratoInstance?.contrato}">
        <div class="control-group">
            <div>
                <span id="contrato-label" class="control-label label label-inverse">
                    Contrato
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="contrato-label">
        %{--<g:link controller="contrato" action="show" id="${fiscalizadorContratoInstance?.contrato?.id}">--}%
                    ${fiscalizadorContratoInstance?.contrato?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${fiscalizadorContratoInstance?.fiscalizador}">
        <div class="control-group">
            <div>
                <span id="fiscalizador-label" class="control-label label label-inverse">
                    Fiscalizador
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fiscalizador-label">
        %{--<g:link controller="persona" action="show" id="${fiscalizadorContratoInstance?.fiscalizador?.id}">--}%
                    ${fiscalizadorContratoInstance?.fiscalizador?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
