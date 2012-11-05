
<%@ page import="janus.FormulaPolinomica" %>

<div id="show-formulaPolinomica" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${formulaPolinomicaInstance?.obra}">
        <div class="control-group">
            <div>
                <span id="obra-label" class="control-label label label-inverse">
                    Obra
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="obra-label">
        %{--<g:link controller="obra" action="show" id="${formulaPolinomicaInstance?.obra?.id}">--}%
                    ${formulaPolinomicaInstance?.obra?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${formulaPolinomicaInstance?.numero}">
        <div class="control-group">
            <div>
                <span id="numero-label" class="control-label label label-inverse">
                    Número
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="numero-label">
                    <g:fieldValue bean="${formulaPolinomicaInstance}" field="numero"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${formulaPolinomicaInstance?.valor}">
        <div class="control-group">
            <div>
                <span id="valor-label" class="control-label label label-inverse">
                    Valor
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="valor-label">
                    <g:fieldValue bean="${formulaPolinomicaInstance}" field="valor"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${formulaPolinomicaInstance?.indice}">
        <div class="control-group">
            <div>
                <span id="indice-label" class="control-label label label-inverse">
                    Indice
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indice-label">
        %{--<g:link controller="indice" action="show" id="${formulaPolinomicaInstance?.indice?.id}">--}%
                    ${formulaPolinomicaInstance?.indice?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
