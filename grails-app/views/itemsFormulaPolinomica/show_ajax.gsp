
<%@ page import="janus.ItemsFormulaPolinomica" %>

<div id="show-itemsFormulaPolinomica" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${itemsFormulaPolinomicaInstance?.item}">
        <div class="control-group">
            <div>
                <span id="item-label" class="control-label label label-inverse">
                    Item
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="item-label">
        %{--<g:link controller="item" action="show" id="${itemsFormulaPolinomicaInstance?.item?.id}">--}%
                    ${itemsFormulaPolinomicaInstance?.item?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${itemsFormulaPolinomicaInstance?.formulaPolinomica}">
        <div class="control-group">
            <div>
                <span id="formulaPolinomica-label" class="control-label label label-inverse">
                    Formula Polinomica
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="formulaPolinomica-label">
        %{--<g:link controller="formulaPolinomica" action="show" id="${itemsFormulaPolinomicaInstance?.formulaPolinomica?.id}">--}%
                    ${itemsFormulaPolinomicaInstance?.formulaPolinomica?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
