
<%@ page import="janus.Contrato" %>

<div id="show-contrato" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${contratoInstance?.oferta}">
        <div class="control-group">
            <div>
                <span id="oferta-label" class="control-label label label-inverse">
                    Oferta
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="oferta-label">
        %{--<g:link controller="oferta" action="show" id="${contratoInstance?.oferta?.id}">--}%
                    ${contratoInstance?.oferta?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.tipoContrato}">
        <div class="control-group">
            <div>
                <span id="tipoContrato-label" class="control-label label label-inverse">
                    Tipo Contrato
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipoContrato-label">
        %{--<g:link controller="tipoContrato" action="show" id="${contratoInstance?.tipoContrato?.id}">--}%
                    ${contratoInstance?.tipoContrato?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.tipoPlazo}">
        <div class="control-group">
            <div>
                <span id="tipoPlazo-label" class="control-label label label-inverse">
                    Tipo Plazo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipoPlazo-label">
        %{--<g:link controller="tipoPlazo" action="show" id="${contratoInstance?.tipoPlazo?.id}">--}%
                    ${contratoInstance?.tipoPlazo?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.padre}">
        <div class="control-group">
            <div>
                <span id="padre-label" class="control-label label label-inverse">
                    Padre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="padre-label">
        %{--<g:link controller="contrato" action="show" id="${contratoInstance?.padre?.id}">--}%
                    ${contratoInstance?.padre?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.periodoInec}">
        <div class="control-group">
            <div>
                <span id="periodoValidez-label" class="control-label label label-inverse">
                    Periodo Validez
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="periodoValidez-label">
        %{--<g:link controller="periodoInec" action="show" id="${contratoInstance?.periodoInec?.id}">--}%
                    ${contratoInstance?.periodoInec?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Codigo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${contratoInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.objeto}">
        <div class="control-group">
            <div>
                <span id="objeto-label" class="control-label label label-inverse">
                    Objeto
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="objeto-label">
                    <g:fieldValue bean="${contratoInstance}" field="objeto"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.fechaSubscripcion}">
        <div class="control-group">
            <div>
                <span id="fechaSubscripcion-label" class="control-label label label-inverse">
                    Fecha Subscripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaSubscripcion-label">
                    <g:formatDate date="${contratoInstance?.fechaSubscripcion}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.fechaIngreso}">
        <div class="control-group">
            <div>
                <span id="fechaIngreso-label" class="control-label label label-inverse">
                    Fecha Ingreso
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaIngreso-label">
                    <g:formatDate date="${contratoInstance?.fechaIngreso}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.fechaInicio}">
        <div class="control-group">
            <div>
                <span id="fechaInicio-label" class="control-label label label-inverse">
                    Fecha Inicio
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaInicio-label">
                    <g:formatDate date="${contratoInstance?.fechaInicio}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.fechaFin}">
        <div class="control-group">
            <div>
                <span id="fechaFin-label" class="control-label label label-inverse">
                    Fecha Fin
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaFin-label">
                    <g:formatDate date="${contratoInstance?.fechaFin}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.monto}">
        <div class="control-group">
            <div>
                <span id="monto-label" class="control-label label label-inverse">
                    Monto
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="monto-label">
                    <g:fieldValue bean="${contratoInstance}" field="monto"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.financiamiento}">
        <div class="control-group">
            <div>
                <span id="financiamiento-label" class="control-label label label-inverse">
                    Financiamiento
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="financiamiento-label">
                    <g:fieldValue bean="${contratoInstance}" field="financiamiento"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.anticipo}">
        <div class="control-group">
            <div>
                <span id="anticipo-label" class="control-label label label-inverse">
                    Anticipo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="anticipo-label">
                    <g:fieldValue bean="${contratoInstance}" field="anticipo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.multas}">
        <div class="control-group">
            <div>
                <span id="multas-label" class="control-label label label-inverse">
                    Multas
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="multas-label">
                    <g:fieldValue bean="${contratoInstance}" field="multas"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.estado}">
        <div class="control-group">
            <div>
                <span id="estado-label" class="control-label label label-inverse">
                    Estado
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="estado-label">
                    <g:fieldValue bean="${contratoInstance}" field="estado"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.responsableTecnico}">
        <div class="control-group">
            <div>
                <span id="responsableTecnico-label" class="control-label label label-inverse">
                    Responsable Tecnico
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="responsableTecnico-label">
                    <g:fieldValue bean="${contratoInstance}" field="responsableTecnico"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.fechaFirma}">
        <div class="control-group">
            <div>
                <span id="fechaFirma-label" class="control-label label label-inverse">
                    Fecha Firma
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaFirma-label">
                    <g:formatDate date="${contratoInstance?.fechaFirma}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.cuentaContable}">
        <div class="control-group">
            <div>
                <span id="cuentaContable-label" class="control-label label label-inverse">
                    Cuenta Contable
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="cuentaContable-label">
                    <g:fieldValue bean="${contratoInstance}" field="cuentaContable"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.prorroga}">
        <div class="control-group">
            <div>
                <span id="prorroga-label" class="control-label label label-inverse">
                    Prorroga
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="prorroga-label">
                    <g:fieldValue bean="${contratoInstance}" field="prorroga"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.observaciones}">
        <div class="control-group">
            <div>
                <span id="observaciones-label" class="control-label label label-inverse">
                    Observaciones
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="observaciones-label">
                    <g:fieldValue bean="${contratoInstance}" field="observaciones"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${contratoInstance?.memo}">
        <div class="control-group">
            <div>
                <span id="memo-label" class="control-label label label-inverse">
                    Memo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="memo-label">
                    <g:fieldValue bean="${contratoInstance}" field="memo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
